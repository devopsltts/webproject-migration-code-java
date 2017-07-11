package com.sun.org.apache.xerces.internal.util;

import java.io.PrintStream;

public final class IntStack
{
  private int fDepth;
  private int[] fData;
  
  public IntStack() {}
  
  public int size()
  {
    return this.fDepth;
  }
  
  public void push(int paramInt)
  {
    ensureCapacity(this.fDepth + 1);
    this.fData[(this.fDepth++)] = paramInt;
  }
  
  public int peek()
  {
    return this.fData[(this.fDepth - 1)];
  }
  
  public int elementAt(int paramInt)
  {
    return this.fData[paramInt];
  }
  
  public int pop()
  {
    return this.fData[(--this.fDepth)];
  }
  
  public void clear()
  {
    this.fDepth = 0;
  }
  
  public void print()
  {
    System.out.print('(');
    System.out.print(this.fDepth);
    System.out.print(") {");
    for (int i = 0; i < this.fDepth; i++)
    {
      if (i == 3)
      {
        System.out.print(" ...");
        break;
      }
      System.out.print(' ');
      System.out.print(this.fData[i]);
      if (i < this.fDepth - 1) {
        System.out.print(',');
      }
    }
    System.out.print(" }");
    System.out.println();
  }
  
  private void ensureCapacity(int paramInt)
  {
    if (this.fData == null)
    {
      this.fData = new int[32];
    }
    else if (this.fData.length <= paramInt)
    {
      int[] arrayOfInt = new int[this.fData.length * 2];
      System.arraycopy(this.fData, 0, arrayOfInt, 0, this.fData.length);
      this.fData = arrayOfInt;
    }
  }
}
