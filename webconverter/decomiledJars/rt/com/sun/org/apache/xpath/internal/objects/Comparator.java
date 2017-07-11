package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.XMLString;

abstract class Comparator
{
  Comparator() {}
  
  abstract boolean compareStrings(XMLString paramXMLString1, XMLString paramXMLString2);
  
  abstract boolean compareNumbers(double paramDouble1, double paramDouble2);
}
