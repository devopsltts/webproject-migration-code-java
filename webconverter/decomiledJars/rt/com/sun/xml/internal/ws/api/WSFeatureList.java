package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceFeature;

public abstract interface WSFeatureList
  extends Iterable<WebServiceFeature>
{
  public abstract boolean isEnabled(@NotNull Class<? extends WebServiceFeature> paramClass);
  
  @Nullable
  public abstract <F extends WebServiceFeature> F get(@NotNull Class<F> paramClass);
  
  @NotNull
  public abstract WebServiceFeature[] toArray();
  
  public abstract void mergeFeatures(@NotNull WebServiceFeature[] paramArrayOfWebServiceFeature, boolean paramBoolean);
  
  public abstract void mergeFeatures(@NotNull Iterable<WebServiceFeature> paramIterable, boolean paramBoolean);
}
