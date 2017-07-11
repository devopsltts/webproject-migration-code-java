package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XML11Char;

public class XML11NMTOKENDatatypeValidator
  extends NMTOKENDatatypeValidator
{
  public XML11NMTOKENDatatypeValidator() {}
  
  public void validate(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    if (!XML11Char.isXML11ValidNmtoken(paramString)) {
      throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[] { paramString });
    }
  }
}
