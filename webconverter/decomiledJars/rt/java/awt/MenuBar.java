package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.MenuBarPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.MenuBarAccessor;

public class MenuBar
  extends MenuComponent
  implements MenuContainer, Accessible
{
  Vector<Menu> menus = new Vector();
  Menu helpMenu;
  private static final String base = "menubar";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -4930327919388951260L;
  private int menuBarSerializedDataVersion = 1;
  
  public MenuBar()
    throws HeadlessException
  {}
  
  String constructComponentName()
  {
    synchronized (MenuBar.class)
    {
      return "menubar" + nameCounter++;
    }
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if (this.peer == null) {
        this.peer = Toolkit.getDefaultToolkit().createMenuBar(this);
      }
      int i = getMenuCount();
      for (int j = 0; j < i; j++) {
        getMenu(j).addNotify();
      }
    }
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      int i = getMenuCount();
      for (int j = 0; j < i; j++) {
        getMenu(j).removeNotify();
      }
      super.removeNotify();
    }
  }
  
  public Menu getHelpMenu()
  {
    return this.helpMenu;
  }
  
  public void setHelpMenu(Menu paramMenu)
  {
    synchronized (getTreeLock())
    {
      if (this.helpMenu == paramMenu) {
        return;
      }
      if (this.helpMenu != null) {
        remove(this.helpMenu);
      }
      this.helpMenu = paramMenu;
      if (paramMenu != null)
      {
        if (paramMenu.parent != this) {
          add(paramMenu);
        }
        paramMenu.isHelpMenu = true;
        paramMenu.parent = this;
        MenuBarPeer localMenuBarPeer = (MenuBarPeer)this.peer;
        if (localMenuBarPeer != null)
        {
          if (paramMenu.peer == null) {
            paramMenu.addNotify();
          }
          localMenuBarPeer.addHelpMenu(paramMenu);
        }
      }
    }
  }
  
  public Menu add(Menu paramMenu)
  {
    synchronized (getTreeLock())
    {
      if (paramMenu.parent != null) {
        paramMenu.parent.remove(paramMenu);
      }
      this.menus.addElement(paramMenu);
      paramMenu.parent = this;
      MenuBarPeer localMenuBarPeer = (MenuBarPeer)this.peer;
      if (localMenuBarPeer != null)
      {
        if (paramMenu.peer == null) {
          paramMenu.addNotify();
        }
        localMenuBarPeer.addMenu(paramMenu);
      }
      return paramMenu;
    }
  }
  
  public void remove(int paramInt)
  {
    synchronized (getTreeLock())
    {
      Menu localMenu = getMenu(paramInt);
      this.menus.removeElementAt(paramInt);
      MenuBarPeer localMenuBarPeer = (MenuBarPeer)this.peer;
      if (localMenuBarPeer != null)
      {
        localMenu.removeNotify();
        localMenu.parent = null;
        localMenuBarPeer.delMenu(paramInt);
      }
      if (this.helpMenu == localMenu)
      {
        this.helpMenu = null;
        localMenu.isHelpMenu = false;
      }
    }
  }
  
  public void remove(MenuComponent paramMenuComponent)
  {
    synchronized (getTreeLock())
    {
      int i = this.menus.indexOf(paramMenuComponent);
      if (i >= 0) {
        remove(i);
      }
    }
  }
  
  public int getMenuCount()
  {
    return countMenus();
  }
  
  @Deprecated
  public int countMenus()
  {
    return getMenuCountImpl();
  }
  
  final int getMenuCountImpl()
  {
    return this.menus.size();
  }
  
  public Menu getMenu(int paramInt)
  {
    return getMenuImpl(paramInt);
  }
  
  final Menu getMenuImpl(int paramInt)
  {
    return (Menu)this.menus.elementAt(paramInt);
  }
  
  public synchronized Enumeration<MenuShortcut> shortcuts()
  {
    Vector localVector = new Vector();
    int i = getMenuCount();
    for (int j = 0; j < i; j++)
    {
      Enumeration localEnumeration = getMenu(j).shortcuts();
      while (localEnumeration.hasMoreElements()) {
        localVector.addElement(localEnumeration.nextElement());
      }
    }
    return localVector.elements();
  }
  
  public MenuItem getShortcutMenuItem(MenuShortcut paramMenuShortcut)
  {
    int i = getMenuCount();
    for (int j = 0; j < i; j++)
    {
      MenuItem localMenuItem = getMenu(j).getShortcutMenuItem(paramMenuShortcut);
      if (localMenuItem != null) {
        return localMenuItem;
      }
    }
    return null;
  }
  
  boolean handleShortcut(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getID();
    if ((i != 401) && (i != 402)) {
      return false;
    }
    int j = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    if ((paramKeyEvent.getModifiers() & j) == 0) {
      return false;
    }
    int k = getMenuCount();
    for (int m = 0; m < k; m++)
    {
      Menu localMenu = getMenu(m);
      if (localMenu.handleShortcut(paramKeyEvent)) {
        return true;
      }
    }
    return false;
  }
  
  public void deleteShortcut(MenuShortcut paramMenuShortcut)
  {
    int i = getMenuCount();
    for (int j = 0; j < i; j++) {
      getMenu(j).deleteShortcut(paramMenuShortcut);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    paramObjectInputStream.defaultReadObject();
    for (int i = 0; i < this.menus.size(); i++)
    {
      Menu localMenu = (Menu)this.menus.elementAt(i);
      localMenu.parent = this;
    }
  }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleAWTMenuBar();
    }
    return this.accessibleContext;
  }
  
  int getAccessibleChildIndex(MenuComponent paramMenuComponent)
  {
    return this.menus.indexOf(paramMenuComponent);
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setMenuBarAccessor(new AWTAccessor.MenuBarAccessor()
    {
      public Menu getHelpMenu(MenuBar paramAnonymousMenuBar)
      {
        return paramAnonymousMenuBar.helpMenu;
      }
      
      public Vector<Menu> getMenus(MenuBar paramAnonymousMenuBar)
      {
        return paramAnonymousMenuBar.menus;
      }
    });
  }
  
  protected class AccessibleAWTMenuBar
    extends MenuComponent.AccessibleAWTMenuComponent
  {
    private static final long serialVersionUID = -8577604491830083815L;
    
    protected AccessibleAWTMenuBar()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.MENU_BAR;
    }
  }
}
