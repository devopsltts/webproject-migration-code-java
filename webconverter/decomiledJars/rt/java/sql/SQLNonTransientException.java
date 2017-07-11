package java.sql;

public class SQLNonTransientException
  extends SQLException
{
  private static final long serialVersionUID = -9104382843534716547L;
  
  public SQLNonTransientException() {}
  
  public SQLNonTransientException(String paramString)
  {
    super(paramString);
  }
  
  public SQLNonTransientException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public SQLNonTransientException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1, paramString2, paramInt);
  }
  
  public SQLNonTransientException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public SQLNonTransientException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public SQLNonTransientException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
  }
  
  public SQLNonTransientException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
  }
}
