package com.sun.xml.internal.ws.transport.http;

import com.sun.istack.internal.NotNull;
import java.io.IOException;

public abstract class HttpMetadataPublisher
{
  public HttpMetadataPublisher() {}
  
  public abstract boolean handleMetadataRequest(@NotNull HttpAdapter paramHttpAdapter, @NotNull WSHTTPConnection paramWSHTTPConnection)
    throws IOException;
}
