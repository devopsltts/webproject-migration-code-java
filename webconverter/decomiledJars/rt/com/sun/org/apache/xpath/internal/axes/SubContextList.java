package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.XPathContext;

public abstract interface SubContextList
{
  public abstract int getLastPos(XPathContext paramXPathContext);
  
  public abstract int getProximityPosition(XPathContext paramXPathContext);
}
