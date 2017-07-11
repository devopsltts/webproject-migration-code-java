package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import sun.awt.AppContext;

public class WindowsToggleButtonUI
  extends BasicToggleButtonUI
{
  protected int dashedRectGapX;
  protected int dashedRectGapY;
  protected int dashedRectGapWidth;
  protected int dashedRectGapHeight;
  protected Color focusColor;
  private static final Object WINDOWS_TOGGLE_BUTTON_UI_KEY = new Object();
  private boolean defaults_initialized = false;
  private transient Color cachedSelectedColor = null;
  private transient Color cachedBackgroundColor = null;
  private transient Color cachedHighlightColor = null;
  
  public WindowsToggleButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    WindowsToggleButtonUI localWindowsToggleButtonUI = (WindowsToggleButtonUI)localAppContext.get(WINDOWS_TOGGLE_BUTTON_UI_KEY);
    if (localWindowsToggleButtonUI == null)
    {
      localWindowsToggleButtonUI = new WindowsToggleButtonUI();
      localAppContext.put(WINDOWS_TOGGLE_BUTTON_UI_KEY, localWindowsToggleButtonUI);
    }
    return localWindowsToggleButtonUI;
  }
  
  protected void installDefaults(AbstractButton paramAbstractButton)
  {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized)
    {
      localObject = getPropertyPrefix();
      this.dashedRectGapX = ((Integer)UIManager.get("Button.dashedRectGapX")).intValue();
      this.dashedRectGapY = ((Integer)UIManager.get("Button.dashedRectGapY")).intValue();
      this.dashedRectGapWidth = ((Integer)UIManager.get("Button.dashedRectGapWidth")).intValue();
      this.dashedRectGapHeight = ((Integer)UIManager.get("Button.dashedRectGapHeight")).intValue();
      this.focusColor = UIManager.getColor((String)localObject + "focus");
      this.defaults_initialized = true;
    }
    Object localObject = XPStyle.getXP();
    if (localObject != null)
    {
      paramAbstractButton.setBorder(((XPStyle)localObject).getBorder(paramAbstractButton, WindowsButtonUI.getXPButtonType(paramAbstractButton)));
      LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.FALSE);
      LookAndFeel.installProperty(paramAbstractButton, "rolloverEnabled", Boolean.TRUE);
    }
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
  
  protected Color getFocusColor()
  {
    return this.focusColor;
  }
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton)
  {
    if ((XPStyle.getXP() == null) && (paramAbstractButton.isContentAreaFilled()))
    {
      Color localColor1 = paramGraphics.getColor();
      Color localColor2 = paramAbstractButton.getBackground();
      Color localColor3 = UIManager.getColor("ToggleButton.highlight");
      if ((localColor2 != this.cachedBackgroundColor) || (localColor3 != this.cachedHighlightColor))
      {
        int i = localColor2.getRed();
        int j = localColor3.getRed();
        int k = localColor2.getGreen();
        int m = localColor3.getGreen();
        int n = localColor2.getBlue();
        int i1 = localColor3.getBlue();
        this.cachedSelectedColor = new Color(Math.min(i, j) + Math.abs(i - j) / 2, Math.min(k, m) + Math.abs(k - m) / 2, Math.min(n, i1) + Math.abs(n - i1) / 2);
        this.cachedBackgroundColor = localColor2;
        this.cachedHighlightColor = localColor3;
      }
      paramGraphics.setColor(this.cachedSelectedColor);
      paramGraphics.fillRect(0, 0, paramAbstractButton.getWidth(), paramAbstractButton.getHeight());
      paramGraphics.setColor(localColor1);
    }
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (XPStyle.getXP() != null) {
      WindowsButtonUI.paintXPButtonBackground(paramGraphics, paramJComponent);
    }
    super.paint(paramGraphics, paramJComponent);
  }
  
  protected void paintText(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle, String paramString)
  {
    WindowsGraphicsUtils.paintText(paramGraphics, paramAbstractButton, paramRectangle, paramString, getTextShiftOffset());
  }
  
  protected void paintFocus(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3)
  {
    paramGraphics.setColor(getFocusColor());
    BasicGraphicsUtils.drawDashedRect(paramGraphics, this.dashedRectGapX, this.dashedRectGapY, paramAbstractButton.getWidth() - this.dashedRectGapWidth, paramAbstractButton.getHeight() - this.dashedRectGapHeight);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Dimension localDimension = super.getPreferredSize(paramJComponent);
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    if ((localDimension != null) && (localAbstractButton.isFocusPainted()))
    {
      if (localDimension.width % 2 == 0) {
        localDimension.width += 1;
      }
      if (localDimension.height % 2 == 0) {
        localDimension.height += 1;
      }
    }
    return localDimension;
  }
}
