package sun.security.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CRLException;
import java.security.cert.CRLReason;
import java.security.cert.X509CRLEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class X509CRLEntryImpl
  extends X509CRLEntry
  implements Comparable<X509CRLEntryImpl>
{
  private SerialNumber serialNumber = null;
  private Date revocationDate = null;
  private CRLExtensions extensions = null;
  private byte[] revokedCert = null;
  private X500Principal certIssuer;
  private static final boolean isExplicit = false;
  private static final long YR_2050 = 2524636800000L;
  
  public X509CRLEntryImpl(BigInteger paramBigInteger, Date paramDate)
  {
    this.serialNumber = new SerialNumber(paramBigInteger);
    this.revocationDate = paramDate;
  }
  
  public X509CRLEntryImpl(BigInteger paramBigInteger, Date paramDate, CRLExtensions paramCRLExtensions)
  {
    this.serialNumber = new SerialNumber(paramBigInteger);
    this.revocationDate = paramDate;
    this.extensions = paramCRLExtensions;
  }
  
  public X509CRLEntryImpl(byte[] paramArrayOfByte)
    throws CRLException
  {
    try
    {
      parse(new DerValue(paramArrayOfByte));
    }
    catch (IOException localIOException)
    {
      this.revokedCert = null;
      throw new CRLException("Parsing error: " + localIOException.toString());
    }
  }
  
  public X509CRLEntryImpl(DerValue paramDerValue)
    throws CRLException
  {
    try
    {
      parse(paramDerValue);
    }
    catch (IOException localIOException)
    {
      this.revokedCert = null;
      throw new CRLException("Parsing error: " + localIOException.toString());
    }
  }
  
  public boolean hasExtensions()
  {
    return this.extensions != null;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws CRLException
  {
    try
    {
      if (this.revokedCert == null)
      {
        DerOutputStream localDerOutputStream1 = new DerOutputStream();
        this.serialNumber.encode(localDerOutputStream1);
        if (this.revocationDate.getTime() < 2524636800000L) {
          localDerOutputStream1.putUTCTime(this.revocationDate);
        } else {
          localDerOutputStream1.putGeneralizedTime(this.revocationDate);
        }
        if (this.extensions != null) {
          this.extensions.encode(localDerOutputStream1, false);
        }
        DerOutputStream localDerOutputStream2 = new DerOutputStream();
        localDerOutputStream2.write((byte)48, localDerOutputStream1);
        this.revokedCert = localDerOutputStream2.toByteArray();
      }
      paramDerOutputStream.write(this.revokedCert);
    }
    catch (IOException localIOException)
    {
      throw new CRLException("Encoding error: " + localIOException.toString());
    }
  }
  
  public byte[] getEncoded()
    throws CRLException
  {
    return (byte[])getEncoded0().clone();
  }
  
  private byte[] getEncoded0()
    throws CRLException
  {
    if (this.revokedCert == null) {
      encode(new DerOutputStream());
    }
    return this.revokedCert;
  }
  
  public X500Principal getCertificateIssuer()
  {
    return this.certIssuer;
  }
  
  void setCertificateIssuer(X500Principal paramX500Principal1, X500Principal paramX500Principal2)
  {
    if (paramX500Principal1.equals(paramX500Principal2)) {
      this.certIssuer = null;
    } else {
      this.certIssuer = paramX500Principal2;
    }
  }
  
  public BigInteger getSerialNumber()
  {
    return this.serialNumber.getNumber();
  }
  
  public Date getRevocationDate()
  {
    return new Date(this.revocationDate.getTime());
  }
  
  public CRLReason getRevocationReason()
  {
    Extension localExtension = getExtension(PKIXExtensions.ReasonCode_Id);
    if (localExtension == null) {
      return null;
    }
    CRLReasonCodeExtension localCRLReasonCodeExtension = (CRLReasonCodeExtension)localExtension;
    return localCRLReasonCodeExtension.getReasonCode();
  }
  
  public static CRLReason getRevocationReason(X509CRLEntry paramX509CRLEntry)
  {
    try
    {
      byte[] arrayOfByte1 = paramX509CRLEntry.getExtensionValue("2.5.29.21");
      if (arrayOfByte1 == null) {
        return null;
      }
      DerValue localDerValue = new DerValue(arrayOfByte1);
      byte[] arrayOfByte2 = localDerValue.getOctetString();
      CRLReasonCodeExtension localCRLReasonCodeExtension = new CRLReasonCodeExtension(Boolean.FALSE, arrayOfByte2);
      return localCRLReasonCodeExtension.getReasonCode();
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public Integer getReasonCode()
    throws IOException
  {
    Extension localExtension = getExtension(PKIXExtensions.ReasonCode_Id);
    if (localExtension == null) {
      return null;
    }
    CRLReasonCodeExtension localCRLReasonCodeExtension = (CRLReasonCodeExtension)localExtension;
    return localCRLReasonCodeExtension.get("reason");
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(this.serialNumber.toString());
    localStringBuilder.append("  On: " + this.revocationDate.toString());
    if (this.certIssuer != null) {
      localStringBuilder.append("\n    Certificate issuer: " + this.certIssuer);
    }
    if (this.extensions != null)
    {
      Collection localCollection = this.extensions.getAllExtensions();
      Extension[] arrayOfExtension = (Extension[])localCollection.toArray(new Extension[0]);
      localStringBuilder.append("\n    CRL Entry Extensions: " + arrayOfExtension.length);
      for (int i = 0; i < arrayOfExtension.length; i++)
      {
        localStringBuilder.append("\n    [" + (i + 1) + "]: ");
        Extension localExtension = arrayOfExtension[i];
        try
        {
          if (OIDMap.getClass(localExtension.getExtensionId()) == null)
          {
            localStringBuilder.append(localExtension.toString());
            byte[] arrayOfByte = localExtension.getExtensionValue();
            if (arrayOfByte != null)
            {
              DerOutputStream localDerOutputStream = new DerOutputStream();
              localDerOutputStream.putOctetString(arrayOfByte);
              arrayOfByte = localDerOutputStream.toByteArray();
              HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
              localStringBuilder.append("Extension unknown: DER encoded OCTET string =\n" + localHexDumpEncoder.encodeBuffer(arrayOfByte) + "\n");
            }
          }
          else
          {
            localStringBuilder.append(localExtension.toString());
          }
        }
        catch (Exception localException)
        {
          localStringBuilder.append(", Error parsing this extension");
        }
      }
    }
    localStringBuilder.append("\n");
    return localStringBuilder.toString();
  }
  
  public boolean hasUnsupportedCriticalExtension()
  {
    if (this.extensions == null) {
      return false;
    }
    return this.extensions.hasUnsupportedCriticalExtension();
  }
  
  public Set<String> getCriticalExtensionOIDs()
  {
    if (this.extensions == null) {
      return null;
    }
    TreeSet localTreeSet = new TreeSet();
    Iterator localIterator = this.extensions.getAllExtensions().iterator();
    while (localIterator.hasNext())
    {
      Extension localExtension = (Extension)localIterator.next();
      if (localExtension.isCritical()) {
        localTreeSet.add(localExtension.getExtensionId().toString());
      }
    }
    return localTreeSet;
  }
  
  public Set<String> getNonCriticalExtensionOIDs()
  {
    if (this.extensions == null) {
      return null;
    }
    TreeSet localTreeSet = new TreeSet();
    Iterator localIterator = this.extensions.getAllExtensions().iterator();
    while (localIterator.hasNext())
    {
      Extension localExtension = (Extension)localIterator.next();
      if (!localExtension.isCritical()) {
        localTreeSet.add(localExtension.getExtensionId().toString());
      }
    }
    return localTreeSet;
  }
  
  public byte[] getExtensionValue(String paramString)
  {
    if (this.extensions == null) {
      return null;
    }
    try
    {
      String str = OIDMap.getName(new ObjectIdentifier(paramString));
      Object localObject1 = null;
      if (str == null)
      {
        localObject2 = new ObjectIdentifier(paramString);
        localObject3 = null;
        Enumeration localEnumeration = this.extensions.getElements();
        while (localEnumeration.hasMoreElements())
        {
          localObject3 = (Extension)localEnumeration.nextElement();
          ObjectIdentifier localObjectIdentifier = ((Extension)localObject3).getExtensionId();
          if (localObjectIdentifier.equals(localObject2)) {
            localObject1 = localObject3;
          }
        }
      }
      else
      {
        localObject1 = this.extensions.get(str);
      }
      if (localObject1 == null) {
        return null;
      }
      Object localObject2 = ((Extension)localObject1).getExtensionValue();
      if (localObject2 == null) {
        return null;
      }
      Object localObject3 = new DerOutputStream();
      ((DerOutputStream)localObject3).putOctetString((byte[])localObject2);
      return ((DerOutputStream)localObject3).toByteArray();
    }
    catch (Exception localException) {}
    return null;
  }
  
  public Extension getExtension(ObjectIdentifier paramObjectIdentifier)
  {
    if (this.extensions == null) {
      return null;
    }
    return this.extensions.get(OIDMap.getName(paramObjectIdentifier));
  }
  
  private void parse(DerValue paramDerValue)
    throws CRLException, IOException
  {
    if (paramDerValue.tag != 48) {
      throw new CRLException("Invalid encoded RevokedCertificate, starting sequence tag missing.");
    }
    if (paramDerValue.data.available() == 0) {
      throw new CRLException("No data encoded for RevokedCertificates");
    }
    this.revokedCert = paramDerValue.toByteArray();
    DerInputStream localDerInputStream = paramDerValue.toDerInputStream();
    DerValue localDerValue = localDerInputStream.getDerValue();
    this.serialNumber = new SerialNumber(localDerValue);
    int i = paramDerValue.data.peekByte();
    if ((byte)i == 23) {
      this.revocationDate = paramDerValue.data.getUTCTime();
    } else if ((byte)i == 24) {
      this.revocationDate = paramDerValue.data.getGeneralizedTime();
    } else {
      throw new CRLException("Invalid encoding for revocation date");
    }
    if (paramDerValue.data.available() == 0) {
      return;
    }
    this.extensions = new CRLExtensions(paramDerValue.toDerInputStream());
  }
  
  public static X509CRLEntryImpl toImpl(X509CRLEntry paramX509CRLEntry)
    throws CRLException
  {
    if ((paramX509CRLEntry instanceof X509CRLEntryImpl)) {
      return (X509CRLEntryImpl)paramX509CRLEntry;
    }
    return new X509CRLEntryImpl(paramX509CRLEntry.getEncoded());
  }
  
  CertificateIssuerExtension getCertificateIssuerExtension()
  {
    return (CertificateIssuerExtension)getExtension(PKIXExtensions.CertificateIssuer_Id);
  }
  
  public Map<String, java.security.cert.Extension> getExtensions()
  {
    if (this.extensions == null) {
      return Collections.emptyMap();
    }
    Collection localCollection = this.extensions.getAllExtensions();
    TreeMap localTreeMap = new TreeMap();
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      Extension localExtension = (Extension)localIterator.next();
      localTreeMap.put(localExtension.getId(), localExtension);
    }
    return localTreeMap;
  }
  
  public int compareTo(X509CRLEntryImpl paramX509CRLEntryImpl)
  {
    int i = getSerialNumber().compareTo(paramX509CRLEntryImpl.getSerialNumber());
    if (i != 0) {
      return i;
    }
    try
    {
      byte[] arrayOfByte1 = getEncoded0();
      byte[] arrayOfByte2 = paramX509CRLEntryImpl.getEncoded0();
      for (int j = 0; (j < arrayOfByte1.length) && (j < arrayOfByte2.length); j++)
      {
        int k = arrayOfByte1[j] & 0xFF;
        int m = arrayOfByte2[j] & 0xFF;
        if (k != m) {
          return k - m;
        }
      }
      return arrayOfByte1.length - arrayOfByte2.length;
    }
    catch (CRLException localCRLException) {}
    return -1;
  }
}
