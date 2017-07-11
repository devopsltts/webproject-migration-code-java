package javax.swing.event;

import java.util.EventListener;

public abstract interface CellEditorListener
  extends EventListener
{
  public abstract void editingStopped(ChangeEvent paramChangeEvent);
  
  public abstract void editingCanceled(ChangeEvent paramChangeEvent);
}
