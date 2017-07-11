package com.sun.xml.internal.ws.client.sei;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Fiber.CompletionCallback;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

public final class SEIStub
  extends Stub
  implements InvocationHandler
{
  Databinding databinding;
  public final SOAPSEIModel seiModel;
  public final SOAPVersion soapVersion;
  private final Map<Method, MethodHandler> methodHandlers = new HashMap();
  
  @Deprecated
  public SEIStub(WSServiceDelegate paramWSServiceDelegate, BindingImpl paramBindingImpl, SOAPSEIModel paramSOAPSEIModel, Tube paramTube, WSEndpointReference paramWSEndpointReference)
  {
    super(paramWSServiceDelegate, paramTube, paramBindingImpl, paramSOAPSEIModel.getPort(), paramSOAPSEIModel.getPort().getAddress(), paramWSEndpointReference);
    this.seiModel = paramSOAPSEIModel;
    this.soapVersion = paramBindingImpl.getSOAPVersion();
    this.databinding = paramSOAPSEIModel.getDatabinding();
    initMethodHandlers();
  }
  
  public SEIStub(WSPortInfo paramWSPortInfo, BindingImpl paramBindingImpl, SOAPSEIModel paramSOAPSEIModel, WSEndpointReference paramWSEndpointReference)
  {
    super(paramWSPortInfo, paramBindingImpl, paramSOAPSEIModel.getPort().getAddress(), paramWSEndpointReference);
    this.seiModel = paramSOAPSEIModel;
    this.soapVersion = paramBindingImpl.getSOAPVersion();
    this.databinding = paramSOAPSEIModel.getDatabinding();
    initMethodHandlers();
  }
  
  private void initMethodHandlers()
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = this.seiModel.getJavaMethods().iterator();
    JavaMethodImpl localJavaMethodImpl;
    Object localObject1;
    while (localIterator.hasNext())
    {
      localJavaMethodImpl = (JavaMethodImpl)localIterator.next();
      if (!localJavaMethodImpl.getMEP().isAsync)
      {
        localObject1 = new SyncMethodHandler(this, localJavaMethodImpl);
        localHashMap.put(localJavaMethodImpl.getOperation(), localJavaMethodImpl);
        this.methodHandlers.put(localJavaMethodImpl.getMethod(), localObject1);
      }
    }
    localIterator = this.seiModel.getJavaMethods().iterator();
    while (localIterator.hasNext())
    {
      localJavaMethodImpl = (JavaMethodImpl)localIterator.next();
      localObject1 = (JavaMethodImpl)localHashMap.get(localJavaMethodImpl.getOperation());
      Method localMethod;
      Object localObject2;
      if (localJavaMethodImpl.getMEP() == MEP.ASYNC_CALLBACK)
      {
        localMethod = localJavaMethodImpl.getMethod();
        localObject2 = new CallbackMethodHandler(this, localMethod, localMethod.getParameterTypes().length - 1);
        this.methodHandlers.put(localMethod, localObject2);
      }
      if (localJavaMethodImpl.getMEP() == MEP.ASYNC_POLL)
      {
        localMethod = localJavaMethodImpl.getMethod();
        localObject2 = new PollingMethodHandler(this, localMethod);
        this.methodHandlers.put(localMethod, localObject2);
      }
    }
  }
  
  @Nullable
  public OperationDispatcher getOperationDispatcher()
  {
    if ((this.operationDispatcher == null) && (this.wsdlPort != null)) {
      this.operationDispatcher = new OperationDispatcher(this.wsdlPort, this.binding, this.seiModel);
    }
    return this.operationDispatcher;
  }
  
  /* Error */
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws java.lang.Throwable
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: aload_2
    //   3: invokespecial 277	com/sun/xml/internal/ws/client/sei/SEIStub:validateInputs	(Ljava/lang/Object;Ljava/lang/reflect/Method;)V
    //   6: invokestatic 263	com/sun/xml/internal/ws/api/server/ContainerResolver:getDefault	()Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver;
    //   9: aload_0
    //   10: getfield 258	com/sun/xml/internal/ws/client/sei/SEIStub:owner	Lcom/sun/xml/internal/ws/client/WSServiceDelegate;
    //   13: invokevirtual 272	com/sun/xml/internal/ws/client/WSServiceDelegate:getContainer	()Lcom/sun/xml/internal/ws/api/server/Container;
    //   16: invokevirtual 265	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver:enterContainer	(Lcom/sun/xml/internal/ws/api/server/Container;)Lcom/sun/xml/internal/ws/api/server/Container;
    //   19: astore 4
    //   21: aload_0
    //   22: getfield 261	com/sun/xml/internal/ws/client/sei/SEIStub:methodHandlers	Ljava/util/Map;
    //   25: aload_2
    //   26: invokeinterface 304 2 0
    //   31: checkcast 125	com/sun/xml/internal/ws/client/sei/MethodHandler
    //   34: astore 5
    //   36: aload 5
    //   38: ifnull +23 -> 61
    //   41: aload 5
    //   43: aload_1
    //   44: aload_3
    //   45: invokevirtual 274	com/sun/xml/internal/ws/client/sei/MethodHandler:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   48: astore 6
    //   50: invokestatic 263	com/sun/xml/internal/ws/api/server/ContainerResolver:getDefault	()Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver;
    //   53: aload 4
    //   55: invokevirtual 264	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver:exitContainer	(Lcom/sun/xml/internal/ws/api/server/Container;)V
    //   58: aload 6
    //   60: areturn
    //   61: aload_2
    //   62: aload_0
    //   63: aload_3
    //   64: invokevirtual 295	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   67: astore 6
    //   69: invokestatic 263	com/sun/xml/internal/ws/api/server/ContainerResolver:getDefault	()Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver;
    //   72: aload 4
    //   74: invokevirtual 264	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver:exitContainer	(Lcom/sun/xml/internal/ws/api/server/Container;)V
    //   77: aload 6
    //   79: areturn
    //   80: astore 6
    //   82: new 132	java/lang/AssertionError
    //   85: dup
    //   86: aload 6
    //   88: invokespecial 287	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
    //   91: athrow
    //   92: astore 6
    //   94: new 132	java/lang/AssertionError
    //   97: dup
    //   98: aload 6
    //   100: invokespecial 287	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
    //   103: athrow
    //   104: astore 6
    //   106: aload 6
    //   108: invokevirtual 291	java/lang/reflect/InvocationTargetException:getCause	()Ljava/lang/Throwable;
    //   111: athrow
    //   112: astore 7
    //   114: invokestatic 263	com/sun/xml/internal/ws/api/server/ContainerResolver:getDefault	()Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver;
    //   117: aload 4
    //   119: invokevirtual 264	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver:exitContainer	(Lcom/sun/xml/internal/ws/api/server/Container;)V
    //   122: aload 7
    //   124: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	125	0	this	SEIStub
    //   0	125	1	paramObject	Object
    //   0	125	2	paramMethod	Method
    //   0	125	3	paramArrayOfObject	Object[]
    //   19	99	4	localContainer	com.sun.xml.internal.ws.api.server.Container
    //   34	8	5	localMethodHandler	MethodHandler
    //   48	30	6	localObject1	Object
    //   80	7	6	localIllegalAccessException	IllegalAccessException
    //   92	7	6	localIllegalArgumentException	IllegalArgumentException
    //   104	3	6	localInvocationTargetException	java.lang.reflect.InvocationTargetException
    //   112	11	7	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   61	69	80	java/lang/IllegalAccessException
    //   61	69	92	java/lang/IllegalArgumentException
    //   61	69	104	java/lang/reflect/InvocationTargetException
    //   21	50	112	finally
    //   61	69	112	finally
    //   80	114	112	finally
  }
  
  private void validateInputs(Object paramObject, Method paramMethod)
  {
    if ((paramObject == null) || (!Proxy.isProxyClass(paramObject.getClass()))) {
      throw new IllegalStateException("Passed object is not proxy!");
    }
    Class localClass = paramMethod.getDeclaringClass();
    if ((paramMethod == null) || (localClass == null) || (Modifier.isStatic(paramMethod.getModifiers()))) {
      throw new IllegalStateException("Invoking static method is not allowed!");
    }
  }
  
  public final Packet doProcess(Packet paramPacket, RequestContext paramRequestContext, ResponseContextReceiver paramResponseContextReceiver)
  {
    return super.process(paramPacket, paramRequestContext, paramResponseContextReceiver);
  }
  
  public final void doProcessAsync(AsyncResponseImpl<?> paramAsyncResponseImpl, Packet paramPacket, RequestContext paramRequestContext, Fiber.CompletionCallback paramCompletionCallback)
  {
    super.processAsync(paramAsyncResponseImpl, paramPacket, paramRequestContext, paramCompletionCallback);
  }
  
  @NotNull
  protected final QName getPortName()
  {
    return this.wsdlPort.getName();
  }
  
  public void setOutboundHeaders(Object... paramVarArgs)
  {
    if (paramVarArgs == null) {
      throw new IllegalArgumentException();
    }
    Header[] arrayOfHeader = new Header[paramVarArgs.length];
    for (int i = 0; i < arrayOfHeader.length; i++)
    {
      if (paramVarArgs[i] == null) {
        throw new IllegalArgumentException();
      }
      arrayOfHeader[i] = Headers.create(this.seiModel.getBindingContext(), paramVarArgs[i]);
    }
    super.setOutboundHeaders(arrayOfHeader);
  }
}
