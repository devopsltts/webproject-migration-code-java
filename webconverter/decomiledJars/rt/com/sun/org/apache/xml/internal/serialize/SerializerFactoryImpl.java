package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

final class SerializerFactoryImpl
  extends SerializerFactory
{
  private String _method;
  
  SerializerFactoryImpl(String paramString)
  {
    this._method = paramString;
    if ((!this._method.equals("xml")) && (!this._method.equals("html")) && (!this._method.equals("xhtml")) && (!this._method.equals("text")))
    {
      String str = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "MethodNotSupported", new Object[] { paramString });
      throw new IllegalArgumentException(str);
    }
  }
  
  public Serializer makeSerializer(OutputFormat paramOutputFormat)
  {
    Serializer localSerializer = getSerializer(paramOutputFormat);
    localSerializer.setOutputFormat(paramOutputFormat);
    return localSerializer;
  }
  
  public Serializer makeSerializer(Writer paramWriter, OutputFormat paramOutputFormat)
  {
    Serializer localSerializer = getSerializer(paramOutputFormat);
    localSerializer.setOutputCharStream(paramWriter);
    return localSerializer;
  }
  
  public Serializer makeSerializer(OutputStream paramOutputStream, OutputFormat paramOutputFormat)
    throws UnsupportedEncodingException
  {
    Serializer localSerializer = getSerializer(paramOutputFormat);
    localSerializer.setOutputByteStream(paramOutputStream);
    return localSerializer;
  }
  
  private Serializer getSerializer(OutputFormat paramOutputFormat)
  {
    if (this._method.equals("xml")) {
      return new XMLSerializer(paramOutputFormat);
    }
    if (this._method.equals("html")) {
      return new HTMLSerializer(paramOutputFormat);
    }
    if (this._method.equals("xhtml")) {
      return new XHTMLSerializer(paramOutputFormat);
    }
    if (this._method.equals("text")) {
      return new TextSerializer();
    }
    String str = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "MethodNotSupported", new Object[] { this._method });
    throw new IllegalStateException(str);
  }
  
  protected String getSupportedMethod()
  {
    return this._method;
  }
}
