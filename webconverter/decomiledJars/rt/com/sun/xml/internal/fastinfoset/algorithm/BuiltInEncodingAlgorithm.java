package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BuiltInEncodingAlgorithm
  implements EncodingAlgorithm
{
  protected static final Pattern SPACE_PATTERN = Pattern.compile("\\s");
  
  public BuiltInEncodingAlgorithm() {}
  
  public abstract int getPrimtiveLengthFromOctetLength(int paramInt)
    throws EncodingAlgorithmException;
  
  public abstract int getOctetLengthFromPrimitiveLength(int paramInt);
  
  public abstract void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3);
  
  public void matchWhiteSpaceDelimnatedWords(CharBuffer paramCharBuffer, WordListener paramWordListener)
  {
    Matcher localMatcher = SPACE_PATTERN.matcher(paramCharBuffer);
    int i = 0;
    int j = 0;
    while (localMatcher.find())
    {
      j = localMatcher.start();
      if (j != i) {
        paramWordListener.word(i, j);
      }
      i = localMatcher.end();
    }
    if (i != paramCharBuffer.length()) {
      paramWordListener.word(i, paramCharBuffer.length());
    }
  }
  
  public StringBuilder removeWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    for (int j = 0; j < paramInt2; j++) {
      if (Character.isWhitespace(paramArrayOfChar[(j + paramInt1)]))
      {
        if (i < j) {
          localStringBuilder.append(paramArrayOfChar, i + paramInt1, j - i);
        }
        i = j + 1;
      }
    }
    if (i < j) {
      localStringBuilder.append(paramArrayOfChar, i + paramInt1, j - i);
    }
    return localStringBuilder;
  }
  
  public static abstract interface WordListener
  {
    public abstract void word(int paramInt1, int paramInt2);
  }
}
