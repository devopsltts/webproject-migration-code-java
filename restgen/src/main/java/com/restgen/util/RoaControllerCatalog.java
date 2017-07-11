
package com.restgen.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoaControllerCatalog {

    private static Map<String,List<RoaMethod>> classMethodsMap = null;
    
    private static RoaControllerCatalog INSTANCE = null;
    
    private RoaControllerCatalog(){}
    
    public static RoaControllerCatalog getInstance(){
        if(INSTANCE == null){
            synchronized(INSTANCE) {
                if(INSTANCE == null){
                    INSTANCE = new RoaControllerCatalog();
                    load();
                }
            }
        }
        return INSTANCE;
    }
    
    private static void load(){
        if(classMethodsMap == null){
            classMethodsMap = new HashMap<String,List<RoaMethod>>();
        }
    }    
}
