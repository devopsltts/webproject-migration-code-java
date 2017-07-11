package com.sun.java.swing.plaf.motif;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.MenuSelectionManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

public class MotifMenuItemUI
  extends BasicMenuItemUI
{
  protected ChangeListener changeListener;
  
  public MotifMenuItemUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifMenuItemUI();
  }
  
  protected void installListeners()
  {
    super.installListeners();
    this.changeListener = createChangeListener(this.menuItem);
    this.menuItem.addChangeListener(this.changeListener);
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    this.menuItem.removeChangeListener(this.changeListener);
  }
  
  protected ChangeListener createChangeListener(JComponent paramJComponent)
  {
    return new ChangeHandler();
  }
  
  protected MouseInputListener createMouseInputListener(JComponent paramJComponent)
  {
    return new MouseInputHandler();
  }
  
  protected class ChangeHandler
    implements ChangeListener
  {
    protected ChangeHandler() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      JMenuItem localJMenuItem = (JMenuItem)paramChangeEvent.getSource();
      LookAndFeel.installProperty(localJMenuItem, "borderPainted", Boolean.valueOf((localJMenuItem.isArmed()) || (localJMenuItem.isSelected())));
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
      localMenuSelectionManager.setSelectedPath(MotifMenuItemUI.this.getPath());
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      JMenuItem localJMenuItem = (JMenuItem)paramMouseEvent.getComponent();
      Point localPoint = paramMouseEvent.getPoint();
      if ((localPoint.x >= 0) && (localPoint.x < localJMenuItem.getWidth()) && (localPoint.y >= 0) && (localPoint.y < localJMenuItem.getHeight()))
      {
        localMenuSelectionManager.clearSelectedPath();
        localJMenuItem.doClick(0);
      }
      else
      {
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
