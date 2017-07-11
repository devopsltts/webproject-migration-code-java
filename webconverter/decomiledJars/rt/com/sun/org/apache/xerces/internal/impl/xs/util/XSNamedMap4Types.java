package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public final class XSNamedMap4Types
  extends XSNamedMapImpl
{
  private final short fType;
  
  public XSNamedMap4Types(String paramString, SymbolHash paramSymbolHash, short paramShort)
  {
    super(paramString, paramSymbolHash);
    this.fType = paramShort;
  }
  
  public XSNamedMap4Types(String[] paramArrayOfString, SymbolHash[] paramArrayOfSymbolHash, int paramInt, short paramShort)
  {
    super(paramArrayOfString, paramArrayOfSymbolHash, paramInt);
    this.fType = paramShort;
  }
  
  public synchronized int getLength()
  {
    if (this.fLength == -1)
    {
      int i = 0;
      for (int j = 0; j < this.fNSNum; j++) {
        i += this.fMaps[j].getLength();
      }
      j = 0;
      XSObject[] arrayOfXSObject = new XSObject[i];
      for (int k = 0; k < this.fNSNum; k++) {
        j += this.fMaps[k].getValues(arrayOfXSObject, j);
      }
      this.fLength = 0;
      this.fArray = new XSObject[i];
      for (int m = 0; m < i; m++)
      {
        XSTypeDefinition localXSTypeDefinition = (XSTypeDefinition)arrayOfXSObject[m];
        if (localXSTypeDefinition.getTypeCategory() == this.fType) {
          this.fArray[(this.fLength++)] = localXSTypeDefinition;
        }
      }
    }
    return this.fLength;
  }
  
  public XSObject itemByName(String paramString1, String paramString2)
  {
    for (int i = 0; i < this.fNSNum; i++) {
      if (isEqual(paramString1, this.fNamespaces[i]))
      {
        XSTypeDefinition localXSTypeDefinition = (XSTypeDefinition)this.fMaps[i].get(paramString2);
        if ((localXSTypeDefinition != null) && (localXSTypeDefinition.getTypeCategory() == this.fType)) {
          return localXSTypeDefinition;
        }
        return null;
      }
    }
    return null;
  }
  
  public synchronized XSObject item(int paramInt)
  {
    if (this.fArray == null) {
      getLength();
    }
    if ((paramInt < 0) || (paramInt >= this.fLength)) {
      return null;
    }
    return this.fArray[paramInt];
  }
}
