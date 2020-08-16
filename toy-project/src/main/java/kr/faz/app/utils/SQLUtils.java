package kr.faz.app.utils;

public class SQLUtils {
	public static int getSqlStringMaxLength(String s) {
		String[] a = s.split("\n");
		int maxLength = 0;
		for ( int i = 0 ; i < a.length ; i++  ) {
			int lastLen = a[i].lastIndexOf("\"");//주석제외

			if ( lastLen > maxLength) {
				maxLength = lastLen ;
			}
		}
		return maxLength;
	}
}
