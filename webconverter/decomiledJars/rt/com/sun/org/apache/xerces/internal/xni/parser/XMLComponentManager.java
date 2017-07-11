package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;

public abstract interface XMLComponentManager
{
  public abstract boolean getFeature(String paramString)
    throws XMLConfigurationException;
  
  public abstract boolean getFeature(String paramString, boolean paramBoolean);
  
  public abstract Object getProperty(String paramString)
    throws XMLConfigurationException;
  
  public abstract Object getProperty(String paramString, Object paramObject);
  
  public abstract FeatureState getFeatureState(String paramString);
  
  public abstract PropertyState getPropertyState(String paramString);
}
