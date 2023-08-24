package com.sde.converter.exceltocsv.services;

import com.sde.converter.commons.Constants;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Iterator;
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

    public byte[] convertExcelToCsvZip(InputStream excelInputStream, String separator, int dataSize) throws IOException {
        Workbook workbook = new XSSFWorkbook(excelInputStream);
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
}
