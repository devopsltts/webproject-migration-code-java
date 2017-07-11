package javax.management;

import java.util.EventListener;

public abstract interface NotificationListener
  extends EventListener
{
  public abstract void handleNotification(Notification paramNotification, Object paramObject);
}
