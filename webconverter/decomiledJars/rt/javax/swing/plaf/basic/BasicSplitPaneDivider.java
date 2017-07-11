package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import sun.swing.DefaultLookup;

public class BasicSplitPaneDivider
  extends Container
  implements PropertyChangeListener
{
  protected static final int ONE_TOUCH_SIZE = 6;
  protected static final int ONE_TOUCH_OFFSET = 2;
  protected DragController dragger;
  protected BasicSplitPaneUI splitPaneUI;
  protected int dividerSize = 0;
  protected Component hiddenDivider;
  protected JSplitPane splitPane;
  protected MouseHandler mouseHandler;
  protected int orientation;
  protected JButton leftButton;
  protected JButton rightButton;
  private Border border;
  private boolean mouseOver;
  private int oneTouchSize;
  private int oneTouchOffset;
  private boolean centerOneTouchButtons;
  
  public BasicSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI)
  {
    this.oneTouchSize = DefaultLookup.getInt(paramBasicSplitPaneUI.getSplitPane(), paramBasicSplitPaneUI, "SplitPane.oneTouchButtonSize", 6);
    this.oneTouchOffset = DefaultLookup.getInt(paramBasicSplitPaneUI.getSplitPane(), paramBasicSplitPaneUI, "SplitPane.oneTouchButtonOffset", 2);
    this.centerOneTouchButtons = DefaultLookup.getBoolean(paramBasicSplitPaneUI.getSplitPane(), paramBasicSplitPaneUI, "SplitPane.centerOneTouchButtons", true);
    setLayout(new DividerLayout());
    setBasicSplitPaneUI(paramBasicSplitPaneUI);
    this.orientation = this.splitPane.getOrientation();
    setCursor(this.orientation == 1 ? Cursor.getPredefinedCursor(11) : Cursor.getPredefinedCursor(9));
    setBackground(UIManager.getColor("SplitPane.background"));
  }
  
  private void revalidateSplitPane()
  {
    invalidate();
    if (this.splitPane != null) {
      this.splitPane.revalidate();
    }
  }
  
  public void setBasicSplitPaneUI(BasicSplitPaneUI paramBasicSplitPaneUI)
  {
    if (this.splitPane != null)
    {
      this.splitPane.removePropertyChangeListener(this);
      if (this.mouseHandler != null)
      {
        this.splitPane.removeMouseListener(this.mouseHandler);
        this.splitPane.removeMouseMotionListener(this.mouseHandler);
        removeMouseListener(this.mouseHandler);
        removeMouseMotionListener(this.mouseHandler);
        this.mouseHandler = null;
      }
    }
    this.splitPaneUI = paramBasicSplitPaneUI;
    if (paramBasicSplitPaneUI != null)
    {
      this.splitPane = paramBasicSplitPaneUI.getSplitPane();
      if (this.splitPane != null)
      {
        if (this.mouseHandler == null) {
          this.mouseHandler = new MouseHandler();
        }
        this.splitPane.addMouseListener(this.mouseHandler);
        this.splitPane.addMouseMotionListener(this.mouseHandler);
        addMouseListener(this.mouseHandler);
        addMouseMotionListener(this.mouseHandler);
        this.splitPane.addPropertyChangeListener(this);
        if (this.splitPane.isOneTouchExpandable()) {
          oneTouchExpandableChanged();
        }
      }
    }
    else
    {
      this.splitPane = null;
    }
  }
  
  public BasicSplitPaneUI getBasicSplitPaneUI()
  {
    return this.splitPaneUI;
  }
  
  public void setDividerSize(int paramInt)
  {
    this.dividerSize = paramInt;
  }
  
  public int getDividerSize()
  {
    return this.dividerSize;
  }
  
  public void setBorder(Border paramBorder)
  {
    Border localBorder = this.border;
    this.border = paramBorder;
  }
  
  public Border getBorder()
  {
    return this.border;
  }
  
  public Insets getInsets()
  {
    Border localBorder = getBorder();
    if (localBorder != null) {
      return localBorder.getBorderInsets(this);
    }
    return super.getInsets();
  }
  
  protected void setMouseOver(boolean paramBoolean)
  {
    this.mouseOver = paramBoolean;
  }
  
  public boolean isMouseOver()
  {
    return this.mouseOver;
  }
  
  public Dimension getPreferredSize()
  {
    if (this.orientation == 1) {
      return new Dimension(getDividerSize(), 1);
    }
    return new Dimension(1, getDividerSize());
  }
  
  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (paramPropertyChangeEvent.getSource() == this.splitPane) {
      if (paramPropertyChangeEvent.getPropertyName() == "orientation")
      {
        this.orientation = this.splitPane.getOrientation();
        setCursor(this.orientation == 1 ? Cursor.getPredefinedCursor(11) : Cursor.getPredefinedCursor(9));
        revalidateSplitPane();
      }
      else if (paramPropertyChangeEvent.getPropertyName() == "oneTouchExpandable")
      {
        oneTouchExpandableChanged();
      }
    }
  }
  
  public void paint(Graphics paramGraphics)
  {
    super.paint(paramGraphics);
    Border localBorder = getBorder();
    if (localBorder != null)
    {
      Dimension localDimension = getSize();
      localBorder.paintBorder(this, paramGraphics, 0, 0, localDimension.width, localDimension.height);
    }
  }
  
  protected void oneTouchExpandableChanged()
  {
    if (!DefaultLookup.getBoolean(this.splitPane, this.splitPaneUI, "SplitPane.supportsOneTouchButtons", true)) {
      return;
    }
    if ((this.splitPane.isOneTouchExpandable()) && (this.leftButton == null) && (this.rightButton == null))
    {
      this.leftButton = createLeftOneTouchButton();
      if (this.leftButton != null) {
        this.leftButton.addActionListener(new OneTouchActionHandler(true));
      }
      this.rightButton = createRightOneTouchButton();
      if (this.rightButton != null) {
        this.rightButton.addActionListener(new OneTouchActionHandler(false));
      }
      if ((this.leftButton != null) && (this.rightButton != null))
      {
        add(this.leftButton);
        add(this.rightButton);
      }
    }
    revalidateSplitPane();
  }
  
  protected JButton createLeftOneTouchButton()
  {
    JButton local1 = new JButton()
    {
      public void setBorder(Border paramAnonymousBorder) {}
      
      public void paint(Graphics paramAnonymousGraphics)
      {
        if (BasicSplitPaneDivider.this.splitPane != null)
        {
          int[] arrayOfInt1 = new int[3];
          int[] arrayOfInt2 = new int[3];
          paramAnonymousGraphics.setColor(getBackground());
          paramAnonymousGraphics.fillRect(0, 0, getWidth(), getHeight());
          paramAnonymousGraphics.setColor(Color.black);
          int i;
          if (BasicSplitPaneDivider.this.orientation == 0)
          {
            i = Math.min(getHeight(), BasicSplitPaneDivider.this.oneTouchSize);
            arrayOfInt1[0] = i;
            arrayOfInt1[1] = 0;
            arrayOfInt1[2] = (i << 1);
            arrayOfInt2[0] = 0;
            int tmp99_97 = i;
            arrayOfInt2[2] = tmp99_97;
            arrayOfInt2[1] = tmp99_97;
            paramAnonymousGraphics.drawPolygon(arrayOfInt1, arrayOfInt2, 3);
          }
          else
          {
            i = Math.min(getWidth(), BasicSplitPaneDivider.this.oneTouchSize);
            int tmp134_132 = i;
            arrayOfInt1[2] = tmp134_132;
            arrayOfInt1[0] = tmp134_132;
            arrayOfInt1[1] = 0;
            arrayOfInt2[0] = 0;
            arrayOfInt2[1] = i;
            arrayOfInt2[2] = (i << 1);
          }
          paramAnonymousGraphics.fillPolygon(arrayOfInt1, arrayOfInt2, 3);
        }
      }
      
      public boolean isFocusTraversable()
      {
        return false;
      }
    };
    local1.setMinimumSize(new Dimension(this.oneTouchSize, this.oneTouchSize));
    local1.setCursor(Cursor.getPredefinedCursor(0));
    local1.setFocusPainted(false);
    local1.setBorderPainted(false);
    local1.setRequestFocusEnabled(false);
    return local1;
  }
  
  protected JButton createRightOneTouchButton()
  {
    JButton local2 = new JButton()
    {
      public void setBorder(Border paramAnonymousBorder) {}
      
      public void paint(Graphics paramAnonymousGraphics)
      {
        if (BasicSplitPaneDivider.this.splitPane != null)
        {
          int[] arrayOfInt1 = new int[3];
          int[] arrayOfInt2 = new int[3];
          paramAnonymousGraphics.setColor(getBackground());
          paramAnonymousGraphics.fillRect(0, 0, getWidth(), getHeight());
          int i;
          if (BasicSplitPaneDivider.this.orientation == 0)
          {
            i = Math.min(getHeight(), BasicSplitPaneDivider.this.oneTouchSize);
            arrayOfInt1[0] = i;
            arrayOfInt1[1] = (i << 1);
            arrayOfInt1[2] = 0;
            arrayOfInt2[0] = i;
            int tmp92_91 = 0;
            arrayOfInt2[2] = tmp92_91;
            arrayOfInt2[1] = tmp92_91;
          }
          else
          {
            i = Math.min(getWidth(), BasicSplitPaneDivider.this.oneTouchSize);
            int tmp119_118 = 0;
            arrayOfInt1[2] = tmp119_118;
            arrayOfInt1[0] = tmp119_118;
            arrayOfInt1[1] = i;
            arrayOfInt2[0] = 0;
            arrayOfInt2[1] = i;
            arrayOfInt2[2] = (i << 1);
          }
          paramAnonymousGraphics.setColor(Color.black);
          paramAnonymousGraphics.fillPolygon(arrayOfInt1, arrayOfInt2, 3);
        }
      }
      
      public boolean isFocusTraversable()
      {
        return false;
      }
    };
    local2.setMinimumSize(new Dimension(this.oneTouchSize, this.oneTouchSize));
    local2.setCursor(Cursor.getPredefinedCursor(0));
    local2.setFocusPainted(false);
    local2.setBorderPainted(false);
    local2.setRequestFocusEnabled(false);
    return local2;
  }
  
  protected void prepareForDragging()
  {
    this.splitPaneUI.startDragging();
  }
  
  protected void dragDividerTo(int paramInt)
  {
    this.splitPaneUI.dragDividerTo(paramInt);
  }
  
  protected void finishDraggingTo(int paramInt)
  {
    this.splitPaneUI.finishDraggingTo(paramInt);
  }
  
  protected class DividerLayout
    implements LayoutManager
  {
    protected DividerLayout() {}
    
    public void layoutContainer(Container paramContainer)
    {
      if ((BasicSplitPaneDivider.this.leftButton != null) && (BasicSplitPaneDivider.this.rightButton != null) && (paramContainer == BasicSplitPaneDivider.this)) {
        if (BasicSplitPaneDivider.this.splitPane.isOneTouchExpandable())
        {
          Insets localInsets = BasicSplitPaneDivider.this.getInsets();
          int i;
          int j;
          int k;
          if (BasicSplitPaneDivider.this.orientation == 0)
          {
            i = localInsets != null ? localInsets.left : 0;
            j = BasicSplitPaneDivider.this.getHeight();
            if (localInsets != null)
            {
              j -= localInsets.top + localInsets.bottom;
              j = Math.max(j, 0);
            }
            j = Math.min(j, BasicSplitPaneDivider.this.oneTouchSize);
            k = (paramContainer.getSize().height - j) / 2;
            if (!BasicSplitPaneDivider.this.centerOneTouchButtons)
            {
              k = localInsets != null ? localInsets.top : 0;
              i = 0;
            }
            BasicSplitPaneDivider.this.leftButton.setBounds(i + BasicSplitPaneDivider.this.oneTouchOffset, k, j * 2, j);
            BasicSplitPaneDivider.this.rightButton.setBounds(i + BasicSplitPaneDivider.this.oneTouchOffset + BasicSplitPaneDivider.this.oneTouchSize * 2, k, j * 2, j);
          }
          else
          {
            i = localInsets != null ? localInsets.top : 0;
            j = BasicSplitPaneDivider.this.getWidth();
            if (localInsets != null)
            {
              j -= localInsets.left + localInsets.right;
              j = Math.max(j, 0);
            }
            j = Math.min(j, BasicSplitPaneDivider.this.oneTouchSize);
            k = (paramContainer.getSize().width - j) / 2;
            if (!BasicSplitPaneDivider.this.centerOneTouchButtons)
            {
              k = localInsets != null ? localInsets.left : 0;
              i = 0;
            }
            BasicSplitPaneDivider.this.leftButton.setBounds(k, i + BasicSplitPaneDivider.this.oneTouchOffset, j, j * 2);
            BasicSplitPaneDivider.this.rightButton.setBounds(k, i + BasicSplitPaneDivider.this.oneTouchOffset + BasicSplitPaneDivider.this.oneTouchSize * 2, j, j * 2);
          }
        }
        else
        {
          BasicSplitPaneDivider.this.leftButton.setBounds(-5, -5, 1, 1);
          BasicSplitPaneDivider.this.rightButton.setBounds(-5, -5, 1, 1);
        }
      }
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      if ((paramContainer != BasicSplitPaneDivider.this) || (BasicSplitPaneDivider.this.splitPane == null)) {
        return new Dimension(0, 0);
      }
      Dimension localDimension = null;
      if ((BasicSplitPaneDivider.this.splitPane.isOneTouchExpandable()) && (BasicSplitPaneDivider.this.leftButton != null)) {
        localDimension = BasicSplitPaneDivider.this.leftButton.getMinimumSize();
      }
      Insets localInsets = BasicSplitPaneDivider.this.getInsets();
      int i = BasicSplitPaneDivider.this.getDividerSize();
      int j = i;
      int k;
      if (BasicSplitPaneDivider.this.orientation == 0)
      {
        if (localDimension != null)
        {
          k = localDimension.height;
          if (localInsets != null) {
            k += localInsets.top + localInsets.bottom;
          }
          j = Math.max(j, k);
        }
        i = 1;
      }
      else
      {
        if (localDimension != null)
        {
          k = localDimension.width;
          if (localInsets != null) {
            k += localInsets.left + localInsets.right;
          }
          i = Math.max(i, k);
        }
        j = 1;
      }
      return new Dimension(i, j);
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return minimumLayoutSize(paramContainer);
    }
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
  }
  
  protected class DragController
  {
    int initialX;
    int maxX;
    int minX;
    int offset;
    
    protected DragController(MouseEvent paramMouseEvent)
    {
      JSplitPane localJSplitPane = BasicSplitPaneDivider.this.splitPaneUI.getSplitPane();
      Component localComponent1 = localJSplitPane.getLeftComponent();
      Component localComponent2 = localJSplitPane.getRightComponent();
      this.initialX = BasicSplitPaneDivider.this.getLocation().x;
      if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this) {
        this.offset = paramMouseEvent.getX();
      } else {
        this.offset = (paramMouseEvent.getX() - this.initialX);
      }
      if ((localComponent1 == null) || (localComponent2 == null) || (this.offset < -1) || (this.offset >= BasicSplitPaneDivider.this.getSize().width))
      {
        this.maxX = -1;
      }
      else
      {
        Insets localInsets = localJSplitPane.getInsets();
        if (localComponent1.isVisible())
        {
          this.minX = localComponent1.getMinimumSize().width;
          if (localInsets != null) {
            this.minX += localInsets.left;
          }
        }
        else
        {
          this.minX = 0;
        }
        int i;
        if (localComponent2.isVisible())
        {
          i = localInsets != null ? localInsets.right : 0;
          this.maxX = Math.max(0, localJSplitPane.getSize().width - (BasicSplitPaneDivider.this.getSize().width + i) - localComponent2.getMinimumSize().width);
        }
        else
        {
          i = localInsets != null ? localInsets.right : 0;
          this.maxX = Math.max(0, localJSplitPane.getSize().width - (BasicSplitPaneDivider.this.getSize().width + i));
        }
        if (this.maxX < this.minX) {
          this.minX = (this.maxX = 0);
        }
      }
    }
    
    protected boolean isValid()
    {
      return this.maxX > 0;
    }
    
    protected int positionForMouseEvent(MouseEvent paramMouseEvent)
    {
      int i = paramMouseEvent.getSource() == BasicSplitPaneDivider.this ? paramMouseEvent.getX() + BasicSplitPaneDivider.this.getLocation().x : paramMouseEvent.getX();
      i = Math.min(this.maxX, Math.max(this.minX, i - this.offset));
      return i;
    }
    
    protected int getNeededLocation(int paramInt1, int paramInt2)
    {
      int i = Math.min(this.maxX, Math.max(this.minX, paramInt1 - this.offset));
      return i;
    }
    
    protected void continueDrag(int paramInt1, int paramInt2)
    {
      BasicSplitPaneDivider.this.dragDividerTo(getNeededLocation(paramInt1, paramInt2));
    }
    
    protected void continueDrag(MouseEvent paramMouseEvent)
    {
      BasicSplitPaneDivider.this.dragDividerTo(positionForMouseEvent(paramMouseEvent));
    }
    
    protected void completeDrag(int paramInt1, int paramInt2)
    {
      BasicSplitPaneDivider.this.finishDraggingTo(getNeededLocation(paramInt1, paramInt2));
    }
    
    protected void completeDrag(MouseEvent paramMouseEvent)
    {
      BasicSplitPaneDivider.this.finishDraggingTo(positionForMouseEvent(paramMouseEvent));
    }
  }
  
  protected class MouseHandler
    extends MouseAdapter
    implements MouseMotionListener
  {
    protected MouseHandler() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (((paramMouseEvent.getSource() == BasicSplitPaneDivider.this) || (paramMouseEvent.getSource() == BasicSplitPaneDivider.this.splitPane)) && (BasicSplitPaneDivider.this.dragger == null) && (BasicSplitPaneDivider.this.splitPane.isEnabled()))
      {
        Component localComponent = BasicSplitPaneDivider.this.splitPaneUI.getNonContinuousLayoutDivider();
        if (BasicSplitPaneDivider.this.hiddenDivider != localComponent)
        {
          if (BasicSplitPaneDivider.this.hiddenDivider != null)
          {
            BasicSplitPaneDivider.this.hiddenDivider.removeMouseListener(this);
            BasicSplitPaneDivider.this.hiddenDivider.removeMouseMotionListener(this);
          }
          BasicSplitPaneDivider.this.hiddenDivider = localComponent;
          if (BasicSplitPaneDivider.this.hiddenDivider != null)
          {
            BasicSplitPaneDivider.this.hiddenDivider.addMouseMotionListener(this);
            BasicSplitPaneDivider.this.hiddenDivider.addMouseListener(this);
          }
        }
        if ((BasicSplitPaneDivider.this.splitPane.getLeftComponent() != null) && (BasicSplitPaneDivider.this.splitPane.getRightComponent() != null))
        {
          if (BasicSplitPaneDivider.this.orientation == 1) {
            BasicSplitPaneDivider.this.dragger = new BasicSplitPaneDivider.DragController(BasicSplitPaneDivider.this, paramMouseEvent);
          } else {
            BasicSplitPaneDivider.this.dragger = new BasicSplitPaneDivider.VerticalDragController(BasicSplitPaneDivider.this, paramMouseEvent);
          }
          if (!BasicSplitPaneDivider.this.dragger.isValid())
          {
            BasicSplitPaneDivider.this.dragger = null;
          }
          else
          {
            BasicSplitPaneDivider.this.prepareForDragging();
            BasicSplitPaneDivider.this.dragger.continueDrag(paramMouseEvent);
          }
        }
        paramMouseEvent.consume();
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (BasicSplitPaneDivider.this.dragger != null)
      {
        if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this.splitPane)
        {
          BasicSplitPaneDivider.this.dragger.completeDrag(paramMouseEvent.getX(), paramMouseEvent.getY());
        }
        else
        {
          Point localPoint;
          if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this)
          {
            localPoint = BasicSplitPaneDivider.this.getLocation();
            BasicSplitPaneDivider.this.dragger.completeDrag(paramMouseEvent.getX() + localPoint.x, paramMouseEvent.getY() + localPoint.y);
          }
          else if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this.hiddenDivider)
          {
            localPoint = BasicSplitPaneDivider.this.hiddenDivider.getLocation();
            int i = paramMouseEvent.getX() + localPoint.x;
            int j = paramMouseEvent.getY() + localPoint.y;
            BasicSplitPaneDivider.this.dragger.completeDrag(i, j);
          }
        }
        BasicSplitPaneDivider.this.dragger = null;
        paramMouseEvent.consume();
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (BasicSplitPaneDivider.this.dragger != null)
      {
        if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this.splitPane)
        {
          BasicSplitPaneDivider.this.dragger.continueDrag(paramMouseEvent.getX(), paramMouseEvent.getY());
        }
        else
        {
          Point localPoint;
          if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this)
          {
            localPoint = BasicSplitPaneDivider.this.getLocation();
            BasicSplitPaneDivider.this.dragger.continueDrag(paramMouseEvent.getX() + localPoint.x, paramMouseEvent.getY() + localPoint.y);
          }
          else if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this.hiddenDivider)
          {
            localPoint = BasicSplitPaneDivider.this.hiddenDivider.getLocation();
            int i = paramMouseEvent.getX() + localPoint.x;
            int j = paramMouseEvent.getY() + localPoint.y;
            BasicSplitPaneDivider.this.dragger.continueDrag(i, j);
          }
        }
        paramMouseEvent.consume();
      }
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this) {
        BasicSplitPaneDivider.this.setMouseOver(true);
      }
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this) {
        BasicSplitPaneDivider.this.setMouseOver(false);
      }
    }
  }
  
  private class OneTouchActionHandler
    implements ActionListener
  {
    private boolean toMinimum;
    
    OneTouchActionHandler(boolean paramBoolean)
    {
      this.toMinimum = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      Insets localInsets = BasicSplitPaneDivider.this.splitPane.getInsets();
      int i = BasicSplitPaneDivider.this.splitPane.getLastDividerLocation();
      int j = BasicSplitPaneDivider.this.splitPaneUI.getDividerLocation(BasicSplitPaneDivider.this.splitPane);
      int m;
      int k;
      if (this.toMinimum)
      {
        if (BasicSplitPaneDivider.this.orientation == 0)
        {
          if (j >= BasicSplitPaneDivider.this.splitPane.getHeight() - localInsets.bottom - BasicSplitPaneDivider.this.getHeight())
          {
            m = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
            k = Math.min(i, m);
            BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
          }
          else
          {
            k = localInsets.top;
            BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
          }
        }
        else if (j >= BasicSplitPaneDivider.this.splitPane.getWidth() - localInsets.right - BasicSplitPaneDivider.this.getWidth())
        {
          m = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
          k = Math.min(i, m);
          BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
        }
        else
        {
          k = localInsets.left;
          BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
        }
      }
      else if (BasicSplitPaneDivider.this.orientation == 0)
      {
        if (j == localInsets.top)
        {
          m = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
          k = Math.min(i, m);
          BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
        }
        else
        {
          k = BasicSplitPaneDivider.this.splitPane.getHeight() - BasicSplitPaneDivider.this.getHeight() - localInsets.top;
          BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
        }
      }
      else if (j == localInsets.left)
      {
        m = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
        k = Math.min(i, m);
        BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
      }
      else
      {
        k = BasicSplitPaneDivider.this.splitPane.getWidth() - BasicSplitPaneDivider.this.getWidth() - localInsets.left;
        BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
      }
      if (j != k)
      {
        BasicSplitPaneDivider.this.splitPane.setDividerLocation(k);
        BasicSplitPaneDivider.this.splitPane.setLastDividerLocation(j);
      }
    }
  }
  
  protected class VerticalDragController
    extends BasicSplitPaneDivider.DragController
  {
    protected VerticalDragController(MouseEvent paramMouseEvent)
    {
      super(paramMouseEvent);
      JSplitPane localJSplitPane = BasicSplitPaneDivider.this.splitPaneUI.getSplitPane();
      Component localComponent1 = localJSplitPane.getLeftComponent();
      Component localComponent2 = localJSplitPane.getRightComponent();
      this.initialX = BasicSplitPaneDivider.this.getLocation().y;
      if (paramMouseEvent.getSource() == BasicSplitPaneDivider.this) {
        this.offset = paramMouseEvent.getY();
      } else {
        this.offset = (paramMouseEvent.getY() - this.initialX);
      }
      if ((localComponent1 == null) || (localComponent2 == null) || (this.offset < -1) || (this.offset > BasicSplitPaneDivider.this.getSize().height))
      {
        this.maxX = -1;
      }
      else
      {
        Insets localInsets = localJSplitPane.getInsets();
        if (localComponent1.isVisible())
        {
          this.minX = localComponent1.getMinimumSize().height;
          if (localInsets != null) {
            this.minX += localInsets.top;
          }
        }
        else
        {
          this.minX = 0;
        }
        int i;
        if (localComponent2.isVisible())
        {
          i = localInsets != null ? localInsets.bottom : 0;
          this.maxX = Math.max(0, localJSplitPane.getSize().height - (BasicSplitPaneDivider.this.getSize().height + i) - localComponent2.getMinimumSize().height);
        }
        else
        {
          i = localInsets != null ? localInsets.bottom : 0;
          this.maxX = Math.max(0, localJSplitPane.getSize().height - (BasicSplitPaneDivider.this.getSize().height + i));
        }
        if (this.maxX < this.minX) {
          this.minX = (this.maxX = 0);
        }
      }
    }
    
    protected int getNeededLocation(int paramInt1, int paramInt2)
    {
      int i = Math.min(this.maxX, Math.max(this.minX, paramInt2 - this.offset));
      return i;
    }
    
    protected int positionForMouseEvent(MouseEvent paramMouseEvent)
    {
      int i = paramMouseEvent.getSource() == BasicSplitPaneDivider.this ? paramMouseEvent.getY() + BasicSplitPaneDivider.this.getLocation().y : paramMouseEvent.getY();
      i = Math.min(this.maxX, Math.max(this.minX, i - this.offset));
      return i;
    }
  }
}
