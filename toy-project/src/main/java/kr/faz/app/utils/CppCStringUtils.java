package kr.faz.app.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CppCStringUtils {
	public static String replace_sql_format(String s, List<String> list) {
		if ( list.size() == 0 ) {
			return s;
		}
		for ( int i = 0; i < list.size(); i++ ) {
			String x = list.get(i);
			int spos = s.indexOf(x);
			String _pre = s.substring(0, spos);
			String _sub = s.substring( spos + x.length() );
			_sub = _sub == null?"":_sub;
			String _mid = " = \"\"\n";
			s = _pre + _mid +  _sub.trim();
		}
		return s;
	}
	public static String insert_sprintf(String sql) {
		String s[] = sql.split("\n");
		String lvStr = s[0].split("=")[0].trim();

		String str = s[s.length-1];
		int commaPos = str.indexOf(",");
		String _pre = str.substring( 0, commaPos);
		String _sub = str.substring(commaPos + 1);
		String _mid = ";\n"+ lvStr +" = " + lvStr + ".sprintf(";
		StringBuffer sb = new StringBuffer();
		
		for ( int i = 0 ; i < s.length-1 ; i++ ) {
			sb.append(s[i]);
			sb.append("\n");
		}
		sb.append( _pre + _mid + _sub);
		return sb.toString() ;
	}
	public static  String regex_sql_format(String s) {
		if ( StringUtils.isEmpty( s )) {
			return s;
		}

		String prefix = "\\.Format" ;
		String subfix = "(.*?)\\(";
		List<String> list =  RegexpUtils.regex( s,  "(" + prefix +"(.*?)" +  subfix + ")" );
		System.out.println("\n\n\n  =>  .Format " + list);
		s = replace_sql_format(s, list);

		return s;
	}
	public static String regex_T(String s) {
		String prefix = "_T(.*?)\\((.*?)\"" ;
		String subfix = "\"(.*?)\\)";
		List<String> list =  RegexpUtils.regex( s,  "(" + prefix +"(.*?)" +  subfix + ")" );
		s = replace_T(s , list);
		return s;
	}
	public static List<String> getRegExp_List_T(String s) {
		String prefix = "_T(.*?)\\((.*?)\"" ;
		String subfix = "\"(.*?)\\)";
		List<String> list =  RegexpUtils.regex( s,  "(" + prefix +"(.*?)" +  subfix + ")" );
		return list;
	}

	public static String replace_word_T( String s ) {
		s = s.substring(s.indexOf( "\"")   );
		s = s.substring( 0 , s.lastIndexOf("\"") +1 );
		return s;
	}

	public static String replace_word_Left(String s ) {
		s = s.replace("Left", "substr");
		s = s.replace("(", "( 0 , ");
		return s;
	}
	public static String replace_word_Mid(String s ) {
		s = s.replace("Mid", "substr");
		return s;
	}
	public static String replace_word_Mask(String s ) {
		s = s.replace("Mask", "Mask");//TODO
		return s;
	}

	public static String replace_T(String s, List<String> list) {
		if ( list.size() == 0 ) {
			return s;
		}

		for ( int i = 0; i < list.size(); i++ ) {
			String x = list.get(i);
			int spos = s.indexOf(x);
			if ( spos == -1 ) {
				System.out.println(" ------------> " + x);
			}
			String _pre = s.substring(0, spos);
			String _sub = s.substring( spos + x.length() );
			String _mid = s.substring(spos , spos +  x.length()  );

			_mid = _mid.substring(_mid.indexOf( "\"")   );
			_mid = _mid.substring( 0 , _mid.lastIndexOf("\"") +1 );
			s = _pre + _mid +  _sub;
		}
		return s;
	}

	public static String regex_Left(String s) {
		List<String> list = getRegex_List_Left(s);
		s = replace_Left(s , list);
		return s;
	}

	public static List<String> getRegex_List_Left(String s) {
		String prefix = "\\.Left" ;
		String subfix = "(.*?)\\)";
		List<String> list =  RegexpUtils.regex( s,  "(" + prefix +"(.*?)" +  subfix + ")" );
		return list;
	}

	public static List<String> getRegex_List_Mid(String s) {
		String prefix = "\\.Mid" ;
		String subfix = "(.*?)\\)";
		List<String> list =  RegexpUtils.regex( s,  "(" + prefix +"(.*?)" +  subfix + ")" );
		return list;
	}
	public static List<String> getMask(String s){
		List<String> list = null;
		
		return list;
	}

	public static String regex_Mid(String s) {
		List<String> list =  getRegex_List_Mid(s);
		s = replace_Mid(s , list);
		return s;
	}

	public static String replace_Left(String s, List<String> list) {
		if ( list.size() == 0 ) {
			return s;
		}
		for ( int i = 0; i < list.size(); i++ ) {
			String x = list.get(i);
			int spos = s.indexOf(x);
			if ( spos == -1 ) {
				System.out.println(" ------------> " + x);
			}

			String _pre = s.substring(0, spos);
			String _sub = s.substring( spos + x.length() );
			String _mid = s.substring(spos , spos +  x.length()  );
			_mid = _mid.replace("Left", "substr");
			_mid = _mid.replace("(", "( 0 , ");
			s = _pre + _mid +  _sub;
		}
		return s;
	}


	public static String replace_Mid(String s, List<String> list) {
		if ( list.size() == 0 ) {
			return s;
		}
		for ( int i = 0; i < list.size(); i++ ) {
			String x = list.get(i);
			int spos = s.indexOf(x);
			String _pre = s.substring(0, spos);
			String _sub = s.substring( spos + x.length() );
			String _mid = s.substring(spos , spos +  x.length()  );
			_mid = _mid.replace("Mid", "substr");
			s = _pre + _mid +  _sub;
		}
		return s;
	}

	/***************************************************
	 * SQL 구문에서 우측에 채워질 문자.
	 * @param _sql
	 * @param maxLength
	 * @return
	 ***************************************************/
	public static String sql_fill_space(String _sql, int maxLength , String fillString) {
		String[] a = _sql.split("\n");
		a[0] = "\t" + a[0];
		a[a.length-1] = "\t" + a[a.length-1];
		a[a.length-2] = "\t" + a[a.length-2];
		for ( int i = 1 ; i < a.length-2 ; i++ ) {
			String s = a[i];
			s = "\t\t+\"\\n " + s.substring( 1 );
			int fstDblQtPos = s.indexOf("\"");
			int dqpos = s.indexOf("\"" , fstDblQtPos + 1 );

			String _mid = s.substring(0,   dqpos ) ;
			String _sub = s.substring( dqpos+1 );
			_mid = _mid + StringUtils.repeat( fillString , maxLength - _mid.toCharArray().length );
			a[i] = _mid + "\".rtrim()  " +  _sub.substring(1).trim() ;
		}

		StringBuffer sb = new StringBuffer();
		for ( int i =  0 ; i < a.length ; i++ ) {
			sb.append(a[i]);
			sb.append("\n");
		}
		return sb.toString();
	}
}
