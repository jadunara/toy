package kr.faz.app.test;

import kr.faz.app.utils.ExceptionUtils;

public class TestException {
	public static void main(String[] args) {
		try {
			int i = 0 ;
			int j = 0 ;
			System.out.println(i / j);
		} catch (Exception e) {
			e.printStackTrace();
			String x = ExceptionUtils.exceptionTraceToString(e);
			System.out.println("-----------------------------------------------");
			System.out.println(x);
		}
	}
}