package kr.faz.app.example;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class RunCmdExample {
	public static void main(String[] args) {
		/** java run cmd or powershell **/
		List<String> commands = new ArrayList<String>();
//		commands.add("cmd");
		commands.add("powershell.exe");
		commands.add("/c");
		commands.add("chcp 65001");/** UTF-8 character set**/
		commands.add(";");
		commands.add("ls");
		commands.add(";");
		
		ProcessBuilder pb = new ProcessBuilder(commands);
		pb = pb.redirectErrorStream(true);
		Process p = null;
		InputStream is = null;
		byte[] revbytes = new byte[1024];
		int readcnt = 0;
		ArrayList<byte[]> bytelist = new ArrayList<byte[]>();
		int msgtotalcnt = 0;
		try {
			p = pb.start();
			int exitCode = p.waitFor();
			System.out.println("exitCode : " + exitCode);
			is = p.getInputStream();
			while ( (readcnt = is.read(revbytes)) != -1) {
				Byte checkbyte = revbytes[readcnt - 1];
				
				if (checkbyte == (byte) 0) {
					bytelist.add(ByteBuffer.allocate(readcnt - 1).put(revbytes, 0, readcnt - 1).array());
					msgtotalcnt += (readcnt - 1);
					break;
				} else {
					bytelist.add(ByteBuffer.allocate(readcnt).put(revbytes, 0, readcnt).array());
					msgtotalcnt += readcnt;
				}
				
			}

			ByteBuffer tempByteBuf = ByteBuffer.allocate(msgtotalcnt);

			for (int i = 0; i < bytelist.size(); i++) {
				tempByteBuf.put(bytelist.get(i));
			}
			
//			String res = new String(tempByteBuf.array(), "MS949");
			String res = new String(tempByteBuf.array() );
			
			System.out.println(res);

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (p != null )
				p.destroy();
		}
	}
}
