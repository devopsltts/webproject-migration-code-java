package com.sun.xml.internal.ws.api;

import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.lang.annotation.Annotation;
import javax.xml.ws.WebServiceFeature;

public class WebServiceFeatureFactory
{
  public WebServiceFeatureFactory() {}
  
  public static WSFeatureList getWSFeatureList(Iterable<Annotation> paramIterable)
  {
    WebServiceFeatureList localWebServiceFeatureList = new WebServiceFeatureList();
    localWebServiceFeatureList.parseAnnotations(paramIterable);
    return localWebServiceFeatureList;
  }
  
  public static WebServiceFeature getWebServiceFeature(Annotation paramAnnotation)
  {
    return WebServiceFeatureList.getFeature(paramAnnotation);
  }
}
