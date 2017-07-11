package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.CompletionHandlerWrapper;
import jdk.management.resource.internal.FutureWrapper;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.AsynchronousServerSocketChannelImpl")
public class AsynchronousServerSocketChannelImplRMHooks
{
  public AsynchronousServerSocketChannelImplRMHooks() {}
  
  @InstrumentationMethod
  public final SocketAddress getLocalAddress()
    throws IOException
  {
    return getLocalAddress();
  }
  
  @InstrumentationMethod
  public final AsynchronousServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    ResourceIdImpl localResourceIdImpl = null;
    ResourceRequest localResourceRequest = null;
    long l = 0L;
    if (getLocalAddress() == null)
    {
      localResourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          throw new IOException("Resource limited: too many open socket channels");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        throw new IOException("Resource limited: too many open socket channels", localResourceRequestDeniedException);
      }
    }
    int i = 0;
    AsynchronousServerSocketChannel localAsynchronousServerSocketChannel = null;
    try
    {
      localAsynchronousServerSocketChannel = bind(paramSocketAddress, paramInt);
      i = 1;
    }
    finally
    {
      if (localResourceRequest != null) {
        localResourceRequest.request(-(l - i), localResourceIdImpl);
      }
    }
    return localAsynchronousServerSocketChannel;
  }
  
  @InstrumentationMethod
  public final Future<AsynchronousSocketChannel> accept()
  {
    Object localObject1 = accept();
    Object localObject2;
    if (((Future)localObject1).isDone())
    {
      try
      {
        localObject2 = (AsynchronousSocketChannel)((Future)localObject1).get();
      }
      catch (InterruptedException localInterruptedException)
      {
        localCompletableFuture1 = new CompletableFuture();
        localCompletableFuture1.completeExceptionally(localInterruptedException);
        return localCompletableFuture1;
      }
      catch (ExecutionException localExecutionException)
      {
        CompletableFuture localCompletableFuture1 = new CompletableFuture();
        localCompletableFuture1.completeExceptionally(localExecutionException.getCause());
        return localCompletableFuture1;
      }
      ResourceIdImpl localResourceIdImpl = null;
      try
      {
        localResourceIdImpl = ResourceIdImpl.of(((AsynchronousSocketChannel)localObject2).getLocalAddress());
      }
      catch (IOException localIOException1) {}
      ResourceRequest localResourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(localObject2);
      long l = 0L;
      Object localObject3 = null;
      try
      {
        l = localResourceRequest.request(1L, localResourceIdImpl);
        if (l < 1L) {
          localObject3 = new ResourceRequestDeniedException("Resource limited: too many open server socket channels");
        }
      }
      catch (ResourceRequestDeniedException localResourceRequestDeniedException)
      {
        localObject3 = localResourceRequestDeniedException;
      }
      if (localObject3 == null)
      {
        localResourceRequest.request(-(l - 1L), localResourceIdImpl);
      }
      else
      {
        localResourceRequest.request(-l, localResourceIdImpl);
        try
        {
          ((AsynchronousSocketChannel)localObject2).close();
        }
        catch (IOException localIOException2) {}
        CompletableFuture localCompletableFuture2 = new CompletableFuture();
        localCompletableFuture2.completeExceptionally((Throwable)localObject3);
        return localCompletableFuture2;
      }
    }
    else
    {
      localObject2 = new FutureWrapper((Future)localObject1);
      localObject1 = localObject2;
    }
    return localObject1;
  }
  
  @InstrumentationMethod
  public final <A> void accept(A paramA, CompletionHandler<AsynchronousSocketChannel, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    paramCompletionHandler = new CompletionHandlerWrapper(paramCompletionHandler);
    accept(paramA, paramCompletionHandler);
  }
}
