package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLChar;

public class IDDatatypeValidator
  implements DatatypeValidator
{
  public IDDatatypeValidator() {}
  
  public void validate(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    if (paramValidationContext.useNamespaces())
    {
      if (!XMLChar.isValidNCName(paramString)) {
        throw new InvalidDatatypeValueException("IDInvalidWithNamespaces", new Object[] { paramString });
      }
    }
    else if (!XMLChar.isValidName(paramString)) {
      throw new InvalidDatatypeValueException("IDInvalid", new Object[] { paramString });
    }
    if (paramValidationContext.isIdDeclared(paramString)) {
      throw new InvalidDatatypeValueException("IDNotUnique", new Object[] { paramString });
    }
    paramValidationContext.addId(paramString);
  }
}
