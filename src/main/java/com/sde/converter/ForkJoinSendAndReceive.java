package com.sde.converter;

import com.sde.converter.commons.OBBase;
import com.sde.converter.commons.OBSendAndReceive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class ForkJoinSendAndReceive extends RecursiveTask<List<OBSendAndReceive>> {

    private static final long serialVersionUID = 1L;
    public static final int THRESHOLD = 1;
    private String key;
    private List<OBSendAndReceive> obSendAndReceives;
    private int start;
    private int end;
    private BaseProxy baseProxy;
    private ConverterContext converterContext;

    public ForkJoinSendAndReceive(String key, List<OBSendAndReceive> obSendAndReceives, int start, int end, BaseProxy baseProxy, ConverterContext converterContext) {
        this.key = key;
        this.obSendAndReceives = obSendAndReceives;
        this.start = start;
        this.end = end;
        this.baseProxy = baseProxy;
        this.converterContext = converterContext;
    }

    public ForkJoinSendAndReceive(String key, List<OBSendAndReceive> obSendAndReceiveList, BaseProxy baseProxy, ConverterContext context) {
        this(key, obSendAndReceiveList, 0, obSendAndReceiveList.size(), baseProxy, context);
    }

    @Override
    protected List<OBSendAndReceive> compute() {
        ArrayList taskList1 = new ArrayList();
        ArrayList taskList2 = new ArrayList();
        int difference = this.end - this.start;
        int taskIndex;
        if (difference <= 1) {
            for (taskIndex = this.start; taskIndex < this.end; ++taskIndex) {
                OBSendAndReceive obSendAndReceive = this.obSendAndReceives.get(taskIndex);
                BaseProxy insideBaseProxy;
                if (obSendAndReceive.getBaseProxy() != null) {
                    insideBaseProxy = obSendAndReceive.getBaseProxy();
                } else {
                    insideBaseProxy = this.baseProxy;
                }

                Thread.currentThread().setName(this.key + " " + Thread.currentThread().getName());
                ThreadLocalConverterContextHolderStrategy.setSdeContext(this.converterContext);
                OBBase obBase = insideBaseProxy.sendAndReceive(obSendAndReceive.getKey(), obSendAndReceive.getRequest(), obSendAndReceive.getResponseType());
                obSendAndReceive.setResponse(obBase);
                taskList1.add(obSendAndReceive);
            }

            return taskList1;
        } else {
            taskIndex = difference / 2;
            ForkJoinSendAndReceive var5 = new ForkJoinSendAndReceive(this.key, this.obSendAndReceives, this.start + taskIndex, this.end, this.baseProxy, converterContext);
            ForkJoinSendAndReceive var6 = new ForkJoinSendAndReceive(this.key, this.obSendAndReceives, this.start, this.end + taskIndex, this.baseProxy, this.converterContext);
            taskList2.add(var5);
            var5.fork();
            taskList2.add(var6);
            var6.fork();

            for (Object o : taskList2) {
                ForkJoinSendAndReceive var8 = (ForkJoinSendAndReceive) o;
                taskList1.addAll(var8.join());
            }
            return taskList1;
        }
    }
}
