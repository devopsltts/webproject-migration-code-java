package com.sun.corba.se.impl.orb;

import java.util.Properties;

public class PropertyOnlyDataCollector
  extends DataCollectorBase
{
  public PropertyOnlyDataCollector(Properties paramProperties, String paramString1, String paramString2)
  {
    super(paramProperties, paramString1, paramString2);
  }
  
  public boolean isApplet()
  {
    return false;
  }
  
  protected void collect()
  {
    checkPropertyDefaults();
    findPropertiesFromProperties();
  }
}
