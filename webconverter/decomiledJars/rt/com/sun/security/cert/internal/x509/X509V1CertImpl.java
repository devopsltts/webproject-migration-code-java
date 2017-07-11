package com.sun.security.cert.internal.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateFactory;
import java.util.Date;

public class X509V1CertImpl
  extends javax.security.cert.X509Certificate
  implements Serializable
{
  static final long serialVersionUID = -2048442350420423405L;
  private java.security.cert.X509Certificate wrappedCert;
  
  private static synchronized CertificateFactory getFactory()
    throws java.security.cert.CertificateException
  {
    return CertificateFactory.getInstance("X.509");
  }
  
  public X509V1CertImpl() {}
  
  public X509V1CertImpl(byte[] paramArrayOfByte)
    throws javax.security.cert.CertificateException
  {
    try
    {
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      this.wrappedCert = ((java.security.cert.X509Certificate)getFactory().generateCertificate(localByteArrayInputStream));
    }
    catch (java.security.cert.CertificateException localCertificateException)
    {
      throw new javax.security.cert.CertificateException(localCertificateException.getMessage());
    }
  }
  
  public X509V1CertImpl(InputStream paramInputStream)
    throws javax.security.cert.CertificateException
  {
    try
    {
      this.wrappedCert = ((java.security.cert.X509Certificate)getFactory().generateCertificate(paramInputStream));
    }
    catch (java.security.cert.CertificateException localCertificateException)
    {
      throw new javax.security.cert.CertificateException(localCertificateException.getMessage());
    }
  }
  
  public byte[] getEncoded()
    throws javax.security.cert.CertificateEncodingException
  {
    try
    {
      return this.wrappedCert.getEncoded();
    }
    catch (java.security.cert.CertificateEncodingException localCertificateEncodingException)
    {
      throw new javax.security.cert.CertificateEncodingException(localCertificateEncodingException.getMessage());
    }
  }
  
  public void verify(PublicKey paramPublicKey)
    throws javax.security.cert.CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
  {
    try
    {
      this.wrappedCert.verify(paramPublicKey);
    }
    catch (java.security.cert.CertificateException localCertificateException)
    {
      throw new javax.security.cert.CertificateException(localCertificateException.getMessage());
    }
  }
  
  public void verify(PublicKey paramPublicKey, String paramString)
    throws javax.security.cert.CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
  {
    try
    {
      this.wrappedCert.verify(paramPublicKey, paramString);
    }
    catch (java.security.cert.CertificateException localCertificateException)
    {
      throw new javax.security.cert.CertificateException(localCertificateException.getMessage());
    }
  }
  
  public void checkValidity()
    throws javax.security.cert.CertificateExpiredException, javax.security.cert.CertificateNotYetValidException
  {
    checkValidity(new Date());
  }
  
  public void checkValidity(Date paramDate)
    throws javax.security.cert.CertificateExpiredException, javax.security.cert.CertificateNotYetValidException
  {
    try
    {
      this.wrappedCert.checkValidity(paramDate);
    }
    catch (java.security.cert.CertificateNotYetValidException localCertificateNotYetValidException)
    {
      throw new javax.security.cert.CertificateNotYetValidException(localCertificateNotYetValidException.getMessage());
    }
    catch (java.security.cert.CertificateExpiredException localCertificateExpiredException)
    {
      throw new javax.security.cert.CertificateExpiredException(localCertificateExpiredException.getMessage());
    }
  }
  
  public String toString()
  {
    return this.wrappedCert.toString();
  }
  
  public PublicKey getPublicKey()
  {
    PublicKey localPublicKey = this.wrappedCert.getPublicKey();
    return localPublicKey;
  }
  
  public int getVersion()
  {
    return this.wrappedCert.getVersion() - 1;
  }
  
  public BigInteger getSerialNumber()
  {
    return this.wrappedCert.getSerialNumber();
  }
  
  public Principal getSubjectDN()
  {
    return this.wrappedCert.getSubjectDN();
  }
  
  public Principal getIssuerDN()
  {
    return this.wrappedCert.getIssuerDN();
  }
  
  public Date getNotBefore()
  {
    return this.wrappedCert.getNotBefore();
  }
  
  public Date getNotAfter()
  {
    return this.wrappedCert.getNotAfter();
  }
  
  public String getSigAlgName()
  {
    return this.wrappedCert.getSigAlgName();
  }
  
  public String getSigAlgOID()
  {
    return this.wrappedCert.getSigAlgOID();
  }
  
  public byte[] getSigAlgParams()
  {
    return this.wrappedCert.getSigAlgParams();
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    try
    {
      paramObjectOutputStream.write(getEncoded());
    }
    catch (javax.security.cert.CertificateEncodingException localCertificateEncodingException)
    {
      throw new IOException("getEncoded failed: " + localCertificateEncodingException.getMessage());
    }
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException
  {
    try
    {
      this.wrappedCert = ((java.security.cert.X509Certificate)getFactory().generateCertificate(paramObjectInputStream));
    }
    catch (java.security.cert.CertificateException localCertificateException)
    {
      throw new IOException("generateCertificate failed: " + localCertificateException.getMessage());
    }
  }
  
  public java.security.cert.X509Certificate getX509Certificate()
  {
    return this.wrappedCert;
  }
}
