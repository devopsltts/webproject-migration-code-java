package com.sun.net.ssl;

import java.security.BasicPermission;

@Deprecated
public final class SSLPermission
  extends BasicPermission
{
  private static final long serialVersionUID = -2583684302506167542L;
  
  public SSLPermission(String paramString)
  {
    super(paramString);
  }
  
  public SSLPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}
