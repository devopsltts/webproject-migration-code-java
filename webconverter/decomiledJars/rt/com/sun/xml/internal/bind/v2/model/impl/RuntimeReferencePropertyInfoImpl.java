package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

class RuntimeReferencePropertyInfoImpl
  extends ReferencePropertyInfoImpl<Type, Class, Field, Method>
  implements RuntimeReferencePropertyInfo
{
  private final Accessor acc;
  
  public RuntimeReferencePropertyInfoImpl(RuntimeClassInfoImpl paramRuntimeClassInfoImpl, PropertySeed<Type, Class, Field, Method> paramPropertySeed)
  {
    super(paramRuntimeClassInfoImpl, paramPropertySeed);
    Accessor localAccessor = ((RuntimeClassInfoImpl.RuntimePropertySeed)paramPropertySeed).getAccessor();
    if ((getAdapter() != null) && (!isCollection())) {
      localAccessor = localAccessor.adapt(getAdapter());
    }
    this.acc = localAccessor;
  }
  
  public Set<? extends RuntimeElement> getElements()
  {
    return super.getElements();
  }
  
  public Set<? extends RuntimeElement> ref()
  {
    return super.ref();
  }
  
  public Accessor getAccessor()
  {
    return this.acc;
  }
  
  public boolean elementOnlyContent()
  {
    return !isMixed();
  }
}
