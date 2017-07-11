package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.transform.Source;

public final class XMLInputSourceAdaptor
  implements Source
{
  public final XMLInputSource fSource;
  
  public XMLInputSourceAdaptor(XMLInputSource paramXMLInputSource)
  {
    this.fSource = paramXMLInputSource;
  }
  
  public void setSystemId(String paramString)
  {
    this.fSource.setSystemId(paramString);
  }
  
  public String getSystemId()
  {
    try
    {
      return XMLEntityManager.expandSystemId(this.fSource.getSystemId(), this.fSource.getBaseSystemId(), false);
    }
    catch (URI.MalformedURIException localMalformedURIException) {}
    return this.fSource.getSystemId();
  }
}
