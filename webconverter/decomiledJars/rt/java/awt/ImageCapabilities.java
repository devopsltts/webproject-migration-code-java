package java.awt;

public class ImageCapabilities
  implements Cloneable
{
  private boolean accelerated = false;
  
  public ImageCapabilities(boolean paramBoolean)
  {
    this.accelerated = paramBoolean;
  }
  
  public boolean isAccelerated()
  {
    return this.accelerated;
  }
  
  public boolean isTrueVolatile()
  {
    return false;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
}
