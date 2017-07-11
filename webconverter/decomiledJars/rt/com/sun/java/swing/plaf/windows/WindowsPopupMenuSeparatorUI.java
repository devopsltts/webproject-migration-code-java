package com.sun.java.swing.plaf.windows;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;

public class WindowsPopupMenuSeparatorUI
  extends BasicPopupMenuSeparatorUI
{
  public WindowsPopupMenuSeparatorUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsPopupMenuSeparatorUI();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    Dimension localDimension = paramJComponent.getSize();
    XPStyle localXPStyle = XPStyle.getXP();
    int i;
    if (WindowsMenuItemUI.isVistaPainting(localXPStyle))
    {
      i = 1;
      Container localContainer = paramJComponent.getParent();
      if ((localContainer instanceof JComponent))
      {
        localObject = ((JComponent)localContainer).getClientProperty(WindowsPopupMenuUI.GUTTER_OFFSET_KEY);
        if ((localObject instanceof Integer))
        {
          i = ((Integer)localObject).intValue() - paramJComponent.getX();
          i += WindowsPopupMenuUI.getGutterWidth();
        }
      }
      Object localObject = localXPStyle.getSkin(paramJComponent, TMSchema.Part.MP_POPUPSEPARATOR);
      int j = ((XPStyle.Skin)localObject).getHeight();
      int k = (localDimension.height - j) / 2;
      ((XPStyle.Skin)localObject).paintSkin(paramGraphics, i, k, localDimension.width - i - 1, j, TMSchema.State.NORMAL);
    }
    else
    {
      i = localDimension.height / 2;
      paramGraphics.setColor(paramJComponent.getForeground());
      paramGraphics.drawLine(1, i - 1, localDimension.width - 2, i - 1);
      paramGraphics.setColor(paramJComponent.getBackground());
      paramGraphics.drawLine(1, i, localDimension.width - 2, i);
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    int i = 0;
    Font localFont = paramJComponent.getFont();
    if (localFont != null) {
      i = paramJComponent.getFontMetrics(localFont).getHeight();
    }
    return new Dimension(0, i / 2 + 2);
  }
}
