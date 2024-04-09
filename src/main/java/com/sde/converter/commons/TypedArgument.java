package com.sde.converter.commons;

import lombok.Data;

import java.io.Serializable;

@Data
public class TypedArgument implements Serializable {

    private static final long serialVersionUID = 1L;
    private String argument;
    private String argumentType;

    public TypedArgument(String argument) {
        super();
        this.argument = argument;
    }

    public TypedArgument(String argument, String argumentType) {
        super();
        this.argument = argument;
        this.argumentType = argumentType;
    }
}
