package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="soap-binding")
public class XmlSOAPBinding
  implements SOAPBinding
{
  @XmlAttribute(name="style")
  protected SoapBindingStyle style;
  @XmlAttribute(name="use")
  protected SoapBindingUse use;
  @XmlAttribute(name="parameter-style")
  protected SoapBindingParameterStyle parameterStyle;
  
  public XmlSOAPBinding() {}
  
  public SoapBindingStyle getStyle()
  {
    if (this.style == null) {
      return SoapBindingStyle.DOCUMENT;
    }
    return this.style;
  }
  
  public void setStyle(SoapBindingStyle paramSoapBindingStyle)
  {
    this.style = paramSoapBindingStyle;
  }
  
  public SoapBindingUse getUse()
  {
    if (this.use == null) {
      return SoapBindingUse.LITERAL;
    }
    return this.use;
  }
  
  public void setUse(SoapBindingUse paramSoapBindingUse)
  {
    this.use = paramSoapBindingUse;
  }
  
  public SoapBindingParameterStyle getParameterStyle()
  {
    if (this.parameterStyle == null) {
      return SoapBindingParameterStyle.WRAPPED;
    }
    return this.parameterStyle;
  }
  
  public void setParameterStyle(SoapBindingParameterStyle paramSoapBindingParameterStyle)
  {
    this.parameterStyle = paramSoapBindingParameterStyle;
  }
  
  public SOAPBinding.Style style()
  {
    return (SOAPBinding.Style)Util.nullSafe(this.style, SOAPBinding.Style.DOCUMENT);
  }
  
  public SOAPBinding.Use use()
  {
    return (SOAPBinding.Use)Util.nullSafe(this.use, SOAPBinding.Use.LITERAL);
  }
  
  public SOAPBinding.ParameterStyle parameterStyle()
  {
    return (SOAPBinding.ParameterStyle)Util.nullSafe(this.parameterStyle, SOAPBinding.ParameterStyle.WRAPPED);
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return SOAPBinding.class;
  }
}
