package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Codec;

public abstract interface EndpointAwareCodec
  extends Codec
{
  public abstract void setEndpoint(@NotNull WSEndpoint paramWSEndpoint);
}
