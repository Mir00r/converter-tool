package com.sde.converter;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ExcelToCsvConverter {
    private static final int NUM_PRODUCERS = 2;
    private static final int NUM_CONSUMERS = 4;
    private static final int CHUNK_SIZE = 1000;

    private final BlockingQueue<File> excelFilesQueue = new LinkedBlockingQueue<>();
    private final ExecutorService producerExecutor = Executors.newFixedThreadPool(NUM_PRODUCERS);
    private final ExecutorService consumerExecutor = Executors.newFixedThreadPool(NUM_CONSUMERS);

    public void convertExcelFilesToCsv(String inputDirectory, String outputDirectory) {
        File[] excelFiles = new File(inputDirectory).listFiles((dir, name) -> name.endsWith(".xlsx"));

        for (File excelFile : excelFiles) {
            excelFilesQueue.offer(excelFile);
        }

        for (int i = 0; i < NUM_PRODUCERS; i++) {
            producerExecutor.execute(this::processExcelFile);
        }

        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumerExecutor.execute(this::convertToCsv);
        }

        producerExecutor.shutdown();
        consumerExecutor.shutdown();
    }

    private void processExcelFile() {
        while (!excelFilesQueue.isEmpty()) {
            try {
                File excelFile = excelFilesQueue.poll();
                if (excelFile != null) {
                    // Process the Excel file and extract data

                    // Convert extracted data to CSV format
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void convertToCsv() {
        while (true) {
            try {
                File excelFile = excelFilesQueue.take();
                if (excelFile == null) {
                    break; // No more files to process
                }

                // Convert Excel data to CSV
                // Save the CSV file in the output directory
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
