package com.sde.converter.exceltocsv.services;

import com.opencsv.CSVWriter;
import com.sde.converter.commons.Constants;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

//    @Async
    public byte[] convertExcelToCsvZip(InputStream excelInputStream, String separator, int dataSize) throws IOException {
        Workbook workbook = new XSSFWorkbook(excelInputStream);
//        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(excelInputStream));
//        Workbook workbook = WorkbookFactory.create(excelInputStream);

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
                    // Dispose of resources
                    if (workbook instanceof SXSSFWorkbook) {
                        ((SXSSFWorkbook) workbook).dispose();
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

    public byte[] convertExcelToCsv(InputStream excelInputStream, String separator, int dataSize) throws IOException {
        ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(zipStream)) {
            // Open the Excel file using SXSSFWorkbook
//            Workbook workbook = new SXSSFWorkbook(new XSSFWorkbook(excelInputStream), 100); // 100 rows in memory at a time
//            Sheet sheet1 = workbook.getSheetAt(0); // Get the desired sheet

//            int totalNumberOfSheets = WorkbookFactory.create(excelInputStream).getNumberOfSheets();
//            int totalNumberOfSheets = new XSSFWorkbook(excelInputStream).getNumberOfSheets();
//            try (Workbook workbook = new SXSSFWorkbook(new XSSFWorkbook(excelInputStream), 100)) {
            try (Workbook workbook = WorkbookFactory.create(excelInputStream)) {
                int totalNumberOfSheets = workbook.getNumberOfSheets();
                // Iterate over sheets
                for (int sheetIndex = 0; sheetIndex < totalNumberOfSheets; sheetIndex++) {
                    Sheet sheet = workbook.getSheetAt(sheetIndex);

                    // Process sheet in chunks
                    int batchSize = dataSize == 0 ? Constants.DEFAULT_BATCH_SIZE : dataSize; // Adjust as needed
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

    public ByteArrayResource convertExcelToCSV(InputStream excelInputStream) throws IOException, InvalidFormatException {
        File outputDirectory = createOutputDirectory(); // Create a temporary directory

        OPCPackage opcPackage = OPCPackage.open(excelInputStream);
        XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);

//        Workbook workbook = new XSSFWorkbook(opcPackage);

        for (Sheet sheet : workbook) {
            String csvFileName = sheet.getSheetName() + ".csv";
            File csvFile = new File(outputDirectory, csvFileName);
            try (CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile))) {
                for (Row row : sheet) {
                    String[] csvRow = new String[row.getLastCellNum()];

                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        csvRow[i] = getCellValueAsString(cell);
                    }
                    csvWriter.writeNext(csvRow);
                }
            }
        }
        workbook.close();
        opcPackage.close();

        // Compress the generated CSV files into a single ZIP resource
        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipFile = new ZipOutputStream(zipOutputStream)) {
            File[] csvFiles = outputDirectory.listFiles((dir, name) -> name.endsWith(".csv"));
            for (File csvFile : csvFiles) {
                try (FileInputStream csvInputStream = new FileInputStream(csvFile)) {
                    ZipEntry zipEntry = new ZipEntry(csvFile.getName());
                    zipFile.putNextEntry(zipEntry);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = csvInputStream.read(buffer)) > 0) {
                        zipFile.write(buffer, 0, len);
                    }
                    zipFile.closeEntry();
                }
            }
        }
        return new ByteArrayResource(zipOutputStream.toByteArray());
    }

    private File createOutputDirectory() {
        File outputDirectory = new File("output");
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        return outputDirectory;
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
