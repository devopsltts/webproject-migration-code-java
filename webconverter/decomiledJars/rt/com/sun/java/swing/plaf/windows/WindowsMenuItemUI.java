package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import sun.swing.SwingUtilities2;

public class WindowsMenuItemUI
  extends BasicMenuItemUI
{
  final WindowsMenuItemUIAccessor accessor = new WindowsMenuItemUIAccessor()
  {
    public JMenuItem getMenuItem()
    {
      return WindowsMenuItemUI.this.menuItem;
    }
    
    public TMSchema.State getState(JMenuItem paramAnonymousJMenuItem)
    {
      return WindowsMenuItemUI.getState(this, paramAnonymousJMenuItem);
    }
    
    public TMSchema.Part getPart(JMenuItem paramAnonymousJMenuItem)
    {
      return WindowsMenuItemUI.getPart(this, paramAnonymousJMenuItem);
    }
  };
  
  public WindowsMenuItemUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsMenuItemUI();
  }
  
  protected void paintText(Graphics paramGraphics, JMenuItem paramJMenuItem, Rectangle paramRectangle, String paramString)
  {
    if (isVistaPainting())
    {
      paintText(this.accessor, paramGraphics, paramJMenuItem, paramRectangle, paramString);
      return;
    }
    ButtonModel localButtonModel = paramJMenuItem.getModel();
    Color localColor = paramGraphics.getColor();
    if ((localButtonModel.isEnabled()) && ((localButtonModel.isArmed()) || (((paramJMenuItem instanceof JMenu)) && (localButtonModel.isSelected())))) {
      paramGraphics.setColor(this.selectionForeground);
    }
    WindowsGraphicsUtils.paintText(paramGraphics, paramJMenuItem, paramRectangle, paramString, 0);
    paramGraphics.setColor(localColor);
  }
  
  protected void paintBackground(Graphics paramGraphics, JMenuItem paramJMenuItem, Color paramColor)
  {
    if (isVistaPainting())
    {
      paintBackground(this.accessor, paramGraphics, paramJMenuItem, paramColor);
      return;
    }
    super.paintBackground(paramGraphics, paramJMenuItem, paramColor);
  }
  
  static void paintBackground(WindowsMenuItemUIAccessor paramWindowsMenuItemUIAccessor, Graphics paramGraphics, JMenuItem paramJMenuItem, Color paramColor)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    assert (isVistaPainting(localXPStyle));
    if (isVistaPainting(localXPStyle))
    {
      int i = paramJMenuItem.getWidth();
      int j = paramJMenuItem.getHeight();
      if (paramJMenuItem.isOpaque())
      {
        localObject = paramGraphics.getColor();
        paramGraphics.setColor(paramJMenuItem.getBackground());
        paramGraphics.fillRect(0, 0, i, j);
        paramGraphics.setColor((Color)localObject);
      }
      Object localObject = paramWindowsMenuItemUIAccessor.getPart(paramJMenuItem);
      XPStyle.Skin localSkin = localXPStyle.getSkin(paramJMenuItem, (TMSchema.Part)localObject);
      localSkin.paintSkin(paramGraphics, 0, 0, i, j, paramWindowsMenuItemUIAccessor.getState(paramJMenuItem));
    }
  }
  
  static void paintText(WindowsMenuItemUIAccessor paramWindowsMenuItemUIAccessor, Graphics paramGraphics, JMenuItem paramJMenuItem, Rectangle paramRectangle, String paramString)
  {
    assert (isVistaPainting());
    if (isVistaPainting())
    {
      TMSchema.State localState = paramWindowsMenuItemUIAccessor.getState(paramJMenuItem);
      FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(paramJMenuItem, paramGraphics);
      int i = paramJMenuItem.getDisplayedMnemonicIndex();
      if (WindowsLookAndFeel.isMnemonicHidden() == true) {
        i = -1;
      }
      WindowsGraphicsUtils.paintXPText(paramJMenuItem, paramWindowsMenuItemUIAccessor.getPart(paramJMenuItem), localState, paramGraphics, paramRectangle.x, paramRectangle.y + localFontMetrics.getAscent(), paramString, i);
    }
  }
  
  static TMSchema.State getState(WindowsMenuItemUIAccessor paramWindowsMenuItemUIAccessor, JMenuItem paramJMenuItem)
  {
    ButtonModel localButtonModel = paramJMenuItem.getModel();
    TMSchema.State localState;
    if (localButtonModel.isArmed()) {
      localState = localButtonModel.isEnabled() ? TMSchema.State.HOT : TMSchema.State.DISABLEDHOT;
    } else {
      localState = localButtonModel.isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED;
    }
    return localState;
  }
  
  static TMSchema.Part getPart(WindowsMenuItemUIAccessor paramWindowsMenuItemUIAccessor, JMenuItem paramJMenuItem)
  {
    return TMSchema.Part.MP_POPUPITEM;
  }
  
  static boolean isVistaPainting(XPStyle paramXPStyle)
  {
    return (paramXPStyle != null) && (paramXPStyle.isSkinDefined(null, TMSchema.Part.MP_POPUPITEM));
  }
  
  static boolean isVistaPainting()
  {
    return isVistaPainting(XPStyle.getXP());
  }
}
