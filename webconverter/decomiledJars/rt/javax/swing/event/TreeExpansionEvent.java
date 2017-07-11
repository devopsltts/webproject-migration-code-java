package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

public class TreeExpansionEvent
  extends EventObject
{
  protected TreePath path;
  
  public TreeExpansionEvent(Object paramObject, TreePath paramTreePath)
  {
    super(paramObject);
    this.path = paramTreePath;
  }
  
  public TreePath getPath()
  {
    return this.path;
  }
}
