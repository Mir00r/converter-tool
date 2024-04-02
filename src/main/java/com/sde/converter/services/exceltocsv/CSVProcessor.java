package com.sde.converter.services.exceltocsv;

import com.opencsv.CSVWriter;
import com.sde.converter.utils.AppUtil;
import com.sde.converter.commons.Constants;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class CSVProcessor {

    public void processExcelChunkToCSV(File sourceDirectory, File outputDirectory, Character separator) throws IOException {
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

    public void processExcelChunkToCSV(Sheet excelFile, File outputDirectory, Character separator) throws IOException {
        String csvFileName = String.format("%s.csv", excelFile.getSheetName().replace(".xlsx", ""));
        File csvFile = new File(outputDirectory, csvFileName);

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile), separator == null ? Constants.DEFAULT_COLUMN_SEPARATOR_CH : separator, '"', '"', "\n")) {
            for (Row row : WorkbookFactory.create((POIFSFileSystem) excelFile).getSheetAt(0)) {
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
