package javax.swing.event;

import java.util.EventListener;

public abstract interface ListDataListener
  extends EventListener
{
  public abstract void intervalAdded(ListDataEvent paramListDataEvent);
  
  public abstract void intervalRemoved(ListDataEvent paramListDataEvent);
  
  public abstract void contentsChanged(ListDataEvent paramListDataEvent);
}
