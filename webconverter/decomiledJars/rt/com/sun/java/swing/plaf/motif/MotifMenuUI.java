package com.sun.java.swing.plaf.motif;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;
import javax.swing.plaf.basic.BasicMenuUI.ChangeHandler;

public class MotifMenuUI
  extends BasicMenuUI
{
  public MotifMenuUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifMenuUI();
  }
  
  protected ChangeListener createChangeListener(JComponent paramJComponent)
  {
    return new MotifChangeHandler((JMenu)paramJComponent, this);
  }
  
  private boolean popupIsOpen(JMenu paramJMenu, MenuElement[] paramArrayOfMenuElement)
  {
    JPopupMenu localJPopupMenu = paramJMenu.getPopupMenu();
    for (int i = paramArrayOfMenuElement.length - 1; i >= 0; i--) {
      if (paramArrayOfMenuElement[i].getComponent() == localJPopupMenu) {
        return true;
      }
    }
    return false;
  }
  
  protected MouseInputListener createMouseInputListener(JComponent paramJComponent)
  {
    return new MouseInputHandler();
  }
  
  public class MotifChangeHandler
    extends BasicMenuUI.ChangeHandler
  {
    public MotifChangeHandler(JMenu paramJMenu, MotifMenuUI paramMotifMenuUI)
    {
      super(paramJMenu, paramMotifMenuUI);
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      JMenuItem localJMenuItem = (JMenuItem)paramChangeEvent.getSource();
      if ((localJMenuItem.isArmed()) || (localJMenuItem.isSelected())) {
        localJMenuItem.setBorderPainted(true);
      } else {
        localJMenuItem.setBorderPainted(false);
      }
      super.stateChanged(paramChangeEvent);
    }
  }
  
  protected class MouseInputHandler
    implements MouseInputListener
  {
    protected MouseInputHandler() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      JMenu localJMenu = (JMenu)paramMouseEvent.getComponent();
      if (localJMenu.isEnabled())
      {
        MenuElement[] arrayOfMenuElement;
        if (localJMenu.isTopLevelMenu()) {
          if (localJMenu.isSelected())
          {
            localMenuSelectionManager.clearSelectedPath();
          }
          else
          {
            localObject = localJMenu.getParent();
            if ((localObject != null) && ((localObject instanceof JMenuBar)))
            {
              arrayOfMenuElement = new MenuElement[2];
              arrayOfMenuElement[0] = ((MenuElement)localObject);
              arrayOfMenuElement[1] = localJMenu;
              localMenuSelectionManager.setSelectedPath(arrayOfMenuElement);
            }
          }
        }
        Object localObject = MotifMenuUI.this.getPath();
        if (localObject.length > 0)
        {
          arrayOfMenuElement = new MenuElement[localObject.length + 1];
          System.arraycopy(localObject, 0, arrayOfMenuElement, 0, localObject.length);
          arrayOfMenuElement[localObject.length] = localJMenu.getPopupMenu();
          localMenuSelectionManager.setSelectedPath(arrayOfMenuElement);
        }
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      JMenuItem localJMenuItem = (JMenuItem)paramMouseEvent.getComponent();
      Point localPoint = paramMouseEvent.getPoint();
      if ((localPoint.x < 0) || (localPoint.x >= localJMenuItem.getWidth()) || (localPoint.y < 0) || (localPoint.y >= localJMenuItem.getHeight())) {
        localMenuSelectionManager.processMouseEvent(paramMouseEvent);
      }
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent) {}
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent);
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
  }
}
