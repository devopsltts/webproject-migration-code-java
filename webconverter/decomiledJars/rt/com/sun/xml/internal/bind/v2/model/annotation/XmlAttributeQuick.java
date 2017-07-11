package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;

final class XmlAttributeQuick
  extends Quick
  implements XmlAttribute
{
  private final XmlAttribute core;
  
  public XmlAttributeQuick(Locatable paramLocatable, XmlAttribute paramXmlAttribute)
  {
    super(paramLocatable);
    this.core = paramXmlAttribute;
  }
  
  protected Annotation getAnnotation()
  {
    return this.core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlAttributeQuick(paramLocatable, (XmlAttribute)paramAnnotation);
  }
  
  public Class<XmlAttribute> annotationType()
  {
    return XmlAttribute.class;
  }
  
  public String name()
  {
    return this.core.name();
  }
  
  public String namespace()
  {
    return this.core.namespace();
  }
  
  public boolean required()
  {
    return this.core.required();
  }
}
