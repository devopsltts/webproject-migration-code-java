
package com.restgen.util;

/**
 * This class will have all the required meta data of Method.
 * <br/>
 * <p>
 * </p>
 * 
 * @author
 * @version 
 */
public class MethodParam {

    private Class paramType;
    private String paramName;
    private Class httpParamType;
    private RoaMethod parentMethod = null;
    
    public Class getParamType() {
        return paramType;
    }
    
    public void setParamType(Class paramType) {
        this.paramType = paramType;
    }
    
    public String getParamName() {
        return paramName;
    }
    
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    
    public Class getHttpParamType() {
        return httpParamType;
    }

    
    public void setHttpParamType(Class httpParamType) {
        this.httpParamType = httpParamType;
    }

    
    public RoaMethod getParentMethod() {
        return parentMethod;
    }

    
    public void setParentMethod(RoaMethod parentMethod) {
        this.parentMethod = parentMethod;
    }
    
}
