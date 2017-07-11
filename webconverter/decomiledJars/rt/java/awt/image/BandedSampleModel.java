package java.awt.image;

public final class BandedSampleModel
  extends ComponentSampleModel
{
  public BandedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super(paramInt1, paramInt2, paramInt3, 1, paramInt2, createIndicesArray(paramInt4), createOffsetArray(paramInt4));
  }
  
  public BandedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    super(paramInt1, paramInt2, paramInt3, 1, paramInt4, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt;
    if (this.numBanks == 1) {
      arrayOfInt = orderBands(this.bandOffsets, paramInt1 * paramInt2);
    } else {
      arrayOfInt = new int[this.bandOffsets.length];
    }
    BandedSampleModel localBandedSampleModel = new BandedSampleModel(this.dataType, paramInt1, paramInt2, paramInt1, this.bankIndices, arrayOfInt);
    return localBandedSampleModel;
  }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt.length > this.bankIndices.length) {
      throw new RasterFormatException("There are only " + this.bankIndices.length + " bands");
    }
    int[] arrayOfInt1 = new int[paramArrayOfInt.length];
    int[] arrayOfInt2 = new int[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      arrayOfInt1[i] = this.bankIndices[paramArrayOfInt[i]];
      arrayOfInt2[i] = this.bandOffsets[paramArrayOfInt[i]];
    }
    return new BandedSampleModel(this.dataType, this.width, this.height, this.scanlineStride, arrayOfInt1, arrayOfInt2);
  }
  
  public DataBuffer createDataBuffer()
  {
    Object localObject = null;
    int i = this.scanlineStride * this.height;
    switch (this.dataType)
    {
    case 0: 
      localObject = new DataBufferByte(i, this.numBanks);
      break;
    case 1: 
      localObject = new DataBufferUShort(i, this.numBanks);
      break;
    case 2: 
      localObject = new DataBufferShort(i, this.numBanks);
      break;
    case 3: 
      localObject = new DataBufferInt(i, this.numBanks);
      break;
    case 4: 
      localObject = new DataBufferFloat(i, this.numBanks);
      break;
    case 5: 
      localObject = new DataBufferDouble(i, this.numBanks);
      break;
    default: 
      throw new IllegalArgumentException("dataType is not one of the supported types.");
    }
    return localObject;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= this.width) || (paramInt2 >= this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = getTransferType();
    int j = getNumDataElements();
    int k = paramInt2 * this.scanlineStride + paramInt1;
    switch (i)
    {
    case 0: 
      byte[] arrayOfByte;
      if (paramObject == null) {
        arrayOfByte = new byte[j];
      } else {
        arrayOfByte = (byte[])paramObject;
      }
      for (int m = 0; m < j; m++) {
        arrayOfByte[m] = ((byte)paramDataBuffer.getElem(this.bankIndices[m], k + this.bandOffsets[m]));
      }
      paramObject = arrayOfByte;
      break;
    case 1: 
    case 2: 
      short[] arrayOfShort;
      if (paramObject == null) {
        arrayOfShort = new short[j];
      } else {
        arrayOfShort = (short[])paramObject;
      }
      for (int n = 0; n < j; n++) {
        arrayOfShort[n] = ((short)paramDataBuffer.getElem(this.bankIndices[n], k + this.bandOffsets[n]));
      }
      paramObject = arrayOfShort;
      break;
    case 3: 
      int[] arrayOfInt;
      if (paramObject == null) {
        arrayOfInt = new int[j];
      } else {
        arrayOfInt = (int[])paramObject;
      }
      for (int i1 = 0; i1 < j; i1++) {
        arrayOfInt[i1] = paramDataBuffer.getElem(this.bankIndices[i1], k + this.bandOffsets[i1]);
      }
      paramObject = arrayOfInt;
      break;
    case 4: 
      float[] arrayOfFloat;
      if (paramObject == null) {
        arrayOfFloat = new float[j];
      } else {
        arrayOfFloat = (float[])paramObject;
      }
      for (int i2 = 0; i2 < j; i2++) {
        arrayOfFloat[i2] = paramDataBuffer.getElemFloat(this.bankIndices[i2], k + this.bandOffsets[i2]);
      }
      paramObject = arrayOfFloat;
      break;
    case 5: 
      double[] arrayOfDouble;
      if (paramObject == null) {
        arrayOfDouble = new double[j];
      } else {
        arrayOfDouble = (double[])paramObject;
      }
      for (int i3 = 0; i3 < j; i3++) {
        arrayOfDouble[i3] = paramDataBuffer.getElemDouble(this.bankIndices[i3], k + this.bandOffsets[i3]);
      }
      paramObject = arrayOfDouble;
    }
    return paramObject;
  }
  
  public int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= this.width) || (paramInt2 >= this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[this.numBands];
    }
    int i = paramInt2 * this.scanlineStride + paramInt1;
    for (int j = 0; j < this.numBands; j++) {
      arrayOfInt[j] = paramDataBuffer.getElem(this.bankIndices[j], i + this.bandOffsets[j]);
    }
    return arrayOfInt;
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= this.width) || (paramInt3 > this.width) || (i < 0) || (i > this.width) || (paramInt2 < 0) || (paramInt2 >= this.height) || (paramInt4 > this.height) || (j < 0) || (j > this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4 * this.numBands];
    }
    for (int k = 0; k < this.numBands; k++)
    {
      int m = paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[k];
      int n = k;
      int i1 = this.bankIndices[k];
      for (int i2 = 0; i2 < paramInt4; i2++)
      {
        int i3 = m;
        for (int i4 = 0; i4 < paramInt3; i4++)
        {
          arrayOfInt[n] = paramDataBuffer.getElem(i1, i3++);
          n += this.numBands;
        }
        m += this.scanlineStride;
      }
    }
    return arrayOfInt;
  }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= this.width) || (paramInt2 >= this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = paramDataBuffer.getElem(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3]);
    return i;
  }
  
  public float getSampleFloat(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= this.width) || (paramInt2 >= this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    float f = paramDataBuffer.getElemFloat(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3]);
    return f;
  }
  
  public double getSampleDouble(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= this.width) || (paramInt2 >= this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    double d = paramDataBuffer.getElemDouble(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3]);
    return d;
  }
  
  public int[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt3 > this.width) || (paramInt2 + paramInt4 > this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4];
    }
    int i = paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt5];
    int j = 0;
    int k = this.bankIndices[paramInt5];
    for (int m = 0; m < paramInt4; m++)
    {
      int n = i;
      for (int i1 = 0; i1 < paramInt3; i1++) {
        arrayOfInt[(j++)] = paramDataBuffer.getElem(k, n++);
      }
      i += this.scanlineStride;
    }
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= this.width) || (paramInt2 >= this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = getTransferType();
    int j = getNumDataElements();
    int k = paramInt2 * this.scanlineStride + paramInt1;
    switch (i)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      for (int m = 0; m < j; m++) {
        paramDataBuffer.setElem(this.bankIndices[m], k + this.bandOffsets[m], arrayOfByte[m] & 0xFF);
      }
      break;
    case 1: 
    case 2: 
      short[] arrayOfShort = (short[])paramObject;
      for (int n = 0; n < j; n++) {
        paramDataBuffer.setElem(this.bankIndices[n], k + this.bandOffsets[n], arrayOfShort[n] & 0xFFFF);
      }
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      for (int i1 = 0; i1 < j; i1++) {
        paramDataBuffer.setElem(this.bankIndices[i1], k + this.bandOffsets[i1], arrayOfInt[i1]);
      }
      break;
    case 4: 
      float[] arrayOfFloat = (float[])paramObject;
      for (int i2 = 0; i2 < j; i2++) {
        paramDataBuffer.setElemFloat(this.bankIndices[i2], k + this.bandOffsets[i2], arrayOfFloat[i2]);
      }
      break;
    case 5: 
      double[] arrayOfDouble = (double[])paramObject;
      for (int i3 = 0; i3 < j; i3++) {
        paramDataBuffer.setElemDouble(this.bankIndices[i3], k + this.bandOffsets[i3], arrayOfDouble[i3]);
      }
    }
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= this.width) || (paramInt2 >= this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = paramInt2 * this.scanlineStride + paramInt1;
    for (int j = 0; j < this.numBands; j++) {
      paramDataBuffer.setElem(this.bankIndices[j], i + this.bandOffsets[j], paramArrayOfInt[j]);
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= this.width) || (paramInt3 > this.width) || (i < 0) || (i > this.width) || (paramInt2 < 0) || (paramInt2 >= this.height) || (paramInt4 > this.height) || (j < 0) || (j > this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    for (int k = 0; k < this.numBands; k++)
    {
      int m = paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[k];
      int n = k;
      int i1 = this.bankIndices[k];
      for (int i2 = 0; i2 < paramInt4; i2++)
      {
        int i3 = m;
        for (int i4 = 0; i4 < paramInt3; i4++)
        {
          paramDataBuffer.setElem(i1, i3++, paramArrayOfInt[n]);
          n += this.numBands;
        }
        m += this.scanlineStride;
      }
    }
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= this.width) || (paramInt2 >= this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    paramDataBuffer.setElem(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3], paramInt4);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= this.width) || (paramInt2 >= this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    paramDataBuffer.setElemFloat(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3], paramFloat);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= this.width) || (paramInt2 >= this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    paramDataBuffer.setElemDouble(this.bankIndices[paramInt3], paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt3], paramDouble);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt3 > this.width) || (paramInt2 + paramInt4 > this.height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = paramInt2 * this.scanlineStride + paramInt1 + this.bandOffsets[paramInt5];
    int j = 0;
    int k = this.bankIndices[paramInt5];
    for (int m = 0; m < paramInt4; m++)
    {
      int n = i;
      for (int i1 = 0; i1 < paramInt3; i1++) {
        paramDataBuffer.setElem(k, n++, paramArrayOfInt[(j++)]);
      }
      i += this.scanlineStride;
    }
  }
  
  private static int[] createOffsetArray(int paramInt)
  {
    int[] arrayOfInt = new int[paramInt];
    for (int i = 0; i < paramInt; i++) {
      arrayOfInt[i] = 0;
    }
    return arrayOfInt;
  }
  
  private static int[] createIndicesArray(int paramInt)
  {
    int[] arrayOfInt = new int[paramInt];
    for (int i = 0; i < paramInt; i++) {
      arrayOfInt[i] = i;
    }
    return arrayOfInt;
  }
  
  public int hashCode()
  {
    return super.hashCode() ^ 0x2;
  }
}
