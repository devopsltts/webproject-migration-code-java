package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class MetalPopupMenuSeparatorUI
  extends MetalSeparatorUI
{
  public MetalPopupMenuSeparatorUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalPopupMenuSeparatorUI();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    Dimension localDimension = paramJComponent.getSize();
    paramGraphics.setColor(paramJComponent.getForeground());
    paramGraphics.drawLine(0, 1, localDimension.width, 1);
    paramGraphics.setColor(paramJComponent.getBackground());
    paramGraphics.drawLine(0, 2, localDimension.width, 2);
    paramGraphics.drawLine(0, 0, 0, 0);
    paramGraphics.drawLine(0, 3, 0, 3);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return new Dimension(0, 4);
  }
}
