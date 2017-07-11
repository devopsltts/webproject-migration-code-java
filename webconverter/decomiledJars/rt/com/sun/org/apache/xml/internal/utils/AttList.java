package com.sun.org.apache.xml.internal.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;

public class AttList
  implements Attributes
{
  NamedNodeMap m_attrs;
  int m_lastIndex;
  DOMHelper m_dh;
  
  public AttList(NamedNodeMap paramNamedNodeMap, DOMHelper paramDOMHelper)
  {
    this.m_attrs = paramNamedNodeMap;
    this.m_lastIndex = (this.m_attrs.getLength() - 1);
    this.m_dh = paramDOMHelper;
  }
  
  public int getLength()
  {
    return this.m_attrs.getLength();
  }
  
  public String getURI(int paramInt)
  {
    String str = this.m_dh.getNamespaceOfNode((Attr)this.m_attrs.item(paramInt));
    if (null == str) {
      str = "";
    }
    return str;
  }
  
  public String getLocalName(int paramInt)
  {
    return this.m_dh.getLocalNameOfNode((Attr)this.m_attrs.item(paramInt));
  }
  
  public String getQName(int paramInt)
  {
    return ((Attr)this.m_attrs.item(paramInt)).getName();
  }
  
  public String getType(int paramInt)
  {
    return "CDATA";
  }
  
  public String getValue(int paramInt)
  {
    return ((Attr)this.m_attrs.item(paramInt)).getValue();
  }
  
  public String getType(String paramString)
  {
    return "CDATA";
  }
  
  public String getType(String paramString1, String paramString2)
  {
    return "CDATA";
  }
  
  public String getValue(String paramString)
  {
    Attr localAttr = (Attr)this.m_attrs.getNamedItem(paramString);
    return null != localAttr ? localAttr.getValue() : null;
  }
  
  public String getValue(String paramString1, String paramString2)
  {
    Node localNode = this.m_attrs.getNamedItemNS(paramString1, paramString2);
    return localNode == null ? null : localNode.getNodeValue();
  }
  
  public int getIndex(String paramString1, String paramString2)
  {
    for (int i = this.m_attrs.getLength() - 1; i >= 0; i--)
    {
      Node localNode = this.m_attrs.item(i);
      String str = localNode.getNamespaceURI();
      if ((str == null ? paramString1 == null : str.equals(paramString1)) && (localNode.getLocalName().equals(paramString2))) {
        return i;
      }
    }
    return -1;
  }
  
  public int getIndex(String paramString)
  {
    for (int i = this.m_attrs.getLength() - 1; i >= 0; i--)
    {
      Node localNode = this.m_attrs.item(i);
      if (localNode.getNodeName().equals(paramString)) {
        return i;
      }
    }
    return -1;
  }
}
