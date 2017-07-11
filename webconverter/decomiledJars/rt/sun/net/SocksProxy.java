package sun.net;

import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;

public final class SocksProxy
  extends Proxy
{
  private final int version;
  
  private SocksProxy(SocketAddress paramSocketAddress, int paramInt)
  {
    super(Proxy.Type.SOCKS, paramSocketAddress);
    this.version = paramInt;
  }
  
  public static SocksProxy create(SocketAddress paramSocketAddress, int paramInt)
  {
    return new SocksProxy(paramSocketAddress, paramInt);
  }
  
  public int protocolVersion()
  {
    return this.version;
  }
}
