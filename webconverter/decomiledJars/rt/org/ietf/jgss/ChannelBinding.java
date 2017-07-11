package org.ietf.jgss;

import java.net.InetAddress;
import java.util.Arrays;

public class ChannelBinding
{
  private InetAddress initiator;
  private InetAddress acceptor;
  private byte[] appData;
  
  public ChannelBinding(InetAddress paramInetAddress1, InetAddress paramInetAddress2, byte[] paramArrayOfByte)
  {
    this.initiator = paramInetAddress1;
    this.acceptor = paramInetAddress2;
    if (paramArrayOfByte != null)
    {
      this.appData = new byte[paramArrayOfByte.length];
      System.arraycopy(paramArrayOfByte, 0, this.appData, 0, paramArrayOfByte.length);
    }
  }
  
  public ChannelBinding(byte[] paramArrayOfByte)
  {
    this(null, null, paramArrayOfByte);
  }
  
  public InetAddress getInitiatorAddress()
  {
    return this.initiator;
  }
  
  public InetAddress getAcceptorAddress()
  {
    return this.acceptor;
  }
  
  public byte[] getApplicationData()
  {
    if (this.appData == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[this.appData.length];
    System.arraycopy(this.appData, 0, arrayOfByte, 0, this.appData.length);
    return arrayOfByte;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ChannelBinding)) {
      return false;
    }
    ChannelBinding localChannelBinding = (ChannelBinding)paramObject;
    if (((this.initiator != null) && (localChannelBinding.initiator == null)) || ((this.initiator == null) && (localChannelBinding.initiator != null))) {
      return false;
    }
    if ((this.initiator != null) && (!this.initiator.equals(localChannelBinding.initiator))) {
      return false;
    }
    if (((this.acceptor != null) && (localChannelBinding.acceptor == null)) || ((this.acceptor == null) && (localChannelBinding.acceptor != null))) {
      return false;
    }
    if ((this.acceptor != null) && (!this.acceptor.equals(localChannelBinding.acceptor))) {
      return false;
    }
    return Arrays.equals(this.appData, localChannelBinding.appData);
  }
  
  public int hashCode()
  {
    if (this.initiator != null) {
      return this.initiator.hashCode();
    }
    if (this.acceptor != null) {
      return this.acceptor.hashCode();
    }
    if (this.appData != null) {
      return new String(this.appData).hashCode();
    }
    return 1;
  }
}
