package com.sde.converter.commons;

public class Constants {
    public static final String DEFAULT_OUTPUT_FILE_NAME = "Output.csv";
    public static final String DEFAULT_COLUMN_SEPARATOR = ",";
    public static final char DEFAULT_COLUMN_SEPARATOR_CH = ',';
    public static final int DEFAULT_BATCH_SIZE = 10000;

    // Error Constants
    public static final String MULTI_SUCCESS_TYPE = "MULTI_SUCCESS";
    public static final String ERROR_TYPE = "ERROR";

    public static final String WARNING_TYPE = "WARNING";
    public static final String APPROVAL_TYPE = "APPROVAL";
    public static final String SUCCESS_TYPE = "SUCCESS";
    public static final String FAIL_RESPONSE_STATUS_CODE = "AB";
    public static final String PENDING_APPROVAL_STATUS_CODE = "SA";
    public static final String PARTIAL_SUCCESS_RESPONSE_STATUS_CODE = "PS";
    public static final String WARNING_RESPONSE_STATUS_CODE = "WA";
    public static final String SUCCESS_RESPONSE_STATUS_CODE = "AA";

    public static final String YES = "Y";
    public static final String NO = "N";


    public enum DataType {
        JSON, XML, SOAP, RSS, ATOM
    }
}
