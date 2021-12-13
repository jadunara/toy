package kr.faz.app.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class RegexExample {
	public static void main(String[] args) {
		RegexExample re = new RegexExample();
		String sql = "C:\\STS\\sts-3.9.11.RELEASE\\WSC\\toy-projects\\src\\main\\resources\\sample.sql";
		File file = new File(sql);
		try {
			String data = FileUtils.readFileToString(file , "UTF-8");
//			System.out.println(data);
			data = re.process(data);
//			System.out.println(replaceGroup("([a-z]+)([0-9]+)([a-z]+)" , data , 1, "%"));
//			System.out.println(replaceGroup( "(\\b\\d+\\b)" , data , 1, "%"));
//			System.out.println(replaceGroup("([a-z])(\\d)"             , "a1b2c3d4e5", 2, 4, "!!!"));
			System.out.println("--------------------------------result---------------------------------------");
			System.out.println(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private String process(String sql) {
		String p = "\\d\\d?\\d?";
		p = "\\b\\d\\d?\\d?\\d?\\d?\\d?\\d?\\d?\\b";
		p = "(\\b\\d+(?:\\.\\d+)\\b?)";
		p = "(\\p{Digit}+)"; //      25 [1123, 17, 01, 01, 1, 2, 3, 4, 5, 02, 1, 02, 01, 1, 2, 5, 1, 3, 5, 2, 333123, 4, 55555555555555555555555555, 3, 4]
		p = "(\\b\\p{Digit}+\\b)"; //25 [1123, 17, 1, 2, 3, 4, 5, 1, 1, 2, 3786, 1, 3, 5, 2, 333123, 4, 55555555555555555555555555, 3, 4]
		p = "(\\b\\p{Digit}+\\b)"; //20 [1123, 17, 1, 2, 3, 4, 5, 1, 1, 2, 3786, 1, 3, 5, 2, 333123, 4, 55555555555555555555555555, 3, 4]
		p = "(\\b(\\p{Digit}+)\\b)";   //20 [1123, 17, 1, 2, 3, 4, 5, 1, 1, 2, 3786, 1, 3, 5, 2, 333123, 4, 55555555555555555555555555, 3, 4]
		p = "((\\b\\d+(?:\\.\\d+)?))"; //18 [1123, 17, 1, 2, 3, 4, 5, 1, 1, 2.3786, 1, 3.5, 2, 333123, 4, 55555555555555555555555555, 3, 4]
		p = "((\\b\\d+(?:\\.\\d+)?\\b))";
		p = "\\b((\\d)*\\d)\\b";
		p = "(\\+|-)?([0-9]+(\\.[0-9]+))";// fail
		p = "(\\+|-)?([0-9]+(\\.[0-9]+))";//소수점 찾 - fail
		p = "^-?[0-9]{1,12}(?:\\.[0-9]{1,4})?$";//소수점 찾 - fail
		p = "\\b[+-]?\\d+\\.\\d+\\b";
		p = "([^\\D]*\\b\\d+\\b)";
		p = "((\\b\\d+\\b))";
		p = "(^\\D+(\\b\\w+\\b))";
		p = "\\/\\*(.*?)\\*\\/";
		sql = sql.replaceAll("\n", "＠LF＠");
		sql = sql.replaceAll("\r", "＠CR＠");

		Map<String, String> map = new LinkedHashMap<String, String>();
		List<String> list = regex(sql, p);
		if ( list.size() > 50 )
			System.exit(-1);
		//list = regex(sql, "\\/\\*(.*)\\*\\/" );
//		sql = regexFakeReplace( list , map , sql , "_COMMENT_" );

		//list = regex(sql, "\\'(.*?)\\'" );
//		sql = regexFakeReplace( list , map , sql , "_SINGLE_QUOTE_");

//		list = regex(sql, p );
		sql = regexFakeReplace( list , map , sql , "_NUMBER_");
		sql = sql.replaceAll( "＠LF＠" , "\n");
		sql = sql.replaceAll( "＠CR＠" , "\r");

		return sql;
	}

	private List<String> regex( String s, String p){
		Pattern pattern = Pattern.compile( p , Pattern.MULTILINE );
		Matcher matcher = pattern.matcher(s );

		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			String x = matcher.group();
			System.out.println(String.format("while position - matcher value [%s] , [%s] , [%s] , [%s] , [%s]"
					                , x
					                , matcher.regionStart()
					                , matcher.regionEnd()
					                , matcher.start()
					                , matcher.end()
					                )
					);
			//matcher.replaceFirst( "[" + x  + "]");
			list.add(x);
		}
		System.out.println("-----regex list result --------" + list.size() + " >>" +  Arrays.toString( list.toArray() ) );
		return list;
	}
	private String regexFakeReplace(List<String> list, Map<String, String> map, String sql, String prefix) {
		if ( list.size() != 0 ) {
			for ( int i = 0 ; i < list.size() ; i++ ) {
				String k = prefix + "@@@@@" + i + "@@@@@";
				String v =  list.get(i);
				sql = sql.replace( v ,  k );
				map.put(k, v);
			}
		}
		return sql;
	}
	public static String replaceGroup(String regex, String source, int groupToReplace, String replacement) {
	    return replaceGroup(regex, source, groupToReplace, 1, replacement);
	}

	public static String replaceGroup(String regex, String source, int groupToReplace, int groupOccurrence, String replacement) {
	    Matcher m = Pattern.compile(regex).matcher(source);
	    for (int i = 0; i < groupOccurrence; i++)
	        if (!m.find()) return source; // pattern not met, may also throw an exception here
//	    return new StringBuilder(source).replace(m.start(groupToReplace), m.end(groupToReplace), replacement).toString();
	    return new StringBuilder(source).replace(m.start(groupToReplace), m.end(groupToReplace), replacement ).toString();
	}

}
