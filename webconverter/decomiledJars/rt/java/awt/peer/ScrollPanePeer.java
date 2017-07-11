package java.awt.peer;

import java.awt.Adjustable;

public abstract interface ScrollPanePeer
  extends ContainerPeer
{
  public abstract int getHScrollbarHeight();
  
  public abstract int getVScrollbarWidth();
  
  public abstract void setScrollPosition(int paramInt1, int paramInt2);
  
  public abstract void childResized(int paramInt1, int paramInt2);
  
  public abstract void setUnitIncrement(Adjustable paramAdjustable, int paramInt);
  
  public abstract void setValue(Adjustable paramAdjustable, int paramInt);
}
