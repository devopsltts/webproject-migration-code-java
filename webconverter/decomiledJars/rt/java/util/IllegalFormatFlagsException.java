package java.util;

public class IllegalFormatFlagsException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 790824L;
  private String flags;
  
  public IllegalFormatFlagsException(String paramString)
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
    return "Flags = '" + this.flags + "'";
  }
}
