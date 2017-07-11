package javax.management.remote;

import java.io.IOException;

public class JMXServerErrorException
  extends IOException
{
  private static final long serialVersionUID = 3996732239558744666L;
  private final Error cause;
  
  public JMXServerErrorException(String paramString, Error paramError)
  {
    super(paramString);
    this.cause = paramError;
  }
  
  public Throwable getCause()
  {
    return this.cause;
  }
}
