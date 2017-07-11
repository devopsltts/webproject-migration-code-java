package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public abstract class ParserImplBase
{
  private ORBUtilSystemException wrapper = ORBUtilSystemException.get("orb.lifecycle");
  
  protected abstract PropertyParser makeParser();
  
  protected void complete() {}
  
  public ParserImplBase() {}
  
  public void init(DataCollector paramDataCollector)
  {
    PropertyParser localPropertyParser = makeParser();
    paramDataCollector.setParser(localPropertyParser);
    Properties localProperties = paramDataCollector.getProperties();
    Map localMap = localPropertyParser.parse(localProperties);
    setFields(localMap);
  }
  
  private Field getAnyField(String paramString)
  {
    Field localField = null;
    try
    {
      Class localClass = getClass();
      for (localField = localClass.getDeclaredField(paramString); localField == null; localField = localClass.getDeclaredField(paramString))
      {
        localClass = localClass.getSuperclass();
        if (localClass == null) {
          break;
        }
      }
    }
    catch (Exception localException)
    {
      throw this.wrapper.fieldNotFound(localException, paramString);
    }
    if (localField == null) {
      throw this.wrapper.fieldNotFound(paramString);
    }
    return localField;
  }
  
  protected void setFields(Map paramMap)
  {
    Set localSet = paramMap.entrySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      final String str = (String)localEntry.getKey();
      final Object localObject = localEntry.getValue();
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws IllegalAccessException, IllegalArgumentException
          {
            Field localField = ParserImplBase.this.getAnyField(str);
            localField.setAccessible(true);
            localField.set(ParserImplBase.this, localObject);
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw this.wrapper.errorSettingField(localPrivilegedActionException.getCause(), str, localObject.toString());
      }
    }
    complete();
  }
}
