package com.sun.xml.internal.ws.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ASCIIUtility
{
  private ASCIIUtility() {}
  
  public static int parseInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws NumberFormatException
  {
    if (paramArrayOfByte == null) {
      throw new NumberFormatException("null");
    }
    int i = 0;
    int j = 0;
    int k = paramInt1;
    if (paramInt2 > paramInt1)
    {
      int m;
      if (paramArrayOfByte[k] == 45)
      {
        j = 1;
        m = Integer.MIN_VALUE;
        k++;
      }
      else
      {
        m = -2147483647;
      }
      int n = m / paramInt3;
      int i1;
      if (k < paramInt2)
      {
        i1 = Character.digit((char)paramArrayOfByte[(k++)], paramInt3);
        if (i1 < 0) {
          throw new NumberFormatException("illegal number: " + toString(paramArrayOfByte, paramInt1, paramInt2));
        }
        i = -i1;
      }
      while (k < paramInt2)
      {
        i1 = Character.digit((char)paramArrayOfByte[(k++)], paramInt3);
        if (i1 < 0) {
          throw new NumberFormatException("illegal number");
        }
        if (i < n) {
          throw new NumberFormatException("illegal number");
        }
        i *= paramInt3;
        if (i < m + i1) {
          throw new NumberFormatException("illegal number");
        }
        i -= i1;
      }
    }
    throw new NumberFormatException("illegal number");
    if (j != 0)
    {
      if (k > paramInt1 + 1) {
        return i;
      }
      throw new NumberFormatException("illegal number");
    }
    return -i;
  }
  
  public static String toString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramInt2 - paramInt1;
    char[] arrayOfChar = new char[i];
    int j = 0;
    int k = paramInt1;
    while (j < i) {
      arrayOfChar[(j++)] = ((char)(paramArrayOfByte[(k++)] & 0xFF));
    }
    return new String(arrayOfChar);
  }
  
  public static void copyStream(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    int i = 1024;
    byte[] arrayOfByte = new byte[i];
    int j;
    while ((j = paramInputStream.read(arrayOfByte, 0, i)) != -1) {
      paramOutputStream.write(arrayOfByte, 0, j);
    }
  }
}
