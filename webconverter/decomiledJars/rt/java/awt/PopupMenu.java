package java.awt;

import java.awt.peer.PopupMenuPeer;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.PopupMenuAccessor;

public class PopupMenu
  extends Menu
{
  private static final String base = "popup";
  static int nameCounter = 0;
  transient boolean isTrayIconPopup = false;
  private static final long serialVersionUID = -4620452533522760060L;
  
  public PopupMenu()
    throws HeadlessException
  {
    this("");
  }
  
  public PopupMenu(String paramString)
    throws HeadlessException
  {
    super(paramString);
  }
  
  public MenuContainer getParent()
  {
    if (this.isTrayIconPopup) {
      return null;
    }
    return super.getParent();
  }
  
  String constructComponentName()
  {
    synchronized (PopupMenu.class)
    {
      return "popup" + nameCounter++;
    }
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if ((this.parent != null) && (!(this.parent instanceof Component)))
      {
        super.addNotify();
      }
      else
      {
        if (this.peer == null) {
          this.peer = Toolkit.getDefaultToolkit().createPopupMenu(this);
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
  }
  
  public void show(Component paramComponent, int paramInt1, int paramInt2)
  {
    MenuContainer localMenuContainer = this.parent;
    if (localMenuContainer == null) {
      throw new NullPointerException("parent is null");
    }
    if (!(localMenuContainer instanceof Component)) {
      throw new IllegalArgumentException("PopupMenus with non-Component parents cannot be shown");
    }
    Component localComponent = (Component)localMenuContainer;
    if (localComponent != paramComponent) {
      if ((localComponent instanceof Container))
      {
        if (!((Container)localComponent).isAncestorOf(paramComponent)) {
          throw new IllegalArgumentException("origin not in parent's hierarchy");
        }
      }
      else {
        throw new IllegalArgumentException("origin not in parent's hierarchy");
      }
    }
    if ((localComponent.getPeer() == null) || (!localComponent.isShowing())) {
      throw new RuntimeException("parent not showing on screen");
    }
    if (this.peer == null) {
      addNotify();
    }
    synchronized (getTreeLock())
    {
      if (this.peer != null) {
        ((PopupMenuPeer)this.peer).show(new Event(paramComponent, 0L, 501, paramInt1, paramInt2, 0, 0));
      }
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleAWTPopupMenu();
    }
    return this.accessibleContext;
  }
  
  static
  {
    AWTAccessor.setPopupMenuAccessor(new AWTAccessor.PopupMenuAccessor()
    {
      public boolean isTrayIconPopup(PopupMenu paramAnonymousPopupMenu)
      {
        return paramAnonymousPopupMenu.isTrayIconPopup;
      }
    });
  }
  
  protected class AccessibleAWTPopupMenu
    extends Menu.AccessibleAWTMenu
  {
    private static final long serialVersionUID = -4282044795947239955L;
    
    protected AccessibleAWTPopupMenu()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.POPUP_MENU;
    }
  }
}
