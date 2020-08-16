package kr.faz.app.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpUtils {
	/******************************************************
	 * 정규식
	 * @param s 찾고자 하는 대상 문자열.
	 * @param p 정규식 문자열.
	 * @return 찾게 되는 문자열.
	 ******************************************************/
	public static List<String> regex( String s, String p){
		Pattern pattern = Pattern.compile( p );
		Matcher matcher = pattern.matcher(s);

		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;
	}
}
