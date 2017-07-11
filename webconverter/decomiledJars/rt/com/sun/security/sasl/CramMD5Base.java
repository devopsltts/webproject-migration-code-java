package com.sun.security.sasl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.security.sasl.SaslException;

abstract class CramMD5Base
{
  protected boolean completed = false;
  protected boolean aborted = false;
  protected byte[] pw;
  private static final int MD5_BLOCKSIZE = 64;
  private static final String SASL_LOGGER_NAME = "javax.security.sasl";
  protected static Logger logger;
  
  protected CramMD5Base()
  {
    initLogger();
  }
  
  public String getMechanismName()
  {
    return "CRAM-MD5";
  }
  
  public boolean isComplete()
  {
    return this.completed;
  }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    if (this.completed) {
      throw new IllegalStateException("CRAM-MD5 supports neither integrity nor privacy");
    }
    throw new IllegalStateException("CRAM-MD5 authentication not completed");
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SaslException
  {
    if (this.completed) {
      throw new IllegalStateException("CRAM-MD5 supports neither integrity nor privacy");
    }
    throw new IllegalStateException("CRAM-MD5 authentication not completed");
  }
  
  public Object getNegotiatedProperty(String paramString)
  {
    if (this.completed)
    {
      if (paramString.equals("javax.security.sasl.qop")) {
        return "auth";
      }
      return null;
    }
    throw new IllegalStateException("CRAM-MD5 authentication not completed");
  }
  
  public void dispose()
    throws SaslException
  {
    clearPassword();
  }
  
  protected void clearPassword()
  {
    if (this.pw != null)
    {
      for (int i = 0; i < this.pw.length; i++) {
        this.pw[i] = 0;
      }
      this.pw = null;
    }
  }
  
  protected void finalize()
  {
    clearPassword();
  }
  
  static final String HMAC_MD5(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws NoSuchAlgorithmException
  {
    MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
    if (paramArrayOfByte1.length > 64) {
      paramArrayOfByte1 = localMessageDigest.digest(paramArrayOfByte1);
    }
    byte[] arrayOfByte1 = new byte[64];
    byte[] arrayOfByte2 = new byte[64];
    for (int i = 0; i < paramArrayOfByte1.length; i++)
    {
      arrayOfByte1[i] = paramArrayOfByte1[i];
      arrayOfByte2[i] = paramArrayOfByte1[i];
    }
    for (i = 0; i < 64; i++)
    {
      int tmp76_74 = i;
      byte[] tmp76_73 = arrayOfByte1;
      tmp76_73[tmp76_74] = ((byte)(tmp76_73[tmp76_74] ^ 0x36));
      int tmp87_85 = i;
      byte[] tmp87_83 = arrayOfByte2;
      tmp87_83[tmp87_85] = ((byte)(tmp87_83[tmp87_85] ^ 0x5C));
    }
    localMessageDigest.update(arrayOfByte1);
    localMessageDigest.update(paramArrayOfByte2);
    byte[] arrayOfByte3 = localMessageDigest.digest();
    localMessageDigest.update(arrayOfByte2);
    localMessageDigest.update(arrayOfByte3);
    arrayOfByte3 = localMessageDigest.digest();
    StringBuffer localStringBuffer = new StringBuffer();
    for (i = 0; i < arrayOfByte3.length; i++) {
      if ((arrayOfByte3[i] & 0xFF) < 16) {
        localStringBuffer.append("0" + Integer.toHexString(arrayOfByte3[i] & 0xFF));
      } else {
        localStringBuffer.append(Integer.toHexString(arrayOfByte3[i] & 0xFF));
      }
    }
    Arrays.fill(arrayOfByte1, (byte)0);
    Arrays.fill(arrayOfByte2, (byte)0);
    arrayOfByte1 = null;
    arrayOfByte2 = null;
    return localStringBuffer.toString();
  }
  
  private static synchronized void initLogger()
  {
    if (logger == null) {
      logger = Logger.getLogger("javax.security.sasl");
    }
  }
}
