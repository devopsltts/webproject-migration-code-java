package com.oracle.nio;

import java.security.BasicPermission;

public final class BufferSecretsPermission
  extends BasicPermission
{
  private static final long serialVersionUID = 0L;
  
  public BufferSecretsPermission(String paramString)
  {
    super(paramString);
    if (!paramString.equals("access")) {
      throw new IllegalArgumentException();
    }
  }
}
