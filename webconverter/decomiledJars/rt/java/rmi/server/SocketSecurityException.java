package java.rmi.server;

@Deprecated
public class SocketSecurityException
  extends ExportException
{
  private static final long serialVersionUID = -7622072999407781979L;
  
  public SocketSecurityException(String paramString)
  {
    super(paramString);
  }
  
  public SocketSecurityException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}
