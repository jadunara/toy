package kr.faz.app.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/********************
 * DISK Util.
 * @author ktnam
 *
 */
public class DiskUtil {
	/***************************************************************************
	 * 
	 * @param path
	 * @return
	 ***************************************************************************/
	public static Map<String, Long> diskUsage(String path) {
		File file = new File(path);
		long totalSpace = file.getTotalSpace();
		long freeSpace = file.getFreeSpace();
		long used = totalSpace - freeSpace;
		Map<String, Long> map = new HashMap<String, Long>();
		map.put("TOTAL", totalSpace);
		map.put("FREE", freeSpace);
		map.put("USED", used);
		return map;
	}
}
