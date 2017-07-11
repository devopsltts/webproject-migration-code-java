package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.jws.WebParam.Mode;
import javax.xml.namespace.QName;

public class SOAPSEIModel
  extends AbstractSEIModelImpl
{
  public SOAPSEIModel(WebServiceFeatureList paramWebServiceFeatureList)
  {
    super(paramWebServiceFeatureList);
  }
  
  protected void populateMaps()
  {
    int i = 0;
    Iterator localIterator1 = getJavaMethods().iterator();
    while (localIterator1.hasNext())
    {
      JavaMethodImpl localJavaMethodImpl = (JavaMethodImpl)localIterator1.next();
      put(localJavaMethodImpl.getMethod(), localJavaMethodImpl);
      int j = 0;
      Iterator localIterator2 = localJavaMethodImpl.getRequestParameters().iterator();
      while (localIterator2.hasNext())
      {
        ParameterImpl localParameterImpl = (ParameterImpl)localIterator2.next();
        ParameterBinding localParameterBinding = localParameterImpl.getBinding();
        if (localParameterBinding.isBody())
        {
          put(localParameterImpl.getName(), localJavaMethodImpl);
          j = 1;
        }
      }
      if (j == 0)
      {
        put(this.emptyBodyName, localJavaMethodImpl);
        i++;
      }
    }
    if (i > 1) {}
  }
  
  public Set<QName> getKnownHeaders()
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator1 = getJavaMethods().iterator();
    while (localIterator1.hasNext())
    {
      JavaMethodImpl localJavaMethodImpl = (JavaMethodImpl)localIterator1.next();
      Iterator localIterator2 = localJavaMethodImpl.getRequestParameters().iterator();
      fillHeaders(localIterator2, localHashSet, WebParam.Mode.IN);
      localIterator2 = localJavaMethodImpl.getResponseParameters().iterator();
      fillHeaders(localIterator2, localHashSet, WebParam.Mode.OUT);
    }
    return localHashSet;
  }
  
  private void fillHeaders(Iterator<ParameterImpl> paramIterator, Set<QName> paramSet, WebParam.Mode paramMode)
  {
    while (paramIterator.hasNext())
    {
      ParameterImpl localParameterImpl = (ParameterImpl)paramIterator.next();
      ParameterBinding localParameterBinding = paramMode == WebParam.Mode.IN ? localParameterImpl.getInBinding() : localParameterImpl.getOutBinding();
      QName localQName = localParameterImpl.getName();
      if ((localParameterBinding.isHeader()) && (!paramSet.contains(localQName))) {
        paramSet.add(localQName);
      }
    }
  }
}
