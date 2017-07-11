package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="existing-annotations-type")
@XmlEnum
public enum ExistingAnnotationsType
{
  MERGE("merge"),  IGNORE("ignore");
  
  private final String value;
  
  private ExistingAnnotationsType(String paramString)
  {
    this.value = paramString;
  }
  
  public String value()
  {
    return this.value;
  }
  
  public static ExistingAnnotationsType fromValue(String paramString)
  {
    for (ExistingAnnotationsType localExistingAnnotationsType : ) {
      if (localExistingAnnotationsType.value.equals(paramString)) {
        return localExistingAnnotationsType;
      }
    }
    throw new IllegalArgumentException(paramString);
  }
}
