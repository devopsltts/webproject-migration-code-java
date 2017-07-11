package java.rmi;

public class AccessException
  extends RemoteException
{
  private static final long serialVersionUID = 6314925228044966088L;
  
  public AccessException(String paramString)
  {
    super(paramString);
  }
  
  public AccessException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}
