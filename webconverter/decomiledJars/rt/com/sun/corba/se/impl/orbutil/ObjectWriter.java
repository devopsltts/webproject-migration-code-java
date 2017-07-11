package com.sun.corba.se.impl.orbutil;

import java.util.Arrays;

public abstract class ObjectWriter
{
  protected StringBuffer result = new StringBuffer();
  
  public static ObjectWriter make(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (paramBoolean) {
      return new IndentingObjectWriter(paramInt1, paramInt2);
    }
    return new SimpleObjectWriter(null);
  }
  
  public abstract void startObject(Object paramObject);
  
  public abstract void startElement();
  
  public abstract void endElement();
  
  public abstract void endObject(String paramString);
  
  public abstract void endObject();
  
  public String toString()
  {
    return this.result.toString();
  }
  
  public void append(boolean paramBoolean)
  {
    this.result.append(paramBoolean);
  }
  
  public void append(char paramChar)
  {
    this.result.append(paramChar);
  }
  
  public void append(short paramShort)
  {
    this.result.append(paramShort);
  }
  
  public void append(int paramInt)
  {
    this.result.append(paramInt);
  }
  
  public void append(long paramLong)
  {
    this.result.append(paramLong);
  }
  
  public void append(float paramFloat)
  {
    this.result.append(paramFloat);
  }
  
  public void append(double paramDouble)
  {
    this.result.append(paramDouble);
  }
  
  public void append(String paramString)
  {
    this.result.append(paramString);
  }
  
  protected ObjectWriter() {}
  
  protected void appendObjectHeader(Object paramObject)
  {
    this.result.append(paramObject.getClass().getName());
    this.result.append("<");
    this.result.append(System.identityHashCode(paramObject));
    this.result.append(">");
    Class localClass = paramObject.getClass().getComponentType();
    if (localClass != null)
    {
      this.result.append("[");
      Object localObject;
      if (localClass == Boolean.TYPE)
      {
        localObject = (boolean[])paramObject;
        this.result.append(localObject.length);
        this.result.append("]");
      }
      else if (localClass == Byte.TYPE)
      {
        localObject = (byte[])paramObject;
        this.result.append(localObject.length);
        this.result.append("]");
      }
      else if (localClass == Short.TYPE)
      {
        localObject = (short[])paramObject;
        this.result.append(localObject.length);
        this.result.append("]");
      }
      else if (localClass == Integer.TYPE)
      {
        localObject = (int[])paramObject;
        this.result.append(localObject.length);
        this.result.append("]");
      }
      else if (localClass == Long.TYPE)
      {
        localObject = (long[])paramObject;
        this.result.append(localObject.length);
        this.result.append("]");
      }
      else if (localClass == Character.TYPE)
      {
        localObject = (char[])paramObject;
        this.result.append(localObject.length);
        this.result.append("]");
      }
      else if (localClass == Float.TYPE)
      {
        localObject = (float[])paramObject;
        this.result.append(localObject.length);
        this.result.append("]");
      }
      else if (localClass == Double.TYPE)
      {
        localObject = (double[])paramObject;
        this.result.append(localObject.length);
        this.result.append("]");
      }
      else
      {
        localObject = (Object[])paramObject;
        this.result.append(localObject.length);
        this.result.append("]");
      }
    }
    this.result.append("(");
  }
  
  private static class IndentingObjectWriter
    extends ObjectWriter
  {
    private int level;
    private int increment;
    
    public IndentingObjectWriter(int paramInt1, int paramInt2)
    {
      this.level = paramInt1;
      this.increment = paramInt2;
      startLine();
    }
    
    private void startLine()
    {
      char[] arrayOfChar = new char[this.level * this.increment];
      Arrays.fill(arrayOfChar, ' ');
      this.result.append(arrayOfChar);
    }
    
    public void startObject(Object paramObject)
    {
      appendObjectHeader(paramObject);
      this.level += 1;
    }
    
    public void startElement()
    {
      this.result.append("\n");
      startLine();
    }
    
    public void endElement() {}
    
    public void endObject(String paramString)
    {
      this.level -= 1;
      this.result.append(paramString);
      this.result.append(")");
    }
    
    public void endObject()
    {
      this.level -= 1;
      this.result.append("\n");
      startLine();
      this.result.append(")");
    }
  }
  
  private static class SimpleObjectWriter
    extends ObjectWriter
  {
    private SimpleObjectWriter() {}
    
    public void startObject(Object paramObject)
    {
      appendObjectHeader(paramObject);
      this.result.append(" ");
    }
    
    public void startElement()
    {
      this.result.append(" ");
    }
    
    public void endObject(String paramString)
    {
      this.result.append(paramString);
      this.result.append(")");
    }
    
    public void endElement() {}
    
    public void endObject()
    {
      this.result.append(")");
    }
  }
}
