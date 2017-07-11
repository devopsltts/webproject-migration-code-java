package javax.swing.text;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;

public class BoxView
  extends CompositeView
{
  int majorAxis;
  int majorSpan;
  int minorSpan;
  boolean majorReqValid;
  boolean minorReqValid;
  SizeRequirements majorRequest;
  SizeRequirements minorRequest;
  boolean majorAllocValid;
  int[] majorOffsets;
  int[] majorSpans;
  boolean minorAllocValid;
  int[] minorOffsets;
  int[] minorSpans;
  Rectangle tempRect = new Rectangle();
  
  public BoxView(Element paramElement, int paramInt)
  {
    super(paramElement);
    this.majorAxis = paramInt;
    this.majorOffsets = new int[0];
    this.majorSpans = new int[0];
    this.majorReqValid = false;
    this.majorAllocValid = false;
    this.minorOffsets = new int[0];
    this.minorSpans = new int[0];
    this.minorReqValid = false;
    this.minorAllocValid = false;
  }
  
  public int getAxis()
  {
    return this.majorAxis;
  }
  
  public void setAxis(int paramInt)
  {
    int i = paramInt != this.majorAxis ? 1 : 0;
    this.majorAxis = paramInt;
    if (i != 0) {
      preferenceChanged(null, true, true);
    }
  }
  
  public void layoutChanged(int paramInt)
  {
    if (paramInt == this.majorAxis) {
      this.majorAllocValid = false;
    } else {
      this.minorAllocValid = false;
    }
  }
  
  protected boolean isLayoutValid(int paramInt)
  {
    if (paramInt == this.majorAxis) {
      return this.majorAllocValid;
    }
    return this.minorAllocValid;
  }
  
  protected void paintChild(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    View localView = getView(paramInt);
    localView.paint(paramGraphics, paramRectangle);
  }
  
  public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView)
  {
    super.replace(paramInt1, paramInt2, paramArrayOfView);
    int i = paramArrayOfView != null ? paramArrayOfView.length : 0;
    this.majorOffsets = updateLayoutArray(this.majorOffsets, paramInt1, i);
    this.majorSpans = updateLayoutArray(this.majorSpans, paramInt1, i);
    this.majorReqValid = false;
    this.majorAllocValid = false;
    this.minorOffsets = updateLayoutArray(this.minorOffsets, paramInt1, i);
    this.minorSpans = updateLayoutArray(this.minorSpans, paramInt1, i);
    this.minorReqValid = false;
    this.minorAllocValid = false;
  }
  
  int[] updateLayoutArray(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = getViewCount();
    int[] arrayOfInt = new int[i];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramInt1);
    System.arraycopy(paramArrayOfInt, paramInt1, arrayOfInt, paramInt1 + paramInt2, i - paramInt2 - paramInt1);
    return arrayOfInt;
  }
  
  protected void forwardUpdate(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    boolean bool = isLayoutValid(this.majorAxis);
    super.forwardUpdate(paramElementChange, paramDocumentEvent, paramShape, paramViewFactory);
    if ((bool) && (!isLayoutValid(this.majorAxis)))
    {
      Container localContainer = getContainer();
      if ((paramShape != null) && (localContainer != null))
      {
        int i = paramDocumentEvent.getOffset();
        int j = getViewIndexAtPosition(i);
        Rectangle localRectangle = getInsideAllocation(paramShape);
        if (this.majorAxis == 0)
        {
          localRectangle.x += this.majorOffsets[j];
          localRectangle.width -= this.majorOffsets[j];
        }
        else
        {
          localRectangle.y += this.minorOffsets[j];
          localRectangle.height -= this.minorOffsets[j];
        }
        localContainer.repaint(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
      }
    }
  }
  
  public void preferenceChanged(View paramView, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool1 = this.majorAxis == 0 ? paramBoolean1 : paramBoolean2;
    boolean bool2 = this.majorAxis == 0 ? paramBoolean2 : paramBoolean1;
    if (bool1)
    {
      this.majorReqValid = false;
      this.majorAllocValid = false;
    }
    if (bool2)
    {
      this.minorReqValid = false;
      this.minorAllocValid = false;
    }
    super.preferenceChanged(paramView, paramBoolean1, paramBoolean2);
  }
  
  public int getResizeWeight(int paramInt)
  {
    checkRequests(paramInt);
    if (paramInt == this.majorAxis)
    {
      if ((this.majorRequest.preferred != this.majorRequest.minimum) || (this.majorRequest.preferred != this.majorRequest.maximum)) {
        return 1;
      }
    }
    else if ((this.minorRequest.preferred != this.minorRequest.minimum) || (this.minorRequest.preferred != this.minorRequest.maximum)) {
      return 1;
    }
    return 0;
  }
  
  void setSpanOnAxis(int paramInt, float paramFloat)
  {
    if (paramInt == this.majorAxis)
    {
      if (this.majorSpan != (int)paramFloat) {
        this.majorAllocValid = false;
      }
      if (!this.majorAllocValid)
      {
        this.majorSpan = ((int)paramFloat);
        checkRequests(this.majorAxis);
        layoutMajorAxis(this.majorSpan, paramInt, this.majorOffsets, this.majorSpans);
        this.majorAllocValid = true;
        updateChildSizes();
      }
    }
    else
    {
      if ((int)paramFloat != this.minorSpan) {
        this.minorAllocValid = false;
      }
      if (!this.minorAllocValid)
      {
        this.minorSpan = ((int)paramFloat);
        checkRequests(paramInt);
        layoutMinorAxis(this.minorSpan, paramInt, this.minorOffsets, this.minorSpans);
        this.minorAllocValid = true;
        updateChildSizes();
      }
    }
  }
  
  void updateChildSizes()
  {
    int i = getViewCount();
    int j;
    View localView;
    if (this.majorAxis == 0) {
      for (j = 0; j < i; j++)
      {
        localView = getView(j);
        localView.setSize(this.majorSpans[j], this.minorSpans[j]);
      }
    } else {
      for (j = 0; j < i; j++)
      {
        localView = getView(j);
        localView.setSize(this.minorSpans[j], this.majorSpans[j]);
      }
    }
  }
  
  float getSpanOnAxis(int paramInt)
  {
    if (paramInt == this.majorAxis) {
      return this.majorSpan;
    }
    return this.minorSpan;
  }
  
  public void setSize(float paramFloat1, float paramFloat2)
  {
    layout(Math.max(0, (int)(paramFloat1 - getLeftInset() - getRightInset())), Math.max(0, (int)(paramFloat2 - getTopInset() - getBottomInset())));
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Rectangle localRectangle1 = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    int i = getViewCount();
    int j = localRectangle1.x + getLeftInset();
    int k = localRectangle1.y + getTopInset();
    Rectangle localRectangle2 = paramGraphics.getClipBounds();
    for (int m = 0; m < i; m++)
    {
      this.tempRect.x = (j + getOffset(0, m));
      this.tempRect.y = (k + getOffset(1, m));
      this.tempRect.width = getSpan(0, m);
      this.tempRect.height = getSpan(1, m);
      int n = this.tempRect.x;
      int i1 = n + this.tempRect.width;
      int i2 = this.tempRect.y;
      int i3 = i2 + this.tempRect.height;
      int i4 = localRectangle2.x;
      int i5 = i4 + localRectangle2.width;
      int i6 = localRectangle2.y;
      int i7 = i6 + localRectangle2.height;
      if ((i1 >= i4) && (i3 >= i6) && (i5 >= n) && (i7 >= i2)) {
        paintChild(paramGraphics, this.tempRect, m);
      }
    }
  }
  
  public Shape getChildAllocation(int paramInt, Shape paramShape)
  {
    if (paramShape != null)
    {
      Shape localShape = super.getChildAllocation(paramInt, paramShape);
      if ((localShape != null) && (!isAllocationValid()))
      {
        Rectangle localRectangle = (localShape instanceof Rectangle) ? (Rectangle)localShape : localShape.getBounds();
        if ((localRectangle.width == 0) && (localRectangle.height == 0)) {
          return null;
        }
      }
      return localShape;
    }
    return null;
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException
  {
    if (!isAllocationValid())
    {
      Rectangle localRectangle = paramShape.getBounds();
      setSize(localRectangle.width, localRectangle.height);
    }
    return super.modelToView(paramInt, paramShape, paramBias);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    if (!isAllocationValid())
    {
      Rectangle localRectangle = paramShape.getBounds();
      setSize(localRectangle.width, localRectangle.height);
    }
    return super.viewToModel(paramFloat1, paramFloat2, paramShape, paramArrayOfBias);
  }
  
  public float getAlignment(int paramInt)
  {
    checkRequests(paramInt);
    if (paramInt == this.majorAxis) {
      return this.majorRequest.alignment;
    }
    return this.minorRequest.alignment;
  }
  
  public float getPreferredSpan(int paramInt)
  {
    checkRequests(paramInt);
    float f = paramInt == 0 ? getLeftInset() + getRightInset() : getTopInset() + getBottomInset();
    if (paramInt == this.majorAxis) {
      return this.majorRequest.preferred + f;
    }
    return this.minorRequest.preferred + f;
  }
  
  public float getMinimumSpan(int paramInt)
  {
    checkRequests(paramInt);
    float f = paramInt == 0 ? getLeftInset() + getRightInset() : getTopInset() + getBottomInset();
    if (paramInt == this.majorAxis) {
      return this.majorRequest.minimum + f;
    }
    return this.minorRequest.minimum + f;
  }
  
  public float getMaximumSpan(int paramInt)
  {
    checkRequests(paramInt);
    float f = paramInt == 0 ? getLeftInset() + getRightInset() : getTopInset() + getBottomInset();
    if (paramInt == this.majorAxis) {
      return this.majorRequest.maximum + f;
    }
    return this.minorRequest.maximum + f;
  }
  
  protected boolean isAllocationValid()
  {
    return (this.majorAllocValid) && (this.minorAllocValid);
  }
  
  protected boolean isBefore(int paramInt1, int paramInt2, Rectangle paramRectangle)
  {
    if (this.majorAxis == 0) {
      return paramInt1 < paramRectangle.x;
    }
    return paramInt2 < paramRectangle.y;
  }
  
  protected boolean isAfter(int paramInt1, int paramInt2, Rectangle paramRectangle)
  {
    if (this.majorAxis == 0) {
      return paramInt1 > paramRectangle.width + paramRectangle.x;
    }
    return paramInt2 > paramRectangle.height + paramRectangle.y;
  }
  
  protected View getViewAtPoint(int paramInt1, int paramInt2, Rectangle paramRectangle)
  {
    int i = getViewCount();
    if (this.majorAxis == 0)
    {
      if (paramInt1 < paramRectangle.x + this.majorOffsets[0])
      {
        childAllocation(0, paramRectangle);
        return getView(0);
      }
      for (j = 0; j < i; j++) {
        if (paramInt1 < paramRectangle.x + this.majorOffsets[j])
        {
          childAllocation(j - 1, paramRectangle);
          return getView(j - 1);
        }
      }
      childAllocation(i - 1, paramRectangle);
      return getView(i - 1);
    }
    if (paramInt2 < paramRectangle.y + this.majorOffsets[0])
    {
      childAllocation(0, paramRectangle);
      return getView(0);
    }
    for (int j = 0; j < i; j++) {
      if (paramInt2 < paramRectangle.y + this.majorOffsets[j])
      {
        childAllocation(j - 1, paramRectangle);
        return getView(j - 1);
      }
    }
    childAllocation(i - 1, paramRectangle);
    return getView(i - 1);
  }
  
  protected void childAllocation(int paramInt, Rectangle paramRectangle)
  {
    paramRectangle.x += getOffset(0, paramInt);
    paramRectangle.y += getOffset(1, paramInt);
    paramRectangle.width = getSpan(0, paramInt);
    paramRectangle.height = getSpan(1, paramInt);
  }
  
  protected void layout(int paramInt1, int paramInt2)
  {
    setSpanOnAxis(0, paramInt1);
    setSpanOnAxis(1, paramInt2);
  }
  
  public int getWidth()
  {
    int i;
    if (this.majorAxis == 0) {
      i = this.majorSpan;
    } else {
      i = this.minorSpan;
    }
    i += getLeftInset() - getRightInset();
    return i;
  }
  
  public int getHeight()
  {
    int i;
    if (this.majorAxis == 1) {
      i = this.majorSpan;
    } else {
      i = this.minorSpan;
    }
    i += getTopInset() - getBottomInset();
    return i;
  }
  
  protected void layoutMajorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    long l1 = 0L;
    int i = getViewCount();
    for (int j = 0; j < i; j++)
    {
      View localView1 = getView(j);
      paramArrayOfInt2[j] = ((int)localView1.getPreferredSpan(paramInt2));
      l1 += paramArrayOfInt2[j];
    }
    long l2 = paramInt1 - l1;
    float f1 = 0.0F;
    int[] arrayOfInt = null;
    float f2;
    if (l2 != 0L)
    {
      long l3 = 0L;
      arrayOfInt = new int[i];
      for (int n = 0; n < i; n++)
      {
        View localView2 = getView(n);
        int i1;
        if (l2 < 0L)
        {
          i1 = (int)localView2.getMinimumSpan(paramInt2);
          paramArrayOfInt2[n] -= i1;
        }
        else
        {
          i1 = (int)localView2.getMaximumSpan(paramInt2);
          arrayOfInt[n] = (i1 - paramArrayOfInt2[n]);
        }
        l3 += i1;
      }
      f2 = (float)Math.abs(l3 - l1);
      f1 = (float)l2 / f2;
      f1 = Math.min(f1, 1.0F);
      f1 = Math.max(f1, -1.0F);
    }
    int k = 0;
    for (int m = 0; m < i; m++)
    {
      paramArrayOfInt1[m] = k;
      if (l2 != 0L)
      {
        f2 = f1 * arrayOfInt[m];
        paramArrayOfInt2[m] += Math.round(f2);
      }
      k = (int)Math.min(k + paramArrayOfInt2[m], 2147483647L);
    }
  }
  
  protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    int i = getViewCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getView(j);
      int k = (int)localView.getMaximumSpan(paramInt2);
      if (k < paramInt1)
      {
        float f = localView.getAlignment(paramInt2);
        paramArrayOfInt1[j] = ((int)((paramInt1 - k) * f));
        paramArrayOfInt2[j] = k;
      }
      else
      {
        int m = (int)localView.getMinimumSpan(paramInt2);
        paramArrayOfInt1[j] = 0;
        paramArrayOfInt2[j] = Math.max(m, paramInt1);
      }
    }
  }
  
  protected SizeRequirements calculateMajorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
  {
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    int i = getViewCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getView(j);
      f1 += localView.getMinimumSpan(paramInt);
      f2 += localView.getPreferredSpan(paramInt);
      f3 += localView.getMaximumSpan(paramInt);
    }
    if (paramSizeRequirements == null) {
      paramSizeRequirements = new SizeRequirements();
    }
    paramSizeRequirements.alignment = 0.5F;
    paramSizeRequirements.minimum = ((int)f1);
    paramSizeRequirements.preferred = ((int)f2);
    paramSizeRequirements.maximum = ((int)f3);
    return paramSizeRequirements;
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
  {
    int i = 0;
    long l = 0L;
    int j = Integer.MAX_VALUE;
    int k = getViewCount();
    for (int m = 0; m < k; m++)
    {
      View localView = getView(m);
      i = Math.max((int)localView.getMinimumSpan(paramInt), i);
      l = Math.max((int)localView.getPreferredSpan(paramInt), l);
      j = Math.max((int)localView.getMaximumSpan(paramInt), j);
    }
    if (paramSizeRequirements == null)
    {
      paramSizeRequirements = new SizeRequirements();
      paramSizeRequirements.alignment = 0.5F;
    }
    paramSizeRequirements.preferred = ((int)l);
    paramSizeRequirements.minimum = i;
    paramSizeRequirements.maximum = j;
    return paramSizeRequirements;
  }
  
  void checkRequests(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("Invalid axis: " + paramInt);
    }
    if (paramInt == this.majorAxis)
    {
      if (!this.majorReqValid)
      {
        this.majorRequest = calculateMajorAxisRequirements(paramInt, this.majorRequest);
        this.majorReqValid = true;
      }
    }
    else if (!this.minorReqValid)
    {
      this.minorRequest = calculateMinorAxisRequirements(paramInt, this.minorRequest);
      this.minorReqValid = true;
    }
  }
  
  protected void baselineLayout(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    int i = (int)(paramInt1 * getAlignment(paramInt2));
    int j = paramInt1 - i;
    int k = getViewCount();
    for (int m = 0; m < k; m++)
    {
      View localView = getView(m);
      float f1 = localView.getAlignment(paramInt2);
      float f2;
      if (localView.getResizeWeight(paramInt2) > 0)
      {
        float f3 = localView.getMinimumSpan(paramInt2);
        float f4 = localView.getMaximumSpan(paramInt2);
        if (f1 == 0.0F)
        {
          f2 = Math.max(Math.min(f4, j), f3);
        }
        else if (f1 == 1.0F)
        {
          f2 = Math.max(Math.min(f4, i), f3);
        }
        else
        {
          float f5 = Math.min(i / f1, j / (1.0F - f1));
          f2 = Math.max(Math.min(f4, f5), f3);
        }
      }
      else
      {
        f2 = localView.getPreferredSpan(paramInt2);
      }
      paramArrayOfInt1[m] = (i - (int)(f2 * f1));
      paramArrayOfInt2[m] = ((int)f2);
    }
  }
  
  protected SizeRequirements baselineRequirements(int paramInt, SizeRequirements paramSizeRequirements)
  {
    SizeRequirements localSizeRequirements1 = new SizeRequirements();
    SizeRequirements localSizeRequirements2 = new SizeRequirements();
    if (paramSizeRequirements == null) {
      paramSizeRequirements = new SizeRequirements();
    }
    paramSizeRequirements.alignment = 0.5F;
    int i = getViewCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getView(j);
      float f1 = localView.getAlignment(paramInt);
      float f2 = localView.getPreferredSpan(paramInt);
      int k = (int)(f1 * f2);
      int m = (int)(f2 - k);
      localSizeRequirements1.preferred = Math.max(k, localSizeRequirements1.preferred);
      localSizeRequirements2.preferred = Math.max(m, localSizeRequirements2.preferred);
      if (localView.getResizeWeight(paramInt) > 0)
      {
        f2 = localView.getMinimumSpan(paramInt);
        k = (int)(f1 * f2);
        m = (int)(f2 - k);
        localSizeRequirements1.minimum = Math.max(k, localSizeRequirements1.minimum);
        localSizeRequirements2.minimum = Math.max(m, localSizeRequirements2.minimum);
        f2 = localView.getMaximumSpan(paramInt);
        k = (int)(f1 * f2);
        m = (int)(f2 - k);
        localSizeRequirements1.maximum = Math.max(k, localSizeRequirements1.maximum);
        localSizeRequirements2.maximum = Math.max(m, localSizeRequirements2.maximum);
      }
      else
      {
        localSizeRequirements1.minimum = Math.max(k, localSizeRequirements1.minimum);
        localSizeRequirements2.minimum = Math.max(m, localSizeRequirements2.minimum);
        localSizeRequirements1.maximum = Math.max(k, localSizeRequirements1.maximum);
        localSizeRequirements2.maximum = Math.max(m, localSizeRequirements2.maximum);
      }
    }
    paramSizeRequirements.preferred = ((int)Math.min(localSizeRequirements1.preferred + localSizeRequirements2.preferred, 2147483647L));
    if (paramSizeRequirements.preferred > 0) {
      paramSizeRequirements.alignment = (localSizeRequirements1.preferred / paramSizeRequirements.preferred);
    }
    if (paramSizeRequirements.alignment == 0.0F)
    {
      paramSizeRequirements.minimum = localSizeRequirements2.minimum;
      paramSizeRequirements.maximum = localSizeRequirements2.maximum;
    }
    else if (paramSizeRequirements.alignment == 1.0F)
    {
      paramSizeRequirements.minimum = localSizeRequirements1.minimum;
      paramSizeRequirements.maximum = localSizeRequirements1.maximum;
    }
    else
    {
      paramSizeRequirements.minimum = Math.round(Math.max(localSizeRequirements1.minimum / paramSizeRequirements.alignment, localSizeRequirements2.minimum / (1.0F - paramSizeRequirements.alignment)));
      paramSizeRequirements.maximum = Math.round(Math.min(localSizeRequirements1.maximum / paramSizeRequirements.alignment, localSizeRequirements2.maximum / (1.0F - paramSizeRequirements.alignment)));
    }
    return paramSizeRequirements;
  }
  
  protected int getOffset(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = paramInt1 == this.majorAxis ? this.majorOffsets : this.minorOffsets;
    return arrayOfInt[paramInt2];
  }
  
  protected int getSpan(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = paramInt1 == this.majorAxis ? this.majorSpans : this.minorSpans;
    return arrayOfInt[paramInt2];
  }
  
  protected boolean flipEastAndWestAtEnds(int paramInt, Position.Bias paramBias)
  {
    if (this.majorAxis == 1)
    {
      int i = paramBias == Position.Bias.Backward ? Math.max(0, paramInt - 1) : paramInt;
      int j = getViewIndexAtPosition(i);
      if (j != -1)
      {
        View localView = getView(j);
        if ((localView != null) && ((localView instanceof CompositeView))) {
          return ((CompositeView)localView).flipEastAndWestAtEnds(paramInt, paramBias);
        }
      }
    }
    return false;
  }
}
