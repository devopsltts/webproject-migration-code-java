package java.sql;

public abstract interface Savepoint
{
  public abstract int getSavepointId()
    throws SQLException;
  
  public abstract String getSavepointName()
    throws SQLException;
}
