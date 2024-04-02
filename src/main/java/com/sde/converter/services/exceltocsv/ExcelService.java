package com.sde.converter.services.exceltocsv;

import com.opencsv.CSVWriter;
import com.sde.converter.utils.AppUtil;
import com.sde.converter.handler.ExcelSheetContentHandler;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ExcelService {

    private static final int NUM_CONSUMERS = 2; // Number of consumer threads

    private final ExcelProcessor excelProcessor;
    private final CSVProcessor csvProcessor;


    @Autowired
    public ExcelService(ExcelProcessor excelProcessor, CSVProcessor csvProcessor) {
        this.excelProcessor = excelProcessor;
        this.csvProcessor = csvProcessor;
    }

    @Async
    public void processExcel(MultipartFile excelFile, Character separator, int dataSize) {
        File excelDirectory = AppUtil.createOutputDirectory("output/excel"); // Create a temporary directory
        File csvDirectory = AppUtil.createOutputDirectory("output/csv"); // Create a temporary directory
        BlockingQueue<Sheet> taskQueue = new LinkedBlockingQueue<>();

        ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
        ExecutorService consumerExecutor = Executors.newFixedThreadPool(NUM_CONSUMERS);

        // Producer thread
        producerExecutor.submit(() -> {
            try {
                excelProcessor.extractSheets(excelFile, dataSize, excelDirectory, taskQueue);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Consumer threads
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumerExecutor.submit(() -> {
                while (true) {
                    try {
                        Sheet sheetInfo = taskQueue.take();
                        if (sheetInfo == null) {
                            break; // No more work
                        }
                        csvProcessor.processExcelChunkToCSV(sheetInfo, csvDirectory, separator);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
//                try {
//                    csvProcessor.processExcelChunkToCSV(excelDirectory, csvDirectory, separator);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            });
        }

        // Shutdown executors when processing is done
        producerExecutor.shutdown();
        consumerExecutor.shutdown();
    }

    public void vaiConvertKor() {
        String excelFilePath = "sample_data/Employee Sample Data_1M.xlsx";
        String csvDirectory = AppUtil.createOutputDirectory("csv").getAbsolutePath();
        String csvPrefix = "output_";
        System.out.println("Conversion completed successfully.");

//        try {
//            FileInputStream excelFile = new FileInputStream(excelFilePath);
//            Workbook workbook = new XSSFWorkbook(excelFile);
//
//            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
//                Sheet sheet = workbook.getSheetAt(sheetIndex);
//                Iterator<Row> rowIterator = sheet.iterator();
//
//                int chunkSize = 100000;
//                int chunkIndex = 1;
//                List<List<String>> currentChunk = new ArrayList<>();
//
//                while (rowIterator.hasNext()) {
//                    Row row = rowIterator.next();
//                    List<String> rowData = new ArrayList<>();
//
//                    for (Cell cell : row) {
//                        rowData.add(cell.toString());
//                    }
//
//                    currentChunk.add(rowData);
//
//                    if (currentChunk.size() == chunkSize || !rowIterator.hasNext()) {
//                        writeCSV(csvDirectory, csvPrefix + sheet.getSheetName() + "_part" + chunkIndex + ".csv", currentChunk);
//                        currentChunk.clear();
//                        chunkIndex++;
//                    }
//                }
//            }
//
//            workbook.close();
//            excelFile.close();
//
//            System.out.println("Conversion completed successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private static void writeCSV(String directory, String fileName, List<List<String>> data) throws IOException {
        File csvFile = new File(directory, fileName);
        FileWriter writer = new FileWriter(csvFile);

        for (List<String> row : data) {
            writer.append(String.join(",", row));
            writer.append("\n");
        }

        writer.close();
        System.out.println("CSV file written: " + csvFile.getAbsolutePath());
    }

    public void processLargeExcel(MultipartFile excelFile, Character separator, int dataSize) throws Exception {
        File inputFile = new File("sample_data/Employee Sample Data_1M.xlsx");

//        InputStream excelInputStream = new FileInputStream("C:/Users/sdarmd/Downloads/Employee Sample Data_1M.xlsx");
//        File outputDirectory = new File("output");
//        outputDirectory.mkdirs();
//        convertExcelToCsv(excelInputStream, outputDirectory);

        try (
                OPCPackage opcPackage = OPCPackage.open(inputFile)
//                InputStream excelInputStream = excelFile.getInputStream()
        ) {
            File excelDirectory = AppUtil.createOutputDirectory("output/excel"); // Create a temporary directory
            File csvDirectory = AppUtil.createOutputDirectory("output/csv"); // Create a temporary directory

//            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            SharedStringsTable sharedStringsTable = new SharedStringsTable();
            XSSFReader xssfReader = new XSSFReader(opcPackage);

//            XMLReader xmlReader = createXMLReader();
            // Create a SAXParserFactory and configure it
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true); // Enable namespace support

            // Create a SAXParser
            SAXParser saxParser = saxParserFactory.newSAXParser();

            // Get the XMLReader from the SAXParser
            XMLReader xmlReader = saxParser.getXMLReader();
            String csvFileName = String.format("%s.csv", inputFile.getName().replace(".xlsx", ""));
            File csvFile = new File(excelDirectory, csvFileName);
            ExcelSheetContentHandler contentHandler = new ExcelSheetContentHandler(sharedStringsTable, new CSVWriter(new FileWriter(csvFile)));
            xmlReader.setContentHandler(contentHandler);

            Iterator<InputStream> sheetStreams = xssfReader.getSheetsData();
            while (sheetStreams.hasNext()) {
                try (InputStream sheetStream = sheetStreams.next()) {
                    InputSource sheetSource = new InputSource(sheetStream);
                    xmlReader.parse(sheetSource);
                }
            }
        } catch (IOException | OpenXML4JException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}

