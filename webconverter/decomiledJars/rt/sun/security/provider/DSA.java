package sun.security.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.util.Arrays;
import sun.security.jca.JCAUtil;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

abstract class DSA
  extends SignatureSpi
{
  private static final boolean debug = false;
  private DSAParams params;
  private BigInteger presetP;
  private BigInteger presetQ;
  private BigInteger presetG;
  private BigInteger presetY;
  private BigInteger presetX;
  private SecureRandom signingRandom;
  private final MessageDigest md;
  
  DSA(MessageDigest paramMessageDigest)
  {
    this.md = paramMessageDigest;
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey)
    throws InvalidKeyException
  {
    if (!(paramPrivateKey instanceof DSAPrivateKey)) {
      throw new InvalidKeyException("not a DSA private key: " + paramPrivateKey);
    }
    DSAPrivateKey localDSAPrivateKey = (DSAPrivateKey)paramPrivateKey;
    DSAParams localDSAParams = localDSAPrivateKey.getParams();
    if (localDSAParams == null) {
      throw new InvalidKeyException("DSA private key lacks parameters");
    }
    this.params = localDSAParams;
    this.presetX = localDSAPrivateKey.getX();
    this.presetY = null;
    this.presetP = localDSAParams.getP();
    this.presetQ = localDSAParams.getQ();
    this.presetG = localDSAParams.getG();
    this.md.reset();
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey)
    throws InvalidKeyException
  {
    if (!(paramPublicKey instanceof DSAPublicKey)) {
      throw new InvalidKeyException("not a DSA public key: " + paramPublicKey);
    }
    DSAPublicKey localDSAPublicKey = (DSAPublicKey)paramPublicKey;
    DSAParams localDSAParams = localDSAPublicKey.getParams();
    if (localDSAParams == null) {
      throw new InvalidKeyException("DSA public key lacks parameters");
    }
    this.params = localDSAParams;
    this.presetY = localDSAPublicKey.getY();
    this.presetX = null;
    this.presetP = localDSAParams.getP();
    this.presetQ = localDSAParams.getQ();
    this.presetG = localDSAParams.getG();
    this.md.reset();
  }
  
  protected void engineUpdate(byte paramByte)
  {
    this.md.update(paramByte);
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.md.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  protected void engineUpdate(ByteBuffer paramByteBuffer)
  {
    this.md.update(paramByteBuffer);
  }
  
  protected byte[] engineSign()
    throws SignatureException
  {
    BigInteger localBigInteger1 = generateK(this.presetQ);
    BigInteger localBigInteger2 = generateR(this.presetP, this.presetQ, this.presetG, localBigInteger1);
    BigInteger localBigInteger3 = generateS(this.presetX, this.presetQ, localBigInteger2, localBigInteger1);
    try
    {
      DerOutputStream localDerOutputStream = new DerOutputStream(100);
      localDerOutputStream.putInteger(localBigInteger2);
      localDerOutputStream.putInteger(localBigInteger3);
      DerValue localDerValue = new DerValue((byte)48, localDerOutputStream.toByteArray());
      return localDerValue.toByteArray();
    }
    catch (IOException localIOException)
    {
      throw new SignatureException("error encoding signature");
    }
  }
  
  protected boolean engineVerify(byte[] paramArrayOfByte)
    throws SignatureException
  {
    return engineVerify(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  protected boolean engineVerify(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    BigInteger localBigInteger1 = null;
    BigInteger localBigInteger2 = null;
    Object localObject;
    try
    {
      DerInputStream localDerInputStream = new DerInputStream(paramArrayOfByte, paramInt1, paramInt2);
      localObject = localDerInputStream.getSequence(2);
      localBigInteger1 = localObject[0].getBigInteger();
      localBigInteger2 = localObject[1].getBigInteger();
    }
    catch (IOException localIOException)
    {
      throw new SignatureException("invalid encoding for signature");
    }
    if (localBigInteger1.signum() < 0) {
      localBigInteger1 = new BigInteger(1, localBigInteger1.toByteArray());
    }
    if (localBigInteger2.signum() < 0) {
      localBigInteger2 = new BigInteger(1, localBigInteger2.toByteArray());
    }
    if ((localBigInteger1.compareTo(this.presetQ) == -1) && (localBigInteger2.compareTo(this.presetQ) == -1))
    {
      BigInteger localBigInteger3 = generateW(this.presetP, this.presetQ, this.presetG, localBigInteger2);
      localObject = generateV(this.presetY, this.presetP, this.presetQ, this.presetG, localBigInteger3, localBigInteger1);
      return ((BigInteger)localObject).equals(localBigInteger1);
    }
    throw new SignatureException("invalid signature: out of range values");
  }
  
  @Deprecated
  protected void engineSetParameter(String paramString, Object paramObject)
  {
    throw new InvalidParameterException("No parameter accepted");
  }
  
  @Deprecated
  protected Object engineGetParameter(String paramString)
  {
    return null;
  }
  
  private BigInteger generateR(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
  {
    BigInteger localBigInteger = paramBigInteger3.modPow(paramBigInteger4, paramBigInteger1);
    return localBigInteger.mod(paramBigInteger2);
  }
  
  private BigInteger generateS(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
    throws SignatureException
  {
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = this.md.digest();
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new SignatureException(localRuntimeException.getMessage());
    }
    int i = paramBigInteger2.bitLength() / 8;
    if (i < arrayOfByte.length) {
      arrayOfByte = Arrays.copyOfRange(arrayOfByte, 0, i);
    }
    BigInteger localBigInteger1 = new BigInteger(1, arrayOfByte);
    BigInteger localBigInteger2 = paramBigInteger4.modInverse(paramBigInteger2);
    return paramBigInteger1.multiply(paramBigInteger3).add(localBigInteger1).multiply(localBigInteger2).mod(paramBigInteger2);
  }
  
  private BigInteger generateW(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
  {
    return paramBigInteger4.modInverse(paramBigInteger2);
  }
  
  private BigInteger generateV(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6)
    throws SignatureException
  {
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = this.md.digest();
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new SignatureException(localRuntimeException.getMessage());
    }
    int i = paramBigInteger3.bitLength() / 8;
    if (i < arrayOfByte.length) {
      arrayOfByte = Arrays.copyOfRange(arrayOfByte, 0, i);
    }
    BigInteger localBigInteger1 = new BigInteger(1, arrayOfByte);
    BigInteger localBigInteger2 = localBigInteger1.multiply(paramBigInteger5).mod(paramBigInteger3);
    BigInteger localBigInteger3 = paramBigInteger6.multiply(paramBigInteger5).mod(paramBigInteger3);
    BigInteger localBigInteger4 = paramBigInteger4.modPow(localBigInteger2, paramBigInteger2);
    BigInteger localBigInteger5 = paramBigInteger1.modPow(localBigInteger3, paramBigInteger2);
    BigInteger localBigInteger6 = localBigInteger4.multiply(localBigInteger5);
    BigInteger localBigInteger7 = localBigInteger6.mod(paramBigInteger2);
    return localBigInteger7.mod(paramBigInteger3);
  }
  
  protected BigInteger generateK(BigInteger paramBigInteger)
  {
    SecureRandom localSecureRandom = getSigningRandom();
    byte[] arrayOfByte = new byte[paramBigInteger.bitLength() / 8];
    for (;;)
    {
      localSecureRandom.nextBytes(arrayOfByte);
      BigInteger localBigInteger = new BigInteger(1, arrayOfByte).mod(paramBigInteger);
      if ((localBigInteger.signum() > 0) && (localBigInteger.compareTo(paramBigInteger) < 0)) {
        return localBigInteger;
      }
    }
  }
  
  protected SecureRandom getSigningRandom()
  {
    if (this.signingRandom == null) {
      if (this.appRandom != null) {
        this.signingRandom = this.appRandom;
      } else {
        this.signingRandom = JCAUtil.getSecureRandom();
      }
    }
    return this.signingRandom;
  }
  
  public String toString()
  {
    String str = "DSA Signature";
    if ((this.presetP != null) && (this.presetQ != null) && (this.presetG != null))
    {
      str = str + "\n\tp: " + Debug.toHexString(this.presetP);
      str = str + "\n\tq: " + Debug.toHexString(this.presetQ);
      str = str + "\n\tg: " + Debug.toHexString(this.presetG);
    }
    else
    {
      str = str + "\n\t P, Q or G not initialized.";
    }
    if (this.presetY != null) {
      str = str + "\n\ty: " + Debug.toHexString(this.presetY);
    }
    if ((this.presetY == null) && (this.presetX == null)) {
      str = str + "\n\tUNINIIALIZED";
    }
    return str;
  }
  
  private static void debug(Exception paramException) {}
  
  private static void debug(String paramString) {}
  
  static class LegacyDSA
    extends DSA
  {
    private int[] kSeed;
    private byte[] kSeedAsByteArray;
    private int[] kSeedLast;
    private static final int round1_kt = 1518500249;
    private static final int round2_kt = 1859775393;
    private static final int round3_kt = -1894007588;
    private static final int round4_kt = -899497514;
    
    public LegacyDSA(MessageDigest paramMessageDigest)
      throws NoSuchAlgorithmException
    {
      super();
    }
    
    @Deprecated
    protected void engineSetParameter(String paramString, Object paramObject)
    {
      if (paramString.equals("KSEED"))
      {
        if ((paramObject instanceof byte[]))
        {
          this.kSeed = byteArray2IntArray((byte[])paramObject);
          this.kSeedAsByteArray = ((byte[])paramObject);
        }
        else
        {
          DSA.debug("unrecognized param: " + paramString);
          throw new InvalidParameterException("kSeed not a byte array");
        }
      }
      else {
        throw new InvalidParameterException("Unsupported parameter");
      }
    }
    
    @Deprecated
    protected Object engineGetParameter(String paramString)
    {
      if (paramString.equals("KSEED")) {
        return this.kSeedAsByteArray;
      }
      return null;
    }
    
    protected BigInteger generateK(BigInteger paramBigInteger)
    {
      BigInteger localBigInteger = null;
      if ((this.kSeed != null) && (!Arrays.equals(this.kSeed, this.kSeedLast)))
      {
        localBigInteger = generateKUsingKSeed(this.kSeed, paramBigInteger);
        if ((localBigInteger.signum() > 0) && (localBigInteger.compareTo(paramBigInteger) < 0))
        {
          this.kSeedLast = ((int[])this.kSeed.clone());
          return localBigInteger;
        }
      }
      SecureRandom localSecureRandom = getSigningRandom();
      for (;;)
      {
        int[] arrayOfInt = new int[5];
        for (int i = 0; i < 5; i++) {
          arrayOfInt[i] = localSecureRandom.nextInt();
        }
        localBigInteger = generateKUsingKSeed(arrayOfInt, paramBigInteger);
        if ((localBigInteger.signum() > 0) && (localBigInteger.compareTo(paramBigInteger) < 0))
        {
          this.kSeedLast = arrayOfInt;
          return localBigInteger;
        }
      }
    }
    
    private BigInteger generateKUsingKSeed(int[] paramArrayOfInt, BigInteger paramBigInteger)
    {
      int[] arrayOfInt1 = { -271733879, -1732584194, 271733878, -1009589776, 1732584193 };
      int[] arrayOfInt2 = SHA_7(paramArrayOfInt, arrayOfInt1);
      byte[] arrayOfByte = new byte[arrayOfInt2.length * 4];
      for (int i = 0; i < arrayOfInt2.length; i++)
      {
        int j = arrayOfInt2[i];
        for (int k = 0; k < 4; k++) {
          arrayOfByte[(i * 4 + k)] = ((byte)(j >>> 24 - k * 8));
        }
      }
      BigInteger localBigInteger = new BigInteger(1, arrayOfByte).mod(paramBigInteger);
      return localBigInteger;
    }
    
    static int[] SHA_7(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      int[] arrayOfInt1 = new int[80];
      System.arraycopy(paramArrayOfInt1, 0, arrayOfInt1, 0, paramArrayOfInt1.length);
      int i = 0;
      for (int j = 16; j <= 79; j++)
      {
        i = arrayOfInt1[(j - 3)] ^ arrayOfInt1[(j - 8)] ^ arrayOfInt1[(j - 14)] ^ arrayOfInt1[(j - 16)];
        arrayOfInt1[j] = (i << 1 | i >>> 31);
      }
      j = paramArrayOfInt2[0];
      int k = paramArrayOfInt2[1];
      int m = paramArrayOfInt2[2];
      int n = paramArrayOfInt2[3];
      int i1 = paramArrayOfInt2[4];
      for (int i2 = 0; i2 < 20; i2++)
      {
        i = (j << 5 | j >>> 27) + (k & m | (k ^ 0xFFFFFFFF) & n) + i1 + arrayOfInt1[i2] + 1518500249;
        i1 = n;
        n = m;
        m = k << 30 | k >>> 2;
        k = j;
        j = i;
      }
      for (i2 = 20; i2 < 40; i2++)
      {
        i = (j << 5 | j >>> 27) + (k ^ m ^ n) + i1 + arrayOfInt1[i2] + 1859775393;
        i1 = n;
        n = m;
        m = k << 30 | k >>> 2;
        k = j;
        j = i;
      }
      for (i2 = 40; i2 < 60; i2++)
      {
        i = (j << 5 | j >>> 27) + (k & m | k & n | m & n) + i1 + arrayOfInt1[i2] + -1894007588;
        i1 = n;
        n = m;
        m = k << 30 | k >>> 2;
        k = j;
        j = i;
      }
      for (i2 = 60; i2 < 80; i2++)
      {
        i = (j << 5 | j >>> 27) + (k ^ m ^ n) + i1 + arrayOfInt1[i2] + -899497514;
        i1 = n;
        n = m;
        m = k << 30 | k >>> 2;
        k = j;
        j = i;
      }
      int[] arrayOfInt2 = new int[5];
      paramArrayOfInt2[0] += j;
      paramArrayOfInt2[1] += k;
      paramArrayOfInt2[2] += m;
      paramArrayOfInt2[3] += n;
      paramArrayOfInt2[4] += i1;
      return arrayOfInt2;
    }
    
    private int[] byteArray2IntArray(byte[] paramArrayOfByte)
    {
      int i = 0;
      int j = paramArrayOfByte.length % 4;
      byte[] arrayOfByte;
      switch (j)
      {
      case 3: 
        arrayOfByte = new byte[paramArrayOfByte.length + 1];
        break;
      case 2: 
        arrayOfByte = new byte[paramArrayOfByte.length + 2];
        break;
      case 1: 
        arrayOfByte = new byte[paramArrayOfByte.length + 3];
        break;
      default: 
        arrayOfByte = new byte[paramArrayOfByte.length + 0];
      }
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
      int[] arrayOfInt = new int[arrayOfByte.length / 4];
      for (int k = 0; k < arrayOfByte.length; k += 4)
      {
        arrayOfInt[i] = (arrayOfByte[(k + 3)] & 0xFF);
        arrayOfInt[i] |= arrayOfByte[(k + 2)] << 8 & 0xFF00;
        arrayOfInt[i] |= arrayOfByte[(k + 1)] << 16 & 0xFF0000;
        arrayOfInt[i] |= arrayOfByte[(k + 0)] << 24 & 0xFF000000;
        i++;
      }
      return arrayOfInt;
    }
  }
  
  public static final class RawDSA
    extends DSA.LegacyDSA
  {
    public RawDSA()
      throws NoSuchAlgorithmException
    {
      super();
    }
    
    public static final class NullDigest20
      extends MessageDigest
    {
      private final byte[] digestBuffer = new byte[20];
      private int ofs = 0;
      
      protected NullDigest20()
      {
        super();
      }
      
      protected void engineUpdate(byte paramByte)
      {
        if (this.ofs == this.digestBuffer.length) {
          this.ofs = Integer.MAX_VALUE;
        } else {
          this.digestBuffer[(this.ofs++)] = paramByte;
        }
      }
      
      protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      {
        if (this.ofs + paramInt2 > this.digestBuffer.length)
        {
          this.ofs = Integer.MAX_VALUE;
        }
        else
        {
          System.arraycopy(paramArrayOfByte, paramInt1, this.digestBuffer, this.ofs, paramInt2);
          this.ofs += paramInt2;
        }
      }
      
      protected final void engineUpdate(ByteBuffer paramByteBuffer)
      {
        int i = paramByteBuffer.remaining();
        if (this.ofs + i > this.digestBuffer.length)
        {
          this.ofs = Integer.MAX_VALUE;
        }
        else
        {
          paramByteBuffer.get(this.digestBuffer, this.ofs, i);
          this.ofs += i;
        }
      }
      
      protected byte[] engineDigest()
        throws RuntimeException
      {
        if (this.ofs != this.digestBuffer.length) {
          throw new RuntimeException("Data for RawDSA must be exactly 20 bytes long");
        }
        reset();
        return this.digestBuffer;
      }
      
      protected int engineDigest(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
        throws DigestException
      {
        if (this.ofs != this.digestBuffer.length) {
          throw new DigestException("Data for RawDSA must be exactly 20 bytes long");
        }
        if (paramInt2 < this.digestBuffer.length) {
          throw new DigestException("Output buffer too small; must be at least 20 bytes");
        }
        System.arraycopy(this.digestBuffer, 0, paramArrayOfByte, paramInt1, this.digestBuffer.length);
        reset();
        return this.digestBuffer.length;
      }
      
      protected void engineReset()
      {
        this.ofs = 0;
      }
      
      protected final int engineGetDigestLength()
      {
        return this.digestBuffer.length;
      }
    }
  }
  
  public static final class SHA1withDSA
    extends DSA.LegacyDSA
  {
    public SHA1withDSA()
      throws NoSuchAlgorithmException
    {
      super();
    }
  }
  
  public static final class SHA224withDSA
    extends DSA
  {
    public SHA224withDSA()
      throws NoSuchAlgorithmException
    {
      super();
    }
  }
  
  public static final class SHA256withDSA
    extends DSA
  {
    public SHA256withDSA()
      throws NoSuchAlgorithmException
    {
      super();
    }
  }
}
