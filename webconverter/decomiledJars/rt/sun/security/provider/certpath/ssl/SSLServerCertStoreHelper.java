package sun.security.provider.certpath.ssl;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.Collection;
import javax.security.auth.x500.X500Principal;
import sun.security.provider.certpath.CertStoreHelper;

public final class SSLServerCertStoreHelper
  extends CertStoreHelper
{
  public SSLServerCertStoreHelper() {}
  
  public CertStore getCertStore(URI paramURI)
    throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
  {
    return SSLServerCertStore.getInstance(paramURI);
  }
  
  public X509CertSelector wrap(X509CertSelector paramX509CertSelector, X500Principal paramX500Principal, String paramString)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public X509CRLSelector wrap(X509CRLSelector paramX509CRLSelector, Collection<X500Principal> paramCollection, String paramString)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean isCausedByNetworkIssue(CertStoreException paramCertStoreException)
  {
    Throwable localThrowable = paramCertStoreException.getCause();
    return (localThrowable != null) && ((localThrowable instanceof IOException));
  }
}
