package org.xml.sax.helpers;

import org.xml.sax.Attributes;

public class AttributesImpl
  implements Attributes
{
  int length;
  String[] data;
  
  public AttributesImpl()
  {
    this.length = 0;
    this.data = null;
  }
  
  public AttributesImpl(Attributes paramAttributes)
  {
    setAttributes(paramAttributes);
  }
  
  public int getLength()
  {
    return this.length;
  }
  
  public String getURI(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.length)) {
      return this.data[(paramInt * 5)];
    }
    return null;
  }
  
  public String getLocalName(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.length)) {
      return this.data[(paramInt * 5 + 1)];
    }
    return null;
  }
  
  public String getQName(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.length)) {
      return this.data[(paramInt * 5 + 2)];
    }
    return null;
  }
  
  public String getType(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.length)) {
      return this.data[(paramInt * 5 + 3)];
    }
    return null;
  }
  
  public String getValue(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.length)) {
      return this.data[(paramInt * 5 + 4)];
    }
    return null;
  }
  
  public int getIndex(String paramString1, String paramString2)
  {
    int i = this.length * 5;
    for (int j = 0; j < i; j += 5) {
      if ((this.data[j].equals(paramString1)) && (this.data[(j + 1)].equals(paramString2))) {
        return j / 5;
      }
    }
    return -1;
  }
  
  public int getIndex(String paramString)
  {
    int i = this.length * 5;
    for (int j = 0; j < i; j += 5) {
      if (this.data[(j + 2)].equals(paramString)) {
        return j / 5;
      }
    }
    return -1;
  }
  
  public String getType(String paramString1, String paramString2)
  {
    int i = this.length * 5;
    for (int j = 0; j < i; j += 5) {
      if ((this.data[j].equals(paramString1)) && (this.data[(j + 1)].equals(paramString2))) {
        return this.data[(j + 3)];
      }
    }
    return null;
  }
  
  public String getType(String paramString)
  {
    int i = this.length * 5;
    for (int j = 0; j < i; j += 5) {
      if (this.data[(j + 2)].equals(paramString)) {
        return this.data[(j + 3)];
      }
    }
    return null;
  }
  
  public String getValue(String paramString1, String paramString2)
  {
    int i = this.length * 5;
    for (int j = 0; j < i; j += 5) {
      if ((this.data[j].equals(paramString1)) && (this.data[(j + 1)].equals(paramString2))) {
        return this.data[(j + 4)];
      }
    }
    return null;
  }
  
  public String getValue(String paramString)
  {
    int i = this.length * 5;
    for (int j = 0; j < i; j += 5) {
      if (this.data[(j + 2)].equals(paramString)) {
        return this.data[(j + 4)];
      }
    }
    return null;
  }
  
  public void clear()
  {
    if (this.data != null) {
      for (int i = 0; i < this.length * 5; i++) {
        this.data[i] = null;
      }
    }
    this.length = 0;
  }
  
  public void setAttributes(Attributes paramAttributes)
  {
    clear();
    this.length = paramAttributes.getLength();
    if (this.length > 0)
    {
      this.data = new String[this.length * 5];
      for (int i = 0; i < this.length; i++)
      {
        this.data[(i * 5)] = paramAttributes.getURI(i);
        this.data[(i * 5 + 1)] = paramAttributes.getLocalName(i);
        this.data[(i * 5 + 2)] = paramAttributes.getQName(i);
        this.data[(i * 5 + 3)] = paramAttributes.getType(i);
        this.data[(i * 5 + 4)] = paramAttributes.getValue(i);
      }
    }
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    ensureCapacity(this.length + 1);
    this.data[(this.length * 5)] = paramString1;
    this.data[(this.length * 5 + 1)] = paramString2;
    this.data[(this.length * 5 + 2)] = paramString3;
    this.data[(this.length * 5 + 3)] = paramString4;
    this.data[(this.length * 5 + 4)] = paramString5;
    this.length += 1;
  }
  
  public void setAttribute(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    if ((paramInt >= 0) && (paramInt < this.length))
    {
      this.data[(paramInt * 5)] = paramString1;
      this.data[(paramInt * 5 + 1)] = paramString2;
      this.data[(paramInt * 5 + 2)] = paramString3;
      this.data[(paramInt * 5 + 3)] = paramString4;
      this.data[(paramInt * 5 + 4)] = paramString5;
    }
    else
    {
      badIndex(paramInt);
    }
  }
  
  public void removeAttribute(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.length))
    {
      if (paramInt < this.length - 1) {
        System.arraycopy(this.data, (paramInt + 1) * 5, this.data, paramInt * 5, (this.length - paramInt - 1) * 5);
      }
      paramInt = (this.length - 1) * 5;
      this.data[(paramInt++)] = null;
      this.data[(paramInt++)] = null;
      this.data[(paramInt++)] = null;
      this.data[(paramInt++)] = null;
      this.data[paramInt] = null;
      this.length -= 1;
    }
    else
    {
      badIndex(paramInt);
    }
  }
  
  public void setURI(int paramInt, String paramString)
  {
    if ((paramInt >= 0) && (paramInt < this.length)) {
      this.data[(paramInt * 5)] = paramString;
    } else {
      badIndex(paramInt);
    }
  }
  
  public void setLocalName(int paramInt, String paramString)
  {
    if ((paramInt >= 0) && (paramInt < this.length)) {
      this.data[(paramInt * 5 + 1)] = paramString;
    } else {
      badIndex(paramInt);
    }
  }
  
  public void setQName(int paramInt, String paramString)
  {
    if ((paramInt >= 0) && (paramInt < this.length)) {
      this.data[(paramInt * 5 + 2)] = paramString;
    } else {
      badIndex(paramInt);
    }
  }
  
  public void setType(int paramInt, String paramString)
  {
    if ((paramInt >= 0) && (paramInt < this.length)) {
      this.data[(paramInt * 5 + 3)] = paramString;
    } else {
      badIndex(paramInt);
    }
  }
  
  public void setValue(int paramInt, String paramString)
  {
    if ((paramInt >= 0) && (paramInt < this.length)) {
      this.data[(paramInt * 5 + 4)] = paramString;
    } else {
      badIndex(paramInt);
    }
  }
  
  private void ensureCapacity(int paramInt)
  {
    if (paramInt <= 0) {
      return;
    }
    int i;
    if ((this.data == null) || (this.data.length == 0))
    {
      i = 25;
    }
    else
    {
      if (this.data.length >= paramInt * 5) {
        return;
      }
      i = this.data.length;
    }
    while (i < paramInt * 5) {
      i *= 2;
    }
    String[] arrayOfString = new String[i];
    if (this.length > 0) {
      System.arraycopy(this.data, 0, arrayOfString, 0, this.length * 5);
    }
    this.data = arrayOfString;
  }
  
  private void badIndex(int paramInt)
    throws ArrayIndexOutOfBoundsException
  {
    String str = "Attempt to modify attribute at illegal index: " + paramInt;
    throw new ArrayIndexOutOfBoundsException(str);
  }
}
