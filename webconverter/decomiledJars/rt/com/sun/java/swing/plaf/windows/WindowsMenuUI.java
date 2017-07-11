package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;
import javax.swing.plaf.basic.BasicMenuUI.MouseInputHandler;

public class WindowsMenuUI
  extends BasicMenuUI
{
  protected Integer menuBarHeight;
  protected boolean hotTrackingOn;
  final WindowsMenuItemUIAccessor accessor = new WindowsMenuItemUIAccessor()
  {
    public JMenuItem getMenuItem()
    {
      return WindowsMenuUI.this.menuItem;
    }
    
    public TMSchema.State getState(JMenuItem paramAnonymousJMenuItem)
    {
      Object localObject1 = paramAnonymousJMenuItem.isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED;
      ButtonModel localButtonModel = paramAnonymousJMenuItem.getModel();
      if ((localButtonModel.isArmed()) || (localButtonModel.isSelected()))
      {
        localObject1 = paramAnonymousJMenuItem.isEnabled() ? TMSchema.State.PUSHED : TMSchema.State.DISABLEDPUSHED;
      }
      else if ((localButtonModel.isRollover()) && (((JMenu)paramAnonymousJMenuItem).isTopLevelMenu()))
      {
        Object localObject2 = localObject1;
        localObject1 = paramAnonymousJMenuItem.isEnabled() ? TMSchema.State.HOT : TMSchema.State.DISABLEDHOT;
        for (MenuElement localMenuElement : ((JMenuBar)paramAnonymousJMenuItem.getParent()).getSubElements()) {
          if (((JMenuItem)localMenuElement).isSelected())
          {
            localObject1 = localObject2;
            break;
          }
        }
      }
      if (!((JMenu)paramAnonymousJMenuItem).isTopLevelMenu()) {
        if (localObject1 == TMSchema.State.PUSHED) {
          localObject1 = TMSchema.State.HOT;
        } else if (localObject1 == TMSchema.State.DISABLEDPUSHED) {
          localObject1 = TMSchema.State.DISABLEDHOT;
        }
      }
      if ((((JMenu)paramAnonymousJMenuItem).isTopLevelMenu()) && (WindowsMenuItemUI.isVistaPainting()) && (!WindowsMenuBarUI.isActive(paramAnonymousJMenuItem))) {
        localObject1 = TMSchema.State.DISABLED;
      }
      return localObject1;
    }
    
    public TMSchema.Part getPart(JMenuItem paramAnonymousJMenuItem)
    {
      return ((JMenu)paramAnonymousJMenuItem).isTopLevelMenu() ? TMSchema.Part.MP_BARITEM : TMSchema.Part.MP_POPUPITEM;
    }
  };
  
  public WindowsMenuUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsMenuUI();
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    if (!WindowsLookAndFeel.isClassicWindows()) {
      this.menuItem.setRolloverEnabled(true);
    }
    this.menuBarHeight = Integer.valueOf(UIManager.getInt("MenuBar.height"));
    Object localObject = UIManager.get("MenuBar.rolloverEnabled");
    this.hotTrackingOn = ((localObject instanceof Boolean) ? ((Boolean)localObject).booleanValue() : true);
  }
  
  protected void paintBackground(Graphics paramGraphics, JMenuItem paramJMenuItem, Color paramColor)
  {
    if (WindowsMenuItemUI.isVistaPainting())
    {
      WindowsMenuItemUI.paintBackground(this.accessor, paramGraphics, paramJMenuItem, paramColor);
      return;
    }
    JMenu localJMenu = (JMenu)paramJMenuItem;
    ButtonModel localButtonModel = localJMenu.getModel();
    if ((WindowsLookAndFeel.isClassicWindows()) || (!localJMenu.isTopLevelMenu()) || ((XPStyle.getXP() != null) && ((localButtonModel.isArmed()) || (localButtonModel.isSelected()))))
    {
      super.paintBackground(paramGraphics, localJMenu, paramColor);
      return;
    }
    Color localColor1 = paramGraphics.getColor();
    int i = localJMenu.getWidth();
    int j = localJMenu.getHeight();
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    Color localColor2 = localUIDefaults.getColor("controlLtHighlight");
    Color localColor3 = localUIDefaults.getColor("controlShadow");
    paramGraphics.setColor(localJMenu.getBackground());
    paramGraphics.fillRect(0, 0, i, j);
    if (localJMenu.isOpaque()) {
      if ((localButtonModel.isArmed()) || (localButtonModel.isSelected()))
      {
        paramGraphics.setColor(localColor3);
        paramGraphics.drawLine(0, 0, i - 1, 0);
        paramGraphics.drawLine(0, 0, 0, j - 2);
        paramGraphics.setColor(localColor2);
        paramGraphics.drawLine(i - 1, 0, i - 1, j - 2);
        paramGraphics.drawLine(0, j - 2, i - 1, j - 2);
      }
      else if ((localButtonModel.isRollover()) && (localButtonModel.isEnabled()))
      {
        int k = 0;
        MenuElement[] arrayOfMenuElement = ((JMenuBar)localJMenu.getParent()).getSubElements();
        for (int m = 0; m < arrayOfMenuElement.length; m++) {
          if (((JMenuItem)arrayOfMenuElement[m]).isSelected())
          {
            k = 1;
            break;
          }
        }
        if (k == 0) {
          if (XPStyle.getXP() != null)
          {
            paramGraphics.setColor(this.selectionBackground);
            paramGraphics.fillRect(0, 0, i, j);
          }
          else
          {
            paramGraphics.setColor(localColor2);
            paramGraphics.drawLine(0, 0, i - 1, 0);
            paramGraphics.drawLine(0, 0, 0, j - 2);
            paramGraphics.setColor(localColor3);
            paramGraphics.drawLine(i - 1, 0, i - 1, j - 2);
            paramGraphics.drawLine(0, j - 2, i - 1, j - 2);
          }
        }
      }
    }
    paramGraphics.setColor(localColor1);
  }
  
  protected void paintText(Graphics paramGraphics, JMenuItem paramJMenuItem, Rectangle paramRectangle, String paramString)
  {
    if (WindowsMenuItemUI.isVistaPainting())
    {
      WindowsMenuItemUI.paintText(this.accessor, paramGraphics, paramJMenuItem, paramRectangle, paramString);
      return;
    }
    JMenu localJMenu = (JMenu)paramJMenuItem;
    ButtonModel localButtonModel = paramJMenuItem.getModel();
    Color localColor = paramGraphics.getColor();
    boolean bool = localButtonModel.isRollover();
    if ((bool) && (localJMenu.isTopLevelMenu()))
    {
      MenuElement[] arrayOfMenuElement = ((JMenuBar)localJMenu.getParent()).getSubElements();
      for (int i = 0; i < arrayOfMenuElement.length; i++) {
        if (((JMenuItem)arrayOfMenuElement[i]).isSelected())
        {
          bool = false;
          break;
        }
      }
    }
    if (((localButtonModel.isSelected()) && ((WindowsLookAndFeel.isClassicWindows()) || (!localJMenu.isTopLevelMenu()))) || ((XPStyle.getXP() != null) && ((bool) || (localButtonModel.isArmed()) || (localButtonModel.isSelected())))) {
      paramGraphics.setColor(this.selectionForeground);
    }
    WindowsGraphicsUtils.paintText(paramGraphics, paramJMenuItem, paramRectangle, paramString, 0);
    paramGraphics.setColor(localColor);
  }
  
  protected MouseInputListener createMouseInputListener(JComponent paramJComponent)
  {
    return new WindowsMouseInputHandler();
  }
  
  protected Dimension getPreferredMenuItemSize(JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, int paramInt)
  {
    Dimension localDimension = super.getPreferredMenuItemSize(paramJComponent, paramIcon1, paramIcon2, paramInt);
    if (((paramJComponent instanceof JMenu)) && (((JMenu)paramJComponent).isTopLevelMenu()) && (this.menuBarHeight != null) && (localDimension.height < this.menuBarHeight.intValue())) {
      localDimension.height = this.menuBarHeight.intValue();
    }
    return localDimension;
  }
  
  protected class WindowsMouseInputHandler
    extends BasicMenuUI.MouseInputHandler
  {
    protected WindowsMouseInputHandler()
    {
      super();
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      super.mouseEntered(paramMouseEvent);
      JMenu localJMenu = (JMenu)paramMouseEvent.getSource();
      if ((WindowsMenuUI.this.hotTrackingOn) && (localJMenu.isTopLevelMenu()) && (localJMenu.isRolloverEnabled()))
      {
        localJMenu.getModel().setRollover(true);
        WindowsMenuUI.this.menuItem.repaint();
      }
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      super.mouseExited(paramMouseEvent);
      JMenu localJMenu = (JMenu)paramMouseEvent.getSource();
      ButtonModel localButtonModel = localJMenu.getModel();
      if (localJMenu.isRolloverEnabled())
      {
        localButtonModel.setRollover(false);
        WindowsMenuUI.this.menuItem.repaint();
      }
    }
  }
}
