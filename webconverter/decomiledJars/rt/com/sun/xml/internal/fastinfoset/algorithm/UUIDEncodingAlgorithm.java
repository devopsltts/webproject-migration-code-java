package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class UUIDEncodingAlgorithm
  extends LongEncodingAlgorithm
{
  private long _msb;
  private long _lsb;
  
  public UUIDEncodingAlgorithm() {}
  
  public final int getPrimtiveLengthFromOctetLength(int paramInt)
    throws EncodingAlgorithmException
  {
    if (paramInt % 16 != 0) {
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfUUID", new Object[] { Integer.valueOf(16) }));
    }
    return paramInt / 8;
  }
  
  public final Object convertFromCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    final CharBuffer localCharBuffer = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    final ArrayList localArrayList = new ArrayList();
    matchWhiteSpaceDelimnatedWords(localCharBuffer, new BuiltInEncodingAlgorithm.WordListener()
    {
      public void word(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        String str = localCharBuffer.subSequence(paramAnonymousInt1, paramAnonymousInt2).toString();
        UUIDEncodingAlgorithm.this.fromUUIDString(str);
        localArrayList.add(Long.valueOf(UUIDEncodingAlgorithm.this._msb));
        localArrayList.add(Long.valueOf(UUIDEncodingAlgorithm.this._lsb));
      }
    });
    return generateArrayFromList(localArrayList);
  }
  
  public final void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer)
  {
    if (!(paramObject instanceof long[])) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotLongArray"));
    }
    long[] arrayOfLong = (long[])paramObject;
    int i = arrayOfLong.length - 2;
    for (int j = 0; j <= i; j += 2)
    {
      paramStringBuffer.append(toUUIDString(arrayOfLong[j], arrayOfLong[(j + 1)]));
      if (j != i) {
        paramStringBuffer.append(' ');
      }
    }
  }
  
  final void fromUUIDString(String paramString)
  {
    String[] arrayOfString = paramString.split("-");
    if (arrayOfString.length != 5) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.invalidUUID", new Object[] { paramString }));
    }
    for (int i = 0; i < 5; i++) {
      arrayOfString[i] = ("0x" + arrayOfString[i]);
    }
    this._msb = Long.parseLong(arrayOfString[0], 16);
    this._msb <<= 16;
    this._msb |= Long.parseLong(arrayOfString[1], 16);
    this._msb <<= 16;
    this._msb |= Long.parseLong(arrayOfString[2], 16);
    this._lsb = Long.parseLong(arrayOfString[3], 16);
    this._lsb <<= 48;
    this._lsb |= Long.parseLong(arrayOfString[4], 16);
  }
  
  final String toUUIDString(long paramLong1, long paramLong2)
  {
    return digits(paramLong1 >> 32, 8) + "-" + digits(paramLong1 >> 16, 4) + "-" + digits(paramLong1, 4) + "-" + digits(paramLong2 >> 48, 4) + "-" + digits(paramLong2, 12);
  }
  
  final String digits(long paramLong, int paramInt)
  {
    long l = 1L << paramInt * 4;
    return Long.toHexString(l | paramLong & l - 1L).substring(1);
  }
}
