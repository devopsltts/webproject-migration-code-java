package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceFeature;

public abstract class ServiceInterceptor
{
  public ServiceInterceptor() {}
  
  public List<WebServiceFeature> preCreateBinding(@NotNull WSPortInfo paramWSPortInfo, @Nullable Class<?> paramClass, @NotNull WSFeatureList paramWSFeatureList)
  {
    return Collections.emptyList();
  }
  
  public void postCreateProxy(@NotNull WSBindingProvider paramWSBindingProvider, @NotNull Class<?> paramClass) {}
  
  public void postCreateDispatch(@NotNull WSBindingProvider paramWSBindingProvider) {}
  
  public static ServiceInterceptor aggregate(ServiceInterceptor... paramVarArgs)
  {
    if (paramVarArgs.length == 1) {
      return paramVarArgs[0];
    }
    new ServiceInterceptor()
    {
      public List<WebServiceFeature> preCreateBinding(@NotNull WSPortInfo paramAnonymousWSPortInfo, @Nullable Class<?> paramAnonymousClass, @NotNull WSFeatureList paramAnonymousWSFeatureList)
      {
        ArrayList localArrayList = new ArrayList();
        for (ServiceInterceptor localServiceInterceptor : this.val$interceptors) {
          localArrayList.addAll(localServiceInterceptor.preCreateBinding(paramAnonymousWSPortInfo, paramAnonymousClass, paramAnonymousWSFeatureList));
        }
        return localArrayList;
      }
      
      public void postCreateProxy(@NotNull WSBindingProvider paramAnonymousWSBindingProvider, @NotNull Class<?> paramAnonymousClass)
      {
        for (ServiceInterceptor localServiceInterceptor : this.val$interceptors) {
          localServiceInterceptor.postCreateProxy(paramAnonymousWSBindingProvider, paramAnonymousClass);
        }
      }
      
      public void postCreateDispatch(@NotNull WSBindingProvider paramAnonymousWSBindingProvider)
      {
        for (ServiceInterceptor localServiceInterceptor : this.val$interceptors) {
          localServiceInterceptor.postCreateDispatch(paramAnonymousWSBindingProvider);
        }
      }
    };
  }
}
