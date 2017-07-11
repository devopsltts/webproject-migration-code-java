package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="web-param-mode")
@XmlEnum
public enum WebParamMode
{
  IN,  OUT,  INOUT;
  
  private WebParamMode() {}
  
  public String value()
  {
    return name();
  }
  
  public static WebParamMode fromValue(String paramString)
  {
    return valueOf(paramString);
  }
}
