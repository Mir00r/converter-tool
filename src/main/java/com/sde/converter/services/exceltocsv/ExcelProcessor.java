package com.sde.converter.services.exceltocsv;

import com.sde.converter.commons.Constants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

@Service
public class ExcelProcessor {

    public void extractSheets(MultipartFile excelFile, int dataSize, File excelDirectory, BlockingQueue<Sheet> taskQueue) throws IOException {
        int chunkSize = dataSize == 0 ? Constants.DEFAULT_BATCH_SIZE : dataSize; // Adjust the chunk size based on your needs

        try (InputStream excelInputStream = excelFile.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(excelInputStream);
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet originalSheet = workbook.getSheetAt(sheetIndex);

                int totalRows = originalSheet.getPhysicalNumberOfRows();
                int startRow = 0;
                int chunkNumber = 1;

                while (startRow < totalRows) {
                    try (SXSSFWorkbook chunkWorkbook = new SXSSFWorkbook()) {
                        Sheet chunkSheet = chunkWorkbook.createSheet("Sheet1");

                        int endRow = Math.min(startRow + chunkSize, totalRows);

                        for (int rowNumber = startRow; rowNumber < endRow; rowNumber++) {
                            Row originalRow = originalSheet.getRow(rowNumber);
                            if (originalRow != null) {
                                Row chunkRow = chunkSheet.createRow(rowNumber - startRow);

                                for (int colNumber = 0; colNumber < originalRow.getLastCellNum(); colNumber++) {
                                    Cell originalCell = originalRow.getCell(colNumber);
                                    Cell chunkCell = chunkRow.createCell(colNumber);
                                    if (originalCell != null) {
                                        CellType cellType = originalCell.getCellType();
                                        chunkCell.setCellType(cellType);
                                        switch (cellType) {
                                            case STRING:
                                                chunkCell.setCellValue(originalCell.getStringCellValue());
                                                break;
                                            case NUMERIC:
                                                chunkCell.setCellValue(originalCell.getNumericCellValue());
                                                break;
                                            // Handle other cell types as needed
                                        }
                                    }
                                }
                            }
                        }
                        try (FileOutputStream chunkFileOutputStream = new FileOutputStream(
                                new File(excelDirectory, "sheet_" + sheetIndex + "_chunk_" + chunkNumber + ".xlsx"))) {
                            chunkWorkbook.write(chunkFileOutputStream);
                        }
                        taskQueue.put(chunkSheet);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    startRow += chunkSize;
                    chunkNumber++;
                }
            }
        }
     catch (IOException e) {
            e.printStackTrace();
//            return "ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)";
        }
    }
}
