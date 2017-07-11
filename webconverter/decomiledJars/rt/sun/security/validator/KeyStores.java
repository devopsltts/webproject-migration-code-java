package sun.security.validator;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class KeyStores
{
  private KeyStores() {}
  
  public static Set<X509Certificate> getTrustedCerts(KeyStore paramKeyStore)
  {
    HashSet localHashSet = new HashSet();
    try
    {
      Enumeration localEnumeration = paramKeyStore.aliases();
      while (localEnumeration.hasMoreElements())
      {
        String str = (String)localEnumeration.nextElement();
        Object localObject;
        if (paramKeyStore.isCertificateEntry(str))
        {
          localObject = paramKeyStore.getCertificate(str);
          if ((localObject instanceof X509Certificate)) {
            localHashSet.add((X509Certificate)localObject);
          }
        }
        else if (paramKeyStore.isKeyEntry(str))
        {
          localObject = paramKeyStore.getCertificateChain(str);
          if ((localObject != null) && (localObject.length > 0) && ((localObject[0] instanceof X509Certificate))) {
            localHashSet.add((X509Certificate)localObject[0]);
          }
        }
      }
    }
    catch (KeyStoreException localKeyStoreException) {}
    return localHashSet;
  }
}
