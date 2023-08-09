package com.wyq.common.pojo;

import java.io.Serializable;

public class MyRpcRequestMessage implements Serializable {

    private Integer requestId;

    //方法名
    private String methodName;
    //参数类型
    private Class<?>[] parameterTypes;
    //参数值
    private Object[] parameters;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Integer getRequestId() {
        return requestId;
    }
}
