package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public abstract class SSLServerSocket
  extends ServerSocket
{
  protected SSLServerSocket()
    throws IOException
  {}
  
  protected SSLServerSocket(int paramInt)
    throws IOException
  {
    super(paramInt);
  }
  
  protected SSLServerSocket(int paramInt1, int paramInt2)
    throws IOException
  {
    super(paramInt1, paramInt2);
  }
  
  protected SSLServerSocket(int paramInt1, int paramInt2, InetAddress paramInetAddress)
    throws IOException
  {
    super(paramInt1, paramInt2, paramInetAddress);
  }
  
  public abstract String[] getEnabledCipherSuites();
  
  public abstract void setEnabledCipherSuites(String[] paramArrayOfString);
  
  public abstract String[] getSupportedCipherSuites();
  
  public abstract String[] getSupportedProtocols();
  
  public abstract String[] getEnabledProtocols();
  
  public abstract void setEnabledProtocols(String[] paramArrayOfString);
  
  public abstract void setNeedClientAuth(boolean paramBoolean);
  
  public abstract boolean getNeedClientAuth();
  
  public abstract void setWantClientAuth(boolean paramBoolean);
  
  public abstract boolean getWantClientAuth();
  
  public abstract void setUseClientMode(boolean paramBoolean);
  
  public abstract boolean getUseClientMode();
  
  public abstract void setEnableSessionCreation(boolean paramBoolean);
  
  public abstract boolean getEnableSessionCreation();
  
  public SSLParameters getSSLParameters()
  {
    SSLParameters localSSLParameters = new SSLParameters();
    localSSLParameters.setCipherSuites(getEnabledCipherSuites());
    localSSLParameters.setProtocols(getEnabledProtocols());
    if (getNeedClientAuth()) {
      localSSLParameters.setNeedClientAuth(true);
    } else if (getWantClientAuth()) {
      localSSLParameters.setWantClientAuth(true);
    }
    return localSSLParameters;
  }
  
  public void setSSLParameters(SSLParameters paramSSLParameters)
  {
    String[] arrayOfString = paramSSLParameters.getCipherSuites();
    if (arrayOfString != null) {
      setEnabledCipherSuites(arrayOfString);
    }
    arrayOfString = paramSSLParameters.getProtocols();
    if (arrayOfString != null) {
      setEnabledProtocols(arrayOfString);
    }
    if (paramSSLParameters.getNeedClientAuth()) {
      setNeedClientAuth(true);
    } else if (paramSSLParameters.getWantClientAuth()) {
      setWantClientAuth(true);
    } else {
      setWantClientAuth(false);
    }
  }
}
