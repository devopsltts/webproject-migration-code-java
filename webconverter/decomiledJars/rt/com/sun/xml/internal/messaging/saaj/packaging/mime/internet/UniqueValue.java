package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

class UniqueValue
{
  private static int part = 0;
  
  UniqueValue() {}
  
  public static String getUniqueBoundaryValue()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("----=_Part_").append(part++).append("_").append(localStringBuffer.hashCode()).append('.').append(System.currentTimeMillis());
    return localStringBuffer.toString();
  }
}
