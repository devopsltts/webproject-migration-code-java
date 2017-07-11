package javax.sql;

import java.util.EventListener;

public abstract interface ConnectionEventListener
  extends EventListener
{
  public abstract void connectionClosed(ConnectionEvent paramConnectionEvent);
  
  public abstract void connectionErrorOccurred(ConnectionEvent paramConnectionEvent);
}
