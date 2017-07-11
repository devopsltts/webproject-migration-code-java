package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;
import org.omg.CosNaming.NameComponent;

public final class NotFound
  extends UserException
{
  public NotFoundReason why = null;
  public NameComponent[] rest_of_name = null;
  
  public NotFound()
  {
    super(NotFoundHelper.id());
  }
  
  public NotFound(NotFoundReason paramNotFoundReason, NameComponent[] paramArrayOfNameComponent)
  {
    super(NotFoundHelper.id());
    this.why = paramNotFoundReason;
    this.rest_of_name = paramArrayOfNameComponent;
  }
  
  public NotFound(String paramString, NotFoundReason paramNotFoundReason, NameComponent[] paramArrayOfNameComponent)
  {
    super(NotFoundHelper.id() + "  " + paramString);
    this.why = paramNotFoundReason;
    this.rest_of_name = paramArrayOfNameComponent;
  }
}
