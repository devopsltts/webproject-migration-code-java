package javax.sql.rowset.spi;

import java.sql.SQLException;
import java.sql.Savepoint;
import javax.sql.RowSetWriter;

public abstract interface TransactionalWriter
  extends RowSetWriter
{
  public abstract void commit()
    throws SQLException;
  
  public abstract void rollback()
    throws SQLException;
  
  public abstract void rollback(Savepoint paramSavepoint)
    throws SQLException;
}
