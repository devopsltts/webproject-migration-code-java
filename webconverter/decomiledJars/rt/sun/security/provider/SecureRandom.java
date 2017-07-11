package sun.security.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandomSpi;

public final class SecureRandom
  extends SecureRandomSpi
  implements Serializable
{
  private static final long serialVersionUID = 3581829991155417889L;
  private static final int DIGEST_SIZE = 20;
  private transient MessageDigest digest;
  private byte[] state;
  private byte[] remainder;
  private int remCount;
  
  public SecureRandom()
  {
    init(null);
  }
  
  private SecureRandom(byte[] paramArrayOfByte)
  {
    init(paramArrayOfByte);
  }
  
  private void init(byte[] paramArrayOfByte)
  {
    try
    {
      this.digest = MessageDigest.getInstance("SHA", "SUN");
    }
    catch (NoSuchProviderException|NoSuchAlgorithmException localNoSuchProviderException)
    {
      try
      {
        this.digest = MessageDigest.getInstance("SHA");
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new InternalError("internal error: SHA-1 not available.", localNoSuchAlgorithmException);
      }
    }
    if (paramArrayOfByte != null) {
      engineSetSeed(paramArrayOfByte);
    }
  }
  
  public byte[] engineGenerateSeed(int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    SeedGenerator.generateSeed(arrayOfByte);
    return arrayOfByte;
  }
  
  public synchronized void engineSetSeed(byte[] paramArrayOfByte)
  {
    if (this.state != null)
    {
      this.digest.update(this.state);
      for (int i = 0; i < this.state.length; i++) {
        this.state[i] = 0;
      }
    }
    this.state = this.digest.digest(paramArrayOfByte);
  }
  
  private static void updateState(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    int i = 1;
    int m = 0;
    for (int n = 0; n < paramArrayOfByte1.length; n++)
    {
      int j = paramArrayOfByte1[n] + paramArrayOfByte2[n] + i;
      int k = (byte)j;
      m |= (paramArrayOfByte1[n] != k ? 1 : 0);
      paramArrayOfByte1[n] = k;
      i = j >> 8;
    }
    if (m == 0)
    {
      int tmp74_73 = 0;
      paramArrayOfByte1[tmp74_73] = ((byte)(paramArrayOfByte1[tmp74_73] + 1));
    }
  }
  
  public synchronized void engineNextBytes(byte[] paramArrayOfByte)
  {
    int i = 0;
    byte[] arrayOfByte1 = this.remainder;
    if (this.state == null)
    {
      byte[] arrayOfByte2 = new byte[20];
      SeederHolder.seeder.engineNextBytes(arrayOfByte2);
      this.state = this.digest.digest(arrayOfByte2);
    }
    int k = this.remCount;
    int j;
    int m;
    if (k > 0)
    {
      j = paramArrayOfByte.length - i < 20 - k ? paramArrayOfByte.length - i : 20 - k;
      for (m = 0; m < j; m++)
      {
        paramArrayOfByte[m] = arrayOfByte1[k];
        arrayOfByte1[(k++)] = 0;
      }
      this.remCount += j;
      i += j;
    }
    while (i < paramArrayOfByte.length)
    {
      this.digest.update(this.state);
      arrayOfByte1 = this.digest.digest();
      updateState(this.state, arrayOfByte1);
      j = paramArrayOfByte.length - i > 20 ? 20 : paramArrayOfByte.length - i;
      for (m = 0; m < j; m++)
      {
        paramArrayOfByte[(i++)] = arrayOfByte1[m];
        arrayOfByte1[m] = 0;
      }
      this.remCount += j;
    }
    this.remainder = arrayOfByte1;
    this.remCount %= 20;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      this.digest = MessageDigest.getInstance("SHA", "SUN");
    }
    catch (NoSuchProviderException|NoSuchAlgorithmException localNoSuchProviderException)
    {
      try
      {
        this.digest = MessageDigest.getInstance("SHA");
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new InternalError("internal error: SHA-1 not available.", localNoSuchAlgorithmException);
      }
    }
  }
  
  private static class SeederHolder
  {
    private static final SecureRandom seeder = new SecureRandom(SeedGenerator.getSystemEntropy(), null);
    
    private SeederHolder() {}
    
    static
    {
      byte[] arrayOfByte = new byte[20];
      SeedGenerator.generateSeed(arrayOfByte);
      seeder.engineSetSeed(arrayOfByte);
    }
  }
}
