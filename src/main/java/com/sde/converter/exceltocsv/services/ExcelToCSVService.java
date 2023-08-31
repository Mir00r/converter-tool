package com.sde.converter.exceltocsv.services;

import com.opencsv.CSVWriter;
import com.sde.converter.AppUtil;
import com.sde.converter.commons.Constants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
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

    public void convertLargeExcelToCSV(InputStream excelInputStream, Character separator, int dataSize) throws IOException {
        int chunkSize = dataSize == 0 ? Constants.DEFAULT_BATCH_SIZE : dataSize; // Adjust the chunk size based on your needs
        File excelDirectory = AppUtil.createOutputDirectory("output/excel"); // Create a temporary directory
        File csvDirectory = AppUtil.createOutputDirectory("output/csv"); // Create a temporary directory

        try (Workbook workbook = WorkbookFactory.create(excelInputStream)) {
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
                    }
                    startRow += chunkSize;
                    chunkNumber++;
                }
            }
        }
        // Process the chunk and convert to CSV
        processExcelChunk(excelDirectory, csvDirectory, separator);
        // Compress CSV files into a single ZIP resource
        compressCSVFiles(csvDirectory);
    }

    private void processExcelChunk(File sourceDirectory, File outputDirectory, Character separator) throws IOException {
        File[] excelFiles = sourceDirectory.listFiles((dir, name) -> name.endsWith(".xlsx"));
        assert excelFiles != null;
        for (File excelFile : excelFiles) {
            String csvFileName = String.format("%s.csv", excelFile.getName().replace(".xlsx", ""));
            File csvFile = new File(outputDirectory, csvFileName);

            try (CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile), separator == null ? Constants.DEFAULT_COLUMN_SEPARATOR_CH : separator, '"', '"', "\n")) {
                for (Row row : WorkbookFactory.create(excelFile).getSheetAt(0)) {
                    String[] csvRow = new String[row.getLastCellNum()];

                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        csvRow[i] = AppUtil.getCellValueAsString(cell);
                    }
                    csvWriter.writeNext(csvRow);
                }
            }
        }
    }

    private void compressCSVFiles(File outputDirectory) throws IOException {
        File[] csvFiles = outputDirectory.listFiles((dir, name) -> name.endsWith(".csv"));

        try (FileOutputStream zipFileOutputStream = new FileOutputStream(outputDirectory + File.separator + "output.zip");
             ZipOutputStream zipOutputStream = new ZipOutputStream(zipFileOutputStream)) {
            for (File csvFile : csvFiles) {
                try (FileInputStream csvInputStream = new FileInputStream(csvFile)) {
                    // Construct the entry name relative to the outputDirectory
                    String entryName = csvFile.getName();

                    // Create the ZIP entry using the constructed entry name
                    ZipEntry zipEntry = new ZipEntry(entryName);
                    zipOutputStream.putNextEntry(zipEntry);

                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = csvInputStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }
                    zipOutputStream.closeEntry();
                }
            }
        }
    }
}
