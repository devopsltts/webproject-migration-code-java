package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public abstract interface ComplexTypeModel
  extends AttrDecls, TypeDefParticle, TypedXmlWriter
{
  @XmlElement
  public abstract SimpleContent simpleContent();
  
  @XmlElement
  public abstract ComplexContent complexContent();
  
  @XmlAttribute
  public abstract ComplexTypeModel mixed(boolean paramBoolean);
}
