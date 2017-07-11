package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceRef;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-service-ref")
public class XmlWebServiceRef
  implements WebServiceRef
{
  @XmlAttribute(name="name")
  protected String name;
  @XmlAttribute(name="type")
  protected String type;
  @XmlAttribute(name="mappedName")
  protected String mappedName;
  @XmlAttribute(name="value")
  protected String value;
  @XmlAttribute(name="wsdlLocation")
  protected String wsdlLocation;
  @XmlAttribute(name="lookup")
  protected String lookup;
  
  public XmlWebServiceRef() {}
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public void setType(String paramString)
  {
    this.type = paramString;
  }
  
  public String getMappedName()
  {
    return this.mappedName;
  }
  
  public void setMappedName(String paramString)
  {
    this.mappedName = paramString;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public void setValue(String paramString)
  {
    this.value = paramString;
  }
  
  public String getWsdlLocation()
  {
    return this.wsdlLocation;
  }
  
  public void setWsdlLocation(String paramString)
  {
    this.wsdlLocation = paramString;
  }
  
  public String getLookup()
  {
    return this.lookup;
  }
  
  public void setLookup(String paramString)
  {
    this.lookup = paramString;
  }
  
  public String name()
  {
    return Util.nullSafe(this.name);
  }
  
  public Class<?> type()
  {
    if (this.type == null) {
      return Object.class;
    }
    return Util.findClass(this.type);
  }
  
  public String mappedName()
  {
    return Util.nullSafe(this.mappedName);
  }
  
  public Class<? extends Service> value()
  {
    if (this.value == null) {
      return Service.class;
    }
    return Util.findClass(this.value);
  }
  
  public String wsdlLocation()
  {
    return Util.nullSafe(this.wsdlLocation);
  }
  
  public String lookup()
  {
    return Util.nullSafe(this.lookup);
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return WebServiceRef.class;
  }
}
