package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="soap-binding-parameter-style")
@XmlEnum
public enum SoapBindingParameterStyle
{
  BARE,  WRAPPED;
  
  private SoapBindingParameterStyle() {}
  
  public String value()
  {
    return name();
  }
  
  public static SoapBindingParameterStyle fromValue(String paramString)
  {
    return valueOf(paramString);
  }
}
