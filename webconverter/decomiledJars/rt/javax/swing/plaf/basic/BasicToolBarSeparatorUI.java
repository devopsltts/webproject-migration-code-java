package javax.swing.plaf.basic;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar.Separator;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

public class BasicToolBarSeparatorUI
  extends BasicSeparatorUI
{
  public BasicToolBarSeparatorUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicToolBarSeparatorUI();
  }
  
  protected void installDefaults(JSeparator paramJSeparator)
  {
    Dimension localDimension = ((JToolBar.Separator)paramJSeparator).getSeparatorSize();
    if ((localDimension == null) || ((localDimension instanceof UIResource)))
    {
      JToolBar.Separator localSeparator = (JToolBar.Separator)paramJSeparator;
      localDimension = (Dimension)UIManager.get("ToolBar.separatorSize");
      if (localDimension != null)
      {
        if (localSeparator.getOrientation() == 0) {
          localDimension = new Dimension(localDimension.height, localDimension.width);
        }
        localSeparator.setSeparatorSize(localDimension);
      }
    }
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {}
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Dimension localDimension = ((JToolBar.Separator)paramJComponent).getSeparatorSize();
    if (localDimension != null) {
      return localDimension.getSize();
    }
    return null;
  }
}
