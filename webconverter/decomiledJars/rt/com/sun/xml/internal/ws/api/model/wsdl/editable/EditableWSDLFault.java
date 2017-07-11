package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;

public abstract interface EditableWSDLFault
  extends WSDLFault
{
  public abstract EditableWSDLMessage getMessage();
  
  @NotNull
  public abstract EditableWSDLOperation getOperation();
  
  public abstract void setAction(String paramString);
  
  public abstract void setDefaultAction(boolean paramBoolean);
  
  public abstract void freeze(EditableWSDLModel paramEditableWSDLModel);
}
