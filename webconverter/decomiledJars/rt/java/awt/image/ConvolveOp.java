package java.awt.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import sun.awt.image.ImagingLib;

public class ConvolveOp
  implements BufferedImageOp, RasterOp
{
  Kernel kernel;
  int edgeHint;
  RenderingHints hints;
  public static final int EDGE_ZERO_FILL = 0;
  public static final int EDGE_NO_OP = 1;
  
  public ConvolveOp(Kernel paramKernel, int paramInt, RenderingHints paramRenderingHints)
  {
    this.kernel = paramKernel;
    this.edgeHint = paramInt;
    this.hints = paramRenderingHints;
  }
  
  public ConvolveOp(Kernel paramKernel)
  {
    this.kernel = paramKernel;
    this.edgeHint = 0;
  }
  
  public int getEdgeCondition()
  {
    return this.edgeHint;
  }
  
  public final Kernel getKernel()
  {
    return (Kernel)this.kernel.clone();
  }
  
  public final BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2)
  {
    if (paramBufferedImage1 == null) {
      throw new NullPointerException("src image is null");
    }
    if (paramBufferedImage1 == paramBufferedImage2) {
      throw new IllegalArgumentException("src image cannot be the same as the dst image");
    }
    int i = 0;
    ColorModel localColorModel1 = paramBufferedImage1.getColorModel();
    BufferedImage localBufferedImage = paramBufferedImage2;
    Object localObject1;
    if ((localColorModel1 instanceof IndexColorModel))
    {
      localObject1 = (IndexColorModel)localColorModel1;
      paramBufferedImage1 = ((IndexColorModel)localObject1).convertToIntDiscrete(paramBufferedImage1.getRaster(), false);
      localColorModel1 = paramBufferedImage1.getColorModel();
    }
    ColorModel localColorModel2;
    if (paramBufferedImage2 == null)
    {
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      localColorModel2 = localColorModel1;
      localBufferedImage = paramBufferedImage2;
    }
    else
    {
      localColorModel2 = paramBufferedImage2.getColorModel();
      if (localColorModel1.getColorSpace().getType() != localColorModel2.getColorSpace().getType())
      {
        i = 1;
        paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
        localColorModel2 = paramBufferedImage2.getColorModel();
      }
      else if ((localColorModel2 instanceof IndexColorModel))
      {
        paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
        localColorModel2 = paramBufferedImage2.getColorModel();
      }
    }
    if (ImagingLib.filter(this, paramBufferedImage1, paramBufferedImage2) == null) {
      throw new ImagingOpException("Unable to convolve src image");
    }
    if (i != 0)
    {
      localObject1 = new ColorConvertOp(this.hints);
      ((ColorConvertOp)localObject1).filter(paramBufferedImage2, localBufferedImage);
    }
    else if (localBufferedImage != paramBufferedImage2)
    {
      localObject1 = localBufferedImage.createGraphics();
      try
      {
        ((Graphics2D)localObject1).drawImage(paramBufferedImage2, 0, 0, null);
      }
      finally
      {
        ((Graphics2D)localObject1).dispose();
      }
    }
    return localBufferedImage;
  }
  
  public final WritableRaster filter(Raster paramRaster, WritableRaster paramWritableRaster)
  {
    if (paramWritableRaster == null)
    {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    }
    else
    {
      if (paramRaster == paramWritableRaster) {
        throw new IllegalArgumentException("src image cannot be the same as the dst image");
      }
      if (paramRaster.getNumBands() != paramWritableRaster.getNumBands()) {
        throw new ImagingOpException("Different number of bands in src  and dst Rasters");
      }
    }
    if (ImagingLib.filter(this, paramRaster, paramWritableRaster) == null) {
      throw new ImagingOpException("Unable to convolve src image");
    }
    return paramWritableRaster;
  }
  
  public BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel)
  {
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    WritableRaster localWritableRaster = null;
    if (paramColorModel == null)
    {
      paramColorModel = paramBufferedImage.getColorModel();
      if ((paramColorModel instanceof IndexColorModel)) {
        paramColorModel = ColorModel.getRGBdefault();
      } else {
        localWritableRaster = paramBufferedImage.getData().createCompatibleWritableRaster(i, j);
      }
    }
    if (localWritableRaster == null) {
      localWritableRaster = paramColorModel.createCompatibleWritableRaster(i, j);
    }
    BufferedImage localBufferedImage = new BufferedImage(paramColorModel, localWritableRaster, paramColorModel.isAlphaPremultiplied(), null);
    return localBufferedImage;
  }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster)
  {
    return paramRaster.createCompatibleWritableRaster();
  }
  
  public final Rectangle2D getBounds2D(BufferedImage paramBufferedImage)
  {
    return getBounds2D(paramBufferedImage.getRaster());
  }
  
  public final Rectangle2D getBounds2D(Raster paramRaster)
  {
    return paramRaster.getBounds();
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
}
