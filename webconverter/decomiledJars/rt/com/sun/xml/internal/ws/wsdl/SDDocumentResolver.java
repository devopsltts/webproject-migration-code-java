package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.SDDocument;

public abstract interface SDDocumentResolver
{
  @Nullable
  public abstract SDDocument resolve(String paramString);
}
