package java.sql;

public class SQLFeatureNotSupportedException
  extends SQLNonTransientException
{
  private static final long serialVersionUID = -1026510870282316051L;
  
  public SQLFeatureNotSupportedException() {}
  
  public SQLFeatureNotSupportedException(String paramString)
  {
    super(paramString);
  }
  
  public SQLFeatureNotSupportedException(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public SQLFeatureNotSupportedException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1, paramString2, paramInt);
  }
  
  public SQLFeatureNotSupportedException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public SQLFeatureNotSupportedException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public SQLFeatureNotSupportedException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramThrowable);
  }
  
  public SQLFeatureNotSupportedException(String paramString1, String paramString2, int paramInt, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
  }
}
