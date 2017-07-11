package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import java.util.Iterator;
import java.util.List;
import javax.jws.soap.SOAPBinding.Style;

public class StubAsyncHandler
  extends StubHandler
{
  private final Class asyncBeanClass;
  
  public StubAsyncHandler(JavaMethodImpl paramJavaMethodImpl1, JavaMethodImpl paramJavaMethodImpl2, MessageContextFactory paramMessageContextFactory)
  {
    super(paramJavaMethodImpl2, paramMessageContextFactory);
    List localList = paramJavaMethodImpl2.getResponseParameters();
    int i = 0;
    Object localObject1 = localList.iterator();
    Object localObject2;
    Object localObject3;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (ParameterImpl)((Iterator)localObject1).next();
      if (((ParameterImpl)localObject2).isWrapperStyle())
      {
        localObject3 = (WrapperParameter)localObject2;
        i += ((WrapperParameter)localObject3).getWrapperChildren().size();
        if (paramJavaMethodImpl2.getBinding().getStyle() == SOAPBinding.Style.DOCUMENT) {
          i += 2;
        }
      }
      else
      {
        i++;
      }
    }
    localObject1 = null;
    if (i > 1)
    {
      localList = paramJavaMethodImpl1.getResponseParameters();
      localObject2 = localList.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (ParameterImpl)((Iterator)localObject2).next();
        if (((ParameterImpl)localObject3).isWrapperStyle())
        {
          WrapperParameter localWrapperParameter = (WrapperParameter)localObject3;
          if (paramJavaMethodImpl2.getBinding().getStyle() == SOAPBinding.Style.DOCUMENT)
          {
            localObject1 = (Class)localWrapperParameter.getTypeInfo().type;
          }
          else
          {
            Iterator localIterator = localWrapperParameter.getWrapperChildren().iterator();
            while (localIterator.hasNext())
            {
              ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
              if (localParameterImpl.getIndex() == -1)
              {
                localObject1 = (Class)localParameterImpl.getTypeInfo().type;
                break;
              }
            }
            if (localObject1 != null) {
              break;
            }
          }
        }
        else if (((ParameterImpl)localObject3).getIndex() == -1)
        {
          localObject1 = (Class)((ParameterImpl)localObject3).getTypeInfo().type;
          break;
        }
      }
    }
    this.asyncBeanClass = ((Class)localObject1);
    switch (i)
    {
    case 0: 
      this.responseBuilder = buildResponseBuilder(paramJavaMethodImpl2, ValueSetterFactory.NONE);
      break;
    case 1: 
      this.responseBuilder = buildResponseBuilder(paramJavaMethodImpl2, ValueSetterFactory.SINGLE);
      break;
    default: 
      this.responseBuilder = buildResponseBuilder(paramJavaMethodImpl2, new ValueSetterFactory.AsyncBeanValueSetterFactory(this.asyncBeanClass));
    }
  }
  
  protected void initArgs(Object[] paramArrayOfObject)
    throws Exception
  {
    if (this.asyncBeanClass != null) {
      paramArrayOfObject[0] = this.asyncBeanClass.newInstance();
    }
  }
  
  ValueGetterFactory getValueGetterFactory()
  {
    return ValueGetterFactory.ASYNC;
  }
}
