package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation._ServerImplBase;
import java.lang.reflect.Method;
import org.omg.CORBA.ORB;

class ServerCallback
  extends _ServerImplBase
{
  private ORB orb;
  private transient Method installMethod;
  private transient Method uninstallMethod;
  private transient Method shutdownMethod;
  private Object[] methodArgs;
  
  ServerCallback(ORB paramORB, Method paramMethod1, Method paramMethod2, Method paramMethod3)
  {
    this.orb = paramORB;
    this.installMethod = paramMethod1;
    this.uninstallMethod = paramMethod2;
    this.shutdownMethod = paramMethod3;
    paramORB.connect(this);
    this.methodArgs = new Object[] { paramORB };
  }
  
  private void invokeMethod(Method paramMethod)
  {
    if (paramMethod != null) {
      try
      {
        paramMethod.invoke(null, this.methodArgs);
      }
      catch (Exception localException)
      {
        ServerMain.logError("could not invoke " + paramMethod.getName() + " method: " + localException.getMessage());
      }
    }
  }
  
  public void shutdown()
  {
    ServerMain.logInformation("Shutdown starting");
    invokeMethod(this.shutdownMethod);
    this.orb.shutdown(true);
    ServerMain.logTerminal("Shutdown completed", 0);
  }
  
  public void install()
  {
    ServerMain.logInformation("Install starting");
    invokeMethod(this.installMethod);
    ServerMain.logInformation("Install completed");
  }
  
  public void uninstall()
  {
    ServerMain.logInformation("uninstall starting");
    invokeMethod(this.uninstallMethod);
    ServerMain.logInformation("uninstall completed");
  }
}
