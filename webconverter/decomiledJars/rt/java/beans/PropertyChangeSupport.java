package java.beans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class PropertyChangeSupport
  implements Serializable
{
  private PropertyChangeListenerMap map = new PropertyChangeListenerMap(null);
  private Object source;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("children", Hashtable.class), new ObjectStreamField("source", Object.class), new ObjectStreamField("propertyChangeSupportSerializedDataVersion", Integer.TYPE) };
  static final long serialVersionUID = 6401253773779951803L;
  
  public PropertyChangeSupport(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    this.source = paramObject;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (paramPropertyChangeListener == null) {
      return;
    }
    if ((paramPropertyChangeListener instanceof PropertyChangeListenerProxy))
    {
      PropertyChangeListenerProxy localPropertyChangeListenerProxy = (PropertyChangeListenerProxy)paramPropertyChangeListener;
      addPropertyChangeListener(localPropertyChangeListenerProxy.getPropertyName(), (PropertyChangeListener)localPropertyChangeListenerProxy.getListener());
    }
    else
    {
      this.map.add(null, paramPropertyChangeListener);
    }
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (paramPropertyChangeListener == null) {
      return;
    }
    if ((paramPropertyChangeListener instanceof PropertyChangeListenerProxy))
    {
      PropertyChangeListenerProxy localPropertyChangeListenerProxy = (PropertyChangeListenerProxy)paramPropertyChangeListener;
      removePropertyChangeListener(localPropertyChangeListenerProxy.getPropertyName(), (PropertyChangeListener)localPropertyChangeListenerProxy.getListener());
    }
    else
    {
      this.map.remove(null, paramPropertyChangeListener);
    }
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners()
  {
    return (PropertyChangeListener[])this.map.getListeners();
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    if ((paramPropertyChangeListener == null) || (paramString == null)) {
      return;
    }
    paramPropertyChangeListener = this.map.extract(paramPropertyChangeListener);
    if (paramPropertyChangeListener != null) {
      this.map.add(paramString, paramPropertyChangeListener);
    }
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    if ((paramPropertyChangeListener == null) || (paramString == null)) {
      return;
    }
    paramPropertyChangeListener = this.map.extract(paramPropertyChangeListener);
    if (paramPropertyChangeListener != null) {
      this.map.remove(paramString, paramPropertyChangeListener);
    }
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners(String paramString)
  {
    return (PropertyChangeListener[])this.map.getListeners(paramString);
  }
  
  public void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 == null) || (paramObject2 == null) || (!paramObject1.equals(paramObject2))) {
      firePropertyChange(new PropertyChangeEvent(this.source, paramString, paramObject1, paramObject2));
    }
  }
  
  public void firePropertyChange(String paramString, int paramInt1, int paramInt2)
  {
    if (paramInt1 != paramInt2) {
      firePropertyChange(paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
    }
  }
  
  public void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 != paramBoolean2) {
      firePropertyChange(paramString, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2));
    }
  }
  
  public void firePropertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    Object localObject1 = paramPropertyChangeEvent.getOldValue();
    Object localObject2 = paramPropertyChangeEvent.getNewValue();
    if ((localObject1 == null) || (localObject2 == null) || (!localObject1.equals(localObject2)))
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      PropertyChangeListener[] arrayOfPropertyChangeListener1 = (PropertyChangeListener[])this.map.get(null);
      PropertyChangeListener[] arrayOfPropertyChangeListener2 = str != null ? (PropertyChangeListener[])this.map.get(str) : null;
      fire(arrayOfPropertyChangeListener1, paramPropertyChangeEvent);
      fire(arrayOfPropertyChangeListener2, paramPropertyChangeEvent);
    }
  }
  
  private static void fire(PropertyChangeListener[] paramArrayOfPropertyChangeListener, PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (paramArrayOfPropertyChangeListener != null) {
      for (PropertyChangeListener localPropertyChangeListener : paramArrayOfPropertyChangeListener) {
        localPropertyChangeListener.propertyChange(paramPropertyChangeEvent);
      }
    }
  }
  
  public void fireIndexedPropertyChange(String paramString, int paramInt, Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 == null) || (paramObject2 == null) || (!paramObject1.equals(paramObject2))) {
      firePropertyChange(new IndexedPropertyChangeEvent(this.source, paramString, paramObject1, paramObject2, paramInt));
    }
  }
  
  public void fireIndexedPropertyChange(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 != paramInt3) {
      fireIndexedPropertyChange(paramString, paramInt1, Integer.valueOf(paramInt2), Integer.valueOf(paramInt3));
    }
  }
  
  public void fireIndexedPropertyChange(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 != paramBoolean2) {
      fireIndexedPropertyChange(paramString, paramInt, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2));
    }
  }
  
  public boolean hasListeners(String paramString)
  {
    return this.map.hasListeners(paramString);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Hashtable localHashtable = null;
    PropertyChangeListener[] arrayOfPropertyChangeListener = null;
    Object localObject1;
    PropertyChangeSupport localPropertyChangeSupport;
    synchronized (this.map)
    {
      localObject1 = this.map.getEntries().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject1).next();
        String str = (String)localEntry.getKey();
        if (str == null)
        {
          arrayOfPropertyChangeListener = (PropertyChangeListener[])localEntry.getValue();
        }
        else
        {
          if (localHashtable == null) {
            localHashtable = new Hashtable();
          }
          localPropertyChangeSupport = new PropertyChangeSupport(this.source);
          localPropertyChangeSupport.map.set(null, (EventListener[])localEntry.getValue());
          localHashtable.put(str, localPropertyChangeSupport);
        }
      }
    }
    ??? = paramObjectOutputStream.putFields();
    ((ObjectOutputStream.PutField)???).put("children", localHashtable);
    ((ObjectOutputStream.PutField)???).put("source", this.source);
    ((ObjectOutputStream.PutField)???).put("propertyChangeSupportSerializedDataVersion", 2);
    paramObjectOutputStream.writeFields();
    if (arrayOfPropertyChangeListener != null) {
      for (localPropertyChangeSupport : arrayOfPropertyChangeListener) {
        if ((localPropertyChangeSupport instanceof Serializable)) {
          paramObjectOutputStream.writeObject(localPropertyChangeSupport);
        }
      }
    }
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    this.map = new PropertyChangeListenerMap(null);
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Hashtable localHashtable = (Hashtable)localGetField.get("children", null);
    this.source = localGetField.get("source", null);
    localGetField.get("propertyChangeSupportSerializedDataVersion", 2);
    Object localObject;
    while (null != (localObject = paramObjectInputStream.readObject())) {
      this.map.add(null, (PropertyChangeListener)localObject);
    }
    if (localHashtable != null)
    {
      Iterator localIterator = localHashtable.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        for (PropertyChangeListener localPropertyChangeListener : ((PropertyChangeSupport)localEntry.getValue()).getPropertyChangeListeners()) {
          this.map.add((String)localEntry.getKey(), localPropertyChangeListener);
        }
      }
    }
  }
  
  private static final class PropertyChangeListenerMap
    extends ChangeListenerMap<PropertyChangeListener>
  {
    private static final PropertyChangeListener[] EMPTY = new PropertyChangeListener[0];
    
    private PropertyChangeListenerMap() {}
    
    protected PropertyChangeListener[] newArray(int paramInt)
    {
      return 0 < paramInt ? new PropertyChangeListener[paramInt] : EMPTY;
    }
    
    protected PropertyChangeListener newProxy(String paramString, PropertyChangeListener paramPropertyChangeListener)
    {
      return new PropertyChangeListenerProxy(paramString, paramPropertyChangeListener);
    }
    
    public final PropertyChangeListener extract(PropertyChangeListener paramPropertyChangeListener)
    {
      while ((paramPropertyChangeListener instanceof PropertyChangeListenerProxy)) {
        paramPropertyChangeListener = (PropertyChangeListener)((PropertyChangeListenerProxy)paramPropertyChangeListener).getListener();
      }
      return paramPropertyChangeListener;
    }
  }
}
