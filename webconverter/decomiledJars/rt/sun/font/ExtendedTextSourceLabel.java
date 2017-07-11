package sun.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.io.PrintStream;
import java.util.Map;

class ExtendedTextSourceLabel
  extends ExtendedTextLabel
  implements Decoration.Label
{
  TextSource source;
  private Decoration decorator;
  private Font font;
  private AffineTransform baseTX;
  private CoreMetrics cm;
  Rectangle2D lb;
  Rectangle2D ab;
  Rectangle2D vb;
  Rectangle2D ib;
  StandardGlyphVector gv;
  float[] charinfo;
  private static final int posx = 0;
  private static final int posy = 1;
  private static final int advx = 2;
  private static final int advy = 3;
  private static final int visx = 4;
  private static final int visy = 5;
  private static final int visw = 6;
  private static final int vish = 7;
  private static final int numvals = 8;
  
  public ExtendedTextSourceLabel(TextSource paramTextSource, Decoration paramDecoration)
  {
    this.source = paramTextSource;
    this.decorator = paramDecoration;
    finishInit();
  }
  
  public ExtendedTextSourceLabel(TextSource paramTextSource, ExtendedTextSourceLabel paramExtendedTextSourceLabel, int paramInt)
  {
    this.source = paramTextSource;
    this.decorator = paramExtendedTextSourceLabel.decorator;
    finishInit();
  }
  
  private void finishInit()
  {
    this.font = this.source.getFont();
    Map localMap = this.font.getAttributes();
    this.baseTX = AttributeValues.getBaselineTransform(localMap);
    if (this.baseTX == null)
    {
      this.cm = this.source.getCoreMetrics();
    }
    else
    {
      AffineTransform localAffineTransform = AttributeValues.getCharTransform(localMap);
      if (localAffineTransform == null) {
        localAffineTransform = new AffineTransform();
      }
      this.font = this.font.deriveFont(localAffineTransform);
      LineMetrics localLineMetrics = this.font.getLineMetrics(this.source.getChars(), this.source.getStart(), this.source.getStart() + this.source.getLength(), this.source.getFRC());
      this.cm = CoreMetrics.get(localLineMetrics);
    }
  }
  
  public Rectangle2D getLogicalBounds()
  {
    return getLogicalBounds(0.0F, 0.0F);
  }
  
  public Rectangle2D getLogicalBounds(float paramFloat1, float paramFloat2)
  {
    if (this.lb == null) {
      this.lb = createLogicalBounds();
    }
    return new Rectangle2D.Float((float)(this.lb.getX() + paramFloat1), (float)(this.lb.getY() + paramFloat2), (float)this.lb.getWidth(), (float)this.lb.getHeight());
  }
  
  public float getAdvance()
  {
    if (this.lb == null) {
      this.lb = createLogicalBounds();
    }
    return (float)this.lb.getWidth();
  }
  
  public Rectangle2D getVisualBounds(float paramFloat1, float paramFloat2)
  {
    if (this.vb == null) {
      this.vb = this.decorator.getVisualBounds(this);
    }
    return new Rectangle2D.Float((float)(this.vb.getX() + paramFloat1), (float)(this.vb.getY() + paramFloat2), (float)this.vb.getWidth(), (float)this.vb.getHeight());
  }
  
  public Rectangle2D getAlignBounds(float paramFloat1, float paramFloat2)
  {
    if (this.ab == null) {
      this.ab = createAlignBounds();
    }
    return new Rectangle2D.Float((float)(this.ab.getX() + paramFloat1), (float)(this.ab.getY() + paramFloat2), (float)this.ab.getWidth(), (float)this.ab.getHeight());
  }
  
  public Rectangle2D getItalicBounds(float paramFloat1, float paramFloat2)
  {
    if (this.ib == null) {
      this.ib = createItalicBounds();
    }
    return new Rectangle2D.Float((float)(this.ib.getX() + paramFloat1), (float)(this.ib.getY() + paramFloat2), (float)this.ib.getWidth(), (float)this.ib.getHeight());
  }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2)
  {
    return getGV().getPixelBounds(paramFontRenderContext, paramFloat1, paramFloat2);
  }
  
  public boolean isSimple()
  {
    return (this.decorator == Decoration.getPlainDecoration()) && (this.baseTX == null);
  }
  
  public AffineTransform getBaselineTransform()
  {
    return this.baseTX;
  }
  
  public Shape handleGetOutline(float paramFloat1, float paramFloat2)
  {
    return getGV().getOutline(paramFloat1, paramFloat2);
  }
  
  public Shape getOutline(float paramFloat1, float paramFloat2)
  {
    return this.decorator.getOutline(this, paramFloat1, paramFloat2);
  }
  
  public void handleDraw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2)
  {
    paramGraphics2D.drawGlyphVector(getGV(), paramFloat1, paramFloat2);
  }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2)
  {
    this.decorator.drawTextAndDecorations(this, paramGraphics2D, paramFloat1, paramFloat2);
  }
  
  protected Rectangle2D createLogicalBounds()
  {
    return getGV().getLogicalBounds();
  }
  
  public Rectangle2D handleGetVisualBounds()
  {
    return getGV().getVisualBounds();
  }
  
  protected Rectangle2D createAlignBounds()
  {
    float[] arrayOfFloat = getCharinfo();
    float f1 = 0.0F;
    float f2 = -this.cm.ascent;
    float f3 = 0.0F;
    float f4 = this.cm.ascent + this.cm.descent;
    if ((this.charinfo == null) || (this.charinfo.length == 0)) {
      return new Rectangle2D.Float(f1, f2, f3, f4);
    }
    int i = (this.source.getLayoutFlags() & 0x8) == 0 ? 1 : 0;
    int j = arrayOfFloat.length - 8;
    if (i != 0) {
      while ((j > 0) && (arrayOfFloat[(j + 6)] == 0.0F)) {
        j -= 8;
      }
    }
    if (j >= 0)
    {
      for (int k = 0; (k < j) && ((arrayOfFloat[(k + 2)] == 0.0F) || ((i == 0) && (arrayOfFloat[(k + 6)] == 0.0F))); k += 8) {}
      f1 = Math.max(0.0F, arrayOfFloat[(k + 0)]);
      f3 = arrayOfFloat[(j + 0)] + arrayOfFloat[(j + 2)] - f1;
    }
    return new Rectangle2D.Float(f1, f2, f3, f4);
  }
  
  public Rectangle2D createItalicBounds()
  {
    float f1 = this.cm.italicAngle;
    Rectangle2D localRectangle2D = getLogicalBounds();
    float f2 = (float)localRectangle2D.getMinX();
    float f3 = -this.cm.ascent;
    float f4 = (float)localRectangle2D.getMaxX();
    float f5 = this.cm.descent;
    if (f1 != 0.0F) {
      if (f1 > 0.0F)
      {
        f2 -= f1 * (f5 - this.cm.ssOffset);
        f4 -= f1 * (f3 - this.cm.ssOffset);
      }
      else
      {
        f2 -= f1 * (f3 - this.cm.ssOffset);
        f4 -= f1 * (f5 - this.cm.ssOffset);
      }
    }
    return new Rectangle2D.Float(f2, f3, f4 - f2, f5 - f3);
  }
  
  private final StandardGlyphVector getGV()
  {
    if (this.gv == null) {
      this.gv = createGV();
    }
    return this.gv;
  }
  
  protected StandardGlyphVector createGV()
  {
    FontRenderContext localFontRenderContext = this.source.getFRC();
    int i = this.source.getLayoutFlags();
    char[] arrayOfChar = this.source.getChars();
    int j = this.source.getStart();
    int k = this.source.getLength();
    GlyphLayout localGlyphLayout = GlyphLayout.get(null);
    this.gv = localGlyphLayout.layout(this.font, localFontRenderContext, arrayOfChar, j, k, i, null);
    GlyphLayout.done(localGlyphLayout);
    return this.gv;
  }
  
  public int getNumCharacters()
  {
    return this.source.getLength();
  }
  
  public CoreMetrics getCoreMetrics()
  {
    return this.cm;
  }
  
  public float getCharX(int paramInt)
  {
    validate(paramInt);
    float[] arrayOfFloat = getCharinfo();
    int i = l2v(paramInt) * 8 + 0;
    if ((arrayOfFloat == null) || (i >= arrayOfFloat.length)) {
      return 0.0F;
    }
    return arrayOfFloat[i];
  }
  
  public float getCharY(int paramInt)
  {
    validate(paramInt);
    float[] arrayOfFloat = getCharinfo();
    int i = l2v(paramInt) * 8 + 1;
    if ((arrayOfFloat == null) || (i >= arrayOfFloat.length)) {
      return 0.0F;
    }
    return arrayOfFloat[i];
  }
  
  public float getCharAdvance(int paramInt)
  {
    validate(paramInt);
    float[] arrayOfFloat = getCharinfo();
    int i = l2v(paramInt) * 8 + 2;
    if ((arrayOfFloat == null) || (i >= arrayOfFloat.length)) {
      return 0.0F;
    }
    return arrayOfFloat[i];
  }
  
  public Rectangle2D handleGetCharVisualBounds(int paramInt)
  {
    validate(paramInt);
    float[] arrayOfFloat = getCharinfo();
    paramInt = l2v(paramInt) * 8;
    if ((arrayOfFloat == null) || (paramInt + 7 >= arrayOfFloat.length)) {
      return new Rectangle2D.Float();
    }
    return new Rectangle2D.Float(arrayOfFloat[(paramInt + 4)], arrayOfFloat[(paramInt + 5)], arrayOfFloat[(paramInt + 6)], arrayOfFloat[(paramInt + 7)]);
  }
  
  public Rectangle2D getCharVisualBounds(int paramInt, float paramFloat1, float paramFloat2)
  {
    Rectangle2D localRectangle2D = this.decorator.getCharVisualBounds(this, paramInt);
    if ((paramFloat1 != 0.0F) || (paramFloat2 != 0.0F)) {
      localRectangle2D.setRect(localRectangle2D.getX() + paramFloat1, localRectangle2D.getY() + paramFloat2, localRectangle2D.getWidth(), localRectangle2D.getHeight());
    }
    return localRectangle2D;
  }
  
  private void validate(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("index " + paramInt + " < 0");
    }
    if (paramInt >= this.source.getLength()) {
      throw new IllegalArgumentException("index " + paramInt + " < " + this.source.getLength());
    }
  }
  
  public int logicalToVisual(int paramInt)
  {
    validate(paramInt);
    return l2v(paramInt);
  }
  
  public int visualToLogical(int paramInt)
  {
    validate(paramInt);
    return v2l(paramInt);
  }
  
  public int getLineBreakIndex(int paramInt, float paramFloat)
  {
    float[] arrayOfFloat = getCharinfo();
    int i = this.source.getLength();
    paramInt--;
    while (paramFloat >= 0.0F)
    {
      paramInt++;
      if (paramInt >= i) {
        break;
      }
      int j = l2v(paramInt) * 8 + 2;
      if (j >= arrayOfFloat.length) {
        break;
      }
      float f = arrayOfFloat[j];
      paramFloat -= f;
    }
    return paramInt;
  }
  
  public float getAdvanceBetween(int paramInt1, int paramInt2)
  {
    float f = 0.0F;
    float[] arrayOfFloat = getCharinfo();
    paramInt1--;
    for (;;)
    {
      paramInt1++;
      if (paramInt1 >= paramInt2) {
        break;
      }
      int i = l2v(paramInt1) * 8 + 2;
      if (i >= arrayOfFloat.length) {
        break;
      }
      f += arrayOfFloat[i];
    }
    return f;
  }
  
  public boolean caretAtOffsetIsValid(int paramInt)
  {
    if ((paramInt == 0) || (paramInt == this.source.getLength())) {
      return true;
    }
    int i = this.source.getChars()[(this.source.getStart() + paramInt)];
    if ((i == 9) || (i == 10) || (i == 13)) {
      return true;
    }
    int j = l2v(paramInt);
    int k = j * 8 + 2;
    float[] arrayOfFloat = getCharinfo();
    if ((arrayOfFloat == null) || (k >= arrayOfFloat.length)) {
      return false;
    }
    return arrayOfFloat[k] != 0.0F;
  }
  
  private final float[] getCharinfo()
  {
    if (this.charinfo == null) {
      this.charinfo = createCharinfo();
    }
    return this.charinfo;
  }
  
  protected float[] createCharinfo()
  {
    StandardGlyphVector localStandardGlyphVector = getGV();
    float[] arrayOfFloat = null;
    try
    {
      arrayOfFloat = localStandardGlyphVector.getGlyphInfo();
    }
    catch (Exception localException)
    {
      System.out.println(this.source);
    }
    int i = localStandardGlyphVector.getNumGlyphs();
    if (i == 0) {
      return arrayOfFloat;
    }
    int[] arrayOfInt = localStandardGlyphVector.getGlyphCharIndices(0, i, null);
    int j = 0;
    if (j != 0)
    {
      System.err.println("number of glyphs: " + i);
      for (f1 = 0; f1 < i; f1++) {
        System.err.println("g: " + f1 + ", x: " + arrayOfFloat[(f1 * 8 + 0)] + ", a: " + arrayOfFloat[(f1 * 8 + 2)] + ", n: " + arrayOfInt[f1]);
      }
    }
    float f1 = arrayOfInt[0];
    int k = f1;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = i;
    int i5 = 8;
    int i6 = 1;
    int i7 = (this.source.getLayoutFlags() & 0x1) == 0 ? 1 : 0;
    if (i7 == 0)
    {
      f1 = arrayOfInt[(i - 1)];
      k = f1;
      m = 0;
      n = arrayOfFloat.length - 8;
      i1 = 0;
      i2 = arrayOfFloat.length - 8;
      i3 = i - 1;
      i4 = -1;
      i5 = -8;
      i6 = -1;
    }
    float f2 = 0.0F;
    float f3 = 0.0F;
    float f4 = 0.0F;
    float f5 = 0.0F;
    float f6 = 0.0F;
    float f7 = 0.0F;
    float f8 = 0.0F;
    int i8 = 0;
    int i10;
    int i13;
    int i12;
    while (i3 != i4)
    {
      int i9 = 0;
      i10 = 0;
      f1 = arrayOfInt[i3];
      k = f1;
      i3 += i6;
      i2 += i5;
      float f9;
      while ((i3 != i4) && ((arrayOfFloat[(i2 + 2)] == 0.0F) || (f1 != m) || (arrayOfInt[i3] <= k) || (k - f1 > i10)))
      {
        if (i9 == 0)
        {
          int i11 = i2 - i5;
          f2 = arrayOfFloat[(i11 + 0)];
          f3 = f2 + arrayOfFloat[(i11 + 2)];
          f4 = arrayOfFloat[(i11 + 4)];
          f5 = arrayOfFloat[(i11 + 5)];
          f6 = f4 + arrayOfFloat[(i11 + 6)];
          f7 = f5 + arrayOfFloat[(i11 + 7)];
          i9 = 1;
        }
        i10++;
        f9 = arrayOfFloat[(i2 + 2)];
        if (f9 != 0.0F)
        {
          f10 = arrayOfFloat[(i2 + 0)];
          f2 = Math.min(f2, f10);
          f3 = Math.max(f3, f10 + f9);
        }
        float f10 = arrayOfFloat[(i2 + 6)];
        if (f10 != 0.0F)
        {
          float f11 = arrayOfFloat[(i2 + 4)];
          float f12 = arrayOfFloat[(i2 + 5)];
          f4 = Math.min(f4, f11);
          f5 = Math.min(f5, f12);
          f6 = Math.max(f6, f11 + f10);
          f7 = Math.max(f7, f12 + arrayOfFloat[(i2 + 7)]);
        }
        f1 = Math.min(f1, arrayOfInt[i3]);
        k = Math.max(k, arrayOfInt[i3]);
        i3 += i6;
        i2 += i5;
      }
      if (j != 0) {
        System.out.println("minIndex = " + f1 + ", maxIndex = " + k);
      }
      m = k + 1;
      arrayOfFloat[(n + 1)] = f8;
      arrayOfFloat[(n + 3)] = 0.0F;
      if (i9 != 0)
      {
        arrayOfFloat[(n + 0)] = f2;
        arrayOfFloat[(n + 2)] = (f3 - f2);
        arrayOfFloat[(n + 4)] = f4;
        arrayOfFloat[(n + 5)] = f5;
        arrayOfFloat[(n + 6)] = (f6 - f4);
        arrayOfFloat[(n + 7)] = (f7 - f5);
        if (k - f1 < i10) {
          i8 = 1;
        }
        if (f1 < k)
        {
          if (i7 == 0) {
            f3 = f2;
          }
          f6 -= f4;
          f7 -= f5;
          f9 = f1;
          i13 = n / 8;
          while (f1 < k)
          {
            f1++;
            i1 += i6;
            n += i5;
            if (((n < 0) || (n >= arrayOfFloat.length)) && (j != 0)) {
              System.out.println("minIndex = " + f9 + ", maxIndex = " + k + ", cp = " + i13);
            }
            arrayOfFloat[(n + 0)] = f3;
            arrayOfFloat[(n + 1)] = f8;
            arrayOfFloat[(n + 2)] = 0.0F;
            arrayOfFloat[(n + 3)] = 0.0F;
            arrayOfFloat[(n + 4)] = f4;
            arrayOfFloat[(n + 5)] = f5;
            arrayOfFloat[(n + 6)] = f6;
            arrayOfFloat[(n + 7)] = f7;
          }
        }
        i9 = 0;
      }
      else if (i8 != 0)
      {
        i12 = i2 - i5;
        arrayOfFloat[(n + 0)] = arrayOfFloat[(i12 + 0)];
        arrayOfFloat[(n + 2)] = arrayOfFloat[(i12 + 2)];
        arrayOfFloat[(n + 4)] = arrayOfFloat[(i12 + 4)];
        arrayOfFloat[(n + 5)] = arrayOfFloat[(i12 + 5)];
        arrayOfFloat[(n + 6)] = arrayOfFloat[(i12 + 6)];
        arrayOfFloat[(n + 7)] = arrayOfFloat[(i12 + 7)];
      }
      n += i5;
      i1 += i6;
    }
    if ((i8 != 0) && (i7 == 0))
    {
      n -= i5;
      System.arraycopy(arrayOfFloat, n, arrayOfFloat, 0, arrayOfFloat.length - n);
    }
    if (j != 0)
    {
      char[] arrayOfChar = this.source.getChars();
      i10 = this.source.getStart();
      i12 = this.source.getLength();
      System.out.println("char info for " + i12 + " characters");
      i13 = 0;
      while (i13 < i12 * 8) {
        System.out.println(" ch: " + Integer.toHexString(arrayOfChar[(i10 + v2l(i13 / 8))]) + " x: " + arrayOfFloat[(i13++)] + " y: " + arrayOfFloat[(i13++)] + " xa: " + arrayOfFloat[(i13++)] + " ya: " + arrayOfFloat[(i13++)] + " l: " + arrayOfFloat[(i13++)] + " t: " + arrayOfFloat[(i13++)] + " w: " + arrayOfFloat[(i13++)] + " h: " + arrayOfFloat[(i13++)]);
      }
    }
    return arrayOfFloat;
  }
  
  protected int l2v(int paramInt)
  {
    return (this.source.getLayoutFlags() & 0x1) == 0 ? paramInt : this.source.getLength() - 1 - paramInt;
  }
  
  protected int v2l(int paramInt)
  {
    return (this.source.getLayoutFlags() & 0x1) == 0 ? paramInt : this.source.getLength() - 1 - paramInt;
  }
  
  public TextLineComponent getSubset(int paramInt1, int paramInt2, int paramInt3)
  {
    return new ExtendedTextSourceLabel(this.source.getSubSource(paramInt1, paramInt2 - paramInt1, paramInt3), this.decorator);
  }
  
  public String toString()
  {
    return this.source.toString(false);
  }
  
  public int getNumJustificationInfos()
  {
    return getGV().getNumGlyphs();
  }
  
  public void getJustificationInfos(GlyphJustificationInfo[] paramArrayOfGlyphJustificationInfo, int paramInt1, int paramInt2, int paramInt3)
  {
    StandardGlyphVector localStandardGlyphVector = getGV();
    float[] arrayOfFloat = getCharinfo();
    float f = localStandardGlyphVector.getFont().getSize2D();
    GlyphJustificationInfo localGlyphJustificationInfo1 = new GlyphJustificationInfo(0.0F, false, 3, 0.0F, 0.0F, false, 3, 0.0F, 0.0F);
    GlyphJustificationInfo localGlyphJustificationInfo2 = new GlyphJustificationInfo(f, true, 1, 0.0F, f, true, 1, 0.0F, f / 4.0F);
    GlyphJustificationInfo localGlyphJustificationInfo3 = new GlyphJustificationInfo(f, true, 2, f, f, false, 3, 0.0F, 0.0F);
    char[] arrayOfChar = this.source.getChars();
    int i = this.source.getStart();
    int j = localStandardGlyphVector.getNumGlyphs();
    int k = 0;
    int m = j;
    int n = (this.source.getLayoutFlags() & 0x1) == 0 ? 1 : 0;
    if ((paramInt2 != 0) || (paramInt3 != this.source.getLength())) {
      if (n != 0)
      {
        k = paramInt2;
        m = paramInt3;
      }
      else
      {
        k = j - paramInt3;
        m = j - paramInt2;
      }
    }
    for (int i1 = 0; i1 < j; i1++)
    {
      GlyphJustificationInfo localGlyphJustificationInfo4 = null;
      if ((i1 >= k) && (i1 < m)) {
        if (arrayOfFloat[(i1 * 8 + 2)] == 0.0F)
        {
          localGlyphJustificationInfo4 = localGlyphJustificationInfo1;
        }
        else
        {
          int i2 = v2l(i1);
          int i3 = arrayOfChar[(i + i2)];
          if (Character.isWhitespace(i3)) {
            localGlyphJustificationInfo4 = localGlyphJustificationInfo2;
          } else if (((i3 >= 19968) && (i3 < 40960)) || ((i3 >= 44032) && (i3 < 55216)) || ((i3 >= 63744) && (i3 < 64256))) {
            localGlyphJustificationInfo4 = localGlyphJustificationInfo3;
          } else {
            localGlyphJustificationInfo4 = localGlyphJustificationInfo1;
          }
        }
      }
      paramArrayOfGlyphJustificationInfo[(paramInt1 + i1)] = localGlyphJustificationInfo4;
    }
  }
  
  public TextLineComponent applyJustificationDeltas(float[] paramArrayOfFloat, int paramInt, boolean[] paramArrayOfBoolean)
  {
    float[] arrayOfFloat1 = (float[])getCharinfo().clone();
    paramArrayOfBoolean[0] = false;
    StandardGlyphVector localStandardGlyphVector = (StandardGlyphVector)getGV().clone();
    float[] arrayOfFloat2 = localStandardGlyphVector.getGlyphPositions(null);
    int i = localStandardGlyphVector.getNumGlyphs();
    char[] arrayOfChar = this.source.getChars();
    int j = this.source.getStart();
    float f1 = 0.0F;
    for (int k = 0; k < i; k++) {
      if (Character.isWhitespace(arrayOfChar[(j + v2l(k))]))
      {
        arrayOfFloat2[(k * 2)] += f1;
        float f2 = paramArrayOfFloat[(paramInt + k * 2)] + paramArrayOfFloat[(paramInt + k * 2 + 1)];
        arrayOfFloat1[(k * 8 + 0)] += f1;
        arrayOfFloat1[(k * 8 + 4)] += f1;
        arrayOfFloat1[(k * 8 + 2)] += f2;
        f1 += f2;
      }
      else
      {
        f1 += paramArrayOfFloat[(paramInt + k * 2)];
        arrayOfFloat2[(k * 2)] += f1;
        arrayOfFloat1[(k * 8 + 0)] += f1;
        arrayOfFloat1[(k * 8 + 4)] += f1;
        f1 += paramArrayOfFloat[(paramInt + k * 2 + 1)];
      }
    }
    arrayOfFloat2[(i * 2)] += f1;
    localStandardGlyphVector.setGlyphPositions(arrayOfFloat2);
    ExtendedTextSourceLabel localExtendedTextSourceLabel = new ExtendedTextSourceLabel(this.source, this.decorator);
    localExtendedTextSourceLabel.gv = localStandardGlyphVector;
    localExtendedTextSourceLabel.charinfo = arrayOfFloat1;
    return localExtendedTextSourceLabel;
  }
}
