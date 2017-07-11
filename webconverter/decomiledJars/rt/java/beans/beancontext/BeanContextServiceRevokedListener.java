package java.beans.beancontext;

import java.util.EventListener;

public abstract interface BeanContextServiceRevokedListener
  extends EventListener
{
  public abstract void serviceRevoked(BeanContextServiceRevokedEvent paramBeanContextServiceRevokedEvent);
}
