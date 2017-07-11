package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.I18N;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class WBMPImageWriter
  extends ImageWriter
{
  private ImageOutputStream stream = null;
  
  private static int getNumBits(int paramInt)
  {
    int i = 32;
    int j = Integer.MIN_VALUE;
    while ((j != 0) && ((paramInt & j) == 0))
    {
      i--;
      j >>>= 1;
    }
    return i;
  }
  
  private static byte[] intToMultiByte(int paramInt)
  {
    int i = getNumBits(paramInt);
    byte[] arrayOfByte = new byte[(i + 6) / 7];
    int j = arrayOfByte.length - 1;
    for (int k = 0; k <= j; k++)
    {
      arrayOfByte[k] = ((byte)(paramInt >>> (j - k) * 7 & 0x7F));
      if (k != j)
      {
        int tmp55_53 = k;
        byte[] tmp55_52 = arrayOfByte;
        tmp55_52[tmp55_53] = ((byte)(tmp55_52[tmp55_53] | 0xFFFFFF80));
      }
    }
    return arrayOfByte;
  }
  
  public WBMPImageWriter(ImageWriterSpi paramImageWriterSpi)
  {
    super(paramImageWriterSpi);
  }
  
  public void setOutput(Object paramObject)
  {
    super.setOutput(paramObject);
    if (paramObject != null)
    {
      if (!(paramObject instanceof ImageOutputStream)) {
        throw new IllegalArgumentException(I18N.getString("WBMPImageWriter"));
      }
      this.stream = ((ImageOutputStream)paramObject);
    }
    else
    {
      this.stream = null;
    }
  }
  
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam)
  {
    return null;
  }
  
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    WBMPMetadata localWBMPMetadata = new WBMPMetadata();
    localWBMPMetadata.wbmpType = 0;
    return localWBMPMetadata;
  }
  
  public IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam)
  {
    return null;
  }
  
  public IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    return null;
  }
  
  public boolean canWriteRasters()
  {
    return true;
  }
  
  public void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    if (this.stream == null) {
      throw new IllegalStateException(I18N.getString("WBMPImageWriter3"));
    }
    if (paramIIOImage == null) {
      throw new IllegalArgumentException(I18N.getString("WBMPImageWriter4"));
    }
    clearAbortRequest();
    processImageStarted(0);
    if (paramImageWriteParam == null) {
      paramImageWriteParam = getDefaultWriteParam();
    }
    RenderedImage localRenderedImage = null;
    Object localObject1 = null;
    boolean bool = paramIIOImage.hasRaster();
    Rectangle localRectangle1 = paramImageWriteParam.getSourceRegion();
    SampleModel localSampleModel = null;
    if (bool)
    {
      localObject1 = paramIIOImage.getRaster();
      localSampleModel = ((Raster)localObject1).getSampleModel();
    }
    else
    {
      localRenderedImage = paramIIOImage.getRenderedImage();
      localSampleModel = localRenderedImage.getSampleModel();
      localObject1 = localRenderedImage.getData();
    }
    checkSampleModel(localSampleModel);
    if (localRectangle1 == null) {
      localRectangle1 = ((Raster)localObject1).getBounds();
    } else {
      localRectangle1 = localRectangle1.intersection(((Raster)localObject1).getBounds());
    }
    if (localRectangle1.isEmpty()) {
      throw new RuntimeException(I18N.getString("WBMPImageWriter1"));
    }
    int i = paramImageWriteParam.getSourceXSubsampling();
    int j = paramImageWriteParam.getSourceYSubsampling();
    int k = paramImageWriteParam.getSubsamplingXOffset();
    int m = paramImageWriteParam.getSubsamplingYOffset();
    localRectangle1.translate(k, m);
    localRectangle1.width -= k;
    localRectangle1.height -= m;
    int n = localRectangle1.x / i;
    int i1 = localRectangle1.y / j;
    int i2 = (localRectangle1.width + i - 1) / i;
    int i3 = (localRectangle1.height + j - 1) / j;
    Rectangle localRectangle2 = new Rectangle(n, i1, i2, i3);
    localSampleModel = localSampleModel.createCompatibleSampleModel(i2, i3);
    Object localObject2 = localSampleModel;
    if ((localSampleModel.getDataType() != 0) || (!(localSampleModel instanceof MultiPixelPackedSampleModel)) || (((MultiPixelPackedSampleModel)localSampleModel).getDataBitOffset() != 0)) {
      localObject2 = new MultiPixelPackedSampleModel(0, i2, i3, 1, i2 + 7 >> 3, 0);
    }
    WritableRaster localWritableRaster;
    Object localObject3;
    int i8;
    int i9;
    int i10;
    int i11;
    if (!localRectangle2.equals(localRectangle1)) {
      if ((i == 1) && (j == 1))
      {
        localObject1 = ((Raster)localObject1).createChild(((Raster)localObject1).getMinX(), ((Raster)localObject1).getMinY(), i2, i3, n, i1, null);
      }
      else
      {
        localWritableRaster = Raster.createWritableRaster((SampleModel)localObject2, new Point(n, i1));
        localObject3 = ((DataBufferByte)localWritableRaster.getDataBuffer()).getData();
        i6 = i1;
        int i7 = localRectangle1.y;
        i8 = 0;
        while (i6 < i1 + i3)
        {
          i9 = 0;
          i10 = localRectangle1.x;
          while (i9 < i2)
          {
            i11 = ((Raster)localObject1).getSample(i10, i7, 0);
            int tmp508_507 = (i8 + (i9 >> 3));
            Object tmp508_499 = localObject3;
            tmp508_499[tmp508_507] = ((byte)(tmp508_499[tmp508_507] | i11 << 7 - (i9 & 0x7)));
            i9++;
            i10 += i;
          }
          i8 += (i2 + 7 >> 3);
          i6++;
          i7 += j;
        }
        localObject1 = localWritableRaster;
      }
    }
    if (!localObject2.equals(((Raster)localObject1).getSampleModel()))
    {
      localWritableRaster = Raster.createWritableRaster((SampleModel)localObject2, new Point(((Raster)localObject1).getMinX(), ((Raster)localObject1).getMinY()));
      localWritableRaster.setRect((Raster)localObject1);
      localObject1 = localWritableRaster;
    }
    int i4 = 0;
    if ((!bool) && ((localRenderedImage.getColorModel() instanceof IndexColorModel)))
    {
      localObject3 = (IndexColorModel)localRenderedImage.getColorModel();
      i4 = ((IndexColorModel)localObject3).getRed(0) > ((IndexColorModel)localObject3).getRed(1) ? 1 : 0;
    }
    int i5 = ((MultiPixelPackedSampleModel)localObject2).getScanlineStride();
    int i6 = (i2 + 7) / 8;
    byte[] arrayOfByte1 = ((DataBufferByte)((Raster)localObject1).getDataBuffer()).getData();
    this.stream.write(0);
    this.stream.write(0);
    this.stream.write(intToMultiByte(i2));
    this.stream.write(intToMultiByte(i3));
    if ((i4 == 0) && (i5 == i6))
    {
      this.stream.write(arrayOfByte1, 0, i3 * i6);
      processImageProgress(100.0F);
    }
    else
    {
      i8 = 0;
      if (i4 == 0)
      {
        for (i9 = 0; (i9 < i3) && (!abortRequested()); i9++)
        {
          this.stream.write(arrayOfByte1, i8, i6);
          i8 += i5;
          processImageProgress(100.0F * i9 / i3);
        }
      }
      else
      {
        byte[] arrayOfByte2 = new byte[i6];
        for (i10 = 0; (i10 < i3) && (!abortRequested()); i10++)
        {
          for (i11 = 0; i11 < i6; i11++) {
            arrayOfByte2[i11] = ((byte)(arrayOfByte1[(i11 + i8)] ^ 0xFFFFFFFF));
          }
          this.stream.write(arrayOfByte2, 0, i6);
          i8 += i5;
          processImageProgress(100.0F * i10 / i3);
        }
      }
    }
    if (abortRequested())
    {
      processWriteAborted();
    }
    else
    {
      processImageComplete();
      this.stream.flushBefore(this.stream.getStreamPosition());
    }
  }
  
  public void reset()
  {
    super.reset();
    this.stream = null;
  }
  
  private void checkSampleModel(SampleModel paramSampleModel)
  {
    int i = paramSampleModel.getDataType();
    if ((i < 0) || (i > 3) || (paramSampleModel.getNumBands() != 1) || (paramSampleModel.getSampleSize(0) != 1)) {
      throw new IllegalArgumentException(I18N.getString("WBMPImageWriter2"));
    }
  }
}
