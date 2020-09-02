package kr.faz.app.utils;

import java.util.Date;

/******************************************
 * SQLite Utils.
 * @author ktnam
 *
 ******************************************/
public class SqliteUtils {
	public static void main(String[] args) {
		System.out.println("test " + new Date());
		String x = getTableInfoSQL("x");
		System.out.println(x);
	}

	/*************************
	 * 테이블을 조회하는 SQL.
	 * @param tableName
	 * @return
	 */
	public static String getTableInfoSQL(String tableName) {
		String s = String.format("PRAGMA \"main\".TABLE_INFO(\"%s\") ", tableName);
		return s;
	}
}
