package org.omg.PortableInterceptor;

public abstract interface ServerRequestInterceptorOperations
  extends InterceptorOperations
{
  public abstract void receive_request_service_contexts(ServerRequestInfo paramServerRequestInfo)
    throws ForwardRequest;
  
  public abstract void receive_request(ServerRequestInfo paramServerRequestInfo)
    throws ForwardRequest;
  
  public abstract void send_reply(ServerRequestInfo paramServerRequestInfo);
  
  public abstract void send_exception(ServerRequestInfo paramServerRequestInfo)
    throws ForwardRequest;
  
  public abstract void send_other(ServerRequestInfo paramServerRequestInfo)
    throws ForwardRequest;
}
