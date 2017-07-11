package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class DSAKeyValueResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(DSAKeyValueResolver.class.getName());
  
  public DSAKeyValueResolver() {}
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    if (paramElement == null) {
      return null;
    }
    Element localElement = null;
    boolean bool = XMLUtils.elementIsInSignatureSpace(paramElement, "KeyValue");
    if (bool) {
      localElement = XMLUtils.selectDsNode(paramElement.getFirstChild(), "DSAKeyValue", 0);
    } else if (XMLUtils.elementIsInSignatureSpace(paramElement, "DSAKeyValue")) {
      localElement = paramElement;
    }
    if (localElement == null) {
      return null;
    }
    try
    {
      DSAKeyValue localDSAKeyValue = new DSAKeyValue(localElement, paramString);
      PublicKey localPublicKey = localDSAKeyValue.getPublicKey();
      return localPublicKey;
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localXMLSecurityException.getMessage(), localXMLSecurityException);
      }
    }
    return null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return null;
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return null;
  }
}
