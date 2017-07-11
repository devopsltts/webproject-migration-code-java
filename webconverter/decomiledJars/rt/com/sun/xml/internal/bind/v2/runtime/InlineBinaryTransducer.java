package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.AccessorException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class InlineBinaryTransducer<V>
  extends FilterTransducer<V>
{
  public InlineBinaryTransducer(Transducer<V> paramTransducer)
  {
    super(paramTransducer);
  }
  
  @NotNull
  public CharSequence print(@NotNull V paramV)
    throws AccessorException
  {
    XMLSerializer localXMLSerializer = XMLSerializer.getInstance();
    boolean bool = localXMLSerializer.setInlineBinaryFlag(true);
    try
    {
      CharSequence localCharSequence = this.core.print(paramV);
      return localCharSequence;
    }
    finally
    {
      localXMLSerializer.setInlineBinaryFlag(bool);
    }
  }
  
  public void writeText(XMLSerializer paramXMLSerializer, V paramV, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    boolean bool = paramXMLSerializer.setInlineBinaryFlag(true);
    try
    {
      this.core.writeText(paramXMLSerializer, paramV, paramString);
    }
    finally
    {
      paramXMLSerializer.setInlineBinaryFlag(bool);
    }
  }
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, V paramV, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    boolean bool = paramXMLSerializer.setInlineBinaryFlag(true);
    try
    {
      this.core.writeLeafElement(paramXMLSerializer, paramName, paramV, paramString);
    }
    finally
    {
      paramXMLSerializer.setInlineBinaryFlag(bool);
    }
  }
}
