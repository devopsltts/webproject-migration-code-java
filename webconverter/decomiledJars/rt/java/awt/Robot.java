package java.awt;

import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.peer.RobotPeer;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import sun.awt.ComponentFactory;
import sun.awt.SunToolkit;
import sun.awt.image.SunWritableRaster;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.security.util.SecurityConstants.AWT;

public class Robot
{
  private static final int MAX_DELAY = 60000;
  private RobotPeer peer;
  private boolean isAutoWaitForIdle = false;
  private int autoDelay = 0;
  private static int LEGAL_BUTTON_MASK = 0;
  private DirectColorModel screenCapCM = null;
  private transient Object anchor = new Object();
  private transient RobotDisposer disposer;
  
  public Robot()
    throws AWTException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new AWTException("headless environment");
    }
    init(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
  }
  
  public Robot(GraphicsDevice paramGraphicsDevice)
    throws AWTException
  {
    checkIsScreenDevice(paramGraphicsDevice);
    init(paramGraphicsDevice);
  }
  
  private void init(GraphicsDevice paramGraphicsDevice)
    throws AWTException
  {
    checkRobotAllowed();
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((localToolkit instanceof ComponentFactory))
    {
      this.peer = ((ComponentFactory)localToolkit).createRobot(this, paramGraphicsDevice);
      this.disposer = new RobotDisposer(this.peer);
      Disposer.addRecord(this.anchor, this.disposer);
    }
    initLegalButtonMask();
  }
  
  private static synchronized void initLegalButtonMask()
  {
    if (LEGAL_BUTTON_MASK != 0) {
      return;
    }
    int i = 0;
    if ((Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled()) && ((Toolkit.getDefaultToolkit() instanceof SunToolkit)))
    {
      int j = ((SunToolkit)Toolkit.getDefaultToolkit()).getNumberOfButtons();
      for (int k = 0; k < j; k++) {
        i |= InputEvent.getMaskForButton(k + 1);
      }
    }
    i |= 0x1C1C;
    LEGAL_BUTTON_MASK = i;
  }
  
  private void checkRobotAllowed()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.AWT.CREATE_ROBOT_PERMISSION);
    }
  }
  
  private void checkIsScreenDevice(GraphicsDevice paramGraphicsDevice)
  {
    if ((paramGraphicsDevice == null) || (paramGraphicsDevice.getType() != 0)) {
      throw new IllegalArgumentException("not a valid screen device");
    }
  }
  
  public synchronized void mouseMove(int paramInt1, int paramInt2)
  {
    this.peer.mouseMove(paramInt1, paramInt2);
    afterEvent();
  }
  
  public synchronized void mousePress(int paramInt)
  {
    checkButtonsArgument(paramInt);
    this.peer.mousePress(paramInt);
    afterEvent();
  }
  
  public synchronized void mouseRelease(int paramInt)
  {
    checkButtonsArgument(paramInt);
    this.peer.mouseRelease(paramInt);
    afterEvent();
  }
  
  private void checkButtonsArgument(int paramInt)
  {
    if ((paramInt | LEGAL_BUTTON_MASK) != LEGAL_BUTTON_MASK) {
      throw new IllegalArgumentException("Invalid combination of button flags");
    }
  }
  
  public synchronized void mouseWheel(int paramInt)
  {
    this.peer.mouseWheel(paramInt);
    afterEvent();
  }
  
  public synchronized void keyPress(int paramInt)
  {
    checkKeycodeArgument(paramInt);
    this.peer.keyPress(paramInt);
    afterEvent();
  }
  
  public synchronized void keyRelease(int paramInt)
  {
    checkKeycodeArgument(paramInt);
    this.peer.keyRelease(paramInt);
    afterEvent();
  }
  
  private void checkKeycodeArgument(int paramInt)
  {
    if (paramInt == 0) {
      throw new IllegalArgumentException("Invalid key code");
    }
  }
  
  public synchronized Color getPixelColor(int paramInt1, int paramInt2)
  {
    Color localColor = new Color(this.peer.getRGBPixel(paramInt1, paramInt2));
    return localColor;
  }
  
  public synchronized BufferedImage createScreenCapture(Rectangle paramRectangle)
  {
    checkScreenCaptureAllowed();
    checkValidRect(paramRectangle);
    if (this.screenCapCM == null) {
      this.screenCapCM = new DirectColorModel(24, 16711680, 65280, 255);
    }
    Toolkit.getDefaultToolkit().sync();
    int[] arrayOfInt2 = new int[3];
    int[] arrayOfInt1 = this.peer.getRGBPixels(paramRectangle);
    DataBufferInt localDataBufferInt = new DataBufferInt(arrayOfInt1, arrayOfInt1.length);
    arrayOfInt2[0] = this.screenCapCM.getRedMask();
    arrayOfInt2[1] = this.screenCapCM.getGreenMask();
    arrayOfInt2[2] = this.screenCapCM.getBlueMask();
    WritableRaster localWritableRaster = Raster.createPackedRaster(localDataBufferInt, paramRectangle.width, paramRectangle.height, paramRectangle.width, arrayOfInt2, null);
    SunWritableRaster.makeTrackable(localDataBufferInt);
    BufferedImage localBufferedImage = new BufferedImage(this.screenCapCM, localWritableRaster, false, null);
    return localBufferedImage;
  }
  
  private static void checkValidRect(Rectangle paramRectangle)
  {
    if ((paramRectangle.width <= 0) || (paramRectangle.height <= 0)) {
      throw new IllegalArgumentException("Rectangle width and height must be > 0");
    }
  }
  
  private static void checkScreenCaptureAllowed()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.AWT.READ_DISPLAY_PIXELS_PERMISSION);
    }
  }
  
  private void afterEvent()
  {
    autoWaitForIdle();
    autoDelay();
  }
  
  public synchronized boolean isAutoWaitForIdle()
  {
    return this.isAutoWaitForIdle;
  }
  
  public synchronized void setAutoWaitForIdle(boolean paramBoolean)
  {
    this.isAutoWaitForIdle = paramBoolean;
  }
  
  private void autoWaitForIdle()
  {
    if (this.isAutoWaitForIdle) {
      waitForIdle();
    }
  }
  
  public synchronized int getAutoDelay()
  {
    return this.autoDelay;
  }
  
  public synchronized void setAutoDelay(int paramInt)
  {
    checkDelayArgument(paramInt);
    this.autoDelay = paramInt;
  }
  
  private void autoDelay()
  {
    delay(this.autoDelay);
  }
  
  public synchronized void delay(int paramInt)
  {
    checkDelayArgument(paramInt);
    try
    {
      Thread.sleep(paramInt);
    }
    catch (InterruptedException localInterruptedException)
    {
      localInterruptedException.printStackTrace();
    }
  }
  
  private void checkDelayArgument(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 60000)) {
      throw new IllegalArgumentException("Delay must be to 0 to 60,000ms");
    }
  }
  
  public synchronized void waitForIdle()
  {
    checkNotDispatchThread();
    try
    {
      SunToolkit.flushPendingEvents();
      EventQueue.invokeAndWait(new Runnable()
      {
        public void run() {}
      });
    }
    catch (InterruptedException localInterruptedException)
    {
      System.err.println("Robot.waitForIdle, non-fatal exception caught:");
      localInterruptedException.printStackTrace();
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      System.err.println("Robot.waitForIdle, non-fatal exception caught:");
      localInvocationTargetException.printStackTrace();
    }
  }
  
  private void checkNotDispatchThread()
  {
    if (EventQueue.isDispatchThread()) {
      throw new IllegalThreadStateException("Cannot call method from the event dispatcher thread");
    }
  }
  
  public synchronized String toString()
  {
    String str = "autoDelay = " + getAutoDelay() + ", " + "autoWaitForIdle = " + isAutoWaitForIdle();
    return getClass().getName() + "[ " + str + " ]";
  }
  
  static class RobotDisposer
    implements DisposerRecord
  {
    private final RobotPeer peer;
    
    public RobotDisposer(RobotPeer paramRobotPeer)
    {
      this.peer = paramRobotPeer;
    }
    
    public void dispose()
    {
      if (this.peer != null) {
        this.peer.dispose();
      }
    }
  }
}
