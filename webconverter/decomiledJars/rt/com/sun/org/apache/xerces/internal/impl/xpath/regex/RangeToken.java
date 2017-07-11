package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.io.PrintStream;
import java.io.Serializable;

final class RangeToken
  extends Token
  implements Serializable
{
  private static final long serialVersionUID = 3257568399592010545L;
  int[] ranges;
  boolean sorted;
  boolean compacted;
  RangeToken icaseCache = null;
  int[] map = null;
  int nonMapIndex;
  private static final int MAPSIZE = 256;
  
  RangeToken(int paramInt)
  {
    super(paramInt);
    setSorted(false);
  }
  
  protected void addRange(int paramInt1, int paramInt2)
  {
    this.icaseCache = null;
    int i;
    int j;
    if (paramInt1 <= paramInt2)
    {
      i = paramInt1;
      j = paramInt2;
    }
    else
    {
      i = paramInt2;
      j = paramInt1;
    }
    int k = 0;
    if (this.ranges == null)
    {
      this.ranges = new int[2];
      this.ranges[0] = i;
      this.ranges[1] = j;
      setSorted(true);
    }
    else
    {
      k = this.ranges.length;
      if (this.ranges[(k - 1)] + 1 == i)
      {
        this.ranges[(k - 1)] = j;
        return;
      }
      int[] arrayOfInt = new int[k + 2];
      System.arraycopy(this.ranges, 0, arrayOfInt, 0, k);
      this.ranges = arrayOfInt;
      if (this.ranges[(k - 1)] >= i) {
        setSorted(false);
      }
      this.ranges[(k++)] = i;
      this.ranges[k] = j;
      if (!this.sorted) {
        sortRanges();
      }
    }
  }
  
  private final boolean isSorted()
  {
    return this.sorted;
  }
  
  private final void setSorted(boolean paramBoolean)
  {
    this.sorted = paramBoolean;
    if (!paramBoolean) {
      this.compacted = false;
    }
  }
  
  private final boolean isCompacted()
  {
    return this.compacted;
  }
  
  private final void setCompacted()
  {
    this.compacted = true;
  }
  
  protected void sortRanges()
  {
    if (isSorted()) {
      return;
    }
    if (this.ranges == null) {
      return;
    }
    for (int i = this.ranges.length - 4; i >= 0; i -= 2) {
      for (int j = 0; j <= i; j += 2) {
        if ((this.ranges[j] > this.ranges[(j + 2)]) || ((this.ranges[j] == this.ranges[(j + 2)]) && (this.ranges[(j + 1)] > this.ranges[(j + 3)])))
        {
          int k = this.ranges[(j + 2)];
          this.ranges[(j + 2)] = this.ranges[j];
          this.ranges[j] = k;
          k = this.ranges[(j + 3)];
          this.ranges[(j + 3)] = this.ranges[(j + 1)];
          this.ranges[(j + 1)] = k;
        }
      }
    }
    setSorted(true);
  }
  
  protected void compactRanges()
  {
    int i = 0;
    if ((this.ranges == null) || (this.ranges.length <= 2)) {
      return;
    }
    if (isCompacted()) {
      return;
    }
    int j = 0;
    int k = 0;
    while (k < this.ranges.length)
    {
      if (j != k)
      {
        this.ranges[j] = this.ranges[(k++)];
        this.ranges[(j + 1)] = this.ranges[(k++)];
      }
      else
      {
        k += 2;
      }
      int m = this.ranges[(j + 1)];
      while ((k < this.ranges.length) && (m + 1 >= this.ranges[k])) {
        if (m + 1 == this.ranges[k])
        {
          if (i != 0) {
            System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[j] + ", " + this.ranges[(j + 1)] + "], [" + this.ranges[k] + ", " + this.ranges[(k + 1)] + "] -> [" + this.ranges[j] + ", " + this.ranges[(k + 1)] + "]");
          }
          this.ranges[(j + 1)] = this.ranges[(k + 1)];
          m = this.ranges[(j + 1)];
          k += 2;
        }
        else if (m >= this.ranges[(k + 1)])
        {
          if (i != 0) {
            System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[j] + ", " + this.ranges[(j + 1)] + "], [" + this.ranges[k] + ", " + this.ranges[(k + 1)] + "] -> [" + this.ranges[j] + ", " + this.ranges[(j + 1)] + "]");
          }
          k += 2;
        }
        else if (m < this.ranges[(k + 1)])
        {
          if (i != 0) {
            System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[j] + ", " + this.ranges[(j + 1)] + "], [" + this.ranges[k] + ", " + this.ranges[(k + 1)] + "] -> [" + this.ranges[j] + ", " + this.ranges[(k + 1)] + "]");
          }
          this.ranges[(j + 1)] = this.ranges[(k + 1)];
          m = this.ranges[(j + 1)];
          k += 2;
        }
        else
        {
          throw new RuntimeException("Token#compactRanges(): Internel Error: [" + this.ranges[j] + "," + this.ranges[(j + 1)] + "] [" + this.ranges[k] + "," + this.ranges[(k + 1)] + "]");
        }
      }
      j += 2;
    }
    if (j != this.ranges.length)
    {
      int[] arrayOfInt = new int[j];
      System.arraycopy(this.ranges, 0, arrayOfInt, 0, j);
      this.ranges = arrayOfInt;
    }
    setCompacted();
  }
  
  protected void mergeRanges(Token paramToken)
  {
    RangeToken localRangeToken = (RangeToken)paramToken;
    sortRanges();
    localRangeToken.sortRanges();
    if (localRangeToken.ranges == null) {
      return;
    }
    this.icaseCache = null;
    setSorted(true);
    if (this.ranges == null)
    {
      this.ranges = new int[localRangeToken.ranges.length];
      System.arraycopy(localRangeToken.ranges, 0, this.ranges, 0, localRangeToken.ranges.length);
      return;
    }
    int[] arrayOfInt = new int[this.ranges.length + localRangeToken.ranges.length];
    int i = 0;
    int j = 0;
    int k = 0;
    while ((i < this.ranges.length) || (j < localRangeToken.ranges.length)) {
      if (i >= this.ranges.length)
      {
        arrayOfInt[(k++)] = localRangeToken.ranges[(j++)];
        arrayOfInt[(k++)] = localRangeToken.ranges[(j++)];
      }
      else if (j >= localRangeToken.ranges.length)
      {
        arrayOfInt[(k++)] = this.ranges[(i++)];
        arrayOfInt[(k++)] = this.ranges[(i++)];
      }
      else if ((localRangeToken.ranges[j] < this.ranges[i]) || ((localRangeToken.ranges[j] == this.ranges[i]) && (localRangeToken.ranges[(j + 1)] < this.ranges[(i + 1)])))
      {
        arrayOfInt[(k++)] = localRangeToken.ranges[(j++)];
        arrayOfInt[(k++)] = localRangeToken.ranges[(j++)];
      }
      else
      {
        arrayOfInt[(k++)] = this.ranges[(i++)];
        arrayOfInt[(k++)] = this.ranges[(i++)];
      }
    }
    this.ranges = arrayOfInt;
  }
  
  protected void subtractRanges(Token paramToken)
  {
    if (paramToken.type == 5)
    {
      intersectRanges(paramToken);
      return;
    }
    RangeToken localRangeToken = (RangeToken)paramToken;
    if ((localRangeToken.ranges == null) || (this.ranges == null)) {
      return;
    }
    this.icaseCache = null;
    sortRanges();
    compactRanges();
    localRangeToken.sortRanges();
    localRangeToken.compactRanges();
    int[] arrayOfInt = new int[this.ranges.length + localRangeToken.ranges.length];
    int i = 0;
    int j = 0;
    int k = 0;
    while ((j < this.ranges.length) && (k < localRangeToken.ranges.length))
    {
      int m = this.ranges[j];
      int n = this.ranges[(j + 1)];
      int i1 = localRangeToken.ranges[k];
      int i2 = localRangeToken.ranges[(k + 1)];
      if (n < i1)
      {
        arrayOfInt[(i++)] = this.ranges[(j++)];
        arrayOfInt[(i++)] = this.ranges[(j++)];
      }
      else if ((n >= i1) && (m <= i2))
      {
        if ((i1 <= m) && (n <= i2))
        {
          j += 2;
        }
        else if (i1 <= m)
        {
          this.ranges[j] = (i2 + 1);
          k += 2;
        }
        else if (n <= i2)
        {
          arrayOfInt[(i++)] = m;
          arrayOfInt[(i++)] = (i1 - 1);
          j += 2;
        }
        else
        {
          arrayOfInt[(i++)] = m;
          arrayOfInt[(i++)] = (i1 - 1);
          this.ranges[j] = (i2 + 1);
          k += 2;
        }
      }
      else if (i2 < m)
      {
        k += 2;
      }
      else
      {
        throw new RuntimeException("Token#subtractRanges(): Internal Error: [" + this.ranges[j] + "," + this.ranges[(j + 1)] + "] - [" + localRangeToken.ranges[k] + "," + localRangeToken.ranges[(k + 1)] + "]");
      }
    }
    while (j < this.ranges.length)
    {
      arrayOfInt[(i++)] = this.ranges[(j++)];
      arrayOfInt[(i++)] = this.ranges[(j++)];
    }
    this.ranges = new int[i];
    System.arraycopy(arrayOfInt, 0, this.ranges, 0, i);
  }
  
  protected void intersectRanges(Token paramToken)
  {
    RangeToken localRangeToken = (RangeToken)paramToken;
    if ((localRangeToken.ranges == null) || (this.ranges == null)) {
      return;
    }
    this.icaseCache = null;
    sortRanges();
    compactRanges();
    localRangeToken.sortRanges();
    localRangeToken.compactRanges();
    int[] arrayOfInt = new int[this.ranges.length + localRangeToken.ranges.length];
    int i = 0;
    int j = 0;
    int k = 0;
    while ((j < this.ranges.length) && (k < localRangeToken.ranges.length))
    {
      int m = this.ranges[j];
      int n = this.ranges[(j + 1)];
      int i1 = localRangeToken.ranges[k];
      int i2 = localRangeToken.ranges[(k + 1)];
      if (n < i1) {
        j += 2;
      } else if ((n >= i1) && (m <= i2))
      {
        if ((i1 <= i1) && (n <= i2))
        {
          arrayOfInt[(i++)] = m;
          arrayOfInt[(i++)] = n;
          j += 2;
        }
        else if (i1 <= m)
        {
          arrayOfInt[(i++)] = m;
          arrayOfInt[(i++)] = i2;
          this.ranges[j] = (i2 + 1);
          k += 2;
        }
        else if (n <= i2)
        {
          arrayOfInt[(i++)] = i1;
          arrayOfInt[(i++)] = n;
          j += 2;
        }
        else
        {
          arrayOfInt[(i++)] = i1;
          arrayOfInt[(i++)] = i2;
          this.ranges[j] = (i2 + 1);
        }
      }
      else if (i2 < m) {
        k += 2;
      } else {
        throw new RuntimeException("Token#intersectRanges(): Internal Error: [" + this.ranges[j] + "," + this.ranges[(j + 1)] + "] & [" + localRangeToken.ranges[k] + "," + localRangeToken.ranges[(k + 1)] + "]");
      }
    }
    while (j < this.ranges.length)
    {
      arrayOfInt[(i++)] = this.ranges[(j++)];
      arrayOfInt[(i++)] = this.ranges[(j++)];
    }
    this.ranges = new int[i];
    System.arraycopy(arrayOfInt, 0, this.ranges, 0, i);
  }
  
  static Token complementRanges(Token paramToken)
  {
    if ((paramToken.type != 4) && (paramToken.type != 5)) {
      throw new IllegalArgumentException("Token#complementRanges(): must be RANGE: " + paramToken.type);
    }
    RangeToken localRangeToken1 = (RangeToken)paramToken;
    localRangeToken1.sortRanges();
    localRangeToken1.compactRanges();
    int i = localRangeToken1.ranges.length + 2;
    if (localRangeToken1.ranges[0] == 0) {
      i -= 2;
    }
    int j = localRangeToken1.ranges[(localRangeToken1.ranges.length - 1)];
    if (j == 1114111) {
      i -= 2;
    }
    RangeToken localRangeToken2 = Token.createRange();
    localRangeToken2.ranges = new int[i];
    int k = 0;
    if (localRangeToken1.ranges[0] > 0)
    {
      localRangeToken2.ranges[(k++)] = 0;
      localRangeToken2.ranges[(k++)] = (localRangeToken1.ranges[0] - 1);
    }
    for (int m = 1; m < localRangeToken1.ranges.length - 2; m += 2)
    {
      localRangeToken2.ranges[(k++)] = (localRangeToken1.ranges[m] + 1);
      localRangeToken2.ranges[(k++)] = (localRangeToken1.ranges[(m + 1)] - 1);
    }
    if (j != 1114111)
    {
      localRangeToken2.ranges[(k++)] = (j + 1);
      localRangeToken2.ranges[k] = 1114111;
    }
    localRangeToken2.setCompacted();
    return localRangeToken2;
  }
  
  synchronized RangeToken getCaseInsensitiveToken()
  {
    if (this.icaseCache != null) {
      return this.icaseCache;
    }
    RangeToken localRangeToken1 = this.type == 4 ? Token.createRange() : Token.createNRange();
    int k;
    for (int i = 0; i < this.ranges.length; i += 2) {
      for (j = this.ranges[i]; j <= this.ranges[(i + 1)]; j++) {
        if (j > 65535)
        {
          localRangeToken1.addRange(j, j);
        }
        else
        {
          k = Character.toUpperCase((char)j);
          localRangeToken1.addRange(k, k);
        }
      }
    }
    RangeToken localRangeToken2 = this.type == 4 ? Token.createRange() : Token.createNRange();
    for (int j = 0; j < localRangeToken1.ranges.length; j += 2) {
      for (k = localRangeToken1.ranges[j]; k <= localRangeToken1.ranges[(j + 1)]; k++) {
        if (k > 65535)
        {
          localRangeToken2.addRange(k, k);
        }
        else
        {
          int m = Character.toUpperCase((char)k);
          localRangeToken2.addRange(m, m);
        }
      }
    }
    localRangeToken2.mergeRanges(localRangeToken1);
    localRangeToken2.mergeRanges(this);
    localRangeToken2.compactRanges();
    this.icaseCache = localRangeToken2;
    return localRangeToken2;
  }
  
  void dumpRanges()
  {
    System.err.print("RANGE: ");
    if (this.ranges == null) {
      System.err.println(" NULL");
    }
    for (int i = 0; i < this.ranges.length; i += 2) {
      System.err.print("[" + this.ranges[i] + "," + this.ranges[(i + 1)] + "] ");
    }
    System.err.println("");
  }
  
  boolean match(int paramInt)
  {
    if (this.map == null) {
      createMap();
    }
    boolean bool;
    int i;
    if (this.type == 4)
    {
      if (paramInt < 256) {
        return (this.map[(paramInt / 32)] & 1 << (paramInt & 0x1F)) != 0;
      }
      bool = false;
      for (i = this.nonMapIndex; i < this.ranges.length; i += 2) {
        if ((this.ranges[i] <= paramInt) && (paramInt <= this.ranges[(i + 1)])) {
          return true;
        }
      }
    }
    else
    {
      if (paramInt < 256) {
        return (this.map[(paramInt / 32)] & 1 << (paramInt & 0x1F)) == 0;
      }
      bool = true;
      for (i = this.nonMapIndex; i < this.ranges.length; i += 2) {
        if ((this.ranges[i] <= paramInt) && (paramInt <= this.ranges[(i + 1)])) {
          return false;
        }
      }
    }
    return bool;
  }
  
  private void createMap()
  {
    int i = 8;
    int[] arrayOfInt = new int[i];
    int j = this.ranges.length;
    for (int k = 0; k < i; k++) {
      arrayOfInt[k] = 0;
    }
    for (k = 0; k < this.ranges.length; k += 2)
    {
      int m = this.ranges[k];
      int n = this.ranges[(k + 1)];
      if (m < 256)
      {
        for (int i1 = m; (i1 <= n) && (i1 < 256); i1++) {
          arrayOfInt[(i1 / 32)] |= 1 << (i1 & 0x1F);
        }
      }
      else
      {
        j = k;
        break;
      }
      if (n >= 256)
      {
        j = k;
        break;
      }
    }
    this.map = arrayOfInt;
    this.nonMapIndex = j;
  }
  
  public String toString(int paramInt)
  {
    String str;
    StringBuffer localStringBuffer;
    int i;
    if (this.type == 4)
    {
      if (this == Token.token_dot)
      {
        str = ".";
      }
      else if (this == Token.token_0to9)
      {
        str = "\\d";
      }
      else if (this == Token.token_wordchars)
      {
        str = "\\w";
      }
      else if (this == Token.token_spaces)
      {
        str = "\\s";
      }
      else
      {
        localStringBuffer = new StringBuffer();
        localStringBuffer.append("[");
        for (i = 0; i < this.ranges.length; i += 2)
        {
          if (((paramInt & 0x400) != 0) && (i > 0)) {
            localStringBuffer.append(",");
          }
          if (this.ranges[i] == this.ranges[(i + 1)])
          {
            localStringBuffer.append(escapeCharInCharClass(this.ranges[i]));
          }
          else
          {
            localStringBuffer.append(escapeCharInCharClass(this.ranges[i]));
            localStringBuffer.append('-');
            localStringBuffer.append(escapeCharInCharClass(this.ranges[(i + 1)]));
          }
        }
        localStringBuffer.append("]");
        str = localStringBuffer.toString();
      }
    }
    else if (this == Token.token_not_0to9)
    {
      str = "\\D";
    }
    else if (this == Token.token_not_wordchars)
    {
      str = "\\W";
    }
    else if (this == Token.token_not_spaces)
    {
      str = "\\S";
    }
    else
    {
      localStringBuffer = new StringBuffer();
      localStringBuffer.append("[^");
      for (i = 0; i < this.ranges.length; i += 2)
      {
        if (((paramInt & 0x400) != 0) && (i > 0)) {
          localStringBuffer.append(",");
        }
        if (this.ranges[i] == this.ranges[(i + 1)])
        {
          localStringBuffer.append(escapeCharInCharClass(this.ranges[i]));
        }
        else
        {
          localStringBuffer.append(escapeCharInCharClass(this.ranges[i]));
          localStringBuffer.append('-');
          localStringBuffer.append(escapeCharInCharClass(this.ranges[(i + 1)]));
        }
      }
      localStringBuffer.append("]");
      str = localStringBuffer.toString();
    }
    return str;
  }
  
  private static String escapeCharInCharClass(int paramInt)
  {
    String str1;
    switch (paramInt)
    {
    case 44: 
    case 45: 
    case 91: 
    case 92: 
    case 93: 
    case 94: 
      str1 = "\\" + (char)paramInt;
      break;
    case 12: 
      str1 = "\\f";
      break;
    case 10: 
      str1 = "\\n";
      break;
    case 13: 
      str1 = "\\r";
      break;
    case 9: 
      str1 = "\\t";
      break;
    case 27: 
      str1 = "\\e";
      break;
    default: 
      String str2;
      if (paramInt < 32)
      {
        str2 = "0" + Integer.toHexString(paramInt);
        str1 = "\\x" + str2.substring(str2.length() - 2, str2.length());
      }
      else if (paramInt >= 65536)
      {
        str2 = "0" + Integer.toHexString(paramInt);
        str1 = "\\v" + str2.substring(str2.length() - 6, str2.length());
      }
      else
      {
        str1 = "" + (char)paramInt;
      }
      break;
    }
    return str1;
  }
}
