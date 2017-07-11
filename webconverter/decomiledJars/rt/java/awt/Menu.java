package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.MenuPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.MenuAccessor;

public class Menu
  extends MenuItem
  implements MenuContainer, Accessible
{
  Vector<MenuComponent> items = new Vector();
  boolean tearOff;
  boolean isHelpMenu;
  private static final String base = "menu";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -8809584163345499784L;
  private int menuSerializedDataVersion = 1;
  
  public Menu()
    throws HeadlessException
  {
    this("", false);
  }
  
  public Menu(String paramString)
    throws HeadlessException
  {
    this(paramString, false);
  }
  
  public Menu(String paramString, boolean paramBoolean)
    throws HeadlessException
  {
    super(paramString);
    this.tearOff = paramBoolean;
  }
  
  String constructComponentName()
  {
    synchronized (Menu.class)
    {
      return "menu" + nameCounter++;
    }
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if (this.peer == null) {
        this.peer = Toolkit.getDefaultToolkit().createMenu(this);
      }
      int i = getItemCount();
      for (int j = 0; j < i; j++)
      {
        MenuItem localMenuItem = getItem(j);
        localMenuItem.parent = this;
        localMenuItem.addNotify();
      }
    }
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      int i = getItemCount();
      for (int j = 0; j < i; j++) {
        getItem(j).removeNotify();
      }
      super.removeNotify();
    }
  }
  
  public boolean isTearOff()
  {
    return this.tearOff;
  }
  
  public int getItemCount()
  {
    return countItems();
  }
  
  @Deprecated
  public int countItems()
  {
    return countItemsImpl();
  }
  
  final int countItemsImpl()
  {
    return this.items.size();
  }
  
  public MenuItem getItem(int paramInt)
  {
    return getItemImpl(paramInt);
  }
  
  final MenuItem getItemImpl(int paramInt)
  {
    return (MenuItem)this.items.elementAt(paramInt);
  }
  
  public MenuItem add(MenuItem paramMenuItem)
  {
    synchronized (getTreeLock())
    {
      if (paramMenuItem.parent != null) {
        paramMenuItem.parent.remove(paramMenuItem);
      }
      this.items.addElement(paramMenuItem);
      paramMenuItem.parent = this;
      MenuPeer localMenuPeer = (MenuPeer)this.peer;
      if (localMenuPeer != null)
      {
        paramMenuItem.addNotify();
        localMenuPeer.addItem(paramMenuItem);
      }
      return paramMenuItem;
    }
  }
  
  public void add(String paramString)
  {
    add(new MenuItem(paramString));
  }
  
  public void insert(MenuItem paramMenuItem, int paramInt)
  {
    synchronized (getTreeLock())
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("index less than zero.");
      }
      int i = getItemCount();
      Vector localVector = new Vector();
      for (int j = paramInt; j < i; j++)
      {
        localVector.addElement(getItem(paramInt));
        remove(paramInt);
      }
      add(paramMenuItem);
      for (j = 0; j < localVector.size(); j++) {
        add((MenuItem)localVector.elementAt(j));
      }
    }
  }
  
  public void insert(String paramString, int paramInt)
  {
    insert(new MenuItem(paramString), paramInt);
  }
  
  public void addSeparator()
  {
    add("-");
  }
  
  public void insertSeparator(int paramInt)
  {
    synchronized (getTreeLock())
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("index less than zero.");
      }
      int i = getItemCount();
      Vector localVector = new Vector();
      for (int j = paramInt; j < i; j++)
      {
        localVector.addElement(getItem(paramInt));
        remove(paramInt);
      }
      addSeparator();
      for (j = 0; j < localVector.size(); j++) {
        add((MenuItem)localVector.elementAt(j));
      }
    }
  }
  
  public void remove(int paramInt)
  {
    synchronized (getTreeLock())
    {
      MenuItem localMenuItem = getItem(paramInt);
      this.items.removeElementAt(paramInt);
      MenuPeer localMenuPeer = (MenuPeer)this.peer;
      if (localMenuPeer != null)
      {
        localMenuItem.removeNotify();
        localMenuItem.parent = null;
        localMenuPeer.delItem(paramInt);
      }
    }
  }
  
  public void remove(MenuComponent paramMenuComponent)
  {
    synchronized (getTreeLock())
    {
      int i = this.items.indexOf(paramMenuComponent);
      if (i >= 0) {
        remove(i);
      }
    }
  }
  
  public void removeAll()
  {
    synchronized (getTreeLock())
    {
      int i = getItemCount();
      for (int j = i - 1; j >= 0; j--) {
        remove(j);
      }
    }
  }
  
  boolean handleShortcut(KeyEvent paramKeyEvent)
  {
    int i = getItemCount();
    for (int j = 0; j < i; j++)
    {
      MenuItem localMenuItem = getItem(j);
      if (localMenuItem.handleShortcut(paramKeyEvent)) {
        return true;
      }
    }
    return false;
  }
  
  MenuItem getShortcutMenuItem(MenuShortcut paramMenuShortcut)
  {
    int i = getItemCount();
    for (int j = 0; j < i; j++)
    {
      MenuItem localMenuItem = getItem(j).getShortcutMenuItem(paramMenuShortcut);
      if (localMenuItem != null) {
        return localMenuItem;
      }
    }
    return null;
  }
  
  synchronized Enumeration<MenuShortcut> shortcuts()
  {
    Vector localVector = new Vector();
    int i = getItemCount();
    for (int j = 0; j < i; j++)
    {
      MenuItem localMenuItem = getItem(j);
      Object localObject;
      if ((localMenuItem instanceof Menu))
      {
        localObject = ((Menu)localMenuItem).shortcuts();
        while (((Enumeration)localObject).hasMoreElements()) {
          localVector.addElement(((Enumeration)localObject).nextElement());
        }
      }
      else
      {
        localObject = localMenuItem.getShortcut();
        if (localObject != null) {
          localVector.addElement(localObject);
        }
      }
    }
    return localVector.elements();
  }
  
  void deleteShortcut(MenuShortcut paramMenuShortcut)
  {
    int i = getItemCount();
    for (int j = 0; j < i; j++) {
      getItem(j).deleteShortcut(paramMenuShortcut);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException, HeadlessException
  {
    paramObjectInputStream.defaultReadObject();
    for (int i = 0; i < this.items.size(); i++)
    {
      MenuItem localMenuItem = (MenuItem)this.items.elementAt(i);
      localMenuItem.parent = this;
    }
  }
  
  public String paramString()
  {
    String str = ",tearOff=" + this.tearOff + ",isHelpMenu=" + this.isHelpMenu;
    return super.paramString() + str;
  }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleAWTMenu();
    }
    return this.accessibleContext;
  }
  
  int getAccessibleChildIndex(MenuComponent paramMenuComponent)
  {
    return this.items.indexOf(paramMenuComponent);
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setMenuAccessor(new AWTAccessor.MenuAccessor()
    {
      public Vector<MenuComponent> getItems(Menu paramAnonymousMenu)
      {
        return paramAnonymousMenu.items;
      }
    });
  }
  
  protected class AccessibleAWTMenu
    extends MenuItem.AccessibleAWTMenuItem
  {
    private static final long serialVersionUID = 5228160894980069094L;
    
    protected AccessibleAWTMenu()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.MENU;
    }
  }
}
