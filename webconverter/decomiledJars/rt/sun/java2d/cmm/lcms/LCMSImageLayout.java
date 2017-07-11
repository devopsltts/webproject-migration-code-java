package sun.java2d.cmm.lcms;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ShortComponentRaster;

class LCMSImageLayout
{
  public static final int SWAPFIRST = 16384;
  public static final int DOSWAP = 1024;
  public static final int PT_RGB_8 = CHANNELS_SH(3) | BYTES_SH(1);
  public static final int PT_GRAY_8 = CHANNELS_SH(1) | BYTES_SH(1);
  public static final int PT_GRAY_16 = CHANNELS_SH(1) | BYTES_SH(2);
  public static final int PT_RGBA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
  public static final int PT_ARGB_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 0x4000;
  public static final int PT_BGR_8 = 0x400 | CHANNELS_SH(3) | BYTES_SH(1);
  public static final int PT_ABGR_8 = 0x400 | EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
  public static final int PT_BGRA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 0x400 | 0x4000;
  public static final int DT_BYTE = 0;
  public static final int DT_SHORT = 1;
  public static final int DT_INT = 2;
  public static final int DT_DOUBLE = 3;
  boolean isIntPacked = false;
  int pixelType;
  int dataType;
  int width;
  int height;
  int nextRowOffset;
  private int nextPixelOffset;
  int offset;
  private boolean imageAtOnce = false;
  Object dataArray;
  private int dataArrayLength;
  
  public static int BYTES_SH(int paramInt)
  {
    return paramInt;
  }
  
  public static int EXTRA_SH(int paramInt)
  {
    return paramInt << 7;
  }
  
  public static int CHANNELS_SH(int paramInt)
  {
    return paramInt << 3;
  }
  
  private LCMSImageLayout(int paramInt1, int paramInt2, int paramInt3)
    throws LCMSImageLayout.ImageLayoutException
  {
    this.pixelType = paramInt2;
    this.width = paramInt1;
    this.height = 1;
    this.nextPixelOffset = paramInt3;
    this.nextRowOffset = safeMult(paramInt3, paramInt1);
    this.offset = 0;
  }
  
  private LCMSImageLayout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws LCMSImageLayout.ImageLayoutException
  {
    this.pixelType = paramInt3;
    this.width = paramInt1;
    this.height = paramInt2;
    this.nextPixelOffset = paramInt4;
    this.nextRowOffset = safeMult(paramInt4, paramInt1);
    this.offset = 0;
  }
  
  public LCMSImageLayout(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws LCMSImageLayout.ImageLayoutException
  {
    this(paramInt1, paramInt2, paramInt3);
    this.dataType = 0;
    this.dataArray = paramArrayOfByte;
    this.dataArrayLength = paramArrayOfByte.length;
    verify();
  }
  
  public LCMSImageLayout(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3)
    throws LCMSImageLayout.ImageLayoutException
  {
    this(paramInt1, paramInt2, paramInt3);
    this.dataType = 1;
    this.dataArray = paramArrayOfShort;
    this.dataArrayLength = (2 * paramArrayOfShort.length);
    verify();
  }
  
  public LCMSImageLayout(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
    throws LCMSImageLayout.ImageLayoutException
  {
    this(paramInt1, paramInt2, paramInt3);
    this.dataType = 2;
    this.dataArray = paramArrayOfInt;
    this.dataArrayLength = (4 * paramArrayOfInt.length);
    verify();
  }
  
  public LCMSImageLayout(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3)
    throws LCMSImageLayout.ImageLayoutException
  {
    this(paramInt1, paramInt2, paramInt3);
    this.dataType = 3;
    this.dataArray = paramArrayOfDouble;
    this.dataArrayLength = (8 * paramArrayOfDouble.length);
    verify();
  }
  
  private LCMSImageLayout() {}
  
  public static LCMSImageLayout createImageLayout(BufferedImage paramBufferedImage)
    throws LCMSImageLayout.ImageLayoutException
  {
    LCMSImageLayout localLCMSImageLayout = new LCMSImageLayout();
    Object localObject;
    switch (paramBufferedImage.getType())
    {
    case 1: 
      localLCMSImageLayout.pixelType = PT_ARGB_8;
      localLCMSImageLayout.isIntPacked = true;
      break;
    case 2: 
      localLCMSImageLayout.pixelType = PT_ARGB_8;
      localLCMSImageLayout.isIntPacked = true;
      break;
    case 4: 
      localLCMSImageLayout.pixelType = PT_ABGR_8;
      localLCMSImageLayout.isIntPacked = true;
      break;
    case 5: 
      localLCMSImageLayout.pixelType = PT_BGR_8;
      break;
    case 6: 
      localLCMSImageLayout.pixelType = PT_ABGR_8;
      break;
    case 10: 
      localLCMSImageLayout.pixelType = PT_GRAY_8;
      break;
    case 11: 
      localLCMSImageLayout.pixelType = PT_GRAY_16;
      break;
    case 3: 
    case 7: 
    case 8: 
    case 9: 
    default: 
      localObject = paramBufferedImage.getColorModel();
      if ((localObject instanceof ComponentColorModel))
      {
        ComponentColorModel localComponentColorModel = (ComponentColorModel)localObject;
        int[] arrayOfInt1 = localComponentColorModel.getComponentSize();
        for (int m : arrayOfInt1) {
          if (m != 8) {
            return null;
          }
        }
        return createImageLayout(paramBufferedImage.getRaster());
      }
      return null;
    }
    localLCMSImageLayout.width = paramBufferedImage.getWidth();
    localLCMSImageLayout.height = paramBufferedImage.getHeight();
    switch (paramBufferedImage.getType())
    {
    case 1: 
    case 2: 
    case 4: 
      localObject = (IntegerComponentRaster)paramBufferedImage.getRaster();
      localLCMSImageLayout.nextRowOffset = safeMult(4, ((IntegerComponentRaster)localObject).getScanlineStride());
      localLCMSImageLayout.nextPixelOffset = safeMult(4, ((IntegerComponentRaster)localObject).getPixelStride());
      localLCMSImageLayout.offset = safeMult(4, ((IntegerComponentRaster)localObject).getDataOffset(0));
      localLCMSImageLayout.dataArray = ((IntegerComponentRaster)localObject).getDataStorage();
      localLCMSImageLayout.dataArrayLength = (4 * ((IntegerComponentRaster)localObject).getDataStorage().length);
      localLCMSImageLayout.dataType = 2;
      if (localLCMSImageLayout.nextRowOffset == localLCMSImageLayout.width * 4 * ((IntegerComponentRaster)localObject).getPixelStride()) {
        localLCMSImageLayout.imageAtOnce = true;
      }
      break;
    case 5: 
    case 6: 
      localObject = (ByteComponentRaster)paramBufferedImage.getRaster();
      localLCMSImageLayout.nextRowOffset = ((ByteComponentRaster)localObject).getScanlineStride();
      localLCMSImageLayout.nextPixelOffset = ((ByteComponentRaster)localObject).getPixelStride();
      int i = paramBufferedImage.getSampleModel().getNumBands() - 1;
      localLCMSImageLayout.offset = ((ByteComponentRaster)localObject).getDataOffset(i);
      localLCMSImageLayout.dataArray = ((ByteComponentRaster)localObject).getDataStorage();
      localLCMSImageLayout.dataArrayLength = ((ByteComponentRaster)localObject).getDataStorage().length;
      localLCMSImageLayout.dataType = 0;
      if (localLCMSImageLayout.nextRowOffset == localLCMSImageLayout.width * ((ByteComponentRaster)localObject).getPixelStride()) {
        localLCMSImageLayout.imageAtOnce = true;
      }
      break;
    case 10: 
      localObject = (ByteComponentRaster)paramBufferedImage.getRaster();
      localLCMSImageLayout.nextRowOffset = ((ByteComponentRaster)localObject).getScanlineStride();
      localLCMSImageLayout.nextPixelOffset = ((ByteComponentRaster)localObject).getPixelStride();
      localLCMSImageLayout.dataArrayLength = ((ByteComponentRaster)localObject).getDataStorage().length;
      localLCMSImageLayout.offset = ((ByteComponentRaster)localObject).getDataOffset(0);
      localLCMSImageLayout.dataArray = ((ByteComponentRaster)localObject).getDataStorage();
      localLCMSImageLayout.dataType = 0;
      if (localLCMSImageLayout.nextRowOffset == localLCMSImageLayout.width * ((ByteComponentRaster)localObject).getPixelStride()) {
        localLCMSImageLayout.imageAtOnce = true;
      }
      break;
    case 11: 
      localObject = (ShortComponentRaster)paramBufferedImage.getRaster();
      localLCMSImageLayout.nextRowOffset = safeMult(2, ((ShortComponentRaster)localObject).getScanlineStride());
      localLCMSImageLayout.nextPixelOffset = safeMult(2, ((ShortComponentRaster)localObject).getPixelStride());
      localLCMSImageLayout.offset = safeMult(2, ((ShortComponentRaster)localObject).getDataOffset(0));
      localLCMSImageLayout.dataArray = ((ShortComponentRaster)localObject).getDataStorage();
      localLCMSImageLayout.dataArrayLength = (2 * ((ShortComponentRaster)localObject).getDataStorage().length);
      localLCMSImageLayout.dataType = 1;
      if (localLCMSImageLayout.nextRowOffset == localLCMSImageLayout.width * 2 * ((ShortComponentRaster)localObject).getPixelStride()) {
        localLCMSImageLayout.imageAtOnce = true;
      }
      break;
    case 3: 
    case 7: 
    case 8: 
    case 9: 
    default: 
      return null;
    }
    localLCMSImageLayout.verify();
    return localLCMSImageLayout;
  }
  
  private void verify()
    throws LCMSImageLayout.ImageLayoutException
  {
    if ((this.offset < 0) || (this.offset >= this.dataArrayLength)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    if (this.nextPixelOffset != getBytesPerPixel(this.pixelType)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    int i = safeMult(this.nextRowOffset, this.height - 1);
    int j = safeMult(this.nextPixelOffset, this.width - 1);
    j = safeAdd(j, i);
    int k = safeAdd(this.offset, j);
    if ((k < 0) || (k >= this.dataArrayLength)) {
      throw new ImageLayoutException("Invalid image layout");
    }
  }
  
  static int safeAdd(int paramInt1, int paramInt2)
    throws LCMSImageLayout.ImageLayoutException
  {
    long l = paramInt1;
    l += paramInt2;
    if ((l < -2147483648L) || (l > 2147483647L)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    return (int)l;
  }
  
  static int safeMult(int paramInt1, int paramInt2)
    throws LCMSImageLayout.ImageLayoutException
  {
    long l = paramInt1;
    l *= paramInt2;
    if ((l < -2147483648L) || (l > 2147483647L)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    return (int)l;
  }
  
  public static LCMSImageLayout createImageLayout(Raster paramRaster)
  {
    LCMSImageLayout localLCMSImageLayout = new LCMSImageLayout();
    if (((paramRaster instanceof ByteComponentRaster)) && ((paramRaster.getSampleModel() instanceof ComponentSampleModel)))
    {
      ByteComponentRaster localByteComponentRaster = (ByteComponentRaster)paramRaster;
      ComponentSampleModel localComponentSampleModel = (ComponentSampleModel)paramRaster.getSampleModel();
      localLCMSImageLayout.pixelType = (CHANNELS_SH(localByteComponentRaster.getNumBands()) | BYTES_SH(1));
      int[] arrayOfInt = localComponentSampleModel.getBandOffsets();
      BandOrder localBandOrder = BandOrder.getBandOrder(arrayOfInt);
      int i = 0;
      switch (1.$SwitchMap$sun$java2d$cmm$lcms$LCMSImageLayout$BandOrder[localBandOrder.ordinal()])
      {
      case 3: 
        localLCMSImageLayout.pixelType |= 0x400;
        i = localComponentSampleModel.getNumBands() - 1;
        break;
      case 2: 
        break;
      default: 
        return null;
      }
      localLCMSImageLayout.nextRowOffset = localByteComponentRaster.getScanlineStride();
      localLCMSImageLayout.nextPixelOffset = localByteComponentRaster.getPixelStride();
      localLCMSImageLayout.offset = localByteComponentRaster.getDataOffset(i);
      localLCMSImageLayout.dataArray = localByteComponentRaster.getDataStorage();
      localLCMSImageLayout.dataType = 0;
      localLCMSImageLayout.width = localByteComponentRaster.getWidth();
      localLCMSImageLayout.height = localByteComponentRaster.getHeight();
      if (localLCMSImageLayout.nextRowOffset == localLCMSImageLayout.width * localByteComponentRaster.getPixelStride()) {
        localLCMSImageLayout.imageAtOnce = true;
      }
      return localLCMSImageLayout;
    }
    return null;
  }
  
  private static int getBytesPerPixel(int paramInt)
  {
    int i = 0x7 & paramInt;
    int j = 0xF & paramInt >> 3;
    int k = 0x7 & paramInt >> 7;
    return i * (j + k);
  }
  
  private static enum BandOrder
  {
    DIRECT,  INVERTED,  ARBITRARY,  UNKNOWN;
    
    private BandOrder() {}
    
    public static BandOrder getBandOrder(int[] paramArrayOfInt)
    {
      BandOrder localBandOrder = UNKNOWN;
      int i = paramArrayOfInt.length;
      for (int j = 0; (localBandOrder != ARBITRARY) && (j < paramArrayOfInt.length); j++) {
        switch (LCMSImageLayout.1.$SwitchMap$sun$java2d$cmm$lcms$LCMSImageLayout$BandOrder[localBandOrder.ordinal()])
        {
        case 1: 
          if (paramArrayOfInt[j] == j) {
            localBandOrder = DIRECT;
          } else if (paramArrayOfInt[j] == i - 1 - j) {
            localBandOrder = INVERTED;
          } else {
            localBandOrder = ARBITRARY;
          }
          break;
        case 2: 
          if (paramArrayOfInt[j] != j) {
            localBandOrder = ARBITRARY;
          }
          break;
        case 3: 
          if (paramArrayOfInt[j] != i - 1 - j) {
            localBandOrder = ARBITRARY;
          }
          break;
        }
      }
      return localBandOrder;
    }
  }
  
  public static class ImageLayoutException
    extends Exception
  {
    public ImageLayoutException(String paramString)
    {
      super();
    }
  }
}
