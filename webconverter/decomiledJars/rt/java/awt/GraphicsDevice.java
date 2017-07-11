package java.awt;

import java.awt.image.ColorModel;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public abstract class GraphicsDevice
{
  private Window fullScreenWindow;
  private AppContext fullScreenAppContext;
  private final Object fsAppContextLock = new Object();
  private Rectangle windowedModeBounds;
  public static final int TYPE_RASTER_SCREEN = 0;
  public static final int TYPE_PRINTER = 1;
  public static final int TYPE_IMAGE_BUFFER = 2;
  
  protected GraphicsDevice() {}
  
  public abstract int getType();
  
  public abstract String getIDstring();
  
  public abstract GraphicsConfiguration[] getConfigurations();
  
  public abstract GraphicsConfiguration getDefaultConfiguration();
  
  public GraphicsConfiguration getBestConfiguration(GraphicsConfigTemplate paramGraphicsConfigTemplate)
  {
    GraphicsConfiguration[] arrayOfGraphicsConfiguration = getConfigurations();
    return paramGraphicsConfigTemplate.getBestConfiguration(arrayOfGraphicsConfiguration);
  }
  
  public boolean isFullScreenSupported()
  {
    return false;
  }
  
  public void setFullScreenWindow(Window paramWindow)
  {
    if (paramWindow != null)
    {
      if (paramWindow.getShape() != null) {
        paramWindow.setShape(null);
      }
      if (paramWindow.getOpacity() < 1.0F) {
        paramWindow.setOpacity(1.0F);
      }
      if (!paramWindow.isOpaque())
      {
        localObject1 = paramWindow.getBackground();
        localObject1 = new Color(((Color)localObject1).getRed(), ((Color)localObject1).getGreen(), ((Color)localObject1).getBlue(), 255);
        paramWindow.setBackground((Color)localObject1);
      }
      Object localObject1 = paramWindow.getGraphicsConfiguration();
      if ((localObject1 != null) && (((GraphicsConfiguration)localObject1).getDevice() != this) && (((GraphicsConfiguration)localObject1).getDevice().getFullScreenWindow() == paramWindow)) {
        ((GraphicsConfiguration)localObject1).getDevice().setFullScreenWindow(null);
      }
    }
    if ((this.fullScreenWindow != null) && (this.windowedModeBounds != null))
    {
      if (this.windowedModeBounds.width == 0) {
        this.windowedModeBounds.width = 1;
      }
      if (this.windowedModeBounds.height == 0) {
        this.windowedModeBounds.height = 1;
      }
      this.fullScreenWindow.setBounds(this.windowedModeBounds);
    }
    synchronized (this.fsAppContextLock)
    {
      if (paramWindow == null) {
        this.fullScreenAppContext = null;
      } else {
        this.fullScreenAppContext = AppContext.getAppContext();
      }
      this.fullScreenWindow = paramWindow;
    }
    if (this.fullScreenWindow != null)
    {
      this.windowedModeBounds = this.fullScreenWindow.getBounds();
      ??? = getDefaultConfiguration();
      Rectangle localRectangle = ((GraphicsConfiguration)???).getBounds();
      if (SunToolkit.isDispatchThreadForAppContext(this.fullScreenWindow)) {
        this.fullScreenWindow.setGraphicsConfiguration((GraphicsConfiguration)???);
      }
      this.fullScreenWindow.setBounds(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
      this.fullScreenWindow.setVisible(true);
      this.fullScreenWindow.toFront();
    }
  }
  
  public Window getFullScreenWindow()
  {
    Window localWindow = null;
    synchronized (this.fsAppContextLock)
    {
      if (this.fullScreenAppContext == AppContext.getAppContext()) {
        localWindow = this.fullScreenWindow;
      }
    }
    return localWindow;
  }
  
  public boolean isDisplayChangeSupported()
  {
    return false;
  }
  
  public void setDisplayMode(DisplayMode paramDisplayMode)
  {
    throw new UnsupportedOperationException("Cannot change display mode");
  }
  
  public DisplayMode getDisplayMode()
  {
    GraphicsConfiguration localGraphicsConfiguration = getDefaultConfiguration();
    Rectangle localRectangle = localGraphicsConfiguration.getBounds();
    ColorModel localColorModel = localGraphicsConfiguration.getColorModel();
    return new DisplayMode(localRectangle.width, localRectangle.height, localColorModel.getPixelSize(), 0);
  }
  
  public DisplayMode[] getDisplayModes()
  {
    return new DisplayMode[] { getDisplayMode() };
  }
  
  public int getAvailableAcceleratedMemory()
  {
    return -1;
  }
  
  public boolean isWindowTranslucencySupported(WindowTranslucency paramWindowTranslucency)
  {
    switch (1.$SwitchMap$java$awt$GraphicsDevice$WindowTranslucency[paramWindowTranslucency.ordinal()])
    {
    case 1: 
      return isWindowShapingSupported();
    case 2: 
      return isWindowOpacitySupported();
    case 3: 
      return isWindowPerpixelTranslucencySupported();
    }
    return false;
  }
  
  static boolean isWindowShapingSupported()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if (!(localToolkit instanceof SunToolkit)) {
      return false;
    }
    return ((SunToolkit)localToolkit).isWindowShapingSupported();
  }
  
  static boolean isWindowOpacitySupported()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if (!(localToolkit instanceof SunToolkit)) {
      return false;
    }
    return ((SunToolkit)localToolkit).isWindowOpacitySupported();
  }
  
  boolean isWindowPerpixelTranslucencySupported()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if (!(localToolkit instanceof SunToolkit)) {
      return false;
    }
    if (!((SunToolkit)localToolkit).isWindowTranslucencySupported()) {
      return false;
    }
    return getTranslucencyCapableGC() != null;
  }
  
  GraphicsConfiguration getTranslucencyCapableGC()
  {
    GraphicsConfiguration localGraphicsConfiguration = getDefaultConfiguration();
    if (localGraphicsConfiguration.isTranslucencyCapable()) {
      return localGraphicsConfiguration;
    }
    GraphicsConfiguration[] arrayOfGraphicsConfiguration = getConfigurations();
    for (int i = 0; i < arrayOfGraphicsConfiguration.length; i++) {
      if (arrayOfGraphicsConfiguration[i].isTranslucencyCapable()) {
        return arrayOfGraphicsConfiguration[i];
      }
    }
    return null;
  }
  
  public static enum WindowTranslucency
  {
    PERPIXEL_TRANSPARENT,  TRANSLUCENT,  PERPIXEL_TRANSLUCENT;
    
    private WindowTranslucency() {}
  }
}
