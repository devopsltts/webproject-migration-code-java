package javax.swing;

import java.io.Serializable;

public class SizeRequirements
  implements Serializable
{
  public int minimum;
  public int preferred;
  public int maximum;
  public float alignment;
  
  public SizeRequirements()
  {
    this.minimum = 0;
    this.preferred = 0;
    this.maximum = 0;
    this.alignment = 0.5F;
  }
  
  public SizeRequirements(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    this.minimum = paramInt1;
    this.preferred = paramInt2;
    this.maximum = paramInt3;
    this.alignment = (paramFloat < 0.0F ? 0.0F : paramFloat > 1.0F ? 1.0F : paramFloat);
  }
  
  public String toString()
  {
    return "[" + this.minimum + "," + this.preferred + "," + this.maximum + "]@" + this.alignment;
  }
  
  public static SizeRequirements getTiledSizeRequirements(SizeRequirements[] paramArrayOfSizeRequirements)
  {
    SizeRequirements localSizeRequirements1 = new SizeRequirements();
    for (int i = 0; i < paramArrayOfSizeRequirements.length; i++)
    {
      SizeRequirements localSizeRequirements2 = paramArrayOfSizeRequirements[i];
      localSizeRequirements1.minimum = ((int)Math.min(localSizeRequirements1.minimum + localSizeRequirements2.minimum, 2147483647L));
      localSizeRequirements1.preferred = ((int)Math.min(localSizeRequirements1.preferred + localSizeRequirements2.preferred, 2147483647L));
      localSizeRequirements1.maximum = ((int)Math.min(localSizeRequirements1.maximum + localSizeRequirements2.maximum, 2147483647L));
    }
    return localSizeRequirements1;
  }
  
  public static SizeRequirements getAlignedSizeRequirements(SizeRequirements[] paramArrayOfSizeRequirements)
  {
    SizeRequirements localSizeRequirements1 = new SizeRequirements();
    SizeRequirements localSizeRequirements2 = new SizeRequirements();
    for (int i = 0; i < paramArrayOfSizeRequirements.length; i++)
    {
      SizeRequirements localSizeRequirements3 = paramArrayOfSizeRequirements[i];
      k = (int)(localSizeRequirements3.alignment * localSizeRequirements3.minimum);
      int m = localSizeRequirements3.minimum - k;
      localSizeRequirements1.minimum = Math.max(k, localSizeRequirements1.minimum);
      localSizeRequirements2.minimum = Math.max(m, localSizeRequirements2.minimum);
      k = (int)(localSizeRequirements3.alignment * localSizeRequirements3.preferred);
      m = localSizeRequirements3.preferred - k;
      localSizeRequirements1.preferred = Math.max(k, localSizeRequirements1.preferred);
      localSizeRequirements2.preferred = Math.max(m, localSizeRequirements2.preferred);
      k = (int)(localSizeRequirements3.alignment * localSizeRequirements3.maximum);
      m = localSizeRequirements3.maximum - k;
      localSizeRequirements1.maximum = Math.max(k, localSizeRequirements1.maximum);
      localSizeRequirements2.maximum = Math.max(m, localSizeRequirements2.maximum);
    }
    i = (int)Math.min(localSizeRequirements1.minimum + localSizeRequirements2.minimum, 2147483647L);
    int j = (int)Math.min(localSizeRequirements1.preferred + localSizeRequirements2.preferred, 2147483647L);
    int k = (int)Math.min(localSizeRequirements1.maximum + localSizeRequirements2.maximum, 2147483647L);
    float f = 0.0F;
    if (i > 0)
    {
      f = localSizeRequirements1.minimum / i;
      f = f < 0.0F ? 0.0F : f > 1.0F ? 1.0F : f;
    }
    return new SizeRequirements(i, j, k, f);
  }
  
  public static void calculateTiledPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    calculateTiledPositions(paramInt, paramSizeRequirements, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, true);
  }
  
  public static void calculateTiledPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
  {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    for (int i = 0; i < paramArrayOfSizeRequirements.length; i++)
    {
      l1 += paramArrayOfSizeRequirements[i].minimum;
      l2 += paramArrayOfSizeRequirements[i].preferred;
      l3 += paramArrayOfSizeRequirements[i].maximum;
    }
    if (paramInt >= l2) {
      expandedTile(paramInt, l1, l2, l3, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, paramBoolean);
    } else {
      compressedTile(paramInt, l1, l2, l3, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, paramBoolean);
    }
  }
  
  private static void compressedTile(int paramInt, long paramLong1, long paramLong2, long paramLong3, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
  {
    float f1 = (float)Math.min(paramLong2 - paramInt, paramLong2 - paramLong1);
    float f2 = paramLong2 - paramLong1 == 0L ? 0.0F : f1 / (float)(paramLong2 - paramLong1);
    int i;
    int j;
    SizeRequirements localSizeRequirements;
    float f3;
    if (paramBoolean)
    {
      i = 0;
      for (j = 0; j < paramArrayOfInt2.length; j++)
      {
        paramArrayOfInt1[j] = i;
        localSizeRequirements = paramArrayOfSizeRequirements[j];
        f3 = f2 * (localSizeRequirements.preferred - localSizeRequirements.minimum);
        paramArrayOfInt2[j] = ((int)(localSizeRequirements.preferred - f3));
        i = (int)Math.min(i + paramArrayOfInt2[j], 2147483647L);
      }
    }
    else
    {
      i = paramInt;
      for (j = 0; j < paramArrayOfInt2.length; j++)
      {
        localSizeRequirements = paramArrayOfSizeRequirements[j];
        f3 = f2 * (localSizeRequirements.preferred - localSizeRequirements.minimum);
        paramArrayOfInt2[j] = ((int)(localSizeRequirements.preferred - f3));
        paramArrayOfInt1[j] = (i - paramArrayOfInt2[j]);
        i = (int)Math.max(i - paramArrayOfInt2[j], 0L);
      }
    }
  }
  
  private static void expandedTile(int paramInt, long paramLong1, long paramLong2, long paramLong3, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
  {
    float f1 = (float)Math.min(paramInt - paramLong2, paramLong3 - paramLong2);
    float f2 = paramLong3 - paramLong2 == 0L ? 0.0F : f1 / (float)(paramLong3 - paramLong2);
    int i;
    int j;
    SizeRequirements localSizeRequirements;
    int k;
    if (paramBoolean)
    {
      i = 0;
      for (j = 0; j < paramArrayOfInt2.length; j++)
      {
        paramArrayOfInt1[j] = i;
        localSizeRequirements = paramArrayOfSizeRequirements[j];
        k = (int)(f2 * (localSizeRequirements.maximum - localSizeRequirements.preferred));
        paramArrayOfInt2[j] = ((int)Math.min(localSizeRequirements.preferred + k, 2147483647L));
        i = (int)Math.min(i + paramArrayOfInt2[j], 2147483647L);
      }
    }
    else
    {
      i = paramInt;
      for (j = 0; j < paramArrayOfInt2.length; j++)
      {
        localSizeRequirements = paramArrayOfSizeRequirements[j];
        k = (int)(f2 * (localSizeRequirements.maximum - localSizeRequirements.preferred));
        paramArrayOfInt2[j] = ((int)Math.min(localSizeRequirements.preferred + k, 2147483647L));
        paramArrayOfInt1[j] = (i - paramArrayOfInt2[j]);
        i = (int)Math.max(i - paramArrayOfInt2[j], 0L);
      }
    }
  }
  
  public static void calculateAlignedPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    calculateAlignedPositions(paramInt, paramSizeRequirements, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, true);
  }
  
  public static void calculateAlignedPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
  {
    float f1 = paramBoolean ? paramSizeRequirements.alignment : 1.0F - paramSizeRequirements.alignment;
    int i = (int)(paramInt * f1);
    int j = paramInt - i;
    for (int k = 0; k < paramArrayOfSizeRequirements.length; k++)
    {
      SizeRequirements localSizeRequirements = paramArrayOfSizeRequirements[k];
      float f2 = paramBoolean ? localSizeRequirements.alignment : 1.0F - localSizeRequirements.alignment;
      int m = (int)(localSizeRequirements.maximum * f2);
      int n = localSizeRequirements.maximum - m;
      int i1 = Math.min(i, m);
      int i2 = Math.min(j, n);
      paramArrayOfInt1[k] = (i - i1);
      paramArrayOfInt2[k] = ((int)Math.min(i1 + i2, 2147483647L));
    }
  }
  
  public static int[] adjustSizes(int paramInt, SizeRequirements[] paramArrayOfSizeRequirements)
  {
    return new int[0];
  }
}
