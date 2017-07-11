package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StaxEntityResolverWrapper
{
  XMLResolver fStaxResolver;
  
  public StaxEntityResolverWrapper(XMLResolver paramXMLResolver)
  {
    this.fStaxResolver = paramXMLResolver;
  }
  
  public void setStaxEntityResolver(XMLResolver paramXMLResolver)
  {
    this.fStaxResolver = paramXMLResolver;
  }
  
  public XMLResolver getStaxEntityResolver()
  {
    return this.fStaxResolver;
  }
  
  public StaxXMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier)
    throws XNIException, IOException
  {
    Object localObject = null;
    try
    {
      localObject = this.fStaxResolver.resolveEntity(paramXMLResourceIdentifier.getPublicId(), paramXMLResourceIdentifier.getLiteralSystemId(), paramXMLResourceIdentifier.getBaseSystemId(), null);
      return getStaxInputSource(localObject);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new XNIException(localXMLStreamException);
    }
  }
  
  StaxXMLInputSource getStaxInputSource(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof InputStream)) {
      return new StaxXMLInputSource(new XMLInputSource(null, null, null, (InputStream)paramObject, null));
    }
    if ((paramObject instanceof XMLStreamReader)) {
      return new StaxXMLInputSource((XMLStreamReader)paramObject);
    }
    if ((paramObject instanceof XMLEventReader)) {
      return new StaxXMLInputSource((XMLEventReader)paramObject);
    }
    return null;
  }
}
