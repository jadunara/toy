package kr.faz.app.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class SQLRelaceComment {
	public static void main(String[] args) throws IOException {
		File sqlFile = new File("C:\\STS\\sts-3.9.11.RELEASE\\WSC\\toy-projects\\src\\main\\resources\\sample.sql");
		String data = FileUtils.readFileToString(sqlFile, "UTF-8");
		new SQLRelaceComment().process(data);
	}

	public void process(String sql) {
		String orgSql = sql ;
		Map<String, String> map = new HashMap<String, String>();

		String p = getRegex("MULTI_LINE_COMMENT");
		List<String> list = regexFindToList(sql, p);
		sql = fakeComment(sql, map, list);
		System.out.println("-------------------------------------------------------\n\n");
		System.out.println(sql);
		System.out.println("-------------------------------------------------------\n\n");
		sql = repareCommnet(sql, map);
		System.out.println("-------------------------------------------------------\n\n");
		System.out.println(sql);
		System.out.println("result : " + orgSql.equals(sql));
	}

	public String getRegex(String t) {
		if ( "MULTI_LINE_COMMENT".equals(t) ) {
			return "/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/";
		}
		return null;
	}
	
	/*******************************************
	 * 찾는 문자열을 임의의 형태로 변경함.
	 * @param s 대상 문자열
	 * @param map 임의의 형태 보관하는 map
	 * @param list 변경대상 문자열 list
	 * @return
	 */
	public String fakeComment(String s, Map<String, String> map, List<String> list) {
		for (int i = 0 ; i < list.size() ; i++ ) {
			System.out.println(list.get(i));
			String key = "@@@@@@@" + i + "@@@@@@@";
			String val = list.get(i);
			s = StringUtils.replace(s, val , key);
			map.put(key , val);
		}
		return s;
	}

	/****************************************************
	 * 변형된 코멘트를 원래의 문자로 복원한다.
	 * @param s 변형된 문자열
	 * @param map 코멘트를 가지고 있는 map
	 * @return
	 */
	public String repareCommnet(String s, Map<String, String> map) {
		Iterator<Entry<String, String>> itx = map.entrySet().iterator();
		while (itx.hasNext()) {
			Entry<String, String> et = itx.next();
			s = StringUtils.replaceOnce(s, et.getKey(), et.getValue());
		}
		return s ;
	}

	/************************************************
	 * 정규식 찾은 데이터를 List 로 반환한다.
	 * @param s 찾을 대상 문자열
	 * @param p 찾을 정규식
	 * @return
	 ************************************************/
	public List<String> regexFindToList( String s, String p){
		Pattern pattern = Pattern.compile( p );
		Matcher matcher = pattern.matcher(s);

		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;
	}
}
