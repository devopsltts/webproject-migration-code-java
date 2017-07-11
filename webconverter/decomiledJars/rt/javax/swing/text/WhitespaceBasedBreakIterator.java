package javax.swing.text;

import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;

class WhitespaceBasedBreakIterator
  extends BreakIterator
{
  private char[] text = new char[0];
  private int[] breaks = { 0 };
  private int pos = 0;
  
  WhitespaceBasedBreakIterator() {}
  
  public void setText(CharacterIterator paramCharacterIterator)
  {
    int i = paramCharacterIterator.getBeginIndex();
    this.text = new char[paramCharacterIterator.getEndIndex() - i];
    int[] arrayOfInt = new int[this.text.length + 1];
    int j = 0;
    arrayOfInt[(j++)] = i;
    int k = 0;
    int m = 0;
    int i1;
    for (int n = paramCharacterIterator.first(); n != 65535; i1 = paramCharacterIterator.next())
    {
      this.text[k] = n;
      boolean bool = Character.isWhitespace(n);
      if ((m != 0) && (!bool)) {
        arrayOfInt[(j++)] = (k + i);
      }
      m = bool;
      k++;
    }
    if (this.text.length > 0) {
      arrayOfInt[(j++)] = (this.text.length + i);
    }
    System.arraycopy(arrayOfInt, 0, this.breaks = new int[j], 0, j);
  }
  
  public CharacterIterator getText()
  {
    return new StringCharacterIterator(new String(this.text));
  }
  
  public int first()
  {
    return this.breaks[(this.pos = 0)];
  }
  
  public int last()
  {
    return this.breaks[(this.pos = this.breaks.length - 1)];
  }
  
  public int current()
  {
    return this.breaks[this.pos];
  }
  
  public int next()
  {
    return this.pos == this.breaks.length - 1 ? -1 : this.breaks[(++this.pos)];
  }
  
  public int previous()
  {
    return this.pos == 0 ? -1 : this.breaks[(--this.pos)];
  }
  
  public int next(int paramInt)
  {
    return checkhit(this.pos + paramInt);
  }
  
  public int following(int paramInt)
  {
    return adjacent(paramInt, 1);
  }
  
  public int preceding(int paramInt)
  {
    return adjacent(paramInt, -1);
  }
  
  private int checkhit(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.breaks.length)) {
      return -1;
    }
    return this.breaks[(this.pos = paramInt)];
  }
  
  private int adjacent(int paramInt1, int paramInt2)
  {
    int i = Arrays.binarySearch(this.breaks, paramInt1);
    int j = i < 0 ? -2 : paramInt2 < 0 ? -1 : 0;
    return checkhit(Math.abs(i) + paramInt2 + j);
  }
}
