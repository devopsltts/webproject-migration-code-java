package com.sun.xml.internal.ws.client;

import javax.xml.ws.WebServiceException;

public abstract class AsyncInvoker
  implements Runnable
{
  protected AsyncResponseImpl responseImpl;
  protected boolean nonNullAsyncHandlerGiven;
  
  public AsyncInvoker() {}
  
  public void setReceiver(AsyncResponseImpl paramAsyncResponseImpl)
  {
    this.responseImpl = paramAsyncResponseImpl;
  }
  
  public AsyncResponseImpl getResponseImpl()
  {
    return this.responseImpl;
  }
  
  public void setResponseImpl(AsyncResponseImpl paramAsyncResponseImpl)
  {
    this.responseImpl = paramAsyncResponseImpl;
  }
  
  public boolean isNonNullAsyncHandlerGiven()
  {
    return this.nonNullAsyncHandlerGiven;
  }
  
  public void setNonNullAsyncHandlerGiven(boolean paramBoolean)
  {
    this.nonNullAsyncHandlerGiven = paramBoolean;
  }
  
  public void run()
  {
    try
    {
      do_run();
    }
    catch (WebServiceException localWebServiceException)
    {
      throw localWebServiceException;
    }
    catch (Throwable localThrowable)
    {
      throw new WebServiceException(localThrowable);
    }
  }
  
  public abstract void do_run();
}
