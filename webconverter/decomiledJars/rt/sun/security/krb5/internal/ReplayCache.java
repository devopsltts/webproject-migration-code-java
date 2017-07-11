package sun.security.krb5.internal;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.rcache.AuthTimeWithHash;
import sun.security.krb5.internal.rcache.DflCache;
import sun.security.krb5.internal.rcache.MemoryCache;

public abstract class ReplayCache
{
  public ReplayCache() {}
  
  public static ReplayCache getInstance(String paramString)
  {
    if (paramString == null) {
      return new MemoryCache();
    }
    if ((paramString.equals("dfl")) || (paramString.startsWith("dfl:"))) {
      return new DflCache(paramString);
    }
    if (paramString.equals("none")) {
      new ReplayCache()
      {
        public void checkAndStore(KerberosTime paramAnonymousKerberosTime, AuthTimeWithHash paramAnonymousAuthTimeWithHash)
          throws KrbApErrException
        {}
      };
    }
    throw new IllegalArgumentException("Unknown type: " + paramString);
  }
  
  public static ReplayCache getInstance()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.krb5.rcache"));
    return getInstance(str);
  }
  
  public abstract void checkAndStore(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash)
    throws KrbApErrException;
}
