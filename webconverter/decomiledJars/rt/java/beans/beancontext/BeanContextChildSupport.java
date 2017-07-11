package java.beans.beancontext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BeanContextChildSupport
  implements BeanContextChild, BeanContextServicesListener, Serializable
{
  static final long serialVersionUID = 6328947014421475877L;
  public BeanContextChild beanContextChildPeer;
  protected PropertyChangeSupport pcSupport;
  protected VetoableChangeSupport vcSupport;
  protected transient BeanContext beanContext;
  protected transient boolean rejectedSetBCOnce;
  
  public BeanContextChildSupport()
  {
    this.beanContextChildPeer = this;
    this.pcSupport = new PropertyChangeSupport(this.beanContextChildPeer);
    this.vcSupport = new VetoableChangeSupport(this.beanContextChildPeer);
  }
  
  public BeanContextChildSupport(BeanContextChild paramBeanContextChild)
  {
    this.beanContextChildPeer = (paramBeanContextChild != null ? paramBeanContextChild : this);
    this.pcSupport = new PropertyChangeSupport(this.beanContextChildPeer);
    this.vcSupport = new VetoableChangeSupport(this.beanContextChildPeer);
  }
  
  public synchronized void setBeanContext(BeanContext paramBeanContext)
    throws PropertyVetoException
  {
    if (paramBeanContext == this.beanContext) {
      return;
    }
    BeanContext localBeanContext1 = this.beanContext;
    BeanContext localBeanContext2 = paramBeanContext;
    if (!this.rejectedSetBCOnce)
    {
      if ((this.rejectedSetBCOnce = !validatePendingSetBeanContext(paramBeanContext) ? 1 : 0) != 0) {
        throw new PropertyVetoException("setBeanContext() change rejected:", new PropertyChangeEvent(this.beanContextChildPeer, "beanContext", localBeanContext1, localBeanContext2));
      }
      try
      {
        fireVetoableChange("beanContext", localBeanContext1, localBeanContext2);
      }
      catch (PropertyVetoException localPropertyVetoException)
      {
        this.rejectedSetBCOnce = true;
        throw localPropertyVetoException;
      }
    }
    if (this.beanContext != null) {
      releaseBeanContextResources();
    }
    this.beanContext = localBeanContext2;
    this.rejectedSetBCOnce = false;
    firePropertyChange("beanContext", localBeanContext1, localBeanContext2);
    if (this.beanContext != null) {
      initializeBeanContextResources();
    }
  }
  
  public synchronized BeanContext getBeanContext()
  {
    return this.beanContext;
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    this.pcSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    this.pcSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    this.vcSupport.addVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    this.vcSupport.removeVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
  
  public void serviceRevoked(BeanContextServiceRevokedEvent paramBeanContextServiceRevokedEvent) {}
  
  public void serviceAvailable(BeanContextServiceAvailableEvent paramBeanContextServiceAvailableEvent) {}
  
  public BeanContextChild getBeanContextChildPeer()
  {
    return this.beanContextChildPeer;
  }
  
  public boolean isDelegated()
  {
    return !equals(this.beanContextChildPeer);
  }
  
  public void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    this.pcSupport.firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  public void fireVetoableChange(String paramString, Object paramObject1, Object paramObject2)
    throws PropertyVetoException
  {
    this.vcSupport.fireVetoableChange(paramString, paramObject1, paramObject2);
  }
  
  public boolean validatePendingSetBeanContext(BeanContext paramBeanContext)
  {
    return true;
  }
  
  protected void releaseBeanContextResources() {}
  
  protected void initializeBeanContextResources() {}
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if ((!equals(this.beanContextChildPeer)) && (!(this.beanContextChildPeer instanceof Serializable))) {
      throw new IOException("BeanContextChildSupport beanContextChildPeer not Serializable");
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
  }
}
