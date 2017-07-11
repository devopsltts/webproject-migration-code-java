package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

class ExchangeImpl
{
  Headers reqHdrs;
  Headers rspHdrs;
  Request req;
  String method;
  boolean writefinished;
  URI uri;
  HttpConnection connection;
  long reqContentLen;
  long rspContentLen;
  InputStream ris;
  OutputStream ros;
  Thread thread;
  boolean close;
  boolean closed;
  boolean http10 = false;
  private static final String pattern = "EEE, dd MMM yyyy HH:mm:ss zzz";
  private static final TimeZone gmtTZ = TimeZone.getTimeZone("GMT");
  private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal()
  {
    protected DateFormat initialValue()
    {
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
      localSimpleDateFormat.setTimeZone(ExchangeImpl.gmtTZ);
      return localSimpleDateFormat;
    }
  };
  private static final String HEAD = "HEAD";
  InputStream uis;
  OutputStream uos;
  LeftOverInputStream uis_orig;
  PlaceholderOutputStream uos_orig;
  boolean sentHeaders;
  Map<String, Object> attributes;
  int rcode = -1;
  HttpPrincipal principal;
  ServerImpl server;
  private byte[] rspbuf = new byte[''];
  
  ExchangeImpl(String paramString, URI paramURI, Request paramRequest, long paramLong, HttpConnection paramHttpConnection)
    throws IOException
  {
    this.req = paramRequest;
    this.reqHdrs = paramRequest.headers();
    this.rspHdrs = new Headers();
    this.method = paramString;
    this.uri = paramURI;
    this.connection = paramHttpConnection;
    this.reqContentLen = paramLong;
    this.ros = paramRequest.outputStream();
    this.ris = paramRequest.inputStream();
    this.server = getServerImpl();
    this.server.startExchange();
  }
  
  public Headers getRequestHeaders()
  {
    return new UnmodifiableHeaders(this.reqHdrs);
  }
  
  public Headers getResponseHeaders()
  {
    return this.rspHdrs;
  }
  
  public URI getRequestURI()
  {
    return this.uri;
  }
  
  public String getRequestMethod()
  {
    return this.method;
  }
  
  public HttpContextImpl getHttpContext()
  {
    return this.connection.getHttpContext();
  }
  
  private boolean isHeadRequest()
  {
    return "HEAD".equals(getRequestMethod());
  }
  
  public void close()
  {
    if (this.closed) {
      return;
    }
    this.closed = true;
    try
    {
      if ((this.uis_orig == null) || (this.uos == null))
      {
        this.connection.close();
        return;
      }
      if (!this.uos_orig.isWrapped())
      {
        this.connection.close();
        return;
      }
      if (!this.uis_orig.isClosed()) {
        this.uis_orig.close();
      }
      this.uos.close();
    }
    catch (IOException localIOException)
    {
      this.connection.close();
    }
  }
  
  public InputStream getRequestBody()
  {
    if (this.uis != null) {
      return this.uis;
    }
    if (this.reqContentLen == -1L)
    {
      this.uis_orig = new ChunkedInputStream(this, this.ris);
      this.uis = this.uis_orig;
    }
    else
    {
      this.uis_orig = new FixedLengthInputStream(this, this.ris, this.reqContentLen);
      this.uis = this.uis_orig;
    }
    return this.uis;
  }
  
  LeftOverInputStream getOriginalInputStream()
  {
    return this.uis_orig;
  }
  
  public int getResponseCode()
  {
    return this.rcode;
  }
  
  public OutputStream getResponseBody()
  {
    if (this.uos == null)
    {
      this.uos_orig = new PlaceholderOutputStream(null);
      this.uos = this.uos_orig;
    }
    return this.uos;
  }
  
  PlaceholderOutputStream getPlaceholderResponseBody()
  {
    getResponseBody();
    return this.uos_orig;
  }
  
  public void sendResponseHeaders(int paramInt, long paramLong)
    throws IOException
  {
    if (this.sentHeaders) {
      throw new IOException("headers already sent");
    }
    this.rcode = paramInt;
    String str1 = "HTTP/1.1 " + paramInt + Code.msg(paramInt) + "\r\n";
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(this.ros);
    PlaceholderOutputStream localPlaceholderOutputStream = getPlaceholderResponseBody();
    localBufferedOutputStream.write(bytes(str1, 0), 0, str1.length());
    int i = 0;
    this.rspHdrs.set("Date", ((DateFormat)dateFormat.get()).format(new Date()));
    Object localObject;
    String str2;
    if (((paramInt >= 100) && (paramInt < 200)) || (paramInt == 204) || (paramInt == 304))
    {
      if (paramLong != -1L)
      {
        localObject = this.server.getLogger();
        str2 = "sendResponseHeaders: rCode = " + paramInt + ": forcing contentLen = -1";
        ((Logger)localObject).warning(str2);
      }
      paramLong = -1L;
    }
    if (isHeadRequest())
    {
      if (paramLong >= 0L)
      {
        localObject = this.server.getLogger();
        str2 = "sendResponseHeaders: being invoked with a content length for a HEAD request";
        ((Logger)localObject).warning(str2);
      }
      i = 1;
      paramLong = 0L;
    }
    else if (paramLong == 0L)
    {
      if (this.http10)
      {
        localPlaceholderOutputStream.setWrappedStream(new UndefLengthOutputStream(this, this.ros));
        this.close = true;
      }
      else
      {
        this.rspHdrs.set("Transfer-encoding", "chunked");
        localPlaceholderOutputStream.setWrappedStream(new ChunkedOutputStream(this, this.ros));
      }
    }
    else
    {
      if (paramLong == -1L)
      {
        i = 1;
        paramLong = 0L;
      }
      this.rspHdrs.set("Content-length", Long.toString(paramLong));
      localPlaceholderOutputStream.setWrappedStream(new FixedLengthOutputStream(this, this.ros, paramLong));
    }
    write(this.rspHdrs, localBufferedOutputStream);
    this.rspContentLen = paramLong;
    localBufferedOutputStream.flush();
    localBufferedOutputStream = null;
    this.sentHeaders = true;
    if (i != 0)
    {
      localObject = new WriteFinishedEvent(this);
      this.server.addEvent((Event)localObject);
      this.closed = true;
    }
    this.server.logReply(paramInt, this.req.requestLine(), null);
  }
  
  void write(Headers paramHeaders, OutputStream paramOutputStream)
    throws IOException
  {
    Set localSet = paramHeaders.entrySet();
    Iterator localIterator1 = localSet.iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      String str1 = (String)localEntry.getKey();
      List localList = (List)localEntry.getValue();
      Iterator localIterator2 = localList.iterator();
      while (localIterator2.hasNext())
      {
        String str2 = (String)localIterator2.next();
        int i = str1.length();
        byte[] arrayOfByte = bytes(str1, 2);
        arrayOfByte[(i++)] = 58;
        arrayOfByte[(i++)] = 32;
        paramOutputStream.write(arrayOfByte, 0, i);
        arrayOfByte = bytes(str2, 2);
        i = str2.length();
        arrayOfByte[(i++)] = 13;
        arrayOfByte[(i++)] = 10;
        paramOutputStream.write(arrayOfByte, 0, i);
      }
    }
    paramOutputStream.write(13);
    paramOutputStream.write(10);
  }
  
  private byte[] bytes(String paramString, int paramInt)
  {
    int i = paramString.length();
    if (i + paramInt > this.rspbuf.length)
    {
      int j = i + paramInt - this.rspbuf.length;
      this.rspbuf = new byte[2 * (this.rspbuf.length + j)];
    }
    char[] arrayOfChar = paramString.toCharArray();
    for (int k = 0; k < arrayOfChar.length; k++) {
      this.rspbuf[k] = ((byte)arrayOfChar[k]);
    }
    return this.rspbuf;
  }
  
  public InetSocketAddress getRemoteAddress()
  {
    Socket localSocket = this.connection.getChannel().socket();
    InetAddress localInetAddress = localSocket.getInetAddress();
    int i = localSocket.getPort();
    return new InetSocketAddress(localInetAddress, i);
  }
  
  public InetSocketAddress getLocalAddress()
  {
    Socket localSocket = this.connection.getChannel().socket();
    InetAddress localInetAddress = localSocket.getLocalAddress();
    int i = localSocket.getLocalPort();
    return new InetSocketAddress(localInetAddress, i);
  }
  
  public String getProtocol()
  {
    String str = this.req.requestLine();
    int i = str.lastIndexOf(' ');
    return str.substring(i + 1);
  }
  
  public SSLSession getSSLSession()
  {
    SSLEngine localSSLEngine = this.connection.getSSLEngine();
    if (localSSLEngine == null) {
      return null;
    }
    return localSSLEngine.getSession();
  }
  
  public Object getAttribute(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("null name parameter");
    }
    if (this.attributes == null) {
      this.attributes = getHttpContext().getAttributes();
    }
    return this.attributes.get(paramString);
  }
  
  public void setAttribute(String paramString, Object paramObject)
  {
    if (paramString == null) {
      throw new NullPointerException("null name parameter");
    }
    if (this.attributes == null) {
      this.attributes = getHttpContext().getAttributes();
    }
    this.attributes.put(paramString, paramObject);
  }
  
  public void setStreams(InputStream paramInputStream, OutputStream paramOutputStream)
  {
    assert (this.uis != null);
    if (paramInputStream != null) {
      this.uis = paramInputStream;
    }
    if (paramOutputStream != null) {
      this.uos = paramOutputStream;
    }
  }
  
  HttpConnection getConnection()
  {
    return this.connection;
  }
  
  ServerImpl getServerImpl()
  {
    return getHttpContext().getServerImpl();
  }
  
  public HttpPrincipal getPrincipal()
  {
    return this.principal;
  }
  
  void setPrincipal(HttpPrincipal paramHttpPrincipal)
  {
    this.principal = paramHttpPrincipal;
  }
  
  static ExchangeImpl get(HttpExchange paramHttpExchange)
  {
    if ((paramHttpExchange instanceof HttpExchangeImpl)) {
      return ((HttpExchangeImpl)paramHttpExchange).getExchangeImpl();
    }
    assert ((paramHttpExchange instanceof HttpsExchangeImpl));
    return ((HttpsExchangeImpl)paramHttpExchange).getExchangeImpl();
  }
}
