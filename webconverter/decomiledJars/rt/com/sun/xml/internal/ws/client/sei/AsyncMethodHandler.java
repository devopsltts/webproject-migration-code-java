package com.sun.xml.internal.ws.client.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber.CompletionCallback;
import com.sun.xml.internal.ws.client.AsyncInvoker;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContext;
import java.lang.reflect.Method;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

abstract class AsyncMethodHandler
  extends MethodHandler
{
  AsyncMethodHandler(SEIStub paramSEIStub, Method paramMethod)
  {
    super(paramSEIStub, paramMethod);
  }
  
  protected final Response<Object> doInvoke(Object paramObject, Object[] paramArrayOfObject, AsyncHandler paramAsyncHandler)
  {
    SEIAsyncInvoker localSEIAsyncInvoker = new SEIAsyncInvoker(paramObject, paramArrayOfObject);
    localSEIAsyncInvoker.setNonNullAsyncHandlerGiven(paramAsyncHandler != null);
    AsyncResponseImpl localAsyncResponseImpl = new AsyncResponseImpl(localSEIAsyncInvoker, paramAsyncHandler);
    localSEIAsyncInvoker.setReceiver(localAsyncResponseImpl);
    localAsyncResponseImpl.run();
    return localAsyncResponseImpl;
  }
  
  ValueGetterFactory getValueGetterFactory()
  {
    return ValueGetterFactory.ASYNC;
  }
  
  private class SEIAsyncInvoker
    extends AsyncInvoker
  {
    private final RequestContext rc = AsyncMethodHandler.this.owner.requestContext.copy();
    private final Object[] args;
    
    SEIAsyncInvoker(Object paramObject, Object[] paramArrayOfObject)
    {
      this.args = paramArrayOfObject;
    }
    
    public void do_run()
    {
      JavaCallInfo localJavaCallInfo = AsyncMethodHandler.this.owner.databinding.createJavaCallInfo(AsyncMethodHandler.this.method, this.args);
      Packet localPacket = (Packet)AsyncMethodHandler.this.owner.databinding.serializeRequest(localJavaCallInfo);
      Fiber.CompletionCallback local1 = new Fiber.CompletionCallback()
      {
        public void onCompletion(@NotNull Packet paramAnonymousPacket)
        {
          AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.setResponseContext(new ResponseContext(paramAnonymousPacket));
          Message localMessage = paramAnonymousPacket.getMessage();
          if (localMessage == null) {
            return;
          }
          try
          {
            Object[] arrayOfObject = new Object[1];
            JavaCallInfo localJavaCallInfo = AsyncMethodHandler.this.owner.databinding.createJavaCallInfo(AsyncMethodHandler.this.method, arrayOfObject);
            localJavaCallInfo = AsyncMethodHandler.this.owner.databinding.deserializeResponse(paramAnonymousPacket, localJavaCallInfo);
            if (localJavaCallInfo.getException() != null) {
              throw localJavaCallInfo.getException();
            }
            AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(arrayOfObject[0], null);
          }
          catch (Throwable localThrowable)
          {
            if ((localThrowable instanceof RuntimeException))
            {
              if ((localThrowable instanceof WebServiceException)) {
                AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(null, localThrowable);
              }
            }
            else if ((localThrowable instanceof Exception))
            {
              AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(null, localThrowable);
              return;
            }
            AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(null, new WebServiceException(localThrowable));
          }
        }
        
        public void onCompletion(@NotNull Throwable paramAnonymousThrowable)
        {
          if ((paramAnonymousThrowable instanceof WebServiceException)) {
            AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(null, paramAnonymousThrowable);
          } else {
            AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(null, new WebServiceException(paramAnonymousThrowable));
          }
        }
      };
      AsyncMethodHandler.this.owner.doProcessAsync(this.responseImpl, localPacket, this.rc, local1);
    }
  }
}
