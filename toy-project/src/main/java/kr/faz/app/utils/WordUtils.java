package kr.faz.app.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class WordUtils {
	public static Map<String , String > find_word_area(String s , String firstWords, String lastWords ) {
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

}
