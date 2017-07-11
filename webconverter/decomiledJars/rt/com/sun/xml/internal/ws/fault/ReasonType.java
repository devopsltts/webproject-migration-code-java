package com.sun.xml.internal.ws.fault;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElements;

class ReasonType
{
  @XmlElements({@javax.xml.bind.annotation.XmlElement(name="Text", namespace="http://www.w3.org/2003/05/soap-envelope", type=TextType.class)})
  private final List<TextType> text = new ArrayList();
  
  ReasonType() {}
  
  ReasonType(String paramString)
  {
    this.text.add(new TextType(paramString));
  }
  
  List<TextType> texts()
  {
    return this.text;
  }
}
