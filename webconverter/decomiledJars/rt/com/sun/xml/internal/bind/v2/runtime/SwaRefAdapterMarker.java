package com.sun.xml.internal.bind.v2.runtime;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class SwaRefAdapterMarker
  extends XmlAdapter<String, DataHandler>
{
  public SwaRefAdapterMarker() {}
  
  public DataHandler unmarshal(String paramString)
    throws Exception
  {
    throw new IllegalStateException("Not implemented");
  }
  
  public String marshal(DataHandler paramDataHandler)
    throws Exception
  {
    throw new IllegalStateException("Not implemented");
  }
}
