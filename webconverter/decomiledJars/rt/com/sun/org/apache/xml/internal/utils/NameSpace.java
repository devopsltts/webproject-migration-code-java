package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;

public class NameSpace
  implements Serializable
{
  static final long serialVersionUID = 1471232939184881839L;
  public NameSpace m_next = null;
  public String m_prefix;
  public String m_uri;
  
  public NameSpace(String paramString1, String paramString2)
  {
    this.m_prefix = paramString1;
    this.m_uri = paramString2;
  }
}
