package javax.swing.text.html;

import java.io.Serializable;
import javax.swing.text.AttributeSet;

public class Option
  implements Serializable
{
  private boolean selected;
  private String label;
  private AttributeSet attr;
  
  public Option(AttributeSet paramAttributeSet)
  {
    this.attr = paramAttributeSet.copyAttributes();
    this.selected = (paramAttributeSet.getAttribute(HTML.Attribute.SELECTED) != null);
  }
  
  public void setLabel(String paramString)
  {
    this.label = paramString;
  }
  
  public String getLabel()
  {
    return this.label;
  }
  
  public AttributeSet getAttributes()
  {
    return this.attr;
  }
  
  public String toString()
  {
    return this.label;
  }
  
  protected void setSelection(boolean paramBoolean)
  {
    this.selected = paramBoolean;
  }
  
  public boolean isSelected()
  {
    return this.selected;
  }
  
  public String getValue()
  {
    String str = (String)this.attr.getAttribute(HTML.Attribute.VALUE);
    if (str == null) {
      str = this.label;
    }
    return str;
  }
}
