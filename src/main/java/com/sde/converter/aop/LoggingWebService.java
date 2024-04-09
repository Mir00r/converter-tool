package com.sde.converter.aop;

import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sde.converter.commons.OBBase;
import com.sde.converter.handler.MessageHandler;
import com.sde.converter.utils.JSONUtil;
import com.sde.converter.utils.PropertyUtil;
import com.sde.converter.utils.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;

@Component
@Order(3)
@Aspect
public class LoggingWebService {

    private static Logger log = LoggerFactory.getLogger(LoggingWebService.class);

    private Marker sysLogMarker = MarkerFactory.getMarker("SYSLOG_MARKER");
    private static final String LINE_SEPERATOR = "|";
    public static final String SYSTEM_LOGGING_KEYWORDS = "syslog.logging.keywords";

    //@Around("execution(* com.sde.modelsuite.webservice.CciiCustomer*.search*(..)) || execution(* com.sde.modelsuite.webservice.CciiCustomer*.retrieve*(..)) || execution(* com.sde.modelsuite.webservice.CciiCustomer*.retreive*(..)) || execution(* com.sde.modelsuite.webservice.CciiCustomer*.inquiry*(..)) || execution(* com.sde.modelsuite.webservice.CciiCustomer*.*Inquiry*(..))")
    @Around(value = "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object tryCatch(ProceedingJoinPoint pjp) throws Throwable {
        ObjectMapper om = JSONUtil.getInstance();
        om.setDefaultPrettyPrinter(new MinimalPrettyPrinter());
        om.configure(SerializationFeature.INDENT_OUTPUT, false);

        StringBuilder sysLogging = new StringBuilder();

        Object input = null;
        Object output = null;
        HttpServletRequest httpServletRequest = null;
        String eventType = "";
        boolean success = true;
        String statusCode = "";
        String statusMessage = "";
        String errorCodes = "";
        String username = "";

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        String className = methodSignature.getDeclaringTypeName();

        boolean isLog = filterLoggingKeyword(className, method);

        if (!isLog) {
            input = pjp.getArgs()[0];
            return pjp.proceed(); // continue processing
        }

        try {
            eventType = StringUtil.hasValue(eventType) ? eventType : method.getName();
            if (method.getAnnotation(LoggingEnabled.class) != null) {
                eventType = method.getAnnotation(LoggingEnabled.class).eventType();
            }
            httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            input = pjp.getArgs()[0];

            output = pjp.proceed(); // continue processing

            if (output instanceof OBBase) {
                OBBase response = (OBBase) output;
                response = MessageHandler.handleResponseStatus(response, ((OBBase) pjp.getArgs()[0]), response.getObHeader().getErrorDetails(), (MethodSignature) pjp.getSignature(), false);
                success = response.getObHeader().getSuccess();
                statusCode = response.getObHeader().getStatusCode();
                statusMessage = response.getObHeader().getStatusMessage() != null ? response.getObHeader().getStatusMessage() : "";
                errorCodes = response.getObHeader().getErrorDetails() != null ? om.writeValueAsString(response.getObHeader().getErrorDetails()) : "";
                username = response.getObHeader().getUsername() != null ? response.getObHeader().getUsername() : "";
            }
            return output;
        } catch (Exception e) {
            success = false;
            statusMessage = e.getMessage();
            log.error(e.getMessage(), e);

            throw e;
        } finally {
            sysLogging.append(eventType).append(LINE_SEPERATOR);

            sysLogging.append("Address: ").append(httpServletRequest.getRequestURL()).append(LINE_SEPERATOR);

            if (httpServletRequest.getCharacterEncoding() != null) {
                sysLogging.append("Encoding: ").append(httpServletRequest.getCharacterEncoding()).append(LINE_SEPERATOR);
            }

            sysLogging.append("Http-Method: ").append(httpServletRequest.getMethod()).append(LINE_SEPERATOR);

            if (httpServletRequest.getContentType() != null) {
                sysLogging.append("Content-Type: ").append(httpServletRequest.getContentType()).append(LINE_SEPERATOR);
            }

            sysLogging.append("Remote Address: ").append(httpServletRequest.getRemoteAddr()).append(LINE_SEPERATOR);
            sysLogging.append("Remote Host: ").append(httpServletRequest.getRemoteHost()).append(LINE_SEPERATOR);
            sysLogging.append("Remote Port: ").append(httpServletRequest.getRemotePort()).append(LINE_SEPERATOR);
            sysLogging.append("Path info: ").append(httpServletRequest.getPathInfo()).append(LINE_SEPERATOR);
            sysLogging.append("Local Address: ").append(httpServletRequest.getLocalAddr()).append(LINE_SEPERATOR);
            sysLogging.append("Local Name: ").append(httpServletRequest.getLocalName()).append(LINE_SEPERATOR);
            sysLogging.append("Local Port: ").append(httpServletRequest.getLocalPort()).append(LINE_SEPERATOR);
            sysLogging.append("Server Port: ").append(httpServletRequest.getServerPort()).append(LINE_SEPERATOR);
            sysLogging.append("Request URI: ").append(httpServletRequest.getRequestURI()).append(LINE_SEPERATOR);
            sysLogging.append("Request URL: ").append(httpServletRequest.getRequestURL()).append(LINE_SEPERATOR);
            sysLogging.append("Servlet Path: ").append(httpServletRequest.getServletPath()).append(LINE_SEPERATOR);

            sysLogging.append("Headers: ");

            if (httpServletRequest.getHeaderNames() != null) {
                HttpServletRequest request = httpServletRequest;
                Collections.list(httpServletRequest.getHeaderNames()).forEach(headerName -> Collections.list(request.getHeaders(headerName)).forEach(headerValue -> sysLogging.append(filterHeader(headerName, headerValue))));
            }

            sysLogging.append(LINE_SEPERATOR);

            if (input != null) {
                sysLogging.append(om.writeValueAsString(input));
            }
            sysLogging.append(LINE_SEPERATOR);
            sysLogging.append(success ? "true" : "false");
            sysLogging.append(LINE_SEPERATOR);
            sysLogging.append(statusCode);
            sysLogging.append(LINE_SEPERATOR);
            sysLogging.append(statusMessage);
            sysLogging.append(LINE_SEPERATOR);
            sysLogging.append(errorCodes);
            sysLogging.append(LINE_SEPERATOR);
            sysLogging.append("Username: ").append(username).append(LINE_SEPERATOR);
            sysLogging.append("Response: ").append(om.writeValueAsString(output)).append(LINE_SEPERATOR);

            log.info(sysLogMarker, sysLogging.toString());
        }
    }

    private static String filterHeader(String headerName, String headerValue) {
        String j = "";

        if ("authorization".equalsIgnoreCase(headerName)) {
            j = headerName + "=[******] ";
        } else {
            j = headerName + "=[" + headerValue + "] ";
        }

        return j;
    }

    private static boolean filterLoggingKeyword(String className, Method method) {
        String methodName = method.getName().toUpperCase();
        String filterStr = StringUtil.hasValue(PropertyUtil.getValue(SYSTEM_LOGGING_KEYWORDS)) ? PropertyUtil.getValue(SYSTEM_LOGGING_KEYWORDS) : "";

        if (StringUtil.hasValue(filterStr)) {
            for (String keyword : filterStr.split(",")) {
                if (methodName.contains(keyword.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}
