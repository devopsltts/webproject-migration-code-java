package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Long
  extends Accessor
{
  public FieldAccessor_Long()
  {
    super(Long.class);
  }
  
  public Object get(Object paramObject)
  {
    return Long.valueOf(((Bean)paramObject).f_long);
  }
  
  public void set(Object paramObject1, Object paramObject2)
  {
    ((Bean)paramObject1).f_long = (paramObject2 == null ? Const.default_value_long : ((Long)paramObject2).longValue());
  }
}
