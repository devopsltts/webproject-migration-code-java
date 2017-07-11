package javax.naming.ldap;

import java.util.EventObject;

public class UnsolicitedNotificationEvent
  extends EventObject
{
  private UnsolicitedNotification notice;
  private static final long serialVersionUID = -2382603380799883705L;
  
  public UnsolicitedNotificationEvent(Object paramObject, UnsolicitedNotification paramUnsolicitedNotification)
  {
    super(paramObject);
    this.notice = paramUnsolicitedNotification;
  }
  
  public UnsolicitedNotification getNotification()
  {
    return this.notice;
  }
  
  public void dispatch(UnsolicitedNotificationListener paramUnsolicitedNotificationListener)
  {
    paramUnsolicitedNotificationListener.notificationReceived(this);
  }
}
