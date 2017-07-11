package sun.security.provider;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.DSAParams;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import sun.security.jca.JCAUtil;

public class DSAKeyPairGenerator
  extends KeyPairGenerator
  implements java.security.interfaces.DSAKeyPairGenerator
{
  private int plen;
  private int qlen;
  private boolean forceNewParameters;
  private DSAParameterSpec params;
  private SecureRandom random;
  
  public DSAKeyPairGenerator()
  {
    super("DSA");
    initialize(1024, null);
  }
  
  private static void checkStrength(int paramInt1, int paramInt2)
  {
    if (((paramInt1 < 512) || (paramInt1 > 1024) || (paramInt1 % 64 != 0) || (paramInt2 != 160)) && ((paramInt1 != 2048) || ((paramInt2 != 224) && (paramInt2 != 256)))) {
      throw new InvalidParameterException("Unsupported prime and subprime size combination: " + paramInt1 + ", " + paramInt2);
    }
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom)
  {
    initialize(paramInt, true, paramSecureRandom);
    this.forceNewParameters = false;
  }
  
  public void initialize(int paramInt, boolean paramBoolean, SecureRandom paramSecureRandom)
  {
    int i = -1;
    if (paramInt <= 1024) {
      i = 160;
    } else if (paramInt == 2048) {
      i = 224;
    }
    checkStrength(paramInt, i);
    if (paramBoolean)
    {
      this.params = null;
    }
    else
    {
      this.params = ParameterCache.getCachedDSAParameterSpec(paramInt, i);
      if (this.params == null) {
        throw new InvalidParameterException("No precomputed parameters for requested modulus size available");
      }
    }
    this.plen = paramInt;
    this.qlen = i;
    this.random = paramSecureRandom;
    this.forceNewParameters = paramBoolean;
  }
  
  public void initialize(DSAParams paramDSAParams, SecureRandom paramSecureRandom)
  {
    if (paramDSAParams == null) {
      throw new InvalidParameterException("Params must not be null");
    }
    DSAParameterSpec localDSAParameterSpec = new DSAParameterSpec(paramDSAParams.getP(), paramDSAParams.getQ(), paramDSAParams.getG());
    initialize0(localDSAParameterSpec, paramSecureRandom);
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidAlgorithmParameterException
  {
    if (!(paramAlgorithmParameterSpec instanceof DSAParameterSpec)) {
      throw new InvalidAlgorithmParameterException("Inappropriate parameter");
    }
    initialize0((DSAParameterSpec)paramAlgorithmParameterSpec, paramSecureRandom);
  }
  
  private void initialize0(DSAParameterSpec paramDSAParameterSpec, SecureRandom paramSecureRandom)
  {
    int i = paramDSAParameterSpec.getP().bitLength();
    int j = paramDSAParameterSpec.getQ().bitLength();
    checkStrength(i, j);
    this.plen = i;
    this.qlen = j;
    this.params = paramDSAParameterSpec;
    this.random = paramSecureRandom;
    this.forceNewParameters = false;
  }
  
  public KeyPair generateKeyPair()
  {
    if (this.random == null) {
      this.random = JCAUtil.getSecureRandom();
    }
    DSAParameterSpec localDSAParameterSpec;
    try
    {
      if (this.forceNewParameters)
      {
        localDSAParameterSpec = ParameterCache.getNewDSAParameterSpec(this.plen, this.qlen, this.random);
      }
      else
      {
        if (this.params == null) {
          this.params = ParameterCache.getDSAParameterSpec(this.plen, this.qlen, this.random);
        }
        localDSAParameterSpec = this.params;
      }
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      throw new ProviderException(localGeneralSecurityException);
    }
    return generateKeyPair(localDSAParameterSpec.getP(), localDSAParameterSpec.getQ(), localDSAParameterSpec.getG(), this.random);
  }
  
  public KeyPair generateKeyPair(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, SecureRandom paramSecureRandom)
  {
    BigInteger localBigInteger1 = generateX(paramSecureRandom, paramBigInteger2);
    BigInteger localBigInteger2 = generateY(localBigInteger1, paramBigInteger1, paramBigInteger3);
    try
    {
      Object localObject;
      if (DSAKeyFactory.SERIAL_INTEROP) {
        localObject = new DSAPublicKey(localBigInteger2, paramBigInteger1, paramBigInteger2, paramBigInteger3);
      } else {
        localObject = new DSAPublicKeyImpl(localBigInteger2, paramBigInteger1, paramBigInteger2, paramBigInteger3);
      }
      DSAPrivateKey localDSAPrivateKey = new DSAPrivateKey(localBigInteger1, paramBigInteger1, paramBigInteger2, paramBigInteger3);
      KeyPair localKeyPair = new KeyPair((PublicKey)localObject, localDSAPrivateKey);
      return localKeyPair;
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new ProviderException(localInvalidKeyException);
    }
  }
  
  private BigInteger generateX(SecureRandom paramSecureRandom, BigInteger paramBigInteger)
  {
    BigInteger localBigInteger = null;
    byte[] arrayOfByte = new byte[this.qlen];
    do
    {
      paramSecureRandom.nextBytes(arrayOfByte);
      localBigInteger = new BigInteger(1, arrayOfByte).mod(paramBigInteger);
    } while ((localBigInteger.signum() <= 0) || (localBigInteger.compareTo(paramBigInteger) >= 0));
    return localBigInteger;
  }
  
  BigInteger generateY(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3)
  {
    BigInteger localBigInteger = paramBigInteger3.modPow(paramBigInteger1, paramBigInteger2);
    return localBigInteger;
  }
}
