package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.runtime.Coordinator;
import javax.xml.bind.annotation.adapters.XmlAdapter;

final class AdaptedAccessor<BeanT, InMemValueT, OnWireValueT>
  extends Accessor<BeanT, OnWireValueT>
{
  private final Accessor<BeanT, InMemValueT> core;
  private final Class<? extends XmlAdapter<OnWireValueT, InMemValueT>> adapter;
  private XmlAdapter<OnWireValueT, InMemValueT> staticAdapter;
  
  AdaptedAccessor(Class<OnWireValueT> paramClass, Accessor<BeanT, InMemValueT> paramAccessor, Class<? extends XmlAdapter<OnWireValueT, InMemValueT>> paramClass1)
  {
    super(paramClass);
    this.core = paramAccessor;
    this.adapter = paramClass1;
  }
  
  public boolean isAdapted()
  {
    return true;
  }
  
  public OnWireValueT get(BeanT paramBeanT)
    throws AccessorException
  {
    Object localObject = this.core.get(paramBeanT);
    XmlAdapter localXmlAdapter = getAdapter();
    try
    {
      return localXmlAdapter.marshal(localObject);
    }
    catch (Exception localException)
    {
      throw new AccessorException(localException);
    }
  }
  
  public void set(BeanT paramBeanT, OnWireValueT paramOnWireValueT)
    throws AccessorException
  {
    XmlAdapter localXmlAdapter = getAdapter();
    try
    {
      this.core.set(paramBeanT, paramOnWireValueT == null ? null : localXmlAdapter.unmarshal(paramOnWireValueT));
    }
    catch (Exception localException)
    {
      throw new AccessorException(localException);
    }
  }
  
  public Object getUnadapted(BeanT paramBeanT)
    throws AccessorException
  {
    return this.core.getUnadapted(paramBeanT);
  }
  
  public void setUnadapted(BeanT paramBeanT, Object paramObject)
    throws AccessorException
  {
    this.core.setUnadapted(paramBeanT, paramObject);
  }
  
  private XmlAdapter<OnWireValueT, InMemValueT> getAdapter()
  {
    Coordinator localCoordinator = Coordinator._getInstance();
    if (localCoordinator != null) {
      return localCoordinator.getAdapter(this.adapter);
    }
    synchronized (this)
    {
      if (this.staticAdapter == null) {
        this.staticAdapter = ((XmlAdapter)ClassFactory.create(this.adapter));
      }
    }
    return this.staticAdapter;
  }
}
