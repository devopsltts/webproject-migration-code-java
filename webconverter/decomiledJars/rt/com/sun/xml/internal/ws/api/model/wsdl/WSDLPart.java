package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.xml.internal.ws.api.model.ParameterBinding;

public abstract interface WSDLPart
  extends WSDLObject
{
  public abstract String getName();
  
  public abstract ParameterBinding getBinding();
  
  public abstract int getIndex();
  
  public abstract WSDLPartDescriptor getDescriptor();
}
