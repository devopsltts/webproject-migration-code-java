package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.RFC2253Parser;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.x500.X500Principal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509IssuerSerial
  extends SignatureElementProxy
  implements XMLX509DataContent
{
  private static Logger log = Logger.getLogger(XMLX509IssuerSerial.class.getName());
  
  public XMLX509IssuerSerial(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public XMLX509IssuerSerial(Document paramDocument, String paramString, BigInteger paramBigInteger)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(this.constructionElement);
    addTextElement(paramString, "X509IssuerName");
    addTextElement(paramBigInteger.toString(), "X509SerialNumber");
  }
  
  public XMLX509IssuerSerial(Document paramDocument, String paramString1, String paramString2)
  {
    this(paramDocument, paramString1, new BigInteger(paramString2));
  }
  
  public XMLX509IssuerSerial(Document paramDocument, String paramString, int paramInt)
  {
    this(paramDocument, paramString, new BigInteger(Integer.toString(paramInt)));
  }
  
  public XMLX509IssuerSerial(Document paramDocument, X509Certificate paramX509Certificate)
  {
    this(paramDocument, paramX509Certificate.getIssuerX500Principal().getName(), paramX509Certificate.getSerialNumber());
  }
  
  public BigInteger getSerialNumber()
  {
    String str = getTextFromChildElement("X509SerialNumber", "http://www.w3.org/2000/09/xmldsig#");
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "X509SerialNumber text: " + str);
    }
    return new BigInteger(str);
  }
  
  public int getSerialNumberInteger()
  {
    return getSerialNumber().intValue();
  }
  
  public String getIssuerName()
  {
    return RFC2253Parser.normalize(getTextFromChildElement("X509IssuerName", "http://www.w3.org/2000/09/xmldsig#"));
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof XMLX509IssuerSerial)) {
      return false;
    }
    XMLX509IssuerSerial localXMLX509IssuerSerial = (XMLX509IssuerSerial)paramObject;
    return (getSerialNumber().equals(localXMLX509IssuerSerial.getSerialNumber())) && (getIssuerName().equals(localXMLX509IssuerSerial.getIssuerName()));
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 31 * i + getSerialNumber().hashCode();
    i = 31 * i + getIssuerName().hashCode();
    return i;
  }
  
  public String getBaseLocalName()
  {
    return "X509IssuerSerial";
  }
}
