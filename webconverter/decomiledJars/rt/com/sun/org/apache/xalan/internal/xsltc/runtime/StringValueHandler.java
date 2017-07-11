package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xml.internal.serializer.EmptySerializer;
import org.xml.sax.SAXException;

public final class StringValueHandler
  extends EmptySerializer
{
  private StringBuilder _buffer = new StringBuilder();
  private String _str = null;
  private static final String EMPTY_STR = "";
  private boolean m_escaping = false;
  private int _nestedLevel = 0;
  
  public StringValueHandler() {}
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (this._nestedLevel > 0) {
      return;
    }
    if (this._str != null)
    {
      this._buffer.append(this._str);
      this._str = null;
    }
    this._buffer.append(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public String getValue()
  {
    if (this._buffer.length() != 0)
    {
      str = this._buffer.toString();
      this._buffer.setLength(0);
      return str;
    }
    String str = this._str;
    this._str = null;
    return str != null ? str : "";
  }
  
  public void characters(String paramString)
    throws SAXException
  {
    if (this._nestedLevel > 0) {
      return;
    }
    if ((this._str == null) && (this._buffer.length() == 0))
    {
      this._str = paramString;
    }
    else
    {
      if (this._str != null)
      {
        this._buffer.append(this._str);
        this._str = null;
      }
      this._buffer.append(paramString);
    }
  }
  
  public void startElement(String paramString)
    throws SAXException
  {
    this._nestedLevel += 1;
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    this._nestedLevel -= 1;
  }
  
  public boolean setEscaping(boolean paramBoolean)
  {
    boolean bool = this.m_escaping;
    this.m_escaping = paramBoolean;
    return paramBoolean;
  }
  
  public String getValueOfPI()
  {
    String str = getValue();
    if (str.indexOf("?>") > 0)
    {
      int i = str.length();
      StringBuilder localStringBuilder = new StringBuilder();
      int j = 0;
      while (j < i)
      {
        char c = str.charAt(j++);
        if ((c == '?') && (str.charAt(j) == '>'))
        {
          localStringBuilder.append("? >");
          j++;
        }
        else
        {
          localStringBuilder.append(c);
        }
      }
      return localStringBuilder.toString();
    }
    return str;
  }
}
