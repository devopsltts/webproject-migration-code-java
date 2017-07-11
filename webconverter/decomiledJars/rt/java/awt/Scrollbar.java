package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollbarPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;

public class Scrollbar
  extends Component
  implements Adjustable, Accessible
{
  public static final int HORIZONTAL = 0;
  public static final int VERTICAL = 1;
  int value;
  int maximum;
  int minimum;
  int visibleAmount;
  int orientation;
  int lineIncrement = 1;
  int pageIncrement = 10;
  transient boolean isAdjusting;
  transient AdjustmentListener adjustmentListener;
  private static final String base = "scrollbar";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 8451667562882310543L;
  private int scrollbarSerializedDataVersion = 1;
  
  private static native void initIDs();
  
  public Scrollbar()
    throws HeadlessException
  {
    this(1, 0, 10, 0, 100);
  }
  
  public Scrollbar(int paramInt)
    throws HeadlessException
  {
    this(paramInt, 0, 10, 0, 100);
  }
  
  public Scrollbar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    switch (paramInt1)
    {
    case 0: 
    case 1: 
      this.orientation = paramInt1;
      break;
    default: 
      throw new IllegalArgumentException("illegal scrollbar orientation");
    }
    setValues(paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  String constructComponentName()
  {
    synchronized (Scrollbar.class)
    {
      return "scrollbar" + nameCounter++;
    }
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if (this.peer == null) {
        this.peer = getToolkit().createScrollbar(this);
      }
      super.addNotify();
    }
  }
  
  public int getOrientation()
  {
    return this.orientation;
  }
  
  public void setOrientation(int paramInt)
  {
    synchronized (getTreeLock())
    {
      if (paramInt == this.orientation) {
        return;
      }
      switch (paramInt)
      {
      case 0: 
      case 1: 
        this.orientation = paramInt;
        break;
      default: 
        throw new IllegalArgumentException("illegal scrollbar orientation");
      }
      if (this.peer != null)
      {
        removeNotify();
        addNotify();
        invalidate();
      }
    }
    if (this.accessibleContext != null) {
      this.accessibleContext.firePropertyChange("AccessibleState", paramInt == 1 ? AccessibleState.HORIZONTAL : AccessibleState.VERTICAL, paramInt == 1 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL);
    }
  }
  
  public int getValue()
  {
    return this.value;
  }
  
  public void setValue(int paramInt)
  {
    setValues(paramInt, this.visibleAmount, this.minimum, this.maximum);
  }
  
  public int getMinimum()
  {
    return this.minimum;
  }
  
  public void setMinimum(int paramInt)
  {
    setValues(this.value, this.visibleAmount, paramInt, this.maximum);
  }
  
  public int getMaximum()
  {
    return this.maximum;
  }
  
  public void setMaximum(int paramInt)
  {
    if (paramInt == Integer.MIN_VALUE) {
      paramInt = -2147483647;
    }
    if (this.minimum >= paramInt) {
      this.minimum = (paramInt - 1);
    }
    setValues(this.value, this.visibleAmount, this.minimum, paramInt);
  }
  
  public int getVisibleAmount()
  {
    return getVisible();
  }
  
  @Deprecated
  public int getVisible()
  {
    return this.visibleAmount;
  }
  
  public void setVisibleAmount(int paramInt)
  {
    setValues(this.value, paramInt, this.minimum, this.maximum);
  }
  
  public void setUnitIncrement(int paramInt)
  {
    setLineIncrement(paramInt);
  }
  
  @Deprecated
  public synchronized void setLineIncrement(int paramInt)
  {
    int i = paramInt < 1 ? 1 : paramInt;
    if (this.lineIncrement == i) {
      return;
    }
    this.lineIncrement = i;
    ScrollbarPeer localScrollbarPeer = (ScrollbarPeer)this.peer;
    if (localScrollbarPeer != null) {
      localScrollbarPeer.setLineIncrement(this.lineIncrement);
    }
  }
  
  public int getUnitIncrement()
  {
    return getLineIncrement();
  }
  
  @Deprecated
  public int getLineIncrement()
  {
    return this.lineIncrement;
  }
  
  public void setBlockIncrement(int paramInt)
  {
    setPageIncrement(paramInt);
  }
  
  @Deprecated
  public synchronized void setPageIncrement(int paramInt)
  {
    int i = paramInt < 1 ? 1 : paramInt;
    if (this.pageIncrement == i) {
      return;
    }
    this.pageIncrement = i;
    ScrollbarPeer localScrollbarPeer = (ScrollbarPeer)this.peer;
    if (localScrollbarPeer != null) {
      localScrollbarPeer.setPageIncrement(this.pageIncrement);
    }
  }
  
  public int getBlockIncrement()
  {
    return getPageIncrement();
  }
  
  @Deprecated
  public int getPageIncrement()
  {
    return this.pageIncrement;
  }
  
  public void setValues(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    synchronized (this)
    {
      if (paramInt3 == Integer.MAX_VALUE) {
        paramInt3 = 2147483646;
      }
      if (paramInt4 <= paramInt3) {
        paramInt4 = paramInt3 + 1;
      }
      long l = paramInt4 - paramInt3;
      if (l > 2147483647L)
      {
        l = 2147483647L;
        paramInt4 = paramInt3 + (int)l;
      }
      if (paramInt2 > (int)l) {
        paramInt2 = (int)l;
      }
      if (paramInt2 < 1) {
        paramInt2 = 1;
      }
      if (paramInt1 < paramInt3) {
        paramInt1 = paramInt3;
      }
      if (paramInt1 > paramInt4 - paramInt2) {
        paramInt1 = paramInt4 - paramInt2;
      }
      i = this.value;
      this.value = paramInt1;
      this.visibleAmount = paramInt2;
      this.minimum = paramInt3;
      this.maximum = paramInt4;
      ScrollbarPeer localScrollbarPeer = (ScrollbarPeer)this.peer;
      if (localScrollbarPeer != null) {
        localScrollbarPeer.setValues(paramInt1, this.visibleAmount, paramInt3, paramInt4);
      }
    }
    if ((i != paramInt1) && (this.accessibleContext != null)) {
      this.accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(i), Integer.valueOf(paramInt1));
    }
  }
  
  public boolean getValueIsAdjusting()
  {
    return this.isAdjusting;
  }
  
  public void setValueIsAdjusting(boolean paramBoolean)
  {
    boolean bool;
    synchronized (this)
    {
      bool = this.isAdjusting;
      this.isAdjusting = paramBoolean;
    }
    if ((bool != paramBoolean) && (this.accessibleContext != null)) {
      this.accessibleContext.firePropertyChange("AccessibleState", bool ? AccessibleState.BUSY : null, paramBoolean ? AccessibleState.BUSY : null);
    }
  }
  
  public synchronized void addAdjustmentListener(AdjustmentListener paramAdjustmentListener)
  {
    if (paramAdjustmentListener == null) {
      return;
    }
    this.adjustmentListener = AWTEventMulticaster.add(this.adjustmentListener, paramAdjustmentListener);
    this.newEventsOnly = true;
  }
  
  public synchronized void removeAdjustmentListener(AdjustmentListener paramAdjustmentListener)
  {
    if (paramAdjustmentListener == null) {
      return;
    }
    this.adjustmentListener = AWTEventMulticaster.remove(this.adjustmentListener, paramAdjustmentListener);
  }
  
  public synchronized AdjustmentListener[] getAdjustmentListeners()
  {
    return (AdjustmentListener[])getListeners(AdjustmentListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    AdjustmentListener localAdjustmentListener = null;
    if (paramClass == AdjustmentListener.class) {
      localAdjustmentListener = this.adjustmentListener;
    } else {
      return super.getListeners(paramClass);
    }
    return AWTEventMulticaster.getListeners(localAdjustmentListener, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    if (paramAWTEvent.id == 601) {
      return ((this.eventMask & 0x100) != 0L) || (this.adjustmentListener != null);
    }
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof AdjustmentEvent))
    {
      processAdjustmentEvent((AdjustmentEvent)paramAWTEvent);
      return;
    }
    super.processEvent(paramAWTEvent);
  }
  
  protected void processAdjustmentEvent(AdjustmentEvent paramAdjustmentEvent)
  {
    AdjustmentListener localAdjustmentListener = this.adjustmentListener;
    if (localAdjustmentListener != null) {
      localAdjustmentListener.adjustmentValueChanged(paramAdjustmentEvent);
    }
  }
  
  protected String paramString()
  {
    return super.paramString() + ",val=" + this.value + ",vis=" + this.visibleAmount + ",min=" + this.minimum + ",max=" + this.maximum + (this.orientation == 1 ? ",vert" : ",horz") + ",isAdjusting=" + this.isAdjusting;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "adjustmentL", this.adjustmentListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    paramObjectInputStream.defaultReadObject();
    Object localObject;
    while (null != (localObject = paramObjectInputStream.readObject()))
    {
      String str = ((String)localObject).intern();
      if ("adjustmentL" == str) {
        addAdjustmentListener((AdjustmentListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleAWTScrollBar();
    }
    return this.accessibleContext;
  }
  
  static
  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
  
  protected class AccessibleAWTScrollBar
    extends Component.AccessibleAWTComponent
    implements AccessibleValue
  {
    private static final long serialVersionUID = -344337268523697807L;
    
    protected AccessibleAWTScrollBar()
    {
      super();
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (Scrollbar.this.getValueIsAdjusting()) {
        localAccessibleStateSet.add(AccessibleState.BUSY);
      }
      if (Scrollbar.this.getOrientation() == 1) {
        localAccessibleStateSet.add(AccessibleState.VERTICAL);
      } else {
        localAccessibleStateSet.add(AccessibleState.HORIZONTAL);
      }
      return localAccessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.SCROLL_BAR;
    }
    
    public AccessibleValue getAccessibleValue()
    {
      return this;
    }
    
    public Number getCurrentAccessibleValue()
    {
      return Integer.valueOf(Scrollbar.this.getValue());
    }
    
    public boolean setCurrentAccessibleValue(Number paramNumber)
    {
      if ((paramNumber instanceof Integer))
      {
        Scrollbar.this.setValue(paramNumber.intValue());
        return true;
      }
      return false;
    }
    
    public Number getMinimumAccessibleValue()
    {
      return Integer.valueOf(Scrollbar.this.getMinimum());
    }
    
    public Number getMaximumAccessibleValue()
    {
      return Integer.valueOf(Scrollbar.this.getMaximum());
    }
  }
}
