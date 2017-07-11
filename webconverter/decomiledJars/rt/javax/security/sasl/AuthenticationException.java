package javax.security.sasl;

public class AuthenticationException
  extends SaslException
{
  private static final long serialVersionUID = -3579708765071815007L;
  
  public AuthenticationException() {}
  
  public AuthenticationException(String paramString)
  {
    super(paramString);
  }
  
  public AuthenticationException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
