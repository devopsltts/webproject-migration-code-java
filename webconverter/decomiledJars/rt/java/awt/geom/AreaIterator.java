package java.awt.geom;

import java.util.NoSuchElementException;
import java.util.Vector;
import sun.awt.geom.Curve;

class AreaIterator
  implements PathIterator
{
  private AffineTransform transform;
  private Vector curves;
  private int index;
  private Curve prevcurve;
  private Curve thiscurve;
  
  public AreaIterator(Vector paramVector, AffineTransform paramAffineTransform)
  {
    this.curves = paramVector;
    this.transform = paramAffineTransform;
    if (paramVector.size() >= 1) {
      this.thiscurve = ((Curve)paramVector.get(0));
    }
  }
  
  public int getWindingRule()
  {
    return 1;
  }
  
  public boolean isDone()
  {
    return (this.prevcurve == null) && (this.thiscurve == null);
  }
  
  public void next()
  {
    if (this.prevcurve != null)
    {
      this.prevcurve = null;
    }
    else
    {
      this.prevcurve = this.thiscurve;
      this.index += 1;
      if (this.index < this.curves.size())
      {
        this.thiscurve = ((Curve)this.curves.get(this.index));
        if ((this.thiscurve.getOrder() != 0) && (this.prevcurve.getX1() == this.thiscurve.getX0()) && (this.prevcurve.getY1() == this.thiscurve.getY0())) {
          this.prevcurve = null;
        }
      }
      else
      {
        this.thiscurve = null;
      }
    }
  }
  
  public int currentSegment(float[] paramArrayOfFloat)
  {
    double[] arrayOfDouble = new double[6];
    int i = currentSegment(arrayOfDouble);
    int j = i == 3 ? 3 : i == 2 ? 2 : i == 4 ? 0 : 1;
    for (int k = 0; k < j * 2; k++) {
      paramArrayOfFloat[k] = ((float)arrayOfDouble[k]);
    }
    return i;
  }
  
  public int currentSegment(double[] paramArrayOfDouble)
  {
    int i;
    int j;
    if (this.prevcurve != null)
    {
      if ((this.thiscurve == null) || (this.thiscurve.getOrder() == 0)) {
        return 4;
      }
      paramArrayOfDouble[0] = this.thiscurve.getX0();
      paramArrayOfDouble[1] = this.thiscurve.getY0();
      i = 1;
      j = 1;
    }
    else
    {
      if (this.thiscurve == null) {
        throw new NoSuchElementException("area iterator out of bounds");
      }
      i = this.thiscurve.getSegment(paramArrayOfDouble);
      j = this.thiscurve.getOrder();
      if (j == 0) {
        j = 1;
      }
    }
    if (this.transform != null) {
      this.transform.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, j);
    }
    return i;
  }
}
