package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

abstract class MultipleGradientPaintContext
  implements PaintContext
{
  protected ColorModel model;
  private static ColorModel xrgbmodel = new DirectColorModel(24, 16711680, 65280, 255);
  protected static ColorModel cachedModel;
  protected static WeakReference<Raster> cached;
  protected Raster saved;
  protected MultipleGradientPaint.CycleMethod cycleMethod;
  protected MultipleGradientPaint.ColorSpaceType colorSpace;
  protected float a00;
  protected float a01;
  protected float a10;
  protected float a11;
  protected float a02;
  protected float a12;
  protected boolean isSimpleLookup;
  protected int fastGradientArraySize;
  protected int[] gradient;
  private int[][] gradients;
  private float[] normalizedIntervals;
  private float[] fractions;
  private int transparencyTest;
  private static final int[] SRGBtoLinearRGB = new int['Ā'];
  private static final int[] LinearRGBtoSRGB = new int['Ā'];
  protected static final int GRADIENT_SIZE = 256;
  protected static final int GRADIENT_SIZE_INDEX = 255;
  private static final int MAX_GRADIENT_ARRAY_SIZE = 5000;
  
  protected MultipleGradientPaintContext(MultipleGradientPaint paramMultipleGradientPaint, ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod, MultipleGradientPaint.ColorSpaceType paramColorSpaceType)
  {
    if (paramRectangle == null) {
      throw new NullPointerException("Device bounds cannot be null");
    }
    if (paramRectangle2D == null) {
      throw new NullPointerException("User bounds cannot be null");
    }
    if (paramAffineTransform == null) {
      throw new NullPointerException("Transform cannot be null");
    }
    if (paramRenderingHints == null) {
      throw new NullPointerException("RenderingHints cannot be null");
    }
    AffineTransform localAffineTransform;
    try
    {
      paramAffineTransform.invert();
      localAffineTransform = paramAffineTransform;
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
      localAffineTransform = new AffineTransform();
    }
    double[] arrayOfDouble = new double[6];
    localAffineTransform.getMatrix(arrayOfDouble);
    this.a00 = ((float)arrayOfDouble[0]);
    this.a10 = ((float)arrayOfDouble[1]);
    this.a01 = ((float)arrayOfDouble[2]);
    this.a11 = ((float)arrayOfDouble[3]);
    this.a02 = ((float)arrayOfDouble[4]);
    this.a12 = ((float)arrayOfDouble[5]);
    this.cycleMethod = paramCycleMethod;
    this.colorSpace = paramColorSpaceType;
    this.fractions = paramArrayOfFloat;
    Object localObject = paramMultipleGradientPaint.gradient != null ? (int[])paramMultipleGradientPaint.gradient.get() : null;
    int[][] arrayOfInt = paramMultipleGradientPaint.gradients != null ? (int[][])paramMultipleGradientPaint.gradients.get() : (int[][])null;
    if ((localObject == null) && (arrayOfInt == null))
    {
      calculateLookupData(paramArrayOfColor);
      paramMultipleGradientPaint.model = this.model;
      paramMultipleGradientPaint.normalizedIntervals = this.normalizedIntervals;
      paramMultipleGradientPaint.isSimpleLookup = this.isSimpleLookup;
      if (this.isSimpleLookup)
      {
        paramMultipleGradientPaint.fastGradientArraySize = this.fastGradientArraySize;
        paramMultipleGradientPaint.gradient = new SoftReference(this.gradient);
      }
      else
      {
        paramMultipleGradientPaint.gradients = new SoftReference(this.gradients);
      }
    }
    else
    {
      this.model = paramMultipleGradientPaint.model;
      this.normalizedIntervals = paramMultipleGradientPaint.normalizedIntervals;
      this.isSimpleLookup = paramMultipleGradientPaint.isSimpleLookup;
      this.gradient = localObject;
      this.fastGradientArraySize = paramMultipleGradientPaint.fastGradientArraySize;
      this.gradients = arrayOfInt;
    }
  }
  
  private void calculateLookupData(Color[] paramArrayOfColor)
  {
    Color[] arrayOfColor;
    if (this.colorSpace == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB)
    {
      arrayOfColor = new Color[paramArrayOfColor.length];
      for (i = 0; i < paramArrayOfColor.length; i++)
      {
        j = paramArrayOfColor[i].getRGB();
        k = j >>> 24;
        int m = SRGBtoLinearRGB[(j >> 16 & 0xFF)];
        int n = SRGBtoLinearRGB[(j >> 8 & 0xFF)];
        int i1 = SRGBtoLinearRGB[(j & 0xFF)];
        arrayOfColor[i] = new Color(m, n, i1, k);
      }
    }
    else
    {
      arrayOfColor = paramArrayOfColor;
    }
    this.normalizedIntervals = new float[this.fractions.length - 1];
    for (int i = 0; i < this.normalizedIntervals.length; i++) {
      this.normalizedIntervals[i] = (this.fractions[(i + 1)] - this.fractions[i]);
    }
    this.transparencyTest = -16777216;
    this.gradients = new int[this.normalizedIntervals.length][];
    float f = 1.0F;
    for (int j = 0; j < this.normalizedIntervals.length; j++) {
      f = f > this.normalizedIntervals[j] ? this.normalizedIntervals[j] : f;
    }
    j = 0;
    for (int k = 0; k < this.normalizedIntervals.length; k++) {
      j = (int)(j + this.normalizedIntervals[k] / f * 256.0F);
    }
    if (j > 5000) {
      calculateMultipleArrayGradient(arrayOfColor);
    } else {
      calculateSingleArrayGradient(arrayOfColor, f);
    }
    if (this.transparencyTest >>> 24 == 255) {
      this.model = xrgbmodel;
    } else {
      this.model = ColorModel.getRGBdefault();
    }
  }
  
  private void calculateSingleArrayGradient(Color[] paramArrayOfColor, float paramFloat)
  {
    this.isSimpleLookup = true;
    int k = 1;
    for (int m = 0; m < this.gradients.length; m++)
    {
      n = (int)(this.normalizedIntervals[m] / paramFloat * 255.0F);
      k += n;
      this.gradients[m] = new int[n];
      int i = paramArrayOfColor[m].getRGB();
      int j = paramArrayOfColor[(m + 1)].getRGB();
      interpolate(i, j, this.gradients[m]);
      this.transparencyTest &= i;
      this.transparencyTest &= j;
    }
    this.gradient = new int[k];
    m = 0;
    for (int n = 0; n < this.gradients.length; n++)
    {
      System.arraycopy(this.gradients[n], 0, this.gradient, m, this.gradients[n].length);
      m += this.gradients[n].length;
    }
    this.gradient[(this.gradient.length - 1)] = paramArrayOfColor[(paramArrayOfColor.length - 1)].getRGB();
    if (this.colorSpace == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB) {
      for (n = 0; n < this.gradient.length; n++) {
        this.gradient[n] = convertEntireColorLinearRGBtoSRGB(this.gradient[n]);
      }
    }
    this.fastGradientArraySize = (this.gradient.length - 1);
  }
  
  private void calculateMultipleArrayGradient(Color[] paramArrayOfColor)
  {
    this.isSimpleLookup = false;
    for (int k = 0; k < this.gradients.length; k++)
    {
      this.gradients[k] = new int['Ā'];
      int i = paramArrayOfColor[k].getRGB();
      int j = paramArrayOfColor[(k + 1)].getRGB();
      interpolate(i, j, this.gradients[k]);
      this.transparencyTest &= i;
      this.transparencyTest &= j;
    }
    if (this.colorSpace == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB) {
      for (k = 0; k < this.gradients.length; k++) {
        for (int m = 0; m < this.gradients[k].length; m++) {
          this.gradients[k][m] = convertEntireColorLinearRGBtoSRGB(this.gradients[k][m]);
        }
      }
    }
  }
  
  private void interpolate(int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    float f = 1.0F / paramArrayOfInt.length;
    int i = paramInt1 >> 24 & 0xFF;
    int j = paramInt1 >> 16 & 0xFF;
    int k = paramInt1 >> 8 & 0xFF;
    int m = paramInt1 & 0xFF;
    int n = (paramInt2 >> 24 & 0xFF) - i;
    int i1 = (paramInt2 >> 16 & 0xFF) - j;
    int i2 = (paramInt2 >> 8 & 0xFF) - k;
    int i3 = (paramInt2 & 0xFF) - m;
    for (int i4 = 0; i4 < paramArrayOfInt.length; i4++) {
      paramArrayOfInt[i4] = ((int)(i + i4 * n * f + 0.5D) << 24 | (int)(j + i4 * i1 * f + 0.5D) << 16 | (int)(k + i4 * i2 * f + 0.5D) << 8 | (int)(m + i4 * i3 * f + 0.5D));
    }
  }
  
  private int convertEntireColorLinearRGBtoSRGB(int paramInt)
  {
    int i = paramInt >> 24 & 0xFF;
    int j = paramInt >> 16 & 0xFF;
    int k = paramInt >> 8 & 0xFF;
    int m = paramInt & 0xFF;
    j = LinearRGBtoSRGB[j];
    k = LinearRGBtoSRGB[k];
    m = LinearRGBtoSRGB[m];
    return i << 24 | j << 16 | k << 8 | m;
  }
  
  protected final int indexIntoGradientsArrays(float paramFloat)
  {
    if (this.cycleMethod == MultipleGradientPaint.CycleMethod.NO_CYCLE)
    {
      if (paramFloat > 1.0F) {
        paramFloat = 1.0F;
      } else if (paramFloat < 0.0F) {
        paramFloat = 0.0F;
      }
    }
    else if (this.cycleMethod == MultipleGradientPaint.CycleMethod.REPEAT)
    {
      paramFloat -= (int)paramFloat;
      if (paramFloat < 0.0F) {
        paramFloat += 1.0F;
      }
    }
    else
    {
      if (paramFloat < 0.0F) {
        paramFloat = -paramFloat;
      }
      i = (int)paramFloat;
      paramFloat -= i;
      if ((i & 0x1) == 1) {
        paramFloat = 1.0F - paramFloat;
      }
    }
    if (this.isSimpleLookup) {
      return this.gradient[((int)(paramFloat * this.fastGradientArraySize))];
    }
    for (int i = 0; i < this.gradients.length; i++) {
      if (paramFloat < this.fractions[(i + 1)])
      {
        float f = paramFloat - this.fractions[i];
        int j = (int)(f / this.normalizedIntervals[i] * 255.0F);
        return this.gradients[i][j];
      }
    }
    return this.gradients[(this.gradients.length - 1)]['ÿ'];
  }
  
  private static int convertSRGBtoLinearRGB(int paramInt)
  {
    float f1 = paramInt / 255.0F;
    float f2;
    if (f1 <= 0.04045F) {
      f2 = f1 / 12.92F;
    } else {
      f2 = (float)Math.pow((f1 + 0.055D) / 1.055D, 2.4D);
    }
    return Math.round(f2 * 255.0F);
  }
  
  private static int convertLinearRGBtoSRGB(int paramInt)
  {
    float f1 = paramInt / 255.0F;
    float f2;
    if (f1 <= 0.0031308D) {
      f2 = f1 * 12.92F;
    } else {
      f2 = 1.055F * (float)Math.pow(f1, 0.4166666666666667D) - 0.055F;
    }
    return Math.round(f2 * 255.0F);
  }
  
  public final Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Raster localRaster = this.saved;
    if ((localRaster == null) || (localRaster.getWidth() < paramInt3) || (localRaster.getHeight() < paramInt4))
    {
      localRaster = getCachedRaster(this.model, paramInt3, paramInt4);
      this.saved = localRaster;
    }
    DataBufferInt localDataBufferInt = (DataBufferInt)localRaster.getDataBuffer();
    int[] arrayOfInt = localDataBufferInt.getData(0);
    int i = localDataBufferInt.getOffset();
    int j = ((SinglePixelPackedSampleModel)localRaster.getSampleModel()).getScanlineStride();
    int k = j - paramInt3;
    fillRaster(arrayOfInt, i, k, paramInt1, paramInt2, paramInt3, paramInt4);
    return localRaster;
  }
  
  protected abstract void fillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  private static synchronized Raster getCachedRaster(ColorModel paramColorModel, int paramInt1, int paramInt2)
  {
    if ((paramColorModel == cachedModel) && (cached != null))
    {
      Raster localRaster = (Raster)cached.get();
      if ((localRaster != null) && (localRaster.getWidth() >= paramInt1) && (localRaster.getHeight() >= paramInt2))
      {
        cached = null;
        return localRaster;
      }
    }
    return paramColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
  }
  
  private static synchronized void putCachedRaster(ColorModel paramColorModel, Raster paramRaster)
  {
    if (cached != null)
    {
      Raster localRaster = (Raster)cached.get();
      if (localRaster != null)
      {
        int i = localRaster.getWidth();
        int j = localRaster.getHeight();
        int k = paramRaster.getWidth();
        int m = paramRaster.getHeight();
        if ((i >= k) && (j >= m)) {
          return;
        }
        if (i * j >= k * m) {
          return;
        }
      }
    }
    cachedModel = paramColorModel;
    cached = new WeakReference(paramRaster);
  }
  
  public final void dispose()
  {
    if (this.saved != null)
    {
      putCachedRaster(this.model, this.saved);
      this.saved = null;
    }
  }
  
  public final ColorModel getColorModel()
  {
    return this.model;
  }
  
  static
  {
    for (int i = 0; i < 256; i++)
    {
      SRGBtoLinearRGB[i] = convertSRGBtoLinearRGB(i);
      LinearRGBtoSRGB[i] = convertLinearRGBtoSRGB(i);
    }
  }
}
