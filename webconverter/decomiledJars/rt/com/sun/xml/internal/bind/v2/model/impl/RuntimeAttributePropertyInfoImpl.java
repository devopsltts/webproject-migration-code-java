package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

class RuntimeAttributePropertyInfoImpl
  extends AttributePropertyInfoImpl<Type, Class, Field, Method>
  implements RuntimeAttributePropertyInfo
{
  RuntimeAttributePropertyInfoImpl(RuntimeClassInfoImpl paramRuntimeClassInfoImpl, PropertySeed<Type, Class, Field, Method> paramPropertySeed)
  {
    super(paramRuntimeClassInfoImpl, paramPropertySeed);
  }
  
  public boolean elementOnlyContent()
  {
    return true;
  }
  
  public RuntimeNonElement getTarget()
  {
    return (RuntimeNonElement)super.getTarget();
  }
  
  public List<? extends RuntimeNonElement> ref()
  {
    return super.ref();
  }
  
  public RuntimePropertyInfo getSource()
  {
    return this;
  }
  
  public void link()
  {
    getTransducer();
    super.link();
  }
}
