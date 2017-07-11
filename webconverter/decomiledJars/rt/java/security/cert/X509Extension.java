package java.security.cert;

import java.util.Set;

public abstract interface X509Extension
{
  public abstract boolean hasUnsupportedCriticalExtension();
  
  public abstract Set<String> getCriticalExtensionOIDs();
  
  public abstract Set<String> getNonCriticalExtensionOIDs();
  
  public abstract byte[] getExtensionValue(String paramString);
}
