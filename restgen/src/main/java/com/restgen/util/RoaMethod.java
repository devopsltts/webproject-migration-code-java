
package com.restgen.util;

import java.util.List;

public class RoaMethod {
    
    private String methodName;
    private Class returnType;
    private List<MethodParam> paramList;
    private String body;
    private Class httpMethodType;
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public Class getReturnType() {
        return returnType;
    }
    
    public void setReturnType(Class returnType) {
        this.returnType = returnType;
    }
    
    public List<MethodParam> getParamList() {
        return paramList;
    }
    
    public void setParamList(List<MethodParam> paramList) {
        this.paramList = paramList;
        for(MethodParam mParam : paramList){
            mParam.setParentMethod(this);
        }
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }

    
    public Class getHttpMethodType() {
        return httpMethodType;
    }

    
    public void setHttpMethodType(Class hhtpMethodType) {
        this.httpMethodType = hhtpMethodType;
    }  
    
}
