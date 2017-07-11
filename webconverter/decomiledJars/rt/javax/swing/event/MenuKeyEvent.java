package javax.swing.event;

import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

public class MenuKeyEvent
  extends KeyEvent
{
  private MenuElement[] path;
  private MenuSelectionManager manager;
  
  public MenuKeyEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, char paramChar, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager)
  {
    super(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramChar);
    this.path = paramArrayOfMenuElement;
    this.manager = paramMenuSelectionManager;
  }
  
  public MenuElement[] getPath()
  {
    return this.path;
  }
  
  public MenuSelectionManager getMenuSelectionManager()
  {
    return this.manager;
  }
}
