package com.sde.converter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

public class FishTagUtil {

    private static Logger log = LoggerFactory.getLogger(FishTagUtil.class);
    private static final String TAG_ID_1 = "TAG-ID";
    private static final String TAG_ID_2 = "TAG-ID";
    private static final String FISH_TAG_ID = "fishTagId";

    public FishTagUtil() {
    }

    public static void setIdToSession(HttpServletRequest var0) {
        var0.getSession().setAttribute(TAG_ID_2, getIdFromMDC());
    }

    public static String getIdFromSession(HttpServletRequest var0) {
        try {
            if (var0.getSession(false) != null) {
                String var1 = (String) var0.getSession().getAttribute(TAG_ID_2);
                var0.getSession().removeAttribute(TAG_ID_2);
                return var1;
            }
        } catch (Exception var2) {
            log.error(var2.getMessage(), var2);
        }
        return null;
    }

    public static String getIdFromHttpHeader(HttpServletRequest var0) {
        try {
            return var0.getHeader(TAG_ID_1);
        } catch (Exception var2) {
            log.error(var2.getMessage(), var2);
            return null;
        }
    }

    public static void addIdToHttpHeader(Map<String, String> var0) {
        String var1 = getIdFromMDC();
        if (var1 != null) {
            var0.put(TAG_ID_1, var1);
        }

    }

    public static void setIdToMDC(String var0) {
        MDC.put(FISH_TAG_ID, var0);
    }

    public static String getIdFromMDC() {
        try {
            return MDC.get(FISH_TAG_ID);
        } catch (Exception var1) {
            log.error(var1.getMessage(), var1);
            return null;
        }
    }

    public static String generateId() {
        try {
            return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        } catch (Exception var1) {
            log.error(var1.getMessage(), var1);
            return null;
        }
    }

    public static void clear() {
        MDC.remove(FISH_TAG_ID);
    }
}
