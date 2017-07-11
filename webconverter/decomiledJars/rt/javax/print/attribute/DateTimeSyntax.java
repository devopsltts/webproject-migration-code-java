package javax.print.attribute;

import java.io.Serializable;
import java.util.Date;

public abstract class DateTimeSyntax
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = -1400819079791208582L;
  private Date value;
  
  protected DateTimeSyntax(Date paramDate)
  {
    if (paramDate == null) {
      throw new NullPointerException("value is null");
    }
    this.value = paramDate;
  }
  
  public Date getValue()
  {
    return new Date(this.value.getTime());
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof DateTimeSyntax)) && (this.value.equals(((DateTimeSyntax)paramObject).value));
  }
  
  public int hashCode()
  {
    return this.value.hashCode();
  }
  
  public String toString()
  {
    return "" + this.value;
  }
}
