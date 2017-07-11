package javax.swing.undo;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class StateEdit
  extends AbstractUndoableEdit
{
  protected static final String RCSID = "$Id: StateEdit.java,v 1.6 1997/10/01 20:05:51 sandipc Exp $";
  protected StateEditable object;
  protected Hashtable<Object, Object> preState;
  protected Hashtable<Object, Object> postState;
  protected String undoRedoName;
  
  public StateEdit(StateEditable paramStateEditable)
  {
    init(paramStateEditable, null);
  }
  
  public StateEdit(StateEditable paramStateEditable, String paramString)
  {
    init(paramStateEditable, paramString);
  }
  
  protected void init(StateEditable paramStateEditable, String paramString)
  {
    this.object = paramStateEditable;
    this.preState = new Hashtable(11);
    this.object.storeState(this.preState);
    this.postState = null;
    this.undoRedoName = paramString;
  }
  
  public void end()
  {
    this.postState = new Hashtable(11);
    this.object.storeState(this.postState);
    removeRedundantState();
  }
  
  public void undo()
  {
    super.undo();
    this.object.restoreState(this.preState);
  }
  
  public void redo()
  {
    super.redo();
    this.object.restoreState(this.postState);
  }
  
  public String getPresentationName()
  {
    return this.undoRedoName;
  }
  
  protected void removeRedundantState()
  {
    Vector localVector = new Vector();
    Enumeration localEnumeration = this.preState.keys();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject1 = localEnumeration.nextElement();
      if ((this.postState.containsKey(localObject1)) && (this.postState.get(localObject1).equals(this.preState.get(localObject1)))) {
        localVector.addElement(localObject1);
      }
    }
    for (int i = localVector.size() - 1; i >= 0; i--)
    {
      Object localObject2 = localVector.elementAt(i);
      this.preState.remove(localObject2);
      this.postState.remove(localObject2);
    }
  }
}
