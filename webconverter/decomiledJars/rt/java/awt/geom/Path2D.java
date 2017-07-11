package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import sun.awt.geom.Curve;

public abstract class Path2D
  implements Shape, Cloneable
{
  public static final int WIND_EVEN_ODD = 0;
  public static final int WIND_NON_ZERO = 1;
  private static final byte SEG_MOVETO = 0;
  private static final byte SEG_LINETO = 1;
  private static final byte SEG_QUADTO = 2;
  private static final byte SEG_CUBICTO = 3;
  private static final byte SEG_CLOSE = 4;
  transient byte[] pointTypes;
  transient int numTypes;
  transient int numCoords;
  transient int windingRule;
  static final int INIT_SIZE = 20;
  static final int EXPAND_MAX = 500;
  static final int EXPAND_MAX_COORDS = 1000;
  static final int EXPAND_MIN = 10;
  private static final byte SERIAL_STORAGE_FLT_ARRAY = 48;
  private static final byte SERIAL_STORAGE_DBL_ARRAY = 49;
  private static final byte SERIAL_SEG_FLT_MOVETO = 64;
  private static final byte SERIAL_SEG_FLT_LINETO = 65;
  private static final byte SERIAL_SEG_FLT_QUADTO = 66;
  private static final byte SERIAL_SEG_FLT_CUBICTO = 67;
  private static final byte SERIAL_SEG_DBL_MOVETO = 80;
  private static final byte SERIAL_SEG_DBL_LINETO = 81;
  private static final byte SERIAL_SEG_DBL_QUADTO = 82;
  private static final byte SERIAL_SEG_DBL_CUBICTO = 83;
  private static final byte SERIAL_SEG_CLOSE = 96;
  private static final byte SERIAL_PATH_END = 97;
  
  Path2D() {}
  
  Path2D(int paramInt1, int paramInt2)
  {
    setWindingRule(paramInt1);
    this.pointTypes = new byte[paramInt2];
  }
  
  abstract float[] cloneCoordsFloat(AffineTransform paramAffineTransform);
  
  abstract double[] cloneCoordsDouble(AffineTransform paramAffineTransform);
  
  abstract void append(float paramFloat1, float paramFloat2);
  
  abstract void append(double paramDouble1, double paramDouble2);
  
  abstract Point2D getPoint(int paramInt);
  
  abstract void needRoom(boolean paramBoolean, int paramInt);
  
  abstract int pointCrossings(double paramDouble1, double paramDouble2);
  
  abstract int rectCrossings(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  static byte[] expandPointTypes(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte.length;
    int j = i + paramInt;
    if (j < i) {
      throw new ArrayIndexOutOfBoundsException("pointTypes exceeds maximum capacity !");
    }
    int k = i;
    if (k > 500) {
      k = Math.max(500, i >> 3);
    } else if (k < 10) {
      k = 10;
    }
    assert (k > 0);
    int m = i + k;
    if (m < j) {
      m = Integer.MAX_VALUE;
    }
    for (;;)
    {
      try
      {
        return Arrays.copyOf(paramArrayOfByte, m);
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        if (m == j) {
          throw localOutOfMemoryError;
        }
        m = j + (m - j) / 2;
      }
    }
  }
  
  public abstract void moveTo(double paramDouble1, double paramDouble2);
  
  public abstract void lineTo(double paramDouble1, double paramDouble2);
  
  public abstract void quadTo(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  public abstract void curveTo(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
  
  public final synchronized void closePath()
  {
    if ((this.numTypes == 0) || (this.pointTypes[(this.numTypes - 1)] != 4))
    {
      needRoom(true, 0);
      this.pointTypes[(this.numTypes++)] = 4;
    }
  }
  
  public final void append(Shape paramShape, boolean paramBoolean)
  {
    append(paramShape.getPathIterator(null), paramBoolean);
  }
  
  public abstract void append(PathIterator paramPathIterator, boolean paramBoolean);
  
  public final synchronized int getWindingRule()
  {
    return this.windingRule;
  }
  
  public final void setWindingRule(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("winding rule must be WIND_EVEN_ODD or WIND_NON_ZERO");
    }
    this.windingRule = paramInt;
  }
  
  public final synchronized Point2D getCurrentPoint()
  {
    int i = this.numCoords;
    if ((this.numTypes < 1) || (i < 1)) {
      return null;
    }
    if (this.pointTypes[(this.numTypes - 1)] == 4) {
      for (int j = this.numTypes - 2; j > 0; j--) {
        switch (this.pointTypes[j])
        {
        case 0: 
          break;
        case 1: 
          i -= 2;
          break;
        case 2: 
          i -= 4;
          break;
        case 3: 
          i -= 6;
        }
      }
    }
    return getPoint(i - 2);
  }
  
  public final synchronized void reset()
  {
    this.numTypes = (this.numCoords = 0);
  }
  
  public abstract void transform(AffineTransform paramAffineTransform);
  
  public final synchronized Shape createTransformedShape(AffineTransform paramAffineTransform)
  {
    Path2D localPath2D = (Path2D)clone();
    if (paramAffineTransform != null) {
      localPath2D.transform(paramAffineTransform);
    }
    return localPath2D;
  }
  
  public final Rectangle getBounds()
  {
    return getBounds2D().getBounds();
  }
  
  public static boolean contains(PathIterator paramPathIterator, double paramDouble1, double paramDouble2)
  {
    if (paramDouble1 * 0.0D + paramDouble2 * 0.0D == 0.0D)
    {
      int i = paramPathIterator.getWindingRule() == 1 ? -1 : 1;
      int j = Curve.pointCrossingsForPath(paramPathIterator, paramDouble1, paramDouble2);
      return (j & i) != 0;
    }
    return false;
  }
  
  public static boolean contains(PathIterator paramPathIterator, Point2D paramPoint2D)
  {
    return contains(paramPathIterator, paramPoint2D.getX(), paramPoint2D.getY());
  }
  
  public final boolean contains(double paramDouble1, double paramDouble2)
  {
    if (paramDouble1 * 0.0D + paramDouble2 * 0.0D == 0.0D)
    {
      if (this.numTypes < 2) {
        return false;
      }
      int i = this.windingRule == 1 ? -1 : 1;
      return (pointCrossings(paramDouble1, paramDouble2) & i) != 0;
    }
    return false;
  }
  
  public final boolean contains(Point2D paramPoint2D)
  {
    return contains(paramPoint2D.getX(), paramPoint2D.getY());
  }
  
  public static boolean contains(PathIterator paramPathIterator, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((Double.isNaN(paramDouble1 + paramDouble3)) || (Double.isNaN(paramDouble2 + paramDouble4))) {
      return false;
    }
    if ((paramDouble3 <= 0.0D) || (paramDouble4 <= 0.0D)) {
      return false;
    }
    int i = paramPathIterator.getWindingRule() == 1 ? -1 : 2;
    int j = Curve.rectCrossingsForPath(paramPathIterator, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (j != Integer.MIN_VALUE) && ((j & i) != 0);
  }
  
  public static boolean contains(PathIterator paramPathIterator, Rectangle2D paramRectangle2D)
  {
    return contains(paramPathIterator, paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public final boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((Double.isNaN(paramDouble1 + paramDouble3)) || (Double.isNaN(paramDouble2 + paramDouble4))) {
      return false;
    }
    if ((paramDouble3 <= 0.0D) || (paramDouble4 <= 0.0D)) {
      return false;
    }
    int i = this.windingRule == 1 ? -1 : 2;
    int j = rectCrossings(paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (j != Integer.MIN_VALUE) && ((j & i) != 0);
  }
  
  public final boolean contains(Rectangle2D paramRectangle2D)
  {
    return contains(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public static boolean intersects(PathIterator paramPathIterator, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((Double.isNaN(paramDouble1 + paramDouble3)) || (Double.isNaN(paramDouble2 + paramDouble4))) {
      return false;
    }
    if ((paramDouble3 <= 0.0D) || (paramDouble4 <= 0.0D)) {
      return false;
    }
    int i = paramPathIterator.getWindingRule() == 1 ? -1 : 2;
    int j = Curve.rectCrossingsForPath(paramPathIterator, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (j == Integer.MIN_VALUE) || ((j & i) != 0);
  }
  
  public static boolean intersects(PathIterator paramPathIterator, Rectangle2D paramRectangle2D)
  {
    return intersects(paramPathIterator, paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public final boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((Double.isNaN(paramDouble1 + paramDouble3)) || (Double.isNaN(paramDouble2 + paramDouble4))) {
      return false;
    }
    if ((paramDouble3 <= 0.0D) || (paramDouble4 <= 0.0D)) {
      return false;
    }
    int i = this.windingRule == 1 ? -1 : 2;
    int j = rectCrossings(paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (j == Integer.MIN_VALUE) || ((j & i) != 0);
  }
  
  public final boolean intersects(Rectangle2D paramRectangle2D)
  {
    return intersects(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public final PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble)
  {
    return new FlatteningPathIterator(getPathIterator(paramAffineTransform), paramDouble);
  }
  
  public abstract Object clone();
  
  final void writeObject(ObjectOutputStream paramObjectOutputStream, boolean paramBoolean)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    double[] arrayOfDouble;
    float[] arrayOfFloat;
    if (paramBoolean)
    {
      arrayOfDouble = ((Double)this).doubleCoords;
      arrayOfFloat = null;
    }
    else
    {
      arrayOfFloat = ((Float)this).floatCoords;
      arrayOfDouble = null;
    }
    int i = this.numTypes;
    paramObjectOutputStream.writeByte(paramBoolean ? 49 : 48);
    paramObjectOutputStream.writeInt(i);
    paramObjectOutputStream.writeInt(this.numCoords);
    paramObjectOutputStream.writeByte((byte)this.windingRule);
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      int m;
      int n;
      switch (this.pointTypes[k])
      {
      case 0: 
        m = 1;
        n = paramBoolean ? 80 : 64;
        break;
      case 1: 
        m = 1;
        n = paramBoolean ? 81 : 65;
        break;
      case 2: 
        m = 2;
        n = paramBoolean ? 82 : 66;
        break;
      case 3: 
        m = 3;
        n = paramBoolean ? 83 : 67;
        break;
      case 4: 
        m = 0;
        n = 96;
        break;
      default: 
        throw new InternalError("unrecognized path type");
      }
      paramObjectOutputStream.writeByte(n);
      for (;;)
      {
        m--;
        if (m < 0) {
          break;
        }
        if (paramBoolean)
        {
          paramObjectOutputStream.writeDouble(arrayOfDouble[(j++)]);
          paramObjectOutputStream.writeDouble(arrayOfDouble[(j++)]);
        }
        else
        {
          paramObjectOutputStream.writeFloat(arrayOfFloat[(j++)]);
          paramObjectOutputStream.writeFloat(arrayOfFloat[(j++)]);
        }
      }
    }
    paramObjectOutputStream.writeByte(97);
  }
  
  final void readObject(ObjectInputStream paramObjectInputStream, boolean paramBoolean)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    paramObjectInputStream.readByte();
    int i = paramObjectInputStream.readInt();
    int j = paramObjectInputStream.readInt();
    try
    {
      setWindingRule(paramObjectInputStream.readByte());
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new InvalidObjectException(localIllegalArgumentException.getMessage());
    }
    this.pointTypes = new byte[i < 0 ? 20 : i];
    if (j < 0) {
      j = 40;
    }
    if (paramBoolean) {
      ((Double)this).doubleCoords = new double[j];
    } else {
      ((Float)this).floatCoords = new float[j];
    }
    for (int k = 0; (i < 0) || (k < i); k++)
    {
      int i2 = paramObjectInputStream.readByte();
      int m;
      int n;
      int i1;
      switch (i2)
      {
      case 64: 
        m = 0;
        n = 1;
        i1 = 0;
        break;
      case 65: 
        m = 0;
        n = 1;
        i1 = 1;
        break;
      case 66: 
        m = 0;
        n = 2;
        i1 = 2;
        break;
      case 67: 
        m = 0;
        n = 3;
        i1 = 3;
        break;
      case 80: 
        m = 1;
        n = 1;
        i1 = 0;
        break;
      case 81: 
        m = 1;
        n = 1;
        i1 = 1;
        break;
      case 82: 
        m = 1;
        n = 2;
        i1 = 2;
        break;
      case 83: 
        m = 1;
        n = 3;
        i1 = 3;
        break;
      case 96: 
        m = 0;
        n = 0;
        i1 = 4;
        break;
      case 97: 
        if (i < 0) {
          break label500;
        }
        throw new StreamCorruptedException("unexpected PATH_END");
      case 68: 
      case 69: 
      case 70: 
      case 71: 
      case 72: 
      case 73: 
      case 74: 
      case 75: 
      case 76: 
      case 77: 
      case 78: 
      case 79: 
      case 84: 
      case 85: 
      case 86: 
      case 87: 
      case 88: 
      case 89: 
      case 90: 
      case 91: 
      case 92: 
      case 93: 
      case 94: 
      case 95: 
      default: 
        throw new StreamCorruptedException("unrecognized path type");
      }
      needRoom(i1 != 0, n * 2);
      if (m != 0) {
        for (;;)
        {
          n--;
          if (n < 0) {
            break;
          }
          append(paramObjectInputStream.readDouble(), paramObjectInputStream.readDouble());
        }
      }
      for (;;)
      {
        n--;
        if (n < 0) {
          break;
        }
        append(paramObjectInputStream.readFloat(), paramObjectInputStream.readFloat());
      }
      this.pointTypes[(this.numTypes++)] = i1;
    }
    label500:
    if ((i >= 0) && (paramObjectInputStream.readByte() != 97)) {
      throw new StreamCorruptedException("missing PATH_END");
    }
  }
  
  public static class Double
    extends Path2D
    implements Serializable
  {
    transient double[] doubleCoords;
    private static final long serialVersionUID = 1826762518450014216L;
    
    public Double()
    {
      this(1, 20);
    }
    
    public Double(int paramInt)
    {
      this(paramInt, 20);
    }
    
    public Double(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.doubleCoords = new double[paramInt2 * 2];
    }
    
    public Double(Shape paramShape)
    {
      this(paramShape, null);
    }
    
    public Double(Shape paramShape, AffineTransform paramAffineTransform)
    {
      Object localObject;
      if ((paramShape instanceof Path2D))
      {
        localObject = (Path2D)paramShape;
        setWindingRule(((Path2D)localObject).windingRule);
        this.numTypes = ((Path2D)localObject).numTypes;
        this.pointTypes = Arrays.copyOf(((Path2D)localObject).pointTypes, ((Path2D)localObject).numTypes);
        this.numCoords = ((Path2D)localObject).numCoords;
        this.doubleCoords = ((Path2D)localObject).cloneCoordsDouble(paramAffineTransform);
      }
      else
      {
        localObject = paramShape.getPathIterator(paramAffineTransform);
        setWindingRule(((PathIterator)localObject).getWindingRule());
        this.pointTypes = new byte[20];
        this.doubleCoords = new double[40];
        append((PathIterator)localObject, false);
      }
    }
    
    float[] cloneCoordsFloat(AffineTransform paramAffineTransform)
    {
      float[] arrayOfFloat = new float[this.numCoords];
      if (paramAffineTransform == null) {
        for (int i = 0; i < this.numCoords; i++) {
          arrayOfFloat[i] = ((float)this.doubleCoords[i]);
        }
      } else {
        paramAffineTransform.transform(this.doubleCoords, 0, arrayOfFloat, 0, this.numCoords / 2);
      }
      return arrayOfFloat;
    }
    
    double[] cloneCoordsDouble(AffineTransform paramAffineTransform)
    {
      double[] arrayOfDouble;
      if (paramAffineTransform == null)
      {
        arrayOfDouble = Arrays.copyOf(this.doubleCoords, this.numCoords);
      }
      else
      {
        arrayOfDouble = new double[this.numCoords];
        paramAffineTransform.transform(this.doubleCoords, 0, arrayOfDouble, 0, this.numCoords / 2);
      }
      return arrayOfDouble;
    }
    
    void append(float paramFloat1, float paramFloat2)
    {
      this.doubleCoords[(this.numCoords++)] = paramFloat1;
      this.doubleCoords[(this.numCoords++)] = paramFloat2;
    }
    
    void append(double paramDouble1, double paramDouble2)
    {
      this.doubleCoords[(this.numCoords++)] = paramDouble1;
      this.doubleCoords[(this.numCoords++)] = paramDouble2;
    }
    
    Point2D getPoint(int paramInt)
    {
      return new Point2D.Double(this.doubleCoords[paramInt], this.doubleCoords[(paramInt + 1)]);
    }
    
    void needRoom(boolean paramBoolean, int paramInt)
    {
      if ((this.numTypes == 0) && (paramBoolean)) {
        throw new IllegalPathStateException("missing initial moveto in path definition");
      }
      if (this.numTypes >= this.pointTypes.length) {
        this.pointTypes = expandPointTypes(this.pointTypes, 1);
      }
      if (this.numCoords > this.doubleCoords.length - paramInt) {
        this.doubleCoords = expandCoords(this.doubleCoords, paramInt);
      }
    }
    
    static double[] expandCoords(double[] paramArrayOfDouble, int paramInt)
    {
      int i = paramArrayOfDouble.length;
      int j = i + paramInt;
      if (j < i) {
        throw new ArrayIndexOutOfBoundsException("coords exceeds maximum capacity !");
      }
      int k = i;
      if (k > 1000) {
        k = Math.max(1000, i >> 3);
      } else if (k < 10) {
        k = 10;
      }
      assert (k > paramInt);
      int m = i + k;
      if (m < j) {
        m = Integer.MAX_VALUE;
      }
      for (;;)
      {
        try
        {
          return Arrays.copyOf(paramArrayOfDouble, m);
        }
        catch (OutOfMemoryError localOutOfMemoryError)
        {
          if (m == j) {
            throw localOutOfMemoryError;
          }
          m = j + (m - j) / 2;
        }
      }
    }
    
    public final synchronized void moveTo(double paramDouble1, double paramDouble2)
    {
      if ((this.numTypes > 0) && (this.pointTypes[(this.numTypes - 1)] == 0))
      {
        this.doubleCoords[(this.numCoords - 2)] = paramDouble1;
        this.doubleCoords[(this.numCoords - 1)] = paramDouble2;
      }
      else
      {
        needRoom(false, 2);
        this.pointTypes[(this.numTypes++)] = 0;
        this.doubleCoords[(this.numCoords++)] = paramDouble1;
        this.doubleCoords[(this.numCoords++)] = paramDouble2;
      }
    }
    
    public final synchronized void lineTo(double paramDouble1, double paramDouble2)
    {
      needRoom(true, 2);
      this.pointTypes[(this.numTypes++)] = 1;
      this.doubleCoords[(this.numCoords++)] = paramDouble1;
      this.doubleCoords[(this.numCoords++)] = paramDouble2;
    }
    
    public final synchronized void quadTo(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      needRoom(true, 4);
      this.pointTypes[(this.numTypes++)] = 2;
      this.doubleCoords[(this.numCoords++)] = paramDouble1;
      this.doubleCoords[(this.numCoords++)] = paramDouble2;
      this.doubleCoords[(this.numCoords++)] = paramDouble3;
      this.doubleCoords[(this.numCoords++)] = paramDouble4;
    }
    
    public final synchronized void curveTo(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
    {
      needRoom(true, 6);
      this.pointTypes[(this.numTypes++)] = 3;
      this.doubleCoords[(this.numCoords++)] = paramDouble1;
      this.doubleCoords[(this.numCoords++)] = paramDouble2;
      this.doubleCoords[(this.numCoords++)] = paramDouble3;
      this.doubleCoords[(this.numCoords++)] = paramDouble4;
      this.doubleCoords[(this.numCoords++)] = paramDouble5;
      this.doubleCoords[(this.numCoords++)] = paramDouble6;
    }
    
    int pointCrossings(double paramDouble1, double paramDouble2)
    {
      if (this.numTypes == 0) {
        return 0;
      }
      double[] arrayOfDouble = this.doubleCoords;
      double d1;
      double d3 = d1 = arrayOfDouble[0];
      double d2;
      double d4 = d2 = arrayOfDouble[1];
      int i = 0;
      int j = 2;
      for (int k = 1; k < this.numTypes; k++)
      {
        double d5;
        double d6;
        switch (this.pointTypes[k])
        {
        case 0: 
          if (d4 != d2) {
            i += Curve.pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d1, d2);
          }
          d1 = d3 = arrayOfDouble[(j++)];
          d2 = d4 = arrayOfDouble[(j++)];
          break;
        case 1: 
          i += Curve.pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d5 = arrayOfDouble[(j++)], d6 = arrayOfDouble[(j++)]);
          d3 = d5;
          d4 = d6;
          break;
        case 2: 
          i += Curve.pointCrossingsForQuad(paramDouble1, paramDouble2, d3, d4, arrayOfDouble[(j++)], arrayOfDouble[(j++)], d5 = arrayOfDouble[(j++)], d6 = arrayOfDouble[(j++)], 0);
          d3 = d5;
          d4 = d6;
          break;
        case 3: 
          i += Curve.pointCrossingsForCubic(paramDouble1, paramDouble2, d3, d4, arrayOfDouble[(j++)], arrayOfDouble[(j++)], arrayOfDouble[(j++)], arrayOfDouble[(j++)], d5 = arrayOfDouble[(j++)], d6 = arrayOfDouble[(j++)], 0);
          d3 = d5;
          d4 = d6;
          break;
        case 4: 
          if (d4 != d2) {
            i += Curve.pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d1, d2);
          }
          d3 = d1;
          d4 = d2;
        }
      }
      if (d4 != d2) {
        i += Curve.pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d1, d2);
      }
      return i;
    }
    
    int rectCrossings(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      if (this.numTypes == 0) {
        return 0;
      }
      double[] arrayOfDouble = this.doubleCoords;
      double d3;
      double d1 = d3 = arrayOfDouble[0];
      double d4;
      double d2 = d4 = arrayOfDouble[1];
      int i = 0;
      int j = 2;
      for (int k = 1; (i != Integer.MIN_VALUE) && (k < this.numTypes); k++)
      {
        double d5;
        double d6;
        switch (this.pointTypes[k])
        {
        case 0: 
          if ((d1 != d3) || (d2 != d4)) {
            i = Curve.rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, d4);
          }
          d3 = d1 = arrayOfDouble[(j++)];
          d4 = d2 = arrayOfDouble[(j++)];
          break;
        case 1: 
          d5 = arrayOfDouble[(j++)];
          d6 = arrayOfDouble[(j++)];
          i = Curve.rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d5, d6);
          d1 = d5;
          d2 = d6;
          break;
        case 2: 
          i = Curve.rectCrossingsForQuad(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, arrayOfDouble[(j++)], arrayOfDouble[(j++)], d5 = arrayOfDouble[(j++)], d6 = arrayOfDouble[(j++)], 0);
          d1 = d5;
          d2 = d6;
          break;
        case 3: 
          i = Curve.rectCrossingsForCubic(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, arrayOfDouble[(j++)], arrayOfDouble[(j++)], arrayOfDouble[(j++)], arrayOfDouble[(j++)], d5 = arrayOfDouble[(j++)], d6 = arrayOfDouble[(j++)], 0);
          d1 = d5;
          d2 = d6;
          break;
        case 4: 
          if ((d1 != d3) || (d2 != d4)) {
            i = Curve.rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, d4);
          }
          d1 = d3;
          d2 = d4;
        }
      }
      if ((i != Integer.MIN_VALUE) && ((d1 != d3) || (d2 != d4))) {
        i = Curve.rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, d4);
      }
      return i;
    }
    
    public final void append(PathIterator paramPathIterator, boolean paramBoolean)
    {
      double[] arrayOfDouble = new double[6];
      while (!paramPathIterator.isDone())
      {
        switch (paramPathIterator.currentSegment(arrayOfDouble))
        {
        case 0: 
          if ((!paramBoolean) || (this.numTypes < 1) || (this.numCoords < 1)) {
            moveTo(arrayOfDouble[0], arrayOfDouble[1]);
          } else if ((this.pointTypes[(this.numTypes - 1)] == 4) || (this.doubleCoords[(this.numCoords - 2)] != arrayOfDouble[0]) || (this.doubleCoords[(this.numCoords - 1)] != arrayOfDouble[1])) {
            lineTo(arrayOfDouble[0], arrayOfDouble[1]);
          }
          break;
        case 1: 
          lineTo(arrayOfDouble[0], arrayOfDouble[1]);
          break;
        case 2: 
          quadTo(arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2], arrayOfDouble[3]);
          break;
        case 3: 
          curveTo(arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2], arrayOfDouble[3], arrayOfDouble[4], arrayOfDouble[5]);
          break;
        case 4: 
          closePath();
        }
        paramPathIterator.next();
        paramBoolean = false;
      }
    }
    
    public final void transform(AffineTransform paramAffineTransform)
    {
      paramAffineTransform.transform(this.doubleCoords, 0, this.doubleCoords, 0, this.numCoords / 2);
    }
    
    public final synchronized Rectangle2D getBounds2D()
    {
      int i = this.numCoords;
      double d4;
      double d2;
      double d3;
      if (i > 0)
      {
        d2 = d4 = this.doubleCoords[(--i)];
        d1 = d3 = this.doubleCoords[(--i)];
        while (i > 0)
        {
          double d5 = this.doubleCoords[(--i)];
          double d6 = this.doubleCoords[(--i)];
          if (d6 < d1) {
            d1 = d6;
          }
          if (d5 < d2) {
            d2 = d5;
          }
          if (d6 > d3) {
            d3 = d6;
          }
          if (d5 > d4) {
            d4 = d5;
          }
        }
      }
      double d1 = d2 = d3 = d4 = 0.0D;
      return new Rectangle2D.Double(d1, d2, d3 - d1, d4 - d2);
    }
    
    public final PathIterator getPathIterator(AffineTransform paramAffineTransform)
    {
      if (paramAffineTransform == null) {
        return new CopyIterator(this);
      }
      return new TxIterator(this, paramAffineTransform);
    }
    
    public final Object clone()
    {
      return new Double(this);
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      super.writeObject(paramObjectOutputStream, true);
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws ClassNotFoundException, IOException
    {
      super.readObject(paramObjectInputStream, true);
    }
    
    static class CopyIterator
      extends Path2D.Iterator
    {
      double[] doubleCoords;
      
      CopyIterator(Path2D.Double paramDouble)
      {
        super();
        this.doubleCoords = paramDouble.doubleCoords;
      }
      
      public int currentSegment(float[] paramArrayOfFloat)
      {
        int i = this.path.pointTypes[this.typeIdx];
        int j = curvecoords[i];
        if (j > 0) {
          for (int k = 0; k < j; k++) {
            paramArrayOfFloat[k] = ((float)this.doubleCoords[(this.pointIdx + k)]);
          }
        }
        return i;
      }
      
      public int currentSegment(double[] paramArrayOfDouble)
      {
        int i = this.path.pointTypes[this.typeIdx];
        int j = curvecoords[i];
        if (j > 0) {
          System.arraycopy(this.doubleCoords, this.pointIdx, paramArrayOfDouble, 0, j);
        }
        return i;
      }
    }
    
    static class TxIterator
      extends Path2D.Iterator
    {
      double[] doubleCoords;
      AffineTransform affine;
      
      TxIterator(Path2D.Double paramDouble, AffineTransform paramAffineTransform)
      {
        super();
        this.doubleCoords = paramDouble.doubleCoords;
        this.affine = paramAffineTransform;
      }
      
      public int currentSegment(float[] paramArrayOfFloat)
      {
        int i = this.path.pointTypes[this.typeIdx];
        int j = curvecoords[i];
        if (j > 0) {
          this.affine.transform(this.doubleCoords, this.pointIdx, paramArrayOfFloat, 0, j / 2);
        }
        return i;
      }
      
      public int currentSegment(double[] paramArrayOfDouble)
      {
        int i = this.path.pointTypes[this.typeIdx];
        int j = curvecoords[i];
        if (j > 0) {
          this.affine.transform(this.doubleCoords, this.pointIdx, paramArrayOfDouble, 0, j / 2);
        }
        return i;
      }
    }
  }
  
  public static class Float
    extends Path2D
    implements Serializable
  {
    transient float[] floatCoords;
    private static final long serialVersionUID = 6990832515060788886L;
    
    public Float()
    {
      this(1, 20);
    }
    
    public Float(int paramInt)
    {
      this(paramInt, 20);
    }
    
    public Float(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.floatCoords = new float[paramInt2 * 2];
    }
    
    public Float(Shape paramShape)
    {
      this(paramShape, null);
    }
    
    public Float(Shape paramShape, AffineTransform paramAffineTransform)
    {
      Object localObject;
      if ((paramShape instanceof Path2D))
      {
        localObject = (Path2D)paramShape;
        setWindingRule(((Path2D)localObject).windingRule);
        this.numTypes = ((Path2D)localObject).numTypes;
        this.pointTypes = Arrays.copyOf(((Path2D)localObject).pointTypes, ((Path2D)localObject).numTypes);
        this.numCoords = ((Path2D)localObject).numCoords;
        this.floatCoords = ((Path2D)localObject).cloneCoordsFloat(paramAffineTransform);
      }
      else
      {
        localObject = paramShape.getPathIterator(paramAffineTransform);
        setWindingRule(((PathIterator)localObject).getWindingRule());
        this.pointTypes = new byte[20];
        this.floatCoords = new float[40];
        append((PathIterator)localObject, false);
      }
    }
    
    float[] cloneCoordsFloat(AffineTransform paramAffineTransform)
    {
      float[] arrayOfFloat;
      if (paramAffineTransform == null)
      {
        arrayOfFloat = Arrays.copyOf(this.floatCoords, this.numCoords);
      }
      else
      {
        arrayOfFloat = new float[this.numCoords];
        paramAffineTransform.transform(this.floatCoords, 0, arrayOfFloat, 0, this.numCoords / 2);
      }
      return arrayOfFloat;
    }
    
    double[] cloneCoordsDouble(AffineTransform paramAffineTransform)
    {
      double[] arrayOfDouble = new double[this.numCoords];
      if (paramAffineTransform == null) {
        for (int i = 0; i < this.numCoords; i++) {
          arrayOfDouble[i] = this.floatCoords[i];
        }
      } else {
        paramAffineTransform.transform(this.floatCoords, 0, arrayOfDouble, 0, this.numCoords / 2);
      }
      return arrayOfDouble;
    }
    
    void append(float paramFloat1, float paramFloat2)
    {
      this.floatCoords[(this.numCoords++)] = paramFloat1;
      this.floatCoords[(this.numCoords++)] = paramFloat2;
    }
    
    void append(double paramDouble1, double paramDouble2)
    {
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble1);
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble2);
    }
    
    Point2D getPoint(int paramInt)
    {
      return new Point2D.Float(this.floatCoords[paramInt], this.floatCoords[(paramInt + 1)]);
    }
    
    void needRoom(boolean paramBoolean, int paramInt)
    {
      if ((this.numTypes == 0) && (paramBoolean)) {
        throw new IllegalPathStateException("missing initial moveto in path definition");
      }
      if (this.numTypes >= this.pointTypes.length) {
        this.pointTypes = expandPointTypes(this.pointTypes, 1);
      }
      if (this.numCoords > this.floatCoords.length - paramInt) {
        this.floatCoords = expandCoords(this.floatCoords, paramInt);
      }
    }
    
    static float[] expandCoords(float[] paramArrayOfFloat, int paramInt)
    {
      int i = paramArrayOfFloat.length;
      int j = i + paramInt;
      if (j < i) {
        throw new ArrayIndexOutOfBoundsException("coords exceeds maximum capacity !");
      }
      int k = i;
      if (k > 1000) {
        k = Math.max(1000, i >> 3);
      } else if (k < 10) {
        k = 10;
      }
      assert (k > paramInt);
      int m = i + k;
      if (m < j) {
        m = Integer.MAX_VALUE;
      }
      for (;;)
      {
        try
        {
          return Arrays.copyOf(paramArrayOfFloat, m);
        }
        catch (OutOfMemoryError localOutOfMemoryError)
        {
          if (m == j) {
            throw localOutOfMemoryError;
          }
          m = j + (m - j) / 2;
        }
      }
    }
    
    public final synchronized void moveTo(double paramDouble1, double paramDouble2)
    {
      if ((this.numTypes > 0) && (this.pointTypes[(this.numTypes - 1)] == 0))
      {
        this.floatCoords[(this.numCoords - 2)] = ((float)paramDouble1);
        this.floatCoords[(this.numCoords - 1)] = ((float)paramDouble2);
      }
      else
      {
        needRoom(false, 2);
        this.pointTypes[(this.numTypes++)] = 0;
        this.floatCoords[(this.numCoords++)] = ((float)paramDouble1);
        this.floatCoords[(this.numCoords++)] = ((float)paramDouble2);
      }
    }
    
    public final synchronized void moveTo(float paramFloat1, float paramFloat2)
    {
      if ((this.numTypes > 0) && (this.pointTypes[(this.numTypes - 1)] == 0))
      {
        this.floatCoords[(this.numCoords - 2)] = paramFloat1;
        this.floatCoords[(this.numCoords - 1)] = paramFloat2;
      }
      else
      {
        needRoom(false, 2);
        this.pointTypes[(this.numTypes++)] = 0;
        this.floatCoords[(this.numCoords++)] = paramFloat1;
        this.floatCoords[(this.numCoords++)] = paramFloat2;
      }
    }
    
    public final synchronized void lineTo(double paramDouble1, double paramDouble2)
    {
      needRoom(true, 2);
      this.pointTypes[(this.numTypes++)] = 1;
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble1);
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble2);
    }
    
    public final synchronized void lineTo(float paramFloat1, float paramFloat2)
    {
      needRoom(true, 2);
      this.pointTypes[(this.numTypes++)] = 1;
      this.floatCoords[(this.numCoords++)] = paramFloat1;
      this.floatCoords[(this.numCoords++)] = paramFloat2;
    }
    
    public final synchronized void quadTo(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      needRoom(true, 4);
      this.pointTypes[(this.numTypes++)] = 2;
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble1);
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble2);
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble3);
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble4);
    }
    
    public final synchronized void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      needRoom(true, 4);
      this.pointTypes[(this.numTypes++)] = 2;
      this.floatCoords[(this.numCoords++)] = paramFloat1;
      this.floatCoords[(this.numCoords++)] = paramFloat2;
      this.floatCoords[(this.numCoords++)] = paramFloat3;
      this.floatCoords[(this.numCoords++)] = paramFloat4;
    }
    
    public final synchronized void curveTo(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
    {
      needRoom(true, 6);
      this.pointTypes[(this.numTypes++)] = 3;
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble1);
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble2);
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble3);
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble4);
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble5);
      this.floatCoords[(this.numCoords++)] = ((float)paramDouble6);
    }
    
    public final synchronized void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    {
      needRoom(true, 6);
      this.pointTypes[(this.numTypes++)] = 3;
      this.floatCoords[(this.numCoords++)] = paramFloat1;
      this.floatCoords[(this.numCoords++)] = paramFloat2;
      this.floatCoords[(this.numCoords++)] = paramFloat3;
      this.floatCoords[(this.numCoords++)] = paramFloat4;
      this.floatCoords[(this.numCoords++)] = paramFloat5;
      this.floatCoords[(this.numCoords++)] = paramFloat6;
    }
    
    int pointCrossings(double paramDouble1, double paramDouble2)
    {
      if (this.numTypes == 0) {
        return 0;
      }
      float[] arrayOfFloat = this.floatCoords;
      double d1;
      double d3 = d1 = arrayOfFloat[0];
      double d2;
      double d4 = d2 = arrayOfFloat[1];
      int i = 0;
      int j = 2;
      for (int k = 1; k < this.numTypes; k++)
      {
        double d5;
        double d6;
        switch (this.pointTypes[k])
        {
        case 0: 
          if (d4 != d2) {
            i += Curve.pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d1, d2);
          }
          d1 = d3 = arrayOfFloat[(j++)];
          d2 = d4 = arrayOfFloat[(j++)];
          break;
        case 1: 
          i += Curve.pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d5 = arrayOfFloat[(j++)], d6 = arrayOfFloat[(j++)]);
          d3 = d5;
          d4 = d6;
          break;
        case 2: 
          i += Curve.pointCrossingsForQuad(paramDouble1, paramDouble2, d3, d4, arrayOfFloat[(j++)], arrayOfFloat[(j++)], d5 = arrayOfFloat[(j++)], d6 = arrayOfFloat[(j++)], 0);
          d3 = d5;
          d4 = d6;
          break;
        case 3: 
          i += Curve.pointCrossingsForCubic(paramDouble1, paramDouble2, d3, d4, arrayOfFloat[(j++)], arrayOfFloat[(j++)], arrayOfFloat[(j++)], arrayOfFloat[(j++)], d5 = arrayOfFloat[(j++)], d6 = arrayOfFloat[(j++)], 0);
          d3 = d5;
          d4 = d6;
          break;
        case 4: 
          if (d4 != d2) {
            i += Curve.pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d1, d2);
          }
          d3 = d1;
          d4 = d2;
        }
      }
      if (d4 != d2) {
        i += Curve.pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d1, d2);
      }
      return i;
    }
    
    int rectCrossings(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      if (this.numTypes == 0) {
        return 0;
      }
      float[] arrayOfFloat = this.floatCoords;
      double d3;
      double d1 = d3 = arrayOfFloat[0];
      double d4;
      double d2 = d4 = arrayOfFloat[1];
      int i = 0;
      int j = 2;
      for (int k = 1; (i != Integer.MIN_VALUE) && (k < this.numTypes); k++)
      {
        double d5;
        double d6;
        switch (this.pointTypes[k])
        {
        case 0: 
          if ((d1 != d3) || (d2 != d4)) {
            i = Curve.rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, d4);
          }
          d3 = d1 = arrayOfFloat[(j++)];
          d4 = d2 = arrayOfFloat[(j++)];
          break;
        case 1: 
          i = Curve.rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d5 = arrayOfFloat[(j++)], d6 = arrayOfFloat[(j++)]);
          d1 = d5;
          d2 = d6;
          break;
        case 2: 
          i = Curve.rectCrossingsForQuad(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, arrayOfFloat[(j++)], arrayOfFloat[(j++)], d5 = arrayOfFloat[(j++)], d6 = arrayOfFloat[(j++)], 0);
          d1 = d5;
          d2 = d6;
          break;
        case 3: 
          i = Curve.rectCrossingsForCubic(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, arrayOfFloat[(j++)], arrayOfFloat[(j++)], arrayOfFloat[(j++)], arrayOfFloat[(j++)], d5 = arrayOfFloat[(j++)], d6 = arrayOfFloat[(j++)], 0);
          d1 = d5;
          d2 = d6;
          break;
        case 4: 
          if ((d1 != d3) || (d2 != d4)) {
            i = Curve.rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, d4);
          }
          d1 = d3;
          d2 = d4;
        }
      }
      if ((i != Integer.MIN_VALUE) && ((d1 != d3) || (d2 != d4))) {
        i = Curve.rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, d4);
      }
      return i;
    }
    
    public final void append(PathIterator paramPathIterator, boolean paramBoolean)
    {
      float[] arrayOfFloat = new float[6];
      while (!paramPathIterator.isDone())
      {
        switch (paramPathIterator.currentSegment(arrayOfFloat))
        {
        case 0: 
          if ((!paramBoolean) || (this.numTypes < 1) || (this.numCoords < 1)) {
            moveTo(arrayOfFloat[0], arrayOfFloat[1]);
          } else if ((this.pointTypes[(this.numTypes - 1)] == 4) || (this.floatCoords[(this.numCoords - 2)] != arrayOfFloat[0]) || (this.floatCoords[(this.numCoords - 1)] != arrayOfFloat[1])) {
            lineTo(arrayOfFloat[0], arrayOfFloat[1]);
          }
          break;
        case 1: 
          lineTo(arrayOfFloat[0], arrayOfFloat[1]);
          break;
        case 2: 
          quadTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
          break;
        case 3: 
          curveTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
          break;
        case 4: 
          closePath();
        }
        paramPathIterator.next();
        paramBoolean = false;
      }
    }
    
    public final void transform(AffineTransform paramAffineTransform)
    {
      paramAffineTransform.transform(this.floatCoords, 0, this.floatCoords, 0, this.numCoords / 2);
    }
    
    public final synchronized Rectangle2D getBounds2D()
    {
      int i = this.numCoords;
      float f4;
      float f2;
      float f3;
      if (i > 0)
      {
        f2 = f4 = this.floatCoords[(--i)];
        f1 = f3 = this.floatCoords[(--i)];
        while (i > 0)
        {
          float f5 = this.floatCoords[(--i)];
          float f6 = this.floatCoords[(--i)];
          if (f6 < f1) {
            f1 = f6;
          }
          if (f5 < f2) {
            f2 = f5;
          }
          if (f6 > f3) {
            f3 = f6;
          }
          if (f5 > f4) {
            f4 = f5;
          }
        }
      }
      float f1 = f2 = f3 = f4 = 0.0F;
      return new Rectangle2D.Float(f1, f2, f3 - f1, f4 - f2);
    }
    
    public final PathIterator getPathIterator(AffineTransform paramAffineTransform)
    {
      if (paramAffineTransform == null) {
        return new CopyIterator(this);
      }
      return new TxIterator(this, paramAffineTransform);
    }
    
    public final Object clone()
    {
      if ((this instanceof GeneralPath)) {
        return new GeneralPath(this);
      }
      return new Float(this);
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      super.writeObject(paramObjectOutputStream, false);
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws ClassNotFoundException, IOException
    {
      super.readObject(paramObjectInputStream, false);
    }
    
    static class CopyIterator
      extends Path2D.Iterator
    {
      float[] floatCoords;
      
      CopyIterator(Path2D.Float paramFloat)
      {
        super();
        this.floatCoords = paramFloat.floatCoords;
      }
      
      public int currentSegment(float[] paramArrayOfFloat)
      {
        int i = this.path.pointTypes[this.typeIdx];
        int j = curvecoords[i];
        if (j > 0) {
          System.arraycopy(this.floatCoords, this.pointIdx, paramArrayOfFloat, 0, j);
        }
        return i;
      }
      
      public int currentSegment(double[] paramArrayOfDouble)
      {
        int i = this.path.pointTypes[this.typeIdx];
        int j = curvecoords[i];
        if (j > 0) {
          for (int k = 0; k < j; k++) {
            paramArrayOfDouble[k] = this.floatCoords[(this.pointIdx + k)];
          }
        }
        return i;
      }
    }
    
    static class TxIterator
      extends Path2D.Iterator
    {
      float[] floatCoords;
      AffineTransform affine;
      
      TxIterator(Path2D.Float paramFloat, AffineTransform paramAffineTransform)
      {
        super();
        this.floatCoords = paramFloat.floatCoords;
        this.affine = paramAffineTransform;
      }
      
      public int currentSegment(float[] paramArrayOfFloat)
      {
        int i = this.path.pointTypes[this.typeIdx];
        int j = curvecoords[i];
        if (j > 0) {
          this.affine.transform(this.floatCoords, this.pointIdx, paramArrayOfFloat, 0, j / 2);
        }
        return i;
      }
      
      public int currentSegment(double[] paramArrayOfDouble)
      {
        int i = this.path.pointTypes[this.typeIdx];
        int j = curvecoords[i];
        if (j > 0) {
          this.affine.transform(this.floatCoords, this.pointIdx, paramArrayOfDouble, 0, j / 2);
        }
        return i;
      }
    }
  }
  
  static abstract class Iterator
    implements PathIterator
  {
    int typeIdx;
    int pointIdx;
    Path2D path;
    static final int[] curvecoords = { 2, 2, 4, 6, 0 };
    
    Iterator(Path2D paramPath2D)
    {
      this.path = paramPath2D;
    }
    
    public int getWindingRule()
    {
      return this.path.getWindingRule();
    }
    
    public boolean isDone()
    {
      return this.typeIdx >= this.path.numTypes;
    }
    
    public void next()
    {
      int i = this.path.pointTypes[(this.typeIdx++)];
      this.pointIdx += curvecoords[i];
    }
  }
}
