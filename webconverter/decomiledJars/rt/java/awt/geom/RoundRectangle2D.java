package java.awt.geom;

import java.io.Serializable;

public abstract class RoundRectangle2D
  extends RectangularShape
{
  protected RoundRectangle2D() {}
  
  public abstract double getArcWidth();
  
  public abstract double getArcHeight();
  
  public abstract void setRoundRect(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
  
  public void setRoundRect(RoundRectangle2D paramRoundRectangle2D)
  {
    setRoundRect(paramRoundRectangle2D.getX(), paramRoundRectangle2D.getY(), paramRoundRectangle2D.getWidth(), paramRoundRectangle2D.getHeight(), paramRoundRectangle2D.getArcWidth(), paramRoundRectangle2D.getArcHeight());
  }
  
  public void setFrame(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    setRoundRect(paramDouble1, paramDouble2, paramDouble3, paramDouble4, getArcWidth(), getArcHeight());
  }
  
  public boolean contains(double paramDouble1, double paramDouble2)
  {
    if (isEmpty()) {
      return false;
    }
    double d1 = getX();
    double d2 = getY();
    double d3 = d1 + getWidth();
    double d4 = d2 + getHeight();
    if ((paramDouble1 < d1) || (paramDouble2 < d2) || (paramDouble1 >= d3) || (paramDouble2 >= d4)) {
      return false;
    }
    double d5 = Math.min(getWidth(), Math.abs(getArcWidth())) / 2.0D;
    double d6 = Math.min(getHeight(), Math.abs(getArcHeight())) / 2.0D;
    if ((paramDouble1 >= d1 += d5) && (paramDouble1 < (d1 = d3 - d5))) {
      return true;
    }
    if ((paramDouble2 >= d2 += d6) && (paramDouble2 < (d2 = d4 - d6))) {
      return true;
    }
    paramDouble1 = (paramDouble1 - d1) / d5;
    paramDouble2 = (paramDouble2 - d2) / d6;
    return paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2 <= 1.0D;
  }
  
  private int classify(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if (paramDouble1 < paramDouble2) {
      return 0;
    }
    if (paramDouble1 < paramDouble2 + paramDouble4) {
      return 1;
    }
    if (paramDouble1 < paramDouble3 - paramDouble4) {
      return 2;
    }
    if (paramDouble1 < paramDouble3) {
      return 3;
    }
    return 4;
  }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((isEmpty()) || (paramDouble3 <= 0.0D) || (paramDouble4 <= 0.0D)) {
      return false;
    }
    double d1 = getX();
    double d2 = getY();
    double d3 = d1 + getWidth();
    double d4 = d2 + getHeight();
    if ((paramDouble1 + paramDouble3 <= d1) || (paramDouble1 >= d3) || (paramDouble2 + paramDouble4 <= d2) || (paramDouble2 >= d4)) {
      return false;
    }
    double d5 = Math.min(getWidth(), Math.abs(getArcWidth())) / 2.0D;
    double d6 = Math.min(getHeight(), Math.abs(getArcHeight())) / 2.0D;
    int i = classify(paramDouble1, d1, d3, d5);
    int j = classify(paramDouble1 + paramDouble3, d1, d3, d5);
    int k = classify(paramDouble2, d2, d4, d6);
    int m = classify(paramDouble2 + paramDouble4, d2, d4, d6);
    if ((i == 2) || (j == 2) || (k == 2) || (m == 2)) {
      return true;
    }
    if (((i < 2) && (j > 2)) || ((k < 2) && (m > 2))) {
      return true;
    }
    paramDouble1 = paramDouble1 -= d3 - d5;
    paramDouble2 = paramDouble2 -= d4 - d6;
    paramDouble1 /= d5;
    paramDouble2 /= d6;
    return paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2 <= 1.0D;
  }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((isEmpty()) || (paramDouble3 <= 0.0D) || (paramDouble4 <= 0.0D)) {
      return false;
    }
    return (contains(paramDouble1, paramDouble2)) && (contains(paramDouble1 + paramDouble3, paramDouble2)) && (contains(paramDouble1, paramDouble2 + paramDouble4)) && (contains(paramDouble1 + paramDouble3, paramDouble2 + paramDouble4));
  }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform)
  {
    return new RoundRectIterator(this, paramAffineTransform);
  }
  
  public int hashCode()
  {
    long l = Double.doubleToLongBits(getX());
    l += Double.doubleToLongBits(getY()) * 37L;
    l += Double.doubleToLongBits(getWidth()) * 43L;
    l += Double.doubleToLongBits(getHeight()) * 47L;
    l += Double.doubleToLongBits(getArcWidth()) * 53L;
    l += Double.doubleToLongBits(getArcHeight()) * 59L;
    return (int)l ^ (int)(l >> 32);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof RoundRectangle2D))
    {
      RoundRectangle2D localRoundRectangle2D = (RoundRectangle2D)paramObject;
      return (getX() == localRoundRectangle2D.getX()) && (getY() == localRoundRectangle2D.getY()) && (getWidth() == localRoundRectangle2D.getWidth()) && (getHeight() == localRoundRectangle2D.getHeight()) && (getArcWidth() == localRoundRectangle2D.getArcWidth()) && (getArcHeight() == localRoundRectangle2D.getArcHeight());
    }
    return false;
  }
  
  public static class Double
    extends RoundRectangle2D
    implements Serializable
  {
    public double x;
    public double y;
    public double width;
    public double height;
    public double arcwidth;
    public double archeight;
    private static final long serialVersionUID = 1048939333485206117L;
    
    public Double() {}
    
    public Double(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
    {
      setRoundRect(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6);
    }
    
    public double getX()
    {
      return this.x;
    }
    
    public double getY()
    {
      return this.y;
    }
    
    public double getWidth()
    {
      return this.width;
    }
    
    public double getHeight()
    {
      return this.height;
    }
    
    public double getArcWidth()
    {
      return this.arcwidth;
    }
    
    public double getArcHeight()
    {
      return this.archeight;
    }
    
    public boolean isEmpty()
    {
      return (this.width <= 0.0D) || (this.height <= 0.0D);
    }
    
    public void setRoundRect(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
    {
      this.x = paramDouble1;
      this.y = paramDouble2;
      this.width = paramDouble3;
      this.height = paramDouble4;
      this.arcwidth = paramDouble5;
      this.archeight = paramDouble6;
    }
    
    public void setRoundRect(RoundRectangle2D paramRoundRectangle2D)
    {
      this.x = paramRoundRectangle2D.getX();
      this.y = paramRoundRectangle2D.getY();
      this.width = paramRoundRectangle2D.getWidth();
      this.height = paramRoundRectangle2D.getHeight();
      this.arcwidth = paramRoundRectangle2D.getArcWidth();
      this.archeight = paramRoundRectangle2D.getArcHeight();
    }
    
    public Rectangle2D getBounds2D()
    {
      return new Rectangle2D.Double(this.x, this.y, this.width, this.height);
    }
  }
  
  public static class Float
    extends RoundRectangle2D
    implements Serializable
  {
    public float x;
    public float y;
    public float width;
    public float height;
    public float arcwidth;
    public float archeight;
    private static final long serialVersionUID = -3423150618393866922L;
    
    public Float() {}
    
    public Float(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    {
      setRoundRect(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
    }
    
    public double getX()
    {
      return this.x;
    }
    
    public double getY()
    {
      return this.y;
    }
    
    public double getWidth()
    {
      return this.width;
    }
    
    public double getHeight()
    {
      return this.height;
    }
    
    public double getArcWidth()
    {
      return this.arcwidth;
    }
    
    public double getArcHeight()
    {
      return this.archeight;
    }
    
    public boolean isEmpty()
    {
      return (this.width <= 0.0F) || (this.height <= 0.0F);
    }
    
    public void setRoundRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    {
      this.x = paramFloat1;
      this.y = paramFloat2;
      this.width = paramFloat3;
      this.height = paramFloat4;
      this.arcwidth = paramFloat5;
      this.archeight = paramFloat6;
    }
    
    public void setRoundRect(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
    {
      this.x = ((float)paramDouble1);
      this.y = ((float)paramDouble2);
      this.width = ((float)paramDouble3);
      this.height = ((float)paramDouble4);
      this.arcwidth = ((float)paramDouble5);
      this.archeight = ((float)paramDouble6);
    }
    
    public void setRoundRect(RoundRectangle2D paramRoundRectangle2D)
    {
      this.x = ((float)paramRoundRectangle2D.getX());
      this.y = ((float)paramRoundRectangle2D.getY());
      this.width = ((float)paramRoundRectangle2D.getWidth());
      this.height = ((float)paramRoundRectangle2D.getHeight());
      this.arcwidth = ((float)paramRoundRectangle2D.getArcWidth());
      this.archeight = ((float)paramRoundRectangle2D.getArcHeight());
    }
    
    public Rectangle2D getBounds2D()
    {
      return new Rectangle2D.Float(this.x, this.y, this.width, this.height);
    }
  }
}
