package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import java.util.Vector;

public abstract class NodeCounter
{
  public static final int END = -1;
  protected int _node = -1;
  protected int _nodeType = -1;
  protected double _value = -2.147483648E9D;
  public final DOM _document;
  public final DTMAxisIterator _iterator;
  public final Translet _translet;
  protected String _format;
  protected String _lang;
  protected String _letterValue;
  protected String _groupSep;
  protected int _groupSize;
  private boolean _separFirst = true;
  private boolean _separLast = false;
  private Vector _separToks = new Vector();
  private Vector _formatToks = new Vector();
  private int _nSepars = 0;
  private int _nFormats = 0;
  private static final String[] Thousands = { "", "m", "mm", "mmm" };
  private static final String[] Hundreds = { "", "c", "cc", "ccc", "cd", "d", "dc", "dcc", "dccc", "cm" };
  private static final String[] Tens = { "", "x", "xx", "xxx", "xl", "l", "lx", "lxx", "lxxx", "xc" };
  private static final String[] Ones = { "", "i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix" };
  private StringBuilder _tempBuffer = new StringBuilder();
  protected boolean _hasFrom;
  
  protected NodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator)
  {
    this._translet = paramTranslet;
    this._document = paramDOM;
    this._iterator = paramDTMAxisIterator;
  }
  
  protected NodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean)
  {
    this._translet = paramTranslet;
    this._document = paramDOM;
    this._iterator = paramDTMAxisIterator;
    this._hasFrom = paramBoolean;
  }
  
  public abstract NodeCounter setStartNode(int paramInt);
  
  public NodeCounter setValue(double paramDouble)
  {
    this._value = paramDouble;
    return this;
  }
  
  protected void setFormatting(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    this._lang = paramString2;
    this._groupSep = paramString4;
    this._letterValue = paramString3;
    this._groupSize = parseStringToAnInt(paramString5);
    setTokens(paramString1);
  }
  
  private int parseStringToAnInt(String paramString)
  {
    if (paramString == null) {
      return 0;
    }
    int i = 0;
    int j = 0;
    int k = 10;
    int m = 0;
    int n = paramString.length();
    if (n > 0)
    {
      int i1;
      if (paramString.charAt(0) == '-')
      {
        j = 1;
        i1 = Integer.MIN_VALUE;
        m++;
      }
      else
      {
        i1 = -2147483647;
      }
      int i2 = i1 / k;
      int i3;
      if (m < n)
      {
        i3 = Character.digit(paramString.charAt(m++), k);
        if (i3 < 0) {
          return 0;
        }
        i = -i3;
      }
      while (m < n)
      {
        i3 = Character.digit(paramString.charAt(m++), k);
        if (i3 < 0) {
          return 0;
        }
        if (i < i2) {
          return 0;
        }
        i *= k;
        if (i < i1 + i3) {
          return 0;
        }
        i -= i3;
      }
    }
    return 0;
    if (j != 0)
    {
      if (m > 1) {
        return i;
      }
      return 0;
    }
    return -i;
  }
  
  private final void setTokens(String paramString)
  {
    if ((this._format != null) && (paramString.equals(this._format))) {
      return;
    }
    this._format = paramString;
    int i = this._format.length();
    int j = 1;
    this._separFirst = true;
    this._separLast = false;
    this._nSepars = 0;
    this._nFormats = 0;
    this._separToks.clear();
    this._formatToks.clear();
    int k = 0;
    int m = 0;
    while (m < i)
    {
      char c = paramString.charAt(m);
      k = m;
      while (Character.isLetterOrDigit(c))
      {
        m++;
        if (m == i) {
          break;
        }
        c = paramString.charAt(m);
      }
      if (m > k)
      {
        if (j != 0)
        {
          this._separToks.addElement(".");
          j = this._separFirst = 0;
        }
        this._formatToks.addElement(paramString.substring(k, m));
      }
      if (m == i) {
        break;
      }
      c = paramString.charAt(m);
      k = m;
      while (!Character.isLetterOrDigit(c))
      {
        m++;
        if (m == i) {
          break;
        }
        c = paramString.charAt(m);
        j = 0;
      }
      if (m > k) {
        this._separToks.addElement(paramString.substring(k, m));
      }
    }
    this._nSepars = this._separToks.size();
    this._nFormats = this._formatToks.size();
    if (this._nSepars > this._nFormats) {
      this._separLast = true;
    }
    if (this._separFirst) {
      this._nSepars -= 1;
    }
    if (this._separLast) {
      this._nSepars -= 1;
    }
    if (this._nSepars == 0)
    {
      this._separToks.insertElementAt(".", 1);
      this._nSepars += 1;
    }
    if (this._separFirst) {
      this._nSepars += 1;
    }
  }
  
  public NodeCounter setDefaultFormatting()
  {
    setFormatting("1", "en", "alphabetic", null, null);
    return this;
  }
  
  public abstract String getCounter();
  
  public String getCounter(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    setFormatting(paramString1, paramString2, paramString3, paramString4, paramString5);
    return getCounter();
  }
  
  public boolean matchesCount(int paramInt)
  {
    return this._nodeType == this._document.getExpandedTypeID(paramInt);
  }
  
  public boolean matchesFrom(int paramInt)
  {
    return false;
  }
  
  protected String formatNumbers(int paramInt)
  {
    return formatNumbers(new int[] { paramInt });
  }
  
  protected String formatNumbers(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length;
    int j = 1;
    for (int k = 0; k < i; k++) {
      if (paramArrayOfInt[k] != Integer.MIN_VALUE) {
        j = 0;
      }
    }
    if (j != 0) {
      return "";
    }
    k = 1;
    int m = 0;
    int n = 0;
    int i1 = 1;
    this._tempBuffer.setLength(0);
    StringBuilder localStringBuilder = this._tempBuffer;
    if (this._separFirst) {
      localStringBuilder.append((String)this._separToks.elementAt(0));
    }
    while (n < i)
    {
      int i2 = paramArrayOfInt[n];
      if (i2 != Integer.MIN_VALUE)
      {
        if (k == 0) {
          localStringBuilder.append((String)this._separToks.elementAt(i1++));
        }
        formatValue(i2, (String)this._formatToks.elementAt(m++), localStringBuilder);
        if (m == this._nFormats) {
          m--;
        }
        if (i1 >= this._nSepars) {
          i1--;
        }
        k = 0;
      }
      n++;
    }
    if (this._separLast) {
      localStringBuilder.append((String)this._separToks.lastElement());
    }
    return localStringBuilder.toString();
  }
  
  private void formatValue(int paramInt, String paramString, StringBuilder paramStringBuilder)
  {
    StringBuilder localStringBuilder1 = paramString.charAt(0);
    int i;
    StringBuilder localStringBuilder2;
    if (Character.isDigit(localStringBuilder1))
    {
      i = (char)(localStringBuilder1 - Character.getNumericValue(localStringBuilder1));
      localStringBuilder2 = paramStringBuilder;
      if (this._groupSize > 0) {
        localStringBuilder2 = new StringBuilder();
      }
      String str = "";
      int k = paramInt;
      while (k > 0)
      {
        str = (char)(i + k % 10) + str;
        k /= 10;
      }
      for (int m = 0; m < paramString.length() - str.length(); m++) {
        localStringBuilder2.append(i);
      }
      localStringBuilder2.append(str);
      if (this._groupSize > 0) {
        for (m = 0; m < localStringBuilder2.length(); m++)
        {
          if ((m != 0) && ((localStringBuilder2.length() - m) % this._groupSize == 0)) {
            paramStringBuilder.append(this._groupSep);
          }
          paramStringBuilder.append(localStringBuilder2.charAt(m));
        }
      }
    }
    else if ((localStringBuilder1 == 105) && (!this._letterValue.equals("alphabetic")))
    {
      paramStringBuilder.append(romanValue(paramInt));
    }
    else if ((localStringBuilder1 == 73) && (!this._letterValue.equals("alphabetic")))
    {
      paramStringBuilder.append(romanValue(paramInt).toUpperCase());
    }
    else
    {
      i = localStringBuilder1;
      localStringBuilder2 = localStringBuilder1;
      int j;
      if ((localStringBuilder1 >= 945) && (localStringBuilder1 <= 969)) {
        j = 969;
      } else {
        while (Character.isLetterOrDigit((char)(j + 1))) {
          j++;
        }
      }
      paramStringBuilder.append(alphaValue(paramInt, i, j));
    }
  }
  
  private String alphaValue(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 <= 0) {
      return "" + paramInt1;
    }
    int i = paramInt3 - paramInt2 + 1;
    char c = (char)((paramInt1 - 1) % i + paramInt2);
    if (paramInt1 > i) {
      return alphaValue((paramInt1 - 1) / i, paramInt2, paramInt3) + c;
    }
    return "" + c;
  }
  
  private String romanValue(int paramInt)
  {
    if ((paramInt <= 0) || (paramInt > 4000)) {
      return "" + paramInt;
    }
    return Thousands[(paramInt / 1000)] + Hundreds[(paramInt / 100 % 10)] + Tens[(paramInt / 10 % 10)] + Ones[(paramInt % 10)];
  }
}
