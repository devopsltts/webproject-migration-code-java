package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLDescriptorKind;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLPartDescriptorImpl
  extends AbstractObjectImpl
  implements WSDLPartDescriptor
{
  private QName name;
  private WSDLDescriptorKind type;
  
  public WSDLPartDescriptorImpl(XMLStreamReader paramXMLStreamReader, QName paramQName, WSDLDescriptorKind paramWSDLDescriptorKind)
  {
    super(paramXMLStreamReader);
    this.name = paramQName;
    this.type = paramWSDLDescriptorKind;
  }
  
  public QName name()
  {
    return this.name;
  }
  
  public WSDLDescriptorKind type()
  {
    return this.type;
  }
}
