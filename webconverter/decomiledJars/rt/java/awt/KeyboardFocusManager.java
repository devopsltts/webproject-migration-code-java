package java.awt;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LightweightPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.KeyboardFocusManagerAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.KeyboardFocusManagerPeerProvider;
import sun.awt.SunToolkit;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public abstract class KeyboardFocusManager
  implements KeyEventDispatcher, KeyEventPostProcessor
{
  private static final PlatformLogger focusLog;
  transient KeyboardFocusManagerPeer peer;
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.KeyboardFocusManager");
  public static final int FORWARD_TRAVERSAL_KEYS = 0;
  public static final int BACKWARD_TRAVERSAL_KEYS = 1;
  public static final int UP_CYCLE_TRAVERSAL_KEYS = 2;
  public static final int DOWN_CYCLE_TRAVERSAL_KEYS = 3;
  static final int TRAVERSAL_KEY_LENGTH = 4;
  private static Component focusOwner;
  private static Component permanentFocusOwner;
  private static Window focusedWindow;
  private static Window activeWindow;
  private FocusTraversalPolicy defaultPolicy = new DefaultFocusTraversalPolicy();
  private static final String[] defaultFocusTraversalKeyPropertyNames = { "forwardDefaultFocusTraversalKeys", "backwardDefaultFocusTraversalKeys", "upCycleDefaultFocusTraversalKeys", "downCycleDefaultFocusTraversalKeys" };
  private static final AWTKeyStroke[][] defaultFocusTraversalKeyStrokes = { { AWTKeyStroke.getAWTKeyStroke(9, 0, false), AWTKeyStroke.getAWTKeyStroke(9, 130, false) }, { AWTKeyStroke.getAWTKeyStroke(9, 65, false), AWTKeyStroke.getAWTKeyStroke(9, 195, false) }, new AWTKeyStroke[0], new AWTKeyStroke[0] };
  private Set<AWTKeyStroke>[] defaultFocusTraversalKeys = new Set[4];
  private static Container currentFocusCycleRoot;
  private VetoableChangeSupport vetoableSupport;
  private PropertyChangeSupport changeSupport;
  private LinkedList<KeyEventDispatcher> keyEventDispatchers;
  private LinkedList<KeyEventPostProcessor> keyEventPostProcessors;
  private static Map<Window, WeakReference<Component>> mostRecentFocusOwners = new WeakHashMap();
  private static AWTPermission replaceKeyboardFocusManagerPermission;
  transient SequencedEvent currentSequencedEvent = null;
  private static LinkedList<HeavyweightFocusRequest> heavyweightRequests = new LinkedList();
  private static LinkedList<LightweightFocusRequest> currentLightweightRequests;
  private static boolean clearingCurrentLightweightRequests;
  private static boolean allowSyncFocusRequests = true;
  private static Component newFocusOwner = null;
  private static volatile boolean disableRestoreFocus;
  static final int SNFH_FAILURE = 0;
  static final int SNFH_SUCCESS_HANDLED = 1;
  static final int SNFH_SUCCESS_PROCEED = 2;
  static Field proxyActive;
  
  private static native void initIDs();
  
  public static KeyboardFocusManager getCurrentKeyboardFocusManager()
  {
    return getCurrentKeyboardFocusManager(AppContext.getAppContext());
  }
  
  static synchronized KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext paramAppContext)
  {
    Object localObject = (KeyboardFocusManager)paramAppContext.get(KeyboardFocusManager.class);
    if (localObject == null)
    {
      localObject = new DefaultKeyboardFocusManager();
      paramAppContext.put(KeyboardFocusManager.class, localObject);
    }
    return localObject;
  }
  
  public static void setCurrentKeyboardFocusManager(KeyboardFocusManager paramKeyboardFocusManager)
    throws SecurityException
  {
    checkReplaceKFMPermission();
    KeyboardFocusManager localKeyboardFocusManager = null;
    synchronized (KeyboardFocusManager.class)
    {
      AppContext localAppContext = AppContext.getAppContext();
      if (paramKeyboardFocusManager != null)
      {
        localKeyboardFocusManager = getCurrentKeyboardFocusManager(localAppContext);
        localAppContext.put(KeyboardFocusManager.class, paramKeyboardFocusManager);
      }
      else
      {
        localKeyboardFocusManager = getCurrentKeyboardFocusManager(localAppContext);
        localAppContext.remove(KeyboardFocusManager.class);
      }
    }
    if (localKeyboardFocusManager != null) {
      localKeyboardFocusManager.firePropertyChange("managingFocus", Boolean.TRUE, Boolean.FALSE);
    }
    if (paramKeyboardFocusManager != null) {
      paramKeyboardFocusManager.firePropertyChange("managingFocus", Boolean.FALSE, Boolean.TRUE);
    }
  }
  
  final void setCurrentSequencedEvent(SequencedEvent paramSequencedEvent)
  {
    synchronized (SequencedEvent.class)
    {
      assert ((paramSequencedEvent == null) || (this.currentSequencedEvent == null));
      this.currentSequencedEvent = paramSequencedEvent;
    }
  }
  
  final SequencedEvent getCurrentSequencedEvent()
  {
    synchronized (SequencedEvent.class)
    {
      return this.currentSequencedEvent;
    }
  }
  
  static Set<AWTKeyStroke> initFocusTraversalKeysSet(String paramString, Set<AWTKeyStroke> paramSet)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    while (localStringTokenizer.hasMoreTokens()) {
      paramSet.add(AWTKeyStroke.getAWTKeyStroke(localStringTokenizer.nextToken()));
    }
    return paramSet.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet(paramSet);
  }
  
  public KeyboardFocusManager()
  {
    for (int i = 0; i < 4; i++)
    {
      HashSet localHashSet = new HashSet();
      for (int j = 0; j < defaultFocusTraversalKeyStrokes[i].length; j++) {
        localHashSet.add(defaultFocusTraversalKeyStrokes[i][j]);
      }
      this.defaultFocusTraversalKeys[i] = (localHashSet.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet(localHashSet));
    }
    initPeer();
  }
  
  private void initPeer()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    KeyboardFocusManagerPeerProvider localKeyboardFocusManagerPeerProvider = (KeyboardFocusManagerPeerProvider)localToolkit;
    this.peer = localKeyboardFocusManagerPeerProvider.getKeyboardFocusManagerPeer();
  }
  
  public Component getFocusOwner()
  {
    synchronized (KeyboardFocusManager.class)
    {
      if (focusOwner == null) {
        return null;
      }
      return focusOwner.appContext == AppContext.getAppContext() ? focusOwner : null;
    }
  }
  
  protected Component getGlobalFocusOwner()
    throws SecurityException
  {
    synchronized (KeyboardFocusManager.class)
    {
      checkKFMSecurity();
      return focusOwner;
    }
  }
  
  protected void setGlobalFocusOwner(Component paramComponent)
    throws SecurityException
  {
    Component localComponent = null;
    int i = 0;
    if ((paramComponent == null) || (paramComponent.isFocusable())) {
      synchronized (KeyboardFocusManager.class)
      {
        checkKFMSecurity();
        localComponent = getFocusOwner();
        try
        {
          fireVetoableChange("focusOwner", localComponent, paramComponent);
        }
        catch (PropertyVetoException localPropertyVetoException)
        {
          return;
        }
        focusOwner = paramComponent;
        if ((paramComponent != null) && ((getCurrentFocusCycleRoot() == null) || (!paramComponent.isFocusCycleRoot(getCurrentFocusCycleRoot()))))
        {
          Container localContainer = paramComponent.getFocusCycleRootAncestor();
          if ((localContainer == null) && ((paramComponent instanceof Window))) {
            localContainer = (Container)paramComponent;
          }
          if (localContainer != null) {
            setGlobalCurrentFocusCycleRootPriv(localContainer);
          }
        }
        i = 1;
      }
    }
    if (i != 0) {
      firePropertyChange("focusOwner", localComponent, paramComponent);
    }
  }
  
  public void clearFocusOwner()
  {
    if (getFocusOwner() != null) {
      clearGlobalFocusOwner();
    }
  }
  
  public void clearGlobalFocusOwner()
    throws SecurityException
  {
    
    if (!GraphicsEnvironment.isHeadless())
    {
      Toolkit.getDefaultToolkit();
      _clearGlobalFocusOwner();
    }
  }
  
  private void _clearGlobalFocusOwner()
  {
    Window localWindow = markClearGlobalFocusOwner();
    this.peer.clearGlobalFocusOwner(localWindow);
  }
  
  void clearGlobalFocusOwnerPriv()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        KeyboardFocusManager.this.clearGlobalFocusOwner();
        return null;
      }
    });
  }
  
  Component getNativeFocusOwner()
  {
    return this.peer.getCurrentFocusOwner();
  }
  
  void setNativeFocusOwner(Component paramComponent)
  {
    if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
      focusLog.finest("Calling peer {0} setCurrentFocusOwner for {1}", new Object[] { String.valueOf(this.peer), String.valueOf(paramComponent) });
    }
    this.peer.setCurrentFocusOwner(paramComponent);
  }
  
  Window getNativeFocusedWindow()
  {
    return this.peer.getCurrentFocusedWindow();
  }
  
  public Component getPermanentFocusOwner()
  {
    synchronized (KeyboardFocusManager.class)
    {
      if (permanentFocusOwner == null) {
        return null;
      }
      return permanentFocusOwner.appContext == AppContext.getAppContext() ? permanentFocusOwner : null;
    }
  }
  
  protected Component getGlobalPermanentFocusOwner()
    throws SecurityException
  {
    synchronized (KeyboardFocusManager.class)
    {
      checkKFMSecurity();
      return permanentFocusOwner;
    }
  }
  
  protected void setGlobalPermanentFocusOwner(Component paramComponent)
    throws SecurityException
  {
    Component localComponent = null;
    int i = 0;
    if ((paramComponent == null) || (paramComponent.isFocusable())) {
      synchronized (KeyboardFocusManager.class)
      {
        checkKFMSecurity();
        localComponent = getPermanentFocusOwner();
        try
        {
          fireVetoableChange("permanentFocusOwner", localComponent, paramComponent);
        }
        catch (PropertyVetoException localPropertyVetoException)
        {
          return;
        }
        permanentFocusOwner = paramComponent;
        setMostRecentFocusOwner(paramComponent);
        i = 1;
      }
    }
    if (i != 0) {
      firePropertyChange("permanentFocusOwner", localComponent, paramComponent);
    }
  }
  
  public Window getFocusedWindow()
  {
    synchronized (KeyboardFocusManager.class)
    {
      if (focusedWindow == null) {
        return null;
      }
      return focusedWindow.appContext == AppContext.getAppContext() ? focusedWindow : null;
    }
  }
  
  protected Window getGlobalFocusedWindow()
    throws SecurityException
  {
    synchronized (KeyboardFocusManager.class)
    {
      checkKFMSecurity();
      return focusedWindow;
    }
  }
  
  protected void setGlobalFocusedWindow(Window paramWindow)
    throws SecurityException
  {
    Window localWindow = null;
    int i = 0;
    if ((paramWindow == null) || (paramWindow.isFocusableWindow())) {
      synchronized (KeyboardFocusManager.class)
      {
        checkKFMSecurity();
        localWindow = getFocusedWindow();
        try
        {
          fireVetoableChange("focusedWindow", localWindow, paramWindow);
        }
        catch (PropertyVetoException localPropertyVetoException)
        {
          return;
        }
        focusedWindow = paramWindow;
        i = 1;
      }
    }
    if (i != 0) {
      firePropertyChange("focusedWindow", localWindow, paramWindow);
    }
  }
  
  public Window getActiveWindow()
  {
    synchronized (KeyboardFocusManager.class)
    {
      if (activeWindow == null) {
        return null;
      }
      return activeWindow.appContext == AppContext.getAppContext() ? activeWindow : null;
    }
  }
  
  protected Window getGlobalActiveWindow()
    throws SecurityException
  {
    synchronized (KeyboardFocusManager.class)
    {
      checkKFMSecurity();
      return activeWindow;
    }
  }
  
  protected void setGlobalActiveWindow(Window paramWindow)
    throws SecurityException
  {
    Window localWindow;
    synchronized (KeyboardFocusManager.class)
    {
      checkKFMSecurity();
      localWindow = getActiveWindow();
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
        focusLog.finer("Setting global active window to " + paramWindow + ", old active " + localWindow);
      }
      try
      {
        fireVetoableChange("activeWindow", localWindow, paramWindow);
      }
      catch (PropertyVetoException localPropertyVetoException)
      {
        return;
      }
      activeWindow = paramWindow;
    }
    firePropertyChange("activeWindow", localWindow, paramWindow);
  }
  
  public synchronized FocusTraversalPolicy getDefaultFocusTraversalPolicy()
  {
    return this.defaultPolicy;
  }
  
  public void setDefaultFocusTraversalPolicy(FocusTraversalPolicy paramFocusTraversalPolicy)
  {
    if (paramFocusTraversalPolicy == null) {
      throw new IllegalArgumentException("default focus traversal policy cannot be null");
    }
    FocusTraversalPolicy localFocusTraversalPolicy;
    synchronized (this)
    {
      localFocusTraversalPolicy = this.defaultPolicy;
      this.defaultPolicy = paramFocusTraversalPolicy;
    }
    firePropertyChange("defaultFocusTraversalPolicy", localFocusTraversalPolicy, paramFocusTraversalPolicy);
  }
  
  public void setDefaultFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("invalid focus traversal key identifier");
    }
    if (paramSet == null) {
      throw new IllegalArgumentException("cannot set null Set of default focus traversal keys");
    }
    Set localSet;
    synchronized (this)
    {
      Iterator localIterator = paramSet.iterator();
      while (localIterator.hasNext())
      {
        AWTKeyStroke localAWTKeyStroke = (AWTKeyStroke)localIterator.next();
        if (localAWTKeyStroke == null) {
          throw new IllegalArgumentException("cannot set null focus traversal key");
        }
        if (localAWTKeyStroke.getKeyChar() != 65535) {
          throw new IllegalArgumentException("focus traversal keys cannot map to KEY_TYPED events");
        }
        for (int i = 0; i < 4; i++) {
          if ((i != paramInt) && (this.defaultFocusTraversalKeys[i].contains(localAWTKeyStroke))) {
            throw new IllegalArgumentException("focus traversal keys must be unique for a Component");
          }
        }
      }
      localSet = this.defaultFocusTraversalKeys[paramInt];
      this.defaultFocusTraversalKeys[paramInt] = Collections.unmodifiableSet(new HashSet(paramSet));
    }
    firePropertyChange(defaultFocusTraversalKeyPropertyNames[paramInt], localSet, paramSet);
  }
  
  public Set<AWTKeyStroke> getDefaultFocusTraversalKeys(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("invalid focus traversal key identifier");
    }
    return this.defaultFocusTraversalKeys[paramInt];
  }
  
  public Container getCurrentFocusCycleRoot()
  {
    synchronized (KeyboardFocusManager.class)
    {
      if (currentFocusCycleRoot == null) {
        return null;
      }
      return currentFocusCycleRoot.appContext == AppContext.getAppContext() ? currentFocusCycleRoot : null;
    }
  }
  
  protected Container getGlobalCurrentFocusCycleRoot()
    throws SecurityException
  {
    synchronized (KeyboardFocusManager.class)
    {
      checkKFMSecurity();
      return currentFocusCycleRoot;
    }
  }
  
  public void setGlobalCurrentFocusCycleRoot(Container paramContainer)
    throws SecurityException
  {
    
    Container localContainer;
    synchronized (KeyboardFocusManager.class)
    {
      localContainer = getCurrentFocusCycleRoot();
      currentFocusCycleRoot = paramContainer;
    }
    firePropertyChange("currentFocusCycleRoot", localContainer, paramContainer);
  }
  
  void setGlobalCurrentFocusCycleRootPriv(final Container paramContainer)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        KeyboardFocusManager.this.setGlobalCurrentFocusCycleRoot(paramContainer);
        return null;
      }
    });
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (paramPropertyChangeListener != null) {
      synchronized (this)
      {
        if (this.changeSupport == null) {
          this.changeSupport = new PropertyChangeSupport(this);
        }
        this.changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
      }
    }
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (paramPropertyChangeListener != null) {
      synchronized (this)
      {
        if (this.changeSupport != null) {
          this.changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
        }
      }
    }
  }
  
  public synchronized PropertyChangeListener[] getPropertyChangeListeners()
  {
    if (this.changeSupport == null) {
      this.changeSupport = new PropertyChangeSupport(this);
    }
    return this.changeSupport.getPropertyChangeListeners();
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    if (paramPropertyChangeListener != null) {
      synchronized (this)
      {
        if (this.changeSupport == null) {
          this.changeSupport = new PropertyChangeSupport(this);
        }
        this.changeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
      }
    }
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    if (paramPropertyChangeListener != null) {
      synchronized (this)
      {
        if (this.changeSupport != null) {
          this.changeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
        }
      }
    }
  }
  
  public synchronized PropertyChangeListener[] getPropertyChangeListeners(String paramString)
  {
    if (this.changeSupport == null) {
      this.changeSupport = new PropertyChangeSupport(this);
    }
    return this.changeSupport.getPropertyChangeListeners(paramString);
  }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == paramObject2) {
      return;
    }
    PropertyChangeSupport localPropertyChangeSupport = this.changeSupport;
    if (localPropertyChangeSupport != null) {
      localPropertyChangeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
    }
  }
  
  public void addVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener)
  {
    if (paramVetoableChangeListener != null) {
      synchronized (this)
      {
        if (this.vetoableSupport == null) {
          this.vetoableSupport = new VetoableChangeSupport(this);
        }
        this.vetoableSupport.addVetoableChangeListener(paramVetoableChangeListener);
      }
    }
  }
  
  public void removeVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener)
  {
    if (paramVetoableChangeListener != null) {
      synchronized (this)
      {
        if (this.vetoableSupport != null) {
          this.vetoableSupport.removeVetoableChangeListener(paramVetoableChangeListener);
        }
      }
    }
  }
  
  public synchronized VetoableChangeListener[] getVetoableChangeListeners()
  {
    if (this.vetoableSupport == null) {
      this.vetoableSupport = new VetoableChangeSupport(this);
    }
    return this.vetoableSupport.getVetoableChangeListeners();
  }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    if (paramVetoableChangeListener != null) {
      synchronized (this)
      {
        if (this.vetoableSupport == null) {
          this.vetoableSupport = new VetoableChangeSupport(this);
        }
        this.vetoableSupport.addVetoableChangeListener(paramString, paramVetoableChangeListener);
      }
    }
  }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    if (paramVetoableChangeListener != null) {
      synchronized (this)
      {
        if (this.vetoableSupport != null) {
          this.vetoableSupport.removeVetoableChangeListener(paramString, paramVetoableChangeListener);
        }
      }
    }
  }
  
  public synchronized VetoableChangeListener[] getVetoableChangeListeners(String paramString)
  {
    if (this.vetoableSupport == null) {
      this.vetoableSupport = new VetoableChangeSupport(this);
    }
    return this.vetoableSupport.getVetoableChangeListeners(paramString);
  }
  
  protected void fireVetoableChange(String paramString, Object paramObject1, Object paramObject2)
    throws PropertyVetoException
  {
    if (paramObject1 == paramObject2) {
      return;
    }
    VetoableChangeSupport localVetoableChangeSupport = this.vetoableSupport;
    if (localVetoableChangeSupport != null) {
      localVetoableChangeSupport.fireVetoableChange(paramString, paramObject1, paramObject2);
    }
  }
  
  public void addKeyEventDispatcher(KeyEventDispatcher paramKeyEventDispatcher)
  {
    if (paramKeyEventDispatcher != null) {
      synchronized (this)
      {
        if (this.keyEventDispatchers == null) {
          this.keyEventDispatchers = new LinkedList();
        }
        this.keyEventDispatchers.add(paramKeyEventDispatcher);
      }
    }
  }
  
  public void removeKeyEventDispatcher(KeyEventDispatcher paramKeyEventDispatcher)
  {
    if (paramKeyEventDispatcher != null) {
      synchronized (this)
      {
        if (this.keyEventDispatchers != null) {
          this.keyEventDispatchers.remove(paramKeyEventDispatcher);
        }
      }
    }
  }
  
  protected synchronized List<KeyEventDispatcher> getKeyEventDispatchers()
  {
    return this.keyEventDispatchers != null ? (List)this.keyEventDispatchers.clone() : null;
  }
  
  public void addKeyEventPostProcessor(KeyEventPostProcessor paramKeyEventPostProcessor)
  {
    if (paramKeyEventPostProcessor != null) {
      synchronized (this)
      {
        if (this.keyEventPostProcessors == null) {
          this.keyEventPostProcessors = new LinkedList();
        }
        this.keyEventPostProcessors.add(paramKeyEventPostProcessor);
      }
    }
  }
  
  public void removeKeyEventPostProcessor(KeyEventPostProcessor paramKeyEventPostProcessor)
  {
    if (paramKeyEventPostProcessor != null) {
      synchronized (this)
      {
        if (this.keyEventPostProcessors != null) {
          this.keyEventPostProcessors.remove(paramKeyEventPostProcessor);
        }
      }
    }
  }
  
  protected List<KeyEventPostProcessor> getKeyEventPostProcessors()
  {
    return this.keyEventPostProcessors != null ? (List)this.keyEventPostProcessors.clone() : null;
  }
  
  static void setMostRecentFocusOwner(Component paramComponent)
  {
    for (Object localObject = paramComponent; (localObject != null) && (!(localObject instanceof Window)); localObject = ((Component)localObject).parent) {}
    if (localObject != null) {
      setMostRecentFocusOwner((Window)localObject, paramComponent);
    }
  }
  
  static synchronized void setMostRecentFocusOwner(Window paramWindow, Component paramComponent)
  {
    WeakReference localWeakReference = null;
    if (paramComponent != null) {
      localWeakReference = new WeakReference(paramComponent);
    }
    mostRecentFocusOwners.put(paramWindow, localWeakReference);
  }
  
  static void clearMostRecentFocusOwner(Component paramComponent)
  {
    if (paramComponent == null) {
      return;
    }
    Container localContainer;
    synchronized (paramComponent.getTreeLock())
    {
      for (localContainer = paramComponent.getParent(); (localContainer != null) && (!(localContainer instanceof Window)); localContainer = localContainer.getParent()) {}
    }
    synchronized (KeyboardFocusManager.class)
    {
      if ((localContainer != null) && (getMostRecentFocusOwner((Window)localContainer) == paramComponent)) {
        setMostRecentFocusOwner((Window)localContainer, null);
      }
      if (localContainer != null)
      {
        Window localWindow = (Window)localContainer;
        if (localWindow.getTemporaryLostComponent() == paramComponent) {
          localWindow.setTemporaryLostComponent(null);
        }
      }
    }
  }
  
  static synchronized Component getMostRecentFocusOwner(Window paramWindow)
  {
    WeakReference localWeakReference = (WeakReference)mostRecentFocusOwners.get(paramWindow);
    return localWeakReference == null ? null : (Component)localWeakReference.get();
  }
  
  public abstract boolean dispatchEvent(AWTEvent paramAWTEvent);
  
  public final void redispatchEvent(Component paramComponent, AWTEvent paramAWTEvent)
  {
    paramAWTEvent.focusManagerIsDispatching = true;
    paramComponent.dispatchEvent(paramAWTEvent);
    paramAWTEvent.focusManagerIsDispatching = false;
  }
  
  public abstract boolean dispatchKeyEvent(KeyEvent paramKeyEvent);
  
  public abstract boolean postProcessKeyEvent(KeyEvent paramKeyEvent);
  
  public abstract void processKeyEvent(Component paramComponent, KeyEvent paramKeyEvent);
  
  protected abstract void enqueueKeyEvents(long paramLong, Component paramComponent);
  
  protected abstract void dequeueKeyEvents(long paramLong, Component paramComponent);
  
  protected abstract void discardKeyEvents(Component paramComponent);
  
  public abstract void focusNextComponent(Component paramComponent);
  
  public abstract void focusPreviousComponent(Component paramComponent);
  
  public abstract void upFocusCycle(Component paramComponent);
  
  public abstract void downFocusCycle(Container paramContainer);
  
  public final void focusNextComponent()
  {
    Component localComponent = getFocusOwner();
    if (localComponent != null) {
      focusNextComponent(localComponent);
    }
  }
  
  public final void focusPreviousComponent()
  {
    Component localComponent = getFocusOwner();
    if (localComponent != null) {
      focusPreviousComponent(localComponent);
    }
  }
  
  public final void upFocusCycle()
  {
    Component localComponent = getFocusOwner();
    if (localComponent != null) {
      upFocusCycle(localComponent);
    }
  }
  
  public final void downFocusCycle()
  {
    Component localComponent = getFocusOwner();
    if ((localComponent instanceof Container)) {
      downFocusCycle((Container)localComponent);
    }
  }
  
  void dumpRequests()
  {
    System.err.println(">>> Requests dump, time: " + System.currentTimeMillis());
    synchronized (heavyweightRequests)
    {
      Iterator localIterator = heavyweightRequests.iterator();
      while (localIterator.hasNext())
      {
        HeavyweightFocusRequest localHeavyweightFocusRequest = (HeavyweightFocusRequest)localIterator.next();
        System.err.println(">>> Req: " + localHeavyweightFocusRequest);
      }
    }
    System.err.println("");
  }
  
  static boolean processSynchronousLightweightTransfer(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
  {
    Window localWindow = SunToolkit.getContainingWindow(paramComponent1);
    if ((localWindow == null) || (!localWindow.syncLWRequests)) {
      return false;
    }
    if (paramComponent2 == null) {
      paramComponent2 = paramComponent1;
    }
    KeyboardFocusManager localKeyboardFocusManager = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(paramComponent2));
    FocusEvent localFocusEvent1 = null;
    FocusEvent localFocusEvent2 = null;
    Component localComponent = localKeyboardFocusManager.getGlobalFocusOwner();
    synchronized (heavyweightRequests)
    {
      HeavyweightFocusRequest localHeavyweightFocusRequest = getLastHWRequest();
      if ((localHeavyweightFocusRequest == null) && (paramComponent1 == localKeyboardFocusManager.getNativeFocusOwner()) && (allowSyncFocusRequests))
      {
        if (paramComponent2 == localComponent) {
          return true;
        }
        localKeyboardFocusManager.enqueueKeyEvents(paramLong, paramComponent2);
        localHeavyweightFocusRequest = new HeavyweightFocusRequest(paramComponent1, paramComponent2, paramBoolean1, CausedFocusEvent.Cause.UNKNOWN);
        heavyweightRequests.add(localHeavyweightFocusRequest);
        if (localComponent != null) {
          localFocusEvent1 = new FocusEvent(localComponent, 1005, paramBoolean1, paramComponent2);
        }
        localFocusEvent2 = new FocusEvent(paramComponent2, 1004, paramBoolean1, localComponent);
      }
    }
    boolean bool1 = false;
    boolean bool2 = clearingCurrentLightweightRequests;
    Throwable localThrowable = null;
    try
    {
      clearingCurrentLightweightRequests = false;
      synchronized (Component.LOCK)
      {
        if ((localFocusEvent1 != null) && (localComponent != null))
        {
          localFocusEvent1.isPosted = true;
          localThrowable = dispatchAndCatchException(localThrowable, localComponent, localFocusEvent1);
          bool1 = true;
        }
        if ((localFocusEvent2 != null) && (paramComponent2 != null))
        {
          localFocusEvent2.isPosted = true;
          localThrowable = dispatchAndCatchException(localThrowable, paramComponent2, localFocusEvent2);
          bool1 = true;
        }
      }
    }
    finally
    {
      clearingCurrentLightweightRequests = bool2;
    }
    if ((localThrowable instanceof RuntimeException)) {
      throw ((RuntimeException)localThrowable);
    }
    if ((localThrowable instanceof Error)) {
      throw ((Error)localThrowable);
    }
    return bool1;
  }
  
  static int shouldNativelyFocusHeavyweight(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
  {
    if (log.isLoggable(PlatformLogger.Level.FINE))
    {
      if (paramComponent1 == null) {
        log.fine("Assertion (heavyweight != null) failed");
      }
      if (paramLong == 0L) {
        log.fine("Assertion (time != 0) failed");
      }
    }
    if (paramComponent2 == null) {
      paramComponent2 = paramComponent1;
    }
    KeyboardFocusManager localKeyboardFocusManager1 = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(paramComponent2));
    KeyboardFocusManager localKeyboardFocusManager2 = getCurrentKeyboardFocusManager();
    Component localComponent1 = localKeyboardFocusManager2.getGlobalFocusOwner();
    Component localComponent2 = localKeyboardFocusManager2.getNativeFocusOwner();
    Window localWindow = localKeyboardFocusManager2.getNativeFocusedWindow();
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      focusLog.finer("SNFH for {0} in {1}", new Object[] { String.valueOf(paramComponent2), String.valueOf(paramComponent1) });
    }
    if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
    {
      focusLog.finest("0. Current focus owner {0}", new Object[] { String.valueOf(localComponent1) });
      focusLog.finest("0. Native focus owner {0}", new Object[] { String.valueOf(localComponent2) });
      focusLog.finest("0. Native focused window {0}", new Object[] { String.valueOf(localWindow) });
    }
    synchronized (heavyweightRequests)
    {
      HeavyweightFocusRequest localHeavyweightFocusRequest = getLastHWRequest();
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("Request {0}", new Object[] { String.valueOf(localHeavyweightFocusRequest) });
      }
      if ((localHeavyweightFocusRequest == null) && (paramComponent1 == localComponent2) && (paramComponent1.getContainingWindow() == localWindow))
      {
        if (paramComponent2 == localComponent1)
        {
          if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            focusLog.finest("1. SNFH_FAILURE for {0}", new Object[] { String.valueOf(paramComponent2) });
          }
          return 0;
        }
        localKeyboardFocusManager1.enqueueKeyEvents(paramLong, paramComponent2);
        localHeavyweightFocusRequest = new HeavyweightFocusRequest(paramComponent1, paramComponent2, paramBoolean1, paramCause);
        heavyweightRequests.add(localHeavyweightFocusRequest);
        if (localComponent1 != null)
        {
          localCausedFocusEvent = new CausedFocusEvent(localComponent1, 1005, paramBoolean1, paramComponent2, paramCause);
          SunToolkit.postEvent(localComponent1.appContext, localCausedFocusEvent);
        }
        CausedFocusEvent localCausedFocusEvent = new CausedFocusEvent(paramComponent2, 1004, paramBoolean1, localComponent1, paramCause);
        SunToolkit.postEvent(paramComponent2.appContext, localCausedFocusEvent);
        if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
          focusLog.finest("2. SNFH_HANDLED for {0}", new Object[] { String.valueOf(paramComponent2) });
        }
        return 1;
      }
      if ((localHeavyweightFocusRequest != null) && (localHeavyweightFocusRequest.heavyweight == paramComponent1))
      {
        if (localHeavyweightFocusRequest.addLightweightRequest(paramComponent2, paramBoolean1, paramCause)) {
          localKeyboardFocusManager1.enqueueKeyEvents(paramLong, paramComponent2);
        }
        if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
          focusLog.finest("3. SNFH_HANDLED for lightweight" + paramComponent2 + " in " + paramComponent1);
        }
        return 1;
      }
      if (!paramBoolean2)
      {
        if (localHeavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER)
        {
          int i = heavyweightRequests.size();
          localHeavyweightFocusRequest = i >= 2 ? (HeavyweightFocusRequest)heavyweightRequests.get(i - 2) : null;
        }
        if (focusedWindowChanged(paramComponent1, localHeavyweightFocusRequest != null ? localHeavyweightFocusRequest.heavyweight : localWindow))
        {
          if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            focusLog.finest("4. SNFH_FAILURE for " + paramComponent2);
          }
          return 0;
        }
      }
      localKeyboardFocusManager1.enqueueKeyEvents(paramLong, paramComponent2);
      heavyweightRequests.add(new HeavyweightFocusRequest(paramComponent1, paramComponent2, paramBoolean1, paramCause));
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("5. SNFH_PROCEED for " + paramComponent2);
      }
      return 2;
    }
  }
  
  static Window markClearGlobalFocusOwner()
  {
    Window localWindow = getCurrentKeyboardFocusManager().getNativeFocusedWindow();
    synchronized (heavyweightRequests)
    {
      HeavyweightFocusRequest localHeavyweightFocusRequest = getLastHWRequest();
      if (localHeavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
        return null;
      }
      heavyweightRequests.add(HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER);
      for (Object localObject1 = localHeavyweightFocusRequest != null ? SunToolkit.getContainingWindow(localHeavyweightFocusRequest.heavyweight) : localWindow; (localObject1 != null) && (!(localObject1 instanceof Frame)) && (!(localObject1 instanceof Dialog)); localObject1 = ((Component)localObject1).getParent_NoClientCode()) {}
      return (Window)localObject1;
    }
  }
  
  Component getCurrentWaitingRequest(Component paramComponent)
  {
    synchronized (heavyweightRequests)
    {
      HeavyweightFocusRequest localHeavyweightFocusRequest = getFirstHWRequest();
      if ((localHeavyweightFocusRequest != null) && (localHeavyweightFocusRequest.heavyweight == paramComponent))
      {
        LightweightFocusRequest localLightweightFocusRequest = (LightweightFocusRequest)localHeavyweightFocusRequest.lightweightRequests.getFirst();
        if (localLightweightFocusRequest != null) {
          return localLightweightFocusRequest.component;
        }
      }
    }
    return null;
  }
  
  static boolean isAutoFocusTransferEnabled()
  {
    synchronized (heavyweightRequests)
    {
      return (heavyweightRequests.size() == 0) && (!disableRestoreFocus) && (null == currentLightweightRequests);
    }
  }
  
  static boolean isAutoFocusTransferEnabledFor(Component paramComponent)
  {
    return (isAutoFocusTransferEnabled()) && (paramComponent.isAutoFocusTransferOnDisposal());
  }
  
  private static Throwable dispatchAndCatchException(Throwable paramThrowable, Component paramComponent, FocusEvent paramFocusEvent)
  {
    Object localObject = null;
    try
    {
      paramComponent.dispatchEvent(paramFocusEvent);
    }
    catch (RuntimeException localRuntimeException)
    {
      localObject = localRuntimeException;
    }
    catch (Error localError)
    {
      localObject = localError;
    }
    if (localObject != null)
    {
      if (paramThrowable != null) {
        handleException(paramThrowable);
      }
      return localObject;
    }
    return paramThrowable;
  }
  
  private static void handleException(Throwable paramThrowable)
  {
    paramThrowable.printStackTrace();
  }
  
  static void processCurrentLightweightRequests()
  {
    KeyboardFocusManager localKeyboardFocusManager = getCurrentKeyboardFocusManager();
    LinkedList localLinkedList = null;
    Component localComponent1 = localKeyboardFocusManager.getGlobalFocusOwner();
    if ((localComponent1 != null) && (localComponent1.appContext != AppContext.getAppContext())) {
      return;
    }
    synchronized (heavyweightRequests)
    {
      if (currentLightweightRequests != null)
      {
        clearingCurrentLightweightRequests = true;
        disableRestoreFocus = true;
        localLinkedList = currentLightweightRequests;
        allowSyncFocusRequests = localLinkedList.size() < 2;
        currentLightweightRequests = null;
      }
      else
      {
        return;
      }
    }
    ??? = null;
    try
    {
      if (localLinkedList != null)
      {
        Component localComponent2 = null;
        Component localComponent3 = null;
        Iterator localIterator = localLinkedList.iterator();
        while (localIterator.hasNext())
        {
          localComponent3 = localKeyboardFocusManager.getGlobalFocusOwner();
          LightweightFocusRequest localLightweightFocusRequest = (LightweightFocusRequest)localIterator.next();
          if (!localIterator.hasNext()) {
            disableRestoreFocus = false;
          }
          CausedFocusEvent localCausedFocusEvent1 = null;
          if (localComponent3 != null) {
            localCausedFocusEvent1 = new CausedFocusEvent(localComponent3, 1005, localLightweightFocusRequest.temporary, localLightweightFocusRequest.component, localLightweightFocusRequest.cause);
          }
          CausedFocusEvent localCausedFocusEvent2 = new CausedFocusEvent(localLightweightFocusRequest.component, 1004, localLightweightFocusRequest.temporary, localComponent3 == null ? localComponent2 : localComponent3, localLightweightFocusRequest.cause);
          if (localComponent3 != null)
          {
            localCausedFocusEvent1.isPosted = true;
            ??? = dispatchAndCatchException((Throwable)???, localComponent3, localCausedFocusEvent1);
          }
          localCausedFocusEvent2.isPosted = true;
          ??? = dispatchAndCatchException((Throwable)???, localLightweightFocusRequest.component, localCausedFocusEvent2);
          if (localKeyboardFocusManager.getGlobalFocusOwner() == localLightweightFocusRequest.component) {
            localComponent2 = localLightweightFocusRequest.component;
          }
        }
      }
    }
    finally
    {
      clearingCurrentLightweightRequests = false;
      disableRestoreFocus = false;
      localLinkedList = null;
      allowSyncFocusRequests = true;
    }
    if ((??? instanceof RuntimeException)) {
      throw ((RuntimeException)???);
    }
    if ((??? instanceof Error)) {
      throw ((Error)???);
    }
  }
  
  static FocusEvent retargetUnexpectedFocusEvent(FocusEvent paramFocusEvent)
  {
    synchronized (heavyweightRequests)
    {
      if (removeFirstRequest()) {
        return (FocusEvent)retargetFocusEvent(paramFocusEvent);
      }
      Component localComponent1 = paramFocusEvent.getComponent();
      Component localComponent2 = paramFocusEvent.getOppositeComponent();
      boolean bool = false;
      if ((paramFocusEvent.getID() == 1005) && ((localComponent2 == null) || (isTemporary(localComponent2, localComponent1)))) {
        bool = true;
      }
      return new CausedFocusEvent(localComponent1, paramFocusEvent.getID(), bool, localComponent2, CausedFocusEvent.Cause.NATIVE_SYSTEM);
    }
  }
  
  static FocusEvent retargetFocusGained(FocusEvent paramFocusEvent)
  {
    assert (paramFocusEvent.getID() == 1004);
    Component localComponent1 = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
    Component localComponent2 = paramFocusEvent.getComponent();
    Component localComponent3 = paramFocusEvent.getOppositeComponent();
    Component localComponent4 = getHeavyweight(localComponent2);
    synchronized (heavyweightRequests)
    {
      HeavyweightFocusRequest localHeavyweightFocusRequest = getFirstHWRequest();
      if (localHeavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
        return retargetUnexpectedFocusEvent(paramFocusEvent);
      }
      if ((localComponent2 != null) && (localComponent4 == null) && (localHeavyweightFocusRequest != null) && (localComponent2 == localHeavyweightFocusRequest.getFirstLightweightRequest().component))
      {
        localComponent2 = localHeavyweightFocusRequest.heavyweight;
        localComponent4 = localComponent2;
      }
      if ((localHeavyweightFocusRequest != null) && (localComponent4 == localHeavyweightFocusRequest.heavyweight))
      {
        heavyweightRequests.removeFirst();
        LightweightFocusRequest localLightweightFocusRequest = (LightweightFocusRequest)localHeavyweightFocusRequest.lightweightRequests.removeFirst();
        Component localComponent5 = localLightweightFocusRequest.component;
        if (localComponent1 != null) {
          newFocusOwner = localComponent5;
        }
        boolean bool = (localComponent3 == null) || (isTemporary(localComponent5, localComponent3)) ? false : localLightweightFocusRequest.temporary;
        if (localHeavyweightFocusRequest.lightweightRequests.size() > 0)
        {
          currentLightweightRequests = localHeavyweightFocusRequest.lightweightRequests;
          EventQueue.invokeLater(new Runnable()
          {
            public void run() {}
          });
        }
        return new CausedFocusEvent(localComponent5, 1004, bool, localComponent3, localLightweightFocusRequest.cause);
      }
      if ((localComponent1 != null) && (localComponent1.getContainingWindow() == localComponent2) && ((localHeavyweightFocusRequest == null) || (localComponent2 != localHeavyweightFocusRequest.heavyweight))) {
        return new CausedFocusEvent(localComponent1, 1004, false, null, CausedFocusEvent.Cause.ACTIVATION);
      }
      return retargetUnexpectedFocusEvent(paramFocusEvent);
    }
  }
  
  static FocusEvent retargetFocusLost(FocusEvent paramFocusEvent)
  {
    assert (paramFocusEvent.getID() == 1005);
    Component localComponent1 = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
    Component localComponent2 = paramFocusEvent.getOppositeComponent();
    Component localComponent3 = getHeavyweight(localComponent2);
    synchronized (heavyweightRequests)
    {
      HeavyweightFocusRequest localHeavyweightFocusRequest = getFirstHWRequest();
      if (localHeavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER)
      {
        if (localComponent1 != null)
        {
          heavyweightRequests.removeFirst();
          return new CausedFocusEvent(localComponent1, 1005, false, null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER);
        }
      }
      else
      {
        if (localComponent2 == null)
        {
          if (localComponent1 != null) {
            return new CausedFocusEvent(localComponent1, 1005, true, null, CausedFocusEvent.Cause.ACTIVATION);
          }
          return paramFocusEvent;
        }
        if ((localHeavyweightFocusRequest != null) && ((localComponent3 == localHeavyweightFocusRequest.heavyweight) || ((localComponent3 == null) && (localComponent2 == localHeavyweightFocusRequest.getFirstLightweightRequest().component))))
        {
          if (localComponent1 == null) {
            return paramFocusEvent;
          }
          LightweightFocusRequest localLightweightFocusRequest = (LightweightFocusRequest)localHeavyweightFocusRequest.lightweightRequests.getFirst();
          boolean bool = isTemporary(localComponent2, localComponent1) ? true : localLightweightFocusRequest.temporary;
          return new CausedFocusEvent(localComponent1, 1005, bool, localLightweightFocusRequest.component, localLightweightFocusRequest.cause);
        }
        if (focusedWindowChanged(localComponent2, localComponent1))
        {
          if ((!paramFocusEvent.isTemporary()) && (localComponent1 != null)) {
            paramFocusEvent = new CausedFocusEvent(localComponent1, 1005, true, localComponent2, CausedFocusEvent.Cause.ACTIVATION);
          }
          return paramFocusEvent;
        }
      }
      return retargetUnexpectedFocusEvent(paramFocusEvent);
    }
  }
  
  static AWTEvent retargetFocusEvent(AWTEvent paramAWTEvent)
  {
    if (clearingCurrentLightweightRequests) {
      return paramAWTEvent;
    }
    KeyboardFocusManager localKeyboardFocusManager = getCurrentKeyboardFocusManager();
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
    {
      if (((paramAWTEvent instanceof FocusEvent)) || ((paramAWTEvent instanceof WindowEvent))) {
        focusLog.finer(">>> {0}", new Object[] { String.valueOf(paramAWTEvent) });
      }
      if ((focusLog.isLoggable(PlatformLogger.Level.FINER)) && ((paramAWTEvent instanceof KeyEvent)))
      {
        focusLog.finer("    focus owner is {0}", new Object[] { String.valueOf(localKeyboardFocusManager.getGlobalFocusOwner()) });
        focusLog.finer(">>> {0}", new Object[] { String.valueOf(paramAWTEvent) });
      }
    }
    synchronized (heavyweightRequests)
    {
      if ((newFocusOwner != null) && (paramAWTEvent.getID() == 1005))
      {
        FocusEvent localFocusEvent = (FocusEvent)paramAWTEvent;
        if ((localKeyboardFocusManager.getGlobalFocusOwner() == localFocusEvent.getComponent()) && (localFocusEvent.getOppositeComponent() == newFocusOwner))
        {
          newFocusOwner = null;
          return paramAWTEvent;
        }
      }
    }
    processCurrentLightweightRequests();
    switch (paramAWTEvent.getID())
    {
    case 1004: 
      paramAWTEvent = retargetFocusGained((FocusEvent)paramAWTEvent);
      break;
    case 1005: 
      paramAWTEvent = retargetFocusLost((FocusEvent)paramAWTEvent);
      break;
    }
    return paramAWTEvent;
  }
  
  void clearMarkers() {}
  
  static boolean removeFirstRequest()
  {
    KeyboardFocusManager localKeyboardFocusManager = getCurrentKeyboardFocusManager();
    synchronized (heavyweightRequests)
    {
      HeavyweightFocusRequest localHeavyweightFocusRequest = getFirstHWRequest();
      if (localHeavyweightFocusRequest != null)
      {
        heavyweightRequests.removeFirst();
        if (localHeavyweightFocusRequest.lightweightRequests != null)
        {
          Iterator localIterator = localHeavyweightFocusRequest.lightweightRequests.iterator();
          while (localIterator.hasNext()) {
            localKeyboardFocusManager.dequeueKeyEvents(-1L, ((LightweightFocusRequest)localIterator.next()).component);
          }
        }
      }
      if (heavyweightRequests.size() == 0) {
        localKeyboardFocusManager.clearMarkers();
      }
      return heavyweightRequests.size() > 0;
    }
  }
  
  static void removeLastFocusRequest(Component paramComponent)
  {
    if ((log.isLoggable(PlatformLogger.Level.FINE)) && (paramComponent == null)) {
      log.fine("Assertion (heavyweight != null) failed");
    }
    KeyboardFocusManager localKeyboardFocusManager = getCurrentKeyboardFocusManager();
    synchronized (heavyweightRequests)
    {
      HeavyweightFocusRequest localHeavyweightFocusRequest = getLastHWRequest();
      if ((localHeavyweightFocusRequest != null) && (localHeavyweightFocusRequest.heavyweight == paramComponent)) {
        heavyweightRequests.removeLast();
      }
      if (heavyweightRequests.size() == 0) {
        localKeyboardFocusManager.clearMarkers();
      }
    }
  }
  
  private static boolean focusedWindowChanged(Component paramComponent1, Component paramComponent2)
  {
    Window localWindow1 = SunToolkit.getContainingWindow(paramComponent1);
    Window localWindow2 = SunToolkit.getContainingWindow(paramComponent2);
    if ((localWindow1 == null) && (localWindow2 == null)) {
      return true;
    }
    if (localWindow1 == null) {
      return true;
    }
    if (localWindow2 == null) {
      return true;
    }
    return localWindow1 != localWindow2;
  }
  
  private static boolean isTemporary(Component paramComponent1, Component paramComponent2)
  {
    Window localWindow1 = SunToolkit.getContainingWindow(paramComponent1);
    Window localWindow2 = SunToolkit.getContainingWindow(paramComponent2);
    if ((localWindow1 == null) && (localWindow2 == null)) {
      return false;
    }
    if (localWindow1 == null) {
      return true;
    }
    if (localWindow2 == null) {
      return false;
    }
    return localWindow1 != localWindow2;
  }
  
  static Component getHeavyweight(Component paramComponent)
  {
    if ((paramComponent == null) || (paramComponent.getPeer() == null)) {
      return null;
    }
    if ((paramComponent.getPeer() instanceof LightweightPeer)) {
      return paramComponent.getNativeContainer();
    }
    return paramComponent;
  }
  
  private static boolean isProxyActiveImpl(KeyEvent paramKeyEvent)
  {
    if (proxyActive == null) {
      proxyActive = (Field)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Field run()
        {
          Field localField = null;
          try
          {
            localField = KeyEvent.class.getDeclaredField("isProxyActive");
            if (localField != null) {
              localField.setAccessible(true);
            }
          }
          catch (NoSuchFieldException localNoSuchFieldException)
          {
            if (!$assertionsDisabled) {
              throw new AssertionError();
            }
          }
          return localField;
        }
      });
    }
    try
    {
      return proxyActive.getBoolean(paramKeyEvent);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return false;
  }
  
  static boolean isProxyActive(KeyEvent paramKeyEvent)
  {
    if (!GraphicsEnvironment.isHeadless()) {
      return isProxyActiveImpl(paramKeyEvent);
    }
    return false;
  }
  
  private static HeavyweightFocusRequest getLastHWRequest()
  {
    synchronized (heavyweightRequests)
    {
      return heavyweightRequests.size() > 0 ? (HeavyweightFocusRequest)heavyweightRequests.getLast() : null;
    }
  }
  
  private static HeavyweightFocusRequest getFirstHWRequest()
  {
    synchronized (heavyweightRequests)
    {
      return heavyweightRequests.size() > 0 ? (HeavyweightFocusRequest)heavyweightRequests.getFirst() : null;
    }
  }
  
  private static void checkReplaceKFMPermission()
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      if (replaceKeyboardFocusManagerPermission == null) {
        replaceKeyboardFocusManagerPermission = new AWTPermission("replaceKeyboardFocusManager");
      }
      localSecurityManager.checkPermission(replaceKeyboardFocusManagerPermission);
    }
  }
  
  private void checkKFMSecurity()
    throws SecurityException
  {
    if (this != getCurrentKeyboardFocusManager()) {
      checkReplaceKFMPermission();
    }
  }
  
  static
  {
    focusLog = PlatformLogger.getLogger("java.awt.focus.KeyboardFocusManager");
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setKeyboardFocusManagerAccessor(new AWTAccessor.KeyboardFocusManagerAccessor()
    {
      public int shouldNativelyFocusHeavyweight(Component paramAnonymousComponent1, Component paramAnonymousComponent2, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, long paramAnonymousLong, CausedFocusEvent.Cause paramAnonymousCause)
      {
        return KeyboardFocusManager.shouldNativelyFocusHeavyweight(paramAnonymousComponent1, paramAnonymousComponent2, paramAnonymousBoolean1, paramAnonymousBoolean2, paramAnonymousLong, paramAnonymousCause);
      }
      
      public boolean processSynchronousLightweightTransfer(Component paramAnonymousComponent1, Component paramAnonymousComponent2, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2, long paramAnonymousLong)
      {
        return KeyboardFocusManager.processSynchronousLightweightTransfer(paramAnonymousComponent1, paramAnonymousComponent2, paramAnonymousBoolean1, paramAnonymousBoolean2, paramAnonymousLong);
      }
      
      public void removeLastFocusRequest(Component paramAnonymousComponent)
      {
        KeyboardFocusManager.removeLastFocusRequest(paramAnonymousComponent);
      }
      
      public void setMostRecentFocusOwner(Window paramAnonymousWindow, Component paramAnonymousComponent)
      {
        KeyboardFocusManager.setMostRecentFocusOwner(paramAnonymousWindow, paramAnonymousComponent);
      }
      
      public KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext paramAnonymousAppContext)
      {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager(paramAnonymousAppContext);
      }
      
      public Container getCurrentFocusCycleRoot()
      {
        return KeyboardFocusManager.currentFocusCycleRoot;
      }
    });
  }
  
  private static final class HeavyweightFocusRequest
  {
    final Component heavyweight;
    final LinkedList<KeyboardFocusManager.LightweightFocusRequest> lightweightRequests;
    static final HeavyweightFocusRequest CLEAR_GLOBAL_FOCUS_OWNER = new HeavyweightFocusRequest();
    
    private HeavyweightFocusRequest()
    {
      this.heavyweight = null;
      this.lightweightRequests = null;
    }
    
    HeavyweightFocusRequest(Component paramComponent1, Component paramComponent2, boolean paramBoolean, CausedFocusEvent.Cause paramCause)
    {
      if ((KeyboardFocusManager.log.isLoggable(PlatformLogger.Level.FINE)) && (paramComponent1 == null)) {
        KeyboardFocusManager.log.fine("Assertion (heavyweight != null) failed");
      }
      this.heavyweight = paramComponent1;
      this.lightweightRequests = new LinkedList();
      addLightweightRequest(paramComponent2, paramBoolean, paramCause);
    }
    
    boolean addLightweightRequest(Component paramComponent, boolean paramBoolean, CausedFocusEvent.Cause paramCause)
    {
      if (KeyboardFocusManager.log.isLoggable(PlatformLogger.Level.FINE))
      {
        if (this == CLEAR_GLOBAL_FOCUS_OWNER) {
          KeyboardFocusManager.log.fine("Assertion (this != HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) failed");
        }
        if (paramComponent == null) {
          KeyboardFocusManager.log.fine("Assertion (descendant != null) failed");
        }
      }
      Object localObject = this.lightweightRequests.size() > 0 ? ((KeyboardFocusManager.LightweightFocusRequest)this.lightweightRequests.getLast()).component : null;
      if (paramComponent != localObject)
      {
        this.lightweightRequests.add(new KeyboardFocusManager.LightweightFocusRequest(paramComponent, paramBoolean, paramCause));
        return true;
      }
      return false;
    }
    
    KeyboardFocusManager.LightweightFocusRequest getFirstLightweightRequest()
    {
      if (this == CLEAR_GLOBAL_FOCUS_OWNER) {
        return null;
      }
      return (KeyboardFocusManager.LightweightFocusRequest)this.lightweightRequests.getFirst();
    }
    
    public String toString()
    {
      int i = 1;
      String str = "HeavyweightFocusRequest[heavweight=" + this.heavyweight + ",lightweightRequests=";
      if (this.lightweightRequests == null)
      {
        str = str + null;
      }
      else
      {
        str = str + "[";
        Iterator localIterator = this.lightweightRequests.iterator();
        while (localIterator.hasNext())
        {
          KeyboardFocusManager.LightweightFocusRequest localLightweightFocusRequest = (KeyboardFocusManager.LightweightFocusRequest)localIterator.next();
          if (i != 0) {
            i = 0;
          } else {
            str = str + ",";
          }
          str = str + localLightweightFocusRequest;
        }
        str = str + "]";
      }
      str = str + "]";
      return str;
    }
  }
  
  private static final class LightweightFocusRequest
  {
    final Component component;
    final boolean temporary;
    final CausedFocusEvent.Cause cause;
    
    LightweightFocusRequest(Component paramComponent, boolean paramBoolean, CausedFocusEvent.Cause paramCause)
    {
      this.component = paramComponent;
      this.temporary = paramBoolean;
      this.cause = paramCause;
    }
    
    public String toString()
    {
      return "LightweightFocusRequest[component=" + this.component + ",temporary=" + this.temporary + ", cause=" + this.cause + "]";
    }
  }
}
