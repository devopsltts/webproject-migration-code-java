package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Writer;
import java.util.StringTokenizer;
import javax.xml.transform.TransformerException;

public class XMLDeclarationParser
{
  private String m_encoding;
  private PushbackReader m_pushbackReader;
  private boolean m_hasHeader;
  private String xmlDecl = null;
  static String gt16 = null;
  static String utf16Decl = null;
  
  public XMLDeclarationParser(PushbackReader paramPushbackReader)
  {
    this.m_pushbackReader = paramPushbackReader;
    this.m_encoding = "utf-8";
    this.m_hasHeader = false;
  }
  
  public String getEncoding()
  {
    return this.m_encoding;
  }
  
  public String getXmlDeclaration()
  {
    return this.xmlDecl;
  }
  
  public void parse()
    throws TransformerException, IOException
  {
    int i = 0;
    int j = 0;
    char[] arrayOfChar = new char[65535];
    StringBuffer localStringBuffer = new StringBuffer();
    while ((i = this.m_pushbackReader.read()) != -1)
    {
      arrayOfChar[j] = ((char)i);
      localStringBuffer.append((char)i);
      j++;
      if (i == 62) {
        break;
      }
    }
    int k = j;
    String str = localStringBuffer.toString();
    int m = 0;
    int n = 0;
    int i1 = str.indexOf(utf16Decl);
    if (i1 > -1)
    {
      m = 1;
    }
    else
    {
      i1 = str.indexOf("<?xml");
      if (i1 > -1) {
        n = 1;
      }
    }
    if ((m == 0) && (n == 0))
    {
      this.m_pushbackReader.unread(arrayOfChar, 0, k);
      return;
    }
    this.m_hasHeader = true;
    if (m != 0)
    {
      this.xmlDecl = new String(str.getBytes(), "utf-16");
      this.xmlDecl = this.xmlDecl.substring(this.xmlDecl.indexOf("<"));
    }
    else
    {
      this.xmlDecl = str;
    }
    if (i1 != 0) {
      throw new IOException("Unexpected characters before XML declaration");
    }
    int i2 = this.xmlDecl.indexOf("version");
    if (i2 == -1) {
      throw new IOException("Mandatory 'version' attribute Missing in XML declaration");
    }
    int i3 = this.xmlDecl.indexOf("encoding");
    if (i3 == -1) {
      return;
    }
    if (i2 > i3) {
      throw new IOException("The 'version' attribute should preceed the 'encoding' attribute in an XML Declaration");
    }
    int i4 = this.xmlDecl.indexOf("standalone");
    if ((i4 > -1) && ((i4 < i2) || (i4 < i3))) {
      throw new IOException("The 'standalone' attribute should be the last attribute in an XML Declaration");
    }
    int i5 = this.xmlDecl.indexOf("=", i3);
    if (i5 == -1) {
      throw new IOException("Missing '=' character after 'encoding' in XML declaration");
    }
    this.m_encoding = parseEncoding(this.xmlDecl, i5);
    if (this.m_encoding.startsWith("\"")) {
      this.m_encoding = this.m_encoding.substring(this.m_encoding.indexOf("\"") + 1, this.m_encoding.lastIndexOf("\""));
    } else if (this.m_encoding.startsWith("'")) {
      this.m_encoding = this.m_encoding.substring(this.m_encoding.indexOf("'") + 1, this.m_encoding.lastIndexOf("'"));
    }
  }
  
  public void writeTo(Writer paramWriter)
    throws IOException
  {
    if (!this.m_hasHeader) {
      return;
    }
    paramWriter.write(this.xmlDecl.toString());
  }
  
  private String parseEncoding(String paramString, int paramInt)
    throws IOException
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString.substring(paramInt + 1));
    if (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      int i = str.indexOf("?");
      if (i > -1) {
        return str.substring(0, i);
      }
      return str;
    }
    throw new IOException("Error parsing 'encoding' attribute in XML declaration");
  }
  
  static
  {
    try
    {
      gt16 = new String(">".getBytes("utf-16"));
      utf16Decl = new String("<?xml".getBytes("utf-16"));
    }
    catch (Exception localException) {}
  }
}
