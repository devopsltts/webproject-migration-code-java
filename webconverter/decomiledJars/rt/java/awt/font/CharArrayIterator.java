package java.awt.font;

import java.text.CharacterIterator;

class CharArrayIterator
  implements CharacterIterator
{
  private char[] chars;
  private int pos;
  private int begin;
  
  CharArrayIterator(char[] paramArrayOfChar)
  {
    reset(paramArrayOfChar, 0);
  }
  
  CharArrayIterator(char[] paramArrayOfChar, int paramInt)
  {
    reset(paramArrayOfChar, paramInt);
  }
  
  public char first()
  {
    this.pos = 0;
    return current();
  }
  
  public char last()
  {
    if (this.chars.length > 0) {
      this.pos = (this.chars.length - 1);
    } else {
      this.pos = 0;
    }
    return current();
  }
  
  public char current()
  {
    if ((this.pos >= 0) && (this.pos < this.chars.length)) {
      return this.chars[this.pos];
    }
    return 65535;
  }
  
  public char next()
  {
    if (this.pos < this.chars.length - 1)
    {
      this.pos += 1;
      return this.chars[this.pos];
    }
    this.pos = this.chars.length;
    return 65535;
  }
  
  public char previous()
  {
    if (this.pos > 0)
    {
      this.pos -= 1;
      return this.chars[this.pos];
    }
    this.pos = 0;
    return 65535;
  }
  
  public char setIndex(int paramInt)
  {
    paramInt -= this.begin;
    if ((paramInt < 0) || (paramInt > this.chars.length)) {
      throw new IllegalArgumentException("Invalid index");
    }
    this.pos = paramInt;
    return current();
  }
  
  public int getBeginIndex()
  {
    return this.begin;
  }
  
  public int getEndIndex()
  {
    return this.begin + this.chars.length;
  }
  
  public int getIndex()
  {
    return this.begin + this.pos;
  }
  
  public Object clone()
  {
    CharArrayIterator localCharArrayIterator = new CharArrayIterator(this.chars, this.begin);
    localCharArrayIterator.pos = this.pos;
    return localCharArrayIterator;
  }
  
  void reset(char[] paramArrayOfChar)
  {
    reset(paramArrayOfChar, 0);
  }
  
  void reset(char[] paramArrayOfChar, int paramInt)
  {
    this.chars = paramArrayOfChar;
    this.begin = paramInt;
    this.pos = 0;
  }
}
