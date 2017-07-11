package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSDecimal;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class DecimalDV
  extends TypeValidator
{
  public DecimalDV() {}
  
  public final short getAllowedFacets()
  {
    return 4088;
  }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return new XDecimal(paramString);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "decimal" });
    }
  }
  
  public final int compare(Object paramObject1, Object paramObject2)
  {
    return ((XDecimal)paramObject1).compareTo((XDecimal)paramObject2);
  }
  
  public final int getTotalDigits(Object paramObject)
  {
    return ((XDecimal)paramObject).totalDigits;
  }
  
  public final int getFractionDigits(Object paramObject)
  {
    return ((XDecimal)paramObject).fracDigits;
  }
  
  static final class XDecimal
    implements XSDecimal
  {
    int sign = 1;
    int totalDigits = 0;
    int intDigits = 0;
    int fracDigits = 0;
    String ivalue = "";
    String fvalue = "";
    boolean integer = false;
    private String canonical;
    
    XDecimal(String paramString)
      throws NumberFormatException
    {
      initD(paramString);
    }
    
    XDecimal(String paramString, boolean paramBoolean)
      throws NumberFormatException
    {
      if (paramBoolean) {
        initI(paramString);
      } else {
        initD(paramString);
      }
    }
    
    void initD(String paramString)
      throws NumberFormatException
    {
      int i = paramString.length();
      if (i == 0) {
        throw new NumberFormatException();
      }
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      if (paramString.charAt(0) == '+')
      {
        j = 1;
      }
      else if (paramString.charAt(0) == '-')
      {
        j = 1;
        this.sign = -1;
      }
      for (int i1 = j; (i1 < i) && (paramString.charAt(i1) == '0'); i1++) {}
      for (k = i1; (k < i) && (TypeValidator.isDigit(paramString.charAt(k))); k++) {}
      if (k < i)
      {
        if (paramString.charAt(k) != '.') {
          throw new NumberFormatException();
        }
        m = k + 1;
        n = i;
      }
      if ((j == k) && (m == n)) {
        throw new NumberFormatException();
      }
      while ((n > m) && (paramString.charAt(n - 1) == '0')) {
        n--;
      }
      for (int i2 = m; i2 < n; i2++) {
        if (!TypeValidator.isDigit(paramString.charAt(i2))) {
          throw new NumberFormatException();
        }
      }
      this.intDigits = (k - i1);
      this.fracDigits = (n - m);
      this.totalDigits = (this.intDigits + this.fracDigits);
      if (this.intDigits > 0)
      {
        this.ivalue = paramString.substring(i1, k);
        if (this.fracDigits > 0) {
          this.fvalue = paramString.substring(m, n);
        }
      }
      else if (this.fracDigits > 0)
      {
        this.fvalue = paramString.substring(m, n);
      }
      else
      {
        this.sign = 0;
      }
    }
    
    void initI(String paramString)
      throws NumberFormatException
    {
      int i = paramString.length();
      if (i == 0) {
        throw new NumberFormatException();
      }
      int j = 0;
      int k = 0;
      if (paramString.charAt(0) == '+')
      {
        j = 1;
      }
      else if (paramString.charAt(0) == '-')
      {
        j = 1;
        this.sign = -1;
      }
      for (int m = j; (m < i) && (paramString.charAt(m) == '0'); m++) {}
      for (k = m; (k < i) && (TypeValidator.isDigit(paramString.charAt(k))); k++) {}
      if (k < i) {
        throw new NumberFormatException();
      }
      if (j == k) {
        throw new NumberFormatException();
      }
      this.intDigits = (k - m);
      this.fracDigits = 0;
      this.totalDigits = this.intDigits;
      if (this.intDigits > 0) {
        this.ivalue = paramString.substring(m, k);
      } else {
        this.sign = 0;
      }
      this.integer = true;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof XDecimal)) {
        return false;
      }
      XDecimal localXDecimal = (XDecimal)paramObject;
      if (this.sign != localXDecimal.sign) {
        return false;
      }
      if (this.sign == 0) {
        return true;
      }
      return (this.intDigits == localXDecimal.intDigits) && (this.fracDigits == localXDecimal.fracDigits) && (this.ivalue.equals(localXDecimal.ivalue)) && (this.fvalue.equals(localXDecimal.fvalue));
    }
    
    public int hashCode()
    {
      int i = 7;
      i = 17 * i + this.sign;
      if (this.sign == 0) {
        return i;
      }
      i = 17 * i + this.intDigits;
      i = 17 * i + this.fracDigits;
      i = 17 * i + Objects.hashCode(this.ivalue);
      i = 17 * i + Objects.hashCode(this.fvalue);
      return i;
    }
    
    public int compareTo(XDecimal paramXDecimal)
    {
      if (this.sign != paramXDecimal.sign) {
        return this.sign > paramXDecimal.sign ? 1 : -1;
      }
      if (this.sign == 0) {
        return 0;
      }
      return this.sign * intComp(paramXDecimal);
    }
    
    private int intComp(XDecimal paramXDecimal)
    {
      if (this.intDigits != paramXDecimal.intDigits) {
        return this.intDigits > paramXDecimal.intDigits ? 1 : -1;
      }
      int i = this.ivalue.compareTo(paramXDecimal.ivalue);
      if (i != 0) {
        return i > 0 ? 1 : -1;
      }
      i = this.fvalue.compareTo(paramXDecimal.fvalue);
      return i > 0 ? 1 : i == 0 ? 0 : -1;
    }
    
    public synchronized String toString()
    {
      if (this.canonical == null) {
        makeCanonical();
      }
      return this.canonical;
    }
    
    private void makeCanonical()
    {
      if (this.sign == 0)
      {
        if (this.integer) {
          this.canonical = "0";
        } else {
          this.canonical = "0.0";
        }
        return;
      }
      if ((this.integer) && (this.sign > 0))
      {
        this.canonical = this.ivalue;
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder(this.totalDigits + 3);
      if (this.sign == -1) {
        localStringBuilder.append('-');
      }
      if (this.intDigits != 0) {
        localStringBuilder.append(this.ivalue);
      } else {
        localStringBuilder.append('0');
      }
      if (!this.integer)
      {
        localStringBuilder.append('.');
        if (this.fracDigits != 0) {
          localStringBuilder.append(this.fvalue);
        } else {
          localStringBuilder.append('0');
        }
      }
      this.canonical = localStringBuilder.toString();
    }
    
    public BigDecimal getBigDecimal()
    {
      if (this.sign == 0) {
        return new BigDecimal(BigInteger.ZERO);
      }
      return new BigDecimal(toString());
    }
    
    public BigInteger getBigInteger()
      throws NumberFormatException
    {
      if (this.fracDigits != 0) {
        throw new NumberFormatException();
      }
      if (this.sign == 0) {
        return BigInteger.ZERO;
      }
      if (this.sign == 1) {
        return new BigInteger(this.ivalue);
      }
      return new BigInteger("-" + this.ivalue);
    }
    
    public long getLong()
      throws NumberFormatException
    {
      if (this.fracDigits != 0) {
        throw new NumberFormatException();
      }
      if (this.sign == 0) {
        return 0L;
      }
      if (this.sign == 1) {
        return Long.parseLong(this.ivalue);
      }
      return Long.parseLong("-" + this.ivalue);
    }
    
    public int getInt()
      throws NumberFormatException
    {
      if (this.fracDigits != 0) {
        throw new NumberFormatException();
      }
      if (this.sign == 0) {
        return 0;
      }
      if (this.sign == 1) {
        return Integer.parseInt(this.ivalue);
      }
      return Integer.parseInt("-" + this.ivalue);
    }
    
    public short getShort()
      throws NumberFormatException
    {
      if (this.fracDigits != 0) {
        throw new NumberFormatException();
      }
      if (this.sign == 0) {
        return 0;
      }
      if (this.sign == 1) {
        return Short.parseShort(this.ivalue);
      }
      return Short.parseShort("-" + this.ivalue);
    }
    
    public byte getByte()
      throws NumberFormatException
    {
      if (this.fracDigits != 0) {
        throw new NumberFormatException();
      }
      if (this.sign == 0) {
        return 0;
      }
      if (this.sign == 1) {
        return Byte.parseByte(this.ivalue);
      }
      return Byte.parseByte("-" + this.ivalue);
    }
  }
}
