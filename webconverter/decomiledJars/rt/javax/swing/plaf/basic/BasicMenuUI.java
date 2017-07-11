package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicMenuUI
  extends BasicMenuItemUI
{
  protected ChangeListener changeListener;
  protected MenuListener menuListener;
  private int lastMnemonic = 0;
  private InputMap selectedWindowInputMap;
  private static final boolean TRACE = false;
  private static final boolean VERBOSE = false;
  private static final boolean DEBUG = false;
  private static boolean crossMenuMnemonic = true;
  
  public BasicMenuUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicMenuUI();
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    BasicMenuItemUI.loadActionMap(paramLazyActionMap);
    paramLazyActionMap.put(new Actions("selectMenu", null, true));
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    updateDefaultBackgroundColor();
    ((JMenu)this.menuItem).setDelay(200);
    crossMenuMnemonic = UIManager.getBoolean("Menu.crossMenuMnemonic");
  }
  
  protected String getPropertyPrefix()
  {
    return "Menu";
  }
  
  protected void installListeners()
  {
    super.installListeners();
    if (this.changeListener == null) {
      this.changeListener = createChangeListener(this.menuItem);
    }
    if (this.changeListener != null) {
      this.menuItem.addChangeListener(this.changeListener);
    }
    if (this.menuListener == null) {
      this.menuListener = createMenuListener(this.menuItem);
    }
    if (this.menuListener != null) {
      ((JMenu)this.menuItem).addMenuListener(this.menuListener);
    }
  }
  
  protected void installKeyboardActions()
  {
    super.installKeyboardActions();
    updateMnemonicBinding();
  }
  
  void installLazyActionMap()
  {
    LazyActionMap.installLazyActionMap(this.menuItem, BasicMenuUI.class, getPropertyPrefix() + ".actionMap");
  }
  
  void updateMnemonicBinding()
  {
    int i = this.menuItem.getModel().getMnemonic();
    int[] arrayOfInt1 = (int[])DefaultLookup.get(this.menuItem, this, "Menu.shortcutKeys");
    if (arrayOfInt1 == null) {
      arrayOfInt1 = new int[] { 8 };
    }
    if (i == this.lastMnemonic) {
      return;
    }
    InputMap localInputMap = SwingUtilities.getUIInputMap(this.menuItem, 2);
    int m;
    if ((this.lastMnemonic != 0) && (localInputMap != null)) {
      for (m : arrayOfInt1) {
        localInputMap.remove(KeyStroke.getKeyStroke(this.lastMnemonic, m, false));
      }
    }
    if (i != 0)
    {
      if (localInputMap == null)
      {
        localInputMap = createInputMap(2);
        SwingUtilities.replaceUIInputMap(this.menuItem, 2, localInputMap);
      }
      for (m : arrayOfInt1) {
        localInputMap.put(KeyStroke.getKeyStroke(i, m, false), "selectMenu");
      }
    }
    this.lastMnemonic = i;
  }
  
  protected void uninstallKeyboardActions()
  {
    super.uninstallKeyboardActions();
    this.lastMnemonic = 0;
  }
  
  protected MouseInputListener createMouseInputListener(JComponent paramJComponent)
  {
    return getHandler();
  }
  
  protected MenuListener createMenuListener(JComponent paramJComponent)
  {
    return null;
  }
  
  protected ChangeListener createChangeListener(JComponent paramJComponent)
  {
    return null;
  }
  
  protected PropertyChangeListener createPropertyChangeListener(JComponent paramJComponent)
  {
    return getHandler();
  }
  
  BasicMenuItemUI.Handler getHandler()
  {
    if (this.handler == null) {
      this.handler = new Handler(null);
    }
    return this.handler;
  }
  
  protected void uninstallDefaults()
  {
    this.menuItem.setArmed(false);
    this.menuItem.setSelected(false);
    this.menuItem.resetKeyboardActions();
    super.uninstallDefaults();
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    if (this.changeListener != null) {
      this.menuItem.removeChangeListener(this.changeListener);
    }
    if (this.menuListener != null) {
      ((JMenu)this.menuItem).removeMenuListener(this.menuListener);
    }
    this.changeListener = null;
    this.menuListener = null;
    this.handler = null;
  }
  
  protected MenuDragMouseListener createMenuDragMouseListener(JComponent paramJComponent)
  {
    return getHandler();
  }
  
  protected MenuKeyListener createMenuKeyListener(JComponent paramJComponent)
  {
    return (MenuKeyListener)getHandler();
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    if (((JMenu)this.menuItem).isTopLevelMenu() == true)
    {
      Dimension localDimension = paramJComponent.getPreferredSize();
      return new Dimension(localDimension.width, 32767);
    }
    return null;
  }
  
  protected void setupPostTimer(JMenu paramJMenu)
  {
    Timer localTimer = new Timer(paramJMenu.getDelay(), new Actions("selectMenu", paramJMenu, false));
    localTimer.setRepeats(false);
    localTimer.start();
  }
  
  private static void appendPath(MenuElement[] paramArrayOfMenuElement, MenuElement paramMenuElement)
  {
    MenuElement[] arrayOfMenuElement = new MenuElement[paramArrayOfMenuElement.length + 1];
    System.arraycopy(paramArrayOfMenuElement, 0, arrayOfMenuElement, 0, paramArrayOfMenuElement.length);
    arrayOfMenuElement[paramArrayOfMenuElement.length] = paramMenuElement;
    MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement);
  }
  
  private void updateDefaultBackgroundColor()
  {
    if (!UIManager.getBoolean("Menu.useMenuBarBackgroundForTopLevel")) {
      return;
    }
    JMenu localJMenu = (JMenu)this.menuItem;
    if ((localJMenu.getBackground() instanceof UIResource)) {
      if (localJMenu.isTopLevelMenu()) {
        localJMenu.setBackground(UIManager.getColor("MenuBar.background"));
      } else {
        localJMenu.setBackground(UIManager.getColor(getPropertyPrefix() + ".background"));
      }
    }
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String SELECT = "selectMenu";
    private JMenu menu;
    private boolean force = false;
    
    Actions(String paramString, JMenu paramJMenu, boolean paramBoolean)
    {
      super();
      this.menu = paramJMenu;
      this.force = paramBoolean;
    }
    
    private JMenu getMenu(ActionEvent paramActionEvent)
    {
      if ((paramActionEvent.getSource() instanceof JMenu)) {
        return (JMenu)paramActionEvent.getSource();
      }
      return this.menu;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JMenu localJMenu = getMenu(paramActionEvent);
      if (!BasicMenuUI.crossMenuMnemonic)
      {
        localObject1 = BasicPopupMenuUI.getLastPopup();
        if ((localObject1 != null) && (localObject1 != localJMenu.getParent())) {
          return;
        }
      }
      Object localObject1 = MenuSelectionManager.defaultManager();
      Object localObject2;
      if (this.force)
      {
        localObject2 = localJMenu.getParent();
        if ((localObject2 != null) && ((localObject2 instanceof JMenuBar)))
        {
          MenuElement[] arrayOfMenuElement2 = localJMenu.getPopupMenu().getSubElements();
          MenuElement[] arrayOfMenuElement1;
          if (arrayOfMenuElement2.length > 0)
          {
            arrayOfMenuElement1 = new MenuElement[4];
            arrayOfMenuElement1[0] = ((MenuElement)localObject2);
            arrayOfMenuElement1[1] = localJMenu;
            arrayOfMenuElement1[2] = localJMenu.getPopupMenu();
            arrayOfMenuElement1[3] = arrayOfMenuElement2[0];
          }
          else
          {
            arrayOfMenuElement1 = new MenuElement[3];
            arrayOfMenuElement1[0] = ((MenuElement)localObject2);
            arrayOfMenuElement1[1] = localJMenu;
            arrayOfMenuElement1[2] = localJMenu.getPopupMenu();
          }
          ((MenuSelectionManager)localObject1).setSelectedPath(arrayOfMenuElement1);
        }
      }
      else
      {
        localObject2 = ((MenuSelectionManager)localObject1).getSelectedPath();
        if ((localObject2.length > 0) && (localObject2[(localObject2.length - 1)] == localJMenu)) {
          BasicMenuUI.appendPath((MenuElement[])localObject2, localJMenu.getPopupMenu());
        }
      }
    }
    
    public boolean isEnabled(Object paramObject)
    {
      if ((paramObject instanceof JMenu)) {
        return ((JMenu)paramObject).isEnabled();
      }
      return true;
    }
  }
  
  public class ChangeHandler
    implements ChangeListener
  {
    public JMenu menu;
    public BasicMenuUI ui;
    public boolean isSelected = false;
    public Component wasFocused;
    
    public ChangeHandler(JMenu paramJMenu, BasicMenuUI paramBasicMenuUI)
    {
      this.menu = paramJMenu;
      this.ui = paramBasicMenuUI;
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent) {}
  }
  
  private class Handler
    extends BasicMenuItemUI.Handler
    implements MenuKeyListener
  {
    private Handler()
    {
      super();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (paramPropertyChangeEvent.getPropertyName() == "mnemonic")
      {
        BasicMenuUI.this.updateMnemonicBinding();
      }
      else
      {
        if (paramPropertyChangeEvent.getPropertyName().equals("ancestor")) {
          BasicMenuUI.this.updateDefaultBackgroundColor();
        }
        super.propertyChange(paramPropertyChangeEvent);
      }
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      JMenu localJMenu = (JMenu)BasicMenuUI.this.menuItem;
      if (!localJMenu.isEnabled()) {
        return;
      }
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      if (localJMenu.isTopLevelMenu()) {
        if ((localJMenu.isSelected()) && (localJMenu.getPopupMenu().isShowing()))
        {
          localMenuSelectionManager.clearSelectedPath();
        }
        else
        {
          localObject = localJMenu.getParent();
          if ((localObject != null) && ((localObject instanceof JMenuBar)))
          {
            MenuElement[] arrayOfMenuElement = new MenuElement[2];
            arrayOfMenuElement[0] = ((MenuElement)localObject);
            arrayOfMenuElement[1] = localJMenu;
            localMenuSelectionManager.setSelectedPath(arrayOfMenuElement);
          }
        }
      }
      Object localObject = localMenuSelectionManager.getSelectedPath();
      if ((localObject.length > 0) && (localObject[(localObject.length - 1)] != localJMenu.getPopupMenu())) {
        if ((localJMenu.isTopLevelMenu()) || (localJMenu.getDelay() == 0)) {
          BasicMenuUI.appendPath((MenuElement[])localObject, localJMenu.getPopupMenu());
        } else {
          BasicMenuUI.this.setupPostTimer(localJMenu);
        }
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      JMenu localJMenu = (JMenu)BasicMenuUI.this.menuItem;
      if (!localJMenu.isEnabled()) {
        return;
      }
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      localMenuSelectionManager.processMouseEvent(paramMouseEvent);
      if (!paramMouseEvent.isConsumed()) {
        localMenuSelectionManager.clearSelectedPath();
      }
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      JMenu localJMenu = (JMenu)BasicMenuUI.this.menuItem;
      if ((!localJMenu.isEnabled()) && (!UIManager.getBoolean("MenuItem.disabledAreNavigable"))) {
        return;
      }
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement1 = localMenuSelectionManager.getSelectedPath();
      if (!localJMenu.isTopLevelMenu())
      {
        if ((arrayOfMenuElement1.length <= 0) || (arrayOfMenuElement1[(arrayOfMenuElement1.length - 1)] != localJMenu.getPopupMenu())) {
          if (localJMenu.getDelay() == 0)
          {
            BasicMenuUI.appendPath(BasicMenuUI.this.getPath(), localJMenu.getPopupMenu());
          }
          else
          {
            localMenuSelectionManager.setSelectedPath(BasicMenuUI.this.getPath());
            BasicMenuUI.this.setupPostTimer(localJMenu);
          }
        }
      }
      else if ((arrayOfMenuElement1.length > 0) && (arrayOfMenuElement1[0] == localJMenu.getParent()))
      {
        MenuElement[] arrayOfMenuElement2 = new MenuElement[3];
        arrayOfMenuElement2[0] = ((MenuElement)localJMenu.getParent());
        arrayOfMenuElement2[1] = localJMenu;
        if (BasicPopupMenuUI.getLastPopup() != null) {
          arrayOfMenuElement2[2] = localJMenu.getPopupMenu();
        }
        localMenuSelectionManager.setSelectedPath(arrayOfMenuElement2);
      }
    }
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      JMenu localJMenu = (JMenu)BasicMenuUI.this.menuItem;
      if (!localJMenu.isEnabled()) {
        return;
      }
      MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent);
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
    
    public void menuDragMouseEntered(MenuDragMouseEvent paramMenuDragMouseEvent) {}
    
    public void menuDragMouseDragged(MenuDragMouseEvent paramMenuDragMouseEvent)
    {
      if (!BasicMenuUI.this.menuItem.isEnabled()) {
        return;
      }
      MenuSelectionManager localMenuSelectionManager = paramMenuDragMouseEvent.getMenuSelectionManager();
      MenuElement[] arrayOfMenuElement1 = paramMenuDragMouseEvent.getPath();
      Point localPoint = paramMenuDragMouseEvent.getPoint();
      Object localObject;
      if ((localPoint.x >= 0) && (localPoint.x < BasicMenuUI.this.menuItem.getWidth()) && (localPoint.y >= 0) && (localPoint.y < BasicMenuUI.this.menuItem.getHeight()))
      {
        localObject = (JMenu)BasicMenuUI.this.menuItem;
        MenuElement[] arrayOfMenuElement2 = localMenuSelectionManager.getSelectedPath();
        if ((arrayOfMenuElement2.length <= 0) || (arrayOfMenuElement2[(arrayOfMenuElement2.length - 1)] != ((JMenu)localObject).getPopupMenu())) {
          if ((((JMenu)localObject).isTopLevelMenu()) || (((JMenu)localObject).getDelay() == 0) || (paramMenuDragMouseEvent.getID() == 506))
          {
            BasicMenuUI.appendPath(arrayOfMenuElement1, ((JMenu)localObject).getPopupMenu());
          }
          else
          {
            localMenuSelectionManager.setSelectedPath(arrayOfMenuElement1);
            BasicMenuUI.this.setupPostTimer((JMenu)localObject);
          }
        }
      }
      else if (paramMenuDragMouseEvent.getID() == 502)
      {
        localObject = localMenuSelectionManager.componentForPoint(paramMenuDragMouseEvent.getComponent(), paramMenuDragMouseEvent.getPoint());
        if (localObject == null) {
          localMenuSelectionManager.clearSelectedPath();
        }
      }
    }
    
    public void menuDragMouseExited(MenuDragMouseEvent paramMenuDragMouseEvent) {}
    
    public void menuDragMouseReleased(MenuDragMouseEvent paramMenuDragMouseEvent) {}
    
    public void menuKeyTyped(MenuKeyEvent paramMenuKeyEvent)
    {
      if ((!BasicMenuUI.crossMenuMnemonic) && (BasicPopupMenuUI.getLastPopup() != null)) {
        return;
      }
      if (BasicPopupMenuUI.getPopups().size() != 0) {
        return;
      }
      int i = Character.toLowerCase((char)BasicMenuUI.this.menuItem.getMnemonic());
      MenuElement[] arrayOfMenuElement1 = paramMenuKeyEvent.getPath();
      if (i == Character.toLowerCase(paramMenuKeyEvent.getKeyChar()))
      {
        JPopupMenu localJPopupMenu = ((JMenu)BasicMenuUI.this.menuItem).getPopupMenu();
        ArrayList localArrayList = new ArrayList(Arrays.asList(arrayOfMenuElement1));
        localArrayList.add(localJPopupMenu);
        MenuElement[] arrayOfMenuElement2 = localJPopupMenu.getSubElements();
        MenuElement localMenuElement = BasicPopupMenuUI.findEnabledChild(arrayOfMenuElement2, -1, true);
        if (localMenuElement != null) {
          localArrayList.add(localMenuElement);
        }
        MenuSelectionManager localMenuSelectionManager = paramMenuKeyEvent.getMenuSelectionManager();
        MenuElement[] arrayOfMenuElement3 = new MenuElement[0];
        arrayOfMenuElement3 = (MenuElement[])localArrayList.toArray(arrayOfMenuElement3);
        localMenuSelectionManager.setSelectedPath(arrayOfMenuElement3);
        paramMenuKeyEvent.consume();
      }
    }
    
    public void menuKeyPressed(MenuKeyEvent paramMenuKeyEvent) {}
    
    public void menuKeyReleased(MenuKeyEvent paramMenuKeyEvent) {}
  }
  
  protected class MouseInputHandler
    implements MouseInputListener
  {
    protected MouseInputHandler() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      BasicMenuUI.this.getHandler().mouseClicked(paramMouseEvent);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      BasicMenuUI.this.getHandler().mousePressed(paramMouseEvent);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      BasicMenuUI.this.getHandler().mouseReleased(paramMouseEvent);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      BasicMenuUI.this.getHandler().mouseEntered(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      BasicMenuUI.this.getHandler().mouseExited(paramMouseEvent);
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      BasicMenuUI.this.getHandler().mouseDragged(paramMouseEvent);
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      BasicMenuUI.this.getHandler().mouseMoved(paramMouseEvent);
    }
  }
}
