package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class WindowsSpinnerUI
  extends BasicSpinnerUI
{
  public WindowsSpinnerUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsSpinnerUI();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (XPStyle.getXP() != null) {
      paintXPBackground(paramGraphics, paramJComponent);
    }
    super.paint(paramGraphics, paramJComponent);
  }
  
  private TMSchema.State getXPState(JComponent paramJComponent)
  {
    TMSchema.State localState = TMSchema.State.NORMAL;
    if (!paramJComponent.isEnabled()) {
      localState = TMSchema.State.DISABLED;
    }
    return localState;
  }
  
  private void paintXPBackground(Graphics paramGraphics, JComponent paramJComponent)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle == null) {
      return;
    }
    XPStyle.Skin localSkin = localXPStyle.getSkin(paramJComponent, TMSchema.Part.EP_EDIT);
    TMSchema.State localState = getXPState(paramJComponent);
    localSkin.paintSkin(paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), localState);
  }
  
  protected Component createPreviousButton()
  {
    if (XPStyle.getXP() != null)
    {
      XPStyle.GlyphButton localGlyphButton = new XPStyle.GlyphButton(this.spinner, TMSchema.Part.SPNP_DOWN);
      Dimension localDimension = UIManager.getDimension("Spinner.arrowButtonSize");
      localGlyphButton.setPreferredSize(localDimension);
      localGlyphButton.setRequestFocusEnabled(false);
      installPreviousButtonListeners(localGlyphButton);
      return localGlyphButton;
    }
    return super.createPreviousButton();
  }
  
  protected Component createNextButton()
  {
    if (XPStyle.getXP() != null)
    {
      XPStyle.GlyphButton localGlyphButton = new XPStyle.GlyphButton(this.spinner, TMSchema.Part.SPNP_UP);
      Dimension localDimension = UIManager.getDimension("Spinner.arrowButtonSize");
      localGlyphButton.setPreferredSize(localDimension);
      localGlyphButton.setRequestFocusEnabled(false);
      installNextButtonListeners(localGlyphButton);
      return localGlyphButton;
    }
    return super.createNextButton();
  }
  
  private UIResource getUIResource(Object[] paramArrayOfObject)
  {
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      if ((paramArrayOfObject[i] instanceof UIResource)) {
        return (UIResource)paramArrayOfObject[i];
      }
    }
    return null;
  }
}
