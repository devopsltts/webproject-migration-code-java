package com.sun.xml.internal.org.jvnet.staxex;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract interface XMLStreamReaderEx
  extends XMLStreamReader
{
  public abstract CharSequence getPCDATA()
    throws XMLStreamException;
  
  public abstract NamespaceContextEx getNamespaceContext();
  
  public abstract String getElementTextTrim()
    throws XMLStreamException;
}
