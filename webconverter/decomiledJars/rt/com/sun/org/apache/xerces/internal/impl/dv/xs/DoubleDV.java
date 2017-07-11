package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSDouble;

public class DoubleDV
  extends TypeValidator
{
  public DoubleDV() {}
  
  public short getAllowedFacets()
  {
    return 2552;
  }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return new XDouble(paramString);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "double" });
    }
  }
  
  public int compare(Object paramObject1, Object paramObject2)
  {
    return ((XDouble)paramObject1).compareTo((XDouble)paramObject2);
  }
  
  public boolean isIdentical(Object paramObject1, Object paramObject2)
  {
    if ((paramObject2 instanceof XDouble)) {
      return ((XDouble)paramObject1).isIdentical((XDouble)paramObject2);
    }
    return false;
  }
  
  static boolean isPossibleFP(String paramString)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      if (((k < 48) || (k > 57)) && (k != 46) && (k != 45) && (k != 43) && (k != 69) && (k != 101)) {
        return false;
      }
    }
    return true;
  }
  
  private static final class XDouble
    implements XSDouble
  {
    private final double value;
    private String canonical;
    
    public XDouble(String paramString)
      throws NumberFormatException
    {
      if (DoubleDV.isPossibleFP(paramString)) {
        this.value = Double.parseDouble(paramString);
      } else if (paramString.equals("INF")) {
        this.value = Double.POSITIVE_INFINITY;
      } else if (paramString.equals("-INF")) {
        this.value = Double.NEGATIVE_INFINITY;
      } else if (paramString.equals("NaN")) {
        this.value = NaN.0D;
      } else {
        throw new NumberFormatException(paramString);
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof XDouble)) {
        return false;
      }
      XDouble localXDouble = (XDouble)paramObject;
      if (this.value == localXDouble.value) {
        return true;
      }
      return (this.value != this.value) && (localXDouble.value != localXDouble.value);
    }
    
    public int hashCode()
    {
      if (this.value == 0.0D) {
        return 0;
      }
      long l = Double.doubleToLongBits(this.value);
      return (int)(l ^ l >>> 32);
    }
    
    public boolean isIdentical(XDouble paramXDouble)
    {
      if (paramXDouble == this) {
        return true;
      }
      if (this.value == paramXDouble.value) {
        return (this.value != 0.0D) || (Double.doubleToLongBits(this.value) == Double.doubleToLongBits(paramXDouble.value));
      }
      return (this.value != this.value) && (paramXDouble.value != paramXDouble.value);
    }
    
    private int compareTo(XDouble paramXDouble)
    {
      double d = paramXDouble.value;
      if (this.value < d) {
        return -1;
      }
      if (this.value > d) {
        return 1;
      }
      if (this.value == d) {
        return 0;
      }
      if (this.value != this.value)
      {
        if (d != d) {
          return 0;
        }
        return 2;
      }
      return 2;
    }
    
    public synchronized String toString()
    {
      if (this.canonical == null) {
        if (this.value == Double.POSITIVE_INFINITY)
        {
          this.canonical = "INF";
        }
        else if (this.value == Double.NEGATIVE_INFINITY)
        {
          this.canonical = "-INF";
        }
        else if (this.value != this.value)
        {
          this.canonical = "NaN";
        }
        else if (this.value == 0.0D)
        {
          this.canonical = "0.0E1";
        }
        else
        {
          this.canonical = Double.toString(this.value);
          if (this.canonical.indexOf('E') == -1)
          {
            int i = this.canonical.length();
            char[] arrayOfChar = new char[i + 3];
            this.canonical.getChars(0, i, arrayOfChar, 0);
            int j = arrayOfChar[0] == '-' ? 2 : 1;
            int k;
            int m;
            if ((this.value >= 1.0D) || (this.value <= -1.0D))
            {
              k = this.canonical.indexOf('.');
              for (m = k; m > j; m--) {
                arrayOfChar[m] = arrayOfChar[(m - 1)];
              }
              arrayOfChar[j] = '.';
              while (arrayOfChar[(i - 1)] == '0') {
                i--;
              }
              if (arrayOfChar[(i - 1)] == '.') {
                i++;
              }
              arrayOfChar[(i++)] = 'E';
              m = k - j;
              arrayOfChar[(i++)] = ((char)(m + 48));
            }
            else
            {
              for (k = j + 1; arrayOfChar[k] == '0'; k++) {}
              arrayOfChar[(j - 1)] = arrayOfChar[k];
              arrayOfChar[j] = '.';
              m = k + 1;
              for (int n = j + 1; m < i; n++)
              {
                arrayOfChar[n] = arrayOfChar[m];
                m++;
              }
              i -= k - j;
              if (i == j + 1) {
                arrayOfChar[(i++)] = '0';
              }
              arrayOfChar[(i++)] = 'E';
              arrayOfChar[(i++)] = '-';
              m = k - j;
              arrayOfChar[(i++)] = ((char)(m + 48));
            }
            this.canonical = new String(arrayOfChar, 0, i);
          }
        }
      }
      return this.canonical;
    }
    
    public double getValue()
    {
      return this.value;
    }
  }
}
