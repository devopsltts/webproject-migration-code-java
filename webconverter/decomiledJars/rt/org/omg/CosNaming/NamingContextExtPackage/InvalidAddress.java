package org.omg.CosNaming.NamingContextExtPackage;

import org.omg.CORBA.UserException;

public final class InvalidAddress
  extends UserException
{
  public InvalidAddress()
  {
    super(InvalidAddressHelper.id());
  }
  
  public InvalidAddress(String paramString)
  {
    super(InvalidAddressHelper.id() + "  " + paramString);
  }
}
