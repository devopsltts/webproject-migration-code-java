package javax.swing.plaf.nimbus;

import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ImageCache
{
  private final LinkedHashMap<Integer, PixelCountSoftReference> map = new LinkedHashMap(16, 0.75F, true);
  private final int maxPixelCount;
  private final int maxSingleImagePixelSize;
  private int currentPixelCount = 0;
  private ReadWriteLock lock = new ReentrantReadWriteLock();
  private ReferenceQueue<Image> referenceQueue = new ReferenceQueue();
  private static final ImageCache instance = new ImageCache();
  
  static ImageCache getInstance()
  {
    return instance;
  }
  
  public ImageCache()
  {
    this.maxPixelCount = 2097152;
    this.maxSingleImagePixelSize = 90000;
  }
  
  public ImageCache(int paramInt1, int paramInt2)
  {
    this.maxPixelCount = paramInt1;
    this.maxSingleImagePixelSize = paramInt2;
  }
  
  public void flush()
  {
    this.lock.readLock().lock();
    try
    {
      this.map.clear();
      this.lock.readLock().unlock();
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  public boolean isImageCachable(int paramInt1, int paramInt2)
  {
    return paramInt1 * paramInt2 < this.maxSingleImagePixelSize;
  }
  
  public Image getImage(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    this.lock.readLock().lock();
    try
    {
      PixelCountSoftReference localPixelCountSoftReference = (PixelCountSoftReference)this.map.get(Integer.valueOf(hash(paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs)));
      if ((localPixelCountSoftReference != null) && (localPixelCountSoftReference.equals(paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs)))
      {
        localImage = (Image)localPixelCountSoftReference.get();
        return localImage;
      }
      Image localImage = null;
      return localImage;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  public boolean setImage(Image paramImage, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (!isImageCachable(paramInt1, paramInt2)) {
      return false;
    }
    int i = hash(paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs);
    this.lock.writeLock().lock();
    try
    {
      PixelCountSoftReference localPixelCountSoftReference = (PixelCountSoftReference)this.map.get(Integer.valueOf(i));
      if ((localPixelCountSoftReference != null) && (localPixelCountSoftReference.get() == paramImage))
      {
        boolean bool1 = true;
        return bool1;
      }
      if (localPixelCountSoftReference != null)
      {
        this.currentPixelCount -= localPixelCountSoftReference.pixelCount;
        this.map.remove(Integer.valueOf(i));
      }
      int j = paramImage.getWidth(null) * paramImage.getHeight(null);
      this.currentPixelCount += j;
      if (this.currentPixelCount > this.maxPixelCount) {
        while ((localPixelCountSoftReference = (PixelCountSoftReference)this.referenceQueue.poll()) != null)
        {
          this.map.remove(Integer.valueOf(localPixelCountSoftReference.hash));
          this.currentPixelCount -= localPixelCountSoftReference.pixelCount;
        }
      }
      if (this.currentPixelCount > this.maxPixelCount)
      {
        Iterator localIterator = this.map.entrySet().iterator();
        while ((this.currentPixelCount > this.maxPixelCount) && (localIterator.hasNext()))
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          localIterator.remove();
          Image localImage = (Image)((PixelCountSoftReference)localEntry.getValue()).get();
          if (localImage != null) {
            localImage.flush();
          }
          this.currentPixelCount -= ((PixelCountSoftReference)localEntry.getValue()).pixelCount;
        }
      }
      this.map.put(Integer.valueOf(i), new PixelCountSoftReference(paramImage, this.referenceQueue, j, i, paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs));
      boolean bool2 = true;
      return bool2;
    }
    finally
    {
      this.lock.writeLock().unlock();
    }
  }
  
  private int hash(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    int i = paramGraphicsConfiguration != null ? paramGraphicsConfiguration.hashCode() : 0;
    i = 31 * i + paramInt1;
    i = 31 * i + paramInt2;
    i = 31 * i + Arrays.deepHashCode(paramVarArgs);
    return i;
  }
  
  private static class PixelCountSoftReference
    extends SoftReference<Image>
  {
    private final int pixelCount;
    private final int hash;
    private final GraphicsConfiguration config;
    private final int w;
    private final int h;
    private final Object[] args;
    
    public PixelCountSoftReference(Image paramImage, ReferenceQueue<? super Image> paramReferenceQueue, int paramInt1, int paramInt2, GraphicsConfiguration paramGraphicsConfiguration, int paramInt3, int paramInt4, Object[] paramArrayOfObject)
    {
      super(paramReferenceQueue);
      this.pixelCount = paramInt1;
      this.hash = paramInt2;
      this.config = paramGraphicsConfiguration;
      this.w = paramInt3;
      this.h = paramInt4;
      this.args = paramArrayOfObject;
    }
    
    public boolean equals(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    {
      return (paramGraphicsConfiguration == this.config) && (paramInt1 == this.w) && (paramInt2 == this.h) && (Arrays.equals(paramArrayOfObject, this.args));
    }
  }
}
