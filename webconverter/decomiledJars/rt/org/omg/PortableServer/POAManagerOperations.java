package org.omg.PortableServer;

import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAManagerPackage.State;

public abstract interface POAManagerOperations
{
  public abstract void activate()
    throws AdapterInactive;
  
  public abstract void hold_requests(boolean paramBoolean)
    throws AdapterInactive;
  
  public abstract void discard_requests(boolean paramBoolean)
    throws AdapterInactive;
  
  public abstract void deactivate(boolean paramBoolean1, boolean paramBoolean2)
    throws AdapterInactive;
  
  public abstract State get_state();
}
