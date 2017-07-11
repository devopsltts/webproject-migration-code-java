package javax.management.remote;

import java.io.IOException;

public class JMXProviderException
  extends IOException
{
  private static final long serialVersionUID = -3166703627550447198L;
  private Throwable cause = null;
  
  public JMXProviderException() {}
  
  public JMXProviderException(String paramString)
  {
    super(paramString);
  }
  
  public JMXProviderException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause()
  {
    return this.cause;
  }
}
