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
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.ibatis.logging.Log;

/**
 * MyBatis 3.3.0
 * Base class for proxies to do logging
 * 
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public abstract class BaseJdbcLogger {

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
		File ruleFile = new File("C:\\STS\\sts-3.9.11.RELEASE\\WSC\\toy-projects\\sql.color.rule");
		try {
			rule = FileUtils.readFileToString(ruleFile, "UTF-8");
		} catch (IOException e) {

			e.printStackTrace();
		}

		String[] rules = rule.split("\n");
		for ( String s : rules ) {
			String[] x = s.split("[:]");
			ruleSet.put(x[0] , x[1].trim());
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
	protected String regexFakeReplace(List<String> list, Map<String, String> commentMap, String sql, String prefix) {
		if ( list.size() != 0 ) {
			for ( int i = 0 ; i < list.size() ; i++ ) {
				String k = prefix + "@@@@@" + i + "@@@@@";
				String v =  list.get(i);
				sql = sql.replace( v ,  k );
				commentMap.put(k, v);
			}
		}
		return sql;
	}

	private String repareReplace(String sql, Map<String, String> map) {
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

			sql = sql.replace( k ,  sColor + v + RESET );
		}
		return sql;
	}

	protected String getParameterValueString(String sql) {
		setRule();
		if ((sql == null) || ("".equals(sql)))
			return "";

		String p = "\\/\\*(.*)\\*\\/";
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

		/******************************
		 * 원복 처리.
		 ******************************/
		sql = repareReplace(sql , commentMap);

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
					sval = "null"  ;

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
		} catch (Exception e) {
			e.printStackTrace();
		}

		commentMap.clear();
		commentMap= null;
		return sql;
	}


	private String setSQLColorApply(String sql) {
		String delim = "()\r\n\t. ";
		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(sql, delim, true);
		while(st.hasMoreElements()) {
			String v = (String) st.nextElement();
			if (v.length() > 1 && ruleSet.containsKey(v.toUpperCase()))  {
				v = StringEscapeUtils.unescapeJava( ruleSet.get(v.toUpperCase() ) )  +  "\033[0m";
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
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK		= "\033[0;30m";   // BLACK
    public static final String RED		= "\033[0;31m";     // RED
    public static final String GREEN		= "\033[0;32m";   // GREEN
    public static final String YELLOW	= "\033[0;33m";  // YELLOW
    public static final String BLUE		= "\033[0;34m";    // BLUE
    public static final String PURPLE	= "\033[0;35m";  // PURPLE
    public static final String CYAN		= "\033[0;36m";    // CYAN
    public static final String WHITE		= "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD	= "\033[1;30m";  // BLACK
    public static final String RED_BOLD		= "\033[1;31m";    // RED
    public static final String GREEN_BOLD	= "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD	= "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD		= "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD	= "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD		= "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD	= "\033[1;37m";  // WHITE

    // Underline
    public static final String BLACK_UNDERLINED		= "\033[4;30m";  // BLACK
    public static final String RED_UNDERLINED		= "\033[4;31m";    // RED
    public static final String GREEN_UNDERLINED		= "\033[4;32m";  // GREEN
    public static final String YELLOW_UNDERLINED	= "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED		= "\033[4;34m";   // BLUE
    public static final String PURPLE_UNDERLINED	= "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED		= "\033[4;36m";   // CYAN
    public static final String WHITE_UNDERLINED		= "\033[4;37m";  // WHITE

    // Background
    public static final String BLACK_BACKGROUND		= "\033[40m";  // BLACK
    public static final String RED_BACKGROUND		= "\033[41m";    // RED
    public static final String GREEN_BACKGROUND		= "\033[42m";  // GREEN
    public static final String YELLOW_BACKGROUND	= "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND		= "\033[44m";   // BLUE
    public static final String PURPLE_BACKGROUND	= "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND		= "\033[46m";   // CYAN
    public static final String WHITE_BACKGROUND		= "\033[47m";  // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT		= "\033[0;90m";  // BLACK
    public static final String RED_BRIGHT		= "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT		= "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT	= "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT		= "\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT	= "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT		= "\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT		= "\033[0;97m";  // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT	= "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT		= "\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT	= "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT	= "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT		= "\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT	= "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT		= "\033[1;96m";  // CYAN
    public static final String WHITE_BOLD_BRIGHT	= "\033[1;97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT		= "\033[0;100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT		= "\033[0;101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT		= "\033[0;102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT	= "\033[0;103m";// YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT		= "\033[0;104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT	= "\033[0;105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT		= "\033[0;106m";  // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT		= "\033[0;107m";   // WHITE
}
