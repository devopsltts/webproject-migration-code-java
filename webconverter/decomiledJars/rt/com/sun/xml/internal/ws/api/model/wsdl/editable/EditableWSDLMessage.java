package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;

public abstract interface EditableWSDLMessage
  extends WSDLMessage
{
  public abstract Iterable<? extends EditableWSDLPart> parts();
  
  public abstract void add(EditableWSDLPart paramEditableWSDLPart);
}
