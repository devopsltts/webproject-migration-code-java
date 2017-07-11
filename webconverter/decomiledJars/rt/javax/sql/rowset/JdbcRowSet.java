package javax.sql.rowset;

import java.sql.SQLException;
import java.sql.Savepoint;
import javax.sql.RowSet;

public abstract interface JdbcRowSet
  extends RowSet, Joinable
{
  public abstract boolean getShowDeleted()
    throws SQLException;
  
  public abstract void setShowDeleted(boolean paramBoolean)
    throws SQLException;
  
  public abstract RowSetWarning getRowSetWarnings()
    throws SQLException;
  
  public abstract void commit()
    throws SQLException;
  
  public abstract boolean getAutoCommit()
    throws SQLException;
  
  public abstract void setAutoCommit(boolean paramBoolean)
    throws SQLException;
  
  public abstract void rollback()
    throws SQLException;
  
  public abstract void rollback(Savepoint paramSavepoint)
    throws SQLException;
}
