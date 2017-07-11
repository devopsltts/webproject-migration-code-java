package sun.security.action;

import java.security.PrivilegedAction;
import java.security.Security;

public class GetBooleanSecurityPropertyAction
  implements PrivilegedAction<Boolean>
{
  private String theProp;
  
  public GetBooleanSecurityPropertyAction(String paramString)
  {
    this.theProp = paramString;
  }
  
  public Boolean run()
  {
    boolean bool = false;
    try
    {
      String str = Security.getProperty(this.theProp);
      bool = (str != null) && (str.equalsIgnoreCase("true"));
    }
    catch (NullPointerException localNullPointerException) {}
    return Boolean.valueOf(bool);
  }
}
