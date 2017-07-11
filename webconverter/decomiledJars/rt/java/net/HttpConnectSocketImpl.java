package java.net;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

class HttpConnectSocketImpl
  extends PlainSocketImpl
{
  private static final String httpURLClazzStr = "sun.net.www.protocol.http.HttpURLConnection";
  private static final String netClientClazzStr = "sun.net.NetworkClient";
  private static final String doTunnelingStr = "doTunneling";
  private static final Field httpField;
  private static final Field serverSocketField;
  private static final Method doTunneling;
  private final String server;
  private InetSocketAddress external_address;
  private HashMap<Integer, Object> optionsMap = new HashMap();
  
  HttpConnectSocketImpl(String paramString, int paramInt)
  {
    this.server = paramString;
    this.port = paramInt;
  }
  
  HttpConnectSocketImpl(Proxy paramProxy)
  {
    SocketAddress localSocketAddress = paramProxy.address();
    if (!(localSocketAddress instanceof InetSocketAddress)) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    InetSocketAddress localInetSocketAddress = (InetSocketAddress)localSocketAddress;
    this.server = localInetSocketAddress.getHostString();
    this.port = localInetSocketAddress.getPort();
  }
  
  protected void connect(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress))) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
    String str1 = localInetSocketAddress.isUnresolved() ? localInetSocketAddress.getHostName() : localInetSocketAddress.getAddress().getHostAddress();
    int i = localInetSocketAddress.getPort();
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkConnect(str1, i);
    }
    String str2 = "http://" + str1 + ":" + i;
    Socket localSocket = privilegedDoTunnel(str2, paramInt);
    this.external_address = localInetSocketAddress;
    close();
    AbstractPlainSocketImpl localAbstractPlainSocketImpl = (AbstractPlainSocketImpl)localSocket.impl;
    getSocket().impl = localAbstractPlainSocketImpl;
    Set localSet = this.optionsMap.entrySet();
    try
    {
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        localAbstractPlainSocketImpl.setOption(((Integer)localEntry.getKey()).intValue(), localEntry.getValue());
      }
    }
    catch (IOException localIOException) {}
  }
  
  public void setOption(int paramInt, Object paramObject)
    throws SocketException
  {
    super.setOption(paramInt, paramObject);
    if (this.external_address != null) {
      return;
    }
    this.optionsMap.put(Integer.valueOf(paramInt), paramObject);
  }
  
  private Socket privilegedDoTunnel(final String paramString, final int paramInt)
    throws IOException
  {
    try
    {
      (Socket)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Socket run()
          throws IOException
        {
          return HttpConnectSocketImpl.this.doTunnel(paramString, paramInt);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
  
  private Socket doTunnel(String paramString, int paramInt)
    throws IOException
  {
    Proxy localProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.server, this.port));
    URL localURL = new URL(paramString);
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURL.openConnection(localProxy);
    localHttpURLConnection.setConnectTimeout(paramInt);
    localHttpURLConnection.setReadTimeout(this.timeout);
    localHttpURLConnection.connect();
    doTunneling(localHttpURLConnection);
    try
    {
      Object localObject = httpField.get(localHttpURLConnection);
      return (Socket)serverSocketField.get(localObject);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new InternalError("Should not reach here", localIllegalAccessException);
    }
  }
  
  private void doTunneling(HttpURLConnection paramHttpURLConnection)
  {
    try
    {
      doTunneling.invoke(paramHttpURLConnection, new Object[0]);
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw new InternalError("Should not reach here", localReflectiveOperationException);
    }
  }
  
  protected InetAddress getInetAddress()
  {
    if (this.external_address != null) {
      return this.external_address.getAddress();
    }
    return super.getInetAddress();
  }
  
  protected int getPort()
  {
    if (this.external_address != null) {
      return this.external_address.getPort();
    }
    return super.getPort();
  }
  
  protected int getLocalPort()
  {
    if (this.socket != null) {
      return super.getLocalPort();
    }
    if (this.external_address != null) {
      return this.external_address.getPort();
    }
    return super.getLocalPort();
  }
  
  static
  {
    try
    {
      Class localClass1 = Class.forName("sun.net.www.protocol.http.HttpURLConnection", true, null);
      httpField = localClass1.getDeclaredField("http");
      doTunneling = localClass1.getDeclaredMethod("doTunneling", new Class[0]);
      Class localClass2 = Class.forName("sun.net.NetworkClient", true, null);
      serverSocketField = localClass2.getDeclaredField("serverSocket");
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          HttpConnectSocketImpl.httpField.setAccessible(true);
          HttpConnectSocketImpl.serverSocketField.setAccessible(true);
          return null;
        }
      });
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw new InternalError("Should not reach here", localReflectiveOperationException);
    }
  }
}
