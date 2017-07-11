package com.sun.org.apache.xml.internal.serializer;

final class ElemContext
{
  final int m_currentElemDepth;
  ElemDesc m_elementDesc = null;
  String m_elementLocalName = null;
  String m_elementName = null;
  String m_elementURI = null;
  boolean m_isCdataSection;
  boolean m_isRaw = false;
  private ElemContext m_next;
  final ElemContext m_prev;
  boolean m_startTagOpen = false;
  
  ElemContext()
  {
    this.m_prev = this;
    this.m_currentElemDepth = 0;
  }
  
  private ElemContext(ElemContext paramElemContext)
  {
    this.m_prev = paramElemContext;
    paramElemContext.m_currentElemDepth += 1;
  }
  
  final ElemContext pop()
  {
    return this.m_prev;
  }
  
  final ElemContext push()
  {
    ElemContext localElemContext = this.m_next;
    if (localElemContext == null)
    {
      localElemContext = new ElemContext(this);
      this.m_next = localElemContext;
    }
    localElemContext.m_startTagOpen = true;
    return localElemContext;
  }
  
  final ElemContext push(String paramString1, String paramString2, String paramString3)
  {
    ElemContext localElemContext = this.m_next;
    if (localElemContext == null)
    {
      localElemContext = new ElemContext(this);
      this.m_next = localElemContext;
    }
    localElemContext.m_elementName = paramString3;
    localElemContext.m_elementLocalName = paramString2;
    localElemContext.m_elementURI = paramString1;
    localElemContext.m_isCdataSection = false;
    localElemContext.m_startTagOpen = true;
    return localElemContext;
  }
}
