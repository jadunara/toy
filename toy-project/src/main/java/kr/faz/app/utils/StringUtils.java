package kr.faz.app.utils;

import java.io.BufferedReader;
import java.sql.Clob;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public StringUtils() {
	}

	/**
	 * String[] 넘어온 값을 String 로 return 한다, Null 일경우 "" 를 return
	 * @param value
	 * @return
	 */
	public static String getStringArrayAppend(String[] value) {
		if (value == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (String str : value)
			sb.append(str);
		return sb.toString();
	}


	public static String clobToString(Clob clob) throws Exception {
		if (clob == null) {
			return "";
		}

		StringBuffer strOut = new StringBuffer();

		String str = "";

		BufferedReader br = new BufferedReader(clob.getCharacterStream());

		while ((str = br.readLine()) != null) {
			strOut.append(str);
		}
		return strOut.toString();
	}

	public static String replaceWinName(String agent) {
		agent = agent.replaceFirst("Windows NT 5.0", "Windows 2000");
		agent = agent.replaceFirst("Windows NT 5.1", "Windows XP");
		agent = agent.replaceFirst("Windows NT 5.2", "Windows 2003");
		agent = agent.replaceFirst("Windows NT 6.0", "Windows Vista");
		agent = agent.replaceFirst("Windows NT 6.1", "Windows 2008");
		agent = agent.replaceFirst("Windows NT 7.0", "Windows 7");
		return agent;
	}

	public static String getOs(String agent) {
		String os = null;
		if (agent.indexOf("Windows NT 5.0") != -1)
			return "Windows 2000";
		if (agent.indexOf("Windows NT 5.1") != -1)
			return "Windows XP";
		if (agent.indexOf("Windows NT 5.2") != -1)
			return "Windows 2003";
		if (agent.indexOf("Windows NT 6.0") != -1)
			return "Windows Vista";
		if (agent.indexOf("Windows NT 6.1") != -1)
			return "Windows 2008";
		if (agent.indexOf("Windows NT 7.0") != -1)
			return "Windows 7";

		return os;
	}

	public static String replace8859(String str) throws Exception {
		return new String(str.getBytes("8859_1"), "ksc5601");
	}

	public static String replaceUTF8(String str) throws Exception {
		return new String(str.getBytes("8859_1"), "UTF-8");
	}

	public static boolean isUTF8(String str) throws Exception {
		byte[] bytes = str.getBytes("ISO-8859-1");
		return isUTF8(bytes, 0, bytes.length);
	}

	public static boolean isUTF(String str, String charactor) throws Exception {
		byte[] bytes = str.getBytes("ISO-8859-1");
		return isUTF8(bytes, 0, bytes.length);
	}

	public static boolean isUTF8(byte[] buf, int offset, int length) {// http://jongsclub.com/webedit/studyView.jsp?num=28

		boolean yesItIs = false;
		for (int i = offset; i < offset + length; i++) {
			if ((buf[i] & 0xC0) == 0xC0) { // 11xxxxxx 패턴 인지 체크
				int nBytes;
				for (nBytes = 2; nBytes < 8; nBytes++) {
					int mask = 1 << (7 - nBytes);
					if ((buf[i] & mask) == 0)
						break;
				}

				// CJK영역이나 아스키 영역의 경우 110xxxxx 10xxxxxx 패턴으로 올수 없다.
				if (nBytes == 2)
					return false;

				// Check that the following bytes begin with 0b10xxxxxx
				for (int j = 1; j < nBytes; j++) {
					if (i + j >= length || (buf[i + j] & 0xC0) != 0x80)
						return false;
				}

				if (nBytes == 3) {
					// 유니코드 형태로 역치환 해서 0x0800 ~ 0xFFFF 사이의 영역인지 체크한다.
					char c = (char) (((buf[i] & 0x0f) << 12) + ((buf[i + 1] & 0x3F) << 6) + (buf[i + 2] & 0x3F));
					if (!(c >= 0x0800 && c <= 0xFFFF)) {
						return false;
					}
				}

				yesItIs = true;
			}
		}
		return yesItIs;
	}

	/**
	 * Null일 경우 해당 문자로 치완한다.
	 * 
	 * @param strTarget
	 * @param strReplaceStr
	 * @return
	 */
	public static String replaceNull(String strTarget, String strReplaceStr) {
		if (strTarget == null) {
			return strReplaceStr;
		} else {
			return strTarget;
		}
	}

	/**
	 * 주어진 길이(iLength)만큼 주어진 문자(cPadder)를 strSource의 왼쪽에 붙혀서 보내준다.
	 *   ex) lpad("abc", 5, '^') ==> "^^abc"
	 *       lpad("abcdefghi", 5, '^') ==> "abcde" lpad(null, 5, '^') ==> "^^^^^"
	 * @param strSource
	 * @param iLength
	 * @param cPadder
	 */
	public static String lpad(String strSource, int iLength, char cPadder) {
		StringBuffer sbBuffer = null;

		if (!isEmpty(strSource)) {
			int iByteSize = getByteSize(strSource);
			if (iByteSize > iLength) {
				return strSource.substring(0, iLength);
			} else if (iByteSize == iLength) {
				return strSource;
			} else {
				int iPadLength = iLength - iByteSize;
				sbBuffer = new StringBuffer();
				for (int j = 0; j < iPadLength; j++) {
					sbBuffer.append(cPadder);
				}
				sbBuffer.append(strSource);
				return sbBuffer.toString();
			}
		}
		sbBuffer = new StringBuffer();
		for (int j = 0; j < iLength; j++) {
			sbBuffer.append(cPadder);
		}

		return sbBuffer.toString();
	}

	/**
	 * 주어진 길이(iLength)만큼 주어진 문자(cPadder)를 strSource의 오른쪽에 붙혀서 보내준다.
	 *  ex) lpad("abc", 5, '^') ==> "^^abc"
	 *      lpad("abcdefghi", 5, '^') ==> "abcde"
	 *      lpad(null, 5, '^') ==> "^^^^^"
	 *
	 * @param strSource
	 * @param iLength
	 * @param cPadder
	 */
	public static String Rpad(String strSource, int iLength, char cPadder) {
		StringBuffer sbBuffer = null;

		if (!isEmpty(strSource)) {
			int iByteSize = getByteSize(strSource);
			if (iByteSize > iLength) {
				return strSource.substring(0, iLength);
			} else if (iByteSize == iLength) {
				return strSource;
			} else {
				int iPadLength = iLength - iByteSize;
				sbBuffer = new StringBuffer();
				sbBuffer.append(strSource);
				for (int j = 0; j < iPadLength; j++) {
					sbBuffer.append(cPadder);
				}

				return sbBuffer.toString();
			}
		}

		sbBuffer = new StringBuffer();
		for (int j = 0; j < iLength; j++) {
			sbBuffer.append(cPadder);
		}

		return sbBuffer.toString();
	}

	/**
	 * null이나 공백검사
	 *
	 * @param String target
	 * @return boolean result
	 *
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * byte size를 가져온다.
	 *
	 * @param String target
	 * @return int bytelength
	 *
	 */
	public static int getByteSize(String str) {
		if (str == null || str.length() == 0) {
			return 0;
		}

		byte[] byteArray = null;
		try {
			byteArray = str.getBytes("KSC5601");
		} catch (Exception ex) {
		}

		if (byteArray == null) {
			return 0;
		}
		return byteArray.length;
	}

	/**
	 * String 이 null , blank 또는 값이 null 인지 체크
	 * @param input String
	 * @return input String가 null , blank String 또는 값이 null 이면 true를 반환한다.
	 */
	public static boolean isNull(String str) {
		if (str == null || str.equals("") || str.equals("null"))
			return true;
		return false;
	}

	/**
	 * Null유무의 검사
	 * 
	 * @param str
	 * @return
	 */
	public static String nullChange(String str) {
		return isNull(str) ? "" : str;
	}

	/**
	 * Null String To Zero
	 * 
	 * @param 체크 String
	 * @return 파라메터가 NULL 이면 0 String, Null이 아니면 파라메터
	 */
	public static String checkZero(String strCheck) {
		// String strTpval = checkNull(strCheck,"").trim();
		// strTpval = checkNull(strCheck,"0");

		// String strTpval = checkNull(strCheck,"0").trim();

		String strTpval = "";
		if (strCheck == null) {
			strTpval = "0";
		} else {
			strTpval = strCheck.trim();
			if (strTpval.equals("")) {
				strTpval = "0";
			}
		}

		long ltpval = Long.parseLong(strTpval);

		return String.valueOf(ltpval);
	}

	/**
	 * null String To blank
	 * 
	 * @param input String
	 * @param input String이 null 일때 변환할 String
	 * @return input String가 null 이면 BLANK String, null 이 아니면 input String 일 반환한다.
	 */
	public static String nullChange(String str, String replace) {
		return isNull(str) ? replace : str;
	}

	/**
	 * Method a2k. 8859_1 에서 MS949 로 문자세트변환
	 * 
	 * @param str 바꾸려는 문자열
	 * @return String
	 */
	public static String a2k(String str) {
		try {
			return new String(str.getBytes("8859_1"), "MS949");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * random한 세션값을 발생시킨다.
	 * 
	 * @return
	 */
	public String getSessionValue() {
		Random rand = new Random();
		double tempSn = rand.nextLong();
		tempSn = tempSn % 10000000000L;
		String strSessionID = new DecimalFormat("0000000000").format(Math.abs(tempSn));

		return strSessionID;
	}

	/**
	 * 현재 시간을 "yyyyMMddHHmmss" 형식으로 가져온다.
	 * 
	 * @return
	 */
	public String getCurTime() {
		Date dCurDate = new Date();
		SimpleDateFormat formatterTime = new SimpleDateFormat("yyyyMMddHHmmss");
		String strCurTime = formatterTime.format(dCurDate);
		return strCurTime;
	}

	/**
	 * 넘어온 값을 where 절에 in 절을 만드는 구문 <br />
	 * Null일경우 "" Return
	 * 
	 * @param arrValue
	 * @return
	 */
	public static String getMakeQueryInParam(String[] arrValue) {
		if (arrValue == null)
			return "";

		StringBuilder sb = new StringBuilder();
		for (String value : arrValue) {
			sb.append(value);
			sb.append(",");
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	/**
	 * 한글 인코딩 한다.
	 * 
	 * @param str 원본 String
	 * @return String 인코딩된 내용
	 */
	public static String toHan(String str) {
		String rstr = null;
		try {
			rstr = (str == null) ? "" : new String(str.getBytes("8859_1"), "KSC5601");
		} catch (java.io.UnsupportedEncodingException e) {
		}
		return rstr;
	}

	/**
	 * 한글 인코딩 한다.
	 * 
	 * @param str 원본 String
	 * @return String 인코딩된 내용
	 */
	public static String toKor(String str) {
		String rstr = null;
		try {
			rstr = (str == null) ? "" : new String(str.getBytes("KSC5601"));
		} catch (java.io.UnsupportedEncodingException e) {
		}
		return rstr;
	}

	/** '_' or '-' 형태의 연결을 카멜 케이스로 변환한다. ex) USER_NAME => userName */
	public static String getCamelize(String str) {
		char[] chars = str.toCharArray();
		boolean nextCharIsUpper = false;
		StringBuffer stringBuffer = new StringBuffer();
		for (char cha : chars) {
			if (cha == '_' || cha == '-') {
				nextCharIsUpper = true;
				continue;
			}
			if (nextCharIsUpper) {
				stringBuffer.append(Character.toUpperCase(cha));
				nextCharIsUpper = false;
			} else
				stringBuffer.append(Character.toLowerCase(cha));
		}
		return stringBuffer.toString();
	}

	/**
	 * 첫글자를 대문자로 바꾼다.
	 * 
	 * @param source
	 * @return
	 */
	public static String capitalize(String source) {
		if (source == null || source.length() == 0) {
			return source;
		}

		// 첫번째 문자가 대문자인지를 판단
		if (source.length() > 1 && Character.isUpperCase(source.charAt(0))) {
			return source;
		}

		// 첫번째 문자를 대문자로 변환
		char chars[] = source.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	/**
	 * 입력받은 문자에서 영문과 숫자만 추출함
	 * 
	 * @param str
	 * @return
	 */
	public static String getNumberAndAlphabet(String str) {

		StringBuilder sb = new StringBuilder();
		char[] c = str.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] >= 'A' && c[i] <= 'z') {
				sb.append(c[i]);
			}
			if (Character.isDigit(c[i])) {
				sb.append(c[i]);
			}
		}
		return sb.toString();
	}

	/**
	 * 입력받은 문자에서 영문과 숫자만 추출함
	 * 
	 * @param str
	 * @return
	 */
	public static String getNumber(String str) {
		StringBuilder sb = new StringBuilder();
		char[] c = str.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (Character.isDigit(c[i])) {
				sb.append(c[i]);
			}
		}
		return sb.toString();
	}

	// ************************************************************************
	// isNotNull
	// @return null이면 false
	// 2002-08-08 추가
	// ************************************************************************
	public static boolean isNotNull(String var) {
		boolean bChk = true;
		int nLength = 0;

		if (var == null) {
			bChk = false;
		} else {
			nLength = var.length();

			if (nLength > 0) {
				var = var.trim();

				if (var.equals("null") || var.equals("")) {
					bChk = false;
				}
			} else
				bChk = false;
		}

		return bChk;
	}

	/**
	 * <PRE>
	 * 문자열을 HEX타입으로 변환
	 * </PRE>
	 *
	 * @param String 변경전
	 *
	 * @return String 4비트문자
	 */
	public static String StringTo4Hex(String TempString) throws Exception {
		StringBuffer ReturnString = new StringBuffer();
		char[] charStr = TempString.toCharArray();

		for (int i = 0; i < charStr.length; i++) {
			String tmp = "";
			tmp = "0000" + Integer.toHexString(charStr[i]);

			String tmp2 = tmp.substring(tmp.length() - 4);

			ReturnString.append(tmp2);

		}

		return ReturnString.toString().toUpperCase();

	}// END METHOD

	/**
	 * <PRE>
	   *  [TUXEDO] null문자 또는 ""인 문자를 " " blank문자로 변환한다.
	   *           턱시도에 문자열을 전달하기 위해..
	 * </PRE>
	 *
	 * @param String 변경전
	 * @return String 4비트문자
	 * @see
	 * @author 2005-05-10
	 */
	public static String NullChange2(String val) {
		if (val == null || val.equals(""))
			val = " ";

		return val;
	}

	public static String nullChangeTrim(String str) {
		return nullChange(str, "").trim();
	}

	/**
	 * 태그 제거 기능
	 * 
	 * @param str
	 * @return String
	 */
	public static String removeTag(String str) {
		return removeTag(str, "&lt;");
	}

	/**
	 * 태그 제거 기능; author: hsboy@aboutjsp.com
	 * @param str
	 * @param delimiter "<" or else
	 * @return String
	 */
	public static String removeTag(String str, String delimiter) {
		int lt = str.indexOf(delimiter);

		if (lt != -1) {
			int gt = str.indexOf(">", lt);
			if ((gt != -1)) {
				str = str.substring(0, lt) + str.substring(gt + 1);
				// 재귀 호출
				str = removeTag(str, delimiter);
			}
		}
		return str;
	}

	/**
	 * Method nchk trim.
	 * @param str
	 * @param defaultStr
	 * @return String
	 */
	public static String nullChangeTrim(String str, String defaultStr) {
		return (str == null || "".equals(str) || "null".equals(str)) ? defaultStr : str;
	}

	public static String niceRequestReplace(String paramValue, String gubun) {
		String result = "";

		if (paramValue != null) {

			paramValue = paramValue.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

			paramValue = paramValue.replaceAll("\\*", "");
			paramValue = paramValue.replaceAll("\\?", "");
			paramValue = paramValue.replaceAll("\\[", "");
			paramValue = paramValue.replaceAll("\\{", "");
			paramValue = paramValue.replaceAll("\\(", "");
			paramValue = paramValue.replaceAll("\\)", "");
			paramValue = paramValue.replaceAll("\\^", "");
			paramValue = paramValue.replaceAll("\\$", "");
			paramValue = paramValue.replaceAll("'", "");
			paramValue = paramValue.replaceAll("@", "");
			paramValue = paramValue.replaceAll("%", "");
			paramValue = paramValue.replaceAll(";", "");
			paramValue = paramValue.replaceAll(":", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll("#", "");
			paramValue = paramValue.replaceAll("--", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll(",", "");

			if (gubun != "encodeData") {
				paramValue = paramValue.replaceAll("\\+", "");
				paramValue = paramValue.replaceAll("/", "");
				paramValue = paramValue.replaceAll("=", "");
			}

			result = paramValue;
		}
		return result;
	}

	/******************************************************************
	 * 문자열 내에서 빈 공백 라인들 제거(순수 공백, 줄바꿈, Tab 등)
	 * @param s
	 * @return
	 */
	public static String removeEmptyLine(String s) {
		return s.replaceAll("(?m)^[ \t]*\r?\n", "");
	}

	/**************************************
	 * 정규식 모음.
	 * @param s
	 * @return
	 */
	public static String getRegex(String s) {
		//multi line 주석
		if ( "MULTI_LINE_COMMENT".equals(s) ) {
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
			String key = "@@@@@@@" + i + "@@@@@@@";
			String val = list.get(i);
			int sPos = s.indexOf(val);
			s = s.substring(0, sPos )  + key + s.substring( sPos + val.length() );
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
	public static String repareCommnet(String s, Map<String, String> map) {
		Iterator<Entry<String, String>> itx = map.entrySet().iterator();
		while (itx.hasNext()) {
			Entry<String, String> et = itx.next();
			//s = StringUtils.replaceOnce(s, et.getKey(), et.getValue());
			int sPos = s.indexOf( et.getKey());
			s = s.substring(0, sPos )  + et.getValue() + s.substring( sPos + et.getKey().length() );
		}
		return s ;
	}

	/************************************************
	 * 정규식 찾은 데이터를 List 로 반환한다.
	 * @param s 찾을 대상 문자열
	 * @param p 찾을 정규식
	 * @return
	 ************************************************/
	public static List<String> regexFindToList( String s, String p){
		Pattern pattern = Pattern.compile( p );
		Matcher matcher = pattern.matcher(s);

		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;
	}
}