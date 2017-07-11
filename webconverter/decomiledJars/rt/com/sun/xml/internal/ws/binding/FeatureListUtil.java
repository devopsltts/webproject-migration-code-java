package com.sun.xml.internal.ws.binding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public class FeatureListUtil
{
  public FeatureListUtil() {}
  
  @NotNull
  public static WebServiceFeatureList mergeList(WebServiceFeatureList... paramVarArgs)
  {
    WebServiceFeatureList localWebServiceFeatureList1 = new WebServiceFeatureList();
    for (WebServiceFeatureList localWebServiceFeatureList2 : paramVarArgs) {
      localWebServiceFeatureList1.addAll(localWebServiceFeatureList2);
    }
    return localWebServiceFeatureList1;
  }
  
  @Nullable
  public static <F extends WebServiceFeature> F mergeFeature(@NotNull Class<F> paramClass, @Nullable WebServiceFeatureList paramWebServiceFeatureList1, @Nullable WebServiceFeatureList paramWebServiceFeatureList2)
    throws WebServiceException
  {
    F ? = paramWebServiceFeatureList1 != null ? paramWebServiceFeatureList1.get(paramClass) : null;
    F ? = paramWebServiceFeatureList2 != null ? paramWebServiceFeatureList2.get(paramClass) : null;
    if (? == null) {
      return ?;
    }
    if (? == null) {
      return ?;
    }
    if (?.equals(?)) {
      return ?;
    }
    throw new WebServiceException(? + ", " + ?);
  }
  
  public static boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass, @Nullable WebServiceFeatureList paramWebServiceFeatureList1, @Nullable WebServiceFeatureList paramWebServiceFeatureList2)
    throws WebServiceException
  {
    WebServiceFeature localWebServiceFeature = mergeFeature(paramClass, paramWebServiceFeatureList1, paramWebServiceFeatureList2);
    return (localWebServiceFeature != null) && (localWebServiceFeature.isEnabled());
  }
}
