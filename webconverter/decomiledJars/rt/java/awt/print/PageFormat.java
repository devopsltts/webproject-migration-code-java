package java.awt.print;

public class PageFormat
  implements Cloneable
{
  public static final int LANDSCAPE = 0;
  public static final int PORTRAIT = 1;
  public static final int REVERSE_LANDSCAPE = 2;
  private Paper mPaper = new Paper();
  private int mOrientation = 1;
  
  public PageFormat() {}
  
  public Object clone()
  {
    PageFormat localPageFormat;
    try
    {
      localPageFormat = (PageFormat)super.clone();
      localPageFormat.mPaper = ((Paper)this.mPaper.clone());
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localCloneNotSupportedException.printStackTrace();
      localPageFormat = null;
    }
    return localPageFormat;
  }
  
  public double getWidth()
  {
    int i = getOrientation();
    double d;
    if (i == 1) {
      d = this.mPaper.getWidth();
    } else {
      d = this.mPaper.getHeight();
    }
    return d;
  }
  
  public double getHeight()
  {
    int i = getOrientation();
    double d;
    if (i == 1) {
      d = this.mPaper.getHeight();
    } else {
      d = this.mPaper.getWidth();
    }
    return d;
  }
  
  public double getImageableX()
  {
    double d;
    switch (getOrientation())
    {
    case 0: 
      d = this.mPaper.getHeight() - (this.mPaper.getImageableY() + this.mPaper.getImageableHeight());
      break;
    case 1: 
      d = this.mPaper.getImageableX();
      break;
    case 2: 
      d = this.mPaper.getImageableY();
      break;
    default: 
      throw new InternalError("unrecognized orientation");
    }
    return d;
  }
  
  public double getImageableY()
  {
    double d;
    switch (getOrientation())
    {
    case 0: 
      d = this.mPaper.getImageableX();
      break;
    case 1: 
      d = this.mPaper.getImageableY();
      break;
    case 2: 
      d = this.mPaper.getWidth() - (this.mPaper.getImageableX() + this.mPaper.getImageableWidth());
      break;
    default: 
      throw new InternalError("unrecognized orientation");
    }
    return d;
  }
  
  public double getImageableWidth()
  {
    double d;
    if (getOrientation() == 1) {
      d = this.mPaper.getImageableWidth();
    } else {
      d = this.mPaper.getImageableHeight();
    }
    return d;
  }
  
  public double getImageableHeight()
  {
    double d;
    if (getOrientation() == 1) {
      d = this.mPaper.getImageableHeight();
    } else {
      d = this.mPaper.getImageableWidth();
    }
    return d;
  }
  
  public Paper getPaper()
  {
    return (Paper)this.mPaper.clone();
  }
  
  public void setPaper(Paper paramPaper)
  {
    this.mPaper = ((Paper)paramPaper.clone());
  }
  
  public void setOrientation(int paramInt)
    throws IllegalArgumentException
  {
    if ((0 <= paramInt) && (paramInt <= 2)) {
      this.mOrientation = paramInt;
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public double[] getMatrix()
  {
    double[] arrayOfDouble = new double[6];
    switch (this.mOrientation)
    {
    case 0: 
      arrayOfDouble[0] = 0.0D;
      arrayOfDouble[1] = -1.0D;
      arrayOfDouble[2] = 1.0D;
      arrayOfDouble[3] = 0.0D;
      arrayOfDouble[4] = 0.0D;
      arrayOfDouble[5] = this.mPaper.getHeight();
      break;
    case 1: 
      arrayOfDouble[0] = 1.0D;
      arrayOfDouble[1] = 0.0D;
      arrayOfDouble[2] = 0.0D;
      arrayOfDouble[3] = 1.0D;
      arrayOfDouble[4] = 0.0D;
      arrayOfDouble[5] = 0.0D;
      break;
    case 2: 
      arrayOfDouble[0] = 0.0D;
      arrayOfDouble[1] = 1.0D;
      arrayOfDouble[2] = -1.0D;
      arrayOfDouble[3] = 0.0D;
      arrayOfDouble[4] = this.mPaper.getWidth();
      arrayOfDouble[5] = 0.0D;
      break;
    default: 
      throw new IllegalArgumentException();
    }
    return arrayOfDouble;
  }
}
