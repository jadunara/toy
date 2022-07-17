package kr.faz.app.example;

import org.apache.commons.lang3.StringUtils;

public class StringCaseExample {
	public static void main(String[] args) {
		StringCaseExample sc = new StringCaseExample();
		System.out.println(sc.camelToSnake("abCXy"));
	}

	public String camelToSnake(String str) {
		String result = "";
		if ( StringUtils.isAllUpperCase(str.replace("_", "" ) )) {
			return str;
		}

		char c = str.charAt(0);
		result = result + Character.toLowerCase(c);

		for (int i = 1; i < str.length(); i++) {

			char ch = str.charAt(i);

			if (Character.isUpperCase(ch) || '.' == ch) {
				result = result + '_';
				result = result + Character.toLowerCase(ch);
			} else {
				result = result + ch;
			}
		}

		return result.toUpperCase();
	}
}
