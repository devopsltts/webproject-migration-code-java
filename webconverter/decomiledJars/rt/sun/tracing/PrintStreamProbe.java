package sun.tracing;

import java.io.PrintStream;

class PrintStreamProbe
  extends ProbeSkeleton
{
  private PrintStreamProvider provider;
  private String name;
  
  PrintStreamProbe(PrintStreamProvider paramPrintStreamProvider, String paramString, Class<?>[] paramArrayOfClass)
  {
    super(paramArrayOfClass);
    this.provider = paramPrintStreamProvider;
    this.name = paramString;
  }
  
  public boolean isEnabled()
  {
    return true;
  }
  
  public void uncheckedTrigger(Object[] paramArrayOfObject)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(this.provider.getName());
    localStringBuffer.append(".");
    localStringBuffer.append(this.name);
    localStringBuffer.append("(");
    int i = 1;
    for (Object localObject : paramArrayOfObject)
    {
      if (i == 0) {
        localStringBuffer.append(",");
      } else {
        i = 0;
      }
      localStringBuffer.append(localObject.toString());
    }
    localStringBuffer.append(")");
    this.provider.getStream().println(localStringBuffer.toString());
  }
}
