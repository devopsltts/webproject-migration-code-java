package sun.java2d.opengl;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

class OGLUtilities
{
  public static final int UNDEFINED = 0;
  public static final int WINDOW = 1;
  public static final int PBUFFER = 2;
  public static final int TEXTURE = 3;
  public static final int FLIP_BACKBUFFER = 4;
  public static final int FBOBJECT = 5;
  
  private OGLUtilities() {}
  
  public static boolean isQueueFlusherThread()
  {
    return OGLRenderQueue.isQueueFlusherThread();
  }
  
  public static boolean invokeWithOGLContextCurrent(Graphics paramGraphics, Runnable paramRunnable)
  {
    OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
    localOGLRenderQueue.lock();
    try
    {
      if (paramGraphics != null)
      {
        if (!(paramGraphics instanceof SunGraphics2D))
        {
          boolean bool1 = false;
          return bool1;
        }
        SurfaceData localSurfaceData = ((SunGraphics2D)paramGraphics).surfaceData;
        if (!(localSurfaceData instanceof OGLSurfaceData))
        {
          boolean bool2 = false;
          return bool2;
        }
        OGLContext.validateContext((OGLSurfaceData)localSurfaceData);
      }
      localOGLRenderQueue.flushAndInvokeNow(paramRunnable);
      OGLContext.invalidateCurrentContext();
    }
    finally
    {
      localOGLRenderQueue.unlock();
    }
    return true;
  }
  
  public static boolean invokeWithOGLSharedContextCurrent(GraphicsConfiguration paramGraphicsConfiguration, Runnable paramRunnable)
  {
    if (!(paramGraphicsConfiguration instanceof OGLGraphicsConfig)) {
      return false;
    }
    OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
    localOGLRenderQueue.lock();
    try
    {
      OGLContext.setScratchSurface((OGLGraphicsConfig)paramGraphicsConfiguration);
      localOGLRenderQueue.flushAndInvokeNow(paramRunnable);
      OGLContext.invalidateCurrentContext();
      localOGLRenderQueue.unlock();
    }
    finally
    {
      localOGLRenderQueue.unlock();
    }
    return true;
  }
  
  public static Rectangle getOGLViewport(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    if (!(paramGraphics instanceof SunGraphics2D)) {
      return null;
    }
    SunGraphics2D localSunGraphics2D = (SunGraphics2D)paramGraphics;
    SurfaceData localSurfaceData = localSunGraphics2D.surfaceData;
    int i = localSunGraphics2D.transX;
    int j = localSunGraphics2D.transY;
    Rectangle localRectangle = localSurfaceData.getBounds();
    int k = i;
    int m = localRectangle.height - (j + paramInt2);
    return new Rectangle(k, m, paramInt1, paramInt2);
  }
  
  public static Rectangle getOGLScissorBox(Graphics paramGraphics)
  {
    if (!(paramGraphics instanceof SunGraphics2D)) {
      return null;
    }
    SunGraphics2D localSunGraphics2D = (SunGraphics2D)paramGraphics;
    SurfaceData localSurfaceData = localSunGraphics2D.surfaceData;
    Region localRegion = localSunGraphics2D.getCompClip();
    if (!localRegion.isRectangular()) {
      return null;
    }
    int i = localRegion.getLoX();
    int j = localRegion.getLoY();
    int k = localRegion.getWidth();
    int m = localRegion.getHeight();
    Rectangle localRectangle = localSurfaceData.getBounds();
    int n = i;
    int i1 = localRectangle.height - (j + m);
    return new Rectangle(n, i1, k, m);
  }
  
  public static Object getOGLSurfaceIdentifier(Graphics paramGraphics)
  {
    if (!(paramGraphics instanceof SunGraphics2D)) {
      return null;
    }
    return ((SunGraphics2D)paramGraphics).surfaceData;
  }
  
  public static int getOGLSurfaceType(Graphics paramGraphics)
  {
    if (!(paramGraphics instanceof SunGraphics2D)) {
      return 0;
    }
    SurfaceData localSurfaceData = ((SunGraphics2D)paramGraphics).surfaceData;
    if (!(localSurfaceData instanceof OGLSurfaceData)) {
      return 0;
    }
    return ((OGLSurfaceData)localSurfaceData).getType();
  }
  
  public static int getOGLTextureType(Graphics paramGraphics)
  {
    if (!(paramGraphics instanceof SunGraphics2D)) {
      return 0;
    }
    SurfaceData localSurfaceData = ((SunGraphics2D)paramGraphics).surfaceData;
    if (!(localSurfaceData instanceof OGLSurfaceData)) {
      return 0;
    }
    return ((OGLSurfaceData)localSurfaceData).getTextureTarget();
  }
}
