package java.util.regex;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public class PatternSyntaxException
  extends IllegalArgumentException
{
  private static final long serialVersionUID = -3864639126226059218L;
  private final String desc;
  private final String pattern;
  private final int index;
  private static final String nl = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
  
  public PatternSyntaxException(String paramString1, String paramString2, int paramInt)
  {
    this.desc = paramString1;
    this.pattern = paramString2;
    this.index = paramInt;
  }
  
  public int getIndex()
  {
    return this.index;
  }
  
  public String getDescription()
  {
    return this.desc;
  }
  
  public String getPattern()
  {
    return this.pattern;
  }
  
  public String getMessage()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(this.desc);
    if (this.index >= 0)
    {
      localStringBuffer.append(" near index ");
      localStringBuffer.append(this.index);
    }
    localStringBuffer.append(nl);
    localStringBuffer.append(this.pattern);
    if (this.index >= 0)
    {
      localStringBuffer.append(nl);
      for (int i = 0; i < this.index; i++) {
        localStringBuffer.append(' ');
      }
      localStringBuffer.append('^');
    }
    return localStringBuffer.toString();
  }
}
