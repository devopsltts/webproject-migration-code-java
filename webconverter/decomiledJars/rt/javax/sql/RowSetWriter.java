package javax.sql;

import java.sql.SQLException;

public abstract interface RowSetWriter
{
  public abstract boolean writeData(RowSetInternal paramRowSetInternal)
    throws SQLException;
}
