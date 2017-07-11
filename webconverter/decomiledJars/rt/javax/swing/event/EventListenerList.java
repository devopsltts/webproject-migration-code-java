package javax.swing.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.EventListener;
import sun.reflect.misc.ReflectUtil;

public class EventListenerList
  implements Serializable
{
  private static final Object[] NULL_ARRAY = new Object[0];
  protected transient Object[] listenerList = NULL_ARRAY;
  
  public EventListenerList() {}
  
  public Object[] getListenerList()
  {
    return this.listenerList;
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    Object[] arrayOfObject = this.listenerList;
    int i = getListenerCount(arrayOfObject, paramClass);
    EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(paramClass, i);
    int j = 0;
    for (int k = arrayOfObject.length - 2; k >= 0; k -= 2) {
      if (arrayOfObject[k] == paramClass) {
        arrayOfEventListener[(j++)] = ((EventListener)arrayOfObject[(k + 1)]);
      }
    }
    return arrayOfEventListener;
  }
  
  public int getListenerCount()
  {
    return this.listenerList.length / 2;
  }
  
  public int getListenerCount(Class<?> paramClass)
  {
    Object[] arrayOfObject = this.listenerList;
    return getListenerCount(arrayOfObject, paramClass);
  }
  
  private int getListenerCount(Object[] paramArrayOfObject, Class paramClass)
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfObject.length; j += 2) {
      if (paramClass == (Class)paramArrayOfObject[j]) {
        i++;
      }
    }
    return i;
  }
  
  public synchronized <T extends EventListener> void add(Class<T> paramClass, T paramT)
  {
    if (paramT == null) {
      return;
    }
    if (!paramClass.isInstance(paramT)) {
      throw new IllegalArgumentException("Listener " + paramT + " is not of type " + paramClass);
    }
    if (this.listenerList == NULL_ARRAY)
    {
      this.listenerList = new Object[] { paramClass, paramT };
    }
    else
    {
      int i = this.listenerList.length;
      Object[] arrayOfObject = new Object[i + 2];
      System.arraycopy(this.listenerList, 0, arrayOfObject, 0, i);
      arrayOfObject[i] = paramClass;
      arrayOfObject[(i + 1)] = paramT;
      this.listenerList = arrayOfObject;
    }
  }
  
  public synchronized <T extends EventListener> void remove(Class<T> paramClass, T paramT)
  {
    if (paramT == null) {
      return;
    }
    if (!paramClass.isInstance(paramT)) {
      throw new IllegalArgumentException("Listener " + paramT + " is not of type " + paramClass);
    }
    int i = -1;
    for (int j = this.listenerList.length - 2; j >= 0; j -= 2) {
      if ((this.listenerList[j] == paramClass) && (this.listenerList[(j + 1)].equals(paramT) == true))
      {
        i = j;
        break;
      }
    }
    if (i != -1)
    {
      Object[] arrayOfObject = new Object[this.listenerList.length - 2];
      System.arraycopy(this.listenerList, 0, arrayOfObject, 0, i);
      if (i < arrayOfObject.length) {
        System.arraycopy(this.listenerList, i + 2, arrayOfObject, i, arrayOfObject.length - i);
      }
      this.listenerList = (arrayOfObject.length == 0 ? NULL_ARRAY : arrayOfObject);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Object[] arrayOfObject = this.listenerList;
    paramObjectOutputStream.defaultWriteObject();
    for (int i = 0; i < arrayOfObject.length; i += 2)
    {
      Class localClass = (Class)arrayOfObject[i];
      EventListener localEventListener = (EventListener)arrayOfObject[(i + 1)];
      if ((localEventListener != null) && ((localEventListener instanceof Serializable)))
      {
        paramObjectOutputStream.writeObject(localClass.getName());
        paramObjectOutputStream.writeObject(localEventListener);
      }
    }
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.listenerList = NULL_ARRAY;
    paramObjectInputStream.defaultReadObject();
    Object localObject;
    while (null != (localObject = paramObjectInputStream.readObject()))
    {
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      EventListener localEventListener = (EventListener)paramObjectInputStream.readObject();
      String str = (String)localObject;
      ReflectUtil.checkPackageAccess(str);
      add(Class.forName(str, true, localClassLoader), localEventListener);
    }
  }
  
  public String toString()
  {
    Object[] arrayOfObject = this.listenerList;
    String str = "EventListenerList: ";
    str = str + arrayOfObject.length / 2 + " listeners: ";
    for (int i = 0; i <= arrayOfObject.length - 2; i += 2)
    {
      str = str + " type " + ((Class)arrayOfObject[i]).getName();
      str = str + " listener " + arrayOfObject[(i + 1)];
    }
    return str;
  }
}
