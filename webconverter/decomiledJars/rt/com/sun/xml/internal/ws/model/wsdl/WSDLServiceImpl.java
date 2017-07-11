package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLServiceImpl
  extends AbstractExtensibleImpl
  implements EditableWSDLService
{
  private final QName name;
  private final Map<QName, EditableWSDLPort> ports;
  private final EditableWSDLModel parent;
  
  public WSDLServiceImpl(XMLStreamReader paramXMLStreamReader, EditableWSDLModel paramEditableWSDLModel, QName paramQName)
  {
    super(paramXMLStreamReader);
    this.parent = paramEditableWSDLModel;
    this.name = paramQName;
    this.ports = new LinkedHashMap();
  }
  
  @NotNull
  public EditableWSDLModel getParent()
  {
    return this.parent;
  }
  
  public QName getName()
  {
    return this.name;
  }
  
  public EditableWSDLPort get(QName paramQName)
  {
    return (EditableWSDLPort)this.ports.get(paramQName);
  }
  
  public EditableWSDLPort getFirstPort()
  {
    if (this.ports.isEmpty()) {
      return null;
    }
    return (EditableWSDLPort)this.ports.values().iterator().next();
  }
  
  public Iterable<EditableWSDLPort> getPorts()
  {
    return this.ports.values();
  }
  
  @Nullable
  public EditableWSDLPort getMatchingPort(QName paramQName)
  {
    Iterator localIterator = getPorts().iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLPort localEditableWSDLPort = (EditableWSDLPort)localIterator.next();
      QName localQName = localEditableWSDLPort.getBinding().getPortTypeName();
      assert (localQName != null);
      if (localQName.equals(paramQName)) {
        return localEditableWSDLPort;
      }
    }
    return null;
  }
  
  public void put(QName paramQName, EditableWSDLPort paramEditableWSDLPort)
  {
    if ((paramQName == null) || (paramEditableWSDLPort == null)) {
      throw new NullPointerException();
    }
    this.ports.put(paramQName, paramEditableWSDLPort);
  }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel)
  {
    Iterator localIterator = this.ports.values().iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLPort localEditableWSDLPort = (EditableWSDLPort)localIterator.next();
      localEditableWSDLPort.freeze(paramEditableWSDLModel);
    }
  }
}
