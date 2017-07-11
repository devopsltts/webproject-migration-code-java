package com.sun.beans.editors;

public class IntegerEditor
  extends NumberEditor
{
  public IntegerEditor() {}
  
  public void setAsText(String paramString)
    throws IllegalArgumentException
  {
    setValue(paramString == null ? null : Integer.decode(paramString));
  }
}
