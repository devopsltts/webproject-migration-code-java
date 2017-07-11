package javax.swing.table;

import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public abstract class AbstractTableModel
  implements TableModel, Serializable
{
  protected EventListenerList listenerList = new EventListenerList();
  
  public AbstractTableModel() {}
  
  public String getColumnName(int paramInt)
  {
    String str = "";
    while (paramInt >= 0)
    {
      str = (char)((char)(paramInt % 26) + 'A') + str;
      paramInt = paramInt / 26 - 1;
    }
    return str;
  }
  
  public int findColumn(String paramString)
  {
    for (int i = 0; i < getColumnCount(); i++) {
      if (paramString.equals(getColumnName(i))) {
        return i;
      }
    }
    return -1;
  }
  
  public Class<?> getColumnClass(int paramInt)
  {
    return Object.class;
  }
  
  public boolean isCellEditable(int paramInt1, int paramInt2)
  {
    return false;
  }
  
  public void setValueAt(Object paramObject, int paramInt1, int paramInt2) {}
  
  public void addTableModelListener(TableModelListener paramTableModelListener)
  {
    this.listenerList.add(TableModelListener.class, paramTableModelListener);
  }
  
  public void removeTableModelListener(TableModelListener paramTableModelListener)
  {
    this.listenerList.remove(TableModelListener.class, paramTableModelListener);
  }
  
  public TableModelListener[] getTableModelListeners()
  {
    return (TableModelListener[])this.listenerList.getListeners(TableModelListener.class);
  }
  
  public void fireTableDataChanged()
  {
    fireTableChanged(new TableModelEvent(this));
  }
  
  public void fireTableStructureChanged()
  {
    fireTableChanged(new TableModelEvent(this, -1));
  }
  
  public void fireTableRowsInserted(int paramInt1, int paramInt2)
  {
    fireTableChanged(new TableModelEvent(this, paramInt1, paramInt2, -1, 1));
  }
  
  public void fireTableRowsUpdated(int paramInt1, int paramInt2)
  {
    fireTableChanged(new TableModelEvent(this, paramInt1, paramInt2, -1, 0));
  }
  
  public void fireTableRowsDeleted(int paramInt1, int paramInt2)
  {
    fireTableChanged(new TableModelEvent(this, paramInt1, paramInt2, -1, -1));
  }
  
  public void fireTableCellUpdated(int paramInt1, int paramInt2)
  {
    fireTableChanged(new TableModelEvent(this, paramInt1, paramInt1, paramInt2));
  }
  
  public void fireTableChanged(TableModelEvent paramTableModelEvent)
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TableModelListener.class) {
        ((TableModelListener)arrayOfObject[(i + 1)]).tableChanged(paramTableModelEvent);
      }
    }
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return this.listenerList.getListeners(paramClass);
  }
}
