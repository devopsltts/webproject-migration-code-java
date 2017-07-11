package java.awt;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

public final class ComponentOrientation
  implements Serializable
{
  private static final long serialVersionUID = -4113291392143563828L;
  private static final int UNK_BIT = 1;
  private static final int HORIZ_BIT = 2;
  private static final int LTR_BIT = 4;
  public static final ComponentOrientation LEFT_TO_RIGHT = new ComponentOrientation(6);
  public static final ComponentOrientation RIGHT_TO_LEFT = new ComponentOrientation(2);
  public static final ComponentOrientation UNKNOWN = new ComponentOrientation(7);
  private int orientation;
  
  public boolean isHorizontal()
  {
    return (this.orientation & 0x2) != 0;
  }
  
  public boolean isLeftToRight()
  {
    return (this.orientation & 0x4) != 0;
  }
  
  public static ComponentOrientation getOrientation(Locale paramLocale)
  {
    String str = paramLocale.getLanguage();
    if (("iw".equals(str)) || ("ar".equals(str)) || ("fa".equals(str)) || ("ur".equals(str))) {
      return RIGHT_TO_LEFT;
    }
    return LEFT_TO_RIGHT;
  }
  
  @Deprecated
  public static ComponentOrientation getOrientation(ResourceBundle paramResourceBundle)
  {
    ComponentOrientation localComponentOrientation = null;
    try
    {
      localComponentOrientation = (ComponentOrientation)paramResourceBundle.getObject("Orientation");
    }
    catch (Exception localException) {}
    if (localComponentOrientation == null) {
      localComponentOrientation = getOrientation(paramResourceBundle.getLocale());
    }
    if (localComponentOrientation == null) {
      localComponentOrientation = getOrientation(Locale.getDefault());
    }
    return localComponentOrientation;
  }
  
  private ComponentOrientation(int paramInt)
  {
    this.orientation = paramInt;
  }
}
