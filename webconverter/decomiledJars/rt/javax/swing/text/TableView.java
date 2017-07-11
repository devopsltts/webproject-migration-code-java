package javax.swing.text;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.BitSet;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.text.html.HTML.Attribute;

public abstract class TableView
  extends BoxView
{
  int[] columnSpans;
  int[] columnOffsets;
  SizeRequirements[] columnRequirements;
  Vector<TableRow> rows = new Vector();
  boolean gridValid = false;
  private static final BitSet EMPTY = new BitSet();
  
  public TableView(Element paramElement)
  {
    super(paramElement, 1);
  }
  
  protected TableRow createTableRow(Element paramElement)
  {
    return new TableRow(paramElement);
  }
  
  @Deprecated
  protected TableCell createTableCell(Element paramElement)
  {
    return new TableCell(paramElement);
  }
  
  int getColumnCount()
  {
    return this.columnSpans.length;
  }
  
  int getColumnSpan(int paramInt)
  {
    return this.columnSpans[paramInt];
  }
  
  int getRowCount()
  {
    return this.rows.size();
  }
  
  int getRowSpan(int paramInt)
  {
    TableRow localTableRow = getRow(paramInt);
    if (localTableRow != null) {
      return (int)localTableRow.getPreferredSpan(1);
    }
    return 0;
  }
  
  TableRow getRow(int paramInt)
  {
    if (paramInt < this.rows.size()) {
      return (TableRow)this.rows.elementAt(paramInt);
    }
    return null;
  }
  
  int getColumnsOccupied(View paramView)
  {
    AttributeSet localAttributeSet = paramView.getElement().getAttributes();
    String str = (String)localAttributeSet.getAttribute(HTML.Attribute.COLSPAN);
    if (str != null) {
      try
      {
        return Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return 1;
  }
  
  int getRowsOccupied(View paramView)
  {
    AttributeSet localAttributeSet = paramView.getElement().getAttributes();
    String str = (String)localAttributeSet.getAttribute(HTML.Attribute.ROWSPAN);
    if (str != null) {
      try
      {
        return Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return 1;
  }
  
  void invalidateGrid()
  {
    this.gridValid = false;
  }
  
  protected void forwardUpdate(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.forwardUpdate(paramElementChange, paramDocumentEvent, paramShape, paramViewFactory);
    if (paramShape != null)
    {
      Container localContainer = getContainer();
      if (localContainer != null)
      {
        Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
        localContainer.repaint(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
      }
    }
  }
  
  public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView)
  {
    super.replace(paramInt1, paramInt2, paramArrayOfView);
    invalidateGrid();
  }
  
  void updateGrid()
  {
    if (!this.gridValid)
    {
      this.rows.removeAllElements();
      int i = getViewCount();
      for (int j = 0; j < i; j++)
      {
        View localView1 = getView(j);
        if ((localView1 instanceof TableRow))
        {
          this.rows.addElement((TableRow)localView1);
          TableRow localTableRow1 = (TableRow)localView1;
          localTableRow1.clearFilledColumns();
          localTableRow1.setRow(j);
        }
      }
      j = 0;
      int k = this.rows.size();
      for (int m = 0; m < k; m++)
      {
        TableRow localTableRow2 = getRow(m);
        int n = 0;
        int i1 = 0;
        while (i1 < localTableRow2.getViewCount())
        {
          View localView2 = localTableRow2.getView(i1);
          while (localTableRow2.isFilled(n)) {
            n++;
          }
          int i2 = getRowsOccupied(localView2);
          int i3 = getColumnsOccupied(localView2);
          if ((i3 > 1) || (i2 > 1))
          {
            int i4 = m + i2;
            int i5 = n + i3;
            for (int i6 = m; i6 < i4; i6++) {
              for (int i7 = n; i7 < i5; i7++) {
                if ((i6 != m) || (i7 != n)) {
                  addFill(i6, i7);
                }
              }
            }
            if (i3 > 1) {
              n += i3 - 1;
            }
          }
          i1++;
          n++;
        }
        j = Math.max(j, n);
      }
      this.columnSpans = new int[j];
      this.columnOffsets = new int[j];
      this.columnRequirements = new SizeRequirements[j];
      for (m = 0; m < j; m++) {
        this.columnRequirements[m] = new SizeRequirements();
      }
      this.gridValid = true;
    }
  }
  
  void addFill(int paramInt1, int paramInt2)
  {
    TableRow localTableRow = getRow(paramInt1);
    if (localTableRow != null) {
      localTableRow.fillColumn(paramInt2);
    }
  }
  
  protected void layoutColumns(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, SizeRequirements[] paramArrayOfSizeRequirements)
  {
    SizeRequirements.calculateTiledPositions(paramInt, null, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    updateGrid();
    int i = getRowCount();
    for (int j = 0; j < i; j++)
    {
      TableRow localTableRow = getRow(j);
      localTableRow.layoutChanged(paramInt2);
    }
    layoutColumns(paramInt1, this.columnOffsets, this.columnSpans, this.columnRequirements);
    super.layoutMinorAxis(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
  {
    updateGrid();
    calculateColumnRequirements(paramInt);
    if (paramSizeRequirements == null) {
      paramSizeRequirements = new SizeRequirements();
    }
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    for (SizeRequirements localSizeRequirements : this.columnRequirements)
    {
      l1 += localSizeRequirements.minimum;
      l2 += localSizeRequirements.preferred;
      l3 += localSizeRequirements.maximum;
    }
    paramSizeRequirements.minimum = ((int)l1);
    paramSizeRequirements.preferred = ((int)l2);
    paramSizeRequirements.maximum = ((int)l3);
    paramSizeRequirements.alignment = 0.0F;
    return paramSizeRequirements;
  }
  
  void calculateColumnRequirements(int paramInt)
  {
    int i = 0;
    int j = getRowCount();
    TableRow localTableRow;
    int m;
    int n;
    int i1;
    View localView;
    int i2;
    for (int k = 0; k < j; k++)
    {
      localTableRow = getRow(k);
      m = 0;
      n = localTableRow.getViewCount();
      i1 = 0;
      while (i1 < n)
      {
        localView = localTableRow.getView(i1);
        while (localTableRow.isFilled(m)) {
          m++;
        }
        i2 = getRowsOccupied(localView);
        int i3 = getColumnsOccupied(localView);
        if (i3 == 1)
        {
          checkSingleColumnCell(paramInt, m, localView);
        }
        else
        {
          i = 1;
          m += i3 - 1;
        }
        i1++;
        m++;
      }
    }
    if (i != 0) {
      for (k = 0; k < j; k++)
      {
        localTableRow = getRow(k);
        m = 0;
        n = localTableRow.getViewCount();
        i1 = 0;
        while (i1 < n)
        {
          localView = localTableRow.getView(i1);
          while (localTableRow.isFilled(m)) {
            m++;
          }
          i2 = getColumnsOccupied(localView);
          if (i2 > 1)
          {
            checkMultiColumnCell(paramInt, m, i2, localView);
            m += i2 - 1;
          }
          i1++;
          m++;
        }
      }
    }
  }
  
  void checkSingleColumnCell(int paramInt1, int paramInt2, View paramView)
  {
    SizeRequirements localSizeRequirements = this.columnRequirements[paramInt2];
    localSizeRequirements.minimum = Math.max((int)paramView.getMinimumSpan(paramInt1), localSizeRequirements.minimum);
    localSizeRequirements.preferred = Math.max((int)paramView.getPreferredSpan(paramInt1), localSizeRequirements.preferred);
    localSizeRequirements.maximum = Math.max((int)paramView.getMaximumSpan(paramInt1), localSizeRequirements.maximum);
  }
  
  void checkMultiColumnCell(int paramInt1, int paramInt2, int paramInt3, View paramView)
  {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    Object localObject1;
    for (int i = 0; i < paramInt3; i++)
    {
      localObject1 = this.columnRequirements[(paramInt2 + i)];
      l1 += ((SizeRequirements)localObject1).minimum;
      l2 += ((SizeRequirements)localObject1).preferred;
      l3 += ((SizeRequirements)localObject1).maximum;
    }
    i = (int)paramView.getMinimumSpan(paramInt1);
    Object localObject2;
    if (i > l1)
    {
      localObject1 = new SizeRequirements[paramInt3];
      for (int k = 0; k < paramInt3; k++)
      {
        localObject3 = localObject1[k] =  = this.columnRequirements[(paramInt2 + k)];
        ((SizeRequirements)localObject3).maximum = Math.max(((SizeRequirements)localObject3).maximum, (int)paramView.getMaximumSpan(paramInt1));
      }
      localObject2 = new int[paramInt3];
      Object localObject3 = new int[paramInt3];
      SizeRequirements.calculateTiledPositions(i, null, (SizeRequirements[])localObject1, (int[])localObject3, (int[])localObject2);
      for (int n = 0; n < paramInt3; n++)
      {
        Object localObject5 = localObject1[n];
        localObject5.minimum = Math.max(localObject2[n], localObject5.minimum);
        localObject5.preferred = Math.max(localObject5.minimum, localObject5.preferred);
        localObject5.maximum = Math.max(localObject5.preferred, localObject5.maximum);
      }
    }
    int j = (int)paramView.getPreferredSpan(paramInt1);
    if (j > l2)
    {
      localObject2 = new SizeRequirements[paramInt3];
      for (int m = 0; m < paramInt3; m++) {
        localObject4 = localObject2[m] =  = this.columnRequirements[(paramInt2 + m)];
      }
      int[] arrayOfInt = new int[paramInt3];
      Object localObject4 = new int[paramInt3];
      SizeRequirements.calculateTiledPositions(j, null, (SizeRequirements[])localObject2, (int[])localObject4, arrayOfInt);
      for (int i1 = 0; i1 < paramInt3; i1++)
      {
        Object localObject6 = localObject2[i1];
        localObject6.preferred = Math.max(arrayOfInt[i1], localObject6.preferred);
        localObject6.maximum = Math.max(localObject6.preferred, localObject6.maximum);
      }
    }
  }
  
  protected View getViewAtPosition(int paramInt, Rectangle paramRectangle)
  {
    int i = getViewCount();
    for (int j = 0; j < i; j++)
    {
      View localView2 = getView(j);
      int k = localView2.getStartOffset();
      int m = localView2.getEndOffset();
      if ((paramInt >= k) && (paramInt < m))
      {
        if (paramRectangle != null) {
          childAllocation(j, paramRectangle);
        }
        return localView2;
      }
    }
    if (paramInt == getEndOffset())
    {
      View localView1 = getView(i - 1);
      if (paramRectangle != null) {
        childAllocation(i - 1, paramRectangle);
      }
      return localView1;
    }
    return null;
  }
  
  static abstract interface GridCell
  {
    public abstract void setGridLocation(int paramInt1, int paramInt2);
    
    public abstract int getGridRow();
    
    public abstract int getGridColumn();
    
    public abstract int getColumnCount();
    
    public abstract int getRowCount();
  }
  
  @Deprecated
  public class TableCell
    extends BoxView
    implements TableView.GridCell
  {
    int row;
    int col;
    
    public TableCell(Element paramElement)
    {
      super(1);
    }
    
    public int getColumnCount()
    {
      return 1;
    }
    
    public int getRowCount()
    {
      return 1;
    }
    
    public void setGridLocation(int paramInt1, int paramInt2)
    {
      this.row = paramInt1;
      this.col = paramInt2;
    }
    
    public int getGridRow()
    {
      return this.row;
    }
    
    public int getGridColumn()
    {
      return this.col;
    }
  }
  
  public class TableRow
    extends BoxView
  {
    BitSet fillColumns = new BitSet();
    int row;
    
    public TableRow(Element paramElement)
    {
      super(0);
    }
    
    void clearFilledColumns()
    {
      this.fillColumns.and(TableView.EMPTY);
    }
    
    void fillColumn(int paramInt)
    {
      this.fillColumns.set(paramInt);
    }
    
    boolean isFilled(int paramInt)
    {
      return this.fillColumns.get(paramInt);
    }
    
    int getRow()
    {
      return this.row;
    }
    
    void setRow(int paramInt)
    {
      this.row = paramInt;
    }
    
    int getColumnCount()
    {
      int i = 0;
      int j = this.fillColumns.size();
      for (int k = 0; k < j; k++) {
        if (this.fillColumns.get(k)) {
          i++;
        }
      }
      return getViewCount() + i;
    }
    
    public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView)
    {
      super.replace(paramInt1, paramInt2, paramArrayOfView);
      TableView.this.invalidateGrid();
    }
    
    protected void layoutMajorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      int i = 0;
      int j = getViewCount();
      int k = 0;
      while (k < j)
      {
        View localView = getView(k);
        while (isFilled(i)) {
          i++;
        }
        int m = TableView.this.getColumnsOccupied(localView);
        paramArrayOfInt2[k] = TableView.this.columnSpans[i];
        paramArrayOfInt1[k] = TableView.this.columnOffsets[i];
        if (m > 1)
        {
          int n = TableView.this.columnSpans.length;
          for (int i1 = 1; i1 < m; i1++) {
            if (i + i1 < n) {
              paramArrayOfInt2[k] += TableView.this.columnSpans[(i + i1)];
            }
          }
          i += m - 1;
        }
        k++;
        i++;
      }
    }
    
    protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    {
      super.layoutMinorAxis(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
      int i = 0;
      int j = getViewCount();
      int k = 0;
      while (k < j)
      {
        View localView = getView(k);
        while (isFilled(i)) {
          i++;
        }
        int m = TableView.this.getColumnsOccupied(localView);
        int n = TableView.this.getRowsOccupied(localView);
        if (n > 1) {
          for (int i1 = 1; i1 < n; i1++)
          {
            int i2 = getRow() + i1;
            if (i2 < TableView.this.getViewCount())
            {
              int i3 = TableView.this.getSpan(1, getRow() + i1);
              paramArrayOfInt2[k] += i3;
            }
          }
        }
        if (m > 1) {
          i += m - 1;
        }
        k++;
        i++;
      }
    }
    
    public int getResizeWeight(int paramInt)
    {
      return 1;
    }
    
    protected View getViewAtPosition(int paramInt, Rectangle paramRectangle)
    {
      int i = getViewCount();
      for (int j = 0; j < i; j++)
      {
        View localView2 = getView(j);
        int k = localView2.getStartOffset();
        int m = localView2.getEndOffset();
        if ((paramInt >= k) && (paramInt < m))
        {
          if (paramRectangle != null) {
            childAllocation(j, paramRectangle);
          }
          return localView2;
        }
      }
      if (paramInt == getEndOffset())
      {
        View localView1 = getView(i - 1);
        if (paramRectangle != null) {
          childAllocation(i - 1, paramRectangle);
        }
        return localView1;
      }
      return null;
    }
  }
}
