package javax.xml.bind;

import java.io.PrintStream;

public class TypeConstraintException
  extends RuntimeException
{
  private String errorCode;
  private volatile Throwable linkedException;
  static final long serialVersionUID = -3059799699420143848L;
  
  public TypeConstraintException(String paramString)
  {
    this(paramString, null, null);
  }
  
  public TypeConstraintException(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public TypeConstraintException(Throwable paramThrowable)
  {
    this(null, null, paramThrowable);
  }
  
  public TypeConstraintException(String paramString, Throwable paramThrowable)
  {
    this(paramString, null, paramThrowable);
  }
  
  public TypeConstraintException(String paramString1, String paramString2, Throwable paramThrowable)
  {
    super(paramString1);
    this.errorCode = paramString2;
    this.linkedException = paramThrowable;
  }
  
  public String getErrorCode()
  {
    return this.errorCode;
  }
  
  public Throwable getLinkedException()
  {
    return this.linkedException;
  }
  
  public void setLinkedException(Throwable paramThrowable)
  {
    this.linkedException = paramThrowable;
  }
  
  public String toString()
  {
    return super.toString() + "\n - with linked exception:\n[" + this.linkedException.toString() + "]";
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    if (this.linkedException != null)
    {
      this.linkedException.printStackTrace(paramPrintStream);
      paramPrintStream.println("--------------- linked to ------------------");
    }
    super.printStackTrace(paramPrintStream);
  }
  
  public void printStackTrace()
  {
    printStackTrace(System.err);
  }
}
