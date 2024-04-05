package com.sde.converter;

public class ThreadLocalConverterContextHolderStrategy {

    private static final ThreadLocal<ConverterContext> converterContext = new InheritableThreadLocal<>();

    public ThreadLocalConverterContextHolderStrategy() {
    }

    public static ConverterContext getSdeContext() {
        return (ConverterContext) converterContext.get();
    }

    public static void setSdeContext(ConverterContext var0) {
        converterContext.set(var0);
    }

    public static void unset() {
        converterContext.remove();
    }
}
