package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.KeyEventPostProcessor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;
import javax.swing.plaf.basic.ComboPopup;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.KeyEventAccessor;
import sun.awt.SunToolkit;

public class WindowsRootPaneUI
  extends BasicRootPaneUI
{
  private static final WindowsRootPaneUI windowsRootPaneUI = new WindowsRootPaneUI();
  static final AltProcessor altProcessor = new AltProcessor();
  
  public WindowsRootPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return windowsRootPaneUI;
  }
  
  static class AltProcessor
    implements KeyEventPostProcessor
  {
    static boolean altKeyPressed = false;
    static boolean menuCanceledOnPress = false;
    static JRootPane root = null;
    static Window winAncestor = null;
    
    AltProcessor() {}
    
    void altPressed(KeyEvent paramKeyEvent)
    {
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement = localMenuSelectionManager.getSelectedPath();
      if ((arrayOfMenuElement.length > 0) && (!(arrayOfMenuElement[0] instanceof ComboPopup)))
      {
        localMenuSelectionManager.clearSelectedPath();
        menuCanceledOnPress = true;
        paramKeyEvent.consume();
      }
      else if (arrayOfMenuElement.length > 0)
      {
        menuCanceledOnPress = false;
        WindowsLookAndFeel.setMnemonicHidden(false);
        WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
        paramKeyEvent.consume();
      }
      else
      {
        menuCanceledOnPress = false;
        WindowsLookAndFeel.setMnemonicHidden(false);
        WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
        JMenuBar localJMenuBar = root != null ? root.getJMenuBar() : null;
        if ((localJMenuBar == null) && ((winAncestor instanceof JFrame))) {
          localJMenuBar = ((JFrame)winAncestor).getJMenuBar();
        }
        Object localObject = localJMenuBar != null ? localJMenuBar.getMenu(0) : null;
        if (localObject != null) {
          paramKeyEvent.consume();
        }
      }
    }
    
    void altReleased(KeyEvent paramKeyEvent)
    {
      if (menuCanceledOnPress)
      {
        WindowsLookAndFeel.setMnemonicHidden(true);
        WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
        return;
      }
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      if (localMenuSelectionManager.getSelectedPath().length == 0)
      {
        JMenuBar localJMenuBar = root != null ? root.getJMenuBar() : null;
        if ((localJMenuBar == null) && ((winAncestor instanceof JFrame))) {
          localJMenuBar = ((JFrame)winAncestor).getJMenuBar();
        }
        Object localObject1 = localJMenuBar != null ? localJMenuBar.getMenu(0) : null;
        int i = 0;
        Toolkit localToolkit = Toolkit.getDefaultToolkit();
        Object localObject2;
        if ((localToolkit instanceof SunToolkit))
        {
          localObject2 = AWTAccessor.getKeyEventAccessor().getOriginalSource(paramKeyEvent);
          i = (SunToolkit.getContainingWindow((Component)localObject2) != winAncestor) || (paramKeyEvent.getWhen() <= ((SunToolkit)localToolkit).getWindowDeactivationTime(winAncestor)) ? 1 : 0;
        }
        if ((localObject1 != null) && (i == 0))
        {
          localObject2 = new MenuElement[2];
          localObject2[0] = localJMenuBar;
          localObject2[1] = localObject1;
          localMenuSelectionManager.setSelectedPath((MenuElement[])localObject2);
        }
        else if (!WindowsLookAndFeel.isMnemonicHidden())
        {
          WindowsLookAndFeel.setMnemonicHidden(true);
          WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
        }
      }
      else if ((localMenuSelectionManager.getSelectedPath()[0] instanceof ComboPopup))
      {
        WindowsLookAndFeel.setMnemonicHidden(true);
        WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
      }
    }
    
    public boolean postProcessKeyEvent(KeyEvent paramKeyEvent)
    {
      if (paramKeyEvent.isConsumed()) {
        return false;
      }
      if (paramKeyEvent.getKeyCode() == 18)
      {
        root = SwingUtilities.getRootPane(paramKeyEvent.getComponent());
        winAncestor = root == null ? null : SwingUtilities.getWindowAncestor(root);
        if (paramKeyEvent.getID() == 401)
        {
          if (!altKeyPressed) {
            altPressed(paramKeyEvent);
          }
          altKeyPressed = true;
          return true;
        }
        if (paramKeyEvent.getID() == 402)
        {
          if (altKeyPressed)
          {
            altReleased(paramKeyEvent);
          }
          else
          {
            MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
            MenuElement[] arrayOfMenuElement = localMenuSelectionManager.getSelectedPath();
            if (arrayOfMenuElement.length <= 0)
            {
              WindowsLookAndFeel.setMnemonicHidden(true);
              WindowsGraphicsUtils.repaintMnemonicsInWindow(winAncestor);
            }
          }
          altKeyPressed = false;
        }
        root = null;
        winAncestor = null;
      }
      else
      {
        altKeyPressed = false;
      }
      return false;
    }
  }
}
