package com.sde.converter.commons;

import com.sde.converter.BaseProxy;
import lombok.Data;

@Data
public class OBSendAndReceive<T> {

    private String key;
    private OBBase request;
    private Class<T> responseType;
    private T response;
    private BaseProxy baseProxy;

    public OBSendAndReceive(String key, OBBase request, Class<T> responseType) {
        this.key = key;
        this.request = request;
        this.responseType = responseType;
    }

    public OBSendAndReceive(String key, OBBase request, Class<T> responseType, BaseProxy baseProxy) {
        this.key = key;
        this.request = request;
        this.responseType = responseType;
        this.baseProxy = baseProxy;
    }
}
