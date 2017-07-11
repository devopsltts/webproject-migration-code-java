package java.awt.font;

import java.io.PrintStream;

class TextJustifier
{
  private GlyphJustificationInfo[] info;
  private int start;
  private int limit;
  static boolean DEBUG = false;
  public static final int MAX_PRIORITY = 3;
  
  TextJustifier(GlyphJustificationInfo[] paramArrayOfGlyphJustificationInfo, int paramInt1, int paramInt2)
  {
    this.info = paramArrayOfGlyphJustificationInfo;
    this.start = paramInt1;
    this.limit = paramInt2;
    if (DEBUG)
    {
      System.out.println("start: " + paramInt1 + ", limit: " + paramInt2);
      for (int i = paramInt1; i < paramInt2; i++)
      {
        GlyphJustificationInfo localGlyphJustificationInfo = paramArrayOfGlyphJustificationInfo[i];
        System.out.println("w: " + localGlyphJustificationInfo.weight + ", gp: " + localGlyphJustificationInfo.growPriority + ", gll: " + localGlyphJustificationInfo.growLeftLimit + ", grl: " + localGlyphJustificationInfo.growRightLimit);
      }
    }
  }
  
  public float[] justify(float paramFloat)
  {
    float[] arrayOfFloat = new float[this.info.length * 2];
    int i = paramFloat > 0.0F ? 1 : 0;
    if (DEBUG) {
      System.out.println("delta: " + paramFloat);
    }
    int j = -1;
    int m;
    for (int k = 0; paramFloat != 0.0F; k++)
    {
      m = k > 3 ? 1 : 0;
      if (m != 0) {
        k = j;
      }
      float f2 = 0.0F;
      float f3 = 0.0F;
      float f4 = 0.0F;
      for (int n = this.start; n < this.limit; n++)
      {
        GlyphJustificationInfo localGlyphJustificationInfo1 = this.info[n];
        if ((i != 0 ? localGlyphJustificationInfo1.growPriority : localGlyphJustificationInfo1.shrinkPriority) == k)
        {
          if (j == -1) {
            j = k;
          }
          if (n != this.start)
          {
            f2 += localGlyphJustificationInfo1.weight;
            if (i != 0)
            {
              f3 += localGlyphJustificationInfo1.growLeftLimit;
              if (localGlyphJustificationInfo1.growAbsorb) {
                f4 += localGlyphJustificationInfo1.weight;
              }
            }
            else
            {
              f3 += localGlyphJustificationInfo1.shrinkLeftLimit;
              if (localGlyphJustificationInfo1.shrinkAbsorb) {
                f4 += localGlyphJustificationInfo1.weight;
              }
            }
          }
          if (n + 1 != this.limit)
          {
            f2 += localGlyphJustificationInfo1.weight;
            if (i != 0)
            {
              f3 += localGlyphJustificationInfo1.growRightLimit;
              if (localGlyphJustificationInfo1.growAbsorb) {
                f4 += localGlyphJustificationInfo1.weight;
              }
            }
            else
            {
              f3 += localGlyphJustificationInfo1.shrinkRightLimit;
              if (localGlyphJustificationInfo1.shrinkAbsorb) {
                f4 += localGlyphJustificationInfo1.weight;
              }
            }
          }
        }
      }
      if (i == 0) {
        f3 = -f3;
      }
      if (f2 != 0.0F) {
        if (m != 0) {
          break label375;
        }
      }
      label375:
      n = (paramFloat < 0.0F ? 1 : 0) == (paramFloat < f3 ? 1 : 0) ? 1 : 0;
      int i1 = (n != 0) && (f4 > 0.0F) ? 1 : 0;
      float f5 = paramFloat / f2;
      float f6 = 0.0F;
      if ((n != 0) && (f4 > 0.0F)) {
        f6 = (paramFloat - f3) / f4;
      }
      if (DEBUG) {
        System.out.println("pass: " + k + ", d: " + paramFloat + ", l: " + f3 + ", w: " + f2 + ", aw: " + f4 + ", wd: " + f5 + ", wa: " + f6 + ", hit: " + (n != 0 ? "y" : "n"));
      }
      int i2 = this.start * 2;
      for (int i3 = this.start; i3 < this.limit; i3++)
      {
        GlyphJustificationInfo localGlyphJustificationInfo2 = this.info[i3];
        if ((i != 0 ? localGlyphJustificationInfo2.growPriority : localGlyphJustificationInfo2.shrinkPriority) == k)
        {
          float f7;
          if (i3 != this.start)
          {
            if (n != 0)
            {
              f7 = i != 0 ? localGlyphJustificationInfo2.growLeftLimit : -localGlyphJustificationInfo2.shrinkLeftLimit;
              if (i1 != 0) {
                f7 += localGlyphJustificationInfo2.weight * f6;
              }
            }
            else
            {
              f7 = localGlyphJustificationInfo2.weight * f5;
            }
            arrayOfFloat[i2] += f7;
          }
          i2++;
          if (i3 + 1 != this.limit)
          {
            if (n != 0)
            {
              f7 = i != 0 ? localGlyphJustificationInfo2.growRightLimit : -localGlyphJustificationInfo2.shrinkRightLimit;
              if (i1 != 0) {
                f7 += localGlyphJustificationInfo2.weight * f6;
              }
            }
            else
            {
              f7 = localGlyphJustificationInfo2.weight * f5;
            }
            arrayOfFloat[i2] += f7;
          }
          i2++;
        }
        else
        {
          i2 += 2;
        }
      }
      if ((m == 0) && (n != 0) && (i1 == 0)) {
        paramFloat -= f3;
      } else {
        paramFloat = 0.0F;
      }
    }
    if (DEBUG)
    {
      float f1 = 0.0F;
      for (m = 0; m < arrayOfFloat.length; m++)
      {
        f1 += arrayOfFloat[m];
        System.out.print(arrayOfFloat[m] + ", ");
        if (m % 20 == 9) {
          System.out.println();
        }
      }
      System.out.println("\ntotal: " + f1);
      System.out.println();
    }
    return arrayOfFloat;
  }
}
