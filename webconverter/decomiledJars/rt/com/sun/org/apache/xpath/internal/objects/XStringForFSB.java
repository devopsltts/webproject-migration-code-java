package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class XStringForFSB
  extends XString
{
  static final long serialVersionUID = -1533039186550674548L;
  int m_start;
  int m_length;
  protected String m_strCache = null;
  protected int m_hash = 0;
  
  public XStringForFSB(FastStringBuffer paramFastStringBuffer, int paramInt1, int paramInt2)
  {
    super(paramFastStringBuffer);
    this.m_start = paramInt1;
    this.m_length = paramInt2;
    if (null == paramFastStringBuffer) {
      throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_FASTSTRINGBUFFER_CANNOT_BE_NULL", null));
    }
  }
  
  private XStringForFSB(String paramString)
  {
    super(paramString);
    throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_FSB_CANNOT_TAKE_STRING", null));
  }
  
  public FastStringBuffer fsb()
  {
    return (FastStringBuffer)this.m_obj;
  }
  
  public void appendToFsb(FastStringBuffer paramFastStringBuffer)
  {
    paramFastStringBuffer.append(str());
  }
  
  public boolean hasString()
  {
    return null != this.m_strCache;
  }
  
  public Object object()
  {
    return str();
  }
  
  public String str()
  {
    if (null == this.m_strCache) {
      this.m_strCache = fsb().getString(this.m_start, this.m_length);
    }
    return this.m_strCache;
  }
  
  public void dispatchCharactersEvents(ContentHandler paramContentHandler)
    throws SAXException
  {
    fsb().sendSAXcharacters(paramContentHandler, this.m_start, this.m_length);
  }
  
  public void dispatchAsComment(LexicalHandler paramLexicalHandler)
    throws SAXException
  {
    fsb().sendSAXComment(paramLexicalHandler, this.m_start, this.m_length);
  }
  
  public int length()
  {
    return this.m_length;
  }
  
  public char charAt(int paramInt)
  {
    return fsb().charAt(this.m_start + paramInt);
  }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3)
  {
    int i = paramInt2 - paramInt1;
    if (i > this.m_length) {
      i = this.m_length;
    }
    if (i > paramArrayOfChar.length - paramInt3) {
      i = paramArrayOfChar.length - paramInt3;
    }
    int j = paramInt1 + this.m_start + i;
    int k = paramInt3;
    FastStringBuffer localFastStringBuffer = fsb();
    for (int m = paramInt1 + this.m_start; m < j; m++) {
      paramArrayOfChar[(k++)] = localFastStringBuffer.charAt(m);
    }
  }
  
  public boolean equals(XMLString paramXMLString)
  {
    if (this == paramXMLString) {
      return true;
    }
    int i = this.m_length;
    if (i == paramXMLString.length())
    {
      FastStringBuffer localFastStringBuffer = fsb();
      int j = this.m_start;
      for (int k = 0; i-- != 0; k++)
      {
        if (localFastStringBuffer.charAt(j) != paramXMLString.charAt(k)) {
          return false;
        }
        j++;
      }
      return true;
    }
    return false;
  }
  
  public boolean equals(XObject paramXObject)
  {
    if (this == paramXObject) {
      return true;
    }
    if (paramXObject.getType() == 2) {
      return paramXObject.equals(this);
    }
    String str = paramXObject.str();
    int i = this.m_length;
    if (i == str.length())
    {
      FastStringBuffer localFastStringBuffer = fsb();
      int j = this.m_start;
      for (int k = 0; i-- != 0; k++)
      {
        if (localFastStringBuffer.charAt(j) != str.charAt(k)) {
          return false;
        }
        j++;
      }
      return true;
    }
    return false;
  }
  
  public boolean equals(String paramString)
  {
    int i = this.m_length;
    if (i == paramString.length())
    {
      FastStringBuffer localFastStringBuffer = fsb();
      int j = this.m_start;
      for (int k = 0; i-- != 0; k++)
      {
        if (localFastStringBuffer.charAt(j) != paramString.charAt(k)) {
          return false;
        }
        j++;
      }
      return true;
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (null == paramObject) {
      return false;
    }
    if ((paramObject instanceof XNumber)) {
      return paramObject.equals(this);
    }
    if ((paramObject instanceof XNodeSet)) {
      return paramObject.equals(this);
    }
    if ((paramObject instanceof XStringForFSB)) {
      return equals((XMLString)paramObject);
    }
    return equals(paramObject.toString());
  }
  
  public boolean equalsIgnoreCase(String paramString)
  {
    return this.m_length == paramString.length() ? str().equalsIgnoreCase(paramString) : false;
  }
  
  public int compareTo(XMLString paramXMLString)
  {
    int i = this.m_length;
    int j = paramXMLString.length();
    int k = Math.min(i, j);
    FastStringBuffer localFastStringBuffer = fsb();
    int m = this.m_start;
    for (int n = 0; k-- != 0; n++)
    {
      int i1 = localFastStringBuffer.charAt(m);
      int i2 = paramXMLString.charAt(n);
      if (i1 != i2) {
        return i1 - i2;
      }
      m++;
    }
    return i - j;
  }
  
  public int compareToIgnoreCase(XMLString paramXMLString)
  {
    int i = this.m_length;
    int j = paramXMLString.length();
    int k = Math.min(i, j);
    FastStringBuffer localFastStringBuffer = fsb();
    int m = this.m_start;
    for (int n = 0; k-- != 0; n++)
    {
      int i1 = Character.toLowerCase(localFastStringBuffer.charAt(m));
      int i2 = Character.toLowerCase(paramXMLString.charAt(n));
      if (i1 != i2) {
        return i1 - i2;
      }
      m++;
    }
    return i - j;
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  public boolean startsWith(XMLString paramXMLString, int paramInt)
  {
    FastStringBuffer localFastStringBuffer = fsb();
    int i = this.m_start + paramInt;
    int j = this.m_start + this.m_length;
    int k = 0;
    int m = paramXMLString.length();
    if ((paramInt < 0) || (paramInt > this.m_length - m)) {
      return false;
    }
    for (;;)
    {
      m--;
      if (m < 0) {
        break;
      }
      if (localFastStringBuffer.charAt(i) != paramXMLString.charAt(k)) {
        return false;
      }
      i++;
      k++;
    }
    return true;
  }
  
  public boolean startsWith(XMLString paramXMLString)
  {
    return startsWith(paramXMLString, 0);
  }
  
  public int indexOf(int paramInt)
  {
    return indexOf(paramInt, 0);
  }
  
  public int indexOf(int paramInt1, int paramInt2)
  {
    int i = this.m_start + this.m_length;
    FastStringBuffer localFastStringBuffer = fsb();
    if (paramInt2 < 0) {
      paramInt2 = 0;
    } else if (paramInt2 >= this.m_length) {
      return -1;
    }
    for (int j = this.m_start + paramInt2; j < i; j++) {
      if (localFastStringBuffer.charAt(j) == paramInt1) {
        return j - this.m_start;
      }
    }
    return -1;
  }
  
  public XMLString substring(int paramInt)
  {
    int i = this.m_length - paramInt;
    if (i <= 0) {
      return XString.EMPTYSTRING;
    }
    int j = this.m_start + paramInt;
    return new XStringForFSB(fsb(), j, i);
  }
  
  public XMLString substring(int paramInt1, int paramInt2)
  {
    int i = paramInt2 - paramInt1;
    if (i > this.m_length) {
      i = this.m_length;
    }
    if (i <= 0) {
      return XString.EMPTYSTRING;
    }
    int j = this.m_start + paramInt1;
    return new XStringForFSB(fsb(), j, i);
  }
  
  public XMLString concat(String paramString)
  {
    return new XString(str().concat(paramString));
  }
  
  public XMLString trim()
  {
    return fixWhiteSpace(true, true, false);
  }
  
  private static boolean isSpace(char paramChar)
  {
    return XMLCharacterRecognizer.isWhiteSpace(paramChar);
  }
  
  public XMLString fixWhiteSpace(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    int i = this.m_length + this.m_start;
    char[] arrayOfChar = new char[this.m_length];
    FastStringBuffer localFastStringBuffer = fsb();
    int j = 0;
    int k = 0;
    int m = 0;
    for (int n = this.m_start; n < i; n++)
    {
      char c = localFastStringBuffer.charAt(n);
      if (isSpace(c))
      {
        if (m == 0)
        {
          if (' ' != c) {
            j = 1;
          }
          arrayOfChar[(k++)] = ' ';
          if ((paramBoolean3) && (k != 0))
          {
            int i1 = arrayOfChar[(k - 1)];
            if ((i1 != 46) && (i1 != 33) && (i1 != 63)) {
              m = 1;
            }
          }
          else
          {
            m = 1;
          }
        }
        else
        {
          j = 1;
          m = 1;
        }
      }
      else
      {
        arrayOfChar[(k++)] = c;
        m = 0;
      }
    }
    if ((paramBoolean2) && (1 <= k) && (' ' == arrayOfChar[(k - 1)]))
    {
      j = 1;
      k--;
    }
    n = 0;
    if ((paramBoolean1) && (0 < k) && (' ' == arrayOfChar[0]))
    {
      j = 1;
      n++;
    }
    XMLStringFactory localXMLStringFactory = XMLStringFactoryImpl.getFactory();
    return j != 0 ? localXMLStringFactory.newstr(arrayOfChar, n, k - n) : this;
  }
  
  public double toDouble()
  {
    if (this.m_length == 0) {
      return NaN.0D;
    }
    String str = fsb().getString(this.m_start, this.m_length);
    for (int i = 0; (i < this.m_length) && (XMLCharacterRecognizer.isWhiteSpace(str.charAt(i))); i++) {}
    if (i == this.m_length) {
      return NaN.0D;
    }
    if (str.charAt(i) == '-') {
      i++;
    }
    while (i < this.m_length)
    {
      int j = str.charAt(i);
      if ((j != 46) && ((j < 48) || (j > 57))) {
        break;
      }
      i++;
    }
    while ((i < this.m_length) && (XMLCharacterRecognizer.isWhiteSpace(str.charAt(i)))) {
      i++;
    }
    if (i != this.m_length) {
      return NaN.0D;
    }
    try
    {
      return Double.parseDouble(str);
    }
    catch (NumberFormatException localNumberFormatException) {}
    return NaN.0D;
  }
}
