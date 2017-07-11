package javax.naming.event;

public abstract interface NamespaceChangeListener
  extends NamingListener
{
  public abstract void objectAdded(NamingEvent paramNamingEvent);
  
  public abstract void objectRemoved(NamingEvent paramNamingEvent);
  
  public abstract void objectRenamed(NamingEvent paramNamingEvent);
}
