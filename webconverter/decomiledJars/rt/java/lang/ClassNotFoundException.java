package java.lang;

public class ClassNotFoundException
  extends ReflectiveOperationException
{
  private static final long serialVersionUID = 9176873029745254542L;
  private Throwable ex;
  
  public ClassNotFoundException()
  {
    super((Throwable)null);
  }
  
  public ClassNotFoundException(String paramString)
  {
    super(paramString, null);
  }
  
  public ClassNotFoundException(String paramString, Throwable paramThrowable)
  {
    super(paramString, null);
    this.ex = paramThrowable;
  }
  
  public Throwable getException()
  {
    return this.ex;
  }
  
  public Throwable getCause()
  {
    return this.ex;
  }
}
