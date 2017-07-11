package com.sun.xml.internal.ws.config.metro.dev;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public abstract interface FeatureReader<T extends WebServiceFeature>
{
  public static final QName ENABLED_ATTRIBUTE_NAME = new QName("enabled");
  
  public abstract T parse(XMLEventReader paramXMLEventReader)
    throws WebServiceException;
}
