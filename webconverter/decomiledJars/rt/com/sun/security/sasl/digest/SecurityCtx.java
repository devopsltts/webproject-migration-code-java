package com.sun.security.sasl.digest;

import javax.security.sasl.SaslException;

abstract interface SecurityCtx
{
  public abstract byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException;
  
  public abstract byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException;
}
