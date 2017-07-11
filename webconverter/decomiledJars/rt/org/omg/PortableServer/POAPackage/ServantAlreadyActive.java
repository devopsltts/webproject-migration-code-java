package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ServantAlreadyActive
  extends UserException
{
  public ServantAlreadyActive()
  {
    super(ServantAlreadyActiveHelper.id());
  }
  
  public ServantAlreadyActive(String paramString)
  {
    super(ServantAlreadyActiveHelper.id() + "  " + paramString);
  }
}
