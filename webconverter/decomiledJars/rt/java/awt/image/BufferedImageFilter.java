package java.awt.image;

public class BufferedImageFilter
  extends ImageFilter
  implements Cloneable
{
  BufferedImageOp bufferedImageOp;
  ColorModel model;
  int width;
  int height;
  byte[] bytePixels;
  int[] intPixels;
  
  public BufferedImageFilter(BufferedImageOp paramBufferedImageOp)
  {
    if (paramBufferedImageOp == null) {
      throw new NullPointerException("Operation cannot be null");
    }
    this.bufferedImageOp = paramBufferedImageOp;
  }
  
  public BufferedImageOp getBufferedImageOp()
  {
    return this.bufferedImageOp;
  }
  
  public void setDimensions(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0))
    {
      imageComplete(3);
      return;
    }
    this.width = paramInt1;
    this.height = paramInt2;
  }
  
  public void setColorModel(ColorModel paramColorModel)
  {
    this.model = paramColorModel;
  }
  
  private void convertToRGB()
  {
    int i = this.width * this.height;
    int[] arrayOfInt = new int[i];
    int j;
    if (this.bytePixels != null) {
      for (j = 0; j < i; j++) {
        arrayOfInt[j] = this.model.getRGB(this.bytePixels[j] & 0xFF);
      }
    } else if (this.intPixels != null) {
      for (j = 0; j < i; j++) {
        arrayOfInt[j] = this.model.getRGB(this.intPixels[j]);
      }
    }
    this.bytePixels = null;
    this.intPixels = arrayOfInt;
    this.model = ColorModel.getRGBdefault();
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    if ((paramInt3 < 0) || (paramInt4 < 0)) {
      throw new IllegalArgumentException("Width (" + paramInt3 + ") and height (" + paramInt4 + ") must be > 0");
    }
    if ((paramInt3 == 0) || (paramInt4 == 0)) {
      return;
    }
    if (paramInt2 < 0)
    {
      i = -paramInt2;
      if (i >= paramInt4) {
        return;
      }
      paramInt5 += paramInt6 * i;
      paramInt2 += i;
      paramInt4 -= i;
    }
    if (paramInt2 + paramInt4 > this.height)
    {
      paramInt4 = this.height - paramInt2;
      if (paramInt4 <= 0) {
        return;
      }
    }
    if (paramInt1 < 0)
    {
      i = -paramInt1;
      if (i >= paramInt3) {
        return;
      }
      paramInt5 += i;
      paramInt1 += i;
      paramInt3 -= i;
    }
    if (paramInt1 + paramInt3 > this.width)
    {
      paramInt3 = this.width - paramInt1;
      if (paramInt3 <= 0) {
        return;
      }
    }
    int i = paramInt2 * this.width + paramInt1;
    int j;
    if (this.intPixels == null)
    {
      if (this.bytePixels == null)
      {
        this.bytePixels = new byte[this.width * this.height];
        this.model = paramColorModel;
      }
      else if (this.model != paramColorModel)
      {
        convertToRGB();
      }
      if (this.bytePixels != null) {
        for (j = paramInt4; j > 0; j--)
        {
          System.arraycopy(paramArrayOfByte, paramInt5, this.bytePixels, i, paramInt3);
          paramInt5 += paramInt6;
          i += this.width;
        }
      }
    }
    if (this.intPixels != null)
    {
      j = this.width - paramInt3;
      int k = paramInt6 - paramInt3;
      for (int m = paramInt4; m > 0; m--)
      {
        for (int n = paramInt3; n > 0; n--) {
          this.intPixels[(i++)] = paramColorModel.getRGB(paramArrayOfByte[(paramInt5++)] & 0xFF);
        }
        paramInt5 += k;
        i += j;
      }
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    if ((paramInt3 < 0) || (paramInt4 < 0)) {
      throw new IllegalArgumentException("Width (" + paramInt3 + ") and height (" + paramInt4 + ") must be > 0");
    }
    if ((paramInt3 == 0) || (paramInt4 == 0)) {
      return;
    }
    if (paramInt2 < 0)
    {
      i = -paramInt2;
      if (i >= paramInt4) {
        return;
      }
      paramInt5 += paramInt6 * i;
      paramInt2 += i;
      paramInt4 -= i;
    }
    if (paramInt2 + paramInt4 > this.height)
    {
      paramInt4 = this.height - paramInt2;
      if (paramInt4 <= 0) {
        return;
      }
    }
    if (paramInt1 < 0)
    {
      i = -paramInt1;
      if (i >= paramInt3) {
        return;
      }
      paramInt5 += i;
      paramInt1 += i;
      paramInt3 -= i;
    }
    if (paramInt1 + paramInt3 > this.width)
    {
      paramInt3 = this.width - paramInt1;
      if (paramInt3 <= 0) {
        return;
      }
    }
    if (this.intPixels == null) {
      if (this.bytePixels == null)
      {
        this.intPixels = new int[this.width * this.height];
        this.model = paramColorModel;
      }
      else
      {
        convertToRGB();
      }
    }
    int i = paramInt2 * this.width + paramInt1;
    int j;
    if (this.model == paramColorModel)
    {
      for (j = paramInt4; j > 0; j--)
      {
        System.arraycopy(paramArrayOfInt, paramInt5, this.intPixels, i, paramInt3);
        paramInt5 += paramInt6;
        i += this.width;
      }
    }
    else
    {
      if (this.model != ColorModel.getRGBdefault()) {
        convertToRGB();
      }
      j = this.width - paramInt3;
      int k = paramInt6 - paramInt3;
      for (int m = paramInt4; m > 0; m--)
      {
        for (int n = paramInt3; n > 0; n--) {
          this.intPixels[(i++)] = paramColorModel.getRGB(paramArrayOfInt[(paramInt5++)]);
        }
        paramInt5 += k;
        i += j;
      }
    }
  }
  
  public void imageComplete(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
    case 4: 
      this.model = null;
      this.width = -1;
      this.height = -1;
      this.intPixels = null;
      this.bytePixels = null;
      break;
    case 2: 
    case 3: 
      if ((this.width > 0) && (this.height > 0))
      {
        WritableRaster localWritableRaster;
        if ((this.model instanceof DirectColorModel))
        {
          if (this.intPixels == null) {
            break;
          }
          localWritableRaster = createDCMraster();
        }
        else if ((this.model instanceof IndexColorModel))
        {
          localObject1 = new int[] { 0 };
          if (this.bytePixels == null) {
            break;
          }
          localObject2 = new DataBufferByte(this.bytePixels, this.width * this.height);
          localWritableRaster = Raster.createInterleavedRaster((DataBuffer)localObject2, this.width, this.height, this.width, 1, (int[])localObject1, null);
        }
        else
        {
          convertToRGB();
          if (this.intPixels == null) {
            break;
          }
          localWritableRaster = createDCMraster();
        }
        Object localObject1 = new BufferedImage(this.model, localWritableRaster, this.model.isAlphaPremultiplied(), null);
        localObject1 = this.bufferedImageOp.filter((BufferedImage)localObject1, null);
        Object localObject2 = ((BufferedImage)localObject1).getRaster();
        ColorModel localColorModel = ((BufferedImage)localObject1).getColorModel();
        int i = ((WritableRaster)localObject2).getWidth();
        int j = ((WritableRaster)localObject2).getHeight();
        this.consumer.setDimensions(i, j);
        this.consumer.setColorModel(localColorModel);
        Object localObject3;
        if ((localColorModel instanceof DirectColorModel))
        {
          localObject3 = (DataBufferInt)((WritableRaster)localObject2).getDataBuffer();
          this.consumer.setPixels(0, 0, i, j, localColorModel, ((DataBufferInt)localObject3).getData(), 0, i);
        }
        else if ((localColorModel instanceof IndexColorModel))
        {
          localObject3 = (DataBufferByte)((WritableRaster)localObject2).getDataBuffer();
          this.consumer.setPixels(0, 0, i, j, localColorModel, ((DataBufferByte)localObject3).getData(), 0, i);
        }
        else
        {
          throw new InternalError("Unknown color model " + localColorModel);
        }
      }
      break;
    }
    this.consumer.imageComplete(paramInt);
  }
  
  private final WritableRaster createDCMraster()
  {
    DirectColorModel localDirectColorModel = (DirectColorModel)this.model;
    boolean bool = this.model.hasAlpha();
    int[] arrayOfInt = new int[3 + (bool ? 1 : 0)];
    arrayOfInt[0] = localDirectColorModel.getRedMask();
    arrayOfInt[1] = localDirectColorModel.getGreenMask();
    arrayOfInt[2] = localDirectColorModel.getBlueMask();
    if (bool) {
      arrayOfInt[3] = localDirectColorModel.getAlphaMask();
    }
    DataBufferInt localDataBufferInt = new DataBufferInt(this.intPixels, this.width * this.height);
    WritableRaster localWritableRaster = Raster.createPackedRaster(localDataBufferInt, this.width, this.height, this.width, arrayOfInt, null);
    return localWritableRaster;
  }
}
