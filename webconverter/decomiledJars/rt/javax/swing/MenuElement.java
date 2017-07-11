package javax.swing;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract interface MenuElement
{
  public abstract void processMouseEvent(MouseEvent paramMouseEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager);
  
  public abstract void processKeyEvent(KeyEvent paramKeyEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager);
  
  public abstract void menuSelectionChanged(boolean paramBoolean);
  
  public abstract MenuElement[] getSubElements();
  
  public abstract Component getComponent();
}
