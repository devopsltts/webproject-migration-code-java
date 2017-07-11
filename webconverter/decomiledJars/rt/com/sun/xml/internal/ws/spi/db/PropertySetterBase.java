package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;

public abstract class PropertySetterBase
  implements PropertySetter
{
  protected Class type;
  
  public PropertySetterBase() {}
  
  public Class getType()
  {
    return this.type;
  }
  
  public static boolean setterPattern(Method paramMethod)
  {
    return (paramMethod.getName().startsWith("set")) && (paramMethod.getName().length() > 3) && (paramMethod.getReturnType().equals(Void.TYPE)) && (paramMethod.getParameterTypes() != null) && (paramMethod.getParameterTypes().length == 1);
  }
}
