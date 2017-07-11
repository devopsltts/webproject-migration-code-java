package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.PrintStream;
import java.security.AccessController;
import sun.awt.DisplayChangedListener;
import sun.awt.image.SurfaceManager.FlushableCacheData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.BlitBg;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.security.action.GetPropertyAction;

public abstract class SurfaceDataProxy
  implements DisplayChangedListener, SurfaceManager.FlushableCacheData
{
  private static boolean cachingAllowed = true;
  private static int defaultThreshold;
  public static SurfaceDataProxy UNCACHED = new SurfaceDataProxy(0)
  {
    public boolean isAccelerated()
    {
      return false;
    }
    
    public boolean isSupportedOperation(SurfaceData paramAnonymousSurfaceData, int paramAnonymousInt, CompositeType paramAnonymousCompositeType, Color paramAnonymousColor)
    {
      return false;
    }
    
    public SurfaceData validateSurfaceData(SurfaceData paramAnonymousSurfaceData1, SurfaceData paramAnonymousSurfaceData2, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      throw new InternalError("UNCACHED should never validate SDs");
    }
    
    public SurfaceData replaceData(SurfaceData paramAnonymousSurfaceData, int paramAnonymousInt, CompositeType paramAnonymousCompositeType, Color paramAnonymousColor)
    {
      return paramAnonymousSurfaceData;
    }
  };
  private int threshold;
  private StateTracker srcTracker;
  private int numtries;
  private SurfaceData cachedSD;
  private StateTracker cacheTracker;
  private boolean valid;
  
  public static boolean isCachingAllowed()
  {
    return cachingAllowed;
  }
  
  public abstract boolean isSupportedOperation(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor);
  
  public abstract SurfaceData validateSurfaceData(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2);
  
  public StateTracker getRetryTracker(SurfaceData paramSurfaceData)
  {
    return new CountdownTracker(this.threshold);
  }
  
  public SurfaceDataProxy()
  {
    this(defaultThreshold);
  }
  
  public SurfaceDataProxy(int paramInt)
  {
    this.threshold = paramInt;
    this.srcTracker = StateTracker.NEVER_CURRENT;
    this.cacheTracker = StateTracker.NEVER_CURRENT;
    this.valid = true;
  }
  
  public boolean isValid()
  {
    return this.valid;
  }
  
  public void invalidate()
  {
    this.valid = false;
  }
  
  public boolean flush(boolean paramBoolean)
  {
    if (paramBoolean) {
      invalidate();
    }
    flush();
    return !isValid();
  }
  
  public synchronized void flush()
  {
    SurfaceData localSurfaceData = this.cachedSD;
    this.cachedSD = null;
    this.cacheTracker = StateTracker.NEVER_CURRENT;
    if (localSurfaceData != null) {
      localSurfaceData.flush();
    }
  }
  
  public boolean isAccelerated()
  {
    return (isValid()) && (this.srcTracker.isCurrent()) && (this.cacheTracker.isCurrent());
  }
  
  protected void activateDisplayListener()
  {
    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if ((localGraphicsEnvironment instanceof SunGraphicsEnvironment)) {
      ((SunGraphicsEnvironment)localGraphicsEnvironment).addDisplayChangedListener(this);
    }
  }
  
  public void displayChanged()
  {
    flush();
  }
  
  public void paletteChanged()
  {
    this.srcTracker = StateTracker.NEVER_CURRENT;
  }
  
  public SurfaceData replaceData(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor)
  {
    if (isSupportedOperation(paramSurfaceData, paramInt, paramCompositeType, paramColor))
    {
      if (!this.srcTracker.isCurrent())
      {
        synchronized (this)
        {
          this.numtries = this.threshold;
          this.srcTracker = paramSurfaceData.getStateTracker();
          this.cacheTracker = StateTracker.NEVER_CURRENT;
        }
        if (!this.srcTracker.isCurrent())
        {
          if (paramSurfaceData.getState() == StateTrackable.State.UNTRACKABLE)
          {
            invalidate();
            flush();
          }
          return paramSurfaceData;
        }
      }
      ??? = this.cachedSD;
      if (!this.cacheTracker.isCurrent())
      {
        synchronized (this)
        {
          if (this.numtries > 0)
          {
            this.numtries -= 1;
            return paramSurfaceData;
          }
        }
        ??? = paramSurfaceData.getBounds();
        int i = ((Rectangle)???).width;
        int j = ((Rectangle)???).height;
        StateTracker localStateTracker = this.srcTracker;
        ??? = validateSurfaceData(paramSurfaceData, (SurfaceData)???, i, j);
        if (??? == null)
        {
          synchronized (this)
          {
            if (localStateTracker == this.srcTracker)
            {
              this.cacheTracker = getRetryTracker(paramSurfaceData);
              this.cachedSD = null;
            }
          }
          return paramSurfaceData;
        }
        updateSurfaceData(paramSurfaceData, (SurfaceData)???, i, j);
        if (!((SurfaceData)???).isValid()) {
          return paramSurfaceData;
        }
        synchronized (this)
        {
          if ((localStateTracker == this.srcTracker) && (localStateTracker.isCurrent()))
          {
            this.cacheTracker = ((SurfaceData)???).getStateTracker();
            this.cachedSD = ((SurfaceData)???);
          }
        }
      }
      if (??? != null) {
        return ???;
      }
    }
    return paramSurfaceData;
  }
  
  public void updateSurfaceData(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2)
  {
    SurfaceType localSurfaceType1 = paramSurfaceData1.getSurfaceType();
    SurfaceType localSurfaceType2 = paramSurfaceData2.getSurfaceType();
    Blit localBlit = Blit.getFromCache(localSurfaceType1, CompositeType.SrcNoEa, localSurfaceType2);
    localBlit.Blit(paramSurfaceData1, paramSurfaceData2, AlphaComposite.Src, null, 0, 0, 0, 0, paramInt1, paramInt2);
    paramSurfaceData2.markDirty();
  }
  
  public void updateSurfaceDataBg(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2, Color paramColor)
  {
    SurfaceType localSurfaceType1 = paramSurfaceData1.getSurfaceType();
    SurfaceType localSurfaceType2 = paramSurfaceData2.getSurfaceType();
    BlitBg localBlitBg = BlitBg.getFromCache(localSurfaceType1, CompositeType.SrcNoEa, localSurfaceType2);
    localBlitBg.BlitBg(paramSurfaceData1, paramSurfaceData2, AlphaComposite.Src, null, paramColor.getRGB(), 0, 0, 0, 0, paramInt1, paramInt2);
    paramSurfaceData2.markDirty();
  }
  
  static
  {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.managedimages"));
    if ((str1 != null) && (str1.equals("false")))
    {
      cachingAllowed = false;
      System.out.println("Disabling managed images");
    }
    defaultThreshold = 1;
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.accthreshold"));
    if (str2 != null) {
      try
      {
        int i = Integer.parseInt(str2);
        if (i >= 0)
        {
          defaultThreshold = i;
          System.out.println("New Default Acceleration Threshold: " + defaultThreshold);
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        System.err.println("Error setting new threshold:" + localNumberFormatException);
      }
    }
  }
  
  public static class CountdownTracker
    implements StateTracker
  {
    private int countdown;
    
    public CountdownTracker(int paramInt)
    {
      this.countdown = paramInt;
    }
    
    public synchronized boolean isCurrent()
    {
      return --this.countdown >= 0;
    }
  }
}
