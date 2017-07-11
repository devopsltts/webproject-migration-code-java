package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

public class TreeModelEvent
  extends EventObject
{
  protected TreePath path;
  protected int[] childIndices;
  protected Object[] children;
  
  public TreeModelEvent(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2)
  {
    this(paramObject, paramArrayOfObject1 == null ? null : new TreePath(paramArrayOfObject1), paramArrayOfInt, paramArrayOfObject2);
  }
  
  public TreeModelEvent(Object paramObject, TreePath paramTreePath, int[] paramArrayOfInt, Object[] paramArrayOfObject)
  {
    super(paramObject);
    this.path = paramTreePath;
    this.childIndices = paramArrayOfInt;
    this.children = paramArrayOfObject;
  }
  
  public TreeModelEvent(Object paramObject, Object[] paramArrayOfObject)
  {
    this(paramObject, paramArrayOfObject == null ? null : new TreePath(paramArrayOfObject));
  }
  
  public TreeModelEvent(Object paramObject, TreePath paramTreePath)
  {
    super(paramObject);
    this.path = paramTreePath;
    this.childIndices = new int[0];
  }
  
  public TreePath getTreePath()
  {
    return this.path;
  }
  
  public Object[] getPath()
  {
    if (this.path != null) {
      return this.path.getPath();
    }
    return null;
  }
  
  public Object[] getChildren()
  {
    if (this.children != null)
    {
      int i = this.children.length;
      Object[] arrayOfObject = new Object[i];
      System.arraycopy(this.children, 0, arrayOfObject, 0, i);
      return arrayOfObject;
    }
    return null;
  }
  
  public int[] getChildIndices()
  {
    if (this.childIndices != null)
    {
      int i = this.childIndices.length;
      int[] arrayOfInt = new int[i];
      System.arraycopy(this.childIndices, 0, arrayOfInt, 0, i);
      return arrayOfInt;
    }
    return null;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(getClass().getName() + " " + Integer.toString(hashCode()));
    if (this.path != null) {
      localStringBuffer.append(" path " + this.path);
    }
    int i;
    if (this.childIndices != null)
    {
      localStringBuffer.append(" indices [ ");
      for (i = 0; i < this.childIndices.length; i++) {
        localStringBuffer.append(Integer.toString(this.childIndices[i]) + " ");
      }
      localStringBuffer.append("]");
    }
    if (this.children != null)
    {
      localStringBuffer.append(" children [ ");
      for (i = 0; i < this.children.length; i++) {
        localStringBuffer.append(this.children[i] + " ");
      }
      localStringBuffer.append("]");
    }
    return localStringBuffer.toString();
  }
}
