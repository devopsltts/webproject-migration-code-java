package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DOMURIDereferencer
  implements URIDereferencer
{
  static final URIDereferencer INSTANCE = new DOMURIDereferencer();
  
  private DOMURIDereferencer()
  {
    Init.init();
  }
  
  public Data dereference(URIReference paramURIReference, XMLCryptoContext paramXMLCryptoContext)
    throws URIReferenceException
  {
    if (paramURIReference == null) {
      throw new NullPointerException("uriRef cannot be null");
    }
    if (paramXMLCryptoContext == null) {
      throw new NullPointerException("context cannot be null");
    }
    DOMURIReference localDOMURIReference = (DOMURIReference)paramURIReference;
    Attr localAttr = (Attr)localDOMURIReference.getHere();
    String str1 = paramURIReference.getURI();
    DOMCryptoContext localDOMCryptoContext = (DOMCryptoContext)paramXMLCryptoContext;
    String str2 = paramXMLCryptoContext.getBaseURI();
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    Object localObject1;
    Object localObject2;
    if ((str1 != null) && (str1.length() != 0) && (str1.charAt(0) == '#'))
    {
      localObject1 = str1.substring(1);
      if (((String)localObject1).startsWith("xpointer(id("))
      {
        int i = ((String)localObject1).indexOf('\'');
        int j = ((String)localObject1).indexOf('\'', i + 1);
        localObject1 = ((String)localObject1).substring(i + 1, j);
      }
      localObject2 = localDOMCryptoContext.getElementById((String)localObject1);
      if (localObject2 != null)
      {
        if (bool)
        {
          localObject3 = ((Node)localObject2).getOwnerDocument().getDocumentElement();
          if (!XMLUtils.protectAgainstWrappingAttack((Node)localObject3, (Element)localObject2, (String)localObject1))
          {
            String str3 = "Multiple Elements with the same ID " + (String)localObject1 + " were detected";
            throw new URIReferenceException(str3);
          }
        }
        Object localObject3 = new XMLSignatureInput((Node)localObject2);
        if (!str1.substring(1).startsWith("xpointer(id(")) {
          ((XMLSignatureInput)localObject3).setExcludeComments(true);
        }
        ((XMLSignatureInput)localObject3).setMIMEType("text/xml");
        if ((str2 != null) && (str2.length() > 0)) {
          ((XMLSignatureInput)localObject3).setSourceURI(str2.concat(localAttr.getNodeValue()));
        } else {
          ((XMLSignatureInput)localObject3).setSourceURI(localAttr.getNodeValue());
        }
        return new ApacheNodeSetData((XMLSignatureInput)localObject3);
      }
    }
    try
    {
      localObject1 = ResourceResolver.getInstance(localAttr, str2, bool);
      localObject2 = ((ResourceResolver)localObject1).resolve(localAttr, str2);
      if (((XMLSignatureInput)localObject2).isOctetStream()) {
        return new ApacheOctetStreamData((XMLSignatureInput)localObject2);
      }
      return new ApacheNodeSetData((XMLSignatureInput)localObject2);
    }
    catch (Exception localException)
    {
      throw new URIReferenceException(localException);
    }
  }
}
