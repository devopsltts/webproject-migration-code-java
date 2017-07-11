package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ButtonModel;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.RootPaneUI;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicRootPaneUI
  extends RootPaneUI
  implements PropertyChangeListener
{
  private static RootPaneUI rootPaneUI = new BasicRootPaneUI();
  
  public BasicRootPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return rootPaneUI;
  }
  
  public void installUI(JComponent paramJComponent)
  {
    installDefaults((JRootPane)paramJComponent);
    installComponents((JRootPane)paramJComponent);
    installListeners((JRootPane)paramJComponent);
    installKeyboardActions((JRootPane)paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults((JRootPane)paramJComponent);
    uninstallComponents((JRootPane)paramJComponent);
    uninstallListeners((JRootPane)paramJComponent);
    uninstallKeyboardActions((JRootPane)paramJComponent);
  }
  
  protected void installDefaults(JRootPane paramJRootPane)
  {
    LookAndFeel.installProperty(paramJRootPane, "opaque", Boolean.FALSE);
  }
  
  protected void installComponents(JRootPane paramJRootPane) {}
  
  protected void installListeners(JRootPane paramJRootPane)
  {
    paramJRootPane.addPropertyChangeListener(this);
  }
  
  protected void installKeyboardActions(JRootPane paramJRootPane)
  {
    InputMap localInputMap = getInputMap(2, paramJRootPane);
    SwingUtilities.replaceUIInputMap(paramJRootPane, 2, localInputMap);
    localInputMap = getInputMap(1, paramJRootPane);
    SwingUtilities.replaceUIInputMap(paramJRootPane, 1, localInputMap);
    LazyActionMap.installLazyActionMap(paramJRootPane, BasicRootPaneUI.class, "RootPane.actionMap");
    updateDefaultButtonBindings(paramJRootPane);
  }
  
  protected void uninstallDefaults(JRootPane paramJRootPane) {}
  
  protected void uninstallComponents(JRootPane paramJRootPane) {}
  
  protected void uninstallListeners(JRootPane paramJRootPane)
  {
    paramJRootPane.removePropertyChangeListener(this);
  }
  
  protected void uninstallKeyboardActions(JRootPane paramJRootPane)
  {
    SwingUtilities.replaceUIInputMap(paramJRootPane, 2, null);
    SwingUtilities.replaceUIActionMap(paramJRootPane, null);
  }
  
  InputMap getInputMap(int paramInt, JComponent paramJComponent)
  {
    if (paramInt == 1) {
      return (InputMap)DefaultLookup.get(paramJComponent, this, "RootPane.ancestorInputMap");
    }
    if (paramInt == 2) {
      return createInputMap(paramInt, paramJComponent);
    }
    return null;
  }
  
  ComponentInputMap createInputMap(int paramInt, JComponent paramJComponent)
  {
    return new RootPaneInputMap(paramJComponent);
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("press"));
    paramLazyActionMap.put(new Actions("release"));
    paramLazyActionMap.put(new Actions("postPopup"));
  }
  
  void updateDefaultButtonBindings(JRootPane paramJRootPane)
  {
    for (InputMap localInputMap = SwingUtilities.getUIInputMap(paramJRootPane, 2); (localInputMap != null) && (!(localInputMap instanceof RootPaneInputMap)); localInputMap = localInputMap.getParent()) {}
    if (localInputMap != null)
    {
      localInputMap.clear();
      if (paramJRootPane.getDefaultButton() != null)
      {
        Object[] arrayOfObject = (Object[])DefaultLookup.get(paramJRootPane, this, "RootPane.defaultButtonWindowKeyBindings");
        if (arrayOfObject != null) {
          LookAndFeel.loadKeyBindings(localInputMap, arrayOfObject);
        }
      }
    }
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (paramPropertyChangeEvent.getPropertyName().equals("defaultButton"))
    {
      JRootPane localJRootPane = (JRootPane)paramPropertyChangeEvent.getSource();
      updateDefaultButtonBindings(localJRootPane);
      if (localJRootPane.getClientProperty("temporaryDefaultButton") == null) {
        localJRootPane.putClientProperty("initialDefaultButton", paramPropertyChangeEvent.getNewValue());
      }
    }
  }
  
  static class Actions
    extends UIAction
  {
    public static final String PRESS = "press";
    public static final String RELEASE = "release";
    public static final String POST_POPUP = "postPopup";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JRootPane localJRootPane = (JRootPane)paramActionEvent.getSource();
      JButton localJButton = localJRootPane.getDefaultButton();
      String str = getName();
      if (str == "postPopup")
      {
        Component localComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if ((localComponent instanceof JComponent))
        {
          JComponent localJComponent = (JComponent)localComponent;
          JPopupMenu localJPopupMenu = localJComponent.getComponentPopupMenu();
          if (localJPopupMenu != null)
          {
            Point localPoint = localJComponent.getPopupLocation(null);
            if (localPoint == null)
            {
              Rectangle localRectangle = localJComponent.getVisibleRect();
              localPoint = new Point(localRectangle.x + localRectangle.width / 2, localRectangle.y + localRectangle.height / 2);
            }
            localJPopupMenu.show(localComponent, localPoint.x, localPoint.y);
          }
        }
      }
      else if ((localJButton != null) && (SwingUtilities.getRootPane(localJButton) == localJRootPane) && (str == "press"))
      {
        localJButton.doClick(20);
      }
    }
    
    public boolean isEnabled(Object paramObject)
    {
      String str = getName();
      Object localObject;
      if (str == "postPopup")
      {
        localObject = MenuSelectionManager.defaultManager().getSelectedPath();
        if ((localObject != null) && (localObject.length != 0)) {
          return false;
        }
        Component localComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if ((localComponent instanceof JComponent))
        {
          JComponent localJComponent = (JComponent)localComponent;
          return localJComponent.getComponentPopupMenu() != null;
        }
        return false;
      }
      if ((paramObject != null) && ((paramObject instanceof JRootPane)))
      {
        localObject = ((JRootPane)paramObject).getDefaultButton();
        return (localObject != null) && (((JButton)localObject).getModel().isEnabled());
      }
      return true;
    }
  }
  
  private static class RootPaneInputMap
    extends ComponentInputMapUIResource
  {
    public RootPaneInputMap(JComponent paramJComponent)
    {
      super();
    }
  }
}
