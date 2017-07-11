package com.sun.corba.se.impl.presentation.rmi;

import org.omg.CORBA.Object;

public class StubFactoryStaticImpl
  extends StubFactoryBase
{
  private Class stubClass;
  
  public StubFactoryStaticImpl(Class paramClass)
  {
    super(null);
    this.stubClass = paramClass;
  }
  
  public Object makeStub()
  {
    Object localObject = null;
    try
    {
      localObject = (Object)this.stubClass.newInstance();
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new RuntimeException(localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
    return localObject;
  }
}
