package sun.awt;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.Window;
import java.awt.peer.ComponentPeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public abstract class KeyboardFocusManagerPeerImpl
  implements KeyboardFocusManagerPeer
{
  private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.focus.KeyboardFocusManagerPeerImpl");
  private static AWTAccessor.KeyboardFocusManagerAccessor kfmAccessor = AWTAccessor.getKeyboardFocusManagerAccessor();
  public static final int SNFH_FAILURE = 0;
  public static final int SNFH_SUCCESS_HANDLED = 1;
  public static final int SNFH_SUCCESS_PROCEED = 2;
  
  public KeyboardFocusManagerPeerImpl() {}
  
  public void clearGlobalFocusOwner(Window paramWindow)
  {
    if (paramWindow != null)
    {
      Component localComponent = paramWindow.getFocusOwner();
      if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
        focusLog.fine("Clearing global focus owner " + localComponent);
      }
      if (localComponent != null)
      {
        CausedFocusEvent localCausedFocusEvent = new CausedFocusEvent(localComponent, 1005, false, null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER);
        SunToolkit.postPriorityEvent(localCausedFocusEvent);
      }
    }
  }
  
  public static boolean shouldFocusOnClick(Component paramComponent)
  {
    int i = 0;
    if (((paramComponent instanceof Canvas)) || ((paramComponent instanceof Scrollbar)))
    {
      i = 1;
    }
    else if ((paramComponent instanceof Panel))
    {
      i = ((Panel)paramComponent).getComponentCount() == 0 ? 1 : 0;
    }
    else
    {
      Object localObject = paramComponent != null ? paramComponent.getPeer() : null;
      i = localObject != null ? localObject.isFocusable() : 0;
    }
    return (i != 0) && (AWTAccessor.getComponentAccessor().canBeFocusOwner(paramComponent));
  }
  
  public static boolean deliverFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause, Component paramComponent3)
  {
    if (paramComponent1 == null) {
      paramComponent1 = paramComponent2;
    }
    Component localComponent = paramComponent3;
    if ((localComponent != null) && (localComponent.getPeer() == null)) {
      localComponent = null;
    }
    if (localComponent != null)
    {
      localCausedFocusEvent = new CausedFocusEvent(localComponent, 1005, false, paramComponent1, paramCause);
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
        focusLog.finer("Posting focus event: " + localCausedFocusEvent);
      }
      SunToolkit.postEvent(SunToolkit.targetToAppContext(localComponent), localCausedFocusEvent);
    }
    CausedFocusEvent localCausedFocusEvent = new CausedFocusEvent(paramComponent1, 1004, false, localComponent, paramCause);
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      focusLog.finer("Posting focus event: " + localCausedFocusEvent);
    }
    SunToolkit.postEvent(SunToolkit.targetToAppContext(paramComponent1), localCausedFocusEvent);
    return true;
  }
  
  public static boolean requestFocusFor(Component paramComponent, CausedFocusEvent.Cause paramCause)
  {
    return AWTAccessor.getComponentAccessor().requestFocus(paramComponent, paramCause);
  }
  
  public static int shouldNativelyFocusHeavyweight(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
  {
    return kfmAccessor.shouldNativelyFocusHeavyweight(paramComponent1, paramComponent2, paramBoolean1, paramBoolean2, paramLong, paramCause);
  }
  
  public static void removeLastFocusRequest(Component paramComponent)
  {
    kfmAccessor.removeLastFocusRequest(paramComponent);
  }
  
  public static boolean processSynchronousLightweightTransfer(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    return kfmAccessor.processSynchronousLightweightTransfer(paramComponent1, paramComponent2, paramBoolean1, paramBoolean2, paramLong);
  }
}
