package com.sun.org.apache.xml.internal.dtm;

import org.w3c.dom.DOMException;

public class DTMDOMException
  extends DOMException
{
  static final long serialVersionUID = 1895654266613192414L;
  
  public DTMDOMException(short paramShort, String paramString)
  {
    super(paramShort, paramString);
  }
  
  public DTMDOMException(short paramShort)
  {
    super(paramShort, "");
  }
}
