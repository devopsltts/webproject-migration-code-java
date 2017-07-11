package sun.awt.geom;

public abstract interface PathConsumer2D
{
  public abstract void moveTo(float paramFloat1, float paramFloat2);
  
  public abstract void lineTo(float paramFloat1, float paramFloat2);
  
  public abstract void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  public abstract void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);
  
  public abstract void closePath();
  
  public abstract void pathDone();
  
  public abstract long getNativeConsumer();
}
