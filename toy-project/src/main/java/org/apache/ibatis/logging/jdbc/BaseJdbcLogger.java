/**
 *    Copyright 2009-2015 the original author or authors.
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
package org.apache.ibatis.logging.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.logging.Log;

/**
 * MyBatis 3.3.0
 * Base class for proxies to do logging
 * 
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public abstract class BaseJdbcLogger {

	public static final String COLOR_TYPE = "_color_type";
	public static final String COLOR_CODE = "_color_code";
	public static final String COLOR_REPLACE = "_color_replace";
	protected static int resultSetLoggerCount = 30;

	protected static final Set<String> SET_METHODS = new HashSet<String>();
	protected static final Set<String> EXECUTE_METHODS = new HashSet<String>();
	private static Map<String, String> ruleSet = new HashMap<String, String>();

	private Map<Object, Object> columnMap = new HashMap<Object, Object>();

	private List<Object> columnNames = new ArrayList<Object>();
	private List<Object> columnValues = new ArrayList<Object>();

	protected Log statementLog;
	protected int queryStack;

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
		SET_METHODS.add("setString");
		SET_METHODS.add("setInt");
		SET_METHODS.add("setByte");
		SET_METHODS.add("setShort");
		SET_METHODS.add("setLong");
		SET_METHODS.add("setDouble");
		SET_METHODS.add("setFloat");
		SET_METHODS.add("setTimestamp");
		SET_METHODS.add("setDate");
		SET_METHODS.add("setTime");
		SET_METHODS.add("setArray");
		SET_METHODS.add("setBigDecimal");
		SET_METHODS.add("setAsciiStream");
		SET_METHODS.add("setBinaryStream");
		SET_METHODS.add("setBlob");
		SET_METHODS.add("setBoolean");
		SET_METHODS.add("setBytes");
		SET_METHODS.add("setCharacterStream");
		SET_METHODS.add("setClob");
		SET_METHODS.add("setObject");
		SET_METHODS.add("setNull");

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
	public void setRule() {
		if ( ruleSet.size() != 0 ) {
			return;
		}

		String rule = "";
		File ruleFile = new File("/STS/sql.color.rule");
		try {
			rule = FileUtils.readFileToString(ruleFile, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		String[] rules = rule.split("\n");
		for ( String s : rules ) {
			if ( s == null || s.trim().length() == 0 || s.trim().startsWith("#")) {
				continue;
			}
			String[] x = s.split("[:]");
			String prefixKey = x[0].trim();
			//resultSetLogger 에서 출력할 갯수.
			if ( "RESULT_SET_LOGGER_COUNT".equals( prefixKey ) ) {
				try {
					int rCnt = Integer.parseInt( x[1].trim() ) ;
					resultSetLoggerCount = rCnt;
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
			}
			try {
				ruleSet.put(prefixKey  , prefixKey );//key 찾기용.
				ruleSet.put(prefixKey + COLOR_TYPE , x[1].trim() );//컬러 type
				ruleSet.put(prefixKey + COLOR_CODE , x[2].trim() );//컬러 code
				ruleSet.put(prefixKey + COLOR_REPLACE  , x[3].trim() );//보여주고 싶은 형태...
			} catch (Exception e) {
				System.err.println("-mybatis color error key word - " + prefixKey);
			}
		}
	}

	protected Object getColumn(Object key) {
		return columnMap.get(key);
	}

	protected String getParameterValueString() {
		List<Object> typeList = new ArrayList<Object>(columnValues.size());
		for (Object value : columnValues) {
			if (value == null) {
				typeList.add("null");
			} else {
				typeList.add(value + "(" + value.getClass().getSimpleName() + ")");
			}
		}
		final String parameters = typeList.toString();
		return parameters.substring(1, parameters.length() - 1);
	}

	public List<String> regex( String s, String p){
		Pattern pattern = Pattern.compile( p );
		Matcher matcher = pattern.matcher(s);

		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;
	}
	public String removeEmptyLine(String s) {
		return s.replaceAll("(?m)^[ \t]*\r?\n", "");
	}
	protected String regexFakeReplace(List<String> list, Map<String, String> commentMap, String sql, String prefix) {
		if ( list.size() != 0 ) {
			for ( int i = 0 ; i < list.size() ; i++ ) {
				String k = prefix + "@@@@@" + i + "@@@@@";
				String v =  list.get(i);
				int sPos = sql.indexOf(v);
				sql = sql.substring(0, sPos) + k + sql.substring(sPos + v.length());
				commentMap.put(k, v);
			}
		}
		return sql;
	}

	private String repareReplace(String s, Map<String, String> map) {
		Iterator<Entry<String, String>> itx = map.entrySet().iterator();
		while (itx.hasNext()) {
			Map.Entry<java.lang.String, java.lang.String> entry = itx.next();
			String k = entry.getKey();
			String v = entry.getValue();
			String sColor = "";
			if ( k.startsWith("_COMMENT_")) {
				sColor = "\033[0;32m";
			}else if ( k.startsWith("_SINGLE_QUOTE_")) {
				sColor = "\033[3;95m";
			}
			//s = s.replace( k ,  sColor + v + RESET );
			
			int sPos = s.indexOf( k );
			s = s.substring(0, sPos) + sColor + v + RESET + s.substring(sPos + k.length());
			
		}
		return s;
	}

	public static void main(String[] args) {
		PreparedStatementLogger bj = new  PreparedStatementLogger(null, null, 0, null);
		String file = "C:\\STS\\sts-3.9.11.RELEASE\\WSC\\toy-projects\\src\\main\\resources\\sample.sql";
		try {
			String sql = FileUtils.readFileToString(new File(file) , "UTF-8");
			sql = bj.getParameterValueString(sql);
			System.out.println(sql);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	protected String getParameterValueString(String sql) {
		setRule();
		if ((sql == null) || ("".equals(sql)))
			return "";

		String p = "/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/";
		Map<String, String> commentMap = new LinkedHashMap<String, String>();
		List<String> list = regex(sql, p);

		/******************************
		 * 주석 처리
		 ******************************/
		sql = regexFakeReplace( list , commentMap , sql , "_COMMENT_" );

		p = "\\'(.*?)\\'";
		list = regex(sql, p);

		/******************************
		 * 주석 처리
		 ******************************/
		sql = regexFakeReplace( list , commentMap , sql , "_SINGLE_QUOTE_");

		sql = removeEmptyLine(sql);
		int questMarkPos = 0;

		try {
			for (int i = 0; i < this.columnValues.size(); i++) {
				questMarkPos = sql.indexOf("?", questMarkPos);
				Object value = this.columnValues.get(i);
				String s = sql.substring(0, questMarkPos);
				String e = sql.substring(questMarkPos + 1);
				String sval = "";
				sql = s ;
				if (value == null) {
					sval = "NULL /** null value **/"  ;

				} else {
					String paramType = value.getClass().getName();

					if (paramType.indexOf("java.lang.String") != -1) {
						sval = "'" + ((String) value).replaceAll("'", "''") + "'";

					} else if (("java.lang.Integer".indexOf(paramType) != -1)  || ("java.math.BigDecimal".indexOf(paramType) != -1)) {
						sval = value.toString();

					} else if ((paramType.indexOf("Timestamp") != -1)  ) {
						sval = "'" + value.toString()  + "'"    ;

					} else if ((paramType.indexOf("Boolean") != -1)  ) {
						sval = value.toString();

					} else if (("null".indexOf(paramType) != -1) || (paramType == null)) {
						sval =  " "+ null +" ";
						paramType = " (null) ";
					} else {
						sval = value.toString();
					}

					sql = s + "\033[0;103m" + sval + RESET +  " /** " + paramType + " **/ " +  e ;
					questMarkPos += sval.length() + paramType.length() + 10 /** 주석 처리 부분 **/ + 8 /** escape 문자 부분.**/ ;
				}
			}

			sql = setSQLColorApply(sql);
			sql = repareReplace(sql, commentMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		commentMap.clear();
		commentMap= null;
		return sql;
	}


	private String setSQLColorApply(String sql) {
		if (ruleSet.size() == 0 )
			return sql;

		String delim = "()\r\n\t. ";
		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(sql, delim, true);
		while(st.hasMoreElements()) {
			String v = (String) st.nextElement();
			if (v.length() > 1 && ruleSet.containsKey(v.toUpperCase()))  {
				String key = v.toUpperCase();
				String colorType = ruleSet.get(key + COLOR_TYPE );
				String colorCode = ruleSet.get(key + COLOR_CODE);
				String replaceData = ruleSet.get(key + COLOR_REPLACE);
				v =   "\033[" + colorType + ";" + colorCode + "m" + replaceData  +  RESET;
			}
			sb.append(v);
		}
		return sb.toString();
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
		//delimeter = System.lineSeparator();
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
    // Reset
    public static final String RESET = "\033[0m  ";  // Text Reset
}
