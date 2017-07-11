package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.CellRendererPane;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTableHeaderUI
  extends TableHeaderUI
{
  private static Cursor resizeCursor = Cursor.getPredefinedCursor(11);
  protected JTableHeader header;
  protected CellRendererPane rendererPane;
  protected MouseInputListener mouseInputListener;
  private int rolloverColumn = -1;
  private int selectedColumnIndex = 0;
  private static FocusListener focusListener = new FocusListener()
  {
    public void focusGained(FocusEvent paramAnonymousFocusEvent)
    {
      repaintHeader(paramAnonymousFocusEvent.getSource());
    }
    
    public void focusLost(FocusEvent paramAnonymousFocusEvent)
    {
      repaintHeader(paramAnonymousFocusEvent.getSource());
    }
    
    private void repaintHeader(Object paramAnonymousObject)
    {
      if ((paramAnonymousObject instanceof JTableHeader))
      {
        JTableHeader localJTableHeader = (JTableHeader)paramAnonymousObject;
        BasicTableHeaderUI localBasicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(localJTableHeader.getUI(), BasicTableHeaderUI.class);
        if (localBasicTableHeaderUI == null) {
          return;
        }
        localJTableHeader.repaint(localJTableHeader.getHeaderRect(localBasicTableHeaderUI.getSelectedColumnIndex()));
      }
    }
  };
  
  public BasicTableHeaderUI() {}
  
  protected MouseInputListener createMouseInputListener()
  {
    return new MouseInputHandler();
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicTableHeaderUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    this.header = ((JTableHeader)paramJComponent);
    this.rendererPane = new CellRendererPane();
    this.header.add(this.rendererPane);
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installColorsAndFont(this.header, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");
    LookAndFeel.installProperty(this.header, "opaque", Boolean.TRUE);
  }
  
  protected void installListeners()
  {
    this.mouseInputListener = createMouseInputListener();
    this.header.addMouseListener(this.mouseInputListener);
    this.header.addMouseMotionListener(this.mouseInputListener);
    this.header.addFocusListener(focusListener);
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = (InputMap)DefaultLookup.get(this.header, this, "TableHeader.ancestorInputMap");
    SwingUtilities.replaceUIInputMap(this.header, 1, localInputMap);
    LazyActionMap.installLazyActionMap(this.header, BasicTableHeaderUI.class, "TableHeader.actionMap");
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();
    this.header.remove(this.rendererPane);
    this.rendererPane = null;
    this.header = null;
  }
  
  protected void uninstallDefaults() {}
  
  protected void uninstallListeners()
  {
    this.header.removeMouseListener(this.mouseInputListener);
    this.header.removeMouseMotionListener(this.mouseInputListener);
    this.mouseInputListener = null;
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIInputMap(this.header, 0, null);
    SwingUtilities.replaceUIActionMap(this.header, null);
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("toggleSortOrder"));
    paramLazyActionMap.put(new Actions("selectColumnToLeft"));
    paramLazyActionMap.put(new Actions("selectColumnToRight"));
    paramLazyActionMap.put(new Actions("moveColumnLeft"));
    paramLazyActionMap.put(new Actions("moveColumnRight"));
    paramLazyActionMap.put(new Actions("resizeLeft"));
    paramLazyActionMap.put(new Actions("resizeRight"));
    paramLazyActionMap.put(new Actions("focusTable"));
  }
  
  protected int getRolloverColumn()
  {
    return this.rolloverColumn;
  }
  
  protected void rolloverColumnUpdated(int paramInt1, int paramInt2) {}
  
  private void updateRolloverColumn(MouseEvent paramMouseEvent)
  {
    if ((this.header.getDraggedColumn() == null) && (this.header.contains(paramMouseEvent.getPoint())))
    {
      int i = this.header.columnAtPoint(paramMouseEvent.getPoint());
      if (i != this.rolloverColumn)
      {
        int j = this.rolloverColumn;
        this.rolloverColumn = i;
        rolloverColumnUpdated(j, this.rolloverColumn);
      }
    }
  }
  
  private int selectNextColumn(boolean paramBoolean)
  {
    int i = getSelectedColumnIndex();
    if (i < this.header.getColumnModel().getColumnCount() - 1)
    {
      i++;
      if (paramBoolean) {
        selectColumn(i);
      }
    }
    return i;
  }
  
  private int selectPreviousColumn(boolean paramBoolean)
  {
    int i = getSelectedColumnIndex();
    if (i > 0)
    {
      i--;
      if (paramBoolean) {
        selectColumn(i);
      }
    }
    return i;
  }
  
  void selectColumn(int paramInt)
  {
    selectColumn(paramInt, true);
  }
  
  void selectColumn(int paramInt, boolean paramBoolean)
  {
    Rectangle localRectangle = this.header.getHeaderRect(this.selectedColumnIndex);
    this.header.repaint(localRectangle);
    this.selectedColumnIndex = paramInt;
    localRectangle = this.header.getHeaderRect(paramInt);
    this.header.repaint(localRectangle);
    if (paramBoolean) {
      scrollToColumn(paramInt);
    }
  }
  
  private void scrollToColumn(int paramInt)
  {
    Container localContainer;
    JTable localJTable;
    if ((this.header.getParent() == null) || ((localContainer = this.header.getParent().getParent()) == null) || (!(localContainer instanceof JScrollPane)) || ((localJTable = this.header.getTable()) == null)) {
      return;
    }
    Rectangle localRectangle1 = localJTable.getVisibleRect();
    Rectangle localRectangle2 = localJTable.getCellRect(0, paramInt, true);
    localRectangle1.x = localRectangle2.x;
    localRectangle1.width = localRectangle2.width;
    localJTable.scrollRectToVisible(localRectangle1);
  }
  
  private int getSelectedColumnIndex()
  {
    int i = this.header.getColumnModel().getColumnCount();
    if ((this.selectedColumnIndex >= i) && (i > 0)) {
      this.selectedColumnIndex = (i - 1);
    }
    return this.selectedColumnIndex;
  }
  
  private static boolean canResize(TableColumn paramTableColumn, JTableHeader paramJTableHeader)
  {
    return (paramTableColumn != null) && (paramJTableHeader.getResizingAllowed()) && (paramTableColumn.getResizable());
  }
  
  private int changeColumnWidth(TableColumn paramTableColumn, JTableHeader paramJTableHeader, int paramInt1, int paramInt2)
  {
    paramTableColumn.setWidth(paramInt2);
    Container localContainer;
    JTable localJTable;
    if ((paramJTableHeader.getParent() == null) || ((localContainer = paramJTableHeader.getParent().getParent()) == null) || (!(localContainer instanceof JScrollPane)) || ((localJTable = paramJTableHeader.getTable()) == null)) {
      return 0;
    }
    if ((!localContainer.getComponentOrientation().isLeftToRight()) && (!paramJTableHeader.getComponentOrientation().isLeftToRight()))
    {
      JViewport localJViewport = ((JScrollPane)localContainer).getViewport();
      int i = localJViewport.getWidth();
      int j = paramInt2 - paramInt1;
      int k = localJTable.getWidth() + j;
      Dimension localDimension = localJTable.getSize();
      localDimension.width += j;
      localJTable.setSize(localDimension);
      if ((k >= i) && (localJTable.getAutoResizeMode() == 0))
      {
        Point localPoint = localJViewport.getViewPosition();
        localPoint.x = Math.max(0, Math.min(k - i, localPoint.x + j));
        localJViewport.setViewPosition(localPoint);
        return j;
      }
    }
    return 0;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    int i = -1;
    TableColumnModel localTableColumnModel = this.header.getColumnModel();
    for (int j = 0; j < localTableColumnModel.getColumnCount(); j++)
    {
      TableColumn localTableColumn = localTableColumnModel.getColumn(j);
      Component localComponent = getHeaderRenderer(j);
      Dimension localDimension = localComponent.getPreferredSize();
      int k = localComponent.getBaseline(localDimension.width, paramInt2);
      if (k >= 0) {
        if (i == -1)
        {
          i = k;
        }
        else if (i != k)
        {
          i = -1;
          break;
        }
      }
    }
    return i;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (this.header.getColumnModel().getColumnCount() <= 0) {
      return;
    }
    boolean bool = this.header.getComponentOrientation().isLeftToRight();
    Rectangle localRectangle1 = paramGraphics.getClipBounds();
    Point localPoint1 = localRectangle1.getLocation();
    Point localPoint2 = new Point(localRectangle1.x + localRectangle1.width - 1, localRectangle1.y);
    TableColumnModel localTableColumnModel = this.header.getColumnModel();
    int i = this.header.columnAtPoint(bool ? localPoint1 : localPoint2);
    int j = this.header.columnAtPoint(bool ? localPoint2 : localPoint1);
    if (i == -1) {
      i = 0;
    }
    if (j == -1) {
      j = localTableColumnModel.getColumnCount() - 1;
    }
    TableColumn localTableColumn1 = this.header.getDraggedColumn();
    Rectangle localRectangle2 = this.header.getHeaderRect(bool ? i : j);
    int m;
    TableColumn localTableColumn2;
    int k;
    if (bool) {
      for (m = i; m <= j; m++)
      {
        localTableColumn2 = localTableColumnModel.getColumn(m);
        k = localTableColumn2.getWidth();
        localRectangle2.width = k;
        if (localTableColumn2 != localTableColumn1) {
          paintCell(paramGraphics, localRectangle2, m);
        }
        localRectangle2.x += k;
      }
    } else {
      for (m = j; m >= i; m--)
      {
        localTableColumn2 = localTableColumnModel.getColumn(m);
        k = localTableColumn2.getWidth();
        localRectangle2.width = k;
        if (localTableColumn2 != localTableColumn1) {
          paintCell(paramGraphics, localRectangle2, m);
        }
        localRectangle2.x += k;
      }
    }
    if (localTableColumn1 != null)
    {
      m = viewIndexForColumn(localTableColumn1);
      Rectangle localRectangle3 = this.header.getHeaderRect(m);
      paramGraphics.setColor(this.header.getParent().getBackground());
      paramGraphics.fillRect(localRectangle3.x, localRectangle3.y, localRectangle3.width, localRectangle3.height);
      localRectangle3.x += this.header.getDraggedDistance();
      paramGraphics.setColor(this.header.getBackground());
      paramGraphics.fillRect(localRectangle3.x, localRectangle3.y, localRectangle3.width, localRectangle3.height);
      paintCell(paramGraphics, localRectangle3, m);
    }
    this.rendererPane.removeAll();
  }
  
  private Component getHeaderRenderer(int paramInt)
  {
    TableColumn localTableColumn = this.header.getColumnModel().getColumn(paramInt);
    TableCellRenderer localTableCellRenderer = localTableColumn.getHeaderRenderer();
    if (localTableCellRenderer == null) {
      localTableCellRenderer = this.header.getDefaultRenderer();
    }
    boolean bool = (!this.header.isPaintingForPrint()) && (paramInt == getSelectedColumnIndex()) && (this.header.hasFocus());
    return localTableCellRenderer.getTableCellRendererComponent(this.header.getTable(), localTableColumn.getHeaderValue(), false, bool, -1, paramInt);
  }
  
  private void paintCell(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    Component localComponent = getHeaderRenderer(paramInt);
    this.rendererPane.paintComponent(paramGraphics, localComponent, this.header, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, true);
  }
  
  private int viewIndexForColumn(TableColumn paramTableColumn)
  {
    TableColumnModel localTableColumnModel = this.header.getColumnModel();
    for (int i = 0; i < localTableColumnModel.getColumnCount(); i++) {
      if (localTableColumnModel.getColumn(i) == paramTableColumn) {
        return i;
      }
    }
    return -1;
  }
  
  private int getHeaderHeight()
  {
    int i = 0;
    int j = 0;
    TableColumnModel localTableColumnModel = this.header.getColumnModel();
    for (int k = 0; k < localTableColumnModel.getColumnCount(); k++)
    {
      TableColumn localTableColumn = localTableColumnModel.getColumn(k);
      int m = localTableColumn.getHeaderRenderer() == null ? 1 : 0;
      if ((m == 0) || (j == 0))
      {
        Component localComponent = getHeaderRenderer(k);
        int n = localComponent.getPreferredSize().height;
        i = Math.max(i, n);
        if ((m != 0) && (n > 0))
        {
          Object localObject = localTableColumn.getHeaderValue();
          if (localObject != null)
          {
            localObject = localObject.toString();
            if ((localObject != null) && (!localObject.equals(""))) {
              j = 1;
            }
          }
        }
      }
    }
    return i;
  }
  
  private Dimension createHeaderSize(long paramLong)
  {
    if (paramLong > 2147483647L) {
      paramLong = 2147483647L;
    }
    return new Dimension((int)paramLong, getHeaderHeight());
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    long l = 0L;
    Enumeration localEnumeration = this.header.getColumnModel().getColumns();
    while (localEnumeration.hasMoreElements())
    {
      TableColumn localTableColumn = (TableColumn)localEnumeration.nextElement();
      l += localTableColumn.getMinWidth();
    }
    return createHeaderSize(l);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    long l = 0L;
    Enumeration localEnumeration = this.header.getColumnModel().getColumns();
    while (localEnumeration.hasMoreElements())
    {
      TableColumn localTableColumn = (TableColumn)localEnumeration.nextElement();
      l += localTableColumn.getPreferredWidth();
    }
    return createHeaderSize(l);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    long l = 0L;
    Enumeration localEnumeration = this.header.getColumnModel().getColumns();
    while (localEnumeration.hasMoreElements())
    {
      TableColumn localTableColumn = (TableColumn)localEnumeration.nextElement();
      l += localTableColumn.getMaxWidth();
    }
    return createHeaderSize(l);
  }
  
  private static class Actions
    extends UIAction
  {
    public static final String TOGGLE_SORT_ORDER = "toggleSortOrder";
    public static final String SELECT_COLUMN_TO_LEFT = "selectColumnToLeft";
    public static final String SELECT_COLUMN_TO_RIGHT = "selectColumnToRight";
    public static final String MOVE_COLUMN_LEFT = "moveColumnLeft";
    public static final String MOVE_COLUMN_RIGHT = "moveColumnRight";
    public static final String RESIZE_LEFT = "resizeLeft";
    public static final String RESIZE_RIGHT = "resizeRight";
    public static final String FOCUS_TABLE = "focusTable";
    
    public Actions(String paramString)
    {
      super();
    }
    
    public boolean isEnabled(Object paramObject)
    {
      if ((paramObject instanceof JTableHeader))
      {
        JTableHeader localJTableHeader = (JTableHeader)paramObject;
        TableColumnModel localTableColumnModel = localJTableHeader.getColumnModel();
        if (localTableColumnModel.getColumnCount() <= 0) {
          return false;
        }
        String str = getName();
        BasicTableHeaderUI localBasicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(localJTableHeader.getUI(), BasicTableHeaderUI.class);
        if (localBasicTableHeaderUI != null)
        {
          if (str == "moveColumnLeft") {
            return (localJTableHeader.getReorderingAllowed()) && (maybeMoveColumn(true, localJTableHeader, localBasicTableHeaderUI, false));
          }
          if (str == "moveColumnRight") {
            return (localJTableHeader.getReorderingAllowed()) && (maybeMoveColumn(false, localJTableHeader, localBasicTableHeaderUI, false));
          }
          if ((str == "resizeLeft") || (str == "resizeRight")) {
            return BasicTableHeaderUI.canResize(localTableColumnModel.getColumn(BasicTableHeaderUI.access$000(localBasicTableHeaderUI)), localJTableHeader);
          }
          if (str == "focusTable") {
            return localJTableHeader.getTable() != null;
          }
        }
      }
      return true;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JTableHeader localJTableHeader = (JTableHeader)paramActionEvent.getSource();
      BasicTableHeaderUI localBasicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(localJTableHeader.getUI(), BasicTableHeaderUI.class);
      if (localBasicTableHeaderUI == null) {
        return;
      }
      String str = getName();
      JTable localJTable;
      if ("toggleSortOrder" == str)
      {
        localJTable = localJTableHeader.getTable();
        RowSorter localRowSorter = localJTable == null ? null : localJTable.getRowSorter();
        if (localRowSorter != null)
        {
          int i = localBasicTableHeaderUI.getSelectedColumnIndex();
          i = localJTable.convertColumnIndexToModel(i);
          localRowSorter.toggleSortOrder(i);
        }
      }
      else if ("selectColumnToLeft" == str)
      {
        if (localJTableHeader.getComponentOrientation().isLeftToRight()) {
          localBasicTableHeaderUI.selectPreviousColumn(true);
        } else {
          localBasicTableHeaderUI.selectNextColumn(true);
        }
      }
      else if ("selectColumnToRight" == str)
      {
        if (localJTableHeader.getComponentOrientation().isLeftToRight()) {
          localBasicTableHeaderUI.selectNextColumn(true);
        } else {
          localBasicTableHeaderUI.selectPreviousColumn(true);
        }
      }
      else if ("moveColumnLeft" == str)
      {
        moveColumn(true, localJTableHeader, localBasicTableHeaderUI);
      }
      else if ("moveColumnRight" == str)
      {
        moveColumn(false, localJTableHeader, localBasicTableHeaderUI);
      }
      else if ("resizeLeft" == str)
      {
        resize(true, localJTableHeader, localBasicTableHeaderUI);
      }
      else if ("resizeRight" == str)
      {
        resize(false, localJTableHeader, localBasicTableHeaderUI);
      }
      else if ("focusTable" == str)
      {
        localJTable = localJTableHeader.getTable();
        if (localJTable != null) {
          localJTable.requestFocusInWindow();
        }
      }
    }
    
    private void moveColumn(boolean paramBoolean, JTableHeader paramJTableHeader, BasicTableHeaderUI paramBasicTableHeaderUI)
    {
      maybeMoveColumn(paramBoolean, paramJTableHeader, paramBasicTableHeaderUI, true);
    }
    
    private boolean maybeMoveColumn(boolean paramBoolean1, JTableHeader paramJTableHeader, BasicTableHeaderUI paramBasicTableHeaderUI, boolean paramBoolean2)
    {
      int i = paramBasicTableHeaderUI.getSelectedColumnIndex();
      int j;
      if (paramJTableHeader.getComponentOrientation().isLeftToRight()) {
        j = paramBoolean1 ? paramBasicTableHeaderUI.selectPreviousColumn(paramBoolean2) : paramBasicTableHeaderUI.selectNextColumn(paramBoolean2);
      } else {
        j = paramBoolean1 ? paramBasicTableHeaderUI.selectNextColumn(paramBoolean2) : paramBasicTableHeaderUI.selectPreviousColumn(paramBoolean2);
      }
      if (j != i) {
        if (paramBoolean2) {
          paramJTableHeader.getColumnModel().moveColumn(i, j);
        } else {
          return true;
        }
      }
      return false;
    }
    
    private void resize(boolean paramBoolean, JTableHeader paramJTableHeader, BasicTableHeaderUI paramBasicTableHeaderUI)
    {
      int i = paramBasicTableHeaderUI.getSelectedColumnIndex();
      TableColumn localTableColumn = paramJTableHeader.getColumnModel().getColumn(i);
      paramJTableHeader.setResizingColumn(localTableColumn);
      int j = localTableColumn.getWidth();
      int k = j;
      if (paramJTableHeader.getComponentOrientation().isLeftToRight()) {
        k += (paramBoolean ? -1 : 1);
      } else {
        k += (paramBoolean ? 1 : -1);
      }
      paramBasicTableHeaderUI.changeColumnWidth(localTableColumn, paramJTableHeader, j, k);
    }
  }
  
  public class MouseInputHandler
    implements MouseInputListener
  {
    private int mouseXOffset;
    private Cursor otherCursor = BasicTableHeaderUI.resizeCursor;
    
    public MouseInputHandler() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      if (!BasicTableHeaderUI.this.header.isEnabled()) {
        return;
      }
      if ((paramMouseEvent.getClickCount() % 2 == 1) && (SwingUtilities.isLeftMouseButton(paramMouseEvent)))
      {
        JTable localJTable = BasicTableHeaderUI.this.header.getTable();
        RowSorter localRowSorter;
        if ((localJTable != null) && ((localRowSorter = localJTable.getRowSorter()) != null))
        {
          int i = BasicTableHeaderUI.this.header.columnAtPoint(paramMouseEvent.getPoint());
          if (i != -1)
          {
            i = localJTable.convertColumnIndexToModel(i);
            localRowSorter.toggleSortOrder(i);
          }
        }
      }
    }
    
    private TableColumn getResizingColumn(Point paramPoint)
    {
      return getResizingColumn(paramPoint, BasicTableHeaderUI.this.header.columnAtPoint(paramPoint));
    }
    
    private TableColumn getResizingColumn(Point paramPoint, int paramInt)
    {
      if (paramInt == -1) {
        return null;
      }
      Rectangle localRectangle = BasicTableHeaderUI.this.header.getHeaderRect(paramInt);
      localRectangle.grow(-3, 0);
      if (localRectangle.contains(paramPoint)) {
        return null;
      }
      int i = localRectangle.x + localRectangle.width / 2;
      int j;
      if (BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight()) {
        j = paramPoint.x < i ? paramInt - 1 : paramInt;
      } else {
        j = paramPoint.x < i ? paramInt : paramInt - 1;
      }
      if (j == -1) {
        return null;
      }
      return BasicTableHeaderUI.this.header.getColumnModel().getColumn(j);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (!BasicTableHeaderUI.this.header.isEnabled()) {
        return;
      }
      BasicTableHeaderUI.this.header.setDraggedColumn(null);
      BasicTableHeaderUI.this.header.setResizingColumn(null);
      BasicTableHeaderUI.this.header.setDraggedDistance(0);
      Point localPoint = paramMouseEvent.getPoint();
      TableColumnModel localTableColumnModel = BasicTableHeaderUI.this.header.getColumnModel();
      int i = BasicTableHeaderUI.this.header.columnAtPoint(localPoint);
      if (i != -1)
      {
        TableColumn localTableColumn1 = getResizingColumn(localPoint, i);
        if (BasicTableHeaderUI.canResize(localTableColumn1, BasicTableHeaderUI.this.header))
        {
          BasicTableHeaderUI.this.header.setResizingColumn(localTableColumn1);
          if (BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight()) {
            this.mouseXOffset = (localPoint.x - localTableColumn1.getWidth());
          } else {
            this.mouseXOffset = (localPoint.x + localTableColumn1.getWidth());
          }
        }
        else if (BasicTableHeaderUI.this.header.getReorderingAllowed())
        {
          TableColumn localTableColumn2 = localTableColumnModel.getColumn(i);
          BasicTableHeaderUI.this.header.setDraggedColumn(localTableColumn2);
          this.mouseXOffset = localPoint.x;
        }
      }
      if (BasicTableHeaderUI.this.header.getReorderingAllowed())
      {
        int j = BasicTableHeaderUI.this.rolloverColumn;
        BasicTableHeaderUI.this.rolloverColumn = -1;
        BasicTableHeaderUI.this.rolloverColumnUpdated(j, BasicTableHeaderUI.this.rolloverColumn);
      }
    }
    
    private void swapCursor()
    {
      Cursor localCursor = BasicTableHeaderUI.this.header.getCursor();
      BasicTableHeaderUI.this.header.setCursor(this.otherCursor);
      this.otherCursor = localCursor;
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      if (!BasicTableHeaderUI.this.header.isEnabled()) {
        return;
      }
      if (BasicTableHeaderUI.canResize(getResizingColumn(paramMouseEvent.getPoint()), BasicTableHeaderUI.this.header) != (BasicTableHeaderUI.this.header.getCursor() == BasicTableHeaderUI.resizeCursor)) {
        swapCursor();
      }
      BasicTableHeaderUI.this.updateRolloverColumn(paramMouseEvent);
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (!BasicTableHeaderUI.this.header.isEnabled()) {
        return;
      }
      int i = paramMouseEvent.getX();
      TableColumn localTableColumn1 = BasicTableHeaderUI.this.header.getResizingColumn();
      TableColumn localTableColumn2 = BasicTableHeaderUI.this.header.getDraggedColumn();
      boolean bool = BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight();
      int k;
      if (localTableColumn1 != null)
      {
        int j = localTableColumn1.getWidth();
        if (bool) {
          k = i - this.mouseXOffset;
        } else {
          k = this.mouseXOffset - i;
        }
        this.mouseXOffset += BasicTableHeaderUI.this.changeColumnWidth(localTableColumn1, BasicTableHeaderUI.this.header, j, k);
      }
      else if (localTableColumn2 != null)
      {
        TableColumnModel localTableColumnModel = BasicTableHeaderUI.this.header.getColumnModel();
        k = i - this.mouseXOffset;
        int m = k < 0 ? -1 : 1;
        int n = BasicTableHeaderUI.this.viewIndexForColumn(localTableColumn2);
        int i1 = n + (bool ? m : -m);
        if ((0 <= i1) && (i1 < localTableColumnModel.getColumnCount()))
        {
          int i2 = localTableColumnModel.getColumn(i1).getWidth();
          if (Math.abs(k) > i2 / 2)
          {
            this.mouseXOffset += m * i2;
            BasicTableHeaderUI.this.header.setDraggedDistance(k - m * i2);
            int i3 = SwingUtilities2.convertColumnIndexToModel(BasicTableHeaderUI.this.header.getColumnModel(), BasicTableHeaderUI.this.getSelectedColumnIndex());
            localTableColumnModel.moveColumn(n, i1);
            BasicTableHeaderUI.this.selectColumn(SwingUtilities2.convertColumnIndexToView(BasicTableHeaderUI.this.header.getColumnModel(), i3), false);
            return;
          }
        }
        setDraggedDistance(k, n);
      }
      BasicTableHeaderUI.this.updateRolloverColumn(paramMouseEvent);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (!BasicTableHeaderUI.this.header.isEnabled()) {
        return;
      }
      setDraggedDistance(0, BasicTableHeaderUI.this.viewIndexForColumn(BasicTableHeaderUI.this.header.getDraggedColumn()));
      BasicTableHeaderUI.this.header.setResizingColumn(null);
      BasicTableHeaderUI.this.header.setDraggedColumn(null);
      BasicTableHeaderUI.this.updateRolloverColumn(paramMouseEvent);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      if (!BasicTableHeaderUI.this.header.isEnabled()) {
        return;
      }
      BasicTableHeaderUI.this.updateRolloverColumn(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      if (!BasicTableHeaderUI.this.header.isEnabled()) {
        return;
      }
      int i = BasicTableHeaderUI.this.rolloverColumn;
      BasicTableHeaderUI.this.rolloverColumn = -1;
      BasicTableHeaderUI.this.rolloverColumnUpdated(i, BasicTableHeaderUI.this.rolloverColumn);
    }
    
    private void setDraggedDistance(int paramInt1, int paramInt2)
    {
      BasicTableHeaderUI.this.header.setDraggedDistance(paramInt1);
      if (paramInt2 != -1) {
        BasicTableHeaderUI.this.header.getColumnModel().moveColumn(paramInt2, paramInt2);
      }
    }
  }
}
