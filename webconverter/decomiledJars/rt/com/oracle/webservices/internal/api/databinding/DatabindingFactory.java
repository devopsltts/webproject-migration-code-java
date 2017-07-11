package com.oracle.webservices.internal.api.databinding;

import java.util.Map;

public abstract class DatabindingFactory
{
  static final String ImplClass = "com.sun.xml.internal.ws.db.DatabindingFactoryImpl";
  
  public DatabindingFactory() {}
  
  public abstract Databinding.Builder createBuilder(Class<?> paramClass1, Class<?> paramClass2);
  
  public abstract Map<String, Object> properties();
  
  public static DatabindingFactory newInstance()
  {
    try
    {
      Class localClass = Class.forName("com.sun.xml.internal.ws.db.DatabindingFactoryImpl");
      return convertIfNecessary(localClass);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  private static DatabindingFactory convertIfNecessary(Class<?> paramClass)
    throws InstantiationException, IllegalAccessException
  {
    return (DatabindingFactory)paramClass.newInstance();
  }
}
