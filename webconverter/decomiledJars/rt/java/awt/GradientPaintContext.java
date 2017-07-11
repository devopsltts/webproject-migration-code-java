package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.lang.ref.WeakReference;
import sun.awt.image.IntegerComponentRaster;

class GradientPaintContext
  implements PaintContext
{
  static ColorModel xrgbmodel = new DirectColorModel(24, 16711680, 65280, 255);
  static ColorModel xbgrmodel = new DirectColorModel(24, 255, 65280, 16711680);
  static ColorModel cachedModel;
  static WeakReference<Raster> cached;
  double x1;
  double y1;
  double dx;
  double dy;
  boolean cyclic;
  int[] interp;
  Raster saved;
  ColorModel model;
  
  static synchronized Raster getCachedRaster(ColorModel paramColorModel, int paramInt1, int paramInt2)
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
  
  static synchronized void putCachedRaster(ColorModel paramColorModel, Raster paramRaster)
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
  
  public GradientPaintContext(ColorModel paramColorModel, Point2D paramPoint2D1, Point2D paramPoint2D2, AffineTransform paramAffineTransform, Color paramColor1, Color paramColor2, boolean paramBoolean)
  {
    Point2D.Double localDouble1 = new Point2D.Double(1.0D, 0.0D);
    Point2D.Double localDouble2 = new Point2D.Double(0.0D, 1.0D);
    try
    {
      AffineTransform localAffineTransform = paramAffineTransform.createInverse();
      localAffineTransform.deltaTransform(localDouble1, localDouble1);
      localAffineTransform.deltaTransform(localDouble2, localDouble2);
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
      localDouble1.setLocation(0.0D, 0.0D);
      localDouble2.setLocation(0.0D, 0.0D);
    }
    double d1 = paramPoint2D2.getX() - paramPoint2D1.getX();
    double d2 = paramPoint2D2.getY() - paramPoint2D1.getY();
    double d3 = d1 * d1 + d2 * d2;
    if (d3 <= Double.MIN_VALUE)
    {
      this.dx = 0.0D;
      this.dy = 0.0D;
    }
    else
    {
      this.dx = ((localDouble1.getX() * d1 + localDouble1.getY() * d2) / d3);
      this.dy = ((localDouble2.getX() * d1 + localDouble2.getY() * d2) / d3);
      if (paramBoolean)
      {
        this.dx %= 1.0D;
        this.dy %= 1.0D;
      }
      else if (this.dx < 0.0D)
      {
        localPoint2D = paramPoint2D1;
        paramPoint2D1 = paramPoint2D2;
        paramPoint2D2 = localPoint2D;
        Color localColor = paramColor1;
        paramColor1 = paramColor2;
        paramColor2 = localColor;
        this.dx = (-this.dx);
        this.dy = (-this.dy);
      }
    }
    Point2D localPoint2D = paramAffineTransform.transform(paramPoint2D1, null);
    this.x1 = localPoint2D.getX();
    this.y1 = localPoint2D.getY();
    this.cyclic = paramBoolean;
    int i = paramColor1.getRGB();
    int j = paramColor2.getRGB();
    int k = i >> 24 & 0xFF;
    int m = i >> 16 & 0xFF;
    int n = i >> 8 & 0xFF;
    int i1 = i & 0xFF;
    int i2 = (j >> 24 & 0xFF) - k;
    int i3 = (j >> 16 & 0xFF) - m;
    int i4 = (j >> 8 & 0xFF) - n;
    int i5 = (j & 0xFF) - i1;
    if ((k == 255) && (i2 == 0))
    {
      this.model = xrgbmodel;
      if ((paramColorModel instanceof DirectColorModel))
      {
        DirectColorModel localDirectColorModel = (DirectColorModel)paramColorModel;
        int i7 = localDirectColorModel.getAlphaMask();
        if (((i7 == 0) || (i7 == 255)) && (localDirectColorModel.getRedMask() == 255) && (localDirectColorModel.getGreenMask() == 65280) && (localDirectColorModel.getBlueMask() == 16711680))
        {
          this.model = xbgrmodel;
          i7 = m;
          m = i1;
          i1 = i7;
          i7 = i3;
          i3 = i5;
          i5 = i7;
        }
      }
    }
    else
    {
      this.model = ColorModel.getRGBdefault();
    }
    this.interp = new int[paramBoolean ? 'ȁ' : 'ā'];
    for (int i6 = 0; i6 <= 256; i6++)
    {
      float f = i6 / 256.0F;
      int i8 = (int)(k + i2 * f) << 24 | (int)(m + i3 * f) << 16 | (int)(n + i4 * f) << 8 | (int)(i1 + i5 * f);
      this.interp[i6] = i8;
      if (paramBoolean) {
        this.interp[(512 - i6)] = i8;
      }
    }
  }
  
  public void dispose()
  {
    if (this.saved != null)
    {
      putCachedRaster(this.model, this.saved);
      this.saved = null;
    }
  }
  
  public ColorModel getColorModel()
  {
    return this.model;
  }
  
  public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    double d = (paramInt1 - this.x1) * this.dx + (paramInt2 - this.y1) * this.dy;
    Raster localRaster = this.saved;
    if ((localRaster == null) || (localRaster.getWidth() < paramInt3) || (localRaster.getHeight() < paramInt4))
    {
      localRaster = getCachedRaster(this.model, paramInt3, paramInt4);
      this.saved = localRaster;
    }
    IntegerComponentRaster localIntegerComponentRaster = (IntegerComponentRaster)localRaster;
    int i = localIntegerComponentRaster.getDataOffset(0);
    int j = localIntegerComponentRaster.getScanlineStride() - paramInt3;
    int[] arrayOfInt = localIntegerComponentRaster.getDataStorage();
    if (this.cyclic) {
      cycleFillRaster(arrayOfInt, i, j, paramInt3, paramInt4, d, this.dx, this.dy);
    } else {
      clipFillRaster(arrayOfInt, i, j, paramInt3, paramInt4, d, this.dx, this.dy);
    }
    localIntegerComponentRaster.markDirty();
    return localRaster;
  }
  
  void cycleFillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    paramDouble1 %= 2.0D;
    int i = (int)(paramDouble1 * 1.073741824E9D) << 1;
    int j = (int)(-paramDouble2 * -2.147483648E9D);
    int k = (int)(-paramDouble3 * -2.147483648E9D);
    for (;;)
    {
      paramInt4--;
      if (paramInt4 < 0) {
        break;
      }
      int m = i;
      for (int n = paramInt3; n > 0; n--)
      {
        paramArrayOfInt[(paramInt1++)] = this.interp[(m >>> 23)];
        m += j;
      }
      paramInt1 += paramInt2;
      i += k;
    }
  }
  
  void clipFillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    for (;;)
    {
      
      if (paramInt4 < 0) {
        break;
      }
      double d = paramDouble1;
      int i = paramInt3;
      int j;
      if (d <= 0.0D)
      {
        j = this.interp[0];
        do
        {
          paramArrayOfInt[(paramInt1++)] = j;
          d += paramDouble2;
          i--;
        } while ((i > 0) && (d <= 0.0D));
      }
      while (d < 1.0D)
      {
        i--;
        if (i < 0) {
          break;
        }
        paramArrayOfInt[(paramInt1++)] = this.interp[((int)(d * 256.0D))];
        d += paramDouble2;
      }
      if (i > 0)
      {
        j = this.interp['Ā'];
        do
        {
          paramArrayOfInt[(paramInt1++)] = j;
          i--;
        } while (i > 0);
      }
      paramInt1 += paramInt2;
      paramDouble1 += paramDouble3;
    }
  }
}
