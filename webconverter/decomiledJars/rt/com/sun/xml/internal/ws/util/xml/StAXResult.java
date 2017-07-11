package com.sun.xml.internal.ws.util.xml;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXResult;

public class StAXResult
  extends SAXResult
{
  public StAXResult(XMLStreamWriter paramXMLStreamWriter)
  {
    if (paramXMLStreamWriter == null) {
      throw new IllegalArgumentException();
    }
    super.setHandler(new ContentHandlerToXMLStreamWriter(paramXMLStreamWriter));
  }
}
