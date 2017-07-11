package java.text;

import java.util.ArrayList;

class CharacterIteratorFieldDelegate
  implements Format.FieldDelegate
{
  private ArrayList<AttributedString> attributedStrings = new ArrayList();
  private int size;
  
  CharacterIteratorFieldDelegate() {}
  
  public void formatted(Format.Field paramField, Object paramObject, int paramInt1, int paramInt2, StringBuffer paramStringBuffer)
  {
    if (paramInt1 != paramInt2)
    {
      int i;
      if (paramInt1 < this.size)
      {
        i = this.size;
        int j = this.attributedStrings.size() - 1;
        while (paramInt1 < i)
        {
          AttributedString localAttributedString2 = (AttributedString)this.attributedStrings.get(j--);
          int k = i - localAttributedString2.length();
          int m = Math.max(0, paramInt1 - k);
          localAttributedString2.addAttribute(paramField, paramObject, m, Math.min(paramInt2 - paramInt1, localAttributedString2.length() - m) + m);
          i = k;
        }
      }
      if (this.size < paramInt1)
      {
        this.attributedStrings.add(new AttributedString(paramStringBuffer.substring(this.size, paramInt1)));
        this.size = paramInt1;
      }
      if (this.size < paramInt2)
      {
        i = Math.max(paramInt1, this.size);
        AttributedString localAttributedString1 = new AttributedString(paramStringBuffer.substring(i, paramInt2));
        localAttributedString1.addAttribute(paramField, paramObject);
        this.attributedStrings.add(localAttributedString1);
        this.size = paramInt2;
      }
    }
  }
  
  public void formatted(int paramInt1, Format.Field paramField, Object paramObject, int paramInt2, int paramInt3, StringBuffer paramStringBuffer)
  {
    formatted(paramField, paramObject, paramInt2, paramInt3, paramStringBuffer);
  }
  
  public AttributedCharacterIterator getIterator(String paramString)
  {
    if (paramString.length() > this.size)
    {
      this.attributedStrings.add(new AttributedString(paramString.substring(this.size)));
      this.size = paramString.length();
    }
    int i = this.attributedStrings.size();
    AttributedCharacterIterator[] arrayOfAttributedCharacterIterator = new AttributedCharacterIterator[i];
    for (int j = 0; j < i; j++) {
      arrayOfAttributedCharacterIterator[j] = ((AttributedString)this.attributedStrings.get(j)).getIterator();
    }
    return new AttributedString(arrayOfAttributedCharacterIterator).getIterator();
  }
}
