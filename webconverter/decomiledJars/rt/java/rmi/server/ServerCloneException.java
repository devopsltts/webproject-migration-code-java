package java.rmi.server;

public class ServerCloneException
  extends CloneNotSupportedException
{
  public Exception detail;
  private static final long serialVersionUID = 6617456357664815945L;
  
  public ServerCloneException(String paramString)
  {
    super(paramString);
    initCause(null);
  }
  
  public ServerCloneException(String paramString, Exception paramException)
  {
    super(paramString);
    initCause(null);
    this.detail = paramException;
  }
  
  public String getMessage()
  {
    if (this.detail == null) {
      return super.getMessage();
    }
    return super.getMessage() + "; nested exception is: \n\t" + this.detail.toString();
  }
  
  public Throwable getCause()
  {
    return this.detail;
  }
}
