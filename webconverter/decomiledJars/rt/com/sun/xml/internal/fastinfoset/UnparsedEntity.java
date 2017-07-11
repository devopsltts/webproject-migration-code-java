package com.sun.xml.internal.fastinfoset;

public class UnparsedEntity
  extends Notation
{
  public final String notationName;
  
  public UnparsedEntity(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    super(paramString1, paramString2, paramString3);
    this.notationName = paramString4;
  }
}
