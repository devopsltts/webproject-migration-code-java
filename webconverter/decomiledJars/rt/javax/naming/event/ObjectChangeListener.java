package javax.naming.event;

public abstract interface ObjectChangeListener
  extends NamingListener
{
  public abstract void objectChanged(NamingEvent paramNamingEvent);
}
