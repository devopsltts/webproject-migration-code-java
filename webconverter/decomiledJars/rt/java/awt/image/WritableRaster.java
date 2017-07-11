package java.awt.image;

import java.awt.Point;
import java.awt.Rectangle;

public class WritableRaster
  extends Raster
{
  protected WritableRaster(SampleModel paramSampleModel, Point paramPoint)
  {
    this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  protected WritableRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint)
  {
    this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  protected WritableRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, WritableRaster paramWritableRaster)
  {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramWritableRaster);
  }
  
  public WritableRaster getWritableParent()
  {
    return (WritableRaster)this.parent;
  }
  
  public WritableRaster createWritableTranslatedChild(int paramInt1, int paramInt2)
  {
    return createWritableChild(this.minX, this.minY, this.width, this.height, paramInt1, paramInt2, null);
  }
  
  public WritableRaster createWritableChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt)
  {
    if (paramInt1 < this.minX) {
      throw new RasterFormatException("parentX lies outside raster");
    }
    if (paramInt2 < this.minY) {
      throw new RasterFormatException("parentY lies outside raster");
    }
    if ((paramInt1 + paramInt3 < paramInt1) || (paramInt1 + paramInt3 > this.width + this.minX)) {
      throw new RasterFormatException("(parentX + width) is outside raster");
    }
    if ((paramInt2 + paramInt4 < paramInt2) || (paramInt2 + paramInt4 > this.height + this.minY)) {
      throw new RasterFormatException("(parentY + height) is outside raster");
    }
    SampleModel localSampleModel;
    if (paramArrayOfInt != null) {
      localSampleModel = this.sampleModel.createSubsetSampleModel(paramArrayOfInt);
    } else {
      localSampleModel = this.sampleModel;
    }
    int i = paramInt5 - paramInt1;
    int j = paramInt6 - paramInt2;
    return new WritableRaster(localSampleModel, getDataBuffer(), new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject)
  {
    this.sampleModel.setDataElements(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramObject, this.dataBuffer);
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Raster paramRaster)
  {
    int i = paramInt1 + paramRaster.getMinX();
    int j = paramInt2 + paramRaster.getMinY();
    int k = paramRaster.getWidth();
    int m = paramRaster.getHeight();
    if ((i < this.minX) || (j < this.minY) || (i + k > this.minX + this.width) || (j + m > this.minY + this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int n = paramRaster.getMinX();
    int i1 = paramRaster.getMinY();
    Object localObject = null;
    for (int i2 = 0; i2 < m; i2++)
    {
      localObject = paramRaster.getDataElements(n, i1 + i2, k, 1, localObject);
      setDataElements(i, j + i2, k, 1, localObject);
    }
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject)
  {
    this.sampleModel.setDataElements(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramObject, this.dataBuffer);
  }
  
  public void setRect(Raster paramRaster)
  {
    setRect(0, 0, paramRaster);
  }
  
  public void setRect(int paramInt1, int paramInt2, Raster paramRaster)
  {
    int i = paramRaster.getWidth();
    int j = paramRaster.getHeight();
    int k = paramRaster.getMinX();
    int m = paramRaster.getMinY();
    int n = paramInt1 + k;
    int i1 = paramInt2 + m;
    int i2;
    if (n < this.minX)
    {
      i2 = this.minX - n;
      i -= i2;
      k += i2;
      n = this.minX;
    }
    if (i1 < this.minY)
    {
      i2 = this.minY - i1;
      j -= i2;
      m += i2;
      i1 = this.minY;
    }
    if (n + i > this.minX + this.width) {
      i = this.minX + this.width - n;
    }
    if (i1 + j > this.minY + this.height) {
      j = this.minY + this.height - i1;
    }
    if ((i <= 0) || (j <= 0)) {
      return;
    }
    switch (paramRaster.getSampleModel().getDataType())
    {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      int[] arrayOfInt = null;
      for (int i3 = 0; i3 < j; i3++)
      {
        arrayOfInt = paramRaster.getPixels(k, m + i3, i, 1, arrayOfInt);
        setPixels(n, i1 + i3, i, 1, arrayOfInt);
      }
      break;
    case 4: 
      float[] arrayOfFloat = null;
      for (int i4 = 0; i4 < j; i4++)
      {
        arrayOfFloat = paramRaster.getPixels(k, m + i4, i, 1, arrayOfFloat);
        setPixels(n, i1 + i4, i, 1, arrayOfFloat);
      }
      break;
    case 5: 
      double[] arrayOfDouble = null;
      for (int i5 = 0; i5 < j; i5++)
      {
        arrayOfDouble = paramRaster.getPixels(k, m + i5, i, 1, arrayOfDouble);
        setPixels(n, i1 + i5, i, 1, arrayOfDouble);
      }
    }
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    this.sampleModel.setPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOfInt, this.dataBuffer);
  }
  
  public void setPixel(int paramInt1, int paramInt2, float[] paramArrayOfFloat)
  {
    this.sampleModel.setPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOfFloat, this.dataBuffer);
  }
  
  public void setPixel(int paramInt1, int paramInt2, double[] paramArrayOfDouble)
  {
    this.sampleModel.setPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOfDouble, this.dataBuffer);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    this.sampleModel.setPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfInt, this.dataBuffer);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat)
  {
    this.sampleModel.setPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfFloat, this.dataBuffer);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble)
  {
    this.sampleModel.setPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfDouble, this.dataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.sampleModel.setSample(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, this.dataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    this.sampleModel.setSample(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramFloat, this.dataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble)
  {
    this.sampleModel.setSample(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramDouble, this.dataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt)
  {
    this.sampleModel.setSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfInt, this.dataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float[] paramArrayOfFloat)
  {
    this.sampleModel.setSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfFloat, this.dataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double[] paramArrayOfDouble)
  {
    this.sampleModel.setSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfDouble, this.dataBuffer);
  }
}
