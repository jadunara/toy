package kr.faz.app.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import kr.faz.app.utils.DiskUtil;

public class DiskUsageExample {
	public static void main(String[] args) {
		java.nio.file.FileSystems fs ;
		Path p = Paths.get("c:/");
		File file = new File("c:/");
		Map<String, Long> map = DiskUtil.diskUsage("c:/");
		System.out.println(map);
		double x =  ((double)map.get("USED")) / ((double)map.get("TOTAL")) ;
		double x1 = ((double)map.get("FREE")) / ((double)map.get("TOTAL")) ;
		System.out.println(x);
		System.out.println(x1);
				

	}
	public static void main1(String[] args) {
//		File file = new File("C:\\STS\\sts-3.9.11.RELEASE\\WSC\\toy-projects\\src\\main\\java\\kr\\faz\\app\\example\\df_example");
		File file = new File("C:\\STS\\sts-3.9.11.RELEASE\\WSC\\toy-projects\\src\\main\\java\\kr\\faz\\app\\example\\df_example");
		try {
			String x = FileUtils.readFileToString(file  , "");
			DiskUsageExample du = new DiskUsageExample();
			du.process(x);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void process(String x) {
		String[] arr = x.split("\n");
	}
}
