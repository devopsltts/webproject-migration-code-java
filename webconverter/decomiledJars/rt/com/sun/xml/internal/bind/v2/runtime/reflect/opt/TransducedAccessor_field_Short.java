package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_field_Short
  extends DefaultTransducedAccessor
{
  public TransducedAccessor_field_Short() {}
  
  public String print(Object paramObject)
  {
    return DatatypeConverterImpl._printShort(((Bean)paramObject).f_short);
  }
  
  public void parse(Object paramObject, CharSequence paramCharSequence)
  {
    ((Bean)paramObject).f_short = DatatypeConverterImpl._parseShort(paramCharSequence);
  }
  
  public boolean hasValue(Object paramObject)
  {
    return true;
  }
}
