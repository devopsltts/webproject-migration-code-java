package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class X509CertificateResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(X509CertificateResolver.class.getName());
  
  public X509CertificateResolver() {}
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    X509Certificate localX509Certificate = engineLookupResolveX509Certificate(paramElement, paramString, paramStorageResolver);
    if (localX509Certificate != null) {
      return localX509Certificate.getPublicKey();
    }
    return null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    try
    {
      Element[] arrayOfElement = XMLUtils.selectDsNodes(paramElement.getFirstChild(), "X509Certificate");
      if ((arrayOfElement == null) || (arrayOfElement.length == 0))
      {
        Element localElement = XMLUtils.selectDsNode(paramElement.getFirstChild(), "X509Data", 0);
        if (localElement != null) {
          return engineLookupResolveX509Certificate(localElement, paramString, paramStorageResolver);
        }
        return null;
      }
      for (int i = 0; i < arrayOfElement.length; i++)
      {
        XMLX509Certificate localXMLX509Certificate = new XMLX509Certificate(arrayOfElement[i], paramString);
        X509Certificate localX509Certificate = localXMLX509Certificate.getX509Certificate();
        if (localX509Certificate != null) {
          return localX509Certificate;
        }
      }
      return null;
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException);
      }
      throw new KeyResolverException("generic.EmptyMessage", localXMLSecurityException);
    }
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return null;
  }
}
