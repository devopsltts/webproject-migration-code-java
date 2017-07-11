package sun.nio.fs;

import java.io.IOException;
import java.util.Map;

abstract interface DynamicFileAttributeView
{
  public abstract void setAttribute(String paramString, Object paramObject)
    throws IOException;
  
  public abstract Map<String, Object> readAttributes(String[] paramArrayOfString)
    throws IOException;
}
