package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import sun.swing.MenuItemCheckIconFactory;

public class WindowsIconFactory
  implements Serializable
{
  private static Icon frame_closeIcon;
  private static Icon frame_iconifyIcon;
  private static Icon frame_maxIcon;
  private static Icon frame_minIcon;
  private static Icon frame_resizeIcon;
  private static Icon checkBoxIcon;
  private static Icon radioButtonIcon;
  private static Icon checkBoxMenuItemIcon;
  private static Icon radioButtonMenuItemIcon;
  private static Icon menuItemCheckIcon;
  private static Icon menuItemArrowIcon;
  private static Icon menuArrowIcon;
  private static VistaMenuItemCheckIconFactory menuItemCheckIconFactory;
  
  public WindowsIconFactory() {}
  
  public static Icon getMenuItemCheckIcon()
  {
    if (menuItemCheckIcon == null) {
      menuItemCheckIcon = new MenuItemCheckIcon(null);
    }
    return menuItemCheckIcon;
  }
  
  public static Icon getMenuItemArrowIcon()
  {
    if (menuItemArrowIcon == null) {
      menuItemArrowIcon = new MenuItemArrowIcon(null);
    }
    return menuItemArrowIcon;
  }
  
  public static Icon getMenuArrowIcon()
  {
    if (menuArrowIcon == null) {
      menuArrowIcon = new MenuArrowIcon(null);
    }
    return menuArrowIcon;
  }
  
  public static Icon getCheckBoxIcon()
  {
    if (checkBoxIcon == null) {
      checkBoxIcon = new CheckBoxIcon(null);
    }
    return checkBoxIcon;
  }
  
  public static Icon getRadioButtonIcon()
  {
    if (radioButtonIcon == null) {
      radioButtonIcon = new RadioButtonIcon(null);
    }
    return radioButtonIcon;
  }
  
  public static Icon getCheckBoxMenuItemIcon()
  {
    if (checkBoxMenuItemIcon == null) {
      checkBoxMenuItemIcon = new CheckBoxMenuItemIcon(null);
    }
    return checkBoxMenuItemIcon;
  }
  
  public static Icon getRadioButtonMenuItemIcon()
  {
    if (radioButtonMenuItemIcon == null) {
      radioButtonMenuItemIcon = new RadioButtonMenuItemIcon(null);
    }
    return radioButtonMenuItemIcon;
  }
  
  static synchronized VistaMenuItemCheckIconFactory getMenuItemCheckIconFactory()
  {
    if (menuItemCheckIconFactory == null) {
      menuItemCheckIconFactory = new VistaMenuItemCheckIconFactory();
    }
    return menuItemCheckIconFactory;
  }
  
  public static Icon createFrameCloseIcon()
  {
    if (frame_closeIcon == null) {
      frame_closeIcon = new FrameButtonIcon(TMSchema.Part.WP_CLOSEBUTTON, null);
    }
    return frame_closeIcon;
  }
  
  public static Icon createFrameIconifyIcon()
  {
    if (frame_iconifyIcon == null) {
      frame_iconifyIcon = new FrameButtonIcon(TMSchema.Part.WP_MINBUTTON, null);
    }
    return frame_iconifyIcon;
  }
  
  public static Icon createFrameMaximizeIcon()
  {
    if (frame_maxIcon == null) {
      frame_maxIcon = new FrameButtonIcon(TMSchema.Part.WP_MAXBUTTON, null);
    }
    return frame_maxIcon;
  }
  
  public static Icon createFrameMinimizeIcon()
  {
    if (frame_minIcon == null) {
      frame_minIcon = new FrameButtonIcon(TMSchema.Part.WP_RESTOREBUTTON, null);
    }
    return frame_minIcon;
  }
  
  public static Icon createFrameResizeIcon()
  {
    if (frame_resizeIcon == null) {
      frame_resizeIcon = new ResizeIcon(null);
    }
    return frame_resizeIcon;
  }
  
  private static class CheckBoxIcon
    implements Icon, Serializable
  {
    static final int csize = 13;
    
    private CheckBoxIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      JCheckBox localJCheckBox = (JCheckBox)paramComponent;
      ButtonModel localButtonModel = localJCheckBox.getModel();
      XPStyle localXPStyle = XPStyle.getXP();
      if (localXPStyle != null)
      {
        TMSchema.State localState;
        if (localButtonModel.isSelected())
        {
          localState = TMSchema.State.CHECKEDNORMAL;
          if (!localButtonModel.isEnabled()) {
            localState = TMSchema.State.CHECKEDDISABLED;
          } else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed())) {
            localState = TMSchema.State.CHECKEDPRESSED;
          } else if (localButtonModel.isRollover()) {
            localState = TMSchema.State.CHECKEDHOT;
          }
        }
        else
        {
          localState = TMSchema.State.UNCHECKEDNORMAL;
          if (!localButtonModel.isEnabled()) {
            localState = TMSchema.State.UNCHECKEDDISABLED;
          } else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed())) {
            localState = TMSchema.State.UNCHECKEDPRESSED;
          } else if (localButtonModel.isRollover()) {
            localState = TMSchema.State.UNCHECKEDHOT;
          }
        }
        TMSchema.Part localPart = TMSchema.Part.BP_CHECKBOX;
        localXPStyle.getSkin(paramComponent, localPart).paintSkin(paramGraphics, paramInt1, paramInt2, localState);
      }
      else
      {
        if (!localJCheckBox.isBorderPaintedFlat())
        {
          paramGraphics.setColor(UIManager.getColor("CheckBox.shadow"));
          paramGraphics.drawLine(paramInt1, paramInt2, paramInt1 + 11, paramInt2);
          paramGraphics.drawLine(paramInt1, paramInt2 + 1, paramInt1, paramInt2 + 11);
          paramGraphics.setColor(UIManager.getColor("CheckBox.highlight"));
          paramGraphics.drawLine(paramInt1 + 12, paramInt2, paramInt1 + 12, paramInt2 + 12);
          paramGraphics.drawLine(paramInt1, paramInt2 + 12, paramInt1 + 11, paramInt2 + 12);
          paramGraphics.setColor(UIManager.getColor("CheckBox.darkShadow"));
          paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 1, paramInt1 + 10, paramInt2 + 1);
          paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 2, paramInt1 + 1, paramInt2 + 10);
          paramGraphics.setColor(UIManager.getColor("CheckBox.light"));
          paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 11, paramInt1 + 11, paramInt2 + 11);
          paramGraphics.drawLine(paramInt1 + 11, paramInt2 + 1, paramInt1 + 11, paramInt2 + 10);
          if (((localButtonModel.isPressed()) && (localButtonModel.isArmed())) || (!localButtonModel.isEnabled())) {
            paramGraphics.setColor(UIManager.getColor("CheckBox.background"));
          } else {
            paramGraphics.setColor(UIManager.getColor("CheckBox.interiorBackground"));
          }
          paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 2, 9, 9);
        }
        else
        {
          paramGraphics.setColor(UIManager.getColor("CheckBox.shadow"));
          paramGraphics.drawRect(paramInt1 + 1, paramInt2 + 1, 10, 10);
          if (((localButtonModel.isPressed()) && (localButtonModel.isArmed())) || (!localButtonModel.isEnabled())) {
            paramGraphics.setColor(UIManager.getColor("CheckBox.background"));
          } else {
            paramGraphics.setColor(UIManager.getColor("CheckBox.interiorBackground"));
          }
          paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 2, 9, 9);
        }
        if (localButtonModel.isEnabled()) {
          paramGraphics.setColor(UIManager.getColor("CheckBox.foreground"));
        } else {
          paramGraphics.setColor(UIManager.getColor("CheckBox.shadow"));
        }
        if (localButtonModel.isSelected())
        {
          paramGraphics.drawLine(paramInt1 + 9, paramInt2 + 3, paramInt1 + 9, paramInt2 + 3);
          paramGraphics.drawLine(paramInt1 + 8, paramInt2 + 4, paramInt1 + 9, paramInt2 + 4);
          paramGraphics.drawLine(paramInt1 + 7, paramInt2 + 5, paramInt1 + 9, paramInt2 + 5);
          paramGraphics.drawLine(paramInt1 + 6, paramInt2 + 6, paramInt1 + 8, paramInt2 + 6);
          paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 7, paramInt1 + 7, paramInt2 + 7);
          paramGraphics.drawLine(paramInt1 + 4, paramInt2 + 8, paramInt1 + 6, paramInt2 + 8);
          paramGraphics.drawLine(paramInt1 + 5, paramInt2 + 9, paramInt1 + 5, paramInt2 + 9);
          paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 5, paramInt1 + 3, paramInt2 + 5);
          paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 6, paramInt1 + 4, paramInt2 + 6);
        }
      }
    }
    
    public int getIconWidth()
    {
      XPStyle localXPStyle = XPStyle.getXP();
      if (localXPStyle != null) {
        return localXPStyle.getSkin(null, TMSchema.Part.BP_CHECKBOX).getWidth();
      }
      return 13;
    }
    
    public int getIconHeight()
    {
      XPStyle localXPStyle = XPStyle.getXP();
      if (localXPStyle != null) {
        return localXPStyle.getSkin(null, TMSchema.Part.BP_CHECKBOX).getHeight();
      }
      return 13;
    }
  }
  
  private static class CheckBoxMenuItemIcon
    implements Icon, UIResource, Serializable
  {
    private CheckBoxMenuItemIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      boolean bool = localButtonModel.isSelected();
      if (bool)
      {
        paramInt2 -= getIconHeight() / 2;
        paramGraphics.drawLine(paramInt1 + 9, paramInt2 + 3, paramInt1 + 9, paramInt2 + 3);
        paramGraphics.drawLine(paramInt1 + 8, paramInt2 + 4, paramInt1 + 9, paramInt2 + 4);
        paramGraphics.drawLine(paramInt1 + 7, paramInt2 + 5, paramInt1 + 9, paramInt2 + 5);
        paramGraphics.drawLine(paramInt1 + 6, paramInt2 + 6, paramInt1 + 8, paramInt2 + 6);
        paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 7, paramInt1 + 7, paramInt2 + 7);
        paramGraphics.drawLine(paramInt1 + 4, paramInt2 + 8, paramInt1 + 6, paramInt2 + 8);
        paramGraphics.drawLine(paramInt1 + 5, paramInt2 + 9, paramInt1 + 5, paramInt2 + 9);
        paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 5, paramInt1 + 3, paramInt2 + 5);
        paramGraphics.drawLine(paramInt1 + 3, paramInt2 + 6, paramInt1 + 4, paramInt2 + 6);
      }
    }
    
    public int getIconWidth()
    {
      return 9;
    }
    
    public int getIconHeight()
    {
      return 9;
    }
  }
  
  private static class FrameButtonIcon
    implements Icon, Serializable
  {
    private TMSchema.Part part;
    
    private FrameButtonIcon(TMSchema.Part paramPart)
    {
      this.part = paramPart;
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      int i = getIconWidth();
      int j = getIconHeight();
      XPStyle localXPStyle = XPStyle.getXP();
      int i2;
      if (localXPStyle != null)
      {
        XPStyle.Skin localSkin = localXPStyle.getSkin(paramComponent, this.part);
        AbstractButton localAbstractButton = (AbstractButton)paramComponent;
        ButtonModel localButtonModel = localAbstractButton.getModel();
        JInternalFrame localJInternalFrame = (JInternalFrame)SwingUtilities.getAncestorOfClass(JInternalFrame.class, localAbstractButton);
        i2 = (localJInternalFrame != null) && (localJInternalFrame.isSelected()) ? 1 : 0;
        TMSchema.State localState;
        if (i2 != 0)
        {
          if (!localButtonModel.isEnabled()) {
            localState = TMSchema.State.DISABLED;
          } else if ((localButtonModel.isArmed()) && (localButtonModel.isPressed())) {
            localState = TMSchema.State.PUSHED;
          } else if (localButtonModel.isRollover()) {
            localState = TMSchema.State.HOT;
          } else {
            localState = TMSchema.State.NORMAL;
          }
        }
        else if (!localButtonModel.isEnabled()) {
          localState = TMSchema.State.INACTIVEDISABLED;
        } else if ((localButtonModel.isArmed()) && (localButtonModel.isPressed())) {
          localState = TMSchema.State.INACTIVEPUSHED;
        } else if (localButtonModel.isRollover()) {
          localState = TMSchema.State.INACTIVEHOT;
        } else {
          localState = TMSchema.State.INACTIVENORMAL;
        }
        localSkin.paintSkin(paramGraphics, 0, 0, i, j, localState);
      }
      else
      {
        paramGraphics.setColor(Color.black);
        int k = i / 12 + 2;
        int m = j / 5;
        int n = j - m * 2 - 1;
        int i1 = i * 3 / 4 - 3;
        i2 = Math.max(j / 8, 2);
        int i3 = Math.max(i / 15, 1);
        if (this.part == TMSchema.Part.WP_CLOSEBUTTON)
        {
          int i4;
          if (i > 47) {
            i4 = 6;
          } else if (i > 37) {
            i4 = 5;
          } else if (i > 26) {
            i4 = 4;
          } else if (i > 16) {
            i4 = 3;
          } else if (i > 12) {
            i4 = 2;
          } else {
            i4 = 1;
          }
          m = j / 12 + 2;
          if (i4 == 1)
          {
            if (i1 % 2 == 1)
            {
              k++;
              i1++;
            }
            paramGraphics.drawLine(k, m, k + i1 - 2, m + i1 - 2);
            paramGraphics.drawLine(k + i1 - 2, m, k, m + i1 - 2);
          }
          else if (i4 == 2)
          {
            if (i1 > 6)
            {
              k++;
              i1--;
            }
            paramGraphics.drawLine(k, m, k + i1 - 2, m + i1 - 2);
            paramGraphics.drawLine(k + i1 - 2, m, k, m + i1 - 2);
            paramGraphics.drawLine(k + 1, m, k + i1 - 1, m + i1 - 2);
            paramGraphics.drawLine(k + i1 - 1, m, k + 1, m + i1 - 2);
          }
          else
          {
            k += 2;
            m++;
            i1 -= 2;
            paramGraphics.drawLine(k, m, k + i1 - 1, m + i1 - 1);
            paramGraphics.drawLine(k + i1 - 1, m, k, m + i1 - 1);
            paramGraphics.drawLine(k + 1, m, k + i1 - 1, m + i1 - 2);
            paramGraphics.drawLine(k + i1 - 2, m, k, m + i1 - 2);
            paramGraphics.drawLine(k, m + 1, k + i1 - 2, m + i1 - 1);
            paramGraphics.drawLine(k + i1 - 1, m + 1, k + 1, m + i1 - 1);
            for (int i5 = 4; i5 <= i4; i5++)
            {
              paramGraphics.drawLine(k + i5 - 2, m, k + i1 - 1, m + i1 - i5 + 1);
              paramGraphics.drawLine(k, m + i5 - 2, k + i1 - i5 + 1, m + i1 - 1);
              paramGraphics.drawLine(k + i1 - i5 + 1, m, k, m + i1 - i5 + 1);
              paramGraphics.drawLine(k + i1 - 1, m + i5 - 2, k + i5 - 2, m + i1 - 1);
            }
          }
        }
        else if (this.part == TMSchema.Part.WP_MINBUTTON)
        {
          paramGraphics.fillRect(k, m + n - i2, i1 - i1 / 3, i2);
        }
        else if (this.part == TMSchema.Part.WP_MAXBUTTON)
        {
          paramGraphics.fillRect(k, m, i1, i2);
          paramGraphics.fillRect(k, m, i3, n);
          paramGraphics.fillRect(k + i1 - i3, m, i3, n);
          paramGraphics.fillRect(k, m + n - i3, i1, i3);
        }
        else if (this.part == TMSchema.Part.WP_RESTOREBUTTON)
        {
          paramGraphics.fillRect(k + i1 / 3, m, i1 - i1 / 3, i2);
          paramGraphics.fillRect(k + i1 / 3, m, i3, n / 3);
          paramGraphics.fillRect(k + i1 - i3, m, i3, n - n / 3);
          paramGraphics.fillRect(k + i1 - i1 / 3, m + n - n / 3 - i3, i1 / 3, i3);
          paramGraphics.fillRect(k, m + n / 3, i1 - i1 / 3, i2);
          paramGraphics.fillRect(k, m + n / 3, i3, n - n / 3);
          paramGraphics.fillRect(k + i1 - i1 / 3 - i3, m + n / 3, i3, n - n / 3);
          paramGraphics.fillRect(k, m + n - i3, i1 - i1 / 3, i3);
        }
      }
    }
    
    public int getIconWidth()
    {
      int i;
      if (XPStyle.getXP() != null)
      {
        i = UIManager.getInt("InternalFrame.titleButtonHeight") - 2;
        Dimension localDimension = XPStyle.getPartSize(TMSchema.Part.WP_CLOSEBUTTON, TMSchema.State.NORMAL);
        if ((localDimension != null) && (localDimension.width != 0) && (localDimension.height != 0)) {
          i = (int)(i * localDimension.width / localDimension.height);
        }
      }
      else
      {
        i = UIManager.getInt("InternalFrame.titleButtonWidth") - 2;
      }
      if (XPStyle.getXP() != null) {
        i -= 2;
      }
      return i;
    }
    
    public int getIconHeight()
    {
      int i = UIManager.getInt("InternalFrame.titleButtonHeight") - 4;
      return i;
    }
  }
  
  private static class MenuArrowIcon
    implements Icon, UIResource, Serializable
  {
    private MenuArrowIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      XPStyle localXPStyle = XPStyle.getXP();
      if (WindowsMenuItemUI.isVistaPainting(localXPStyle))
      {
        TMSchema.State localState = TMSchema.State.NORMAL;
        if ((paramComponent instanceof JMenuItem)) {
          localState = ((JMenuItem)paramComponent).getModel().isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED;
        }
        XPStyle.Skin localSkin = localXPStyle.getSkin(paramComponent, TMSchema.Part.MP_POPUPSUBMENU);
        if (WindowsGraphicsUtils.isLeftToRight(paramComponent))
        {
          localSkin.paintSkin(paramGraphics, paramInt1, paramInt2, localState);
        }
        else
        {
          Graphics2D localGraphics2D = (Graphics2D)paramGraphics.create();
          localGraphics2D.translate(paramInt1 + localSkin.getWidth(), paramInt2);
          localGraphics2D.scale(-1.0D, 1.0D);
          localSkin.paintSkin(localGraphics2D, 0, 0, localState);
          localGraphics2D.dispose();
        }
      }
      else
      {
        paramGraphics.translate(paramInt1, paramInt2);
        if (WindowsGraphicsUtils.isLeftToRight(paramComponent))
        {
          paramGraphics.drawLine(0, 0, 0, 7);
          paramGraphics.drawLine(1, 1, 1, 6);
          paramGraphics.drawLine(2, 2, 2, 5);
          paramGraphics.drawLine(3, 3, 3, 4);
        }
        else
        {
          paramGraphics.drawLine(4, 0, 4, 7);
          paramGraphics.drawLine(3, 1, 3, 6);
          paramGraphics.drawLine(2, 2, 2, 5);
          paramGraphics.drawLine(1, 3, 1, 4);
        }
        paramGraphics.translate(-paramInt1, -paramInt2);
      }
    }
    
    public int getIconWidth()
    {
      XPStyle localXPStyle = XPStyle.getXP();
      if (WindowsMenuItemUI.isVistaPainting(localXPStyle))
      {
        XPStyle.Skin localSkin = localXPStyle.getSkin(null, TMSchema.Part.MP_POPUPSUBMENU);
        return localSkin.getWidth();
      }
      return 4;
    }
    
    public int getIconHeight()
    {
      XPStyle localXPStyle = XPStyle.getXP();
      if (WindowsMenuItemUI.isVistaPainting(localXPStyle))
      {
        XPStyle.Skin localSkin = localXPStyle.getSkin(null, TMSchema.Part.MP_POPUPSUBMENU);
        return localSkin.getHeight();
      }
      return 8;
    }
  }
  
  private static class MenuItemArrowIcon
    implements Icon, UIResource, Serializable
  {
    private MenuItemArrowIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return 4;
    }
    
    public int getIconHeight()
    {
      return 8;
    }
  }
  
  private static class MenuItemCheckIcon
    implements Icon, UIResource, Serializable
  {
    private MenuItemCheckIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return 9;
    }
    
    public int getIconHeight()
    {
      return 9;
    }
  }
  
  private static class RadioButtonIcon
    implements Icon, UIResource, Serializable
  {
    private RadioButtonIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      XPStyle localXPStyle = XPStyle.getXP();
      if (localXPStyle != null)
      {
        TMSchema.Part localPart = TMSchema.Part.BP_RADIOBUTTON;
        XPStyle.Skin localSkin = localXPStyle.getSkin(localAbstractButton, localPart);
        int i = 0;
        TMSchema.State localState;
        if (localButtonModel.isSelected())
        {
          localState = TMSchema.State.CHECKEDNORMAL;
          if (!localButtonModel.isEnabled()) {
            localState = TMSchema.State.CHECKEDDISABLED;
          } else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed())) {
            localState = TMSchema.State.CHECKEDPRESSED;
          } else if (localButtonModel.isRollover()) {
            localState = TMSchema.State.CHECKEDHOT;
          }
        }
        else
        {
          localState = TMSchema.State.UNCHECKEDNORMAL;
          if (!localButtonModel.isEnabled()) {
            localState = TMSchema.State.UNCHECKEDDISABLED;
          } else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed())) {
            localState = TMSchema.State.UNCHECKEDPRESSED;
          } else if (localButtonModel.isRollover()) {
            localState = TMSchema.State.UNCHECKEDHOT;
          }
        }
        localSkin.paintSkin(paramGraphics, paramInt1, paramInt2, localState);
      }
      else
      {
        if (((localButtonModel.isPressed()) && (localButtonModel.isArmed())) || (!localButtonModel.isEnabled())) {
          paramGraphics.setColor(UIManager.getColor("RadioButton.background"));
        } else {
          paramGraphics.setColor(UIManager.getColor("RadioButton.interiorBackground"));
        }
        paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 2, 8, 8);
        paramGraphics.setColor(UIManager.getColor("RadioButton.shadow"));
        paramGraphics.drawLine(paramInt1 + 4, paramInt2 + 0, paramInt1 + 7, paramInt2 + 0);
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 1, paramInt1 + 3, paramInt2 + 1);
        paramGraphics.drawLine(paramInt1 + 8, paramInt2 + 1, paramInt1 + 9, paramInt2 + 1);
        paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 2, paramInt1 + 1, paramInt2 + 3);
        paramGraphics.drawLine(paramInt1 + 0, paramInt2 + 4, paramInt1 + 0, paramInt2 + 7);
        paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 8, paramInt1 + 1, paramInt2 + 9);
        paramGraphics.setColor(UIManager.getColor("RadioButton.highlight"));
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 10, paramInt1 + 3, paramInt2 + 10);
        paramGraphics.drawLine(paramInt1 + 4, paramInt2 + 11, paramInt1 + 7, paramInt2 + 11);
        paramGraphics.drawLine(paramInt1 + 8, paramInt2 + 10, paramInt1 + 9, paramInt2 + 10);
        paramGraphics.drawLine(paramInt1 + 10, paramInt2 + 9, paramInt1 + 10, paramInt2 + 8);
        paramGraphics.drawLine(paramInt1 + 11, paramInt2 + 7, paramInt1 + 11, paramInt2 + 4);
        paramGraphics.drawLine(paramInt1 + 10, paramInt2 + 3, paramInt1 + 10, paramInt2 + 2);
        paramGraphics.setColor(UIManager.getColor("RadioButton.darkShadow"));
        paramGraphics.drawLine(paramInt1 + 4, paramInt2 + 1, paramInt1 + 7, paramInt2 + 1);
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 2, paramInt1 + 3, paramInt2 + 2);
        paramGraphics.drawLine(paramInt1 + 8, paramInt2 + 2, paramInt1 + 9, paramInt2 + 2);
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 3, paramInt1 + 2, paramInt2 + 3);
        paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 4, paramInt1 + 1, paramInt2 + 7);
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 8, paramInt1 + 2, paramInt2 + 8);
        paramGraphics.setColor(UIManager.getColor("RadioButton.light"));
        paramGraphics.drawLine(paramInt1 + 2, paramInt2 + 9, paramInt1 + 3, paramInt2 + 9);
        paramGraphics.drawLine(paramInt1 + 4, paramInt2 + 10, paramInt1 + 7, paramInt2 + 10);
        paramGraphics.drawLine(paramInt1 + 8, paramInt2 + 9, paramInt1 + 9, paramInt2 + 9);
        paramGraphics.drawLine(paramInt1 + 9, paramInt2 + 8, paramInt1 + 9, paramInt2 + 8);
        paramGraphics.drawLine(paramInt1 + 10, paramInt2 + 7, paramInt1 + 10, paramInt2 + 4);
        paramGraphics.drawLine(paramInt1 + 9, paramInt2 + 3, paramInt1 + 9, paramInt2 + 3);
        if (localButtonModel.isSelected())
        {
          if (localButtonModel.isEnabled()) {
            paramGraphics.setColor(UIManager.getColor("RadioButton.foreground"));
          } else {
            paramGraphics.setColor(UIManager.getColor("RadioButton.shadow"));
          }
          paramGraphics.fillRect(paramInt1 + 4, paramInt2 + 5, 4, 2);
          paramGraphics.fillRect(paramInt1 + 5, paramInt2 + 4, 2, 4);
        }
      }
    }
    
    public int getIconWidth()
    {
      XPStyle localXPStyle = XPStyle.getXP();
      if (localXPStyle != null) {
        return localXPStyle.getSkin(null, TMSchema.Part.BP_RADIOBUTTON).getWidth();
      }
      return 13;
    }
    
    public int getIconHeight()
    {
      XPStyle localXPStyle = XPStyle.getXP();
      if (localXPStyle != null) {
        return localXPStyle.getSkin(null, TMSchema.Part.BP_RADIOBUTTON).getHeight();
      }
      return 13;
    }
  }
  
  private static class RadioButtonMenuItemIcon
    implements Icon, UIResource, Serializable
  {
    private RadioButtonMenuItemIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      if (localAbstractButton.isSelected() == true) {
        paramGraphics.fillRoundRect(paramInt1 + 3, paramInt2 + 3, getIconWidth() - 6, getIconHeight() - 6, 4, 4);
      }
    }
    
    public int getIconWidth()
    {
      return 12;
    }
    
    public int getIconHeight()
    {
      return 12;
    }
  }
  
  private static class ResizeIcon
    implements Icon, Serializable
  {
    private ResizeIcon() {}
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
    {
      paramGraphics.setColor(UIManager.getColor("InternalFrame.resizeIconHighlight"));
      paramGraphics.drawLine(0, 11, 11, 0);
      paramGraphics.drawLine(4, 11, 11, 4);
      paramGraphics.drawLine(8, 11, 11, 8);
      paramGraphics.setColor(UIManager.getColor("InternalFrame.resizeIconShadow"));
      paramGraphics.drawLine(1, 11, 11, 1);
      paramGraphics.drawLine(2, 11, 11, 2);
      paramGraphics.drawLine(5, 11, 11, 5);
      paramGraphics.drawLine(6, 11, 11, 6);
      paramGraphics.drawLine(9, 11, 11, 9);
      paramGraphics.drawLine(10, 11, 11, 10);
    }
    
    public int getIconWidth()
    {
      return 13;
    }
    
    public int getIconHeight()
    {
      return 13;
    }
  }
  
  static class VistaMenuItemCheckIconFactory
    implements MenuItemCheckIconFactory
  {
    private static final int OFFSET = 3;
    
    VistaMenuItemCheckIconFactory() {}
    
    public Icon getIcon(JMenuItem paramJMenuItem)
    {
      return new VistaMenuItemCheckIcon(paramJMenuItem);
    }
    
    public boolean isCompatible(Object paramObject, String paramString)
    {
      return ((paramObject instanceof VistaMenuItemCheckIcon)) && (((VistaMenuItemCheckIcon)paramObject).type == getType(paramString));
    }
    
    public Icon getIcon(String paramString)
    {
      return new VistaMenuItemCheckIcon(paramString);
    }
    
    static int getIconWidth()
    {
      XPStyle localXPStyle = XPStyle.getXP();
      return (localXPStyle != null ? localXPStyle.getSkin(null, TMSchema.Part.MP_POPUPCHECK).getWidth() : 16) + 6;
    }
    
    private static Class<? extends JMenuItem> getType(Component paramComponent)
    {
      Object localObject = null;
      if ((paramComponent instanceof JCheckBoxMenuItem)) {
        localObject = JCheckBoxMenuItem.class;
      } else if ((paramComponent instanceof JRadioButtonMenuItem)) {
        localObject = JRadioButtonMenuItem.class;
      } else if ((paramComponent instanceof JMenu)) {
        localObject = JMenu.class;
      } else if ((paramComponent instanceof JMenuItem)) {
        localObject = JMenuItem.class;
      }
      return localObject;
    }
    
    private static Class<? extends JMenuItem> getType(String paramString)
    {
      Object localObject = null;
      if (paramString == "CheckBoxMenuItem") {
        localObject = JCheckBoxMenuItem.class;
      } else if (paramString == "RadioButtonMenuItem") {
        localObject = JRadioButtonMenuItem.class;
      } else if (paramString == "Menu") {
        localObject = JMenu.class;
      } else if (paramString == "MenuItem") {
        localObject = JMenuItem.class;
      } else {
        localObject = JMenuItem.class;
      }
      return localObject;
    }
    
    private static class VistaMenuItemCheckIcon
      implements Icon, UIResource, Serializable
    {
      private final JMenuItem menuItem;
      private final Class<? extends JMenuItem> type;
      
      VistaMenuItemCheckIcon(JMenuItem paramJMenuItem)
      {
        this.type = WindowsIconFactory.VistaMenuItemCheckIconFactory.getType(paramJMenuItem);
        this.menuItem = paramJMenuItem;
      }
      
      VistaMenuItemCheckIcon(String paramString)
      {
        this.type = WindowsIconFactory.VistaMenuItemCheckIconFactory.getType(paramString);
        this.menuItem = null;
      }
      
      public int getIconHeight()
      {
        Icon localIcon1 = getLaFIcon();
        if (localIcon1 != null) {
          return localIcon1.getIconHeight();
        }
        Icon localIcon2 = getIcon();
        int i = 0;
        if (localIcon2 != null)
        {
          i = localIcon2.getIconHeight();
        }
        else
        {
          XPStyle localXPStyle = XPStyle.getXP();
          if (localXPStyle != null)
          {
            XPStyle.Skin localSkin = localXPStyle.getSkin(null, TMSchema.Part.MP_POPUPCHECK);
            i = localSkin.getHeight();
          }
          else
          {
            i = 16;
          }
        }
        i += 6;
        return i;
      }
      
      public int getIconWidth()
      {
        Icon localIcon1 = getLaFIcon();
        if (localIcon1 != null) {
          return localIcon1.getIconWidth();
        }
        Icon localIcon2 = getIcon();
        int i = 0;
        if (localIcon2 != null) {
          i = localIcon2.getIconWidth() + 6;
        } else {
          i = WindowsIconFactory.VistaMenuItemCheckIconFactory.getIconWidth();
        }
        return i;
      }
      
      public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
      {
        Icon localIcon1 = getLaFIcon();
        if (localIcon1 != null)
        {
          localIcon1.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2);
          return;
        }
        assert ((this.menuItem == null) || (paramComponent == this.menuItem));
        Icon localIcon2 = getIcon();
        if ((this.type == JCheckBoxMenuItem.class) || (this.type == JRadioButtonMenuItem.class))
        {
          AbstractButton localAbstractButton = (AbstractButton)paramComponent;
          if (localAbstractButton.isSelected())
          {
            TMSchema.Part localPart1 = TMSchema.Part.MP_POPUPCHECKBACKGROUND;
            TMSchema.Part localPart2 = TMSchema.Part.MP_POPUPCHECK;
            TMSchema.State localState1;
            TMSchema.State localState2;
            if (isEnabled(paramComponent, null))
            {
              localState1 = localIcon2 != null ? TMSchema.State.BITMAP : TMSchema.State.NORMAL;
              localState2 = this.type == JRadioButtonMenuItem.class ? TMSchema.State.BULLETNORMAL : TMSchema.State.CHECKMARKNORMAL;
            }
            else
            {
              localState1 = TMSchema.State.DISABLEDPUSHED;
              localState2 = this.type == JRadioButtonMenuItem.class ? TMSchema.State.BULLETDISABLED : TMSchema.State.CHECKMARKDISABLED;
            }
            XPStyle localXPStyle = XPStyle.getXP();
            if (localXPStyle != null)
            {
              XPStyle.Skin localSkin = localXPStyle.getSkin(paramComponent, localPart1);
              localSkin.paintSkin(paramGraphics, paramInt1, paramInt2, getIconWidth(), getIconHeight(), localState1);
              if (localIcon2 == null)
              {
                localSkin = localXPStyle.getSkin(paramComponent, localPart2);
                localSkin.paintSkin(paramGraphics, paramInt1 + 3, paramInt2 + 3, localState2);
              }
            }
          }
        }
        if (localIcon2 != null) {
          localIcon2.paintIcon(paramComponent, paramGraphics, paramInt1 + 3, paramInt2 + 3);
        }
      }
      
      private static WindowsMenuItemUIAccessor getAccessor(JMenuItem paramJMenuItem)
      {
        WindowsMenuItemUIAccessor localWindowsMenuItemUIAccessor = null;
        Object localObject = paramJMenuItem != null ? paramJMenuItem.getUI() : null;
        if ((localObject instanceof WindowsMenuItemUI)) {
          localWindowsMenuItemUIAccessor = ((WindowsMenuItemUI)localObject).accessor;
        } else if ((localObject instanceof WindowsMenuUI)) {
          localWindowsMenuItemUIAccessor = ((WindowsMenuUI)localObject).accessor;
        } else if ((localObject instanceof WindowsCheckBoxMenuItemUI)) {
          localWindowsMenuItemUIAccessor = ((WindowsCheckBoxMenuItemUI)localObject).accessor;
        } else if ((localObject instanceof WindowsRadioButtonMenuItemUI)) {
          localWindowsMenuItemUIAccessor = ((WindowsRadioButtonMenuItemUI)localObject).accessor;
        }
        return localWindowsMenuItemUIAccessor;
      }
      
      private static boolean isEnabled(Component paramComponent, TMSchema.State paramState)
      {
        if ((paramState == null) && ((paramComponent instanceof JMenuItem)))
        {
          WindowsMenuItemUIAccessor localWindowsMenuItemUIAccessor = getAccessor((JMenuItem)paramComponent);
          if (localWindowsMenuItemUIAccessor != null) {
            paramState = localWindowsMenuItemUIAccessor.getState((JMenuItem)paramComponent);
          }
        }
        if (paramState == null)
        {
          if (paramComponent != null) {
            return paramComponent.isEnabled();
          }
          return true;
        }
        return (paramState != TMSchema.State.DISABLED) && (paramState != TMSchema.State.DISABLEDHOT) && (paramState != TMSchema.State.DISABLEDPUSHED);
      }
      
      private Icon getIcon()
      {
        Icon localIcon = null;
        if (this.menuItem == null) {
          return localIcon;
        }
        WindowsMenuItemUIAccessor localWindowsMenuItemUIAccessor = getAccessor(this.menuItem);
        Object localObject = localWindowsMenuItemUIAccessor != null ? localWindowsMenuItemUIAccessor.getState(this.menuItem) : null;
        if (isEnabled(this.menuItem, null))
        {
          if (localObject == TMSchema.State.PUSHED) {
            localIcon = this.menuItem.getPressedIcon();
          } else {
            localIcon = this.menuItem.getIcon();
          }
        }
        else {
          localIcon = this.menuItem.getDisabledIcon();
        }
        return localIcon;
      }
      
      private Icon getLaFIcon()
      {
        Icon localIcon = (Icon)UIManager.getDefaults().get(typeToString(this.type));
        if (((localIcon instanceof VistaMenuItemCheckIcon)) && (((VistaMenuItemCheckIcon)localIcon).type == this.type)) {
          localIcon = null;
        }
        return localIcon;
      }
      
      private static String typeToString(Class<? extends JMenuItem> paramClass)
      {
        assert ((paramClass == JMenuItem.class) || (paramClass == JMenu.class) || (paramClass == JCheckBoxMenuItem.class) || (paramClass == JRadioButtonMenuItem.class));
        StringBuilder localStringBuilder = new StringBuilder(paramClass.getName());
        localStringBuilder.delete(0, localStringBuilder.lastIndexOf("J") + 1);
        localStringBuilder.append(".checkIcon");
        return localStringBuilder.toString();
      }
    }
  }
}
