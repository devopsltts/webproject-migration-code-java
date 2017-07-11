package javax.print;

public class PrintException
  extends Exception
{
  public PrintException() {}
  
  public PrintException(String paramString)
  {
    super(paramString);
  }
  
  public PrintException(Exception paramException)
  {
    super(paramException);
  }
  
  public PrintException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}
