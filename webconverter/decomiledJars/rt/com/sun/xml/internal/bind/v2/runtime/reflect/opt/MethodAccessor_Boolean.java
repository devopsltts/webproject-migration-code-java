package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Boolean
  extends Accessor
{
  public MethodAccessor_Boolean()
  {
    super(Boolean.class);
  }
  
  public Object get(Object paramObject)
  {
    return Boolean.valueOf(((Bean)paramObject).get_boolean());
  }
  
  public void set(Object paramObject1, Object paramObject2)
  {
    ((Bean)paramObject1).set_boolean(paramObject2 == null ? Const.default_value_boolean : ((Boolean)paramObject2).booleanValue());
  }
}
