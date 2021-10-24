package kr.faz.app.utils;

public class SizeUtil {
	public static String sizeHuman(long v) {

		if ( v < (long)Math.pow(1024, 1) ) {
			return v + "";

		} else if ( v < (long)Math.pow(1024, 2)) {//killo byte
			return sizeMark(v , 2 , "K") ;

		} else if ( v < (long)Math.pow(1024, 3)) {//mega byte
			return sizeMark(v , 3 , "M") ;

		} else if ( v < (long)Math.pow(1024, 4)) {//giga byte
			return sizeMark(v , 4 , "G") ;

		} else if ( v < (long)Math.pow(1024, 5)) {//tera byte
			return sizeMark(v , 5 , "T");

		} else if ( v < (long)Math.pow(1024, 6)) {//peta byte
			return sizeMark(v , 6 , "P");

		} else if ( v < (long)Math.pow(1024, 7)) {//exa byte
			return sizeMark(v , 7 , "E") ;

		} else if ( v < (long)Math.pow(1024, 8)) {//zetta byte
			return sizeMark(v , 8 , "Z") ;

		} else {//Yotta byte
			return sizeMark(v , 9 , "Y");
		}
	}

	/********************************************************
	 * Symbol Mark 와 소수점 2자리 까지 처리함.
	 * @param v long value
	 * @param p 제곱되는 값(power)
	 * @param symbolMark
	 * @return
	 ********************************************************/
	public static String sizeMark(long v , int p , String symbolMark) {
		 double d = (v / Math.pow(1024, p) );

		 if ( (d+"").endsWith(".0")) {
			 return String.format("%d %s",  (long)d , symbolMark );
		 } else {
			 return String.format("%.2f %s",  d , symbolMark );
		 }
	}
}
