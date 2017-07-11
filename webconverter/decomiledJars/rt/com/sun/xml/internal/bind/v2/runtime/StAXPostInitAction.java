package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;

final class StAXPostInitAction
  implements Runnable
{
  private final XMLStreamWriter xsw;
  private final XMLEventWriter xew;
  private final NamespaceContext nsc;
  private final XMLSerializer serializer;
  
  StAXPostInitAction(XMLStreamWriter paramXMLStreamWriter, XMLSerializer paramXMLSerializer)
  {
    this.xsw = paramXMLStreamWriter;
    this.xew = null;
    this.nsc = null;
    this.serializer = paramXMLSerializer;
  }
  
  StAXPostInitAction(XMLEventWriter paramXMLEventWriter, XMLSerializer paramXMLSerializer)
  {
    this.xsw = null;
    this.xew = paramXMLEventWriter;
    this.nsc = null;
    this.serializer = paramXMLSerializer;
  }
  
  StAXPostInitAction(NamespaceContext paramNamespaceContext, XMLSerializer paramXMLSerializer)
  {
    this.xsw = null;
    this.xew = null;
    this.nsc = paramNamespaceContext;
    this.serializer = paramXMLSerializer;
  }
  
  public void run()
  {
    NamespaceContext localNamespaceContext = this.nsc;
    if (this.xsw != null) {
      localNamespaceContext = this.xsw.getNamespaceContext();
    }
    if (this.xew != null) {
      localNamespaceContext = this.xew.getNamespaceContext();
    }
    if (localNamespaceContext == null) {
      return;
    }
    for (String str1 : this.serializer.grammar.nameList.namespaceURIs)
    {
      String str2 = localNamespaceContext.getPrefix(str1);
      if (str2 != null) {
        this.serializer.addInscopeBinding(str1, str2);
      }
    }
  }
}
