package com.sde.converter.exceltocsv.services;

import com.sde.converter.commons.Constants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ExcelToCSVService {

    public void convertExcelToCsv(InputStream excelInputStream, OutputStream csvOutputStream, String separator) throws IOException {
        Workbook workbook = new XSSFWorkbook(excelInputStream);
        Sheet sheet = workbook.getSheetAt(0); // Assuming you want the first sheet

        try (BufferedWriter csvWriter = new BufferedWriter(new OutputStreamWriter(csvOutputStream))) {
            for (Row row : sheet) {
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    csvWriter.write(cell.toString());
                    if (cellIterator.hasNext()) {
                        csvWriter.write(separator == null ? Constants.DEFAULT_COLUMN_SEPARATOR : separator);
                    }
                }
                csvWriter.newLine();
            }
        }
        workbook.close();
    }

    // Convert the sheet in chunks
    public void convertSheetToCsvChunks(InputStream excelInputStream, OutputStream csvOutputStream, String separator, int dataSize) throws IOException {
        int batchSize = dataSize == 0 ? Constants.DEFAULT_BATCH_SIZE : dataSize; // Process 1000 rows at a time

        Workbook workbook = new XSSFWorkbook(excelInputStream);
        try (BufferedWriter csvWriter = new BufferedWriter(new OutputStreamWriter(csvOutputStream))) {
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    int currentRow = 0;
                    while (currentRow < batchSize && rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        Iterator<Cell> cellIterator = row.cellIterator();
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            csvWriter.write(cell.toString());
                            if (cellIterator.hasNext()) {
                                csvWriter.write(separator == null ? Constants.DEFAULT_COLUMN_SEPARATOR : separator);
                            }
                        }
                        csvWriter.newLine();
                        currentRow++;
                    }
                    // Flush and reset the writer for each batch
                    csvWriter.flush();
                }
            }
        }
        workbook.close();
    }

    @Async
    public byte[] convertExcelToCsvZip(InputStream excelInputStream, String separator, int dataSize) throws IOException {
//        Workbook workbook = new XSSFWorkbook(excelInputStream);
//        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(excelInputStream));
        Workbook workbook = WorkbookFactory.create(excelInputStream);

        ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
        int batchSize = dataSize == 0 ? Constants.DEFAULT_BATCH_SIZE : dataSize;

        try (ZipOutputStream zipOut = new ZipOutputStream(zipStream)) {
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                ByteArrayOutputStream csvOutputStream = new ByteArrayOutputStream();
                try (BufferedWriter csvWriter = new BufferedWriter(new OutputStreamWriter(csvOutputStream))) {
                    for (Row row : sheet) {
                        Iterator<Cell> cellIterator = row.cellIterator();
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            csvWriter.write(cell.toString());
                            if (cellIterator.hasNext()) {
                                csvWriter.write(separator == null ? Constants.DEFAULT_COLUMN_SEPARATOR : separator);
                            }
                        }
                        csvWriter.newLine();
                    }
                }

                // Write CSV content to ZIP entry
                zipOut.putNextEntry(new ZipEntry(sheet.getSheetName() + (sheetIndex + 1) + ".csv"));
                zipOut.write(csvOutputStream.toByteArray());
                csvOutputStream.close();

                // If batchSize is reached, flush and reset the output stream
                if (batchSize > 0 && (sheetIndex + 1) % batchSize == 0) {
                    zipOut.flush();
                }
            }
        }
        workbook.close();
        return zipStream.toByteArray();
    }

    public void convertExcelToCsv(String excelFilePath) throws IOException {
        Workbook workbook = new SXSSFWorkbook();
        // Load the Excel file using SXSSFWorkbook

        // Assuming each sheet has around 10,000 rows
        int batchSize = 10000;
        int sheetIndex = 0;

        while (true) {
            Sheet sheet = workbook.getSheetAt(sheetIndex++);
            if (sheet == null) {
                break; // No more sheets
            }

            // Process batch of rows
            for (int rowNumber = 0; rowNumber < sheet.getPhysicalNumberOfRows(); rowNumber++) {
                Row row = sheet.getRow(rowNumber);
                // Convert and write row data to CSV
            }
        }

        // Close SXSSFWorkbook and release resources
        if (workbook instanceof SXSSFWorkbook) {
            ((SXSSFWorkbook) workbook).dispose();
        }
    }

    public byte[] convertExcelToCsv(InputStream excelInputStream, String separator) throws IOException {
        ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(zipStream)) {
            try (Workbook workbook = WorkbookFactory.create(excelInputStream)) {
                // Iterate over sheets
                for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                    Sheet sheet = workbook.getSheetAt(sheetIndex);

                    // Process sheet in chunks
                    int batchSize = 1000; // Adjust as needed
                    for (int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex += batchSize) {
                        int endIndex = Math.min(rowIndex + batchSize, sheet.getPhysicalNumberOfRows());
                        processAndWriteSheetData(sheet, rowIndex, endIndex, separator, zipOut);
                    }
                }
            }
        }
        return zipStream.toByteArray();
    }

    private void processAndWriteSheetData(Sheet sheet, int startIndex, int endIndex, String separator, ZipOutputStream zipOut)
            throws IOException {
        ByteArrayOutputStream csvOutputStream = new ByteArrayOutputStream();
        try (BufferedWriter csvWriter = new BufferedWriter(new OutputStreamWriter(csvOutputStream))) {
            for (int rowIndex = startIndex; rowIndex < endIndex; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        csvWriter.append(cell.toString()).append(separator == null ? Constants.DEFAULT_COLUMN_SEPARATOR : separator);
                    }
                    csvWriter.append("\n");
                }
                // Write CSV content to ZIP entry
                zipOut.putNextEntry(new ZipEntry(sheet.getSheetName() + (rowIndex + 1) + ".csv"));
                zipOut.write(csvOutputStream.toByteArray());
                csvOutputStream.close();
            }
            csvWriter.flush();
        }
    }
}
