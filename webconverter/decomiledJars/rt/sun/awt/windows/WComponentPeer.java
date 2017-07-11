package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.dnd.peer.DropTargetPeer;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.PaintEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.GlobalCursorManager;
import sun.awt.PaintEventDispatcher;
import sun.awt.RepaintArea;
import sun.awt.SunToolkit;
import sun.awt.Win32GraphicsConfig;
import sun.awt.Win32GraphicsEnvironment;
import sun.awt.event.IgnorePaintEvent;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.ToolkitImage;
import sun.java2d.InvalidPipeException;
import sun.java2d.ScreenUpdateManager;
import sun.java2d.SurfaceData;
import sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData;
import sun.java2d.opengl.OGLSurfaceData;
import sun.java2d.pipe.Region;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public abstract class WComponentPeer
  extends WObjectPeer
  implements ComponentPeer, DropTargetPeer
{
  protected volatile long hwnd;
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WComponentPeer");
  private static final PlatformLogger shapeLog = PlatformLogger.getLogger("sun.awt.windows.shape.WComponentPeer");
  private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.windows.focus.WComponentPeer");
  SurfaceData surfaceData;
  private RepaintArea paintArea;
  protected Win32GraphicsConfig winGraphicsConfig;
  boolean isLayouting = false;
  boolean paintPending = false;
  int oldWidth = -1;
  int oldHeight = -1;
  private int numBackBuffers = 0;
  private VolatileImage backBuffer = null;
  private BufferCapabilities backBufferCaps = null;
  private Color foreground;
  private Color background;
  private Font font;
  int nDropTargets;
  long nativeDropTargetContext;
  public int serialNum = 0;
  private static final double BANDING_DIVISOR = 4.0D;
  static final Font defaultFont = new Font("Dialog", 0, 12);
  private int updateX1;
  private int updateY1;
  private int updateX2;
  private int updateY2;
  private volatile boolean isAccelCapable = true;
  
  public native boolean isObscured();
  
  public boolean canDetermineObscurity()
  {
    return true;
  }
  
  private synchronized native void pShow();
  
  synchronized native void hide();
  
  synchronized native void enable();
  
  synchronized native void disable();
  
  public long getHWnd()
  {
    return this.hwnd;
  }
  
  public native Point getLocationOnScreen();
  
  public void setVisible(boolean paramBoolean)
  {
    if (paramBoolean) {
      show();
    } else {
      hide();
    }
  }
  
  public void show()
  {
    Dimension localDimension = ((Component)this.target).getSize();
    this.oldHeight = localDimension.height;
    this.oldWidth = localDimension.width;
    pShow();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {
      enable();
    } else {
      disable();
    }
  }
  
  private native void reshapeNoCheck(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this.paintPending = ((paramInt3 != this.oldWidth) || (paramInt4 != this.oldHeight));
    if ((paramInt5 & 0x4000) != 0) {
      reshapeNoCheck(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    if ((paramInt3 != this.oldWidth) || (paramInt4 != this.oldHeight))
    {
      try
      {
        replaceSurfaceData();
      }
      catch (InvalidPipeException localInvalidPipeException) {}
      this.oldWidth = paramInt3;
      this.oldHeight = paramInt4;
    }
    this.serialNum += 1;
  }
  
  void dynamicallyLayoutContainer()
  {
    if (log.isLoggable(PlatformLogger.Level.FINE))
    {
      localContainer = WToolkit.getNativeContainer((Component)this.target);
      if (localContainer != null) {
        log.fine("Assertion (parent == null) failed");
      }
    }
    final Container localContainer = (Container)this.target;
    WToolkit.executeOnEventHandlerThread(localContainer, new Runnable()
    {
      public void run()
      {
        localContainer.invalidate();
        localContainer.validate();
        if (((WComponentPeer.this.surfaceData instanceof D3DSurfaceData.D3DWindowSurfaceData)) || ((WComponentPeer.this.surfaceData instanceof OGLSurfaceData))) {
          try
          {
            WComponentPeer.this.replaceSurfaceData();
          }
          catch (InvalidPipeException localInvalidPipeException) {}
        }
      }
    });
  }
  
  void paintDamagedAreaImmediately()
  {
    updateWindow();
    SunToolkit.flushPendingEvents();
    this.paintArea.paint(this.target, shouldClearRectBeforePaint());
  }
  
  synchronized native void updateWindow();
  
  public void paint(Graphics paramGraphics)
  {
    ((Component)this.target).paint(paramGraphics);
  }
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  private native int[] createPrintedPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public void print(Graphics paramGraphics)
  {
    Component localComponent = (Component)this.target;
    int i = localComponent.getWidth();
    int j = localComponent.getHeight();
    int k = (int)(j / 4.0D);
    if (k == 0) {
      k = j;
    }
    int m = 0;
    while (m < j)
    {
      int n = m + k - 1;
      if (n >= j) {
        n = j - 1;
      }
      int i1 = n - m + 1;
      Color localColor = localComponent.getBackground();
      int[] arrayOfInt = createPrintedPixels(0, m, i, i1, localColor == null ? 255 : localColor.getAlpha());
      if (arrayOfInt != null)
      {
        BufferedImage localBufferedImage = new BufferedImage(i, i1, 2);
        localBufferedImage.setRGB(0, 0, i, i1, arrayOfInt, 0, i);
        paramGraphics.drawImage(localBufferedImage, 0, m, null);
        localBufferedImage.flush();
      }
      m += k;
    }
    localComponent.print(paramGraphics);
  }
  
  public void coalescePaintEvent(PaintEvent paramPaintEvent)
  {
    Rectangle localRectangle = paramPaintEvent.getUpdateRect();
    if (!(paramPaintEvent instanceof IgnorePaintEvent)) {
      this.paintArea.add(localRectangle, paramPaintEvent.getID());
    }
    if (log.isLoggable(PlatformLogger.Level.FINEST)) {
      switch (paramPaintEvent.getID())
      {
      case 801: 
        log.finest("coalescePaintEvent: UPDATE: add: x = " + localRectangle.x + ", y = " + localRectangle.y + ", width = " + localRectangle.width + ", height = " + localRectangle.height);
        return;
      case 800: 
        log.finest("coalescePaintEvent: PAINT: add: x = " + localRectangle.x + ", y = " + localRectangle.y + ", width = " + localRectangle.width + ", height = " + localRectangle.height);
        return;
      }
    }
  }
  
  public synchronized native void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public boolean handleJavaKeyEvent(KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public void handleJavaMouseEvent(MouseEvent paramMouseEvent)
  {
    switch (paramMouseEvent.getID())
    {
    case 501: 
      if ((this.target == paramMouseEvent.getSource()) && (!((Component)this.target).isFocusOwner()) && (WKeyboardFocusManagerPeer.shouldFocusOnClick((Component)this.target))) {
        WKeyboardFocusManagerPeer.requestFocusFor((Component)this.target, CausedFocusEvent.Cause.MOUSE_EVENT);
      }
      break;
    }
  }
  
  native void nativeHandleEvent(AWTEvent paramAWTEvent);
  
  public void handleEvent(AWTEvent paramAWTEvent)
  {
    int i = paramAWTEvent.getID();
    if (((paramAWTEvent instanceof InputEvent)) && (!((InputEvent)paramAWTEvent).isConsumed()) && (((Component)this.target).isEnabled())) {
      if (((paramAWTEvent instanceof MouseEvent)) && (!(paramAWTEvent instanceof MouseWheelEvent))) {
        handleJavaMouseEvent((MouseEvent)paramAWTEvent);
      } else if (((paramAWTEvent instanceof KeyEvent)) && (handleJavaKeyEvent((KeyEvent)paramAWTEvent))) {
        return;
      }
    }
    switch (i)
    {
    case 800: 
      this.paintPending = false;
    case 801: 
      if ((!this.isLayouting) && (!this.paintPending)) {
        this.paintArea.paint(this.target, shouldClearRectBeforePaint());
      }
      return;
    case 1004: 
    case 1005: 
      handleJavaFocusEvent((FocusEvent)paramAWTEvent);
    }
    nativeHandleEvent(paramAWTEvent);
  }
  
  void handleJavaFocusEvent(FocusEvent paramFocusEvent)
  {
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      focusLog.finer(paramFocusEvent.toString());
    }
    setFocus(paramFocusEvent.getID() == 1004);
  }
  
  native void setFocus(boolean paramBoolean);
  
  public Dimension getMinimumSize()
  {
    return ((Component)this.target).getSize();
  }
  
  public Dimension getPreferredSize()
  {
    return getMinimumSize();
  }
  
  public void layout() {}
  
  public Rectangle getBounds()
  {
    return ((Component)this.target).getBounds();
  }
  
  public boolean isFocusable()
  {
    return false;
  }
  
  public GraphicsConfiguration getGraphicsConfiguration()
  {
    if (this.winGraphicsConfig != null) {
      return this.winGraphicsConfig;
    }
    return ((Component)this.target).getGraphicsConfiguration();
  }
  
  public SurfaceData getSurfaceData()
  {
    return this.surfaceData;
  }
  
  public void replaceSurfaceData()
  {
    replaceSurfaceData(this.numBackBuffers, this.backBufferCaps);
  }
  
  public void createScreenSurface(boolean paramBoolean)
  {
    Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
    ScreenUpdateManager localScreenUpdateManager = ScreenUpdateManager.getInstance();
    this.surfaceData = localScreenUpdateManager.createScreenSurface(localWin32GraphicsConfig, this, this.numBackBuffers, paramBoolean);
  }
  
  public void replaceSurfaceData(int paramInt, BufferCapabilities paramBufferCapabilities)
  {
    SurfaceData localSurfaceData = null;
    VolatileImage localVolatileImage = null;
    synchronized (((Component)this.target).getTreeLock())
    {
      synchronized (this)
      {
        if (this.pData == 0L) {
          return;
        }
        this.numBackBuffers = paramInt;
        ScreenUpdateManager localScreenUpdateManager = ScreenUpdateManager.getInstance();
        localSurfaceData = this.surfaceData;
        localScreenUpdateManager.dropScreenSurface(localSurfaceData);
        createScreenSurface(true);
        if (localSurfaceData != null) {
          localSurfaceData.invalidate();
        }
        localVolatileImage = this.backBuffer;
        if (this.numBackBuffers > 0)
        {
          this.backBufferCaps = paramBufferCapabilities;
          Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
          this.backBuffer = localWin32GraphicsConfig.createBackBuffer(this);
        }
        else if (this.backBuffer != null)
        {
          this.backBufferCaps = null;
          this.backBuffer = null;
        }
      }
    }
    if (localSurfaceData != null)
    {
      localSurfaceData.flush();
      localSurfaceData = null;
    }
    if (localVolatileImage != null)
    {
      localVolatileImage.flush();
      localSurfaceData = null;
    }
  }
  
  public void replaceSurfaceDataLater()
  {
    Runnable local2 = new Runnable()
    {
      public void run()
      {
        if (!WComponentPeer.this.isDisposed()) {
          try
          {
            WComponentPeer.this.replaceSurfaceData();
          }
          catch (InvalidPipeException localInvalidPipeException) {}
        }
      }
    };
    Component localComponent = (Component)this.target;
    if (!PaintEventDispatcher.getPaintEventDispatcher().queueSurfaceDataReplacing(localComponent, local2)) {
      postEvent(new InvocationEvent(localComponent, local2));
    }
  }
  
  public boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration)
  {
    this.winGraphicsConfig = ((Win32GraphicsConfig)paramGraphicsConfiguration);
    try
    {
      replaceSurfaceData();
    }
    catch (InvalidPipeException localInvalidPipeException) {}
    return false;
  }
  
  public ColorModel getColorModel()
  {
    GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration();
    if (localGraphicsConfiguration != null) {
      return localGraphicsConfiguration.getColorModel();
    }
    return null;
  }
  
  public ColorModel getDeviceColorModel()
  {
    Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
    if (localWin32GraphicsConfig != null) {
      return localWin32GraphicsConfig.getDeviceColorModel();
    }
    return null;
  }
  
  public ColorModel getColorModel(int paramInt)
  {
    GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration();
    if (localGraphicsConfiguration != null) {
      return localGraphicsConfiguration.getColorModel(paramInt);
    }
    return null;
  }
  
  public Graphics getGraphics()
  {
    if (isDisposed()) {
      return null;
    }
    Component localComponent = (Component)getTarget();
    Window localWindow = SunToolkit.getContainingWindow(localComponent);
    Object localObject4;
    if (localWindow != null)
    {
      localObject1 = ((WWindowPeer)localWindow.getPeer()).getTranslucentGraphics();
      if (localObject1 != null)
      {
        int i = 0;
        int j = 0;
        for (localObject4 = localComponent; localObject4 != localWindow; localObject4 = ((Component)localObject4).getParent())
        {
          i += ((Component)localObject4).getX();
          j += ((Component)localObject4).getY();
        }
        ((Graphics)localObject1).translate(i, j);
        ((Graphics)localObject1).clipRect(0, 0, localComponent.getWidth(), localComponent.getHeight());
        return localObject1;
      }
    }
    Object localObject1 = this.surfaceData;
    if (localObject1 != null)
    {
      Object localObject2 = this.background;
      if (localObject2 == null) {
        localObject2 = SystemColor.window;
      }
      Object localObject3 = this.foreground;
      if (localObject3 == null) {
        localObject3 = SystemColor.windowText;
      }
      localObject4 = this.font;
      if (localObject4 == null) {
        localObject4 = defaultFont;
      }
      ScreenUpdateManager localScreenUpdateManager = ScreenUpdateManager.getInstance();
      return localScreenUpdateManager.createGraphics((SurfaceData)localObject1, this, (Color)localObject3, (Color)localObject2, (Font)localObject4);
    }
    return null;
  }
  
  public FontMetrics getFontMetrics(Font paramFont)
  {
    return WFontMetrics.getFontMetrics(paramFont);
  }
  
  private synchronized native void _dispose();
  
  protected void disposeImpl()
  {
    SurfaceData localSurfaceData = this.surfaceData;
    this.surfaceData = null;
    ScreenUpdateManager.getInstance().dropScreenSurface(localSurfaceData);
    localSurfaceData.invalidate();
    WToolkit.targetDisposedPeer(this.target, this);
    _dispose();
  }
  
  public void disposeLater()
  {
    postEvent(new InvocationEvent(this.target, new Runnable()
    {
      public void run()
      {
        WComponentPeer.this.dispose();
      }
    }));
  }
  
  public synchronized void setForeground(Color paramColor)
  {
    this.foreground = paramColor;
    _setForeground(paramColor.getRGB());
  }
  
  public synchronized void setBackground(Color paramColor)
  {
    this.background = paramColor;
    _setBackground(paramColor.getRGB());
  }
  
  public Color getBackgroundNoSync()
  {
    return this.background;
  }
  
  private native void _setForeground(int paramInt);
  
  private native void _setBackground(int paramInt);
  
  public synchronized void setFont(Font paramFont)
  {
    this.font = paramFont;
    _setFont(paramFont);
  }
  
  synchronized native void _setFont(Font paramFont);
  
  public void updateCursorImmediately()
  {
    WGlobalCursorManager.getCursorManager().updateCursorImmediately();
  }
  
  public boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
  {
    if (WKeyboardFocusManagerPeer.processSynchronousLightweightTransfer((Component)this.target, paramComponent, paramBoolean1, paramBoolean2, paramLong)) {
      return true;
    }
    int i = WKeyboardFocusManagerPeer.shouldNativelyFocusHeavyweight((Component)this.target, paramComponent, paramBoolean1, paramBoolean2, paramLong, paramCause);
    switch (i)
    {
    case 0: 
      return false;
    case 2: 
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
        focusLog.finer("Proceeding with request to " + paramComponent + " in " + this.target);
      }
      Window localWindow = SunToolkit.getContainingWindow((Component)this.target);
      if (localWindow == null) {
        return rejectFocusRequestHelper("WARNING: Parent window is null");
      }
      WWindowPeer localWWindowPeer = (WWindowPeer)localWindow.getPeer();
      if (localWWindowPeer == null) {
        return rejectFocusRequestHelper("WARNING: Parent window's peer is null");
      }
      boolean bool = localWWindowPeer.requestWindowFocus(paramCause);
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
        focusLog.finer("Requested window focus: " + bool);
      }
      if ((!bool) || (!localWindow.isFocused())) {
        return rejectFocusRequestHelper("Waiting for asynchronous processing of the request");
      }
      return WKeyboardFocusManagerPeer.deliverFocus(paramComponent, (Component)this.target, paramBoolean1, paramBoolean2, paramLong, paramCause);
    case 1: 
      return true;
    }
    return false;
  }
  
  private boolean rejectFocusRequestHelper(String paramString)
  {
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      focusLog.finer(paramString);
    }
    WKeyboardFocusManagerPeer.removeLastFocusRequest((Component)this.target);
    return false;
  }
  
  public Image createImage(ImageProducer paramImageProducer)
  {
    return new ToolkitImage(paramImageProducer);
  }
  
  public Image createImage(int paramInt1, int paramInt2)
  {
    Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
    return localWin32GraphicsConfig.createAcceleratedImage((Component)this.target, paramInt1, paramInt2);
  }
  
  public VolatileImage createVolatileImage(int paramInt1, int paramInt2)
  {
    return new SunVolatileImage((Component)this.target, paramInt1, paramInt2);
  }
  
  public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    return Toolkit.getDefaultToolkit().prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    return Toolkit.getDefaultToolkit().checkImage(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + this.target + "]";
  }
  
  WComponentPeer(Component paramComponent)
  {
    this.target = paramComponent;
    this.paintArea = new RepaintArea();
    create(getNativeParent());
    checkCreation();
    createScreenSurface(false);
    initialize();
    start();
  }
  
  abstract void create(WComponentPeer paramWComponentPeer);
  
  WComponentPeer getNativeParent()
  {
    Container localContainer = SunToolkit.getNativeContainer((Component)this.target);
    return (WComponentPeer)WToolkit.targetToPeer(localContainer);
  }
  
  protected void checkCreation()
  {
    if ((this.hwnd == 0L) || (this.pData == 0L))
    {
      if (this.createError != null) {
        throw this.createError;
      }
      throw new InternalError("couldn't create component peer");
    }
  }
  
  synchronized native void start();
  
  void initialize()
  {
    if (((Component)this.target).isVisible()) {
      show();
    }
    Color localColor = ((Component)this.target).getForeground();
    if (localColor != null) {
      setForeground(localColor);
    }
    Font localFont = ((Component)this.target).getFont();
    if (localFont != null) {
      setFont(localFont);
    }
    if (!((Component)this.target).isEnabled()) {
      disable();
    }
    Rectangle localRectangle = ((Component)this.target).getBounds();
    setBounds(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, 3);
  }
  
  void handleRepaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  void handleExpose(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    postPaintIfNecessary(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void handlePaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    postPaintIfNecessary(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  private void postPaintIfNecessary(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!AWTAccessor.getComponentAccessor().getIgnoreRepaint((Component)this.target))
    {
      PaintEvent localPaintEvent = PaintEventDispatcher.getPaintEventDispatcher().createPaintEvent((Component)this.target, paramInt1, paramInt2, paramInt3, paramInt4);
      if (localPaintEvent != null) {
        postEvent(localPaintEvent);
      }
    }
  }
  
  void postEvent(AWTEvent paramAWTEvent)
  {
    preprocessPostEvent(paramAWTEvent);
    WToolkit.postEvent(WToolkit.targetToAppContext(this.target), paramAWTEvent);
  }
  
  void preprocessPostEvent(AWTEvent paramAWTEvent) {}
  
  public void beginLayout()
  {
    this.isLayouting = true;
  }
  
  public void endLayout()
  {
    if ((!this.paintArea.isEmpty()) && (!this.paintPending) && (!((Component)this.target).getIgnoreRepaint())) {
      postEvent(new PaintEvent((Component)this.target, 800, new Rectangle()));
    }
    this.isLayouting = false;
  }
  
  public native void beginValidate();
  
  public native void endValidate();
  
  public Dimension preferredSize()
  {
    return getPreferredSize();
  }
  
  public synchronized void addDropTarget(DropTarget paramDropTarget)
  {
    if (this.nDropTargets == 0) {
      this.nativeDropTargetContext = addNativeDropTarget();
    }
    this.nDropTargets += 1;
  }
  
  public synchronized void removeDropTarget(DropTarget paramDropTarget)
  {
    this.nDropTargets -= 1;
    if (this.nDropTargets == 0)
    {
      removeNativeDropTarget();
      this.nativeDropTargetContext = 0L;
    }
  }
  
  native long addNativeDropTarget();
  
  native void removeNativeDropTarget();
  
  native boolean nativeHandlesWheelScrolling();
  
  public boolean handlesWheelScrolling()
  {
    return nativeHandlesWheelScrolling();
  }
  
  public boolean isPaintPending()
  {
    return (this.paintPending) && (this.isLayouting);
  }
  
  public void createBuffers(int paramInt, BufferCapabilities paramBufferCapabilities)
    throws AWTException
  {
    Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
    localWin32GraphicsConfig.assertOperationSupported((Component)this.target, paramInt, paramBufferCapabilities);
    try
    {
      replaceSurfaceData(paramInt - 1, paramBufferCapabilities);
    }
    catch (InvalidPipeException localInvalidPipeException)
    {
      throw new AWTException(localInvalidPipeException.getMessage());
    }
  }
  
  public void destroyBuffers()
  {
    replaceSurfaceData(0, null);
  }
  
  public void flip(int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents)
  {
    VolatileImage localVolatileImage = this.backBuffer;
    if (localVolatileImage == null) {
      throw new IllegalStateException("Buffers have not been created");
    }
    Win32GraphicsConfig localWin32GraphicsConfig = (Win32GraphicsConfig)getGraphicsConfiguration();
    localWin32GraphicsConfig.flip(this, (Component)this.target, localVolatileImage, paramInt1, paramInt2, paramInt3, paramInt4, paramFlipContents);
  }
  
  public synchronized Image getBackBuffer()
  {
    VolatileImage localVolatileImage = this.backBuffer;
    if (localVolatileImage == null) {
      throw new IllegalStateException("Buffers have not been created");
    }
    return localVolatileImage;
  }
  
  public BufferCapabilities getBackBufferCaps()
  {
    return this.backBufferCaps;
  }
  
  public int getBackBuffersNum()
  {
    return this.numBackBuffers;
  }
  
  public boolean shouldClearRectBeforePaint()
  {
    return true;
  }
  
  native void pSetParent(ComponentPeer paramComponentPeer);
  
  public void reparent(ContainerPeer paramContainerPeer)
  {
    pSetParent(paramContainerPeer);
  }
  
  public boolean isReparentSupported()
  {
    return true;
  }
  
  public void setBoundsOperation(int paramInt) {}
  
  public boolean isAccelCapable()
  {
    if ((!this.isAccelCapable) || (!isContainingTopLevelAccelCapable((Component)this.target))) {
      return false;
    }
    boolean bool = SunToolkit.isContainingTopLevelTranslucent((Component)this.target);
    return (!bool) || (Win32GraphicsEnvironment.isVistaOS());
  }
  
  public void disableAcceleration()
  {
    this.isAccelCapable = false;
  }
  
  native void setRectangularShape(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Region paramRegion);
  
  private static final boolean isContainingTopLevelAccelCapable(Component paramComponent)
  {
    while ((paramComponent != null) && (!(paramComponent instanceof WEmbeddedFrame))) {
      paramComponent = paramComponent.getParent();
    }
    if (paramComponent == null) {
      return true;
    }
    return ((WEmbeddedFramePeer)paramComponent.getPeer()).isAccelCapable();
  }
  
  public void applyShape(Region paramRegion)
  {
    if (shapeLog.isLoggable(PlatformLogger.Level.FINER)) {
      shapeLog.finer("*** INFO: Setting shape: PEER: " + this + "; TARGET: " + this.target + "; SHAPE: " + paramRegion);
    }
    if (paramRegion != null) {
      setRectangularShape(paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getHiX(), paramRegion.getHiY(), paramRegion.isRectangular() ? null : paramRegion);
    } else {
      setRectangularShape(0, 0, 0, 0, null);
    }
  }
  
  public void setZOrder(ComponentPeer paramComponentPeer)
  {
    long l = paramComponentPeer != null ? ((WComponentPeer)paramComponentPeer).getHWnd() : 0L;
    setZOrder(l);
  }
  
  private native void setZOrder(long paramLong);
  
  public boolean isLightweightFramePeer()
  {
    return false;
  }
}
