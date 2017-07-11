package sun.security.util;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import sun.misc.JavaIOAccess;
import sun.misc.SharedSecrets;

public class Password
{
  private static volatile CharsetEncoder enc;
  
  public Password() {}
  
  public static char[] readPassword(InputStream paramInputStream)
    throws IOException
  {
    return readPassword(paramInputStream, false);
  }
  
  public static char[] readPassword(InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    char[] arrayOfChar1 = null;
    byte[] arrayOfByte = null;
    try
    {
      Console localConsole = null;
      Object localObject1;
      if ((!paramBoolean) && (paramInputStream == System.in) && ((localConsole = System.console()) != null))
      {
        arrayOfChar1 = localConsole.readPassword();
        if ((arrayOfChar1 != null) && (arrayOfChar1.length == 0))
        {
          localObject1 = null;
          return localObject1;
        }
        arrayOfByte = convertToBytes(arrayOfChar1);
        paramInputStream = new ByteArrayInputStream(arrayOfByte);
      }
      char[] arrayOfChar2 = localObject1 = new char[''];
      int i = arrayOfChar2.length;
      int j = 0;
      int m = 0;
      while (m == 0)
      {
        int k;
        switch (k = paramInputStream.read())
        {
        case -1: 
        case 10: 
          m = 1;
          break;
        case 13: 
          int n = paramInputStream.read();
          if ((n != 10) && (n != -1))
          {
            if (!(paramInputStream instanceof PushbackInputStream)) {
              paramInputStream = new PushbackInputStream(paramInputStream);
            }
            ((PushbackInputStream)paramInputStream).unread(n);
          }
          else
          {
            m = 1;
          }
          break;
        default: 
          i--;
          if (i < 0)
          {
            arrayOfChar2 = new char[j + 128];
            i = arrayOfChar2.length - j - 1;
            System.arraycopy(localObject1, 0, arrayOfChar2, 0, j);
            Arrays.fill((char[])localObject1, ' ');
            localObject1 = arrayOfChar2;
          }
          arrayOfChar2[(j++)] = ((char)k);
        }
      }
      if (j == 0)
      {
        arrayOfChar3 = null;
        return arrayOfChar3;
      }
      char[] arrayOfChar3 = new char[j];
      System.arraycopy(arrayOfChar2, 0, arrayOfChar3, 0, j);
      Arrays.fill(arrayOfChar2, ' ');
      char[] arrayOfChar4 = arrayOfChar3;
      return arrayOfChar4;
    }
    finally
    {
      if (arrayOfChar1 != null) {
        Arrays.fill(arrayOfChar1, ' ');
      }
      if (arrayOfByte != null) {
        Arrays.fill(arrayOfByte, (byte)0);
      }
    }
  }
  
  private static byte[] convertToBytes(char[] paramArrayOfChar)
  {
    if (enc == null) {
      synchronized (Password.class)
      {
        enc = SharedSecrets.getJavaIOAccess().charset().newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      }
    }
    ??? = new byte[(int)(enc.maxBytesPerChar() * paramArrayOfChar.length)];
    ByteBuffer localByteBuffer = ByteBuffer.wrap((byte[])???);
    synchronized (enc)
    {
      enc.reset().encode(CharBuffer.wrap(paramArrayOfChar), localByteBuffer, true);
    }
    if (localByteBuffer.position() < ???.length) {
      ???[localByteBuffer.position()] = 10;
    }
    return ???;
  }
}
