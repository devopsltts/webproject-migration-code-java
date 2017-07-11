package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;

@Deprecated
public abstract class TrustManagerFactorySpi
{
  public TrustManagerFactorySpi() {}
  
  protected abstract void engineInit(KeyStore paramKeyStore)
    throws KeyStoreException;
  
  protected abstract TrustManager[] engineGetTrustManagers();
}
