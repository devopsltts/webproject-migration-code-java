package sun.java2d.pipe;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.DrawGlyphListLCD;
import sun.java2d.loops.RenderLoops;

public class LCDTextRenderer
  extends GlyphListLoopPipe
{
  public LCDTextRenderer() {}
  
  protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList)
  {
    paramSunGraphics2D.loops.drawGlyphListLCDLoop.DrawGlyphListLCD(paramSunGraphics2D, paramSunGraphics2D.surfaceData, paramGlyphList);
  }
}
