package kr.faz.app.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import kr.faz.app.utils.CppCStringUtils;
import kr.faz.app.utils.SQLUtils;

public class StringVCppRegExTest {
	public static void main(String[] args) {
		StringVCppRegExTest s1 = new StringVCppRegExTest();
		try {
			s1.process();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void process() throws IOException {
		String f = "C:\\STS\\sts-3.9.11.RELEASE\\WSC\\toy-projects\\src\\main\\java\\kr\\faz\\app\\test\\resources\\CPP_SQL";
		f = "C:\\STS\\sts-3.9.11.RELEASE\\WSC\\toy-projects\\src\\main\\java\\kr\\faz\\app\\test\\resources\\CPP_SQL";
		File file = new File(f);
		String s = FileUtils.readFileToString(file, "UTF-8");
		Map<String, String> map = new LinkedHashMap<String, String>();
		List<String> list = new ArrayList<String>();
		map.put("ORG_TXT" , s);

//		s = replace_normal_format(s);
//		s = CppCStringUtils.regex_sql_format(s);
//
		processSQL(map, list);
//		s = map.get("ORG_TXT").toString();
//
		System.out.println("--------------------------------------- _T 제거 결과(ORG)   ---------------------------------------");
		System.out.println(map.get("ORG_TXT"));
		System.out.println("--------------------------------------- _T 제거 결과(RESULT)---------------------------------------");
		System.out.println( map.get("_PRE_") +  map.get("_SUB_") );
	}
//	public String replace_normal_format(String s) {
//		if ( StringUtils.isEmpty( s )) {
//			return s;
//		}
//
//		Map<String, String> map = find_word_area(s, ".Format", ";");
//		if ( map == null )
//			return s;
//
//		String mid = map.get("MID");
//		System.out.println("\n\n mid >> "+  mid);
//
//
//		System.out.println("\n\n map >> " + map);
//		return s;
//	}

	private void processSQL(Map<String, String> map, List<String> list) {
		map.put("_SUB_",  map.get("ORG_TXT") );
		for ( int i = 0 ; i < 100 ; i++ ) {
			if(  !processSQLFind(map , list) ) {
				break;
			}
		}
		
		String _sub =  map.get("_SUB_");
		_sub = regex_all(_sub);
		map.put( "_SUB_", _sub);
		list.add(_sub);
		return  ;
	}



	private boolean processSQLFind(Map<String, String> map, List<String> list) {
		String s1 = map.get("_SUB_");
		if ( s1.indexOf("SELECT") == -1 || s1.indexOf("FROM") == -1  ) {
			return false ;
		}

		String[] a = s1.split("\n");
		int sqlSpos = -1;
		int sqlEpos = -1;

		boolean findSelect = false;
		boolean findSemiColon = false;

		for ( int i = 0 ; i < a.length; i++ ) {
			String s = a[i];
			if ( findSelect == false  ) {
				if ( s.indexOf("SELECT") != -1 ) {
					findSelect = true;
					sqlSpos = i;
				}

			} else if (!findSemiColon ){
				if ( s.indexOf(";") !=  -1) {
					findSemiColon = true;
					sqlEpos = i;
					break;
				}
			}
		}

		if ( !findSelect ||  !findSemiColon ) {
			return false;
		}

		String _pre = "";
		String _sub = "";
		String _sql = "";
		String _x = "";

		for ( int i = 0 ; i < a.length ; i++ ) {
			if ( i < sqlSpos  ) {//1  < 10 , 2 < 10 , 3 < 10
				_pre+= a[i] + "\n";
			} else if ( i >= sqlSpos && i <= sqlEpos ) {
				_sql += a[i].trim() + "\n";
			} else if ( i > sqlEpos ) {
				_sub += a[i] + "\n";
			} else {
				_x += a[i] + "\n";
				throw new RuntimeException("parsing error \n" + a[i] );
			}
		}

		_pre = CppCStringUtils.regex_T(_pre);
		_pre = regex_all(_pre);
		
		//TODO replace all-_-;;

		System.out.println("\n\n_sql : " + _sql);
		System.out.println("\n\n_x[뭐지] : " + _x);

		_sql = StringUtils.replace(_sql , "_T(\"\\n\\r\")" , "");
		_sql = CppCStringUtils.regex_T(_sql);


		String _sql1 = CppCStringUtils.regex_sql_format(_sql);

		//수정된 내역이 있다면 처리.
		if ( !_sql1.equals(_sql ) ) {
			_sql = _sql1;
			_sql = CppCStringUtils.insert_sprintf(_sql);
		}

		int maxLength = SQLUtils.getSqlStringMaxLength(_sql) + 10; /** 10 은 여유분 **/
		_sql = CppCStringUtils.sql_fill_space(_sql , maxLength , " " );

		list.add(_pre);
		list.add(_sql);

		_pre += _sql;
		String pre_str = StringUtils.defaultString( map.get("_PRE_") ) ;
		map.put("_PRE_" , pre_str+_pre );
		map.put("_SUB_" , _sub);

		System.out.println("\nmaxLength :  " + maxLength);
		System.out.println("\n\n_sql :\n" + _sql);
		//map.put("ORG_TXT" , _pre + _sql + _sub);

		return true;
	}


	private String regex_all(String s) {
		s = CppCStringUtils.regex_T(s);
		s = CppCStringUtils.regex_Left(s);
		s = CppCStringUtils.regex_Mid(s);
//		s = CppCStringUtils.regex_Format(s);

		return s;
	}
}
