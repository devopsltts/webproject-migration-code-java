package sun.security.provider.certpath;

import java.io.IOException;
import java.net.URI;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Cache;

public abstract class CertStoreHelper
{
  private static final int NUM_TYPES = 2;
  private static final Map<String, String> classMap = new HashMap(2);
  private static Cache<String, CertStoreHelper> cache = Cache.newSoftMemoryCache(2);
  
  public CertStoreHelper() {}
  
  public static CertStoreHelper getInstance(final String paramString)
    throws NoSuchAlgorithmException
  {
    CertStoreHelper localCertStoreHelper = (CertStoreHelper)cache.get(paramString);
    if (localCertStoreHelper != null) {
      return localCertStoreHelper;
    }
    String str = (String)classMap.get(paramString);
    if (str == null) {
      throw new NoSuchAlgorithmException(paramString + " not available");
    }
    try
    {
      localCertStoreHelper = (CertStoreHelper)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public CertStoreHelper run()
          throws ClassNotFoundException
        {
          try
          {
            Class localClass = Class.forName(this.val$cl, true, null);
            CertStoreHelper localCertStoreHelper = (CertStoreHelper)localClass.newInstance();
            CertStoreHelper.cache.put(paramString, localCertStoreHelper);
            return localCertStoreHelper;
          }
          catch (InstantiationException|IllegalAccessException localInstantiationException)
          {
            throw new AssertionError(localInstantiationException);
          }
        }
      });
      return localCertStoreHelper;
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new NoSuchAlgorithmException(paramString + " not available", localPrivilegedActionException.getException());
    }
  }
  
  static boolean isCausedByNetworkIssue(String paramString, CertStoreException paramCertStoreException)
  {
    switch (paramString)
    {
    case "LDAP": 
    case "SSLServer": 
      try
      {
        CertStoreHelper localCertStoreHelper = getInstance(paramString);
        return localCertStoreHelper.isCausedByNetworkIssue(paramCertStoreException);
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        return false;
      }
    case "URI": 
      Throwable localThrowable = paramCertStoreException.getCause();
      return (localThrowable != null) && ((localThrowable instanceof IOException));
    }
    return false;
  }
  
  public abstract CertStore getCertStore(URI paramURI)
    throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
  
  public abstract X509CertSelector wrap(X509CertSelector paramX509CertSelector, X500Principal paramX500Principal, String paramString)
    throws IOException;
  
  public abstract X509CRLSelector wrap(X509CRLSelector paramX509CRLSelector, Collection<X500Principal> paramCollection, String paramString)
    throws IOException;
  
  public abstract boolean isCausedByNetworkIssue(CertStoreException paramCertStoreException);
  
  static
  {
    classMap.put("LDAP", "sun.security.provider.certpath.ldap.LDAPCertStoreHelper");
    classMap.put("SSLServer", "sun.security.provider.certpath.ssl.SSLServerCertStoreHelper");
  }
}
