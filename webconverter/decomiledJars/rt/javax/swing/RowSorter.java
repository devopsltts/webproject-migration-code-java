package javax.swing;

import java.util.List;
import javax.swing.event.EventListenerList;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterEvent.Type;
import javax.swing.event.RowSorterListener;

public abstract class RowSorter<M>
{
  private EventListenerList listenerList = new EventListenerList();
  
  public RowSorter() {}
  
  public abstract M getModel();
  
  public abstract void toggleSortOrder(int paramInt);
  
  public abstract int convertRowIndexToModel(int paramInt);
  
  public abstract int convertRowIndexToView(int paramInt);
  
  public abstract void setSortKeys(List<? extends SortKey> paramList);
  
  public abstract List<? extends SortKey> getSortKeys();
  
  public abstract int getViewRowCount();
  
  public abstract int getModelRowCount();
  
  public abstract void modelStructureChanged();
  
  public abstract void allRowsChanged();
  
  public abstract void rowsInserted(int paramInt1, int paramInt2);
  
  public abstract void rowsDeleted(int paramInt1, int paramInt2);
  
  public abstract void rowsUpdated(int paramInt1, int paramInt2);
  
  public abstract void rowsUpdated(int paramInt1, int paramInt2, int paramInt3);
  
  public void addRowSorterListener(RowSorterListener paramRowSorterListener)
  {
    this.listenerList.add(RowSorterListener.class, paramRowSorterListener);
  }
  
  public void removeRowSorterListener(RowSorterListener paramRowSorterListener)
  {
    this.listenerList.remove(RowSorterListener.class, paramRowSorterListener);
  }
  
  protected void fireSortOrderChanged()
  {
    fireRowSorterChanged(new RowSorterEvent(this));
  }
  
  protected void fireRowSorterChanged(int[] paramArrayOfInt)
  {
    fireRowSorterChanged(new RowSorterEvent(this, RowSorterEvent.Type.SORTED, paramArrayOfInt));
  }
  
  void fireRowSorterChanged(RowSorterEvent paramRowSorterEvent)
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == RowSorterListener.class) {
        ((RowSorterListener)arrayOfObject[(i + 1)]).sorterChanged(paramRowSorterEvent);
      }
    }
  }
  
  public static class SortKey
  {
    private int column;
    private SortOrder sortOrder;
    
    public SortKey(int paramInt, SortOrder paramSortOrder)
    {
      if (paramSortOrder == null) {
        throw new IllegalArgumentException("sort order must be non-null");
      }
      this.column = paramInt;
      this.sortOrder = paramSortOrder;
    }
    
    public final int getColumn()
    {
      return this.column;
    }
    
    public final SortOrder getSortOrder()
    {
      return this.sortOrder;
    }
    
    public int hashCode()
    {
      int i = 17;
      i = 37 * i + this.column;
      i = 37 * i + this.sortOrder.hashCode();
      return i;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof SortKey)) {
        return (((SortKey)paramObject).column == this.column) && (((SortKey)paramObject).sortOrder == this.sortOrder);
      }
      return false;
    }
  }
}
