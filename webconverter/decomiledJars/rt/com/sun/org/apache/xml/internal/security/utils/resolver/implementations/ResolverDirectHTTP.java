package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResolverDirectHTTP
  extends ResourceResolverSpi
{
  private static Logger log = Logger.getLogger(ResolverDirectHTTP.class.getName());
  private static final String[] properties = { "http.proxy.host", "http.proxy.port", "http.proxy.username", "http.proxy.password", "http.basic.username", "http.basic.password" };
  private static final int HttpProxyHost = 0;
  private static final int HttpProxyPort = 1;
  private static final int HttpProxyUser = 2;
  private static final int HttpProxyPass = 3;
  private static final int HttpBasicUser = 4;
  private static final int HttpBasicPass = 5;
  
  public ResolverDirectHTTP() {}
  
  public boolean engineIsThreadSafe()
  {
    return true;
  }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext)
    throws ResourceResolverException
  {
    try
    {
      URI localURI = getNewURI(paramResourceResolverContext.uriToResolve, paramResourceResolverContext.baseUri);
      URL localURL = localURI.toURL();
      URLConnection localURLConnection = openConnection(localURL);
      String str1 = localURLConnection.getHeaderField("WWW-Authenticate");
      if ((str1 != null) && (str1.startsWith("Basic")))
      {
        str2 = engineGetProperty(properties[4]);
        localObject1 = engineGetProperty(properties[5]);
        if ((str2 != null) && (localObject1 != null))
        {
          localURLConnection = openConnection(localURL);
          localObject2 = str2 + ":" + (String)localObject1;
          localObject3 = Base64.encode(((String)localObject2).getBytes("ISO-8859-1"));
          localURLConnection.setRequestProperty("Authorization", "Basic " + (String)localObject3);
        }
      }
      String str2 = localURLConnection.getHeaderField("Content-Type");
      Object localObject1 = localURLConnection.getInputStream();
      Object localObject2 = new ByteArrayOutputStream();
      Object localObject3 = new byte['က'];
      int i = 0;
      int j = 0;
      while ((i = ((InputStream)localObject1).read((byte[])localObject3)) >= 0)
      {
        ((ByteArrayOutputStream)localObject2).write((byte[])localObject3, 0, i);
        j += i;
      }
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Fetched " + j + " bytes from URI " + localURI.toString());
      }
      XMLSignatureInput localXMLSignatureInput = new XMLSignatureInput(((ByteArrayOutputStream)localObject2).toByteArray());
      localXMLSignatureInput.setSourceURI(localURI.toString());
      localXMLSignatureInput.setMIMEType(str2);
      return localXMLSignatureInput;
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new ResourceResolverException("generic.EmptyMessage", localURISyntaxException, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new ResourceResolverException("generic.EmptyMessage", localMalformedURLException, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
    }
    catch (IOException localIOException)
    {
      throw new ResourceResolverException("generic.EmptyMessage", localIOException, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new ResourceResolverException("generic.EmptyMessage", localIllegalArgumentException, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
    }
  }
  
  private URLConnection openConnection(URL paramURL)
    throws IOException
  {
    String str1 = engineGetProperty(properties[0]);
    String str2 = engineGetProperty(properties[1]);
    String str3 = engineGetProperty(properties[2]);
    String str4 = engineGetProperty(properties[3]);
    Proxy localProxy = null;
    if ((str1 != null) && (str2 != null))
    {
      int i = Integer.parseInt(str2);
      localProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(str1, i));
    }
    URLConnection localURLConnection;
    if (localProxy != null)
    {
      localURLConnection = paramURL.openConnection(localProxy);
      if ((str3 != null) && (str4 != null))
      {
        String str5 = str3 + ":" + str4;
        String str6 = "Basic " + Base64.encode(str5.getBytes("ISO-8859-1"));
        localURLConnection.setRequestProperty("Proxy-Authorization", str6);
      }
    }
    else
    {
      localURLConnection = paramURL.openConnection();
    }
    return localURLConnection;
  }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext)
  {
    if (paramResourceResolverContext.uriToResolve == null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "quick fail, uri == null");
      }
      return false;
    }
    if ((paramResourceResolverContext.uriToResolve.equals("")) || (paramResourceResolverContext.uriToResolve.charAt(0) == '#'))
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "quick fail for empty URIs and local ones");
      }
      return false;
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "I was asked whether I can resolve " + paramResourceResolverContext.uriToResolve);
    }
    if ((paramResourceResolverContext.uriToResolve.startsWith("http:")) || ((paramResourceResolverContext.baseUri != null) && (paramResourceResolverContext.baseUri.startsWith("http:"))))
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I state that I can resolve " + paramResourceResolverContext.uriToResolve);
      }
      return true;
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "I state that I can't resolve " + paramResourceResolverContext.uriToResolve);
    }
    return false;
  }
  
  public String[] engineGetPropertyKeys()
  {
    return (String[])properties.clone();
  }
  
  private static URI getNewURI(String paramString1, String paramString2)
    throws URISyntaxException
  {
    URI localURI1 = null;
    if ((paramString2 == null) || ("".equals(paramString2))) {
      localURI1 = new URI(paramString1);
    } else {
      localURI1 = new URI(paramString2).resolve(paramString1);
    }
    if (localURI1.getFragment() != null)
    {
      URI localURI2 = new URI(localURI1.getScheme(), localURI1.getSchemeSpecificPart(), null);
      return localURI2;
    }
    return localURI1;
  }
}
