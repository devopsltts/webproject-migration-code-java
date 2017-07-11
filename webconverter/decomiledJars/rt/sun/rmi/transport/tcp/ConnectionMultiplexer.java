package sun.rmi.transport.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.server.LogStream;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.Hashtable;
import sun.rmi.runtime.Log;
import sun.rmi.transport.Connection;
import sun.security.action.GetPropertyAction;

final class ConnectionMultiplexer
{
  static int logLevel = LogStream.parseLevel(getLogLevel());
  static final Log multiplexLog = Log.getLog("sun.rmi.transport.tcp.multiplex", "multiplex", logLevel);
  private static final int OPEN = 225;
  private static final int CLOSE = 226;
  private static final int CLOSEACK = 227;
  private static final int REQUEST = 228;
  private static final int TRANSMIT = 229;
  private TCPChannel channel;
  private InputStream in;
  private OutputStream out;
  private boolean orig;
  private DataInputStream dataIn;
  private DataOutputStream dataOut;
  private Hashtable<Integer, MultiplexConnectionInfo> connectionTable = new Hashtable(7);
  private int numConnections = 0;
  private static final int maxConnections = 256;
  private int lastID = 4097;
  private boolean alive = true;
  
  private static String getLogLevel()
  {
    return (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.tcp.multiplex.logLevel"));
  }
  
  public ConnectionMultiplexer(TCPChannel paramTCPChannel, InputStream paramInputStream, OutputStream paramOutputStream, boolean paramBoolean)
  {
    this.channel = paramTCPChannel;
    this.in = paramInputStream;
    this.out = paramOutputStream;
    this.orig = paramBoolean;
    this.dataIn = new DataInputStream(paramInputStream);
    this.dataOut = new DataOutputStream(paramOutputStream);
  }
  
  public void run()
    throws IOException
  {
    try
    {
      int i;
      for (;;)
      {
        i = this.dataIn.readUnsignedByte();
        int j;
        MultiplexConnectionInfo localMultiplexConnectionInfo;
        int k;
        switch (i)
        {
        case 225: 
          j = this.dataIn.readUnsignedShort();
          if (multiplexLog.isLoggable(Log.VERBOSE)) {
            multiplexLog.log(Log.VERBOSE, "operation  OPEN " + j);
          }
          localMultiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(Integer.valueOf(j));
          if (localMultiplexConnectionInfo != null) {
            throw new IOException("OPEN: Connection ID already exists");
          }
          localMultiplexConnectionInfo = new MultiplexConnectionInfo(j);
          localMultiplexConnectionInfo.in = new MultiplexInputStream(this, localMultiplexConnectionInfo, 2048);
          localMultiplexConnectionInfo.out = new MultiplexOutputStream(this, localMultiplexConnectionInfo, 2048);
          synchronized (this.connectionTable)
          {
            this.connectionTable.put(Integer.valueOf(j), localMultiplexConnectionInfo);
            this.numConnections += 1;
          }
          ??? = new TCPConnection(this.channel, localMultiplexConnectionInfo.in, localMultiplexConnectionInfo.out);
          this.channel.acceptMultiplexConnection((Connection)???);
          break;
        case 226: 
          j = this.dataIn.readUnsignedShort();
          if (multiplexLog.isLoggable(Log.VERBOSE)) {
            multiplexLog.log(Log.VERBOSE, "operation  CLOSE " + j);
          }
          localMultiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(Integer.valueOf(j));
          if (localMultiplexConnectionInfo == null) {
            throw new IOException("CLOSE: Invalid connection ID");
          }
          localMultiplexConnectionInfo.in.disconnect();
          localMultiplexConnectionInfo.out.disconnect();
          if (!localMultiplexConnectionInfo.closed) {
            sendCloseAck(localMultiplexConnectionInfo);
          }
          synchronized (this.connectionTable)
          {
            this.connectionTable.remove(Integer.valueOf(j));
            this.numConnections -= 1;
          }
          break;
        case 227: 
          j = this.dataIn.readUnsignedShort();
          if (multiplexLog.isLoggable(Log.VERBOSE)) {
            multiplexLog.log(Log.VERBOSE, "operation  CLOSEACK " + j);
          }
          localMultiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(Integer.valueOf(j));
          if (localMultiplexConnectionInfo == null) {
            throw new IOException("CLOSEACK: Invalid connection ID");
          }
          if (!localMultiplexConnectionInfo.closed) {
            throw new IOException("CLOSEACK: Connection not closed");
          }
          localMultiplexConnectionInfo.in.disconnect();
          localMultiplexConnectionInfo.out.disconnect();
          synchronized (this.connectionTable)
          {
            this.connectionTable.remove(Integer.valueOf(j));
            this.numConnections -= 1;
          }
          break;
        case 228: 
          j = this.dataIn.readUnsignedShort();
          localMultiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(Integer.valueOf(j));
          if (localMultiplexConnectionInfo == null) {
            throw new IOException("REQUEST: Invalid connection ID");
          }
          k = this.dataIn.readInt();
          if (multiplexLog.isLoggable(Log.VERBOSE)) {
            multiplexLog.log(Log.VERBOSE, "operation  REQUEST " + j + ": " + k);
          }
          localMultiplexConnectionInfo.out.request(k);
          break;
        case 229: 
          j = this.dataIn.readUnsignedShort();
          localMultiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(Integer.valueOf(j));
          if (localMultiplexConnectionInfo == null) {
            throw new IOException("SEND: Invalid connection ID");
          }
          k = this.dataIn.readInt();
          if (multiplexLog.isLoggable(Log.VERBOSE)) {
            multiplexLog.log(Log.VERBOSE, "operation  TRANSMIT " + j + ": " + k);
          }
          localMultiplexConnectionInfo.in.receive(k, this.dataIn);
        }
      }
      throw new IOException("Invalid operation: " + Integer.toHexString(i));
    }
    finally
    {
      shutDown();
    }
  }
  
  public synchronized TCPConnection openConnection()
    throws IOException
  {
    int i;
    do
    {
      this.lastID = (++this.lastID & 0x7FFF);
      i = this.lastID;
      if (this.orig) {
        i |= 0x8000;
      }
    } while (this.connectionTable.get(Integer.valueOf(i)) != null);
    MultiplexConnectionInfo localMultiplexConnectionInfo = new MultiplexConnectionInfo(i);
    localMultiplexConnectionInfo.in = new MultiplexInputStream(this, localMultiplexConnectionInfo, 2048);
    localMultiplexConnectionInfo.out = new MultiplexOutputStream(this, localMultiplexConnectionInfo, 2048);
    synchronized (this.connectionTable)
    {
      if (!this.alive) {
        throw new IOException("Multiplexer connection dead");
      }
      if (this.numConnections >= 256) {
        throw new IOException("Cannot exceed 256 simultaneous multiplexed connections");
      }
      this.connectionTable.put(Integer.valueOf(i), localMultiplexConnectionInfo);
      this.numConnections += 1;
    }
    synchronized (this.dataOut)
    {
      try
      {
        this.dataOut.writeByte(225);
        this.dataOut.writeShort(i);
        this.dataOut.flush();
      }
      catch (IOException localIOException)
      {
        multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
        shutDown();
        throw localIOException;
      }
    }
    return new TCPConnection(this.channel, localMultiplexConnectionInfo.in, localMultiplexConnectionInfo.out);
  }
  
  public void shutDown()
  {
    synchronized (this.connectionTable)
    {
      if (!this.alive) {
        return;
      }
      this.alive = false;
      Enumeration localEnumeration = this.connectionTable.elements();
      while (localEnumeration.hasMoreElements())
      {
        MultiplexConnectionInfo localMultiplexConnectionInfo = (MultiplexConnectionInfo)localEnumeration.nextElement();
        localMultiplexConnectionInfo.in.disconnect();
        localMultiplexConnectionInfo.out.disconnect();
      }
      this.connectionTable.clear();
      this.numConnections = 0;
    }
    try
    {
      this.in.close();
    }
    catch (IOException localIOException1) {}
    try
    {
      this.out.close();
    }
    catch (IOException localIOException2) {}
  }
  
  void sendRequest(MultiplexConnectionInfo paramMultiplexConnectionInfo, int paramInt)
    throws IOException
  {
    synchronized (this.dataOut)
    {
      if ((this.alive) && (!paramMultiplexConnectionInfo.closed)) {
        try
        {
          this.dataOut.writeByte(228);
          this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
          this.dataOut.writeInt(paramInt);
          this.dataOut.flush();
        }
        catch (IOException localIOException)
        {
          multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
          shutDown();
          throw localIOException;
        }
      }
    }
  }
  
  void sendTransmit(MultiplexConnectionInfo paramMultiplexConnectionInfo, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (this.dataOut)
    {
      if ((this.alive) && (!paramMultiplexConnectionInfo.closed)) {
        try
        {
          this.dataOut.writeByte(229);
          this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
          this.dataOut.writeInt(paramInt2);
          this.dataOut.write(paramArrayOfByte, paramInt1, paramInt2);
          this.dataOut.flush();
        }
        catch (IOException localIOException)
        {
          multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
          shutDown();
          throw localIOException;
        }
      }
    }
  }
  
  void sendClose(MultiplexConnectionInfo paramMultiplexConnectionInfo)
    throws IOException
  {
    paramMultiplexConnectionInfo.out.disconnect();
    synchronized (this.dataOut)
    {
      if ((this.alive) && (!paramMultiplexConnectionInfo.closed)) {
        try
        {
          this.dataOut.writeByte(226);
          this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
          this.dataOut.flush();
          paramMultiplexConnectionInfo.closed = true;
        }
        catch (IOException localIOException)
        {
          multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
          shutDown();
          throw localIOException;
        }
      }
    }
  }
  
  void sendCloseAck(MultiplexConnectionInfo paramMultiplexConnectionInfo)
    throws IOException
  {
    synchronized (this.dataOut)
    {
      if ((this.alive) && (!paramMultiplexConnectionInfo.closed)) {
        try
        {
          this.dataOut.writeByte(227);
          this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
          this.dataOut.flush();
          paramMultiplexConnectionInfo.closed = true;
        }
        catch (IOException localIOException)
        {
          multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
          shutDown();
          throw localIOException;
        }
      }
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    super.finalize();
    shutDown();
  }
}
