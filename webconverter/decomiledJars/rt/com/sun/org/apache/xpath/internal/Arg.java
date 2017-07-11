package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Objects;

public class Arg
{
  private QName m_qname;
  private XObject m_val;
  private String m_expression;
  private boolean m_isFromWithParam;
  private boolean m_isVisible;
  
  public final QName getQName()
  {
    return this.m_qname;
  }
  
  public final void setQName(QName paramQName)
  {
    this.m_qname = paramQName;
  }
  
  public final XObject getVal()
  {
    return this.m_val;
  }
  
  public final void setVal(XObject paramXObject)
  {
    this.m_val = paramXObject;
  }
  
  public void detach()
  {
    if (null != this.m_val)
    {
      this.m_val.allowDetachToRelease(true);
      this.m_val.detach();
    }
  }
  
  public String getExpression()
  {
    return this.m_expression;
  }
  
  public void setExpression(String paramString)
  {
    this.m_expression = paramString;
  }
  
  public boolean isFromWithParam()
  {
    return this.m_isFromWithParam;
  }
  
  public boolean isVisible()
  {
    return this.m_isVisible;
  }
  
  public void setIsVisible(boolean paramBoolean)
  {
    this.m_isVisible = paramBoolean;
  }
  
  public Arg()
  {
    this.m_qname = new QName("");
    this.m_val = null;
    this.m_expression = null;
    this.m_isVisible = true;
    this.m_isFromWithParam = false;
  }
  
  public Arg(QName paramQName, String paramString, boolean paramBoolean)
  {
    this.m_qname = paramQName;
    this.m_val = null;
    this.m_expression = paramString;
    this.m_isFromWithParam = paramBoolean;
    this.m_isVisible = (!paramBoolean);
  }
  
  public Arg(QName paramQName, XObject paramXObject)
  {
    this.m_qname = paramQName;
    this.m_val = paramXObject;
    this.m_isVisible = true;
    this.m_isFromWithParam = false;
    this.m_expression = null;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(this.m_qname);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof QName)) {
      return this.m_qname.equals(paramObject);
    }
    return super.equals(paramObject);
  }
  
  public Arg(QName paramQName, XObject paramXObject, boolean paramBoolean)
  {
    this.m_qname = paramQName;
    this.m_val = paramXObject;
    this.m_isFromWithParam = paramBoolean;
    this.m_isVisible = (!paramBoolean);
    this.m_expression = null;
  }
}
