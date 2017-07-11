package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.HandlerChain;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="handler-chain")
public class XmlHandlerChain
  implements HandlerChain
{
  @XmlAttribute(name="file")
  protected String file;
  
  public XmlHandlerChain() {}
  
  public String getFile()
  {
    return this.file;
  }
  
  public void setFile(String paramString)
  {
    this.file = paramString;
  }
  
  public String file()
  {
    return Util.nullSafe(this.file);
  }
  
  public String name()
  {
    return "";
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return HandlerChain.class;
  }
}
