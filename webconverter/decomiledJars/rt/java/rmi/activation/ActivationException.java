package java.rmi.activation;

public class ActivationException
  extends Exception
{
  public Throwable detail;
  private static final long serialVersionUID = -4320118837291406071L;
  
  public ActivationException()
  {
    initCause(null);
  }
  
  public ActivationException(String paramString)
  {
    super(paramString);
    initCause(null);
  }
  
  public ActivationException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    initCause(null);
    this.detail = paramThrowable;
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
