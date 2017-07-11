package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

final class WildcardTypeImpl
  implements WildcardType
{
  private final Type[] ub;
  private final Type[] lb;
  
  public WildcardTypeImpl(Type[] paramArrayOfType1, Type[] paramArrayOfType2)
  {
    this.ub = paramArrayOfType1;
    this.lb = paramArrayOfType2;
  }
  
  public Type[] getUpperBounds()
  {
    return this.ub;
  }
  
  public Type[] getLowerBounds()
  {
    return this.lb;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(this.lb) ^ Arrays.hashCode(this.ub);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof WildcardType))
    {
      WildcardType localWildcardType = (WildcardType)paramObject;
      return (Arrays.equals(localWildcardType.getLowerBounds(), this.lb)) && (Arrays.equals(localWildcardType.getUpperBounds(), this.ub));
    }
    return false;
  }
}
