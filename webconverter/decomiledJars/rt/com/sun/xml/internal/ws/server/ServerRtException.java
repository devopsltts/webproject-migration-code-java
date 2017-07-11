package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class ServerRtException
  extends JAXWSExceptionBase
{
  public ServerRtException(String paramString, Object... paramVarArgs)
  {
    super(paramString, paramVarArgs);
  }
  
  public ServerRtException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public ServerRtException(Localizable paramLocalizable)
  {
    super("server.rt.err", new Object[] { paramLocalizable });
  }
  
  public String getDefaultResourceBundleName()
  {
    return "com.sun.xml.internal.ws.resources.server";
  }
}
