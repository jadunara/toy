package kr.faz.app.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class PDFReaderExample {
	public static void main(String args[]) {

		System.out.println("start!");

		PDFReaderExample pdfr = new PDFReaderExample();
		File file = new File("D:\\sample file\\기관운영업무추진비(건설국장).pdf");
		try {
//			String text = pdfr.read();
//			System.out.println(text);

			pdfr.read(file);
			pdfr.read2(file);
		} catch (Exception e) {
			System.out.println("exception : " + e);
		}

	}

	private void read2(File file) {
		PDFStreamEngine engine = null;
		try {
			PDDocument document = PDDocument.load(file);

			engine = new PDFTextStripper();
//			PDPage page = engine.getCurrentPage();
//			PDMetadata meta = page.getMetadata();
			PDPage page = document.getPage(0);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private void read(File file) {
		// TODO Auto-generated method stub
		try {
			PDDocument document = PDDocument.load(file);
//			document.getClass();
	        if (!document.isEncrypted()) {

	                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
	                stripper.setSortByPosition(true);

	                PDFTextStripper tStripper = new PDFTextStripper();

	                String pdfFileInText = tStripper.getText(document);
	                //System.out.println("Text:" + st);

					// split by whitespace
	                String lines[] = pdfFileInText.split("\\r?\\n");
	                for (String line : lines) {
	                    System.out.println(line);
	                }

	            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String read() throws FileNotFoundException, IOException {
		String result = null;
		File file = new File("D:\\sample file\\기관운영업무추진비(건설국장).pdf");
		PDFParser pdfp;
		RandomAccessRead source;
		PDDocument pdd = null;
		COSDocument cos = null;
		PDFTextStripper pdfts = null;
		try {
			source = new RandomAccessFile(file, "r");//mode must be one of "r", "rw", "rws", or "rwd"
			pdfp = new PDFParser(source);
			pdfp.parse();
			pdd = pdfp.getPDDocument();
			cos = pdfp.getDocument();
			pdfts = new PDFTextStripper();
			PDDocumentInformation pddi = pdd.getDocumentInformation();
			System.out.println("총 페이지수 : " + pdd.getNumberOfPages());
			System.out.println("제목 : " + pddi.getTitle());
			System.out.println("제목 : " + pddi.getSubject());
			System.out.println("작성자 : " + pddi.getAuthor());
			System.out.println("작성기 : " + pddi.getCreator());
			pdfts.setLineSeparator("\n");
			pdfts.setWordSeparator(" ");
			result = pdfts.getText(pdd);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if ( cos != null )
				try {
					cos.close();
				} catch (IOException e) {
				}
			if ( pdd != null)
				try {
					pdd.close();
				} catch (IOException e) {
				}
		}
		return result;
	}

}
