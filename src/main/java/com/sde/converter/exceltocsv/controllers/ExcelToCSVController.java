package com.sde.converter.exceltocsv.controllers;

import com.sde.converter.commons.Constants;
import com.sde.converter.exceltocsv.services.ExcelToCSVService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/convert")
public class ExcelToCSVController {

    private final ExcelToCSVService excelToCSVService;

    @Autowired
    public ExcelToCSVController(ExcelToCSVService excelToCSVService) {
        this.excelToCSVService = excelToCSVService;
    }


    @PostMapping("/excel-to-csv")
    public ResponseEntity<byte[]> excelToCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "separator", required = false) String separator,
            @RequestParam(value = "fileName", required = false) String fileName
    ) {
        try (InputStream excelInputStream = file.getInputStream(); ByteArrayOutputStream csvOutputStream = new ByteArrayOutputStream()) {

            this.excelToCSVService.convertExcelToCsv(excelInputStream, csvOutputStream, separator);
            // Prepare CSV response
            byte[] csvContent = csvOutputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", fileName == null ? Constants.DEFAULT_OUTPUT_FILE_NAME : fileName);
            return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error converting file: " + Arrays.toString(e.getMessage().getBytes())).getBytes());
        }
    }

    @PostMapping(value = "/excel-to-csv/batch-wise")
    public ResponseEntity<byte[]> excelToCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "separator", required = false) String separator,
            @RequestParam(value = "fileName", required = false) String fileName,
            @RequestParam(value = "batchSize", defaultValue = "0") int batchSize
    ) {
        try (InputStream excelInputStream = file.getInputStream(); ByteArrayOutputStream csvOutputStream = new ByteArrayOutputStream()) {

            this.excelToCSVService.convertSheetToCsvChunks(excelInputStream, csvOutputStream, separator, batchSize);
            // Prepare CSV response
            byte[] csvContent = csvOutputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", fileName == null ? Constants.DEFAULT_OUTPUT_FILE_NAME : fileName);
            return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error converting file: " + Arrays.toString(e.getMessage().getBytes())).getBytes());
        }
    }

    @PostMapping(value = "/excel-to-csv-zip")
    public ResponseEntity<byte[]> excelToCsvZip(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "separator", required = false) String separator,
            @RequestParam(name = "batchSize", defaultValue = "0") int batchSize
    ) {
        try (InputStream excelInputStream = file.getInputStream()) {

            byte[] zipContent = this.excelToCSVService.convertExcelToCsvZip(excelInputStream, separator, batchSize);
//            byte[] zipContent = this.excelToCSVService.convertExcelToCsv(excelInputStream, separator, batchSize);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/zip"));
            headers.setContentDispositionFormData(file.getOriginalFilename() + ".zip", file.getOriginalFilename() + ".zip");
            return new ResponseEntity<>(zipContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error converting file: " + Arrays.toString(e.getMessage().getBytes())).getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/excel-to-csv-zip1")
    public ResponseEntity<Resource> convertExcelToCSV(@RequestParam("file") MultipartFile file) {
        try (InputStream excelInputStream = file.getInputStream()) {
            ByteArrayResource zipResource = this.excelToCSVService.convertExcelToCSV(excelInputStream);

            // Set appropriate headers for downloading the ZIP file
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"output.zip\"");

            return ResponseEntity.ok().headers(headers).body(zipResource);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
            // Handle error response
            return ResponseEntity.internalServerError().build();
        }
    }


}
