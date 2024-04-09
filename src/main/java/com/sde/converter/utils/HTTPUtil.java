package com.sde.converter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class HTTPUtil {

    private static Logger log = LoggerFactory.getLogger(HTTPUtil.class);
    public static String allowableResourcesRoot = "/WEB-INF/pages";
    private static final Pattern[] patterns = new
            Pattern[]{
            Pattern.compile("<script(.*?)>((.|\\s)*?)</script>", 2),
            Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", 42),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", 42),
            Pattern.compile("style[\r\n]*=[\r\n]*\\'(.*?)\\'", 42),
            Pattern.compile("style[\r\n]*=[\r\n]*\\\"(.*?)\\\"", 42),
            Pattern.compile("onmouseover[\r\n]*=[\r\n]*\\'(.*?)\\'", 42),
            Pattern.compile("onmouseover[\r\n]*=[\r\n]*\\\"(.*?)\\\"", 42),
            Pattern.compile("onmouseover(.*?)=", 42),
            Pattern.compile("</script>", 2),
            Pattern.compile("<script(.*?)>", 42),
            Pattern.compile("eval\\((.*?)\\)", 42),
            Pattern.compile("expression\\((.*?)\\)", 42),
            Pattern.compile("javascript:", 2),
            Pattern.compile("vbscript:", 2),
            Pattern.compile("onload(.*?)=", 42),
            Pattern.compile("onerror(.*?)=", 42),
            Pattern.compile("alert\\((.*?)\\)", 42),
            Pattern.compile("<iframe(.*?)>", 42),
            Pattern.compile("&apos;", 2)};

    public HTTPUtil() {
    }

//    public static <T> T getSessionAttribute(HttpSession var0, String var1) {
//        return var0.getAttribute(var1);
//    }
//
//    public static <T> T getRequestAttribute(HttpServletRequest var0, String var1) {
//        return var0.getAttribute(var1);
//    }

    private static Cookie getCookie(HttpServletRequest var0, String var1) {
        Cookie[] var2 = var0.getCookies();
        if (var2 != null) {
            for (Cookie var6 : var2) {
                if (var6.getName().equals(var1)) {
                    return var6;
                }
            }
        }
        return null;
    }

    public static void addCookie(HttpServletRequest var0, HttpServletResponse var1, String var2, String var3) {
        Cookie var4 = new Cookie(var2, var3);
        var4.setHttpOnly(true);
        addCookie(var0, var1, var4);
    }

    public static void addCookie(HttpServletRequest var0, HttpServletResponse var1, Cookie var2) {
        var2.setSecure(var0.isSecure());
        if (var2.getPath() == null) {
            var2.setPath(var0.getContextPath() + "/");
        }

        var1.addCookie(var2);
    }

    public static void killCookie(HttpServletRequest var0, HttpServletResponse var1, String var2) {
        String var3 = "//";
        String var4 = "";
        Cookie var5 = getCookie(var0, var2);
        if (var5 != null) {
            var3 = var5.getPath();
            var4 = var5.getDomain();
        }

        Cookie var6 = new Cookie(var2, "deleted");
        var6.setMaxAge(0);
        if (var4 != null) {
            var6.setDomain(var4);
        }

        if (var3 != null) {
            var6.setPath(var3);
        }

        addCookie(var0, var1, var6);
    }

    public static void killAllCookies(HttpServletRequest var0, HttpServletResponse var1) {
        Cookie[] var2 = var0.getCookies();
        if (var2 != null) {
            Cookie[] var3 = var2;
            int var4 = var2.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Cookie var6 = var3[var5];
                killCookie(var0, var1, var6.getName());
            }
        }
    }

    public static String cleanXSS(String var0) {
        if (StringUtil.hasValue(var0)) {
            String var1 = var0;
            var0 = Normalizer.normalize(var0, Normalizer.Form.NFD);
            var0 = var0.replaceAll("\u0000", "");
            Pattern[] var2 = patterns;
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                Pattern var5 = var2[var4];
                var0 = var5.matcher(var0).replaceAll("");
            }

            if (!var1.equals(var0)) {
                log.error("cleanXSS \n\r\tbefore = [" + var1 + "] \n\r\tafter. = [" + var0 + "]");
            }
        }

        return var0;
    }

    public static String getIpAddress(HttpServletRequest var0) {
        String var1 = null;

        try {
            var1 = var0.getHeader("X-Forwarded-For");
            if (var1 == null || var1.isEmpty() || "unknown".equalsIgnoreCase(var1)) {
                var1 = var0.getHeader("Proxy-Client-IP");
            }

            if (var1 == null || var1.isEmpty() || "unknown".equalsIgnoreCase(var1)) {
                var1 = var0.getHeader("WL-Proxy-Client-IP");
            }

            if (var1 == null || var1.isEmpty() || "unknown".equalsIgnoreCase(var1)) {
                var1 = var0.getRemoteAddr();
            }
        } catch (Exception ignored) {
        }

        return var1;
    }

    public static String getJsModuleEnvPath() {
        return PropertyUtil.getValue("ui.js.module.folder");
    }
}
