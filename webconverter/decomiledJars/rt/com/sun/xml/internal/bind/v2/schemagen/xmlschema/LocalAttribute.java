package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("attribute")
public abstract interface LocalAttribute
  extends Annotated, AttributeType, FixedOrDefault, TypedXmlWriter
{
  @XmlAttribute
  public abstract LocalAttribute form(String paramString);
  
  @XmlAttribute
  public abstract LocalAttribute name(String paramString);
  
  @XmlAttribute
  public abstract LocalAttribute ref(QName paramQName);
  
  @XmlAttribute
  public abstract LocalAttribute use(String paramString);
}
