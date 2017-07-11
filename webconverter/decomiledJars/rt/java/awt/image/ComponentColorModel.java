package java.awt.image;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.util.Arrays;

public class ComponentColorModel
  extends ColorModel
{
  private boolean signed;
  private boolean is_sRGB_stdScale;
  private boolean is_LinearRGB_stdScale;
  private boolean is_LinearGray_stdScale;
  private boolean is_ICCGray_stdScale;
  private byte[] tosRGB8LUT;
  private byte[] fromsRGB8LUT8;
  private short[] fromsRGB8LUT16;
  private byte[] fromLinearGray16ToOtherGray8LUT;
  private short[] fromLinearGray16ToOtherGray16LUT;
  private boolean needScaleInit;
  private boolean noUnnorm;
  private boolean nonStdScale;
  private float[] min;
  private float[] diffMinMax;
  private float[] compOffset;
  private float[] compScale;
  
  public ComponentColorModel(ColorSpace paramColorSpace, int[] paramArrayOfInt, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
  {
    super(bitsHelper(paramInt2, paramColorSpace, paramBoolean1), bitsArrayHelper(paramArrayOfInt, paramInt2, paramColorSpace, paramBoolean1), paramColorSpace, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
    switch (paramInt2)
    {
    case 0: 
    case 1: 
    case 3: 
      this.signed = false;
      this.needScaleInit = true;
      break;
    case 2: 
      this.signed = true;
      this.needScaleInit = true;
      break;
    case 4: 
    case 5: 
      this.signed = true;
      this.needScaleInit = false;
      this.noUnnorm = true;
      this.nonStdScale = false;
      break;
    default: 
      throw new IllegalArgumentException("This constructor is not compatible with transferType " + paramInt2);
    }
    setupLUTs();
  }
  
  public ComponentColorModel(ColorSpace paramColorSpace, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
  {
    this(paramColorSpace, null, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
  }
  
  private static int bitsHelper(int paramInt, ColorSpace paramColorSpace, boolean paramBoolean)
  {
    int i = DataBuffer.getDataTypeSize(paramInt);
    int j = paramColorSpace.getNumComponents();
    if (paramBoolean) {
      j++;
    }
    return i * j;
  }
  
  private static int[] bitsArrayHelper(int[] paramArrayOfInt, int paramInt, ColorSpace paramColorSpace, boolean paramBoolean)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 3: 
      if (paramArrayOfInt != null) {
        return paramArrayOfInt;
      }
      break;
    }
    int i = DataBuffer.getDataTypeSize(paramInt);
    int j = paramColorSpace.getNumComponents();
    if (paramBoolean) {
      j++;
    }
    int[] arrayOfInt = new int[j];
    for (int k = 0; k < j; k++) {
      arrayOfInt[k] = i;
    }
    return arrayOfInt;
  }
  
  private void setupLUTs()
  {
    if (this.is_sRGB)
    {
      this.is_sRGB_stdScale = true;
      this.nonStdScale = false;
    }
    else if (ColorModel.isLinearRGBspace(this.colorSpace))
    {
      this.is_LinearRGB_stdScale = true;
      this.nonStdScale = false;
      if (this.transferType == 0)
      {
        this.tosRGB8LUT = ColorModel.getLinearRGB8TosRGB8LUT();
        this.fromsRGB8LUT8 = ColorModel.getsRGB8ToLinearRGB8LUT();
      }
      else
      {
        this.tosRGB8LUT = ColorModel.getLinearRGB16TosRGB8LUT();
        this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
      }
    }
    else if ((this.colorSpaceType == 6) && ((this.colorSpace instanceof ICC_ColorSpace)) && (this.colorSpace.getMinValue(0) == 0.0F) && (this.colorSpace.getMaxValue(0) == 1.0F))
    {
      ICC_ColorSpace localICC_ColorSpace = (ICC_ColorSpace)this.colorSpace;
      this.is_ICCGray_stdScale = true;
      this.nonStdScale = false;
      this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
      if (ColorModel.isLinearGRAYspace(localICC_ColorSpace))
      {
        this.is_LinearGray_stdScale = true;
        if (this.transferType == 0) {
          this.tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(localICC_ColorSpace);
        } else {
          this.tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(localICC_ColorSpace);
        }
      }
      else if (this.transferType == 0)
      {
        this.tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(localICC_ColorSpace);
        this.fromLinearGray16ToOtherGray8LUT = ColorModel.getLinearGray16ToOtherGray8LUT(localICC_ColorSpace);
      }
      else
      {
        this.tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(localICC_ColorSpace);
        this.fromLinearGray16ToOtherGray16LUT = ColorModel.getLinearGray16ToOtherGray16LUT(localICC_ColorSpace);
      }
    }
    else if (this.needScaleInit)
    {
      this.nonStdScale = false;
      for (int i = 0; i < this.numColorComponents; i++) {
        if ((this.colorSpace.getMinValue(i) != 0.0F) || (this.colorSpace.getMaxValue(i) != 1.0F))
        {
          this.nonStdScale = true;
          break;
        }
      }
      if (this.nonStdScale)
      {
        this.min = new float[this.numColorComponents];
        this.diffMinMax = new float[this.numColorComponents];
        for (i = 0; i < this.numColorComponents; i++)
        {
          this.min[i] = this.colorSpace.getMinValue(i);
          this.diffMinMax[i] = (this.colorSpace.getMaxValue(i) - this.min[i]);
        }
      }
    }
  }
  
  private void initScale()
  {
    this.needScaleInit = false;
    if ((this.nonStdScale) || (this.signed)) {
      this.noUnnorm = true;
    } else {
      this.noUnnorm = false;
    }
    Object localObject;
    int j;
    float[] arrayOfFloat1;
    float[] arrayOfFloat2;
    switch (this.transferType)
    {
    case 0: 
      localObject = new byte[this.numComponents];
      for (j = 0; j < this.numColorComponents; j++) {
        localObject[j] = 0;
      }
      if (this.supportsAlpha) {
        localObject[this.numColorComponents] = ((byte)((1 << this.nBits[this.numColorComponents]) - 1));
      }
      arrayOfFloat1 = getNormalizedComponents(localObject, null, 0);
      for (j = 0; j < this.numColorComponents; j++) {
        localObject[j] = ((byte)((1 << this.nBits[j]) - 1));
      }
      arrayOfFloat2 = getNormalizedComponents(localObject, null, 0);
      break;
    case 1: 
      localObject = new short[this.numComponents];
      for (j = 0; j < this.numColorComponents; j++) {
        localObject[j] = 0;
      }
      if (this.supportsAlpha) {
        localObject[this.numColorComponents] = ((short)((1 << this.nBits[this.numColorComponents]) - 1));
      }
      arrayOfFloat1 = getNormalizedComponents(localObject, null, 0);
      for (j = 0; j < this.numColorComponents; j++) {
        localObject[j] = ((short)((1 << this.nBits[j]) - 1));
      }
      arrayOfFloat2 = getNormalizedComponents(localObject, null, 0);
      break;
    case 3: 
      localObject = new int[this.numComponents];
      for (j = 0; j < this.numColorComponents; j++) {
        localObject[j] = 0;
      }
      if (this.supportsAlpha) {
        localObject[this.numColorComponents] = ((1 << this.nBits[this.numColorComponents]) - 1);
      }
      arrayOfFloat1 = getNormalizedComponents(localObject, null, 0);
      for (j = 0; j < this.numColorComponents; j++) {
        localObject[j] = ((1 << this.nBits[j]) - 1);
      }
      arrayOfFloat2 = getNormalizedComponents(localObject, null, 0);
      break;
    case 2: 
      localObject = new short[this.numComponents];
      for (j = 0; j < this.numColorComponents; j++) {
        localObject[j] = 0;
      }
      if (this.supportsAlpha) {
        localObject[this.numColorComponents] = 32767;
      }
      arrayOfFloat1 = getNormalizedComponents(localObject, null, 0);
      for (j = 0; j < this.numColorComponents; j++) {
        localObject[j] = 32767;
      }
      arrayOfFloat2 = getNormalizedComponents(localObject, null, 0);
      break;
    default: 
      arrayOfFloat1 = arrayOfFloat2 = null;
    }
    this.nonStdScale = false;
    for (int i = 0; i < this.numColorComponents; i++) {
      if ((arrayOfFloat1[i] != 0.0F) || (arrayOfFloat2[i] != 1.0F))
      {
        this.nonStdScale = true;
        break;
      }
    }
    if (this.nonStdScale)
    {
      this.noUnnorm = true;
      this.is_sRGB_stdScale = false;
      this.is_LinearRGB_stdScale = false;
      this.is_LinearGray_stdScale = false;
      this.is_ICCGray_stdScale = false;
      this.compOffset = new float[this.numColorComponents];
      this.compScale = new float[this.numColorComponents];
      for (i = 0; i < this.numColorComponents; i++)
      {
        this.compOffset[i] = arrayOfFloat1[i];
        this.compScale[i] = (1.0F / (arrayOfFloat2[i] - arrayOfFloat1[i]));
      }
    }
  }
  
  private int getRGBComponent(int paramInt1, int paramInt2)
  {
    if (this.numComponents > 1) {
      throw new IllegalArgumentException("More than one component per pixel");
    }
    if (this.signed) {
      throw new IllegalArgumentException("Component value is signed");
    }
    if (this.needScaleInit) {
      initScale();
    }
    Object localObject1 = null;
    switch (this.transferType)
    {
    case 0: 
      localObject2 = new byte[] { (byte)paramInt1 };
      localObject1 = localObject2;
      break;
    case 1: 
      localObject2 = new short[] { (short)paramInt1 };
      localObject1 = localObject2;
      break;
    case 3: 
      localObject2 = new int[] { paramInt1 };
      localObject1 = localObject2;
    }
    Object localObject2 = getNormalizedComponents(localObject1, null, 0);
    float[] arrayOfFloat = this.colorSpace.toRGB((float[])localObject2);
    return (int)(arrayOfFloat[paramInt2] * 255.0F + 0.5F);
  }
  
  public int getRed(int paramInt)
  {
    return getRGBComponent(paramInt, 0);
  }
  
  public int getGreen(int paramInt)
  {
    return getRGBComponent(paramInt, 1);
  }
  
  public int getBlue(int paramInt)
  {
    return getRGBComponent(paramInt, 2);
  }
  
  public int getAlpha(int paramInt)
  {
    if (!this.supportsAlpha) {
      return 255;
    }
    if (this.numComponents > 1) {
      throw new IllegalArgumentException("More than one component per pixel");
    }
    if (this.signed) {
      throw new IllegalArgumentException("Component value is signed");
    }
    return (int)(paramInt / ((1 << this.nBits[0]) - 1) * 255.0F + 0.5F);
  }
  
  public int getRGB(int paramInt)
  {
    if (this.numComponents > 1) {
      throw new IllegalArgumentException("More than one component per pixel");
    }
    if (this.signed) {
      throw new IllegalArgumentException("Component value is signed");
    }
    return getAlpha(paramInt) << 24 | getRed(paramInt) << 16 | getGreen(paramInt) << 8 | getBlue(paramInt) << 0;
  }
  
  private int extractComponent(Object paramObject, int paramInt1, int paramInt2)
  {
    int i = (this.supportsAlpha) && (this.isAlphaPremultiplied) ? 1 : 0;
    int j = 0;
    int m = (1 << this.nBits[paramInt1]) - 1;
    Object localObject;
    float f2;
    int k;
    switch (this.transferType)
    {
    case 2: 
      localObject = (short[])paramObject;
      f2 = (1 << paramInt2) - 1;
      if (i != 0)
      {
        int n = localObject[this.numColorComponents];
        if (n != 0) {
          return (int)(localObject[paramInt1] / n * f2 + 0.5F);
        }
        return 0;
      }
      return (int)(localObject[paramInt1] / 32767.0F * f2 + 0.5F);
    case 4: 
      localObject = (float[])paramObject;
      f2 = (1 << paramInt2) - 1;
      if (i != 0)
      {
        float f4 = localObject[this.numColorComponents];
        if (f4 != 0.0F) {
          return (int)(localObject[paramInt1] / f4 * f2 + 0.5F);
        }
        return 0;
      }
      return (int)(localObject[paramInt1] * f2 + 0.5F);
    case 5: 
      localObject = (double[])paramObject;
      double d1 = (1 << paramInt2) - 1;
      if (i != 0)
      {
        double d2 = localObject[this.numColorComponents];
        if (d2 != 0.0D) {
          return (int)(localObject[paramInt1] / d2 * d1 + 0.5D);
        }
        return 0;
      }
      return (int)(localObject[paramInt1] * d1 + 0.5D);
    case 0: 
      localObject = (byte[])paramObject;
      k = localObject[paramInt1] & m;
      paramInt2 = 8;
      if (i != 0) {
        j = localObject[this.numColorComponents] & m;
      }
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      k = arrayOfShort[paramInt1] & m;
      if (i != 0) {
        j = arrayOfShort[this.numColorComponents] & m;
      }
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      k = arrayOfInt[paramInt1];
      if (i != 0) {
        j = arrayOfInt[this.numColorComponents];
      }
      break;
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    }
    float f1;
    float f3;
    if (i != 0)
    {
      if (j != 0)
      {
        f1 = (1 << paramInt2) - 1;
        f3 = k / m;
        float f5 = ((1 << this.nBits[this.numColorComponents]) - 1) / j;
        return (int)(f3 * f5 * f1 + 0.5F);
      }
      return 0;
    }
    if (this.nBits[paramInt1] != paramInt2)
    {
      f1 = (1 << paramInt2) - 1;
      f3 = k / m;
      return (int)(f3 * f1 + 0.5F);
    }
    return k;
  }
  
  private int getRGBComponent(Object paramObject, int paramInt)
  {
    if (this.needScaleInit) {
      initScale();
    }
    if (this.is_sRGB_stdScale) {
      return extractComponent(paramObject, paramInt, 8);
    }
    int i;
    if (this.is_LinearRGB_stdScale)
    {
      i = extractComponent(paramObject, paramInt, 16);
      return this.tosRGB8LUT[i] & 0xFF;
    }
    if (this.is_ICCGray_stdScale)
    {
      i = extractComponent(paramObject, 0, 16);
      return this.tosRGB8LUT[i] & 0xFF;
    }
    float[] arrayOfFloat1 = getNormalizedComponents(paramObject, null, 0);
    float[] arrayOfFloat2 = this.colorSpace.toRGB(arrayOfFloat1);
    return (int)(arrayOfFloat2[paramInt] * 255.0F + 0.5F);
  }
  
  public int getRed(Object paramObject)
  {
    return getRGBComponent(paramObject, 0);
  }
  
  public int getGreen(Object paramObject)
  {
    return getRGBComponent(paramObject, 1);
  }
  
  public int getBlue(Object paramObject)
  {
    return getRGBComponent(paramObject, 2);
  }
  
  public int getAlpha(Object paramObject)
  {
    if (!this.supportsAlpha) {
      return 255;
    }
    int i = 0;
    int j = this.numColorComponents;
    int k = (1 << this.nBits[j]) - 1;
    switch (this.transferType)
    {
    case 2: 
      short[] arrayOfShort1 = (short[])paramObject;
      i = (int)(arrayOfShort1[j] / 32767.0F * 255.0F + 0.5F);
      return i;
    case 4: 
      float[] arrayOfFloat = (float[])paramObject;
      i = (int)(arrayOfFloat[j] * 255.0F + 0.5F);
      return i;
    case 5: 
      double[] arrayOfDouble = (double[])paramObject;
      i = (int)(arrayOfDouble[j] * 255.0D + 0.5D);
      return i;
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[j] & k;
      break;
    case 1: 
      short[] arrayOfShort2 = (short[])paramObject;
      i = arrayOfShort2[j] & k;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[j];
      break;
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    }
    if (this.nBits[j] == 8) {
      return i;
    }
    return (int)(i / ((1 << this.nBits[j]) - 1) * 255.0F + 0.5F);
  }
  
  public int getRGB(Object paramObject)
  {
    if (this.needScaleInit) {
      initScale();
    }
    if ((this.is_sRGB_stdScale) || (this.is_LinearRGB_stdScale)) {
      return getAlpha(paramObject) << 24 | getRed(paramObject) << 16 | getGreen(paramObject) << 8 | getBlue(paramObject);
    }
    if (this.colorSpaceType == 6)
    {
      int i = getRed(paramObject);
      return getAlpha(paramObject) << 24 | i << 16 | i << 8 | i;
    }
    float[] arrayOfFloat1 = getNormalizedComponents(paramObject, null, 0);
    float[] arrayOfFloat2 = this.colorSpace.toRGB(arrayOfFloat1);
    return getAlpha(paramObject) << 24 | (int)(arrayOfFloat2[0] * 255.0F + 0.5F) << 16 | (int)(arrayOfFloat2[1] * 255.0F + 0.5F) << 8 | (int)(arrayOfFloat2[2] * 255.0F + 0.5F) << 0;
  }
  
  public Object getDataElements(int paramInt, Object paramObject)
  {
    int i = paramInt >> 16 & 0xFF;
    int j = paramInt >> 8 & 0xFF;
    int k = paramInt & 0xFF;
    if (this.needScaleInit) {
      initScale();
    }
    Object localObject1;
    int m;
    int i7;
    if (this.signed)
    {
      float f1;
      float[] arrayOfFloat2;
      switch (this.transferType)
      {
      case 2: 
        if (paramObject == null) {
          localObject1 = new short[this.numComponents];
        } else {
          localObject1 = (short[])paramObject;
        }
        if ((this.is_sRGB_stdScale) || (this.is_LinearRGB_stdScale))
        {
          f1 = 128.49803F;
          if (this.is_LinearRGB_stdScale)
          {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            f1 = 0.49999237F;
          }
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[3] = ((short)(int)(m * 128.49803F + 0.5F));
            if (this.isAlphaPremultiplied) {
              f1 = m * f1 * 0.003921569F;
            }
          }
          localObject1[0] = ((short)(int)(i * f1 + 0.5F));
          localObject1[1] = ((short)(int)(j * f1 + 0.5F));
          localObject1[2] = ((short)(int)(k * f1 + 0.5F));
        }
        else if (this.is_LinearGray_stdScale)
        {
          i = this.fromsRGB8LUT16[i] & 0xFFFF;
          j = this.fromsRGB8LUT16[j] & 0xFFFF;
          k = this.fromsRGB8LUT16[k] & 0xFFFF;
          float f4 = (0.2125F * i + 0.7154F * j + 0.0721F * k) / 65535.0F;
          f1 = 32767.0F;
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[1] = ((short)(int)(m * 128.49803F + 0.5F));
            if (this.isAlphaPremultiplied) {
              f1 = m * f1 * 0.003921569F;
            }
          }
          localObject1[0] = ((short)(int)(f4 * f1 + 0.5F));
        }
        else if (this.is_ICCGray_stdScale)
        {
          i = this.fromsRGB8LUT16[i] & 0xFFFF;
          j = this.fromsRGB8LUT16[j] & 0xFFFF;
          k = this.fromsRGB8LUT16[k] & 0xFFFF;
          int i4 = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
          i4 = this.fromLinearGray16ToOtherGray16LUT[i4] & 0xFFFF;
          f1 = 0.49999237F;
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[1] = ((short)(int)(m * 128.49803F + 0.5F));
            if (this.isAlphaPremultiplied) {
              f1 = m * f1 * 0.003921569F;
            }
          }
          localObject1[0] = ((short)(int)(i4 * f1 + 0.5F));
        }
        else
        {
          f1 = 0.003921569F;
          float[] arrayOfFloat1 = new float[3];
          arrayOfFloat1[0] = (i * f1);
          arrayOfFloat1[1] = (j * f1);
          arrayOfFloat1[2] = (k * f1);
          arrayOfFloat1 = this.colorSpace.fromRGB(arrayOfFloat1);
          if (this.nonStdScale) {
            for (i7 = 0; i7 < this.numColorComponents; i7++)
            {
              arrayOfFloat1[i7] = ((arrayOfFloat1[i7] - this.compOffset[i7]) * this.compScale[i7]);
              if (arrayOfFloat1[i7] < 0.0F) {
                arrayOfFloat1[i7] = 0.0F;
              }
              if (arrayOfFloat1[i7] > 1.0F) {
                arrayOfFloat1[i7] = 1.0F;
              }
            }
          }
          f1 = 32767.0F;
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[this.numColorComponents] = ((short)(int)(m * 128.49803F + 0.5F));
            if (this.isAlphaPremultiplied) {
              f1 *= m * 0.003921569F;
            }
          }
          for (i7 = 0; i7 < this.numColorComponents; i7++) {
            localObject1[i7] = ((short)(int)(arrayOfFloat1[i7] * f1 + 0.5F));
          }
        }
        return localObject1;
      case 4: 
        if (paramObject == null) {
          localObject1 = new float[this.numComponents];
        } else {
          localObject1 = (float[])paramObject;
        }
        if ((this.is_sRGB_stdScale) || (this.is_LinearRGB_stdScale))
        {
          if (this.is_LinearRGB_stdScale)
          {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            f1 = 1.5259022E-5F;
          }
          else
          {
            f1 = 0.003921569F;
          }
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[3] = (m * 0.003921569F);
            if (this.isAlphaPremultiplied) {
              f1 *= localObject1[3];
            }
          }
          localObject1[0] = (i * f1);
          localObject1[1] = (j * f1);
          localObject1[2] = (k * f1);
        }
        else if (this.is_LinearGray_stdScale)
        {
          i = this.fromsRGB8LUT16[i] & 0xFFFF;
          j = this.fromsRGB8LUT16[j] & 0xFFFF;
          k = this.fromsRGB8LUT16[k] & 0xFFFF;
          localObject1[0] = ((0.2125F * i + 0.7154F * j + 0.0721F * k) / 65535.0F);
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[1] = (m * 0.003921569F);
            if (this.isAlphaPremultiplied) {
              localObject1[0] *= localObject1[1];
            }
          }
        }
        else if (this.is_ICCGray_stdScale)
        {
          i = this.fromsRGB8LUT16[i] & 0xFFFF;
          j = this.fromsRGB8LUT16[j] & 0xFFFF;
          k = this.fromsRGB8LUT16[k] & 0xFFFF;
          int i5 = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
          localObject1[0] = ((this.fromLinearGray16ToOtherGray16LUT[i5] & 0xFFFF) / 65535.0F);
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[1] = (m * 0.003921569F);
            if (this.isAlphaPremultiplied) {
              localObject1[0] *= localObject1[1];
            }
          }
        }
        else
        {
          arrayOfFloat2 = new float[3];
          f1 = 0.003921569F;
          arrayOfFloat2[0] = (i * f1);
          arrayOfFloat2[1] = (j * f1);
          arrayOfFloat2[2] = (k * f1);
          arrayOfFloat2 = this.colorSpace.fromRGB(arrayOfFloat2);
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[this.numColorComponents] = (m * f1);
            if (this.isAlphaPremultiplied)
            {
              f1 *= m;
              for (i7 = 0; i7 < this.numColorComponents; i7++) {
                arrayOfFloat2[i7] *= f1;
              }
            }
          }
          for (i7 = 0; i7 < this.numColorComponents; i7++) {
            localObject1[i7] = arrayOfFloat2[i7];
          }
        }
        return localObject1;
      case 5: 
        if (paramObject == null) {
          localObject1 = new double[this.numComponents];
        } else {
          localObject1 = (double[])paramObject;
        }
        if ((this.is_sRGB_stdScale) || (this.is_LinearRGB_stdScale))
        {
          double d;
          if (this.is_LinearRGB_stdScale)
          {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            d = 1.5259021896696422E-5D;
          }
          else
          {
            d = 0.00392156862745098D;
          }
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[3] = (m * 0.00392156862745098D);
            if (this.isAlphaPremultiplied) {
              d *= localObject1[3];
            }
          }
          localObject1[0] = (i * d);
          localObject1[1] = (j * d);
          localObject1[2] = (k * d);
        }
        else if (this.is_LinearGray_stdScale)
        {
          i = this.fromsRGB8LUT16[i] & 0xFFFF;
          j = this.fromsRGB8LUT16[j] & 0xFFFF;
          k = this.fromsRGB8LUT16[k] & 0xFFFF;
          localObject1[0] = ((0.2125D * i + 0.7154D * j + 0.0721D * k) / 65535.0D);
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[1] = (m * 0.00392156862745098D);
            if (this.isAlphaPremultiplied) {
              localObject1[0] *= localObject1[1];
            }
          }
        }
        else if (this.is_ICCGray_stdScale)
        {
          i = this.fromsRGB8LUT16[i] & 0xFFFF;
          j = this.fromsRGB8LUT16[j] & 0xFFFF;
          k = this.fromsRGB8LUT16[k] & 0xFFFF;
          int n = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
          localObject1[0] = ((this.fromLinearGray16ToOtherGray16LUT[n] & 0xFFFF) / 65535.0D);
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[1] = (m * 0.00392156862745098D);
            if (this.isAlphaPremultiplied) {
              localObject1[0] *= localObject1[1];
            }
          }
        }
        else
        {
          float f2 = 0.003921569F;
          arrayOfFloat2 = new float[3];
          arrayOfFloat2[0] = (i * f2);
          arrayOfFloat2[1] = (j * f2);
          arrayOfFloat2[2] = (k * f2);
          arrayOfFloat2 = this.colorSpace.fromRGB(arrayOfFloat2);
          if (this.supportsAlpha)
          {
            m = paramInt >> 24 & 0xFF;
            localObject1[this.numColorComponents] = (m * 0.00392156862745098D);
            if (this.isAlphaPremultiplied)
            {
              f2 *= m;
              for (i7 = 0; i7 < this.numColorComponents; i7++) {
                arrayOfFloat2[i7] *= f2;
              }
            }
          }
          for (i7 = 0; i7 < this.numColorComponents; i7++) {
            localObject1[i7] = arrayOfFloat2[i7];
          }
        }
        return localObject1;
      }
    }
    if ((this.transferType == 3) && (paramObject != null)) {
      localObject1 = (int[])paramObject;
    } else {
      localObject1 = new int[this.numComponents];
    }
    float f5;
    Object localObject2;
    if ((this.is_sRGB_stdScale) || (this.is_LinearRGB_stdScale))
    {
      int i1;
      if (this.is_LinearRGB_stdScale)
      {
        if (this.transferType == 0)
        {
          i = this.fromsRGB8LUT8[i] & 0xFF;
          j = this.fromsRGB8LUT8[j] & 0xFF;
          k = this.fromsRGB8LUT8[k] & 0xFF;
          i1 = 8;
          f5 = 0.003921569F;
        }
        else
        {
          i = this.fromsRGB8LUT16[i] & 0xFFFF;
          j = this.fromsRGB8LUT16[j] & 0xFFFF;
          k = this.fromsRGB8LUT16[k] & 0xFFFF;
          i1 = 16;
          f5 = 1.5259022E-5F;
        }
      }
      else
      {
        i1 = 8;
        f5 = 0.003921569F;
      }
      if (this.supportsAlpha)
      {
        m = paramInt >> 24 & 0xFF;
        if (this.nBits[3] == 8) {
          localObject1[3] = m;
        } else {
          localObject1[3] = ((int)(m * 0.003921569F * ((1 << this.nBits[3]) - 1) + 0.5F));
        }
        if (this.isAlphaPremultiplied)
        {
          f5 *= m * 0.003921569F;
          i1 = -1;
        }
      }
      if (this.nBits[0] == i1) {
        localObject1[0] = i;
      } else {
        localObject1[0] = ((int)(i * f5 * ((1 << this.nBits[0]) - 1) + 0.5F));
      }
      if (this.nBits[1] == i1) {
        localObject1[1] = j;
      } else {
        localObject1[1] = ((int)(j * f5 * ((1 << this.nBits[1]) - 1) + 0.5F));
      }
      if (this.nBits[2] == i1) {
        localObject1[2] = k;
      } else {
        localObject1[2] = ((int)(k * f5 * ((1 << this.nBits[2]) - 1) + 0.5F));
      }
    }
    else if (this.is_LinearGray_stdScale)
    {
      i = this.fromsRGB8LUT16[i] & 0xFFFF;
      j = this.fromsRGB8LUT16[j] & 0xFFFF;
      k = this.fromsRGB8LUT16[k] & 0xFFFF;
      float f3 = (0.2125F * i + 0.7154F * j + 0.0721F * k) / 65535.0F;
      if (this.supportsAlpha)
      {
        m = paramInt >> 24 & 0xFF;
        if (this.nBits[1] == 8) {
          localObject1[1] = m;
        } else {
          localObject1[1] = ((int)(m * 0.003921569F * ((1 << this.nBits[1]) - 1) + 0.5F));
        }
        if (this.isAlphaPremultiplied) {
          f3 *= m * 0.003921569F;
        }
      }
      localObject1[0] = ((int)(f3 * ((1 << this.nBits[0]) - 1) + 0.5F));
    }
    else if (this.is_ICCGray_stdScale)
    {
      i = this.fromsRGB8LUT16[i] & 0xFFFF;
      j = this.fromsRGB8LUT16[j] & 0xFFFF;
      k = this.fromsRGB8LUT16[k] & 0xFFFF;
      int i2 = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
      f5 = (this.fromLinearGray16ToOtherGray16LUT[i2] & 0xFFFF) / 65535.0F;
      if (this.supportsAlpha)
      {
        m = paramInt >> 24 & 0xFF;
        if (this.nBits[1] == 8) {
          localObject1[1] = m;
        } else {
          localObject1[1] = ((int)(m * 0.003921569F * ((1 << this.nBits[1]) - 1) + 0.5F));
        }
        if (this.isAlphaPremultiplied) {
          f5 *= m * 0.003921569F;
        }
      }
      localObject1[0] = ((int)(f5 * ((1 << this.nBits[0]) - 1) + 0.5F));
    }
    else
    {
      localObject2 = new float[3];
      f5 = 0.003921569F;
      localObject2[0] = (i * f5);
      localObject2[1] = (j * f5);
      localObject2[2] = (k * f5);
      localObject2 = this.colorSpace.fromRGB((float[])localObject2);
      if (this.nonStdScale) {
        for (i7 = 0; i7 < this.numColorComponents; i7++)
        {
          localObject2[i7] = ((localObject2[i7] - this.compOffset[i7]) * this.compScale[i7]);
          if (localObject2[i7] < 0.0F) {
            localObject2[i7] = 0.0F;
          }
          if (localObject2[i7] > 1.0F) {
            localObject2[i7] = 1.0F;
          }
        }
      }
      if (this.supportsAlpha)
      {
        m = paramInt >> 24 & 0xFF;
        if (this.nBits[this.numColorComponents] == 8) {
          localObject1[this.numColorComponents] = m;
        } else {
          localObject1[this.numColorComponents] = ((int)(m * f5 * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F));
        }
        if (this.isAlphaPremultiplied)
        {
          f5 *= m;
          for (i7 = 0; i7 < this.numColorComponents; i7++) {
            localObject2[i7] *= f5;
          }
        }
      }
      for (i7 = 0; i7 < this.numColorComponents; i7++) {
        localObject1[i7] = ((int)(localObject2[i7] * ((1 << this.nBits[i7]) - 1) + 0.5F));
      }
    }
    int i6;
    switch (this.transferType)
    {
    case 0: 
      if (paramObject == null) {
        localObject2 = new byte[this.numComponents];
      } else {
        localObject2 = (byte[])paramObject;
      }
      for (i6 = 0; i6 < this.numComponents; i6++) {
        localObject2[i6] = ((byte)(0xFF & localObject1[i6]));
      }
      return localObject2;
    case 1: 
      if (paramObject == null) {
        localObject2 = new short[this.numComponents];
      } else {
        localObject2 = (short[])paramObject;
      }
      for (i6 = 0; i6 < this.numComponents; i6++) {
        localObject2[i6] = ((short)(localObject1[i6] & 0xFFFF));
      }
      return localObject2;
    case 3: 
      if (this.maxBits > 23) {
        for (int i3 = 0; i3 < this.numComponents; i3++) {
          if (localObject1[i3] > (1 << this.nBits[i3]) - 1) {
            localObject1[i3] = ((1 << this.nBits[i3]) - 1);
          }
        }
      }
      return localObject1;
    }
    throw new IllegalArgumentException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int[] getComponents(int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    if (this.numComponents > 1) {
      throw new IllegalArgumentException("More than one component per pixel");
    }
    if (this.needScaleInit) {
      initScale();
    }
    if (this.noUnnorm) {
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
    }
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[paramInt2 + 1];
    }
    paramArrayOfInt[(paramInt2 + 0)] = (paramInt1 & (1 << this.nBits[0]) - 1);
    return paramArrayOfInt;
  }
  
  public int[] getComponents(Object paramObject, int[] paramArrayOfInt, int paramInt)
  {
    if (this.needScaleInit) {
      initScale();
    }
    if (this.noUnnorm) {
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
    }
    int[] arrayOfInt;
    if ((paramObject instanceof int[]))
    {
      arrayOfInt = (int[])paramObject;
    }
    else
    {
      arrayOfInt = DataBuffer.toIntArray(paramObject);
      if (arrayOfInt == null) {
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      }
    }
    if (arrayOfInt.length < this.numComponents) {
      throw new IllegalArgumentException("Length of pixel array < number of components in model");
    }
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[paramInt + this.numComponents];
    } else if (paramArrayOfInt.length - paramInt < this.numComponents) {
      throw new IllegalArgumentException("Length of components array < number of components in model");
    }
    System.arraycopy(arrayOfInt, 0, paramArrayOfInt, paramInt, this.numComponents);
    return paramArrayOfInt;
  }
  
  public int[] getUnnormalizedComponents(float[] paramArrayOfFloat, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    if (this.needScaleInit) {
      initScale();
    }
    if (this.noUnnorm) {
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
    }
    return super.getUnnormalizedComponents(paramArrayOfFloat, paramInt1, paramArrayOfInt, paramInt2);
  }
  
  public float[] getNormalizedComponents(int[] paramArrayOfInt, int paramInt1, float[] paramArrayOfFloat, int paramInt2)
  {
    if (this.needScaleInit) {
      initScale();
    }
    if (this.noUnnorm) {
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
    }
    return super.getNormalizedComponents(paramArrayOfInt, paramInt1, paramArrayOfFloat, paramInt2);
  }
  
  public int getDataElement(int[] paramArrayOfInt, int paramInt)
  {
    if (this.needScaleInit) {
      initScale();
    }
    if (this.numComponents == 1)
    {
      if (this.noUnnorm) {
        throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
      }
      return paramArrayOfInt[(paramInt + 0)];
    }
    throw new IllegalArgumentException("This model returns " + this.numComponents + " elements in the pixel array.");
  }
  
  public Object getDataElements(int[] paramArrayOfInt, int paramInt, Object paramObject)
  {
    if (this.needScaleInit) {
      initScale();
    }
    if (this.noUnnorm) {
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
    }
    if (paramArrayOfInt.length - paramInt < this.numComponents) {
      throw new IllegalArgumentException("Component array too small (should be " + this.numComponents);
    }
    Object localObject;
    int i;
    switch (this.transferType)
    {
    case 3: 
      if (paramObject == null) {
        localObject = new int[this.numComponents];
      } else {
        localObject = (int[])paramObject;
      }
      System.arraycopy(paramArrayOfInt, paramInt, localObject, 0, this.numComponents);
      return localObject;
    case 0: 
      if (paramObject == null) {
        localObject = new byte[this.numComponents];
      } else {
        localObject = (byte[])paramObject;
      }
      for (i = 0; i < this.numComponents; i++) {
        localObject[i] = ((byte)(paramArrayOfInt[(paramInt + i)] & 0xFF));
      }
      return localObject;
    case 1: 
      if (paramObject == null) {
        localObject = new short[this.numComponents];
      } else {
        localObject = (short[])paramObject;
      }
      for (i = 0; i < this.numComponents; i++) {
        localObject[i] = ((short)(paramArrayOfInt[(paramInt + i)] & 0xFFFF));
      }
      return localObject;
    }
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int getDataElement(float[] paramArrayOfFloat, int paramInt)
  {
    if (this.numComponents > 1) {
      throw new IllegalArgumentException("More than one component per pixel");
    }
    if (this.signed) {
      throw new IllegalArgumentException("Component value is signed");
    }
    if (this.needScaleInit) {
      initScale();
    }
    Object localObject1 = getDataElements(paramArrayOfFloat, paramInt, null);
    Object localObject2;
    switch (this.transferType)
    {
    case 0: 
      localObject2 = (byte[])localObject1;
      return localObject2[0] & 0xFF;
    case 1: 
      localObject2 = (short[])localObject1;
      return localObject2[0] & 0xFFFF;
    case 3: 
      localObject2 = (int[])localObject1;
      return localObject2[0];
    }
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public Object getDataElements(float[] paramArrayOfFloat, int paramInt, Object paramObject)
  {
    int i = (this.supportsAlpha) && (this.isAlphaPremultiplied) ? 1 : 0;
    if (this.needScaleInit) {
      initScale();
    }
    float[] arrayOfFloat1;
    if (this.nonStdScale)
    {
      arrayOfFloat1 = new float[this.numComponents];
      int j = 0;
      for (int k = paramInt; j < this.numColorComponents; k++)
      {
        arrayOfFloat1[j] = ((paramArrayOfFloat[k] - this.compOffset[j]) * this.compScale[j]);
        if (arrayOfFloat1[j] < 0.0F) {
          arrayOfFloat1[j] = 0.0F;
        }
        if (arrayOfFloat1[j] > 1.0F) {
          arrayOfFloat1[j] = 1.0F;
        }
        j++;
      }
      if (this.supportsAlpha) {
        arrayOfFloat1[this.numColorComponents] = paramArrayOfFloat[(this.numColorComponents + paramInt)];
      }
      paramInt = 0;
    }
    else
    {
      arrayOfFloat1 = paramArrayOfFloat;
    }
    int i2;
    int i4;
    int i6;
    int i8;
    int i10;
    switch (this.transferType)
    {
    case 0: 
      byte[] arrayOfByte;
      if (paramObject == null) {
        arrayOfByte = new byte[this.numComponents];
      } else {
        arrayOfByte = (byte[])paramObject;
      }
      int n;
      if (i != 0)
      {
        float f1 = arrayOfFloat1[(this.numColorComponents + paramInt)];
        n = 0;
        for (i2 = paramInt; n < this.numColorComponents; i2++)
        {
          arrayOfByte[n] = ((byte)(int)(arrayOfFloat1[i2] * f1 * ((1 << this.nBits[n]) - 1) + 0.5F));
          n++;
        }
        arrayOfByte[this.numColorComponents] = ((byte)(int)(f1 * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F));
      }
      else
      {
        int m = 0;
        for (n = paramInt; m < this.numComponents; n++)
        {
          arrayOfByte[m] = ((byte)(int)(arrayOfFloat1[n] * ((1 << this.nBits[m]) - 1) + 0.5F));
          m++;
        }
      }
      return arrayOfByte;
    case 1: 
      short[] arrayOfShort1;
      if (paramObject == null) {
        arrayOfShort1 = new short[this.numComponents];
      } else {
        arrayOfShort1 = (short[])paramObject;
      }
      if (i != 0)
      {
        float f2 = arrayOfFloat1[(this.numColorComponents + paramInt)];
        i2 = 0;
        for (i4 = paramInt; i2 < this.numColorComponents; i4++)
        {
          arrayOfShort1[i2] = ((short)(int)(arrayOfFloat1[i4] * f2 * ((1 << this.nBits[i2]) - 1) + 0.5F));
          i2++;
        }
        arrayOfShort1[this.numColorComponents] = ((short)(int)(f2 * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F));
      }
      else
      {
        int i1 = 0;
        for (i2 = paramInt; i1 < this.numComponents; i2++)
        {
          arrayOfShort1[i1] = ((short)(int)(arrayOfFloat1[i2] * ((1 << this.nBits[i1]) - 1) + 0.5F));
          i1++;
        }
      }
      return arrayOfShort1;
    case 3: 
      int[] arrayOfInt;
      if (paramObject == null) {
        arrayOfInt = new int[this.numComponents];
      } else {
        arrayOfInt = (int[])paramObject;
      }
      if (i != 0)
      {
        float f3 = arrayOfFloat1[(this.numColorComponents + paramInt)];
        i4 = 0;
        for (i6 = paramInt; i4 < this.numColorComponents; i6++)
        {
          arrayOfInt[i4] = ((int)(arrayOfFloat1[i6] * f3 * ((1 << this.nBits[i4]) - 1) + 0.5F));
          i4++;
        }
        arrayOfInt[this.numColorComponents] = ((int)(f3 * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F));
      }
      else
      {
        int i3 = 0;
        for (i4 = paramInt; i3 < this.numComponents; i4++)
        {
          arrayOfInt[i3] = ((int)(arrayOfFloat1[i4] * ((1 << this.nBits[i3]) - 1) + 0.5F));
          i3++;
        }
      }
      return arrayOfInt;
    case 2: 
      short[] arrayOfShort2;
      if (paramObject == null) {
        arrayOfShort2 = new short[this.numComponents];
      } else {
        arrayOfShort2 = (short[])paramObject;
      }
      if (i != 0)
      {
        float f4 = arrayOfFloat1[(this.numColorComponents + paramInt)];
        i6 = 0;
        for (i8 = paramInt; i6 < this.numColorComponents; i8++)
        {
          arrayOfShort2[i6] = ((short)(int)(arrayOfFloat1[i8] * f4 * 32767.0F + 0.5F));
          i6++;
        }
        arrayOfShort2[this.numColorComponents] = ((short)(int)(f4 * 32767.0F + 0.5F));
      }
      else
      {
        int i5 = 0;
        for (i6 = paramInt; i5 < this.numComponents; i6++)
        {
          arrayOfShort2[i5] = ((short)(int)(arrayOfFloat1[i6] * 32767.0F + 0.5F));
          i5++;
        }
      }
      return arrayOfShort2;
    case 4: 
      float[] arrayOfFloat2;
      if (paramObject == null) {
        arrayOfFloat2 = new float[this.numComponents];
      } else {
        arrayOfFloat2 = (float[])paramObject;
      }
      if (i != 0)
      {
        float f5 = paramArrayOfFloat[(this.numColorComponents + paramInt)];
        i8 = 0;
        for (i10 = paramInt; i8 < this.numColorComponents; i10++)
        {
          paramArrayOfFloat[i10] *= f5;
          i8++;
        }
        arrayOfFloat2[this.numColorComponents] = f5;
      }
      else
      {
        int i7 = 0;
        for (i8 = paramInt; i7 < this.numComponents; i8++)
        {
          arrayOfFloat2[i7] = paramArrayOfFloat[i8];
          i7++;
        }
      }
      return arrayOfFloat2;
    case 5: 
      double[] arrayOfDouble;
      if (paramObject == null) {
        arrayOfDouble = new double[this.numComponents];
      } else {
        arrayOfDouble = (double[])paramObject;
      }
      if (i != 0)
      {
        double d = paramArrayOfFloat[(this.numColorComponents + paramInt)];
        int i11 = 0;
        for (int i12 = paramInt; i11 < this.numColorComponents; i12++)
        {
          arrayOfDouble[i11] = (paramArrayOfFloat[i12] * d);
          i11++;
        }
        arrayOfDouble[this.numColorComponents] = d;
      }
      else
      {
        int i9 = 0;
        for (i10 = paramInt; i9 < this.numComponents; i10++)
        {
          arrayOfDouble[i9] = paramArrayOfFloat[i10];
          i9++;
        }
      }
      return arrayOfDouble;
    }
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public float[] getNormalizedComponents(Object paramObject, float[] paramArrayOfFloat, int paramInt)
  {
    if (paramArrayOfFloat == null) {
      paramArrayOfFloat = new float[this.numComponents + paramInt];
    }
    int k;
    int n;
    int i1;
    int i2;
    int i3;
    switch (this.transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      int j = 0;
      for (k = paramInt; j < this.numComponents; k++)
      {
        paramArrayOfFloat[k] = ((arrayOfByte[j] & 0xFF) / ((1 << this.nBits[j]) - 1));
        j++;
      }
      break;
    case 1: 
      short[] arrayOfShort1 = (short[])paramObject;
      k = 0;
      for (n = paramInt; k < this.numComponents; n++)
      {
        paramArrayOfFloat[n] = ((arrayOfShort1[k] & 0xFFFF) / ((1 << this.nBits[k]) - 1));
        k++;
      }
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      n = 0;
      for (i1 = paramInt; n < this.numComponents; i1++)
      {
        paramArrayOfFloat[i1] = (arrayOfInt[n] / ((1 << this.nBits[n]) - 1));
        n++;
      }
      break;
    case 2: 
      short[] arrayOfShort2 = (short[])paramObject;
      i1 = 0;
      for (i2 = paramInt; i1 < this.numComponents; i2++)
      {
        paramArrayOfFloat[i2] = (arrayOfShort2[i1] / 32767.0F);
        i1++;
      }
      break;
    case 4: 
      float[] arrayOfFloat = (float[])paramObject;
      i2 = 0;
      for (i3 = paramInt; i2 < this.numComponents; i3++)
      {
        paramArrayOfFloat[i3] = arrayOfFloat[i2];
        i2++;
      }
      break;
    case 5: 
      double[] arrayOfDouble = (double[])paramObject;
      i3 = 0;
      for (int i4 = paramInt; i3 < this.numComponents; i4++)
      {
        paramArrayOfFloat[i4] = ((float)arrayOfDouble[i3]);
        i3++;
      }
      break;
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    }
    if ((this.supportsAlpha) && (this.isAlphaPremultiplied))
    {
      float f1 = paramArrayOfFloat[(this.numColorComponents + paramInt)];
      if (f1 != 0.0F)
      {
        float f2 = 1.0F / f1;
        for (int m = paramInt; m < this.numColorComponents + paramInt; m++) {
          paramArrayOfFloat[m] *= f2;
        }
      }
    }
    if (this.min != null) {
      for (int i = 0; i < this.numColorComponents; i++) {
        paramArrayOfFloat[(i + paramInt)] = (this.min[i] + this.diffMinMax[i] * paramArrayOfFloat[(i + paramInt)]);
      }
    }
    return paramArrayOfFloat;
  }
  
  public ColorModel coerceData(WritableRaster paramWritableRaster, boolean paramBoolean)
  {
    if ((!this.supportsAlpha) || (this.isAlphaPremultiplied == paramBoolean)) {
      return this;
    }
    int i = paramWritableRaster.getWidth();
    int j = paramWritableRaster.getHeight();
    int k = paramWritableRaster.getNumBands() - 1;
    int m = paramWritableRaster.getMinX();
    int n = paramWritableRaster.getMinY();
    Object localObject1;
    int i4;
    int i1;
    float f1;
    int i7;
    int i3;
    if (paramBoolean)
    {
      Object localObject2;
      float f3;
      int i5;
      switch (this.transferType)
      {
      case 0: 
        localObject1 = null;
        localObject2 = null;
        f3 = 1.0F / ((1 << this.nBits[k]) - 1);
        i4 = 0;
        while (i4 < j)
        {
          i1 = m;
          i5 = 0;
          while (i5 < i)
          {
            localObject1 = (byte[])paramWritableRaster.getDataElements(i1, n, localObject1);
            f1 = (localObject1[k] & 0xFF) * f3;
            if (f1 != 0.0F)
            {
              for (i7 = 0; i7 < k; i7++) {
                localObject1[i7] = ((byte)(int)((localObject1[i7] & 0xFF) * f1 + 0.5F));
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            else
            {
              if (localObject2 == null)
              {
                localObject2 = new byte[this.numComponents];
                Arrays.fill((byte[])localObject2, (byte)0);
              }
              paramWritableRaster.setDataElements(i1, n, localObject2);
            }
            i5++;
            i1++;
          }
          i4++;
          n++;
        }
        break;
      case 1: 
        localObject1 = null;
        localObject2 = null;
        f3 = 1.0F / ((1 << this.nBits[k]) - 1);
        i4 = 0;
        while (i4 < j)
        {
          i1 = m;
          i5 = 0;
          while (i5 < i)
          {
            localObject1 = (short[])paramWritableRaster.getDataElements(i1, n, localObject1);
            f1 = (localObject1[k] & 0xFFFF) * f3;
            if (f1 != 0.0F)
            {
              for (i7 = 0; i7 < k; i7++) {
                localObject1[i7] = ((short)(int)((localObject1[i7] & 0xFFFF) * f1 + 0.5F));
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            else
            {
              if (localObject2 == null)
              {
                localObject2 = new short[this.numComponents];
                Arrays.fill((short[])localObject2, (short)0);
              }
              paramWritableRaster.setDataElements(i1, n, localObject2);
            }
            i5++;
            i1++;
          }
          i4++;
          n++;
        }
        break;
      case 3: 
        localObject1 = null;
        localObject2 = null;
        f3 = 1.0F / ((1 << this.nBits[k]) - 1);
        i4 = 0;
        while (i4 < j)
        {
          i1 = m;
          i5 = 0;
          while (i5 < i)
          {
            localObject1 = (int[])paramWritableRaster.getDataElements(i1, n, localObject1);
            f1 = localObject1[k] * f3;
            if (f1 != 0.0F)
            {
              for (i7 = 0; i7 < k; i7++) {
                localObject1[i7] = ((int)(localObject1[i7] * f1 + 0.5F));
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            else
            {
              if (localObject2 == null)
              {
                localObject2 = new int[this.numComponents];
                Arrays.fill((int[])localObject2, 0);
              }
              paramWritableRaster.setDataElements(i1, n, localObject2);
            }
            i5++;
            i1++;
          }
          i4++;
          n++;
        }
        break;
      case 2: 
        localObject1 = null;
        localObject2 = null;
        f3 = 3.051851E-5F;
        i4 = 0;
        while (i4 < j)
        {
          i1 = m;
          i5 = 0;
          while (i5 < i)
          {
            localObject1 = (short[])paramWritableRaster.getDataElements(i1, n, localObject1);
            f1 = localObject1[k] * f3;
            if (f1 != 0.0F)
            {
              for (i7 = 0; i7 < k; i7++) {
                localObject1[i7] = ((short)(int)(localObject1[i7] * f1 + 0.5F));
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            else
            {
              if (localObject2 == null)
              {
                localObject2 = new short[this.numComponents];
                Arrays.fill((short[])localObject2, (short)0);
              }
              paramWritableRaster.setDataElements(i1, n, localObject2);
            }
            i5++;
            i1++;
          }
          i4++;
          n++;
        }
        break;
      case 4: 
        localObject1 = null;
        localObject2 = null;
        i3 = 0;
        while (i3 < j)
        {
          i1 = m;
          i4 = 0;
          while (i4 < i)
          {
            localObject1 = (float[])paramWritableRaster.getDataElements(i1, n, localObject1);
            f1 = localObject1[k];
            if (f1 != 0.0F)
            {
              for (i5 = 0; i5 < k; i5++) {
                localObject1[i5] *= f1;
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            else
            {
              if (localObject2 == null)
              {
                localObject2 = new float[this.numComponents];
                Arrays.fill((float[])localObject2, 0.0F);
              }
              paramWritableRaster.setDataElements(i1, n, localObject2);
            }
            i4++;
            i1++;
          }
          i3++;
          n++;
        }
        break;
      case 5: 
        localObject1 = null;
        localObject2 = null;
        i3 = 0;
        while (i3 < j)
        {
          i1 = m;
          i4 = 0;
          while (i4 < i)
          {
            localObject1 = (double[])paramWritableRaster.getDataElements(i1, n, localObject1);
            double d2 = localObject1[k];
            if (d2 != 0.0D)
            {
              for (int i8 = 0; i8 < k; i8++) {
                localObject1[i8] *= d2;
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            else
            {
              if (localObject2 == null)
              {
                localObject2 = new double[this.numComponents];
                Arrays.fill((double[])localObject2, 0.0D);
              }
              paramWritableRaster.setDataElements(i1, n, localObject2);
            }
            i4++;
            i1++;
          }
          i3++;
          n++;
        }
        break;
      default: 
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      }
    }
    else
    {
      float f2;
      float f5;
      int i2;
      switch (this.transferType)
      {
      case 0: 
        localObject1 = null;
        f2 = 1.0F / ((1 << this.nBits[k]) - 1);
        i3 = 0;
        while (i3 < j)
        {
          i1 = m;
          i4 = 0;
          while (i4 < i)
          {
            localObject1 = (byte[])paramWritableRaster.getDataElements(i1, n, localObject1);
            f1 = (localObject1[k] & 0xFF) * f2;
            if (f1 != 0.0F)
            {
              f5 = 1.0F / f1;
              for (i7 = 0; i7 < k; i7++) {
                localObject1[i7] = ((byte)(int)((localObject1[i7] & 0xFF) * f5 + 0.5F));
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            i4++;
            i1++;
          }
          i3++;
          n++;
        }
        break;
      case 1: 
        localObject1 = null;
        f2 = 1.0F / ((1 << this.nBits[k]) - 1);
        i3 = 0;
        while (i3 < j)
        {
          i1 = m;
          i4 = 0;
          while (i4 < i)
          {
            localObject1 = (short[])paramWritableRaster.getDataElements(i1, n, localObject1);
            f1 = (localObject1[k] & 0xFFFF) * f2;
            if (f1 != 0.0F)
            {
              f5 = 1.0F / f1;
              for (i7 = 0; i7 < k; i7++) {
                localObject1[i7] = ((short)(int)((localObject1[i7] & 0xFFFF) * f5 + 0.5F));
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            i4++;
            i1++;
          }
          i3++;
          n++;
        }
        break;
      case 3: 
        localObject1 = null;
        f2 = 1.0F / ((1 << this.nBits[k]) - 1);
        i3 = 0;
        while (i3 < j)
        {
          i1 = m;
          i4 = 0;
          while (i4 < i)
          {
            localObject1 = (int[])paramWritableRaster.getDataElements(i1, n, localObject1);
            f1 = localObject1[k] * f2;
            if (f1 != 0.0F)
            {
              f5 = 1.0F / f1;
              for (i7 = 0; i7 < k; i7++) {
                localObject1[i7] = ((int)(localObject1[i7] * f5 + 0.5F));
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            i4++;
            i1++;
          }
          i3++;
          n++;
        }
        break;
      case 2: 
        localObject1 = null;
        f2 = 3.051851E-5F;
        i3 = 0;
        while (i3 < j)
        {
          i1 = m;
          i4 = 0;
          while (i4 < i)
          {
            localObject1 = (short[])paramWritableRaster.getDataElements(i1, n, localObject1);
            f1 = localObject1[k] * f2;
            if (f1 != 0.0F)
            {
              f5 = 1.0F / f1;
              for (i7 = 0; i7 < k; i7++) {
                localObject1[i7] = ((short)(int)(localObject1[i7] * f5 + 0.5F));
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            i4++;
            i1++;
          }
          i3++;
          n++;
        }
        break;
      case 4: 
        localObject1 = null;
        i2 = 0;
        while (i2 < j)
        {
          i1 = m;
          i3 = 0;
          while (i3 < i)
          {
            localObject1 = (float[])paramWritableRaster.getDataElements(i1, n, localObject1);
            f1 = localObject1[k];
            if (f1 != 0.0F)
            {
              float f4 = 1.0F / f1;
              for (int i6 = 0; i6 < k; i6++) {
                localObject1[i6] *= f4;
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            i3++;
            i1++;
          }
          i2++;
          n++;
        }
        break;
      case 5: 
        localObject1 = null;
        i2 = 0;
        while (i2 < j)
        {
          i1 = m;
          i3 = 0;
          while (i3 < i)
          {
            localObject1 = (double[])paramWritableRaster.getDataElements(i1, n, localObject1);
            double d1 = localObject1[k];
            if (d1 != 0.0D)
            {
              double d3 = 1.0D / d1;
              for (int i9 = 0; i9 < k; i9++) {
                localObject1[i9] *= d3;
              }
              paramWritableRaster.setDataElements(i1, n, localObject1);
            }
            i3++;
            i1++;
          }
          i2++;
          n++;
        }
        break;
      default: 
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      }
    }
    if (!this.signed) {
      return new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, paramBoolean, this.transparency, this.transferType);
    }
    return new ComponentColorModel(this.colorSpace, this.supportsAlpha, paramBoolean, this.transparency, this.transferType);
  }
  
  public boolean isCompatibleRaster(Raster paramRaster)
  {
    SampleModel localSampleModel = paramRaster.getSampleModel();
    if ((localSampleModel instanceof ComponentSampleModel))
    {
      if (localSampleModel.getNumBands() != getNumComponents()) {
        return false;
      }
      for (int i = 0; i < this.nBits.length; i++) {
        if (localSampleModel.getSampleSize(i) < this.nBits[i]) {
          return false;
        }
      }
      return paramRaster.getTransferType() == this.transferType;
    }
    return false;
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * paramInt2 * this.numComponents;
    WritableRaster localWritableRaster = null;
    switch (this.transferType)
    {
    case 0: 
    case 1: 
      localWritableRaster = Raster.createInterleavedRaster(this.transferType, paramInt1, paramInt2, this.numComponents, null);
      break;
    default: 
      SampleModel localSampleModel = createCompatibleSampleModel(paramInt1, paramInt2);
      DataBuffer localDataBuffer = localSampleModel.createDataBuffer();
      localWritableRaster = Raster.createWritableRaster(localSampleModel, localDataBuffer, null);
    }
    return localWritableRaster;
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = new int[this.numComponents];
    for (int i = 0; i < this.numComponents; i++) {
      arrayOfInt[i] = i;
    }
    switch (this.transferType)
    {
    case 0: 
    case 1: 
      return new PixelInterleavedSampleModel(this.transferType, paramInt1, paramInt2, this.numComponents, paramInt1 * this.numComponents, arrayOfInt);
    }
    return new ComponentSampleModel(this.transferType, paramInt1, paramInt2, this.numComponents, paramInt1 * this.numComponents, arrayOfInt);
  }
  
  public boolean isCompatibleSampleModel(SampleModel paramSampleModel)
  {
    if (!(paramSampleModel instanceof ComponentSampleModel)) {
      return false;
    }
    if (this.numComponents != paramSampleModel.getNumBands()) {
      return false;
    }
    return paramSampleModel.getTransferType() == this.transferType;
  }
  
  public WritableRaster getAlphaRaster(WritableRaster paramWritableRaster)
  {
    if (!hasAlpha()) {
      return null;
    }
    int i = paramWritableRaster.getMinX();
    int j = paramWritableRaster.getMinY();
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = (paramWritableRaster.getNumBands() - 1);
    return paramWritableRaster.createWritableChild(i, j, paramWritableRaster.getWidth(), paramWritableRaster.getHeight(), i, j, arrayOfInt);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!super.equals(paramObject)) {
      return false;
    }
    return paramObject.getClass() == getClass();
  }
}
