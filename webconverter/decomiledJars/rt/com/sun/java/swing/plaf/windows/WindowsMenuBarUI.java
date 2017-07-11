package com.sun.java.swing.plaf.windows;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

public class WindowsMenuBarUI
  extends BasicMenuBarUI
{
  private WindowListener windowListener = null;
  private HierarchyListener hierarchyListener = null;
  private Window window = null;
  
  public WindowsMenuBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsMenuBarUI();
  }
  
  protected void uninstallListeners()
  {
    uninstallWindowListener();
    if (this.hierarchyListener != null)
    {
      this.menuBar.removeHierarchyListener(this.hierarchyListener);
      this.hierarchyListener = null;
    }
    super.uninstallListeners();
  }
  
  private void installWindowListener()
  {
    if (this.windowListener == null)
    {
      Container localContainer = this.menuBar.getTopLevelAncestor();
      if ((localContainer instanceof Window))
      {
        this.window = ((Window)localContainer);
        this.windowListener = new WindowAdapter()
        {
          public void windowActivated(WindowEvent paramAnonymousWindowEvent)
          {
            WindowsMenuBarUI.this.menuBar.repaint();
          }
          
          public void windowDeactivated(WindowEvent paramAnonymousWindowEvent)
          {
            WindowsMenuBarUI.this.menuBar.repaint();
          }
        };
        ((Window)localContainer).addWindowListener(this.windowListener);
      }
    }
  }
  
  private void uninstallWindowListener()
  {
    if ((this.windowListener != null) && (this.window != null)) {
      this.window.removeWindowListener(this.windowListener);
    }
    this.window = null;
    this.windowListener = null;
  }
  
  protected void installListeners()
  {
    if (WindowsLookAndFeel.isOnVista())
    {
      installWindowListener();
      this.hierarchyListener = new HierarchyListener()
      {
        public void hierarchyChanged(HierarchyEvent paramAnonymousHierarchyEvent)
        {
          if ((paramAnonymousHierarchyEvent.getChangeFlags() & 0x2) != 0L) {
            if (WindowsMenuBarUI.this.menuBar.isDisplayable()) {
              WindowsMenuBarUI.this.installWindowListener();
            } else {
              WindowsMenuBarUI.this.uninstallWindowListener();
            }
          }
        }
      };
      this.menuBar.addHierarchyListener(this.hierarchyListener);
    }
    super.installListeners();
  }
  
  protected void installKeyboardActions()
  {
    super.installKeyboardActions();
    Object localObject = SwingUtilities.getUIActionMap(this.menuBar);
    if (localObject == null)
    {
      localObject = new ActionMapUIResource();
      SwingUtilities.replaceUIActionMap(this.menuBar, (ActionMap)localObject);
    }
    ((ActionMap)localObject).put("takeFocus", new TakeFocus(null));
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (WindowsMenuItemUI.isVistaPainting(localXPStyle))
    {
      XPStyle.Skin localSkin = localXPStyle.getSkin(paramJComponent, TMSchema.Part.MP_BARBACKGROUND);
      int i = paramJComponent.getWidth();
      int j = paramJComponent.getHeight();
      TMSchema.State localState = isActive(paramJComponent) ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE;
      localSkin.paintSkin(paramGraphics, 0, 0, i, j, localState);
    }
    else
    {
      super.paint(paramGraphics, paramJComponent);
    }
  }
  
  static boolean isActive(JComponent paramJComponent)
  {
    JRootPane localJRootPane = paramJComponent.getRootPane();
    if (localJRootPane != null)
    {
      Container localContainer = localJRootPane.getParent();
      if ((localContainer instanceof Window)) {
        return ((Window)localContainer).isActive();
      }
    }
    return true;
  }
  
  private static class TakeFocus
    extends AbstractAction
  {
    private TakeFocus() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JMenuBar localJMenuBar = (JMenuBar)paramActionEvent.getSource();
      JMenu localJMenu = localJMenuBar.getMenu(0);
      if (localJMenu != null)
      {
        MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
        MenuElement[] arrayOfMenuElement = new MenuElement[2];
        arrayOfMenuElement[0] = localJMenuBar;
        arrayOfMenuElement[1] = localJMenu;
        localMenuSelectionManager.setSelectedPath(arrayOfMenuElement);
        WindowsLookAndFeel.setMnemonicHidden(false);
        WindowsLookAndFeel.repaintRootPane(localJMenuBar);
      }
    }
  }
}
