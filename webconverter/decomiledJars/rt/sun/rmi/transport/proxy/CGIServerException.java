package sun.rmi.transport.proxy;

class CGIServerException
  extends Exception
{
  private static final long serialVersionUID = 6928425456704527017L;
  
  public CGIServerException(String paramString)
  {
    super(paramString);
  }
  
  public CGIServerException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
