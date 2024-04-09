package com.sde.converter.commons;

import com.sde.converter.utils.HTTPUtil;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class SecurityContext {

    public SecurityContext() {
    }

    public static OBUserDetail getUserDetail() {
        return getPrincipal(OBUserDetail.class);
    }

    public static SecurityProperties.User getUser() {
        return getPrincipal(SecurityProperties.User.class);
    }

    public static <T> T getPrincipal(Class<T> var0) {
        return SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails ? (T) SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null;
    }

    public static String getIpAddress() {
        try {
            HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            return HTTPUtil.getIpAddress(servletRequest);
        } catch (Exception var1) {
            return null;
        }
    }

    public static String getDomainId() {
        String domain = null;

        try {
            HttpServletRequest var1 = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            domain = (String) var1.getSession().getAttribute("DOMAIN");
        } catch (Exception ignored) {
        }

        return domain;
    }
}
