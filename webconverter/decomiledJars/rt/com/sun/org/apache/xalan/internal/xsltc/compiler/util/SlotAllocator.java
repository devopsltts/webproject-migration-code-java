package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.Type;

final class SlotAllocator
{
  private int _firstAvailableSlot;
  private int _size = 8;
  private int _free = 0;
  private int[] _slotsTaken = new int[this._size];
  
  SlotAllocator() {}
  
  public void initialize(LocalVariableGen[] paramArrayOfLocalVariableGen)
  {
    int i = paramArrayOfLocalVariableGen.length;
    int j = 0;
    for (int n = 0; n < i; n++)
    {
      int k = paramArrayOfLocalVariableGen[n].getType().getSize();
      int m = paramArrayOfLocalVariableGen[n].getIndex();
      j = Math.max(j, m + k);
    }
    this._firstAvailableSlot = j;
  }
  
  public int allocateSlot(Type paramType)
  {
    int i = paramType.getSize();
    int j = this._free;
    int k = this._firstAvailableSlot;
    int m = 0;
    if (this._free + i > this._size)
    {
      int[] arrayOfInt = new int[this._size *= 2];
      for (int i1 = 0; i1 < j; i1++) {
        arrayOfInt[i1] = this._slotsTaken[i1];
      }
      this._slotsTaken = arrayOfInt;
    }
    while (m < j)
    {
      if (k + i <= this._slotsTaken[m])
      {
        for (n = j - 1; n >= m; n--) {
          this._slotsTaken[(n + i)] = this._slotsTaken[n];
        }
        break;
      }
      k = this._slotsTaken[(m++)] + 1;
    }
    for (int n = 0; n < i; n++) {
      this._slotsTaken[(m + n)] = (k + n);
    }
    this._free += i;
    return k;
  }
  
  public void releaseSlot(LocalVariableGen paramLocalVariableGen)
  {
    int i = paramLocalVariableGen.getType().getSize();
    int j = paramLocalVariableGen.getIndex();
    int k = this._free;
    for (int m = 0; m < k; m++) {
      if (this._slotsTaken[m] == j)
      {
        int n = m + i;
        while (n < k) {
          this._slotsTaken[(m++)] = this._slotsTaken[(n++)];
        }
        this._free -= i;
        return;
      }
    }
    String str = "Variable slot allocation error(size=" + i + ", slot=" + j + ", limit=" + k + ")";
    ErrorMsg localErrorMsg = new ErrorMsg("INTERNAL_ERR", str);
    throw new Error(localErrorMsg.toString());
  }
}
