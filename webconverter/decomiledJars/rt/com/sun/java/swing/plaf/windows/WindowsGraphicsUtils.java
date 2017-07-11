package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import sun.swing.SwingUtilities2;

public class WindowsGraphicsUtils
{
  public WindowsGraphicsUtils() {}
  
  public static void paintText(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle, String paramString, int paramInt)
  {
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(paramAbstractButton, paramGraphics);
    int i = paramAbstractButton.getDisplayedMnemonicIndex();
    if (WindowsLookAndFeel.isMnemonicHidden() == true) {
      i = -1;
    }
    XPStyle localXPStyle = XPStyle.getXP();
    if ((localXPStyle != null) && (!(paramAbstractButton instanceof JMenuItem))) {
      paintXPText(paramAbstractButton, paramGraphics, paramRectangle.x + paramInt, paramRectangle.y + localFontMetrics.getAscent() + paramInt, paramString, i);
    } else {
      paintClassicText(paramAbstractButton, paramGraphics, paramRectangle.x + paramInt, paramRectangle.y + localFontMetrics.getAscent() + paramInt, paramString, i);
    }
  }
  
  static void paintClassicText(AbstractButton paramAbstractButton, Graphics paramGraphics, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    ButtonModel localButtonModel = paramAbstractButton.getModel();
    Color localColor1 = paramAbstractButton.getForeground();
    if (localButtonModel.isEnabled())
    {
      if (((!(paramAbstractButton instanceof JMenuItem)) || (!localButtonModel.isArmed())) && ((!(paramAbstractButton instanceof JMenu)) || ((!localButtonModel.isSelected()) && (!localButtonModel.isRollover())))) {
        paramGraphics.setColor(paramAbstractButton.getForeground());
      }
      SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1, paramInt2);
    }
    else
    {
      localColor1 = UIManager.getColor("Button.shadow");
      Color localColor2 = UIManager.getColor("Button.disabledShadow");
      if (localButtonModel.isArmed())
      {
        localColor1 = UIManager.getColor("Button.disabledForeground");
      }
      else
      {
        if (localColor2 == null) {
          localColor2 = paramAbstractButton.getBackground().darker();
        }
        paramGraphics.setColor(localColor2);
        SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1 + 1, paramInt2 + 1);
      }
      if (localColor1 == null) {
        localColor1 = paramAbstractButton.getBackground().brighter();
      }
      paramGraphics.setColor(localColor1);
      SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1, paramInt2);
    }
  }
  
  static void paintXPText(AbstractButton paramAbstractButton, Graphics paramGraphics, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    TMSchema.Part localPart = WindowsButtonUI.getXPButtonType(paramAbstractButton);
    TMSchema.State localState = WindowsButtonUI.getXPButtonState(paramAbstractButton);
    paintXPText(paramAbstractButton, localPart, localState, paramGraphics, paramInt1, paramInt2, paramString, paramInt3);
  }
  
  static void paintXPText(AbstractButton paramAbstractButton, TMSchema.Part paramPart, TMSchema.State paramState, Graphics paramGraphics, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle == null) {
      return;
    }
    Color localColor1 = paramAbstractButton.getForeground();
    if ((localColor1 instanceof UIResource))
    {
      localColor1 = localXPStyle.getColor(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTCOLOR, paramAbstractButton.getForeground());
      if ((paramPart == TMSchema.Part.TP_BUTTON) && (paramState == TMSchema.State.DISABLED))
      {
        localObject = localXPStyle.getColor(paramAbstractButton, paramPart, TMSchema.State.NORMAL, TMSchema.Prop.TEXTCOLOR, paramAbstractButton.getForeground());
        if (localColor1.equals(localObject)) {
          localColor1 = localXPStyle.getColor(paramAbstractButton, TMSchema.Part.BP_PUSHBUTTON, paramState, TMSchema.Prop.TEXTCOLOR, localColor1);
        }
      }
      Object localObject = localXPStyle.getTypeEnum(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTSHADOWTYPE);
      if ((localObject == TMSchema.TypeEnum.TST_SINGLE) || (localObject == TMSchema.TypeEnum.TST_CONTINUOUS))
      {
        Color localColor2 = localXPStyle.getColor(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTSHADOWCOLOR, Color.black);
        Point localPoint = localXPStyle.getPoint(paramAbstractButton, paramPart, paramState, TMSchema.Prop.TEXTSHADOWOFFSET);
        if (localPoint != null)
        {
          paramGraphics.setColor(localColor2);
          SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1 + localPoint.x, paramInt2 + localPoint.y);
        }
      }
    }
    paramGraphics.setColor(localColor1);
    SwingUtilities2.drawStringUnderlineCharAt(paramAbstractButton, paramGraphics, paramString, paramInt3, paramInt1, paramInt2);
  }
  
  static boolean isLeftToRight(Component paramComponent)
  {
    return paramComponent.getComponentOrientation().isLeftToRight();
  }
  
  static void repaintMnemonicsInWindow(Window paramWindow)
  {
    if ((paramWindow == null) || (!paramWindow.isShowing())) {
      return;
    }
    Window[] arrayOfWindow = paramWindow.getOwnedWindows();
    for (int i = 0; i < arrayOfWindow.length; i++) {
      repaintMnemonicsInWindow(arrayOfWindow[i]);
    }
    repaintMnemonicsInContainer(paramWindow);
  }
  
  static void repaintMnemonicsInContainer(Container paramContainer)
  {
    for (int i = 0; i < paramContainer.getComponentCount(); i++)
    {
      Component localComponent = paramContainer.getComponent(i);
      if ((localComponent != null) && (localComponent.isVisible())) {
        if (((localComponent instanceof AbstractButton)) && (((AbstractButton)localComponent).getMnemonic() != 0)) {
          localComponent.repaint();
        } else if (((localComponent instanceof JLabel)) && (((JLabel)localComponent).getDisplayedMnemonic() != 0)) {
          localComponent.repaint();
        } else if ((localComponent instanceof Container)) {
          repaintMnemonicsInContainer((Container)localComponent);
        }
      }
    }
  }
}
