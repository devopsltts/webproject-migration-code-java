package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLChar;

public class EntityDV
  extends TypeValidator
{
  public EntityDV() {}
  
  public short getAllowedFacets()
  {
    return 2079;
  }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    if (!XMLChar.isValidNCName(paramString)) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "NCName" });
    }
    return paramString;
  }
  
  public void checkExtraRules(Object paramObject, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    if (!paramValidationContext.isEntityUnparsed((String)paramObject)) {
      throw new InvalidDatatypeValueException("UndeclaredEntity", new Object[] { paramObject });
    }
  }
}
