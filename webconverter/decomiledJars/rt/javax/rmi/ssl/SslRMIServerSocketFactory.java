package javax.rmi.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SslRMIServerSocketFactory
  implements RMIServerSocketFactory
{
  private static SSLSocketFactory defaultSSLSocketFactory = null;
  private final String[] enabledCipherSuites;
  private final String[] enabledProtocols;
  private final boolean needClientAuth;
  private List<String> enabledCipherSuitesList;
  private List<String> enabledProtocolsList;
  private SSLContext context;
  
  public SslRMIServerSocketFactory()
  {
    this(null, null, false);
  }
  
  public SslRMIServerSocketFactory(String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean)
    throws IllegalArgumentException
  {
    this(null, paramArrayOfString1, paramArrayOfString2, paramBoolean);
  }
  
  public SslRMIServerSocketFactory(SSLContext paramSSLContext, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean)
    throws IllegalArgumentException
  {
    this.enabledCipherSuites = (paramArrayOfString1 == null ? null : (String[])paramArrayOfString1.clone());
    this.enabledProtocols = (paramArrayOfString2 == null ? null : (String[])paramArrayOfString2.clone());
    this.needClientAuth = paramBoolean;
    this.context = paramSSLContext;
    SSLSocketFactory localSSLSocketFactory = paramSSLContext == null ? getDefaultSSLSocketFactory() : paramSSLContext.getSocketFactory();
    SSLSocket localSSLSocket = null;
    if ((this.enabledCipherSuites != null) || (this.enabledProtocols != null)) {
      try
      {
        localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket();
      }
      catch (Exception localException)
      {
        throw ((IllegalArgumentException)new IllegalArgumentException("Unable to check if the cipher suites and protocols to enable are supported").initCause(localException));
      }
    }
    if (this.enabledCipherSuites != null)
    {
      localSSLSocket.setEnabledCipherSuites(this.enabledCipherSuites);
      this.enabledCipherSuitesList = Arrays.asList(this.enabledCipherSuites);
    }
    if (this.enabledProtocols != null)
    {
      localSSLSocket.setEnabledProtocols(this.enabledProtocols);
      this.enabledProtocolsList = Arrays.asList(this.enabledProtocols);
    }
  }
  
  public final String[] getEnabledCipherSuites()
  {
    return this.enabledCipherSuites == null ? null : (String[])this.enabledCipherSuites.clone();
  }
  
  public final String[] getEnabledProtocols()
  {
    return this.enabledProtocols == null ? null : (String[])this.enabledProtocols.clone();
  }
  
  public final boolean getNeedClientAuth()
  {
    return this.needClientAuth;
  }
  
  public ServerSocket createServerSocket(int paramInt)
    throws IOException
  {
    final SSLSocketFactory localSSLSocketFactory = this.context == null ? getDefaultSSLSocketFactory() : this.context.getSocketFactory();
    new ServerSocket(paramInt)
    {
      public Socket accept()
        throws IOException
      {
        Socket localSocket = super.accept();
        SSLSocket localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket(localSocket, localSocket.getInetAddress().getHostName(), localSocket.getPort(), true);
        localSSLSocket.setUseClientMode(false);
        if (SslRMIServerSocketFactory.this.enabledCipherSuites != null) {
          localSSLSocket.setEnabledCipherSuites(SslRMIServerSocketFactory.this.enabledCipherSuites);
        }
        if (SslRMIServerSocketFactory.this.enabledProtocols != null) {
          localSSLSocket.setEnabledProtocols(SslRMIServerSocketFactory.this.enabledProtocols);
        }
        localSSLSocket.setNeedClientAuth(SslRMIServerSocketFactory.this.needClientAuth);
        return localSSLSocket;
      }
    };
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof SslRMIServerSocketFactory)) {
      return false;
    }
    SslRMIServerSocketFactory localSslRMIServerSocketFactory = (SslRMIServerSocketFactory)paramObject;
    return (getClass().equals(localSslRMIServerSocketFactory.getClass())) && (checkParameters(localSslRMIServerSocketFactory));
  }
  
  private boolean checkParameters(SslRMIServerSocketFactory paramSslRMIServerSocketFactory)
  {
    if (this.context == null ? paramSslRMIServerSocketFactory.context != null : !this.context.equals(paramSslRMIServerSocketFactory.context)) {
      return false;
    }
    if (this.needClientAuth != paramSslRMIServerSocketFactory.needClientAuth) {
      return false;
    }
    if (((this.enabledCipherSuites == null) && (paramSslRMIServerSocketFactory.enabledCipherSuites != null)) || ((this.enabledCipherSuites != null) && (paramSslRMIServerSocketFactory.enabledCipherSuites == null))) {
      return false;
    }
    List localList;
    if ((this.enabledCipherSuites != null) && (paramSslRMIServerSocketFactory.enabledCipherSuites != null))
    {
      localList = Arrays.asList(paramSslRMIServerSocketFactory.enabledCipherSuites);
      if (!this.enabledCipherSuitesList.equals(localList)) {
        return false;
      }
    }
    if (((this.enabledProtocols == null) && (paramSslRMIServerSocketFactory.enabledProtocols != null)) || ((this.enabledProtocols != null) && (paramSslRMIServerSocketFactory.enabledProtocols == null))) {
      return false;
    }
    if ((this.enabledProtocols != null) && (paramSslRMIServerSocketFactory.enabledProtocols != null))
    {
      localList = Arrays.asList(paramSslRMIServerSocketFactory.enabledProtocols);
      if (!this.enabledProtocolsList.equals(localList)) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    return getClass().hashCode() + (this.context == null ? 0 : this.context.hashCode()) + (this.needClientAuth ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode()) + (this.enabledCipherSuites == null ? 0 : this.enabledCipherSuitesList.hashCode()) + (this.enabledProtocols == null ? 0 : this.enabledProtocolsList.hashCode());
  }
  
  private static synchronized SSLSocketFactory getDefaultSSLSocketFactory()
  {
    if (defaultSSLSocketFactory == null) {
      defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
    }
    return defaultSSLSocketFactory;
  }
}
