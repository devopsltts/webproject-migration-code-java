package java.util;

public class DuplicateFormatFlagsException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 18890531L;
  private String flags;
  
  public DuplicateFormatFlagsException(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    this.flags = paramString;
  }
  
  public String getFlags()
  {
    return this.flags;
  }
  
  public String getMessage()
  {
    return String.format("Flags = '%s'", new Object[] { this.flags });
  }
}
