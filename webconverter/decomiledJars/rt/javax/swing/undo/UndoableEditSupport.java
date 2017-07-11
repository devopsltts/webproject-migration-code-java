package javax.swing.undo;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

public class UndoableEditSupport
{
  protected int updateLevel = 0;
  protected CompoundEdit compoundEdit = null;
  protected Vector<UndoableEditListener> listeners = new Vector();
  protected Object realSource = paramObject == null ? this : paramObject;
  
  public UndoableEditSupport()
  {
    this(null);
  }
  
  public UndoableEditSupport(Object paramObject) {}
  
  public synchronized void addUndoableEditListener(UndoableEditListener paramUndoableEditListener)
  {
    this.listeners.addElement(paramUndoableEditListener);
  }
  
  public synchronized void removeUndoableEditListener(UndoableEditListener paramUndoableEditListener)
  {
    this.listeners.removeElement(paramUndoableEditListener);
  }
  
  public synchronized UndoableEditListener[] getUndoableEditListeners()
  {
    return (UndoableEditListener[])this.listeners.toArray(new UndoableEditListener[0]);
  }
  
  protected void _postEdit(UndoableEdit paramUndoableEdit)
  {
    UndoableEditEvent localUndoableEditEvent = new UndoableEditEvent(this.realSource, paramUndoableEdit);
    Enumeration localEnumeration = ((Vector)this.listeners.clone()).elements();
    while (localEnumeration.hasMoreElements()) {
      ((UndoableEditListener)localEnumeration.nextElement()).undoableEditHappened(localUndoableEditEvent);
    }
  }
  
  public synchronized void postEdit(UndoableEdit paramUndoableEdit)
  {
    if (this.updateLevel == 0) {
      _postEdit(paramUndoableEdit);
    } else {
      this.compoundEdit.addEdit(paramUndoableEdit);
    }
  }
  
  public int getUpdateLevel()
  {
    return this.updateLevel;
  }
  
  public synchronized void beginUpdate()
  {
    if (this.updateLevel == 0) {
      this.compoundEdit = createCompoundEdit();
    }
    this.updateLevel += 1;
  }
  
  protected CompoundEdit createCompoundEdit()
  {
    return new CompoundEdit();
  }
  
  public synchronized void endUpdate()
  {
    this.updateLevel -= 1;
    if (this.updateLevel == 0)
    {
      this.compoundEdit.end();
      _postEdit(this.compoundEdit);
      this.compoundEdit = null;
    }
  }
  
  public String toString()
  {
    return super.toString() + " updateLevel: " + this.updateLevel + " listeners: " + this.listeners + " compoundEdit: " + this.compoundEdit;
  }
}
