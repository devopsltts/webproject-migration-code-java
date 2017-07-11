package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import sun.awt.AppContext;

public class MotifButtonUI
  extends BasicButtonUI
{
  protected Color selectColor;
  private boolean defaults_initialized = false;
  private static final Object MOTIF_BUTTON_UI_KEY = new Object();
  
  public MotifButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    MotifButtonUI localMotifButtonUI = (MotifButtonUI)localAppContext.get(MOTIF_BUTTON_UI_KEY);
    if (localMotifButtonUI == null)
    {
      localMotifButtonUI = new MotifButtonUI();
      localAppContext.put(MOTIF_BUTTON_UI_KEY, localMotifButtonUI);
    }
    return localMotifButtonUI;
  }
  
  protected BasicButtonListener createButtonListener(AbstractButton paramAbstractButton)
  {
    return new MotifButtonListener(paramAbstractButton);
  }
  
  public void installDefaults(AbstractButton paramAbstractButton)
  {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized)
    {
      this.selectColor = UIManager.getColor(getPropertyPrefix() + "select");
      this.defaults_initialized = true;
    }
    LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.FALSE);
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
  
  protected Color getSelectColor()
  {
    return this.selectColor;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    fillContentArea(paramGraphics, (AbstractButton)paramJComponent, paramJComponent.getBackground());
    super.paint(paramGraphics, paramJComponent);
  }
  
  protected void paintIcon(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
  {
    Shape localShape = paramGraphics.getClip();
    Rectangle localRectangle1 = AbstractBorder.getInteriorRectangle(paramJComponent, paramJComponent.getBorder(), 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    Rectangle localRectangle2 = localShape.getBounds();
    localRectangle1 = SwingUtilities.computeIntersection(localRectangle2.x, localRectangle2.y, localRectangle2.width, localRectangle2.height, localRectangle1);
    paramGraphics.setClip(localRectangle1);
    super.paintIcon(paramGraphics, paramJComponent, paramRectangle);
    paramGraphics.setClip(localShape);
  }
  
  protected void paintFocus(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3) {}
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton)
  {
    fillContentArea(paramGraphics, paramAbstractButton, this.selectColor);
  }
  
  protected void fillContentArea(Graphics paramGraphics, AbstractButton paramAbstractButton, Color paramColor)
  {
    if (paramAbstractButton.isContentAreaFilled())
    {
      Insets localInsets1 = paramAbstractButton.getMargin();
      Insets localInsets2 = paramAbstractButton.getInsets();
      Dimension localDimension = paramAbstractButton.getSize();
      paramGraphics.setColor(paramColor);
      paramGraphics.fillRect(localInsets2.left - localInsets1.left, localInsets2.top - localInsets1.top, localDimension.width - (localInsets2.left - localInsets1.left) - (localInsets2.right - localInsets1.right), localDimension.height - (localInsets2.top - localInsets1.top) - (localInsets2.bottom - localInsets1.bottom));
    }
  }
}
