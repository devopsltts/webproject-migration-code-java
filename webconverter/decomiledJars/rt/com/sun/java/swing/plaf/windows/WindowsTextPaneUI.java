package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.Caret;

public class WindowsTextPaneUI
  extends BasicTextPaneUI
{
  public WindowsTextPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsTextPaneUI();
  }
  
  protected Caret createCaret()
  {
    return new WindowsTextUI.WindowsCaret();
  }
}
