package javax.swing;

import com.sun.java.swing.SwingUtilities3;
import java.applet.Applet;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InvocationEvent;
import java.awt.image.VolatileImage;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.awt.AWTAccessor.WindowAccessor;
import sun.awt.AppContext;
import sun.awt.DisplayChangedListener;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphicsEnvironment;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingAccessor;
import sun.swing.SwingAccessor.RepaintManagerAccessor;
import sun.swing.SwingUtilities2.RepaintListener;

public class RepaintManager
{
  static final boolean HANDLE_TOP_LEVEL_PAINT;
  private static final short BUFFER_STRATEGY_NOT_SPECIFIED = 0;
  private static final short BUFFER_STRATEGY_SPECIFIED_ON = 1;
  private static final short BUFFER_STRATEGY_SPECIFIED_OFF = 2;
  private static final short BUFFER_STRATEGY_TYPE;
  private Map<GraphicsConfiguration, VolatileImage> volatileMap = new HashMap(1);
  private Map<Container, Rectangle> hwDirtyComponents;
  private Map<Component, Rectangle> dirtyComponents;
  private Map<Component, Rectangle> tmpDirtyComponents;
  private List<Component> invalidComponents;
  private List<Runnable> runnableList;
  boolean doubleBufferingEnabled = true;
  private Dimension doubleBufferMaxSize;
  DoubleBufferInfo standardDoubleBuffer;
  private PaintManager paintManager;
  private static final Object repaintManagerKey = RepaintManager.class;
  static boolean volatileImageBufferEnabled = true;
  private static final int volatileBufferType;
  private static boolean nativeDoubleBuffering;
  private static final int VOLATILE_LOOP_MAX = 2;
  private int paintDepth = 0;
  private short bufferStrategyType;
  private boolean painting;
  private JComponent repaintRoot;
  private Thread paintThread;
  private final ProcessingRunnable processingRunnable;
  private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
  private static final DisplayChangedListener displayChangedHandler = new DisplayChangedHandler();
  Rectangle tmp = new Rectangle();
  private List<SwingUtilities2.RepaintListener> repaintListeners = new ArrayList(1);
  
  public static RepaintManager currentManager(Component paramComponent)
  {
    return currentManager(AppContext.getAppContext());
  }
  
  static RepaintManager currentManager(AppContext paramAppContext)
  {
    RepaintManager localRepaintManager = (RepaintManager)paramAppContext.get(repaintManagerKey);
    if (localRepaintManager == null)
    {
      localRepaintManager = new RepaintManager(BUFFER_STRATEGY_TYPE);
      paramAppContext.put(repaintManagerKey, localRepaintManager);
    }
    return localRepaintManager;
  }
  
  public static RepaintManager currentManager(JComponent paramJComponent)
  {
    return currentManager(paramJComponent);
  }
  
  public static void setCurrentManager(RepaintManager paramRepaintManager)
  {
    if (paramRepaintManager != null) {
      SwingUtilities.appContextPut(repaintManagerKey, paramRepaintManager);
    } else {
      SwingUtilities.appContextRemove(repaintManagerKey);
    }
  }
  
  public RepaintManager()
  {
    this((short)2);
  }
  
  private RepaintManager(short paramShort)
  {
    synchronized (this)
    {
      this.dirtyComponents = new IdentityHashMap();
      this.tmpDirtyComponents = new IdentityHashMap();
      this.bufferStrategyType = paramShort;
      this.hwDirtyComponents = new IdentityHashMap();
    }
    this.processingRunnable = new ProcessingRunnable(null);
  }
  
  private void displayChanged()
  {
    clearImages();
  }
  
  public synchronized void addInvalidComponent(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null)
    {
      localRepaintManager.addInvalidComponent(paramJComponent);
      return;
    }
    Container localContainer = SwingUtilities.getValidateRoot(paramJComponent, true);
    if (localContainer == null) {
      return;
    }
    if (this.invalidComponents == null)
    {
      this.invalidComponents = new ArrayList();
    }
    else
    {
      int i = this.invalidComponents.size();
      for (int j = 0; j < i; j++) {
        if (localContainer == this.invalidComponents.get(j)) {
          return;
        }
      }
    }
    this.invalidComponents.add(localContainer);
    scheduleProcessingRunnable(SunToolkit.targetToAppContext(paramJComponent));
  }
  
  public synchronized void removeInvalidComponent(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null)
    {
      localRepaintManager.removeInvalidComponent(paramJComponent);
      return;
    }
    if (this.invalidComponents != null)
    {
      int i = this.invalidComponents.indexOf(paramJComponent);
      if (i != -1) {
        this.invalidComponents.remove(i);
      }
    }
  }
  
  private void addDirtyRegion0(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 <= 0) || (paramInt4 <= 0) || (paramContainer == null)) {
      return;
    }
    if ((paramContainer.getWidth() <= 0) || (paramContainer.getHeight() <= 0)) {
      return;
    }
    if (extendDirtyRegion(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4)) {
      return;
    }
    Object localObject1 = null;
    for (Container localContainer = paramContainer; localContainer != null; localContainer = localContainer.getParent())
    {
      if ((!localContainer.isVisible()) || (localContainer.getPeer() == null)) {
        return;
      }
      if (((localContainer instanceof Window)) || ((localContainer instanceof Applet)))
      {
        if (((localContainer instanceof Frame)) && ((((Frame)localContainer).getExtendedState() & 0x1) == 1)) {
          return;
        }
        localObject1 = localContainer;
        break;
      }
    }
    if (localObject1 == null) {
      return;
    }
    synchronized (this)
    {
      if (extendDirtyRegion(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4)) {
        return;
      }
      this.dirtyComponents.put(paramContainer, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
    }
    scheduleProcessingRunnable(SunToolkit.targetToAppContext(paramContainer));
  }
  
  public void addDirtyRegion(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null)
    {
      localRepaintManager.addDirtyRegion(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    addDirtyRegion0(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void addDirtyRegion(Window paramWindow, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    addDirtyRegion0(paramWindow, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void addDirtyRegion(Applet paramApplet, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    addDirtyRegion0(paramApplet, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  void scheduleHeavyWeightPaints()
  {
    Map localMap;
    synchronized (this)
    {
      if (this.hwDirtyComponents.size() == 0) {
        return;
      }
      localMap = this.hwDirtyComponents;
      this.hwDirtyComponents = new IdentityHashMap();
    }
    ??? = localMap.keySet().iterator();
    while (((Iterator)???).hasNext())
    {
      Container localContainer = (Container)((Iterator)???).next();
      Rectangle localRectangle = (Rectangle)localMap.get(localContainer);
      if ((localContainer instanceof Window)) {
        addDirtyRegion((Window)localContainer, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
      } else if ((localContainer instanceof Applet)) {
        addDirtyRegion((Applet)localContainer, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
      } else {
        addDirtyRegion0(localContainer, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
      }
    }
  }
  
  void nativeAddDirtyRegion(AppContext paramAppContext, Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 > 0) && (paramInt4 > 0))
    {
      synchronized (this)
      {
        Rectangle localRectangle = (Rectangle)this.hwDirtyComponents.get(paramContainer);
        if (localRectangle == null) {
          this.hwDirtyComponents.put(paramContainer, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
        } else {
          this.hwDirtyComponents.put(paramContainer, SwingUtilities.computeUnion(paramInt1, paramInt2, paramInt3, paramInt4, localRectangle));
        }
      }
      scheduleProcessingRunnable(paramAppContext);
    }
  }
  
  void nativeQueueSurfaceDataRunnable(AppContext paramAppContext, final Component paramComponent, final Runnable paramRunnable)
  {
    synchronized (this)
    {
      if (this.runnableList == null) {
        this.runnableList = new LinkedList();
      }
      this.runnableList.add(new Runnable()
      {
        public void run()
        {
          AccessControlContext localAccessControlContext1 = AccessController.getContext();
          AccessControlContext localAccessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(paramComponent);
          RepaintManager.javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
          {
            public Void run()
            {
              RepaintManager.2.this.val$r.run();
              return null;
            }
          }, localAccessControlContext1, localAccessControlContext2);
        }
      });
    }
    scheduleProcessingRunnable(paramAppContext);
  }
  
  private synchronized boolean extendDirtyRegion(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rectangle localRectangle = (Rectangle)this.dirtyComponents.get(paramComponent);
    if (localRectangle != null)
    {
      SwingUtilities.computeUnion(paramInt1, paramInt2, paramInt3, paramInt4, localRectangle);
      return true;
    }
    return false;
  }
  
  public Rectangle getDirtyRegion(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null) {
      return localRepaintManager.getDirtyRegion(paramJComponent);
    }
    Rectangle localRectangle;
    synchronized (this)
    {
      localRectangle = (Rectangle)this.dirtyComponents.get(paramJComponent);
    }
    if (localRectangle == null) {
      return new Rectangle(0, 0, 0, 0);
    }
    return new Rectangle(localRectangle);
  }
  
  public void markCompletelyDirty(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null)
    {
      localRepaintManager.markCompletelyDirty(paramJComponent);
      return;
    }
    addDirtyRegion(paramJComponent, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  public void markCompletelyClean(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null)
    {
      localRepaintManager.markCompletelyClean(paramJComponent);
      return;
    }
    synchronized (this)
    {
      this.dirtyComponents.remove(paramJComponent);
    }
  }
  
  public boolean isCompletelyDirty(JComponent paramJComponent)
  {
    RepaintManager localRepaintManager = getDelegate(paramJComponent);
    if (localRepaintManager != null) {
      return localRepaintManager.isCompletelyDirty(paramJComponent);
    }
    Rectangle localRectangle = getDirtyRegion(paramJComponent);
    return (localRectangle.width == Integer.MAX_VALUE) && (localRectangle.height == Integer.MAX_VALUE);
  }
  
  public void validateInvalidComponents()
  {
    List localList;
    synchronized (this)
    {
      if (this.invalidComponents == null) {
        return;
      }
      localList = this.invalidComponents;
      this.invalidComponents = null;
    }
    ??? = localList.size();
    for (Object localObject2 = 0; localObject2 < ???; localObject2++)
    {
      final Component localComponent = (Component)localList.get(localObject2);
      AccessControlContext localAccessControlContext1 = AccessController.getContext();
      AccessControlContext localAccessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(localComponent);
      javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
      {
        public Void run()
        {
          localComponent.validate();
          return null;
        }
      }, localAccessControlContext1, localAccessControlContext2);
    }
  }
  
  private void prePaintDirtyRegions()
  {
    Map localMap;
    List localList;
    synchronized (this)
    {
      localMap = this.dirtyComponents;
      localList = this.runnableList;
      this.runnableList = null;
    }
    if (localList != null)
    {
      ??? = localList.iterator();
      while (((Iterator)???).hasNext())
      {
        Runnable localRunnable = (Runnable)((Iterator)???).next();
        localRunnable.run();
      }
    }
    paintDirtyRegions();
    if (localMap.size() > 0) {
      paintDirtyRegions(localMap);
    }
  }
  
  private void updateWindows(Map<Component, Rectangle> paramMap)
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((!(localToolkit instanceof SunToolkit)) || (!((SunToolkit)localToolkit).needUpdateWindow())) {
      return;
    }
    HashSet localHashSet = new HashSet();
    Set localSet = paramMap.keySet();
    Iterator localIterator = localSet.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Component)localIterator.next();
      Window localWindow = (localObject instanceof Window) ? (Window)localObject : SwingUtilities.getWindowAncestor((Component)localObject);
      if ((localWindow != null) && (!localWindow.isOpaque())) {
        localHashSet.add(localWindow);
      }
    }
    localIterator = localHashSet.iterator();
    while (localIterator.hasNext())
    {
      localObject = (Window)localIterator.next();
      AWTAccessor.getWindowAccessor().updateWindow((Window)localObject);
    }
  }
  
  boolean isPainting()
  {
    return this.painting;
  }
  
  public void paintDirtyRegions()
  {
    synchronized (this)
    {
      Map localMap = this.tmpDirtyComponents;
      this.tmpDirtyComponents = this.dirtyComponents;
      this.dirtyComponents = localMap;
      this.dirtyComponents.clear();
    }
    paintDirtyRegions(this.tmpDirtyComponents);
  }
  
  private void paintDirtyRegions(final Map<Component, Rectangle> paramMap)
  {
    if (paramMap.isEmpty()) {
      return;
    }
    final ArrayList localArrayList = new ArrayList(paramMap.size());
    Object localObject1 = paramMap.keySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Component localComponent1 = (Component)((Iterator)localObject1).next();
      collectDirtyComponents(paramMap, localComponent1, localArrayList);
    }
    localObject1 = new AtomicInteger(localArrayList.size());
    this.painting = true;
    try
    {
      for (int i = 0; i < ((AtomicInteger)localObject1).get(); i++)
      {
        final int j = i;
        final Component localComponent2 = (Component)localArrayList.get(i);
        AccessControlContext localAccessControlContext1 = AccessController.getContext();
        AccessControlContext localAccessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(localComponent2);
        javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction()
        {
          public Void run()
          {
            Rectangle localRectangle = (Rectangle)paramMap.get(localComponent2);
            if (localRectangle == null) {
              return null;
            }
            int i = localComponent2.getHeight();
            int j = localComponent2.getWidth();
            SwingUtilities.computeIntersection(0, 0, j, i, localRectangle);
            if ((localComponent2 instanceof JComponent))
            {
              ((JComponent)localComponent2).paintImmediately(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
            }
            else if (localComponent2.isShowing())
            {
              Graphics localGraphics = JComponent.safelyGetGraphics(localComponent2, localComponent2);
              if (localGraphics != null)
              {
                localGraphics.setClip(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
                try
                {
                  localComponent2.paint(localGraphics);
                }
                finally
                {
                  localGraphics.dispose();
                }
              }
            }
            if (RepaintManager.this.repaintRoot != null)
            {
              RepaintManager.this.adjustRoots(RepaintManager.this.repaintRoot, localArrayList, j + 1);
              this.val$count.set(localArrayList.size());
              RepaintManager.this.paintManager.isRepaintingRoot = true;
              RepaintManager.this.repaintRoot.paintImmediately(0, 0, RepaintManager.this.repaintRoot.getWidth(), RepaintManager.this.repaintRoot.getHeight());
              RepaintManager.this.paintManager.isRepaintingRoot = false;
              RepaintManager.this.repaintRoot = null;
            }
            return null;
          }
        }, localAccessControlContext1, localAccessControlContext2);
      }
    }
    finally
    {
      this.painting = false;
    }
    updateWindows(paramMap);
    paramMap.clear();
  }
  
  private void adjustRoots(JComponent paramJComponent, List<Component> paramList, int paramInt)
  {
    for (int i = paramList.size() - 1; i >= paramInt; i--)
    {
      for (Object localObject = (Component)paramList.get(i); (localObject != paramJComponent) && (localObject != null) && ((localObject instanceof JComponent)); localObject = ((Component)localObject).getParent()) {}
      if (localObject == paramJComponent) {
        paramList.remove(i);
      }
    }
  }
  
  void collectDirtyComponents(Map<Component, Rectangle> paramMap, Component paramComponent, List<Component> paramList)
  {
    Object localObject2;
    Object localObject1 = localObject2 = paramComponent;
    int n = paramComponent.getX();
    int i1 = paramComponent.getY();
    int i2 = paramComponent.getWidth();
    int i3 = paramComponent.getHeight();
    int k;
    int i = k = 0;
    int m;
    int j = m = 0;
    this.tmp.setBounds((Rectangle)paramMap.get(paramComponent));
    SwingUtilities.computeIntersection(0, 0, i2, i3, this.tmp);
    if (this.tmp.isEmpty()) {
      return;
    }
    while ((localObject1 instanceof JComponent))
    {
      Container localContainer = ((Component)localObject1).getParent();
      if (localContainer == null) {
        break;
      }
      localObject1 = localContainer;
      i += n;
      j += i1;
      this.tmp.setLocation(this.tmp.x + n, this.tmp.y + i1);
      n = ((Component)localObject1).getX();
      i1 = ((Component)localObject1).getY();
      i2 = ((Component)localObject1).getWidth();
      i3 = ((Component)localObject1).getHeight();
      this.tmp = SwingUtilities.computeIntersection(0, 0, i2, i3, this.tmp);
      if (this.tmp.isEmpty()) {
        return;
      }
      if (paramMap.get(localObject1) != null)
      {
        localObject2 = localObject1;
        k = i;
        m = j;
      }
    }
    if (paramComponent != localObject2)
    {
      this.tmp.setLocation(this.tmp.x + k - i, this.tmp.y + m - j);
      Rectangle localRectangle = (Rectangle)paramMap.get(localObject2);
      SwingUtilities.computeUnion(this.tmp.x, this.tmp.y, this.tmp.width, this.tmp.height, localRectangle);
    }
    if (!paramList.contains(localObject2)) {
      paramList.add(localObject2);
    }
  }
  
  public synchronized String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (this.dirtyComponents != null) {
      localStringBuffer.append("" + this.dirtyComponents);
    }
    return localStringBuffer.toString();
  }
  
  public Image getOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2)
  {
    RepaintManager localRepaintManager = getDelegate(paramComponent);
    if (localRepaintManager != null) {
      return localRepaintManager.getOffscreenBuffer(paramComponent, paramInt1, paramInt2);
    }
    return _getOffscreenBuffer(paramComponent, paramInt1, paramInt2);
  }
  
  public Image getVolatileOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2)
  {
    RepaintManager localRepaintManager = getDelegate(paramComponent);
    if (localRepaintManager != null) {
      return localRepaintManager.getVolatileOffscreenBuffer(paramComponent, paramInt1, paramInt2);
    }
    Window localWindow = (paramComponent instanceof Window) ? (Window)paramComponent : SwingUtilities.getWindowAncestor(paramComponent);
    if (!localWindow.isOpaque())
    {
      localObject = Toolkit.getDefaultToolkit();
      if (((localObject instanceof SunToolkit)) && (((SunToolkit)localObject).needUpdateWindow())) {
        return null;
      }
    }
    Object localObject = paramComponent.getGraphicsConfiguration();
    if (localObject == null) {
      localObject = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    Dimension localDimension = getDoubleBufferMaximumSize();
    int i = paramInt1 > localDimension.width ? localDimension.width : paramInt1 < 1 ? 1 : paramInt1;
    int j = paramInt2 > localDimension.height ? localDimension.height : paramInt2 < 1 ? 1 : paramInt2;
    VolatileImage localVolatileImage = (VolatileImage)this.volatileMap.get(localObject);
    if ((localVolatileImage == null) || (localVolatileImage.getWidth() < i) || (localVolatileImage.getHeight() < j))
    {
      if (localVolatileImage != null) {
        localVolatileImage.flush();
      }
      localVolatileImage = ((GraphicsConfiguration)localObject).createCompatibleVolatileImage(i, j, volatileBufferType);
      this.volatileMap.put(localObject, localVolatileImage);
    }
    return localVolatileImage;
  }
  
  private Image _getOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2)
  {
    Dimension localDimension = getDoubleBufferMaximumSize();
    Window localWindow = (paramComponent instanceof Window) ? (Window)paramComponent : SwingUtilities.getWindowAncestor(paramComponent);
    if (!localWindow.isOpaque())
    {
      localObject = Toolkit.getDefaultToolkit();
      if (((localObject instanceof SunToolkit)) && (((SunToolkit)localObject).needUpdateWindow())) {
        return null;
      }
    }
    if (this.standardDoubleBuffer == null) {
      this.standardDoubleBuffer = new DoubleBufferInfo(null);
    }
    DoubleBufferInfo localDoubleBufferInfo = this.standardDoubleBuffer;
    int i = paramInt1 > localDimension.width ? localDimension.width : paramInt1 < 1 ? 1 : paramInt1;
    int j = paramInt2 > localDimension.height ? localDimension.height : paramInt2 < 1 ? 1 : paramInt2;
    if ((localDoubleBufferInfo.needsReset) || ((localDoubleBufferInfo.image != null) && ((localDoubleBufferInfo.size.width < i) || (localDoubleBufferInfo.size.height < j))))
    {
      localDoubleBufferInfo.needsReset = false;
      if (localDoubleBufferInfo.image != null)
      {
        localDoubleBufferInfo.image.flush();
        localDoubleBufferInfo.image = null;
      }
      i = Math.max(localDoubleBufferInfo.size.width, i);
      j = Math.max(localDoubleBufferInfo.size.height, j);
    }
    Object localObject = localDoubleBufferInfo.image;
    if (localDoubleBufferInfo.image == null)
    {
      localObject = paramComponent.createImage(i, j);
      localDoubleBufferInfo.size = new Dimension(i, j);
      if ((paramComponent instanceof JComponent))
      {
        ((JComponent)paramComponent).setCreatedDoubleBuffer(true);
        localDoubleBufferInfo.image = ((Image)localObject);
      }
    }
    return localObject;
  }
  
  public void setDoubleBufferMaximumSize(Dimension paramDimension)
  {
    this.doubleBufferMaxSize = paramDimension;
    if (this.doubleBufferMaxSize == null) {
      clearImages();
    } else {
      clearImages(paramDimension.width, paramDimension.height);
    }
  }
  
  private void clearImages()
  {
    clearImages(0, 0);
  }
  
  private void clearImages(int paramInt1, int paramInt2)
  {
    if ((this.standardDoubleBuffer != null) && (this.standardDoubleBuffer.image != null) && ((this.standardDoubleBuffer.image.getWidth(null) > paramInt1) || (this.standardDoubleBuffer.image.getHeight(null) > paramInt2)))
    {
      this.standardDoubleBuffer.image.flush();
      this.standardDoubleBuffer.image = null;
    }
    Iterator localIterator = this.volatileMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      GraphicsConfiguration localGraphicsConfiguration = (GraphicsConfiguration)localIterator.next();
      VolatileImage localVolatileImage = (VolatileImage)this.volatileMap.get(localGraphicsConfiguration);
      if ((localVolatileImage.getWidth() > paramInt1) || (localVolatileImage.getHeight() > paramInt2))
      {
        localVolatileImage.flush();
        localIterator.remove();
      }
    }
  }
  
  public Dimension getDoubleBufferMaximumSize()
  {
    if (this.doubleBufferMaxSize == null) {
      try
      {
        Rectangle localRectangle = new Rectangle();
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice localGraphicsDevice : localGraphicsEnvironment.getScreenDevices())
        {
          GraphicsConfiguration localGraphicsConfiguration = localGraphicsDevice.getDefaultConfiguration();
          localRectangle = localRectangle.union(localGraphicsConfiguration.getBounds());
        }
        this.doubleBufferMaxSize = new Dimension(localRectangle.width, localRectangle.height);
      }
      catch (HeadlessException localHeadlessException)
      {
        this.doubleBufferMaxSize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
      }
    }
    return this.doubleBufferMaxSize;
  }
  
  public void setDoubleBufferingEnabled(boolean paramBoolean)
  {
    this.doubleBufferingEnabled = paramBoolean;
    PaintManager localPaintManager = getPaintManager();
    if ((!paramBoolean) && (localPaintManager.getClass() != PaintManager.class)) {
      setPaintManager(new PaintManager());
    }
  }
  
  public boolean isDoubleBufferingEnabled()
  {
    return this.doubleBufferingEnabled;
  }
  
  void resetDoubleBuffer()
  {
    if (this.standardDoubleBuffer != null) {
      this.standardDoubleBuffer.needsReset = true;
    }
  }
  
  void resetVolatileDoubleBuffer(GraphicsConfiguration paramGraphicsConfiguration)
  {
    Image localImage = (Image)this.volatileMap.remove(paramGraphicsConfiguration);
    if (localImage != null) {
      localImage.flush();
    }
  }
  
  boolean useVolatileDoubleBuffer()
  {
    return volatileImageBufferEnabled;
  }
  
  private synchronized boolean isPaintingThread()
  {
    return Thread.currentThread() == this.paintThread;
  }
  
  void paint(JComponent paramJComponent1, JComponent paramJComponent2, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    PaintManager localPaintManager = getPaintManager();
    if ((!isPaintingThread()) && (localPaintManager.getClass() != PaintManager.class))
    {
      localPaintManager = new PaintManager();
      localPaintManager.repaintManager = this;
    }
    if (!localPaintManager.paint(paramJComponent1, paramJComponent2, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4))
    {
      paramGraphics.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
      paramJComponent1.paintToOffscreen(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
    }
  }
  
  void copyArea(JComponent paramJComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    getPaintManager().copyArea(paramJComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean);
  }
  
  private void addRepaintListener(SwingUtilities2.RepaintListener paramRepaintListener)
  {
    this.repaintListeners.add(paramRepaintListener);
  }
  
  private void removeRepaintListener(SwingUtilities2.RepaintListener paramRepaintListener)
  {
    this.repaintListeners.remove(paramRepaintListener);
  }
  
  void notifyRepaintPerformed(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Iterator localIterator = this.repaintListeners.iterator();
    while (localIterator.hasNext())
    {
      SwingUtilities2.RepaintListener localRepaintListener = (SwingUtilities2.RepaintListener)localIterator.next();
      localRepaintListener.repaintPerformed(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  void beginPaint()
  {
    int i = 0;
    Thread localThread = Thread.currentThread();
    int j;
    synchronized (this)
    {
      j = this.paintDepth;
      if ((this.paintThread == null) || (localThread == this.paintThread))
      {
        this.paintThread = localThread;
        this.paintDepth += 1;
      }
      else
      {
        i = 1;
      }
    }
    if ((i == 0) && (j == 0)) {
      getPaintManager().beginPaint();
    }
  }
  
  void endPaint()
  {
    if (isPaintingThread())
    {
      PaintManager localPaintManager = null;
      synchronized (this)
      {
        if (--this.paintDepth == 0) {
          localPaintManager = getPaintManager();
        }
      }
      if (localPaintManager != null)
      {
        localPaintManager.endPaint();
        synchronized (this)
        {
          this.paintThread = null;
        }
      }
    }
  }
  
  boolean show(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return getPaintManager().show(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  void doubleBufferingChanged(JRootPane paramJRootPane)
  {
    getPaintManager().doubleBufferingChanged(paramJRootPane);
  }
  
  void setPaintManager(PaintManager paramPaintManager)
  {
    if (paramPaintManager == null) {
      paramPaintManager = new PaintManager();
    }
    PaintManager localPaintManager;
    synchronized (this)
    {
      localPaintManager = this.paintManager;
      this.paintManager = paramPaintManager;
      paramPaintManager.repaintManager = this;
    }
    if (localPaintManager != null) {
      localPaintManager.dispose();
    }
  }
  
  private synchronized PaintManager getPaintManager()
  {
    if (this.paintManager == null)
    {
      BufferStrategyPaintManager localBufferStrategyPaintManager = null;
      if ((this.doubleBufferingEnabled) && (!nativeDoubleBuffering)) {
        switch (this.bufferStrategyType)
        {
        case 0: 
          Toolkit localToolkit = Toolkit.getDefaultToolkit();
          if ((localToolkit instanceof SunToolkit))
          {
            SunToolkit localSunToolkit = (SunToolkit)localToolkit;
            if (localSunToolkit.useBufferPerWindow()) {
              localBufferStrategyPaintManager = new BufferStrategyPaintManager();
            }
          }
          break;
        case 1: 
          localBufferStrategyPaintManager = new BufferStrategyPaintManager();
          break;
        }
      }
      setPaintManager(localBufferStrategyPaintManager);
    }
    return this.paintManager;
  }
  
  private void scheduleProcessingRunnable(AppContext paramAppContext)
  {
    if (this.processingRunnable.markPending())
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      if ((localToolkit instanceof SunToolkit)) {
        SunToolkit.getSystemEventQueueImplPP(paramAppContext).postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), this.processingRunnable));
      } else {
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), this.processingRunnable));
      }
    }
  }
  
  private RepaintManager getDelegate(Component paramComponent)
  {
    RepaintManager localRepaintManager = SwingUtilities3.getDelegateRepaintManager(paramComponent);
    if (this == localRepaintManager) {
      localRepaintManager = null;
    }
    return localRepaintManager;
  }
  
  static
  {
    SwingAccessor.setRepaintManagerAccessor(new SwingAccessor.RepaintManagerAccessor()
    {
      public void addRepaintListener(RepaintManager paramAnonymousRepaintManager, SwingUtilities2.RepaintListener paramAnonymousRepaintListener)
      {
        paramAnonymousRepaintManager.addRepaintListener(paramAnonymousRepaintListener);
      }
      
      public void removeRepaintListener(RepaintManager paramAnonymousRepaintManager, SwingUtilities2.RepaintListener paramAnonymousRepaintListener)
      {
        paramAnonymousRepaintManager.removeRepaintListener(paramAnonymousRepaintListener);
      }
    });
    volatileImageBufferEnabled = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.volatileImageBufferEnabled", "true")));
    boolean bool = GraphicsEnvironment.isHeadless();
    if ((volatileImageBufferEnabled) && (bool)) {
      volatileImageBufferEnabled = false;
    }
    nativeDoubleBuffering = "true".equals(AccessController.doPrivileged(new GetPropertyAction("awt.nativeDoubleBuffering")));
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("swing.bufferPerWindow"));
    if (bool) {
      BUFFER_STRATEGY_TYPE = 2;
    } else if (str == null) {
      BUFFER_STRATEGY_TYPE = 0;
    } else if ("true".equals(str)) {
      BUFFER_STRATEGY_TYPE = 1;
    } else {
      BUFFER_STRATEGY_TYPE = 2;
    }
    HANDLE_TOP_LEVEL_PAINT = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.handleTopLevelPaint", "true")));
    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if ((localGraphicsEnvironment instanceof SunGraphicsEnvironment)) {
      ((SunGraphicsEnvironment)localGraphicsEnvironment).addDisplayChangedListener(displayChangedHandler);
    }
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if (((localToolkit instanceof SunToolkit)) && (((SunToolkit)localToolkit).isSwingBackbufferTranslucencySupported())) {
      volatileBufferType = 3;
    } else {
      volatileBufferType = 1;
    }
  }
  
  private static final class DisplayChangedHandler
    implements DisplayChangedListener
  {
    DisplayChangedHandler() {}
    
    public void displayChanged() {}
    
    public void paletteChanged() {}
    
    private static void scheduleDisplayChanges()
    {
      Iterator localIterator = AppContext.getAppContexts().iterator();
      while (localIterator.hasNext())
      {
        AppContext localAppContext = (AppContext)localIterator.next();
        synchronized (localAppContext)
        {
          if (!localAppContext.isDisposed())
          {
            EventQueue localEventQueue = (EventQueue)localAppContext.get(AppContext.EVENT_QUEUE_KEY);
            if (localEventQueue != null) {
              localEventQueue.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), new RepaintManager.DisplayChangedRunnable(null)));
            }
          }
        }
      }
    }
  }
  
  private static final class DisplayChangedRunnable
    implements Runnable
  {
    private DisplayChangedRunnable() {}
    
    public void run()
    {
      RepaintManager.currentManager((JComponent)null).displayChanged();
    }
  }
  
  private class DoubleBufferInfo
  {
    public Image image;
    public Dimension size;
    public boolean needsReset = false;
    
    private DoubleBufferInfo() {}
  }
  
  static class PaintManager
  {
    protected RepaintManager repaintManager;
    boolean isRepaintingRoot;
    
    PaintManager() {}
    
    public boolean paint(JComponent paramJComponent1, JComponent paramJComponent2, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      boolean bool = false;
      Image localImage;
      if ((this.repaintManager.useVolatileDoubleBuffer()) && ((localImage = getValidImage(this.repaintManager.getVolatileOffscreenBuffer(paramJComponent2, paramInt3, paramInt4))) != null))
      {
        VolatileImage localVolatileImage = (VolatileImage)localImage;
        GraphicsConfiguration localGraphicsConfiguration = paramJComponent2.getGraphicsConfiguration();
        for (int i = 0; (!bool) && (i < 2); i++)
        {
          if (localVolatileImage.validate(localGraphicsConfiguration) == 2)
          {
            this.repaintManager.resetVolatileDoubleBuffer(localGraphicsConfiguration);
            localImage = this.repaintManager.getVolatileOffscreenBuffer(paramJComponent2, paramInt3, paramInt4);
            localVolatileImage = (VolatileImage)localImage;
          }
          paintDoubleBuffered(paramJComponent1, localVolatileImage, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
          bool = !localVolatileImage.contentsLost();
        }
      }
      if ((!bool) && ((localImage = getValidImage(this.repaintManager.getOffscreenBuffer(paramJComponent2, paramInt3, paramInt4))) != null))
      {
        paintDoubleBuffered(paramJComponent1, localImage, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
        bool = true;
      }
      return bool;
    }
    
    public void copyArea(JComponent paramJComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
    {
      paramGraphics.copyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    
    public void beginPaint() {}
    
    public void endPaint() {}
    
    public boolean show(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return false;
    }
    
    public void doubleBufferingChanged(JRootPane paramJRootPane) {}
    
    protected void paintDoubleBuffered(JComponent paramJComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Graphics localGraphics = paramImage.getGraphics();
      int i = Math.min(paramInt3, paramImage.getWidth(null));
      int j = Math.min(paramInt4, paramImage.getHeight(null));
      try
      {
        int k = paramInt1;
        int n = paramInt1 + paramInt3;
        while (k < n)
        {
          int m = paramInt2;
          int i1 = paramInt2 + paramInt4;
          while (m < i1)
          {
            localGraphics.translate(-k, -m);
            localGraphics.setClip(k, m, i, j);
            Graphics2D localGraphics2D;
            Object localObject1;
            if ((RepaintManager.volatileBufferType != 1) && ((localGraphics instanceof Graphics2D)))
            {
              localGraphics2D = (Graphics2D)localGraphics;
              localObject1 = localGraphics2D.getBackground();
              localGraphics2D.setBackground(paramJComponent.getBackground());
              localGraphics2D.clearRect(k, m, i, j);
              localGraphics2D.setBackground((Color)localObject1);
            }
            paramJComponent.paintToOffscreen(localGraphics, k, m, i, j, n, i1);
            paramGraphics.setClip(k, m, i, j);
            if ((RepaintManager.volatileBufferType != 1) && ((paramGraphics instanceof Graphics2D)))
            {
              localGraphics2D = (Graphics2D)paramGraphics;
              localObject1 = localGraphics2D.getComposite();
              localGraphics2D.setComposite(AlphaComposite.Src);
              localGraphics2D.drawImage(paramImage, k, m, paramJComponent);
              localGraphics2D.setComposite((Composite)localObject1);
            }
            else
            {
              paramGraphics.drawImage(paramImage, k, m, paramJComponent);
            }
            localGraphics.translate(k, m);
            m += j;
          }
          k += i;
        }
      }
      finally
      {
        localGraphics.dispose();
      }
    }
    
    private Image getValidImage(Image paramImage)
    {
      if ((paramImage != null) && (paramImage.getWidth(null) > 0) && (paramImage.getHeight(null) > 0)) {
        return paramImage;
      }
      return null;
    }
    
    protected void repaintRoot(JComponent paramJComponent)
    {
      assert (this.repaintManager.repaintRoot == null);
      if (this.repaintManager.painting) {
        this.repaintManager.repaintRoot = paramJComponent;
      } else {
        paramJComponent.repaint();
      }
    }
    
    protected boolean isRepaintingRoot()
    {
      return this.isRepaintingRoot;
    }
    
    protected void dispose() {}
  }
  
  private final class ProcessingRunnable
    implements Runnable
  {
    private boolean pending;
    
    private ProcessingRunnable() {}
    
    public synchronized boolean markPending()
    {
      if (!this.pending)
      {
        this.pending = true;
        return true;
      }
      return false;
    }
    
    public void run()
    {
      synchronized (this)
      {
        this.pending = false;
      }
      RepaintManager.this.scheduleHeavyWeightPaints();
      RepaintManager.this.validateInvalidComponents();
      RepaintManager.this.prePaintDirtyRegions();
    }
  }
}
