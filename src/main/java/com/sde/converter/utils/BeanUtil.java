package com.sde.converter.utils;

import org.apache.commons.beanutils.BeanUtils;

public class BeanUtil {

    public static void copyProperties(Object dest, Object orig) throws RuntimeException {
        try {
            BeanUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }
}
