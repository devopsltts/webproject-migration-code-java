package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public class TextRenderer
  extends GlyphListPipe
{
  CompositePipe outpipe;
  
  public TextRenderer(CompositePipe paramCompositePipe)
  {
    this.outpipe = paramCompositePipe;
  }
  
  protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList)
  {
    int i = paramGlyphList.getNumGlyphs();
    Region localRegion = paramSunGraphics2D.getCompClip();
    int j = localRegion.getLoX();
    int k = localRegion.getLoY();
    int m = localRegion.getHiX();
    int n = localRegion.getHiY();
    Object localObject1 = null;
    try
    {
      int[] arrayOfInt1 = paramGlyphList.getBounds();
      Rectangle localRectangle = new Rectangle(arrayOfInt1[0], arrayOfInt1[1], arrayOfInt1[2] - arrayOfInt1[0], arrayOfInt1[3] - arrayOfInt1[1]);
      Shape localShape = paramSunGraphics2D.untransformShape(localRectangle);
      localObject1 = this.outpipe.startSequence(paramSunGraphics2D, localShape, localRectangle, arrayOfInt1);
      for (int i1 = 0; i1 < i; i1++)
      {
        paramGlyphList.setGlyphIndex(i1);
        int[] arrayOfInt2 = paramGlyphList.getMetrics();
        int i2 = arrayOfInt2[0];
        int i3 = arrayOfInt2[1];
        int i4 = arrayOfInt2[2];
        int i5 = i2 + i4;
        int i6 = i3 + arrayOfInt2[3];
        int i7 = 0;
        if (i2 < j)
        {
          i7 = j - i2;
          i2 = j;
        }
        if (i3 < k)
        {
          i7 += (k - i3) * i4;
          i3 = k;
        }
        if (i5 > m) {
          i5 = m;
        }
        if (i6 > n) {
          i6 = n;
        }
        if ((i5 > i2) && (i6 > i3) && (this.outpipe.needTile(localObject1, i2, i3, i5 - i2, i6 - i3)))
        {
          byte[] arrayOfByte = paramGlyphList.getGrayBits();
          this.outpipe.renderPathTile(localObject1, arrayOfByte, i7, i4, i2, i3, i5 - i2, i6 - i3);
        }
        else
        {
          this.outpipe.skipTile(localObject1, i2, i3);
        }
      }
    }
    finally
    {
      if (localObject1 != null) {
        this.outpipe.endSequence(localObject1);
      }
    }
  }
}
