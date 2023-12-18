package com.sde.converter.handler;

import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ExcelSheetContentHandler extends DefaultHandler {

    private final SharedStringsTable sharedStringsTable;
    private final CSVWriter csvWriter;
    private final List<String[]> rowData = new ArrayList<>();
    private StringBuilder cellContents = new StringBuilder();
    private int currentColumn = -1;
    private int currentRow = -1;
    private boolean nextIsString;
    private boolean isCellOpen;

    private Map<Integer, String> columnNames;
    private String currentCellValue;
    private String[] header = new String[0];

    public ExcelSheetContentHandler(SharedStringsTable sharedStringsTable, CSVWriter csvWriter) {
        this.sharedStringsTable = sharedStringsTable;
        this.csvWriter = csvWriter;
        this.columnNames = new HashMap<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//        if (qName.equals("row")) {
//            header = new String[attributes.getLength()];
//            Arrays.fill(header, ""); // Initialize header with empty strings
//        } else if (qName.equals("c")) {
//            String cellType = attributes.getValue("t");
//            String cellValue = attributes.getValue("v");
//            int columnIndex = CellReference.convertColStringToIndex(attributes.getValue("r"));
//
//            if (header.length <= columnIndex) {
//                String[] newHeader = Arrays.copyOf(header, columnIndex + 1);
//                Arrays.fill(newHeader, header.length, newHeader.length, "");
//                header = newHeader;
//            }
//
//            if (cellType != null && cellType.equals("s") && cellValue != null) {
//                int sstIndex = Integer.parseInt(cellValue);
//                cellValue = new XSSFRichTextString(String.valueOf(sstIndex)).toString();
//            }
//
//            header[columnIndex] = cellValue;
//        }

        if ("c".equals(qName)) {
            String cellType = attributes.getValue("t");
            isCellOpen = true;

            // Clear the current cell data
//            currentCellValue = "";
            cellContents = new StringBuilder();
        }

//        if (qName.equals("c")) {
//            String cellType = attributes.getValue("t");
//            if (cellType != null && cellType.equals("s")) {
//                currentColumn = attributes.getIndex("r");
//                cellContents.setLength(0);
//            }
//        }

//        if ("c".equals(qName)) {
//            String cellType = attributes.getValue("t");
//            String columnReference = attributes.getValue("r");
//
//            if (columnReference != null) {
//                currentColumn = getColumnIndexFromReference(columnReference);
//                CellType type = (cellType != null && cellType.equals("s")) ? CellType.STRING : CellType.NUMERIC;
//
//                nextIsString = type == CellType.STRING;
//            }
//        } else if ("row".equals(qName)) {
//            currentRow = attributes.getIndex("r") - 1;
//        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (isCellOpen) {
            // Cell value is complete, handle it (e.g., write to CSV)
            String cellData = cellContents.toString();
            // Handle the cell data, e.g., write to a CSV file
//            writeToCsv(cellData);

            isCellOpen = false;
        }

//        if (qName.equals("row")) {
//            if (rowData.size() == 100000) {
//                try {
//                    writeCSV();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                rowData.clear();
//            }
//        }
//        if ("c".equals(qName)) {
//            rowData.add(currentCellValue.split(","));
//            isCellOpen = false;
//        }

//        if (qName.equals("c")) {
//            if (currentColumn >= 0) {
//                int index = Integer.parseInt(cellContents.toString());
//                String[] value = new String[]{String.valueOf(sharedStringsTable.getItemAt(index))};
//                rowData.add(value);
//                cellContents.setLength(0);
//                currentColumn = -1;
//            }
//        } else if (qName.equals("row")) {
//            csvWriter.writeAll(rowData);
//            rowData.clear();
//        }
//        if ("v".equals(qName)) {
//            if (nextIsString) {
//                int idx = Integer.parseInt(currentCellValue);
//                currentCellValue = new XSSFRichTextString(String.valueOf(sharedStringsTable.getItemAt(idx))).toString();
//                nextIsString = false;
//            }
//
//            columnNames.put(currentColumn, currentCellValue);
//        } else if ("row".equals(qName)) {
//            if (currentRow >= 0) {
//                // Process the row data, using columnNames to access cell values
//                // You can use dataFormatter.formatCellValue to get formatted cell values
//            }
//
//            // Clear columnNames for the next row
//            columnNames.clear();
//            currentRow = -1;
//        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
//        cellContents.append(ch, start, length);
        if (isCellOpen) {
            currentCellValue += new String(ch, start, length);
        }
    }

    private void writeCSV() throws IOException {
        String sheetName = header[0];
        String csvFileName = "csvPrefix" + sheetName + ".csv";
        String csvFilePath = "|" + File.separator + csvFileName;

        try (FileWriter writer = new FileWriter(csvFilePath);
             CSVWriter csvWriter = new CSVWriter(writer, '|', CSVWriter.DEFAULT_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            csvWriter.writeNext(header);
            csvWriter.writeAll(rowData);
        }

        System.out.println("CSV file written: " + csvFilePath);
    }

    private int getColumnIndexFromReference(String reference) {
        int col = 0;
        for (int i = 0; i < reference.length(); i++) {
            if (Character.isLetter(reference.charAt(i))) {
                col = col * 26 + (Character.toUpperCase(reference.charAt(i)) - 'A' + 1);
            }
        }
        return col - 1;
    }
}
