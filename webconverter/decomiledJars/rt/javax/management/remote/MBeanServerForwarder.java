package javax.management.remote;

import javax.management.MBeanServer;

public abstract interface MBeanServerForwarder
  extends MBeanServer
{
  public abstract MBeanServer getMBeanServer();
  
  public abstract void setMBeanServer(MBeanServer paramMBeanServer);
}
