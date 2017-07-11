package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

final class RadialGradientPaintContext
  extends MultipleGradientPaintContext
{
  private boolean isSimpleFocus = false;
  private boolean isNonCyclic = false;
  private float radius;
  private float centerX;
  private float centerY;
  private float focusX;
  private float focusY;
  private float radiusSq;
  private float constA;
  private float constB;
  private float gDeltaDelta;
  private float trivial;
  private static final float SCALEBACK = 0.99F;
  private static final int SQRT_LUT_SIZE = 2048;
  private static float[] sqrtLut = new float['ࠁ'];
  
  RadialGradientPaintContext(RadialGradientPaint paramRadialGradientPaint, ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod, MultipleGradientPaint.ColorSpaceType paramColorSpaceType)
  {
    super(paramRadialGradientPaint, paramColorModel, paramRectangle, paramRectangle2D, paramAffineTransform, paramRenderingHints, paramArrayOfFloat, paramArrayOfColor, paramCycleMethod, paramColorSpaceType);
    this.centerX = paramFloat1;
    this.centerY = paramFloat2;
    this.focusX = paramFloat4;
    this.focusY = paramFloat5;
    this.radius = paramFloat3;
    this.isSimpleFocus = ((this.focusX == this.centerX) && (this.focusY == this.centerY));
    this.isNonCyclic = (paramCycleMethod == MultipleGradientPaint.CycleMethod.NO_CYCLE);
    this.radiusSq = (this.radius * this.radius);
    float f1 = this.focusX - this.centerX;
    float f2 = this.focusY - this.centerY;
    double d = f1 * f1 + f2 * f2;
    if (d > this.radiusSq * 0.99F)
    {
      float f3 = (float)Math.sqrt(this.radiusSq * 0.99F / d);
      f1 *= f3;
      f2 *= f3;
      this.focusX = (this.centerX + f1);
      this.focusY = (this.centerY + f2);
    }
    this.trivial = ((float)Math.sqrt(this.radiusSq - f1 * f1));
    this.constA = (this.a02 - this.centerX);
    this.constB = (this.a12 - this.centerY);
    this.gDeltaDelta = (2.0F * (this.a00 * this.a00 + this.a10 * this.a10) / this.radiusSq);
  }
  
  protected void fillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if ((this.isSimpleFocus) && (this.isNonCyclic) && (this.isSimpleLookup)) {
      simpleNonCyclicFillRaster(paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    } else {
      cyclicCircularGradientFillRaster(paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
  }
  
  private void simpleNonCyclicFillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    float f1 = this.a00 * paramInt3 + this.a01 * paramInt4 + this.constA;
    float f2 = this.a10 * paramInt3 + this.a11 * paramInt4 + this.constB;
    float f3 = this.gDeltaDelta;
    paramInt2 += paramInt5;
    int i = this.gradient[this.fastGradientArraySize];
    for (int j = 0; j < paramInt6; j++)
    {
      float f4 = (f1 * f1 + f2 * f2) / this.radiusSq;
      float f5 = 2.0F * (this.a00 * f1 + this.a10 * f2) / this.radiusSq + f3 / 2.0F;
      for (int k = 0; (k < paramInt5) && (f4 >= 1.0F); k++)
      {
        paramArrayOfInt[(paramInt1 + k)] = i;
        f4 += f5;
        f5 += f3;
      }
      while ((k < paramInt5) && (f4 < 1.0F))
      {
        int m;
        if (f4 <= 0.0F)
        {
          m = 0;
        }
        else
        {
          float f6 = f4 * 2048.0F;
          int n = (int)f6;
          float f7 = sqrtLut[n];
          float f8 = sqrtLut[(n + 1)] - f7;
          f6 = f7 + (f6 - n) * f8;
          m = (int)(f6 * this.fastGradientArraySize);
        }
        paramArrayOfInt[(paramInt1 + k)] = this.gradient[m];
        f4 += f5;
        f5 += f3;
        k++;
      }
      while (k < paramInt5)
      {
        paramArrayOfInt[(paramInt1 + k)] = i;
        k++;
      }
      paramInt1 += paramInt2;
      f1 += this.a01;
      f2 += this.a11;
    }
  }
  
  private void cyclicCircularGradientFillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    double d1 = -this.radiusSq + this.centerX * this.centerX + this.centerY * this.centerY;
    float f1 = this.a00 * paramInt3 + this.a01 * paramInt4 + this.a02;
    float f2 = this.a10 * paramInt3 + this.a11 * paramInt4 + this.a12;
    float f3 = 2.0F * this.centerY;
    float f4 = -2.0F * this.centerX;
    int i = paramInt1;
    int j = paramInt5 + paramInt2;
    for (int k = 0; k < paramInt6; k++)
    {
      float f11 = this.a01 * k + f1;
      float f12 = this.a11 * k + f2;
      for (int m = 0; m < paramInt5; m++)
      {
        double d7;
        double d8;
        if (f11 == this.focusX)
        {
          d7 = this.focusX;
          d8 = this.centerY;
          d8 += (f12 > this.focusY ? this.trivial : -this.trivial);
        }
        else
        {
          double d5 = (f12 - this.focusY) / (f11 - this.focusX);
          double d6 = f12 - d5 * f11;
          double d2 = d5 * d5 + 1.0D;
          double d3 = f4 + -2.0D * d5 * (this.centerY - d6);
          double d4 = d1 + d6 * (d6 - f3);
          float f6 = (float)Math.sqrt(d3 * d3 - 4.0D * d2 * d4);
          d7 = -d3;
          d7 += (f11 < this.focusX ? -f6 : f6);
          d7 /= 2.0D * d2;
          d8 = d5 * d7 + d6;
        }
        float f9 = f11 - this.focusX;
        f9 *= f9;
        float f10 = f12 - this.focusY;
        f10 *= f10;
        float f7 = f9 + f10;
        f9 = (float)d7 - this.focusX;
        f9 *= f9;
        f10 = (float)d8 - this.focusY;
        f10 *= f10;
        float f8 = f9 + f10;
        float f5 = (float)Math.sqrt(f7 / f8);
        paramArrayOfInt[(i + m)] = indexIntoGradientsArrays(f5);
        f11 += this.a00;
        f12 += this.a10;
      }
      i += j;
    }
  }
  
  static
  {
    for (int i = 0; i < sqrtLut.length; i++) {
      sqrtLut[i] = ((float)Math.sqrt(i / 2048.0F));
    }
  }
}
