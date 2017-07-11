package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;

public abstract interface WSDLPartDescriptor
  extends WSDLObject
{
  public abstract QName name();
  
  public abstract WSDLDescriptorKind type();
}
