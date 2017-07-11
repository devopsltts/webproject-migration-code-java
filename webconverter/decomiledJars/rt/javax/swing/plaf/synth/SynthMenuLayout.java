package javax.swing.plaf.synth;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JPopupMenu;
import javax.swing.plaf.basic.DefaultMenuLayout;

class SynthMenuLayout
  extends DefaultMenuLayout
{
  public SynthMenuLayout(Container paramContainer, int paramInt)
  {
    super(paramContainer, paramInt);
  }
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    if ((paramContainer instanceof JPopupMenu))
    {
      JPopupMenu localJPopupMenu = (JPopupMenu)paramContainer;
      localJPopupMenu.putClientProperty(SynthMenuItemLayoutHelper.MAX_ACC_OR_ARROW_WIDTH, null);
    }
    return super.preferredLayoutSize(paramContainer);
  }
}
