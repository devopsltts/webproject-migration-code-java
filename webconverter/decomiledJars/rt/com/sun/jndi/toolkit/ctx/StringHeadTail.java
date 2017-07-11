package com.sun.jndi.toolkit.ctx;

public class StringHeadTail
{
  private int status;
  private String head;
  private String tail;
  
  public StringHeadTail(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, 0);
  }
  
  public StringHeadTail(String paramString1, String paramString2, int paramInt)
  {
    this.status = paramInt;
    this.head = paramString1;
    this.tail = paramString2;
  }
  
  public void setStatus(int paramInt)
  {
    this.status = paramInt;
  }
  
  public String getHead()
  {
    return this.head;
  }
  
  public String getTail()
  {
    return this.tail;
  }
  
  public int getStatus()
  {
    return this.status;
  }
}
