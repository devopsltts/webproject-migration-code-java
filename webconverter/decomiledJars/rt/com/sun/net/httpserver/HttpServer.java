package com.sun.net.httpserver;

import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import jdk.Exported;

@Exported
public abstract class HttpServer
{
  protected HttpServer() {}
  
  public static HttpServer create()
    throws IOException
  {
    return create(null, 0);
  }
  
  public static HttpServer create(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    HttpServerProvider localHttpServerProvider = HttpServerProvider.provider();
    return localHttpServerProvider.createHttpServer(paramInetSocketAddress, paramInt);
  }
  
  public abstract void bind(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException;
  
  public abstract void start();
  
  public abstract void setExecutor(Executor paramExecutor);
  
  public abstract Executor getExecutor();
  
  public abstract void stop(int paramInt);
  
  public abstract HttpContext createContext(String paramString, HttpHandler paramHttpHandler);
  
  public abstract HttpContext createContext(String paramString);
  
  public abstract void removeContext(String paramString)
    throws IllegalArgumentException;
  
  public abstract void removeContext(HttpContext paramHttpContext);
  
  public abstract InetSocketAddress getAddress();
}
