package java.net;

import java.io.IOException;
import java.util.Enumeration;

public class MulticastSocket
  extends DatagramSocket
{
  private boolean interfaceSet;
  private Object ttlLock = new Object();
  private Object infLock = new Object();
  private InetAddress infAddress = null;
  
  public MulticastSocket()
    throws IOException
  {
    this(new InetSocketAddress(0));
  }
  
  public MulticastSocket(int paramInt)
    throws IOException
  {
    this(new InetSocketAddress(paramInt));
  }
  
  public MulticastSocket(SocketAddress paramSocketAddress)
    throws IOException
  {
    super((SocketAddress)null);
    setReuseAddress(true);
    if (paramSocketAddress != null) {
      try
      {
        bind(paramSocketAddress);
        if (!isBound()) {
          close();
        }
      }
      finally
      {
        if (!isBound()) {
          close();
        }
      }
    }
  }
  
  @Deprecated
  public void setTTL(byte paramByte)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setTTL(paramByte);
  }
  
  public void setTimeToLive(int paramInt)
    throws IOException
  {
    if ((paramInt < 0) || (paramInt > 255)) {
      throw new IllegalArgumentException("ttl out of range");
    }
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    getImpl().setTimeToLive(paramInt);
  }
  
  @Deprecated
  public byte getTTL()
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    return getImpl().getTTL();
  }
  
  public int getTimeToLive()
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    return getImpl().getTimeToLive();
  }
  
  public void joinGroup(InetAddress paramInetAddress)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    checkAddress(paramInetAddress, "joinGroup");
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkMulticast(paramInetAddress);
    }
    if (!paramInetAddress.isMulticastAddress()) {
      throw new SocketException("Not a multicast address");
    }
    NetworkInterface localNetworkInterface = NetworkInterface.getDefault();
    if ((!this.interfaceSet) && (localNetworkInterface != null)) {
      setNetworkInterface(localNetworkInterface);
    }
    getImpl().join(paramInetAddress);
  }
  
  public void leaveGroup(InetAddress paramInetAddress)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    checkAddress(paramInetAddress, "leaveGroup");
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkMulticast(paramInetAddress);
    }
    if (!paramInetAddress.isMulticastAddress()) {
      throw new SocketException("Not a multicast address");
    }
    getImpl().leave(paramInetAddress);
  }
  
  public void joinGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress))) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    if (this.oldImpl) {
      throw new UnsupportedOperationException();
    }
    checkAddress(((InetSocketAddress)paramSocketAddress).getAddress(), "joinGroup");
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkMulticast(((InetSocketAddress)paramSocketAddress).getAddress());
    }
    if (!((InetSocketAddress)paramSocketAddress).getAddress().isMulticastAddress()) {
      throw new SocketException("Not a multicast address");
    }
    getImpl().joinGroup(paramSocketAddress, paramNetworkInterface);
  }
  
  public void leaveGroup(SocketAddress paramSocketAddress, NetworkInterface paramNetworkInterface)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress))) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    if (this.oldImpl) {
      throw new UnsupportedOperationException();
    }
    checkAddress(((InetSocketAddress)paramSocketAddress).getAddress(), "leaveGroup");
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkMulticast(((InetSocketAddress)paramSocketAddress).getAddress());
    }
    if (!((InetSocketAddress)paramSocketAddress).getAddress().isMulticastAddress()) {
      throw new SocketException("Not a multicast address");
    }
    getImpl().leaveGroup(paramSocketAddress, paramNetworkInterface);
  }
  
  public void setInterface(InetAddress paramInetAddress)
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    checkAddress(paramInetAddress, "setInterface");
    synchronized (this.infLock)
    {
      getImpl().setOption(16, paramInetAddress);
      this.infAddress = paramInetAddress;
      this.interfaceSet = true;
    }
  }
  
  public InetAddress getInterface()
    throws SocketException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    synchronized (this.infLock)
    {
      InetAddress localInetAddress1 = (InetAddress)getImpl().getOption(16);
      if (this.infAddress == null) {
        return localInetAddress1;
      }
      if (localInetAddress1.equals(this.infAddress)) {
        return localInetAddress1;
      }
      try
      {
        NetworkInterface localNetworkInterface = NetworkInterface.getByInetAddress(localInetAddress1);
        Enumeration localEnumeration = localNetworkInterface.getInetAddresses();
        while (localEnumeration.hasMoreElements())
        {
          InetAddress localInetAddress2 = (InetAddress)localEnumeration.nextElement();
          if (localInetAddress2.equals(this.infAddress)) {
            return this.infAddress;
          }
        }
        this.infAddress = null;
        return localInetAddress1;
      }
      catch (Exception localException)
      {
        return localInetAddress1;
      }
    }
  }
  
  public void setNetworkInterface(NetworkInterface paramNetworkInterface)
    throws SocketException
  {
    synchronized (this.infLock)
    {
      getImpl().setOption(31, paramNetworkInterface);
      this.infAddress = null;
      this.interfaceSet = true;
    }
  }
  
  public NetworkInterface getNetworkInterface()
    throws SocketException
  {
    NetworkInterface localNetworkInterface = (NetworkInterface)getImpl().getOption(31);
    if ((localNetworkInterface.getIndex() == 0) || (localNetworkInterface.getIndex() == -1))
    {
      InetAddress[] arrayOfInetAddress = new InetAddress[1];
      arrayOfInetAddress[0] = InetAddress.anyLocalAddress();
      return new NetworkInterface(arrayOfInetAddress[0].getHostName(), 0, arrayOfInetAddress);
    }
    return localNetworkInterface;
  }
  
  public void setLoopbackMode(boolean paramBoolean)
    throws SocketException
  {
    getImpl().setOption(18, Boolean.valueOf(paramBoolean));
  }
  
  public boolean getLoopbackMode()
    throws SocketException
  {
    return ((Boolean)getImpl().getOption(18)).booleanValue();
  }
  
  @Deprecated
  public void send(DatagramPacket paramDatagramPacket, byte paramByte)
    throws IOException
  {
    if (isClosed()) {
      throw new SocketException("Socket is closed");
    }
    checkAddress(paramDatagramPacket.getAddress(), "send");
    synchronized (this.ttlLock)
    {
      synchronized (paramDatagramPacket)
      {
        Object localObject1;
        if (this.connectState == 0)
        {
          localObject1 = System.getSecurityManager();
          if (localObject1 != null) {
            if (paramDatagramPacket.getAddress().isMulticastAddress()) {
              ((SecurityManager)localObject1).checkMulticast(paramDatagramPacket.getAddress(), paramByte);
            } else {
              ((SecurityManager)localObject1).checkConnect(paramDatagramPacket.getAddress().getHostAddress(), paramDatagramPacket.getPort());
            }
          }
        }
        else
        {
          localObject1 = null;
          localObject1 = paramDatagramPacket.getAddress();
          if (localObject1 == null)
          {
            paramDatagramPacket.setAddress(this.connectedAddress);
            paramDatagramPacket.setPort(this.connectedPort);
          }
          else if ((!((InetAddress)localObject1).equals(this.connectedAddress)) || (paramDatagramPacket.getPort() != this.connectedPort))
          {
            throw new SecurityException("connected address and packet address differ");
          }
        }
        byte b = getTTL();
        try
        {
          if (paramByte != b) {
            getImpl().setTTL(paramByte);
          }
          getImpl().send(paramDatagramPacket);
        }
        finally
        {
          if (paramByte != b) {
            getImpl().setTTL(b);
          }
        }
      }
    }
  }
}
