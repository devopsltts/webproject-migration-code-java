package javax.print.attribute;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;

public abstract class EnumSyntax
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = -2739521845085831642L;
  private int value;
  
  protected EnumSyntax(int paramInt)
  {
    this.value = paramInt;
  }
  
  public int getValue()
  {
    return this.value;
  }
  
  public Object clone()
  {
    return this;
  }
  
  public int hashCode()
  {
    return this.value;
  }
  
  public String toString()
  {
    String[] arrayOfString = getStringTable();
    int i = this.value - getOffset();
    return (arrayOfString != null) && (i >= 0) && (i < arrayOfString.length) ? arrayOfString[i] : Integer.toString(this.value);
  }
  
  protected Object readResolve()
    throws ObjectStreamException
  {
    EnumSyntax[] arrayOfEnumSyntax = getEnumValueTable();
    if (arrayOfEnumSyntax == null) {
      throw new InvalidObjectException("Null enumeration value table for class " + getClass());
    }
    int i = getOffset();
    int j = this.value - i;
    if ((0 > j) || (j >= arrayOfEnumSyntax.length)) {
      throw new InvalidObjectException("Integer value = " + this.value + " not in valid range " + i + ".." + (i + arrayOfEnumSyntax.length - 1) + "for class " + getClass());
    }
    EnumSyntax localEnumSyntax = arrayOfEnumSyntax[j];
    if (localEnumSyntax == null) {
      throw new InvalidObjectException("No enumeration value for integer value = " + this.value + "for class " + getClass());
    }
    return localEnumSyntax;
  }
  
  protected String[] getStringTable()
  {
    return null;
  }
  
  protected EnumSyntax[] getEnumValueTable()
  {
    return null;
  }
  
  protected int getOffset()
  {
    return 0;
  }
}
