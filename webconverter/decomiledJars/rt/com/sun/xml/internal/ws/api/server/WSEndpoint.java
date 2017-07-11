package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.config.management.EndpointCreationAttributes;
import com.sun.xml.internal.ws.api.config.management.ManagedEndpointFactory;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.server.EndpointAwareTube;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;

public abstract class WSEndpoint<T>
  implements ComponentRegistry
{
  public WSEndpoint() {}
  
  @NotNull
  public abstract Codec createCodec();
  
  @NotNull
  public abstract QName getServiceName();
  
  @NotNull
  public abstract QName getPortName();
  
  @NotNull
  public abstract Class<T> getImplementationClass();
  
  @NotNull
  public abstract WSBinding getBinding();
  
  @NotNull
  public abstract Container getContainer();
  
  @Nullable
  public abstract WSDLPort getPort();
  
  public abstract void setExecutor(@NotNull Executor paramExecutor);
  
  public final void schedule(@NotNull Packet paramPacket, @NotNull CompletionCallback paramCompletionCallback)
  {
    schedule(paramPacket, paramCompletionCallback, null);
  }
  
  public abstract void schedule(@NotNull Packet paramPacket, @NotNull CompletionCallback paramCompletionCallback, @Nullable FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor);
  
  public void process(@NotNull Packet paramPacket, @NotNull CompletionCallback paramCompletionCallback, @Nullable FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor)
  {
    schedule(paramPacket, paramCompletionCallback, paramFiberContextSwitchInterceptor);
  }
  
  public Engine getEngine()
  {
    throw new UnsupportedOperationException();
  }
  
  @NotNull
  public abstract PipeHead createPipeHead();
  
  public abstract void dispose();
  
  @Nullable
  public abstract ServiceDefinition getServiceDefinition();
  
  public List<BoundEndpoint> getBoundEndpoints()
  {
    Module localModule = (Module)getContainer().getSPI(Module.class);
    return localModule != null ? localModule.getBoundEndpoints() : null;
  }
  
  /**
   * @deprecated
   */
  @NotNull
  public abstract Set<EndpointComponent> getComponentRegistry();
  
  @NotNull
  public Set<Component> getComponents()
  {
    return Collections.emptySet();
  }
  
  @Nullable
  public <S> S getSPI(@NotNull Class<S> paramClass)
  {
    Set localSet = getComponents();
    if (localSet != null)
    {
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        Component localComponent = (Component)localIterator.next();
        Object localObject = localComponent.getSPI(paramClass);
        if (localObject != null) {
          return localObject;
        }
      }
    }
    return getContainer().getSPI(paramClass);
  }
  
  @Nullable
  public abstract SEIModel getSEIModel();
  
  /**
   * @deprecated
   */
  public abstract PolicyMap getPolicyMap();
  
  @NotNull
  public abstract ManagedObjectManager getManagedObjectManager();
  
  public abstract void closeManagedObjectManager();
  
  @NotNull
  public abstract ServerTubeAssemblerContext getAssemblerContext();
  
  public static <T> WSEndpoint<T> create(@NotNull Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, @Nullable EntityResolver paramEntityResolver, boolean paramBoolean2)
  {
    return create(paramClass, paramBoolean1, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, paramBoolean2, true);
  }
  
  public static <T> WSEndpoint<T> create(@NotNull Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, @Nullable EntityResolver paramEntityResolver, boolean paramBoolean2, boolean paramBoolean3)
  {
    WSEndpoint localWSEndpoint1 = EndpointFactory.createEndpoint(paramClass, paramBoolean1, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, paramBoolean2, paramBoolean3);
    Iterator localIterator = ServiceFinder.find(ManagedEndpointFactory.class).iterator();
    if (localIterator.hasNext())
    {
      ManagedEndpointFactory localManagedEndpointFactory = (ManagedEndpointFactory)localIterator.next();
      EndpointCreationAttributes localEndpointCreationAttributes = new EndpointCreationAttributes(paramBoolean1, paramInvoker, paramEntityResolver, paramBoolean2);
      WSEndpoint localWSEndpoint2 = localManagedEndpointFactory.createEndpoint(localWSEndpoint1, localEndpointCreationAttributes);
      if ((localWSEndpoint1.getAssemblerContext().getTerminalTube() instanceof EndpointAwareTube)) {
        ((EndpointAwareTube)localWSEndpoint1.getAssemblerContext().getTerminalTube()).setEndpoint(localWSEndpoint2);
      }
      return localWSEndpoint2;
    }
    return localWSEndpoint1;
  }
  
  @Deprecated
  public static <T> WSEndpoint<T> create(@NotNull Class<T> paramClass, boolean paramBoolean, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, @Nullable EntityResolver paramEntityResolver)
  {
    return create(paramClass, paramBoolean, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, false);
  }
  
  public static <T> WSEndpoint<T> create(@NotNull Class<T> paramClass, boolean paramBoolean, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, @Nullable URL paramURL)
  {
    return create(paramClass, paramBoolean, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, XmlUtil.createEntityResolver(paramURL), false);
  }
  
  @NotNull
  public static QName getDefaultServiceName(Class paramClass)
  {
    return getDefaultServiceName(paramClass, true, null);
  }
  
  @NotNull
  public static QName getDefaultServiceName(Class paramClass, MetadataReader paramMetadataReader)
  {
    return getDefaultServiceName(paramClass, true, paramMetadataReader);
  }
  
  @NotNull
  public static QName getDefaultServiceName(Class paramClass, boolean paramBoolean)
  {
    return getDefaultServiceName(paramClass, paramBoolean, null);
  }
  
  @NotNull
  public static QName getDefaultServiceName(Class paramClass, boolean paramBoolean, MetadataReader paramMetadataReader)
  {
    return EndpointFactory.getDefaultServiceName(paramClass, paramBoolean, paramMetadataReader);
  }
  
  @NotNull
  public static QName getDefaultPortName(@NotNull QName paramQName, Class paramClass)
  {
    return getDefaultPortName(paramQName, paramClass, null);
  }
  
  @NotNull
  public static QName getDefaultPortName(@NotNull QName paramQName, Class paramClass, MetadataReader paramMetadataReader)
  {
    return getDefaultPortName(paramQName, paramClass, true, paramMetadataReader);
  }
  
  @NotNull
  public static QName getDefaultPortName(@NotNull QName paramQName, Class paramClass, boolean paramBoolean)
  {
    return getDefaultPortName(paramQName, paramClass, paramBoolean, null);
  }
  
  @NotNull
  public static QName getDefaultPortName(@NotNull QName paramQName, Class paramClass, boolean paramBoolean, MetadataReader paramMetadataReader)
  {
    return EndpointFactory.getDefaultPortName(paramQName, paramClass, paramBoolean, paramMetadataReader);
  }
  
  public abstract <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, String paramString1, String paramString2, Element... paramVarArgs);
  
  public abstract <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, String paramString1, String paramString2, List<Element> paramList1, List<Element> paramList2);
  
  public boolean equalsProxiedInstance(WSEndpoint paramWSEndpoint)
  {
    if (paramWSEndpoint == null) {
      return false;
    }
    return equals(paramWSEndpoint);
  }
  
  @Nullable
  public abstract OperationDispatcher getOperationDispatcher();
  
  public abstract Packet createServiceResponseForException(ThrowableContainerPropertySet paramThrowableContainerPropertySet, Packet paramPacket, SOAPVersion paramSOAPVersion, WSDLPort paramWSDLPort, SEIModel paramSEIModel, WSBinding paramWSBinding);
  
  public static abstract interface CompletionCallback
  {
    public abstract void onCompletion(@NotNull Packet paramPacket);
  }
  
  public static abstract interface PipeHead
  {
    @NotNull
    public abstract Packet process(@NotNull Packet paramPacket, @Nullable WebServiceContextDelegate paramWebServiceContextDelegate, @Nullable TransportBackChannel paramTransportBackChannel);
  }
}
