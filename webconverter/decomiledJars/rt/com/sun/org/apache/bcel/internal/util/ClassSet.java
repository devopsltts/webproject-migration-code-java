package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class ClassSet
  implements Serializable
{
  private HashMap _map = new HashMap();
  
  public ClassSet() {}
  
  public boolean add(JavaClass paramJavaClass)
  {
    boolean bool = false;
    if (!this._map.containsKey(paramJavaClass.getClassName()))
    {
      bool = true;
      this._map.put(paramJavaClass.getClassName(), paramJavaClass);
    }
    return bool;
  }
  
  public void remove(JavaClass paramJavaClass)
  {
    this._map.remove(paramJavaClass.getClassName());
  }
  
  public boolean empty()
  {
    return this._map.isEmpty();
  }
  
  public JavaClass[] toArray()
  {
    Collection localCollection = this._map.values();
    JavaClass[] arrayOfJavaClass = new JavaClass[localCollection.size()];
    localCollection.toArray(arrayOfJavaClass);
    return arrayOfJavaClass;
  }
  
  public String[] getClassNames()
  {
    return (String[])this._map.keySet().toArray(new String[this._map.keySet().size()]);
  }
}
