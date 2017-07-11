package java.beans;

public class PropertyVetoException
  extends Exception
{
  private static final long serialVersionUID = 129596057694162164L;
  private PropertyChangeEvent evt;
  
  public PropertyVetoException(String paramString, PropertyChangeEvent paramPropertyChangeEvent)
  {
    super(paramString);
    this.evt = paramPropertyChangeEvent;
  }
  
  public PropertyChangeEvent getPropertyChangeEvent()
  {
    return this.evt;
  }
}
