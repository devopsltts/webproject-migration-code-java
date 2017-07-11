package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoundedRangeModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicScrollBarUI
  extends ScrollBarUI
  implements LayoutManager, SwingConstants
{
  private static final int POSITIVE_SCROLL = 1;
  private static final int NEGATIVE_SCROLL = -1;
  private static final int MIN_SCROLL = 2;
  private static final int MAX_SCROLL = 3;
  protected Dimension minimumThumbSize;
  protected Dimension maximumThumbSize;
  protected Color thumbHighlightColor;
  protected Color thumbLightShadowColor;
  protected Color thumbDarkShadowColor;
  protected Color thumbColor;
  protected Color trackColor;
  protected Color trackHighlightColor;
  protected JScrollBar scrollbar;
  protected JButton incrButton;
  protected JButton decrButton;
  protected boolean isDragging;
  protected TrackListener trackListener;
  protected ArrowButtonListener buttonListener;
  protected ModelListener modelListener;
  protected Rectangle thumbRect;
  protected Rectangle trackRect;
  protected int trackHighlight;
  protected static final int NO_HIGHLIGHT = 0;
  protected static final int DECREASE_HIGHLIGHT = 1;
  protected static final int INCREASE_HIGHLIGHT = 2;
  protected ScrollListener scrollListener;
  protected PropertyChangeListener propertyChangeListener;
  protected Timer scrollTimer;
  private static final int scrollSpeedThrottle = 60;
  private boolean supportsAbsolutePositioning;
  protected int scrollBarWidth;
  private Handler handler;
  private boolean thumbActive;
  private boolean useCachedValue = false;
  private int scrollBarValue;
  protected int incrGap;
  protected int decrGap;
  
  public BasicScrollBarUI() {}
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("positiveUnitIncrement"));
    paramLazyActionMap.put(new Actions("positiveBlockIncrement"));
    paramLazyActionMap.put(new Actions("negativeUnitIncrement"));
    paramLazyActionMap.put(new Actions("negativeBlockIncrement"));
    paramLazyActionMap.put(new Actions("minScroll"));
    paramLazyActionMap.put(new Actions("maxScroll"));
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicScrollBarUI();
  }
  
  protected void configureScrollBarColors()
  {
    LookAndFeel.installColors(this.scrollbar, "ScrollBar.background", "ScrollBar.foreground");
    this.thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
    this.thumbLightShadowColor = UIManager.getColor("ScrollBar.thumbShadow");
    this.thumbDarkShadowColor = UIManager.getColor("ScrollBar.thumbDarkShadow");
    this.thumbColor = UIManager.getColor("ScrollBar.thumb");
    this.trackColor = UIManager.getColor("ScrollBar.track");
    this.trackHighlightColor = UIManager.getColor("ScrollBar.trackHighlight");
  }
  
  public void installUI(JComponent paramJComponent)
  {
    this.scrollbar = ((JScrollBar)paramJComponent);
    this.thumbRect = new Rectangle(0, 0, 0, 0);
    this.trackRect = new Rectangle(0, 0, 0, 0);
    installDefaults();
    installComponents();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    this.scrollbar = ((JScrollBar)paramJComponent);
    uninstallListeners();
    uninstallDefaults();
    uninstallComponents();
    uninstallKeyboardActions();
    this.thumbRect = null;
    this.scrollbar = null;
    this.incrButton = null;
    this.decrButton = null;
  }
  
  protected void installDefaults()
  {
    this.scrollBarWidth = UIManager.getInt("ScrollBar.width");
    if (this.scrollBarWidth <= 0) {
      this.scrollBarWidth = 16;
    }
    this.minimumThumbSize = ((Dimension)UIManager.get("ScrollBar.minimumThumbSize"));
    this.maximumThumbSize = ((Dimension)UIManager.get("ScrollBar.maximumThumbSize"));
    Boolean localBoolean = (Boolean)UIManager.get("ScrollBar.allowsAbsolutePositioning");
    this.supportsAbsolutePositioning = (localBoolean != null ? localBoolean.booleanValue() : false);
    this.trackHighlight = 0;
    if ((this.scrollbar.getLayout() == null) || ((this.scrollbar.getLayout() instanceof UIResource))) {
      this.scrollbar.setLayout(this);
    }
    configureScrollBarColors();
    LookAndFeel.installBorder(this.scrollbar, "ScrollBar.border");
    LookAndFeel.installProperty(this.scrollbar, "opaque", Boolean.TRUE);
    this.scrollBarValue = this.scrollbar.getValue();
    this.incrGap = UIManager.getInt("ScrollBar.incrementButtonGap");
    this.decrGap = UIManager.getInt("ScrollBar.decrementButtonGap");
    String str = (String)this.scrollbar.getClientProperty("JComponent.sizeVariant");
    if (str != null) {
      if ("large".equals(str))
      {
        this.scrollBarWidth = ((int)(this.scrollBarWidth * 1.15D));
        this.incrGap = ((int)(this.incrGap * 1.15D));
        this.decrGap = ((int)(this.decrGap * 1.15D));
      }
      else if ("small".equals(str))
      {
        this.scrollBarWidth = ((int)(this.scrollBarWidth * 0.857D));
        this.incrGap = ((int)(this.incrGap * 0.857D));
        this.decrGap = ((int)(this.decrGap * 0.714D));
      }
      else if ("mini".equals(str))
      {
        this.scrollBarWidth = ((int)(this.scrollBarWidth * 0.714D));
        this.incrGap = ((int)(this.incrGap * 0.714D));
        this.decrGap = ((int)(this.decrGap * 0.714D));
      }
    }
  }
  
  protected void installComponents()
  {
    switch (this.scrollbar.getOrientation())
    {
    case 1: 
      this.incrButton = createIncreaseButton(5);
      this.decrButton = createDecreaseButton(1);
      break;
    case 0: 
      if (this.scrollbar.getComponentOrientation().isLeftToRight())
      {
        this.incrButton = createIncreaseButton(3);
        this.decrButton = createDecreaseButton(7);
      }
      else
      {
        this.incrButton = createIncreaseButton(7);
        this.decrButton = createDecreaseButton(3);
      }
      break;
    }
    this.scrollbar.add(this.incrButton);
    this.scrollbar.add(this.decrButton);
    this.scrollbar.setEnabled(this.scrollbar.isEnabled());
  }
  
  protected void uninstallComponents()
  {
    this.scrollbar.remove(this.incrButton);
    this.scrollbar.remove(this.decrButton);
  }
  
  protected void installListeners()
  {
    this.trackListener = createTrackListener();
    this.buttonListener = createArrowButtonListener();
    this.modelListener = createModelListener();
    this.propertyChangeListener = createPropertyChangeListener();
    this.scrollbar.addMouseListener(this.trackListener);
    this.scrollbar.addMouseMotionListener(this.trackListener);
    this.scrollbar.getModel().addChangeListener(this.modelListener);
    this.scrollbar.addPropertyChangeListener(this.propertyChangeListener);
    this.scrollbar.addFocusListener(getHandler());
    if (this.incrButton != null) {
      this.incrButton.addMouseListener(this.buttonListener);
    }
    if (this.decrButton != null) {
      this.decrButton.addMouseListener(this.buttonListener);
    }
    this.scrollListener = createScrollListener();
    this.scrollTimer = new Timer(60, this.scrollListener);
    this.scrollTimer.setInitialDelay(300);
  }
  
  protected void installKeyboardActions()
  {
    LazyActionMap.installLazyActionMap(this.scrollbar, BasicScrollBarUI.class, "ScrollBar.actionMap");
    InputMap localInputMap = getInputMap(0);
    SwingUtilities.replaceUIInputMap(this.scrollbar, 0, localInputMap);
    localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(this.scrollbar, 1, localInputMap);
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIInputMap(this.scrollbar, 0, null);
    SwingUtilities.replaceUIActionMap(this.scrollbar, null);
  }
  
  private InputMap getInputMap(int paramInt)
  {
    InputMap localInputMap1;
    InputMap localInputMap2;
    if (paramInt == 0)
    {
      localInputMap1 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.focusInputMap");
      if ((this.scrollbar.getComponentOrientation().isLeftToRight()) || ((localInputMap2 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.focusInputMap.RightToLeft")) == null)) {
        return localInputMap1;
      }
      localInputMap2.setParent(localInputMap1);
      return localInputMap2;
    }
    if (paramInt == 1)
    {
      localInputMap1 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.ancestorInputMap");
      if ((this.scrollbar.getComponentOrientation().isLeftToRight()) || ((localInputMap2 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.ancestorInputMap.RightToLeft")) == null)) {
        return localInputMap1;
      }
      localInputMap2.setParent(localInputMap1);
      return localInputMap2;
    }
    return null;
  }
  
  protected void uninstallListeners()
  {
    this.scrollTimer.stop();
    this.scrollTimer = null;
    if (this.decrButton != null) {
      this.decrButton.removeMouseListener(this.buttonListener);
    }
    if (this.incrButton != null) {
      this.incrButton.removeMouseListener(this.buttonListener);
    }
    this.scrollbar.getModel().removeChangeListener(this.modelListener);
    this.scrollbar.removeMouseListener(this.trackListener);
    this.scrollbar.removeMouseMotionListener(this.trackListener);
    this.scrollbar.removePropertyChangeListener(this.propertyChangeListener);
    this.scrollbar.removeFocusListener(getHandler());
    this.handler = null;
  }
  
  protected void uninstallDefaults()
  {
    LookAndFeel.uninstallBorder(this.scrollbar);
    if (this.scrollbar.getLayout() == this) {
      this.scrollbar.setLayout(null);
    }
  }
  
  private Handler getHandler()
  {
    if (this.handler == null) {
      this.handler = new Handler(null);
    }
    return this.handler;
  }
  
  protected TrackListener createTrackListener()
  {
    return new TrackListener();
  }
  
  protected ArrowButtonListener createArrowButtonListener()
  {
    return new ArrowButtonListener();
  }
  
  protected ModelListener createModelListener()
  {
    return new ModelListener();
  }
  
  protected ScrollListener createScrollListener()
  {
    return new ScrollListener();
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  private void updateThumbState(int paramInt1, int paramInt2)
  {
    Rectangle localRectangle = getThumbBounds();
    setThumbRollover(localRectangle.contains(paramInt1, paramInt2));
  }
  
  protected void setThumbRollover(boolean paramBoolean)
  {
    if (this.thumbActive != paramBoolean)
    {
      this.thumbActive = paramBoolean;
      this.scrollbar.repaint(getThumbBounds());
    }
  }
  
  public boolean isThumbRollover()
  {
    return this.thumbActive;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    paintTrack(paramGraphics, paramJComponent, getTrackBounds());
    Rectangle localRectangle = getThumbBounds();
    if (localRectangle.intersects(paramGraphics.getClipBounds())) {
      paintThumb(paramGraphics, paramJComponent, localRectangle);
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return this.scrollbar.getOrientation() == 1 ? new Dimension(this.scrollBarWidth, 48) : new Dimension(48, this.scrollBarWidth);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  protected JButton createDecreaseButton(int paramInt)
  {
    return new BasicArrowButton(paramInt, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
  }
  
  protected JButton createIncreaseButton(int paramInt)
  {
    return new BasicArrowButton(paramInt, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
  }
  
  protected void paintDecreaseHighlight(Graphics paramGraphics)
  {
    Insets localInsets = this.scrollbar.getInsets();
    Rectangle localRectangle = getThumbBounds();
    paramGraphics.setColor(this.trackHighlightColor);
    int i;
    int j;
    int k;
    int m;
    if (this.scrollbar.getOrientation() == 1)
    {
      i = localInsets.left;
      j = this.trackRect.y;
      k = this.scrollbar.getWidth() - (localInsets.left + localInsets.right);
      m = localRectangle.y - j;
      paramGraphics.fillRect(i, j, k, m);
    }
    else
    {
      if (this.scrollbar.getComponentOrientation().isLeftToRight())
      {
        i = this.trackRect.x;
        j = localRectangle.x - i;
      }
      else
      {
        i = localRectangle.x + localRectangle.width;
        j = this.trackRect.x + this.trackRect.width - i;
      }
      k = localInsets.top;
      m = this.scrollbar.getHeight() - (localInsets.top + localInsets.bottom);
      paramGraphics.fillRect(i, k, j, m);
    }
  }
  
  protected void paintIncreaseHighlight(Graphics paramGraphics)
  {
    Insets localInsets = this.scrollbar.getInsets();
    Rectangle localRectangle = getThumbBounds();
    paramGraphics.setColor(this.trackHighlightColor);
    int i;
    int j;
    int k;
    int m;
    if (this.scrollbar.getOrientation() == 1)
    {
      i = localInsets.left;
      j = localRectangle.y + localRectangle.height;
      k = this.scrollbar.getWidth() - (localInsets.left + localInsets.right);
      m = this.trackRect.y + this.trackRect.height - j;
      paramGraphics.fillRect(i, j, k, m);
    }
    else
    {
      if (this.scrollbar.getComponentOrientation().isLeftToRight())
      {
        i = localRectangle.x + localRectangle.width;
        j = this.trackRect.x + this.trackRect.width - i;
      }
      else
      {
        i = this.trackRect.x;
        j = localRectangle.x - i;
      }
      k = localInsets.top;
      m = this.scrollbar.getHeight() - (localInsets.top + localInsets.bottom);
      paramGraphics.fillRect(i, k, j, m);
    }
  }
  
  protected void paintTrack(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
  {
    paramGraphics.setColor(this.trackColor);
    paramGraphics.fillRect(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
    if (this.trackHighlight == 1) {
      paintDecreaseHighlight(paramGraphics);
    } else if (this.trackHighlight == 2) {
      paintIncreaseHighlight(paramGraphics);
    }
  }
  
  protected void paintThumb(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle)
  {
    if ((paramRectangle.isEmpty()) || (!this.scrollbar.isEnabled())) {
      return;
    }
    int i = paramRectangle.width;
    int j = paramRectangle.height;
    paramGraphics.translate(paramRectangle.x, paramRectangle.y);
    paramGraphics.setColor(this.thumbDarkShadowColor);
    SwingUtilities2.drawRect(paramGraphics, 0, 0, i - 1, j - 1);
    paramGraphics.setColor(this.thumbColor);
    paramGraphics.fillRect(0, 0, i - 1, j - 1);
    paramGraphics.setColor(this.thumbHighlightColor);
    SwingUtilities2.drawVLine(paramGraphics, 1, 1, j - 2);
    SwingUtilities2.drawHLine(paramGraphics, 2, i - 3, 1);
    paramGraphics.setColor(this.thumbLightShadowColor);
    SwingUtilities2.drawHLine(paramGraphics, 2, i - 2, j - 2);
    SwingUtilities2.drawVLine(paramGraphics, i - 2, 1, j - 3);
    paramGraphics.translate(-paramRectangle.x, -paramRectangle.y);
  }
  
  protected Dimension getMinimumThumbSize()
  {
    return this.minimumThumbSize;
  }
  
  protected Dimension getMaximumThumbSize()
  {
    return this.maximumThumbSize;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    return getPreferredSize((JComponent)paramContainer);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    return getMinimumSize((JComponent)paramContainer);
  }
  
  private int getValue(JScrollBar paramJScrollBar)
  {
    return this.useCachedValue ? this.scrollBarValue : paramJScrollBar.getValue();
  }
  
  protected void layoutVScrollbar(JScrollBar paramJScrollBar)
  {
    Dimension localDimension = paramJScrollBar.getSize();
    Insets localInsets = paramJScrollBar.getInsets();
    int i = localDimension.width - (localInsets.left + localInsets.right);
    int j = localInsets.left;
    boolean bool = DefaultLookup.getBoolean(this.scrollbar, this, "ScrollBar.squareButtons", false);
    int k = bool ? i : this.decrButton.getPreferredSize().height;
    int m = localInsets.top;
    int n = bool ? i : this.incrButton.getPreferredSize().height;
    int i1 = localDimension.height - (localInsets.bottom + n);
    int i2 = localInsets.top + localInsets.bottom;
    int i3 = k + n;
    int i4 = this.decrGap + this.incrGap;
    float f1 = localDimension.height - (i2 + i3) - i4;
    float f2 = paramJScrollBar.getMinimum();
    float f3 = paramJScrollBar.getVisibleAmount();
    float f4 = paramJScrollBar.getMaximum() - f2;
    float f5 = getValue(paramJScrollBar);
    int i5 = f4 <= 0.0F ? getMaximumThumbSize().height : (int)(f1 * (f3 / f4));
    i5 = Math.max(i5, getMinimumThumbSize().height);
    i5 = Math.min(i5, getMaximumThumbSize().height);
    int i6 = i1 - this.incrGap - i5;
    if (f5 < paramJScrollBar.getMaximum() - paramJScrollBar.getVisibleAmount())
    {
      float f6 = f1 - i5;
      i6 = (int)(0.5F + f6 * ((f5 - f2) / (f4 - f3)));
      i6 += m + k + this.decrGap;
    }
    int i7 = localDimension.height - i2;
    if (i7 < i3)
    {
      n = k = i7 / 2;
      i1 = localDimension.height - (localInsets.bottom + n);
    }
    this.decrButton.setBounds(j, m, i, k);
    this.incrButton.setBounds(j, i1, i, n);
    int i8 = m + k + this.decrGap;
    int i9 = i1 - this.incrGap - i8;
    this.trackRect.setBounds(j, i8, i, i9);
    if (i5 >= (int)f1)
    {
      if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
        setThumbBounds(j, i8, i, i9);
      } else {
        setThumbBounds(0, 0, 0, 0);
      }
    }
    else
    {
      if (i6 + i5 > i1 - this.incrGap) {
        i6 = i1 - this.incrGap - i5;
      }
      if (i6 < m + k + this.decrGap) {
        i6 = m + k + this.decrGap + 1;
      }
      setThumbBounds(j, i6, i, i5);
    }
  }
  
  protected void layoutHScrollbar(JScrollBar paramJScrollBar)
  {
    Dimension localDimension = paramJScrollBar.getSize();
    Insets localInsets = paramJScrollBar.getInsets();
    int i = localDimension.height - (localInsets.top + localInsets.bottom);
    int j = localInsets.top;
    boolean bool1 = paramJScrollBar.getComponentOrientation().isLeftToRight();
    boolean bool2 = DefaultLookup.getBoolean(this.scrollbar, this, "ScrollBar.squareButtons", false);
    int k = bool2 ? i : this.decrButton.getPreferredSize().width;
    int m = bool2 ? i : this.incrButton.getPreferredSize().width;
    if (!bool1)
    {
      n = k;
      k = m;
      m = n;
    }
    int n = localInsets.left;
    int i1 = localDimension.width - (localInsets.right + m);
    int i2 = bool1 ? this.decrGap : this.incrGap;
    int i3 = bool1 ? this.incrGap : this.decrGap;
    int i4 = localInsets.left + localInsets.right;
    int i5 = k + m;
    float f1 = localDimension.width - (i4 + i5) - (i2 + i3);
    float f2 = paramJScrollBar.getMinimum();
    float f3 = paramJScrollBar.getMaximum();
    float f4 = paramJScrollBar.getVisibleAmount();
    float f5 = f3 - f2;
    float f6 = getValue(paramJScrollBar);
    int i6 = f5 <= 0.0F ? getMaximumThumbSize().width : (int)(f1 * (f4 / f5));
    i6 = Math.max(i6, getMinimumThumbSize().width);
    i6 = Math.min(i6, getMaximumThumbSize().width);
    int i7 = bool1 ? i1 - i3 - i6 : n + k + i2;
    if (f6 < f3 - paramJScrollBar.getVisibleAmount())
    {
      float f7 = f1 - i6;
      if (bool1) {
        i7 = (int)(0.5F + f7 * ((f6 - f2) / (f5 - f4)));
      } else {
        i7 = (int)(0.5F + f7 * ((f3 - f4 - f6) / (f5 - f4)));
      }
      i7 += n + k + i2;
    }
    int i8 = localDimension.width - i4;
    if (i8 < i5)
    {
      m = k = i8 / 2;
      i1 = localDimension.width - (localInsets.right + m + i3);
    }
    (bool1 ? this.decrButton : this.incrButton).setBounds(n, j, k, i);
    (bool1 ? this.incrButton : this.decrButton).setBounds(i1, j, m, i);
    int i9 = n + k + i2;
    int i10 = i1 - i3 - i9;
    this.trackRect.setBounds(i9, j, i10, i);
    if (i6 >= (int)f1)
    {
      if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
        setThumbBounds(i9, j, i10, i);
      } else {
        setThumbBounds(0, 0, 0, 0);
      }
    }
    else
    {
      if (i7 + i6 > i1 - i3) {
        i7 = i1 - i3 - i6;
      }
      if (i7 < n + k + i2) {
        i7 = n + k + i2 + 1;
      }
      setThumbBounds(i7, j, i6, i);
    }
  }
  
  public void layoutContainer(Container paramContainer)
  {
    if (this.isDragging) {
      return;
    }
    JScrollBar localJScrollBar = (JScrollBar)paramContainer;
    switch (localJScrollBar.getOrientation())
    {
    case 1: 
      layoutVScrollbar(localJScrollBar);
      break;
    case 0: 
      layoutHScrollbar(localJScrollBar);
    }
  }
  
  protected void setThumbBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.thumbRect.x == paramInt1) && (this.thumbRect.y == paramInt2) && (this.thumbRect.width == paramInt3) && (this.thumbRect.height == paramInt4)) {
      return;
    }
    int i = Math.min(paramInt1, this.thumbRect.x);
    int j = Math.min(paramInt2, this.thumbRect.y);
    int k = Math.max(paramInt1 + paramInt3, this.thumbRect.x + this.thumbRect.width);
    int m = Math.max(paramInt2 + paramInt4, this.thumbRect.y + this.thumbRect.height);
    this.thumbRect.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    this.scrollbar.repaint(i, j, k - i, m - j);
    setThumbRollover(false);
  }
  
  protected Rectangle getThumbBounds()
  {
    return this.thumbRect;
  }
  
  protected Rectangle getTrackBounds()
  {
    return this.trackRect;
  }
  
  static void scrollByBlock(JScrollBar paramJScrollBar, int paramInt)
  {
    int i = paramJScrollBar.getValue();
    int j = paramJScrollBar.getBlockIncrement(paramInt);
    int k = j * (paramInt > 0 ? 1 : -1);
    int m = i + k;
    if ((k > 0) && (m < i)) {
      m = paramJScrollBar.getMaximum();
    } else if ((k < 0) && (m > i)) {
      m = paramJScrollBar.getMinimum();
    }
    paramJScrollBar.setValue(m);
  }
  
  protected void scrollByBlock(int paramInt)
  {
    scrollByBlock(this.scrollbar, paramInt);
    this.trackHighlight = (paramInt > 0 ? 2 : 1);
    Rectangle localRectangle = getTrackBounds();
    this.scrollbar.repaint(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
  }
  
  static void scrollByUnits(JScrollBar paramJScrollBar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = -1;
    if (paramBoolean) {
      if (paramInt1 < 0) {
        j = paramJScrollBar.getValue() - paramJScrollBar.getBlockIncrement(paramInt1);
      } else {
        j = paramJScrollBar.getValue() + paramJScrollBar.getBlockIncrement(paramInt1);
      }
    }
    for (int k = 0; k < paramInt2; k++)
    {
      int i;
      if (paramInt1 > 0) {
        i = paramJScrollBar.getUnitIncrement(paramInt1);
      } else {
        i = -paramJScrollBar.getUnitIncrement(paramInt1);
      }
      int m = paramJScrollBar.getValue();
      int n = m + i;
      if ((i > 0) && (n < m)) {
        n = paramJScrollBar.getMaximum();
      } else if ((i < 0) && (n > m)) {
        n = paramJScrollBar.getMinimum();
      }
      if (m == n) {
        break;
      }
      if ((paramBoolean) && (k > 0))
      {
        assert (j != -1);
        if (((paramInt1 < 0) && (n < j)) || ((paramInt1 > 0) && (n > j))) {
          break;
        }
      }
      paramJScrollBar.setValue(n);
    }
  }
  
  protected void scrollByUnit(int paramInt)
  {
    scrollByUnits(this.scrollbar, paramInt, 1, false);
  }
  
  public boolean getSupportsAbsolutePositioning()
  {
    return this.supportsAbsolutePositioning;
  }
  
  private boolean isMouseLeftOfThumb()
  {
    return this.trackListener.currentMouseX < getThumbBounds().x;
  }
  
  private boolean isMouseRightOfThumb()
  {
    Rectangle localRectangle = getThumbBounds();
    return this.trackListener.currentMouseX > localRectangle.x + localRectangle.width;
  }
  
  private boolean isMouseBeforeThumb()
  {
    return this.scrollbar.getComponentOrientation().isLeftToRight() ? isMouseLeftOfThumb() : isMouseRightOfThumb();
  }
  
  private boolean isMouseAfterThumb()
  {
    return this.scrollbar.getComponentOrientation().isLeftToRight() ? isMouseRightOfThumb() : isMouseLeftOfThumb();
  }
  
  private void updateButtonDirections()
  {
    int i = this.scrollbar.getOrientation();
    if (this.scrollbar.getComponentOrientation().isLeftToRight())
    {
      if ((this.incrButton instanceof BasicArrowButton)) {
        ((BasicArrowButton)this.incrButton).setDirection(i == 0 ? 3 : 5);
      }
      if ((this.decrButton instanceof BasicArrowButton)) {
        ((BasicArrowButton)this.decrButton).setDirection(i == 0 ? 7 : 1);
      }
    }
    else
    {
      if ((this.incrButton instanceof BasicArrowButton)) {
        ((BasicArrowButton)this.incrButton).setDirection(i == 0 ? 7 : 5);
      }
      if ((this.decrButton instanceof BasicArrowButton)) {
        ((BasicArrowButton)this.decrButton).setDirection(i == 0 ? 3 : 1);
      }
    }
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String POSITIVE_UNIT_INCREMENT = "positiveUnitIncrement";
    private static final String POSITIVE_BLOCK_INCREMENT = "positiveBlockIncrement";
    private static final String NEGATIVE_UNIT_INCREMENT = "negativeUnitIncrement";
    private static final String NEGATIVE_BLOCK_INCREMENT = "negativeBlockIncrement";
    private static final String MIN_SCROLL = "minScroll";
    private static final String MAX_SCROLL = "maxScroll";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JScrollBar localJScrollBar = (JScrollBar)paramActionEvent.getSource();
      String str = getName();
      if (str == "positiveUnitIncrement") {
        scroll(localJScrollBar, 1, false);
      } else if (str == "positiveBlockIncrement") {
        scroll(localJScrollBar, 1, true);
      } else if (str == "negativeUnitIncrement") {
        scroll(localJScrollBar, -1, false);
      } else if (str == "negativeBlockIncrement") {
        scroll(localJScrollBar, -1, true);
      } else if (str == "minScroll") {
        scroll(localJScrollBar, 2, true);
      } else if (str == "maxScroll") {
        scroll(localJScrollBar, 3, true);
      }
    }
    
    private void scroll(JScrollBar paramJScrollBar, int paramInt, boolean paramBoolean)
    {
      if ((paramInt == -1) || (paramInt == 1))
      {
        int i;
        if (paramBoolean)
        {
          if (paramInt == -1) {
            i = -1 * paramJScrollBar.getBlockIncrement(-1);
          } else {
            i = paramJScrollBar.getBlockIncrement(1);
          }
        }
        else if (paramInt == -1) {
          i = -1 * paramJScrollBar.getUnitIncrement(-1);
        } else {
          i = paramJScrollBar.getUnitIncrement(1);
        }
        paramJScrollBar.setValue(paramJScrollBar.getValue() + i);
      }
      else if (paramInt == 2)
      {
        paramJScrollBar.setValue(paramJScrollBar.getMinimum());
      }
      else if (paramInt == 3)
      {
        paramJScrollBar.setValue(paramJScrollBar.getMaximum());
      }
    }
  }
  
  protected class ArrowButtonListener
    extends MouseAdapter
  {
    boolean handledEvent;
    
    protected ArrowButtonListener() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (!BasicScrollBarUI.this.scrollbar.isEnabled()) {
        return;
      }
      if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
        return;
      }
      int i = paramMouseEvent.getSource() == BasicScrollBarUI.this.incrButton ? 1 : -1;
      BasicScrollBarUI.this.scrollByUnit(i);
      BasicScrollBarUI.this.scrollTimer.stop();
      BasicScrollBarUI.this.scrollListener.setDirection(i);
      BasicScrollBarUI.this.scrollListener.setScrollByBlock(false);
      BasicScrollBarUI.this.scrollTimer.start();
      this.handledEvent = true;
      if ((!BasicScrollBarUI.this.scrollbar.hasFocus()) && (BasicScrollBarUI.this.scrollbar.isRequestFocusEnabled())) {
        BasicScrollBarUI.this.scrollbar.requestFocus();
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      BasicScrollBarUI.this.scrollTimer.stop();
      this.handledEvent = false;
      BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(false);
    }
  }
  
  private class Handler
    implements FocusListener, PropertyChangeListener
  {
    private Handler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      BasicScrollBarUI.this.scrollbar.repaint();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicScrollBarUI.this.scrollbar.repaint();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      Object localObject;
      if ("model" == str)
      {
        localObject = (BoundedRangeModel)paramPropertyChangeEvent.getOldValue();
        BoundedRangeModel localBoundedRangeModel = (BoundedRangeModel)paramPropertyChangeEvent.getNewValue();
        ((BoundedRangeModel)localObject).removeChangeListener(BasicScrollBarUI.this.modelListener);
        localBoundedRangeModel.addChangeListener(BasicScrollBarUI.this.modelListener);
        BasicScrollBarUI.this.scrollBarValue = BasicScrollBarUI.this.scrollbar.getValue();
        BasicScrollBarUI.this.scrollbar.repaint();
        BasicScrollBarUI.this.scrollbar.revalidate();
      }
      else if ("orientation" == str)
      {
        BasicScrollBarUI.this.updateButtonDirections();
      }
      else if ("componentOrientation" == str)
      {
        BasicScrollBarUI.this.updateButtonDirections();
        localObject = BasicScrollBarUI.this.getInputMap(0);
        SwingUtilities.replaceUIInputMap(BasicScrollBarUI.this.scrollbar, 0, (InputMap)localObject);
      }
    }
  }
  
  protected class ModelListener
    implements ChangeListener
  {
    protected ModelListener() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      if (!BasicScrollBarUI.this.useCachedValue) {
        BasicScrollBarUI.this.scrollBarValue = BasicScrollBarUI.this.scrollbar.getValue();
      }
      BasicScrollBarUI.this.layoutContainer(BasicScrollBarUI.this.scrollbar);
      BasicScrollBarUI.this.useCachedValue = false;
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicScrollBarUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
  
  protected class ScrollListener
    implements ActionListener
  {
    int direction = 1;
    boolean useBlockIncrement;
    
    public ScrollListener()
    {
      this.direction = 1;
      this.useBlockIncrement = false;
    }
    
    public ScrollListener(int paramInt, boolean paramBoolean)
    {
      this.direction = paramInt;
      this.useBlockIncrement = paramBoolean;
    }
    
    public void setDirection(int paramInt)
    {
      this.direction = paramInt;
    }
    
    public void setScrollByBlock(boolean paramBoolean)
    {
      this.useBlockIncrement = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (this.useBlockIncrement)
      {
        BasicScrollBarUI.this.scrollByBlock(this.direction);
        if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1)
        {
          if (this.direction > 0)
          {
            if (BasicScrollBarUI.this.getThumbBounds().y + BasicScrollBarUI.this.getThumbBounds().height >= BasicScrollBarUI.this.trackListener.currentMouseY) {
              ((Timer)paramActionEvent.getSource()).stop();
            }
          }
          else if (BasicScrollBarUI.this.getThumbBounds().y <= BasicScrollBarUI.this.trackListener.currentMouseY) {
            ((Timer)paramActionEvent.getSource()).stop();
          }
        }
        else if (((this.direction > 0) && (!BasicScrollBarUI.this.isMouseAfterThumb())) || ((this.direction < 0) && (!BasicScrollBarUI.this.isMouseBeforeThumb()))) {
          ((Timer)paramActionEvent.getSource()).stop();
        }
      }
      else
      {
        BasicScrollBarUI.this.scrollByUnit(this.direction);
      }
      if ((this.direction > 0) && (BasicScrollBarUI.this.scrollbar.getValue() + BasicScrollBarUI.this.scrollbar.getVisibleAmount() >= BasicScrollBarUI.this.scrollbar.getMaximum())) {
        ((Timer)paramActionEvent.getSource()).stop();
      } else if ((this.direction < 0) && (BasicScrollBarUI.this.scrollbar.getValue() <= BasicScrollBarUI.this.scrollbar.getMinimum())) {
        ((Timer)paramActionEvent.getSource()).stop();
      }
    }
  }
  
  protected class TrackListener
    extends MouseAdapter
    implements MouseMotionListener
  {
    protected transient int offset;
    protected transient int currentMouseX;
    protected transient int currentMouseY;
    private transient int direction = 1;
    
    protected TrackListener() {}
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (BasicScrollBarUI.this.isDragging) {
        BasicScrollBarUI.this.updateThumbState(paramMouseEvent.getX(), paramMouseEvent.getY());
      }
      if ((SwingUtilities.isRightMouseButton(paramMouseEvent)) || ((!BasicScrollBarUI.this.getSupportsAbsolutePositioning()) && (SwingUtilities.isMiddleMouseButton(paramMouseEvent)))) {
        return;
      }
      if (!BasicScrollBarUI.this.scrollbar.isEnabled()) {
        return;
      }
      Rectangle localRectangle = BasicScrollBarUI.this.getTrackBounds();
      BasicScrollBarUI.this.scrollbar.repaint(localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height);
      BasicScrollBarUI.this.trackHighlight = 0;
      BasicScrollBarUI.this.isDragging = false;
      this.offset = 0;
      BasicScrollBarUI.this.scrollTimer.stop();
      BasicScrollBarUI.this.useCachedValue = true;
      BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(false);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if ((SwingUtilities.isRightMouseButton(paramMouseEvent)) || ((!BasicScrollBarUI.this.getSupportsAbsolutePositioning()) && (SwingUtilities.isMiddleMouseButton(paramMouseEvent)))) {
        return;
      }
      if (!BasicScrollBarUI.this.scrollbar.isEnabled()) {
        return;
      }
      if ((!BasicScrollBarUI.this.scrollbar.hasFocus()) && (BasicScrollBarUI.this.scrollbar.isRequestFocusEnabled())) {
        BasicScrollBarUI.this.scrollbar.requestFocus();
      }
      BasicScrollBarUI.this.useCachedValue = true;
      BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(true);
      this.currentMouseX = paramMouseEvent.getX();
      this.currentMouseY = paramMouseEvent.getY();
      if (BasicScrollBarUI.this.getThumbBounds().contains(this.currentMouseX, this.currentMouseY))
      {
        switch (BasicScrollBarUI.this.scrollbar.getOrientation())
        {
        case 1: 
          this.offset = (this.currentMouseY - BasicScrollBarUI.this.getThumbBounds().y);
          break;
        case 0: 
          this.offset = (this.currentMouseX - BasicScrollBarUI.this.getThumbBounds().x);
        }
        BasicScrollBarUI.this.isDragging = true;
        return;
      }
      if ((BasicScrollBarUI.this.getSupportsAbsolutePositioning()) && (SwingUtilities.isMiddleMouseButton(paramMouseEvent)))
      {
        switch (BasicScrollBarUI.this.scrollbar.getOrientation())
        {
        case 1: 
          this.offset = (BasicScrollBarUI.this.getThumbBounds().height / 2);
          break;
        case 0: 
          this.offset = (BasicScrollBarUI.this.getThumbBounds().width / 2);
        }
        BasicScrollBarUI.this.isDragging = true;
        setValueFrom(paramMouseEvent);
        return;
      }
      BasicScrollBarUI.this.isDragging = false;
      Dimension localDimension = BasicScrollBarUI.this.scrollbar.getSize();
      this.direction = 1;
      int i;
      switch (BasicScrollBarUI.this.scrollbar.getOrientation())
      {
      case 1: 
        if (BasicScrollBarUI.this.getThumbBounds().isEmpty())
        {
          i = localDimension.height / 2;
          this.direction = (this.currentMouseY < i ? -1 : 1);
        }
        else
        {
          i = BasicScrollBarUI.this.getThumbBounds().y;
          this.direction = (this.currentMouseY < i ? -1 : 1);
        }
        break;
      case 0: 
        if (BasicScrollBarUI.this.getThumbBounds().isEmpty())
        {
          i = localDimension.width / 2;
          this.direction = (this.currentMouseX < i ? -1 : 1);
        }
        else
        {
          i = BasicScrollBarUI.this.getThumbBounds().x;
          this.direction = (this.currentMouseX < i ? -1 : 1);
        }
        if (!BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight()) {
          this.direction = (-this.direction);
        }
        break;
      }
      BasicScrollBarUI.this.scrollByBlock(this.direction);
      BasicScrollBarUI.this.scrollTimer.stop();
      BasicScrollBarUI.this.scrollListener.setDirection(this.direction);
      BasicScrollBarUI.this.scrollListener.setScrollByBlock(true);
      startScrollTimerIfNecessary();
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if ((SwingUtilities.isRightMouseButton(paramMouseEvent)) || ((!BasicScrollBarUI.this.getSupportsAbsolutePositioning()) && (SwingUtilities.isMiddleMouseButton(paramMouseEvent)))) {
        return;
      }
      if ((!BasicScrollBarUI.this.scrollbar.isEnabled()) || (BasicScrollBarUI.this.getThumbBounds().isEmpty())) {
        return;
      }
      if (BasicScrollBarUI.this.isDragging)
      {
        setValueFrom(paramMouseEvent);
      }
      else
      {
        this.currentMouseX = paramMouseEvent.getX();
        this.currentMouseY = paramMouseEvent.getY();
        BasicScrollBarUI.this.updateThumbState(this.currentMouseX, this.currentMouseY);
        startScrollTimerIfNecessary();
      }
    }
    
    private void setValueFrom(MouseEvent paramMouseEvent)
    {
      boolean bool = BasicScrollBarUI.this.isThumbRollover();
      BoundedRangeModel localBoundedRangeModel = BasicScrollBarUI.this.scrollbar.getModel();
      Rectangle localRectangle = BasicScrollBarUI.this.getThumbBounds();
      int i;
      int j;
      int k;
      float f1;
      if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1)
      {
        i = BasicScrollBarUI.this.trackRect.y;
        j = BasicScrollBarUI.this.trackRect.y + BasicScrollBarUI.this.trackRect.height - localRectangle.height;
        k = Math.min(j, Math.max(i, paramMouseEvent.getY() - this.offset));
        BasicScrollBarUI.this.setThumbBounds(localRectangle.x, k, localRectangle.width, localRectangle.height);
        f1 = BasicScrollBarUI.this.getTrackBounds().height;
      }
      else
      {
        i = BasicScrollBarUI.this.trackRect.x;
        j = BasicScrollBarUI.this.trackRect.x + BasicScrollBarUI.this.trackRect.width - localRectangle.width;
        k = Math.min(j, Math.max(i, paramMouseEvent.getX() - this.offset));
        BasicScrollBarUI.this.setThumbBounds(k, localRectangle.y, localRectangle.width, localRectangle.height);
        f1 = BasicScrollBarUI.this.getTrackBounds().width;
      }
      if (k == j)
      {
        if ((BasicScrollBarUI.this.scrollbar.getOrientation() == 1) || (BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight())) {
          BasicScrollBarUI.this.scrollbar.setValue(localBoundedRangeModel.getMaximum() - localBoundedRangeModel.getExtent());
        } else {
          BasicScrollBarUI.this.scrollbar.setValue(localBoundedRangeModel.getMinimum());
        }
      }
      else
      {
        float f2 = localBoundedRangeModel.getMaximum() - localBoundedRangeModel.getExtent();
        float f3 = f2 - localBoundedRangeModel.getMinimum();
        float f4 = k - i;
        float f5 = j - i;
        int m;
        if ((BasicScrollBarUI.this.scrollbar.getOrientation() == 1) || (BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight())) {
          m = (int)(0.5D + f4 / f5 * f3);
        } else {
          m = (int)(0.5D + (j - k) / f5 * f3);
        }
        BasicScrollBarUI.this.useCachedValue = true;
        BasicScrollBarUI.this.scrollBarValue = (m + localBoundedRangeModel.getMinimum());
        BasicScrollBarUI.this.scrollbar.setValue(adjustValueIfNecessary(BasicScrollBarUI.this.scrollBarValue));
      }
      BasicScrollBarUI.this.setThumbRollover(bool);
    }
    
    private int adjustValueIfNecessary(int paramInt)
    {
      if ((BasicScrollBarUI.this.scrollbar.getParent() instanceof JScrollPane))
      {
        JScrollPane localJScrollPane = (JScrollPane)BasicScrollBarUI.this.scrollbar.getParent();
        JViewport localJViewport = localJScrollPane.getViewport();
        Component localComponent = localJViewport.getView();
        if ((localComponent instanceof JList))
        {
          JList localJList = (JList)localComponent;
          if (DefaultLookup.getBoolean(localJList, localJList.getUI(), "List.lockToPositionOnScroll", false))
          {
            int i = paramInt;
            int j = localJList.getLayoutOrientation();
            int k = BasicScrollBarUI.this.scrollbar.getOrientation();
            int m;
            Rectangle localRectangle1;
            if ((k == 1) && (j == 0))
            {
              m = localJList.locationToIndex(new Point(0, paramInt));
              localRectangle1 = localJList.getCellBounds(m, m);
              if (localRectangle1 != null) {
                i = localRectangle1.y;
              }
            }
            if ((k == 0) && ((j == 1) || (j == 2))) {
              if (localJScrollPane.getComponentOrientation().isLeftToRight())
              {
                m = localJList.locationToIndex(new Point(paramInt, 0));
                localRectangle1 = localJList.getCellBounds(m, m);
                if (localRectangle1 != null) {
                  i = localRectangle1.x;
                }
              }
              else
              {
                Point localPoint = new Point(paramInt, 0);
                int n = localJViewport.getExtentSize().width;
                localPoint.x += n - 1;
                int i1 = localJList.locationToIndex(localPoint);
                Rectangle localRectangle2 = localJList.getCellBounds(i1, i1);
                if (localRectangle2 != null) {
                  i = localRectangle2.x + localRectangle2.width - n;
                }
              }
            }
            paramInt = i;
          }
        }
      }
      return paramInt;
    }
    
    private void startScrollTimerIfNecessary()
    {
      if (BasicScrollBarUI.this.scrollTimer.isRunning()) {
        return;
      }
      Rectangle localRectangle = BasicScrollBarUI.this.getThumbBounds();
      switch (BasicScrollBarUI.this.scrollbar.getOrientation())
      {
      case 1: 
        if (this.direction > 0)
        {
          if (localRectangle.y + localRectangle.height < BasicScrollBarUI.this.trackListener.currentMouseY) {
            BasicScrollBarUI.this.scrollTimer.start();
          }
        }
        else if (localRectangle.y > BasicScrollBarUI.this.trackListener.currentMouseY) {
          BasicScrollBarUI.this.scrollTimer.start();
        }
        break;
      case 0: 
        if (((this.direction > 0) && (BasicScrollBarUI.this.isMouseAfterThumb())) || ((this.direction < 0) && (BasicScrollBarUI.this.isMouseBeforeThumb()))) {
          BasicScrollBarUI.this.scrollTimer.start();
        }
        break;
      }
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      if (!BasicScrollBarUI.this.isDragging) {
        BasicScrollBarUI.this.updateThumbState(paramMouseEvent.getX(), paramMouseEvent.getY());
      }
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      if (!BasicScrollBarUI.this.isDragging) {
        BasicScrollBarUI.this.setThumbRollover(false);
      }
    }
  }
}
