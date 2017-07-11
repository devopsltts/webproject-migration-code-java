package java.util.concurrent;

public class CompletionException
  extends RuntimeException
{
  private static final long serialVersionUID = 7830266012832686185L;
  
  protected CompletionException() {}
  
  protected CompletionException(String paramString)
  {
    super(paramString);
  }
  
  public CompletionException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public CompletionException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}
