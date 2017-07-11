package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class WindowsSplitPaneDivider
  extends BasicSplitPaneDivider
{
  public WindowsSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI)
  {
    super(paramBasicSplitPaneUI);
  }
  
  public void paint(Graphics paramGraphics)
  {
    Color localColor = this.splitPane.hasFocus() ? UIManager.getColor("SplitPane.shadow") : getBackground();
    Dimension localDimension = getSize();
    if (localColor != null)
    {
      paramGraphics.setColor(localColor);
      paramGraphics.fillRect(0, 0, localDimension.width, localDimension.height);
    }
    super.paint(paramGraphics);
  }
}
