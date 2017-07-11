package javax.swing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

class Autoscroller
  implements ActionListener
{
  private static Autoscroller sharedInstance = new Autoscroller();
  private static MouseEvent event;
  private static Timer timer;
  private static JComponent component;
  
  public static void stop(JComponent paramJComponent)
  {
    sharedInstance._stop(paramJComponent);
  }
  
  public static boolean isRunning(JComponent paramJComponent)
  {
    return sharedInstance._isRunning(paramJComponent);
  }
  
  public static void processMouseDragged(MouseEvent paramMouseEvent)
  {
    sharedInstance._processMouseDragged(paramMouseEvent);
  }
  
  Autoscroller() {}
  
  private void start(JComponent paramJComponent, MouseEvent paramMouseEvent)
  {
    Point localPoint = paramJComponent.getLocationOnScreen();
    if (component != paramJComponent) {
      _stop(component);
    }
    component = paramJComponent;
    event = new MouseEvent(component, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), paramMouseEvent.getX() + localPoint.x, paramMouseEvent.getY() + localPoint.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
    if (timer == null) {
      timer = new Timer(100, this);
    }
    if (!timer.isRunning()) {
      timer.start();
    }
  }
  
  private void _stop(JComponent paramJComponent)
  {
    if (component == paramJComponent)
    {
      if (timer != null) {
        timer.stop();
      }
      timer = null;
      event = null;
      component = null;
    }
  }
  
  private boolean _isRunning(JComponent paramJComponent)
  {
    return (paramJComponent == component) && (timer != null) && (timer.isRunning());
  }
  
  private void _processMouseDragged(MouseEvent paramMouseEvent)
  {
    JComponent localJComponent = (JComponent)paramMouseEvent.getComponent();
    boolean bool = true;
    if (localJComponent.isShowing())
    {
      Rectangle localRectangle = localJComponent.getVisibleRect();
      bool = localRectangle.contains(paramMouseEvent.getX(), paramMouseEvent.getY());
    }
    if (bool) {
      _stop(localJComponent);
    } else {
      start(localJComponent, paramMouseEvent);
    }
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    JComponent localJComponent = component;
    if ((localJComponent == null) || (!localJComponent.isShowing()) || (event == null))
    {
      _stop(localJComponent);
      return;
    }
    Point localPoint = localJComponent.getLocationOnScreen();
    MouseEvent localMouseEvent = new MouseEvent(localJComponent, event.getID(), event.getWhen(), event.getModifiers(), event.getX() - localPoint.x, event.getY() - localPoint.y, event.getXOnScreen(), event.getYOnScreen(), event.getClickCount(), event.isPopupTrigger(), 0);
    localJComponent.superProcessMouseMotionEvent(localMouseEvent);
  }
}
