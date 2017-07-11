package com.sun.corba.se.impl.ior;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ObjectAdapterIdArray
  extends ObjectAdapterIdBase
{
  private final String[] objectAdapterId;
  
  public ObjectAdapterIdArray(String[] paramArrayOfString)
  {
    this.objectAdapterId = paramArrayOfString;
  }
  
  public ObjectAdapterIdArray(String paramString1, String paramString2)
  {
    this.objectAdapterId = new String[2];
    this.objectAdapterId[0] = paramString1;
    this.objectAdapterId[1] = paramString2;
  }
  
  public int getNumLevels()
  {
    return this.objectAdapterId.length;
  }
  
  public Iterator iterator()
  {
    return Arrays.asList(this.objectAdapterId).iterator();
  }
  
  public String[] getAdapterName()
  {
    return (String[])this.objectAdapterId.clone();
  }
}
