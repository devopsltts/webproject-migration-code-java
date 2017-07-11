package com.sun.xml.internal.ws.transport.http.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.client.ClientTransportException;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.transport.Headers;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;

public class HttpClientTransport
{
  private static final byte[] THROW_AWAY_BUFFER = new byte[' '];
  int statusCode;
  String statusMessage;
  int contentLength;
  private final Map<String, List<String>> reqHeaders;
  private Map<String, List<String>> respHeaders = null;
  private OutputStream outputStream;
  private boolean https;
  private HttpURLConnection httpConnection = null;
  private final EndpointAddress endpoint;
  private final Packet context;
  private final Integer chunkSize;
  
  public HttpClientTransport(@NotNull Packet paramPacket, @NotNull Map<String, List<String>> paramMap)
  {
    this.endpoint = paramPacket.endpointAddress;
    this.context = paramPacket;
    this.reqHeaders = paramMap;
    this.chunkSize = ((Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size"));
  }
  
  OutputStream getOutput()
  {
    try
    {
      createHttpConnection();
      if (requiresOutputStream())
      {
        this.outputStream = this.httpConnection.getOutputStream();
        if (this.chunkSize != null) {
          this.outputStream = new WSChunkedOuputStream(this.outputStream, this.chunkSize.intValue());
        }
        List localList = (List)this.reqHeaders.get("Content-Encoding");
        if ((localList != null) && (((String)localList.get(0)).contains("gzip"))) {
          this.outputStream = new GZIPOutputStream(this.outputStream);
        }
      }
      this.httpConnection.connect();
    }
    catch (Exception localException)
    {
      throw new ClientTransportException(ClientMessages.localizableHTTP_CLIENT_FAILED(localException), localException);
    }
    return this.outputStream;
  }
  
  void closeOutput()
    throws IOException
  {
    if (this.outputStream != null)
    {
      this.outputStream.close();
      this.outputStream = null;
    }
  }
  
  @Nullable
  InputStream getInput()
  {
    Object localObject;
    try
    {
      localObject = readResponse();
      if (localObject != null)
      {
        String str = this.httpConnection.getContentEncoding();
        if ((str != null) && (str.contains("gzip"))) {
          localObject = new GZIPInputStream((InputStream)localObject);
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(Integer.valueOf(this.statusCode), this.statusMessage), localIOException);
    }
    return localObject;
  }
  
  public Map<String, List<String>> getHeaders()
  {
    if (this.respHeaders != null) {
      return this.respHeaders;
    }
    this.respHeaders = new Headers();
    this.respHeaders.putAll(this.httpConnection.getHeaderFields());
    return this.respHeaders;
  }
  
  @Nullable
  protected InputStream readResponse()
  {
    InputStream localInputStream1;
    try
    {
      localInputStream1 = this.httpConnection.getInputStream();
    }
    catch (IOException localIOException)
    {
      localInputStream1 = this.httpConnection.getErrorStream();
    }
    if (localInputStream1 == null) {
      return localInputStream1;
    }
    final InputStream localInputStream2 = localInputStream1;
    new FilterInputStream(localInputStream2)
    {
      boolean closed;
      
      public void close()
        throws IOException
      {
        if (!this.closed)
        {
          this.closed = true;
          while (localInputStream2.read(HttpClientTransport.THROW_AWAY_BUFFER) != -1) {}
          super.close();
        }
      }
    };
  }
  
  protected void readResponseCodeAndMessage()
  {
    try
    {
      this.statusCode = this.httpConnection.getResponseCode();
      this.statusMessage = this.httpConnection.getResponseMessage();
      this.contentLength = this.httpConnection.getContentLength();
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
  }
  
  protected HttpURLConnection openConnection(Packet paramPacket)
  {
    return null;
  }
  
  protected boolean checkHTTPS(HttpURLConnection paramHttpURLConnection)
  {
    if ((paramHttpURLConnection instanceof HttpsURLConnection))
    {
      String str = (String)this.context.invocationProperties.get("com.sun.xml.internal.ws.client.http.HostnameVerificationProperty");
      if ((str != null) && (str.equalsIgnoreCase("true"))) {
        ((HttpsURLConnection)paramHttpURLConnection).setHostnameVerifier(new HttpClientVerifier(null));
      }
      HostnameVerifier localHostnameVerifier = (HostnameVerifier)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.https.client.hostname.verifier");
      if (localHostnameVerifier != null) {
        ((HttpsURLConnection)paramHttpURLConnection).setHostnameVerifier(localHostnameVerifier);
      }
      SSLSocketFactory localSSLSocketFactory = (SSLSocketFactory)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory");
      if (localSSLSocketFactory != null) {
        ((HttpsURLConnection)paramHttpURLConnection).setSSLSocketFactory(localSSLSocketFactory);
      }
      return true;
    }
    return false;
  }
  
  private void createHttpConnection()
    throws IOException
  {
    this.httpConnection = openConnection(this.context);
    if (this.httpConnection == null) {
      this.httpConnection = ((HttpURLConnection)this.endpoint.openConnection());
    }
    String str1 = this.endpoint.getURI().getScheme();
    if (str1.equals("https")) {
      this.https = true;
    }
    if (checkHTTPS(this.httpConnection)) {
      this.https = true;
    }
    this.httpConnection.setAllowUserInteraction(true);
    this.httpConnection.setDoOutput(true);
    this.httpConnection.setDoInput(true);
    String str2 = (String)this.context.invocationProperties.get("javax.xml.ws.http.request.method");
    String str3 = str2 != null ? str2 : "POST";
    this.httpConnection.setRequestMethod(str3);
    Integer localInteger1 = (Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.request.timeout");
    if (localInteger1 != null) {
      this.httpConnection.setReadTimeout(localInteger1.intValue());
    }
    Integer localInteger2 = (Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.connect.timeout");
    if (localInteger2 != null) {
      this.httpConnection.setConnectTimeout(localInteger2.intValue());
    }
    Integer localInteger3 = (Integer)this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size");
    if (localInteger3 != null) {
      this.httpConnection.setChunkedStreamingMode(localInteger3.intValue());
    }
    Iterator localIterator1 = this.reqHeaders.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      if (!"Content-Length".equals(localEntry.getKey()))
      {
        Iterator localIterator2 = ((List)localEntry.getValue()).iterator();
        while (localIterator2.hasNext())
        {
          String str4 = (String)localIterator2.next();
          this.httpConnection.addRequestProperty((String)localEntry.getKey(), str4);
        }
      }
    }
  }
  
  boolean isSecure()
  {
    return this.https;
  }
  
  protected void setStatusCode(int paramInt)
  {
    this.statusCode = paramInt;
  }
  
  private boolean requiresOutputStream()
  {
    return (!this.httpConnection.getRequestMethod().equalsIgnoreCase("GET")) && (!this.httpConnection.getRequestMethod().equalsIgnoreCase("HEAD")) && (!this.httpConnection.getRequestMethod().equalsIgnoreCase("DELETE"));
  }
  
  @Nullable
  String getContentType()
  {
    return this.httpConnection.getContentType();
  }
  
  public int getContentLength()
  {
    return this.httpConnection.getContentLength();
  }
  
  static
  {
    try
    {
      JAXBContext.newInstance(new Class[0]).createUnmarshaller();
    }
    catch (JAXBException localJAXBException) {}
  }
  
  private static class HttpClientVerifier
    implements HostnameVerifier
  {
    private HttpClientVerifier() {}
    
    public boolean verify(String paramString, SSLSession paramSSLSession)
    {
      return true;
    }
  }
  
  private static class LocalhostHttpClientVerifier
    implements HostnameVerifier
  {
    private LocalhostHttpClientVerifier() {}
    
    public boolean verify(String paramString, SSLSession paramSSLSession)
    {
      return ("localhost".equalsIgnoreCase(paramString)) || ("127.0.0.1".equals(paramString));
    }
  }
  
  private static final class WSChunkedOuputStream
    extends FilterOutputStream
  {
    final int chunkSize;
    
    WSChunkedOuputStream(OutputStream paramOutputStream, int paramInt)
    {
      super();
      this.chunkSize = paramInt;
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      while (paramInt2 > 0)
      {
        int i = paramInt2 > this.chunkSize ? this.chunkSize : paramInt2;
        this.out.write(paramArrayOfByte, paramInt1, i);
        paramInt2 -= i;
        paramInt1 += i;
      }
    }
  }
}
