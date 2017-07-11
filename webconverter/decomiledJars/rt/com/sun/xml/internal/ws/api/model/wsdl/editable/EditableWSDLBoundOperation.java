package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation.ANONYMOUS;
import java.util.Map;
import javax.jws.WebParam.Mode;
import javax.jws.soap.SOAPBinding.Style;

public abstract interface EditableWSDLBoundOperation
  extends WSDLBoundOperation
{
  @NotNull
  public abstract EditableWSDLOperation getOperation();
  
  @NotNull
  public abstract EditableWSDLBoundPortType getBoundPortType();
  
  @Nullable
  public abstract EditableWSDLPart getPart(@NotNull String paramString, @NotNull WebParam.Mode paramMode);
  
  @NotNull
  public abstract Map<String, ? extends EditableWSDLPart> getInParts();
  
  @NotNull
  public abstract Map<String, ? extends EditableWSDLPart> getOutParts();
  
  @NotNull
  public abstract Iterable<? extends EditableWSDLBoundFault> getFaults();
  
  public abstract void addPart(EditableWSDLPart paramEditableWSDLPart, WebParam.Mode paramMode);
  
  public abstract void addFault(@NotNull EditableWSDLBoundFault paramEditableWSDLBoundFault);
  
  public abstract void setAnonymous(WSDLBoundOperation.ANONYMOUS paramANONYMOUS);
  
  public abstract void setInputExplicitBodyParts(boolean paramBoolean);
  
  public abstract void setOutputExplicitBodyParts(boolean paramBoolean);
  
  public abstract void setFaultExplicitBodyParts(boolean paramBoolean);
  
  public abstract void setRequestNamespace(String paramString);
  
  public abstract void setResponseNamespace(String paramString);
  
  public abstract void setSoapAction(String paramString);
  
  public abstract void setStyle(SOAPBinding.Style paramStyle);
  
  public abstract void freeze(EditableWSDLModel paramEditableWSDLModel);
}
