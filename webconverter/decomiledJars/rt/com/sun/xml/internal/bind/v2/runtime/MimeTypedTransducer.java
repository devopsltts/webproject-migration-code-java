package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.AccessorException;
import java.io.IOException;
import javax.activation.MimeType;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class MimeTypedTransducer<V>
  extends FilterTransducer<V>
{
  private final MimeType expectedMimeType;
  
  public MimeTypedTransducer(Transducer<V> paramTransducer, MimeType paramMimeType)
  {
    super(paramTransducer);
    this.expectedMimeType = paramMimeType;
  }
  
  public CharSequence print(V paramV)
    throws AccessorException
  {
    XMLSerializer localXMLSerializer = XMLSerializer.getInstance();
    MimeType localMimeType = localXMLSerializer.setExpectedMimeType(this.expectedMimeType);
    try
    {
      CharSequence localCharSequence = this.core.print(paramV);
      return localCharSequence;
    }
    finally
    {
      localXMLSerializer.setExpectedMimeType(localMimeType);
    }
  }
  
  public void writeText(XMLSerializer paramXMLSerializer, V paramV, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    MimeType localMimeType = paramXMLSerializer.setExpectedMimeType(this.expectedMimeType);
    try
    {
      this.core.writeText(paramXMLSerializer, paramV, paramString);
    }
    finally
    {
      paramXMLSerializer.setExpectedMimeType(localMimeType);
    }
  }
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, V paramV, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    MimeType localMimeType = paramXMLSerializer.setExpectedMimeType(this.expectedMimeType);
    try
    {
      this.core.writeLeafElement(paramXMLSerializer, paramName, paramV, paramString);
    }
    finally
    {
      paramXMLSerializer.setExpectedMimeType(localMimeType);
    }
  }
}
