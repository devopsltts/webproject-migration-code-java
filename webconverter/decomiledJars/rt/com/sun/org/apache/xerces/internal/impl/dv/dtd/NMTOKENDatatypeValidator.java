package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLChar;

public class NMTOKENDatatypeValidator
  implements DatatypeValidator
{
  public NMTOKENDatatypeValidator() {}
  
  public void validate(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    if (!XMLChar.isValidNmtoken(paramString)) {
      throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[] { paramString });
    }
  }
}
