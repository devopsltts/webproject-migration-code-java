package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.XMLString;

class EqualComparator
  extends Comparator
{
  EqualComparator() {}
  
  boolean compareStrings(XMLString paramXMLString1, XMLString paramXMLString2)
  {
    return paramXMLString1.equals(paramXMLString2);
  }
  
  boolean compareNumbers(double paramDouble1, double paramDouble2)
  {
    return paramDouble1 == paramDouble2;
  }
}
