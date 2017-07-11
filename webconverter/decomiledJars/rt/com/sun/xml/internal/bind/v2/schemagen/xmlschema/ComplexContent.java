package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("complexContent")
public abstract interface ComplexContent
  extends Annotated, TypedXmlWriter
{
  @XmlElement
  public abstract ComplexExtension extension();
  
  @XmlElement
  public abstract ComplexRestriction restriction();
  
  @XmlAttribute
  public abstract ComplexContent mixed(boolean paramBoolean);
}
