package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import javax.xml.transform.TransformerException;

public class XNumber
  extends XObject
{
  static final long serialVersionUID = -2720400709619020193L;
  double m_val;
  
  public XNumber(double paramDouble)
  {
    this.m_val = paramDouble;
  }
  
  public XNumber(Number paramNumber)
  {
    this.m_val = paramNumber.doubleValue();
    setObject(paramNumber);
  }
  
  public int getType()
  {
    return 2;
  }
  
  public String getTypeString()
  {
    return "#NUMBER";
  }
  
  public double num()
  {
    return this.m_val;
  }
  
  public double num(XPathContext paramXPathContext)
    throws TransformerException
  {
    return this.m_val;
  }
  
  public boolean bool()
  {
    return (!Double.isNaN(this.m_val)) && (this.m_val != 0.0D);
  }
  
  public String str()
  {
    if (Double.isNaN(this.m_val)) {
      return "NaN";
    }
    if (Double.isInfinite(this.m_val))
    {
      if (this.m_val > 0.0D) {
        return "Infinity";
      }
      return "-Infinity";
    }
    double d = this.m_val;
    String str1 = Double.toString(d);
    int i = str1.length();
    if ((str1.charAt(i - 2) == '.') && (str1.charAt(i - 1) == '0'))
    {
      str1 = str1.substring(0, i - 2);
      if (str1.equals("-0")) {
        return "0";
      }
      return str1;
    }
    int j = str1.indexOf('E');
    if (j < 0)
    {
      if (str1.charAt(i - 1) == '0') {
        return str1.substring(0, i - 1);
      }
      return str1;
    }
    int k = Integer.parseInt(str1.substring(j + 1));
    String str2;
    if (str1.charAt(0) == '-')
    {
      str2 = "-";
      str1 = str1.substring(1);
      j--;
    }
    else
    {
      str2 = "";
    }
    int m = j - 2;
    if (k >= m) {
      return str2 + str1.substring(0, 1) + str1.substring(2, j) + zeros(k - m);
    }
    while (str1.charAt(j - 1) == '0') {
      j--;
    }
    if (k > 0) {
      return str2 + str1.substring(0, 1) + str1.substring(2, 2 + k) + "." + str1.substring(2 + k, j);
    }
    return str2 + "0." + zeros(-1 - k) + str1.substring(0, 1) + str1.substring(2, j);
  }
  
  private static String zeros(int paramInt)
  {
    if (paramInt < 1) {
      return "";
    }
    char[] arrayOfChar = new char[paramInt];
    for (int i = 0; i < paramInt; i++) {
      arrayOfChar[i] = '0';
    }
    return new String(arrayOfChar);
  }
  
  public Object object()
  {
    if (null == this.m_obj) {
      setObject(new Double(this.m_val));
    }
    return this.m_obj;
  }
  
  public boolean equals(XObject paramXObject)
  {
    int i = paramXObject.getType();
    try
    {
      if (i == 4) {
        return paramXObject.equals(this);
      }
      if (i == 1) {
        return paramXObject.bool() == bool();
      }
      return this.m_val == paramXObject.num();
    }
    catch (TransformerException localTransformerException)
    {
      throw new WrappedRuntimeException(localTransformerException);
    }
  }
  
  public boolean isStableNumber()
  {
    return true;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    paramXPathVisitor.visitNumberLiteral(paramExpressionOwner, this);
  }
}
