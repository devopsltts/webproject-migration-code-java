package com.sun.org.apache.xerces.internal.impl.dtd.models;

public class CMStateSet
{
  int fBitCount;
  int fByteCount;
  int fBits1;
  int fBits2;
  byte[] fByteArray;
  
  public CMStateSet(int paramInt)
  {
    this.fBitCount = paramInt;
    if (this.fBitCount < 0) {
      throw new RuntimeException("ImplementationMessages.VAL_CMSI");
    }
    if (this.fBitCount > 64)
    {
      this.fByteCount = (this.fBitCount / 8);
      if (this.fBitCount % 8 != 0) {
        this.fByteCount += 1;
      }
      this.fByteArray = new byte[this.fByteCount];
    }
    zeroBits();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    try
    {
      localStringBuffer.append("{");
      for (int i = 0; i < this.fBitCount; i++) {
        if (getBit(i)) {
          localStringBuffer.append(" " + i);
        }
      }
      localStringBuffer.append(" }");
    }
    catch (RuntimeException localRuntimeException) {}
    return localStringBuffer.toString();
  }
  
  public final void intersection(CMStateSet paramCMStateSet)
  {
    if (this.fBitCount < 65)
    {
      this.fBits1 &= paramCMStateSet.fBits1;
      this.fBits2 &= paramCMStateSet.fBits2;
    }
    else
    {
      for (int i = this.fByteCount - 1; i >= 0; i--)
      {
        int tmp54_53 = i;
        byte[] tmp54_50 = this.fByteArray;
        tmp54_50[tmp54_53] = ((byte)(tmp54_50[tmp54_53] & paramCMStateSet.fByteArray[i]));
      }
    }
  }
  
  public final boolean getBit(int paramInt)
  {
    if (paramInt >= this.fBitCount) {
      throw new RuntimeException("ImplementationMessages.VAL_CMSI");
    }
    if (this.fBitCount < 65)
    {
      i = 1 << paramInt % 32;
      if (paramInt < 32) {
        return (this.fBits1 & i) != 0;
      }
      return (this.fBits2 & i) != 0;
    }
    int i = (byte)(1 << paramInt % 8);
    int j = paramInt >> 3;
    return (this.fByteArray[j] & i) != 0;
  }
  
  public final boolean isEmpty()
  {
    if (this.fBitCount < 65) {
      return (this.fBits1 == 0) && (this.fBits2 == 0);
    }
    for (int i = this.fByteCount - 1; i >= 0; i--) {
      if (this.fByteArray[i] != 0) {
        return false;
      }
    }
    return true;
  }
  
  final boolean isSameSet(CMStateSet paramCMStateSet)
  {
    if (this.fBitCount != paramCMStateSet.fBitCount) {
      return false;
    }
    if (this.fBitCount < 65) {
      return (this.fBits1 == paramCMStateSet.fBits1) && (this.fBits2 == paramCMStateSet.fBits2);
    }
    for (int i = this.fByteCount - 1; i >= 0; i--) {
      if (this.fByteArray[i] != paramCMStateSet.fByteArray[i]) {
        return false;
      }
    }
    return true;
  }
  
  public final void union(CMStateSet paramCMStateSet)
  {
    if (this.fBitCount < 65)
    {
      this.fBits1 |= paramCMStateSet.fBits1;
      this.fBits2 |= paramCMStateSet.fBits2;
    }
    else
    {
      for (int i = this.fByteCount - 1; i >= 0; i--)
      {
        int tmp54_53 = i;
        byte[] tmp54_50 = this.fByteArray;
        tmp54_50[tmp54_53] = ((byte)(tmp54_50[tmp54_53] | paramCMStateSet.fByteArray[i]));
      }
    }
  }
  
  public final void setBit(int paramInt)
  {
    if (paramInt >= this.fBitCount) {
      throw new RuntimeException("ImplementationMessages.VAL_CMSI");
    }
    int i;
    if (this.fBitCount < 65)
    {
      i = 1 << paramInt % 32;
      if (paramInt < 32)
      {
        this.fBits1 &= (i ^ 0xFFFFFFFF);
        this.fBits1 |= i;
      }
      else
      {
        this.fBits2 &= (i ^ 0xFFFFFFFF);
        this.fBits2 |= i;
      }
    }
    else
    {
      i = (byte)(1 << paramInt % 8);
      int j = paramInt >> 3;
      int tmp107_106 = j;
      byte[] tmp107_103 = this.fByteArray;
      tmp107_103[tmp107_106] = ((byte)(tmp107_103[tmp107_106] & (i ^ 0xFFFFFFFF)));
      int tmp120_119 = j;
      byte[] tmp120_116 = this.fByteArray;
      tmp120_116[tmp120_119] = ((byte)(tmp120_116[tmp120_119] | i));
    }
  }
  
  public final void setTo(CMStateSet paramCMStateSet)
  {
    if (this.fBitCount != paramCMStateSet.fBitCount) {
      throw new RuntimeException("ImplementationMessages.VAL_CMSI");
    }
    if (this.fBitCount < 65)
    {
      this.fBits1 = paramCMStateSet.fBits1;
      this.fBits2 = paramCMStateSet.fBits2;
    }
    else
    {
      for (int i = this.fByteCount - 1; i >= 0; i--) {
        this.fByteArray[i] = paramCMStateSet.fByteArray[i];
      }
    }
  }
  
  public final void zeroBits()
  {
    if (this.fBitCount < 65)
    {
      this.fBits1 = 0;
      this.fBits2 = 0;
    }
    else
    {
      for (int i = this.fByteCount - 1; i >= 0; i--) {
        this.fByteArray[i] = 0;
      }
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof CMStateSet)) {
      return false;
    }
    return isSameSet((CMStateSet)paramObject);
  }
  
  public int hashCode()
  {
    if (this.fBitCount < 65) {
      return this.fBits1 + this.fBits2 * 31;
    }
    int i = 0;
    for (int j = this.fByteCount - 1; j >= 0; j--) {
      i = this.fByteArray[j] + i * 31;
    }
    return i;
  }
}
