package java.awt;

import java.awt.event.ComponentEvent;
import java.awt.event.InvocationEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.DialogPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.lang.ref.WeakReference;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.util.IdentityArrayList;
import sun.awt.util.IdentityLinkedList;
import sun.security.util.SecurityConstants.AWT;

public class Dialog
  extends Window
{
  boolean resizable = true;
  boolean undecorated = false;
  private transient boolean initialized = false;
  public static final ModalityType DEFAULT_MODALITY_TYPE = ModalityType.APPLICATION_MODAL;
  boolean modal;
  ModalityType modalityType;
  static transient IdentityArrayList<Dialog> modalDialogs = new IdentityArrayList();
  transient IdentityArrayList<Window> blockedWindows = new IdentityArrayList();
  String title;
  private transient ModalEventFilter modalFilter;
  private volatile transient SecondaryLoop secondaryLoop;
  volatile transient boolean isInHide = false;
  volatile transient boolean isInDispose = false;
  private static final String base = "dialog";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 5920926903803293709L;
  
  public Dialog(Frame paramFrame)
  {
    this(paramFrame, "", false);
  }
  
  public Dialog(Frame paramFrame, boolean paramBoolean)
  {
    this(paramFrame, "", paramBoolean);
  }
  
  public Dialog(Frame paramFrame, String paramString)
  {
    this(paramFrame, paramString, false);
  }
  
  public Dialog(Frame paramFrame, String paramString, boolean paramBoolean)
  {
    this(paramFrame, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
  }
  
  public Dialog(Frame paramFrame, String paramString, boolean paramBoolean, GraphicsConfiguration paramGraphicsConfiguration)
  {
    this(paramFrame, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS, paramGraphicsConfiguration);
  }
  
  public Dialog(Dialog paramDialog)
  {
    this(paramDialog, "", false);
  }
  
  public Dialog(Dialog paramDialog, String paramString)
  {
    this(paramDialog, paramString, false);
  }
  
  public Dialog(Dialog paramDialog, String paramString, boolean paramBoolean)
  {
    this(paramDialog, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
  }
  
  public Dialog(Dialog paramDialog, String paramString, boolean paramBoolean, GraphicsConfiguration paramGraphicsConfiguration)
  {
    this(paramDialog, paramString, paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS, paramGraphicsConfiguration);
  }
  
  public Dialog(Window paramWindow)
  {
    this(paramWindow, "", ModalityType.MODELESS);
  }
  
  public Dialog(Window paramWindow, String paramString)
  {
    this(paramWindow, paramString, ModalityType.MODELESS);
  }
  
  public Dialog(Window paramWindow, ModalityType paramModalityType)
  {
    this(paramWindow, "", paramModalityType);
  }
  
  public Dialog(Window paramWindow, String paramString, ModalityType paramModalityType)
  {
    super(paramWindow);
    if ((paramWindow != null) && (!(paramWindow instanceof Frame)) && (!(paramWindow instanceof Dialog))) {
      throw new IllegalArgumentException("Wrong parent window");
    }
    this.title = paramString;
    setModalityType(paramModalityType);
    SunToolkit.checkAndSetPolicy(this);
    this.initialized = true;
  }
  
  public Dialog(Window paramWindow, String paramString, ModalityType paramModalityType, GraphicsConfiguration paramGraphicsConfiguration)
  {
    super(paramWindow, paramGraphicsConfiguration);
    if ((paramWindow != null) && (!(paramWindow instanceof Frame)) && (!(paramWindow instanceof Dialog))) {
      throw new IllegalArgumentException("wrong owner window");
    }
    this.title = paramString;
    setModalityType(paramModalityType);
    SunToolkit.checkAndSetPolicy(this);
    this.initialized = true;
  }
  
  String constructComponentName()
  {
    synchronized (Dialog.class)
    {
      return "dialog" + nameCounter++;
    }
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if ((this.parent != null) && (this.parent.getPeer() == null)) {
        this.parent.addNotify();
      }
      if (this.peer == null) {
        this.peer = getToolkit().createDialog(this);
      }
      super.addNotify();
    }
  }
  
  public boolean isModal()
  {
    return isModal_NoClientCode();
  }
  
  final boolean isModal_NoClientCode()
  {
    return this.modalityType != ModalityType.MODELESS;
  }
  
  public void setModal(boolean paramBoolean)
  {
    this.modal = paramBoolean;
    setModalityType(paramBoolean ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
  }
  
  public ModalityType getModalityType()
  {
    return this.modalityType;
  }
  
  public void setModalityType(ModalityType paramModalityType)
  {
    if (paramModalityType == null) {
      paramModalityType = ModalityType.MODELESS;
    }
    if (!Toolkit.getDefaultToolkit().isModalityTypeSupported(paramModalityType)) {
      paramModalityType = ModalityType.MODELESS;
    }
    if (this.modalityType == paramModalityType) {
      return;
    }
    checkModalityPermission(paramModalityType);
    this.modalityType = paramModalityType;
    this.modal = (this.modalityType != ModalityType.MODELESS);
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public void setTitle(String paramString)
  {
    String str = this.title;
    synchronized (this)
    {
      this.title = paramString;
      DialogPeer localDialogPeer = (DialogPeer)this.peer;
      if (localDialogPeer != null) {
        localDialogPeer.setTitle(paramString);
      }
    }
    firePropertyChange("title", str, paramString);
  }
  
  private boolean conditionalShow(Component paramComponent, AtomicLong paramAtomicLong)
  {
    closeSplashScreen();
    boolean bool;
    synchronized (getTreeLock())
    {
      if (this.peer == null) {
        addNotify();
      }
      validateUnconditionally();
      if (this.visible)
      {
        toFront();
        bool = false;
      }
      else
      {
        this.visible = (bool = 1);
        if (!isModal())
        {
          checkShouldBeBlocked(this);
        }
        else
        {
          modalDialogs.add(this);
          modalShow();
        }
        if ((paramComponent != null) && (paramAtomicLong != null) && (isFocusable()) && (isEnabled()) && (!isModalBlocked()))
        {
          paramAtomicLong.set(Toolkit.getEventQueue().getMostRecentKeyEventTime());
          KeyboardFocusManager.getCurrentKeyboardFocusManager().enqueueKeyEvents(paramAtomicLong.get(), paramComponent);
        }
        mixOnShowing();
        this.peer.setVisible(true);
        if (isModalBlocked()) {
          this.modalBlocker.toFront();
        }
        setLocationByPlatform(false);
        for (int i = 0; i < this.ownedWindowList.size(); i++)
        {
          Window localWindow = (Window)((WeakReference)this.ownedWindowList.elementAt(i)).get();
          if ((localWindow != null) && (localWindow.showWithParent))
          {
            localWindow.show();
            localWindow.showWithParent = false;
          }
        }
        Window.updateChildFocusableWindowState(this);
        createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
        if ((this.componentListener != null) || ((this.eventMask & 1L) != 0L) || (Toolkit.enabledOnToolkit(1L)))
        {
          ComponentEvent localComponentEvent = new ComponentEvent(this, 102);
          Toolkit.getEventQueue().postEvent(localComponentEvent);
        }
      }
    }
    if ((bool) && ((this.state & 0x1) == 0))
    {
      postWindowEvent(200);
      this.state |= 0x1;
    }
    return bool;
  }
  
  public void setVisible(boolean paramBoolean)
  {
    super.setVisible(paramBoolean);
  }
  
  @Deprecated
  public void show()
  {
    if (!this.initialized) {
      throw new IllegalStateException("The dialog component has not been initialized properly");
    }
    this.beforeFirstShow = false;
    if (!isModal())
    {
      conditionalShow(null, null);
    }
    else
    {
      AppContext localAppContext1 = AppContext.getAppContext();
      AtomicLong localAtomicLong = new AtomicLong();
      Component localComponent = null;
      try
      {
        localComponent = getMostRecentFocusOwner();
        if (conditionalShow(localComponent, localAtomicLong))
        {
          this.modalFilter = ModalEventFilter.createFilterForDialog(this);
          Conditional local1 = new Conditional()
          {
            public boolean evaluate()
            {
              return Dialog.this.windowClosingException == null;
            }
          };
          Object localObject1;
          AppContext localAppContext2;
          EventQueue localEventQueue;
          Object localObject2;
          if (this.modalityType == ModalityType.TOOLKIT_MODAL)
          {
            localObject1 = AppContext.getAppContexts().iterator();
            while (((Iterator)localObject1).hasNext())
            {
              localAppContext2 = (AppContext)((Iterator)localObject1).next();
              if (localAppContext2 != localAppContext1)
              {
                localEventQueue = (EventQueue)localAppContext2.get(AppContext.EVENT_QUEUE_KEY);
                localObject2 = new Runnable()
                {
                  public void run() {}
                };
                localEventQueue.postEvent(new InvocationEvent(this, (Runnable)localObject2));
                EventDispatchThread localEventDispatchThread = localEventQueue.getDispatchThread();
                localEventDispatchThread.addEventFilter(this.modalFilter);
              }
            }
          }
          modalityPushed();
          try
          {
            localObject1 = (EventQueue)AccessController.doPrivileged(new PrivilegedAction()
            {
              public EventQueue run()
              {
                return Toolkit.getDefaultToolkit().getSystemEventQueue();
              }
            });
            this.secondaryLoop = ((EventQueue)localObject1).createSecondaryLoop(local1, this.modalFilter, 0L);
            if (!this.secondaryLoop.enter()) {
              this.secondaryLoop = null;
            }
          }
          finally
          {
            modalityPopped();
          }
          if (this.modalityType == ModalityType.TOOLKIT_MODAL)
          {
            localObject1 = AppContext.getAppContexts().iterator();
            while (((Iterator)localObject1).hasNext())
            {
              localAppContext2 = (AppContext)((Iterator)localObject1).next();
              if (localAppContext2 != localAppContext1)
              {
                localEventQueue = (EventQueue)localAppContext2.get(AppContext.EVENT_QUEUE_KEY);
                localObject2 = localEventQueue.getDispatchThread();
                ((EventDispatchThread)localObject2).removeEventFilter(this.modalFilter);
              }
            }
          }
          if (this.windowClosingException != null)
          {
            this.windowClosingException.fillInStackTrace();
            throw this.windowClosingException;
          }
        }
      }
      finally
      {
        if (localComponent != null) {
          KeyboardFocusManager.getCurrentKeyboardFocusManager().dequeueKeyEvents(localAtomicLong.get(), localComponent);
        }
      }
    }
  }
  
  final void modalityPushed()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((localToolkit instanceof SunToolkit))
    {
      SunToolkit localSunToolkit = (SunToolkit)localToolkit;
      localSunToolkit.notifyModalityPushed(this);
    }
  }
  
  final void modalityPopped()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((localToolkit instanceof SunToolkit))
    {
      SunToolkit localSunToolkit = (SunToolkit)localToolkit;
      localSunToolkit.notifyModalityPopped(this);
    }
  }
  
  void interruptBlocking()
  {
    if (isModal())
    {
      disposeImpl();
    }
    else if (this.windowClosingException != null)
    {
      this.windowClosingException.fillInStackTrace();
      this.windowClosingException.printStackTrace();
      this.windowClosingException = null;
    }
  }
  
  private void hideAndDisposePreHandler()
  {
    this.isInHide = true;
    synchronized (getTreeLock())
    {
      if (this.secondaryLoop != null)
      {
        modalHide();
        if (this.modalFilter != null) {
          this.modalFilter.disable();
        }
        modalDialogs.remove(this);
      }
    }
  }
  
  private void hideAndDisposeHandler()
  {
    if (this.secondaryLoop != null)
    {
      this.secondaryLoop.exit();
      this.secondaryLoop = null;
    }
    this.isInHide = false;
  }
  
  @Deprecated
  public void hide()
  {
    hideAndDisposePreHandler();
    super.hide();
    if (!this.isInDispose) {
      hideAndDisposeHandler();
    }
  }
  
  void doDispose()
  {
    this.isInDispose = true;
    super.doDispose();
    hideAndDisposeHandler();
    this.isInDispose = false;
  }
  
  public void toBack()
  {
    super.toBack();
    if (this.visible) {
      synchronized (getTreeLock())
      {
        Iterator localIterator = this.blockedWindows.iterator();
        while (localIterator.hasNext())
        {
          Window localWindow = (Window)localIterator.next();
          localWindow.toBack_NoClientCode();
        }
      }
    }
  }
  
  public boolean isResizable()
  {
    return this.resizable;
  }
  
  public void setResizable(boolean paramBoolean)
  {
    int i = 0;
    synchronized (this)
    {
      this.resizable = paramBoolean;
      DialogPeer localDialogPeer = (DialogPeer)this.peer;
      if (localDialogPeer != null)
      {
        localDialogPeer.setResizable(paramBoolean);
        i = 1;
      }
    }
    if (i != 0) {
      invalidateIfValid();
    }
  }
  
  public void setUndecorated(boolean paramBoolean)
  {
    synchronized (getTreeLock())
    {
      if (isDisplayable()) {
        throw new IllegalComponentStateException("The dialog is displayable.");
      }
      if (!paramBoolean)
      {
        if (getOpacity() < 1.0F) {
          throw new IllegalComponentStateException("The dialog is not opaque");
        }
        if (getShape() != null) {
          throw new IllegalComponentStateException("The dialog does not have a default shape");
        }
        Color localColor = getBackground();
        if ((localColor != null) && (localColor.getAlpha() < 255)) {
          throw new IllegalComponentStateException("The dialog background color is not opaque");
        }
      }
      this.undecorated = paramBoolean;
    }
  }
  
  public boolean isUndecorated()
  {
    return this.undecorated;
  }
  
  public void setOpacity(float paramFloat)
  {
    synchronized (getTreeLock())
    {
      if ((paramFloat < 1.0F) && (!isUndecorated())) {
        throw new IllegalComponentStateException("The dialog is decorated");
      }
      super.setOpacity(paramFloat);
    }
  }
  
  public void setShape(Shape paramShape)
  {
    synchronized (getTreeLock())
    {
      if ((paramShape != null) && (!isUndecorated())) {
        throw new IllegalComponentStateException("The dialog is decorated");
      }
      super.setShape(paramShape);
    }
  }
  
  public void setBackground(Color paramColor)
  {
    synchronized (getTreeLock())
    {
      if ((paramColor != null) && (paramColor.getAlpha() < 255) && (!isUndecorated())) {
        throw new IllegalComponentStateException("The dialog is decorated");
      }
      super.setBackground(paramColor);
    }
  }
  
  protected String paramString()
  {
    String str = super.paramString() + "," + this.modalityType;
    if (this.title != null) {
      str = str + ",title=" + this.title;
    }
    return str;
  }
  
  private static native void initIDs();
  
  void modalShow()
  {
    IdentityArrayList localIdentityArrayList1 = new IdentityArrayList();
    Iterator localIterator = modalDialogs.iterator();
    Dialog localDialog1;
    while (localIterator.hasNext())
    {
      localDialog1 = (Dialog)localIterator.next();
      if (localDialog1.shouldBlock(this))
      {
        for (localObject1 = localDialog1; (localObject1 != null) && (localObject1 != this); localObject1 = ((Window)localObject1).getOwner_NoClientCode()) {}
        if ((localObject1 == this) || (!shouldBlock(localDialog1)) || (this.modalityType.compareTo(localDialog1.getModalityType()) < 0)) {
          localIdentityArrayList1.add(localDialog1);
        }
      }
    }
    for (int i = 0; i < localIdentityArrayList1.size(); i++)
    {
      localDialog1 = (Dialog)localIdentityArrayList1.get(i);
      if (localDialog1.isModalBlocked())
      {
        localObject1 = localDialog1.getModalBlocker();
        if (!localIdentityArrayList1.contains(localObject1)) {
          localIdentityArrayList1.add(i + 1, localObject1);
        }
      }
    }
    if (localIdentityArrayList1.size() > 0) {
      ((Dialog)localIdentityArrayList1.get(0)).blockWindow(this);
    }
    IdentityArrayList localIdentityArrayList2 = new IdentityArrayList(localIdentityArrayList1);
    for (int j = 0; j < localIdentityArrayList2.size(); j++)
    {
      localObject1 = (Window)localIdentityArrayList2.get(j);
      localObject2 = ((Window)localObject1).getOwnedWindows_NoClientCode();
      for (Object localObject4 : localObject2) {
        localIdentityArrayList2.add(localObject4);
      }
    }
    Object localObject1 = new IdentityLinkedList();
    Object localObject2 = Window.getAllUnblockedWindows();
    ??? = ((IdentityArrayList)localObject2).iterator();
    while (((Iterator)???).hasNext())
    {
      Window localWindow = (Window)((Iterator)???).next();
      if ((shouldBlock(localWindow)) && (!localIdentityArrayList2.contains(localWindow))) {
        if (((localWindow instanceof Dialog)) && (((Dialog)localWindow).isModal_NoClientCode()))
        {
          Dialog localDialog2 = (Dialog)localWindow;
          if ((localDialog2.shouldBlock(this)) && (modalDialogs.indexOf(localDialog2) > modalDialogs.indexOf(this))) {}
        }
        else
        {
          ((List)localObject1).add(localWindow);
        }
      }
    }
    blockWindows((List)localObject1);
    if (!isModalBlocked()) {
      updateChildrenBlocking();
    }
  }
  
  void modalHide()
  {
    IdentityArrayList localIdentityArrayList = new IdentityArrayList();
    int i = this.blockedWindows.size();
    Window localWindow;
    for (int j = 0; j < i; j++)
    {
      localWindow = (Window)this.blockedWindows.get(0);
      localIdentityArrayList.add(localWindow);
      unblockWindow(localWindow);
    }
    for (j = 0; j < i; j++)
    {
      localWindow = (Window)localIdentityArrayList.get(j);
      if (((localWindow instanceof Dialog)) && (((Dialog)localWindow).isModal_NoClientCode()))
      {
        Dialog localDialog = (Dialog)localWindow;
        localDialog.modalShow();
      }
      else
      {
        checkShouldBeBlocked(localWindow);
      }
    }
  }
  
  boolean shouldBlock(Window paramWindow)
  {
    if ((!isVisible_NoClientCode()) || ((!paramWindow.isVisible_NoClientCode()) && (!paramWindow.isInShow)) || (this.isInHide) || (paramWindow == this) || (!isModal_NoClientCode())) {
      return false;
    }
    if (((paramWindow instanceof Dialog)) && (((Dialog)paramWindow).isInHide)) {
      return false;
    }
    Object localObject;
    for (Dialog localDialog = this; localDialog != null; localDialog = localDialog.getModalBlocker())
    {
      for (localObject = paramWindow; (localObject != null) && (localObject != localDialog); localObject = ((Component)localObject).getParent_NoClientCode()) {}
      if (localObject == localDialog) {
        return false;
      }
    }
    switch (4.$SwitchMap$java$awt$Dialog$ModalityType[this.modalityType.ordinal()])
    {
    case 1: 
      return false;
    case 2: 
      if (paramWindow.isModalExcluded(ModalExclusionType.APPLICATION_EXCLUDE))
      {
        for (localObject = this; (localObject != null) && (localObject != paramWindow); localObject = ((Component)localObject).getParent_NoClientCode()) {}
        return localObject == paramWindow;
      }
      return getDocumentRoot() == paramWindow.getDocumentRoot();
    case 3: 
      return (!paramWindow.isModalExcluded(ModalExclusionType.APPLICATION_EXCLUDE)) && (this.appContext == paramWindow.appContext);
    case 4: 
      return !paramWindow.isModalExcluded(ModalExclusionType.TOOLKIT_EXCLUDE);
    }
    return false;
  }
  
  void blockWindow(Window paramWindow)
  {
    if (!paramWindow.isModalBlocked())
    {
      paramWindow.setModalBlocked(this, true, true);
      this.blockedWindows.add(paramWindow);
    }
  }
  
  void blockWindows(List<Window> paramList)
  {
    DialogPeer localDialogPeer = (DialogPeer)this.peer;
    if (localDialogPeer == null) {
      return;
    }
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Window localWindow = (Window)localIterator.next();
      if (!localWindow.isModalBlocked()) {
        localWindow.setModalBlocked(this, true, false);
      } else {
        localIterator.remove();
      }
    }
    localDialogPeer.blockWindows(paramList);
    this.blockedWindows.addAll(paramList);
  }
  
  void unblockWindow(Window paramWindow)
  {
    if ((paramWindow.isModalBlocked()) && (this.blockedWindows.contains(paramWindow)))
    {
      this.blockedWindows.remove(paramWindow);
      paramWindow.setModalBlocked(this, false, true);
    }
  }
  
  static void checkShouldBeBlocked(Window paramWindow)
  {
    synchronized (paramWindow.getTreeLock())
    {
      for (int i = 0; i < modalDialogs.size(); i++)
      {
        Dialog localDialog = (Dialog)modalDialogs.get(i);
        if (localDialog.shouldBlock(paramWindow))
        {
          localDialog.blockWindow(paramWindow);
          break;
        }
      }
    }
  }
  
  private void checkModalityPermission(ModalityType paramModalityType)
  {
    if (paramModalityType == ModalityType.TOOLKIT_MODAL)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkPermission(SecurityConstants.AWT.TOOLKIT_MODALITY_PERMISSION);
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    ModalityType localModalityType = (ModalityType)localGetField.get("modalityType", null);
    try
    {
      checkModalityPermission(localModalityType);
    }
    catch (AccessControlException localAccessControlException)
    {
      localModalityType = DEFAULT_MODALITY_TYPE;
    }
    if (localModalityType == null)
    {
      this.modal = localGetField.get("modal", false);
      setModal(this.modal);
    }
    else
    {
      this.modalityType = localModalityType;
    }
    this.resizable = localGetField.get("resizable", true);
    this.undecorated = localGetField.get("undecorated", false);
    this.title = ((String)localGetField.get("title", ""));
    this.blockedWindows = new IdentityArrayList();
    SunToolkit.checkAndSetPolicy(this);
    this.initialized = true;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleAWTDialog();
    }
    return this.accessibleContext;
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
  
  protected class AccessibleAWTDialog
    extends Window.AccessibleAWTWindow
  {
    private static final long serialVersionUID = 4837230331833941201L;
    
    protected AccessibleAWTDialog()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.DIALOG;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (Dialog.this.getFocusOwner() != null) {
        localAccessibleStateSet.add(AccessibleState.ACTIVE);
      }
      if (Dialog.this.isModal()) {
        localAccessibleStateSet.add(AccessibleState.MODAL);
      }
      if (Dialog.this.isResizable()) {
        localAccessibleStateSet.add(AccessibleState.RESIZABLE);
      }
      return localAccessibleStateSet;
    }
  }
  
  public static enum ModalExclusionType
  {
    NO_EXCLUDE,  APPLICATION_EXCLUDE,  TOOLKIT_EXCLUDE;
    
    private ModalExclusionType() {}
  }
  
  public static enum ModalityType
  {
    MODELESS,  DOCUMENT_MODAL,  APPLICATION_MODAL,  TOOLKIT_MODAL;
    
    private ModalityType() {}
  }
}
