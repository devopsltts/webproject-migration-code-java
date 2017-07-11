package sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.SerialNumber;

class AdaptableX509CertSelector
  extends X509CertSelector
{
  private static final Debug debug = Debug.getInstance("certpath");
  private Date startDate;
  private Date endDate;
  private byte[] ski;
  private BigInteger serial;
  
  AdaptableX509CertSelector() {}
  
  void setValidityPeriod(Date paramDate1, Date paramDate2)
  {
    this.startDate = paramDate1;
    this.endDate = paramDate2;
  }
  
  public void setSubjectKeyIdentifier(byte[] paramArrayOfByte)
  {
    throw new IllegalArgumentException();
  }
  
  public void setSerialNumber(BigInteger paramBigInteger)
  {
    throw new IllegalArgumentException();
  }
  
  void setSkiAndSerialNumber(AuthorityKeyIdentifierExtension paramAuthorityKeyIdentifierExtension)
    throws IOException
  {
    this.ski = null;
    this.serial = null;
    if (paramAuthorityKeyIdentifierExtension != null)
    {
      KeyIdentifier localKeyIdentifier = (KeyIdentifier)paramAuthorityKeyIdentifierExtension.get("key_id");
      if (localKeyIdentifier != null)
      {
        localObject = new DerOutputStream();
        ((DerOutputStream)localObject).putOctetString(localKeyIdentifier.getIdentifier());
        this.ski = ((DerOutputStream)localObject).toByteArray();
      }
      Object localObject = (SerialNumber)paramAuthorityKeyIdentifierExtension.get("serial_number");
      if (localObject != null) {
        this.serial = ((SerialNumber)localObject).getNumber();
      }
    }
  }
  
  public boolean match(Certificate paramCertificate)
  {
    X509Certificate localX509Certificate = (X509Certificate)paramCertificate;
    if (!matchSubjectKeyID(localX509Certificate)) {
      return false;
    }
    int i = localX509Certificate.getVersion();
    if ((this.serial != null) && (i > 2) && (!this.serial.equals(localX509Certificate.getSerialNumber()))) {
      return false;
    }
    if (i < 3)
    {
      if (this.startDate != null) {
        try
        {
          localX509Certificate.checkValidity(this.startDate);
        }
        catch (CertificateException localCertificateException1)
        {
          return false;
        }
      }
      if (this.endDate != null) {
        try
        {
          localX509Certificate.checkValidity(this.endDate);
        }
        catch (CertificateException localCertificateException2)
        {
          return false;
        }
      }
    }
    return super.match(paramCertificate);
  }
  
  private boolean matchSubjectKeyID(X509Certificate paramX509Certificate)
  {
    if (this.ski == null) {
      return true;
    }
    try
    {
      byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.14");
      if (arrayOfByte1 == null)
      {
        if (debug != null) {
          debug.println("AdaptableX509CertSelector.match: no subject key ID extension. Subject: " + paramX509Certificate.getSubjectX500Principal());
        }
        return true;
      }
      DerInputStream localDerInputStream = new DerInputStream(arrayOfByte1);
      byte[] arrayOfByte2 = localDerInputStream.getOctetString();
      if ((arrayOfByte2 == null) || (!Arrays.equals(this.ski, arrayOfByte2)))
      {
        if (debug != null) {
          debug.println("AdaptableX509CertSelector.match: subject key IDs don't match. Expected: " + Arrays.toString(this.ski) + " " + "Cert's: " + Arrays.toString(arrayOfByte2));
        }
        return false;
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null) {
        debug.println("AdaptableX509CertSelector.match: exception in subject key ID check");
      }
      return false;
    }
    return true;
  }
  
  public Object clone()
  {
    AdaptableX509CertSelector localAdaptableX509CertSelector = (AdaptableX509CertSelector)super.clone();
    if (this.startDate != null) {
      localAdaptableX509CertSelector.startDate = ((Date)this.startDate.clone());
    }
    if (this.endDate != null) {
      localAdaptableX509CertSelector.endDate = ((Date)this.endDate.clone());
    }
    if (this.ski != null) {
      localAdaptableX509CertSelector.ski = ((byte[])this.ski.clone());
    }
    return localAdaptableX509CertSelector;
  }
}
