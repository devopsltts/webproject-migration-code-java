package javax.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MenuSelectionManager
{
  private Vector<MenuElement> selection = new Vector();
  private static final boolean TRACE = false;
  private static final boolean VERBOSE = false;
  private static final boolean DEBUG = false;
  private static final StringBuilder MENU_SELECTION_MANAGER_KEY = new StringBuilder("javax.swing.MenuSelectionManager");
  protected transient ChangeEvent changeEvent = null;
  protected EventListenerList listenerList = new EventListenerList();
  
  public MenuSelectionManager() {}
  
  public static MenuSelectionManager defaultManager()
  {
    synchronized (MENU_SELECTION_MANAGER_KEY)
    {
      AppContext localAppContext = AppContext.getAppContext();
      MenuSelectionManager localMenuSelectionManager = (MenuSelectionManager)localAppContext.get(MENU_SELECTION_MANAGER_KEY);
      if (localMenuSelectionManager == null)
      {
        localMenuSelectionManager = new MenuSelectionManager();
        localAppContext.put(MENU_SELECTION_MANAGER_KEY, localMenuSelectionManager);
        Object localObject1 = localAppContext.get(SwingUtilities2.MENU_SELECTION_MANAGER_LISTENER_KEY);
        if ((localObject1 != null) && ((localObject1 instanceof ChangeListener))) {
          localMenuSelectionManager.addChangeListener((ChangeListener)localObject1);
        }
      }
      return localMenuSelectionManager;
    }
  }
  
  public void setSelectedPath(MenuElement[] paramArrayOfMenuElement)
  {
    int k = this.selection.size();
    int m = 0;
    if (paramArrayOfMenuElement == null) {
      paramArrayOfMenuElement = new MenuElement[0];
    }
    int i = 0;
    int j = paramArrayOfMenuElement.length;
    while ((i < j) && (i < k) && (this.selection.elementAt(i) == paramArrayOfMenuElement[i]))
    {
      m++;
      i++;
    }
    for (i = k - 1; i >= m; i--)
    {
      MenuElement localMenuElement = (MenuElement)this.selection.elementAt(i);
      this.selection.removeElementAt(i);
      localMenuElement.menuSelectionChanged(false);
    }
    i = m;
    j = paramArrayOfMenuElement.length;
    while (i < j)
    {
      if (paramArrayOfMenuElement[i] != null)
      {
        this.selection.addElement(paramArrayOfMenuElement[i]);
        paramArrayOfMenuElement[i].menuSelectionChanged(true);
      }
      i++;
    }
    fireStateChanged();
  }
  
  public MenuElement[] getSelectedPath()
  {
    MenuElement[] arrayOfMenuElement = new MenuElement[this.selection.size()];
    int i = 0;
    int j = this.selection.size();
    while (i < j)
    {
      arrayOfMenuElement[i] = ((MenuElement)this.selection.elementAt(i));
      i++;
    }
    return arrayOfMenuElement;
  }
  
  public void clearSelectedPath()
  {
    if (this.selection.size() > 0) {
      setSelectedPath(null);
    }
  }
  
  public void addChangeListener(ChangeListener paramChangeListener)
  {
    this.listenerList.add(ChangeListener.class, paramChangeListener);
  }
  
  public void removeChangeListener(ChangeListener paramChangeListener)
  {
    this.listenerList.remove(ChangeListener.class, paramChangeListener);
  }
  
  public ChangeListener[] getChangeListeners()
  {
    return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
  }
  
  protected void fireStateChanged()
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class)
      {
        if (this.changeEvent == null) {
          this.changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener)arrayOfObject[(i + 1)]).stateChanged(this.changeEvent);
      }
    }
  }
  
  public void processMouseEvent(MouseEvent paramMouseEvent)
  {
    Point localPoint = paramMouseEvent.getPoint();
    Component localComponent2 = paramMouseEvent.getComponent();
    if ((localComponent2 != null) && (!localComponent2.isShowing())) {
      return;
    }
    int i4 = paramMouseEvent.getID();
    int i5 = paramMouseEvent.getModifiers();
    if (((i4 == 504) || (i4 == 505)) && ((i5 & 0x1C) != 0)) {
      return;
    }
    if (localComponent2 != null) {
      SwingUtilities.convertPointToScreen(localPoint, localComponent2);
    }
    int i = localPoint.x;
    int j = localPoint.y;
    Vector localVector = (Vector)this.selection.clone();
    int i3 = localVector.size();
    int i6 = 0;
    for (int k = i3 - 1; (k >= 0) && (i6 == 0); k--)
    {
      MenuElement localMenuElement = (MenuElement)localVector.elementAt(k);
      MenuElement[] arrayOfMenuElement1 = localMenuElement.getSubElements();
      MenuElement[] arrayOfMenuElement2 = null;
      int m = 0;
      int n = arrayOfMenuElement1.length;
      while ((m < n) && (i6 == 0))
      {
        if (arrayOfMenuElement1[m] != null)
        {
          Component localComponent1 = arrayOfMenuElement1[m].getComponent();
          if (localComponent1.isShowing())
          {
            int i1;
            int i2;
            if ((localComponent1 instanceof JComponent))
            {
              i1 = localComponent1.getWidth();
              i2 = localComponent1.getHeight();
            }
            else
            {
              Rectangle localRectangle = localComponent1.getBounds();
              i1 = localRectangle.width;
              i2 = localRectangle.height;
            }
            localPoint.x = i;
            localPoint.y = j;
            SwingUtilities.convertPointFromScreen(localPoint, localComponent1);
            if ((localPoint.x >= 0) && (localPoint.x < i1) && (localPoint.y >= 0) && (localPoint.y < i2))
            {
              if (arrayOfMenuElement2 == null)
              {
                arrayOfMenuElement2 = new MenuElement[k + 2];
                for (int i7 = 0; i7 <= k; i7++) {
                  arrayOfMenuElement2[i7] = ((MenuElement)localVector.elementAt(i7));
                }
              }
              arrayOfMenuElement2[(k + 1)] = arrayOfMenuElement1[m];
              MenuElement[] arrayOfMenuElement3 = getSelectedPath();
              if ((arrayOfMenuElement3[(arrayOfMenuElement3.length - 1)] != arrayOfMenuElement2[(k + 1)]) && ((arrayOfMenuElement3.length < 2) || (arrayOfMenuElement3[(arrayOfMenuElement3.length - 2)] != arrayOfMenuElement2[(k + 1)])))
              {
                localObject = arrayOfMenuElement3[(arrayOfMenuElement3.length - 1)].getComponent();
                MouseEvent localMouseEvent1 = new MouseEvent((Component)localObject, 505, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), localPoint.x, localPoint.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
                arrayOfMenuElement3[(arrayOfMenuElement3.length - 1)].processMouseEvent(localMouseEvent1, arrayOfMenuElement2, this);
                MouseEvent localMouseEvent2 = new MouseEvent(localComponent1, 504, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), localPoint.x, localPoint.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
                arrayOfMenuElement1[m].processMouseEvent(localMouseEvent2, arrayOfMenuElement2, this);
              }
              Object localObject = new MouseEvent(localComponent1, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), localPoint.x, localPoint.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
              arrayOfMenuElement1[m].processMouseEvent((MouseEvent)localObject, arrayOfMenuElement2, this);
              i6 = 1;
              paramMouseEvent.consume();
            }
          }
        }
        m++;
      }
    }
  }
  
  private void printMenuElementArray(MenuElement[] paramArrayOfMenuElement)
  {
    printMenuElementArray(paramArrayOfMenuElement, false);
  }
  
  private void printMenuElementArray(MenuElement[] paramArrayOfMenuElement, boolean paramBoolean)
  {
    System.out.println("Path is(");
    int i = 0;
    int j = paramArrayOfMenuElement.length;
    while (i < j)
    {
      for (int k = 0; k <= i; k++) {
        System.out.print("  ");
      }
      MenuElement localMenuElement = paramArrayOfMenuElement[i];
      if ((localMenuElement instanceof JMenuItem)) {
        System.out.println(((JMenuItem)localMenuElement).getText() + ", ");
      } else if ((localMenuElement instanceof JMenuBar)) {
        System.out.println("JMenuBar, ");
      } else if ((localMenuElement instanceof JPopupMenu)) {
        System.out.println("JPopupMenu, ");
      } else if (localMenuElement == null) {
        System.out.println("NULL , ");
      } else {
        System.out.println("" + localMenuElement + ", ");
      }
      i++;
    }
    System.out.println(")");
    if (paramBoolean == true) {
      Thread.dumpStack();
    }
  }
  
  public Component componentForPoint(Component paramComponent, Point paramPoint)
  {
    Point localPoint = paramPoint;
    SwingUtilities.convertPointToScreen(localPoint, paramComponent);
    int i = localPoint.x;
    int j = localPoint.y;
    Vector localVector = (Vector)this.selection.clone();
    int i3 = localVector.size();
    for (int k = i3 - 1; k >= 0; k--)
    {
      MenuElement localMenuElement = (MenuElement)localVector.elementAt(k);
      MenuElement[] arrayOfMenuElement = localMenuElement.getSubElements();
      int m = 0;
      int n = arrayOfMenuElement.length;
      while (m < n)
      {
        if (arrayOfMenuElement[m] != null)
        {
          Component localComponent = arrayOfMenuElement[m].getComponent();
          if (localComponent.isShowing())
          {
            int i1;
            int i2;
            if ((localComponent instanceof JComponent))
            {
              i1 = localComponent.getWidth();
              i2 = localComponent.getHeight();
            }
            else
            {
              Rectangle localRectangle = localComponent.getBounds();
              i1 = localRectangle.width;
              i2 = localRectangle.height;
            }
            localPoint.x = i;
            localPoint.y = j;
            SwingUtilities.convertPointFromScreen(localPoint, localComponent);
            if ((localPoint.x >= 0) && (localPoint.x < i1) && (localPoint.y >= 0) && (localPoint.y < i2)) {
              return localComponent;
            }
          }
        }
        m++;
      }
    }
    return null;
  }
  
  public void processKeyEvent(KeyEvent paramKeyEvent)
  {
    MenuElement[] arrayOfMenuElement1 = new MenuElement[0];
    arrayOfMenuElement1 = (MenuElement[])this.selection.toArray(arrayOfMenuElement1);
    int i = arrayOfMenuElement1.length;
    if (i < 1) {
      return;
    }
    for (int j = i - 1; j >= 0; j--)
    {
      MenuElement localMenuElement = arrayOfMenuElement1[j];
      MenuElement[] arrayOfMenuElement3 = localMenuElement.getSubElements();
      arrayOfMenuElement2 = null;
      for (int k = 0; k < arrayOfMenuElement3.length; k++) {
        if ((arrayOfMenuElement3[k] != null) && (arrayOfMenuElement3[k].getComponent().isShowing()) && (arrayOfMenuElement3[k].getComponent().isEnabled()))
        {
          if (arrayOfMenuElement2 == null)
          {
            arrayOfMenuElement2 = new MenuElement[j + 2];
            System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement2, 0, j + 1);
          }
          arrayOfMenuElement2[(j + 1)] = arrayOfMenuElement3[k];
          arrayOfMenuElement3[k].processKeyEvent(paramKeyEvent, arrayOfMenuElement2, this);
          if (paramKeyEvent.isConsumed()) {
            return;
          }
        }
      }
    }
    MenuElement[] arrayOfMenuElement2 = new MenuElement[1];
    arrayOfMenuElement2[0] = arrayOfMenuElement1[0];
    arrayOfMenuElement2[0].processKeyEvent(paramKeyEvent, arrayOfMenuElement2, this);
    if (paramKeyEvent.isConsumed()) {}
  }
  
  public boolean isComponentPartOfCurrentMenu(Component paramComponent)
  {
    if (this.selection.size() > 0)
    {
      MenuElement localMenuElement = (MenuElement)this.selection.elementAt(0);
      return isComponentPartOfCurrentMenu(localMenuElement, paramComponent);
    }
    return false;
  }
  
  private boolean isComponentPartOfCurrentMenu(MenuElement paramMenuElement, Component paramComponent)
  {
    if (paramMenuElement == null) {
      return false;
    }
    if (paramMenuElement.getComponent() == paramComponent) {
      return true;
    }
    MenuElement[] arrayOfMenuElement = paramMenuElement.getSubElements();
    int i = 0;
    int j = arrayOfMenuElement.length;
    while (i < j)
    {
      if (isComponentPartOfCurrentMenu(arrayOfMenuElement[i], paramComponent)) {
        return true;
      }
      i++;
    }
    return false;
  }
}
