package sun.misc;

import java.util.Arrays;

public class FormattedFloatingDecimal
{
  private int decExponentRounded;
  private char[] mantissa;
  private char[] exponent;
  private static final ThreadLocal<Object> threadLocalCharBuffer = new ThreadLocal()
  {
    protected Object initialValue()
    {
      return new char[20];
    }
  };
  
  public static FormattedFloatingDecimal valueOf(double paramDouble, int paramInt, Form paramForm)
  {
    FloatingDecimal.BinaryToASCIIConverter localBinaryToASCIIConverter = FloatingDecimal.getBinaryToASCIIConverter(paramDouble, paramForm == Form.COMPATIBLE);
    return new FormattedFloatingDecimal(paramInt, paramForm, localBinaryToASCIIConverter);
  }
  
  private static char[] getBuffer()
  {
    return (char[])threadLocalCharBuffer.get();
  }
  
  private FormattedFloatingDecimal(int paramInt, Form paramForm, FloatingDecimal.BinaryToASCIIConverter paramBinaryToASCIIConverter)
  {
    if (paramBinaryToASCIIConverter.isExceptional())
    {
      this.mantissa = paramBinaryToASCIIConverter.toJavaFormatString().toCharArray();
      this.exponent = null;
      return;
    }
    char[] arrayOfChar = getBuffer();
    int i = paramBinaryToASCIIConverter.getDigits(arrayOfChar);
    int j = paramBinaryToASCIIConverter.getDecimalExponent();
    boolean bool = paramBinaryToASCIIConverter.isNegative();
    int k;
    switch (2.$SwitchMap$sun$misc$FormattedFloatingDecimal$Form[paramForm.ordinal()])
    {
    case 1: 
      k = j;
      this.decExponentRounded = k;
      fillCompatible(paramInt, arrayOfChar, i, k, bool);
      break;
    case 2: 
      k = applyPrecision(j, arrayOfChar, i, j + paramInt);
      fillDecimal(paramInt, arrayOfChar, i, k, bool);
      this.decExponentRounded = k;
      break;
    case 3: 
      k = applyPrecision(j, arrayOfChar, i, paramInt + 1);
      fillScientific(paramInt, arrayOfChar, i, k, bool);
      this.decExponentRounded = k;
      break;
    case 4: 
      k = applyPrecision(j, arrayOfChar, i, paramInt);
      if ((k - 1 < -4) || (k - 1 >= paramInt))
      {
        paramInt--;
        fillScientific(paramInt, arrayOfChar, i, k, bool);
      }
      else
      {
        paramInt -= k;
        fillDecimal(paramInt, arrayOfChar, i, k, bool);
      }
      this.decExponentRounded = k;
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
  }
  
  public int getExponentRounded()
  {
    return this.decExponentRounded - 1;
  }
  
  public char[] getMantissa()
  {
    return this.mantissa;
  }
  
  public char[] getExponent()
  {
    return this.exponent;
  }
  
  private static int applyPrecision(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
  {
    if ((paramInt3 >= paramInt2) || (paramInt3 < 0)) {
      return paramInt1;
    }
    if (paramInt3 == 0)
    {
      if (paramArrayOfChar[0] >= '5')
      {
        paramArrayOfChar[0] = '1';
        Arrays.fill(paramArrayOfChar, 1, paramInt2, '0');
        return paramInt1 + 1;
      }
      Arrays.fill(paramArrayOfChar, 0, paramInt2, '0');
      return paramInt1;
    }
    int i = paramArrayOfChar[paramInt3];
    if (i >= 53)
    {
      int j = paramInt3;
      i = paramArrayOfChar[(--j)];
      if (i == 57)
      {
        while ((i == 57) && (j > 0)) {
          i = paramArrayOfChar[(--j)];
        }
        if (i == 57)
        {
          paramArrayOfChar[0] = '1';
          Arrays.fill(paramArrayOfChar, 1, paramInt2, '0');
          return paramInt1 + 1;
        }
      }
      paramArrayOfChar[j] = ((char)(i + 1));
      Arrays.fill(paramArrayOfChar, j + 1, paramInt2, '0');
    }
    else
    {
      Arrays.fill(paramArrayOfChar, paramInt3, paramInt2, '0');
    }
    return paramInt1;
  }
  
  private void fillCompatible(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int i = paramBoolean ? 1 : 0;
    int j;
    if ((paramInt3 > 0) && (paramInt3 < 8))
    {
      if (paramInt2 < paramInt3)
      {
        j = paramInt3 - paramInt2;
        this.mantissa = create(paramBoolean, paramInt2 + j + 2);
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt2);
        Arrays.fill(this.mantissa, i + paramInt2, i + paramInt2 + j, '0');
        this.mantissa[(i + paramInt2 + j)] = '.';
        this.mantissa[(i + paramInt2 + j + 1)] = '0';
      }
      else if (paramInt3 < paramInt2)
      {
        j = Math.min(paramInt2 - paramInt3, paramInt1);
        this.mantissa = create(paramBoolean, paramInt3 + 1 + j);
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt3);
        this.mantissa[(i + paramInt3)] = '.';
        System.arraycopy(paramArrayOfChar, paramInt3, this.mantissa, i + paramInt3 + 1, j);
      }
      else
      {
        this.mantissa = create(paramBoolean, paramInt2 + 2);
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt2);
        this.mantissa[(i + paramInt2)] = '.';
        this.mantissa[(i + paramInt2 + 1)] = '0';
      }
    }
    else
    {
      int k;
      if ((paramInt3 <= 0) && (paramInt3 > -3))
      {
        j = Math.max(0, Math.min(-paramInt3, paramInt1));
        k = Math.max(0, Math.min(paramInt2, paramInt1 + paramInt3));
        if (j > 0)
        {
          this.mantissa = create(paramBoolean, j + 2 + k);
          this.mantissa[i] = '0';
          this.mantissa[(i + 1)] = '.';
          Arrays.fill(this.mantissa, i + 2, i + 2 + j, '0');
          if (k > 0) {
            System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2 + j, k);
          }
        }
        else if (k > 0)
        {
          this.mantissa = create(paramBoolean, j + 2 + k);
          this.mantissa[i] = '0';
          this.mantissa[(i + 1)] = '.';
          System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2, k);
        }
        else
        {
          this.mantissa = create(paramBoolean, 1);
          this.mantissa[i] = '0';
        }
      }
      else
      {
        if (paramInt2 > 1)
        {
          this.mantissa = create(paramBoolean, paramInt2 + 1);
          this.mantissa[i] = paramArrayOfChar[0];
          this.mantissa[(i + 1)] = '.';
          System.arraycopy(paramArrayOfChar, 1, this.mantissa, i + 2, paramInt2 - 1);
        }
        else
        {
          this.mantissa = create(paramBoolean, 3);
          this.mantissa[i] = paramArrayOfChar[0];
          this.mantissa[(i + 1)] = '.';
          this.mantissa[(i + 2)] = '0';
        }
        boolean bool = paramInt3 <= 0;
        if (bool)
        {
          j = -paramInt3 + 1;
          k = 1;
        }
        else
        {
          j = paramInt3 - 1;
          k = 0;
        }
        if (j <= 9)
        {
          this.exponent = create(bool, 1);
          this.exponent[k] = ((char)(j + 48));
        }
        else if (j <= 99)
        {
          this.exponent = create(bool, 2);
          this.exponent[k] = ((char)(j / 10 + 48));
          this.exponent[(k + 1)] = ((char)(j % 10 + 48));
        }
        else
        {
          this.exponent = create(bool, 3);
          this.exponent[k] = ((char)(j / 100 + 48));
          j %= 100;
          this.exponent[(k + 1)] = ((char)(j / 10 + 48));
          this.exponent[(k + 2)] = ((char)(j % 10 + 48));
        }
      }
    }
  }
  
  private static char[] create(boolean paramBoolean, int paramInt)
  {
    if (paramBoolean)
    {
      char[] arrayOfChar = new char[paramInt + 1];
      arrayOfChar[0] = '-';
      return arrayOfChar;
    }
    return new char[paramInt];
  }
  
  private void fillDecimal(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int i = paramBoolean ? 1 : 0;
    int j;
    if (paramInt3 > 0)
    {
      if (paramInt2 < paramInt3)
      {
        this.mantissa = create(paramBoolean, paramInt3);
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt2);
        Arrays.fill(this.mantissa, i + paramInt2, i + paramInt3, '0');
      }
      else
      {
        j = Math.min(paramInt2 - paramInt3, paramInt1);
        this.mantissa = create(paramBoolean, paramInt3 + (j > 0 ? j + 1 : 0));
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt3);
        if (j > 0)
        {
          this.mantissa[(i + paramInt3)] = '.';
          System.arraycopy(paramArrayOfChar, paramInt3, this.mantissa, i + paramInt3 + 1, j);
        }
      }
    }
    else if (paramInt3 <= 0)
    {
      j = Math.max(0, Math.min(-paramInt3, paramInt1));
      int k = Math.max(0, Math.min(paramInt2, paramInt1 + paramInt3));
      if (j > 0)
      {
        this.mantissa = create(paramBoolean, j + 2 + k);
        this.mantissa[i] = '0';
        this.mantissa[(i + 1)] = '.';
        Arrays.fill(this.mantissa, i + 2, i + 2 + j, '0');
        if (k > 0) {
          System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2 + j, k);
        }
      }
      else if (k > 0)
      {
        this.mantissa = create(paramBoolean, j + 2 + k);
        this.mantissa[i] = '0';
        this.mantissa[(i + 1)] = '.';
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2, k);
      }
      else
      {
        this.mantissa = create(paramBoolean, 1);
        this.mantissa[i] = '0';
      }
    }
  }
  
  private void fillScientific(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int i = paramBoolean ? 1 : 0;
    int j = Math.max(0, Math.min(paramInt2 - 1, paramInt1));
    if (j > 0)
    {
      this.mantissa = create(paramBoolean, j + 2);
      this.mantissa[i] = paramArrayOfChar[0];
      this.mantissa[(i + 1)] = '.';
      System.arraycopy(paramArrayOfChar, 1, this.mantissa, i + 2, j);
    }
    else
    {
      this.mantissa = create(paramBoolean, 1);
      this.mantissa[i] = paramArrayOfChar[0];
    }
    int k;
    int m;
    if (paramInt3 <= 0)
    {
      k = 45;
      m = -paramInt3 + 1;
    }
    else
    {
      k = 43;
      m = paramInt3 - 1;
    }
    if (m <= 9)
    {
      this.exponent = new char[] { k, '0', (char)(m + 48) };
    }
    else if (m <= 99)
    {
      this.exponent = new char[] { k, (char)(m / 10 + 48), (char)(m % 10 + 48) };
    }
    else
    {
      int n = (char)(m / 100 + 48);
      m %= 100;
      this.exponent = new char[] { k, n, (char)(m / 10 + 48), (char)(m % 10 + 48) };
    }
  }
  
  public static enum Form
  {
    SCIENTIFIC,  COMPATIBLE,  DECIMAL_FLOAT,  GENERAL;
    
    private Form() {}
  }
}
