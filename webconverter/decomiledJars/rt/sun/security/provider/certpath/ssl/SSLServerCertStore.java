package sun.security.provider.certpath.ssl;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Provider;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

public final class SSLServerCertStore
  extends CertStoreSpi
{
  private final URI uri;
  private static final GetChainTrustManager trustManager = new GetChainTrustManager(null);
  private static final SSLSocketFactory socketFactory;
  private static final HostnameVerifier hostnameVerifier = new HostnameVerifier()
  {
    public boolean verify(String paramAnonymousString, SSLSession paramAnonymousSSLSession)
    {
      return true;
    }
  };
  
  SSLServerCertStore(URI paramURI)
    throws InvalidAlgorithmParameterException
  {
    super(null);
    this.uri = paramURI;
  }
  
  public Collection<X509Certificate> engineGetCertificates(CertSelector paramCertSelector)
    throws CertStoreException
  {
    try
    {
      URLConnection localURLConnection = this.uri.toURL().openConnection();
      if ((localURLConnection instanceof HttpsURLConnection))
      {
        if (socketFactory == null) {
          throw new CertStoreException("No initialized SSLSocketFactory");
        }
        HttpsURLConnection localHttpsURLConnection = (HttpsURLConnection)localURLConnection;
        localHttpsURLConnection.setSSLSocketFactory(socketFactory);
        localHttpsURLConnection.setHostnameVerifier(hostnameVerifier);
        synchronized (trustManager)
        {
          try
          {
            localHttpsURLConnection.connect();
            List localList1 = getMatchingCerts(trustManager.serverChain, paramCertSelector);
            trustManager.cleanup();
            return localList1;
          }
          catch (IOException localIOException2)
          {
            if (trustManager.exchangedServerCerts)
            {
              List localList2 = getMatchingCerts(trustManager.serverChain, paramCertSelector);
              trustManager.cleanup();
              return localList2;
            }
            throw localIOException2;
          }
          finally
          {
            trustManager.cleanup();
          }
        }
      }
    }
    catch (IOException localIOException1)
    {
      throw new CertStoreException(localIOException1);
    }
    return Collections.emptySet();
  }
  
  private static List<X509Certificate> getMatchingCerts(List<X509Certificate> paramList, CertSelector paramCertSelector)
  {
    if (paramCertSelector == null) {
      return paramList;
    }
    ArrayList localArrayList = new ArrayList(paramList.size());
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      X509Certificate localX509Certificate = (X509Certificate)localIterator.next();
      if (paramCertSelector.match(localX509Certificate)) {
        localArrayList.add(localX509Certificate);
      }
    }
    return localArrayList;
  }
  
  public Collection<X509CRL> engineGetCRLs(CRLSelector paramCRLSelector)
    throws CertStoreException
  {
    throw new UnsupportedOperationException();
  }
  
  static CertStore getInstance(URI paramURI)
    throws InvalidAlgorithmParameterException
  {
    return new CS(new SSLServerCertStore(paramURI), null, "SSLServer", null);
  }
  
  static
  {
    SSLSocketFactory localSSLSocketFactory;
    try
    {
      SSLContext localSSLContext = SSLContext.getInstance("SSL");
      localSSLContext.init(null, new TrustManager[] { trustManager }, null);
      localSSLSocketFactory = localSSLContext.getSocketFactory();
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      localSSLSocketFactory = null;
    }
    socketFactory = localSSLSocketFactory;
  }
  
  private static class CS
    extends CertStore
  {
    protected CS(CertStoreSpi paramCertStoreSpi, Provider paramProvider, String paramString, CertStoreParameters paramCertStoreParameters)
    {
      super(paramProvider, paramString, paramCertStoreParameters);
    }
  }
  
  private static class GetChainTrustManager
    extends X509ExtendedTrustManager
  {
    private List<X509Certificate> serverChain = Collections.emptyList();
    private boolean exchangedServerCerts = false;
    
    private GetChainTrustManager() {}
    
    public X509Certificate[] getAcceptedIssuers()
    {
      return new X509Certificate[0];
    }
    
    public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
      throws CertificateException
    {
      throw new UnsupportedOperationException();
    }
    
    public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket)
      throws CertificateException
    {
      throw new UnsupportedOperationException();
    }
    
    public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine)
      throws CertificateException
    {
      throw new UnsupportedOperationException();
    }
    
    public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
      throws CertificateException
    {
      this.exchangedServerCerts = true;
      this.serverChain = (paramArrayOfX509Certificate == null ? Collections.emptyList() : Arrays.asList(paramArrayOfX509Certificate));
    }
    
    public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket)
      throws CertificateException
    {
      checkServerTrusted(paramArrayOfX509Certificate, paramString);
    }
    
    public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine)
      throws CertificateException
    {
      checkServerTrusted(paramArrayOfX509Certificate, paramString);
    }
    
    void cleanup()
    {
      this.exchangedServerCerts = false;
      this.serverChain = Collections.emptyList();
    }
  }
}
