package com.sun.xml.internal.ws.transport.http;

import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.NonAnonymousResponseProcessor;
import com.sun.xml.internal.ws.api.ha.HaInfo;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.Packet.State;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.server.AbstractServerAsyncTransport;
import com.sun.xml.internal.ws.api.server.Adapter;
import com.sun.xml.internal.ws.api.server.Adapter.Toolkit;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSEndpoint.CompletionCallback;
import com.sun.xml.internal.ws.api.server.WSEndpoint.PipeHead;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.Pool;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPBinding;

public class HttpAdapter
  extends Adapter<HttpToolkit>
{
  private static final Logger LOGGER;
  protected Map<String, SDDocument> wsdls;
  private Map<SDDocument, String> revWsdls;
  private ServiceDefinition serviceDefinition = null;
  public final HttpAdapterList<? extends HttpAdapter> owner;
  public final String urlPattern;
  protected boolean stickyCookie;
  protected boolean disableJreplicaCookie = false;
  public static final CompletionCallback NO_OP_COMPLETION_CALLBACK;
  public static volatile boolean dump;
  public static volatile int dump_threshold;
  public static volatile boolean publishStatusPage;
  
  public static HttpAdapter createAlone(WSEndpoint paramWSEndpoint)
  {
    return new DummyList(null).createAdapter("", "", paramWSEndpoint);
  }
  
  /**
   * @deprecated
   */
  protected HttpAdapter(WSEndpoint paramWSEndpoint, HttpAdapterList<? extends HttpAdapter> paramHttpAdapterList)
  {
    this(paramWSEndpoint, paramHttpAdapterList, null);
  }
  
  protected HttpAdapter(WSEndpoint paramWSEndpoint, HttpAdapterList<? extends HttpAdapter> paramHttpAdapterList, String paramString)
  {
    super(paramWSEndpoint);
    this.owner = paramHttpAdapterList;
    this.urlPattern = paramString;
    initWSDLMap(paramWSEndpoint.getServiceDefinition());
  }
  
  public ServiceDefinition getServiceDefinition()
  {
    return this.serviceDefinition;
  }
  
  public final void initWSDLMap(ServiceDefinition paramServiceDefinition)
  {
    this.serviceDefinition = paramServiceDefinition;
    if (paramServiceDefinition == null)
    {
      this.wsdls = Collections.emptyMap();
      this.revWsdls = Collections.emptyMap();
    }
    else
    {
      this.wsdls = new HashMap();
      TreeMap localTreeMap = new TreeMap();
      Iterator localIterator1 = paramServiceDefinition.iterator();
      while (localIterator1.hasNext())
      {
        SDDocument localSDDocument1 = (SDDocument)localIterator1.next();
        if (localSDDocument1 == paramServiceDefinition.getPrimary())
        {
          this.wsdls.put("wsdl", localSDDocument1);
          this.wsdls.put("WSDL", localSDDocument1);
        }
        else
        {
          localTreeMap.put(localSDDocument1.getURL().toString(), localSDDocument1);
        }
      }
      int i = 1;
      int j = 1;
      Iterator localIterator2 = localTreeMap.entrySet().iterator();
      Map.Entry localEntry;
      while (localIterator2.hasNext())
      {
        localEntry = (Map.Entry)localIterator2.next();
        SDDocument localSDDocument2 = (SDDocument)localEntry.getValue();
        if (localSDDocument2.isWSDL()) {
          this.wsdls.put("wsdl=" + i++, localSDDocument2);
        }
        if (localSDDocument2.isSchema()) {
          this.wsdls.put("xsd=" + j++, localSDDocument2);
        }
      }
      this.revWsdls = new HashMap();
      localIterator2 = this.wsdls.entrySet().iterator();
      while (localIterator2.hasNext())
      {
        localEntry = (Map.Entry)localIterator2.next();
        if (!((String)localEntry.getKey()).equals("WSDL")) {
          this.revWsdls.put(localEntry.getValue(), localEntry.getKey());
        }
      }
    }
  }
  
  public String getValidPath()
  {
    if (this.urlPattern.endsWith("/*")) {
      return this.urlPattern.substring(0, this.urlPattern.length() - 2);
    }
    return this.urlPattern;
  }
  
  protected HttpToolkit createToolkit()
  {
    return new HttpToolkit();
  }
  
  public void handle(@NotNull WSHTTPConnection paramWSHTTPConnection)
    throws IOException
  {
    if (handleGet(paramWSHTTPConnection)) {
      return;
    }
    Pool localPool = getPool();
    HttpToolkit localHttpToolkit = (HttpToolkit)localPool.take();
    try
    {
      localHttpToolkit.handle(paramWSHTTPConnection);
    }
    finally
    {
      localPool.recycle(localHttpToolkit);
    }
  }
  
  public boolean handleGet(@NotNull WSHTTPConnection paramWSHTTPConnection)
    throws IOException
  {
    Object localObject1;
    Object localObject2;
    if (paramWSHTTPConnection.getRequestMethod().equals("GET"))
    {
      localObject1 = this.endpoint.getComponents().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Component)((Iterator)localObject1).next();
        HttpMetadataPublisher localHttpMetadataPublisher = (HttpMetadataPublisher)((Component)localObject2).getSPI(HttpMetadataPublisher.class);
        if ((localHttpMetadataPublisher != null) && (localHttpMetadataPublisher.handleMetadataRequest(this, paramWSHTTPConnection))) {
          return true;
        }
      }
      if (isMetadataQuery(paramWSHTTPConnection.getQueryString()))
      {
        publishWSDL(paramWSHTTPConnection);
        return true;
      }
      localObject1 = getEndpoint().getBinding();
      if (!(localObject1 instanceof HTTPBinding))
      {
        writeWebServicesHtmlPage(paramWSHTTPConnection);
        return true;
      }
    }
    else if (paramWSHTTPConnection.getRequestMethod().equals("HEAD"))
    {
      paramWSHTTPConnection.getInput().close();
      localObject1 = getEndpoint().getBinding();
      if (isMetadataQuery(paramWSHTTPConnection.getQueryString()))
      {
        localObject2 = (SDDocument)this.wsdls.get(paramWSHTTPConnection.getQueryString());
        paramWSHTTPConnection.setStatus(localObject2 != null ? 200 : 404);
        paramWSHTTPConnection.getOutput().close();
        paramWSHTTPConnection.close();
        return true;
      }
      if (!(localObject1 instanceof HTTPBinding))
      {
        paramWSHTTPConnection.setStatus(404);
        paramWSHTTPConnection.getOutput().close();
        paramWSHTTPConnection.close();
        return true;
      }
    }
    return false;
  }
  
  private Packet decodePacket(@NotNull WSHTTPConnection paramWSHTTPConnection, @NotNull Codec paramCodec)
    throws IOException
  {
    String str = paramWSHTTPConnection.getRequestHeader("Content-Type");
    InputStream localInputStream = paramWSHTTPConnection.getInput();
    Packet localPacket = new Packet();
    localPacket.soapAction = fixQuotesAroundSoapAction(paramWSHTTPConnection.getRequestHeader("SOAPAction"));
    localPacket.wasTransportSecure = paramWSHTTPConnection.isSecure();
    localPacket.acceptableMimeTypes = paramWSHTTPConnection.getRequestHeader("Accept");
    localPacket.addSatellite(paramWSHTTPConnection);
    addSatellites(localPacket);
    localPacket.isAdapterDeliversNonAnonymousResponse = true;
    localPacket.component = this;
    localPacket.transportBackChannel = new Oneway(paramWSHTTPConnection);
    localPacket.webServiceContextDelegate = paramWSHTTPConnection.getWebServiceContextDelegate();
    localPacket.setState(Packet.State.ServerRequest);
    if ((dump) || (LOGGER.isLoggable(Level.FINER)))
    {
      ByteArrayBuffer localByteArrayBuffer = new ByteArrayBuffer();
      localByteArrayBuffer.write(localInputStream);
      localInputStream.close();
      dump(localByteArrayBuffer, "HTTP request", paramWSHTTPConnection.getRequestHeaders());
      localInputStream = localByteArrayBuffer.newInputStream();
    }
    paramCodec.decode(localInputStream, str, localPacket);
    return localPacket;
  }
  
  protected void addSatellites(Packet paramPacket) {}
  
  public static String fixQuotesAroundSoapAction(String paramString)
  {
    if ((paramString != null) && ((!paramString.startsWith("\"")) || (!paramString.endsWith("\""))))
    {
      if (LOGGER.isLoggable(Level.INFO)) {
        LOGGER.log(Level.INFO, "Received WS-I BP non-conformant Unquoted SoapAction HTTP header: {0}", paramString);
      }
      String str = paramString;
      if (!paramString.startsWith("\"")) {
        str = "\"" + str;
      }
      if (!paramString.endsWith("\"")) {
        str = str + "\"";
      }
      return str;
    }
    return paramString;
  }
  
  protected NonAnonymousResponseProcessor getNonAnonymousResponseProcessor()
  {
    return NonAnonymousResponseProcessor.getDefault();
  }
  
  protected void writeClientError(int paramInt, @NotNull OutputStream paramOutputStream, @NotNull Packet paramPacket)
    throws IOException
  {}
  
  private boolean isClientErrorStatus(int paramInt)
  {
    return paramInt == 403;
  }
  
  private boolean isNonAnonymousUri(EndpointAddress paramEndpointAddress)
  {
    return (paramEndpointAddress != null) && (!paramEndpointAddress.toString().equals(AddressingVersion.W3C.anonymousUri)) && (!paramEndpointAddress.toString().equals(AddressingVersion.MEMBER.anonymousUri));
  }
  
  private void encodePacket(@NotNull Packet paramPacket, @NotNull WSHTTPConnection paramWSHTTPConnection, @NotNull Codec paramCodec)
    throws IOException
  {
    Object localObject1;
    Object localObject2;
    if ((isNonAnonymousUri(paramPacket.endpointAddress)) && (paramPacket.getMessage() != null)) {
      try
      {
        paramPacket = getNonAnonymousResponseProcessor().process(paramPacket);
      }
      catch (RuntimeException localRuntimeException)
      {
        localObject1 = paramPacket.getBinding().getSOAPVersion();
        localObject2 = SOAPFaultBuilder.createSOAPFaultMessage((SOAPVersion)localObject1, null, localRuntimeException);
        paramPacket = paramPacket.createServerResponse((Message)localObject2, paramPacket.endpoint.getPort(), null, paramPacket.endpoint.getBinding());
      }
    }
    if (paramWSHTTPConnection.isClosed()) {
      return;
    }
    Message localMessage = paramPacket.getMessage();
    addStickyCookie(paramWSHTTPConnection);
    addReplicaCookie(paramWSHTTPConnection, paramPacket);
    if (localMessage == null)
    {
      if (!paramWSHTTPConnection.isClosed())
      {
        if (paramWSHTTPConnection.getStatus() == 0) {
          paramWSHTTPConnection.setStatus(202);
        }
        localObject1 = paramWSHTTPConnection.getProtocol().contains("1.1") ? paramWSHTTPConnection.getOutput() : new Http10OutputStream(paramWSHTTPConnection);
        if ((dump) || (LOGGER.isLoggable(Level.FINER)))
        {
          localObject2 = new ByteArrayBuffer();
          paramCodec.encode(paramPacket, (OutputStream)localObject2);
          dump((ByteArrayBuffer)localObject2, "HTTP response " + paramWSHTTPConnection.getStatus(), paramWSHTTPConnection.getResponseHeaders());
          ((ByteArrayBuffer)localObject2).writeTo((OutputStream)localObject1);
        }
        else
        {
          paramCodec.encode(paramPacket, (OutputStream)localObject1);
        }
        try
        {
          ((OutputStream)localObject1).close();
        }
        catch (IOException localIOException)
        {
          throw new WebServiceException(localIOException);
        }
      }
    }
    else
    {
      if (paramWSHTTPConnection.getStatus() == 0) {
        paramWSHTTPConnection.setStatus(localMessage.isFault() ? 500 : 200);
      }
      Object localObject3;
      if (isClientErrorStatus(paramWSHTTPConnection.getStatus()))
      {
        localObject1 = paramWSHTTPConnection.getOutput();
        if ((dump) || (LOGGER.isLoggable(Level.FINER)))
        {
          localObject3 = new ByteArrayBuffer();
          writeClientError(paramWSHTTPConnection.getStatus(), (OutputStream)localObject3, paramPacket);
          dump((ByteArrayBuffer)localObject3, "HTTP response " + paramWSHTTPConnection.getStatus(), paramWSHTTPConnection.getResponseHeaders());
          ((ByteArrayBuffer)localObject3).writeTo((OutputStream)localObject1);
        }
        else
        {
          writeClientError(paramWSHTTPConnection.getStatus(), (OutputStream)localObject1, paramPacket);
        }
        ((OutputStream)localObject1).close();
        return;
      }
      localObject1 = paramCodec.getStaticContentType(paramPacket);
      Object localObject4;
      if (localObject1 != null)
      {
        paramWSHTTPConnection.setContentTypeResponseHeader(((ContentType)localObject1).getContentType());
        localObject3 = paramWSHTTPConnection.getProtocol().contains("1.1") ? paramWSHTTPConnection.getOutput() : new Http10OutputStream(paramWSHTTPConnection);
        if ((dump) || (LOGGER.isLoggable(Level.FINER)))
        {
          localObject4 = new ByteArrayBuffer();
          paramCodec.encode(paramPacket, (OutputStream)localObject4);
          dump((ByteArrayBuffer)localObject4, "HTTP response " + paramWSHTTPConnection.getStatus(), paramWSHTTPConnection.getResponseHeaders());
          ((ByteArrayBuffer)localObject4).writeTo((OutputStream)localObject3);
        }
        else
        {
          paramCodec.encode(paramPacket, (OutputStream)localObject3);
        }
        ((OutputStream)localObject3).close();
      }
      else
      {
        localObject3 = new ByteArrayBuffer();
        localObject1 = paramCodec.encode(paramPacket, (OutputStream)localObject3);
        paramWSHTTPConnection.setContentTypeResponseHeader(((ContentType)localObject1).getContentType());
        if ((dump) || (LOGGER.isLoggable(Level.FINER))) {
          dump((ByteArrayBuffer)localObject3, "HTTP response " + paramWSHTTPConnection.getStatus(), paramWSHTTPConnection.getResponseHeaders());
        }
        localObject4 = paramWSHTTPConnection.getOutput();
        ((ByteArrayBuffer)localObject3).writeTo((OutputStream)localObject4);
        ((OutputStream)localObject4).close();
      }
    }
  }
  
  private void addStickyCookie(WSHTTPConnection paramWSHTTPConnection)
  {
    if (this.stickyCookie)
    {
      String str1 = paramWSHTTPConnection.getRequestHeader("proxy-jroute");
      if (str1 == null) {
        return;
      }
      String str2 = paramWSHTTPConnection.getCookie("JROUTE");
      if ((str2 == null) || (!str2.equals(str1))) {
        paramWSHTTPConnection.setCookie("JROUTE", str1);
      }
    }
  }
  
  private void addReplicaCookie(WSHTTPConnection paramWSHTTPConnection, Packet paramPacket)
  {
    if (this.stickyCookie)
    {
      HaInfo localHaInfo = null;
      if (paramPacket.supports("com.sun.xml.internal.ws.api.message.packet.hainfo")) {
        localHaInfo = (HaInfo)paramPacket.get("com.sun.xml.internal.ws.api.message.packet.hainfo");
      }
      if (localHaInfo != null)
      {
        paramWSHTTPConnection.setCookie("METRO_KEY", localHaInfo.getKey());
        if (!this.disableJreplicaCookie) {
          paramWSHTTPConnection.setCookie("JREPLICA", localHaInfo.getReplicaInstance());
        }
      }
    }
  }
  
  public void invokeAsync(WSHTTPConnection paramWSHTTPConnection)
    throws IOException
  {
    invokeAsync(paramWSHTTPConnection, NO_OP_COMPLETION_CALLBACK);
  }
  
  public void invokeAsync(final WSHTTPConnection paramWSHTTPConnection, final CompletionCallback paramCompletionCallback)
    throws IOException
  {
    if (handleGet(paramWSHTTPConnection))
    {
      paramCompletionCallback.onCompletion();
      return;
    }
    final Pool localPool = getPool();
    final HttpToolkit localHttpToolkit = (HttpToolkit)localPool.take();
    Packet localPacket1;
    try
    {
      localPacket1 = decodePacket(paramWSHTTPConnection, localHttpToolkit.codec);
    }
    catch (ExceptionHasMessage localExceptionHasMessage)
    {
      LOGGER.log(Level.SEVERE, localExceptionHasMessage.getMessage(), localExceptionHasMessage);
      localPacket2 = new Packet();
      localPacket2.setMessage(localExceptionHasMessage.getFaultMessage());
      encodePacket(localPacket2, paramWSHTTPConnection, localHttpToolkit.codec);
      localPool.recycle(localHttpToolkit);
      paramWSHTTPConnection.close();
      paramCompletionCallback.onCompletion();
      return;
    }
    catch (UnsupportedMediaException localUnsupportedMediaException)
    {
      LOGGER.log(Level.SEVERE, localUnsupportedMediaException.getMessage(), localUnsupportedMediaException);
      Packet localPacket2 = new Packet();
      paramWSHTTPConnection.setStatus(415);
      encodePacket(localPacket2, paramWSHTTPConnection, localHttpToolkit.codec);
      localPool.recycle(localHttpToolkit);
      paramWSHTTPConnection.close();
      paramCompletionCallback.onCompletion();
      return;
    }
    this.endpoint.process(localPacket1, new WSEndpoint.CompletionCallback()
    {
      public void onCompletion(@NotNull Packet paramAnonymousPacket)
      {
        try
        {
          try
          {
            HttpAdapter.this.encodePacket(paramAnonymousPacket, paramWSHTTPConnection, localHttpToolkit.codec);
          }
          catch (IOException localIOException)
          {
            HttpAdapter.LOGGER.log(Level.SEVERE, localIOException.getMessage(), localIOException);
          }
          localPool.recycle(localHttpToolkit);
          paramWSHTTPConnection.close();
          paramCompletionCallback.onCompletion();
        }
        finally
        {
          paramWSHTTPConnection.close();
          paramCompletionCallback.onCompletion();
        }
      }
    }, null);
  }
  
  private boolean isMetadataQuery(String paramString)
  {
    return (paramString != null) && ((paramString.equals("WSDL")) || (paramString.startsWith("wsdl")) || (paramString.startsWith("xsd=")));
  }
  
  public void publishWSDL(@NotNull WSHTTPConnection paramWSHTTPConnection)
    throws IOException
  {
    paramWSHTTPConnection.getInput().close();
    SDDocument localSDDocument = (SDDocument)this.wsdls.get(paramWSHTTPConnection.getQueryString());
    if (localSDDocument == null)
    {
      writeNotFoundErrorPage(paramWSHTTPConnection, "Invalid Request");
      return;
    }
    paramWSHTTPConnection.setStatus(200);
    paramWSHTTPConnection.setContentTypeResponseHeader("text/xml;charset=utf-8");
    Http10OutputStream localHttp10OutputStream = paramWSHTTPConnection.getProtocol().contains("1.1") ? paramWSHTTPConnection.getOutput() : new Http10OutputStream(paramWSHTTPConnection);
    PortAddressResolver localPortAddressResolver = getPortAddressResolver(paramWSHTTPConnection.getBaseAddress());
    DocumentAddressResolver localDocumentAddressResolver = getDocumentAddressResolver(localPortAddressResolver);
    localSDDocument.writeTo(localPortAddressResolver, localDocumentAddressResolver, localHttp10OutputStream);
    localHttp10OutputStream.close();
  }
  
  public PortAddressResolver getPortAddressResolver(String paramString)
  {
    return this.owner.createPortAddressResolver(paramString, this.endpoint.getImplementationClass());
  }
  
  public DocumentAddressResolver getDocumentAddressResolver(PortAddressResolver paramPortAddressResolver)
  {
    final String str = paramPortAddressResolver.getAddressFor(this.endpoint.getServiceName(), this.endpoint.getPortName().getLocalPart());
    assert (str != null);
    new DocumentAddressResolver()
    {
      public String getRelativeAddressFor(@NotNull SDDocument paramAnonymousSDDocument1, @NotNull SDDocument paramAnonymousSDDocument2)
      {
        assert (HttpAdapter.this.revWsdls.containsKey(paramAnonymousSDDocument2));
        return str + '?' + (String)HttpAdapter.this.revWsdls.get(paramAnonymousSDDocument2);
      }
    };
  }
  
  private void writeNotFoundErrorPage(WSHTTPConnection paramWSHTTPConnection, String paramString)
    throws IOException
  {
    paramWSHTTPConnection.setStatus(404);
    paramWSHTTPConnection.setContentTypeResponseHeader("text/html; charset=utf-8");
    PrintWriter localPrintWriter = new PrintWriter(new OutputStreamWriter(paramWSHTTPConnection.getOutput(), "UTF-8"));
    localPrintWriter.println("<html>");
    localPrintWriter.println("<head><title>");
    localPrintWriter.println(WsservletMessages.SERVLET_HTML_TITLE());
    localPrintWriter.println("</title></head>");
    localPrintWriter.println("<body>");
    localPrintWriter.println(WsservletMessages.SERVLET_HTML_NOT_FOUND(paramString));
    localPrintWriter.println("</body>");
    localPrintWriter.println("</html>");
    localPrintWriter.close();
  }
  
  private void writeInternalServerError(WSHTTPConnection paramWSHTTPConnection)
    throws IOException
  {
    paramWSHTTPConnection.setStatus(500);
    paramWSHTTPConnection.getOutput().close();
  }
  
  private static void dump(ByteArrayBuffer paramByteArrayBuffer, String paramString, Map<String, List<String>> paramMap)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    PrintWriter localPrintWriter = new PrintWriter(localByteArrayOutputStream, true);
    localPrintWriter.println("---[" + paramString + "]---");
    if (paramMap != null)
    {
      localObject = paramMap.entrySet().iterator();
      while (((Iterator)localObject).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
        if (((List)localEntry.getValue()).isEmpty())
        {
          localPrintWriter.println(localEntry.getValue());
        }
        else
        {
          Iterator localIterator = ((List)localEntry.getValue()).iterator();
          while (localIterator.hasNext())
          {
            String str = (String)localIterator.next();
            localPrintWriter.println((String)localEntry.getKey() + ": " + str);
          }
        }
      }
    }
    if (paramByteArrayBuffer.size() > dump_threshold)
    {
      localObject = paramByteArrayBuffer.getRawData();
      localByteArrayOutputStream.write((byte[])localObject, 0, dump_threshold);
      localPrintWriter.println();
      localPrintWriter.println(WsservletMessages.MESSAGE_TOO_LONG(HttpAdapter.class.getName() + ".dumpTreshold"));
    }
    else
    {
      paramByteArrayBuffer.writeTo(localByteArrayOutputStream);
    }
    localPrintWriter.println("--------------------");
    Object localObject = localByteArrayOutputStream.toString();
    if (dump) {
      System.out.println((String)localObject);
    }
    if (LOGGER.isLoggable(Level.FINER)) {
      LOGGER.log(Level.FINER, (String)localObject);
    }
  }
  
  private void writeWebServicesHtmlPage(WSHTTPConnection paramWSHTTPConnection)
    throws IOException
  {
    if (!publishStatusPage) {
      return;
    }
    paramWSHTTPConnection.getInput().close();
    paramWSHTTPConnection.setStatus(200);
    paramWSHTTPConnection.setContentTypeResponseHeader("text/html; charset=utf-8");
    PrintWriter localPrintWriter = new PrintWriter(new OutputStreamWriter(paramWSHTTPConnection.getOutput(), "UTF-8"));
    localPrintWriter.println("<html>");
    localPrintWriter.println("<head><title>");
    localPrintWriter.println(WsservletMessages.SERVLET_HTML_TITLE());
    localPrintWriter.println("</title></head>");
    localPrintWriter.println("<body>");
    localPrintWriter.println(WsservletMessages.SERVLET_HTML_TITLE_2());
    Module localModule = (Module)getEndpoint().getContainer().getSPI(Module.class);
    List localList = Collections.emptyList();
    if (localModule != null) {
      localList = localModule.getBoundEndpoints();
    }
    if (localList.isEmpty())
    {
      localPrintWriter.println(WsservletMessages.SERVLET_HTML_NO_INFO_AVAILABLE());
    }
    else
    {
      localPrintWriter.println("<table width='100%' border='1'>");
      localPrintWriter.println("<tr>");
      localPrintWriter.println("<td>");
      localPrintWriter.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_PORT_NAME());
      localPrintWriter.println("</td>");
      localPrintWriter.println("<td>");
      localPrintWriter.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_INFORMATION());
      localPrintWriter.println("</td>");
      localPrintWriter.println("</tr>");
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        BoundEndpoint localBoundEndpoint = (BoundEndpoint)localIterator.next();
        String str = localBoundEndpoint.getAddress(paramWSHTTPConnection.getBaseAddress()).toString();
        localPrintWriter.println("<tr>");
        localPrintWriter.println("<td>");
        localPrintWriter.println(WsservletMessages.SERVLET_HTML_ENDPOINT_TABLE(localBoundEndpoint.getEndpoint().getServiceName(), localBoundEndpoint.getEndpoint().getPortName()));
        localPrintWriter.println("</td>");
        localPrintWriter.println("<td>");
        localPrintWriter.println(WsservletMessages.SERVLET_HTML_INFORMATION_TABLE(str, localBoundEndpoint.getEndpoint().getImplementationClass().getName()));
        localPrintWriter.println("</td>");
        localPrintWriter.println("</tr>");
      }
      localPrintWriter.println("</table>");
    }
    localPrintWriter.println("</body>");
    localPrintWriter.println("</html>");
    localPrintWriter.close();
  }
  
  public static synchronized void setPublishStatus(boolean paramBoolean)
  {
    publishStatusPage = paramBoolean;
  }
  
  public static void setDump(boolean paramBoolean)
  {
    dump = paramBoolean;
  }
  
  static
  {
    LOGGER = Logger.getLogger(HttpAdapter.class.getName());
    NO_OP_COMPLETION_CALLBACK = new CompletionCallback()
    {
      public void onCompletion() {}
    };
    dump = false;
    dump_threshold = 4096;
    publishStatusPage = true;
    try
    {
      dump = Boolean.getBoolean(HttpAdapter.class.getName() + ".dump");
    }
    catch (SecurityException localSecurityException1)
    {
      if (LOGGER.isLoggable(Level.CONFIG)) {
        LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[] { HttpAdapter.class.getName() + ".dump" });
      }
    }
    try
    {
      dump_threshold = Integer.getInteger(HttpAdapter.class.getName() + ".dumpTreshold", 4096).intValue();
    }
    catch (SecurityException localSecurityException2)
    {
      if (LOGGER.isLoggable(Level.CONFIG)) {
        LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[] { HttpAdapter.class.getName() + ".dumpTreshold" });
      }
    }
    try
    {
      setPublishStatus(Boolean.getBoolean(HttpAdapter.class.getName() + ".publishStatusPage"));
    }
    catch (SecurityException localSecurityException3)
    {
      if (LOGGER.isLoggable(Level.CONFIG)) {
        LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[] { HttpAdapter.class.getName() + ".publishStatusPage" });
      }
    }
  }
  
  final class AsyncTransport
    extends AbstractServerAsyncTransport<WSHTTPConnection>
  {
    public AsyncTransport()
    {
      super();
    }
    
    public void handleAsync(WSHTTPConnection paramWSHTTPConnection)
      throws IOException
    {
      super.handle(paramWSHTTPConnection);
    }
    
    protected void encodePacket(WSHTTPConnection paramWSHTTPConnection, @NotNull Packet paramPacket, @NotNull Codec paramCodec)
      throws IOException
    {
      HttpAdapter.this.encodePacket(paramPacket, paramWSHTTPConnection, paramCodec);
    }
    
    @Nullable
    protected String getAcceptableMimeTypes(WSHTTPConnection paramWSHTTPConnection)
    {
      return null;
    }
    
    @Nullable
    protected TransportBackChannel getTransportBackChannel(WSHTTPConnection paramWSHTTPConnection)
    {
      return new HttpAdapter.Oneway(paramWSHTTPConnection);
    }
    
    @NotNull
    protected PropertySet getPropertySet(WSHTTPConnection paramWSHTTPConnection)
    {
      return paramWSHTTPConnection;
    }
    
    @NotNull
    protected WebServiceContextDelegate getWebServiceContextDelegate(WSHTTPConnection paramWSHTTPConnection)
    {
      return paramWSHTTPConnection.getWebServiceContextDelegate();
    }
  }
  
  public static abstract interface CompletionCallback
  {
    public abstract void onCompletion();
  }
  
  private static final class DummyList
    extends HttpAdapterList<HttpAdapter>
  {
    private DummyList() {}
    
    protected HttpAdapter createHttpAdapter(String paramString1, String paramString2, WSEndpoint<?> paramWSEndpoint)
    {
      return new HttpAdapter(paramWSEndpoint, this, paramString2);
    }
  }
  
  private static final class Http10OutputStream
    extends ByteArrayBuffer
  {
    private final WSHTTPConnection con;
    
    Http10OutputStream(WSHTTPConnection paramWSHTTPConnection)
    {
      this.con = paramWSHTTPConnection;
    }
    
    public void close()
      throws IOException
    {
      super.close();
      this.con.setContentLengthResponseHeader(size());
      OutputStream localOutputStream = this.con.getOutput();
      writeTo(localOutputStream);
      localOutputStream.close();
    }
  }
  
  final class HttpToolkit
    extends Adapter.Toolkit
  {
    HttpToolkit()
    {
      super();
    }
    
    public void handle(WSHTTPConnection paramWSHTTPConnection)
      throws IOException
    {
      try
      {
        int i = 0;
        Packet localPacket;
        try
        {
          localPacket = HttpAdapter.this.decodePacket(paramWSHTTPConnection, this.codec);
          i = 1;
        }
        catch (Exception localException)
        {
          localPacket = new Packet();
          if ((localException instanceof ExceptionHasMessage))
          {
            HttpAdapter.LOGGER.log(Level.SEVERE, localException.getMessage(), localException);
            localPacket.setMessage(((ExceptionHasMessage)localException).getFaultMessage());
          }
          else if ((localException instanceof UnsupportedMediaException))
          {
            HttpAdapter.LOGGER.log(Level.SEVERE, localException.getMessage(), localException);
            paramWSHTTPConnection.setStatus(415);
          }
          else
          {
            HttpAdapter.LOGGER.log(Level.SEVERE, localException.getMessage(), localException);
            paramWSHTTPConnection.setStatus(500);
          }
        }
        if (i != 0) {
          try
          {
            localPacket = this.head.process(localPacket, paramWSHTTPConnection.getWebServiceContextDelegate(), localPacket.transportBackChannel);
          }
          catch (Throwable localThrowable)
          {
            HttpAdapter.LOGGER.log(Level.SEVERE, localThrowable.getMessage(), localThrowable);
            if (!paramWSHTTPConnection.isClosed()) {
              HttpAdapter.this.writeInternalServerError(paramWSHTTPConnection);
            }
            return;
          }
        }
        HttpAdapter.this.encodePacket(localPacket, paramWSHTTPConnection, this.codec);
      }
      finally
      {
        if (!paramWSHTTPConnection.isClosed())
        {
          if (HttpAdapter.LOGGER.isLoggable(Level.FINE)) {
            HttpAdapter.LOGGER.log(Level.FINE, "Closing HTTP Connection with status: {0}", Integer.valueOf(paramWSHTTPConnection.getStatus()));
          }
          paramWSHTTPConnection.close();
        }
      }
    }
  }
  
  static final class Oneway
    implements TransportBackChannel
  {
    WSHTTPConnection con;
    boolean closed;
    
    Oneway(WSHTTPConnection paramWSHTTPConnection)
    {
      this.con = paramWSHTTPConnection;
    }
    
    public void close()
    {
      if (!this.closed)
      {
        this.closed = true;
        if (this.con.getStatus() == 0) {
          this.con.setStatus(202);
        }
        OutputStream localOutputStream = null;
        try
        {
          localOutputStream = this.con.getOutput();
        }
        catch (IOException localIOException1) {}
        if ((HttpAdapter.dump) || (HttpAdapter.LOGGER.isLoggable(Level.FINER))) {
          try
          {
            ByteArrayBuffer localByteArrayBuffer = new ByteArrayBuffer();
            HttpAdapter.dump(localByteArrayBuffer, "HTTP response " + this.con.getStatus(), this.con.getResponseHeaders());
          }
          catch (Exception localException)
          {
            throw new WebServiceException(localException.toString(), localException);
          }
        }
        if (localOutputStream != null) {
          try
          {
            localOutputStream.close();
          }
          catch (IOException localIOException2)
          {
            throw new WebServiceException(localIOException2);
          }
        }
        this.con.close();
      }
    }
  }
}
