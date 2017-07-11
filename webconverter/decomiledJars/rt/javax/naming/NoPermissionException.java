package javax.naming;

public class NoPermissionException
  extends NamingSecurityException
{
  private static final long serialVersionUID = 8395332708699751775L;
  
  public NoPermissionException(String paramString)
  {
    super(paramString);
  }
  
  public NoPermissionException() {}
}
