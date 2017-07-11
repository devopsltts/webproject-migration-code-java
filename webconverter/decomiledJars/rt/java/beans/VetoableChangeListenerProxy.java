package java.beans;

import java.util.EventListenerProxy;

public class VetoableChangeListenerProxy
  extends EventListenerProxy<VetoableChangeListener>
  implements VetoableChangeListener
{
  private final String propertyName;
  
  public VetoableChangeListenerProxy(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    super(paramVetoableChangeListener);
    this.propertyName = paramString;
  }
  
  public void vetoableChange(PropertyChangeEvent paramPropertyChangeEvent)
    throws PropertyVetoException
  {
    ((VetoableChangeListener)getListener()).vetoableChange(paramPropertyChangeEvent);
  }
  
  public String getPropertyName()
  {
    return this.propertyName;
  }
}
