package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.RFC2253Parser;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509SubjectName
  extends SignatureElementProxy
  implements XMLX509DataContent
{
  public XMLX509SubjectName(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public XMLX509SubjectName(Document paramDocument, String paramString)
  {
    super(paramDocument);
    addText(paramString);
  }
  
  public XMLX509SubjectName(Document paramDocument, X509Certificate paramX509Certificate)
  {
    this(paramDocument, paramX509Certificate.getSubjectX500Principal().getName());
  }
  
  public String getSubjectName()
  {
    return RFC2253Parser.normalize(getTextFromTextChild());
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof XMLX509SubjectName)) {
      return false;
    }
    XMLX509SubjectName localXMLX509SubjectName = (XMLX509SubjectName)paramObject;
    String str1 = localXMLX509SubjectName.getSubjectName();
    String str2 = getSubjectName();
    return str2.equals(str1);
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 31 * i + getSubjectName().hashCode();
    return i;
  }
  
  public String getBaseLocalName()
  {
    return "X509SubjectName";
  }
}
