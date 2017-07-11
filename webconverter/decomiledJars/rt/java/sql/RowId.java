package java.sql;

public abstract interface RowId
{
  public abstract boolean equals(Object paramObject);
  
  public abstract byte[] getBytes();
  
  public abstract String toString();
  
  public abstract int hashCode();
}
