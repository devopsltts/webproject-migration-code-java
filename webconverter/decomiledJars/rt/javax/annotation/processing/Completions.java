package javax.annotation.processing;

public class Completions
{
  private Completions() {}
  
  public static Completion of(String paramString1, String paramString2)
  {
    return new SimpleCompletion(paramString1, paramString2);
  }
  
  public static Completion of(String paramString)
  {
    return new SimpleCompletion(paramString, "");
  }
  
  private static class SimpleCompletion
    implements Completion
  {
    private String value;
    private String message;
    
    SimpleCompletion(String paramString1, String paramString2)
    {
      if ((paramString1 == null) || (paramString2 == null)) {
        throw new NullPointerException("Null completion strings not accepted.");
      }
      this.value = paramString1;
      this.message = paramString2;
    }
    
    public String getValue()
    {
      return this.value;
    }
    
    public String getMessage()
    {
      return this.message;
    }
    
    public String toString()
    {
      return "[\"" + this.value + "\", \"" + this.message + "\"]";
    }
  }
}
