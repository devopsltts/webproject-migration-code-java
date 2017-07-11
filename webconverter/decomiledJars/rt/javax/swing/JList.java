package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ListUI;
import javax.swing.text.Position.Bias;
import sun.swing.SwingUtilities2;
import sun.swing.SwingUtilities2.Section;

public class JList<E>
  extends JComponent
  implements Scrollable, Accessible
{
  private static final String uiClassID = "ListUI";
  public static final int VERTICAL = 0;
  public static final int VERTICAL_WRAP = 1;
  public static final int HORIZONTAL_WRAP = 2;
  private int fixedCellWidth = -1;
  private int fixedCellHeight = -1;
  private int horizontalScrollIncrement = -1;
  private E prototypeCellValue;
  private int visibleRowCount = 8;
  private Color selectionForeground;
  private Color selectionBackground;
  private boolean dragEnabled;
  private ListSelectionModel selectionModel;
  private ListModel<E> dataModel;
  private ListCellRenderer<? super E> cellRenderer;
  private ListSelectionListener selectionListener;
  private int layoutOrientation;
  private DropMode dropMode = DropMode.USE_SELECTION;
  private transient DropLocation dropLocation;
  
  public JList(ListModel<E> paramListModel)
  {
    if (paramListModel == null) {
      throw new IllegalArgumentException("dataModel must be non null");
    }
    ToolTipManager localToolTipManager = ToolTipManager.sharedInstance();
    localToolTipManager.registerComponent(this);
    this.layoutOrientation = 0;
    this.dataModel = paramListModel;
    this.selectionModel = createSelectionModel();
    setAutoscrolls(true);
    setOpaque(true);
    updateUI();
  }
  
  public JList(E[] paramArrayOfE)
  {
    this(new AbstractListModel()
    {
      public int getSize()
      {
        return JList.this.length;
      }
      
      public E getElementAt(int paramAnonymousInt)
      {
        return JList.this[paramAnonymousInt];
      }
    });
  }
  
  public JList(Vector<? extends E> paramVector)
  {
    this(new AbstractListModel()
    {
      public int getSize()
      {
        return JList.this.size();
      }
      
      public E getElementAt(int paramAnonymousInt)
      {
        return JList.this.elementAt(paramAnonymousInt);
      }
    });
  }
  
  public JList()
  {
    this(new AbstractListModel()
    {
      public int getSize()
      {
        return 0;
      }
      
      public E getElementAt(int paramAnonymousInt)
      {
        throw new IndexOutOfBoundsException("No Data Model");
      }
    });
  }
  
  public ListUI getUI()
  {
    return (ListUI)this.ui;
  }
  
  public void setUI(ListUI paramListUI)
  {
    super.setUI(paramListUI);
  }
  
  public void updateUI()
  {
    setUI((ListUI)UIManager.getUI(this));
    ListCellRenderer localListCellRenderer = getCellRenderer();
    if ((localListCellRenderer instanceof Component)) {
      SwingUtilities.updateComponentTreeUI((Component)localListCellRenderer);
    }
  }
  
  public String getUIClassID()
  {
    return "ListUI";
  }
  
  private void updateFixedCellSize()
  {
    ListCellRenderer localListCellRenderer = getCellRenderer();
    Object localObject = getPrototypeCellValue();
    if ((localListCellRenderer != null) && (localObject != null))
    {
      Component localComponent = localListCellRenderer.getListCellRendererComponent(this, localObject, 0, false, false);
      Font localFont = localComponent.getFont();
      localComponent.setFont(getFont());
      Dimension localDimension = localComponent.getPreferredSize();
      this.fixedCellWidth = localDimension.width;
      this.fixedCellHeight = localDimension.height;
      localComponent.setFont(localFont);
    }
  }
  
  public E getPrototypeCellValue()
  {
    return this.prototypeCellValue;
  }
  
  public void setPrototypeCellValue(E paramE)
  {
    Object localObject = this.prototypeCellValue;
    this.prototypeCellValue = paramE;
    if ((paramE != null) && (!paramE.equals(localObject))) {
      updateFixedCellSize();
    }
    firePropertyChange("prototypeCellValue", localObject, paramE);
  }
  
  public int getFixedCellWidth()
  {
    return this.fixedCellWidth;
  }
  
  public void setFixedCellWidth(int paramInt)
  {
    int i = this.fixedCellWidth;
    this.fixedCellWidth = paramInt;
    firePropertyChange("fixedCellWidth", i, this.fixedCellWidth);
  }
  
  public int getFixedCellHeight()
  {
    return this.fixedCellHeight;
  }
  
  public void setFixedCellHeight(int paramInt)
  {
    int i = this.fixedCellHeight;
    this.fixedCellHeight = paramInt;
    firePropertyChange("fixedCellHeight", i, this.fixedCellHeight);
  }
  
  @Transient
  public ListCellRenderer<? super E> getCellRenderer()
  {
    return this.cellRenderer;
  }
  
  public void setCellRenderer(ListCellRenderer<? super E> paramListCellRenderer)
  {
    ListCellRenderer localListCellRenderer = this.cellRenderer;
    this.cellRenderer = paramListCellRenderer;
    if ((paramListCellRenderer != null) && (!paramListCellRenderer.equals(localListCellRenderer))) {
      updateFixedCellSize();
    }
    firePropertyChange("cellRenderer", localListCellRenderer, paramListCellRenderer);
  }
  
  public Color getSelectionForeground()
  {
    return this.selectionForeground;
  }
  
  public void setSelectionForeground(Color paramColor)
  {
    Color localColor = this.selectionForeground;
    this.selectionForeground = paramColor;
    firePropertyChange("selectionForeground", localColor, paramColor);
  }
  
  public Color getSelectionBackground()
  {
    return this.selectionBackground;
  }
  
  public void setSelectionBackground(Color paramColor)
  {
    Color localColor = this.selectionBackground;
    this.selectionBackground = paramColor;
    firePropertyChange("selectionBackground", localColor, paramColor);
  }
  
  public int getVisibleRowCount()
  {
    return this.visibleRowCount;
  }
  
  public void setVisibleRowCount(int paramInt)
  {
    int i = this.visibleRowCount;
    this.visibleRowCount = Math.max(0, paramInt);
    firePropertyChange("visibleRowCount", i, paramInt);
  }
  
  public int getLayoutOrientation()
  {
    return this.layoutOrientation;
  }
  
  public void setLayoutOrientation(int paramInt)
  {
    int i = this.layoutOrientation;
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
      this.layoutOrientation = paramInt;
      firePropertyChange("layoutOrientation", i, paramInt);
      break;
    default: 
      throw new IllegalArgumentException("layoutOrientation must be one of: VERTICAL, HORIZONTAL_WRAP or VERTICAL_WRAP");
    }
  }
  
  public int getFirstVisibleIndex()
  {
    Rectangle localRectangle1 = getVisibleRect();
    int i;
    if (getComponentOrientation().isLeftToRight()) {
      i = locationToIndex(localRectangle1.getLocation());
    } else {
      i = locationToIndex(new Point(localRectangle1.x + localRectangle1.width - 1, localRectangle1.y));
    }
    if (i != -1)
    {
      Rectangle localRectangle2 = getCellBounds(i, i);
      if (localRectangle2 != null)
      {
        SwingUtilities.computeIntersection(localRectangle1.x, localRectangle1.y, localRectangle1.width, localRectangle1.height, localRectangle2);
        if ((localRectangle2.width == 0) || (localRectangle2.height == 0)) {
          i = -1;
        }
      }
    }
    return i;
  }
  
  public int getLastVisibleIndex()
  {
    boolean bool = getComponentOrientation().isLeftToRight();
    Rectangle localRectangle1 = getVisibleRect();
    Point localPoint1;
    if (bool) {
      localPoint1 = new Point(localRectangle1.x + localRectangle1.width - 1, localRectangle1.y + localRectangle1.height - 1);
    } else {
      localPoint1 = new Point(localRectangle1.x, localRectangle1.y + localRectangle1.height - 1);
    }
    int i = locationToIndex(localPoint1);
    if (i != -1)
    {
      Rectangle localRectangle2 = getCellBounds(i, i);
      if (localRectangle2 != null)
      {
        SwingUtilities.computeIntersection(localRectangle1.x, localRectangle1.y, localRectangle1.width, localRectangle1.height, localRectangle2);
        if ((localRectangle2.width == 0) || (localRectangle2.height == 0))
        {
          int j = getLayoutOrientation() == 2 ? 1 : 0;
          Point localPoint2 = j != 0 ? new Point(localPoint1.x, localRectangle1.y) : new Point(localRectangle1.x, localPoint1.y);
          int m = -1;
          int n = i;
          i = -1;
          int k;
          do
          {
            k = m;
            m = locationToIndex(localPoint2);
            if (m != -1)
            {
              localRectangle2 = getCellBounds(m, m);
              if ((m != n) && (localRectangle2 != null) && (localRectangle2.contains(localPoint2)))
              {
                i = m;
                if (j != 0)
                {
                  localPoint2.y = (localRectangle2.y + localRectangle2.height);
                  if (localPoint2.y >= localPoint1.y) {
                    k = m;
                  }
                }
                else
                {
                  localPoint2.x = (localRectangle2.x + localRectangle2.width);
                  if (localPoint2.x >= localPoint1.x) {
                    k = m;
                  }
                }
              }
              else
              {
                k = m;
              }
            }
          } while ((m != -1) && (k != m));
        }
      }
    }
    return i;
  }
  
  public void ensureIndexIsVisible(int paramInt)
  {
    Rectangle localRectangle = getCellBounds(paramInt, paramInt);
    if (localRectangle != null) {
      scrollRectToVisible(localRectangle);
    }
  }
  
  public void setDragEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) && (GraphicsEnvironment.isHeadless())) {
      throw new HeadlessException();
    }
    this.dragEnabled = paramBoolean;
  }
  
  public boolean getDragEnabled()
  {
    return this.dragEnabled;
  }
  
  public final void setDropMode(DropMode paramDropMode)
  {
    if (paramDropMode != null) {
      switch (6.$SwitchMap$javax$swing$DropMode[paramDropMode.ordinal()])
      {
      case 1: 
      case 2: 
      case 3: 
      case 4: 
        this.dropMode = paramDropMode;
        return;
      }
    }
    throw new IllegalArgumentException(paramDropMode + ": Unsupported drop mode for list");
  }
  
  public final DropMode getDropMode()
  {
    return this.dropMode;
  }
  
  DropLocation dropLocationForPoint(Point paramPoint)
  {
    DropLocation localDropLocation = null;
    Rectangle localRectangle = null;
    int i = locationToIndex(paramPoint);
    if (i != -1) {
      localRectangle = getCellBounds(i, i);
    }
    boolean bool1;
    switch (6.$SwitchMap$javax$swing$DropMode[this.dropMode.ordinal()])
    {
    case 1: 
    case 2: 
      localDropLocation = new DropLocation(paramPoint, (localRectangle != null) && (localRectangle.contains(paramPoint)) ? i : -1, false, null);
      break;
    case 3: 
      if (i == -1)
      {
        localDropLocation = new DropLocation(paramPoint, getModel().getSize(), true, null);
      }
      else
      {
        if (this.layoutOrientation == 2)
        {
          bool1 = getComponentOrientation().isLeftToRight();
          if (SwingUtilities2.liesInHorizontal(localRectangle, paramPoint, bool1, false) == SwingUtilities2.Section.TRAILING) {
            i++;
          } else if ((i == getModel().getSize() - 1) && (paramPoint.y >= localRectangle.y + localRectangle.height)) {
            i++;
          }
        }
        else if (SwingUtilities2.liesInVertical(localRectangle, paramPoint, false) == SwingUtilities2.Section.TRAILING)
        {
          i++;
        }
        localDropLocation = new DropLocation(paramPoint, i, true, null);
      }
      break;
    case 4: 
      if (i == -1)
      {
        localDropLocation = new DropLocation(paramPoint, getModel().getSize(), true, null);
      }
      else
      {
        bool1 = false;
        if (this.layoutOrientation == 2)
        {
          boolean bool2 = getComponentOrientation().isLeftToRight();
          SwingUtilities2.Section localSection2 = SwingUtilities2.liesInHorizontal(localRectangle, paramPoint, bool2, true);
          if (localSection2 == SwingUtilities2.Section.TRAILING)
          {
            i++;
            bool1 = true;
          }
          else if ((i == getModel().getSize() - 1) && (paramPoint.y >= localRectangle.y + localRectangle.height))
          {
            i++;
            bool1 = true;
          }
          else if (localSection2 == SwingUtilities2.Section.LEADING)
          {
            bool1 = true;
          }
        }
        else
        {
          SwingUtilities2.Section localSection1 = SwingUtilities2.liesInVertical(localRectangle, paramPoint, true);
          if (localSection1 == SwingUtilities2.Section.LEADING)
          {
            bool1 = true;
          }
          else if (localSection1 == SwingUtilities2.Section.TRAILING)
          {
            i++;
            bool1 = true;
          }
        }
        localDropLocation = new DropLocation(paramPoint, i, bool1, null);
      }
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError("Unexpected drop mode");
      }
      break;
    }
    return localDropLocation;
  }
  
  Object setDropLocation(TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean)
  {
    Object localObject = null;
    DropLocation localDropLocation1 = (DropLocation)paramDropLocation;
    if (this.dropMode == DropMode.USE_SELECTION) {
      if (localDropLocation1 == null)
      {
        if ((!paramBoolean) && (paramObject != null))
        {
          setSelectedIndices(((int[][])(int[][])paramObject)[0]);
          int i = ((int[][])(int[][])paramObject)[1][0];
          int k = ((int[][])(int[][])paramObject)[1][1];
          SwingUtilities2.setLeadAnchorWithoutSelection(getSelectionModel(), k, i);
        }
      }
      else
      {
        if (this.dropLocation == null)
        {
          int[] arrayOfInt = getSelectedIndices();
          localObject = new int[][] { arrayOfInt, { getAnchorSelectionIndex(), getLeadSelectionIndex() } };
        }
        else
        {
          localObject = paramObject;
        }
        int j = localDropLocation1.getIndex();
        if (j == -1)
        {
          clearSelection();
          getSelectionModel().setAnchorSelectionIndex(-1);
          getSelectionModel().setLeadSelectionIndex(-1);
        }
        else
        {
          setSelectionInterval(j, j);
        }
      }
    }
    DropLocation localDropLocation2 = this.dropLocation;
    this.dropLocation = localDropLocation1;
    firePropertyChange("dropLocation", localDropLocation2, this.dropLocation);
    return localObject;
  }
  
  public final DropLocation getDropLocation()
  {
    return this.dropLocation;
  }
  
  public int getNextMatch(String paramString, int paramInt, Position.Bias paramBias)
  {
    ListModel localListModel = getModel();
    int i = localListModel.getSize();
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    if ((paramInt < 0) || (paramInt >= i)) {
      throw new IllegalArgumentException();
    }
    paramString = paramString.toUpperCase();
    int j = paramBias == Position.Bias.Forward ? 1 : -1;
    int k = paramInt;
    do
    {
      Object localObject = localListModel.getElementAt(k);
      if (localObject != null)
      {
        String str;
        if ((localObject instanceof String))
        {
          str = ((String)localObject).toUpperCase();
        }
        else
        {
          str = localObject.toString();
          if (str != null) {
            str = str.toUpperCase();
          }
        }
        if ((str != null) && (str.startsWith(paramString))) {
          return k;
        }
      }
      k = (k + j + i) % i;
    } while (k != paramInt);
    return -1;
  }
  
  public String getToolTipText(MouseEvent paramMouseEvent)
  {
    if (paramMouseEvent != null)
    {
      Point localPoint = paramMouseEvent.getPoint();
      int i = locationToIndex(localPoint);
      ListCellRenderer localListCellRenderer = getCellRenderer();
      Rectangle localRectangle;
      if ((i != -1) && (localListCellRenderer != null) && ((localRectangle = getCellBounds(i, i)) != null) && (localRectangle.contains(localPoint.x, localPoint.y)))
      {
        ListSelectionModel localListSelectionModel = getSelectionModel();
        Component localComponent = localListCellRenderer.getListCellRendererComponent(this, getModel().getElementAt(i), i, localListSelectionModel.isSelectedIndex(i), (hasFocus()) && (localListSelectionModel.getLeadSelectionIndex() == i));
        if ((localComponent instanceof JComponent))
        {
          localPoint.translate(-localRectangle.x, -localRectangle.y);
          MouseEvent localMouseEvent = new MouseEvent(localComponent, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), localPoint.x, localPoint.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
          String str = ((JComponent)localComponent).getToolTipText(localMouseEvent);
          if (str != null) {
            return str;
          }
        }
      }
    }
    return super.getToolTipText();
  }
  
  public int locationToIndex(Point paramPoint)
  {
    ListUI localListUI = getUI();
    return localListUI != null ? localListUI.locationToIndex(this, paramPoint) : -1;
  }
  
  public Point indexToLocation(int paramInt)
  {
    ListUI localListUI = getUI();
    return localListUI != null ? localListUI.indexToLocation(this, paramInt) : null;
  }
  
  public Rectangle getCellBounds(int paramInt1, int paramInt2)
  {
    ListUI localListUI = getUI();
    return localListUI != null ? localListUI.getCellBounds(this, paramInt1, paramInt2) : null;
  }
  
  public ListModel<E> getModel()
  {
    return this.dataModel;
  }
  
  public void setModel(ListModel<E> paramListModel)
  {
    if (paramListModel == null) {
      throw new IllegalArgumentException("model must be non null");
    }
    ListModel localListModel = this.dataModel;
    this.dataModel = paramListModel;
    firePropertyChange("model", localListModel, this.dataModel);
    clearSelection();
  }
  
  public void setListData(final E[] paramArrayOfE)
  {
    setModel(new AbstractListModel()
    {
      public int getSize()
      {
        return paramArrayOfE.length;
      }
      
      public E getElementAt(int paramAnonymousInt)
      {
        return paramArrayOfE[paramAnonymousInt];
      }
    });
  }
  
  public void setListData(final Vector<? extends E> paramVector)
  {
    setModel(new AbstractListModel()
    {
      public int getSize()
      {
        return paramVector.size();
      }
      
      public E getElementAt(int paramAnonymousInt)
      {
        return paramVector.elementAt(paramAnonymousInt);
      }
    });
  }
  
  protected ListSelectionModel createSelectionModel()
  {
    return new DefaultListSelectionModel();
  }
  
  public ListSelectionModel getSelectionModel()
  {
    return this.selectionModel;
  }
  
  protected void fireSelectionValueChanged(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    ListSelectionEvent localListSelectionEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ListSelectionListener.class)
      {
        if (localListSelectionEvent == null) {
          localListSelectionEvent = new ListSelectionEvent(this, paramInt1, paramInt2, paramBoolean);
        }
        ((ListSelectionListener)arrayOfObject[(i + 1)]).valueChanged(localListSelectionEvent);
      }
    }
  }
  
  public void addListSelectionListener(ListSelectionListener paramListSelectionListener)
  {
    if (this.selectionListener == null)
    {
      this.selectionListener = new ListSelectionHandler(null);
      getSelectionModel().addListSelectionListener(this.selectionListener);
    }
    this.listenerList.add(ListSelectionListener.class, paramListSelectionListener);
  }
  
  public void removeListSelectionListener(ListSelectionListener paramListSelectionListener)
  {
    this.listenerList.remove(ListSelectionListener.class, paramListSelectionListener);
  }
  
  public ListSelectionListener[] getListSelectionListeners()
  {
    return (ListSelectionListener[])this.listenerList.getListeners(ListSelectionListener.class);
  }
  
  public void setSelectionModel(ListSelectionModel paramListSelectionModel)
  {
    if (paramListSelectionModel == null) {
      throw new IllegalArgumentException("selectionModel must be non null");
    }
    if (this.selectionListener != null)
    {
      this.selectionModel.removeListSelectionListener(this.selectionListener);
      paramListSelectionModel.addListSelectionListener(this.selectionListener);
    }
    ListSelectionModel localListSelectionModel = this.selectionModel;
    this.selectionModel = paramListSelectionModel;
    firePropertyChange("selectionModel", localListSelectionModel, paramListSelectionModel);
  }
  
  public void setSelectionMode(int paramInt)
  {
    getSelectionModel().setSelectionMode(paramInt);
  }
  
  public int getSelectionMode()
  {
    return getSelectionModel().getSelectionMode();
  }
  
  public int getAnchorSelectionIndex()
  {
    return getSelectionModel().getAnchorSelectionIndex();
  }
  
  public int getLeadSelectionIndex()
  {
    return getSelectionModel().getLeadSelectionIndex();
  }
  
  public int getMinSelectionIndex()
  {
    return getSelectionModel().getMinSelectionIndex();
  }
  
  public int getMaxSelectionIndex()
  {
    return getSelectionModel().getMaxSelectionIndex();
  }
  
  public boolean isSelectedIndex(int paramInt)
  {
    return getSelectionModel().isSelectedIndex(paramInt);
  }
  
  public boolean isSelectionEmpty()
  {
    return getSelectionModel().isSelectionEmpty();
  }
  
  public void clearSelection()
  {
    getSelectionModel().clearSelection();
  }
  
  public void setSelectionInterval(int paramInt1, int paramInt2)
  {
    getSelectionModel().setSelectionInterval(paramInt1, paramInt2);
  }
  
  public void addSelectionInterval(int paramInt1, int paramInt2)
  {
    getSelectionModel().addSelectionInterval(paramInt1, paramInt2);
  }
  
  public void removeSelectionInterval(int paramInt1, int paramInt2)
  {
    getSelectionModel().removeSelectionInterval(paramInt1, paramInt2);
  }
  
  public void setValueIsAdjusting(boolean paramBoolean)
  {
    getSelectionModel().setValueIsAdjusting(paramBoolean);
  }
  
  public boolean getValueIsAdjusting()
  {
    return getSelectionModel().getValueIsAdjusting();
  }
  
  @Transient
  public int[] getSelectedIndices()
  {
    ListSelectionModel localListSelectionModel = getSelectionModel();
    int i = localListSelectionModel.getMinSelectionIndex();
    int j = localListSelectionModel.getMaxSelectionIndex();
    if ((i < 0) || (j < 0)) {
      return new int[0];
    }
    int[] arrayOfInt1 = new int[1 + (j - i)];
    int k = 0;
    for (int m = i; m <= j; m++) {
      if (localListSelectionModel.isSelectedIndex(m)) {
        arrayOfInt1[(k++)] = m;
      }
    }
    int[] arrayOfInt2 = new int[k];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, k);
    return arrayOfInt2;
  }
  
  public void setSelectedIndex(int paramInt)
  {
    if (paramInt >= getModel().getSize()) {
      return;
    }
    getSelectionModel().setSelectionInterval(paramInt, paramInt);
  }
  
  public void setSelectedIndices(int[] paramArrayOfInt)
  {
    ListSelectionModel localListSelectionModel = getSelectionModel();
    localListSelectionModel.clearSelection();
    int i = getModel().getSize();
    for (int m : paramArrayOfInt) {
      if (m < i) {
        localListSelectionModel.addSelectionInterval(m, m);
      }
    }
  }
  
  @Deprecated
  public Object[] getSelectedValues()
  {
    ListSelectionModel localListSelectionModel = getSelectionModel();
    ListModel localListModel = getModel();
    int i = localListSelectionModel.getMinSelectionIndex();
    int j = localListSelectionModel.getMaxSelectionIndex();
    if ((i < 0) || (j < 0)) {
      return new Object[0];
    }
    Object[] arrayOfObject1 = new Object[1 + (j - i)];
    int k = 0;
    for (int m = i; m <= j; m++) {
      if (localListSelectionModel.isSelectedIndex(m)) {
        arrayOfObject1[(k++)] = localListModel.getElementAt(m);
      }
    }
    Object[] arrayOfObject2 = new Object[k];
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, k);
    return arrayOfObject2;
  }
  
  public List<E> getSelectedValuesList()
  {
    ListSelectionModel localListSelectionModel = getSelectionModel();
    ListModel localListModel = getModel();
    int i = localListSelectionModel.getMinSelectionIndex();
    int j = localListSelectionModel.getMaxSelectionIndex();
    if ((i < 0) || (j < 0)) {
      return Collections.emptyList();
    }
    ArrayList localArrayList = new ArrayList();
    for (int k = i; k <= j; k++) {
      if (localListSelectionModel.isSelectedIndex(k)) {
        localArrayList.add(localListModel.getElementAt(k));
      }
    }
    return localArrayList;
  }
  
  public int getSelectedIndex()
  {
    return getMinSelectionIndex();
  }
  
  public E getSelectedValue()
  {
    int i = getMinSelectionIndex();
    return i == -1 ? null : getModel().getElementAt(i);
  }
  
  public void setSelectedValue(Object paramObject, boolean paramBoolean)
  {
    if (paramObject == null)
    {
      setSelectedIndex(-1);
    }
    else if (!paramObject.equals(getSelectedValue()))
    {
      ListModel localListModel = getModel();
      int i = 0;
      int j = localListModel.getSize();
      while (i < j)
      {
        if (paramObject.equals(localListModel.getElementAt(i)))
        {
          setSelectedIndex(i);
          if (paramBoolean) {
            ensureIndexIsVisible(i);
          }
          repaint();
          return;
        }
        i++;
      }
      setSelectedIndex(-1);
    }
    repaint();
  }
  
  private void checkScrollableParameters(Rectangle paramRectangle, int paramInt)
  {
    if (paramRectangle == null) {
      throw new IllegalArgumentException("visibleRect must be non-null");
    }
    switch (paramInt)
    {
    case 0: 
    case 1: 
      break;
    default: 
      throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
    }
  }
  
  public Dimension getPreferredScrollableViewportSize()
  {
    if (getLayoutOrientation() != 0) {
      return getPreferredSize();
    }
    Insets localInsets = getInsets();
    int i = localInsets.left + localInsets.right;
    int j = localInsets.top + localInsets.bottom;
    int k = getVisibleRowCount();
    int m = getFixedCellWidth();
    int n = getFixedCellHeight();
    int i1;
    int i2;
    if ((m > 0) && (n > 0))
    {
      i1 = m + i;
      i2 = k * n + j;
      return new Dimension(i1, i2);
    }
    if (getModel().getSize() > 0)
    {
      i1 = getPreferredSize().width;
      Rectangle localRectangle = getCellBounds(0, 0);
      if (localRectangle != null) {
        i2 = k * localRectangle.height + j;
      } else {
        i2 = 1;
      }
      return new Dimension(i1, i2);
    }
    m = m > 0 ? m : 256;
    n = n > 0 ? n : 16;
    return new Dimension(m, n * k);
  }
  
  public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    checkScrollableParameters(paramRectangle, paramInt1);
    Point localPoint;
    if (paramInt1 == 1)
    {
      int i = locationToIndex(paramRectangle.getLocation());
      if (i == -1) {
        return 0;
      }
      if (paramInt2 > 0)
      {
        localRectangle1 = getCellBounds(i, i);
        return localRectangle1 == null ? 0 : localRectangle1.height - (paramRectangle.y - localRectangle1.y);
      }
      Rectangle localRectangle1 = getCellBounds(i, i);
      if ((localRectangle1.y == paramRectangle.y) && (i == 0)) {
        return 0;
      }
      if (localRectangle1.y == paramRectangle.y)
      {
        localPoint = localRectangle1.getLocation();
        localPoint.y -= 1;
        int k = locationToIndex(localPoint);
        Rectangle localRectangle3 = getCellBounds(k, k);
        if ((localRectangle3 == null) || (localRectangle3.y >= localRectangle1.y)) {
          return 0;
        }
        return localRectangle3.height;
      }
      return paramRectangle.y - localRectangle1.y;
    }
    if ((paramInt1 == 0) && (getLayoutOrientation() != 0))
    {
      boolean bool = getComponentOrientation().isLeftToRight();
      if (bool) {
        localPoint = paramRectangle.getLocation();
      } else {
        localPoint = new Point(paramRectangle.x + paramRectangle.width - 1, paramRectangle.y);
      }
      int j = locationToIndex(localPoint);
      if (j != -1)
      {
        Rectangle localRectangle2 = getCellBounds(j, j);
        if ((localRectangle2 != null) && (localRectangle2.contains(localPoint)))
        {
          int m;
          int n;
          if (bool)
          {
            m = paramRectangle.x;
            n = localRectangle2.x;
          }
          else
          {
            m = paramRectangle.x + paramRectangle.width;
            n = localRectangle2.x + localRectangle2.width;
          }
          if (n != m)
          {
            if (paramInt2 < 0) {
              return Math.abs(m - n);
            }
            if (bool) {
              return n + localRectangle2.width - m;
            }
            return m - localRectangle2.x;
          }
          return localRectangle2.width;
        }
      }
    }
    Font localFont = getFont();
    return localFont != null ? localFont.getSize() : 1;
  }
  
  public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    checkScrollableParameters(paramRectangle, paramInt1);
    int j;
    int k;
    Rectangle localRectangle3;
    if (paramInt1 == 1)
    {
      int i = paramRectangle.height;
      if (paramInt2 > 0)
      {
        j = locationToIndex(new Point(paramRectangle.x, paramRectangle.y + paramRectangle.height - 1));
        if (j != -1)
        {
          Rectangle localRectangle1 = getCellBounds(j, j);
          if (localRectangle1 != null)
          {
            i = localRectangle1.y - paramRectangle.y;
            if ((i == 0) && (j < getModel().getSize() - 1)) {
              i = localRectangle1.height;
            }
          }
        }
      }
      else
      {
        j = locationToIndex(new Point(paramRectangle.x, paramRectangle.y - paramRectangle.height));
        k = getFirstVisibleIndex();
        if (j != -1)
        {
          if (k == -1) {
            k = locationToIndex(paramRectangle.getLocation());
          }
          Rectangle localRectangle2 = getCellBounds(j, j);
          localRectangle3 = getCellBounds(k, k);
          if ((localRectangle2 != null) && (localRectangle3 != null))
          {
            while ((localRectangle2.y + paramRectangle.height < localRectangle3.y + localRectangle3.height) && (localRectangle2.y < localRectangle3.y))
            {
              j++;
              localRectangle2 = getCellBounds(j, j);
            }
            i = paramRectangle.y - localRectangle2.y;
            if ((i <= 0) && (localRectangle2.y > 0))
            {
              j--;
              localRectangle2 = getCellBounds(j, j);
              if (localRectangle2 != null) {
                i = paramRectangle.y - localRectangle2.y;
              }
            }
          }
        }
      }
      return i;
    }
    if ((paramInt1 == 0) && (getLayoutOrientation() != 0))
    {
      boolean bool = getComponentOrientation().isLeftToRight();
      j = paramRectangle.width;
      int m;
      if (paramInt2 > 0)
      {
        k = paramRectangle.x + (bool ? paramRectangle.width - 1 : 0);
        m = locationToIndex(new Point(k, paramRectangle.y));
        if (m != -1)
        {
          localRectangle3 = getCellBounds(m, m);
          if (localRectangle3 != null)
          {
            if (bool) {
              j = localRectangle3.x - paramRectangle.x;
            } else {
              j = paramRectangle.x + paramRectangle.width - (localRectangle3.x + localRectangle3.width);
            }
            if (j < 0) {
              j += localRectangle3.width;
            } else if ((j == 0) && (m < getModel().getSize() - 1)) {
              j = localRectangle3.width;
            }
          }
        }
      }
      else
      {
        k = paramRectangle.x + (bool ? -paramRectangle.width : paramRectangle.width - 1 + paramRectangle.width);
        m = locationToIndex(new Point(k, paramRectangle.y));
        if (m != -1)
        {
          localRectangle3 = getCellBounds(m, m);
          if (localRectangle3 != null)
          {
            int n = localRectangle3.x + localRectangle3.width;
            if (bool)
            {
              if ((localRectangle3.x < paramRectangle.x - paramRectangle.width) && (n < paramRectangle.x)) {
                j = paramRectangle.x - n;
              } else {
                j = paramRectangle.x - localRectangle3.x;
              }
            }
            else
            {
              int i1 = paramRectangle.x + paramRectangle.width;
              if ((n > i1 + paramRectangle.width) && (localRectangle3.x > i1)) {
                j = localRectangle3.x - i1;
              } else {
                j = n - i1;
              }
            }
          }
        }
      }
      return j;
    }
    return paramRectangle.width;
  }
  
  public boolean getScrollableTracksViewportWidth()
  {
    if ((getLayoutOrientation() == 2) && (getVisibleRowCount() <= 0)) {
      return true;
    }
    Container localContainer = SwingUtilities.getUnwrappedParent(this);
    if ((localContainer instanceof JViewport)) {
      return localContainer.getWidth() > getPreferredSize().width;
    }
    return false;
  }
  
  public boolean getScrollableTracksViewportHeight()
  {
    if ((getLayoutOrientation() == 1) && (getVisibleRowCount() <= 0)) {
      return true;
    }
    Container localContainer = SwingUtilities.getUnwrappedParent(this);
    if ((localContainer instanceof JViewport)) {
      return localContainer.getHeight() > getPreferredSize().height;
    }
    return false;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ListUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (this.ui != null)) {
        this.ui.installUI(this);
      }
    }
  }
  
  protected String paramString()
  {
    String str1 = this.selectionForeground != null ? this.selectionForeground.toString() : "";
    String str2 = this.selectionBackground != null ? this.selectionBackground.toString() : "";
    return super.paramString() + ",fixedCellHeight=" + this.fixedCellHeight + ",fixedCellWidth=" + this.fixedCellWidth + ",horizontalScrollIncrement=" + this.horizontalScrollIncrement + ",selectionBackground=" + str2 + ",selectionForeground=" + str1 + ",visibleRowCount=" + this.visibleRowCount + ",layoutOrientation=" + this.layoutOrientation;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleJList();
    }
    return this.accessibleContext;
  }
  
  protected class AccessibleJList
    extends JComponent.AccessibleJComponent
    implements AccessibleSelection, PropertyChangeListener, ListSelectionListener, ListDataListener
  {
    int leadSelectionIndex;
    
    public AccessibleJList()
    {
      super();
      JList.this.addPropertyChangeListener(this);
      JList.this.getSelectionModel().addListSelectionListener(this);
      JList.this.getModel().addListDataListener(this);
      this.leadSelectionIndex = JList.this.getLeadSelectionIndex();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      Object localObject1 = paramPropertyChangeEvent.getOldValue();
      Object localObject2 = paramPropertyChangeEvent.getNewValue();
      if (str.compareTo("model") == 0)
      {
        if ((localObject1 != null) && ((localObject1 instanceof ListModel))) {
          ((ListModel)localObject1).removeListDataListener(this);
        }
        if ((localObject2 != null) && ((localObject2 instanceof ListModel))) {
          ((ListModel)localObject2).addListDataListener(this);
        }
      }
      else if (str.compareTo("selectionModel") == 0)
      {
        if ((localObject1 != null) && ((localObject1 instanceof ListSelectionModel))) {
          ((ListSelectionModel)localObject1).removeListSelectionListener(this);
        }
        if ((localObject2 != null) && ((localObject2 instanceof ListSelectionModel))) {
          ((ListSelectionModel)localObject2).addListSelectionListener(this);
        }
        firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
      }
    }
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent)
    {
      int i = this.leadSelectionIndex;
      this.leadSelectionIndex = JList.this.getLeadSelectionIndex();
      if (i != this.leadSelectionIndex)
      {
        localObject1 = i >= 0 ? getAccessibleChild(i) : null;
        localObject2 = this.leadSelectionIndex >= 0 ? getAccessibleChild(this.leadSelectionIndex) : null;
        firePropertyChange("AccessibleActiveDescendant", localObject1, localObject2);
      }
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
      firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
      Object localObject1 = getAccessibleStateSet();
      Object localObject2 = JList.this.getSelectionModel();
      if (((ListSelectionModel)localObject2).getSelectionMode() != 0)
      {
        if (!((AccessibleStateSet)localObject1).contains(AccessibleState.MULTISELECTABLE))
        {
          ((AccessibleStateSet)localObject1).add(AccessibleState.MULTISELECTABLE);
          firePropertyChange("AccessibleState", null, AccessibleState.MULTISELECTABLE);
        }
      }
      else if (((AccessibleStateSet)localObject1).contains(AccessibleState.MULTISELECTABLE))
      {
        ((AccessibleStateSet)localObject1).remove(AccessibleState.MULTISELECTABLE);
        firePropertyChange("AccessibleState", AccessibleState.MULTISELECTABLE, null);
      }
    }
    
    public void intervalAdded(ListDataEvent paramListDataEvent)
    {
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    public void intervalRemoved(ListDataEvent paramListDataEvent)
    {
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    public void contentsChanged(ListDataEvent paramListDataEvent)
    {
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (JList.this.selectionModel.getSelectionMode() != 0) {
        localAccessibleStateSet.add(AccessibleState.MULTISELECTABLE);
      }
      return localAccessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.LIST;
    }
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      int i = JList.this.locationToIndex(paramPoint);
      if (i >= 0) {
        return new AccessibleJListChild(JList.this, i);
      }
      return null;
    }
    
    public int getAccessibleChildrenCount()
    {
      return JList.this.getModel().getSize();
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      if (paramInt >= JList.this.getModel().getSize()) {
        return null;
      }
      return new AccessibleJListChild(JList.this, paramInt);
    }
    
    public AccessibleSelection getAccessibleSelection()
    {
      return this;
    }
    
    public int getAccessibleSelectionCount()
    {
      return JList.this.getSelectedIndices().length;
    }
    
    public Accessible getAccessibleSelection(int paramInt)
    {
      int i = getAccessibleSelectionCount();
      if ((paramInt < 0) || (paramInt >= i)) {
        return null;
      }
      return getAccessibleChild(JList.this.getSelectedIndices()[paramInt]);
    }
    
    public boolean isAccessibleChildSelected(int paramInt)
    {
      return JList.this.isSelectedIndex(paramInt);
    }
    
    public void addAccessibleSelection(int paramInt)
    {
      JList.this.addSelectionInterval(paramInt, paramInt);
    }
    
    public void removeAccessibleSelection(int paramInt)
    {
      JList.this.removeSelectionInterval(paramInt, paramInt);
    }
    
    public void clearAccessibleSelection()
    {
      JList.this.clearSelection();
    }
    
    public void selectAllAccessibleSelection()
    {
      JList.this.addSelectionInterval(0, getAccessibleChildrenCount() - 1);
    }
    
    protected class AccessibleJListChild
      extends AccessibleContext
      implements Accessible, AccessibleComponent
    {
      private JList<E> parent = null;
      private int indexInParent;
      private Component component = null;
      private AccessibleContext accessibleContext = null;
      private ListModel<E> listModel;
      private ListCellRenderer<? super E> cellRenderer = null;
      
      public AccessibleJListChild(int paramInt)
      {
        this.parent = paramInt;
        setAccessibleParent(paramInt);
        int i;
        this.indexInParent = i;
        if (paramInt != null)
        {
          this.listModel = paramInt.getModel();
          this.cellRenderer = paramInt.getCellRenderer();
        }
      }
      
      private Component getCurrentComponent()
      {
        return getComponentAtIndex(this.indexInParent);
      }
      
      private AccessibleContext getCurrentAccessibleContext()
      {
        Component localComponent = getComponentAtIndex(this.indexInParent);
        if ((localComponent instanceof Accessible)) {
          return localComponent.getAccessibleContext();
        }
        return null;
      }
      
      private Component getComponentAtIndex(int paramInt)
      {
        if ((paramInt < 0) || (paramInt >= this.listModel.getSize())) {
          return null;
        }
        if ((this.parent != null) && (this.listModel != null) && (this.cellRenderer != null))
        {
          Object localObject = this.listModel.getElementAt(paramInt);
          boolean bool1 = this.parent.isSelectedIndex(paramInt);
          boolean bool2 = (this.parent.isFocusOwner()) && (paramInt == this.parent.getLeadSelectionIndex());
          return this.cellRenderer.getListCellRendererComponent(this.parent, localObject, paramInt, bool1, bool2);
        }
        return null;
      }
      
      public AccessibleContext getAccessibleContext()
      {
        return this;
      }
      
      public String getAccessibleName()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleName();
        }
        return null;
      }
      
      public void setAccessibleName(String paramString)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.setAccessibleName(paramString);
        }
      }
      
      public String getAccessibleDescription()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleDescription();
        }
        return null;
      }
      
      public void setAccessibleDescription(String paramString)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.setAccessibleDescription(paramString);
        }
      }
      
      public AccessibleRole getAccessibleRole()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleRole();
        }
        return null;
      }
      
      public AccessibleStateSet getAccessibleStateSet()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        AccessibleStateSet localAccessibleStateSet;
        if (localAccessibleContext != null) {
          localAccessibleStateSet = localAccessibleContext.getAccessibleStateSet();
        } else {
          localAccessibleStateSet = new AccessibleStateSet();
        }
        localAccessibleStateSet.add(AccessibleState.SELECTABLE);
        if ((this.parent.isFocusOwner()) && (this.indexInParent == this.parent.getLeadSelectionIndex())) {
          localAccessibleStateSet.add(AccessibleState.ACTIVE);
        }
        if (this.parent.isSelectedIndex(this.indexInParent)) {
          localAccessibleStateSet.add(AccessibleState.SELECTED);
        }
        if (isShowing()) {
          localAccessibleStateSet.add(AccessibleState.SHOWING);
        } else if (localAccessibleStateSet.contains(AccessibleState.SHOWING)) {
          localAccessibleStateSet.remove(AccessibleState.SHOWING);
        }
        if (isVisible()) {
          localAccessibleStateSet.add(AccessibleState.VISIBLE);
        } else if (localAccessibleStateSet.contains(AccessibleState.VISIBLE)) {
          localAccessibleStateSet.remove(AccessibleState.VISIBLE);
        }
        localAccessibleStateSet.add(AccessibleState.TRANSIENT);
        return localAccessibleStateSet;
      }
      
      public int getAccessibleIndexInParent()
      {
        return this.indexInParent;
      }
      
      public int getAccessibleChildrenCount()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleChildrenCount();
        }
        return 0;
      }
      
      public Accessible getAccessibleChild(int paramInt)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null)
        {
          Accessible localAccessible = localAccessibleContext.getAccessibleChild(paramInt);
          localAccessibleContext.setAccessibleParent(this);
          return localAccessible;
        }
        return null;
      }
      
      public Locale getLocale()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getLocale();
        }
        return null;
      }
      
      public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.addPropertyChangeListener(paramPropertyChangeListener);
        }
      }
      
      public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          localAccessibleContext.removePropertyChangeListener(paramPropertyChangeListener);
        }
      }
      
      public AccessibleAction getAccessibleAction()
      {
        return getCurrentAccessibleContext().getAccessibleAction();
      }
      
      public AccessibleComponent getAccessibleComponent()
      {
        return this;
      }
      
      public AccessibleSelection getAccessibleSelection()
      {
        return getCurrentAccessibleContext().getAccessibleSelection();
      }
      
      public AccessibleText getAccessibleText()
      {
        return getCurrentAccessibleContext().getAccessibleText();
      }
      
      public AccessibleValue getAccessibleValue()
      {
        return getCurrentAccessibleContext().getAccessibleValue();
      }
      
      public Color getBackground()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getBackground();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getBackground();
        }
        return null;
      }
      
      public void setBackground(Color paramColor)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setBackground(paramColor);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setBackground(paramColor);
          }
        }
      }
      
      public Color getForeground()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getForeground();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getForeground();
        }
        return null;
      }
      
      public void setForeground(Color paramColor)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setForeground(paramColor);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setForeground(paramColor);
          }
        }
      }
      
      public Cursor getCursor()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getCursor();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getCursor();
        }
        Accessible localAccessible = getAccessibleParent();
        if ((localAccessible instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessible).getCursor();
        }
        return null;
      }
      
      public void setCursor(Cursor paramCursor)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setCursor(paramCursor);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setCursor(paramCursor);
          }
        }
      }
      
      public Font getFont()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getFont();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getFont();
        }
        return null;
      }
      
      public void setFont(Font paramFont)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setFont(paramFont);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setFont(paramFont);
          }
        }
      }
      
      public FontMetrics getFontMetrics(Font paramFont)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getFontMetrics(paramFont);
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.getFontMetrics(paramFont);
        }
        return null;
      }
      
      public boolean isEnabled()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).isEnabled();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.isEnabled();
        }
        return false;
      }
      
      public void setEnabled(boolean paramBoolean)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setEnabled(paramBoolean);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setEnabled(paramBoolean);
          }
        }
      }
      
      public boolean isVisible()
      {
        int i = this.parent.getFirstVisibleIndex();
        int j = this.parent.getLastVisibleIndex();
        if (j == -1) {
          j = this.parent.getModel().getSize() - 1;
        }
        return (this.indexInParent >= i) && (this.indexInParent <= j);
      }
      
      public void setVisible(boolean paramBoolean) {}
      
      public boolean isShowing()
      {
        return (this.parent.isShowing()) && (isVisible());
      }
      
      public boolean contains(Point paramPoint)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          localObject = ((AccessibleComponent)localAccessibleContext).getBounds();
          return ((Rectangle)localObject).contains(paramPoint);
        }
        Object localObject = getCurrentComponent();
        if (localObject != null)
        {
          Rectangle localRectangle = ((Component)localObject).getBounds();
          return localRectangle.contains(paramPoint);
        }
        return getBounds().contains(paramPoint);
      }
      
      public Point getLocationOnScreen()
      {
        if (this.parent != null)
        {
          Point localPoint1 = this.parent.getLocationOnScreen();
          Point localPoint2 = this.parent.indexToLocation(this.indexInParent);
          if (localPoint2 != null)
          {
            localPoint2.translate(localPoint1.x, localPoint1.y);
            return localPoint2;
          }
          return null;
        }
        return null;
      }
      
      public Point getLocation()
      {
        if (this.parent != null) {
          return this.parent.indexToLocation(this.indexInParent);
        }
        return null;
      }
      
      public void setLocation(Point paramPoint)
      {
        if ((this.parent != null) && (this.parent.contains(paramPoint))) {
          JList.this.ensureIndexIsVisible(this.indexInParent);
        }
      }
      
      public Rectangle getBounds()
      {
        if (this.parent != null) {
          return this.parent.getCellBounds(this.indexInParent, this.indexInParent);
        }
        return null;
      }
      
      public void setBounds(Rectangle paramRectangle)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          ((AccessibleComponent)localAccessibleContext).setBounds(paramRectangle);
        }
      }
      
      public Dimension getSize()
      {
        Rectangle localRectangle = getBounds();
        if (localRectangle != null) {
          return localRectangle.getSize();
        }
        return null;
      }
      
      public void setSize(Dimension paramDimension)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).setSize(paramDimension);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.setSize(paramDimension);
          }
        }
      }
      
      public Accessible getAccessibleAt(Point paramPoint)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).getAccessibleAt(paramPoint);
        }
        return null;
      }
      
      public boolean isFocusTraversable()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent)) {
          return ((AccessibleComponent)localAccessibleContext).isFocusTraversable();
        }
        Component localComponent = getCurrentComponent();
        if (localComponent != null) {
          return localComponent.isFocusTraversable();
        }
        return false;
      }
      
      public void requestFocus()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).requestFocus();
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.requestFocus();
          }
        }
      }
      
      public void addFocusListener(FocusListener paramFocusListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).addFocusListener(paramFocusListener);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.addFocusListener(paramFocusListener);
          }
        }
      }
      
      public void removeFocusListener(FocusListener paramFocusListener)
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if ((localAccessibleContext instanceof AccessibleComponent))
        {
          ((AccessibleComponent)localAccessibleContext).removeFocusListener(paramFocusListener);
        }
        else
        {
          Component localComponent = getCurrentComponent();
          if (localComponent != null) {
            localComponent.removeFocusListener(paramFocusListener);
          }
        }
      }
      
      public AccessibleIcon[] getAccessibleIcon()
      {
        AccessibleContext localAccessibleContext = getCurrentAccessibleContext();
        if (localAccessibleContext != null) {
          return localAccessibleContext.getAccessibleIcon();
        }
        return null;
      }
    }
  }
  
  public static final class DropLocation
    extends TransferHandler.DropLocation
  {
    private final int index;
    private final boolean isInsert;
    
    private DropLocation(Point paramPoint, int paramInt, boolean paramBoolean)
    {
      super();
      this.index = paramInt;
      this.isInsert = paramBoolean;
    }
    
    public int getIndex()
    {
      return this.index;
    }
    
    public boolean isInsert()
    {
      return this.isInsert;
    }
    
    public String toString()
    {
      return getClass().getName() + "[dropPoint=" + getDropPoint() + "," + "index=" + this.index + "," + "insert=" + this.isInsert + "]";
    }
  }
  
  private class ListSelectionHandler
    implements ListSelectionListener, Serializable
  {
    private ListSelectionHandler() {}
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent)
    {
      JList.this.fireSelectionValueChanged(paramListSelectionEvent.getFirstIndex(), paramListSelectionEvent.getLastIndex(), paramListSelectionEvent.getValueIsAdjusting());
    }
  }
}
