package com.wyq.common.pojo;

import java.io.Serializable;

public class MyRpcResponseMessage implements Serializable {
    //正常结果返回
    private Object result;
    //异常结果返回
    private Exception exceptionResult;

    private Integer responseId;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getExceptionResult() {
        return exceptionResult;
    }

    public void setExceptionResult(Exception exceptionResult) {
        this.exceptionResult = exceptionResult;
    }

    public Integer getResponseId() {
        return responseId;
    }

    public void setResponseId(Integer responseId) {
        this.responseId = responseId;
    }
}
