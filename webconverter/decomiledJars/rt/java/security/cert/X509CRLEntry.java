package java.security.cert;

import java.math.BigInteger;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.X509CRLEntryImpl;

public abstract class X509CRLEntry
  implements X509Extension
{
  public X509CRLEntry() {}
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof X509CRLEntry)) {
      return false;
    }
    try
    {
      byte[] arrayOfByte1 = getEncoded();
      byte[] arrayOfByte2 = ((X509CRLEntry)paramObject).getEncoded();
      if (arrayOfByte1.length != arrayOfByte2.length) {
        return false;
      }
      for (int i = 0; i < arrayOfByte1.length; i++) {
        if (arrayOfByte1[i] != arrayOfByte2[i]) {
          return false;
        }
      }
    }
    catch (CRLException localCRLException)
    {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 0;
    try
    {
      byte[] arrayOfByte = getEncoded();
      for (int j = 1; j < arrayOfByte.length; j++) {
        i += arrayOfByte[j] * j;
      }
    }
    catch (CRLException localCRLException)
    {
      return i;
    }
    return i;
  }
  
  public abstract byte[] getEncoded()
    throws CRLException;
  
  public abstract BigInteger getSerialNumber();
  
  public X500Principal getCertificateIssuer()
  {
    return null;
  }
  
  public abstract Date getRevocationDate();
  
  public abstract boolean hasExtensions();
  
  public abstract String toString();
  
  public CRLReason getRevocationReason()
  {
    if (!hasExtensions()) {
      return null;
    }
    return X509CRLEntryImpl.getRevocationReason(this);
  }
}
