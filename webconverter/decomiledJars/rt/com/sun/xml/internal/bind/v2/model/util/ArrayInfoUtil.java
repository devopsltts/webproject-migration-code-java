package com.sun.xml.internal.bind.v2.model.util;

import com.sun.xml.internal.bind.v2.TODO;
import javax.xml.namespace.QName;

public class ArrayInfoUtil
{
  private ArrayInfoUtil() {}
  
  public static QName calcArrayTypeName(QName paramQName)
  {
    String str;
    if (paramQName.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema"))
    {
      TODO.checkSpec("this URI");
      str = "http://jaxb.dev.java.net/array";
    }
    else
    {
      str = paramQName.getNamespaceURI();
    }
    return new QName(str, paramQName.getLocalPart() + "Array");
  }
}
