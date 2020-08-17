package kr.faz.app.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class WordUtils {
	public static Map<String , String > find_word_area_by_line(String s , String firstWords, String lastWords ) {
		if ( StringUtils.isEmpty( s ) || StringUtils.isEmpty( firstWords ) || StringUtils.isEmpty( lastWords)) {
			return null;
		}

		if ( s.indexOf(firstWords) == -1 || s.indexOf(lastWords) == -1  ) {
			return null ;
		}

		Map<String, String> map = new LinkedHashMap<String, String>();

		int sPos = s.indexOf(firstWords);
		int ePos = s.indexOf(lastWords , sPos);
		int subSpos = 0;
		int loopCnt = 0;
		while(true) {
			int t = s.indexOf("\n", subSpos);
			if ( t > sPos) {
				break;
			}
			loopCnt++;
			if ( loopCnt++ > 100) {
				throw new RuntimeException("unlimit while please check.......");
			}
		}
		sPos = subSpos;

		String _pre = s.substring(0, sPos);
		String _sub = s.substring(ePos + lastWords.length()) ;
		String _mid = s.substring( sPos, ePos + lastWords.length());

		map.put("PRE" , _pre);
		map.put("MID" , _mid);
		map.put("SUB" , _sub);
		return map;
	}
	public static Map<String, String> find_word(String s, String findWord ) {
		if ( StringUtils.isEmpty( s ) || StringUtils.isEmpty( findWord )  ) {
			return null;
		}

		int sPos = s.indexOf(findWord);

		Map<String, String> map = new LinkedHashMap<String, String>();
		
		String _pre = s.substring(0, sPos);
		String _mid = s.substring(sPos, sPos + findWord.length());
		String _sub = s.substring(sPos + findWord.length() ) ;
		map.put("PRE" , _pre);
		map.put("MID" , _mid);
		map.put("SUB" , _sub);


		return map;
	}
}
