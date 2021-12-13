package kr.faz.app.example;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/********************************************************************
 * 선관위 개표율 조회.
 * @author ktnam
 *
 ********************************************************************/
public class MohwCovidExample {

	public static void main(String[] args) {
		Connection conn = Jsoup.connect("http://ncov.mohw.go.kr/");
		Selector s = null;
		try {
			conn.ignoreContentType(true);
			Response res = conn.execute();
			Map<String, String> headers = res.headers();

			s = Selector.open();
			while(true) {
				System.out.print( new Date().toString() + " > " );
				process(headers);
				s.select(20*1000);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if ( s != null )
				try {
					s.close();
				} catch (IOException e) {
				}
		}
	}

	private static void process(Map<String, String> headers) {
		Connection conn = Jsoup.connect("http://ncov.mohw.go.kr/bdBoardList_Real.do?brdId=1&brdGubun=13&ncvContSeq=&contSeq=&board_id=&gubun=");
		conn.ignoreContentType(true);
		conn.headers(headers);
		String applyDateTxt = "04.19. 00시";
		try {
			Response res = conn.execute();
			String html = res.body();
			Document doc = Jsoup.parse(html);
			Elements elements = doc.select("#content > div > div.timetable > p > span");
			Map<String, String> map = new LinkedHashMap<String, String>();
			Element applyDateEm = elements.get(0);
			if ( applyDateTxt.equals(applyDateEm.text()) ) {
				System.out.println("미반영");
				return;
			}

			Elements ems = doc.select("#mapAll > div > ul > li:nth-child(2) > div:nth-child(2) > span");
			System.out.println("반영 > " + ems.get(0).text());
			System.exit(0);



		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
