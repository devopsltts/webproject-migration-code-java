package sun.security.provider;

import java.math.BigInteger;
import java.security.AlgorithmParameterGeneratorSpi;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAGenParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;

public class DSAParameterGenerator
  extends AlgorithmParameterGeneratorSpi
{
  private static final DSAGenParameterSpec DEFAULTS = new DSAGenParameterSpec(1024, 160, 160);
  private int valueL = -1;
  private int valueN = -1;
  private int seedLen = -1;
  private SecureRandom random;
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  public DSAParameterGenerator() {}
  
  protected void engineInit(int paramInt, SecureRandom paramSecureRandom)
  {
    if ((paramInt >= 512) && (paramInt <= 1024) && (paramInt % 64 == 0)) {
      this.valueN = 160;
    } else if (paramInt == 2048) {
      this.valueN = 224;
    } else {
      throw new InvalidParameterException("Prime size should be 512 - 1024, or 2048");
    }
    this.valueL = paramInt;
    this.seedLen = this.valueN;
    this.random = paramSecureRandom;
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidAlgorithmParameterException
  {
    if (!(paramAlgorithmParameterSpec instanceof DSAGenParameterSpec)) {
      throw new InvalidAlgorithmParameterException("Invalid parameter");
    }
    DSAGenParameterSpec localDSAGenParameterSpec = (DSAGenParameterSpec)paramAlgorithmParameterSpec;
    int i = localDSAGenParameterSpec.getPrimePLength();
    if (i > 2048) {
      throw new InvalidParameterException("No support for prime size " + i);
    }
    this.valueL = i;
    this.valueN = localDSAGenParameterSpec.getSubprimeQLength();
    this.seedLen = localDSAGenParameterSpec.getSeedLength();
    this.random = paramSecureRandom;
  }
  
  protected AlgorithmParameters engineGenerateParameters()
  {
    AlgorithmParameters localAlgorithmParameters = null;
    try
    {
      if (this.random == null) {
        this.random = new SecureRandom();
      }
      if (this.valueL == -1) {
        try
        {
          engineInit(DEFAULTS, this.random);
        }
        catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException) {}
      }
      BigInteger[] arrayOfBigInteger = generatePandQ(this.random, this.valueL, this.valueN, this.seedLen);
      BigInteger localBigInteger1 = arrayOfBigInteger[0];
      BigInteger localBigInteger2 = arrayOfBigInteger[1];
      BigInteger localBigInteger3 = generateG(localBigInteger1, localBigInteger2);
      DSAParameterSpec localDSAParameterSpec = new DSAParameterSpec(localBigInteger1, localBigInteger2, localBigInteger3);
      localAlgorithmParameters = AlgorithmParameters.getInstance("DSA", "SUN");
      localAlgorithmParameters.init(localDSAParameterSpec);
    }
    catch (InvalidParameterSpecException localInvalidParameterSpecException)
    {
      throw new RuntimeException(localInvalidParameterSpecException.getMessage());
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new RuntimeException(localNoSuchAlgorithmException.getMessage());
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      throw new RuntimeException(localNoSuchProviderException.getMessage());
    }
    return localAlgorithmParameters;
  }
  
  private static BigInteger[] generatePandQ(SecureRandom paramSecureRandom, int paramInt1, int paramInt2, int paramInt3)
  {
    String str = null;
    if (paramInt2 == 160) {
      str = "SHA";
    } else if (paramInt2 == 224) {
      str = "SHA-224";
    } else if (paramInt2 == 256) {
      str = "SHA-256";
    }
    MessageDigest localMessageDigest = null;
    try
    {
      localMessageDigest = MessageDigest.getInstance(str);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      localNoSuchAlgorithmException.printStackTrace();
    }
    int i = localMessageDigest.getDigestLength() * 8;
    int j = (paramInt1 - 1) / i;
    int k = (paramInt1 - 1) % i;
    byte[] arrayOfByte = new byte[paramInt3 / 8];
    BigInteger localBigInteger1 = TWO.pow(paramInt3);
    int m = 80;
    if (paramInt1 == 2048) {
      m = 112;
    }
    BigInteger localBigInteger4 = null;
    for (;;)
    {
      paramSecureRandom.nextBytes(arrayOfByte);
      localBigInteger4 = new BigInteger(1, arrayOfByte);
      BigInteger localBigInteger5 = new BigInteger(1, localMessageDigest.digest(arrayOfByte)).mod(TWO.pow(paramInt2 - 1));
      BigInteger localBigInteger3 = TWO.pow(paramInt2 - 1).add(localBigInteger5).add(ONE).subtract(localBigInteger5.mod(TWO));
      if (localBigInteger3.isProbablePrime(m))
      {
        localBigInteger5 = ONE;
        for (int n = 0; n < 4 * paramInt1; n++)
        {
          BigInteger[] arrayOfBigInteger1 = new BigInteger[j + 1];
          for (int i1 = 0; i1 <= j; i1++)
          {
            BigInteger localBigInteger7 = BigInteger.valueOf(i1);
            localBigInteger9 = localBigInteger4.add(localBigInteger5).add(localBigInteger7).mod(localBigInteger1);
            localObject = localMessageDigest.digest(toByteArray(localBigInteger9));
            arrayOfBigInteger1[i1] = new BigInteger(1, (byte[])localObject);
          }
          BigInteger localBigInteger6 = arrayOfBigInteger1[0];
          for (int i2 = 1; i2 < j; i2++) {
            localBigInteger6 = localBigInteger6.add(arrayOfBigInteger1[i2].multiply(TWO.pow(i2 * i)));
          }
          localBigInteger6 = localBigInteger6.add(arrayOfBigInteger1[j].mod(TWO.pow(k)).multiply(TWO.pow(j * i)));
          BigInteger localBigInteger8 = TWO.pow(paramInt1 - 1);
          BigInteger localBigInteger9 = localBigInteger6.add(localBigInteger8);
          Object localObject = localBigInteger9.mod(localBigInteger3.multiply(TWO));
          BigInteger localBigInteger2 = localBigInteger9.subtract(((BigInteger)localObject).subtract(ONE));
          if ((localBigInteger2.compareTo(localBigInteger8) > -1) && (localBigInteger2.isProbablePrime(m)))
          {
            BigInteger[] arrayOfBigInteger2 = { localBigInteger2, localBigInteger3, localBigInteger4, BigInteger.valueOf(n) };
            return arrayOfBigInteger2;
          }
          localBigInteger5 = localBigInteger5.add(BigInteger.valueOf(j)).add(ONE);
        }
      }
    }
  }
  
  private static BigInteger generateG(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    BigInteger localBigInteger1 = ONE;
    BigInteger localBigInteger2 = paramBigInteger1.subtract(ONE).divide(paramBigInteger2);
    BigInteger localBigInteger3 = ONE;
    while (localBigInteger3.compareTo(TWO) < 0)
    {
      localBigInteger3 = localBigInteger1.modPow(localBigInteger2, paramBigInteger1);
      localBigInteger1 = localBigInteger1.add(ONE);
    }
    return localBigInteger3;
  }
  
  private static byte[] toByteArray(BigInteger paramBigInteger)
  {
    Object localObject = paramBigInteger.toByteArray();
    if (localObject[0] == 0)
    {
      byte[] arrayOfByte = new byte[localObject.length - 1];
      System.arraycopy(localObject, 1, arrayOfByte, 0, arrayOfByte.length);
      localObject = arrayOfByte;
    }
    return localObject;
  }
}
