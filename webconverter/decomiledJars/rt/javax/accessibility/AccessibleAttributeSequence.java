package javax.accessibility;

import javax.swing.text.AttributeSet;

public class AccessibleAttributeSequence
{
  public int startIndex;
  public int endIndex;
  public AttributeSet attributes;
  
  public AccessibleAttributeSequence(int paramInt1, int paramInt2, AttributeSet paramAttributeSet)
  {
    this.startIndex = paramInt1;
    this.endIndex = paramInt2;
    this.attributes = paramAttributeSet;
  }
}
