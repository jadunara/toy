package kr.faz.app.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import kr.faz.app.utils.ConsoleColors;

public class ConsoleColorsExample {
	private static Map<String, String> ruleSet = new HashMap<String, String>();
	public static void main3(String[] args) {
		String sql = "SELECT * FROM DUAL  SELECT * FROM DUAL ";
		Pattern pattern = Pattern.compile("(SELECT)|(FROM)" , Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		while(matcher.find()) {
			System.out.println(matcher.group()
					 +  " regionStart > " + matcher.regionStart()
					 +  " regionEnd > " + matcher.regionEnd()
					 +  " start > " + matcher.start()
					 +  " end > " + matcher.end()
					 );
		}
	}
	public static void main2(String[] args) {
		File ruleFile = new File("C:\\STS\\sts-3.9.11.RELEASE\\WSC\\toy-projects\\sql.color.rule");
		File sqlFile = new File("C:\\STS\\sts-3.9.11.RELEASE\\WSC\\toy-projects\\src\\main\\resources\\sample.sql");
		String rule = "" , sql = "";
		try {
			rule = FileUtils.readFileToString(ruleFile, "UTF-8");
			sql = FileUtils.readFileToString(sqlFile, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		String p = "\\/\\*(.*)\\*\\/";
		List<String> list = regex(sql, p);
		Map<String, String> commentMap = new LinkedHashMap<String, String>();
		if ( list.size() != 0 ) {
			for ( int i = 0 ; i < list.size() ; i++ ) {
				String k = "@@@@@" + i + "@@@@@";
				String v =  list.get(i);
				sql = sql.replace( v ,  k );
				commentMap.put(k, v);
			}
		}
		System.out.println(sql);

		for ( int i = 0 ; i < list.size() ; i++ ) {
			String k = "@@@@@" + i + "@@@@@";
			String v =  commentMap.get(k);
			sql = sql.replace( k ,  v );
		}
		System.out.println(sql);

		//setRule(rule);
		//new ConsoleColorsExample().process( sql );
	}

	public static List<String> regex( String s, String p){
		Pattern pattern = Pattern.compile( p );
		Matcher matcher = pattern.matcher(s);

		List<String> list = new ArrayList<String>();
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;
	}
	public static void setRule(String rule) {
		if ( ruleSet.size() == 0 ) {
			String[] rules = rule.split("\n");
			for ( String s : rules ) {
				String[] x = s.split("[:]");
				ruleSet.put(x[0] , x[1].trim());
			}
		}
	}
	public void process(String sql) {
		String delim = "()\r\n\t ";
		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(sql, delim, true);
		while(st.hasMoreElements()) {
			String v = (String) st.nextElement();
			//System.out.println(v);
			if (v.length() > 1 && ruleSet.containsKey(v.toUpperCase()))  {
				v = StringEscapeUtils.unescapeJava( ruleSet.get(v.toUpperCase() ) )  +  "\033[0m";
			}
			//System.out.println(v);
			sb.append(v);
		}
		System.out.println(sb);
//		System.out.println("\033[0m".length());

	}
	public static void main(String[] args) {
		String sql = "SELECT * FROM DUAL";
//		System.out.println( sql.replaceAll("SELECT",   ConsoleColors.RED_BRIGHT            +  " SELECT" + ConsoleColors.RESET) );
//		System.out.println( sql.replaceAll("SELECT",   ConsoleColors.RED_UNDERLINED        +  " SELECT" + ConsoleColors.RESET) );
//		System.out.println( sql.replaceAll("SELECT",   ConsoleColors.RED                   +  " SELECT" + ConsoleColors.RESET) );
//		System.out.println( sql.replaceAll("SELECT",   ConsoleColors.RED_BOLD_BRIGHT       +  " SELECT" + ConsoleColors.RESET) );
//		System.out.println( sql.replaceAll("SELECT",   ConsoleColors.BLUE_BACKGROUND  + ConsoleColors.BLUE_BACKGROUND_BRIGHT +  " SELECT" + ConsoleColors.RESET) );
//		System.out.println( sql.replaceAll("SELECT",   ConsoleColors.BLUE_BACKGROUND_BRIGHT  + ConsoleColors.RED +  " SELECT" + ConsoleColors.RESET) );
//		System.out.println( sql.replaceAll("SELECT",   "\033[5;120m" +  " SELECT" + ConsoleColors.RESET) );
		for ( int i = 31 ; i < 48 ; i++) {
			//printColor(i);
		}
		for ( int i = 90 ; i < 110 ; i++) {
			//printColor(i);
		}
		System.out.println("\033[0;103m".length());
		//ConsoleColorsExample.main1(null);
	}
	public static void printColor(int i) {
		System.out.println(String.format("%3s",  i )
				+ " -> \033[0;" + i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[1;" + i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[2;" + i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[3;" + i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[4;" + i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[5;" + i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[6;" + i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[7;" + i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[8;" + i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[9;" + i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[10;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[11;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[12;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[13;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[14;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[15;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[16;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[17;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[18;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[19;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[21;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[22;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[23;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[24;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				+ " -> \033[25;"+ i+ "m" + "SELECT"  +  ConsoleColors.RESET
				);
	}
}
