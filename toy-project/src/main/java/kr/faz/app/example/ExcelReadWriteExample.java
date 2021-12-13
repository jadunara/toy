package kr.faz.app.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReadWriteExample {
	public static void main(String[] args) {
		ExcelReadWriteExample erw = new ExcelReadWriteExample();
		erw.process();
	}

	public void process() {
		String filePath = "D:\\sample file";
		String fileName = "업무추진비 집행내역(2020.3월-동탄1동).xls";
		Workbook wb = null;
		String excelFile = filePath + File.separator + fileName;// File.separator ( / or \ )
		FileInputStream fis = null;
		List<Row> list = new ArrayList<Row>();
		DataFormatter formatter = new DataFormatter();
		try {
			fis = new FileInputStream(excelFile);
			wb = WorkbookFactory.create(fis);
			int sheetCounts = wb.getNumberOfSheets();
			for (int i = 0; i < sheetCounts; i++) {
				Sheet sheet = wb.getSheetAt(i);
				System.out.println(sheet.getSheetName());
				boolean startFlag = false;
				int lastRowNum = sheet.getLastRowNum();
				for ( int j = 0 ; j < lastRowNum ; j++ ) {
					Row row = sheet.getRow(j);
					if ( row == null )
						continue;
					Cell fstCell = row.getCell(0);
					if ( fstCell == null )
						continue;
					if ( !startFlag ) {
						String seqNumStr =  StringUtils.trimToEmpty( fstCell.getStringCellValue() ) ;

						if ( "연번".equals(seqNumStr) ) {
							startFlag = true;
						}
					}
					if ( !startFlag )
						continue;
					list.add(row);
				}
			}
			for ( int i = 0 ; i < list.size() ; i++ ) {
				System.out.println(formatter.formatCellValue( list.get(i).getCell(0) ) );
			}
			createExcel(list);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (wb != null)
				try {
					wb.close();
				} catch (IOException e) {
				}

			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
				}
		}
	}

	private void createExcel(List<Row> list) {
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("전체");
		DataFormatter formatter = new DataFormatter();
		Format df = new SimpleDateFormat("yyyy-MM-dd");

		for ( int i = 0 ; i < list.size() ; i++ ) {
			System.out.println("----- row copy ----- " + i);
			Row newRow = sheet.createRow(i);
			Row row = list.get(i);

			for ( int k = 0 ; k < row.getLastCellNum() ; k++ ) {
				Cell cell = row.getCell(k);
				if ( cell == null)
					continue;

				System.out.println("-------------------  row copy " + i + " ----- cell copy  " + k + " (" + cell.getCellType()  +  ") >> " + formatter.formatCellValue(  cell  ) );

				Cell newCell = newRow.createCell(cell.getColumnIndex());
//				copyCell(cell, newCell, cell.getCellType());
				if ( cell.getCellType() == CellType.NUMERIC ) {
					if (DateUtil.isCellDateFormatted(cell, null)) {

						Date d = cell.getDateCellValue();
				        String cellValue = df.format(d);
				        newCell.setCellValue(cellValue);

					} else {
						newCell.setCellValue( formatter.formatCellValue(  cell  )  )  ;
					}
				} else {
					newCell.setCellValue( formatter.formatCellValue(  cell  )  )  ;
				}
			}
		}

		System.out.println("------------------- cell copy end ---");


		OutputStream ops = null;
		try {
			ops = new FileOutputStream("D:/total_excel.xlsx");
			wb.write(ops);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				wb.close();
			} catch (IOException e) {
			}
			if ( ops != null )
				try {
					ops.close();
				} catch (IOException e) {
				}
		}
	}

	private void copyCell(Cell cell, Cell newCell, CellType cellType) {
		switch (cellType) {
		case _NONE :
			break;
		case BLANK :
			break;
		case BOOLEAN :
			newCell.setCellValue( cell.getBooleanCellValue());
			break;
		case ERROR :
			newCell.setCellErrorValue(cell.getErrorCellValue());
			break;
		case FORMULA :
			newCell.setCellFormula( cell.getCellFormula());
			break;
		case NUMERIC :
			newCell.setCellValue( cell.getNumericCellValue());
			break;
		case STRING :
			newCell.setCellValue( cell.getRichStringCellValue());
			break;

		default:
			break;
		}

	}
}
