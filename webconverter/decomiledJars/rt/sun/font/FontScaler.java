package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D.Float;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public abstract class FontScaler
  implements DisposerRecord
{
  private static FontScaler nullScaler = null;
  private static Constructor<FontScaler> scalerConstructor = null;
  protected WeakReference<Font2D> font = null;
  protected long nativeScaler = 0L;
  protected boolean disposed = false;
  
  public FontScaler() {}
  
  public static FontScaler getScaler(Font2D paramFont2D, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    FontScaler localFontScaler = null;
    try
    {
      Object[] arrayOfObject = { paramFont2D, Integer.valueOf(paramInt1), Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt2) };
      localFontScaler = (FontScaler)scalerConstructor.newInstance(arrayOfObject);
      Disposer.addObjectRecord(paramFont2D, localFontScaler);
    }
    catch (Throwable localThrowable)
    {
      localFontScaler = nullScaler;
      FontManager localFontManager = FontManagerFactory.getInstance();
      localFontManager.deRegisterBadFont(paramFont2D);
    }
    return localFontScaler;
  }
  
  public static synchronized FontScaler getNullScaler()
  {
    if (nullScaler == null) {
      nullScaler = new NullFontScaler();
    }
    return nullScaler;
  }
  
  abstract StrikeMetrics getFontMetrics(long paramLong)
    throws FontScalerException;
  
  abstract float getGlyphAdvance(long paramLong, int paramInt)
    throws FontScalerException;
  
  abstract void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat)
    throws FontScalerException;
  
  abstract long getGlyphImage(long paramLong, int paramInt)
    throws FontScalerException;
  
  abstract Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt)
    throws FontScalerException;
  
  abstract GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2)
    throws FontScalerException;
  
  abstract GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2)
    throws FontScalerException;
  
  public void dispose() {}
  
  abstract int getNumGlyphs()
    throws FontScalerException;
  
  abstract int getMissingGlyphCode()
    throws FontScalerException;
  
  abstract int getGlyphCode(char paramChar)
    throws FontScalerException;
  
  abstract long getLayoutTableCache()
    throws FontScalerException;
  
  abstract Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2)
    throws FontScalerException;
  
  abstract long getUnitsPerEm();
  
  abstract long createScalerContext(double[] paramArrayOfDouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, boolean paramBoolean);
  
  abstract void invalidateScalerContext(long paramLong);
  
  static
  {
    Object localObject = null;
    Class[] arrayOfClass = { Font2D.class, Integer.TYPE, Boolean.TYPE, Integer.TYPE };
    try
    {
      if (FontUtilities.isOpenJDK) {
        localObject = Class.forName("sun.font.FreetypeFontScaler");
      } else {
        localObject = Class.forName("sun.font.T2KFontScaler");
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      localObject = NullFontScaler.class;
    }
    try
    {
      scalerConstructor = ((Class)localObject).getConstructor(arrayOfClass);
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
  }
}
