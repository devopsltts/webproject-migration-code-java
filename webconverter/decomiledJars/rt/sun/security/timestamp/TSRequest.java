package sun.security.timestamp;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Extension;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public class TSRequest
{
  private int version = 1;
  private AlgorithmId hashAlgorithmId = null;
  private byte[] hashValue;
  private String policyId = null;
  private BigInteger nonce = null;
  private boolean returnCertificate = false;
  private X509Extension[] extensions = null;
  
  public TSRequest(String paramString, byte[] paramArrayOfByte, MessageDigest paramMessageDigest)
    throws NoSuchAlgorithmException
  {
    this.policyId = paramString;
    this.hashAlgorithmId = AlgorithmId.get(paramMessageDigest.getAlgorithm());
    this.hashValue = paramMessageDigest.digest(paramArrayOfByte);
  }
  
  public byte[] getHashedMessage()
  {
    return (byte[])this.hashValue.clone();
  }
  
  public void setVersion(int paramInt)
  {
    this.version = paramInt;
  }
  
  public void setPolicyId(String paramString)
  {
    this.policyId = paramString;
  }
  
  public void setNonce(BigInteger paramBigInteger)
  {
    this.nonce = paramBigInteger;
  }
  
  public void requestCertificate(boolean paramBoolean)
  {
    this.returnCertificate = paramBoolean;
  }
  
  public void setExtensions(X509Extension[] paramArrayOfX509Extension)
  {
    this.extensions = paramArrayOfX509Extension;
  }
  
  public byte[] encode()
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(this.version);
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    this.hashAlgorithmId.encode(localDerOutputStream2);
    localDerOutputStream2.putOctetString(this.hashValue);
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    if (this.policyId != null) {
      localDerOutputStream1.putOID(new ObjectIdentifier(this.policyId));
    }
    if (this.nonce != null) {
      localDerOutputStream1.putInteger(this.nonce);
    }
    if (this.returnCertificate) {
      localDerOutputStream1.putBoolean(true);
    }
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write((byte)48, localDerOutputStream1);
    return localDerOutputStream3.toByteArray();
  }
}
