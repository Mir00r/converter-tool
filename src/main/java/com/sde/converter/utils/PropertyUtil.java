package com.sde.converter.utils;

import com.sde.converter.SystemParameterCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class PropertyUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
    private static Properties properties = new Properties();


    private static String properties(String var0) {
        String var1 = properties.getProperty(var0);
        return "";
//        return var1 != null ? var1 : (String) ((SystemParameterCache) SpringBeanUtil.getBean(SystemParameterCache.class)).getObject(var0);
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        PropertyUtil.properties = properties;
    }

    public static String getValue(String var0) {
        return properties(var0);
    }

    public static long getLong(String var0) {
        String var1 = properties(var0);
        return StringUtil.hasValue(var1) ? Long.parseLong(var1.trim()) : null;
    }

    public static Integer getInteger(String var0) {
        String var1 = properties(var0);
        return StringUtil.hasValue(var1) ? Integer.valueOf(var1.trim()) : null;
    }

    public static boolean getBoolean(String var0) {
        return Boolean.parseBoolean(properties(var0));
    }

    public static void removeValue(String var0) {
        properties.remove(StringUtil.convertNullString(var0));
    }

    public static void insertUpdateValue(String var0, String var1) {
        properties.put(StringUtil.convertNullString(var0), decryptData(StringUtil.convertNullString(var1)));
    }

    public static String decryptData(String var0) {
        if (StringUtil.hasValue(var0)) {
            if (var0.startsWith(AESUtil.prefix)) {
                var0 = var0.substring(AESUtil.prefix.length());

                try {
                    var0 = AESUtil.decryptBase64SafeString(var0);
                } catch (Exception var2) {
                    logger.error(var2.getMessage(), var2);
                }
            } else if (var0.startsWith("{3DES}")) {
                var0 = var0.substring("{3DES}".length());
            }
        }

        return var0;
    }
}
