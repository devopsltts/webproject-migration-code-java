package java.net;

import java.io.IOException;
import sun.net.sdp.SdpSupport;

class SdpSocketImpl
  extends PlainSocketImpl
{
  SdpSocketImpl() {}
  
  protected void create(boolean paramBoolean)
    throws IOException
  {
    if (!paramBoolean) {
      throw new UnsupportedOperationException("Must be a stream socket");
    }
    this.fd = SdpSupport.createSocket();
    if (this.socket != null) {
      this.socket.setCreated();
    }
    if (this.serverSocket != null) {
      this.serverSocket.setCreated();
    }
  }
}
