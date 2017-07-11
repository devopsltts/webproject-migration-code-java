package java.security;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;
import sun.security.jca.ProviderList;
import sun.security.jca.Providers;
import sun.security.util.Debug;

public class SecureRandom
  extends Random
{
  private static final Debug pdebug = Debug.getInstance("provider", "Provider");
  private static final boolean skipDebug = (Debug.isOn("engine=")) && (!Debug.isOn("securerandom"));
  private Provider provider = null;
  private SecureRandomSpi secureRandomSpi = null;
  private String algorithm;
  private static volatile SecureRandom seedGenerator = null;
  static final long serialVersionUID = 4940670005562187L;
  private byte[] state;
  private MessageDigest digest = null;
  private byte[] randomBytes;
  private int randomBytesUsed;
  private long counter;
  
  public SecureRandom()
  {
    super(0L);
    getDefaultPRNG(false, null);
  }
  
  public SecureRandom(byte[] paramArrayOfByte)
  {
    super(0L);
    getDefaultPRNG(true, paramArrayOfByte);
  }
  
  private void getDefaultPRNG(boolean paramBoolean, byte[] paramArrayOfByte)
  {
    String str = getPrngAlgorithm();
    if (str == null)
    {
      str = "SHA1PRNG";
      this.secureRandomSpi = new sun.security.provider.SecureRandom();
      this.provider = Providers.getSunProvider();
      if (paramBoolean) {
        this.secureRandomSpi.engineSetSeed(paramArrayOfByte);
      }
    }
    else
    {
      try
      {
        SecureRandom localSecureRandom = getInstance(str);
        this.secureRandomSpi = localSecureRandom.getSecureRandomSpi();
        this.provider = localSecureRandom.getProvider();
        if (paramBoolean) {
          this.secureRandomSpi.engineSetSeed(paramArrayOfByte);
        }
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new RuntimeException(localNoSuchAlgorithmException);
      }
    }
    if (getClass() == SecureRandom.class) {
      this.algorithm = str;
    }
  }
  
  protected SecureRandom(SecureRandomSpi paramSecureRandomSpi, Provider paramProvider)
  {
    this(paramSecureRandomSpi, paramProvider, null);
  }
  
  private SecureRandom(SecureRandomSpi paramSecureRandomSpi, Provider paramProvider, String paramString)
  {
    super(0L);
    this.secureRandomSpi = paramSecureRandomSpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
    if ((!skipDebug) && (pdebug != null)) {
      pdebug.println("SecureRandom." + paramString + " algorithm from: " + this.provider.getName());
    }
  }
  
  public static SecureRandom getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, paramString);
    return new SecureRandom((SecureRandomSpi)localInstance.impl, localInstance.provider, paramString);
  }
  
  public static SecureRandom getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, paramString1, paramString2);
    return new SecureRandom((SecureRandomSpi)localInstance.impl, localInstance.provider, paramString1);
  }
  
  public static SecureRandom getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, paramString, paramProvider);
    return new SecureRandom((SecureRandomSpi)localInstance.impl, localInstance.provider, paramString);
  }
  
  SecureRandomSpi getSecureRandomSpi()
  {
    return this.secureRandomSpi;
  }
  
  public final Provider getProvider()
  {
    return this.provider;
  }
  
  public String getAlgorithm()
  {
    return this.algorithm != null ? this.algorithm : "unknown";
  }
  
  public synchronized void setSeed(byte[] paramArrayOfByte)
  {
    this.secureRandomSpi.engineSetSeed(paramArrayOfByte);
  }
  
  public void setSeed(long paramLong)
  {
    if (paramLong != 0L) {
      this.secureRandomSpi.engineSetSeed(longToByteArray(paramLong));
    }
  }
  
  public synchronized void nextBytes(byte[] paramArrayOfByte)
  {
    this.secureRandomSpi.engineNextBytes(paramArrayOfByte);
  }
  
  protected final int next(int paramInt)
  {
    int i = (paramInt + 7) / 8;
    byte[] arrayOfByte = new byte[i];
    int j = 0;
    nextBytes(arrayOfByte);
    for (int k = 0; k < i; k++) {
      j = (j << 8) + (arrayOfByte[k] & 0xFF);
    }
    return j >>> i * 8 - paramInt;
  }
  
  public static byte[] getSeed(int paramInt)
  {
    if (seedGenerator == null) {
      seedGenerator = new SecureRandom();
    }
    return seedGenerator.generateSeed(paramInt);
  }
  
  public byte[] generateSeed(int paramInt)
  {
    return this.secureRandomSpi.engineGenerateSeed(paramInt);
  }
  
  private static byte[] longToByteArray(long paramLong)
  {
    byte[] arrayOfByte = new byte[8];
    for (int i = 0; i < 8; i++)
    {
      arrayOfByte[i] = ((byte)(int)paramLong);
      paramLong >>= 8;
    }
    return arrayOfByte;
  }
  
  private static String getPrngAlgorithm()
  {
    Iterator localIterator1 = Providers.getProviderList().providers().iterator();
    while (localIterator1.hasNext())
    {
      Provider localProvider = (Provider)localIterator1.next();
      Iterator localIterator2 = localProvider.getServices().iterator();
      while (localIterator2.hasNext())
      {
        Provider.Service localService = (Provider.Service)localIterator2.next();
        if (localService.getType().equals("SecureRandom")) {
          return localService.getAlgorithm();
        }
      }
    }
    return null;
  }
  
  public static SecureRandom getInstanceStrong()
    throws NoSuchAlgorithmException
  {
    String str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Security.getProperty("securerandom.strongAlgorithms");
      }
    });
    if ((str1 == null) || (str1.length() == 0)) {
      throw new NoSuchAlgorithmException("Null/empty securerandom.strongAlgorithms Security Property");
    }
    for (String str2 = str1; str2 != null; str2 = null)
    {
      Matcher localMatcher;
      if ((localMatcher = StrongPatternHolder.pattern.matcher(str2)).matches())
      {
        String str3 = localMatcher.group(1);
        String str4 = localMatcher.group(3);
        try
        {
          if (str4 == null) {
            return getInstance(str3);
          }
          return getInstance(str3, str4);
        }
        catch (NoSuchAlgorithmException|NoSuchProviderException localNoSuchAlgorithmException)
        {
          str2 = localMatcher.group(5);
        }
      }
    }
    throw new NoSuchAlgorithmException("No strong SecureRandom impls available: " + str1);
  }
  
  private static final class StrongPatternHolder
  {
    private static Pattern pattern = Pattern.compile("\\s*([\\S&&[^:,]]*)(\\:([\\S&&[^,]]*))?\\s*(\\,(.*))?");
    
    private StrongPatternHolder() {}
  }
}
