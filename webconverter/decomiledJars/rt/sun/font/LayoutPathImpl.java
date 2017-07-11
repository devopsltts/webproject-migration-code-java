package sun.font;

import java.awt.Shape;
import java.awt.font.LayoutPath;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;

public abstract class LayoutPathImpl
  extends LayoutPath
{
  private static final boolean LOGMAP = false;
  private static final Formatter LOG = new Formatter(System.out);
  
  public LayoutPathImpl() {}
  
  public Point2D pointToPath(double paramDouble1, double paramDouble2)
  {
    Point2D.Double localDouble = new Point2D.Double(paramDouble1, paramDouble2);
    pointToPath(localDouble, localDouble);
    return localDouble;
  }
  
  public Point2D pathToPoint(double paramDouble1, double paramDouble2, boolean paramBoolean)
  {
    Point2D.Double localDouble = new Point2D.Double(paramDouble1, paramDouble2);
    pathToPoint(localDouble, paramBoolean, localDouble);
    return localDouble;
  }
  
  public void pointToPath(double paramDouble1, double paramDouble2, Point2D paramPoint2D)
  {
    paramPoint2D.setLocation(paramDouble1, paramDouble2);
    pointToPath(paramPoint2D, paramPoint2D);
  }
  
  public void pathToPoint(double paramDouble1, double paramDouble2, boolean paramBoolean, Point2D paramPoint2D)
  {
    paramPoint2D.setLocation(paramDouble1, paramDouble2);
    pathToPoint(paramPoint2D, paramBoolean, paramPoint2D);
  }
  
  public abstract double start();
  
  public abstract double end();
  
  public abstract double length();
  
  public abstract Shape mapShape(Shape paramShape);
  
  public static LayoutPathImpl getPath(EndType paramEndType, double... paramVarArgs)
  {
    if ((paramVarArgs.length & 0x1) != 0) {
      throw new IllegalArgumentException("odd number of points not allowed");
    }
    return SegmentPath.get(paramEndType, paramVarArgs);
  }
  
  public static class EmptyPath
    extends LayoutPathImpl
  {
    private AffineTransform tx;
    
    public EmptyPath(AffineTransform paramAffineTransform)
    {
      this.tx = paramAffineTransform;
    }
    
    public void pathToPoint(Point2D paramPoint2D1, boolean paramBoolean, Point2D paramPoint2D2)
    {
      if (this.tx != null) {
        this.tx.transform(paramPoint2D1, paramPoint2D2);
      } else {
        paramPoint2D2.setLocation(paramPoint2D1);
      }
    }
    
    public boolean pointToPath(Point2D paramPoint2D1, Point2D paramPoint2D2)
    {
      paramPoint2D2.setLocation(paramPoint2D1);
      if (this.tx != null) {
        try
        {
          this.tx.inverseTransform(paramPoint2D1, paramPoint2D2);
        }
        catch (NoninvertibleTransformException localNoninvertibleTransformException) {}
      }
      return paramPoint2D2.getX() > 0.0D;
    }
    
    public double start()
    {
      return 0.0D;
    }
    
    public double end()
    {
      return 0.0D;
    }
    
    public double length()
    {
      return 0.0D;
    }
    
    public Shape mapShape(Shape paramShape)
    {
      if (this.tx != null) {
        return this.tx.createTransformedShape(paramShape);
      }
      return paramShape;
    }
  }
  
  public static enum EndType
  {
    PINNED,  EXTENDED,  CLOSED;
    
    private EndType() {}
    
    public boolean isPinned()
    {
      return this == PINNED;
    }
    
    public boolean isExtended()
    {
      return this == EXTENDED;
    }
    
    public boolean isClosed()
    {
      return this == CLOSED;
    }
  }
  
  public static final class SegmentPath
    extends LayoutPathImpl
  {
    private double[] data;
    LayoutPathImpl.EndType etype;
    
    public static SegmentPath get(LayoutPathImpl.EndType paramEndType, double... paramVarArgs)
    {
      return new LayoutPathImpl.SegmentPathBuilder().build(paramEndType, paramVarArgs);
    }
    
    SegmentPath(double[] paramArrayOfDouble, LayoutPathImpl.EndType paramEndType)
    {
      this.data = paramArrayOfDouble;
      this.etype = paramEndType;
    }
    
    public void pathToPoint(Point2D paramPoint2D1, boolean paramBoolean, Point2D paramPoint2D2)
    {
      locateAndGetIndex(paramPoint2D1, paramBoolean, paramPoint2D2);
    }
    
    public boolean pointToPath(Point2D paramPoint2D1, Point2D paramPoint2D2)
    {
      double d1 = paramPoint2D1.getX();
      double d2 = paramPoint2D1.getY();
      double d3 = this.data[0];
      double d4 = this.data[1];
      double d5 = this.data[2];
      double d6 = Double.MAX_VALUE;
      double d7 = 0.0D;
      double d8 = 0.0D;
      double d9 = 0.0D;
      int i = 0;
      for (int j = 3; j < this.data.length; j += 3)
      {
        double d11 = this.data[j];
        double d13 = this.data[(j + 1)];
        double d15 = this.data[(j + 2)];
        double d16 = d11 - d3;
        double d17 = d13 - d4;
        double d18 = d15 - d5;
        double d19 = d1 - d3;
        double d20 = d2 - d4;
        double d21 = d16 * d19 + d17 * d20;
        double d22;
        double d23;
        double d24;
        int n;
        if ((d18 == 0.0D) || ((d21 < 0.0D) && ((!this.etype.isExtended()) || (j != 3))))
        {
          d22 = d3;
          d23 = d4;
          d24 = d5;
          n = j;
        }
        else
        {
          d25 = d18 * d18;
          if ((d21 <= d25) || ((this.etype.isExtended()) && (j == this.data.length - 3)))
          {
            d26 = d21 / d25;
            d22 = d3 + d26 * d16;
            d23 = d4 + d26 * d17;
            d24 = d5 + d26 * d18;
            n = j;
          }
          else
          {
            if (j != this.data.length - 3) {
              break label358;
            }
            d22 = d11;
            d23 = d13;
            d24 = d15;
            n = this.data.length;
          }
        }
        double d25 = d1 - d22;
        double d26 = d2 - d23;
        double d27 = d25 * d25 + d26 * d26;
        if (d27 <= d6)
        {
          d6 = d27;
          d7 = d22;
          d8 = d23;
          d9 = d24;
          i = n;
        }
        label358:
        d3 = d11;
        d4 = d13;
        d5 = d15;
      }
      d3 = this.data[(i - 3)];
      d4 = this.data[(i - 2)];
      if ((d7 != d3) || (d8 != d4))
      {
        double d10 = this.data[i];
        double d12 = this.data[(i + 1)];
        double d14 = Math.sqrt(d6);
        if ((d1 - d7) * (d12 - d4) > (d2 - d8) * (d10 - d3)) {
          d14 = -d14;
        }
        paramPoint2D2.setLocation(d9, d14);
        return false;
      }
      int k = (i != 3) && (this.data[(i - 1)] != this.data[(i - 4)]) ? 1 : 0;
      int m = (i != this.data.length) && (this.data[(i - 1)] != this.data[(i + 2)]) ? 1 : 0;
      boolean bool = (this.etype.isExtended()) && ((i == 3) || (i == this.data.length));
      if ((k != 0) && (m != 0))
      {
        Point2D.Double localDouble1 = new Point2D.Double(d1, d2);
        calcoffset(i - 3, bool, localDouble1);
        Point2D.Double localDouble2 = new Point2D.Double(d1, d2);
        calcoffset(i, bool, localDouble2);
        if (Math.abs(localDouble1.y) > Math.abs(localDouble2.y))
        {
          paramPoint2D2.setLocation(localDouble1);
          return true;
        }
        paramPoint2D2.setLocation(localDouble2);
        return false;
      }
      if (k != 0)
      {
        paramPoint2D2.setLocation(d1, d2);
        calcoffset(i - 3, bool, paramPoint2D2);
        return true;
      }
      paramPoint2D2.setLocation(d1, d2);
      calcoffset(i, bool, paramPoint2D2);
      return false;
    }
    
    private void calcoffset(int paramInt, boolean paramBoolean, Point2D paramPoint2D)
    {
      double d1 = this.data[(paramInt - 3)];
      double d2 = this.data[(paramInt - 2)];
      double d3 = paramPoint2D.getX() - d1;
      double d4 = paramPoint2D.getY() - d2;
      double d5 = this.data[paramInt] - d1;
      double d6 = this.data[(paramInt + 1)] - d2;
      double d7 = this.data[(paramInt + 2)] - this.data[(paramInt - 1)];
      double d8 = (d3 * d5 + d4 * d6) / d7;
      double d9 = (d3 * -d6 + d4 * d5) / d7;
      if (!paramBoolean) {
        if (d8 < 0.0D) {
          d8 = 0.0D;
        } else if (d8 > d7) {
          d8 = d7;
        }
      }
      d8 += this.data[(paramInt - 1)];
      paramPoint2D.setLocation(d8, d9);
    }
    
    public Shape mapShape(Shape paramShape)
    {
      return new Mapper().mapShape(paramShape);
    }
    
    public double start()
    {
      return this.data[2];
    }
    
    public double end()
    {
      return this.data[(this.data.length - 1)];
    }
    
    public double length()
    {
      return this.data[(this.data.length - 1)] - this.data[2];
    }
    
    private double getClosedAdvance(double paramDouble, boolean paramBoolean)
    {
      if (this.etype.isClosed())
      {
        paramDouble -= this.data[2];
        int i = (int)(paramDouble / length());
        paramDouble -= i * length();
        if ((paramDouble < 0.0D) || ((paramDouble == 0.0D) && (paramBoolean))) {
          paramDouble += length();
        }
        paramDouble += this.data[2];
      }
      return paramDouble;
    }
    
    private int getSegmentIndexForAdvance(double paramDouble, boolean paramBoolean)
    {
      paramDouble = getClosedAdvance(paramDouble, paramBoolean);
      int i = 5;
      int j = this.data.length - 1;
      while (i < j)
      {
        double d = this.data[i];
        if ((paramDouble < d) || ((paramDouble == d) && (paramBoolean))) {
          break;
        }
        i += 3;
      }
      return i - 2;
    }
    
    private void map(int paramInt, double paramDouble1, double paramDouble2, Point2D paramPoint2D)
    {
      double d1 = this.data[paramInt] - this.data[(paramInt - 3)];
      double d2 = this.data[(paramInt + 1)] - this.data[(paramInt - 2)];
      double d3 = this.data[(paramInt + 2)] - this.data[(paramInt - 1)];
      double d4 = d1 / d3;
      double d5 = d2 / d3;
      paramDouble1 -= this.data[(paramInt - 1)];
      paramPoint2D.setLocation(this.data[(paramInt - 3)] + paramDouble1 * d4 - paramDouble2 * d5, this.data[(paramInt - 2)] + paramDouble1 * d5 + paramDouble2 * d4);
    }
    
    private int locateAndGetIndex(Point2D paramPoint2D1, boolean paramBoolean, Point2D paramPoint2D2)
    {
      double d1 = paramPoint2D1.getX();
      double d2 = paramPoint2D1.getY();
      int i = getSegmentIndexForAdvance(d1, paramBoolean);
      map(i, d1, d2, paramPoint2D2);
      return i;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("{");
      localStringBuilder.append(this.etype.toString());
      localStringBuilder.append(" ");
      for (int i = 0; i < this.data.length; i += 3)
      {
        if (i > 0) {
          localStringBuilder.append(",");
        }
        float f1 = (int)(this.data[i] * 100.0D) / 100.0F;
        float f2 = (int)(this.data[(i + 1)] * 100.0D) / 100.0F;
        float f3 = (int)(this.data[(i + 2)] * 10.0D) / 10.0F;
        localStringBuilder.append("{");
        localStringBuilder.append(f1);
        localStringBuilder.append(",");
        localStringBuilder.append(f2);
        localStringBuilder.append(",");
        localStringBuilder.append(f3);
        localStringBuilder.append("}");
      }
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    class LineInfo
    {
      double sx;
      double sy;
      double lx;
      double ly;
      double m;
      
      LineInfo() {}
      
      void set(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
      {
        this.sx = paramDouble1;
        this.sy = paramDouble2;
        this.lx = paramDouble3;
        this.ly = paramDouble4;
        double d1 = paramDouble3 - paramDouble1;
        if (d1 == 0.0D)
        {
          this.m = 0.0D;
        }
        else
        {
          double d2 = paramDouble4 - paramDouble2;
          this.m = (d2 / d1);
        }
      }
      
      void set(LineInfo paramLineInfo)
      {
        this.sx = paramLineInfo.sx;
        this.sy = paramLineInfo.sy;
        this.lx = paramLineInfo.lx;
        this.ly = paramLineInfo.ly;
        this.m = paramLineInfo.m;
      }
      
      boolean pin(double paramDouble1, double paramDouble2, LineInfo paramLineInfo)
      {
        paramLineInfo.set(this);
        if (this.lx >= this.sx)
        {
          if ((this.sx < paramDouble2) && (this.lx >= paramDouble1))
          {
            if (this.sx < paramDouble1)
            {
              if (this.m != 0.0D) {
                this.sy += this.m * (paramDouble1 - this.sx);
              }
              paramLineInfo.sx = paramDouble1;
            }
            if (this.lx > paramDouble2)
            {
              if (this.m != 0.0D) {
                this.ly += this.m * (paramDouble2 - this.lx);
              }
              paramLineInfo.lx = paramDouble2;
            }
            return true;
          }
        }
        else if ((this.lx < paramDouble2) && (this.sx >= paramDouble1))
        {
          if (this.lx < paramDouble1)
          {
            if (this.m != 0.0D) {
              this.ly += this.m * (paramDouble1 - this.lx);
            }
            paramLineInfo.lx = paramDouble1;
          }
          if (this.sx > paramDouble2)
          {
            if (this.m != 0.0D) {
              this.sy += this.m * (paramDouble2 - this.sx);
            }
            paramLineInfo.sx = paramDouble2;
          }
          return true;
        }
        return false;
      }
      
      boolean pin(int paramInt, LineInfo paramLineInfo)
      {
        double d1 = LayoutPathImpl.SegmentPath.this.data[(paramInt - 1)];
        double d2 = LayoutPathImpl.SegmentPath.this.data[(paramInt + 2)];
        switch (LayoutPathImpl.1.$SwitchMap$sun$font$LayoutPathImpl$EndType[LayoutPathImpl.SegmentPath.this.etype.ordinal()])
        {
        case 1: 
          break;
        case 2: 
          if (paramInt == 3) {
            d1 = Double.NEGATIVE_INFINITY;
          }
          if (paramInt == LayoutPathImpl.SegmentPath.this.data.length - 3) {
            d2 = Double.POSITIVE_INFINITY;
          }
          break;
        }
        return pin(d1, d2, paramLineInfo);
      }
    }
    
    class Mapper
    {
      final LayoutPathImpl.SegmentPath.LineInfo li = new LayoutPathImpl.SegmentPath.LineInfo(LayoutPathImpl.SegmentPath.this);
      final ArrayList<LayoutPathImpl.SegmentPath.Segment> segments = new ArrayList();
      final Point2D.Double mpt;
      final Point2D.Double cpt;
      boolean haveMT;
      
      Mapper()
      {
        for (int i = 3; i < LayoutPathImpl.SegmentPath.this.data.length; i += 3) {
          if (LayoutPathImpl.SegmentPath.this.data[(i + 2)] != LayoutPathImpl.SegmentPath.this.data[(i - 1)]) {
            this.segments.add(new LayoutPathImpl.SegmentPath.Segment(LayoutPathImpl.SegmentPath.this, i));
          }
        }
        this.mpt = new Point2D.Double();
        this.cpt = new Point2D.Double();
      }
      
      void init()
      {
        this.haveMT = false;
        Iterator localIterator = this.segments.iterator();
        while (localIterator.hasNext())
        {
          LayoutPathImpl.SegmentPath.Segment localSegment = (LayoutPathImpl.SegmentPath.Segment)localIterator.next();
          localSegment.init();
        }
      }
      
      void moveTo(double paramDouble1, double paramDouble2)
      {
        this.mpt.x = paramDouble1;
        this.mpt.y = paramDouble2;
        this.haveMT = true;
      }
      
      void lineTo(double paramDouble1, double paramDouble2)
      {
        if (this.haveMT)
        {
          this.cpt.x = this.mpt.x;
          this.cpt.y = this.mpt.y;
        }
        if ((paramDouble1 == this.cpt.x) && (paramDouble2 == this.cpt.y)) {
          return;
        }
        LayoutPathImpl.SegmentPath.Segment localSegment;
        if (this.haveMT)
        {
          this.haveMT = false;
          localIterator = this.segments.iterator();
          while (localIterator.hasNext())
          {
            localSegment = (LayoutPathImpl.SegmentPath.Segment)localIterator.next();
            localSegment.move();
          }
        }
        this.li.set(this.cpt.x, this.cpt.y, paramDouble1, paramDouble2);
        Iterator localIterator = this.segments.iterator();
        while (localIterator.hasNext())
        {
          localSegment = (LayoutPathImpl.SegmentPath.Segment)localIterator.next();
          localSegment.line(this.li);
        }
        this.cpt.x = paramDouble1;
        this.cpt.y = paramDouble2;
      }
      
      void close()
      {
        lineTo(this.mpt.x, this.mpt.y);
        Iterator localIterator = this.segments.iterator();
        while (localIterator.hasNext())
        {
          LayoutPathImpl.SegmentPath.Segment localSegment = (LayoutPathImpl.SegmentPath.Segment)localIterator.next();
          localSegment.close();
        }
      }
      
      public Shape mapShape(Shape paramShape)
      {
        PathIterator localPathIterator = paramShape.getPathIterator(null, 1.0D);
        init();
        double[] arrayOfDouble = new double[2];
        while (!localPathIterator.isDone())
        {
          switch (localPathIterator.currentSegment(arrayOfDouble))
          {
          case 4: 
            close();
            break;
          case 0: 
            moveTo(arrayOfDouble[0], arrayOfDouble[1]);
            break;
          case 1: 
            lineTo(arrayOfDouble[0], arrayOfDouble[1]);
            break;
          }
          localPathIterator.next();
        }
        GeneralPath localGeneralPath = new GeneralPath();
        Iterator localIterator = this.segments.iterator();
        while (localIterator.hasNext())
        {
          LayoutPathImpl.SegmentPath.Segment localSegment = (LayoutPathImpl.SegmentPath.Segment)localIterator.next();
          localGeneralPath.append(localSegment.gp, false);
        }
        return localGeneralPath;
      }
    }
    
    class Segment
    {
      final int ix;
      final double ux;
      final double uy;
      final LayoutPathImpl.SegmentPath.LineInfo temp;
      boolean broken;
      double cx;
      double cy;
      GeneralPath gp;
      
      Segment(int paramInt)
      {
        this.ix = paramInt;
        double d = LayoutPathImpl.SegmentPath.this.data[(paramInt + 2)] - LayoutPathImpl.SegmentPath.this.data[(paramInt - 1)];
        this.ux = ((LayoutPathImpl.SegmentPath.this.data[paramInt] - LayoutPathImpl.SegmentPath.this.data[(paramInt - 3)]) / d);
        this.uy = ((LayoutPathImpl.SegmentPath.this.data[(paramInt + 1)] - LayoutPathImpl.SegmentPath.this.data[(paramInt - 2)]) / d);
        this.temp = new LayoutPathImpl.SegmentPath.LineInfo(LayoutPathImpl.SegmentPath.this);
      }
      
      void init()
      {
        this.broken = true;
        this.cx = (this.cy = Double.MIN_VALUE);
        this.gp = new GeneralPath();
      }
      
      void move()
      {
        this.broken = true;
      }
      
      void close()
      {
        if (!this.broken) {
          this.gp.closePath();
        }
      }
      
      void line(LayoutPathImpl.SegmentPath.LineInfo paramLineInfo)
      {
        if (paramLineInfo.pin(this.ix, this.temp))
        {
          this.temp.sx -= LayoutPathImpl.SegmentPath.this.data[(this.ix - 1)];
          double d1 = LayoutPathImpl.SegmentPath.this.data[(this.ix - 3)] + this.temp.sx * this.ux - this.temp.sy * this.uy;
          double d2 = LayoutPathImpl.SegmentPath.this.data[(this.ix - 2)] + this.temp.sx * this.uy + this.temp.sy * this.ux;
          this.temp.lx -= LayoutPathImpl.SegmentPath.this.data[(this.ix - 1)];
          double d3 = LayoutPathImpl.SegmentPath.this.data[(this.ix - 3)] + this.temp.lx * this.ux - this.temp.ly * this.uy;
          double d4 = LayoutPathImpl.SegmentPath.this.data[(this.ix - 2)] + this.temp.lx * this.uy + this.temp.ly * this.ux;
          if ((d1 != this.cx) || (d2 != this.cy)) {
            if (this.broken) {
              this.gp.moveTo((float)d1, (float)d2);
            } else {
              this.gp.lineTo((float)d1, (float)d2);
            }
          }
          this.gp.lineTo((float)d3, (float)d4);
          this.broken = false;
          this.cx = d3;
          this.cy = d4;
        }
      }
    }
  }
  
  public static final class SegmentPathBuilder
  {
    private double[] data;
    private int w;
    private double px;
    private double py;
    private double a;
    private boolean pconnect;
    
    public SegmentPathBuilder() {}
    
    public void reset(int paramInt)
    {
      if ((this.data == null) || (paramInt > this.data.length)) {
        this.data = new double[paramInt];
      } else if (paramInt == 0) {
        this.data = null;
      }
      this.w = 0;
      this.px = (this.py = 0.0D);
      this.pconnect = false;
    }
    
    public LayoutPathImpl.SegmentPath build(LayoutPathImpl.EndType paramEndType, double... paramVarArgs)
    {
      assert (paramVarArgs.length % 2 == 0);
      reset(paramVarArgs.length / 2 * 3);
      for (int i = 0; i < paramVarArgs.length; i += 2) {
        nextPoint(paramVarArgs[i], paramVarArgs[(i + 1)], i != 0);
      }
      return complete(paramEndType);
    }
    
    public void moveTo(double paramDouble1, double paramDouble2)
    {
      nextPoint(paramDouble1, paramDouble2, false);
    }
    
    public void lineTo(double paramDouble1, double paramDouble2)
    {
      nextPoint(paramDouble1, paramDouble2, true);
    }
    
    private void nextPoint(double paramDouble1, double paramDouble2, boolean paramBoolean)
    {
      if ((paramDouble1 == this.px) && (paramDouble2 == this.py)) {
        return;
      }
      if (this.w == 0)
      {
        if (this.data == null) {
          this.data = new double[6];
        }
        if (paramBoolean) {
          this.w = 3;
        }
      }
      if ((this.w != 0) && (!paramBoolean) && (!this.pconnect))
      {
        double tmp82_81 = paramDouble1;
        this.px = tmp82_81;
        this.data[(this.w - 3)] = tmp82_81;
        double tmp99_98 = paramDouble2;
        this.py = tmp99_98;
        this.data[(this.w - 2)] = tmp99_98;
        return;
      }
      if (this.w == this.data.length)
      {
        double[] arrayOfDouble = new double[this.w * 2];
        System.arraycopy(this.data, 0, arrayOfDouble, 0, this.w);
        this.data = arrayOfDouble;
      }
      if (paramBoolean)
      {
        double d1 = paramDouble1 - this.px;
        double d2 = paramDouble2 - this.py;
        this.a += Math.sqrt(d1 * d1 + d2 * d2);
      }
      this.data[(this.w++)] = paramDouble1;
      this.data[(this.w++)] = paramDouble2;
      this.data[(this.w++)] = this.a;
      this.px = paramDouble1;
      this.py = paramDouble2;
      this.pconnect = paramBoolean;
    }
    
    public LayoutPathImpl.SegmentPath complete()
    {
      return complete(LayoutPathImpl.EndType.EXTENDED);
    }
    
    public LayoutPathImpl.SegmentPath complete(LayoutPathImpl.EndType paramEndType)
    {
      if ((this.data == null) || (this.w < 6)) {
        return null;
      }
      LayoutPathImpl.SegmentPath localSegmentPath;
      if (this.w == this.data.length)
      {
        localSegmentPath = new LayoutPathImpl.SegmentPath(this.data, paramEndType);
        reset(0);
      }
      else
      {
        double[] arrayOfDouble = new double[this.w];
        System.arraycopy(this.data, 0, arrayOfDouble, 0, this.w);
        localSegmentPath = new LayoutPathImpl.SegmentPath(arrayOfDouble, paramEndType);
        reset(2);
      }
      return localSegmentPath;
    }
  }
}
