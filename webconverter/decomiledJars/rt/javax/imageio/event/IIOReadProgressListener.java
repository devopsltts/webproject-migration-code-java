package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageReader;

public abstract interface IIOReadProgressListener
  extends EventListener
{
  public abstract void sequenceStarted(ImageReader paramImageReader, int paramInt);
  
  public abstract void sequenceComplete(ImageReader paramImageReader);
  
  public abstract void imageStarted(ImageReader paramImageReader, int paramInt);
  
  public abstract void imageProgress(ImageReader paramImageReader, float paramFloat);
  
  public abstract void imageComplete(ImageReader paramImageReader);
  
  public abstract void thumbnailStarted(ImageReader paramImageReader, int paramInt1, int paramInt2);
  
  public abstract void thumbnailProgress(ImageReader paramImageReader, float paramFloat);
  
  public abstract void thumbnailComplete(ImageReader paramImageReader);
  
  public abstract void readAborted(ImageReader paramImageReader);
}
