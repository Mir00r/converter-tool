package com.sde.converter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

public class RuntimeServerUtil {

    private static Logger log = LoggerFactory.getLogger(RuntimeServerUtil.class);
    private static final String C = "annotations";
    public static final boolean webLogicPresent = ClassUtils.isPresent("weblogic.management.Helper", RuntimeServerUtil.class.getClassLoader());
    public static final boolean webspherePresent = ClassUtils.isPresent("com.ibm.websphere.management.AdminServiceFactory", RuntimeServerUtil.class.getClassLoader());
    public static final boolean tomcatPresent = ClassUtils.isPresent("org.apache.catalina.Server", RuntimeServerUtil.class.getClassLoader());
    public static final boolean jbossPresent = ClassUtils.isPresent("org.jboss.system.server.Server", RuntimeServerUtil.class.getClassLoader());
    public static final boolean jettyPresent = ClassUtils.isPresent("org.eclipse.jetty.server.Server", RuntimeServerUtil.class.getClassLoader());
    public static final boolean undertowPresent = ClassUtils.isPresent("io.undertow.Undertow", RuntimeServerUtil.class.getClassLoader());
    private static final String B = System.getProperty("java.specification.version");
    public static final boolean jdk1_5Present;
    public static final boolean jdk1_6Present;
    public static final boolean jdk1_7Present;
    public static final boolean jdk1_8Present;
    public static final boolean jdk9Present;
    public static final boolean jdk10Present;
    public static final boolean jdk11Present;
    public static final boolean jdk12Present;
    public static final boolean jdk13Present;
    public static final boolean jdk14Present;

    public RuntimeServerUtil() {
    }

    public static void display() {
        if (webspherePresent) {
            log.debug("RuntimeServerUtil.webspherePresent = [" + webspherePresent + "]");
        }

        if (tomcatPresent) {
            log.debug("RuntimeServerUtil.tomcatPresent =    [" + tomcatPresent + "]");
        }

        if (webLogicPresent) {
            log.debug("RuntimeServerUtil.weblogicPresent =  [" + webLogicPresent + "]");
        }

        if (jbossPresent) {
            log.debug("RuntimeServerUtil.jbossPresent =     [" + jbossPresent + "]");
        }

        if (jettyPresent) {
            log.debug("RuntimeServerUtil.jettyPresent =     [" + jettyPresent + "]");
        }

        if (undertowPresent) {
            log.debug("RuntimeServerUtil.undertowPresent =  [" + undertowPresent + "]");
        }

        if (jdk1_6Present) {
            log.debug("RuntimeServerUtil.jdk1_6Present =    [" + jdk1_6Present + "]");
        }

        if (jdk1_7Present) {
            log.debug("RuntimeServerUtil.jdk1_7Present =    [" + jdk1_7Present + "]");
        }

        if (jdk1_8Present) {
            log.debug("RuntimeServerUtil.jdk1_8Present =    [" + jdk1_8Present + "]");
        }

        if (jdk9Present) {
            log.debug("RuntimeServerUtil.jdk9Present =      [" + jdk9Present + "]");
        }

        if (jdk10Present) {
            log.debug("RuntimeServerUtil.jdk10Present =     [" + jdk10Present + "]");
        }

        if (jdk11Present) {
            log.debug("RuntimeServerUtil.jdk11Present =     [" + jdk11Present + "]");
        }

        if (jdk12Present) {
            log.debug("RuntimeServerUtil.jdk12Present =     [" + jdk12Present + "]");
        }

        if (jdk13Present) {
            log.debug("RuntimeServerUtil.jdk13Present =     [" + jdk13Present + "]");
        }

        if (jdk14Present) {
            log.debug("RuntimeServerUtil.jdk14Present =     [" + jdk14Present + "]");
        }

    }

    public static boolean isJDK7OrLower() {
        boolean var0 = true;

        try {
            Class.class.getDeclaredField("annotations");
        } catch (NoSuchFieldException var2) {
            var0 = false;
        }

        return var0;
    }

    static {
        jdk1_5Present = "1.5".equals(B);
        jdk1_6Present = "1.6".equals(B);
        jdk1_7Present = "1.7".equals(B);
        jdk1_8Present = "1.8".equals(B);
        jdk9Present = "9".equals(B);
        jdk10Present = "10".equals(B);
        jdk11Present = "11".equals(B);
        jdk12Present = "12".equals(B);
        jdk13Present = "13".equals(B);
        jdk14Present = "14".equals(B);
    }
}
