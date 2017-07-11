package javax.xml.ws;

public class WebServiceException
  extends RuntimeException
{
  public WebServiceException() {}
  
  public WebServiceException(String paramString)
  {
    super(paramString);
  }
  
  public WebServiceException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public WebServiceException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}
