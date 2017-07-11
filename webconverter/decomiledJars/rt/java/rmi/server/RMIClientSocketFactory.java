package java.rmi.server;

import java.io.IOException;
import java.net.Socket;

public abstract interface RMIClientSocketFactory
{
  public abstract Socket createSocket(String paramString, int paramInt)
    throws IOException;
}
