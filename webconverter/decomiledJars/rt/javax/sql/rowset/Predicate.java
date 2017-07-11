package javax.sql.rowset;

import java.sql.SQLException;
import javax.sql.RowSet;

public abstract interface Predicate
{
  public abstract boolean evaluate(RowSet paramRowSet);
  
  public abstract boolean evaluate(Object paramObject, int paramInt)
    throws SQLException;
  
  public abstract boolean evaluate(Object paramObject, String paramString)
    throws SQLException;
}
