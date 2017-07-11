package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import org.xml.sax.Locator;

public abstract interface WSDLObject
{
  @NotNull
  public abstract Locator getLocation();
}
