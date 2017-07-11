package javax.management;

public abstract interface PersistentMBean
{
  public abstract void load()
    throws MBeanException, RuntimeOperationsException, InstanceNotFoundException;
  
  public abstract void store()
    throws MBeanException, RuntimeOperationsException, InstanceNotFoundException;
}
