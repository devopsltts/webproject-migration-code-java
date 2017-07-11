package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;

public abstract interface WSDLMessage
  extends WSDLObject, WSDLExtensible
{
  public abstract QName getName();
  
  public abstract Iterable<? extends WSDLPart> parts();
}
