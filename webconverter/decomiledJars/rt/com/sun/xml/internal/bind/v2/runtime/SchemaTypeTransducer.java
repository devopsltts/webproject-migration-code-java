package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.AccessorException;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class SchemaTypeTransducer<V>
  extends FilterTransducer<V>
{
  private final QName schemaType;
  
  public SchemaTypeTransducer(Transducer<V> paramTransducer, QName paramQName)
  {
    super(paramTransducer);
    this.schemaType = paramQName;
  }
  
  public CharSequence print(V paramV)
    throws AccessorException
  {
    XMLSerializer localXMLSerializer = XMLSerializer.getInstance();
    QName localQName = localXMLSerializer.setSchemaType(this.schemaType);
    try
    {
      CharSequence localCharSequence = this.core.print(paramV);
      return localCharSequence;
    }
    finally
    {
      localXMLSerializer.setSchemaType(localQName);
    }
  }
  
  public void writeText(XMLSerializer paramXMLSerializer, V paramV, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    QName localQName = paramXMLSerializer.setSchemaType(this.schemaType);
    try
    {
      this.core.writeText(paramXMLSerializer, paramV, paramString);
    }
    finally
    {
      paramXMLSerializer.setSchemaType(localQName);
    }
  }
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, V paramV, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    QName localQName = paramXMLSerializer.setSchemaType(this.schemaType);
    try
    {
      this.core.writeLeafElement(paramXMLSerializer, paramName, paramV, paramString);
    }
    finally
    {
      paramXMLSerializer.setSchemaType(localQName);
    }
  }
}
