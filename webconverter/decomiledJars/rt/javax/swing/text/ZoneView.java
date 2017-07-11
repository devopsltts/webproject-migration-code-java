package javax.swing.text;

import java.awt.Graphics;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;

public class ZoneView
  extends BoxView
{
  int maxZoneSize = 8192;
  int maxZonesLoaded = 3;
  Vector<View> loadedZones = new Vector();
  
  public ZoneView(Element paramElement, int paramInt)
  {
    super(paramElement, paramInt);
  }
  
  public int getMaximumZoneSize()
  {
    return this.maxZoneSize;
  }
  
  public void setMaximumZoneSize(int paramInt)
  {
    this.maxZoneSize = paramInt;
  }
  
  public int getMaxZonesLoaded()
  {
    return this.maxZonesLoaded;
  }
  
  public void setMaxZonesLoaded(int paramInt)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("ZoneView.setMaxZonesLoaded must be greater than 0.");
    }
    this.maxZonesLoaded = paramInt;
    unloadOldZones();
  }
  
  protected void zoneWasLoaded(View paramView)
  {
    this.loadedZones.addElement(paramView);
    unloadOldZones();
  }
  
  void unloadOldZones()
  {
    while (this.loadedZones.size() > getMaxZonesLoaded())
    {
      View localView = (View)this.loadedZones.elementAt(0);
      this.loadedZones.removeElementAt(0);
      unloadZone(localView);
    }
  }
  
  protected void unloadZone(View paramView)
  {
    paramView.removeAll();
  }
  
  protected boolean isZoneLoaded(View paramView)
  {
    return paramView.getViewCount() > 0;
  }
  
  protected View createZone(int paramInt1, int paramInt2)
  {
    Document localDocument = getDocument();
    Zone localZone;
    try
    {
      localZone = new Zone(getElement(), localDocument.createPosition(paramInt1), localDocument.createPosition(paramInt2));
    }
    catch (BadLocationException localBadLocationException)
    {
      throw new StateInvariantError(localBadLocationException.getMessage());
    }
    return localZone;
  }
  
  protected void loadChildren(ViewFactory paramViewFactory)
  {
    Document localDocument = getDocument();
    int i = getStartOffset();
    int j = getEndOffset();
    append(createZone(i, j));
    handleInsert(i, j - i);
  }
  
  protected int getViewIndexAtPosition(int paramInt)
  {
    int i = getViewCount();
    if (paramInt == getEndOffset()) {
      return i - 1;
    }
    for (int j = 0; j < i; j++)
    {
      View localView = getView(j);
      if ((paramInt >= localView.getStartOffset()) && (paramInt < localView.getEndOffset())) {
        return j;
      }
    }
    return -1;
  }
  
  void handleInsert(int paramInt1, int paramInt2)
  {
    int i = getViewIndex(paramInt1, Position.Bias.Forward);
    View localView = getView(i);
    int j = localView.getStartOffset();
    int k = localView.getEndOffset();
    if (k - j > this.maxZoneSize) {
      splitZone(i, j, k);
    }
  }
  
  void handleRemove(int paramInt1, int paramInt2) {}
  
  void splitZone(int paramInt1, int paramInt2, int paramInt3)
  {
    Element localElement = getElement();
    Document localDocument = localElement.getDocument();
    Vector localVector = new Vector();
    int i = paramInt2;
    do
    {
      paramInt2 = i;
      i = Math.min(getDesiredZoneEnd(paramInt2), paramInt3);
      localVector.addElement(createZone(paramInt2, i));
    } while (i < paramInt3);
    View localView = getView(paramInt1);
    View[] arrayOfView = new View[localVector.size()];
    localVector.copyInto(arrayOfView);
    replace(paramInt1, 1, arrayOfView);
  }
  
  int getDesiredZoneEnd(int paramInt)
  {
    Element localElement1 = getElement();
    int i = localElement1.getElementIndex(paramInt + this.maxZoneSize / 2);
    Element localElement2 = localElement1.getElement(i);
    int j = localElement2.getStartOffset();
    int k = localElement2.getEndOffset();
    if ((k - paramInt > this.maxZoneSize) && (j > paramInt)) {
      return j;
    }
    return k;
  }
  
  protected boolean updateChildren(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, ViewFactory paramViewFactory)
  {
    return false;
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    handleInsert(paramDocumentEvent.getOffset(), paramDocumentEvent.getLength());
    super.insertUpdate(paramDocumentEvent, paramShape, paramViewFactory);
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    handleRemove(paramDocumentEvent.getOffset(), paramDocumentEvent.getLength());
    super.removeUpdate(paramDocumentEvent, paramShape, paramViewFactory);
  }
  
  class Zone
    extends AsyncBoxView
  {
    private Position start;
    private Position end;
    
    public Zone(Element paramElement, Position paramPosition1, Position paramPosition2)
    {
      super(ZoneView.this.getAxis());
      this.start = paramPosition1;
      this.end = paramPosition2;
    }
    
    public void load()
    {
      if (!isLoaded())
      {
        setEstimatedMajorSpan(true);
        Element localElement = getElement();
        ViewFactory localViewFactory = getViewFactory();
        int i = localElement.getElementIndex(getStartOffset());
        int j = localElement.getElementIndex(getEndOffset());
        View[] arrayOfView = new View[j - i + 1];
        for (int k = i; k <= j; k++) {
          arrayOfView[(k - i)] = localViewFactory.create(localElement.getElement(k));
        }
        replace(0, 0, arrayOfView);
        ZoneView.this.zoneWasLoaded(this);
      }
    }
    
    public void unload()
    {
      setEstimatedMajorSpan(true);
      removeAll();
    }
    
    public boolean isLoaded()
    {
      return getViewCount() != 0;
    }
    
    protected void loadChildren(ViewFactory paramViewFactory)
    {
      setEstimatedMajorSpan(true);
      Element localElement = getElement();
      int i = localElement.getElementIndex(getStartOffset());
      int j = localElement.getElementIndex(getEndOffset());
      int k = j - i;
      View localView = paramViewFactory.create(localElement.getElement(i));
      localView.setParent(this);
      float f1 = localView.getPreferredSpan(0);
      float f2 = localView.getPreferredSpan(1);
      if (getMajorAxis() == 0) {
        f1 *= k;
      } else {
        f2 += k;
      }
      setSize(f1, f2);
    }
    
    protected void flushRequirementChanges()
    {
      if (isLoaded()) {
        super.flushRequirementChanges();
      }
    }
    
    public int getViewIndex(int paramInt, Position.Bias paramBias)
    {
      int i = paramBias == Position.Bias.Backward ? 1 : 0;
      paramInt = i != 0 ? Math.max(0, paramInt - 1) : paramInt;
      Element localElement = getElement();
      int j = localElement.getElementIndex(paramInt);
      int k = localElement.getElementIndex(getStartOffset());
      return j - k;
    }
    
    protected boolean updateChildren(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, ViewFactory paramViewFactory)
    {
      Element[] arrayOfElement1 = paramElementChange.getChildrenRemoved();
      Element[] arrayOfElement2 = paramElementChange.getChildrenAdded();
      Element localElement = getElement();
      int i = localElement.getElementIndex(getStartOffset());
      int j = localElement.getElementIndex(getEndOffset() - 1);
      int k = paramElementChange.getIndex();
      if ((k >= i) && (k <= j))
      {
        int m = k - i;
        int n = Math.min(j - i + 1, arrayOfElement2.length);
        int i1 = Math.min(j - i + 1, arrayOfElement1.length);
        View[] arrayOfView = new View[n];
        for (int i2 = 0; i2 < n; i2++) {
          arrayOfView[i2] = paramViewFactory.create(arrayOfElement2[i2]);
        }
        replace(m, i1, arrayOfView);
      }
      return true;
    }
    
    public AttributeSet getAttributes()
    {
      return ZoneView.this.getAttributes();
    }
    
    public void paint(Graphics paramGraphics, Shape paramShape)
    {
      load();
      super.paint(paramGraphics, paramShape);
    }
    
    public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
    {
      load();
      return super.viewToModel(paramFloat1, paramFloat2, paramShape, paramArrayOfBias);
    }
    
    public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
      throws BadLocationException
    {
      load();
      return super.modelToView(paramInt, paramShape, paramBias);
    }
    
    public int getStartOffset()
    {
      return this.start.getOffset();
    }
    
    public int getEndOffset()
    {
      return this.end.getOffset();
    }
    
    public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
    {
      if (isLoaded()) {
        super.insertUpdate(paramDocumentEvent, paramShape, paramViewFactory);
      }
    }
    
    public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
    {
      if (isLoaded()) {
        super.removeUpdate(paramDocumentEvent, paramShape, paramViewFactory);
      }
    }
    
    public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
    {
      if (isLoaded()) {
        super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
      }
    }
  }
}
