package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleRole;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.plaf.UIResource;

public class JScrollPane
  extends JComponent
  implements ScrollPaneConstants, Accessible
{
  private Border viewportBorder;
  private static final String uiClassID = "ScrollPaneUI";
  protected int verticalScrollBarPolicy = 20;
  protected int horizontalScrollBarPolicy = 30;
  protected JViewport viewport;
  protected JScrollBar verticalScrollBar;
  protected JScrollBar horizontalScrollBar;
  protected JViewport rowHeader;
  protected JViewport columnHeader;
  protected Component lowerLeft;
  protected Component lowerRight;
  protected Component upperLeft;
  protected Component upperRight;
  private boolean wheelScrollState = true;
  
  public JScrollPane(Component paramComponent, int paramInt1, int paramInt2)
  {
    setLayout(new ScrollPaneLayout.UIResource());
    setVerticalScrollBarPolicy(paramInt1);
    setHorizontalScrollBarPolicy(paramInt2);
    setViewport(createViewport());
    setVerticalScrollBar(createVerticalScrollBar());
    setHorizontalScrollBar(createHorizontalScrollBar());
    if (paramComponent != null) {
      setViewportView(paramComponent);
    }
    setUIProperty("opaque", Boolean.valueOf(true));
    updateUI();
    if (!getComponentOrientation().isLeftToRight()) {
      this.viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
    }
  }
  
  public JScrollPane(Component paramComponent)
  {
    this(paramComponent, 20, 30);
  }
  
  public JScrollPane(int paramInt1, int paramInt2)
  {
    this(null, paramInt1, paramInt2);
  }
  
  public JScrollPane()
  {
    this(null, 20, 30);
  }
  
  public ScrollPaneUI getUI()
  {
    return (ScrollPaneUI)this.ui;
  }
  
  public void setUI(ScrollPaneUI paramScrollPaneUI)
  {
    super.setUI(paramScrollPaneUI);
  }
  
  public void updateUI()
  {
    setUI((ScrollPaneUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "ScrollPaneUI";
  }
  
  public void setLayout(LayoutManager paramLayoutManager)
  {
    if ((paramLayoutManager instanceof ScrollPaneLayout))
    {
      super.setLayout(paramLayoutManager);
      ((ScrollPaneLayout)paramLayoutManager).syncWithScrollPane(this);
    }
    else if (paramLayoutManager == null)
    {
      super.setLayout(paramLayoutManager);
    }
    else
    {
      String str = "layout of JScrollPane must be a ScrollPaneLayout";
      throw new ClassCastException(str);
    }
  }
  
  public boolean isValidateRoot()
  {
    return true;
  }
  
  public int getVerticalScrollBarPolicy()
  {
    return this.verticalScrollBarPolicy;
  }
  
  public void setVerticalScrollBarPolicy(int paramInt)
  {
    switch (paramInt)
    {
    case 20: 
    case 21: 
    case 22: 
      break;
    default: 
      throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
    }
    int i = this.verticalScrollBarPolicy;
    this.verticalScrollBarPolicy = paramInt;
    firePropertyChange("verticalScrollBarPolicy", i, paramInt);
    revalidate();
    repaint();
  }
  
  public int getHorizontalScrollBarPolicy()
  {
    return this.horizontalScrollBarPolicy;
  }
  
  public void setHorizontalScrollBarPolicy(int paramInt)
  {
    switch (paramInt)
    {
    case 30: 
    case 31: 
    case 32: 
      break;
    default: 
      throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
    }
    int i = this.horizontalScrollBarPolicy;
    this.horizontalScrollBarPolicy = paramInt;
    firePropertyChange("horizontalScrollBarPolicy", i, paramInt);
    revalidate();
    repaint();
  }
  
  public Border getViewportBorder()
  {
    return this.viewportBorder;
  }
  
  public void setViewportBorder(Border paramBorder)
  {
    Border localBorder = this.viewportBorder;
    this.viewportBorder = paramBorder;
    firePropertyChange("viewportBorder", localBorder, paramBorder);
  }
  
  public Rectangle getViewportBorderBounds()
  {
    Rectangle localRectangle = new Rectangle(getSize());
    Insets localInsets = getInsets();
    localRectangle.x = localInsets.left;
    localRectangle.y = localInsets.top;
    localRectangle.width -= localInsets.left + localInsets.right;
    localRectangle.height -= localInsets.top + localInsets.bottom;
    boolean bool = SwingUtilities.isLeftToRight(this);
    JViewport localJViewport1 = getColumnHeader();
    if ((localJViewport1 != null) && (localJViewport1.isVisible()))
    {
      int i = localJViewport1.getHeight();
      localRectangle.y += i;
      localRectangle.height -= i;
    }
    JViewport localJViewport2 = getRowHeader();
    if ((localJViewport2 != null) && (localJViewport2.isVisible()))
    {
      int j = localJViewport2.getWidth();
      if (bool) {
        localRectangle.x += j;
      }
      localRectangle.width -= j;
    }
    JScrollBar localJScrollBar1 = getVerticalScrollBar();
    if ((localJScrollBar1 != null) && (localJScrollBar1.isVisible()))
    {
      int k = localJScrollBar1.getWidth();
      if (!bool) {
        localRectangle.x += k;
      }
      localRectangle.width -= k;
    }
    JScrollBar localJScrollBar2 = getHorizontalScrollBar();
    if ((localJScrollBar2 != null) && (localJScrollBar2.isVisible())) {
      localRectangle.height -= localJScrollBar2.getHeight();
    }
    return localRectangle;
  }
  
  public JScrollBar createHorizontalScrollBar()
  {
    return new ScrollBar(0);
  }
  
  @Transient
  public JScrollBar getHorizontalScrollBar()
  {
    return this.horizontalScrollBar;
  }
  
  public void setHorizontalScrollBar(JScrollBar paramJScrollBar)
  {
    JScrollBar localJScrollBar = getHorizontalScrollBar();
    this.horizontalScrollBar = paramJScrollBar;
    if (paramJScrollBar != null) {
      add(paramJScrollBar, "HORIZONTAL_SCROLLBAR");
    } else if (localJScrollBar != null) {
      remove(localJScrollBar);
    }
    firePropertyChange("horizontalScrollBar", localJScrollBar, paramJScrollBar);
    revalidate();
    repaint();
  }
  
  public JScrollBar createVerticalScrollBar()
  {
    return new ScrollBar(1);
  }
  
  @Transient
  public JScrollBar getVerticalScrollBar()
  {
    return this.verticalScrollBar;
  }
  
  public void setVerticalScrollBar(JScrollBar paramJScrollBar)
  {
    JScrollBar localJScrollBar = getVerticalScrollBar();
    this.verticalScrollBar = paramJScrollBar;
    add(paramJScrollBar, "VERTICAL_SCROLLBAR");
    firePropertyChange("verticalScrollBar", localJScrollBar, paramJScrollBar);
    revalidate();
    repaint();
  }
  
  protected JViewport createViewport()
  {
    return new JViewport();
  }
  
  public JViewport getViewport()
  {
    return this.viewport;
  }
  
  public void setViewport(JViewport paramJViewport)
  {
    JViewport localJViewport = getViewport();
    this.viewport = paramJViewport;
    if (paramJViewport != null) {
      add(paramJViewport, "VIEWPORT");
    } else if (localJViewport != null) {
      remove(localJViewport);
    }
    firePropertyChange("viewport", localJViewport, paramJViewport);
    if (this.accessibleContext != null) {
      ((AccessibleJScrollPane)this.accessibleContext).resetViewPort();
    }
    revalidate();
    repaint();
  }
  
  public void setViewportView(Component paramComponent)
  {
    if (getViewport() == null) {
      setViewport(createViewport());
    }
    getViewport().setView(paramComponent);
  }
  
  @Transient
  public JViewport getRowHeader()
  {
    return this.rowHeader;
  }
  
  public void setRowHeader(JViewport paramJViewport)
  {
    JViewport localJViewport = getRowHeader();
    this.rowHeader = paramJViewport;
    if (paramJViewport != null) {
      add(paramJViewport, "ROW_HEADER");
    } else if (localJViewport != null) {
      remove(localJViewport);
    }
    firePropertyChange("rowHeader", localJViewport, paramJViewport);
    revalidate();
    repaint();
  }
  
  public void setRowHeaderView(Component paramComponent)
  {
    if (getRowHeader() == null) {
      setRowHeader(createViewport());
    }
    getRowHeader().setView(paramComponent);
  }
  
  @Transient
  public JViewport getColumnHeader()
  {
    return this.columnHeader;
  }
  
  public void setColumnHeader(JViewport paramJViewport)
  {
    JViewport localJViewport = getColumnHeader();
    this.columnHeader = paramJViewport;
    if (paramJViewport != null) {
      add(paramJViewport, "COLUMN_HEADER");
    } else if (localJViewport != null) {
      remove(localJViewport);
    }
    firePropertyChange("columnHeader", localJViewport, paramJViewport);
    revalidate();
    repaint();
  }
  
  public void setColumnHeaderView(Component paramComponent)
  {
    if (getColumnHeader() == null) {
      setColumnHeader(createViewport());
    }
    getColumnHeader().setView(paramComponent);
  }
  
  public Component getCorner(String paramString)
  {
    boolean bool = getComponentOrientation().isLeftToRight();
    if (paramString.equals("LOWER_LEADING_CORNER")) {
      paramString = bool ? "LOWER_LEFT_CORNER" : "LOWER_RIGHT_CORNER";
    } else if (paramString.equals("LOWER_TRAILING_CORNER")) {
      paramString = bool ? "LOWER_RIGHT_CORNER" : "LOWER_LEFT_CORNER";
    } else if (paramString.equals("UPPER_LEADING_CORNER")) {
      paramString = bool ? "UPPER_LEFT_CORNER" : "UPPER_RIGHT_CORNER";
    } else if (paramString.equals("UPPER_TRAILING_CORNER")) {
      paramString = bool ? "UPPER_RIGHT_CORNER" : "UPPER_LEFT_CORNER";
    }
    if (paramString.equals("LOWER_LEFT_CORNER")) {
      return this.lowerLeft;
    }
    if (paramString.equals("LOWER_RIGHT_CORNER")) {
      return this.lowerRight;
    }
    if (paramString.equals("UPPER_LEFT_CORNER")) {
      return this.upperLeft;
    }
    if (paramString.equals("UPPER_RIGHT_CORNER")) {
      return this.upperRight;
    }
    return null;
  }
  
  public void setCorner(String paramString, Component paramComponent)
  {
    boolean bool = getComponentOrientation().isLeftToRight();
    if (paramString.equals("LOWER_LEADING_CORNER")) {
      paramString = bool ? "LOWER_LEFT_CORNER" : "LOWER_RIGHT_CORNER";
    } else if (paramString.equals("LOWER_TRAILING_CORNER")) {
      paramString = bool ? "LOWER_RIGHT_CORNER" : "LOWER_LEFT_CORNER";
    } else if (paramString.equals("UPPER_LEADING_CORNER")) {
      paramString = bool ? "UPPER_LEFT_CORNER" : "UPPER_RIGHT_CORNER";
    } else if (paramString.equals("UPPER_TRAILING_CORNER")) {
      paramString = bool ? "UPPER_RIGHT_CORNER" : "UPPER_LEFT_CORNER";
    }
    Component localComponent;
    if (paramString.equals("LOWER_LEFT_CORNER"))
    {
      localComponent = this.lowerLeft;
      this.lowerLeft = paramComponent;
    }
    else if (paramString.equals("LOWER_RIGHT_CORNER"))
    {
      localComponent = this.lowerRight;
      this.lowerRight = paramComponent;
    }
    else if (paramString.equals("UPPER_LEFT_CORNER"))
    {
      localComponent = this.upperLeft;
      this.upperLeft = paramComponent;
    }
    else if (paramString.equals("UPPER_RIGHT_CORNER"))
    {
      localComponent = this.upperRight;
      this.upperRight = paramComponent;
    }
    else
    {
      throw new IllegalArgumentException("invalid corner key");
    }
    if (localComponent != null) {
      remove(localComponent);
    }
    if (paramComponent != null) {
      add(paramComponent, paramString);
    }
    firePropertyChange(paramString, localComponent, paramComponent);
    revalidate();
    repaint();
  }
  
  public void setComponentOrientation(ComponentOrientation paramComponentOrientation)
  {
    super.setComponentOrientation(paramComponentOrientation);
    if (this.verticalScrollBar != null) {
      this.verticalScrollBar.setComponentOrientation(paramComponentOrientation);
    }
    if (this.horizontalScrollBar != null) {
      this.horizontalScrollBar.setComponentOrientation(paramComponentOrientation);
    }
  }
  
  public boolean isWheelScrollingEnabled()
  {
    return this.wheelScrollState;
  }
  
  public void setWheelScrollingEnabled(boolean paramBoolean)
  {
    boolean bool = this.wheelScrollState;
    this.wheelScrollState = paramBoolean;
    firePropertyChange("wheelScrollingEnabled", bool, paramBoolean);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ScrollPaneUI"))
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
    String str1 = this.viewportBorder != null ? this.viewportBorder.toString() : "";
    String str2 = this.viewport != null ? this.viewport.toString() : "";
    String str3;
    if (this.verticalScrollBarPolicy == 20) {
      str3 = "VERTICAL_SCROLLBAR_AS_NEEDED";
    } else if (this.verticalScrollBarPolicy == 21) {
      str3 = "VERTICAL_SCROLLBAR_NEVER";
    } else if (this.verticalScrollBarPolicy == 22) {
      str3 = "VERTICAL_SCROLLBAR_ALWAYS";
    } else {
      str3 = "";
    }
    String str4;
    if (this.horizontalScrollBarPolicy == 30) {
      str4 = "HORIZONTAL_SCROLLBAR_AS_NEEDED";
    } else if (this.horizontalScrollBarPolicy == 31) {
      str4 = "HORIZONTAL_SCROLLBAR_NEVER";
    } else if (this.horizontalScrollBarPolicy == 32) {
      str4 = "HORIZONTAL_SCROLLBAR_ALWAYS";
    } else {
      str4 = "";
    }
    String str5 = this.horizontalScrollBar != null ? this.horizontalScrollBar.toString() : "";
    String str6 = this.verticalScrollBar != null ? this.verticalScrollBar.toString() : "";
    String str7 = this.columnHeader != null ? this.columnHeader.toString() : "";
    String str8 = this.rowHeader != null ? this.rowHeader.toString() : "";
    String str9 = this.lowerLeft != null ? this.lowerLeft.toString() : "";
    String str10 = this.lowerRight != null ? this.lowerRight.toString() : "";
    String str11 = this.upperLeft != null ? this.upperLeft.toString() : "";
    String str12 = this.upperRight != null ? this.upperRight.toString() : "";
    return super.paramString() + ",columnHeader=" + str7 + ",horizontalScrollBar=" + str5 + ",horizontalScrollBarPolicy=" + str4 + ",lowerLeft=" + str9 + ",lowerRight=" + str10 + ",rowHeader=" + str8 + ",upperLeft=" + str11 + ",upperRight=" + str12 + ",verticalScrollBar=" + str6 + ",verticalScrollBarPolicy=" + str3 + ",viewport=" + str2 + ",viewportBorder=" + str1;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleJScrollPane();
    }
    return this.accessibleContext;
  }
  
  protected class AccessibleJScrollPane
    extends JComponent.AccessibleJComponent
    implements ChangeListener, PropertyChangeListener
  {
    protected JViewport viewPort = null;
    
    public void resetViewPort()
    {
      if (this.viewPort != null)
      {
        this.viewPort.removeChangeListener(this);
        this.viewPort.removePropertyChangeListener(this);
      }
      this.viewPort = JScrollPane.this.getViewport();
      if (this.viewPort != null)
      {
        this.viewPort.addChangeListener(this);
        this.viewPort.addPropertyChangeListener(this);
      }
    }
    
    public AccessibleJScrollPane()
    {
      super();
      resetViewPort();
      JScrollBar localJScrollBar = JScrollPane.this.getHorizontalScrollBar();
      if (localJScrollBar != null) {
        setScrollBarRelations(localJScrollBar);
      }
      localJScrollBar = JScrollPane.this.getVerticalScrollBar();
      if (localJScrollBar != null) {
        setScrollBarRelations(localJScrollBar);
      }
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.SCROLL_PANE;
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      if (paramChangeEvent == null) {
        throw new NullPointerException();
      }
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (((str == "horizontalScrollBar") || (str == "verticalScrollBar")) && ((paramPropertyChangeEvent.getNewValue() instanceof JScrollBar))) {
        setScrollBarRelations((JScrollBar)paramPropertyChangeEvent.getNewValue());
      }
    }
    
    void setScrollBarRelations(JScrollBar paramJScrollBar)
    {
      AccessibleRelation localAccessibleRelation1 = new AccessibleRelation(AccessibleRelation.CONTROLLED_BY, paramJScrollBar);
      AccessibleRelation localAccessibleRelation2 = new AccessibleRelation(AccessibleRelation.CONTROLLER_FOR, JScrollPane.this);
      AccessibleContext localAccessibleContext = paramJScrollBar.getAccessibleContext();
      localAccessibleContext.getAccessibleRelationSet().add(localAccessibleRelation2);
      getAccessibleRelationSet().add(localAccessibleRelation1);
    }
  }
  
  protected class ScrollBar
    extends JScrollBar
    implements UIResource
  {
    private boolean unitIncrementSet;
    private boolean blockIncrementSet;
    
    public ScrollBar(int paramInt)
    {
      super();
      putClientProperty("JScrollBar.fastWheelScrolling", Boolean.TRUE);
    }
    
    public void setUnitIncrement(int paramInt)
    {
      this.unitIncrementSet = true;
      putClientProperty("JScrollBar.fastWheelScrolling", null);
      super.setUnitIncrement(paramInt);
    }
    
    public int getUnitIncrement(int paramInt)
    {
      JViewport localJViewport = JScrollPane.this.getViewport();
      if ((!this.unitIncrementSet) && (localJViewport != null) && ((localJViewport.getView() instanceof Scrollable)))
      {
        Scrollable localScrollable = (Scrollable)localJViewport.getView();
        Rectangle localRectangle = localJViewport.getViewRect();
        return localScrollable.getScrollableUnitIncrement(localRectangle, getOrientation(), paramInt);
      }
      return super.getUnitIncrement(paramInt);
    }
    
    public void setBlockIncrement(int paramInt)
    {
      this.blockIncrementSet = true;
      putClientProperty("JScrollBar.fastWheelScrolling", null);
      super.setBlockIncrement(paramInt);
    }
    
    public int getBlockIncrement(int paramInt)
    {
      JViewport localJViewport = JScrollPane.this.getViewport();
      if ((this.blockIncrementSet) || (localJViewport == null)) {
        return super.getBlockIncrement(paramInt);
      }
      if ((localJViewport.getView() instanceof Scrollable))
      {
        Scrollable localScrollable = (Scrollable)localJViewport.getView();
        Rectangle localRectangle = localJViewport.getViewRect();
        return localScrollable.getScrollableBlockIncrement(localRectangle, getOrientation(), paramInt);
      }
      if (getOrientation() == 1) {
        return localJViewport.getExtentSize().height;
      }
      return localJViewport.getExtentSize().width;
    }
  }
}
