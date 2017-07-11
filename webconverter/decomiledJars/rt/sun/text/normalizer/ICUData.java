package sun.text.normalizer;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.MissingResourceException;

public final class ICUData
{
  public ICUData() {}
  
  private static InputStream getStream(Class<ICUData> paramClass, final String paramString, boolean paramBoolean)
  {
    InputStream localInputStream = null;
    if (System.getSecurityManager() != null) {
      localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction()
      {
        public InputStream run()
        {
          return this.val$root.getResourceAsStream(paramString);
        }
      });
    } else {
      localInputStream = paramClass.getResourceAsStream(paramString);
    }
    if ((localInputStream == null) && (paramBoolean)) {
      throw new MissingResourceException("could not locate data", paramClass.getPackage().getName(), paramString);
    }
    return localInputStream;
  }
  
  public static InputStream getStream(String paramString)
  {
    return getStream(ICUData.class, paramString, false);
  }
  
  public static InputStream getRequiredStream(String paramString)
  {
    return getStream(ICUData.class, paramString, true);
  }
}
