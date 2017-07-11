package javax.swing.plaf.basic;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class BasicPopupMenuSeparatorUI
  extends BasicSeparatorUI
{
  public BasicPopupMenuSeparatorUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicPopupMenuSeparatorUI();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    Dimension localDimension = paramJComponent.getSize();
    paramGraphics.setColor(paramJComponent.getForeground());
    paramGraphics.drawLine(0, 0, localDimension.width, 0);
    paramGraphics.setColor(paramJComponent.getBackground());
    paramGraphics.drawLine(0, 1, localDimension.width, 1);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return new Dimension(0, 2);
  }
}
