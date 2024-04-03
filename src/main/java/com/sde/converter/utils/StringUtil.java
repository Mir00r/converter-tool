package com.sde.converter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class StringUtil {

    public static final String EMPTY = "";
    private static Logger logger = LoggerFactory.getLogger(StringUtil.class);

    public StringUtil() {
    }

    public static boolean hasValue(String var0) {
        return var0 != null && !var0.trim().isEmpty();
    }

    public static boolean hasValue(StringBuffer var0) {
        return var0 != null && !var0.toString().trim().isEmpty();
    }

    public static String convertNullString(String var0) {
        return var0 != null ? var0.trim() : "";
    }

    public static int convertNullInt(String var0) {
        return var0 != null ? Integer.parseInt(var0.trim()) : 0;
    }

    public static short convertNullShort(String var0) {
        return var0 != null ? Short.parseShort(var0.trim()) : 0;
    }

    public static BigDecimal convertNullBigDecimal(String var0) {
        return var0 != null ? new BigDecimal(var0.trim()) : new BigDecimal("0");
    }

    public static long convertNullLong(String var0) {
        return var0 != null ? Long.parseLong(var0.trim()) : 0L;
    }

    public static String stripNonValidXMLCharacters(String var0) {
        if (!hasValue(var0)) {
            return "";
        } else {
            StringBuffer var1 = new StringBuffer();

            for(int var3 = 0; var3 < var0.length(); ++var3) {
                char var2 = var0.charAt(var3);
                if (var2 == '\t' || var2 == '\n' || var2 == '\r' || var2 >= ' ' && var2 <= '\ud7ff' || var2 >= '\ue000' && var2 <= '�' || var2 >= 65536 && var2 <= 1114111) {
                    var1.append(var2);
                }
            }

            return var1.toString();
        }
    }

    public static boolean isAlphanumeric(String var0) {
        if (!hasValue(var0)) {
            return false;
        } else {
            char[] var1 = var0.toCharArray();
            int var2 = var1.length;

            for (char var4 : var1) {
                if (!Character.isLetterOrDigit(var4)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static String removeSpaceInString(String var0) {
        return var0.replace(" ", "");
    }

    public static String toLowerCamelCase(String var0) {
        if (!hasValue(var0)) {
            return "";
        } else {
            StringBuilder var1 = new StringBuilder();
            char[] var2 = var0.toCharArray();
            int var3 = var2.length;

            for (char var5 : var2) {
                if (!Character.isUpperCase(var5)) {
                    break;
                }

                var1.append(var5);
            }

            if (var1.length() > 0) {
                int var6 = var1.length();
                if (var6 == 1) {
                    var0 = var0.substring(0, var6).toLowerCase() + var0.substring(var6);
                } else {
                    var0 = var0.substring(0, var6 - 1).toLowerCase() + var0.substring(var6 - 1);
                }
            }

            return var0;
        }
    }

    public static String concatenateString(String var0, String var1, String var2) {
        if (!hasValue(var0) && !hasValue(var1)) {
            return "";
        } else {
            return hasValue(var0) ? var0 + var2 + convertNullString(var1) : convertNullString(var1);
        }
    }

    public static String removeRemainingStringFromACharOnwards(String var0, char var1) {
        if (!hasValue(var0)) {
            return "";
        } else {
            int var2;
            if ((var2 = var0.indexOf(var1)) > 0) {
                var0 = var0.substring(0, var2);
            }

            return var0;
        }
    }
}
