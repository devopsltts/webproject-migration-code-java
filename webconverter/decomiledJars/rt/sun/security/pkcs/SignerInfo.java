package sun.security.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import sun.misc.HexDumpEncoder;
import sun.security.timestamp.TimestampToken;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.X500Name;

public class SignerInfo
  implements DerEncoder
{
  BigInteger version;
  X500Name issuerName;
  BigInteger certificateSerialNumber;
  AlgorithmId digestAlgorithmId;
  AlgorithmId digestEncryptionAlgorithmId;
  byte[] encryptedDigest;
  Timestamp timestamp;
  private boolean hasTimestamp = true;
  private static final Debug debug = Debug.getInstance("jar");
  PKCS9Attributes authenticatedAttributes;
  PKCS9Attributes unauthenticatedAttributes;
  
  public SignerInfo(X500Name paramX500Name, BigInteger paramBigInteger, AlgorithmId paramAlgorithmId1, AlgorithmId paramAlgorithmId2, byte[] paramArrayOfByte)
  {
    this.version = BigInteger.ONE;
    this.issuerName = paramX500Name;
    this.certificateSerialNumber = paramBigInteger;
    this.digestAlgorithmId = paramAlgorithmId1;
    this.digestEncryptionAlgorithmId = paramAlgorithmId2;
    this.encryptedDigest = paramArrayOfByte;
  }
  
  public SignerInfo(X500Name paramX500Name, BigInteger paramBigInteger, AlgorithmId paramAlgorithmId1, PKCS9Attributes paramPKCS9Attributes1, AlgorithmId paramAlgorithmId2, byte[] paramArrayOfByte, PKCS9Attributes paramPKCS9Attributes2)
  {
    this.version = BigInteger.ONE;
    this.issuerName = paramX500Name;
    this.certificateSerialNumber = paramBigInteger;
    this.digestAlgorithmId = paramAlgorithmId1;
    this.authenticatedAttributes = paramPKCS9Attributes1;
    this.digestEncryptionAlgorithmId = paramAlgorithmId2;
    this.encryptedDigest = paramArrayOfByte;
    this.unauthenticatedAttributes = paramPKCS9Attributes2;
  }
  
  public SignerInfo(DerInputStream paramDerInputStream)
    throws IOException, ParsingException
  {
    this(paramDerInputStream, false);
  }
  
  public SignerInfo(DerInputStream paramDerInputStream, boolean paramBoolean)
    throws IOException, ParsingException
  {
    this.version = paramDerInputStream.getBigInteger();
    DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(2);
    byte[] arrayOfByte = arrayOfDerValue[0].toByteArray();
    this.issuerName = new X500Name(new DerValue((byte)48, arrayOfByte));
    this.certificateSerialNumber = arrayOfDerValue[1].getBigInteger();
    DerValue localDerValue = paramDerInputStream.getDerValue();
    this.digestAlgorithmId = AlgorithmId.parse(localDerValue);
    if (paramBoolean) {
      paramDerInputStream.getSet(0);
    } else if ((byte)paramDerInputStream.peekByte() == -96) {
      this.authenticatedAttributes = new PKCS9Attributes(paramDerInputStream);
    }
    localDerValue = paramDerInputStream.getDerValue();
    this.digestEncryptionAlgorithmId = AlgorithmId.parse(localDerValue);
    this.encryptedDigest = paramDerInputStream.getOctetString();
    if (paramBoolean) {
      paramDerInputStream.getSet(0);
    } else if ((paramDerInputStream.available() != 0) && ((byte)paramDerInputStream.peekByte() == -95)) {
      this.unauthenticatedAttributes = new PKCS9Attributes(paramDerInputStream, true);
    }
    if (paramDerInputStream.available() != 0) {
      throw new ParsingException("extra data at the end");
    }
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    derEncode(paramDerOutputStream);
  }
  
  public void derEncode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(this.version);
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    this.issuerName.encode(localDerOutputStream2);
    localDerOutputStream2.putInteger(this.certificateSerialNumber);
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    this.digestAlgorithmId.encode(localDerOutputStream1);
    if (this.authenticatedAttributes != null) {
      this.authenticatedAttributes.encode((byte)-96, localDerOutputStream1);
    }
    this.digestEncryptionAlgorithmId.encode(localDerOutputStream1);
    localDerOutputStream1.putOctetString(this.encryptedDigest);
    if (this.unauthenticatedAttributes != null) {
      this.unauthenticatedAttributes.encode((byte)-95, localDerOutputStream1);
    }
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write((byte)48, localDerOutputStream1);
    paramOutputStream.write(localDerOutputStream3.toByteArray());
  }
  
  public X509Certificate getCertificate(PKCS7 paramPKCS7)
    throws IOException
  {
    return paramPKCS7.getCertificate(this.certificateSerialNumber, this.issuerName);
  }
  
  public ArrayList<X509Certificate> getCertificateChain(PKCS7 paramPKCS7)
    throws IOException
  {
    X509Certificate localX509Certificate1 = paramPKCS7.getCertificate(this.certificateSerialNumber, this.issuerName);
    if (localX509Certificate1 == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(localX509Certificate1);
    X509Certificate[] arrayOfX509Certificate = paramPKCS7.getCertificates();
    if ((arrayOfX509Certificate == null) || (localX509Certificate1.getSubjectDN().equals(localX509Certificate1.getIssuerDN()))) {
      return localArrayList;
    }
    Principal localPrincipal = localX509Certificate1.getIssuerDN();
    int i = 0;
    for (;;)
    {
      int j = 0;
      for (int k = i; k < arrayOfX509Certificate.length; k++) {
        if (localPrincipal.equals(arrayOfX509Certificate[k].getSubjectDN()))
        {
          localArrayList.add(arrayOfX509Certificate[k]);
          if (arrayOfX509Certificate[k].getSubjectDN().equals(arrayOfX509Certificate[k].getIssuerDN()))
          {
            i = arrayOfX509Certificate.length;
          }
          else
          {
            localPrincipal = arrayOfX509Certificate[k].getIssuerDN();
            X509Certificate localX509Certificate2 = arrayOfX509Certificate[i];
            arrayOfX509Certificate[i] = arrayOfX509Certificate[k];
            arrayOfX509Certificate[k] = localX509Certificate2;
            i++;
          }
          j = 1;
          break;
        }
      }
      if (j == 0) {
        break;
      }
    }
    return localArrayList;
  }
  
  SignerInfo verify(PKCS7 paramPKCS7, byte[] paramArrayOfByte)
    throws NoSuchAlgorithmException, SignatureException
  {
    try
    {
      ContentInfo localContentInfo = paramPKCS7.getContentInfo();
      if (paramArrayOfByte == null) {
        paramArrayOfByte = localContentInfo.getContentBytes();
      }
      String str = getDigestAlgorithmId().getName();
      byte[] arrayOfByte;
      if (this.authenticatedAttributes == null)
      {
        arrayOfByte = paramArrayOfByte;
      }
      else
      {
        localObject1 = (ObjectIdentifier)this.authenticatedAttributes.getAttributeValue(PKCS9Attribute.CONTENT_TYPE_OID);
        if ((localObject1 == null) || (!((ObjectIdentifier)localObject1).equals(localContentInfo.contentType))) {
          return null;
        }
        localObject2 = (byte[])this.authenticatedAttributes.getAttributeValue(PKCS9Attribute.MESSAGE_DIGEST_OID);
        if (localObject2 == null) {
          return null;
        }
        localObject3 = MessageDigest.getInstance(str);
        localObject4 = ((MessageDigest)localObject3).digest(paramArrayOfByte);
        if (localObject2.length != localObject4.length) {
          return null;
        }
        for (int i = 0; i < localObject2.length; i++) {
          if (localObject2[i] != localObject4[i]) {
            return null;
          }
        }
        arrayOfByte = this.authenticatedAttributes.getDerEncoding();
      }
      Object localObject1 = getDigestEncryptionAlgorithmId().getName();
      Object localObject2 = AlgorithmId.getEncAlgFromSigAlg((String)localObject1);
      if (localObject2 != null) {
        localObject1 = localObject2;
      }
      Object localObject3 = AlgorithmId.makeSigAlg(str, (String)localObject1);
      Object localObject4 = Signature.getInstance((String)localObject3);
      X509Certificate localX509Certificate = getCertificate(paramPKCS7);
      if (localX509Certificate == null) {
        return null;
      }
      if (localX509Certificate.hasUnsupportedCriticalExtension()) {
        throw new SignatureException("Certificate has unsupported critical extension(s)");
      }
      boolean[] arrayOfBoolean = localX509Certificate.getKeyUsage();
      if (arrayOfBoolean != null)
      {
        try
        {
          localObject5 = new KeyUsageExtension(arrayOfBoolean);
        }
        catch (IOException localIOException2)
        {
          throw new SignatureException("Failed to parse keyUsage extension");
        }
        boolean bool1 = ((KeyUsageExtension)localObject5).get("digital_signature").booleanValue();
        boolean bool2 = ((KeyUsageExtension)localObject5).get("non_repudiation").booleanValue();
        if ((!bool1) && (!bool2)) {
          throw new SignatureException("Key usage restricted: cannot be used for digital signatures");
        }
      }
      Object localObject5 = localX509Certificate.getPublicKey();
      ((Signature)localObject4).initVerify((PublicKey)localObject5);
      ((Signature)localObject4).update(arrayOfByte);
      if (((Signature)localObject4).verify(this.encryptedDigest)) {
        return this;
      }
    }
    catch (IOException localIOException1)
    {
      throw new SignatureException("IO error verifying signature:\n" + localIOException1.getMessage());
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new SignatureException("InvalidKey: " + localInvalidKeyException.getMessage());
    }
    return null;
  }
  
  SignerInfo verify(PKCS7 paramPKCS7)
    throws NoSuchAlgorithmException, SignatureException
  {
    return verify(paramPKCS7, null);
  }
  
  public BigInteger getVersion()
  {
    return this.version;
  }
  
  public X500Name getIssuerName()
  {
    return this.issuerName;
  }
  
  public BigInteger getCertificateSerialNumber()
  {
    return this.certificateSerialNumber;
  }
  
  public AlgorithmId getDigestAlgorithmId()
  {
    return this.digestAlgorithmId;
  }
  
  public PKCS9Attributes getAuthenticatedAttributes()
  {
    return this.authenticatedAttributes;
  }
  
  public AlgorithmId getDigestEncryptionAlgorithmId()
  {
    return this.digestEncryptionAlgorithmId;
  }
  
  public byte[] getEncryptedDigest()
  {
    return this.encryptedDigest;
  }
  
  public PKCS9Attributes getUnauthenticatedAttributes()
  {
    return this.unauthenticatedAttributes;
  }
  
  public Timestamp getTimestamp()
    throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException
  {
    if ((this.timestamp != null) || (!this.hasTimestamp)) {
      return this.timestamp;
    }
    if (this.unauthenticatedAttributes == null)
    {
      this.hasTimestamp = false;
      return null;
    }
    PKCS9Attribute localPKCS9Attribute = this.unauthenticatedAttributes.getAttribute(PKCS9Attribute.SIGNATURE_TIMESTAMP_TOKEN_OID);
    if (localPKCS9Attribute == null)
    {
      this.hasTimestamp = false;
      return null;
    }
    PKCS7 localPKCS7 = new PKCS7((byte[])localPKCS9Attribute.getValue());
    byte[] arrayOfByte = localPKCS7.getContentInfo().getData();
    SignerInfo[] arrayOfSignerInfo = localPKCS7.verify(arrayOfByte);
    ArrayList localArrayList = arrayOfSignerInfo[0].getCertificateChain(localPKCS7);
    CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
    CertPath localCertPath = localCertificateFactory.generateCertPath(localArrayList);
    TimestampToken localTimestampToken = new TimestampToken(arrayOfByte);
    verifyTimestamp(localTimestampToken);
    this.timestamp = new Timestamp(localTimestampToken.getDate(), localCertPath);
    return this.timestamp;
  }
  
  private void verifyTimestamp(TimestampToken paramTimestampToken)
    throws NoSuchAlgorithmException, SignatureException
  {
    MessageDigest localMessageDigest = MessageDigest.getInstance(paramTimestampToken.getHashAlgorithm().getName());
    if (!Arrays.equals(paramTimestampToken.getHashedMessage(), localMessageDigest.digest(this.encryptedDigest))) {
      throw new SignatureException("Signature timestamp (#" + paramTimestampToken.getSerialNumber() + ") generated on " + paramTimestampToken.getDate() + " is inapplicable");
    }
    if (debug != null)
    {
      debug.println();
      debug.println("Detected signature timestamp (#" + paramTimestampToken.getSerialNumber() + ") generated on " + paramTimestampToken.getDate());
      debug.println();
    }
  }
  
  public String toString()
  {
    HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
    String str = "";
    str = str + "Signer Info for (issuer): " + this.issuerName + "\n";
    str = str + "\tversion: " + Debug.toHexString(this.version) + "\n";
    str = str + "\tcertificateSerialNumber: " + Debug.toHexString(this.certificateSerialNumber) + "\n";
    str = str + "\tdigestAlgorithmId: " + this.digestAlgorithmId + "\n";
    if (this.authenticatedAttributes != null) {
      str = str + "\tauthenticatedAttributes: " + this.authenticatedAttributes + "\n";
    }
    str = str + "\tdigestEncryptionAlgorithmId: " + this.digestEncryptionAlgorithmId + "\n";
    str = str + "\tencryptedDigest: \n" + localHexDumpEncoder.encodeBuffer(this.encryptedDigest) + "\n";
    if (this.unauthenticatedAttributes != null) {
      str = str + "\tunauthenticatedAttributes: " + this.unauthenticatedAttributes + "\n";
    }
    return str;
  }
}
