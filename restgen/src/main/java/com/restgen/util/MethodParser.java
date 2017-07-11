
package com.restgen.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class MethodParser {
    
    private static Logger logger = Logger.getLogger(MethodParser.class);
    private static Map<String,Class> primitiveTypeMap = new HashMap<>();

    public static List<RoaMethod> parseMethod(List<String> methods) {
        
        if(methods == null || methods.isEmpty()){
            return Collections.emptyList();
        }
        
        loadPrimitiveTypeMap();
        
        List<RoaMethod> roaMethodList = new ArrayList<RoaMethod>();
        for(String method : methods) {
            if(null == method || "".equals(method.trim())) {
                return Collections.emptyList();
            }
            int parenthasis1 = method.indexOf("(");
            int parenthasis2 = method.indexOf(")", parenthasis1);
            String methodFirstPart = method.substring(0, parenthasis1);
            String methodSecondPart = method.substring(parenthasis1 + 1, parenthasis2);
            String methodBody = method.substring(method.indexOf("{") + 1, method.lastIndexOf("}")).trim();
            
            // Removing last ';', since it was added by tool automatically.
            if(methodBody.endsWith(";")){
                methodBody = methodBody.substring(0,methodBody.length() - 1);
            }
            String[] methodFirstDataSplit = methodFirstPart.split("\\s+");
            String[] methodParamSplit = methodSecondPart.split(",");
            String methodName = methodFirstDataSplit[methodFirstDataSplit.length - 1];
            String returnType = methodFirstDataSplit[methodFirstDataSplit.length - 2];

            List<MethodParam> paramList = new ArrayList<MethodParam>();
            for(String param : methodParamSplit) {
                if(param.trim().equals("")) {
                    continue;
                }
                String[] paramSplit = param.split("\\s+");
                String paramType = paramSplit[0];
                String paramName = paramSplit[1];
                MethodParam mParam = new MethodParam();
                mParam.setParamName(paramName);
                mParam.setParamType(getAbsoluteType(paramType));
                paramList.add(mParam);
            }

            RoaMethod roaMethod = new RoaMethod();
            roaMethod.setBody(methodBody);
            roaMethod.setMethodName(methodName);
            roaMethod.setParamList(paramList);
            roaMethod.setReturnType(getAbsoluteType(returnType));
            roaMethodList.add(roaMethod);
        }
        return roaMethodList;
    }

    private static void loadPrimitiveTypeMap() {
        primitiveTypeMap.put("int", Integer.class);
        primitiveTypeMap.put("boolean", Boolean.class);
        primitiveTypeMap.put("byte", Byte.class);
        primitiveTypeMap.put("char", String.class);
        primitiveTypeMap.put("short", Short.class);
        primitiveTypeMap.put("long",Long.class);
        primitiveTypeMap.put("float", Float.class);
        primitiveTypeMap.put("double", Double.class);
    }

    private static Class getAbsoluteType(String type) {
        
        type = type.split("<")[0];
        Class clazz = String.class;
        if(isPrimitiveType(type)) {
           return primitiveTypeMap.get(type);
        } else if(isJavaLangType(type)) {
            try {
                return Class.forName("java.lang." + type);
            } catch(ClassNotFoundException e) {
            }
        } else if(isJavaUtilType(type)) {
            try {
                return Class.forName("java.util." + type);
            } catch(ClassNotFoundException e) {
            }
        } else if(isSqlType(type)) {
            try {
                return Class.forName("java.sql." + type);
            } catch(ClassNotFoundException e) {
            }
        } else {
            try {
                return Class.forName("com.restgen.model." + type);
            } catch(ClassNotFoundException e) {
                logger.error("ERROR:  model class '" + type + ".java' is missing in com.restgen.model package",e);
            }
        }
        return clazz;
    }

    private static boolean isPrimitiveType(String type) {
        return primitiveTypeMap.containsKey(type);
    }

    private static boolean isJavaUtilType(String type) {
        boolean flag = true;
        try {
            Class.forName("java.util." + type);
        } catch(ClassNotFoundException e) {
            flag = false;
        }
        return flag;
    }

    private static boolean isSqlType(String type) {
        boolean flag = true;
        try {
            Class.forName("java.sql." + type);
        } catch(ClassNotFoundException e) {
            flag = false;
        }
        return flag;
    }

    private static boolean isJavaLangType(String type) {
        boolean flag = true;
        try {
            Class.forName("java.lang." + type);
        } catch(ClassNotFoundException e) {
            flag = false;
        }
        return flag;
    }

}
