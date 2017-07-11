package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.server.AsyncProviderCallback;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.server.AbstractWebServiceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncProviderInvokerTube<T>
  extends ProviderInvokerTube<T>
{
  private static final Logger LOGGER = Logger.getLogger("com.sun.xml.internal.ws.server.AsyncProviderInvokerTube");
  
  public AsyncProviderInvokerTube(Invoker paramInvoker, ProviderArgumentsBuilder<T> paramProviderArgumentsBuilder)
  {
    super(paramInvoker, paramProviderArgumentsBuilder);
  }
  
  @NotNull
  public NextAction processRequest(@NotNull Packet paramPacket)
  {
    Object localObject1 = this.argsBuilder.getParameter(paramPacket);
    NoSuspendResumer localNoSuspendResumer = new NoSuspendResumer(null);
    AsyncProviderCallbackImpl localAsyncProviderCallbackImpl = new AsyncProviderCallbackImpl(paramPacket, localNoSuspendResumer);
    AsyncWebServiceContext localAsyncWebServiceContext = new AsyncWebServiceContext(getEndpoint(), paramPacket);
    LOGGER.fine("Invoking AsyncProvider Endpoint");
    try
    {
      getInvoker(paramPacket).invokeAsyncProvider(paramPacket, localObject1, localAsyncProviderCallbackImpl, localAsyncWebServiceContext);
    }
    catch (Throwable localThrowable1)
    {
      LOGGER.log(Level.SEVERE, localThrowable1.getMessage(), localThrowable1);
      return doThrow(localThrowable1);
    }
    synchronized (localAsyncProviderCallbackImpl)
    {
      if (localNoSuspendResumer.response != null)
      {
        ThrowableContainerPropertySet localThrowableContainerPropertySet = (ThrowableContainerPropertySet)localNoSuspendResumer.response.getSatellite(ThrowableContainerPropertySet.class);
        Throwable localThrowable2 = localThrowableContainerPropertySet != null ? localThrowableContainerPropertySet.getThrowable() : null;
        return localThrowable2 != null ? doThrow(localNoSuspendResumer.response, localThrowable2) : doReturnWith(localNoSuspendResumer.response);
      }
      localAsyncProviderCallbackImpl.resumer = new FiberResumer();
      return doSuspend();
    }
  }
  
  @NotNull
  public NextAction processResponse(@NotNull Packet paramPacket)
  {
    return doReturnWith(paramPacket);
  }
  
  @NotNull
  public NextAction processException(@NotNull Throwable paramThrowable)
  {
    return doThrow(paramThrowable);
  }
  
  public class AsyncProviderCallbackImpl
    implements AsyncProviderCallback<T>
  {
    private final Packet request;
    private AsyncProviderInvokerTube.Resumer resumer;
    
    public AsyncProviderCallbackImpl(Packet paramPacket, AsyncProviderInvokerTube.Resumer paramResumer)
    {
      this.request = paramPacket;
      this.resumer = paramResumer;
    }
    
    public void send(@Nullable T paramT)
    {
      if ((paramT == null) && (this.request.transportBackChannel != null)) {
        this.request.transportBackChannel.close();
      }
      Packet localPacket = AsyncProviderInvokerTube.this.argsBuilder.getResponse(this.request, paramT, AsyncProviderInvokerTube.this.getEndpoint().getPort(), AsyncProviderInvokerTube.this.getEndpoint().getBinding());
      synchronized (this)
      {
        this.resumer.onResume(localPacket);
      }
    }
    
    public void sendError(@NotNull Throwable paramThrowable)
    {
      Object localObject1;
      if ((paramThrowable instanceof Exception)) {
        localObject1 = (Exception)paramThrowable;
      } else {
        localObject1 = new RuntimeException(paramThrowable);
      }
      Packet localPacket = AsyncProviderInvokerTube.this.argsBuilder.getResponse(this.request, (Exception)localObject1, AsyncProviderInvokerTube.this.getEndpoint().getPort(), AsyncProviderInvokerTube.this.getEndpoint().getBinding());
      synchronized (this)
      {
        this.resumer.onResume(localPacket);
      }
    }
  }
  
  public class AsyncWebServiceContext
    extends AbstractWebServiceContext
  {
    final Packet packet;
    
    public AsyncWebServiceContext(WSEndpoint paramWSEndpoint, Packet paramPacket)
    {
      super();
      this.packet = paramPacket;
    }
    
    @NotNull
    public Packet getRequestPacket()
    {
      return this.packet;
    }
  }
  
  public class FiberResumer
    implements AsyncProviderInvokerTube.Resumer
  {
    private final Fiber fiber = Fiber.current();
    
    public FiberResumer() {}
    
    public void onResume(Packet paramPacket)
    {
      ThrowableContainerPropertySet localThrowableContainerPropertySet = (ThrowableContainerPropertySet)paramPacket.getSatellite(ThrowableContainerPropertySet.class);
      Throwable localThrowable = localThrowableContainerPropertySet != null ? localThrowableContainerPropertySet.getThrowable() : null;
      this.fiber.resume(localThrowable, paramPacket);
    }
  }
  
  private class NoSuspendResumer
    implements AsyncProviderInvokerTube.Resumer
  {
    protected Packet response = null;
    
    private NoSuspendResumer() {}
    
    public void onResume(Packet paramPacket)
    {
      this.response = paramPacket;
    }
  }
  
  private static abstract interface Resumer
  {
    public abstract void onResume(Packet paramPacket);
  }
}
