package com.sde.converter.exceltocsv.services;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;

@Service
public class ExcelStreamingService {

//    public void processLargeExcel(InputStream excelInputStream) {
//        SXSSFWorkbook workbook = new SXSSFWorkbook(excelInputStream);
//        try (Workbook workbook = new XSSFWorkbook(excelInputStream);
//             SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workbook, 100); // 100 rows in memory
//
//             // Process each sheet
//        ) {
//
//            for (int sheetIndex = 0; sheetIndex < sxssfWorkbook.getNumberOfSheets(); sheetIndex++) {
//                SXSSFSheet sheet = sxssfWorkbook.getSheetAt(sheetIndex);
//
//                for (Row row : sheet) {
//                    // Process each row
//                    for (Cell cell : row) {
//                        // Process each cell
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void generateLargeExcel(OutputStream excelOutputStream) {
//        try (SXSSFWorkbook workbook = new SXSSFWorkbook();
//             SXSSFSheet sheet = workbook.createSheet("Sheet1")
//
//             // Generate data and write to the sheet
//        ) {
//
//            for (int rowNum = 0; rowNum < 1000000; rowNum++) {
//                SXSSFRow row = sheet.createRow(rowNum);
//                for (int cellNum = 0; cellNum < 10; cellNum++) {
//                    SXSSFCell cell = row.createCell(cellNum);
//                    cell.setCellValue("Value " + rowNum + "-" + cellNum);
//                }
//            }
//            workbook.write(excelOutputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
