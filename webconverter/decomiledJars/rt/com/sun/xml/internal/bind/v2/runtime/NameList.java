package com.sun.xml.internal.bind.v2.runtime;

public final class NameList
{
  public final String[] namespaceURIs;
  public final boolean[] nsUriCannotBeDefaulted;
  public final String[] localNames;
  public final int numberOfElementNames;
  public final int numberOfAttributeNames;
  
  public NameList(String[] paramArrayOfString1, boolean[] paramArrayOfBoolean, String[] paramArrayOfString2, int paramInt1, int paramInt2)
  {
    this.namespaceURIs = paramArrayOfString1;
    this.nsUriCannotBeDefaulted = paramArrayOfBoolean;
    this.localNames = paramArrayOfString2;
    this.numberOfElementNames = paramInt1;
    this.numberOfAttributeNames = paramInt2;
  }
}
