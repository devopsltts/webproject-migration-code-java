package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class BooleanEncodingAlgorithm
  extends BuiltInEncodingAlgorithm
{
  private static final int[] BIT_TABLE = { 128, 64, 32, 16, 8, 4, 2, 1 };
  
  public BooleanEncodingAlgorithm() {}
  
  public int getPrimtiveLengthFromOctetLength(int paramInt)
    throws EncodingAlgorithmException
  {
    throw new UnsupportedOperationException();
  }
  
  public int getOctetLengthFromPrimitiveLength(int paramInt)
  {
    if (paramInt < 5) {
      return 1;
    }
    int i = paramInt / 8;
    return i == 0 ? 2 : 1 + i;
  }
  
  public final Object decodeFromBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws EncodingAlgorithmException
  {
    int i = getPrimtiveLengthFromOctetLength(paramInt2, paramArrayOfByte[paramInt1]);
    boolean[] arrayOfBoolean = new boolean[i];
    decodeFromBytesToBooleanArray(arrayOfBoolean, 0, i, paramArrayOfByte, paramInt1, paramInt2);
    return arrayOfBoolean;
  }
  
  public final Object decodeFromInputStream(InputStream paramInputStream)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramInputStream.read();
    if (i == -1) {
      throw new EOFException();
    }
    int j = i >> 4 & 0xFF;
    int k = 4;
    int m = 8;
    int n = 0;
    do
    {
      n = paramInputStream.read();
      if (n == -1) {
        m -= j;
      }
      while (k < m) {
        localArrayList.add(Boolean.valueOf((i & BIT_TABLE[(k++)]) > 0));
      }
      i = n;
    } while (i != -1);
    return generateArrayFromList(localArrayList);
  }
  
  public void encodeToOutputStream(Object paramObject, OutputStream paramOutputStream)
    throws IOException
  {
    if (!(paramObject instanceof boolean[])) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotBoolean"));
    }
    boolean[] arrayOfBoolean = (boolean[])paramObject;
    int i = arrayOfBoolean.length;
    int j = (i + 4) % 8;
    int k = j == 0 ? 0 : 8 - j;
    int m = 4;
    int n = k << 4;
    int i1 = 0;
    while (i1 < i)
    {
      if (arrayOfBoolean[(i1++)] != 0) {
        n |= BIT_TABLE[m];
      }
      m++;
      if (m == 8)
      {
        paramOutputStream.write(n);
        m = n = 0;
      }
    }
    if (m != 8) {
      paramOutputStream.write(n);
    }
  }
  
  public final Object convertFromCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return new boolean[0];
    }
    final CharBuffer localCharBuffer = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    final ArrayList localArrayList = new ArrayList();
    matchWhiteSpaceDelimnatedWords(localCharBuffer, new BuiltInEncodingAlgorithm.WordListener()
    {
      public void word(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (localCharBuffer.charAt(paramAnonymousInt1) == 't') {
          localArrayList.add(Boolean.TRUE);
        } else {
          localArrayList.add(Boolean.FALSE);
        }
      }
    });
    return generateArrayFromList(localArrayList);
  }
  
  public final void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer)
  {
    if (paramObject == null) {
      return;
    }
    boolean[] arrayOfBoolean = (boolean[])paramObject;
    if (arrayOfBoolean.length == 0) {
      return;
    }
    paramStringBuffer.ensureCapacity(arrayOfBoolean.length * 5);
    int i = arrayOfBoolean.length - 1;
    for (int j = 0; j <= i; j++)
    {
      if (arrayOfBoolean[j] != 0) {
        paramStringBuffer.append("true");
      } else {
        paramStringBuffer.append("false");
      }
      if (j != i) {
        paramStringBuffer.append(' ');
      }
    }
  }
  
  public int getPrimtiveLengthFromOctetLength(int paramInt1, int paramInt2)
    throws EncodingAlgorithmException
  {
    int i = paramInt2 >> 4 & 0xFF;
    if (paramInt1 == 1)
    {
      if (i > 3) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.unusedBits4"));
      }
      return 4 - i;
    }
    if (i > 7) {
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.unusedBits8"));
    }
    return paramInt1 * 8 - 4 - i;
  }
  
  public final void decodeFromBytesToBooleanArray(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3, int paramInt4)
  {
    int i = paramArrayOfByte[(paramInt3++)] & 0xFF;
    int j = 4;
    int k = paramInt1 + paramInt2;
    while (paramInt1 < k)
    {
      if (j == 8)
      {
        i = paramArrayOfByte[(paramInt3++)] & 0xFF;
        j = 0;
      }
      paramArrayOfBoolean[(paramInt1++)] = ((i & BIT_TABLE[(j++)]) > 0 ? 1 : false);
    }
  }
  
  public void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
  {
    if (!(paramObject instanceof boolean[])) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotBoolean"));
    }
    encodeToBytesFromBooleanArray((boolean[])paramObject, paramInt1, paramInt2, paramArrayOfByte, paramInt3);
  }
  
  public void encodeToBytesFromBooleanArray(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
  {
    int i = (paramInt2 + 4) % 8;
    int j = i == 0 ? 0 : 8 - i;
    int k = 4;
    int m = j << 4;
    int n = paramInt1 + paramInt2;
    while (paramInt1 < n)
    {
      if (paramArrayOfBoolean[(paramInt1++)] != 0) {
        m |= BIT_TABLE[k];
      }
      k++;
      if (k == 8)
      {
        paramArrayOfByte[(paramInt3++)] = ((byte)m);
        k = m = 0;
      }
    }
    if (k > 0) {
      paramArrayOfByte[paramInt3] = ((byte)m);
    }
  }
  
  private boolean[] generateArrayFromList(List paramList)
  {
    boolean[] arrayOfBoolean = new boolean[paramList.size()];
    for (int i = 0; i < arrayOfBoolean.length; i++) {
      arrayOfBoolean[i] = ((Boolean)paramList.get(i)).booleanValue();
    }
    return arrayOfBoolean;
  }
}
