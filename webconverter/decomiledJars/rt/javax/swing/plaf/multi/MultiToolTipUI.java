package javax.swing.plaf.multi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolTipUI;

public class MultiToolTipUI
  extends ToolTipUI
{
  protected Vector uis = new Vector();
  
  public MultiToolTipUI() {}
  
  public ComponentUI[] getUIs()
  {
    return MultiLookAndFeel.uisToArray(this.uis);
  }
  
  public boolean contains(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    boolean bool = ((ComponentUI)this.uis.elementAt(0)).contains(paramJComponent, paramInt1, paramInt2);
    for (int i = 1; i < this.uis.size(); i++) {
      ((ComponentUI)this.uis.elementAt(i)).contains(paramJComponent, paramInt1, paramInt2);
    }
    return bool;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    for (int i = 0; i < this.uis.size(); i++) {
      ((ComponentUI)this.uis.elementAt(i)).update(paramGraphics, paramJComponent);
    }
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    MultiToolTipUI localMultiToolTipUI = new MultiToolTipUI();
    return MultiLookAndFeel.createUIs(localMultiToolTipUI, ((MultiToolTipUI)localMultiToolTipUI).uis, paramJComponent);
  }
  
  public void installUI(JComponent paramJComponent)
  {
    for (int i = 0; i < this.uis.size(); i++) {
      ((ComponentUI)this.uis.elementAt(i)).installUI(paramJComponent);
    }
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    for (int i = 0; i < this.uis.size(); i++) {
      ((ComponentUI)this.uis.elementAt(i)).uninstallUI(paramJComponent);
    }
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    for (int i = 0; i < this.uis.size(); i++) {
      ((ComponentUI)this.uis.elementAt(i)).paint(paramGraphics, paramJComponent);
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Dimension localDimension = ((ComponentUI)this.uis.elementAt(0)).getPreferredSize(paramJComponent);
    for (int i = 1; i < this.uis.size(); i++) {
      ((ComponentUI)this.uis.elementAt(i)).getPreferredSize(paramJComponent);
    }
    return localDimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    Dimension localDimension = ((ComponentUI)this.uis.elementAt(0)).getMinimumSize(paramJComponent);
    for (int i = 1; i < this.uis.size(); i++) {
      ((ComponentUI)this.uis.elementAt(i)).getMinimumSize(paramJComponent);
    }
    return localDimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    Dimension localDimension = ((ComponentUI)this.uis.elementAt(0)).getMaximumSize(paramJComponent);
    for (int i = 1; i < this.uis.size(); i++) {
      ((ComponentUI)this.uis.elementAt(i)).getMaximumSize(paramJComponent);
    }
    return localDimension;
  }
  
  public int getAccessibleChildrenCount(JComponent paramJComponent)
  {
    int i = ((ComponentUI)this.uis.elementAt(0)).getAccessibleChildrenCount(paramJComponent);
    for (int j = 1; j < this.uis.size(); j++) {
      ((ComponentUI)this.uis.elementAt(j)).getAccessibleChildrenCount(paramJComponent);
    }
    return i;
  }
  
  public Accessible getAccessibleChild(JComponent paramJComponent, int paramInt)
  {
    Accessible localAccessible = ((ComponentUI)this.uis.elementAt(0)).getAccessibleChild(paramJComponent, paramInt);
    for (int i = 1; i < this.uis.size(); i++) {
      ((ComponentUI)this.uis.elementAt(i)).getAccessibleChild(paramJComponent, paramInt);
    }
    return localAccessible;
  }
}
