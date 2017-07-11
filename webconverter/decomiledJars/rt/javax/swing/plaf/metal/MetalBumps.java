package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import sun.awt.AppContext;

class MetalBumps
  implements Icon
{
  static final Color ALPHA = new Color(0, 0, 0, 0);
  protected int xBumps;
  protected int yBumps;
  protected Color topColor;
  protected Color shadowColor;
  protected Color backColor;
  private static final Object METAL_BUMPS = new Object();
  protected BumpBuffer buffer;
  
  public MetalBumps(int paramInt1, int paramInt2, Color paramColor1, Color paramColor2, Color paramColor3)
  {
    setBumpArea(paramInt1, paramInt2);
    setBumpColors(paramColor1, paramColor2, paramColor3);
  }
  
  private static BumpBuffer createBuffer(GraphicsConfiguration paramGraphicsConfiguration, Color paramColor1, Color paramColor2, Color paramColor3)
  {
    AppContext localAppContext = AppContext.getAppContext();
    Object localObject1 = (List)localAppContext.get(METAL_BUMPS);
    if (localObject1 == null)
    {
      localObject1 = new ArrayList();
      localAppContext.put(METAL_BUMPS, localObject1);
    }
    Object localObject2 = ((List)localObject1).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      BumpBuffer localBumpBuffer = (BumpBuffer)((Iterator)localObject2).next();
      if (localBumpBuffer.hasSameConfiguration(paramGraphicsConfiguration, paramColor1, paramColor2, paramColor3)) {
        return localBumpBuffer;
      }
    }
    localObject2 = new BumpBuffer(paramGraphicsConfiguration, paramColor1, paramColor2, paramColor3);
    ((List)localObject1).add(localObject2);
    return localObject2;
  }
  
  public void setBumpArea(Dimension paramDimension)
  {
    setBumpArea(paramDimension.width, paramDimension.height);
  }
  
  public void setBumpArea(int paramInt1, int paramInt2)
  {
    this.xBumps = (paramInt1 / 2);
    this.yBumps = (paramInt2 / 2);
  }
  
  public void setBumpColors(Color paramColor1, Color paramColor2, Color paramColor3)
  {
    this.topColor = paramColor1;
    this.shadowColor = paramColor2;
    if (paramColor3 == null) {
      this.backColor = ALPHA;
    } else {
      this.backColor = paramColor3;
    }
  }
  
  public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    GraphicsConfiguration localGraphicsConfiguration = (paramGraphics instanceof Graphics2D) ? ((Graphics2D)paramGraphics).getDeviceConfiguration() : null;
    if ((this.buffer == null) || (!this.buffer.hasSameConfiguration(localGraphicsConfiguration, this.topColor, this.shadowColor, this.backColor))) {
      this.buffer = createBuffer(localGraphicsConfiguration, this.topColor, this.shadowColor, this.backColor);
    }
    int i = 64;
    int j = 64;
    int k = getIconWidth();
    int m = getIconHeight();
    int n = paramInt1 + k;
    int i1 = paramInt2 + m;
    int i2 = paramInt1;
    while (paramInt2 < i1)
    {
      int i3 = Math.min(i1 - paramInt2, j);
      paramInt1 = i2;
      while (paramInt1 < n)
      {
        int i4 = Math.min(n - paramInt1, i);
        paramGraphics.drawImage(this.buffer.getImage(), paramInt1, paramInt2, paramInt1 + i4, paramInt2 + i3, 0, 0, i4, i3, null);
        paramInt1 += i;
      }
      paramInt2 += j;
    }
  }
  
  public int getIconWidth()
  {
    return this.xBumps * 2;
  }
  
  public int getIconHeight()
  {
    return this.yBumps * 2;
  }
}
