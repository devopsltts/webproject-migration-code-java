package sun.java2d.cmm.kcms;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.ProfileDeferralMgr;

public class ICC_Transform
  implements ColorTransform
{
  long ID;
  
  public ICC_Transform() {}
  
  long getID()
  {
    return this.ID;
  }
  
  public void finalize()
  {
    CMM.checkStatus(CMM.cmmFreeTransform(this.ID));
  }
  
  public int getNumInComponents()
  {
    int[] arrayOfInt = new int[2];
    CMM.checkStatus(CMM.cmmGetNumComponents(this.ID, arrayOfInt));
    return arrayOfInt[0];
  }
  
  public int getNumOutComponents()
  {
    int[] arrayOfInt = new int[2];
    CMM.checkStatus(CMM.cmmGetNumComponents(this.ID, arrayOfInt));
    return arrayOfInt[1];
  }
  
  public void colorConvert(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2)
  {
    CMMImageLayout localCMMImageLayout1 = getImageLayout(paramBufferedImage1);
    CMMImageLayout localCMMImageLayout2;
    if (localCMMImageLayout1 != null)
    {
      localCMMImageLayout2 = getImageLayout(paramBufferedImage2);
      if (localCMMImageLayout2 != null)
      {
        synchronized (this)
        {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, localCMMImageLayout1, localCMMImageLayout2));
        }
        return;
      }
    }
    ??? = paramBufferedImage1.getRaster();
    WritableRaster localWritableRaster = paramBufferedImage2.getRaster();
    ColorModel localColorModel1 = paramBufferedImage1.getColorModel();
    ColorModel localColorModel2 = paramBufferedImage2.getColorModel();
    Object localObject2 = paramBufferedImage1.getWidth();
    int i = paramBufferedImage1.getHeight();
    Object localObject3 = localColorModel1.getNumColorComponents();
    Object localObject4 = localColorModel2.getNumColorComponents();
    int j = 8;
    float f = 255.0F;
    for (Object localObject5 = 0; localObject5 < localObject3; localObject5++) {
      if (localColorModel1.getComponentSize(localObject5) > 8)
      {
        j = 16;
        f = 65535.0F;
      }
    }
    for (Object localObject6 = 0; localObject6 < localObject4; localObject6++) {
      if (localColorModel2.getComponentSize(localObject6) > 8)
      {
        j = 16;
        f = 65535.0F;
      }
    }
    localObject6 = new float[localObject3];
    float[] arrayOfFloat1 = new float[localObject3];
    ColorSpace localColorSpace = localColorModel1.getColorSpace();
    for (Object localObject7 = 0; localObject7 < localObject3; localObject7++)
    {
      localObject6[localObject7] = localColorSpace.getMinValue(localObject7);
      arrayOfFloat1[localObject7] = (f / (localColorSpace.getMaxValue(localObject7) - localObject6[localObject7]));
    }
    localColorSpace = localColorModel2.getColorSpace();
    localObject7 = new float[localObject4];
    float[] arrayOfFloat2 = new float[localObject4];
    for (Object localObject8 = 0; localObject8 < localObject4; localObject8++)
    {
      localObject7[localObject8] = localColorSpace.getMinValue(localObject8);
      arrayOfFloat2[localObject8] = ((localColorSpace.getMaxValue(localObject8) - localObject7[localObject8]) / f);
    }
    boolean bool = localColorModel2.hasAlpha();
    int k = (localColorModel1.hasAlpha()) && (bool) ? 1 : 0;
    float[] arrayOfFloat3;
    if (bool) {
      arrayOfFloat3 = new float[localObject4 + 1];
    } else {
      arrayOfFloat3 = new float[localObject4];
    }
    Object localObject9;
    Object localObject10;
    float[] arrayOfFloat5;
    pelArrayInfo localPelArrayInfo;
    Object localObject11;
    float[] arrayOfFloat4;
    int m;
    if (j == 8)
    {
      localObject9 = new byte[localObject2 * localObject3];
      localObject10 = new byte[localObject2 * localObject4];
      arrayOfFloat5 = null;
      if (k != 0) {
        arrayOfFloat5 = new float[localObject2];
      }
      localPelArrayInfo = new pelArrayInfo(this, (byte[])localObject9, (byte[])localObject10);
      try
      {
        localCMMImageLayout1 = new CMMImageLayout((byte[])localObject9, localPelArrayInfo.nPels, localPelArrayInfo.nSrc);
        localCMMImageLayout2 = new CMMImageLayout((byte[])localObject10, localPelArrayInfo.nPels, localPelArrayInfo.nDest);
      }
      catch (CMMImageLayout.ImageLayoutException localImageLayoutException1)
      {
        throw new CMMException("Unable to convert images");
      }
      for (int n = 0; n < i; n++)
      {
        localObject11 = null;
        arrayOfFloat4 = null;
        m = 0;
        for (Object localObject12 = 0; localObject12 < localObject2; localObject12++)
        {
          localObject11 = ((Raster)???).getDataElements(localObject12, n, localObject11);
          arrayOfFloat4 = localColorModel1.getNormalizedComponents(localObject11, arrayOfFloat4, 0);
          for (Object localObject16 = 0; localObject16 < localObject3; localObject16++) {
            localObject9[(m++)] = ((byte)(int)((arrayOfFloat4[localObject16] - localObject6[localObject16]) * arrayOfFloat1[localObject16] + 0.5F));
          }
          if (k != 0) {
            arrayOfFloat5[localObject12] = arrayOfFloat4[localObject3];
          }
        }
        synchronized (this)
        {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, localCMMImageLayout1, localCMMImageLayout2));
        }
        localObject11 = null;
        m = 0;
        for (Object localObject13 = 0; localObject13 < localObject2; localObject13++)
        {
          for (Object localObject17 = 0; localObject17 < localObject4; localObject17++) {
            arrayOfFloat3[localObject17] = ((localObject10[(m++)] & 0xFF) * arrayOfFloat2[localObject17] + localObject7[localObject17]);
          }
          if (k != 0) {
            arrayOfFloat3[localObject4] = arrayOfFloat5[localObject13];
          } else if (bool) {
            arrayOfFloat3[localObject4] = 1.0F;
          }
          localObject11 = localColorModel2.getDataElements(arrayOfFloat3, 0, localObject11);
          localWritableRaster.setDataElements(localObject13, n, localObject11);
        }
      }
    }
    else
    {
      localObject9 = new short[localObject2 * localObject3];
      localObject10 = new short[localObject2 * localObject4];
      arrayOfFloat5 = null;
      if (k != 0) {
        arrayOfFloat5 = new float[localObject2];
      }
      localPelArrayInfo = new pelArrayInfo(this, (short[])localObject9, (short[])localObject10);
      try
      {
        localCMMImageLayout1 = new CMMImageLayout((short[])localObject9, localPelArrayInfo.nPels, localPelArrayInfo.nSrc);
        localCMMImageLayout2 = new CMMImageLayout((short[])localObject10, localPelArrayInfo.nPels, localPelArrayInfo.nDest);
      }
      catch (CMMImageLayout.ImageLayoutException localImageLayoutException2)
      {
        throw new CMMException("Unable to convert images");
      }
      for (int i1 = 0; i1 < i; i1++)
      {
        localObject11 = null;
        arrayOfFloat4 = null;
        m = 0;
        for (??? = 0; ??? < localObject2; ???++)
        {
          localObject11 = ((Raster)???).getDataElements(???, i1, localObject11);
          arrayOfFloat4 = localColorModel1.getNormalizedComponents(localObject11, arrayOfFloat4, 0);
          for (Object localObject18 = 0; localObject18 < localObject3; localObject18++) {
            localObject9[(m++)] = ((short)(int)((arrayOfFloat4[localObject18] - localObject6[localObject18]) * arrayOfFloat1[localObject18] + 0.5F));
          }
          if (k != 0) {
            arrayOfFloat5[???] = arrayOfFloat4[localObject3];
          }
        }
        synchronized (this)
        {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, localCMMImageLayout1, localCMMImageLayout2));
        }
        localObject11 = null;
        m = 0;
        for (Object localObject15 = 0; localObject15 < localObject2; localObject15++)
        {
          for (Object localObject19 = 0; localObject19 < localObject4; localObject19++) {
            arrayOfFloat3[localObject19] = ((localObject10[(m++)] & 0xFFFF) * arrayOfFloat2[localObject19] + localObject7[localObject19]);
          }
          if (k != 0) {
            arrayOfFloat3[localObject4] = arrayOfFloat5[localObject15];
          } else if (bool) {
            arrayOfFloat3[localObject4] = 1.0F;
          }
          localObject11 = localColorModel2.getDataElements(arrayOfFloat3, 0, localObject11);
          localWritableRaster.setDataElements(localObject15, i1, localObject11);
        }
      }
    }
  }
  
  private CMMImageLayout getImageLayout(BufferedImage paramBufferedImage)
  {
    try
    {
      switch (paramBufferedImage.getType())
      {
      case 1: 
      case 2: 
      case 4: 
        return new CMMImageLayout(paramBufferedImage);
      case 5: 
      case 6: 
        localObject = (ComponentColorModel)paramBufferedImage.getColorModel();
        if ((localObject.getClass() == ComponentColorModel.class) || (checkMinMaxScaling((ComponentColorModel)localObject))) {
          return new CMMImageLayout(paramBufferedImage);
        }
        return null;
      case 10: 
        localObject = (ComponentColorModel)paramBufferedImage.getColorModel();
        if (((ComponentColorModel)localObject).getComponentSize(0) != 8) {
          return null;
        }
        if ((localObject.getClass() == ComponentColorModel.class) || (checkMinMaxScaling((ComponentColorModel)localObject))) {
          return new CMMImageLayout(paramBufferedImage);
        }
        return null;
      case 11: 
        localObject = (ComponentColorModel)paramBufferedImage.getColorModel();
        if (((ComponentColorModel)localObject).getComponentSize(0) != 16) {
          return null;
        }
        if ((localObject.getClass() == ComponentColorModel.class) || (checkMinMaxScaling((ComponentColorModel)localObject))) {
          return new CMMImageLayout(paramBufferedImage);
        }
        return null;
      }
      Object localObject = paramBufferedImage.getColorModel();
      SampleModel localSampleModel;
      int j;
      int k;
      if ((localObject instanceof DirectColorModel))
      {
        localSampleModel = paramBufferedImage.getSampleModel();
        if (!(localSampleModel instanceof SinglePixelPackedSampleModel)) {
          return null;
        }
        if (((ColorModel)localObject).getTransferType() != 3) {
          return null;
        }
        if ((((ColorModel)localObject).hasAlpha()) && (((ColorModel)localObject).isAlphaPremultiplied())) {
          return null;
        }
        DirectColorModel localDirectColorModel = (DirectColorModel)localObject;
        j = localDirectColorModel.getRedMask();
        k = localDirectColorModel.getGreenMask();
        int m = localDirectColorModel.getBlueMask();
        int n = localDirectColorModel.getAlphaMask();
        int i4;
        int i3;
        int i2;
        int i1 = i2 = i3 = i4 = -1;
        int i5 = 0;
        int i6 = 3;
        if (n != 0) {
          i6 = 4;
        }
        int i7 = 0;
        int i8 = -16777216;
        while (i7 < 4)
        {
          if (j == i8)
          {
            i1 = i7;
            i5++;
          }
          else if (k == i8)
          {
            i2 = i7;
            i5++;
          }
          else if (m == i8)
          {
            i3 = i7;
            i5++;
          }
          else if (n == i8)
          {
            i4 = i7;
            i5++;
          }
          i7++;
          i8 >>>= 8;
        }
        if (i5 != i6) {
          return null;
        }
        return new CMMImageLayout(paramBufferedImage, (SinglePixelPackedSampleModel)localSampleModel, i1, i2, i3, i4);
      }
      if ((localObject instanceof ComponentColorModel))
      {
        localSampleModel = paramBufferedImage.getSampleModel();
        if (!(localSampleModel instanceof ComponentSampleModel)) {
          return null;
        }
        if ((((ColorModel)localObject).hasAlpha()) && (((ColorModel)localObject).isAlphaPremultiplied())) {
          return null;
        }
        int i = ((ColorModel)localObject).getNumComponents();
        if (localSampleModel.getNumBands() != i) {
          return null;
        }
        j = ((ColorModel)localObject).getTransferType();
        if (j == 0) {
          for (k = 0; k < i; k++) {
            if (((ColorModel)localObject).getComponentSize(k) != 8) {
              return null;
            }
          }
        } else if (j == 1) {
          for (k = 0; k < i; k++) {
            if (((ColorModel)localObject).getComponentSize(k) != 16) {
              return null;
            }
          }
        } else {
          return null;
        }
        ComponentColorModel localComponentColorModel = (ComponentColorModel)localObject;
        if ((localComponentColorModel.getClass() == ComponentColorModel.class) || (checkMinMaxScaling(localComponentColorModel))) {
          return new CMMImageLayout(paramBufferedImage, (ComponentSampleModel)localSampleModel);
        }
        return null;
      }
      return null;
    }
    catch (CMMImageLayout.ImageLayoutException localImageLayoutException)
    {
      throw new CMMException("Unable to convert image");
    }
  }
  
  private boolean checkMinMaxScaling(ComponentColorModel paramComponentColorModel)
  {
    int i = paramComponentColorModel.getNumComponents();
    int j = paramComponentColorModel.getNumColorComponents();
    int[] arrayOfInt = paramComponentColorModel.getComponentSize();
    boolean bool = paramComponentColorModel.hasAlpha();
    float[] arrayOfFloat1;
    float[] arrayOfFloat2;
    float f1;
    switch (paramComponentColorModel.getTransferType())
    {
    case 0: 
      localObject = new byte[i];
      for (k = 0; k < j; k++) {
        localObject[k] = 0;
      }
      if (bool) {
        localObject[j] = ((byte)((1 << arrayOfInt[j]) - 1));
      }
      arrayOfFloat1 = paramComponentColorModel.getNormalizedComponents(localObject, null, 0);
      for (k = 0; k < j; k++) {
        localObject[k] = ((byte)((1 << arrayOfInt[k]) - 1));
      }
      arrayOfFloat2 = paramComponentColorModel.getNormalizedComponents(localObject, null, 0);
      f1 = 256.0F;
      break;
    case 1: 
      localObject = new short[i];
      for (k = 0; k < j; k++) {
        localObject[k] = 0;
      }
      if (bool) {
        localObject[j] = ((short)(byte)((1 << arrayOfInt[j]) - 1));
      }
      arrayOfFloat1 = paramComponentColorModel.getNormalizedComponents(localObject, null, 0);
      for (k = 0; k < j; k++) {
        localObject[k] = ((short)(byte)((1 << arrayOfInt[k]) - 1));
      }
      arrayOfFloat2 = paramComponentColorModel.getNormalizedComponents(localObject, null, 0);
      f1 = 65536.0F;
      break;
    default: 
      return false;
    }
    Object localObject = paramComponentColorModel.getColorSpace();
    for (int k = 0; k < j; k++)
    {
      float f2 = ((ColorSpace)localObject).getMinValue(k);
      float f3 = ((ColorSpace)localObject).getMaxValue(k);
      float f4 = (f3 - f2) / f1;
      f2 -= arrayOfFloat1[k];
      if (f2 < 0.0F) {
        f2 = -f2;
      }
      f3 -= arrayOfFloat2[k];
      if (f3 < 0.0F) {
        f3 = -f3;
      }
      if ((f2 > f4) || (f3 > f4)) {
        return false;
      }
    }
    return true;
  }
  
  public void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4)
  {
    SampleModel localSampleModel1 = paramRaster.getSampleModel();
    SampleModel localSampleModel2 = paramWritableRaster.getSampleModel();
    int i = paramRaster.getTransferType();
    int j = paramWritableRaster.getTransferType();
    int k;
    if ((i == 4) || (i == 5)) {
      k = 1;
    } else {
      k = 0;
    }
    int m;
    if ((j == 4) || (j == 5)) {
      m = 1;
    } else {
      m = 0;
    }
    Object localObject1 = paramRaster.getWidth();
    int n = paramRaster.getHeight();
    Object localObject2 = paramRaster.getNumBands();
    Object localObject3 = paramWritableRaster.getNumBands();
    float[] arrayOfFloat1 = new float[localObject2];
    float[] arrayOfFloat2 = new float[localObject3];
    float[] arrayOfFloat3 = new float[localObject2];
    float[] arrayOfFloat4 = new float[localObject3];
    for (Object localObject4 = 0; localObject4 < localObject2; localObject4++) {
      if (k != 0)
      {
        arrayOfFloat1[localObject4] = (65535.0F / (paramArrayOfFloat2[localObject4] - paramArrayOfFloat1[localObject4]));
        arrayOfFloat3[localObject4] = paramArrayOfFloat1[localObject4];
      }
      else
      {
        if (i == 2) {
          arrayOfFloat1[localObject4] = 2.0000305F;
        } else {
          arrayOfFloat1[localObject4] = (65535.0F / ((1 << localSampleModel1.getSampleSize(localObject4)) - 1));
        }
        arrayOfFloat3[localObject4] = 0.0F;
      }
    }
    for (Object localObject5 = 0; localObject5 < localObject3; localObject5++) {
      if (m != 0)
      {
        arrayOfFloat2[localObject5] = ((paramArrayOfFloat4[localObject5] - paramArrayOfFloat3[localObject5]) / 65535.0F);
        arrayOfFloat4[localObject5] = paramArrayOfFloat3[localObject5];
      }
      else
      {
        if (j == 2) {
          arrayOfFloat2[localObject5] = 0.49999237F;
        } else {
          arrayOfFloat2[localObject5] = (((1 << localSampleModel2.getSampleSize(localObject5)) - 1) / 65535.0F);
        }
        arrayOfFloat4[localObject5] = 0.0F;
      }
    }
    int i1 = paramRaster.getMinY();
    int i2 = paramWritableRaster.getMinY();
    short[] arrayOfShort1 = new short[localObject1 * localObject2];
    short[] arrayOfShort2 = new short[localObject1 * localObject3];
    pelArrayInfo localPelArrayInfo = new pelArrayInfo(this, arrayOfShort1, arrayOfShort2);
    CMMImageLayout localCMMImageLayout1;
    CMMImageLayout localCMMImageLayout2;
    try
    {
      localCMMImageLayout1 = new CMMImageLayout(arrayOfShort1, localPelArrayInfo.nPels, localPelArrayInfo.nSrc);
      localCMMImageLayout2 = new CMMImageLayout(arrayOfShort2, localPelArrayInfo.nPels, localPelArrayInfo.nDest);
    }
    catch (CMMImageLayout.ImageLayoutException localImageLayoutException)
    {
      throw new CMMException("Unable to convert rasters");
    }
    int i6 = 0;
    while (i6 < n)
    {
      int i3 = paramRaster.getMinX();
      int i5 = 0;
      Object localObject6 = 0;
      float f;
      while (localObject6 < localObject1)
      {
        for (Object localObject8 = 0; localObject8 < localObject2; localObject8++)
        {
          f = paramRaster.getSampleFloat(i3, i1, localObject8);
          arrayOfShort1[(i5++)] = ((short)(int)((f - arrayOfFloat3[localObject8]) * arrayOfFloat1[localObject8] + 0.5F));
        }
        localObject6++;
        i3++;
      }
      synchronized (this)
      {
        CMM.checkStatus(CMM.cmmColorConvert(this.ID, localCMMImageLayout1, localCMMImageLayout2));
      }
      int i4 = paramWritableRaster.getMinX();
      i5 = 0;
      Object localObject7 = 0;
      while (localObject7 < localObject1)
      {
        for (Object localObject9 = 0; localObject9 < localObject3; localObject9++)
        {
          f = (arrayOfShort2[(i5++)] & 0xFFFF) * arrayOfFloat2[localObject9] + arrayOfFloat4[localObject9];
          paramWritableRaster.setSample(i4, i2, localObject9, f);
        }
        localObject7++;
        i4++;
      }
      i6++;
      i1++;
      i2++;
    }
  }
  
  public void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster)
  {
    CMMImageLayout localCMMImageLayout1 = getImageLayout(paramRaster);
    CMMImageLayout localCMMImageLayout2;
    if (localCMMImageLayout1 != null)
    {
      localCMMImageLayout2 = getImageLayout(paramWritableRaster);
      if (localCMMImageLayout2 != null)
      {
        synchronized (this)
        {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, localCMMImageLayout1, localCMMImageLayout2));
        }
        return;
      }
    }
    ??? = paramRaster.getSampleModel();
    SampleModel localSampleModel = paramWritableRaster.getSampleModel();
    int i = paramRaster.getTransferType();
    int j = paramWritableRaster.getTransferType();
    Object localObject2 = paramRaster.getWidth();
    int k = paramRaster.getHeight();
    Object localObject3 = paramRaster.getNumBands();
    Object localObject4 = paramWritableRaster.getNumBands();
    int m = 8;
    float f = 255.0F;
    for (Object localObject5 = 0; localObject5 < localObject3; localObject5++) {
      if (((SampleModel)???).getSampleSize(localObject5) > 8)
      {
        m = 16;
        f = 65535.0F;
      }
    }
    for (Object localObject6 = 0; localObject6 < localObject4; localObject6++) {
      if (localSampleModel.getSampleSize(localObject6) > 8)
      {
        m = 16;
        f = 65535.0F;
      }
    }
    localObject6 = new float[localObject3];
    float[] arrayOfFloat = new float[localObject4];
    for (Object localObject7 = 0; localObject7 < localObject3; localObject7++) {
      if (i == 2) {
        localObject6[localObject7] = (f / 32767.0F);
      } else {
        localObject6[localObject7] = (f / ((1 << ((SampleModel)???).getSampleSize(localObject7)) - 1));
      }
    }
    for (Object localObject8 = 0; localObject8 < localObject4; localObject8++) {
      if (j == 2) {
        arrayOfFloat[localObject8] = (32767.0F / f);
      } else {
        arrayOfFloat[localObject8] = (((1 << localSampleModel.getSampleSize(localObject8)) - 1) / f);
      }
    }
    int n = paramRaster.getMinY();
    int i1 = paramWritableRaster.getMinY();
    Object localObject9;
    Object localObject10;
    pelArrayInfo localPelArrayInfo;
    int i2;
    int i5;
    int i4;
    int i3;
    if (m == 8)
    {
      localObject9 = new byte[localObject2 * localObject3];
      localObject10 = new byte[localObject2 * localObject4];
      localPelArrayInfo = new pelArrayInfo(this, (byte[])localObject9, (byte[])localObject10);
      try
      {
        localCMMImageLayout1 = new CMMImageLayout((byte[])localObject9, localPelArrayInfo.nPels, localPelArrayInfo.nSrc);
        localCMMImageLayout2 = new CMMImageLayout((byte[])localObject10, localPelArrayInfo.nPels, localPelArrayInfo.nDest);
      }
      catch (CMMImageLayout.ImageLayoutException localImageLayoutException1)
      {
        throw new CMMException("Unable to convert rasters");
      }
      int i6 = 0;
      while (i6 < k)
      {
        i2 = paramRaster.getMinX();
        i5 = 0;
        Object localObject11 = 0;
        while (localObject11 < localObject2)
        {
          for (Object localObject15 = 0; localObject15 < localObject3; localObject15++)
          {
            i4 = paramRaster.getSample(i2, n, localObject15);
            localObject9[(i5++)] = ((byte)(int)(i4 * localObject6[localObject15] + 0.5F));
          }
          localObject11++;
          i2++;
        }
        synchronized (this)
        {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, localCMMImageLayout1, localCMMImageLayout2));
        }
        i3 = paramWritableRaster.getMinX();
        i5 = 0;
        Object localObject12 = 0;
        while (localObject12 < localObject2)
        {
          for (Object localObject16 = 0; localObject16 < localObject4; localObject16++)
          {
            i4 = (int)((localObject10[(i5++)] & 0xFF) * arrayOfFloat[localObject16] + 0.5F);
            paramWritableRaster.setSample(i3, i1, localObject16, i4);
          }
          localObject12++;
          i3++;
        }
        i6++;
        n++;
        i1++;
      }
    }
    else
    {
      localObject9 = new short[localObject2 * localObject3];
      localObject10 = new short[localObject2 * localObject4];
      localPelArrayInfo = new pelArrayInfo(this, (short[])localObject9, (short[])localObject10);
      try
      {
        localCMMImageLayout1 = new CMMImageLayout((short[])localObject9, localPelArrayInfo.nPels, localPelArrayInfo.nSrc);
        localCMMImageLayout2 = new CMMImageLayout((short[])localObject10, localPelArrayInfo.nPels, localPelArrayInfo.nDest);
      }
      catch (CMMImageLayout.ImageLayoutException localImageLayoutException2)
      {
        throw new CMMException("Unable to convert rasters");
      }
      int i7 = 0;
      while (i7 < k)
      {
        i2 = paramRaster.getMinX();
        i5 = 0;
        ??? = 0;
        while (??? < localObject2)
        {
          for (Object localObject17 = 0; localObject17 < localObject3; localObject17++)
          {
            i4 = paramRaster.getSample(i2, n, localObject17);
            localObject9[(i5++)] = ((short)(int)(i4 * localObject6[localObject17] + 0.5F));
          }
          ???++;
          i2++;
        }
        synchronized (this)
        {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, localCMMImageLayout1, localCMMImageLayout2));
        }
        i3 = paramWritableRaster.getMinX();
        i5 = 0;
        Object localObject14 = 0;
        while (localObject14 < localObject2)
        {
          for (Object localObject18 = 0; localObject18 < localObject4; localObject18++)
          {
            i4 = (int)((localObject10[(i5++)] & 0xFFFF) * arrayOfFloat[localObject18] + 0.5F);
            paramWritableRaster.setSample(i3, i1, localObject18, i4);
          }
          localObject14++;
          i3++;
        }
        i7++;
        n++;
        i1++;
      }
    }
  }
  
  private CMMImageLayout getImageLayout(Raster paramRaster)
  {
    SampleModel localSampleModel = paramRaster.getSampleModel();
    if ((localSampleModel instanceof ComponentSampleModel))
    {
      int i = paramRaster.getNumBands();
      int j = localSampleModel.getTransferType();
      int k;
      if (j == 0) {
        for (k = 0; k < i; k++) {
          if (localSampleModel.getSampleSize(k) != 8) {
            return null;
          }
        }
      } else if (j == 1) {
        for (k = 0; k < i; k++) {
          if (localSampleModel.getSampleSize(k) != 16) {
            return null;
          }
        }
      } else {
        return null;
      }
      try
      {
        return new CMMImageLayout(paramRaster, (ComponentSampleModel)localSampleModel);
      }
      catch (CMMImageLayout.ImageLayoutException localImageLayoutException)
      {
        throw new CMMException("Unable to convert raster");
      }
    }
    return null;
  }
  
  public short[] colorConvert(short[] paramArrayOfShort1, short[] paramArrayOfShort2)
  {
    pelArrayInfo localPelArrayInfo = new pelArrayInfo(this, paramArrayOfShort1, paramArrayOfShort2);
    short[] arrayOfShort;
    if (paramArrayOfShort2 != null) {
      arrayOfShort = paramArrayOfShort2;
    } else {
      arrayOfShort = new short[localPelArrayInfo.destSize];
    }
    CMMImageLayout localCMMImageLayout1;
    CMMImageLayout localCMMImageLayout2;
    try
    {
      localCMMImageLayout1 = new CMMImageLayout(paramArrayOfShort1, localPelArrayInfo.nPels, localPelArrayInfo.nSrc);
      localCMMImageLayout2 = new CMMImageLayout(arrayOfShort, localPelArrayInfo.nPels, localPelArrayInfo.nDest);
    }
    catch (CMMImageLayout.ImageLayoutException localImageLayoutException)
    {
      throw new CMMException("Unable to convert data");
    }
    synchronized (this)
    {
      CMM.checkStatus(CMM.cmmColorConvert(this.ID, localCMMImageLayout1, localCMMImageLayout2));
    }
    return arrayOfShort;
  }
  
  public byte[] colorConvert(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    pelArrayInfo localPelArrayInfo = new pelArrayInfo(this, paramArrayOfByte1, paramArrayOfByte2);
    byte[] arrayOfByte;
    if (paramArrayOfByte2 != null) {
      arrayOfByte = paramArrayOfByte2;
    } else {
      arrayOfByte = new byte[localPelArrayInfo.destSize];
    }
    CMMImageLayout localCMMImageLayout1;
    CMMImageLayout localCMMImageLayout2;
    try
    {
      localCMMImageLayout1 = new CMMImageLayout(paramArrayOfByte1, localPelArrayInfo.nPels, localPelArrayInfo.nSrc);
      localCMMImageLayout2 = new CMMImageLayout(arrayOfByte, localPelArrayInfo.nPels, localPelArrayInfo.nDest);
    }
    catch (CMMImageLayout.ImageLayoutException localImageLayoutException)
    {
      throw new CMMException("Unable to convert data");
    }
    synchronized (this)
    {
      CMM.checkStatus(CMM.cmmColorConvert(this.ID, localCMMImageLayout1, localCMMImageLayout2));
    }
    return arrayOfByte;
  }
  
  static
  {
    if (ProfileDeferralMgr.deferring) {
      ProfileDeferralMgr.activateProfiles();
    }
  }
}
