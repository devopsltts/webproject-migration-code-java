package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;
import sun.awt.image.ByteInterleavedRaster;
import sun.awt.image.IntegerInterleavedRaster;
import sun.awt.image.SunWritableRaster;

abstract class TexturePaintContext
  implements PaintContext
{
  public static ColorModel xrgbmodel = new DirectColorModel(24, 16711680, 65280, 255);
  public static ColorModel argbmodel = ColorModel.getRGBdefault();
  ColorModel colorModel;
  int bWidth;
  int bHeight;
  int maxWidth;
  WritableRaster outRas;
  double xOrg;
  double yOrg;
  double incXAcross;
  double incYAcross;
  double incXDown;
  double incYDown;
  int colincx;
  int colincy;
  int colincxerr;
  int colincyerr;
  int rowincx;
  int rowincy;
  int rowincxerr;
  int rowincyerr;
  private static WeakReference<Raster> xrgbRasRef;
  private static WeakReference<Raster> argbRasRef;
  private static WeakReference<Raster> byteRasRef;
  
  public static PaintContext getContext(BufferedImage paramBufferedImage, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints, Rectangle paramRectangle)
  {
    WritableRaster localWritableRaster = paramBufferedImage.getRaster();
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    int i = paramRectangle.width;
    Object localObject1 = paramRenderingHints.get(RenderingHints.KEY_INTERPOLATION);
    boolean bool = paramRenderingHints.get(RenderingHints.KEY_RENDERING) == RenderingHints.VALUE_RENDER_QUALITY;
    Object localObject2;
    if (((localWritableRaster instanceof IntegerInterleavedRaster)) && ((!bool) || (isFilterableDCM(localColorModel))))
    {
      localObject2 = (IntegerInterleavedRaster)localWritableRaster;
      if ((((IntegerInterleavedRaster)localObject2).getNumDataElements() == 1) && (((IntegerInterleavedRaster)localObject2).getPixelStride() == 1)) {
        return new Int((IntegerInterleavedRaster)localObject2, localColorModel, paramAffineTransform, i, bool);
      }
    }
    else if ((localWritableRaster instanceof ByteInterleavedRaster))
    {
      localObject2 = (ByteInterleavedRaster)localWritableRaster;
      if ((((ByteInterleavedRaster)localObject2).getNumDataElements() == 1) && (((ByteInterleavedRaster)localObject2).getPixelStride() == 1)) {
        if (bool)
        {
          if (isFilterableICM(localColorModel)) {
            return new ByteFilter((ByteInterleavedRaster)localObject2, localColorModel, paramAffineTransform, i);
          }
        }
        else {
          return new Byte((ByteInterleavedRaster)localObject2, localColorModel, paramAffineTransform, i);
        }
      }
    }
    return new Any(localWritableRaster, localColorModel, paramAffineTransform, i, bool);
  }
  
  public static boolean isFilterableICM(ColorModel paramColorModel)
  {
    if ((paramColorModel instanceof IndexColorModel))
    {
      IndexColorModel localIndexColorModel = (IndexColorModel)paramColorModel;
      if (localIndexColorModel.getMapSize() <= 256) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isFilterableDCM(ColorModel paramColorModel)
  {
    if ((paramColorModel instanceof DirectColorModel))
    {
      DirectColorModel localDirectColorModel = (DirectColorModel)paramColorModel;
      return (isMaskOK(localDirectColorModel.getAlphaMask(), true)) && (isMaskOK(localDirectColorModel.getRedMask(), false)) && (isMaskOK(localDirectColorModel.getGreenMask(), false)) && (isMaskOK(localDirectColorModel.getBlueMask(), false));
    }
    return false;
  }
  
  public static boolean isMaskOK(int paramInt, boolean paramBoolean)
  {
    if ((paramBoolean) && (paramInt == 0)) {
      return true;
    }
    return (paramInt == 255) || (paramInt == 65280) || (paramInt == 16711680) || (paramInt == -16777216);
  }
  
  public static ColorModel getInternedColorModel(ColorModel paramColorModel)
  {
    if ((xrgbmodel == paramColorModel) || (xrgbmodel.equals(paramColorModel))) {
      return xrgbmodel;
    }
    if ((argbmodel == paramColorModel) || (argbmodel.equals(paramColorModel))) {
      return argbmodel;
    }
    return paramColorModel;
  }
  
  TexturePaintContext(ColorModel paramColorModel, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3)
  {
    this.colorModel = getInternedColorModel(paramColorModel);
    this.bWidth = paramInt1;
    this.bHeight = paramInt2;
    this.maxWidth = paramInt3;
    try
    {
      paramAffineTransform = paramAffineTransform.createInverse();
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
      paramAffineTransform.setToScale(0.0D, 0.0D);
    }
    this.incXAcross = mod(paramAffineTransform.getScaleX(), paramInt1);
    this.incYAcross = mod(paramAffineTransform.getShearY(), paramInt2);
    this.incXDown = mod(paramAffineTransform.getShearX(), paramInt1);
    this.incYDown = mod(paramAffineTransform.getScaleY(), paramInt2);
    this.xOrg = paramAffineTransform.getTranslateX();
    this.yOrg = paramAffineTransform.getTranslateY();
    this.colincx = ((int)this.incXAcross);
    this.colincy = ((int)this.incYAcross);
    this.colincxerr = fractAsInt(this.incXAcross);
    this.colincyerr = fractAsInt(this.incYAcross);
    this.rowincx = ((int)this.incXDown);
    this.rowincy = ((int)this.incYDown);
    this.rowincxerr = fractAsInt(this.incXDown);
    this.rowincyerr = fractAsInt(this.incYDown);
  }
  
  static int fractAsInt(double paramDouble)
  {
    return (int)(paramDouble % 1.0D * 2.147483647E9D);
  }
  
  static double mod(double paramDouble1, double paramDouble2)
  {
    paramDouble1 %= paramDouble2;
    if (paramDouble1 < 0.0D)
    {
      paramDouble1 += paramDouble2;
      if (paramDouble1 >= paramDouble2) {
        paramDouble1 = 0.0D;
      }
    }
    return paramDouble1;
  }
  
  public void dispose()
  {
    dropRaster(this.colorModel, this.outRas);
  }
  
  public ColorModel getColorModel()
  {
    return this.colorModel;
  }
  
  public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.outRas == null) || (this.outRas.getWidth() < paramInt3) || (this.outRas.getHeight() < paramInt4)) {
      this.outRas = makeRaster(paramInt4 == 1 ? Math.max(paramInt3, this.maxWidth) : paramInt3, paramInt4);
    }
    double d1 = mod(this.xOrg + paramInt1 * this.incXAcross + paramInt2 * this.incXDown, this.bWidth);
    double d2 = mod(this.yOrg + paramInt1 * this.incYAcross + paramInt2 * this.incYDown, this.bHeight);
    setRaster((int)d1, (int)d2, fractAsInt(d1), fractAsInt(d2), paramInt3, paramInt4, this.bWidth, this.bHeight, this.colincx, this.colincxerr, this.colincy, this.colincyerr, this.rowincx, this.rowincxerr, this.rowincy, this.rowincyerr);
    SunWritableRaster.markDirty(this.outRas);
    return this.outRas;
  }
  
  static synchronized WritableRaster makeRaster(ColorModel paramColorModel, Raster paramRaster, int paramInt1, int paramInt2)
  {
    WritableRaster localWritableRaster;
    if (xrgbmodel == paramColorModel)
    {
      if (xrgbRasRef != null)
      {
        localWritableRaster = (WritableRaster)xrgbRasRef.get();
        if ((localWritableRaster != null) && (localWritableRaster.getWidth() >= paramInt1) && (localWritableRaster.getHeight() >= paramInt2))
        {
          xrgbRasRef = null;
          return localWritableRaster;
        }
      }
      if ((paramInt1 <= 32) && (paramInt2 <= 32)) {
        paramInt1 = paramInt2 = 32;
      }
    }
    else if (argbmodel == paramColorModel)
    {
      if (argbRasRef != null)
      {
        localWritableRaster = (WritableRaster)argbRasRef.get();
        if ((localWritableRaster != null) && (localWritableRaster.getWidth() >= paramInt1) && (localWritableRaster.getHeight() >= paramInt2))
        {
          argbRasRef = null;
          return localWritableRaster;
        }
      }
      if ((paramInt1 <= 32) && (paramInt2 <= 32)) {
        paramInt1 = paramInt2 = 32;
      }
    }
    if (paramRaster != null) {
      return paramRaster.createCompatibleWritableRaster(paramInt1, paramInt2);
    }
    return paramColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
  }
  
  static synchronized void dropRaster(ColorModel paramColorModel, Raster paramRaster)
  {
    if (paramRaster == null) {
      return;
    }
    if (xrgbmodel == paramColorModel) {
      xrgbRasRef = new WeakReference(paramRaster);
    } else if (argbmodel == paramColorModel) {
      argbRasRef = new WeakReference(paramRaster);
    }
  }
  
  static synchronized WritableRaster makeByteRaster(Raster paramRaster, int paramInt1, int paramInt2)
  {
    if (byteRasRef != null)
    {
      WritableRaster localWritableRaster = (WritableRaster)byteRasRef.get();
      if ((localWritableRaster != null) && (localWritableRaster.getWidth() >= paramInt1) && (localWritableRaster.getHeight() >= paramInt2))
      {
        byteRasRef = null;
        return localWritableRaster;
      }
    }
    if ((paramInt1 <= 32) && (paramInt2 <= 32)) {
      paramInt1 = paramInt2 = 32;
    }
    return paramRaster.createCompatibleWritableRaster(paramInt1, paramInt2);
  }
  
  static synchronized void dropByteRaster(Raster paramRaster)
  {
    if (paramRaster == null) {
      return;
    }
    byteRasRef = new WeakReference(paramRaster);
  }
  
  public abstract WritableRaster makeRaster(int paramInt1, int paramInt2);
  
  public abstract void setRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, int paramInt13, int paramInt14, int paramInt15, int paramInt16);
  
  public static int blend(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    paramInt1 >>>= 19;
    paramInt2 >>>= 19;
    int m;
    int k;
    int j;
    int i = j = k = m = 0;
    for (int n = 0; n < 4; n++)
    {
      int i1 = paramArrayOfInt[n];
      paramInt1 = 4096 - paramInt1;
      if ((n & 0x1) == 0) {
        paramInt2 = 4096 - paramInt2;
      }
      int i2 = paramInt1 * paramInt2;
      if (i2 != 0)
      {
        i += (i1 >>> 24) * i2;
        j += (i1 >>> 16 & 0xFF) * i2;
        k += (i1 >>> 8 & 0xFF) * i2;
        m += (i1 & 0xFF) * i2;
      }
    }
    return i + 8388608 >>> 24 << 24 | j + 8388608 >>> 24 << 16 | k + 8388608 >>> 24 << 8 | m + 8388608 >>> 24;
  }
  
  static class Any
    extends TexturePaintContext
  {
    WritableRaster srcRas;
    boolean filter;
    
    public Any(WritableRaster paramWritableRaster, ColorModel paramColorModel, AffineTransform paramAffineTransform, int paramInt, boolean paramBoolean)
    {
      super(paramAffineTransform, paramWritableRaster.getWidth(), paramWritableRaster.getHeight(), paramInt);
      this.srcRas = paramWritableRaster;
      this.filter = paramBoolean;
    }
    
    public WritableRaster makeRaster(int paramInt1, int paramInt2)
    {
      return makeRaster(this.colorModel, this.srcRas, paramInt1, paramInt2);
    }
    
    public void setRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, int paramInt13, int paramInt14, int paramInt15, int paramInt16)
    {
      Object localObject = null;
      int i = paramInt1;
      int j = paramInt2;
      int k = paramInt3;
      int m = paramInt4;
      WritableRaster localWritableRaster1 = this.srcRas;
      WritableRaster localWritableRaster2 = this.outRas;
      int[] arrayOfInt = this.filter ? new int[4] : null;
      for (int n = 0; n < paramInt6; n++)
      {
        paramInt1 = i;
        paramInt2 = j;
        paramInt3 = k;
        paramInt4 = m;
        for (int i1 = 0; i1 < paramInt5; i1++)
        {
          localObject = localWritableRaster1.getDataElements(paramInt1, paramInt2, localObject);
          if (this.filter)
          {
            int i2;
            if ((i2 = paramInt1 + 1) >= paramInt7) {
              i2 = 0;
            }
            int i3;
            if ((i3 = paramInt2 + 1) >= paramInt8) {
              i3 = 0;
            }
            arrayOfInt[0] = this.colorModel.getRGB(localObject);
            localObject = localWritableRaster1.getDataElements(i2, paramInt2, localObject);
            arrayOfInt[1] = this.colorModel.getRGB(localObject);
            localObject = localWritableRaster1.getDataElements(paramInt1, i3, localObject);
            arrayOfInt[2] = this.colorModel.getRGB(localObject);
            localObject = localWritableRaster1.getDataElements(i2, i3, localObject);
            arrayOfInt[3] = this.colorModel.getRGB(localObject);
            int i4 = TexturePaintContext.blend(arrayOfInt, paramInt3, paramInt4);
            localObject = this.colorModel.getDataElements(i4, localObject);
          }
          localWritableRaster2.setDataElements(i1, n, localObject);
          if (paramInt3 += paramInt10 < 0)
          {
            paramInt3 &= 0x7FFFFFFF;
            paramInt1++;
          }
          if (paramInt1 += paramInt9 >= paramInt7) {
            paramInt1 -= paramInt7;
          }
          if (paramInt4 += paramInt12 < 0)
          {
            paramInt4 &= 0x7FFFFFFF;
            paramInt2++;
          }
          if (paramInt2 += paramInt11 >= paramInt8) {
            paramInt2 -= paramInt8;
          }
        }
        if (k += paramInt14 < 0)
        {
          k &= 0x7FFFFFFF;
          i++;
        }
        if (i += paramInt13 >= paramInt7) {
          i -= paramInt7;
        }
        if (m += paramInt16 < 0)
        {
          m &= 0x7FFFFFFF;
          j++;
        }
        if (j += paramInt15 >= paramInt8) {
          j -= paramInt8;
        }
      }
    }
  }
  
  static class Byte
    extends TexturePaintContext
  {
    ByteInterleavedRaster srcRas;
    byte[] inData;
    int inOff;
    int inSpan;
    byte[] outData;
    int outOff;
    int outSpan;
    
    public Byte(ByteInterleavedRaster paramByteInterleavedRaster, ColorModel paramColorModel, AffineTransform paramAffineTransform, int paramInt)
    {
      super(paramAffineTransform, paramByteInterleavedRaster.getWidth(), paramByteInterleavedRaster.getHeight(), paramInt);
      this.srcRas = paramByteInterleavedRaster;
      this.inData = paramByteInterleavedRaster.getDataStorage();
      this.inSpan = paramByteInterleavedRaster.getScanlineStride();
      this.inOff = paramByteInterleavedRaster.getDataOffset(0);
    }
    
    public WritableRaster makeRaster(int paramInt1, int paramInt2)
    {
      WritableRaster localWritableRaster = makeByteRaster(this.srcRas, paramInt1, paramInt2);
      ByteInterleavedRaster localByteInterleavedRaster = (ByteInterleavedRaster)localWritableRaster;
      this.outData = localByteInterleavedRaster.getDataStorage();
      this.outSpan = localByteInterleavedRaster.getScanlineStride();
      this.outOff = localByteInterleavedRaster.getDataOffset(0);
      return localWritableRaster;
    }
    
    public void dispose()
    {
      dropByteRaster(this.outRas);
    }
    
    public void setRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, int paramInt13, int paramInt14, int paramInt15, int paramInt16)
    {
      byte[] arrayOfByte1 = this.inData;
      byte[] arrayOfByte2 = this.outData;
      int i = this.outOff;
      int j = this.inSpan;
      int k = this.inOff;
      int m = this.outSpan;
      int n = (paramInt9 == 1) && (paramInt10 == 0) && (paramInt11 == 0) && (paramInt12 == 0) ? 1 : 0;
      int i1 = paramInt1;
      int i2 = paramInt2;
      int i3 = paramInt3;
      int i4 = paramInt4;
      if (n != 0) {
        m -= paramInt5;
      }
      for (int i5 = 0; i5 < paramInt6; i5++)
      {
        int i6;
        if (n != 0)
        {
          i6 = k + i2 * j + paramInt7;
          paramInt1 = paramInt7 - i1;
          i += paramInt5;
          int i7;
          if (paramInt7 >= 32)
          {
            i7 = paramInt5;
            while (i7 > 0)
            {
              int i8 = i7 < paramInt1 ? i7 : paramInt1;
              System.arraycopy(arrayOfByte1, i6 - paramInt1, arrayOfByte2, i - i7, i8);
              i7 -= i8;
              if (paramInt1 -= i8 == 0) {
                paramInt1 = paramInt7;
              }
            }
          }
          else
          {
            for (i7 = paramInt5; i7 > 0; i7--)
            {
              arrayOfByte2[(i - i7)] = arrayOfByte1[(i6 - paramInt1)];
              paramInt1--;
              if (paramInt1 == 0) {
                paramInt1 = paramInt7;
              }
            }
          }
        }
        else
        {
          paramInt1 = i1;
          paramInt2 = i2;
          paramInt3 = i3;
          paramInt4 = i4;
          for (i6 = 0; i6 < paramInt5; i6++)
          {
            arrayOfByte2[(i + i6)] = arrayOfByte1[(k + paramInt2 * j + paramInt1)];
            if (paramInt3 += paramInt10 < 0)
            {
              paramInt3 &= 0x7FFFFFFF;
              paramInt1++;
            }
            if (paramInt1 += paramInt9 >= paramInt7) {
              paramInt1 -= paramInt7;
            }
            if (paramInt4 += paramInt12 < 0)
            {
              paramInt4 &= 0x7FFFFFFF;
              paramInt2++;
            }
            if (paramInt2 += paramInt11 >= paramInt8) {
              paramInt2 -= paramInt8;
            }
          }
        }
        if (i3 += paramInt14 < 0)
        {
          i3 &= 0x7FFFFFFF;
          i1++;
        }
        if (i1 += paramInt13 >= paramInt7) {
          i1 -= paramInt7;
        }
        if (i4 += paramInt16 < 0)
        {
          i4 &= 0x7FFFFFFF;
          i2++;
        }
        if (i2 += paramInt15 >= paramInt8) {
          i2 -= paramInt8;
        }
        i += m;
      }
    }
  }
  
  static class ByteFilter
    extends TexturePaintContext
  {
    ByteInterleavedRaster srcRas;
    int[] inPalette = new int['Ā'];
    byte[] inData;
    int inOff;
    int inSpan;
    int[] outData;
    int outOff;
    int outSpan;
    
    public ByteFilter(ByteInterleavedRaster paramByteInterleavedRaster, ColorModel paramColorModel, AffineTransform paramAffineTransform, int paramInt)
    {
      super(paramAffineTransform, paramByteInterleavedRaster.getWidth(), paramByteInterleavedRaster.getHeight(), paramInt);
      ((IndexColorModel)paramColorModel).getRGBs(this.inPalette);
      this.srcRas = paramByteInterleavedRaster;
      this.inData = paramByteInterleavedRaster.getDataStorage();
      this.inSpan = paramByteInterleavedRaster.getScanlineStride();
      this.inOff = paramByteInterleavedRaster.getDataOffset(0);
    }
    
    public WritableRaster makeRaster(int paramInt1, int paramInt2)
    {
      WritableRaster localWritableRaster = makeRaster(this.colorModel, null, paramInt1, paramInt2);
      IntegerInterleavedRaster localIntegerInterleavedRaster = (IntegerInterleavedRaster)localWritableRaster;
      this.outData = localIntegerInterleavedRaster.getDataStorage();
      this.outSpan = localIntegerInterleavedRaster.getScanlineStride();
      this.outOff = localIntegerInterleavedRaster.getDataOffset(0);
      return localWritableRaster;
    }
    
    public void setRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, int paramInt13, int paramInt14, int paramInt15, int paramInt16)
    {
      byte[] arrayOfByte = this.inData;
      int[] arrayOfInt1 = this.outData;
      int i = this.outOff;
      int j = this.inSpan;
      int k = this.inOff;
      int m = this.outSpan;
      int n = paramInt1;
      int i1 = paramInt2;
      int i2 = paramInt3;
      int i3 = paramInt4;
      int[] arrayOfInt2 = new int[4];
      for (int i4 = 0; i4 < paramInt6; i4++)
      {
        paramInt1 = n;
        paramInt2 = i1;
        paramInt3 = i2;
        paramInt4 = i3;
        for (int i5 = 0; i5 < paramInt5; i5++)
        {
          int i6;
          if ((i6 = paramInt1 + 1) >= paramInt7) {
            i6 = 0;
          }
          int i7;
          if ((i7 = paramInt2 + 1) >= paramInt8) {
            i7 = 0;
          }
          arrayOfInt2[0] = this.inPalette[(0xFF & arrayOfByte[(k + paramInt1 + j * paramInt2)])];
          arrayOfInt2[1] = this.inPalette[(0xFF & arrayOfByte[(k + i6 + j * paramInt2)])];
          arrayOfInt2[2] = this.inPalette[(0xFF & arrayOfByte[(k + paramInt1 + j * i7)])];
          arrayOfInt2[3] = this.inPalette[(0xFF & arrayOfByte[(k + i6 + j * i7)])];
          arrayOfInt1[(i + i5)] = TexturePaintContext.blend(arrayOfInt2, paramInt3, paramInt4);
          if (paramInt3 += paramInt10 < 0)
          {
            paramInt3 &= 0x7FFFFFFF;
            paramInt1++;
          }
          if (paramInt1 += paramInt9 >= paramInt7) {
            paramInt1 -= paramInt7;
          }
          if (paramInt4 += paramInt12 < 0)
          {
            paramInt4 &= 0x7FFFFFFF;
            paramInt2++;
          }
          if (paramInt2 += paramInt11 >= paramInt8) {
            paramInt2 -= paramInt8;
          }
        }
        if (i2 += paramInt14 < 0)
        {
          i2 &= 0x7FFFFFFF;
          n++;
        }
        if (n += paramInt13 >= paramInt7) {
          n -= paramInt7;
        }
        if (i3 += paramInt16 < 0)
        {
          i3 &= 0x7FFFFFFF;
          i1++;
        }
        if (i1 += paramInt15 >= paramInt8) {
          i1 -= paramInt8;
        }
        i += m;
      }
    }
  }
  
  static class Int
    extends TexturePaintContext
  {
    IntegerInterleavedRaster srcRas;
    int[] inData;
    int inOff;
    int inSpan;
    int[] outData;
    int outOff;
    int outSpan;
    boolean filter;
    
    public Int(IntegerInterleavedRaster paramIntegerInterleavedRaster, ColorModel paramColorModel, AffineTransform paramAffineTransform, int paramInt, boolean paramBoolean)
    {
      super(paramAffineTransform, paramIntegerInterleavedRaster.getWidth(), paramIntegerInterleavedRaster.getHeight(), paramInt);
      this.srcRas = paramIntegerInterleavedRaster;
      this.inData = paramIntegerInterleavedRaster.getDataStorage();
      this.inSpan = paramIntegerInterleavedRaster.getScanlineStride();
      this.inOff = paramIntegerInterleavedRaster.getDataOffset(0);
      this.filter = paramBoolean;
    }
    
    public WritableRaster makeRaster(int paramInt1, int paramInt2)
    {
      WritableRaster localWritableRaster = makeRaster(this.colorModel, this.srcRas, paramInt1, paramInt2);
      IntegerInterleavedRaster localIntegerInterleavedRaster = (IntegerInterleavedRaster)localWritableRaster;
      this.outData = localIntegerInterleavedRaster.getDataStorage();
      this.outSpan = localIntegerInterleavedRaster.getScanlineStride();
      this.outOff = localIntegerInterleavedRaster.getDataOffset(0);
      return localWritableRaster;
    }
    
    public void setRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, int paramInt13, int paramInt14, int paramInt15, int paramInt16)
    {
      int[] arrayOfInt1 = this.inData;
      int[] arrayOfInt2 = this.outData;
      int i = this.outOff;
      int j = this.inSpan;
      int k = this.inOff;
      int m = this.outSpan;
      boolean bool = this.filter;
      int n = (paramInt9 == 1) && (paramInt10 == 0) && (paramInt11 == 0) && (paramInt12 == 0) && (!bool) ? 1 : 0;
      int i1 = paramInt1;
      int i2 = paramInt2;
      int i3 = paramInt3;
      int i4 = paramInt4;
      if (n != 0) {
        m -= paramInt5;
      }
      int[] arrayOfInt3 = bool ? new int[4] : null;
      for (int i5 = 0; i5 < paramInt6; i5++)
      {
        int i6;
        int i7;
        int i8;
        if (n != 0)
        {
          i6 = k + i2 * j + paramInt7;
          paramInt1 = paramInt7 - i1;
          i += paramInt5;
          if (paramInt7 >= 32)
          {
            i7 = paramInt5;
            while (i7 > 0)
            {
              i8 = i7 < paramInt1 ? i7 : paramInt1;
              System.arraycopy(arrayOfInt1, i6 - paramInt1, arrayOfInt2, i - i7, i8);
              i7 -= i8;
              if (paramInt1 -= i8 == 0) {
                paramInt1 = paramInt7;
              }
            }
          }
          else
          {
            for (i7 = paramInt5; i7 > 0; i7--)
            {
              arrayOfInt2[(i - i7)] = arrayOfInt1[(i6 - paramInt1)];
              paramInt1--;
              if (paramInt1 == 0) {
                paramInt1 = paramInt7;
              }
            }
          }
        }
        else
        {
          paramInt1 = i1;
          paramInt2 = i2;
          paramInt3 = i3;
          paramInt4 = i4;
          for (i6 = 0; i6 < paramInt5; i6++)
          {
            if (bool)
            {
              if ((i7 = paramInt1 + 1) >= paramInt7) {
                i7 = 0;
              }
              if ((i8 = paramInt2 + 1) >= paramInt8) {
                i8 = 0;
              }
              arrayOfInt3[0] = arrayOfInt1[(k + paramInt2 * j + paramInt1)];
              arrayOfInt3[1] = arrayOfInt1[(k + paramInt2 * j + i7)];
              arrayOfInt3[2] = arrayOfInt1[(k + i8 * j + paramInt1)];
              arrayOfInt3[3] = arrayOfInt1[(k + i8 * j + i7)];
              arrayOfInt2[(i + i6)] = TexturePaintContext.blend(arrayOfInt3, paramInt3, paramInt4);
            }
            else
            {
              arrayOfInt2[(i + i6)] = arrayOfInt1[(k + paramInt2 * j + paramInt1)];
            }
            if (paramInt3 += paramInt10 < 0)
            {
              paramInt3 &= 0x7FFFFFFF;
              paramInt1++;
            }
            if (paramInt1 += paramInt9 >= paramInt7) {
              paramInt1 -= paramInt7;
            }
            if (paramInt4 += paramInt12 < 0)
            {
              paramInt4 &= 0x7FFFFFFF;
              paramInt2++;
            }
            if (paramInt2 += paramInt11 >= paramInt8) {
              paramInt2 -= paramInt8;
            }
          }
        }
        if (i3 += paramInt14 < 0)
        {
          i3 &= 0x7FFFFFFF;
          i1++;
        }
        if (i1 += paramInt13 >= paramInt7) {
          i1 -= paramInt7;
        }
        if (i4 += paramInt16 < 0)
        {
          i4 &= 0x7FFFFFFF;
          i2++;
        }
        if (i2 += paramInt15 >= paramInt8) {
          i2 -= paramInt8;
        }
        i += m;
      }
    }
  }
}
