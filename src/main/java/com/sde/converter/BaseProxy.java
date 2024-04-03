package com.sde.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sde.converter.utils.PropertyUtil;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public class BaseProxy {

    private static Logger F = LoggerFactory.getLogger(BaseProxy.class);
    private Marker B = MarkerFactory.getMarker("SEND");
    private Marker A = MarkerFactory.getMarker("RECEIVE");
    private String connectionTimeOut;
    private String readTimeOut;
    private String baseURL;
    private boolean noMappingURL = false;
    private String username;
    private String password;
    private String formatType = "JSON";
    private ObjectMapper I = new ObjectMapper();
    private static String D = System.getProperty("line.separator");
    private ForkJoinPool M;

//    public void addBasicAuthenticationCredentials(Map<String, String> var1) {
//        String var2 = PropertyUtil.getValue(this.G) + ":" + PropertyUtil.getValue(this.J);
//        byte[] var3 = Base64.encodeBase64(var2.getBytes());
//        String var4 = "Basic " + new String(var3);
//        var1.put("Authorization", var4);
//    }
}
