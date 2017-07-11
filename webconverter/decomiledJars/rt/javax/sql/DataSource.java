package javax.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Wrapper;

public abstract interface DataSource
  extends CommonDataSource, Wrapper
{
  public abstract Connection getConnection()
    throws SQLException;
  
  public abstract Connection getConnection(String paramString1, String paramString2)
    throws SQLException;
}
