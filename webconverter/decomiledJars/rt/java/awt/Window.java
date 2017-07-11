package java.awt;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.geom.Path2D.Float;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.im.InputContext;
import java.awt.image.BufferStrategy;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.WindowAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.SunToolkit;
import sun.awt.util.IdentityArrayList;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.pipe.Region;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants.AWT;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class Window
  extends Container
  implements Accessible
{
  String warningString;
  transient List<Image> icons;
  private transient Component temporaryLostComponent;
  static boolean systemSyncLWRequests = false;
  boolean syncLWRequests = false;
  transient boolean beforeFirstShow = true;
  private transient boolean disposing = false;
  transient WindowDisposerRecord disposerRecord = null;
  static final int OPENED = 1;
  int state;
  private boolean alwaysOnTop;
  private static final IdentityArrayList<Window> allWindows = new IdentityArrayList();
  transient Vector<WeakReference<Window>> ownedWindowList = new Vector();
  private transient WeakReference<Window> weakThis;
  transient boolean showWithParent;
  transient Dialog modalBlocker;
  Dialog.ModalExclusionType modalExclusionType;
  transient WindowListener windowListener;
  transient WindowStateListener windowStateListener;
  transient WindowFocusListener windowFocusListener;
  transient InputContext inputContext;
  private transient Object inputContextLock = new Object();
  private FocusManager focusMgr;
  private boolean focusableWindowState = true;
  private volatile boolean autoRequestFocus = true;
  transient boolean isInShow = false;
  private float opacity = 1.0F;
  private Shape shape = null;
  private static final String base = "win";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 4497834738069338734L;
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.Window");
  private static final boolean locationByPlatformProp;
  transient boolean isTrayIconWindow = false;
  private volatile transient int securityWarningWidth = 0;
  private volatile transient int securityWarningHeight = 0;
  private transient double securityWarningPointX = 2.0D;
  private transient double securityWarningPointY = 0.0D;
  private transient float securityWarningAlignmentX = 1.0F;
  private transient float securityWarningAlignmentY = 0.0F;
  transient Object anchor = new Object();
  private static final AtomicBoolean beforeFirstWindowShown;
  private Type type = Type.NORMAL;
  private int windowSerializedDataVersion = 2;
  private boolean locationByPlatform = locationByPlatformProp;
  
  private static native void initIDs();
  
  Window(GraphicsConfiguration paramGraphicsConfiguration)
  {
    init(paramGraphicsConfiguration);
  }
  
  private GraphicsConfiguration initGC(GraphicsConfiguration paramGraphicsConfiguration)
  {
    
    if (paramGraphicsConfiguration == null) {
      paramGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    setGraphicsConfiguration(paramGraphicsConfiguration);
    return paramGraphicsConfiguration;
  }
  
  private void init(GraphicsConfiguration paramGraphicsConfiguration)
  {
    GraphicsEnvironment.checkHeadless();
    this.syncLWRequests = systemSyncLWRequests;
    this.weakThis = new WeakReference(this);
    addToWindowList();
    setWarningString();
    this.cursor = Cursor.getPredefinedCursor(0);
    this.visible = false;
    paramGraphicsConfiguration = initGC(paramGraphicsConfiguration);
    if (paramGraphicsConfiguration.getDevice().getType() != 0) {
      throw new IllegalArgumentException("not a screen device");
    }
    setLayout(new BorderLayout());
    Rectangle localRectangle = paramGraphicsConfiguration.getBounds();
    Insets localInsets = getToolkit().getScreenInsets(paramGraphicsConfiguration);
    int i = getX() + localRectangle.x + localInsets.left;
    int j = getY() + localRectangle.y + localInsets.top;
    if ((i != this.x) || (j != this.y))
    {
      setLocation(i, j);
      setLocationByPlatform(locationByPlatformProp);
    }
    this.modalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
    this.disposerRecord = new WindowDisposerRecord(this.appContext, this);
    Disposer.addRecord(this.anchor, this.disposerRecord);
    SunToolkit.checkAndSetPolicy(this);
  }
  
  Window()
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    init((GraphicsConfiguration)null);
  }
  
  public Window(Frame paramFrame)
  {
    this(paramFrame == null ? (GraphicsConfiguration)null : paramFrame.getGraphicsConfiguration());
    ownedInit(paramFrame);
  }
  
  public Window(Window paramWindow)
  {
    this(paramWindow == null ? (GraphicsConfiguration)null : paramWindow.getGraphicsConfiguration());
    ownedInit(paramWindow);
  }
  
  public Window(Window paramWindow, GraphicsConfiguration paramGraphicsConfiguration)
  {
    this(paramGraphicsConfiguration);
    ownedInit(paramWindow);
  }
  
  private void ownedInit(Window paramWindow)
  {
    this.parent = paramWindow;
    if (paramWindow != null)
    {
      paramWindow.addOwnedWindow(this.weakThis);
      if (paramWindow.isAlwaysOnTop()) {
        try
        {
          setAlwaysOnTop(true);
        }
        catch (SecurityException localSecurityException) {}
      }
    }
    this.disposerRecord.updateOwner();
  }
  
  String constructComponentName()
  {
    synchronized (Window.class)
    {
      return "win" + nameCounter++;
    }
  }
  
  public List<Image> getIconImages()
  {
    List localList = this.icons;
    if ((localList == null) || (localList.size() == 0)) {
      return new ArrayList();
    }
    return new ArrayList(localList);
  }
  
  public synchronized void setIconImages(List<? extends Image> paramList)
  {
    this.icons = (paramList == null ? new ArrayList() : new ArrayList(paramList));
    WindowPeer localWindowPeer = (WindowPeer)this.peer;
    if (localWindowPeer != null) {
      localWindowPeer.updateIconImages();
    }
    firePropertyChange("iconImage", null, null);
  }
  
  public void setIconImage(Image paramImage)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramImage != null) {
      localArrayList.add(paramImage);
    }
    setIconImages(localArrayList);
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      Container localContainer = this.parent;
      if ((localContainer != null) && (localContainer.getPeer() == null)) {
        localContainer.addNotify();
      }
      if (this.peer == null) {
        this.peer = getToolkit().createWindow(this);
      }
      synchronized (allWindows)
      {
        allWindows.add(this);
      }
      super.addNotify();
    }
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      synchronized (allWindows)
      {
        allWindows.remove(this);
      }
      super.removeNotify();
    }
  }
  
  public void pack()
  {
    Container localContainer = this.parent;
    if ((localContainer != null) && (localContainer.getPeer() == null)) {
      localContainer.addNotify();
    }
    if (this.peer == null) {
      addNotify();
    }
    Dimension localDimension = getPreferredSize();
    if (this.peer != null) {
      setClientSize(localDimension.width, localDimension.height);
    }
    if (this.beforeFirstShow) {
      this.isPacked = true;
    }
    validateUnconditionally();
  }
  
  public void setMinimumSize(Dimension paramDimension)
  {
    synchronized (getTreeLock())
    {
      super.setMinimumSize(paramDimension);
      Dimension localDimension = getSize();
      if ((isMinimumSizeSet()) && ((localDimension.width < paramDimension.width) || (localDimension.height < paramDimension.height)))
      {
        int i = Math.max(this.width, paramDimension.width);
        int j = Math.max(this.height, paramDimension.height);
        setSize(i, j);
      }
      if (this.peer != null) {
        ((WindowPeer)this.peer).updateMinimumSize();
      }
    }
  }
  
  public void setSize(Dimension paramDimension)
  {
    super.setSize(paramDimension);
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    super.setSize(paramInt1, paramInt2);
  }
  
  public void setLocation(int paramInt1, int paramInt2)
  {
    super.setLocation(paramInt1, paramInt2);
  }
  
  public void setLocation(Point paramPoint)
  {
    super.setLocation(paramPoint);
  }
  
  @Deprecated
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (isMinimumSizeSet())
    {
      Dimension localDimension = getMinimumSize();
      if (paramInt3 < localDimension.width) {
        paramInt3 = localDimension.width;
      }
      if (paramInt4 < localDimension.height) {
        paramInt4 = localDimension.height;
      }
    }
    super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  void setClientSize(int paramInt1, int paramInt2)
  {
    synchronized (getTreeLock())
    {
      setBoundsOp(4);
      setBounds(this.x, this.y, paramInt1, paramInt2);
    }
  }
  
  final void closeSplashScreen()
  {
    if (this.isTrayIconWindow) {
      return;
    }
    if (beforeFirstWindowShown.getAndSet(false))
    {
      SunToolkit.closeSplashScreen();
      SplashScreen.markClosed();
    }
  }
  
  public void setVisible(boolean paramBoolean)
  {
    super.setVisible(paramBoolean);
  }
  
  @Deprecated
  public void show()
  {
    if (this.peer == null) {
      addNotify();
    }
    validateUnconditionally();
    this.isInShow = true;
    if (this.visible)
    {
      toFront();
    }
    else
    {
      this.beforeFirstShow = false;
      closeSplashScreen();
      Dialog.checkShouldBeBlocked(this);
      super.show();
      synchronized (getTreeLock())
      {
        this.locationByPlatform = false;
      }
      for (int i = 0; i < this.ownedWindowList.size(); i++)
      {
        Window localWindow = (Window)((WeakReference)this.ownedWindowList.elementAt(i)).get();
        if ((localWindow != null) && (localWindow.showWithParent))
        {
          localWindow.show();
          localWindow.showWithParent = false;
        }
      }
      if (!isModalBlocked()) {
        updateChildrenBlocking();
      } else {
        this.modalBlocker.toFront_NoClientCode();
      }
      if (((this instanceof Frame)) || ((this instanceof Dialog))) {
        updateChildFocusableWindowState(this);
      }
    }
    this.isInShow = false;
    if ((this.state & 0x1) == 0)
    {
      postWindowEvent(200);
      this.state |= 0x1;
    }
  }
  
  static void updateChildFocusableWindowState(Window paramWindow)
  {
    if ((paramWindow.getPeer() != null) && (paramWindow.isShowing())) {
      ((WindowPeer)paramWindow.getPeer()).updateFocusableWindowState();
    }
    for (int i = 0; i < paramWindow.ownedWindowList.size(); i++)
    {
      Window localWindow = (Window)((WeakReference)paramWindow.ownedWindowList.elementAt(i)).get();
      if (localWindow != null) {
        updateChildFocusableWindowState(localWindow);
      }
    }
  }
  
  synchronized void postWindowEvent(int paramInt)
  {
    if ((this.windowListener != null) || ((this.eventMask & 0x40) != 0L) || (Toolkit.enabledOnToolkit(64L)))
    {
      WindowEvent localWindowEvent = new WindowEvent(this, paramInt);
      Toolkit.getEventQueue().postEvent(localWindowEvent);
    }
  }
  
  @Deprecated
  public void hide()
  {
    synchronized (this.ownedWindowList)
    {
      for (int i = 0; i < this.ownedWindowList.size(); i++)
      {
        Window localWindow = (Window)((WeakReference)this.ownedWindowList.elementAt(i)).get();
        if ((localWindow != null) && (localWindow.visible))
        {
          localWindow.hide();
          localWindow.showWithParent = true;
        }
      }
    }
    if (isModalBlocked()) {
      this.modalBlocker.unblockWindow(this);
    }
    super.hide();
    synchronized (getTreeLock())
    {
      this.locationByPlatform = false;
    }
  }
  
  final void clearMostRecentFocusOwnerOnHide() {}
  
  public void dispose()
  {
    doDispose();
  }
  
  void disposeImpl()
  {
    dispose();
    if (getPeer() != null) {
      doDispose();
    }
  }
  
  void doDispose()
  {
    boolean bool = isDisplayable();
    Runnable local1DisposeAction = new Runnable()
    {
      public void run()
      {
        Window.this.disposing = true;
        try
        {
          GraphicsDevice localGraphicsDevice = Window.this.getGraphicsConfiguration().getDevice();
          if (localGraphicsDevice.getFullScreenWindow() == Window.this) {
            localGraphicsDevice.setFullScreenWindow(null);
          }
          Object[] arrayOfObject;
          synchronized (Window.this.ownedWindowList)
          {
            arrayOfObject = new Object[Window.this.ownedWindowList.size()];
            Window.this.ownedWindowList.copyInto(arrayOfObject);
          }
          for (??? = 0; ??? < arrayOfObject.length; ???++)
          {
            Window localWindow = (Window)((WeakReference)arrayOfObject[???]).get();
            if (localWindow != null) {
              localWindow.disposeImpl();
            }
          }
          Window.this.hide();
          Window.this.beforeFirstShow = true;
          Window.this.removeNotify();
          synchronized (Window.this.inputContextLock)
          {
            if (Window.this.inputContext != null)
            {
              Window.this.inputContext.dispose();
              Window.this.inputContext = null;
            }
          }
          Window.this.clearCurrentFocusCycleRootOnHide();
        }
        finally
        {
          Window.this.disposing = false;
        }
      }
    };
    if (EventQueue.isDispatchThread()) {
      local1DisposeAction.run();
    } else {
      try
      {
        EventQueue.invokeAndWait(this, local1DisposeAction);
      }
      catch (InterruptedException localInterruptedException)
      {
        System.err.println("Disposal was interrupted:");
        localInterruptedException.printStackTrace();
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        System.err.println("Exception during disposal:");
        localInvocationTargetException.printStackTrace();
      }
    }
    if (bool) {
      postWindowEvent(202);
    }
  }
  
  void adjustListeningChildrenOnParent(long paramLong, int paramInt) {}
  
  void adjustDecendantsOnParent(int paramInt) {}
  
  public void toFront()
  {
    toFront_NoClientCode();
  }
  
  final void toFront_NoClientCode()
  {
    if (this.visible)
    {
      WindowPeer localWindowPeer = (WindowPeer)this.peer;
      if (localWindowPeer != null) {
        localWindowPeer.toFront();
      }
      if (isModalBlocked()) {
        this.modalBlocker.toFront_NoClientCode();
      }
    }
  }
  
  public void toBack()
  {
    toBack_NoClientCode();
  }
  
  final void toBack_NoClientCode()
  {
    if (isAlwaysOnTop()) {
      try
      {
        setAlwaysOnTop(false);
      }
      catch (SecurityException localSecurityException) {}
    }
    if (this.visible)
    {
      WindowPeer localWindowPeer = (WindowPeer)this.peer;
      if (localWindowPeer != null) {
        localWindowPeer.toBack();
      }
    }
  }
  
  public Toolkit getToolkit()
  {
    return Toolkit.getDefaultToolkit();
  }
  
  public final String getWarningString()
  {
    return this.warningString;
  }
  
  private void setWarningString()
  {
    this.warningString = null;
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkPermission(SecurityConstants.AWT.TOPLEVEL_WINDOW_PERMISSION);
      }
      catch (SecurityException localSecurityException)
      {
        this.warningString = ((String)AccessController.doPrivileged(new GetPropertyAction("awt.appletWarning", "Java Applet Window")));
      }
    }
  }
  
  public Locale getLocale()
  {
    if (this.locale == null) {
      return Locale.getDefault();
    }
    return this.locale;
  }
  
  public InputContext getInputContext()
  {
    synchronized (this.inputContextLock)
    {
      if (this.inputContext == null) {
        this.inputContext = InputContext.getInstance();
      }
    }
    return this.inputContext;
  }
  
  public void setCursor(Cursor paramCursor)
  {
    if (paramCursor == null) {
      paramCursor = Cursor.getPredefinedCursor(0);
    }
    super.setCursor(paramCursor);
  }
  
  public Window getOwner()
  {
    return getOwner_NoClientCode();
  }
  
  final Window getOwner_NoClientCode()
  {
    return (Window)this.parent;
  }
  
  public Window[] getOwnedWindows()
  {
    return getOwnedWindows_NoClientCode();
  }
  
  final Window[] getOwnedWindows_NoClientCode()
  {
    Window[] arrayOfWindow1;
    synchronized (this.ownedWindowList)
    {
      int i = this.ownedWindowList.size();
      int j = 0;
      Window[] arrayOfWindow2 = new Window[i];
      for (int k = 0; k < i; k++)
      {
        arrayOfWindow2[j] = ((Window)((WeakReference)this.ownedWindowList.elementAt(k)).get());
        if (arrayOfWindow2[j] != null) {
          j++;
        }
      }
      if (i != j) {
        arrayOfWindow1 = (Window[])Arrays.copyOf(arrayOfWindow2, j);
      } else {
        arrayOfWindow1 = arrayOfWindow2;
      }
    }
    return arrayOfWindow1;
  }
  
  boolean isModalBlocked()
  {
    return this.modalBlocker != null;
  }
  
  void setModalBlocked(Dialog paramDialog, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.modalBlocker = (paramBoolean1 ? paramDialog : null);
    if (paramBoolean2)
    {
      WindowPeer localWindowPeer = (WindowPeer)this.peer;
      if (localWindowPeer != null) {
        localWindowPeer.setModalBlocked(paramDialog, paramBoolean1);
      }
    }
  }
  
  Dialog getModalBlocker()
  {
    return this.modalBlocker;
  }
  
  static IdentityArrayList<Window> getAllWindows()
  {
    synchronized (allWindows)
    {
      IdentityArrayList localIdentityArrayList = new IdentityArrayList();
      localIdentityArrayList.addAll(allWindows);
      return localIdentityArrayList;
    }
  }
  
  static IdentityArrayList<Window> getAllUnblockedWindows()
  {
    synchronized (allWindows)
    {
      IdentityArrayList localIdentityArrayList = new IdentityArrayList();
      for (int i = 0; i < allWindows.size(); i++)
      {
        Window localWindow = (Window)allWindows.get(i);
        if (!localWindow.isModalBlocked()) {
          localIdentityArrayList.add(localWindow);
        }
      }
      return localIdentityArrayList;
    }
  }
  
  private static Window[] getWindows(AppContext paramAppContext)
  {
    synchronized (Window.class)
    {
      Vector localVector = (Vector)paramAppContext.get(Window.class);
      Window[] arrayOfWindow1;
      if (localVector != null)
      {
        int i = localVector.size();
        int j = 0;
        Window[] arrayOfWindow2 = new Window[i];
        for (int k = 0; k < i; k++)
        {
          Window localWindow = (Window)((WeakReference)localVector.get(k)).get();
          if (localWindow != null) {
            arrayOfWindow2[(j++)] = localWindow;
          }
        }
        if (i != j) {
          arrayOfWindow1 = (Window[])Arrays.copyOf(arrayOfWindow2, j);
        } else {
          arrayOfWindow1 = arrayOfWindow2;
        }
      }
      else
      {
        arrayOfWindow1 = new Window[0];
      }
      return arrayOfWindow1;
    }
  }
  
  public static Window[] getWindows()
  {
    return getWindows(AppContext.getAppContext());
  }
  
  public static Window[] getOwnerlessWindows()
  {
    Window[] arrayOfWindow1 = getWindows();
    int i = 0;
    for (Window localWindow1 : arrayOfWindow1) {
      if (localWindow1.getOwner() == null) {
        i++;
      }
    }
    ??? = new Window[i];
    ??? = 0;
    for (Window localWindow2 : arrayOfWindow1) {
      if (localWindow2.getOwner() == null) {
        ???[(???++)] = localWindow2;
      }
    }
    return ???;
  }
  
  Window getDocumentRoot()
  {
    synchronized (getTreeLock())
    {
      for (Window localWindow = this; localWindow.getOwner() != null; localWindow = localWindow.getOwner()) {}
      return localWindow;
    }
  }
  
  public void setModalExclusionType(Dialog.ModalExclusionType paramModalExclusionType)
  {
    if (paramModalExclusionType == null) {
      paramModalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
    }
    if (!Toolkit.getDefaultToolkit().isModalExclusionTypeSupported(paramModalExclusionType)) {
      paramModalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
    }
    if (this.modalExclusionType == paramModalExclusionType) {
      return;
    }
    if (paramModalExclusionType == Dialog.ModalExclusionType.TOOLKIT_EXCLUDE)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkPermission(SecurityConstants.AWT.TOOLKIT_MODALITY_PERMISSION);
      }
    }
    this.modalExclusionType = paramModalExclusionType;
  }
  
  public Dialog.ModalExclusionType getModalExclusionType()
  {
    return this.modalExclusionType;
  }
  
  boolean isModalExcluded(Dialog.ModalExclusionType paramModalExclusionType)
  {
    if ((this.modalExclusionType != null) && (this.modalExclusionType.compareTo(paramModalExclusionType) >= 0)) {
      return true;
    }
    Window localWindow = getOwner_NoClientCode();
    return (localWindow != null) && (localWindow.isModalExcluded(paramModalExclusionType));
  }
  
  void updateChildrenBlocking()
  {
    Vector localVector = new Vector();
    Window[] arrayOfWindow = getOwnedWindows();
    for (int i = 0; i < arrayOfWindow.length; i++) {
      localVector.add(arrayOfWindow[i]);
    }
    for (i = 0; i < localVector.size(); i++)
    {
      Window localWindow = (Window)localVector.get(i);
      if (localWindow.isVisible())
      {
        if (localWindow.isModalBlocked())
        {
          localObject = localWindow.getModalBlocker();
          ((Dialog)localObject).unblockWindow(localWindow);
        }
        Dialog.checkShouldBeBlocked(localWindow);
        Object localObject = localWindow.getOwnedWindows();
        for (int j = 0; j < localObject.length; j++) {
          localVector.add(localObject[j]);
        }
      }
    }
  }
  
  public synchronized void addWindowListener(WindowListener paramWindowListener)
  {
    if (paramWindowListener == null) {
      return;
    }
    this.newEventsOnly = true;
    this.windowListener = AWTEventMulticaster.add(this.windowListener, paramWindowListener);
  }
  
  public synchronized void addWindowStateListener(WindowStateListener paramWindowStateListener)
  {
    if (paramWindowStateListener == null) {
      return;
    }
    this.windowStateListener = AWTEventMulticaster.add(this.windowStateListener, paramWindowStateListener);
    this.newEventsOnly = true;
  }
  
  public synchronized void addWindowFocusListener(WindowFocusListener paramWindowFocusListener)
  {
    if (paramWindowFocusListener == null) {
      return;
    }
    this.windowFocusListener = AWTEventMulticaster.add(this.windowFocusListener, paramWindowFocusListener);
    this.newEventsOnly = true;
  }
  
  public synchronized void removeWindowListener(WindowListener paramWindowListener)
  {
    if (paramWindowListener == null) {
      return;
    }
    this.windowListener = AWTEventMulticaster.remove(this.windowListener, paramWindowListener);
  }
  
  public synchronized void removeWindowStateListener(WindowStateListener paramWindowStateListener)
  {
    if (paramWindowStateListener == null) {
      return;
    }
    this.windowStateListener = AWTEventMulticaster.remove(this.windowStateListener, paramWindowStateListener);
  }
  
  public synchronized void removeWindowFocusListener(WindowFocusListener paramWindowFocusListener)
  {
    if (paramWindowFocusListener == null) {
      return;
    }
    this.windowFocusListener = AWTEventMulticaster.remove(this.windowFocusListener, paramWindowFocusListener);
  }
  
  public synchronized WindowListener[] getWindowListeners()
  {
    return (WindowListener[])getListeners(WindowListener.class);
  }
  
  public synchronized WindowFocusListener[] getWindowFocusListeners()
  {
    return (WindowFocusListener[])getListeners(WindowFocusListener.class);
  }
  
  public synchronized WindowStateListener[] getWindowStateListeners()
  {
    return (WindowStateListener[])getListeners(WindowStateListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    Object localObject = null;
    if (paramClass == WindowFocusListener.class) {
      localObject = this.windowFocusListener;
    } else if (paramClass == WindowStateListener.class) {
      localObject = this.windowStateListener;
    } else if (paramClass == WindowListener.class) {
      localObject = this.windowListener;
    } else {
      return super.getListeners(paramClass);
    }
    return AWTEventMulticaster.getListeners((EventListener)localObject, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    switch (paramAWTEvent.id)
    {
    case 200: 
    case 201: 
    case 202: 
    case 203: 
    case 204: 
    case 205: 
    case 206: 
      return ((this.eventMask & 0x40) != 0L) || (this.windowListener != null);
    case 207: 
    case 208: 
      return ((this.eventMask & 0x80000) != 0L) || (this.windowFocusListener != null);
    case 209: 
      return ((this.eventMask & 0x40000) != 0L) || (this.windowStateListener != null);
    }
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof WindowEvent))
    {
      switch (paramAWTEvent.getID())
      {
      case 200: 
      case 201: 
      case 202: 
      case 203: 
      case 204: 
      case 205: 
      case 206: 
        processWindowEvent((WindowEvent)paramAWTEvent);
        break;
      case 207: 
      case 208: 
        processWindowFocusEvent((WindowEvent)paramAWTEvent);
        break;
      case 209: 
        processWindowStateEvent((WindowEvent)paramAWTEvent);
      }
      return;
    }
    super.processEvent(paramAWTEvent);
  }
  
  protected void processWindowEvent(WindowEvent paramWindowEvent)
  {
    WindowListener localWindowListener = this.windowListener;
    if (localWindowListener != null) {
      switch (paramWindowEvent.getID())
      {
      case 200: 
        localWindowListener.windowOpened(paramWindowEvent);
        break;
      case 201: 
        localWindowListener.windowClosing(paramWindowEvent);
        break;
      case 202: 
        localWindowListener.windowClosed(paramWindowEvent);
        break;
      case 203: 
        localWindowListener.windowIconified(paramWindowEvent);
        break;
      case 204: 
        localWindowListener.windowDeiconified(paramWindowEvent);
        break;
      case 205: 
        localWindowListener.windowActivated(paramWindowEvent);
        break;
      case 206: 
        localWindowListener.windowDeactivated(paramWindowEvent);
        break;
      }
    }
  }
  
  protected void processWindowFocusEvent(WindowEvent paramWindowEvent)
  {
    WindowFocusListener localWindowFocusListener = this.windowFocusListener;
    if (localWindowFocusListener != null) {
      switch (paramWindowEvent.getID())
      {
      case 207: 
        localWindowFocusListener.windowGainedFocus(paramWindowEvent);
        break;
      case 208: 
        localWindowFocusListener.windowLostFocus(paramWindowEvent);
        break;
      }
    }
  }
  
  protected void processWindowStateEvent(WindowEvent paramWindowEvent)
  {
    WindowStateListener localWindowStateListener = this.windowStateListener;
    if (localWindowStateListener != null) {
      switch (paramWindowEvent.getID())
      {
      case 209: 
        localWindowStateListener.windowStateChanged(paramWindowEvent);
        break;
      }
    }
  }
  
  void preProcessKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((paramKeyEvent.isActionKey()) && (paramKeyEvent.getKeyCode() == 112) && (paramKeyEvent.isControlDown()) && (paramKeyEvent.isShiftDown()) && (paramKeyEvent.getID() == 401)) {
      list(System.out, 0);
    }
  }
  
  void postProcessKeyEvent(KeyEvent paramKeyEvent) {}
  
  public final void setAlwaysOnTop(boolean paramBoolean)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.AWT.SET_WINDOW_ALWAYS_ON_TOP_PERMISSION);
    }
    boolean bool;
    synchronized (this)
    {
      bool = this.alwaysOnTop;
      this.alwaysOnTop = paramBoolean;
    }
    if (bool != paramBoolean)
    {
      if (isAlwaysOnTopSupported())
      {
        ??? = (WindowPeer)this.peer;
        synchronized (getTreeLock())
        {
          if (??? != null) {
            ((WindowPeer)???).updateAlwaysOnTopState();
          }
        }
      }
      firePropertyChange("alwaysOnTop", bool, paramBoolean);
    }
    setOwnedWindowsAlwaysOnTop(paramBoolean);
  }
  
  private void setOwnedWindowsAlwaysOnTop(boolean paramBoolean)
  {
    WeakReference[] arrayOfWeakReference;
    synchronized (this.ownedWindowList)
    {
      arrayOfWeakReference = new WeakReference[this.ownedWindowList.size()];
      this.ownedWindowList.copyInto(arrayOfWeakReference);
    }
    for (Object localObject2 : arrayOfWeakReference)
    {
      Window localWindow = (Window)localObject2.get();
      if (localWindow != null) {
        try
        {
          localWindow.setAlwaysOnTop(paramBoolean);
        }
        catch (SecurityException localSecurityException) {}
      }
    }
  }
  
  public boolean isAlwaysOnTopSupported()
  {
    return Toolkit.getDefaultToolkit().isAlwaysOnTopSupported();
  }
  
  public final boolean isAlwaysOnTop()
  {
    return this.alwaysOnTop;
  }
  
  public Component getFocusOwner()
  {
    return isFocused() ? KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() : null;
  }
  
  public Component getMostRecentFocusOwner()
  {
    if (isFocused()) {
      return getFocusOwner();
    }
    Component localComponent = KeyboardFocusManager.getMostRecentFocusOwner(this);
    if (localComponent != null) {
      return localComponent;
    }
    return isFocusableWindow() ? getFocusTraversalPolicy().getInitialComponent(this) : null;
  }
  
  public boolean isActive()
  {
    return KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() == this;
  }
  
  public boolean isFocused()
  {
    return KeyboardFocusManager.getCurrentKeyboardFocusManager().getGlobalFocusedWindow() == this;
  }
  
  public Set<AWTKeyStroke> getFocusTraversalKeys(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("invalid focus traversal key identifier");
    }
    Set<AWTKeyStroke> localSet = this.focusTraversalKeys != null ? this.focusTraversalKeys[paramInt] : null;
    if (localSet != null) {
      return localSet;
    }
    return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(paramInt);
  }
  
  public final void setFocusCycleRoot(boolean paramBoolean) {}
  
  public final boolean isFocusCycleRoot()
  {
    return true;
  }
  
  public final Container getFocusCycleRootAncestor()
  {
    return null;
  }
  
  public final boolean isFocusableWindow()
  {
    if (!getFocusableWindowState()) {
      return false;
    }
    if (((this instanceof Frame)) || ((this instanceof Dialog))) {
      return true;
    }
    if (getFocusTraversalPolicy().getDefaultComponent(this) == null) {
      return false;
    }
    for (Window localWindow = getOwner(); localWindow != null; localWindow = localWindow.getOwner()) {
      if (((localWindow instanceof Frame)) || ((localWindow instanceof Dialog))) {
        return localWindow.isShowing();
      }
    }
    return false;
  }
  
  public boolean getFocusableWindowState()
  {
    return this.focusableWindowState;
  }
  
  public void setFocusableWindowState(boolean paramBoolean)
  {
    boolean bool;
    synchronized (this)
    {
      bool = this.focusableWindowState;
      this.focusableWindowState = paramBoolean;
    }
    ??? = (WindowPeer)this.peer;
    if (??? != null) {
      ((WindowPeer)???).updateFocusableWindowState();
    }
    firePropertyChange("focusableWindowState", bool, paramBoolean);
    if ((bool) && (!paramBoolean) && (isFocused()))
    {
      for (Window localWindow = getOwner(); localWindow != null; localWindow = localWindow.getOwner())
      {
        Component localComponent = KeyboardFocusManager.getMostRecentFocusOwner(localWindow);
        if ((localComponent != null) && (localComponent.requestFocus(false, CausedFocusEvent.Cause.ACTIVATION))) {
          return;
        }
      }
      KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
    }
  }
  
  public void setAutoRequestFocus(boolean paramBoolean)
  {
    this.autoRequestFocus = paramBoolean;
  }
  
  public boolean isAutoRequestFocus()
  {
    return this.autoRequestFocus;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    super.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    super.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public boolean isValidateRoot()
  {
    return true;
  }
  
  void dispatchEventImpl(AWTEvent paramAWTEvent)
  {
    if (paramAWTEvent.getID() == 101)
    {
      invalidate();
      validate();
    }
    super.dispatchEventImpl(paramAWTEvent);
  }
  
  @Deprecated
  public boolean postEvent(Event paramEvent)
  {
    if (handleEvent(paramEvent))
    {
      paramEvent.consume();
      return true;
    }
    return false;
  }
  
  public boolean isShowing()
  {
    return this.visible;
  }
  
  boolean isDisposing()
  {
    return this.disposing;
  }
  
  @Deprecated
  public void applyResourceBundle(ResourceBundle paramResourceBundle)
  {
    applyComponentOrientation(ComponentOrientation.getOrientation(paramResourceBundle));
  }
  
  @Deprecated
  public void applyResourceBundle(String paramString)
  {
    applyResourceBundle(ResourceBundle.getBundle(paramString));
  }
  
  void addOwnedWindow(WeakReference<Window> paramWeakReference)
  {
    if (paramWeakReference != null) {
      synchronized (this.ownedWindowList)
      {
        if (!this.ownedWindowList.contains(paramWeakReference)) {
          this.ownedWindowList.addElement(paramWeakReference);
        }
      }
    }
  }
  
  void removeOwnedWindow(WeakReference<Window> paramWeakReference)
  {
    if (paramWeakReference != null) {
      this.ownedWindowList.removeElement(paramWeakReference);
    }
  }
  
  void connectOwnedWindow(Window paramWindow)
  {
    paramWindow.parent = this;
    addOwnedWindow(paramWindow.weakThis);
    paramWindow.disposerRecord.updateOwner();
  }
  
  private void addToWindowList()
  {
    synchronized (Window.class)
    {
      Vector localVector = (Vector)this.appContext.get(Window.class);
      if (localVector == null)
      {
        localVector = new Vector();
        this.appContext.put(Window.class, localVector);
      }
      localVector.add(this.weakThis);
    }
  }
  
  private static void removeFromWindowList(AppContext paramAppContext, WeakReference<Window> paramWeakReference)
  {
    synchronized (Window.class)
    {
      Vector localVector = (Vector)paramAppContext.get(Window.class);
      if (localVector != null) {
        localVector.remove(paramWeakReference);
      }
    }
  }
  
  private void removeFromWindowList()
  {
    removeFromWindowList(this.appContext, this.weakThis);
  }
  
  public void setType(Type paramType)
  {
    if (paramType == null) {
      throw new IllegalArgumentException("type should not be null.");
    }
    synchronized (getTreeLock())
    {
      if (isDisplayable()) {
        throw new IllegalComponentStateException("The window is displayable.");
      }
      synchronized (getObjectLock())
      {
        this.type = paramType;
      }
    }
  }
  
  public Type getType()
  {
    synchronized (getObjectLock())
    {
      return this.type;
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    synchronized (this)
    {
      this.focusMgr = new FocusManager();
      this.focusMgr.focusRoot = this;
      this.focusMgr.focusOwner = getMostRecentFocusOwner();
      paramObjectOutputStream.defaultWriteObject();
      this.focusMgr = null;
      AWTEventMulticaster.save(paramObjectOutputStream, "windowL", this.windowListener);
      AWTEventMulticaster.save(paramObjectOutputStream, "windowFocusL", this.windowFocusListener);
      AWTEventMulticaster.save(paramObjectOutputStream, "windowStateL", this.windowStateListener);
    }
    paramObjectOutputStream.writeObject(null);
    synchronized (this.ownedWindowList)
    {
      for (int i = 0; i < this.ownedWindowList.size(); i++)
      {
        Window localWindow = (Window)((WeakReference)this.ownedWindowList.elementAt(i)).get();
        if (localWindow != null)
        {
          paramObjectOutputStream.writeObject("ownedL");
          paramObjectOutputStream.writeObject(localWindow);
        }
      }
    }
    paramObjectOutputStream.writeObject(null);
    if (this.icons != null)
    {
      ??? = this.icons.iterator();
      while (((Iterator)???).hasNext())
      {
        Image localImage = (Image)((Iterator)???).next();
        if ((localImage instanceof Serializable)) {
          paramObjectOutputStream.writeObject(localImage);
        }
      }
    }
    paramObjectOutputStream.writeObject(null);
  }
  
  private void initDeserializedWindow()
  {
    setWarningString();
    this.inputContextLock = new Object();
    this.visible = false;
    this.weakThis = new WeakReference(this);
    this.anchor = new Object();
    this.disposerRecord = new WindowDisposerRecord(this.appContext, this);
    Disposer.addRecord(this.anchor, this.disposerRecord);
    addToWindowList();
    initGC(null);
    this.ownedWindowList = new Vector();
  }
  
  private void deserializeResources(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    if (this.windowSerializedDataVersion < 2)
    {
      if ((this.focusMgr != null) && (this.focusMgr.focusOwner != null)) {
        KeyboardFocusManager.setMostRecentFocusOwner(this, this.focusMgr.focusOwner);
      }
      this.focusableWindowState = true;
    }
    Object localObject1;
    Object localObject2;
    while (null != (localObject1 = paramObjectInputStream.readObject()))
    {
      localObject2 = ((String)localObject1).intern();
      if ("windowL" == localObject2) {
        addWindowListener((WindowListener)paramObjectInputStream.readObject());
      } else if ("windowFocusL" == localObject2) {
        addWindowFocusListener((WindowFocusListener)paramObjectInputStream.readObject());
      } else if ("windowStateL" == localObject2) {
        addWindowStateListener((WindowStateListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
    try
    {
      while (null != (localObject1 = paramObjectInputStream.readObject()))
      {
        localObject2 = ((String)localObject1).intern();
        if ("ownedL" == localObject2) {
          connectOwnedWindow((Window)paramObjectInputStream.readObject());
        } else {
          paramObjectInputStream.readObject();
        }
      }
      localObject2 = paramObjectInputStream.readObject();
      this.icons = new ArrayList();
      while (localObject2 != null)
      {
        if ((localObject2 instanceof Image)) {
          this.icons.add((Image)localObject2);
        }
        localObject2 = paramObjectInputStream.readObject();
      }
    }
    catch (OptionalDataException localOptionalDataException) {}
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    initDeserializedWindow();
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    this.syncLWRequests = localGetField.get("syncLWRequests", systemSyncLWRequests);
    this.state = localGetField.get("state", 0);
    this.focusableWindowState = localGetField.get("focusableWindowState", true);
    this.windowSerializedDataVersion = localGetField.get("windowSerializedDataVersion", 1);
    this.locationByPlatform = localGetField.get("locationByPlatform", locationByPlatformProp);
    this.focusMgr = ((FocusManager)localGetField.get("focusMgr", null));
    Dialog.ModalExclusionType localModalExclusionType = (Dialog.ModalExclusionType)localGetField.get("modalExclusionType", Dialog.ModalExclusionType.NO_EXCLUDE);
    setModalExclusionType(localModalExclusionType);
    boolean bool = localGetField.get("alwaysOnTop", false);
    if (bool) {
      setAlwaysOnTop(bool);
    }
    this.shape = ((Shape)localGetField.get("shape", null));
    this.opacity = Float.valueOf(localGetField.get("opacity", 1.0F)).floatValue();
    this.securityWarningWidth = 0;
    this.securityWarningHeight = 0;
    this.securityWarningPointX = 2.0D;
    this.securityWarningPointY = 0.0D;
    this.securityWarningAlignmentX = 1.0F;
    this.securityWarningAlignmentY = 0.0F;
    deserializeResources(paramObjectInputStream);
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleAWTWindow();
    }
    return this.accessibleContext;
  }
  
  void setGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration)
  {
    if (paramGraphicsConfiguration == null) {
      paramGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    synchronized (getTreeLock())
    {
      super.setGraphicsConfiguration(paramGraphicsConfiguration);
      if (log.isLoggable(PlatformLogger.Level.FINER)) {
        log.finer("+ Window.setGraphicsConfiguration(): new GC is \n+ " + getGraphicsConfiguration_NoClientCode() + "\n+ this is " + this);
      }
    }
  }
  
  public void setLocationRelativeTo(Component paramComponent)
  {
    int i = 0;
    int j = 0;
    GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration_NoClientCode();
    Rectangle localRectangle = localGraphicsConfiguration.getBounds();
    Dimension localDimension = getSize();
    Window localWindow = SunToolkit.getContainingWindow(paramComponent);
    Object localObject;
    Point localPoint;
    if ((paramComponent == null) || (localWindow == null))
    {
      localObject = GraphicsEnvironment.getLocalGraphicsEnvironment();
      localGraphicsConfiguration = ((GraphicsEnvironment)localObject).getDefaultScreenDevice().getDefaultConfiguration();
      localRectangle = localGraphicsConfiguration.getBounds();
      localPoint = ((GraphicsEnvironment)localObject).getCenterPoint();
      i = localPoint.x - localDimension.width / 2;
      j = localPoint.y - localDimension.height / 2;
    }
    else if (!paramComponent.isShowing())
    {
      localGraphicsConfiguration = localWindow.getGraphicsConfiguration();
      localRectangle = localGraphicsConfiguration.getBounds();
      i = localRectangle.x + (localRectangle.width - localDimension.width) / 2;
      j = localRectangle.y + (localRectangle.height - localDimension.height) / 2;
    }
    else
    {
      localGraphicsConfiguration = localWindow.getGraphicsConfiguration();
      localRectangle = localGraphicsConfiguration.getBounds();
      localObject = paramComponent.getSize();
      localPoint = paramComponent.getLocationOnScreen();
      i = localPoint.x + (((Dimension)localObject).width - localDimension.width) / 2;
      j = localPoint.y + (((Dimension)localObject).height - localDimension.height) / 2;
      if (j + localDimension.height > localRectangle.y + localRectangle.height)
      {
        j = localRectangle.y + localRectangle.height - localDimension.height;
        if (localPoint.x - localRectangle.x + ((Dimension)localObject).width / 2 < localRectangle.width / 2) {
          i = localPoint.x + ((Dimension)localObject).width;
        } else {
          i = localPoint.x - localDimension.width;
        }
      }
    }
    if (j + localDimension.height > localRectangle.y + localRectangle.height) {
      j = localRectangle.y + localRectangle.height - localDimension.height;
    }
    if (j < localRectangle.y) {
      j = localRectangle.y;
    }
    if (i + localDimension.width > localRectangle.x + localRectangle.width) {
      i = localRectangle.x + localRectangle.width - localDimension.width;
    }
    if (i < localRectangle.x) {
      i = localRectangle.x;
    }
    setLocation(i, j);
  }
  
  void deliverMouseWheelToAncestor(MouseWheelEvent paramMouseWheelEvent) {}
  
  boolean dispatchMouseWheelToAncestor(MouseWheelEvent paramMouseWheelEvent)
  {
    return false;
  }
  
  public void createBufferStrategy(int paramInt)
  {
    super.createBufferStrategy(paramInt);
  }
  
  public void createBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities)
    throws AWTException
  {
    super.createBufferStrategy(paramInt, paramBufferCapabilities);
  }
  
  public BufferStrategy getBufferStrategy()
  {
    return super.getBufferStrategy();
  }
  
  Component getTemporaryLostComponent()
  {
    return this.temporaryLostComponent;
  }
  
  Component setTemporaryLostComponent(Component paramComponent)
  {
    Component localComponent = this.temporaryLostComponent;
    if ((paramComponent == null) || (paramComponent.canBeFocusOwner())) {
      this.temporaryLostComponent = paramComponent;
    } else {
      this.temporaryLostComponent = null;
    }
    return localComponent;
  }
  
  boolean canContainFocusOwner(Component paramComponent)
  {
    return (super.canContainFocusOwner(paramComponent)) && (isFocusableWindow());
  }
  
  public void setLocationByPlatform(boolean paramBoolean)
  {
    synchronized (getTreeLock())
    {
      if ((paramBoolean) && (isShowing())) {
        throw new IllegalComponentStateException("The window is showing on screen.");
      }
      this.locationByPlatform = paramBoolean;
    }
  }
  
  public boolean isLocationByPlatform()
  {
    synchronized (getTreeLock())
    {
      return this.locationByPlatform;
    }
  }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    synchronized (getTreeLock())
    {
      if ((getBoundsOp() == 1) || (getBoundsOp() == 3)) {
        this.locationByPlatform = false;
      }
      super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void setBounds(Rectangle paramRectangle)
  {
    setBounds(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
  }
  
  boolean isRecursivelyVisible()
  {
    return this.visible;
  }
  
  public float getOpacity()
  {
    synchronized (getTreeLock())
    {
      return this.opacity;
    }
  }
  
  public void setOpacity(float paramFloat)
  {
    synchronized (getTreeLock())
    {
      if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
        throw new IllegalArgumentException("The value of opacity should be in the range [0.0f .. 1.0f].");
      }
      if (paramFloat < 1.0F)
      {
        localObject1 = getGraphicsConfiguration();
        GraphicsDevice localGraphicsDevice = ((GraphicsConfiguration)localObject1).getDevice();
        if (((GraphicsConfiguration)localObject1).getDevice().getFullScreenWindow() == this) {
          throw new IllegalComponentStateException("Setting opacity for full-screen window is not supported.");
        }
        if (!localGraphicsDevice.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
          throw new UnsupportedOperationException("TRANSLUCENT translucency is not supported.");
        }
      }
      this.opacity = paramFloat;
      Object localObject1 = (WindowPeer)getPeer();
      if (localObject1 != null) {
        ((WindowPeer)localObject1).setOpacity(paramFloat);
      }
    }
  }
  
  public Shape getShape()
  {
    synchronized (getTreeLock())
    {
      return this.shape == null ? null : new Path2D.Float(this.shape);
    }
  }
  
  public void setShape(Shape paramShape)
  {
    synchronized (getTreeLock())
    {
      if (paramShape != null)
      {
        localObject1 = getGraphicsConfiguration();
        GraphicsDevice localGraphicsDevice = ((GraphicsConfiguration)localObject1).getDevice();
        if (((GraphicsConfiguration)localObject1).getDevice().getFullScreenWindow() == this) {
          throw new IllegalComponentStateException("Setting shape for full-screen window is not supported.");
        }
        if (!localGraphicsDevice.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT)) {
          throw new UnsupportedOperationException("PERPIXEL_TRANSPARENT translucency is not supported.");
        }
      }
      this.shape = (paramShape == null ? null : new Path2D.Float(paramShape));
      Object localObject1 = (WindowPeer)getPeer();
      if (localObject1 != null) {
        ((WindowPeer)localObject1).applyShape(paramShape == null ? null : Region.getInstance(paramShape, null));
      }
    }
  }
  
  public Color getBackground()
  {
    return super.getBackground();
  }
  
  public void setBackground(Color paramColor)
  {
    Color localColor = getBackground();
    super.setBackground(paramColor);
    if ((localColor != null) && (localColor.equals(paramColor))) {
      return;
    }
    int i = localColor != null ? localColor.getAlpha() : 255;
    int j = paramColor != null ? paramColor.getAlpha() : 255;
    if ((i == 255) && (j < 255))
    {
      localObject = getGraphicsConfiguration();
      GraphicsDevice localGraphicsDevice = ((GraphicsConfiguration)localObject).getDevice();
      if (((GraphicsConfiguration)localObject).getDevice().getFullScreenWindow() == this) {
        throw new IllegalComponentStateException("Making full-screen window non opaque is not supported.");
      }
      if (!((GraphicsConfiguration)localObject).isTranslucencyCapable())
      {
        GraphicsConfiguration localGraphicsConfiguration = localGraphicsDevice.getTranslucencyCapableGC();
        if (localGraphicsConfiguration == null) {
          throw new UnsupportedOperationException("PERPIXEL_TRANSLUCENT translucency is not supported");
        }
        setGraphicsConfiguration(localGraphicsConfiguration);
      }
      setLayersOpaque(this, false);
    }
    else if ((i < 255) && (j == 255))
    {
      setLayersOpaque(this, true);
    }
    Object localObject = (WindowPeer)getPeer();
    if (localObject != null) {
      ((WindowPeer)localObject).setOpaque(j == 255);
    }
  }
  
  public boolean isOpaque()
  {
    Color localColor = getBackground();
    return localColor.getAlpha() == 255;
  }
  
  private void updateWindow()
  {
    synchronized (getTreeLock())
    {
      WindowPeer localWindowPeer = (WindowPeer)getPeer();
      if (localWindowPeer != null) {
        localWindowPeer.updateWindow();
      }
    }
  }
  
  public void paint(Graphics paramGraphics)
  {
    Graphics localGraphics;
    if (!isOpaque()) {
      localGraphics = paramGraphics.create();
    }
    try
    {
      if ((localGraphics instanceof Graphics2D))
      {
        localGraphics.setColor(getBackground());
        ((Graphics2D)localGraphics).setComposite(AlphaComposite.getInstance(2));
        localGraphics.fillRect(0, 0, getWidth(), getHeight());
      }
      localGraphics.dispose();
    }
    finally
    {
      localGraphics.dispose();
    }
  }
  
  private static void setLayersOpaque(Component paramComponent, boolean paramBoolean)
  {
    if (SunToolkit.isInstanceOf(paramComponent, "javax.swing.RootPaneContainer"))
    {
      RootPaneContainer localRootPaneContainer = (RootPaneContainer)paramComponent;
      JRootPane localJRootPane = localRootPaneContainer.getRootPane();
      JLayeredPane localJLayeredPane = localJRootPane.getLayeredPane();
      Container localContainer = localJRootPane.getContentPane();
      Object localObject = (localContainer instanceof JComponent) ? (JComponent)localContainer : null;
      localJLayeredPane.setOpaque(paramBoolean);
      localJRootPane.setOpaque(paramBoolean);
      if (localObject != null)
      {
        localObject.setOpaque(paramBoolean);
        int i = localObject.getComponentCount();
        if (i > 0)
        {
          Component localComponent = localObject.getComponent(0);
          if ((localComponent instanceof RootPaneContainer)) {
            setLayersOpaque(localComponent, paramBoolean);
          }
        }
      }
    }
  }
  
  final Container getContainer()
  {
    return null;
  }
  
  final void applyCompoundShape(Region paramRegion) {}
  
  final void applyCurrentShape() {}
  
  final void mixOnReshaping() {}
  
  final Point getLocationOnWindow()
  {
    return new Point(0, 0);
  }
  
  private static double limit(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    paramDouble1 = Math.max(paramDouble1, paramDouble2);
    paramDouble1 = Math.min(paramDouble1, paramDouble3);
    return paramDouble1;
  }
  
  private Point2D calculateSecurityWarningPosition(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    double d1 = paramDouble1 + paramDouble3 * this.securityWarningAlignmentX + this.securityWarningPointX;
    double d2 = paramDouble2 + paramDouble4 * this.securityWarningAlignmentY + this.securityWarningPointY;
    d1 = limit(d1, paramDouble1 - this.securityWarningWidth - 2.0D, paramDouble1 + paramDouble3 + 2.0D);
    d2 = limit(d2, paramDouble2 - this.securityWarningHeight - 2.0D, paramDouble2 + paramDouble4 + 2.0D);
    GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration_NoClientCode();
    Rectangle localRectangle = localGraphicsConfiguration.getBounds();
    Insets localInsets = Toolkit.getDefaultToolkit().getScreenInsets(localGraphicsConfiguration);
    d1 = limit(d1, localRectangle.x + localInsets.left, localRectangle.x + localRectangle.width - localInsets.right - this.securityWarningWidth);
    d2 = limit(d2, localRectangle.y + localInsets.top, localRectangle.y + localRectangle.height - localInsets.bottom - this.securityWarningHeight);
    return new Point2D.Double(d1, d2);
  }
  
  void updateZOrder() {}
  
  static
  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.awt.syncLWRequests"));
    systemSyncLWRequests = (str != null) && (str.equals("true"));
    str = (String)AccessController.doPrivileged(new GetPropertyAction("java.awt.Window.locationByPlatform"));
    locationByPlatformProp = (str != null) && (str.equals("true"));
    beforeFirstWindowShown = new AtomicBoolean(true);
    AWTAccessor.setWindowAccessor(new AWTAccessor.WindowAccessor()
    {
      public float getOpacity(Window paramAnonymousWindow)
      {
        return paramAnonymousWindow.opacity;
      }
      
      public void setOpacity(Window paramAnonymousWindow, float paramAnonymousFloat)
      {
        paramAnonymousWindow.setOpacity(paramAnonymousFloat);
      }
      
      public Shape getShape(Window paramAnonymousWindow)
      {
        return paramAnonymousWindow.getShape();
      }
      
      public void setShape(Window paramAnonymousWindow, Shape paramAnonymousShape)
      {
        paramAnonymousWindow.setShape(paramAnonymousShape);
      }
      
      public void setOpaque(Window paramAnonymousWindow, boolean paramAnonymousBoolean)
      {
        Color localColor = paramAnonymousWindow.getBackground();
        if (localColor == null) {
          localColor = new Color(0, 0, 0, 0);
        }
        paramAnonymousWindow.setBackground(new Color(localColor.getRed(), localColor.getGreen(), localColor.getBlue(), paramAnonymousBoolean ? 255 : 0));
      }
      
      public void updateWindow(Window paramAnonymousWindow)
      {
        paramAnonymousWindow.updateWindow();
      }
      
      public Dimension getSecurityWarningSize(Window paramAnonymousWindow)
      {
        return new Dimension(paramAnonymousWindow.securityWarningWidth, paramAnonymousWindow.securityWarningHeight);
      }
      
      public void setSecurityWarningSize(Window paramAnonymousWindow, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramAnonymousWindow.securityWarningWidth = paramAnonymousInt1;
        paramAnonymousWindow.securityWarningHeight = paramAnonymousInt2;
      }
      
      public void setSecurityWarningPosition(Window paramAnonymousWindow, Point2D paramAnonymousPoint2D, float paramAnonymousFloat1, float paramAnonymousFloat2)
      {
        paramAnonymousWindow.securityWarningPointX = paramAnonymousPoint2D.getX();
        paramAnonymousWindow.securityWarningPointY = paramAnonymousPoint2D.getY();
        paramAnonymousWindow.securityWarningAlignmentX = paramAnonymousFloat1;
        paramAnonymousWindow.securityWarningAlignmentY = paramAnonymousFloat2;
        synchronized (paramAnonymousWindow.getTreeLock())
        {
          WindowPeer localWindowPeer = (WindowPeer)paramAnonymousWindow.getPeer();
          if (localWindowPeer != null) {
            localWindowPeer.repositionSecurityWarning();
          }
        }
      }
      
      public Point2D calculateSecurityWarningPosition(Window paramAnonymousWindow, double paramAnonymousDouble1, double paramAnonymousDouble2, double paramAnonymousDouble3, double paramAnonymousDouble4)
      {
        return paramAnonymousWindow.calculateSecurityWarningPosition(paramAnonymousDouble1, paramAnonymousDouble2, paramAnonymousDouble3, paramAnonymousDouble4);
      }
      
      public void setLWRequestStatus(Window paramAnonymousWindow, boolean paramAnonymousBoolean)
      {
        paramAnonymousWindow.syncLWRequests = paramAnonymousBoolean;
      }
      
      public boolean isAutoRequestFocus(Window paramAnonymousWindow)
      {
        return paramAnonymousWindow.autoRequestFocus;
      }
      
      public boolean isTrayIconWindow(Window paramAnonymousWindow)
      {
        return paramAnonymousWindow.isTrayIconWindow;
      }
      
      public void setTrayIconWindow(Window paramAnonymousWindow, boolean paramAnonymousBoolean)
      {
        paramAnonymousWindow.isTrayIconWindow = paramAnonymousBoolean;
      }
    });
  }
  
  protected class AccessibleAWTWindow
    extends Container.AccessibleAWTContainer
  {
    private static final long serialVersionUID = 4215068635060671780L;
    
    protected AccessibleAWTWindow()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.WINDOW;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (Window.this.getFocusOwner() != null) {
        localAccessibleStateSet.add(AccessibleState.ACTIVE);
      }
      return localAccessibleStateSet;
    }
  }
  
  public static enum Type
  {
    NORMAL,  UTILITY,  POPUP;
    
    private Type() {}
  }
  
  static class WindowDisposerRecord
    implements DisposerRecord
  {
    WeakReference<Window> owner;
    final WeakReference<Window> weakThis;
    final WeakReference<AppContext> context;
    
    WindowDisposerRecord(AppContext paramAppContext, Window paramWindow)
    {
      this.weakThis = paramWindow.weakThis;
      this.context = new WeakReference(paramAppContext);
    }
    
    public void updateOwner()
    {
      Window localWindow = (Window)this.weakThis.get();
      this.owner = (localWindow == null ? null : new WeakReference(localWindow.getOwner()));
    }
    
    public void dispose()
    {
      if (this.owner != null)
      {
        localObject = (Window)this.owner.get();
        if (localObject != null) {
          ((Window)localObject).removeOwnedWindow(this.weakThis);
        }
      }
      Object localObject = (AppContext)this.context.get();
      if (null != localObject) {
        Window.removeFromWindowList((AppContext)localObject, this.weakThis);
      }
    }
  }
}
