package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import sun.java2d.SunGraphics2D;

public class PixelToParallelogramConverter
  extends PixelToShapeConverter
  implements ShapeDrawPipe
{
  ParallelogramPipe outrenderer;
  double minPenSize;
  double normPosition;
  double normRoundingBias;
  boolean adjustfill;
  
  public PixelToParallelogramConverter(ShapeDrawPipe paramShapeDrawPipe, ParallelogramPipe paramParallelogramPipe, double paramDouble1, double paramDouble2, boolean paramBoolean)
  {
    super(paramShapeDrawPipe);
    this.outrenderer = paramParallelogramPipe;
    this.minPenSize = paramDouble1;
    this.normPosition = paramDouble2;
    this.normRoundingBias = (0.5D - paramDouble2);
    this.adjustfill = paramBoolean;
  }
  
  public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!drawGeneralLine(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4)) {
      super.drawLine(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 >= 0) && (paramInt4 >= 0))
    {
      if (paramSunGraphics2D.strokeState < 3)
      {
        BasicStroke localBasicStroke = (BasicStroke)paramSunGraphics2D.stroke;
        if ((paramInt3 > 0) && (paramInt4 > 0))
        {
          if ((localBasicStroke.getLineJoin() == 0) && (localBasicStroke.getDashArray() == null))
          {
            double d = localBasicStroke.getLineWidth();
            drawRectangle(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, d);
          }
        }
        else
        {
          drawLine(paramSunGraphics2D, paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
          return;
        }
      }
      super.drawRect(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 > 0) && (paramInt4 > 0)) {
      fillRectangle(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    if (paramSunGraphics2D.strokeState < 3)
    {
      BasicStroke localBasicStroke = (BasicStroke)paramSunGraphics2D.stroke;
      Object localObject;
      if ((paramShape instanceof Rectangle2D))
      {
        if ((localBasicStroke.getLineJoin() == 0) && (localBasicStroke.getDashArray() == null))
        {
          localObject = (Rectangle2D)paramShape;
          double d1 = ((Rectangle2D)localObject).getWidth();
          double d2 = ((Rectangle2D)localObject).getHeight();
          double d3 = ((Rectangle2D)localObject).getX();
          double d4 = ((Rectangle2D)localObject).getY();
          if ((d1 >= 0.0D) && (d2 >= 0.0D))
          {
            double d5 = localBasicStroke.getLineWidth();
            drawRectangle(paramSunGraphics2D, d3, d4, d1, d2, d5);
          }
        }
      }
      else if ((paramShape instanceof Line2D))
      {
        localObject = (Line2D)paramShape;
        if (drawGeneralLine(paramSunGraphics2D, ((Line2D)localObject).getX1(), ((Line2D)localObject).getY1(), ((Line2D)localObject).getX2(), ((Line2D)localObject).getY2())) {
          return;
        }
      }
    }
    this.outpipe.draw(paramSunGraphics2D, paramShape);
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    if ((paramShape instanceof Rectangle2D))
    {
      Rectangle2D localRectangle2D = (Rectangle2D)paramShape;
      double d1 = localRectangle2D.getWidth();
      double d2 = localRectangle2D.getHeight();
      if ((d1 > 0.0D) && (d2 > 0.0D))
      {
        double d3 = localRectangle2D.getX();
        double d4 = localRectangle2D.getY();
        fillRectangle(paramSunGraphics2D, d3, d4, d1, d2);
      }
      return;
    }
    this.outpipe.fill(paramSunGraphics2D, paramShape);
  }
  
  static double len(double paramDouble1, double paramDouble2)
  {
    return paramDouble2 == 0.0D ? Math.abs(paramDouble1) : paramDouble1 == 0.0D ? Math.abs(paramDouble2) : Math.sqrt(paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2);
  }
  
  double normalize(double paramDouble)
  {
    return Math.floor(paramDouble + this.normRoundingBias) + this.normPosition;
  }
  
  public boolean drawGeneralLine(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((paramSunGraphics2D.strokeState == 3) || (paramSunGraphics2D.strokeState == 1)) {
      return false;
    }
    BasicStroke localBasicStroke = (BasicStroke)paramSunGraphics2D.stroke;
    int i = localBasicStroke.getEndCap();
    if ((i == 1) || (localBasicStroke.getDashArray() != null)) {
      return false;
    }
    double d1 = localBasicStroke.getLineWidth();
    double d2 = paramDouble3 - paramDouble1;
    double d3 = paramDouble4 - paramDouble2;
    double d4;
    double d5;
    double d6;
    double d7;
    switch (paramSunGraphics2D.transformState)
    {
    case 3: 
    case 4: 
      double[] arrayOfDouble1 = { paramDouble1, paramDouble2, paramDouble3, paramDouble4 };
      paramSunGraphics2D.transform.transform(arrayOfDouble1, 0, arrayOfDouble1, 0, 2);
      d4 = arrayOfDouble1[0];
      d5 = arrayOfDouble1[1];
      d6 = arrayOfDouble1[2];
      d7 = arrayOfDouble1[3];
      break;
    case 1: 
    case 2: 
      double d8 = paramSunGraphics2D.transform.getTranslateX();
      double d10 = paramSunGraphics2D.transform.getTranslateY();
      d4 = paramDouble1 + d8;
      d5 = paramDouble2 + d10;
      d6 = paramDouble3 + d8;
      d7 = paramDouble4 + d10;
      break;
    case 0: 
      d4 = paramDouble1;
      d5 = paramDouble2;
      d6 = paramDouble3;
      d7 = paramDouble4;
      break;
    default: 
      throw new InternalError("unknown TRANSFORM state...");
    }
    if (paramSunGraphics2D.strokeHint != 2)
    {
      if ((paramSunGraphics2D.strokeState == 0) && ((this.outrenderer instanceof PixelDrawPipe)))
      {
        int j = (int)Math.floor(d4 - paramSunGraphics2D.transX);
        int k = (int)Math.floor(d5 - paramSunGraphics2D.transY);
        int m = (int)Math.floor(d6 - paramSunGraphics2D.transX);
        int n = (int)Math.floor(d7 - paramSunGraphics2D.transY);
        ((PixelDrawPipe)this.outrenderer).drawLine(paramSunGraphics2D, j, k, m, n);
        return true;
      }
      d4 = normalize(d4);
      d5 = normalize(d5);
      d6 = normalize(d6);
      d7 = normalize(d7);
    }
    if (paramSunGraphics2D.transformState >= 3)
    {
      d9 = len(d2, d3);
      if (d9 == 0.0D) {
        d2 = d9 = 1.0D;
      }
      double[] arrayOfDouble2 = { d3 / d9, -d2 / d9 };
      paramSunGraphics2D.transform.deltaTransform(arrayOfDouble2, 0, arrayOfDouble2, 0, 1);
      d1 *= len(arrayOfDouble2[0], arrayOfDouble2[1]);
    }
    d1 = Math.max(d1, this.minPenSize);
    d2 = d6 - d4;
    d3 = d7 - d5;
    double d9 = len(d2, d3);
    double d11;
    double d12;
    if (d9 == 0.0D)
    {
      if (i == 0) {
        return true;
      }
      d11 = d1;
      d12 = 0.0D;
    }
    else
    {
      d11 = d1 * d2 / d9;
      d12 = d1 * d3 / d9;
    }
    double d13 = d4 + d12 / 2.0D;
    double d14 = d5 - d11 / 2.0D;
    if (i == 2)
    {
      d13 -= d11 / 2.0D;
      d14 -= d12 / 2.0D;
      d2 += d11;
      d3 += d12;
    }
    this.outrenderer.fillParallelogram(paramSunGraphics2D, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d13, d14, -d12, d11, d2, d3);
    return true;
  }
  
  public void fillRectangle(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    AffineTransform localAffineTransform = paramSunGraphics2D.transform;
    double d3 = localAffineTransform.getScaleX();
    double d4 = localAffineTransform.getShearY();
    double d5 = localAffineTransform.getShearX();
    double d6 = localAffineTransform.getScaleY();
    double d1 = paramDouble1 * d3 + paramDouble2 * d5 + localAffineTransform.getTranslateX();
    double d2 = paramDouble1 * d4 + paramDouble2 * d6 + localAffineTransform.getTranslateY();
    d3 *= paramDouble3;
    d4 *= paramDouble3;
    d5 *= paramDouble4;
    d6 *= paramDouble4;
    if ((this.adjustfill) && (paramSunGraphics2D.strokeState < 3) && (paramSunGraphics2D.strokeHint != 2))
    {
      double d7 = normalize(d1);
      double d8 = normalize(d2);
      d3 = normalize(d1 + d3) - d7;
      d4 = normalize(d2 + d4) - d8;
      d5 = normalize(d1 + d5) - d7;
      d6 = normalize(d2 + d6) - d8;
      d1 = d7;
      d2 = d8;
    }
    this.outrenderer.fillParallelogram(paramSunGraphics2D, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4, d1, d2, d3, d4, d5, d6);
  }
  
  public void drawRectangle(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5)
  {
    AffineTransform localAffineTransform = paramSunGraphics2D.transform;
    double d3 = localAffineTransform.getScaleX();
    double d4 = localAffineTransform.getShearY();
    double d5 = localAffineTransform.getShearX();
    double d6 = localAffineTransform.getScaleY();
    double d1 = paramDouble1 * d3 + paramDouble2 * d5 + localAffineTransform.getTranslateX();
    double d2 = paramDouble1 * d4 + paramDouble2 * d6 + localAffineTransform.getTranslateY();
    double d7 = len(d3, d4) * paramDouble5;
    double d8 = len(d5, d6) * paramDouble5;
    d3 *= paramDouble3;
    d4 *= paramDouble3;
    d5 *= paramDouble4;
    d6 *= paramDouble4;
    if ((paramSunGraphics2D.strokeState < 3) && (paramSunGraphics2D.strokeHint != 2))
    {
      d9 = normalize(d1);
      d10 = normalize(d2);
      d3 = normalize(d1 + d3) - d9;
      d4 = normalize(d2 + d4) - d10;
      d5 = normalize(d1 + d5) - d9;
      d6 = normalize(d2 + d6) - d10;
      d1 = d9;
      d2 = d10;
    }
    d7 = Math.max(d7, this.minPenSize);
    d8 = Math.max(d8, this.minPenSize);
    double d9 = len(d3, d4);
    double d10 = len(d5, d6);
    if ((d7 >= d9) || (d8 >= d10)) {
      fillOuterParallelogram(paramSunGraphics2D, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4, d1, d2, d3, d4, d5, d6, d9, d10, d7, d8);
    } else {
      this.outrenderer.drawParallelogram(paramSunGraphics2D, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4, d1, d2, d3, d4, d5, d6, d7 / d9, d8 / d10);
    }
  }
  
  public void fillOuterParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12, double paramDouble13, double paramDouble14)
  {
    double d1 = paramDouble7 / paramDouble11;
    double d2 = paramDouble8 / paramDouble11;
    double d3 = paramDouble9 / paramDouble12;
    double d4 = paramDouble10 / paramDouble12;
    if (paramDouble11 == 0.0D)
    {
      if (paramDouble12 == 0.0D)
      {
        d3 = 0.0D;
        d4 = 1.0D;
      }
      d1 = d4;
      d2 = -d3;
    }
    else if (paramDouble12 == 0.0D)
    {
      d3 = d2;
      d4 = -d1;
    }
    d1 *= paramDouble13;
    d2 *= paramDouble13;
    d3 *= paramDouble14;
    d4 *= paramDouble14;
    paramDouble5 -= (d1 + d3) / 2.0D;
    paramDouble6 -= (d2 + d4) / 2.0D;
    paramDouble7 += d1;
    paramDouble8 += d2;
    paramDouble9 += d3;
    paramDouble10 += d4;
    this.outrenderer.fillParallelogram(paramSunGraphics2D, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10);
  }
}
