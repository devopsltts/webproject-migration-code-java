package jdk.internal.util.xml.impl;

import jdk.internal.org.xml.sax.Attributes;

public class Attrs
  implements Attributes
{
  String[] mItems = new String[64];
  private char mLength;
  private char mAttrIdx = '\000';
  
  public Attrs() {}
  
  public void setLength(char paramChar)
  {
    if (paramChar > (char)(this.mItems.length >> 3)) {
      this.mItems = new String[paramChar << '\003'];
    }
    this.mLength = paramChar;
  }
  
  public int getLength()
  {
    return this.mLength;
  }
  
  public String getURI(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < this.mLength) ? this.mItems[(paramInt << 3)] : null;
  }
  
  public String getLocalName(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < this.mLength) ? this.mItems[((paramInt << 3) + 2)] : null;
  }
  
  public String getQName(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mLength)) {
      return null;
    }
    return this.mItems[((paramInt << 3) + 1)];
  }
  
  public String getType(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < this.mItems.length >> 3) ? this.mItems[((paramInt << 3) + 4)] : null;
  }
  
  public String getValue(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < this.mLength) ? this.mItems[((paramInt << 3) + 3)] : null;
  }
  
  public int getIndex(String paramString1, String paramString2)
  {
    int i = this.mLength;
    for (int j = 0; j < i; j = (char)(j + 1)) {
      if ((this.mItems[(j << 3)].equals(paramString1)) && (this.mItems[((j << 3) + 2)].equals(paramString2))) {
        return j;
      }
    }
    return -1;
  }
  
  int getIndexNullNS(String paramString1, String paramString2)
  {
    int i = this.mLength;
    int j;
    if (paramString1 != null) {
      for (j = 0; j < i; j = (char)(j + 1)) {
        if ((this.mItems[(j << 3)].equals(paramString1)) && (this.mItems[((j << 3) + 2)].equals(paramString2))) {
          return j;
        }
      }
    } else {
      for (j = 0; j < i; j = (char)(j + 1)) {
        if (this.mItems[((j << 3) + 2)].equals(paramString2)) {
          return j;
        }
      }
    }
    return -1;
  }
  
  public int getIndex(String paramString)
  {
    int i = this.mLength;
    for (int j = 0; j < i; j = (char)(j + 1)) {
      if (this.mItems[((j << 3) + 1)].equals(paramString)) {
        return j;
      }
    }
    return -1;
  }
  
  public String getType(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    return i >= 0 ? this.mItems[((i << 3) + 4)] : null;
  }
  
  public String getType(String paramString)
  {
    int i = getIndex(paramString);
    return i >= 0 ? this.mItems[((i << 3) + 4)] : null;
  }
  
  public String getValue(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    return i >= 0 ? this.mItems[((i << 3) + 3)] : null;
  }
  
  public String getValue(String paramString)
  {
    int i = getIndex(paramString);
    return i >= 0 ? this.mItems[((i << 3) + 3)] : null;
  }
  
  public boolean isDeclared(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mLength)) {
      throw new ArrayIndexOutOfBoundsException("");
    }
    return this.mItems[((paramInt << 3) + 5)] != null;
  }
  
  public boolean isDeclared(String paramString)
  {
    int i = getIndex(paramString);
    if (i < 0) {
      throw new IllegalArgumentException("");
    }
    return this.mItems[((i << 3) + 5)] != null;
  }
  
  public boolean isDeclared(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    if (i < 0) {
      throw new IllegalArgumentException("");
    }
    return this.mItems[((i << 3) + 5)] != null;
  }
  
  public boolean isSpecified(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mLength)) {
      throw new ArrayIndexOutOfBoundsException("");
    }
    String str = this.mItems[((paramInt << 3) + 5)];
    return str.charAt(0) == 'd';
  }
  
  public boolean isSpecified(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    if (i < 0) {
      throw new IllegalArgumentException("");
    }
    String str = this.mItems[((i << 3) + 5)];
    return str.charAt(0) == 'd';
  }
  
  public boolean isSpecified(String paramString)
  {
    int i = getIndex(paramString);
    if (i < 0) {
      throw new IllegalArgumentException("");
    }
    String str = this.mItems[((i << 3) + 5)];
    return str.charAt(0) == 'd';
  }
}
