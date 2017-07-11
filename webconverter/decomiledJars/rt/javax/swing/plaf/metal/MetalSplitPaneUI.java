package javax.swing.plaf.metal;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class MetalSplitPaneUI
  extends BasicSplitPaneUI
{
  public MetalSplitPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalSplitPaneUI();
  }
  
  public BasicSplitPaneDivider createDefaultDivider()
  {
    return new MetalSplitPaneDivider(this);
  }
}
