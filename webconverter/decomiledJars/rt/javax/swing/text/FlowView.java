package javax.swing.text;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.event.DocumentEvent.EventType;

public abstract class FlowView
  extends BoxView
{
  protected int layoutSpan = Integer.MAX_VALUE;
  protected View layoutPool;
  protected FlowStrategy strategy = new FlowStrategy();
  
  public FlowView(Element paramElement, int paramInt)
  {
    super(paramElement, paramInt);
  }
  
  public int getFlowAxis()
  {
    if (getAxis() == 1) {
      return 0;
    }
    return 1;
  }
  
  public int getFlowSpan(int paramInt)
  {
    return this.layoutSpan;
  }
  
  public int getFlowStart(int paramInt)
  {
    return 0;
  }
  
  protected abstract View createRow();
  
  protected void loadChildren(ViewFactory paramViewFactory)
  {
    if (this.layoutPool == null) {
      this.layoutPool = new LogicalView(getElement());
    }
    this.layoutPool.setParent(this);
    this.strategy.insertUpdate(this, null, null);
  }
  
  protected int getViewIndexAtPosition(int paramInt)
  {
    if ((paramInt >= getStartOffset()) && (paramInt < getEndOffset())) {
      for (int i = 0; i < getViewCount(); i++)
      {
        View localView = getView(i);
        if ((paramInt >= localView.getStartOffset()) && (paramInt < localView.getEndOffset())) {
          return i;
        }
      }
    }
    return -1;
  }
  
  protected void layout(int paramInt1, int paramInt2)
  {
    int i = getFlowAxis();
    int j;
    if (i == 0) {
      j = paramInt1;
    } else {
      j = paramInt2;
    }
    if (this.layoutSpan != j)
    {
      layoutChanged(i);
      layoutChanged(getAxis());
      this.layoutSpan = j;
    }
    if (!isLayoutValid(i))
    {
      int k = getAxis();
      int m = k == 0 ? getWidth() : getHeight();
      this.strategy.layout(this);
      int n = (int)getPreferredSpan(k);
      if (m != n)
      {
        View localView = getParent();
        if (localView != null) {
          localView.preferenceChanged(this, k == 0, k == 1);
        }
        Container localContainer = getContainer();
        if (localContainer != null) {
          localContainer.repaint();
        }
      }
    }
    super.layout(paramInt1, paramInt2);
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
  {
    if (paramSizeRequirements == null) {
      paramSizeRequirements = new SizeRequirements();
    }
    float f1 = this.layoutPool.getPreferredSpan(paramInt);
    float f2 = this.layoutPool.getMinimumSpan(paramInt);
    paramSizeRequirements.minimum = ((int)f2);
    paramSizeRequirements.preferred = Math.max(paramSizeRequirements.minimum, (int)f1);
    paramSizeRequirements.maximum = Integer.MAX_VALUE;
    paramSizeRequirements.alignment = 0.5F;
    return paramSizeRequirements;
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    this.layoutPool.insertUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    this.strategy.insertUpdate(this, paramDocumentEvent, getInsideAllocation(paramShape));
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    this.layoutPool.removeUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    this.strategy.removeUpdate(this, paramDocumentEvent, getInsideAllocation(paramShape));
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    this.layoutPool.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    this.strategy.changedUpdate(this, paramDocumentEvent, getInsideAllocation(paramShape));
  }
  
  public void setParent(View paramView)
  {
    super.setParent(paramView);
    if ((paramView == null) && (this.layoutPool != null)) {
      this.layoutPool.setParent(null);
    }
  }
  
  public static class FlowStrategy
  {
    Position damageStart = null;
    Vector<View> viewBuffer;
    
    public FlowStrategy() {}
    
    void addDamage(FlowView paramFlowView, int paramInt)
    {
      if ((paramInt >= paramFlowView.getStartOffset()) && (paramInt < paramFlowView.getEndOffset()) && ((this.damageStart == null) || (paramInt < this.damageStart.getOffset()))) {
        try
        {
          this.damageStart = paramFlowView.getDocument().createPosition(paramInt);
        }
        catch (BadLocationException localBadLocationException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
        }
      }
    }
    
    void unsetDamage()
    {
      this.damageStart = null;
    }
    
    public void insertUpdate(FlowView paramFlowView, DocumentEvent paramDocumentEvent, Rectangle paramRectangle)
    {
      if (paramDocumentEvent != null) {
        addDamage(paramFlowView, paramDocumentEvent.getOffset());
      }
      if (paramRectangle != null)
      {
        Container localContainer = paramFlowView.getContainer();
        if (localContainer != null) {
          localContainer.repaint(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
        }
      }
      else
      {
        paramFlowView.preferenceChanged(null, true, true);
      }
    }
    
    public void removeUpdate(FlowView paramFlowView, DocumentEvent paramDocumentEvent, Rectangle paramRectangle)
    {
      addDamage(paramFlowView, paramDocumentEvent.getOffset());
      if (paramRectangle != null)
      {
        Container localContainer = paramFlowView.getContainer();
        if (localContainer != null) {
          localContainer.repaint(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
        }
      }
      else
      {
        paramFlowView.preferenceChanged(null, true, true);
      }
    }
    
    public void changedUpdate(FlowView paramFlowView, DocumentEvent paramDocumentEvent, Rectangle paramRectangle)
    {
      addDamage(paramFlowView, paramDocumentEvent.getOffset());
      if (paramRectangle != null)
      {
        Container localContainer = paramFlowView.getContainer();
        if (localContainer != null) {
          localContainer.repaint(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
        }
      }
      else
      {
        paramFlowView.preferenceChanged(null, true, true);
      }
    }
    
    protected View getLogicalView(FlowView paramFlowView)
    {
      return paramFlowView.layoutPool;
    }
    
    public void layout(FlowView paramFlowView)
    {
      View localView1 = getLogicalView(paramFlowView);
      int k = paramFlowView.getEndOffset();
      int i;
      int j;
      if (paramFlowView.majorAllocValid)
      {
        if (this.damageStart == null) {
          return;
        }
        for (m = this.damageStart.getOffset(); (i = paramFlowView.getViewIndexAtPosition(m)) < 0; m--) {}
        if (i > 0) {
          i--;
        }
        j = paramFlowView.getView(i).getStartOffset();
      }
      else
      {
        i = 0;
        j = paramFlowView.getStartOffset();
      }
      reparentViews(localView1, j);
      this.viewBuffer = new Vector(10, 10);
      int m = paramFlowView.getViewCount();
      while (j < k)
      {
        View localView2;
        if (i >= m)
        {
          localView2 = paramFlowView.createRow();
          paramFlowView.append(localView2);
        }
        else
        {
          localView2 = paramFlowView.getView(i);
        }
        j = layoutRow(paramFlowView, i, j);
        i++;
      }
      this.viewBuffer = null;
      if (i < m) {
        paramFlowView.replace(i, m - i, null);
      }
      unsetDamage();
    }
    
    protected int layoutRow(FlowView paramFlowView, int paramInt1, int paramInt2)
    {
      View localView1 = paramFlowView.getView(paramInt1);
      float f1 = paramFlowView.getFlowStart(paramInt1);
      float f2 = paramFlowView.getFlowSpan(paramInt1);
      int i = paramFlowView.getEndOffset();
      TabExpander localTabExpander = (paramFlowView instanceof TabExpander) ? (TabExpander)paramFlowView : null;
      int j = paramFlowView.getFlowAxis();
      int k = 0;
      float f3 = 0.0F;
      float f4 = 0.0F;
      int m = -1;
      int n = 0;
      this.viewBuffer.clear();
      while ((paramInt2 < i) && (f2 >= 0.0F))
      {
        localObject = createView(paramFlowView, paramInt2, (int)f2, paramInt1);
        if (localObject == null) {
          break;
        }
        int i1 = ((View)localObject).getBreakWeight(j, f1, f2);
        if (i1 >= 3000)
        {
          View localView2 = ((View)localObject).breakView(j, paramInt2, f1, f2);
          if (localView2 != null)
          {
            this.viewBuffer.add(localView2);
            break;
          }
          if (n != 0) {
            break;
          }
          this.viewBuffer.add(localObject);
          break;
        }
        if ((i1 >= k) && (i1 > 0))
        {
          k = i1;
          f3 = f1;
          f4 = f2;
          m = n;
        }
        float f5;
        if ((j == 0) && ((localObject instanceof TabableView))) {
          f5 = ((TabableView)localObject).getTabbedSpan(f1, localTabExpander);
        } else {
          f5 = ((View)localObject).getPreferredSpan(j);
        }
        if ((f5 > f2) && (m >= 0))
        {
          if (m < n) {
            localObject = (View)this.viewBuffer.get(m);
          }
          for (int i2 = n - 1; i2 >= m; i2--) {
            this.viewBuffer.remove(i2);
          }
          localObject = ((View)localObject).breakView(j, ((View)localObject).getStartOffset(), f3, f4);
        }
        f2 -= f5;
        f1 += f5;
        this.viewBuffer.add(localObject);
        paramInt2 = ((View)localObject).getEndOffset();
        n++;
      }
      Object localObject = new View[this.viewBuffer.size()];
      this.viewBuffer.toArray((Object[])localObject);
      localView1.replace(0, localView1.getViewCount(), (View[])localObject);
      return localObject.length > 0 ? localView1.getEndOffset() : paramInt2;
    }
    
    protected void adjustRow(FlowView paramFlowView, int paramInt1, int paramInt2, int paramInt3)
    {
      int i = paramFlowView.getFlowAxis();
      View localView1 = paramFlowView.getView(paramInt1);
      int j = localView1.getViewCount();
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = -1;
      for (int i2 = 0; i2 < j; i2++)
      {
        localView2 = localView1.getView(i2);
        int i3 = paramInt2 - k;
        int i4 = localView2.getBreakWeight(i, paramInt3 + k, i3);
        if ((i4 >= m) && (i4 > 0))
        {
          m = i4;
          i1 = i2;
          n = k;
          if (i4 >= 3000) {
            break;
          }
        }
        k = (int)(k + localView2.getPreferredSpan(i));
      }
      if (i1 < 0) {
        return;
      }
      i2 = paramInt2 - n;
      View localView2 = localView1.getView(i1);
      localView2 = localView2.breakView(i, localView2.getStartOffset(), paramInt3 + n, i2);
      View[] arrayOfView = new View[1];
      arrayOfView[0] = localView2;
      View localView3 = getLogicalView(paramFlowView);
      int i5 = localView1.getView(i1).getStartOffset();
      int i6 = localView1.getEndOffset();
      for (int i7 = 0; i7 < localView3.getViewCount(); i7++)
      {
        View localView4 = localView3.getView(i7);
        if (localView4.getEndOffset() > i6) {
          break;
        }
        if (localView4.getStartOffset() >= i5) {
          localView4.setParent(localView3);
        }
      }
      localView1.replace(i1, j - i1, arrayOfView);
    }
    
    void reparentViews(View paramView, int paramInt)
    {
      int i = paramView.getViewIndex(paramInt, Position.Bias.Forward);
      if (i >= 0) {
        for (int j = i; j < paramView.getViewCount(); j++) {
          paramView.getView(j).setParent(paramView);
        }
      }
    }
    
    protected View createView(FlowView paramFlowView, int paramInt1, int paramInt2, int paramInt3)
    {
      View localView1 = getLogicalView(paramFlowView);
      int i = localView1.getViewIndex(paramInt1, Position.Bias.Forward);
      View localView2 = localView1.getView(i);
      if (paramInt1 == localView2.getStartOffset()) {
        return localView2;
      }
      localView2 = localView2.createFragment(paramInt1, localView2.getEndOffset());
      return localView2;
    }
  }
  
  static class LogicalView
    extends CompositeView
  {
    LogicalView(Element paramElement)
    {
      super();
    }
    
    protected int getViewIndexAtPosition(int paramInt)
    {
      Element localElement = getElement();
      if (localElement.isLeaf()) {
        return 0;
      }
      return super.getViewIndexAtPosition(paramInt);
    }
    
    protected void loadChildren(ViewFactory paramViewFactory)
    {
      Element localElement = getElement();
      if (localElement.isLeaf())
      {
        LabelView localLabelView = new LabelView(localElement);
        append(localLabelView);
      }
      else
      {
        super.loadChildren(paramViewFactory);
      }
    }
    
    public AttributeSet getAttributes()
    {
      View localView = getParent();
      return localView != null ? localView.getAttributes() : null;
    }
    
    public float getPreferredSpan(int paramInt)
    {
      float f1 = 0.0F;
      float f2 = 0.0F;
      int i = getViewCount();
      for (int j = 0; j < i; j++)
      {
        View localView = getView(j);
        f2 += localView.getPreferredSpan(paramInt);
        if (localView.getBreakWeight(paramInt, 0.0F, 2.14748365E9F) >= 3000)
        {
          f1 = Math.max(f1, f2);
          f2 = 0.0F;
        }
      }
      f1 = Math.max(f1, f2);
      return f1;
    }
    
    public float getMinimumSpan(int paramInt)
    {
      float f1 = 0.0F;
      float f2 = 0.0F;
      int i = 0;
      int j = getViewCount();
      for (int k = 0; k < j; k++)
      {
        View localView = getView(k);
        if (localView.getBreakWeight(paramInt, 0.0F, 2.14748365E9F) == 0)
        {
          f2 += localView.getPreferredSpan(paramInt);
          i = 1;
        }
        else if (i != 0)
        {
          f1 = Math.max(f2, f1);
          i = 0;
          f2 = 0.0F;
        }
        if ((localView instanceof ComponentView)) {
          f1 = Math.max(f1, localView.getMinimumSpan(paramInt));
        }
      }
      f1 = Math.max(f1, f2);
      return f1;
    }
    
    protected void forwardUpdateToView(View paramView, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
    {
      View localView = paramView.getParent();
      paramView.setParent(this);
      super.forwardUpdateToView(paramView, paramDocumentEvent, paramShape, paramViewFactory);
      paramView.setParent(localView);
    }
    
    protected void forwardUpdate(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
    {
      super.forwardUpdate(paramElementChange, paramDocumentEvent, paramShape, paramViewFactory);
      DocumentEvent.EventType localEventType = paramDocumentEvent.getType();
      if ((localEventType == DocumentEvent.EventType.INSERT) || (localEventType == DocumentEvent.EventType.REMOVE))
      {
        this.firstUpdateIndex = Math.min(this.lastUpdateIndex + 1, getViewCount() - 1);
        this.lastUpdateIndex = Math.max(getViewCount() - 1, 0);
        for (int i = this.firstUpdateIndex; i <= this.lastUpdateIndex; i++)
        {
          View localView = getView(i);
          if (localView != null) {
            localView.updateAfterChange();
          }
        }
      }
    }
    
    public void paint(Graphics paramGraphics, Shape paramShape) {}
    
    protected boolean isBefore(int paramInt1, int paramInt2, Rectangle paramRectangle)
    {
      return false;
    }
    
    protected boolean isAfter(int paramInt1, int paramInt2, Rectangle paramRectangle)
    {
      return false;
    }
    
    protected View getViewAtPoint(int paramInt1, int paramInt2, Rectangle paramRectangle)
    {
      return null;
    }
    
    protected void childAllocation(int paramInt, Rectangle paramRectangle) {}
  }
}
