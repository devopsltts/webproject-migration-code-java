package javax.swing.event;

import java.util.EventListener;

public abstract interface TreeModelListener
  extends EventListener
{
  public abstract void treeNodesChanged(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeNodesInserted(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeNodesRemoved(TreeModelEvent paramTreeModelEvent);
  
  public abstract void treeStructureChanged(TreeModelEvent paramTreeModelEvent);
}
