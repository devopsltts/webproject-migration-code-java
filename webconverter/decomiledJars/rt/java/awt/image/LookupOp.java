package java.awt.image;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import sun.awt.image.ImagingLib;

public class LookupOp
  implements BufferedImageOp, RasterOp
{
  private LookupTable ltable;
  private int numComponents;
  RenderingHints hints;
  
  public LookupOp(LookupTable paramLookupTable, RenderingHints paramRenderingHints)
  {
    this.ltable = paramLookupTable;
    this.hints = paramRenderingHints;
    this.numComponents = this.ltable.getNumComponents();
  }
  
  public final LookupTable getTable()
  {
    return this.ltable;
  }
  
  public final BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2)
  {
    ColorModel localColorModel1 = paramBufferedImage1.getColorModel();
    int i = localColorModel1.getNumColorComponents();
    if ((localColorModel1 instanceof IndexColorModel)) {
      throw new IllegalArgumentException("LookupOp cannot be performed on an indexed image");
    }
    int j = this.ltable.getNumComponents();
    if ((j != 1) && (j != localColorModel1.getNumComponents()) && (j != localColorModel1.getNumColorComponents())) {
      throw new IllegalArgumentException("Number of arrays in the  lookup table (" + j + " is not compatible with the " + " src image: " + paramBufferedImage1);
    }
    int k = 0;
    int m = paramBufferedImage1.getWidth();
    int n = paramBufferedImage1.getHeight();
    ColorModel localColorModel2;
    if (paramBufferedImage2 == null)
    {
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      localColorModel2 = localColorModel1;
    }
    else
    {
      if (m != paramBufferedImage2.getWidth()) {
        throw new IllegalArgumentException("Src width (" + m + ") not equal to dst width (" + paramBufferedImage2.getWidth() + ")");
      }
      if (n != paramBufferedImage2.getHeight()) {
        throw new IllegalArgumentException("Src height (" + n + ") not equal to dst height (" + paramBufferedImage2.getHeight() + ")");
      }
      localColorModel2 = paramBufferedImage2.getColorModel();
      if (localColorModel1.getColorSpace().getType() != localColorModel2.getColorSpace().getType())
      {
        k = 1;
        paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      }
    }
    BufferedImage localBufferedImage = paramBufferedImage2;
    Object localObject;
    if (ImagingLib.filter(this, paramBufferedImage1, paramBufferedImage2) == null)
    {
      localObject = paramBufferedImage1.getRaster();
      WritableRaster localWritableRaster = paramBufferedImage2.getRaster();
      int i1;
      int i2;
      if ((localColorModel1.hasAlpha()) && ((i - 1 == j) || (j == 1)))
      {
        i1 = ((WritableRaster)localObject).getMinX();
        i2 = ((WritableRaster)localObject).getMinY();
        int[] arrayOfInt1 = new int[i - 1];
        for (int i4 = 0; i4 < i - 1; i4++) {
          arrayOfInt1[i4] = i4;
        }
        localObject = ((WritableRaster)localObject).createWritableChild(i1, i2, ((WritableRaster)localObject).getWidth(), ((WritableRaster)localObject).getHeight(), i1, i2, arrayOfInt1);
      }
      if (localColorModel2.hasAlpha())
      {
        i1 = localWritableRaster.getNumBands();
        if ((i1 - 1 == j) || (j == 1))
        {
          i2 = localWritableRaster.getMinX();
          int i3 = localWritableRaster.getMinY();
          int[] arrayOfInt2 = new int[i - 1];
          for (int i5 = 0; i5 < i - 1; i5++) {
            arrayOfInt2[i5] = i5;
          }
          localWritableRaster = localWritableRaster.createWritableChild(i2, i3, localWritableRaster.getWidth(), localWritableRaster.getHeight(), i2, i3, arrayOfInt2);
        }
      }
      filter((Raster)localObject, localWritableRaster);
    }
    if (k != 0)
    {
      localObject = new ColorConvertOp(this.hints);
      ((ColorConvertOp)localObject).filter(paramBufferedImage2, localBufferedImage);
    }
    return localBufferedImage;
  }
  
  public final WritableRaster filter(Raster paramRaster, WritableRaster paramWritableRaster)
  {
    int i = paramRaster.getNumBands();
    int j = paramWritableRaster.getNumBands();
    int k = paramRaster.getHeight();
    int m = paramRaster.getWidth();
    int[] arrayOfInt = new int[i];
    if (paramWritableRaster == null) {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    } else if ((k != paramWritableRaster.getHeight()) || (m != paramWritableRaster.getWidth())) {
      throw new IllegalArgumentException("Width or height of Rasters do not match");
    }
    j = paramWritableRaster.getNumBands();
    if (i != j) {
      throw new IllegalArgumentException("Number of channels in the src (" + i + ") does not match number of channels" + " in the destination (" + j + ")");
    }
    int n = this.ltable.getNumComponents();
    if ((n != 1) && (n != paramRaster.getNumBands())) {
      throw new IllegalArgumentException("Number of arrays in the  lookup table (" + n + " is not compatible with the " + " src Raster: " + paramRaster);
    }
    if (ImagingLib.filter(this, paramRaster, paramWritableRaster) != null) {
      return paramWritableRaster;
    }
    if ((this.ltable instanceof ByteLookupTable))
    {
      byteFilter((ByteLookupTable)this.ltable, paramRaster, paramWritableRaster, m, k, i);
    }
    else if ((this.ltable instanceof ShortLookupTable))
    {
      shortFilter((ShortLookupTable)this.ltable, paramRaster, paramWritableRaster, m, k, i);
    }
    else
    {
      int i1 = paramRaster.getMinX();
      int i2 = paramRaster.getMinY();
      int i3 = paramWritableRaster.getMinX();
      int i4 = paramWritableRaster.getMinY();
      int i5 = 0;
      while (i5 < k)
      {
        int i6 = i1;
        int i7 = i3;
        int i8 = 0;
        while (i8 < m)
        {
          paramRaster.getPixel(i6, i2, arrayOfInt);
          this.ltable.lookupPixel(arrayOfInt, arrayOfInt);
          paramWritableRaster.setPixel(i7, i4, arrayOfInt);
          i8++;
          i6++;
          i7++;
        }
        i5++;
        i2++;
        i4++;
      }
    }
    return paramWritableRaster;
  }
  
  public final Rectangle2D getBounds2D(BufferedImage paramBufferedImage)
  {
    return getBounds2D(paramBufferedImage.getRaster());
  }
  
  public final Rectangle2D getBounds2D(Raster paramRaster)
  {
    return paramRaster.getBounds();
  }
  
  public BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel)
  {
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    int k = 0;
    BufferedImage localBufferedImage;
    if (paramColorModel == null)
    {
      Object localObject = paramBufferedImage.getColorModel();
      WritableRaster localWritableRaster = paramBufferedImage.getRaster();
      if ((localObject instanceof ComponentColorModel))
      {
        DataBuffer localDataBuffer = localWritableRaster.getDataBuffer();
        boolean bool1 = ((ColorModel)localObject).hasAlpha();
        boolean bool2 = ((ColorModel)localObject).isAlphaPremultiplied();
        int m = ((ColorModel)localObject).getTransparency();
        int[] arrayOfInt = null;
        if ((this.ltable instanceof ByteLookupTable))
        {
          if (localDataBuffer.getDataType() == 1)
          {
            if (bool1)
            {
              arrayOfInt = new int[2];
              if (m == 2) {
                arrayOfInt[1] = 1;
              } else {
                arrayOfInt[1] = 8;
              }
            }
            else
            {
              arrayOfInt = new int[1];
            }
            arrayOfInt[0] = 8;
          }
        }
        else if ((this.ltable instanceof ShortLookupTable))
        {
          k = 1;
          if (localDataBuffer.getDataType() == 0)
          {
            if (bool1)
            {
              arrayOfInt = new int[2];
              if (m == 2) {
                arrayOfInt[1] = 1;
              } else {
                arrayOfInt[1] = 16;
              }
            }
            else
            {
              arrayOfInt = new int[1];
            }
            arrayOfInt[0] = 16;
          }
        }
        if (arrayOfInt != null) {
          localObject = new ComponentColorModel(((ColorModel)localObject).getColorSpace(), arrayOfInt, bool1, bool2, m, k);
        }
      }
      localBufferedImage = new BufferedImage((ColorModel)localObject, ((ColorModel)localObject).createCompatibleWritableRaster(i, j), ((ColorModel)localObject).isAlphaPremultiplied(), null);
    }
    else
    {
      localBufferedImage = new BufferedImage(paramColorModel, paramColorModel.createCompatibleWritableRaster(i, j), paramColorModel.isAlphaPremultiplied(), null);
    }
    return localBufferedImage;
  }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster)
  {
    return paramRaster.createCompatibleWritableRaster();
  }
  
  public final Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2)
  {
    if (paramPoint2D2 == null) {
      paramPoint2D2 = new Point2D.Float();
    }
    paramPoint2D2.setLocation(paramPoint2D1.getX(), paramPoint2D1.getY());
    return paramPoint2D2;
  }
  
  public final RenderingHints getRenderingHints()
  {
    return this.hints;
  }
  
  private final void byteFilter(ByteLookupTable paramByteLookupTable, Raster paramRaster, WritableRaster paramWritableRaster, int paramInt1, int paramInt2, int paramInt3)
  {
    int[] arrayOfInt = null;
    byte[][] arrayOfByte = paramByteLookupTable.getTable();
    int i = paramByteLookupTable.getOffset();
    int k = 1;
    if (arrayOfByte.length == 1) {
      k = 0;
    }
    int i2 = arrayOfByte[0].length;
    for (int n = 0; n < paramInt2; n++)
    {
      int j = 0;
      int i1 = 0;
      while (i1 < paramInt3)
      {
        arrayOfInt = paramRaster.getSamples(0, n, paramInt1, 1, i1, arrayOfInt);
        for (int m = 0; m < paramInt1; m++)
        {
          int i3 = arrayOfInt[m] - i;
          if ((i3 < 0) || (i3 > i2)) {
            throw new IllegalArgumentException("index (" + i3 + "(out of range: " + " srcPix[" + m + "]=" + arrayOfInt[m] + " offset=" + i);
          }
          arrayOfInt[m] = arrayOfByte[j][i3];
        }
        paramWritableRaster.setSamples(0, n, paramInt1, 1, i1, arrayOfInt);
        i1++;
        j += k;
      }
    }
  }
  
  private final void shortFilter(ShortLookupTable paramShortLookupTable, Raster paramRaster, WritableRaster paramWritableRaster, int paramInt1, int paramInt2, int paramInt3)
  {
    int[] arrayOfInt = null;
    short[][] arrayOfShort = paramShortLookupTable.getTable();
    int j = paramShortLookupTable.getOffset();
    int m = 1;
    if (arrayOfShort.length == 1) {
      m = 0;
    }
    int n = 0;
    int i1 = 0;
    int i3 = 65535;
    for (i1 = 0; i1 < paramInt2; i1++)
    {
      int k = 0;
      int i = 0;
      while (i < paramInt3)
      {
        arrayOfInt = paramRaster.getSamples(0, i1, paramInt1, 1, i, arrayOfInt);
        for (n = 0; n < paramInt1; n++)
        {
          int i2 = arrayOfInt[n] - j;
          if ((i2 < 0) || (i2 > i3)) {
            throw new IllegalArgumentException("index out of range " + i2 + " x is " + n + "srcPix[x]=" + arrayOfInt[n] + " offset=" + j);
          }
          arrayOfInt[n] = arrayOfShort[k][i2];
        }
        paramWritableRaster.setSamples(0, i1, paramInt1, 1, i, arrayOfInt);
        i++;
        k += m;
      }
    }
  }
}
