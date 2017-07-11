package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeyName
  extends SignatureElementProxy
  implements KeyInfoContent
{
  public KeyName(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public KeyName(Document paramDocument, String paramString)
  {
    super(paramDocument);
    addText(paramString);
  }
  
  public String getKeyName()
  {
    return getTextFromTextChild();
  }
  
  public String getBaseLocalName()
  {
    return "KeyName";
  }
}
