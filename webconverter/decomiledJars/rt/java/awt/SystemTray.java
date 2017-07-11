package java.awt;

import java.awt.peer.SystemTrayPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.SystemTrayAccessor;
import sun.awt.AppContext;
import sun.awt.HeadlessToolkit;
import sun.awt.SunToolkit;
import sun.security.util.SecurityConstants.AWT;

public class SystemTray
{
  private static SystemTray systemTray;
  private int currentIconID = 0;
  private transient SystemTrayPeer peer;
  private static final TrayIcon[] EMPTY_TRAY_ARRAY = new TrayIcon[0];
  
  private SystemTray()
  {
    addNotify();
  }
  
  public static SystemTray getSystemTray()
  {
    
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    initializeSystemTrayIfNeeded();
    if (!isSupported()) {
      throw new UnsupportedOperationException("The system tray is not supported on the current platform.");
    }
    return systemTray;
  }
  
  public static boolean isSupported()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((localToolkit instanceof SunToolkit))
    {
      initializeSystemTrayIfNeeded();
      return ((SunToolkit)localToolkit).isTraySupported();
    }
    if ((localToolkit instanceof HeadlessToolkit)) {
      return ((HeadlessToolkit)localToolkit).isTraySupported();
    }
    return false;
  }
  
  public void add(TrayIcon paramTrayIcon)
    throws AWTException
  {
    if (paramTrayIcon == null) {
      throw new NullPointerException("adding null TrayIcon");
    }
    TrayIcon[] arrayOfTrayIcon1 = null;
    TrayIcon[] arrayOfTrayIcon2 = null;
    Vector localVector = null;
    synchronized (this)
    {
      arrayOfTrayIcon1 = systemTray.getTrayIcons();
      localVector = (Vector)AppContext.getAppContext().get(TrayIcon.class);
      if (localVector == null)
      {
        localVector = new Vector(3);
        AppContext.getAppContext().put(TrayIcon.class, localVector);
      }
      else if (localVector.contains(paramTrayIcon))
      {
        throw new IllegalArgumentException("adding TrayIcon that is already added");
      }
      localVector.add(paramTrayIcon);
      arrayOfTrayIcon2 = systemTray.getTrayIcons();
      paramTrayIcon.setID(++this.currentIconID);
    }
    try
    {
      paramTrayIcon.addNotify();
    }
    catch (AWTException localAWTException)
    {
      localVector.remove(paramTrayIcon);
      throw localAWTException;
    }
    firePropertyChange("trayIcons", arrayOfTrayIcon1, arrayOfTrayIcon2);
  }
  
  public void remove(TrayIcon paramTrayIcon)
  {
    if (paramTrayIcon == null) {
      return;
    }
    TrayIcon[] arrayOfTrayIcon1 = null;
    TrayIcon[] arrayOfTrayIcon2 = null;
    synchronized (this)
    {
      arrayOfTrayIcon1 = systemTray.getTrayIcons();
      Vector localVector = (Vector)AppContext.getAppContext().get(TrayIcon.class);
      if ((localVector == null) || (!localVector.remove(paramTrayIcon))) {
        return;
      }
      paramTrayIcon.removeNotify();
      arrayOfTrayIcon2 = systemTray.getTrayIcons();
    }
    firePropertyChange("trayIcons", arrayOfTrayIcon1, arrayOfTrayIcon2);
  }
  
  public TrayIcon[] getTrayIcons()
  {
    Vector localVector = (Vector)AppContext.getAppContext().get(TrayIcon.class);
    if (localVector != null) {
      return (TrayIcon[])localVector.toArray(new TrayIcon[localVector.size()]);
    }
    return EMPTY_TRAY_ARRAY;
  }
  
  public Dimension getTrayIconSize()
  {
    return this.peer.getTrayIconSize();
  }
  
  public synchronized void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    if (paramPropertyChangeListener == null) {
      return;
    }
    getCurrentChangeSupport().addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public synchronized void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    if (paramPropertyChangeListener == null) {
      return;
    }
    getCurrentChangeSupport().removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public synchronized PropertyChangeListener[] getPropertyChangeListeners(String paramString)
  {
    return getCurrentChangeSupport().getPropertyChangeListeners(paramString);
  }
  
  private void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 != null) && (paramObject2 != null) && (paramObject1.equals(paramObject2))) {
      return;
    }
    getCurrentChangeSupport().firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  private synchronized PropertyChangeSupport getCurrentChangeSupport()
  {
    PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(SystemTray.class);
    if (localPropertyChangeSupport == null)
    {
      localPropertyChangeSupport = new PropertyChangeSupport(this);
      AppContext.getAppContext().put(SystemTray.class, localPropertyChangeSupport);
    }
    return localPropertyChangeSupport;
  }
  
  synchronized void addNotify()
  {
    if (this.peer == null)
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      if ((localToolkit instanceof SunToolkit)) {
        this.peer = ((SunToolkit)Toolkit.getDefaultToolkit()).createSystemTray(this);
      } else if ((localToolkit instanceof HeadlessToolkit)) {
        this.peer = ((HeadlessToolkit)Toolkit.getDefaultToolkit()).createSystemTray(this);
      }
    }
  }
  
  static void checkSystemTrayAllowed()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.AWT.ACCESS_SYSTEM_TRAY_PERMISSION);
    }
  }
  
  private static void initializeSystemTrayIfNeeded()
  {
    synchronized (SystemTray.class)
    {
      if (systemTray == null) {
        systemTray = new SystemTray();
      }
    }
  }
  
  static
  {
    AWTAccessor.setSystemTrayAccessor(new AWTAccessor.SystemTrayAccessor()
    {
      public void firePropertyChange(SystemTray paramAnonymousSystemTray, String paramAnonymousString, Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        paramAnonymousSystemTray.firePropertyChange(paramAnonymousString, paramAnonymousObject1, paramAnonymousObject2);
      }
    });
  }
}
