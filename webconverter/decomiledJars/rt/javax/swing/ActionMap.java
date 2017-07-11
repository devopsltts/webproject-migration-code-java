package javax.swing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class ActionMap
  implements Serializable
{
  private transient ArrayTable arrayTable;
  private ActionMap parent;
  
  public ActionMap() {}
  
  public void setParent(ActionMap paramActionMap)
  {
    this.parent = paramActionMap;
  }
  
  public ActionMap getParent()
  {
    return this.parent;
  }
  
  public void put(Object paramObject, Action paramAction)
  {
    if (paramObject == null) {
      return;
    }
    if (paramAction == null)
    {
      remove(paramObject);
    }
    else
    {
      if (this.arrayTable == null) {
        this.arrayTable = new ArrayTable();
      }
      this.arrayTable.put(paramObject, paramAction);
    }
  }
  
  public Action get(Object paramObject)
  {
    Action localAction = this.arrayTable == null ? null : (Action)this.arrayTable.get(paramObject);
    if (localAction == null)
    {
      ActionMap localActionMap = getParent();
      if (localActionMap != null) {
        return localActionMap.get(paramObject);
      }
    }
    return localAction;
  }
  
  public void remove(Object paramObject)
  {
    if (this.arrayTable != null) {
      this.arrayTable.remove(paramObject);
    }
  }
  
  public void clear()
  {
    if (this.arrayTable != null) {
      this.arrayTable.clear();
    }
  }
  
  public Object[] keys()
  {
    if (this.arrayTable == null) {
      return null;
    }
    return this.arrayTable.getKeys(null);
  }
  
  public int size()
  {
    if (this.arrayTable == null) {
      return 0;
    }
    return this.arrayTable.size();
  }
  
  public Object[] allKeys()
  {
    int i = size();
    ActionMap localActionMap = getParent();
    if (i == 0)
    {
      if (localActionMap != null) {
        return localActionMap.allKeys();
      }
      return keys();
    }
    if (localActionMap == null) {
      return keys();
    }
    Object[] arrayOfObject1 = keys();
    Object[] arrayOfObject2 = localActionMap.allKeys();
    if (arrayOfObject2 == null) {
      return arrayOfObject1;
    }
    if (arrayOfObject1 == null) {
      return arrayOfObject2;
    }
    HashMap localHashMap = new HashMap();
    for (int j = arrayOfObject1.length - 1; j >= 0; j--) {
      localHashMap.put(arrayOfObject1[j], arrayOfObject1[j]);
    }
    for (j = arrayOfObject2.length - 1; j >= 0; j--) {
      localHashMap.put(arrayOfObject2[j], arrayOfObject2[j]);
    }
    return localHashMap.keySet().toArray();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    ArrayTable.writeArrayTable(paramObjectOutputStream, this.arrayTable);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    for (int i = paramObjectInputStream.readInt() - 1; i >= 0; i--) {
      put(paramObjectInputStream.readObject(), (Action)paramObjectInputStream.readObject());
    }
  }
}
