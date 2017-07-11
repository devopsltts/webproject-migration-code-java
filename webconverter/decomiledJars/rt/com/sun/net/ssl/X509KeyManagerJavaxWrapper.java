package com.sun.net.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;

final class X509KeyManagerJavaxWrapper
  implements javax.net.ssl.X509KeyManager
{
  private X509KeyManager theX509KeyManager;
  
  X509KeyManagerJavaxWrapper(X509KeyManager paramX509KeyManager)
  {
    this.theX509KeyManager = paramX509KeyManager;
  }
  
  public String[] getClientAliases(String paramString, Principal[] paramArrayOfPrincipal)
  {
    return this.theX509KeyManager.getClientAliases(paramString, paramArrayOfPrincipal);
  }
  
  public String chooseClientAlias(String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, Socket paramSocket)
  {
    if (paramArrayOfString == null) {
      return null;
    }
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str;
      if ((str = this.theX509KeyManager.chooseClientAlias(paramArrayOfString[i], paramArrayOfPrincipal)) != null) {
        return str;
      }
    }
    return null;
  }
  
  public String chooseEngineClientAlias(String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine)
  {
    if (paramArrayOfString == null) {
      return null;
    }
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str;
      if ((str = this.theX509KeyManager.chooseClientAlias(paramArrayOfString[i], paramArrayOfPrincipal)) != null) {
        return str;
      }
    }
    return null;
  }
  
  public String[] getServerAliases(String paramString, Principal[] paramArrayOfPrincipal)
  {
    return this.theX509KeyManager.getServerAliases(paramString, paramArrayOfPrincipal);
  }
  
  public String chooseServerAlias(String paramString, Principal[] paramArrayOfPrincipal, Socket paramSocket)
  {
    if (paramString == null) {
      return null;
    }
    return this.theX509KeyManager.chooseServerAlias(paramString, paramArrayOfPrincipal);
  }
  
  public String chooseEngineServerAlias(String paramString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine)
  {
    if (paramString == null) {
      return null;
    }
    return this.theX509KeyManager.chooseServerAlias(paramString, paramArrayOfPrincipal);
  }
  
  public X509Certificate[] getCertificateChain(String paramString)
  {
    return this.theX509KeyManager.getCertificateChain(paramString);
  }
  
  public PrivateKey getPrivateKey(String paramString)
  {
    return this.theX509KeyManager.getPrivateKey(paramString);
  }
}
