package java.awt.event;

import java.awt.AWTEvent;
import java.awt.ItemSelectable;

public class ItemEvent
  extends AWTEvent
{
  public static final int ITEM_FIRST = 701;
  public static final int ITEM_LAST = 701;
  public static final int ITEM_STATE_CHANGED = 701;
  public static final int SELECTED = 1;
  public static final int DESELECTED = 2;
  Object item;
  int stateChange;
  private static final long serialVersionUID = -608708132447206933L;
  
  public ItemEvent(ItemSelectable paramItemSelectable, int paramInt1, Object paramObject, int paramInt2)
  {
    super(paramItemSelectable, paramInt1);
    this.item = paramObject;
    this.stateChange = paramInt2;
  }
  
  public ItemSelectable getItemSelectable()
  {
    return (ItemSelectable)this.source;
  }
  
  public Object getItem()
  {
    return this.item;
  }
  
  public int getStateChange()
  {
    return this.stateChange;
  }
  
  public String paramString()
  {
    String str1;
    switch (this.id)
    {
    case 701: 
      str1 = "ITEM_STATE_CHANGED";
      break;
    default: 
      str1 = "unknown type";
    }
    String str2;
    switch (this.stateChange)
    {
    case 1: 
      str2 = "SELECTED";
      break;
    case 2: 
      str2 = "DESELECTED";
      break;
    default: 
      str2 = "unknown type";
    }
    return str1 + ",item=" + this.item + ",stateChange=" + str2;
  }
}
