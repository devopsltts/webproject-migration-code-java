package com.sun.xml.internal.ws.wsdl.writer.document.soap;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("header")
public abstract interface Header
  extends TypedXmlWriter, BodyType
{
  @XmlAttribute
  public abstract Header message(QName paramQName);
  
  @XmlElement
  public abstract HeaderFault headerFault();
  
  @XmlAttribute
  public abstract BodyType part(String paramString);
}
