package com.sun.management.jmx;

import javax.management.Notification;
import javax.management.NotificationFilter;

@Deprecated
public class TraceFilter
  implements NotificationFilter
{
  protected int levels;
  protected int types;
  
  public TraceFilter(int paramInt1, int paramInt2)
    throws IllegalArgumentException
  {
    this.levels = paramInt1;
    this.types = paramInt2;
  }
  
  public boolean isNotificationEnabled(Notification paramNotification)
  {
    return false;
  }
  
  public int getLevels()
  {
    return this.levels;
  }
  
  public int getTypes()
  {
    return this.types;
  }
}
