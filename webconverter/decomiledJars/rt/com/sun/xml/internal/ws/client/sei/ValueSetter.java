package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public abstract class ValueSetter
{
  private static final ValueSetter RETURN_VALUE = new ReturnValue(null);
  private static final ValueSetter[] POOL = new ValueSetter[16];
  static final ValueSetter SINGLE_VALUE = new SingleValue(null);
  
  private ValueSetter() {}
  
  abstract Object put(Object paramObject, Object[] paramArrayOfObject);
  
  static ValueSetter getSync(ParameterImpl paramParameterImpl)
  {
    int i = paramParameterImpl.getIndex();
    if (i == -1) {
      return RETURN_VALUE;
    }
    if (i < POOL.length) {
      return POOL[i];
    }
    return new Param(i);
  }
  
  static
  {
    for (int i = 0; i < POOL.length; i++) {
      POOL[i] = new Param(i);
    }
  }
  
  static final class AsyncBeanValueSetter
    extends ValueSetter
  {
    private final PropertyAccessor accessor;
    
    AsyncBeanValueSetter(ParameterImpl paramParameterImpl, Class paramClass)
    {
      super();
      QName localQName = paramParameterImpl.getName();
      try
      {
        this.accessor = paramParameterImpl.getOwner().getBindingContext().getElementPropertyAccessor(paramClass, localQName.getNamespaceURI(), localQName.getLocalPart());
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(paramClass + " do not have a property of the name " + localQName, localJAXBException);
      }
    }
    
    Object put(Object paramObject, Object[] paramArrayOfObject)
    {
      assert (paramArrayOfObject != null);
      assert (paramArrayOfObject.length == 1);
      assert (paramArrayOfObject[0] != null);
      Object localObject = paramArrayOfObject[0];
      try
      {
        this.accessor.set(localObject, paramObject);
      }
      catch (Exception localException)
      {
        throw new WebServiceException(localException);
      }
      return null;
    }
  }
  
  static final class Param
    extends ValueSetter
  {
    private final int idx;
    
    public Param(int paramInt)
    {
      super();
      this.idx = paramInt;
    }
    
    Object put(Object paramObject, Object[] paramArrayOfObject)
    {
      Object localObject = paramArrayOfObject[this.idx];
      if (localObject != null)
      {
        assert ((localObject instanceof Holder));
        ((Holder)localObject).value = paramObject;
      }
      return null;
    }
  }
  
  private static final class ReturnValue
    extends ValueSetter
  {
    private ReturnValue()
    {
      super();
    }
    
    Object put(Object paramObject, Object[] paramArrayOfObject)
    {
      return paramObject;
    }
  }
  
  private static final class SingleValue
    extends ValueSetter
  {
    private SingleValue()
    {
      super();
    }
    
    Object put(Object paramObject, Object[] paramArrayOfObject)
    {
      paramArrayOfObject[0] = paramObject;
      return null;
    }
  }
}
