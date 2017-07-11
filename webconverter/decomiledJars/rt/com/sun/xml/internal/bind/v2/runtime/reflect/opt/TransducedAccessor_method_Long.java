package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_method_Long
  extends DefaultTransducedAccessor
{
  public TransducedAccessor_method_Long() {}
  
  public String print(Object paramObject)
  {
    return DatatypeConverterImpl._printLong(((Bean)paramObject).get_long());
  }
  
  public void parse(Object paramObject, CharSequence paramCharSequence)
  {
    ((Bean)paramObject).set_long(DatatypeConverterImpl._parseLong(paramCharSequence));
  }
  
  public boolean hasValue(Object paramObject)
  {
    return true;
  }
}
