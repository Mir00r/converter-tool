package com.sde.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.sde.converter.utils.PropertyUtil;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class BaseProxy {

    private static Logger logger = LoggerFactory.getLogger(BaseProxy.class);
    private Marker sendRequest = MarkerFactory.getMarker("SEND");
    private Marker recieveResponse = MarkerFactory.getMarker("RECEIVE");
    private String connectionTimeOut;
    private String readTimeOut;
    private String baseURL;
    private boolean noMappingURL = false;
    private String username;
    private String password;
    private String formatType = "JSON";
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String separator = System.getProperty("line.separator");
    private ForkJoinPool forkJoinPool;

    public BaseProxy() {
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        this.objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        Calendar calendar = Calendar.getInstance();
        this.objectMapper.setDateFormat((new StdDateFormat()).withTimeZone(calendar.getTimeZone()).withColonInTimeZone(false));
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    }

    public void close() {
        this.forkJoinPool.shutdown();

        try {
            this.forkJoinPool.awaitTermination(PropertyUtil.getInteger(this.connectionTimeOut) != null ? (long) PropertyUtil.getInteger(this.connectionTimeOut) : 60L, TimeUnit.SECONDS);
        } catch (InterruptedException var2) {
            logger.debug(var2.getMessage(), var2);
            this.forkJoinPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void addBasicAuthenticationCredentials(Map<String, String> var1) {
        String var2 = PropertyUtil.getValue(this.username) + ":" + PropertyUtil.getValue(this.password);
        byte[] var3 = Base64.encodeBase64(var2.getBytes());
        String var4 = "Basic " + new String(var3);
        var1.put("Authorization", var4);
    }


}
