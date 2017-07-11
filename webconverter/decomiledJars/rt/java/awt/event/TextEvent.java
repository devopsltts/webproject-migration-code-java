package java.awt.event;

import java.awt.AWTEvent;

public class TextEvent
  extends AWTEvent
{
  public static final int TEXT_FIRST = 900;
  public static final int TEXT_LAST = 900;
  public static final int TEXT_VALUE_CHANGED = 900;
  private static final long serialVersionUID = 6269902291250941179L;
  
  public TextEvent(Object paramObject, int paramInt)
  {
    super(paramObject, paramInt);
  }
  
  public String paramString()
  {
    String str;
    switch (this.id)
    {
    case 900: 
      str = "TEXT_VALUE_CHANGED";
      break;
    default: 
      str = "unknown type";
    }
    return str;
  }
}
