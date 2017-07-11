package java.beans.beancontext;

import java.awt.Component;
import java.awt.Container;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.Visibility;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

public class BeanContextSupport
  extends BeanContextChildSupport
  implements BeanContext, Serializable, PropertyChangeListener, VetoableChangeListener
{
  static final long serialVersionUID = -4879613978649577204L;
  protected transient HashMap children;
  private int serializable = 0;
  protected transient ArrayList bcmListeners;
  protected Locale locale;
  protected boolean okToUseGui;
  protected boolean designTime;
  private transient PropertyChangeListener childPCL;
  private transient VetoableChangeListener childVCL;
  private transient boolean serializing;
  
  public BeanContextSupport(BeanContext paramBeanContext, Locale paramLocale, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramBeanContext);
    this.locale = (paramLocale != null ? paramLocale : Locale.getDefault());
    this.designTime = paramBoolean1;
    this.okToUseGui = paramBoolean2;
    initialize();
  }
  
  public BeanContextSupport(BeanContext paramBeanContext, Locale paramLocale, boolean paramBoolean)
  {
    this(paramBeanContext, paramLocale, paramBoolean, true);
  }
  
  public BeanContextSupport(BeanContext paramBeanContext, Locale paramLocale)
  {
    this(paramBeanContext, paramLocale, false, true);
  }
  
  public BeanContextSupport(BeanContext paramBeanContext)
  {
    this(paramBeanContext, null, false, true);
  }
  
  public BeanContextSupport()
  {
    this(null, null, false, true);
  }
  
  public BeanContext getBeanContextPeer()
  {
    return (BeanContext)getBeanContextChildPeer();
  }
  
  public Object instantiateChild(String paramString)
    throws IOException, ClassNotFoundException
  {
    BeanContext localBeanContext = getBeanContextPeer();
    return Beans.instantiate(localBeanContext.getClass().getClassLoader(), paramString, localBeanContext);
  }
  
  public int size()
  {
    synchronized (this.children)
    {
      return this.children.size();
    }
  }
  
  public boolean isEmpty()
  {
    synchronized (this.children)
    {
      return this.children.isEmpty();
    }
  }
  
  public boolean contains(Object paramObject)
  {
    synchronized (this.children)
    {
      return this.children.containsKey(paramObject);
    }
  }
  
  public boolean containsKey(Object paramObject)
  {
    synchronized (this.children)
    {
      return this.children.containsKey(paramObject);
    }
  }
  
  public Iterator iterator()
  {
    synchronized (this.children)
    {
      return new BCSIterator(this.children.keySet().iterator());
    }
  }
  
  public Object[] toArray()
  {
    synchronized (this.children)
    {
      return this.children.keySet().toArray();
    }
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    synchronized (this.children)
    {
      return this.children.keySet().toArray(paramArrayOfObject);
    }
  }
  
  protected BCSChild createBCSChild(Object paramObject1, Object paramObject2)
  {
    return new BCSChild(paramObject1, paramObject2);
  }
  
  public boolean add(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException();
    }
    if (this.children.containsKey(paramObject)) {
      return false;
    }
    synchronized (BeanContext.globalHierarchyLock)
    {
      if (this.children.containsKey(paramObject)) {
        return false;
      }
      if (!validatePendingAdd(paramObject)) {
        throw new IllegalStateException();
      }
      BeanContextChild localBeanContextChild1 = getChildBeanContextChild(paramObject);
      BeanContextChild localBeanContextChild2 = null;
      synchronized (paramObject)
      {
        if ((paramObject instanceof BeanContextProxy))
        {
          localBeanContextChild2 = ((BeanContextProxy)paramObject).getBeanContextProxy();
          if (localBeanContextChild2 == null) {
            throw new NullPointerException("BeanContextPeer.getBeanContextProxy()");
          }
        }
        BCSChild localBCSChild1 = createBCSChild(paramObject, localBeanContextChild2);
        BCSChild localBCSChild2 = null;
        synchronized (this.children)
        {
          this.children.put(paramObject, localBCSChild1);
          if (localBeanContextChild2 != null) {
            this.children.put(localBeanContextChild2, localBCSChild2 = createBCSChild(localBeanContextChild2, paramObject));
          }
        }
        if (localBeanContextChild1 != null) {
          synchronized (localBeanContextChild1)
          {
            try
            {
              localBeanContextChild1.setBeanContext(getBeanContextPeer());
            }
            catch (PropertyVetoException localPropertyVetoException)
            {
              synchronized (this.children)
              {
                this.children.remove(paramObject);
                if (localBeanContextChild2 != null) {
                  this.children.remove(localBeanContextChild2);
                }
              }
              throw new IllegalStateException();
            }
            localBeanContextChild1.addPropertyChangeListener("beanContext", this.childPCL);
            localBeanContextChild1.addVetoableChangeListener("beanContext", this.childVCL);
          }
        }
        ??? = getChildVisibility(paramObject);
        if (??? != null) {
          if (this.okToUseGui) {
            ((Visibility)???).okToUseGui();
          } else {
            ((Visibility)???).dontUseGui();
          }
        }
        if (getChildSerializable(paramObject) != null) {
          this.serializable += 1;
        }
        childJustAddedHook(paramObject, localBCSChild1);
        if (localBeanContextChild2 != null)
        {
          ??? = getChildVisibility(localBeanContextChild2);
          if (??? != null) {
            if (this.okToUseGui) {
              ((Visibility)???).okToUseGui();
            } else {
              ((Visibility)???).dontUseGui();
            }
          }
          if (getChildSerializable(localBeanContextChild2) != null) {
            this.serializable += 1;
          }
          childJustAddedHook(localBeanContextChild2, localBCSChild2);
        }
      }
      fireChildrenAdded(new BeanContextMembershipEvent(getBeanContextPeer(), new Object[] { paramObject, localBeanContextChild2 == null ? new Object[] { paramObject } : localBeanContextChild2 }));
    }
    return true;
  }
  
  public boolean remove(Object paramObject)
  {
    return remove(paramObject, true);
  }
  
  protected boolean remove(Object paramObject, boolean paramBoolean)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException();
    }
    synchronized (BeanContext.globalHierarchyLock)
    {
      if (!containsKey(paramObject)) {
        return false;
      }
      if (!validatePendingRemove(paramObject)) {
        throw new IllegalStateException();
      }
      BCSChild localBCSChild1 = (BCSChild)this.children.get(paramObject);
      BCSChild localBCSChild2 = null;
      Object localObject1 = null;
      synchronized (paramObject)
      {
        if (paramBoolean)
        {
          BeanContextChild localBeanContextChild = getChildBeanContextChild(paramObject);
          if (localBeanContextChild != null) {
            synchronized (localBeanContextChild)
            {
              localBeanContextChild.removePropertyChangeListener("beanContext", this.childPCL);
              localBeanContextChild.removeVetoableChangeListener("beanContext", this.childVCL);
              try
              {
                localBeanContextChild.setBeanContext(null);
              }
              catch (PropertyVetoException localPropertyVetoException)
              {
                localBeanContextChild.addPropertyChangeListener("beanContext", this.childPCL);
                localBeanContextChild.addVetoableChangeListener("beanContext", this.childVCL);
                throw new IllegalStateException();
              }
            }
          }
        }
        synchronized (this.children)
        {
          this.children.remove(paramObject);
          if (localBCSChild1.isProxyPeer())
          {
            localBCSChild2 = (BCSChild)this.children.get(localObject1 = localBCSChild1.getProxyPeer());
            this.children.remove(localObject1);
          }
        }
        if (getChildSerializable(paramObject) != null) {
          this.serializable -= 1;
        }
        childJustRemovedHook(paramObject, localBCSChild1);
        if (localObject1 != null)
        {
          if (getChildSerializable(localObject1) != null) {
            this.serializable -= 1;
          }
          childJustRemovedHook(localObject1, localBCSChild2);
        }
      }
      fireChildrenRemoved(new BeanContextMembershipEvent(getBeanContextPeer(), new Object[] { paramObject, localObject1 == null ? new Object[] { paramObject } : localObject1 }));
    }
    return true;
  }
  
  public boolean containsAll(Collection paramCollection)
  {
    synchronized (this.children)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext()) {
        if (!contains(localIterator.next())) {
          return false;
        }
      }
      return true;
    }
  }
  
  public boolean addAll(Collection paramCollection)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection paramCollection)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(Collection paramCollection)
  {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public void addBeanContextMembershipListener(BeanContextMembershipListener paramBeanContextMembershipListener)
  {
    if (paramBeanContextMembershipListener == null) {
      throw new NullPointerException("listener");
    }
    synchronized (this.bcmListeners)
    {
      if (this.bcmListeners.contains(paramBeanContextMembershipListener)) {
        return;
      }
      this.bcmListeners.add(paramBeanContextMembershipListener);
    }
  }
  
  public void removeBeanContextMembershipListener(BeanContextMembershipListener paramBeanContextMembershipListener)
  {
    if (paramBeanContextMembershipListener == null) {
      throw new NullPointerException("listener");
    }
    synchronized (this.bcmListeners)
    {
      if (!this.bcmListeners.contains(paramBeanContextMembershipListener)) {
        return;
      }
      this.bcmListeners.remove(paramBeanContextMembershipListener);
    }
  }
  
  public InputStream getResourceAsStream(String paramString, BeanContextChild paramBeanContextChild)
  {
    if (paramString == null) {
      throw new NullPointerException("name");
    }
    if (paramBeanContextChild == null) {
      throw new NullPointerException("bcc");
    }
    if (containsKey(paramBeanContextChild))
    {
      ClassLoader localClassLoader = paramBeanContextChild.getClass().getClassLoader();
      return localClassLoader != null ? localClassLoader.getResourceAsStream(paramString) : ClassLoader.getSystemResourceAsStream(paramString);
    }
    throw new IllegalArgumentException("Not a valid child");
  }
  
  public URL getResource(String paramString, BeanContextChild paramBeanContextChild)
  {
    if (paramString == null) {
      throw new NullPointerException("name");
    }
    if (paramBeanContextChild == null) {
      throw new NullPointerException("bcc");
    }
    if (containsKey(paramBeanContextChild))
    {
      ClassLoader localClassLoader = paramBeanContextChild.getClass().getClassLoader();
      return localClassLoader != null ? localClassLoader.getResource(paramString) : ClassLoader.getSystemResource(paramString);
    }
    throw new IllegalArgumentException("Not a valid child");
  }
  
  public synchronized void setDesignTime(boolean paramBoolean)
  {
    if (this.designTime != paramBoolean)
    {
      this.designTime = paramBoolean;
      firePropertyChange("designMode", Boolean.valueOf(!paramBoolean), Boolean.valueOf(paramBoolean));
    }
  }
  
  public synchronized boolean isDesignTime()
  {
    return this.designTime;
  }
  
  public synchronized void setLocale(Locale paramLocale)
    throws PropertyVetoException
  {
    if ((this.locale != null) && (!this.locale.equals(paramLocale)) && (paramLocale != null))
    {
      Locale localLocale = this.locale;
      fireVetoableChange("locale", localLocale, paramLocale);
      this.locale = paramLocale;
      firePropertyChange("locale", localLocale, paramLocale);
    }
  }
  
  public synchronized Locale getLocale()
  {
    return this.locale;
  }
  
  public synchronized boolean needsGui()
  {
    BeanContext localBeanContext = getBeanContextPeer();
    if (localBeanContext != this)
    {
      if ((localBeanContext instanceof Visibility)) {
        return localBeanContext.needsGui();
      }
      if (((localBeanContext instanceof Container)) || ((localBeanContext instanceof Component))) {
        return true;
      }
    }
    synchronized (this.children)
    {
      Iterator localIterator = this.children.keySet().iterator();
      while (localIterator.hasNext())
      {
        Object localObject1 = localIterator.next();
        try
        {
          return ((Visibility)localObject1).needsGui();
        }
        catch (ClassCastException localClassCastException)
        {
          if (((localObject1 instanceof Container)) || ((localObject1 instanceof Component))) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public synchronized void dontUseGui()
  {
    if (this.okToUseGui)
    {
      this.okToUseGui = false;
      synchronized (this.children)
      {
        Iterator localIterator = this.children.keySet().iterator();
        while (localIterator.hasNext())
        {
          Visibility localVisibility = getChildVisibility(localIterator.next());
          if (localVisibility != null) {
            localVisibility.dontUseGui();
          }
        }
      }
    }
  }
  
  public synchronized void okToUseGui()
  {
    if (!this.okToUseGui)
    {
      this.okToUseGui = true;
      synchronized (this.children)
      {
        Iterator localIterator = this.children.keySet().iterator();
        while (localIterator.hasNext())
        {
          Visibility localVisibility = getChildVisibility(localIterator.next());
          if (localVisibility != null) {
            localVisibility.okToUseGui();
          }
        }
      }
    }
  }
  
  public boolean avoidingGui()
  {
    return (!this.okToUseGui) && (needsGui());
  }
  
  public boolean isSerializing()
  {
    return this.serializing;
  }
  
  protected Iterator bcsChildren()
  {
    synchronized (this.children)
    {
      return this.children.values().iterator();
    }
  }
  
  protected void bcsPreSerializationHook(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {}
  
  protected void bcsPreDeserializationHook(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {}
  
  protected void childDeserializedHook(Object paramObject, BCSChild paramBCSChild)
  {
    synchronized (this.children)
    {
      this.children.put(paramObject, paramBCSChild);
    }
  }
  
  protected final void serialize(ObjectOutputStream paramObjectOutputStream, Collection paramCollection)
    throws IOException
  {
    int i = 0;
    Object[] arrayOfObject = paramCollection.toArray();
    for (int j = 0; j < arrayOfObject.length; j++) {
      if ((arrayOfObject[j] instanceof Serializable)) {
        i++;
      } else {
        arrayOfObject[j] = null;
      }
    }
    paramObjectOutputStream.writeInt(i);
    for (j = 0; i > 0; j++)
    {
      Object localObject = arrayOfObject[j];
      if (localObject != null)
      {
        paramObjectOutputStream.writeObject(localObject);
        i--;
      }
    }
  }
  
  protected final void deserialize(ObjectInputStream paramObjectInputStream, Collection paramCollection)
    throws IOException, ClassNotFoundException
  {
    int i = 0;
    i = paramObjectInputStream.readInt();
    while (i-- > 0) {
      paramCollection.add(paramObjectInputStream.readObject());
    }
  }
  
  public final void writeChildren(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (this.serializable <= 0) {
      return;
    }
    boolean bool = this.serializing;
    this.serializing = true;
    int i = 0;
    synchronized (this.children)
    {
      Iterator localIterator = this.children.entrySet().iterator();
      while ((localIterator.hasNext()) && (i < this.serializable))
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if ((localEntry.getKey() instanceof Serializable))
        {
          try
          {
            paramObjectOutputStream.writeObject(localEntry.getKey());
            paramObjectOutputStream.writeObject(localEntry.getValue());
          }
          catch (IOException localIOException)
          {
            this.serializing = bool;
            throw localIOException;
          }
          i++;
        }
      }
    }
    this.serializing = bool;
    if (i != this.serializable) {
      throw new IOException("wrote different number of children than expected");
    }
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    this.serializing = true;
    synchronized (BeanContext.globalHierarchyLock)
    {
      try
      {
        paramObjectOutputStream.defaultWriteObject();
        bcsPreSerializationHook(paramObjectOutputStream);
        if ((this.serializable > 0) && (equals(getBeanContextPeer()))) {
          writeChildren(paramObjectOutputStream);
        }
        serialize(paramObjectOutputStream, this.bcmListeners);
        this.serializing = false;
      }
      finally
      {
        this.serializing = false;
      }
    }
  }
  
  public final void readChildren(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    int i = this.serializable;
    while (i-- > 0)
    {
      Object localObject1 = null;
      BCSChild localBCSChild = null;
      try
      {
        localObject1 = paramObjectInputStream.readObject();
        localBCSChild = (BCSChild)paramObjectInputStream.readObject();
      }
      catch (IOException localIOException)
      {
        continue;
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      continue;
      synchronized (localObject1)
      {
        BeanContextChild localBeanContextChild = null;
        try
        {
          localBeanContextChild = (BeanContextChild)localObject1;
        }
        catch (ClassCastException localClassCastException) {}
        if (localBeanContextChild != null)
        {
          try
          {
            localBeanContextChild.setBeanContext(getBeanContextPeer());
            localBeanContextChild.addPropertyChangeListener("beanContext", this.childPCL);
            localBeanContextChild.addVetoableChangeListener("beanContext", this.childVCL);
          }
          catch (PropertyVetoException localPropertyVetoException) {}
          continue;
        }
        childDeserializedHook(localObject1, localBCSChild);
      }
    }
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    synchronized (BeanContext.globalHierarchyLock)
    {
      paramObjectInputStream.defaultReadObject();
      initialize();
      bcsPreDeserializationHook(paramObjectInputStream);
      if ((this.serializable > 0) && (equals(getBeanContextPeer()))) {
        readChildren(paramObjectInputStream);
      }
      deserialize(paramObjectInputStream, this.bcmListeners = new ArrayList(1));
    }
  }
  
  public void vetoableChange(PropertyChangeEvent paramPropertyChangeEvent)
    throws PropertyVetoException
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    Object localObject1 = paramPropertyChangeEvent.getSource();
    synchronized (this.children)
    {
      if (("beanContext".equals(str)) && (containsKey(localObject1)) && (!getBeanContextPeer().equals(paramPropertyChangeEvent.getNewValue())))
      {
        if (!validatePendingRemove(localObject1)) {
          throw new PropertyVetoException("current BeanContext vetoes setBeanContext()", paramPropertyChangeEvent);
        }
        ((BCSChild)this.children.get(localObject1)).setRemovePending(true);
      }
    }
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    Object localObject1 = paramPropertyChangeEvent.getSource();
    synchronized (this.children)
    {
      if (("beanContext".equals(str)) && (containsKey(localObject1)) && (((BCSChild)this.children.get(localObject1)).isRemovePending()))
      {
        BeanContext localBeanContext = getBeanContextPeer();
        if ((localBeanContext.equals(paramPropertyChangeEvent.getOldValue())) && (!localBeanContext.equals(paramPropertyChangeEvent.getNewValue()))) {
          remove(localObject1, false);
        } else {
          ((BCSChild)this.children.get(localObject1)).setRemovePending(false);
        }
      }
    }
  }
  
  protected boolean validatePendingAdd(Object paramObject)
  {
    return true;
  }
  
  protected boolean validatePendingRemove(Object paramObject)
  {
    return true;
  }
  
  protected void childJustAddedHook(Object paramObject, BCSChild paramBCSChild) {}
  
  protected void childJustRemovedHook(Object paramObject, BCSChild paramBCSChild) {}
  
  protected static final Visibility getChildVisibility(Object paramObject)
  {
    try
    {
      return (Visibility)paramObject;
    }
    catch (ClassCastException localClassCastException) {}
    return null;
  }
  
  protected static final Serializable getChildSerializable(Object paramObject)
  {
    try
    {
      return (Serializable)paramObject;
    }
    catch (ClassCastException localClassCastException) {}
    return null;
  }
  
  protected static final PropertyChangeListener getChildPropertyChangeListener(Object paramObject)
  {
    try
    {
      return (PropertyChangeListener)paramObject;
    }
    catch (ClassCastException localClassCastException) {}
    return null;
  }
  
  protected static final VetoableChangeListener getChildVetoableChangeListener(Object paramObject)
  {
    try
    {
      return (VetoableChangeListener)paramObject;
    }
    catch (ClassCastException localClassCastException) {}
    return null;
  }
  
  protected static final BeanContextMembershipListener getChildBeanContextMembershipListener(Object paramObject)
  {
    try
    {
      return (BeanContextMembershipListener)paramObject;
    }
    catch (ClassCastException localClassCastException) {}
    return null;
  }
  
  protected static final BeanContextChild getChildBeanContextChild(Object paramObject)
  {
    try
    {
      BeanContextChild localBeanContextChild = (BeanContextChild)paramObject;
      if (((paramObject instanceof BeanContextChild)) && ((paramObject instanceof BeanContextProxy))) {
        throw new IllegalArgumentException("child cannot implement both BeanContextChild and BeanContextProxy");
      }
      return localBeanContextChild;
    }
    catch (ClassCastException localClassCastException1)
    {
      try
      {
        return ((BeanContextProxy)paramObject).getBeanContextProxy();
      }
      catch (ClassCastException localClassCastException2) {}
    }
    return null;
  }
  
  protected final void fireChildrenAdded(BeanContextMembershipEvent paramBeanContextMembershipEvent)
  {
    Object[] arrayOfObject;
    synchronized (this.bcmListeners)
    {
      arrayOfObject = this.bcmListeners.toArray();
    }
    for (int i = 0; i < arrayOfObject.length; i++) {
      ((BeanContextMembershipListener)arrayOfObject[i]).childrenAdded(paramBeanContextMembershipEvent);
    }
  }
  
  protected final void fireChildrenRemoved(BeanContextMembershipEvent paramBeanContextMembershipEvent)
  {
    Object[] arrayOfObject;
    synchronized (this.bcmListeners)
    {
      arrayOfObject = this.bcmListeners.toArray();
    }
    for (int i = 0; i < arrayOfObject.length; i++) {
      ((BeanContextMembershipListener)arrayOfObject[i]).childrenRemoved(paramBeanContextMembershipEvent);
    }
  }
  
  protected synchronized void initialize()
  {
    this.children = new HashMap(this.serializable + 1);
    this.bcmListeners = new ArrayList(1);
    this.childPCL = new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
      {
        BeanContextSupport.this.propertyChange(paramAnonymousPropertyChangeEvent);
      }
    };
    this.childVCL = new VetoableChangeListener()
    {
      public void vetoableChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
        throws PropertyVetoException
      {
        BeanContextSupport.this.vetoableChange(paramAnonymousPropertyChangeEvent);
      }
    };
  }
  
  protected final Object[] copyChildren()
  {
    synchronized (this.children)
    {
      return this.children.keySet().toArray();
    }
  }
  
  protected static final boolean classEquals(Class paramClass1, Class paramClass2)
  {
    return (paramClass1.equals(paramClass2)) || (paramClass1.getName().equals(paramClass2.getName()));
  }
  
  protected class BCSChild
    implements Serializable
  {
    private static final long serialVersionUID = -5815286101609939109L;
    private Object child;
    private Object proxyPeer;
    private transient boolean removePending;
    
    BCSChild(Object paramObject1, Object paramObject2)
    {
      this.child = paramObject1;
      this.proxyPeer = paramObject2;
    }
    
    Object getChild()
    {
      return this.child;
    }
    
    void setRemovePending(boolean paramBoolean)
    {
      this.removePending = paramBoolean;
    }
    
    boolean isRemovePending()
    {
      return this.removePending;
    }
    
    boolean isProxyPeer()
    {
      return this.proxyPeer != null;
    }
    
    Object getProxyPeer()
    {
      return this.proxyPeer;
    }
  }
  
  protected static final class BCSIterator
    implements Iterator
  {
    private Iterator src;
    
    BCSIterator(Iterator paramIterator)
    {
      this.src = paramIterator;
    }
    
    public boolean hasNext()
    {
      return this.src.hasNext();
    }
    
    public Object next()
    {
      return this.src.next();
    }
    
    public void remove() {}
  }
}
