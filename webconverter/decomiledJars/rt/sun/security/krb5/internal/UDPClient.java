package sun.security.krb5.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.UnknownHostException;

class UDPClient
  extends NetClient
{
  InetAddress iaddr;
  int iport;
  int bufSize = 65507;
  DatagramSocket dgSocket;
  DatagramPacket dgPacketIn;
  
  UDPClient(String paramString, int paramInt1, int paramInt2)
    throws UnknownHostException, SocketException
  {
    this.iaddr = InetAddress.getByName(paramString);
    this.iport = paramInt1;
    this.dgSocket = new DatagramSocket();
    this.dgSocket.setSoTimeout(paramInt2);
    this.dgSocket.connect(this.iaddr, this.iport);
  }
  
  public void send(byte[] paramArrayOfByte)
    throws IOException
  {
    DatagramPacket localDatagramPacket = new DatagramPacket(paramArrayOfByte, paramArrayOfByte.length, this.iaddr, this.iport);
    this.dgSocket.send(localDatagramPacket);
  }
  
  public byte[] receive()
    throws IOException
  {
    byte[] arrayOfByte1 = new byte[this.bufSize];
    this.dgPacketIn = new DatagramPacket(arrayOfByte1, arrayOfByte1.length);
    try
    {
      this.dgSocket.receive(this.dgPacketIn);
    }
    catch (SocketException localSocketException)
    {
      if ((localSocketException instanceof PortUnreachableException)) {
        throw localSocketException;
      }
      this.dgSocket.receive(this.dgPacketIn);
    }
    byte[] arrayOfByte2 = new byte[this.dgPacketIn.getLength()];
    System.arraycopy(this.dgPacketIn.getData(), 0, arrayOfByte2, 0, this.dgPacketIn.getLength());
    return arrayOfByte2;
  }
  
  public void close()
  {
    this.dgSocket.close();
  }
}
