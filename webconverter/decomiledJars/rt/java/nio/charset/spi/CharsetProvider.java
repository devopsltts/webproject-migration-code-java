package java.nio.charset.spi;

import java.nio.charset.Charset;
import java.util.Iterator;

public abstract class CharsetProvider
{
  protected CharsetProvider()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("charsetProvider"));
    }
  }
  
  public abstract Iterator<Charset> charsets();
  
  public abstract Charset charsetForName(String paramString);
}
