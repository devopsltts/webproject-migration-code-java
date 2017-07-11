package javax.sql.rowset;

import java.sql.SQLException;

public abstract interface FilteredRowSet
  extends WebRowSet
{
  public abstract void setFilter(Predicate paramPredicate)
    throws SQLException;
  
  public abstract Predicate getFilter();
}
