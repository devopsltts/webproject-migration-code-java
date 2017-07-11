package com.oracle.webservices.internal.api.message;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

public abstract interface PropertySet
{
  public abstract boolean containsKey(Object paramObject);
  
  public abstract Object get(Object paramObject);
  
  public abstract Object put(String paramString, Object paramObject);
  
  public abstract boolean supports(Object paramObject);
  
  public abstract Object remove(Object paramObject);
  
  @Deprecated
  public abstract Map<String, Object> createMapView();
  
  public abstract Map<String, Object> asMap();
  
  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target({java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD})
  public static @interface Property
  {
    String[] value();
  }
}
