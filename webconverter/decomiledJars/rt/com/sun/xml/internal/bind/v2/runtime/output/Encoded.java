package com.sun.xml.internal.bind.v2.runtime.output;

import java.io.IOException;

public final class Encoded
{
  public byte[] buf;
  public int len;
  private static final byte[][] entities = new byte[''][];
  private static final byte[][] attributeEntities = new byte[''][];
  
  public Encoded() {}
  
  public Encoded(String paramString)
  {
    set(paramString);
  }
  
  public void ensureSize(int paramInt)
  {
    if ((this.buf == null) || (this.buf.length < paramInt)) {
      this.buf = new byte[paramInt];
    }
  }
  
  public final void set(String paramString)
  {
    int i = paramString.length();
    ensureSize(i * 3 + 1);
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      int m = paramString.charAt(k);
      if (m > 127)
      {
        if (m > 2047)
        {
          if ((55296 <= m) && (m <= 57343))
          {
            int n = ((m & 0x3FF) << 10 | paramString.charAt(++k) & 0x3FF) + 65536;
            this.buf[(j++)] = ((byte)(0xF0 | n >> 18));
            this.buf[(j++)] = ((byte)(0x80 | n >> 12 & 0x3F));
            this.buf[(j++)] = ((byte)(0x80 | n >> 6 & 0x3F));
            this.buf[(j++)] = ((byte)(128 + (n & 0x3F)));
            continue;
          }
          this.buf[(j++)] = ((byte)(224 + (m >> 12)));
          this.buf[(j++)] = ((byte)(128 + (m >> 6 & 0x3F)));
        }
        else
        {
          this.buf[(j++)] = ((byte)(192 + (m >> 6)));
        }
        this.buf[(j++)] = ((byte)(128 + (m & 0x3F)));
      }
      else
      {
        this.buf[(j++)] = ((byte)m);
      }
    }
    this.len = j;
  }
  
  public final void setEscape(String paramString, boolean paramBoolean)
  {
    int i = paramString.length();
    ensureSize(i * 6 + 1);
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      int m = paramString.charAt(k);
      int n = j;
      if (m > 127)
      {
        if (m > 2047)
        {
          if ((55296 <= m) && (m <= 57343))
          {
            int i1 = ((m & 0x3FF) << 10 | paramString.charAt(++k) & 0x3FF) + 65536;
            this.buf[(j++)] = ((byte)(0xF0 | i1 >> 18));
            this.buf[(j++)] = ((byte)(0x80 | i1 >> 12 & 0x3F));
            this.buf[(j++)] = ((byte)(0x80 | i1 >> 6 & 0x3F));
            this.buf[(j++)] = ((byte)(128 + (i1 & 0x3F)));
            continue;
          }
          this.buf[(n++)] = ((byte)(224 + (m >> 12)));
          this.buf[(n++)] = ((byte)(128 + (m >> 6 & 0x3F)));
        }
        else
        {
          this.buf[(n++)] = ((byte)(192 + (m >> 6)));
        }
        this.buf[(n++)] = ((byte)(128 + (m & 0x3F)));
      }
      else
      {
        byte[] arrayOfByte;
        if ((arrayOfByte = attributeEntities[m]) != null)
        {
          if ((paramBoolean) || (entities[m] != null)) {
            n = writeEntity(arrayOfByte, n);
          } else {
            this.buf[(n++)] = ((byte)m);
          }
        }
        else {
          this.buf[(n++)] = ((byte)m);
        }
      }
      j = n;
    }
    this.len = j;
  }
  
  private int writeEntity(byte[] paramArrayOfByte, int paramInt)
  {
    System.arraycopy(paramArrayOfByte, 0, this.buf, paramInt, paramArrayOfByte.length);
    return paramInt + paramArrayOfByte.length;
  }
  
  public final void write(UTF8XmlOutput paramUTF8XmlOutput)
    throws IOException
  {
    paramUTF8XmlOutput.write(this.buf, 0, this.len);
  }
  
  public void append(char paramChar)
  {
    this.buf[(this.len++)] = ((byte)paramChar);
  }
  
  public void compact()
  {
    byte[] arrayOfByte = new byte[this.len];
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.len);
    this.buf = arrayOfByte;
  }
  
  private static void add(char paramChar, String paramString, boolean paramBoolean)
  {
    byte[] arrayOfByte = UTF8XmlOutput.toBytes(paramString);
    attributeEntities[paramChar] = arrayOfByte;
    if (!paramBoolean) {
      entities[paramChar] = arrayOfByte;
    }
  }
  
  static
  {
    add('&', "&amp;", false);
    add('<', "&lt;", false);
    add('>', "&gt;", false);
    add('"', "&quot;", true);
    add('\t', "&#x9;", true);
    add('\r', "&#xD;", false);
    add('\n', "&#xA;", true);
  }
}
