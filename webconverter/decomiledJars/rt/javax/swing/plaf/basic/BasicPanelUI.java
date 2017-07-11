package javax.swing.plaf.basic;

import java.awt.Component.BaselineResizeBehavior;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;

public class BasicPanelUI
  extends PanelUI
{
  private static PanelUI panelUI;
  
  public BasicPanelUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    if (panelUI == null) {
      panelUI = new BasicPanelUI();
    }
    return panelUI;
  }
  
  public void installUI(JComponent paramJComponent)
  {
    JPanel localJPanel = (JPanel)paramJComponent;
    super.installUI(localJPanel);
    installDefaults(localJPanel);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    JPanel localJPanel = (JPanel)paramJComponent;
    uninstallDefaults(localJPanel);
    super.uninstallUI(paramJComponent);
  }
  
  protected void installDefaults(JPanel paramJPanel)
  {
    LookAndFeel.installColorsAndFont(paramJPanel, "Panel.background", "Panel.foreground", "Panel.font");
    LookAndFeel.installBorder(paramJPanel, "Panel.border");
    LookAndFeel.installProperty(paramJPanel, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults(JPanel paramJPanel)
  {
    LookAndFeel.uninstallBorder(paramJPanel);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    Border localBorder = paramJComponent.getBorder();
    if ((localBorder instanceof AbstractBorder)) {
      return ((AbstractBorder)localBorder).getBaseline(paramJComponent, paramInt1, paramInt2);
    }
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    Border localBorder = paramJComponent.getBorder();
    if ((localBorder instanceof AbstractBorder)) {
      return ((AbstractBorder)localBorder).getBaselineResizeBehavior(paramJComponent);
    }
    return Component.BaselineResizeBehavior.OTHER;
  }
}
