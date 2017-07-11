package java.text;

import java.util.ArrayList;

final class MergeCollation
{
  ArrayList<PatternEntry> patterns = new ArrayList();
  private transient PatternEntry saveEntry = null;
  private transient PatternEntry lastEntry = null;
  private transient StringBuffer excess = new StringBuffer();
  private transient byte[] statusArray = new byte[' '];
  private final byte BITARRAYMASK = 1;
  private final int BYTEPOWER = 3;
  private final int BYTEMASK = 7;
  
  public MergeCollation(String paramString)
    throws ParseException
  {
    for (int i = 0; i < this.statusArray.length; i++) {
      this.statusArray[i] = 0;
    }
    setPattern(paramString);
  }
  
  public String getPattern()
  {
    return getPattern(true);
  }
  
  public String getPattern(boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    PatternEntry localPatternEntry1 = null;
    ArrayList localArrayList = null;
    PatternEntry localPatternEntry2;
    for (int i = 0; i < this.patterns.size(); i++)
    {
      localPatternEntry2 = (PatternEntry)this.patterns.get(i);
      if (localPatternEntry2.extension.length() != 0)
      {
        if (localArrayList == null) {
          localArrayList = new ArrayList();
        }
        localArrayList.add(localPatternEntry2);
      }
      else
      {
        if (localArrayList != null)
        {
          PatternEntry localPatternEntry3 = findLastWithNoExtension(i - 1);
          for (int k = localArrayList.size() - 1; k >= 0; k--)
          {
            localPatternEntry1 = (PatternEntry)localArrayList.get(k);
            localPatternEntry1.addToBuffer(localStringBuffer, false, paramBoolean, localPatternEntry3);
          }
          localArrayList = null;
        }
        localPatternEntry2.addToBuffer(localStringBuffer, false, paramBoolean, null);
      }
    }
    if (localArrayList != null)
    {
      localPatternEntry2 = findLastWithNoExtension(i - 1);
      for (int j = localArrayList.size() - 1; j >= 0; j--)
      {
        localPatternEntry1 = (PatternEntry)localArrayList.get(j);
        localPatternEntry1.addToBuffer(localStringBuffer, false, paramBoolean, localPatternEntry2);
      }
      localArrayList = null;
    }
    return localStringBuffer.toString();
  }
  
  private final PatternEntry findLastWithNoExtension(int paramInt)
  {
    
    while (paramInt >= 0)
    {
      PatternEntry localPatternEntry = (PatternEntry)this.patterns.get(paramInt);
      if (localPatternEntry.extension.length() == 0) {
        return localPatternEntry;
      }
      paramInt--;
    }
    return null;
  }
  
  public String emitPattern()
  {
    return emitPattern(true);
  }
  
  public String emitPattern(boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < this.patterns.size(); i++)
    {
      PatternEntry localPatternEntry = (PatternEntry)this.patterns.get(i);
      if (localPatternEntry != null) {
        localPatternEntry.addToBuffer(localStringBuffer, true, paramBoolean, null);
      }
    }
    return localStringBuffer.toString();
  }
  
  public void setPattern(String paramString)
    throws ParseException
  {
    this.patterns.clear();
    addPattern(paramString);
  }
  
  public void addPattern(String paramString)
    throws ParseException
  {
    if (paramString == null) {
      return;
    }
    PatternEntry.Parser localParser = new PatternEntry.Parser(paramString);
    for (PatternEntry localPatternEntry = localParser.next(); localPatternEntry != null; localPatternEntry = localParser.next()) {
      fixEntry(localPatternEntry);
    }
  }
  
  public int getCount()
  {
    return this.patterns.size();
  }
  
  public PatternEntry getItemAt(int paramInt)
  {
    return (PatternEntry)this.patterns.get(paramInt);
  }
  
  private final void fixEntry(PatternEntry paramPatternEntry)
    throws ParseException
  {
    if ((this.lastEntry != null) && (paramPatternEntry.chars.equals(this.lastEntry.chars)) && (paramPatternEntry.extension.equals(this.lastEntry.extension)))
    {
      if ((paramPatternEntry.strength != 3) && (paramPatternEntry.strength != -2)) {
        throw new ParseException("The entries " + this.lastEntry + " and " + paramPatternEntry + " are adjacent in the rules, but have conflicting " + "strengths: A character can't be unequal to itself.", -1);
      }
      return;
    }
    int i = 1;
    if (paramPatternEntry.strength != -2)
    {
      int j = -1;
      if (paramPatternEntry.chars.length() == 1)
      {
        k = paramPatternEntry.chars.charAt(0);
        int m = k >> 3;
        int n = this.statusArray[m];
        int i1 = (byte)(1 << (k & 0x7));
        if ((n != 0) && ((n & i1) != 0)) {
          j = this.patterns.lastIndexOf(paramPatternEntry);
        } else {
          this.statusArray[m] = ((byte)(n | i1));
        }
      }
      else
      {
        j = this.patterns.lastIndexOf(paramPatternEntry);
      }
      if (j != -1) {
        this.patterns.remove(j);
      }
      this.excess.setLength(0);
      int k = findLastEntry(this.lastEntry, this.excess);
      if (this.excess.length() != 0)
      {
        paramPatternEntry.extension = (this.excess + paramPatternEntry.extension);
        if (k != this.patterns.size())
        {
          this.lastEntry = this.saveEntry;
          i = 0;
        }
      }
      if (k == this.patterns.size())
      {
        this.patterns.add(paramPatternEntry);
        this.saveEntry = paramPatternEntry;
      }
      else
      {
        this.patterns.add(k, paramPatternEntry);
      }
    }
    if (i != 0) {
      this.lastEntry = paramPatternEntry;
    }
  }
  
  private final int findLastEntry(PatternEntry paramPatternEntry, StringBuffer paramStringBuffer)
    throws ParseException
  {
    if (paramPatternEntry == null) {
      return 0;
    }
    if (paramPatternEntry.strength != -2)
    {
      i = -1;
      if (paramPatternEntry.chars.length() == 1)
      {
        int j = paramPatternEntry.chars.charAt(0) >> '\003';
        if ((this.statusArray[j] & '\001' << (paramPatternEntry.chars.charAt(0) & 0x7)) != 0) {
          i = this.patterns.lastIndexOf(paramPatternEntry);
        }
      }
      else
      {
        i = this.patterns.lastIndexOf(paramPatternEntry);
      }
      if (i == -1) {
        throw new ParseException("couldn't find last entry: " + paramPatternEntry, i);
      }
      return i + 1;
    }
    for (int i = this.patterns.size() - 1; i >= 0; i--)
    {
      PatternEntry localPatternEntry = (PatternEntry)this.patterns.get(i);
      if (localPatternEntry.chars.regionMatches(0, paramPatternEntry.chars, 0, localPatternEntry.chars.length()))
      {
        paramStringBuffer.append(paramPatternEntry.chars.substring(localPatternEntry.chars.length(), paramPatternEntry.chars.length()));
        break;
      }
    }
    if (i == -1) {
      throw new ParseException("couldn't find: " + paramPatternEntry, i);
    }
    return i + 1;
  }
}
