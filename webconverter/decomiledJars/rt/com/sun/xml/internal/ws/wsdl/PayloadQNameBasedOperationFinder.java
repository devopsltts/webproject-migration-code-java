package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.util.QNameMap;
import com.sun.xml.internal.ws.util.QNameMap.Entry;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

final class PayloadQNameBasedOperationFinder
  extends WSDLOperationFinder
{
  private static final Logger LOGGER = Logger.getLogger(PayloadQNameBasedOperationFinder.class.getName());
  public static final String EMPTY_PAYLOAD_LOCAL = "";
  public static final String EMPTY_PAYLOAD_NSURI = "";
  public static final QName EMPTY_PAYLOAD = new QName("", "");
  private final QNameMap<WSDLOperationMapping> methodHandlers = new QNameMap();
  private final QNameMap<List<String>> unique = new QNameMap();
  
  public PayloadQNameBasedOperationFinder(WSDLPort paramWSDLPort, WSBinding paramWSBinding, @Nullable SEIModel paramSEIModel)
  {
    super(paramWSDLPort, paramWSBinding, paramSEIModel);
    Iterator localIterator;
    Object localObject1;
    QName localQName;
    if (paramSEIModel != null)
    {
      localIterator = ((AbstractSEIModelImpl)paramSEIModel).getJavaMethods().iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (JavaMethodImpl)localIterator.next();
        if (!((JavaMethodImpl)localObject1).getMEP().isAsync)
        {
          localQName = ((JavaMethodImpl)localObject1).getRequestPayloadName();
          if (localQName == null) {
            localQName = EMPTY_PAYLOAD;
          }
          Object localObject2 = (List)this.unique.get(localQName);
          if (localObject2 == null)
          {
            localObject2 = new ArrayList();
            this.unique.put(localQName, localObject2);
          }
          ((List)localObject2).add(((JavaMethodImpl)localObject1).getMethod().getName());
        }
      }
      localIterator = this.unique.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (QNameMap.Entry)localIterator.next();
        if (((List)((QNameMap.Entry)localObject1).getValue()).size() > 1) {
          LOGGER.warning(ServerMessages.NON_UNIQUE_DISPATCH_QNAME(((QNameMap.Entry)localObject1).getValue(), ((QNameMap.Entry)localObject1).createQName()));
        }
      }
      localIterator = ((AbstractSEIModelImpl)paramSEIModel).getJavaMethods().iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (JavaMethodImpl)localIterator.next();
        localQName = ((JavaMethodImpl)localObject1).getRequestPayloadName();
        if (localQName == null) {
          localQName = EMPTY_PAYLOAD;
        }
        if (((List)this.unique.get(localQName)).size() == 1) {
          this.methodHandlers.put(localQName, wsdlOperationMapping((JavaMethodImpl)localObject1));
        }
      }
    }
    else
    {
      localIterator = paramWSDLPort.getBinding().getBindingOperations().iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (WSDLBoundOperation)localIterator.next();
        localQName = ((WSDLBoundOperation)localObject1).getRequestPayloadName();
        if (localQName == null) {
          localQName = EMPTY_PAYLOAD;
        }
        this.methodHandlers.put(localQName, wsdlOperationMapping((WSDLBoundOperation)localObject1));
      }
    }
  }
  
  public WSDLOperationMapping getWSDLOperationMapping(Packet paramPacket)
    throws DispatchException
  {
    Message localMessage = paramPacket.getMessage();
    String str1 = localMessage.getPayloadLocalPart();
    String str2;
    if (str1 == null)
    {
      str1 = "";
      str2 = "";
    }
    else
    {
      str2 = localMessage.getPayloadNamespaceURI();
      if (str2 == null) {
        str2 = "";
      }
    }
    WSDLOperationMapping localWSDLOperationMapping = (WSDLOperationMapping)this.methodHandlers.get(str2, str1);
    if ((localWSDLOperationMapping == null) && (!this.unique.containsKey(str2, str1)))
    {
      String str3 = "{" + str2 + "}" + str1;
      String str4 = ServerMessages.DISPATCH_CANNOT_FIND_METHOD(str3);
      throw new DispatchException(SOAPFaultBuilder.createSOAPFaultMessage(this.binding.getSOAPVersion(), str4, this.binding.getSOAPVersion().faultCodeClient));
    }
    return localWSDLOperationMapping;
  }
}
