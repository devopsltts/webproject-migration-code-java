package com.sun.media.sound;

public final class SoftCubicResampler
  extends SoftAbstractResampler
{
  public SoftCubicResampler() {}
  
  public int getPadding()
  {
    return 3;
  }
  
  public void interpolate(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float[] paramArrayOfFloat3, float paramFloat2, float[] paramArrayOfFloat4, int[] paramArrayOfInt, int paramInt)
  {
    float f1 = paramArrayOfFloat3[0];
    float f2 = paramArrayOfFloat2[0];
    int i = paramArrayOfInt[0];
    float f3 = paramFloat1;
    int j = paramInt;
    int k;
    float f4;
    float f5;
    float f6;
    float f7;
    float f8;
    float f9;
    float f10;
    float f11;
    float f12;
    if (paramFloat2 == 0.0F) {
      while ((f2 < f3) && (i < j))
      {
        k = (int)f2;
        f4 = f2 - k;
        f5 = paramArrayOfFloat1[(k - 1)];
        f6 = paramArrayOfFloat1[k];
        f7 = paramArrayOfFloat1[(k + 1)];
        f8 = paramArrayOfFloat1[(k + 2)];
        f9 = f8 - f7 + f6 - f5;
        f10 = f5 - f6 - f9;
        f11 = f7 - f5;
        f12 = f6;
        paramArrayOfFloat4[(i++)] = (((f9 * f4 + f10) * f4 + f11) * f4 + f12);
        f2 += f1;
      }
    }
    while ((f2 < f3) && (i < j))
    {
      k = (int)f2;
      f4 = f2 - k;
      f5 = paramArrayOfFloat1[(k - 1)];
      f6 = paramArrayOfFloat1[k];
      f7 = paramArrayOfFloat1[(k + 1)];
      f8 = paramArrayOfFloat1[(k + 2)];
      f9 = f8 - f7 + f6 - f5;
      f10 = f5 - f6 - f9;
      f11 = f7 - f5;
      f12 = f6;
      paramArrayOfFloat4[(i++)] = (((f9 * f4 + f10) * f4 + f11) * f4 + f12);
      f2 += f1;
      f1 += paramFloat2;
    }
    paramArrayOfFloat2[0] = f2;
    paramArrayOfInt[0] = i;
    paramArrayOfFloat3[0] = f1;
  }
}
