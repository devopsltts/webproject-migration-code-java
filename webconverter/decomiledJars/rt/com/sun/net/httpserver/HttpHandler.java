package com.sun.net.httpserver;

import java.io.IOException;
import jdk.Exported;

@Exported
public abstract interface HttpHandler
{
  public abstract void handle(HttpExchange paramHttpExchange)
    throws IOException;
}
