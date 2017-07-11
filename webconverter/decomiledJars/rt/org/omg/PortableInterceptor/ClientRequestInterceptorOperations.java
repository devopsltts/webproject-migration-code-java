package org.omg.PortableInterceptor;

public abstract interface ClientRequestInterceptorOperations
  extends InterceptorOperations
{
  public abstract void send_request(ClientRequestInfo paramClientRequestInfo)
    throws ForwardRequest;
  
  public abstract void send_poll(ClientRequestInfo paramClientRequestInfo);
  
  public abstract void receive_reply(ClientRequestInfo paramClientRequestInfo);
  
  public abstract void receive_exception(ClientRequestInfo paramClientRequestInfo)
    throws ForwardRequest;
  
  public abstract void receive_other(ClientRequestInfo paramClientRequestInfo)
    throws ForwardRequest;
}
