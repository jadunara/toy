package kr.faz.app.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
	public static String exceptionTraceToString(Exception e) {
		StringBuffer sb = new StringBuffer();
		sb.append(e.toString());
		sb.append("  ");
		sb.append(e.getMessage());
		sb.append("\n");

		StackTraceElement[] st = e.getStackTrace();
		for (int i = 0; i < st.length; i++) {
			StackTraceElement s = st[i];
			sb.append("  at ");

			sb.append(s.getClassName());

			if (s.isNativeMethod()) {

			} else {
				sb.append(".");
				sb.append(s.getMethodName());
				sb.append(" Line ( ");
				sb.append(s.getLineNumber());
				sb.append(" )");
			}

			sb.append(" : ");
			sb.append(e.getMessage());
			sb.append(System.lineSeparator());
		}
		sb.append("\n");

		return sb.toString();
	}

	public static String getPrintStackTrace(Exception e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter( errors ) );
		return errors.toString();
	}
}
