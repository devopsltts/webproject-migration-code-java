package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.istack.internal.Nullable;

public abstract interface DocumentLocationResolver
{
  @Nullable
  public abstract String getLocationFor(String paramString1, String paramString2);
}
