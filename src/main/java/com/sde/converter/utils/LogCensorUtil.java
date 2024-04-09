package com.sde.converter.utils;

import com.sde.converter.commons.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogCensorUtil {

    private static final String value = "(value)";
    private static Logger log = LoggerFactory.getLogger(LogCensorUtil.class);

    public LogCensorUtil() {
    }

    public static String censorSensitiveInformation(String var0, String var1, String var2) {
        if (Constants.DataType.JSON.name().equals(var2)) {
            return setLog(var0, var1);
        } else {
            return !Constants.DataType.XML.name().equals(var2) && !Constants.DataType.SOAP.name().equals(var2) ? var0 : setValue(var0, var1);
        }
    }

    public static String censorSensitiveInformation(String var0, String[] var1, String var2) {
        if (Constants.DataType.JSON.name().equals(var2)) {
            return setLog(var0, var1);
        } else {
            return !Constants.DataType.XML.name().equals(var2) && !Constants.DataType.SOAP.name().equals(var2) ? var0 : setValue(var0, var1);
        }
    }

    private static String setValue(String var0, String var1) {
        var0 = var0.replaceAll(String.format("<%s((.+?)|(.?))>[\\s\\S]*?</%s>", var1, var1), "<" + var1 + "$1>" + "(value)" + "</" + var1 + ">");
        return var0;
    }

    private static String setValue(String var0, String[] var1) {
        String var2 = String.join("|", var1);
        var0 = var0.replaceAll(String.format("(<(%s)(.+?|.?)>)[\\s\\S]*?(</(%s)>)", var2, var2), "$1(value)$4");
        return var0;
    }

    private static String setLog(String var0, String var1) {
        var0 = var0.replaceAll(String.format("(?i)\"%s\"\\s*:\\s*([[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$|true|false]+)", var1), "\"" + var1 + "\" : " + "(value)");
        var0 = var0.replaceAll(String.format("(?i)\"%s\"\\s*:\\s*\"((?=[ -~])[^\"]+)\"", var1), "\"" + var1 + "\" : " + "(value)");
        var0 = var0.replaceAll(String.format("(?i)\"%s\"\\s*:\\s*\\[((?=[ -~])[^\\]]+)\\]", var1), "\"" + var1 + "\" : [ " + "(value)" + " ]");
        return var0;
    }

    private static String setLog(String var0, String[] var1) {
        String var2 = String.join("|", var1);
        var0 = var0.replaceAll(String.format("(?i)\"(%s)\"\\s*:\\s*([[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$]+|true|false|(\"(?=[ -~])[^\"]+\"))", var2), "\"$1\" : (value)");
        var0 = var0.replaceAll(String.format("(?i)\"(%s)\"\\s*:\\s*\\[((?=[ -~])[^\\]]+)\\]", var2), "\"$1\" : [ (value) ]");
        return var0;
    }

    public static boolean isSensitiveKey(String var0) {
        return setLog(getSensitiveKeys(), var0);
    }

    public static String[] getSensitiveKeys() {
        String var0 = PropertyUtil.getValue("converter.log.censor.sensitive.key");
        return StringUtil.hasValue(var0) ? var0.split("\\,") : new String[0];
    }

    private static boolean setLog(String[] var0, String var1) {
        int var2 = 0;

        for (int var3 = var0.length; var2 < var3; ++var2) {
            if (var0[var2].equalsIgnoreCase(var1)) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] var0) throws Exception {
        String var1 = "{\"obUserDetail\" : {\n\"password\" : \"$2a$10$r3lFiB3wKcYZJ6KrrWuNk.0fIgc6lmM56LCeHR58IidytB6thviQG\"\n}\n}";
        String var2 = setLog(var1, "password");
        System.out.print(var2);
        var1 = "{\"obUserDetail\" : {\n\"decimal\" : -1.999e111\n}\n}";
        var2 = setLog(var1, "decimal");
        System.out.print(var2);
        var1 = "{\"obUserDetail\" : {\n\"boolean\" : true\n}\n}";
        var2 = setLog(var1, "boolean");
        System.out.print(var2);
    }
}
