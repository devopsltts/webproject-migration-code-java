package sun.net.spi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import sun.misc.REException;
import sun.misc.RegexpPool;
import sun.net.NetProperties;
import sun.net.SocksProxy;

public class DefaultProxySelector
  extends ProxySelector
{
  static final String[][] props = { { "http", "http.proxy", "proxy", "socksProxy" }, { "https", "https.proxy", "proxy", "socksProxy" }, { "ftp", "ftp.proxy", "ftpProxy", "proxy", "socksProxy" }, { "gopher", "gopherProxy", "socksProxy" }, { "socket", "socksProxy" } };
  private static final String SOCKS_PROXY_VERSION = "socksProxyVersion";
  private static boolean hasSystemProxies = false;
  
  public DefaultProxySelector() {}
  
  public List<Proxy> select(URI paramURI)
  {
    if (paramURI == null) {
      throw new IllegalArgumentException("URI can't be null.");
    }
    String str1 = paramURI.getScheme();
    Object localObject1 = paramURI.getHost();
    if (localObject1 == null)
    {
      localObject2 = paramURI.getAuthority();
      if (localObject2 != null)
      {
        int i = ((String)localObject2).indexOf('@');
        if (i >= 0) {
          localObject2 = ((String)localObject2).substring(i + 1);
        }
        i = ((String)localObject2).lastIndexOf(':');
        if (i >= 0) {
          localObject2 = ((String)localObject2).substring(0, i);
        }
        localObject1 = localObject2;
      }
    }
    if ((str1 == null) || (localObject1 == null)) {
      throw new IllegalArgumentException("protocol = " + str1 + " host = " + (String)localObject1);
    }
    Object localObject2 = new ArrayList(1);
    NonProxyInfo localNonProxyInfo1 = null;
    if ("http".equalsIgnoreCase(str1)) {
      localNonProxyInfo1 = NonProxyInfo.httpNonProxyInfo;
    } else if ("https".equalsIgnoreCase(str1)) {
      localNonProxyInfo1 = NonProxyInfo.httpNonProxyInfo;
    } else if ("ftp".equalsIgnoreCase(str1)) {
      localNonProxyInfo1 = NonProxyInfo.ftpNonProxyInfo;
    } else if ("socket".equalsIgnoreCase(str1)) {
      localNonProxyInfo1 = NonProxyInfo.socksNonProxyInfo;
    }
    final String str2 = str1;
    final NonProxyInfo localNonProxyInfo2 = localNonProxyInfo1;
    final String str3 = ((String)localObject1).toLowerCase();
    Proxy localProxy = (Proxy)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Proxy run()
      {
        String str1 = null;
        int j = 0;
        String str2 = null;
        InetSocketAddress localInetSocketAddress = null;
        for (int i = 0; i < DefaultProxySelector.props.length; i++) {
          if (DefaultProxySelector.props[i][0].equalsIgnoreCase(str2))
          {
            for (Object localObject1 = 1; localObject1 < DefaultProxySelector.props[i].length; localObject1++)
            {
              str1 = NetProperties.get(DefaultProxySelector.props[i][localObject1] + "Host");
              if ((str1 != null) && (str1.length() != 0)) {
                break;
              }
            }
            Object localObject3;
            if ((str1 == null) || (str1.length() == 0))
            {
              if (DefaultProxySelector.hasSystemProxies)
              {
                String str3;
                if (str2.equalsIgnoreCase("socket")) {
                  str3 = "socks";
                } else {
                  str3 = str2;
                }
                localObject3 = DefaultProxySelector.this.getSystemProxy(str3, str3);
                if (localObject3 != null) {
                  return localObject3;
                }
              }
              return Proxy.NO_PROXY;
            }
            if (localNonProxyInfo2 != null)
            {
              str2 = NetProperties.get(localNonProxyInfo2.property);
              synchronized (localNonProxyInfo2)
              {
                if (str2 == null)
                {
                  if (localNonProxyInfo2.defaultVal != null)
                  {
                    str2 = localNonProxyInfo2.defaultVal;
                  }
                  else
                  {
                    localNonProxyInfo2.hostsSource = null;
                    localNonProxyInfo2.hostsPool = null;
                  }
                }
                else if (str2.length() != 0) {
                  str2 = str2 + "|localhost|127.*|[::1]|0.0.0.0|[::0]";
                }
                if ((str2 != null) && (!str2.equals(localNonProxyInfo2.hostsSource)))
                {
                  localObject3 = new RegexpPool();
                  StringTokenizer localStringTokenizer = new StringTokenizer(str2, "|", false);
                  try
                  {
                    while (localStringTokenizer.hasMoreTokens()) {
                      ((RegexpPool)localObject3).add(localStringTokenizer.nextToken().toLowerCase(), Boolean.TRUE);
                    }
                  }
                  catch (REException localREException) {}
                  localNonProxyInfo2.hostsPool = ((RegexpPool)localObject3);
                  localNonProxyInfo2.hostsSource = str2;
                }
                if ((localNonProxyInfo2.hostsPool != null) && (localNonProxyInfo2.hostsPool.match(str3) != null)) {
                  return Proxy.NO_PROXY;
                }
              }
            }
            j = NetProperties.getInteger(DefaultProxySelector.props[i][localObject1] + "Port", 0).intValue();
            if ((j == 0) && (localObject1 < DefaultProxySelector.props[i].length - 1)) {
              for (Object localObject2 = 1; localObject2 < DefaultProxySelector.props[i].length - 1; localObject2++) {
                if ((localObject2 != localObject1) && (j == 0)) {
                  j = NetProperties.getInteger(DefaultProxySelector.props[i][localObject2] + "Port", 0).intValue();
                }
              }
            }
            if (j == 0) {
              if (localObject1 == DefaultProxySelector.props[i].length - 1) {
                j = DefaultProxySelector.this.defaultPort("socket");
              } else {
                j = DefaultProxySelector.this.defaultPort(str2);
              }
            }
            localInetSocketAddress = InetSocketAddress.createUnresolved(str1, j);
            if (localObject1 == DefaultProxySelector.props[i].length - 1)
            {
              int k = NetProperties.getInteger("socksProxyVersion", 5).intValue();
              return SocksProxy.create(localInetSocketAddress, k);
            }
            return new Proxy(Proxy.Type.HTTP, localInetSocketAddress);
          }
        }
        return Proxy.NO_PROXY;
      }
    });
    ((List)localObject2).add(localProxy);
    return localObject2;
  }
  
  public void connectFailed(URI paramURI, SocketAddress paramSocketAddress, IOException paramIOException)
  {
    if ((paramURI == null) || (paramSocketAddress == null) || (paramIOException == null)) {
      throw new IllegalArgumentException("Arguments can't be null.");
    }
  }
  
  private int defaultPort(String paramString)
  {
    if ("http".equalsIgnoreCase(paramString)) {
      return 80;
    }
    if ("https".equalsIgnoreCase(paramString)) {
      return 443;
    }
    if ("ftp".equalsIgnoreCase(paramString)) {
      return 80;
    }
    if ("socket".equalsIgnoreCase(paramString)) {
      return 1080;
    }
    if ("gopher".equalsIgnoreCase(paramString)) {
      return 80;
    }
    return -1;
  }
  
  private static native boolean init();
  
  private synchronized native Proxy getSystemProxy(String paramString1, String paramString2);
  
  static
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        return NetProperties.getBoolean("java.net.useSystemProxies");
      }
    });
    if ((localBoolean != null) && (localBoolean.booleanValue()))
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          System.loadLibrary("net");
          return null;
        }
      });
      hasSystemProxies = init();
    }
  }
  
  static class NonProxyInfo
  {
    static final String defStringVal = "localhost|127.*|[::1]|0.0.0.0|[::0]";
    String hostsSource;
    RegexpPool hostsPool;
    final String property;
    final String defaultVal;
    static NonProxyInfo ftpNonProxyInfo = new NonProxyInfo("ftp.nonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
    static NonProxyInfo httpNonProxyInfo = new NonProxyInfo("http.nonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
    static NonProxyInfo socksNonProxyInfo = new NonProxyInfo("socksNonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
    
    NonProxyInfo(String paramString1, String paramString2, RegexpPool paramRegexpPool, String paramString3)
    {
      this.property = paramString1;
      this.hostsSource = paramString2;
      this.hostsPool = paramRegexpPool;
      this.defaultVal = paramString3;
    }
  }
}
