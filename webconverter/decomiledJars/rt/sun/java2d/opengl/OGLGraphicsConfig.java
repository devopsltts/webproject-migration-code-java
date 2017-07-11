package sun.java2d.opengl;

import sun.awt.image.SurfaceManager.ProxiedGraphicsConfig;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.hw.AccelGraphicsConfig;

abstract interface OGLGraphicsConfig
  extends AccelGraphicsConfig, SurfaceManager.ProxiedGraphicsConfig
{
  public abstract OGLContext getContext();
  
  public abstract long getNativeConfigInfo();
  
  public abstract boolean isCapPresent(int paramInt);
  
  public abstract SurfaceData createManagedSurface(int paramInt1, int paramInt2, int paramInt3);
}
