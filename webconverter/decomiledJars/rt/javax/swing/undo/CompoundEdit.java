package javax.swing.undo;

import java.util.Enumeration;
import java.util.Vector;

public class CompoundEdit
  extends AbstractUndoableEdit
{
  boolean inProgress = true;
  protected Vector<UndoableEdit> edits = new Vector();
  
  public CompoundEdit() {}
  
  public void undo()
    throws CannotUndoException
  {
    super.undo();
    int i = this.edits.size();
    while (i-- > 0)
    {
      UndoableEdit localUndoableEdit = (UndoableEdit)this.edits.elementAt(i);
      localUndoableEdit.undo();
    }
  }
  
  public void redo()
    throws CannotRedoException
  {
    super.redo();
    Enumeration localEnumeration = this.edits.elements();
    while (localEnumeration.hasMoreElements()) {
      ((UndoableEdit)localEnumeration.nextElement()).redo();
    }
  }
  
  protected UndoableEdit lastEdit()
  {
    int i = this.edits.size();
    if (i > 0) {
      return (UndoableEdit)this.edits.elementAt(i - 1);
    }
    return null;
  }
  
  public void die()
  {
    int i = this.edits.size();
    for (int j = i - 1; j >= 0; j--)
    {
      UndoableEdit localUndoableEdit = (UndoableEdit)this.edits.elementAt(j);
      localUndoableEdit.die();
    }
    super.die();
  }
  
  public boolean addEdit(UndoableEdit paramUndoableEdit)
  {
    if (!this.inProgress) {
      return false;
    }
    UndoableEdit localUndoableEdit = lastEdit();
    if (localUndoableEdit == null)
    {
      this.edits.addElement(paramUndoableEdit);
    }
    else if (!localUndoableEdit.addEdit(paramUndoableEdit))
    {
      if (paramUndoableEdit.replaceEdit(localUndoableEdit)) {
        this.edits.removeElementAt(this.edits.size() - 1);
      }
      this.edits.addElement(paramUndoableEdit);
    }
    return true;
  }
  
  public void end()
  {
    this.inProgress = false;
  }
  
  public boolean canUndo()
  {
    return (!isInProgress()) && (super.canUndo());
  }
  
  public boolean canRedo()
  {
    return (!isInProgress()) && (super.canRedo());
  }
  
  public boolean isInProgress()
  {
    return this.inProgress;
  }
  
  public boolean isSignificant()
  {
    Enumeration localEnumeration = this.edits.elements();
    while (localEnumeration.hasMoreElements()) {
      if (((UndoableEdit)localEnumeration.nextElement()).isSignificant()) {
        return true;
      }
    }
    return false;
  }
  
  public String getPresentationName()
  {
    UndoableEdit localUndoableEdit = lastEdit();
    if (localUndoableEdit != null) {
      return localUndoableEdit.getPresentationName();
    }
    return super.getPresentationName();
  }
  
  public String getUndoPresentationName()
  {
    UndoableEdit localUndoableEdit = lastEdit();
    if (localUndoableEdit != null) {
      return localUndoableEdit.getUndoPresentationName();
    }
    return super.getUndoPresentationName();
  }
  
  public String getRedoPresentationName()
  {
    UndoableEdit localUndoableEdit = lastEdit();
    if (localUndoableEdit != null) {
      return localUndoableEdit.getRedoPresentationName();
    }
    return super.getRedoPresentationName();
  }
  
  public String toString()
  {
    return super.toString() + " inProgress: " + this.inProgress + " edits: " + this.edits;
  }
}
