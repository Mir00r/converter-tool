package com.sde.converter.configs;

import com.sde.converter.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger log = LoggerFactory.getLogger(EndpointsListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
//	    Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
//	        .getHandlerMethods();
//	    map.forEach((key, value) -> log.info("{} {}", key, value));
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> value;
        MethodParameter[] params;
        for (RequestMappingInfo info : requestMappingHandlerMapping.getHandlerMethods().keySet()) {
            value = new HashMap<>();
            value.put("url", info.getPatternsCondition().getPatterns().toString());
            value.put("method", info.getMethodsCondition().getMethods().toString());
            value.put("consume", info.getConsumesCondition().getConsumableMediaTypes().toString());
            value.put("produce", info.getProducesCondition().getProducibleMediaTypes().toString());
            value.put("param", info.getParamsCondition().getExpressions().toString());
            value.put("header", info.getHeadersCondition().getExpressions().toString());
            value.put("class", info.getMethodsCondition().getMethods().getClass().toString());
            params = requestMappingHandlerMapping.getHandlerMethods().get(info).getMethodParameters();
            value.put("request", (params != null && params.length > 0) ? params[0].getParameter().getType().getName() : "");
            value.put("response", requestMappingHandlerMapping.getHandlerMethods().get(info).getReturnType().getGenericParameterType().getTypeName());
            result.add(value);
        }
        try {
            log.debug("[show: endpoints=" + JSONUtil.encode(result));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
