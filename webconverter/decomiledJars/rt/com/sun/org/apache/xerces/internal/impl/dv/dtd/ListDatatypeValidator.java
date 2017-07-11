package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.util.StringTokenizer;

public class ListDatatypeValidator
  implements DatatypeValidator
{
  DatatypeValidator fItemValidator;
  
  public ListDatatypeValidator(DatatypeValidator paramDatatypeValidator)
  {
    this.fItemValidator = paramDatatypeValidator;
  }
  
  public void validate(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ");
    int i = localStringTokenizer.countTokens();
    if (i == 0) {
      throw new InvalidDatatypeValueException("EmptyList", null);
    }
    while (localStringTokenizer.hasMoreTokens()) {
      this.fItemValidator.validate(localStringTokenizer.nextToken(), paramValidationContext);
    }
  }
}
