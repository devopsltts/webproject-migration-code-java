package com.sun.xml.internal.bind.v2.runtime.output;

import java.io.IOException;

public abstract class Pcdata
  implements CharSequence
{
  public Pcdata() {}
  
  public abstract void writeTo(UTF8XmlOutput paramUTF8XmlOutput)
    throws IOException;
  
  public void writeTo(char[] paramArrayOfChar, int paramInt)
  {
    toString().getChars(0, length(), paramArrayOfChar, paramInt);
  }
  
  public abstract String toString();
}
