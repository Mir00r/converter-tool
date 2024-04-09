package com.sde.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.sde.converter.commons.*;
import com.sde.converter.utils.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class BaseProxy {

    private static Logger logger = LoggerFactory.getLogger(BaseProxy.class);
    private Marker sendRequest = MarkerFactory.getMarker("SEND");
    private Marker receiveResponse = MarkerFactory.getMarker("RECEIVE");
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

    public List<OBSendAndReceive> parallelSendAndReceive(List<OBSendAndReceive> var1) {
        ConverterContext var2 = null;
        if (ThreadLocalConverterContextHolderStrategy.getSdeContext() != null) {
            var2 = ThreadLocalConverterContextHolderStrategy.getSdeContext();
        } else {
            OBUserDetail userDetail = SecurityContext.getUserDetail();
            if (userDetail != null) {
                var2 = new ConverterContext();
                var2.setUserId(userDetail.getId());
                var2.setUsername(userDetail.getUsername());
                var2.setDomainId(SecurityContext.getDomainId());
                var2.setIpAddress(SecurityContext.getIpAddress());
            }
        }

        ForkJoinSendAndReceive var8 = new ForkJoinSendAndReceive(Thread.currentThread().getName(), var1, this, var2);
        return this.forkJoinPool.invoke(var8);
    }

    @Async
    public <T extends OBBase> void sendAndIgnore(String var1, OBBase var2, Class<T> var3) {
        this.sendAndReceive(var1, var2, var3);
    }

    public <T extends OBBase> T sendAndReceive(String var1, OBBase var2, Class<T> var3, String var4) {
        return this.sendAndReceive(var1, var2, var3, (MultipartFile) null, true, var4);
    }

    public <T extends OBBase> T sendAndReceive(String var1, OBBase var2, Class<T> var3) {
        return this.sendAndReceive(var1, var2, var3, (MultipartFile) null, true);
    }

    public <T extends OBBase> T sendAndReceive(String var1, OBBase var2, Class<T> var3, MultipartFile var4) {
        return this.sendAndReceive(var1, var2, var3, var4, true);
    }

    public void preSendAndReceive(OBBase var1) {
    }

    public <T extends OBBase> T sendAndReceive(String var1, OBBase var2, Class<T> var3, MultipartFile var4, boolean var5) {
        return this.sendAndReceive(var1, var2, var3, var4, var5, PropertyUtil.getInteger(this.K), PropertyUtil.getInteger(this.C));
    }

    public <T extends OBBase> T sendAndReceive(String var1, OBBase var2, Class<T> var3, MultipartFile var4, boolean var5, String var6) {
        return this.sendAndReceive(var1, var2, var3, var4, var5, PropertyUtil.getInteger(this.K), PropertyUtil.getInteger(this.C), var6);
    }

    public <T extends OBBase> T sendAndReceive(String var1, OBBase var2, Class<T> var3, MultipartFile var4, boolean var5, Integer var6, Integer var7) {
        return this.sendAndReceive(var1, var2, var3, var4, var5, var6, var7, "POST");
    }

    public <T extends OBBase> T sendAndReceive(String var1, OBBase obBase, Class<T> var3, MultipartFile var4, boolean var5, Integer var6, Integer var7, String var8) {
        if (!StringUtil.hasValue(var8)) {
            var8 = "POST";
        }

        long var9 = System.currentTimeMillis();
        OBBase var11 = null;
        String var12 = "";
        HashMap var13 = new HashMap();
        this.addBasicAuthenticationCredentials(var13);
        this.setSendRequest(var13);
        this.preSendAndReceive(obBase);
        this.setReceiveResponse(obBase);
        ArrayList var16;
        if (!StringUtil.hasValue(obBase.getObHeader().getUsername())) {
            if (ThreadLocalConverterContextHolderStrategy.getSdeContext() != null) {
                obBase.getObHeader().setIpAddress(ThreadLocalConverterContextHolderStrategy.getSdeContext().getIpAddress());
                obBase.getObHeader().setDomainId(ThreadLocalConverterContextHolderStrategy.getSdeContext().getDomainId());
                obBase.getObHeader().setUserId(ThreadLocalConverterContextHolderStrategy.getSdeContext().getUserId());
                obBase.getObHeader().setUsername(ThreadLocalConverterContextHolderStrategy.getSdeContext().getUsername());
                obBase.getObHeader().setDefaultOrganisationUnit(ThreadLocalConverterContextHolderStrategy.getSdeContext().getDefaultOrganisationUnit());
                obBase.getObHeader().setDefaultOrganisation(ThreadLocalConverterContextHolderStrategy.getSdeContext().getDefaultOrganisation());
                obBase.getObHeader().setDefaultOrganisationUnitType(ThreadLocalConverterContextHolderStrategy.getSdeContext().getDefaultOrganisationUnitType());
                obBase.getObHeader().setDefaultOrganisationUnitCategory(ThreadLocalConverterContextHolderStrategy.getSdeContext().getDefaultOrganisationUnitCategory());
                obBase.getObHeader().setRoleCodeList(ThreadLocalConverterContextHolderStrategy.getSdeContext().getRoleCodeList());
                obBase.getObHeader().setDefaultOrganisationUnitCurrency(ThreadLocalConverterContextHolderStrategy.getSdeContext().getDefaultOrganisationCurrency());
            } else {
                OBUserDetail var14 = SecurityContext.getUserDetail();
                if (var14 == null) {
                    obBase.getObHeader().setUserId("ANONYMOUS");
                    obBase.getObHeader().setUsername("ANONYMOUS");
                } else {
                    obBase.getObHeader().setUserId(var14.getId());
                    obBase.getObHeader().setUsername(var14.getUsername());
//                    if (var14.getDefaultOrganizationUnitMpDetail() != null) {
//                        obBase.getObHeader().setDefaultOrganizationUnit(var14.getDefaultOrganizationUnitMpDetail().getOrganizationUnitDetail().getCode());
//                        obBase.getObHeader().setDefaultOrganization(var14.getDefaultOrganizationUnitMpDetail().getOrganizationUnitDetail().getOrganizationDetail().getCode());
//                        obBase.getObHeader().setDefaultOrganizationUnitType(var14.getDefaultOrganizationUnitMpDetail().getOrganizationUnitDetail().getType());
//                        obBase.getObHeader().setDefaultOrganizationUnitCategory(var14.getDefaultOrganizationUnitMpDetail().getOrganizationUnitDetail().getOrganizationUnitCategory());
//                        obBase.getObHeader().setDefaultOrganizationCurrency(var14.getDefaultOrganizationUnitMpDetail().getOrganizationUnitDetail().getOrganizationDetail().getCurrency());
//                        List var15 = var14.getDefaultOrganizationUnitMpDetail().getUserRoleDetails();
//                        if (var15 != null) {
//                            var16 = new ArrayList();
//                            Iterator var17 = var15.iterator();
//
//                            while(var17.hasNext()) {
//                                OBUserRoleDetail var18 = (OBUserRoleDetail)var17.next();
//                                var16.add(var18.getCode());
//                            }
//
//                            obBase.getObHeader().setRoleCodeList(var16);
//                        }
//                    }
                }

                obBase.getObHeader().setDomainId(SecurityContext.getDomainId());
                obBase.getObHeader().setIpAddress(SecurityContext.getIpAddress());
            }
        }

        try {
            try {
                String var27 = PropertyUtil.getValue(var1);
                Validate.notEmpty(var27, "Please check the environment.properties, key = [" + var1 + "] the value not defined");
                if (Constants.DataType.JSON.name().equals(this.formatType)) {
                    var12 = PropertyUtil.getValue(this.baseURL) + this.getReceiveResponse("/services/rest") + var27;
                    this.setReceiveResponse(var13, "application/json");
                    var13.put("Host", PropertyUtil.getValue(this.baseURL).substring(PropertyUtil.getValue(this.baseURL).lastIndexOf("/") + 1));
                    String var29 = this.objectMapper.writeValueAsString(obBase);
                    if (logger.isInfoEnabled()) {
                        logger.info(this.sendRequest, this.getReceiveResponse(var12, var13, var29, var6, var7, var5, var8));
                    }

                    String var30 = HTTPManager.sendAndReceive(var29, var4, var12, var6, var7, var13, var8);
                    if (logger.isInfoEnabled()) {
                        logger.info(this.sendRequest, this.getReceiveResponse(var12, var13, var30, var9, var5));
                    }

                    var11 = this.objectMapper.readValue(var30, var3);
                    return (T) var11;
                }

                if (Constants.DataType.XML.name().equals(this.formatType)) {
                    return (T) var11;
                }

                if (Constants.DataType.SOAP.name().equals(this.formatType)) {
                    return (T) var11;
                }

                if (Constants.DataType.RSS.name().equals(this.formatType)) {
                    var12 = this.getReceiveResponse("/services/rest") + var27 + ".rss";
                } else if (Constants.DataType.ATOM.name().equals(this.formatType)) {
                    var12 = this.getReceiveResponse("/services/rest") + var27 + ".atom";
                }
            } catch (Exception var24) {
                Exception var26 = var24;
                logger.error(var24.getMessage(), var24);

                try {
                    OBBase var28 = var3.newInstance();
                    var28.getObHeader().setSuccess(false);
                    var28.getObHeader().setStatusCode("AB");
                    var28.getObHeader().setStatusMessage(var26.getMessage());
                    if (var26 instanceof SocketTimeoutException) {
                        var16 = new ArrayList();
                        OBErrorDetail var31 = new OBErrorDetail();
                        var31.setCode("TIMEOUT");
                        var31.setMessage(var26.getMessage());
                        var16.add(var31);
                        var28.getObHeader().setErrorDetails(var16);
                    }

                    return (T) var28;
                } catch (Exception var23) {
                    logger.error(var23.getMessage(), var23);
                }
            }
            return (T) var11;
        } finally {
            ;
        }
    }

    private String getReceiveResponse(String sendUrl, HashMap<String, String> hashMap, String responseValue, Integer connectTimeout, Integer readTimeout, boolean isLogSensitiveKey, String requestMethod) {
        StringBuilder response = new StringBuilder();
        response.append(separator).append("--------------------------------------").append(separator);
        response.append("Send URL = [").append(sendUrl).append("], connectTimeout = [").append(connectTimeout).append("], readTimeout = [").append(readTimeout).append("], requestMethod = [").append(requestMethod).append("]");
        response.append(separator);
        if (hashMap != null) {

            for (Map.Entry<String, String> stringStringEntry : hashMap.entrySet()) {
                if ("Authorization".equals(stringStringEntry.getKey())) {
                    response.append(stringStringEntry.getKey()).append(" : (value)").append(separator);
                } else if (!"TAG-ID".equals(stringStringEntry.getKey())) {
                    response.append(stringStringEntry.getKey()).append(" : ").append(stringStringEntry.getValue()).append(separator);
                }
            }
        }

        return getLogWithSensitiveKeyOrNot(responseValue, isLogSensitiveKey, response);
    }

    private String getReceiveResponse(String var1, HashMap<String, String> hashMap, String var3, long var4, boolean isLogSensitiveKey) {
        long var7 = System.currentTimeMillis() - var4;
        StringBuilder var9 = new StringBuilder();
        var9.append(separator).append("--------------------------------------").append(separator);
        var9.append("Receive URL = [").append(var1).append("], execution time = [").append(var7).append("] ms.");
        var9.append(separator);

        return getLogWithSensitiveKeyOrNot(var3, isLogSensitiveKey, var9);
    }

    private String getReceiveResponse(String var1) {
        return this.noMappingURL ? "" : var1;
    }

    private String getLogWithSensitiveKeyOrNot(String responseValue, boolean isLogSensitiveKey, StringBuilder response) {
        if (isLogSensitiveKey) {
            String[] var11 = LogCensorUtil.getSensitiveKeys();
            String var10 = LogCensorUtil.censorSensitiveInformation(responseValue, var11, this.formatType);
            response.append(var10);
        } else {
            response.append("(value) ... [").append(responseValue.getBytes().length).append("] bytes");
        }

        response.append(separator).append("--------------------------------------");
        return response.toString();
    }

    private void setSendRequest(Map<String, String> var1) {
        FishTagUtil.addIdToHttpHeader(var1);
    }

    private void setReceiveResponse(OBBase var1) {
        String var2 = FishTagUtil.generateId();
        OBHeader var3;
        if (var1.getObHeader() != null) {
            var3 = var1.getObHeader();
        } else {
            var3 = new OBHeader();
        }

        var3.setId(var2);
        var3.setDateTimeIn(DateUtil.retrieveDateNow());
        if (ThreadLocalConverterContextHolderStrategy.getSdeContext() != null) {
            var3.setDomainId(ThreadLocalConverterContextHolderStrategy.getSdeContext().getDomainId());
        } else {
            var3.setDomainId(SecurityContext.getDomainId());
        }

        var1.setObHeader(var3);
    }

    private void setReceiveResponse(Map<String, String> var1, String var2) {
        var1.put("Content-Type", var2);
    }

    private void setReceiveResponse(Map<String, String> var1) {
        var1.put("Accept-Encoding", "gzip,deflate");
    }

    public void addBasicAuthenticationCredentials(Map<String, String> var1) {
        String var2 = PropertyUtil.getValue(this.username) + ":" + PropertyUtil.getValue(this.password);
        byte[] var3 = Base64.encodeBase64(var2.getBytes());
        String var4 = "Basic " + new String(var3);
        var1.put("Authorization", var4);
    }


}
