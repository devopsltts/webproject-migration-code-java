package java.beans.beancontext;

public abstract interface BeanContextServicesListener
  extends BeanContextServiceRevokedListener
{
  public abstract void serviceAvailable(BeanContextServiceAvailableEvent paramBeanContextServiceAvailableEvent);
}
