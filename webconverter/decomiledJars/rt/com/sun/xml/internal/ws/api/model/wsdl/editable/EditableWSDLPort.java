package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;

public abstract interface EditableWSDLPort
  extends WSDLPort
{
  @NotNull
  public abstract EditableWSDLBoundPortType getBinding();
  
  @NotNull
  public abstract EditableWSDLService getOwner();
  
  public abstract void setAddress(EndpointAddress paramEndpointAddress);
  
  public abstract void setEPR(@NotNull WSEndpointReference paramWSEndpointReference);
  
  public abstract void freeze(EditableWSDLModel paramEditableWSDLModel);
}
