package javax.swing.event;

import java.util.EventListener;

public abstract interface UndoableEditListener
  extends EventListener
{
  public abstract void undoableEditHappened(UndoableEditEvent paramUndoableEditEvent);
}
