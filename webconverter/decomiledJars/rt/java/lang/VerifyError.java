package java.lang;

public class VerifyError
  extends LinkageError
{
  private static final long serialVersionUID = 7001962396098498785L;
  
  public VerifyError() {}
  
  public VerifyError(String paramString)
  {
    super(paramString);
  }
}
