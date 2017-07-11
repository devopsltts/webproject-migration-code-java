package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.ExternalSubsetResolver;
import com.sun.org.apache.xerces.internal.impl.XMLEntityDescription;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

public class EntityResolver2Wrapper
  implements ExternalSubsetResolver
{
  protected EntityResolver2 fEntityResolver;
  
  public EntityResolver2Wrapper() {}
  
  public EntityResolver2Wrapper(EntityResolver2 paramEntityResolver2)
  {
    setEntityResolver(paramEntityResolver2);
  }
  
  public void setEntityResolver(EntityResolver2 paramEntityResolver2)
  {
    this.fEntityResolver = paramEntityResolver2;
  }
  
  public EntityResolver2 getEntityResolver()
  {
    return this.fEntityResolver;
  }
  
  public XMLInputSource getExternalSubset(XMLDTDDescription paramXMLDTDDescription)
    throws XNIException, IOException
  {
    if (this.fEntityResolver != null)
    {
      String str1 = paramXMLDTDDescription.getRootName();
      String str2 = paramXMLDTDDescription.getBaseSystemId();
      try
      {
        InputSource localInputSource = this.fEntityResolver.getExternalSubset(str1, str2);
        return localInputSource != null ? createXMLInputSource(localInputSource, str2) : null;
      }
      catch (SAXException localSAXException)
      {
        Object localObject = localSAXException.getException();
        if (localObject == null) {
          localObject = localSAXException;
        }
        throw new XNIException((Exception)localObject);
      }
    }
    return null;
  }
  
  public XMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier)
    throws XNIException, IOException
  {
    if (this.fEntityResolver != null)
    {
      String str1 = paramXMLResourceIdentifier.getPublicId();
      String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
      String str3 = paramXMLResourceIdentifier.getBaseSystemId();
      String str4 = null;
      if ((paramXMLResourceIdentifier instanceof XMLDTDDescription)) {
        str4 = "[dtd]";
      } else if ((paramXMLResourceIdentifier instanceof XMLEntityDescription)) {
        str4 = ((XMLEntityDescription)paramXMLResourceIdentifier).getEntityName();
      }
      if ((str1 == null) && (str2 == null)) {
        return null;
      }
      try
      {
        InputSource localInputSource = this.fEntityResolver.resolveEntity(str4, str1, str3, str2);
        return localInputSource != null ? createXMLInputSource(localInputSource, str3) : null;
      }
      catch (SAXException localSAXException)
      {
        Object localObject = localSAXException.getException();
        if (localObject == null) {
          localObject = localSAXException;
        }
        throw new XNIException((Exception)localObject);
      }
    }
    return null;
  }
  
  private XMLInputSource createXMLInputSource(InputSource paramInputSource, String paramString)
  {
    String str1 = paramInputSource.getPublicId();
    String str2 = paramInputSource.getSystemId();
    String str3 = paramString;
    InputStream localInputStream = paramInputSource.getByteStream();
    Reader localReader = paramInputSource.getCharacterStream();
    String str4 = paramInputSource.getEncoding();
    XMLInputSource localXMLInputSource = new XMLInputSource(str1, str2, str3);
    localXMLInputSource.setByteStream(localInputStream);
    localXMLInputSource.setCharacterStream(localReader);
    localXMLInputSource.setEncoding(str4);
    return localXMLInputSource;
  }
}
