package kr.faz.app.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

@Intercepts({
			  @Signature(type = Executor.class, method = "query"  , args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class })
			, @Signature(type = Executor.class, method = "update" , args = { MappedStatement.class, Object.class  })
			}
		)

/*****************
 * <plugins> <plugin interceptor="com.kimchi.util.MybatisInterceptor"></plugin> </plugins>
 * 
 * @author ktnam
 *
 */
public class MybatisInterceptor implements Interceptor {
	private boolean IS_SUBMIT = true;
	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		// QueryId
//		String queryID = ((MappedStatement) invocation.getArgs()[0]).getId();

		// Query Parameter
		Object param = invocation.getArgs()[1];

		MappedStatement ms = ((MappedStatement) invocation.getArgs()[0]);
		String queryID = ms.getId();

		//TODO is skip

		try {

			//TODO -- Line comment 는 문제가 된다...2022.07.28
			List<ValType> metaList = new ArrayList<ValType>();
			
			String sql = setVariableMapping( param , metaList , ms);
			
			if ( IS_SUBMIT ) {
				//TODO header
				//sql = header + sql
				sql = setHeaderData(metaList) + sql ;
			}
			
			if ( IS_SUBMIT) {
				//TODO footer
				//sql += footer
				sql += setFooterData () ;
			}

			System.out.println(sql);

		} catch (Exception e) {
			System.err.println("ERROR queryID - " + queryID  + "   , " + e.getMessage() );
			e.printStackTrace();
		}

		Object px = invocation.proceed();
		return px;
	}

	private String setHeaderData(List<ValType> metaList) {
		// TODO Auto-generated method stub
		return null;
	}

	private String setFooterData() {
		// TODO Auto-generated method stub
		return null;
	}

	private String setVariableMapping( Object param, List<ValType> metaList, MappedStatement ms) throws IllegalArgumentException, IllegalAccessException {
		String queryID = ms.getId();

		BoundSql bs = ms.getBoundSql(param);
		// Query String
		String sql = ms.getBoundSql(param).getSql();

		sql = sql.replaceAll("(?m)^[ \t]*\r?\n", "");

		List<ParameterMapping> mappings = bs.getParameterMappings();

		SqlCommandType sqlCommentType = ms.getSqlCommandType();

		String pattern = getRegex("MULTI_LINE_COMMENT");
		List<String> list = regexFindToList(sql, pattern);
		
		Map<String, String> map = new HashMap<String, String>();
		sql = fakeComment(sql, map, list);
		sql = replaceSqlCommentType(sql, sqlCommentType , queryID );

		int sPos = 0;
		for (int i = 0; i < mappings.size(); i++) {
			ParameterMapping p = mappings.get(i);

			String property = p.getProperty();

			sPos = sql.indexOf("?", sPos);
			String preStr = sql.substring(0, sPos);
			String subStr = sql.substring(sPos + 1);
			String valStr = "";

			valStr = getValue(p , param , metaList, bs);

			if ( IS_SUBMIT ) {
				sql = preStr + " :" + camelToSnake(property) + subStr;
			} else {
				sql = preStr + valStr + subStr;
			}
		}
		
		sql = repareCommnet(sql, map);

		return sql;
	}

	private String getValue(ParameterMapping p, Object param, List<ValType> metaList, BoundSql bs) throws IllegalArgumentException, IllegalAccessException {
		String className = param.getClass().getName();
		String property = p.getProperty();
		String valStr = "";
		ValType vt = new ValType();

		vt.setName(property);

		if (param instanceof String || param instanceof Integer || param instanceof Long || param instanceof Float || param instanceof Double) {
			valStr = valueToString(param, className, property , p , vt);
		} else if (param instanceof Map) {
			Map paramterObjectMap = (Map)param;
			Object paramValue = null;
			if(bs.hasAdditionalParameter(property)) {
				paramValue = bs.getAdditionalParameter(property);
			} else {
				paramValue = paramterObjectMap.get(property);
			}
			valStr = valueToString( paramValue , className, property , p , vt);
		} else if (param instanceof Date) {
			valStr = valueToString(param, className, property , p , vt);
		} else {
			Class<?> aClass = param.getClass();
			Field declaredField = null;
			try {
				declaredField = aClass.getDeclaredField(property);
			} catch (NoSuchFieldException e) {
				while (aClass.getSuperclass() != null) {
					aClass = aClass.getSuperclass();
					try {
						declaredField = aClass.getDeclaredField(property);
						break;
					} catch (NoSuchFieldException ignored) {
					}
				}
			}

			if (declaredField == null)
				return " NULL /** null object **/ ";

			declaredField.setAccessible(true);
			Object valObj = declaredField.get(param);
			if ("startIndex".equals(property)) {
				System.out.println("startIndex :"   );
			}
			valObj = declaredField.get(param);
			valStr = valueToString(valObj, className, property , p , vt);

		}
		vt.setVal(valStr);
		metaList.add(vt);
		return valStr;
	}

	private class ValType {
		private String name;
		private String type;
		private String val;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
	}
	private String replaceSqlCommentType(String sql, SqlCommandType sqlCommentType, String queryID) {
		if (sqlCommentType.equals ( SqlCommandType.SELECT  ) ) {
			
		} else if (sqlCommentType.equals ( SqlCommandType.DELETE  ) ) {
			
		} else if (sqlCommentType.equals ( SqlCommandType.INSERT  ) || sqlCommentType.equals ( SqlCommandType.UPDATE  ) ) {
			
		} else {
			throw new RuntimeException(String.format("알려지지 않은 SQL Comment TYpe [%s] [%s]", sqlCommentType.toString() , queryID ));
		}

		return sql;
	}

	public String valueToString(Object param, String className, String property, ParameterMapping p, ValType vt) {
		String val = "";
		if (param instanceof String) {
			val = String.format("'%s'", param.toString());
			vt.setType("VARCHAR2");
		} else if (param instanceof Integer || param instanceof Long || param instanceof Float || param instanceof Double) {
			val = String.format("%s", param.toString());
			vt.setType("NUMBER");
		} else if (param == null) {
			val = " NULL /** - NULL **/ ";
		} else {
			val = param.toString() + " /** Other Type Class **/" ;//
		}

		val += String.format(" /** [%s - %s](Mode.%s) **/", property, className , p.getMode());
		return val;
	}

	/******************************************************************
	 * 문자열 내에서 빈 공백 라인들 제거(순수 공백, 줄바꿈, Tab 등)
	 * 
	 * @param s
	 * @return
	 */
	public String removeEmptyLine(String s) {
		return s.replaceAll("(?m)^[ \t]*\r?\n", "");
	}

	public String getRegex(String t) {
		if ("MULTI_LINE_COMMENT".equals(t)) {
			return "/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/";
		}
		return null;
	}

	/*******************************************
	 * 찾는 문자열을 임의의 형태로 변경함.
	 * 
	 * @param s    대상 문자열
	 * @param map  임의의 형태 보관하는 map
	 * @param list 변경대상 문자열 list
	 * @return
	 */
	public String fakeComment(String s, Map<String, String> map, List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			String key = "@@@@@@@" + i + "@@@@@@@";
			String val = list.get(i);
			int sPos = s.indexOf(val);
			s = s.substring(0, sPos) + key + s.substring(sPos + val.length());
			map.put(key, val);
		}
		return s;
	}

	/****************************************************
	 * 변형된 코멘트를 원래의 문자로 복원한다.
	 * 
	 * @param s   변형된 문자열
	 * @param map 코멘트를 가지고 있는 map
	 * @return
	 */
	public String repareCommnet(String s, Map<String, String> map) {
		Iterator<Entry<String, String>> itx = map.entrySet().iterator();
		while (itx.hasNext()) {
			Entry<String, String> et = itx.next();
			int sPos = s.indexOf(et.getKey());
			s = s.substring(0, sPos) + et.getValue() + s.substring(sPos + et.getKey().length());
		}
		return s;
	}

	/************************************************
	 * 정규식 찾은 데이터를 List 로 반환한다.
	 * 
	 * @param s 찾을 대상 문자열
	 * @param p 찾을 정규식
	 * @return
	 ************************************************/
	public List<String> regexFindToList(String s, String p) {
		Pattern pattern = Pattern.compile(p);
		Matcher matcher = pattern.matcher(s);

		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;
	}

	public String camelToSnake(String str) {
		String result = "";
		if (isAllUpperCase(str.replace("_", ""))) {
			return str;
		}

		char c = str.charAt(0);
		result = result + Character.toLowerCase(c);

		for (int i = 1; i < str.length(); i++) {

			char ch = str.charAt(i);

			if (Character.isUpperCase(ch)) {
				result = result + '_';
				result = result + Character.toLowerCase(ch);
			} else if ('.' == ch) {
				result = result + '_';
			} else {
				result = result + ch;
			}
		}

		return result.toUpperCase();
	}
	
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

    /**
     * <p>Checks if the CharSequence contains only uppercase characters.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty String (length()=0) will return {@code false}.</p>
     *
     * <pre>
     * StringUtils.isAllUpperCase(null)   = false
     * StringUtils.isAllUpperCase("")     = false
     * StringUtils.isAllUpperCase("  ")   = false
     * StringUtils.isAllUpperCase("ABC")  = true
     * StringUtils.isAllUpperCase("aBC")  = false
     * StringUtils.isAllUpperCase("A C")  = false
     * StringUtils.isAllUpperCase("A1C")  = false
     * StringUtils.isAllUpperCase("A/C")  = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains uppercase characters, and is non-null
     * @since 2.5
     * @since 3.0 Changed signature from isAllUpperCase(String) to isAllUpperCase(CharSequence)
     */
    public boolean isAllUpperCase(final CharSequence cs) {
        if (cs == null || isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isUpperCase(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the CharSequence.
     * That functionality is available in isBlank().</p>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to isEmpty(CharSequence)
     */
    public boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
