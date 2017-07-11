package java.security;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;
import sun.security.util.Debug;

public class KeyFactory
{
  private static final Debug debug = Debug.getInstance("jca", "KeyFactory");
  private final String algorithm;
  private Provider provider;
  private volatile KeyFactorySpi spi;
  private final Object lock = new Object();
  private Iterator<Provider.Service> serviceIterator;
  
  protected KeyFactory(KeyFactorySpi paramKeyFactorySpi, Provider paramProvider, String paramString)
  {
    this.spi = paramKeyFactorySpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
  }
  
  private KeyFactory(String paramString)
    throws NoSuchAlgorithmException
  {
    this.algorithm = paramString;
    List localList = GetInstance.getServices("KeyFactory", paramString);
    this.serviceIterator = localList.iterator();
    if (nextSpi(null) == null) {
      throw new NoSuchAlgorithmException(paramString + " KeyFactory not available");
    }
  }
  
  public static KeyFactory getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    return new KeyFactory(paramString);
  }
  
  public static KeyFactory getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("KeyFactory", KeyFactorySpi.class, paramString1, paramString2);
    return new KeyFactory((KeyFactorySpi)localInstance.impl, localInstance.provider, paramString1);
  }
  
  public static KeyFactory getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("KeyFactory", KeyFactorySpi.class, paramString, paramProvider);
    return new KeyFactory((KeyFactorySpi)localInstance.impl, localInstance.provider, paramString);
  }
  
  public final Provider getProvider()
  {
    synchronized (this.lock)
    {
      this.serviceIterator = null;
      return this.provider;
    }
  }
  
  public final String getAlgorithm()
  {
    return this.algorithm;
  }
  
  private KeyFactorySpi nextSpi(KeyFactorySpi paramKeyFactorySpi)
  {
    synchronized (this.lock)
    {
      if ((paramKeyFactorySpi != null) && (paramKeyFactorySpi != this.spi)) {
        return this.spi;
      }
      if (this.serviceIterator == null) {
        return null;
      }
      while (this.serviceIterator.hasNext())
      {
        Provider.Service localService = (Provider.Service)this.serviceIterator.next();
        try
        {
          Object localObject1 = localService.newInstance(null);
          if ((localObject1 instanceof KeyFactorySpi))
          {
            KeyFactorySpi localKeyFactorySpi = (KeyFactorySpi)localObject1;
            this.provider = localService.getProvider();
            this.spi = localKeyFactorySpi;
            return localKeyFactorySpi;
          }
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}
      }
      this.serviceIterator = null;
      return null;
    }
  }
  
  public final PublicKey generatePublic(KeySpec paramKeySpec)
    throws InvalidKeySpecException
  {
    if (this.serviceIterator == null) {
      return this.spi.engineGeneratePublic(paramKeySpec);
    }
    Object localObject = null;
    KeyFactorySpi localKeyFactorySpi = this.spi;
    do
    {
      try
      {
        return localKeyFactorySpi.engineGeneratePublic(paramKeySpec);
      }
      catch (Exception localException)
      {
        if (localObject == null) {
          localObject = localException;
        }
        localKeyFactorySpi = nextSpi(localKeyFactorySpi);
      }
    } while (localKeyFactorySpi != null);
    if ((localObject instanceof RuntimeException)) {
      throw ((RuntimeException)localObject);
    }
    if ((localObject instanceof InvalidKeySpecException)) {
      throw ((InvalidKeySpecException)localObject);
    }
    throw new InvalidKeySpecException("Could not generate public key", localObject);
  }
  
  public final PrivateKey generatePrivate(KeySpec paramKeySpec)
    throws InvalidKeySpecException
  {
    if (this.serviceIterator == null) {
      return this.spi.engineGeneratePrivate(paramKeySpec);
    }
    Object localObject = null;
    KeyFactorySpi localKeyFactorySpi = this.spi;
    do
    {
      try
      {
        return localKeyFactorySpi.engineGeneratePrivate(paramKeySpec);
      }
      catch (Exception localException)
      {
        if (localObject == null) {
          localObject = localException;
        }
        localKeyFactorySpi = nextSpi(localKeyFactorySpi);
      }
    } while (localKeyFactorySpi != null);
    if ((localObject instanceof RuntimeException)) {
      throw ((RuntimeException)localObject);
    }
    if ((localObject instanceof InvalidKeySpecException)) {
      throw ((InvalidKeySpecException)localObject);
    }
    throw new InvalidKeySpecException("Could not generate private key", localObject);
  }
  
  public final <T extends KeySpec> T getKeySpec(Key paramKey, Class<T> paramClass)
    throws InvalidKeySpecException
  {
    if (this.serviceIterator == null) {
      return this.spi.engineGetKeySpec(paramKey, paramClass);
    }
    Object localObject = null;
    KeyFactorySpi localKeyFactorySpi = this.spi;
    do
    {
      try
      {
        return localKeyFactorySpi.engineGetKeySpec(paramKey, paramClass);
      }
      catch (Exception localException)
      {
        if (localObject == null) {
          localObject = localException;
        }
        localKeyFactorySpi = nextSpi(localKeyFactorySpi);
      }
    } while (localKeyFactorySpi != null);
    if ((localObject instanceof RuntimeException)) {
      throw ((RuntimeException)localObject);
    }
    if ((localObject instanceof InvalidKeySpecException)) {
      throw ((InvalidKeySpecException)localObject);
    }
    throw new InvalidKeySpecException("Could not get key spec", localObject);
  }
  
  public final Key translateKey(Key paramKey)
    throws InvalidKeyException
  {
    if (this.serviceIterator == null) {
      return this.spi.engineTranslateKey(paramKey);
    }
    Object localObject = null;
    KeyFactorySpi localKeyFactorySpi = this.spi;
    do
    {
      try
      {
        return localKeyFactorySpi.engineTranslateKey(paramKey);
      }
      catch (Exception localException)
      {
        if (localObject == null) {
          localObject = localException;
        }
        localKeyFactorySpi = nextSpi(localKeyFactorySpi);
      }
    } while (localKeyFactorySpi != null);
    if ((localObject instanceof RuntimeException)) {
      throw ((RuntimeException)localObject);
    }
    if ((localObject instanceof InvalidKeyException)) {
      throw ((InvalidKeyException)localObject);
    }
    throw new InvalidKeyException("Could not translate key", localObject);
  }
}
