/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.logging.jdbc3;

import java.lang.reflect.Method;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.reflection.ArrayUtil;

/**
 * Base class for proxies to do logging.
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public abstract class BaseJdbcLogger {

	protected static final Set<String> SET_METHODS;
	protected static final Set<String> EXECUTE_METHODS = new HashSet<>();

	private final Map<Object, Object> columnMap = new HashMap<>();

	private final List<Object> columnNames = new ArrayList<>();
	private final List<Object> columnValues = new ArrayList<>();

	protected final Log statementLog;
	protected final int queryStack;

	/*
	 * Default constructor
	 */
	public BaseJdbcLogger(Log log, int queryStack) {
		this.statementLog = log;
		if (queryStack == 0) {
			this.queryStack = 1;
		} else {
			this.queryStack = queryStack;
		}
	}

	static {
		SET_METHODS = Arrays.stream(PreparedStatement.class.getDeclaredMethods())
				.filter(method -> method.getName().startsWith("set")).filter(method -> method.getParameterCount() > 1)
				.map(Method::getName).collect(Collectors.toSet());

		EXECUTE_METHODS.add("execute");
		EXECUTE_METHODS.add("executeUpdate");
		EXECUTE_METHODS.add("executeQuery");
		EXECUTE_METHODS.add("addBatch");
	}

	protected void setColumn(Object key, Object value) {
		columnMap.put(key, value);
		columnNames.add(key);
		columnValues.add(value);
	}

	protected Object getColumn(Object key) {
		return columnMap.get(key);
	}

	protected String getParameterValueString() {
		List<Object> typeList = new ArrayList<>(columnValues.size());
		for (Object value : columnValues) {
			if (value == null) {
				typeList.add("null");
			} else {
				typeList.add(objectValueString(value) + "(" + value.getClass().getSimpleName() + ")");
			}
		}
		final String parameters = typeList.toString();
		return parameters.substring(1, parameters.length() - 1);
	}

	protected String objectValueString(Object value) {
		if (value instanceof Array) {
			try {
				return ArrayUtil.toString(((Array) value).getArray());
			} catch (SQLException e) {
				return value.toString();
			}
		}
		return value.toString();
	}

	protected String getParameterValueString(String sql) {
		if ((sql == null) || ("".equals(sql)))
			return "";

		int questMarkPos = 0;

		try {
			for (int i = 0; i < this.columnValues.size(); i++) {
				questMarkPos = sql.indexOf("?", questMarkPos);
				Object value = this.columnValues.get(i);
				String s = sql.substring(0, questMarkPos);
				String e = sql.substring(questMarkPos + 1);
				if (value == null) {
					sql = s + "null" + e;
					questMarkPos += 4;
				} else {
					String paramType = value.getClass().getName();

					if (paramType.indexOf("java.lang.String") != -1) {
						value = ((String) value).replaceAll("'", "''");
						sql = s + "'" + value + "'" + e;
						questMarkPos += ((String) value).length() + 2;
					} else if (("java.lang.Integer".indexOf(paramType) != -1)
							|| ("java.math.BigDecimal".indexOf(paramType) != -1)) {
						sql = s + value.toString() + e;
						questMarkPos += value.toString().length();
					} else if (("null".indexOf(paramType) != -1) || (paramType == null)) {
						sql = s + null + e;
						questMarkPos += 4;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	protected String getColumnString() {
		return columnNames.toString();
	}

	protected void clearColumnInfo() {
		columnMap.clear();
		columnNames.clear();
		columnValues.clear();
	}

	protected String removeBreakingWhitespace(String original) {
		StringBuilder builder = new StringBuilder();
		String delimeter = "\n\r";
		delimeter = System.lineSeparator();
		StringTokenizer whitespaceStripper = new StringTokenizer(original, delimeter);
		while (whitespaceStripper.hasMoreTokens()) {
			String value = whitespaceStripper.nextToken();
			if (value == null || "".equals(value.trim())) {
				continue;
			}

			builder.append(value);
			builder.append("\n");
		}
		return builder.toString();
	}

	protected boolean isDebugEnabled() {
		return statementLog.isDebugEnabled();
	}

	protected boolean isTraceEnabled() {
		return statementLog.isTraceEnabled();
	}

	protected void debug(String text, boolean input) {
		if (statementLog.isDebugEnabled()) {
			statementLog.debug(prefix(input) + text);
		}
	}

	protected void trace(String text, boolean input) {
		if (statementLog.isTraceEnabled()) {
			statementLog.trace(prefix(input) + text);
		}
	}

	private String prefix(boolean isInput) {
		char[] buffer = new char[queryStack * 2 + 2];
		Arrays.fill(buffer, '=');
		buffer[queryStack * 2 + 1] = ' ';
		if (isInput) {
			buffer[queryStack * 2] = '>';
		} else {
			buffer[0] = '<';
		}
		return new String(buffer);
	}

}
