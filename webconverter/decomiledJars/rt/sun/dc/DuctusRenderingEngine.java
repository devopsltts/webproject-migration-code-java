package sun.dc;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D.Float;
import java.awt.geom.PathIterator;
import java.io.PrintStream;
import sun.awt.geom.PathConsumer2D;
import sun.dc.path.FastPathProducer;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathException;
import sun.dc.pr.PRException;
import sun.dc.pr.PathDasher;
import sun.dc.pr.PathStroker;
import sun.dc.pr.Rasterizer;
import sun.java2d.pipe.AATileGenerator;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderingEngine;

public class DuctusRenderingEngine
  extends RenderingEngine
{
  static final float PenUnits = 0.01F;
  static final int MinPenUnits = 100;
  static final int MinPenUnitsAA = 20;
  static final float MinPenSizeAA = 0.19999999F;
  static final float UPPER_BND = 1.7014117E38F;
  static final float LOWER_BND = -1.7014117E38F;
  private static final int[] RasterizerCaps = { 30, 10, 20 };
  private static final int[] RasterizerCorners = { 50, 10, 40 };
  private static Rasterizer theRasterizer;
  
  public DuctusRenderingEngine() {}
  
  static float[] getTransformMatrix(AffineTransform paramAffineTransform)
  {
    float[] arrayOfFloat = new float[4];
    double[] arrayOfDouble = new double[6];
    paramAffineTransform.getMatrix(arrayOfDouble);
    for (int i = 0; i < 4; i++) {
      arrayOfFloat[i] = ((float)arrayOfDouble[i]);
    }
    return arrayOfFloat;
  }
  
  public Shape createStrokedShape(Shape paramShape, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOfFloat, float paramFloat3)
  {
    FillAdapter localFillAdapter = new FillAdapter();
    PathStroker localPathStroker = new PathStroker(localFillAdapter);
    PathDasher localPathDasher = null;
    try
    {
      localPathStroker.setPenDiameter(paramFloat1);
      localPathStroker.setPenT4(null);
      localPathStroker.setCaps(RasterizerCaps[paramInt1]);
      localPathStroker.setCorners(RasterizerCorners[paramInt2], paramFloat2);
      Object localObject1;
      if (paramArrayOfFloat != null)
      {
        localPathDasher = new PathDasher(localPathStroker);
        localPathDasher.setDash(paramArrayOfFloat, paramFloat3);
        localPathDasher.setDashT4(null);
        localObject1 = localPathDasher;
      }
      else
      {
        localObject1 = localPathStroker;
      }
      feedConsumer((PathConsumer)localObject1, paramShape.getPathIterator(null));
    }
    finally
    {
      localPathStroker.dispose();
      if (localPathDasher != null) {
        localPathDasher.dispose();
      }
    }
    return localFillAdapter.getShape();
  }
  
  public void strokeTo(Shape paramShape, AffineTransform paramAffineTransform, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PathConsumer2D paramPathConsumer2D)
  {
    PathStroker localPathStroker = new PathStroker(paramPathConsumer2D);
    Object localObject1 = localPathStroker;
    float[] arrayOfFloat1 = null;
    if (!paramBoolean1)
    {
      localPathStroker.setPenDiameter(paramBasicStroke.getLineWidth());
      if (paramAffineTransform != null) {
        arrayOfFloat1 = getTransformMatrix(paramAffineTransform);
      }
      localPathStroker.setPenT4(arrayOfFloat1);
      localPathStroker.setPenFitting(0.01F, 100);
    }
    localPathStroker.setCaps(RasterizerCaps[paramBasicStroke.getEndCap()]);
    localPathStroker.setCorners(RasterizerCorners[paramBasicStroke.getLineJoin()], paramBasicStroke.getMiterLimit());
    float[] arrayOfFloat2 = paramBasicStroke.getDashArray();
    Object localObject2;
    if (arrayOfFloat2 != null)
    {
      localObject2 = new PathDasher(localPathStroker);
      ((PathDasher)localObject2).setDash(arrayOfFloat2, paramBasicStroke.getDashPhase());
      if ((paramAffineTransform != null) && (arrayOfFloat1 == null)) {
        arrayOfFloat1 = getTransformMatrix(paramAffineTransform);
      }
      ((PathDasher)localObject2).setDashT4(arrayOfFloat1);
      localObject1 = localObject2;
    }
    try
    {
      localObject2 = paramShape.getPathIterator(paramAffineTransform);
      feedConsumer((PathIterator)localObject2, (PathConsumer)localObject1, paramBoolean2, 0.25F);
      PathConsumer localPathConsumer;
      return;
    }
    catch (PathException localPathException)
    {
      throw new InternalError("Unable to Stroke shape (" + localPathException.getMessage() + ")", localPathException);
    }
    finally
    {
      while ((localObject1 != null) && (localObject1 != paramPathConsumer2D))
      {
        localPathConsumer = ((PathConsumer)localObject1).getConsumer();
        ((PathConsumer)localObject1).dispose();
        localObject1 = localPathConsumer;
      }
    }
  }
  
  public static void feedConsumer(PathIterator paramPathIterator, PathConsumer paramPathConsumer, boolean paramBoolean, float paramFloat)
    throws PathException
  {
    paramPathConsumer.beginPath();
    int i = 0;
    int j = 0;
    int k = 0;
    float f1 = 0.0F;
    float f2 = 0.0F;
    float[] arrayOfFloat = new float[6];
    float f3 = 0.5F - paramFloat;
    float f4 = 0.0F;
    float f5 = 0.0F;
    while (!paramPathIterator.isDone())
    {
      int m = paramPathIterator.currentSegment(arrayOfFloat);
      if (i == 1)
      {
        i = 0;
        if (m != 0)
        {
          paramPathConsumer.beginSubpath(f1, f2);
          k = 1;
        }
      }
      if (paramBoolean)
      {
        int n;
        switch (m)
        {
        case 3: 
          n = 4;
          break;
        case 2: 
          n = 2;
          break;
        case 0: 
        case 1: 
          n = 0;
          break;
        case 4: 
        default: 
          n = -1;
        }
        if (n >= 0)
        {
          float f6 = arrayOfFloat[n];
          float f7 = arrayOfFloat[(n + 1)];
          float f8 = (float)Math.floor(f6 + f3) + paramFloat;
          float f9 = (float)Math.floor(f7 + f3) + paramFloat;
          arrayOfFloat[n] = f8;
          arrayOfFloat[(n + 1)] = f9;
          f8 -= f6;
          f9 -= f7;
          switch (m)
          {
          case 3: 
            arrayOfFloat[0] += f4;
            arrayOfFloat[1] += f5;
            arrayOfFloat[2] += f8;
            arrayOfFloat[3] += f9;
            break;
          case 2: 
            arrayOfFloat[0] += (f8 + f4) / 2.0F;
            arrayOfFloat[1] += (f9 + f5) / 2.0F;
            break;
          }
          f4 = f8;
          f5 = f9;
        }
      }
      switch (m)
      {
      case 0: 
        if ((arrayOfFloat[0] < 1.7014117E38F) && (arrayOfFloat[0] > -1.7014117E38F) && (arrayOfFloat[1] < 1.7014117E38F) && (arrayOfFloat[1] > -1.7014117E38F))
        {
          f1 = arrayOfFloat[0];
          f2 = arrayOfFloat[1];
          paramPathConsumer.beginSubpath(f1, f2);
          k = 1;
          j = 0;
        }
        else
        {
          j = 1;
        }
        break;
      case 1: 
        if ((arrayOfFloat[0] < 1.7014117E38F) && (arrayOfFloat[0] > -1.7014117E38F) && (arrayOfFloat[1] < 1.7014117E38F) && (arrayOfFloat[1] > -1.7014117E38F)) {
          if (j != 0)
          {
            paramPathConsumer.beginSubpath(arrayOfFloat[0], arrayOfFloat[1]);
            k = 1;
            j = 0;
          }
          else
          {
            paramPathConsumer.appendLine(arrayOfFloat[0], arrayOfFloat[1]);
          }
        }
        break;
      case 2: 
        if ((arrayOfFloat[2] < 1.7014117E38F) && (arrayOfFloat[2] > -1.7014117E38F) && (arrayOfFloat[3] < 1.7014117E38F) && (arrayOfFloat[3] > -1.7014117E38F)) {
          if (j != 0)
          {
            paramPathConsumer.beginSubpath(arrayOfFloat[2], arrayOfFloat[3]);
            k = 1;
            j = 0;
          }
          else if ((arrayOfFloat[0] < 1.7014117E38F) && (arrayOfFloat[0] > -1.7014117E38F) && (arrayOfFloat[1] < 1.7014117E38F) && (arrayOfFloat[1] > -1.7014117E38F))
          {
            paramPathConsumer.appendQuadratic(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
          }
          else
          {
            paramPathConsumer.appendLine(arrayOfFloat[2], arrayOfFloat[3]);
          }
        }
        break;
      case 3: 
        if ((arrayOfFloat[4] < 1.7014117E38F) && (arrayOfFloat[4] > -1.7014117E38F) && (arrayOfFloat[5] < 1.7014117E38F) && (arrayOfFloat[5] > -1.7014117E38F)) {
          if (j != 0)
          {
            paramPathConsumer.beginSubpath(arrayOfFloat[4], arrayOfFloat[5]);
            k = 1;
            j = 0;
          }
          else if ((arrayOfFloat[0] < 1.7014117E38F) && (arrayOfFloat[0] > -1.7014117E38F) && (arrayOfFloat[1] < 1.7014117E38F) && (arrayOfFloat[1] > -1.7014117E38F) && (arrayOfFloat[2] < 1.7014117E38F) && (arrayOfFloat[2] > -1.7014117E38F) && (arrayOfFloat[3] < 1.7014117E38F) && (arrayOfFloat[3] > -1.7014117E38F))
          {
            paramPathConsumer.appendCubic(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
          }
          else
          {
            paramPathConsumer.appendLine(arrayOfFloat[4], arrayOfFloat[5]);
          }
        }
        break;
      case 4: 
        if (k != 0)
        {
          paramPathConsumer.closedSubpath();
          k = 0;
          i = 1;
        }
        break;
      }
      paramPathIterator.next();
    }
    paramPathConsumer.endPath();
  }
  
  public static synchronized Rasterizer getRasterizer()
  {
    Rasterizer localRasterizer = theRasterizer;
    if (localRasterizer == null) {
      localRasterizer = new Rasterizer();
    } else {
      theRasterizer = null;
    }
    return localRasterizer;
  }
  
  public static synchronized void dropRasterizer(Rasterizer paramRasterizer)
  {
    paramRasterizer.reset();
    theRasterizer = paramRasterizer;
  }
  
  public float getMinimumAAPenSize()
  {
    return 0.19999999F;
  }
  
  public AATileGenerator getAATileGenerator(Shape paramShape, AffineTransform paramAffineTransform, Region paramRegion, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt)
  {
    Rasterizer localRasterizer = getRasterizer();
    PathIterator localPathIterator = paramShape.getPathIterator(paramAffineTransform);
    if (paramBasicStroke != null)
    {
      float[] arrayOfFloat1 = null;
      localRasterizer.setUsage(3);
      if (paramBoolean1)
      {
        localRasterizer.setPenDiameter(0.19999999F);
      }
      else
      {
        localRasterizer.setPenDiameter(paramBasicStroke.getLineWidth());
        if (paramAffineTransform != null)
        {
          arrayOfFloat1 = getTransformMatrix(paramAffineTransform);
          localRasterizer.setPenT4(arrayOfFloat1);
        }
        localRasterizer.setPenFitting(0.01F, 20);
      }
      localRasterizer.setCaps(RasterizerCaps[paramBasicStroke.getEndCap()]);
      localRasterizer.setCorners(RasterizerCorners[paramBasicStroke.getLineJoin()], paramBasicStroke.getMiterLimit());
      float[] arrayOfFloat2 = paramBasicStroke.getDashArray();
      if (arrayOfFloat2 != null)
      {
        localRasterizer.setDash(arrayOfFloat2, paramBasicStroke.getDashPhase());
        if ((paramAffineTransform != null) && (arrayOfFloat1 == null)) {
          arrayOfFloat1 = getTransformMatrix(paramAffineTransform);
        }
        localRasterizer.setDashT4(arrayOfFloat1);
      }
    }
    else
    {
      localRasterizer.setUsage(localPathIterator.getWindingRule() == 0 ? 1 : 2);
    }
    localRasterizer.beginPath();
    int i = 0;
    int j = 0;
    int k = 0;
    float f1 = 0.0F;
    float f2 = 0.0F;
    float[] arrayOfFloat3 = new float[6];
    float f3 = 0.0F;
    float f4 = 0.0F;
    while (!localPathIterator.isDone())
    {
      int m = localPathIterator.currentSegment(arrayOfFloat3);
      if (i == 1)
      {
        i = 0;
        if (m != 0)
        {
          localRasterizer.beginSubpath(f1, f2);
          k = 1;
        }
      }
      if (paramBoolean2)
      {
        int n;
        switch (m)
        {
        case 3: 
          n = 4;
          break;
        case 2: 
          n = 2;
          break;
        case 0: 
        case 1: 
          n = 0;
          break;
        case 4: 
        default: 
          n = -1;
        }
        if (n >= 0)
        {
          float f5 = arrayOfFloat3[n];
          float f6 = arrayOfFloat3[(n + 1)];
          float f7 = (float)Math.floor(f5) + 0.5F;
          float f8 = (float)Math.floor(f6) + 0.5F;
          arrayOfFloat3[n] = f7;
          arrayOfFloat3[(n + 1)] = f8;
          f7 -= f5;
          f8 -= f6;
          switch (m)
          {
          case 3: 
            arrayOfFloat3[0] += f3;
            arrayOfFloat3[1] += f4;
            arrayOfFloat3[2] += f7;
            arrayOfFloat3[3] += f8;
            break;
          case 2: 
            arrayOfFloat3[0] += (f7 + f3) / 2.0F;
            arrayOfFloat3[1] += (f8 + f4) / 2.0F;
            break;
          }
          f3 = f7;
          f4 = f8;
        }
      }
      switch (m)
      {
      case 0: 
        if ((arrayOfFloat3[0] < 1.7014117E38F) && (arrayOfFloat3[0] > -1.7014117E38F) && (arrayOfFloat3[1] < 1.7014117E38F) && (arrayOfFloat3[1] > -1.7014117E38F))
        {
          f1 = arrayOfFloat3[0];
          f2 = arrayOfFloat3[1];
          localRasterizer.beginSubpath(f1, f2);
          k = 1;
          j = 0;
        }
        else
        {
          j = 1;
        }
        break;
      case 1: 
        if ((arrayOfFloat3[0] < 1.7014117E38F) && (arrayOfFloat3[0] > -1.7014117E38F) && (arrayOfFloat3[1] < 1.7014117E38F) && (arrayOfFloat3[1] > -1.7014117E38F)) {
          if (j != 0)
          {
            localRasterizer.beginSubpath(arrayOfFloat3[0], arrayOfFloat3[1]);
            k = 1;
            j = 0;
          }
          else
          {
            localRasterizer.appendLine(arrayOfFloat3[0], arrayOfFloat3[1]);
          }
        }
        break;
      case 2: 
        if ((arrayOfFloat3[2] < 1.7014117E38F) && (arrayOfFloat3[2] > -1.7014117E38F) && (arrayOfFloat3[3] < 1.7014117E38F) && (arrayOfFloat3[3] > -1.7014117E38F)) {
          if (j != 0)
          {
            localRasterizer.beginSubpath(arrayOfFloat3[2], arrayOfFloat3[3]);
            k = 1;
            j = 0;
          }
          else if ((arrayOfFloat3[0] < 1.7014117E38F) && (arrayOfFloat3[0] > -1.7014117E38F) && (arrayOfFloat3[1] < 1.7014117E38F) && (arrayOfFloat3[1] > -1.7014117E38F))
          {
            localRasterizer.appendQuadratic(arrayOfFloat3[0], arrayOfFloat3[1], arrayOfFloat3[2], arrayOfFloat3[3]);
          }
          else
          {
            localRasterizer.appendLine(arrayOfFloat3[2], arrayOfFloat3[3]);
          }
        }
        break;
      case 3: 
        if ((arrayOfFloat3[4] < 1.7014117E38F) && (arrayOfFloat3[4] > -1.7014117E38F) && (arrayOfFloat3[5] < 1.7014117E38F) && (arrayOfFloat3[5] > -1.7014117E38F)) {
          if (j != 0)
          {
            localRasterizer.beginSubpath(arrayOfFloat3[4], arrayOfFloat3[5]);
            k = 1;
            j = 0;
          }
          else if ((arrayOfFloat3[0] < 1.7014117E38F) && (arrayOfFloat3[0] > -1.7014117E38F) && (arrayOfFloat3[1] < 1.7014117E38F) && (arrayOfFloat3[1] > -1.7014117E38F) && (arrayOfFloat3[2] < 1.7014117E38F) && (arrayOfFloat3[2] > -1.7014117E38F) && (arrayOfFloat3[3] < 1.7014117E38F) && (arrayOfFloat3[3] > -1.7014117E38F))
          {
            localRasterizer.appendCubic(arrayOfFloat3[0], arrayOfFloat3[1], arrayOfFloat3[2], arrayOfFloat3[3], arrayOfFloat3[4], arrayOfFloat3[5]);
          }
          else
          {
            localRasterizer.appendLine(arrayOfFloat3[4], arrayOfFloat3[5]);
          }
        }
        break;
      case 4: 
        if (k != 0)
        {
          localRasterizer.closedSubpath();
          k = 0;
          i = 1;
        }
        break;
      }
      localPathIterator.next();
    }
    try
    {
      localRasterizer.endPath();
      localRasterizer.getAlphaBox(paramArrayOfInt);
      paramRegion.clipBoxToBounds(paramArrayOfInt);
      if ((paramArrayOfInt[0] >= paramArrayOfInt[2]) || (paramArrayOfInt[1] >= paramArrayOfInt[3]))
      {
        dropRasterizer(localRasterizer);
        return null;
      }
      localRasterizer.setOutputArea(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2] - paramArrayOfInt[0], paramArrayOfInt[3] - paramArrayOfInt[1]);
    }
    catch (PRException localPRException)
    {
      System.err.println("DuctusRenderingEngine.getAATileGenerator: " + localPRException);
    }
    return localRasterizer;
  }
  
  public AATileGenerator getAATileGenerator(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, Region paramRegion, int[] paramArrayOfInt)
  {
    int i = (paramDouble7 > 0.0D) && (paramDouble8 > 0.0D) ? 1 : 0;
    double d1;
    double d2;
    double d3;
    double d4;
    if (i != 0)
    {
      d1 = paramDouble3 * paramDouble7;
      d2 = paramDouble4 * paramDouble7;
      d3 = paramDouble5 * paramDouble8;
      d4 = paramDouble6 * paramDouble8;
      paramDouble1 -= (d1 + d3) / 2.0D;
      paramDouble2 -= (d2 + d4) / 2.0D;
      paramDouble3 += d1;
      paramDouble4 += d2;
      paramDouble5 += d3;
      paramDouble6 += d4;
      if ((paramDouble7 > 1.0D) && (paramDouble8 > 1.0D)) {
        i = 0;
      }
    }
    else
    {
      d1 = d2 = d3 = d4 = 0.0D;
    }
    Rasterizer localRasterizer = getRasterizer();
    localRasterizer.setUsage(1);
    localRasterizer.beginPath();
    localRasterizer.beginSubpath((float)paramDouble1, (float)paramDouble2);
    localRasterizer.appendLine((float)(paramDouble1 + paramDouble3), (float)(paramDouble2 + paramDouble4));
    localRasterizer.appendLine((float)(paramDouble1 + paramDouble3 + paramDouble5), (float)(paramDouble2 + paramDouble4 + paramDouble6));
    localRasterizer.appendLine((float)(paramDouble1 + paramDouble5), (float)(paramDouble2 + paramDouble6));
    localRasterizer.closedSubpath();
    if (i != 0)
    {
      paramDouble1 += d1 + d3;
      paramDouble2 += d2 + d4;
      paramDouble3 -= 2.0D * d1;
      paramDouble4 -= 2.0D * d2;
      paramDouble5 -= 2.0D * d3;
      paramDouble6 -= 2.0D * d4;
      localRasterizer.beginSubpath((float)paramDouble1, (float)paramDouble2);
      localRasterizer.appendLine((float)(paramDouble1 + paramDouble3), (float)(paramDouble2 + paramDouble4));
      localRasterizer.appendLine((float)(paramDouble1 + paramDouble3 + paramDouble5), (float)(paramDouble2 + paramDouble4 + paramDouble6));
      localRasterizer.appendLine((float)(paramDouble1 + paramDouble5), (float)(paramDouble2 + paramDouble6));
      localRasterizer.closedSubpath();
    }
    try
    {
      localRasterizer.endPath();
      localRasterizer.getAlphaBox(paramArrayOfInt);
      paramRegion.clipBoxToBounds(paramArrayOfInt);
      if ((paramArrayOfInt[0] >= paramArrayOfInt[2]) || (paramArrayOfInt[1] >= paramArrayOfInt[3]))
      {
        dropRasterizer(localRasterizer);
        return null;
      }
      localRasterizer.setOutputArea(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2] - paramArrayOfInt[0], paramArrayOfInt[3] - paramArrayOfInt[1]);
    }
    catch (PRException localPRException)
    {
      System.err.println("DuctusRenderingEngine.getAATileGenerator: " + localPRException);
    }
    return localRasterizer;
  }
  
  private void feedConsumer(PathConsumer paramPathConsumer, PathIterator paramPathIterator)
  {
    try
    {
      paramPathConsumer.beginPath();
      int i = 0;
      float f1 = 0.0F;
      float f2 = 0.0F;
      float[] arrayOfFloat = new float[6];
      while (!paramPathIterator.isDone())
      {
        int j = paramPathIterator.currentSegment(arrayOfFloat);
        if (i == 1)
        {
          i = 0;
          if (j != 0) {
            paramPathConsumer.beginSubpath(f1, f2);
          }
        }
        switch (j)
        {
        case 0: 
          f1 = arrayOfFloat[0];
          f2 = arrayOfFloat[1];
          paramPathConsumer.beginSubpath(arrayOfFloat[0], arrayOfFloat[1]);
          break;
        case 1: 
          paramPathConsumer.appendLine(arrayOfFloat[0], arrayOfFloat[1]);
          break;
        case 2: 
          paramPathConsumer.appendQuadratic(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
          break;
        case 3: 
          paramPathConsumer.appendCubic(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
          break;
        case 4: 
          paramPathConsumer.closedSubpath();
          i = 1;
        }
        paramPathIterator.next();
      }
      paramPathConsumer.endPath();
    }
    catch (PathException localPathException)
    {
      throw new InternalError("Unable to Stroke shape (" + localPathException.getMessage() + ")", localPathException);
    }
  }
  
  private class FillAdapter
    implements PathConsumer
  {
    boolean closed;
    Path2D.Float path = new Path2D.Float(1);
    
    public FillAdapter() {}
    
    public Shape getShape()
    {
      return this.path;
    }
    
    public void dispose() {}
    
    public PathConsumer getConsumer()
    {
      return null;
    }
    
    public void beginPath() {}
    
    public void beginSubpath(float paramFloat1, float paramFloat2)
    {
      if (this.closed)
      {
        this.path.closePath();
        this.closed = false;
      }
      this.path.moveTo(paramFloat1, paramFloat2);
    }
    
    public void appendLine(float paramFloat1, float paramFloat2)
    {
      this.path.lineTo(paramFloat1, paramFloat2);
    }
    
    public void appendQuadratic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      this.path.quadTo(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    }
    
    public void appendCubic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    {
      this.path.curveTo(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
    }
    
    public void closedSubpath()
    {
      this.closed = true;
    }
    
    public void endPath()
    {
      if (this.closed)
      {
        this.path.closePath();
        this.closed = false;
      }
    }
    
    public void useProxy(FastPathProducer paramFastPathProducer)
      throws PathException
    {
      paramFastPathProducer.sendTo(this);
    }
    
    public long getCPathConsumer()
    {
      return 0L;
    }
  }
}
