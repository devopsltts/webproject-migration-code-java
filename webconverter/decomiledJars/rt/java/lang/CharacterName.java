package java.lang;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.zip.InflaterInputStream;

class CharacterName
{
  private static SoftReference<byte[]> refStrPool;
  private static int[][] lookup;
  
  CharacterName() {}
  
  private static synchronized byte[] initNamePool()
  {
    arrayOfByte1 = null;
    if ((refStrPool != null) && ((arrayOfByte1 = (byte[])refStrPool.get()) != null)) {
      return arrayOfByte1;
    }
    DataInputStream localDataInputStream = null;
    try
    {
      localDataInputStream = new DataInputStream(new InflaterInputStream((InputStream)AccessController.doPrivileged(new PrivilegedAction()
      {
        public InputStream run()
        {
          return getClass().getResourceAsStream("uniName.dat");
        }
      })));
      lookup = new int['ᄀ'][];
      int i = localDataInputStream.readInt();
      int j = localDataInputStream.readInt();
      byte[] arrayOfByte2 = new byte[j];
      localDataInputStream.readFully(arrayOfByte2);
      int k = 0;
      int m = 0;
      int n = 0;
      do
      {
        int i1 = arrayOfByte2[(m++)] & 0xFF;
        if (i1 == 0)
        {
          i1 = arrayOfByte2[(m++)] & 0xFF;
          n = (arrayOfByte2[(m++)] & 0xFF) << 16 | (arrayOfByte2[(m++)] & 0xFF) << 8 | arrayOfByte2[(m++)] & 0xFF;
        }
        else
        {
          n++;
        }
        int i2 = n >> 8;
        if (lookup[i2] == null) {
          lookup[i2] = new int['Ā'];
        }
        lookup[i2][(n & 0xFF)] = (k << 8 | i1);
        k += i1;
      } while (m < j);
      arrayOfByte1 = new byte[i - j];
      localDataInputStream.readFully(arrayOfByte1);
      refStrPool = new SoftReference(arrayOfByte1);
      return arrayOfByte1;
    }
    catch (Exception localException2)
    {
      throw new InternalError(localException2.getMessage(), localException2);
    }
    finally
    {
      try
      {
        if (localDataInputStream != null) {
          localDataInputStream.close();
        }
      }
      catch (Exception localException3) {}
    }
  }
  
  public static String get(int paramInt)
  {
    byte[] arrayOfByte = null;
    if ((refStrPool == null) || ((arrayOfByte = (byte[])refStrPool.get()) == null)) {
      arrayOfByte = initNamePool();
    }
    int i = 0;
    if ((lookup[(paramInt >> 8)] == null) || ((i = lookup[(paramInt >> 8)][(paramInt & 0xFF)]) == 0)) {
      return null;
    }
    String str = new String(arrayOfByte, 0, i >>> 8, i & 0xFF);
    return str;
  }
}
