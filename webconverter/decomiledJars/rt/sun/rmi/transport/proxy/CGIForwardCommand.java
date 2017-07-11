package sun.rmi.transport.proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

final class CGIForwardCommand
  implements CGICommandHandler
{
  CGIForwardCommand() {}
  
  public String getName()
  {
    return "forward";
  }
  
  private String getLine(DataInputStream paramDataInputStream)
    throws IOException
  {
    return paramDataInputStream.readLine();
  }
  
  public void execute(String paramString)
    throws CGIClientException, CGIServerException
  {
    if (!CGIHandler.RequestMethod.equals("POST")) {
      throw new CGIClientException("can only forward POST requests");
    }
    int i;
    try
    {
      i = Integer.parseInt(paramString);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new CGIClientException("invalid port number.", localNumberFormatException);
    }
    if ((i <= 0) || (i > 65535)) {
      throw new CGIClientException("invalid port: " + i);
    }
    if (i < 1024) {
      throw new CGIClientException("permission denied for port: " + i);
    }
    Socket localSocket;
    try
    {
      localSocket = new Socket(InetAddress.getLocalHost(), i);
    }
    catch (IOException localIOException1)
    {
      throw new CGIServerException("could not connect to local port", localIOException1);
    }
    DataInputStream localDataInputStream1 = new DataInputStream(System.in);
    byte[] arrayOfByte = new byte[CGIHandler.ContentLength];
    try
    {
      localDataInputStream1.readFully(arrayOfByte);
    }
    catch (EOFException localEOFException1)
    {
      throw new CGIClientException("unexpected EOF reading request body", localEOFException1);
    }
    catch (IOException localIOException2)
    {
      throw new CGIClientException("error reading request body", localIOException2);
    }
    try
    {
      DataOutputStream localDataOutputStream = new DataOutputStream(localSocket.getOutputStream());
      localDataOutputStream.writeBytes("POST / HTTP/1.0\r\n");
      localDataOutputStream.writeBytes("Content-length: " + CGIHandler.ContentLength + "\r\n\r\n");
      localDataOutputStream.write(arrayOfByte);
      localDataOutputStream.flush();
    }
    catch (IOException localIOException3)
    {
      throw new CGIServerException("error writing to server", localIOException3);
    }
    DataInputStream localDataInputStream2;
    try
    {
      localDataInputStream2 = new DataInputStream(localSocket.getInputStream());
    }
    catch (IOException localIOException4)
    {
      throw new CGIServerException("error reading from server", localIOException4);
    }
    String str1 = "Content-length:".toLowerCase();
    int j = 0;
    int k = -1;
    String str2;
    do
    {
      try
      {
        str2 = getLine(localDataInputStream2);
      }
      catch (IOException localIOException5)
      {
        throw new CGIServerException("error reading from server", localIOException5);
      }
      if (str2 == null) {
        throw new CGIServerException("unexpected EOF reading server response");
      }
      if (str2.toLowerCase().startsWith(str1))
      {
        if (j != 0) {
          throw new CGIServerException("Multiple Content-length entries found.");
        }
        k = Integer.parseInt(str2.substring(str1.length()).trim());
        j = 1;
      }
    } while ((str2.length() != 0) && (str2.charAt(0) != '\r') && (str2.charAt(0) != '\n'));
    if ((j == 0) || (k < 0)) {
      throw new CGIServerException("missing or invalid content length in server response");
    }
    arrayOfByte = new byte[k];
    try
    {
      localDataInputStream2.readFully(arrayOfByte);
    }
    catch (EOFException localEOFException2)
    {
      throw new CGIServerException("unexpected EOF reading server response", localEOFException2);
    }
    catch (IOException localIOException6)
    {
      throw new CGIServerException("error reading from server", localIOException6);
    }
    System.out.println("Status: 200 OK");
    System.out.println("Content-type: application/octet-stream");
    System.out.println("");
    try
    {
      System.out.write(arrayOfByte);
    }
    catch (IOException localIOException7)
    {
      throw new CGIServerException("error writing response", localIOException7);
    }
    System.out.flush();
  }
}
