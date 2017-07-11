package javax.swing.tree;

public abstract interface MutableTreeNode
  extends TreeNode
{
  public abstract void insert(MutableTreeNode paramMutableTreeNode, int paramInt);
  
  public abstract void remove(int paramInt);
  
  public abstract void remove(MutableTreeNode paramMutableTreeNode);
  
  public abstract void setUserObject(Object paramObject);
  
  public abstract void removeFromParent();
  
  public abstract void setParent(MutableTreeNode paramMutableTreeNode);
}
