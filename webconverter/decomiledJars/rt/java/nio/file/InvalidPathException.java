package java.nio.file;

public class InvalidPathException
  extends IllegalArgumentException
{
  static final long serialVersionUID = 4355821422286746137L;
  private String input;
  private int index;
  
  public InvalidPathException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString2);
    if ((paramString1 == null) || (paramString2 == null)) {
      throw new NullPointerException();
    }
    if (paramInt < -1) {
      throw new IllegalArgumentException();
    }
    this.input = paramString1;
    this.index = paramInt;
  }
  
  public InvalidPathException(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, -1);
  }
  
  public String getInput()
  {
    return this.input;
  }
  
  public String getReason()
  {
    return super.getMessage();
  }
  
  public int getIndex()
  {
    return this.index;
  }
  
  public String getMessage()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(getReason());
    if (this.index > -1)
    {
      localStringBuffer.append(" at index ");
      localStringBuffer.append(this.index);
    }
    localStringBuffer.append(": ");
    localStringBuffer.append(this.input);
    return localStringBuffer.toString();
  }
}
