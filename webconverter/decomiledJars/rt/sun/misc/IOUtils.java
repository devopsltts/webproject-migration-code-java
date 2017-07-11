package sun.misc;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class IOUtils
{
  public IOUtils() {}
  
  public static byte[] readFully(InputStream paramInputStream, int paramInt, boolean paramBoolean)
    throws IOException
  {
    byte[] arrayOfByte = new byte[0];
    if (paramInt == -1) {
      paramInt = Integer.MAX_VALUE;
    }
    int i = 0;
    while (i < paramInt)
    {
      int j;
      if (i >= arrayOfByte.length)
      {
        j = Math.min(paramInt - i, arrayOfByte.length + 1024);
        if (arrayOfByte.length < i + j) {
          arrayOfByte = Arrays.copyOf(arrayOfByte, i + j);
        }
      }
      else
      {
        j = arrayOfByte.length - i;
      }
      int k = paramInputStream.read(arrayOfByte, i, j);
      if (k < 0)
      {
        if ((paramBoolean) && (paramInt != Integer.MAX_VALUE)) {
          throw new EOFException("Detect premature EOF");
        }
        if (arrayOfByte.length == i) {
          break;
        }
        arrayOfByte = Arrays.copyOf(arrayOfByte, i);
        break;
      }
      i += k;
    }
    return arrayOfByte;
  }
}
