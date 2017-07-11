package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTabbedPaneUI
  extends TabbedPaneUI
  implements SwingConstants
{
  protected JTabbedPane tabPane;
  protected Color highlight;
  protected Color lightHighlight;
  protected Color shadow;
  protected Color darkShadow;
  protected Color focus;
  private Color selectedColor;
  protected int textIconGap;
  protected int tabRunOverlay;
  protected Insets tabInsets;
  protected Insets selectedTabPadInsets;
  protected Insets tabAreaInsets;
  protected Insets contentBorderInsets;
  private boolean tabsOverlapBorder;
  private boolean tabsOpaque = true;
  private boolean contentOpaque = true;
  @Deprecated
  protected KeyStroke upKey;
  @Deprecated
  protected KeyStroke downKey;
  @Deprecated
  protected KeyStroke leftKey;
  @Deprecated
  protected KeyStroke rightKey;
  protected int[] tabRuns = new int[10];
  protected int runCount = 0;
  protected int selectedRun = -1;
  protected Rectangle[] rects = new Rectangle[0];
  protected int maxTabHeight;
  protected int maxTabWidth;
  protected ChangeListener tabChangeListener;
  protected PropertyChangeListener propertyChangeListener;
  protected MouseListener mouseListener;
  protected FocusListener focusListener;
  private Insets currentPadInsets = new Insets(0, 0, 0, 0);
  private Insets currentTabAreaInsets = new Insets(0, 0, 0, 0);
  private Component visibleComponent;
  private Vector<View> htmlViews;
  private Hashtable<Integer, Integer> mnemonicToIndexMap;
  private InputMap mnemonicInputMap;
  private ScrollableTabSupport tabScroller;
  private TabContainer tabContainer;
  protected transient Rectangle calcRect = new Rectangle(0, 0, 0, 0);
  private int focusIndex;
  private Handler handler;
  private int rolloverTabIndex;
  private boolean isRunsDirty;
  private boolean calculatedBaseline;
  private int baseline;
  private static int[] xCropLen = { 1, 1, 0, 0, 1, 1, 2, 2 };
  private static int[] yCropLen = { 0, 3, 3, 6, 6, 9, 9, 12 };
  private static final int CROP_SEGMENT = 12;
  
  public BasicTabbedPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicTabbedPaneUI();
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("navigateNext"));
    paramLazyActionMap.put(new Actions("navigatePrevious"));
    paramLazyActionMap.put(new Actions("navigateRight"));
    paramLazyActionMap.put(new Actions("navigateLeft"));
    paramLazyActionMap.put(new Actions("navigateUp"));
    paramLazyActionMap.put(new Actions("navigateDown"));
    paramLazyActionMap.put(new Actions("navigatePageUp"));
    paramLazyActionMap.put(new Actions("navigatePageDown"));
    paramLazyActionMap.put(new Actions("requestFocus"));
    paramLazyActionMap.put(new Actions("requestFocusForVisibleComponent"));
    paramLazyActionMap.put(new Actions("setSelectedIndex"));
    paramLazyActionMap.put(new Actions("selectTabWithFocus"));
    paramLazyActionMap.put(new Actions("scrollTabsForwardAction"));
    paramLazyActionMap.put(new Actions("scrollTabsBackwardAction"));
  }
  
  public void installUI(JComponent paramJComponent)
  {
    this.tabPane = ((JTabbedPane)paramJComponent);
    this.calculatedBaseline = false;
    this.rolloverTabIndex = -1;
    this.focusIndex = -1;
    paramJComponent.setLayout(createLayoutManager());
    installComponents();
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallKeyboardActions();
    uninstallListeners();
    uninstallDefaults();
    uninstallComponents();
    paramJComponent.setLayout(null);
    this.tabPane = null;
  }
  
  protected LayoutManager createLayoutManager()
  {
    if (this.tabPane.getTabLayoutPolicy() == 1) {
      return new TabbedPaneScrollLayout(null);
    }
    return new TabbedPaneLayout();
  }
  
  private boolean scrollableTabLayoutEnabled()
  {
    return this.tabPane.getLayout() instanceof TabbedPaneScrollLayout;
  }
  
  protected void installComponents()
  {
    if ((scrollableTabLayoutEnabled()) && (this.tabScroller == null))
    {
      this.tabScroller = new ScrollableTabSupport(this.tabPane.getTabPlacement());
      this.tabPane.add(this.tabScroller.viewport);
    }
    installTabContainer();
  }
  
  private void installTabContainer()
  {
    for (int i = 0; i < this.tabPane.getTabCount(); i++)
    {
      Component localComponent = this.tabPane.getTabComponentAt(i);
      if (localComponent != null)
      {
        if (this.tabContainer == null) {
          this.tabContainer = new TabContainer();
        }
        this.tabContainer.add(localComponent);
      }
    }
    if (this.tabContainer == null) {
      return;
    }
    if (scrollableTabLayoutEnabled()) {
      this.tabScroller.tabPanel.add(this.tabContainer);
    } else {
      this.tabPane.add(this.tabContainer);
    }
  }
  
  protected JButton createScrollButton(int paramInt)
  {
    if ((paramInt != 5) && (paramInt != 1) && (paramInt != 3) && (paramInt != 7)) {
      throw new IllegalArgumentException("Direction must be one of: SOUTH, NORTH, EAST or WEST");
    }
    return new ScrollableTabButton(paramInt);
  }
  
  protected void uninstallComponents()
  {
    uninstallTabContainer();
    if (scrollableTabLayoutEnabled())
    {
      this.tabPane.remove(this.tabScroller.viewport);
      this.tabPane.remove(this.tabScroller.scrollForwardButton);
      this.tabPane.remove(this.tabScroller.scrollBackwardButton);
      this.tabScroller = null;
    }
  }
  
  private void uninstallTabContainer()
  {
    if (this.tabContainer == null) {
      return;
    }
    this.tabContainer.notifyTabbedPane = false;
    this.tabContainer.removeAll();
    if (scrollableTabLayoutEnabled())
    {
      this.tabContainer.remove(this.tabScroller.croppedEdge);
      this.tabScroller.tabPanel.remove(this.tabContainer);
    }
    else
    {
      this.tabPane.remove(this.tabContainer);
    }
    this.tabContainer = null;
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installColorsAndFont(this.tabPane, "TabbedPane.background", "TabbedPane.foreground", "TabbedPane.font");
    this.highlight = UIManager.getColor("TabbedPane.light");
    this.lightHighlight = UIManager.getColor("TabbedPane.highlight");
    this.shadow = UIManager.getColor("TabbedPane.shadow");
    this.darkShadow = UIManager.getColor("TabbedPane.darkShadow");
    this.focus = UIManager.getColor("TabbedPane.focus");
    this.selectedColor = UIManager.getColor("TabbedPane.selected");
    this.textIconGap = UIManager.getInt("TabbedPane.textIconGap");
    this.tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
    this.selectedTabPadInsets = UIManager.getInsets("TabbedPane.selectedTabPadInsets");
    this.tabAreaInsets = UIManager.getInsets("TabbedPane.tabAreaInsets");
    this.tabsOverlapBorder = UIManager.getBoolean("TabbedPane.tabsOverlapBorder");
    this.contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
    this.tabRunOverlay = UIManager.getInt("TabbedPane.tabRunOverlay");
    this.tabsOpaque = UIManager.getBoolean("TabbedPane.tabsOpaque");
    this.contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");
    Object localObject = UIManager.get("TabbedPane.opaque");
    if (localObject == null) {
      localObject = Boolean.FALSE;
    }
    LookAndFeel.installProperty(this.tabPane, "opaque", localObject);
    if (this.tabInsets == null) {
      this.tabInsets = new Insets(0, 4, 1, 4);
    }
    if (this.selectedTabPadInsets == null) {
      this.selectedTabPadInsets = new Insets(2, 2, 2, 1);
    }
    if (this.tabAreaInsets == null) {
      this.tabAreaInsets = new Insets(3, 2, 0, 2);
    }
    if (this.contentBorderInsets == null) {
      this.contentBorderInsets = new Insets(2, 2, 3, 3);
    }
  }
  
  protected void uninstallDefaults()
  {
    this.highlight = null;
    this.lightHighlight = null;
    this.shadow = null;
    this.darkShadow = null;
    this.focus = null;
    this.tabInsets = null;
    this.selectedTabPadInsets = null;
    this.tabAreaInsets = null;
    this.contentBorderInsets = null;
  }
  
  protected void installListeners()
  {
    if ((this.propertyChangeListener = createPropertyChangeListener()) != null) {
      this.tabPane.addPropertyChangeListener(this.propertyChangeListener);
    }
    if ((this.tabChangeListener = createChangeListener()) != null) {
      this.tabPane.addChangeListener(this.tabChangeListener);
    }
    if ((this.mouseListener = createMouseListener()) != null) {
      this.tabPane.addMouseListener(this.mouseListener);
    }
    this.tabPane.addMouseMotionListener(getHandler());
    if ((this.focusListener = createFocusListener()) != null) {
      this.tabPane.addFocusListener(this.focusListener);
    }
    this.tabPane.addContainerListener(getHandler());
    if (this.tabPane.getTabCount() > 0) {
      this.htmlViews = createHTMLVector();
    }
  }
  
  protected void uninstallListeners()
  {
    if (this.mouseListener != null)
    {
      this.tabPane.removeMouseListener(this.mouseListener);
      this.mouseListener = null;
    }
    this.tabPane.removeMouseMotionListener(getHandler());
    if (this.focusListener != null)
    {
      this.tabPane.removeFocusListener(this.focusListener);
      this.focusListener = null;
    }
    this.tabPane.removeContainerListener(getHandler());
    if (this.htmlViews != null)
    {
      this.htmlViews.removeAllElements();
      this.htmlViews = null;
    }
    if (this.tabChangeListener != null)
    {
      this.tabPane.removeChangeListener(this.tabChangeListener);
      this.tabChangeListener = null;
    }
    if (this.propertyChangeListener != null)
    {
      this.tabPane.removePropertyChangeListener(this.propertyChangeListener);
      this.propertyChangeListener = null;
    }
    this.handler = null;
  }
  
  protected MouseListener createMouseListener()
  {
    return getHandler();
  }
  
  protected FocusListener createFocusListener()
  {
    return getHandler();
  }
  
  protected ChangeListener createChangeListener()
  {
    return getHandler();
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  private Handler getHandler()
  {
    if (this.handler == null) {
      this.handler = new Handler(null);
    }
    return this.handler;
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(this.tabPane, 1, localInputMap);
    localInputMap = getInputMap(0);
    SwingUtilities.replaceUIInputMap(this.tabPane, 0, localInputMap);
    LazyActionMap.installLazyActionMap(this.tabPane, BasicTabbedPaneUI.class, "TabbedPane.actionMap");
    updateMnemonics();
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 1) {
      return (InputMap)DefaultLookup.get(this.tabPane, this, "TabbedPane.ancestorInputMap");
    }
    if (paramInt == 0) {
      return (InputMap)DefaultLookup.get(this.tabPane, this, "TabbedPane.focusInputMap");
    }
    return null;
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIActionMap(this.tabPane, null);
    SwingUtilities.replaceUIInputMap(this.tabPane, 1, null);
    SwingUtilities.replaceUIInputMap(this.tabPane, 0, null);
    SwingUtilities.replaceUIInputMap(this.tabPane, 2, null);
    this.mnemonicToIndexMap = null;
    this.mnemonicInputMap = null;
  }
  
  private void updateMnemonics()
  {
    resetMnemonics();
    for (int i = this.tabPane.getTabCount() - 1; i >= 0; i--)
    {
      int j = this.tabPane.getMnemonicAt(i);
      if (j > 0) {
        addMnemonic(i, j);
      }
    }
  }
  
  private void resetMnemonics()
  {
    if (this.mnemonicToIndexMap != null)
    {
      this.mnemonicToIndexMap.clear();
      this.mnemonicInputMap.clear();
    }
  }
  
  private void addMnemonic(int paramInt1, int paramInt2)
  {
    if (this.mnemonicToIndexMap == null) {
      initMnemonics();
    }
    this.mnemonicInputMap.put(KeyStroke.getKeyStroke(paramInt2, BasicLookAndFeel.getFocusAcceleratorKeyMask()), "setSelectedIndex");
    this.mnemonicToIndexMap.put(Integer.valueOf(paramInt2), Integer.valueOf(paramInt1));
  }
  
  private void initMnemonics()
  {
    this.mnemonicToIndexMap = new Hashtable();
    this.mnemonicInputMap = new ComponentInputMapUIResource(this.tabPane);
    this.mnemonicInputMap.setParent(SwingUtilities.getUIInputMap(this.tabPane, 2));
    SwingUtilities.replaceUIInputMap(this.tabPane, 2, this.mnemonicInputMap);
  }
  
  private void setRolloverTab(int paramInt1, int paramInt2)
  {
    setRolloverTab(tabForCoordinate(this.tabPane, paramInt1, paramInt2, false));
  }
  
  protected void setRolloverTab(int paramInt)
  {
    this.rolloverTabIndex = paramInt;
  }
  
  protected int getRolloverTab()
  {
    return this.rolloverTabIndex;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    return null;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return null;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    int i = calculateBaselineIfNecessary();
    if (i != -1)
    {
      int j = this.tabPane.getTabPlacement();
      Insets localInsets1 = this.tabPane.getInsets();
      Insets localInsets2 = getTabAreaInsets(j);
      switch (j)
      {
      case 1: 
        i += localInsets1.top + localInsets2.top;
        return i;
      case 3: 
        i = paramInt2 - localInsets1.bottom - localInsets2.bottom - this.maxTabHeight + i;
        return i;
      case 2: 
      case 4: 
        i += localInsets1.top + localInsets2.top;
        return i;
      }
    }
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    switch (this.tabPane.getTabPlacement())
    {
    case 1: 
    case 2: 
    case 4: 
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    case 3: 
      return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
    }
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  protected int getBaseline(int paramInt)
  {
    if (this.tabPane.getTabComponentAt(paramInt) != null)
    {
      int i = getBaselineOffset();
      if (i != 0) {
        return -1;
      }
      Component localComponent = this.tabPane.getTabComponentAt(paramInt);
      Dimension localDimension = localComponent.getPreferredSize();
      Insets localInsets = getTabInsets(this.tabPane.getTabPlacement(), paramInt);
      int m = this.maxTabHeight - localInsets.top - localInsets.bottom;
      return localComponent.getBaseline(localDimension.width, localDimension.height) + (m - localDimension.height) / 2 + localInsets.top;
    }
    Object localObject = getTextViewForTab(paramInt);
    if (localObject != null)
    {
      j = (int)((View)localObject).getPreferredSpan(1);
      k = BasicHTML.getHTMLBaseline((View)localObject, (int)((View)localObject).getPreferredSpan(0), j);
      if (k >= 0) {
        return this.maxTabHeight / 2 - j / 2 + k + getBaselineOffset();
      }
      return -1;
    }
    localObject = getFontMetrics();
    int j = ((FontMetrics)localObject).getHeight();
    int k = ((FontMetrics)localObject).getAscent();
    return this.maxTabHeight / 2 - j / 2 + k + getBaselineOffset();
  }
  
  protected int getBaselineOffset()
  {
    switch (this.tabPane.getTabPlacement())
    {
    case 1: 
      if (this.tabPane.getTabCount() > 1) {
        return 1;
      }
      return -1;
    case 3: 
      if (this.tabPane.getTabCount() > 1) {
        return -1;
      }
      return 1;
    }
    return this.maxTabHeight % 2;
  }
  
  private int calculateBaselineIfNecessary()
  {
    if (!this.calculatedBaseline)
    {
      this.calculatedBaseline = true;
      this.baseline = -1;
      if (this.tabPane.getTabCount() > 0) {
        calculateBaseline();
      }
    }
    return this.baseline;
  }
  
  private void calculateBaseline()
  {
    int i = this.tabPane.getTabCount();
    int j = this.tabPane.getTabPlacement();
    this.maxTabHeight = calculateMaxTabHeight(j);
    this.baseline = getBaseline(0);
    if (isHorizontalTabPlacement())
    {
      for (int k = 1; k < i; k++) {
        if (getBaseline(k) != this.baseline)
        {
          this.baseline = -1;
          break;
        }
      }
    }
    else
    {
      FontMetrics localFontMetrics = getFontMetrics();
      int m = localFontMetrics.getHeight();
      int n = calculateTabHeight(j, 0, m);
      for (int i1 = 1; i1 < i; i1++)
      {
        int i2 = calculateTabHeight(j, i1, m);
        if (n != i2)
        {
          this.baseline = -1;
          break;
        }
      }
    }
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    int i = this.tabPane.getSelectedIndex();
    int j = this.tabPane.getTabPlacement();
    ensureCurrentLayout();
    if (this.tabsOverlapBorder) {
      paintContentBorder(paramGraphics, j, i);
    }
    if (!scrollableTabLayoutEnabled()) {
      paintTabArea(paramGraphics, j, i);
    }
    if (!this.tabsOverlapBorder) {
      paintContentBorder(paramGraphics, j, i);
    }
  }
  
  protected void paintTabArea(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    int i = this.tabPane.getTabCount();
    Rectangle localRectangle1 = new Rectangle();
    Rectangle localRectangle2 = new Rectangle();
    Rectangle localRectangle3 = paramGraphics.getClipBounds();
    for (int j = this.runCount - 1; j >= 0; j--)
    {
      int k = this.tabRuns[j];
      int m = this.tabRuns[(j + 1)];
      int n = m != 0 ? m - 1 : i - 1;
      for (int i1 = k; i1 <= n; i1++) {
        if ((i1 != paramInt2) && (this.rects[i1].intersects(localRectangle3))) {
          paintTab(paramGraphics, paramInt1, this.rects, i1, localRectangle1, localRectangle2);
        }
      }
    }
    if ((paramInt2 >= 0) && (this.rects[paramInt2].intersects(localRectangle3))) {
      paintTab(paramGraphics, paramInt1, this.rects, paramInt2, localRectangle1, localRectangle2);
    }
  }
  
  protected void paintTab(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2)
  {
    Rectangle localRectangle = paramArrayOfRectangle[paramInt2];
    int i = this.tabPane.getSelectedIndex();
    boolean bool = i == paramInt2;
    if ((this.tabsOpaque) || (this.tabPane.isOpaque())) {
      paintTabBackground(paramGraphics, paramInt1, paramInt2, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, bool);
    }
    paintTabBorder(paramGraphics, paramInt1, paramInt2, localRectangle.x, localRectangle.y, localRectangle.width, localRectangle.height, bool);
    String str1 = this.tabPane.getTitleAt(paramInt2);
    Font localFont = this.tabPane.getFont();
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(this.tabPane, paramGraphics, localFont);
    Icon localIcon = getIconForTab(paramInt2);
    layoutLabel(paramInt1, localFontMetrics, paramInt2, str1, localIcon, localRectangle, paramRectangle1, paramRectangle2, bool);
    if (this.tabPane.getTabComponentAt(paramInt2) == null)
    {
      String str2 = str1;
      if ((scrollableTabLayoutEnabled()) && (this.tabScroller.croppedEdge.isParamsSet()) && (this.tabScroller.croppedEdge.getTabIndex() == paramInt2) && (isHorizontalTabPlacement()))
      {
        int j = this.tabScroller.croppedEdge.getCropline() - (paramRectangle2.x - localRectangle.x) - this.tabScroller.croppedEdge.getCroppedSideWidth();
        str2 = SwingUtilities2.clipStringIfNecessary(null, localFontMetrics, str1, j);
      }
      else if ((!scrollableTabLayoutEnabled()) && (isHorizontalTabPlacement()))
      {
        str2 = SwingUtilities2.clipStringIfNecessary(null, localFontMetrics, str1, paramRectangle2.width);
      }
      paintText(paramGraphics, paramInt1, localFont, localFontMetrics, paramInt2, str2, paramRectangle2, bool);
      paintIcon(paramGraphics, paramInt1, paramInt2, localIcon, paramRectangle1, bool);
    }
    paintFocusIndicator(paramGraphics, paramInt1, paramArrayOfRectangle, paramInt2, paramRectangle1, paramRectangle2, bool);
  }
  
  private boolean isHorizontalTabPlacement()
  {
    return (this.tabPane.getTabPlacement() == 1) || (this.tabPane.getTabPlacement() == 3);
  }
  
  private static Polygon createCroppedTabShape(int paramInt1, Rectangle paramRectangle, int paramInt2)
  {
    int i;
    int j;
    int k;
    int m;
    switch (paramInt1)
    {
    case 2: 
    case 4: 
      i = paramRectangle.width;
      j = paramRectangle.x;
      k = paramRectangle.x + paramRectangle.width;
      m = paramRectangle.y + paramRectangle.height;
      break;
    case 1: 
    case 3: 
    default: 
      i = paramRectangle.height;
      j = paramRectangle.y;
      k = paramRectangle.y + paramRectangle.height;
      m = paramRectangle.x + paramRectangle.width;
    }
    int n = i / 12;
    if (i % 12 > 0) {
      n++;
    }
    int i1 = 2 + n * 8;
    int[] arrayOfInt1 = new int[i1];
    int[] arrayOfInt2 = new int[i1];
    int i2 = 0;
    arrayOfInt1[i2] = m;
    arrayOfInt2[(i2++)] = k;
    arrayOfInt1[i2] = m;
    arrayOfInt2[(i2++)] = j;
    for (int i3 = 0; i3 < n; i3++) {
      for (int i4 = 0; i4 < xCropLen.length; i4++)
      {
        arrayOfInt1[i2] = (paramInt2 - xCropLen[i4]);
        arrayOfInt2[i2] = (j + i3 * 12 + yCropLen[i4]);
        if (arrayOfInt2[i2] >= k)
        {
          arrayOfInt2[i2] = k;
          i2++;
          break;
        }
        i2++;
      }
    }
    if ((paramInt1 == 1) || (paramInt1 == 3)) {
      return new Polygon(arrayOfInt1, arrayOfInt2, i2);
    }
    return new Polygon(arrayOfInt2, arrayOfInt1, i2);
  }
  
  private void paintCroppedTabEdge(Graphics paramGraphics)
  {
    int i = this.tabScroller.croppedEdge.getTabIndex();
    int j = this.tabScroller.croppedEdge.getCropline();
    int k;
    int m;
    int n;
    switch (this.tabPane.getTabPlacement())
    {
    case 2: 
    case 4: 
      k = this.rects[i].x;
      m = j;
      n = k;
      paramGraphics.setColor(this.shadow);
    }
    while (n <= k + this.rects[i].width)
    {
      for (int i1 = 0; i1 < xCropLen.length; i1 += 2) {
        paramGraphics.drawLine(n + yCropLen[i1], m - xCropLen[i1], n + yCropLen[(i1 + 1)] - 1, m - xCropLen[(i1 + 1)]);
      }
      n += 12;
      continue;
      k = j;
      m = this.rects[i].y;
      i1 = m;
      paramGraphics.setColor(this.shadow);
      while (i1 <= m + this.rects[i].height)
      {
        for (int i2 = 0; i2 < xCropLen.length; i2 += 2) {
          paramGraphics.drawLine(k - xCropLen[i2], i1 + yCropLen[i2], k - xCropLen[(i2 + 1)], i1 + yCropLen[(i2 + 1)] - 1);
        }
        i1 += 12;
      }
    }
  }
  
  protected void layoutLabel(int paramInt1, FontMetrics paramFontMetrics, int paramInt2, String paramString, Icon paramIcon, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, boolean paramBoolean)
  {
    paramRectangle3.x = (paramRectangle3.y = paramRectangle2.x = paramRectangle2.y = 0);
    View localView = getTextViewForTab(paramInt2);
    if (localView != null) {
      this.tabPane.putClientProperty("html", localView);
    }
    SwingUtilities.layoutCompoundLabel(this.tabPane, paramFontMetrics, paramString, paramIcon, 0, 0, 0, 11, paramRectangle1, paramRectangle2, paramRectangle3, this.textIconGap);
    this.tabPane.putClientProperty("html", null);
    int i = getTabLabelShiftX(paramInt1, paramInt2, paramBoolean);
    int j = getTabLabelShiftY(paramInt1, paramInt2, paramBoolean);
    paramRectangle2.x += i;
    paramRectangle2.y += j;
    paramRectangle3.x += i;
    paramRectangle3.y += j;
  }
  
  protected void paintIcon(Graphics paramGraphics, int paramInt1, int paramInt2, Icon paramIcon, Rectangle paramRectangle, boolean paramBoolean)
  {
    if (paramIcon != null) {
      paramIcon.paintIcon(this.tabPane, paramGraphics, paramRectangle.x, paramRectangle.y);
    }
  }
  
  protected void paintText(Graphics paramGraphics, int paramInt1, Font paramFont, FontMetrics paramFontMetrics, int paramInt2, String paramString, Rectangle paramRectangle, boolean paramBoolean)
  {
    paramGraphics.setFont(paramFont);
    View localView = getTextViewForTab(paramInt2);
    if (localView != null)
    {
      localView.paint(paramGraphics, paramRectangle);
    }
    else
    {
      int i = this.tabPane.getDisplayedMnemonicIndexAt(paramInt2);
      if ((this.tabPane.isEnabled()) && (this.tabPane.isEnabledAt(paramInt2)))
      {
        Object localObject = this.tabPane.getForegroundAt(paramInt2);
        if ((paramBoolean) && ((localObject instanceof UIResource)))
        {
          Color localColor = UIManager.getColor("TabbedPane.selectedForeground");
          if (localColor != null) {
            localObject = localColor;
          }
        }
        paramGraphics.setColor((Color)localObject);
        SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + paramFontMetrics.getAscent());
      }
      else
      {
        paramGraphics.setColor(this.tabPane.getBackgroundAt(paramInt2).brighter());
        SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + paramFontMetrics.getAscent());
        paramGraphics.setColor(this.tabPane.getBackgroundAt(paramInt2).darker());
        SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, paramGraphics, paramString, i, paramRectangle.x - 1, paramRectangle.y + paramFontMetrics.getAscent() - 1);
      }
    }
  }
  
  protected int getTabLabelShiftX(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Rectangle localRectangle = this.rects[paramInt2];
    String str = paramBoolean ? "selectedLabelShift" : "labelShift";
    int i = DefaultLookup.getInt(this.tabPane, this, "TabbedPane." + str, 1);
    switch (paramInt1)
    {
    case 2: 
      return i;
    case 4: 
      return -i;
    }
    return localRectangle.width % 2;
  }
  
  protected int getTabLabelShiftY(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Rectangle localRectangle = this.rects[paramInt2];
    int i = paramBoolean ? DefaultLookup.getInt(this.tabPane, this, "TabbedPane.selectedLabelShift", -1) : DefaultLookup.getInt(this.tabPane, this, "TabbedPane.labelShift", 1);
    switch (paramInt1)
    {
    case 3: 
      return -i;
    case 2: 
    case 4: 
      return localRectangle.height % 2;
    }
    return i;
  }
  
  protected void paintFocusIndicator(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2, boolean paramBoolean)
  {
    Rectangle localRectangle = paramArrayOfRectangle[paramInt2];
    if ((this.tabPane.hasFocus()) && (paramBoolean))
    {
      paramGraphics.setColor(this.focus);
      int i;
      int j;
      int k;
      int m;
      switch (paramInt1)
      {
      case 2: 
        i = localRectangle.x + 3;
        j = localRectangle.y + 3;
        k = localRectangle.width - 5;
        m = localRectangle.height - 6;
        break;
      case 4: 
        i = localRectangle.x + 2;
        j = localRectangle.y + 3;
        k = localRectangle.width - 5;
        m = localRectangle.height - 6;
        break;
      case 3: 
        i = localRectangle.x + 3;
        j = localRectangle.y + 2;
        k = localRectangle.width - 6;
        m = localRectangle.height - 5;
        break;
      case 1: 
      default: 
        i = localRectangle.x + 3;
        j = localRectangle.y + 3;
        k = localRectangle.width - 6;
        m = localRectangle.height - 5;
      }
      BasicGraphicsUtils.drawDashedRect(paramGraphics, i, j, k, m);
    }
  }
  
  protected void paintTabBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    paramGraphics.setColor(this.lightHighlight);
    switch (paramInt1)
    {
    case 2: 
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + 1, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 3);
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 1);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + paramInt5 - 1, paramInt4);
      paramGraphics.setColor(this.shadow);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
      break;
    case 4: 
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 3, paramInt4);
      paramGraphics.setColor(this.shadow);
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
      break;
    case 3: 
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, paramInt4 + paramInt6 - 3);
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + 1, paramInt4 + paramInt6 - 2);
      paramGraphics.setColor(this.shadow);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
      break;
    case 1: 
    default: 
      paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 1);
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 1);
      paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + paramInt5 - 3, paramInt4);
      paramGraphics.setColor(this.shadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 1);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
    }
  }
  
  protected void paintTabBackground(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean)
  {
    paramGraphics.setColor((!paramBoolean) || (this.selectedColor == null) ? this.tabPane.getBackgroundAt(paramInt2) : this.selectedColor);
    switch (paramInt1)
    {
    case 2: 
      paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 1, paramInt6 - 3);
      break;
    case 4: 
      paramGraphics.fillRect(paramInt3, paramInt4 + 1, paramInt5 - 2, paramInt6 - 3);
      break;
    case 3: 
      paramGraphics.fillRect(paramInt3 + 1, paramInt4, paramInt5 - 3, paramInt6 - 1);
      break;
    case 1: 
    default: 
      paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 3, paramInt6 - 1);
    }
  }
  
  protected void paintContentBorder(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    int i = this.tabPane.getWidth();
    int j = this.tabPane.getHeight();
    Insets localInsets1 = this.tabPane.getInsets();
    Insets localInsets2 = getTabAreaInsets(paramInt1);
    int k = localInsets1.left;
    int m = localInsets1.top;
    int n = i - localInsets1.right - localInsets1.left;
    int i1 = j - localInsets1.top - localInsets1.bottom;
    switch (paramInt1)
    {
    case 2: 
      k += calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth);
      if (this.tabsOverlapBorder) {
        k -= localInsets2.right;
      }
      n -= k - localInsets1.left;
      break;
    case 4: 
      n -= calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth);
      if (this.tabsOverlapBorder) {
        n += localInsets2.left;
      }
      break;
    case 3: 
      i1 -= calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight);
      if (this.tabsOverlapBorder) {
        i1 += localInsets2.top;
      }
      break;
    case 1: 
    default: 
      m += calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight);
      if (this.tabsOverlapBorder) {
        m -= localInsets2.bottom;
      }
      i1 -= m - localInsets1.top;
    }
    if ((this.tabPane.getTabCount() > 0) && ((this.contentOpaque) || (this.tabPane.isOpaque())))
    {
      Color localColor = UIManager.getColor("TabbedPane.contentAreaColor");
      if (localColor != null) {
        paramGraphics.setColor(localColor);
      } else if ((this.selectedColor == null) || (paramInt2 == -1)) {
        paramGraphics.setColor(this.tabPane.getBackground());
      } else {
        paramGraphics.setColor(this.selectedColor);
      }
      paramGraphics.fillRect(k, m, n, i1);
    }
    paintContentBorderTopEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
    paintContentBorderLeftEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
    paintContentBorderBottomEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
    paintContentBorderRightEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
  }
  
  protected void paintContentBorderTopEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.lightHighlight);
    if ((paramInt1 != 1) || (paramInt2 < 0) || (localRectangle.y + localRectangle.height + 1 < paramInt4) || (localRectangle.x < paramInt3) || (localRectangle.x > paramInt3 + paramInt5))
    {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
    }
    else
    {
      paramGraphics.drawLine(paramInt3, paramInt4, localRectangle.x - 1, paramInt4);
      if (localRectangle.x + localRectangle.width < paramInt3 + paramInt5 - 2)
      {
        paramGraphics.drawLine(localRectangle.x + localRectangle.width, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
      }
      else
      {
        paramGraphics.setColor(this.shadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
      }
    }
  }
  
  protected void paintContentBorderLeftEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.lightHighlight);
    if ((paramInt1 != 2) || (paramInt2 < 0) || (localRectangle.x + localRectangle.width + 1 < paramInt3) || (localRectangle.y < paramInt4) || (localRectangle.y > paramInt4 + paramInt6))
    {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, paramInt4 + paramInt6 - 2);
    }
    else
    {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, localRectangle.y - 1);
      if (localRectangle.y + localRectangle.height < paramInt4 + paramInt6 - 2) {
        paramGraphics.drawLine(paramInt3, localRectangle.y + localRectangle.height, paramInt3, paramInt4 + paramInt6 - 2);
      }
    }
  }
  
  protected void paintContentBorderBottomEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.shadow);
    if ((paramInt1 != 3) || (paramInt2 < 0) || (localRectangle.y - 1 > paramInt6) || (localRectangle.x < paramInt3) || (localRectangle.x > paramInt3 + paramInt5))
    {
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    }
    else
    {
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, localRectangle.x - 1, paramInt4 + paramInt6 - 2);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, localRectangle.x - 1, paramInt4 + paramInt6 - 1);
      if (localRectangle.x + localRectangle.width < paramInt3 + paramInt5 - 2)
      {
        paramGraphics.setColor(this.shadow);
        paramGraphics.drawLine(localRectangle.x + localRectangle.width, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
        paramGraphics.setColor(this.darkShadow);
        paramGraphics.drawLine(localRectangle.x + localRectangle.width, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
      }
    }
  }
  
  protected void paintContentBorderRightEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    Rectangle localRectangle = paramInt2 < 0 ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.shadow);
    if ((paramInt1 != 4) || (paramInt2 < 0) || (localRectangle.x - 1 > paramInt5) || (localRectangle.y < paramInt4) || (localRectangle.y > paramInt4 + paramInt6))
    {
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    }
    else
    {
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, localRectangle.y - 1);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, localRectangle.y - 1);
      if (localRectangle.y + localRectangle.height < paramInt4 + paramInt6 - 2)
      {
        paramGraphics.setColor(this.shadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, localRectangle.y + localRectangle.height, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
        paramGraphics.setColor(this.darkShadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 1, localRectangle.y + localRectangle.height, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2);
      }
    }
  }
  
  private void ensureCurrentLayout()
  {
    if (!this.tabPane.isValid()) {
      this.tabPane.validate();
    }
    if (!this.tabPane.isValid())
    {
      TabbedPaneLayout localTabbedPaneLayout = (TabbedPaneLayout)this.tabPane.getLayout();
      localTabbedPaneLayout.calculateLayoutInfo();
    }
  }
  
  public Rectangle getTabBounds(JTabbedPane paramJTabbedPane, int paramInt)
  {
    ensureCurrentLayout();
    Rectangle localRectangle = new Rectangle();
    return getTabBounds(paramInt, localRectangle);
  }
  
  public int getTabRunCount(JTabbedPane paramJTabbedPane)
  {
    ensureCurrentLayout();
    return this.runCount;
  }
  
  public int tabForCoordinate(JTabbedPane paramJTabbedPane, int paramInt1, int paramInt2)
  {
    return tabForCoordinate(paramJTabbedPane, paramInt1, paramInt2, true);
  }
  
  private int tabForCoordinate(JTabbedPane paramJTabbedPane, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean) {
      ensureCurrentLayout();
    }
    if (this.isRunsDirty) {
      return -1;
    }
    Point localPoint = new Point(paramInt1, paramInt2);
    if (scrollableTabLayoutEnabled())
    {
      translatePointToTabPanel(paramInt1, paramInt2, localPoint);
      Rectangle localRectangle = this.tabScroller.viewport.getViewRect();
      if (!localRectangle.contains(localPoint)) {
        return -1;
      }
    }
    int i = this.tabPane.getTabCount();
    for (int j = 0; j < i; j++) {
      if (this.rects[j].contains(localPoint.x, localPoint.y)) {
        return j;
      }
    }
    return -1;
  }
  
  protected Rectangle getTabBounds(int paramInt, Rectangle paramRectangle)
  {
    paramRectangle.width = this.rects[paramInt].width;
    paramRectangle.height = this.rects[paramInt].height;
    if (scrollableTabLayoutEnabled())
    {
      Point localPoint1 = this.tabScroller.viewport.getLocation();
      Point localPoint2 = this.tabScroller.viewport.getViewPosition();
      paramRectangle.x = (this.rects[paramInt].x + localPoint1.x - localPoint2.x);
      paramRectangle.y = (this.rects[paramInt].y + localPoint1.y - localPoint2.y);
    }
    else
    {
      paramRectangle.x = this.rects[paramInt].x;
      paramRectangle.y = this.rects[paramInt].y;
    }
    return paramRectangle;
  }
  
  private int getClosestTab(int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = Math.min(this.rects.length, this.tabPane.getTabCount());
    int k = j;
    int m = this.tabPane.getTabPlacement();
    int n = (m == 1) || (m == 3) ? 1 : 0;
    int i1 = n != 0 ? paramInt1 : paramInt2;
    while (i != k)
    {
      int i2 = (k + i) / 2;
      int i3;
      int i4;
      if (n != 0)
      {
        i3 = this.rects[i2].x;
        i4 = i3 + this.rects[i2].width;
      }
      else
      {
        i3 = this.rects[i2].y;
        i4 = i3 + this.rects[i2].height;
      }
      if (i1 < i3)
      {
        k = i2;
        if (i == k) {
          return Math.max(0, i2 - 1);
        }
      }
      else if (i1 >= i4)
      {
        i = i2;
        if (k - i <= 1) {
          return Math.max(i2 + 1, j - 1);
        }
      }
      else
      {
        return i2;
      }
    }
    return i;
  }
  
  private Point translatePointToTabPanel(int paramInt1, int paramInt2, Point paramPoint)
  {
    Point localPoint1 = this.tabScroller.viewport.getLocation();
    Point localPoint2 = this.tabScroller.viewport.getViewPosition();
    paramPoint.x = (paramInt1 - localPoint1.x + localPoint2.x);
    paramPoint.y = (paramInt2 - localPoint1.y + localPoint2.y);
    return paramPoint;
  }
  
  protected Component getVisibleComponent()
  {
    return this.visibleComponent;
  }
  
  protected void setVisibleComponent(Component paramComponent)
  {
    if ((this.visibleComponent != null) && (this.visibleComponent != paramComponent) && (this.visibleComponent.getParent() == this.tabPane) && (this.visibleComponent.isVisible())) {
      this.visibleComponent.setVisible(false);
    }
    if ((paramComponent != null) && (!paramComponent.isVisible())) {
      paramComponent.setVisible(true);
    }
    this.visibleComponent = paramComponent;
  }
  
  protected void assureRectsCreated(int paramInt)
  {
    int i = this.rects.length;
    if (paramInt != i)
    {
      Rectangle[] arrayOfRectangle = new Rectangle[paramInt];
      System.arraycopy(this.rects, 0, arrayOfRectangle, 0, Math.min(i, paramInt));
      this.rects = arrayOfRectangle;
      for (int j = i; j < paramInt; j++) {
        this.rects[j] = new Rectangle();
      }
    }
  }
  
  protected void expandTabRunsArray()
  {
    int i = this.tabRuns.length;
    int[] arrayOfInt = new int[i + 10];
    System.arraycopy(this.tabRuns, 0, arrayOfInt, 0, this.runCount);
    this.tabRuns = arrayOfInt;
  }
  
  protected int getRunForTab(int paramInt1, int paramInt2)
  {
    for (int i = 0; i < this.runCount; i++)
    {
      int j = this.tabRuns[i];
      int k = lastTabInRun(paramInt1, i);
      if ((paramInt2 >= j) && (paramInt2 <= k)) {
        return i;
      }
    }
    return 0;
  }
  
  protected int lastTabInRun(int paramInt1, int paramInt2)
  {
    if (this.runCount == 1) {
      return paramInt1 - 1;
    }
    int i = paramInt2 == this.runCount - 1 ? 0 : paramInt2 + 1;
    if (this.tabRuns[i] == 0) {
      return paramInt1 - 1;
    }
    return this.tabRuns[i] - 1;
  }
  
  protected int getTabRunOverlay(int paramInt)
  {
    return this.tabRunOverlay;
  }
  
  protected int getTabRunIndent(int paramInt1, int paramInt2)
  {
    return 0;
  }
  
  protected boolean shouldPadTabRun(int paramInt1, int paramInt2)
  {
    return this.runCount > 1;
  }
  
  protected boolean shouldRotateTabRuns(int paramInt)
  {
    return true;
  }
  
  protected Icon getIconForTab(int paramInt)
  {
    return (!this.tabPane.isEnabled()) || (!this.tabPane.isEnabledAt(paramInt)) ? this.tabPane.getDisabledIconAt(paramInt) : this.tabPane.getIconAt(paramInt);
  }
  
  protected View getTextViewForTab(int paramInt)
  {
    if (this.htmlViews != null) {
      return (View)this.htmlViews.elementAt(paramInt);
    }
    return null;
  }
  
  protected int calculateTabHeight(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 0;
    Component localComponent = this.tabPane.getTabComponentAt(paramInt2);
    if (localComponent != null)
    {
      i = localComponent.getPreferredSize().height;
    }
    else
    {
      localObject = getTextViewForTab(paramInt2);
      if (localObject != null) {
        i += (int)((View)localObject).getPreferredSpan(1);
      } else {
        i += paramInt3;
      }
      Icon localIcon = getIconForTab(paramInt2);
      if (localIcon != null) {
        i = Math.max(i, localIcon.getIconHeight());
      }
    }
    Object localObject = getTabInsets(paramInt1, paramInt2);
    i += ((Insets)localObject).top + ((Insets)localObject).bottom + 2;
    return i;
  }
  
  protected int calculateMaxTabHeight(int paramInt)
  {
    FontMetrics localFontMetrics = getFontMetrics();
    int i = this.tabPane.getTabCount();
    int j = 0;
    int k = localFontMetrics.getHeight();
    for (int m = 0; m < i; m++) {
      j = Math.max(calculateTabHeight(paramInt, m, k), j);
    }
    return j;
  }
  
  protected int calculateTabWidth(int paramInt1, int paramInt2, FontMetrics paramFontMetrics)
  {
    Insets localInsets = getTabInsets(paramInt1, paramInt2);
    int i = localInsets.left + localInsets.right + 3;
    Component localComponent = this.tabPane.getTabComponentAt(paramInt2);
    if (localComponent != null)
    {
      i += localComponent.getPreferredSize().width;
    }
    else
    {
      Icon localIcon = getIconForTab(paramInt2);
      if (localIcon != null) {
        i += localIcon.getIconWidth() + this.textIconGap;
      }
      View localView = getTextViewForTab(paramInt2);
      if (localView != null)
      {
        i += (int)localView.getPreferredSpan(0);
      }
      else
      {
        String str = this.tabPane.getTitleAt(paramInt2);
        i += SwingUtilities2.stringWidth(this.tabPane, paramFontMetrics, str);
      }
    }
    return i;
  }
  
  protected int calculateMaxTabWidth(int paramInt)
  {
    FontMetrics localFontMetrics = getFontMetrics();
    int i = this.tabPane.getTabCount();
    int j = 0;
    for (int k = 0; k < i; k++) {
      j = Math.max(calculateTabWidth(paramInt, k, localFontMetrics), j);
    }
    return j;
  }
  
  protected int calculateTabAreaHeight(int paramInt1, int paramInt2, int paramInt3)
  {
    Insets localInsets = getTabAreaInsets(paramInt1);
    int i = getTabRunOverlay(paramInt1);
    return paramInt2 > 0 ? paramInt2 * (paramInt3 - i) + i + localInsets.top + localInsets.bottom : 0;
  }
  
  protected int calculateTabAreaWidth(int paramInt1, int paramInt2, int paramInt3)
  {
    Insets localInsets = getTabAreaInsets(paramInt1);
    int i = getTabRunOverlay(paramInt1);
    return paramInt2 > 0 ? paramInt2 * (paramInt3 - i) + i + localInsets.left + localInsets.right : 0;
  }
  
  protected Insets getTabInsets(int paramInt1, int paramInt2)
  {
    return this.tabInsets;
  }
  
  protected Insets getSelectedTabPadInsets(int paramInt)
  {
    rotateInsets(this.selectedTabPadInsets, this.currentPadInsets, paramInt);
    return this.currentPadInsets;
  }
  
  protected Insets getTabAreaInsets(int paramInt)
  {
    rotateInsets(this.tabAreaInsets, this.currentTabAreaInsets, paramInt);
    return this.currentTabAreaInsets;
  }
  
  protected Insets getContentBorderInsets(int paramInt)
  {
    return this.contentBorderInsets;
  }
  
  protected FontMetrics getFontMetrics()
  {
    Font localFont = this.tabPane.getFont();
    return this.tabPane.getFontMetrics(localFont);
  }
  
  protected void navigateSelectedTab(int paramInt)
  {
    int i = this.tabPane.getTabPlacement();
    int j = DefaultLookup.getBoolean(this.tabPane, this, "TabbedPane.selectionFollowsFocus", true) ? this.tabPane.getSelectedIndex() : getFocusIndex();
    int k = this.tabPane.getTabCount();
    boolean bool = BasicGraphicsUtils.isLeftToRight(this.tabPane);
    if (k <= 0) {
      return;
    }
    int m;
    switch (i)
    {
    case 2: 
    case 4: 
      switch (paramInt)
      {
      case 12: 
        selectNextTab(j);
        break;
      case 13: 
        selectPreviousTab(j);
        break;
      case 1: 
        selectPreviousTabInRun(j);
        break;
      case 5: 
        selectNextTabInRun(j);
        break;
      case 7: 
        m = getTabRunOffset(i, k, j, false);
        selectAdjacentRunTab(i, j, m);
        break;
      case 3: 
        m = getTabRunOffset(i, k, j, true);
        selectAdjacentRunTab(i, j, m);
      }
      break;
    case 1: 
    case 3: 
    default: 
      switch (paramInt)
      {
      case 12: 
        selectNextTab(j);
        break;
      case 13: 
        selectPreviousTab(j);
        break;
      case 1: 
        m = getTabRunOffset(i, k, j, false);
        selectAdjacentRunTab(i, j, m);
        break;
      case 5: 
        m = getTabRunOffset(i, k, j, true);
        selectAdjacentRunTab(i, j, m);
        break;
      case 3: 
        if (bool) {
          selectNextTabInRun(j);
        } else {
          selectPreviousTabInRun(j);
        }
        break;
      case 7: 
        if (bool) {
          selectPreviousTabInRun(j);
        } else {
          selectNextTabInRun(j);
        }
        break;
      }
      break;
    }
  }
  
  protected void selectNextTabInRun(int paramInt)
  {
    int i = this.tabPane.getTabCount();
    for (int j = getNextTabIndexInRun(i, paramInt); (j != paramInt) && (!this.tabPane.isEnabledAt(j)); j = getNextTabIndexInRun(i, j)) {}
    navigateTo(j);
  }
  
  protected void selectPreviousTabInRun(int paramInt)
  {
    int i = this.tabPane.getTabCount();
    for (int j = getPreviousTabIndexInRun(i, paramInt); (j != paramInt) && (!this.tabPane.isEnabledAt(j)); j = getPreviousTabIndexInRun(i, j)) {}
    navigateTo(j);
  }
  
  protected void selectNextTab(int paramInt)
  {
    for (int i = getNextTabIndex(paramInt); (i != paramInt) && (!this.tabPane.isEnabledAt(i)); i = getNextTabIndex(i)) {}
    navigateTo(i);
  }
  
  protected void selectPreviousTab(int paramInt)
  {
    for (int i = getPreviousTabIndex(paramInt); (i != paramInt) && (!this.tabPane.isEnabledAt(i)); i = getPreviousTabIndex(i)) {}
    navigateTo(i);
  }
  
  protected void selectAdjacentRunTab(int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.runCount < 2) {
      return;
    }
    Rectangle localRectangle = this.rects[paramInt2];
    int i;
    switch (paramInt1)
    {
    case 2: 
    case 4: 
      i = tabForCoordinate(this.tabPane, localRectangle.x + localRectangle.width / 2 + paramInt3, localRectangle.y + localRectangle.height / 2);
      break;
    case 1: 
    case 3: 
    default: 
      i = tabForCoordinate(this.tabPane, localRectangle.x + localRectangle.width / 2, localRectangle.y + localRectangle.height / 2 + paramInt3);
    }
    if (i != -1)
    {
      while ((!this.tabPane.isEnabledAt(i)) && (i != paramInt2)) {
        i = getNextTabIndex(i);
      }
      navigateTo(i);
    }
  }
  
  private void navigateTo(int paramInt)
  {
    if (DefaultLookup.getBoolean(this.tabPane, this, "TabbedPane.selectionFollowsFocus", true)) {
      this.tabPane.setSelectedIndex(paramInt);
    } else {
      setFocusIndex(paramInt, true);
    }
  }
  
  void setFocusIndex(int paramInt, boolean paramBoolean)
  {
    if ((paramBoolean) && (!this.isRunsDirty))
    {
      repaintTab(this.focusIndex);
      this.focusIndex = paramInt;
      repaintTab(this.focusIndex);
    }
    else
    {
      this.focusIndex = paramInt;
    }
  }
  
  private void repaintTab(int paramInt)
  {
    if ((!this.isRunsDirty) && (paramInt >= 0) && (paramInt < this.tabPane.getTabCount())) {
      this.tabPane.repaint(getTabBounds(this.tabPane, paramInt));
    }
  }
  
  private void validateFocusIndex()
  {
    if (this.focusIndex >= this.tabPane.getTabCount()) {
      setFocusIndex(this.tabPane.getSelectedIndex(), false);
    }
  }
  
  protected int getFocusIndex()
  {
    return this.focusIndex;
  }
  
  protected int getTabRunOffset(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int i = getRunForTab(paramInt2, paramInt3);
    int j;
    switch (paramInt1)
    {
    case 2: 
      if (i == 0) {
        j = paramBoolean ? -(calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth) : -this.maxTabWidth;
      } else if (i == this.runCount - 1) {
        j = paramBoolean ? this.maxTabWidth : calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth;
      } else {
        j = paramBoolean ? this.maxTabWidth : -this.maxTabWidth;
      }
      break;
    case 4: 
      if (i == 0) {
        j = paramBoolean ? this.maxTabWidth : calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth;
      } else if (i == this.runCount - 1) {
        j = paramBoolean ? -(calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth) : -this.maxTabWidth;
      } else {
        j = paramBoolean ? this.maxTabWidth : -this.maxTabWidth;
      }
      break;
    case 3: 
      if (i == 0) {
        j = paramBoolean ? this.maxTabHeight : calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight;
      } else if (i == this.runCount - 1) {
        j = paramBoolean ? -(calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight) : -this.maxTabHeight;
      } else {
        j = paramBoolean ? this.maxTabHeight : -this.maxTabHeight;
      }
      break;
    case 1: 
    default: 
      if (i == 0) {
        j = paramBoolean ? -(calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight) : -this.maxTabHeight;
      } else if (i == this.runCount - 1) {
        j = paramBoolean ? this.maxTabHeight : calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight;
      } else {
        j = paramBoolean ? this.maxTabHeight : -this.maxTabHeight;
      }
      break;
    }
    return j;
  }
  
  protected int getPreviousTabIndex(int paramInt)
  {
    int i = paramInt - 1 >= 0 ? paramInt - 1 : this.tabPane.getTabCount() - 1;
    return i >= 0 ? i : 0;
  }
  
  protected int getNextTabIndex(int paramInt)
  {
    return (paramInt + 1) % this.tabPane.getTabCount();
  }
  
  protected int getNextTabIndexInRun(int paramInt1, int paramInt2)
  {
    if (this.runCount < 2) {
      return getNextTabIndex(paramInt2);
    }
    int i = getRunForTab(paramInt1, paramInt2);
    int j = getNextTabIndex(paramInt2);
    if (j == this.tabRuns[getNextTabRun(i)]) {
      return this.tabRuns[i];
    }
    return j;
  }
  
  protected int getPreviousTabIndexInRun(int paramInt1, int paramInt2)
  {
    if (this.runCount < 2) {
      return getPreviousTabIndex(paramInt2);
    }
    int i = getRunForTab(paramInt1, paramInt2);
    if (paramInt2 == this.tabRuns[i])
    {
      int j = this.tabRuns[getNextTabRun(i)] - 1;
      return j != -1 ? j : paramInt1 - 1;
    }
    return getPreviousTabIndex(paramInt2);
  }
  
  protected int getPreviousTabRun(int paramInt)
  {
    int i = paramInt - 1 >= 0 ? paramInt - 1 : this.runCount - 1;
    return i >= 0 ? i : 0;
  }
  
  protected int getNextTabRun(int paramInt)
  {
    return (paramInt + 1) % this.runCount;
  }
  
  protected static void rotateInsets(Insets paramInsets1, Insets paramInsets2, int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
      paramInsets2.top = paramInsets1.left;
      paramInsets2.left = paramInsets1.top;
      paramInsets2.bottom = paramInsets1.right;
      paramInsets2.right = paramInsets1.bottom;
      break;
    case 3: 
      paramInsets2.top = paramInsets1.bottom;
      paramInsets2.left = paramInsets1.left;
      paramInsets2.bottom = paramInsets1.top;
      paramInsets2.right = paramInsets1.right;
      break;
    case 4: 
      paramInsets2.top = paramInsets1.left;
      paramInsets2.left = paramInsets1.bottom;
      paramInsets2.bottom = paramInsets1.right;
      paramInsets2.right = paramInsets1.top;
      break;
    case 1: 
    default: 
      paramInsets2.top = paramInsets1.top;
      paramInsets2.left = paramInsets1.left;
      paramInsets2.bottom = paramInsets1.bottom;
      paramInsets2.right = paramInsets1.right;
    }
  }
  
  boolean requestFocusForVisibleComponent()
  {
    return SwingUtilities2.tabbedPaneChangeFocusTo(getVisibleComponent());
  }
  
  private Vector<View> createHTMLVector()
  {
    Vector localVector = new Vector();
    int i = this.tabPane.getTabCount();
    if (i > 0) {
      for (int j = 0; j < i; j++)
      {
        String str = this.tabPane.getTitleAt(j);
        if (BasicHTML.isHTMLString(str)) {
          localVector.addElement(BasicHTML.createHTMLView(this.tabPane, str));
        } else {
          localVector.addElement(null);
        }
      }
    }
    return localVector;
  }
  
  private static class Actions
    extends UIAction
  {
    static final String NEXT = "navigateNext";
    static final String PREVIOUS = "navigatePrevious";
    static final String RIGHT = "navigateRight";
    static final String LEFT = "navigateLeft";
    static final String UP = "navigateUp";
    static final String DOWN = "navigateDown";
    static final String PAGE_UP = "navigatePageUp";
    static final String PAGE_DOWN = "navigatePageDown";
    static final String REQUEST_FOCUS = "requestFocus";
    static final String REQUEST_FOCUS_FOR_VISIBLE = "requestFocusForVisibleComponent";
    static final String SET_SELECTED = "setSelectedIndex";
    static final String SELECT_FOCUSED = "selectTabWithFocus";
    static final String SCROLL_FORWARD = "scrollTabsForwardAction";
    static final String SCROLL_BACKWARD = "scrollTabsBackwardAction";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      String str1 = getName();
      JTabbedPane localJTabbedPane = (JTabbedPane)paramActionEvent.getSource();
      BasicTabbedPaneUI localBasicTabbedPaneUI = (BasicTabbedPaneUI)BasicLookAndFeel.getUIOfType(localJTabbedPane.getUI(), BasicTabbedPaneUI.class);
      if (localBasicTabbedPaneUI == null) {
        return;
      }
      if (str1 == "navigateNext")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(12);
      }
      else if (str1 == "navigatePrevious")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(13);
      }
      else if (str1 == "navigateRight")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(3);
      }
      else if (str1 == "navigateLeft")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(7);
      }
      else if (str1 == "navigateUp")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(1);
      }
      else if (str1 == "navigateDown")
      {
        localBasicTabbedPaneUI.navigateSelectedTab(5);
      }
      else
      {
        int i;
        if (str1 == "navigatePageUp")
        {
          i = localJTabbedPane.getTabPlacement();
          if ((i == 1) || (i == 3)) {
            localBasicTabbedPaneUI.navigateSelectedTab(7);
          } else {
            localBasicTabbedPaneUI.navigateSelectedTab(1);
          }
        }
        else if (str1 == "navigatePageDown")
        {
          i = localJTabbedPane.getTabPlacement();
          if ((i == 1) || (i == 3)) {
            localBasicTabbedPaneUI.navigateSelectedTab(3);
          } else {
            localBasicTabbedPaneUI.navigateSelectedTab(5);
          }
        }
        else if (str1 == "requestFocus")
        {
          localJTabbedPane.requestFocus();
        }
        else if (str1 == "requestFocusForVisibleComponent")
        {
          localBasicTabbedPaneUI.requestFocusForVisibleComponent();
        }
        else if (str1 == "setSelectedIndex")
        {
          String str2 = paramActionEvent.getActionCommand();
          if ((str2 != null) && (str2.length() > 0))
          {
            int k = paramActionEvent.getActionCommand().charAt(0);
            if ((k >= 97) && (k <= 122)) {
              k -= 32;
            }
            Integer localInteger = (Integer)localBasicTabbedPaneUI.mnemonicToIndexMap.get(Integer.valueOf(k));
            if ((localInteger != null) && (localJTabbedPane.isEnabledAt(localInteger.intValue()))) {
              localJTabbedPane.setSelectedIndex(localInteger.intValue());
            }
          }
        }
        else if (str1 == "selectTabWithFocus")
        {
          int j = localBasicTabbedPaneUI.getFocusIndex();
          if (j != -1) {
            localJTabbedPane.setSelectedIndex(j);
          }
        }
        else if (str1 == "scrollTabsForwardAction")
        {
          if (localBasicTabbedPaneUI.scrollableTabLayoutEnabled()) {
            localBasicTabbedPaneUI.tabScroller.scrollForward(localJTabbedPane.getTabPlacement());
          }
        }
        else if ((str1 == "scrollTabsBackwardAction") && (localBasicTabbedPaneUI.scrollableTabLayoutEnabled()))
        {
          localBasicTabbedPaneUI.tabScroller.scrollBackward(localJTabbedPane.getTabPlacement());
        }
      }
    }
  }
  
  private class CroppedEdge
    extends JPanel
    implements UIResource
  {
    private Shape shape;
    private int tabIndex;
    private int cropline;
    private int cropx;
    private int cropy;
    
    public CroppedEdge()
    {
      setOpaque(false);
    }
    
    public void setParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.tabIndex = paramInt1;
      this.cropline = paramInt2;
      this.cropx = paramInt3;
      this.cropy = paramInt4;
      Rectangle localRectangle = BasicTabbedPaneUI.this.rects[paramInt1];
      setBounds(localRectangle);
      this.shape = BasicTabbedPaneUI.createCroppedTabShape(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), localRectangle, paramInt2);
      if ((getParent() == null) && (BasicTabbedPaneUI.this.tabContainer != null)) {
        BasicTabbedPaneUI.this.tabContainer.add(this, 0);
      }
    }
    
    public void resetParams()
    {
      this.shape = null;
      if ((getParent() == BasicTabbedPaneUI.this.tabContainer) && (BasicTabbedPaneUI.this.tabContainer != null)) {
        BasicTabbedPaneUI.this.tabContainer.remove(this);
      }
    }
    
    public boolean isParamsSet()
    {
      return this.shape != null;
    }
    
    public int getTabIndex()
    {
      return this.tabIndex;
    }
    
    public int getCropline()
    {
      return this.cropline;
    }
    
    public int getCroppedSideWidth()
    {
      return 3;
    }
    
    private Color getBgColor()
    {
      Container localContainer = BasicTabbedPaneUI.this.tabPane.getParent();
      if (localContainer != null)
      {
        Color localColor = localContainer.getBackground();
        if (localColor != null) {
          return localColor;
        }
      }
      return UIManager.getColor("control");
    }
    
    protected void paintComponent(Graphics paramGraphics)
    {
      super.paintComponent(paramGraphics);
      if ((isParamsSet()) && ((paramGraphics instanceof Graphics2D)))
      {
        Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
        localGraphics2D.clipRect(0, 0, getWidth(), getHeight());
        localGraphics2D.setColor(getBgColor());
        localGraphics2D.translate(this.cropx, this.cropy);
        localGraphics2D.fill(this.shape);
        BasicTabbedPaneUI.this.paintCroppedTabEdge(paramGraphics);
        localGraphics2D.translate(-this.cropx, -this.cropy);
      }
    }
  }
  
  public class FocusHandler
    extends FocusAdapter
  {
    public FocusHandler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      BasicTabbedPaneUI.this.getHandler().focusGained(paramFocusEvent);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicTabbedPaneUI.this.getHandler().focusLost(paramFocusEvent);
    }
  }
  
  private class Handler
    implements ChangeListener, ContainerListener, FocusListener, MouseListener, MouseMotionListener, PropertyChangeListener
  {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      JTabbedPane localJTabbedPane = (JTabbedPane)paramPropertyChangeEvent.getSource();
      String str = paramPropertyChangeEvent.getPropertyName();
      boolean bool1 = BasicTabbedPaneUI.this.scrollableTabLayoutEnabled();
      if (str == "mnemonicAt")
      {
        BasicTabbedPaneUI.this.updateMnemonics();
        localJTabbedPane.repaint();
      }
      else if (str == "displayedMnemonicIndexAt")
      {
        localJTabbedPane.repaint();
      }
      else if (str == "indexForTitle")
      {
        BasicTabbedPaneUI.this.calculatedBaseline = false;
        Integer localInteger = (Integer)paramPropertyChangeEvent.getNewValue();
        if (BasicTabbedPaneUI.this.htmlViews != null) {
          BasicTabbedPaneUI.this.htmlViews.removeElementAt(localInteger.intValue());
        }
        updateHtmlViews(localInteger.intValue());
      }
      else if (str == "tabLayoutPolicy")
      {
        BasicTabbedPaneUI.this.uninstallUI(localJTabbedPane);
        BasicTabbedPaneUI.this.installUI(localJTabbedPane);
        BasicTabbedPaneUI.this.calculatedBaseline = false;
      }
      else if (str == "tabPlacement")
      {
        if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
          BasicTabbedPaneUI.this.tabScroller.createButtons();
        }
        BasicTabbedPaneUI.this.calculatedBaseline = false;
      }
      else if ((str == "opaque") && (bool1))
      {
        boolean bool2 = ((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue();
        BasicTabbedPaneUI.this.tabScroller.tabPanel.setOpaque(bool2);
        BasicTabbedPaneUI.this.tabScroller.viewport.setOpaque(bool2);
      }
      else
      {
        Object localObject;
        if ((str == "background") && (bool1))
        {
          localObject = (Color)paramPropertyChangeEvent.getNewValue();
          BasicTabbedPaneUI.this.tabScroller.tabPanel.setBackground((Color)localObject);
          BasicTabbedPaneUI.this.tabScroller.viewport.setBackground((Color)localObject);
          Color localColor = BasicTabbedPaneUI.this.selectedColor == null ? localObject : BasicTabbedPaneUI.this.selectedColor;
          BasicTabbedPaneUI.this.tabScroller.scrollForwardButton.setBackground(localColor);
          BasicTabbedPaneUI.this.tabScroller.scrollBackwardButton.setBackground(localColor);
        }
        else if (str == "indexForTabComponent")
        {
          if (BasicTabbedPaneUI.this.tabContainer != null) {
            BasicTabbedPaneUI.TabContainer.access$1700(BasicTabbedPaneUI.this.tabContainer);
          }
          localObject = BasicTabbedPaneUI.this.tabPane.getTabComponentAt(((Integer)paramPropertyChangeEvent.getNewValue()).intValue());
          if (localObject != null) {
            if (BasicTabbedPaneUI.this.tabContainer == null) {
              BasicTabbedPaneUI.this.installTabContainer();
            } else {
              BasicTabbedPaneUI.this.tabContainer.add((Component)localObject);
            }
          }
          BasicTabbedPaneUI.this.tabPane.revalidate();
          BasicTabbedPaneUI.this.tabPane.repaint();
          BasicTabbedPaneUI.this.calculatedBaseline = false;
        }
        else if (str == "indexForNullComponent")
        {
          BasicTabbedPaneUI.this.isRunsDirty = true;
          updateHtmlViews(((Integer)paramPropertyChangeEvent.getNewValue()).intValue());
        }
        else if (str == "font")
        {
          BasicTabbedPaneUI.this.calculatedBaseline = false;
        }
      }
    }
    
    private void updateHtmlViews(int paramInt)
    {
      String str = BasicTabbedPaneUI.this.tabPane.getTitleAt(paramInt);
      boolean bool = BasicHTML.isHTMLString(str);
      if (bool)
      {
        if (BasicTabbedPaneUI.this.htmlViews == null)
        {
          BasicTabbedPaneUI.this.htmlViews = BasicTabbedPaneUI.this.createHTMLVector();
        }
        else
        {
          View localView = BasicHTML.createHTMLView(BasicTabbedPaneUI.this.tabPane, str);
          BasicTabbedPaneUI.this.htmlViews.insertElementAt(localView, paramInt);
        }
      }
      else if (BasicTabbedPaneUI.this.htmlViews != null) {
        BasicTabbedPaneUI.this.htmlViews.insertElementAt(null, paramInt);
      }
      BasicTabbedPaneUI.this.updateMnemonics();
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      JTabbedPane localJTabbedPane = (JTabbedPane)paramChangeEvent.getSource();
      localJTabbedPane.revalidate();
      localJTabbedPane.repaint();
      BasicTabbedPaneUI.this.setFocusIndex(localJTabbedPane.getSelectedIndex(), false);
      if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled())
      {
        BasicTabbedPaneUI.this.ensureCurrentLayout();
        int i = localJTabbedPane.getSelectedIndex();
        if ((i < BasicTabbedPaneUI.this.rects.length) && (i != -1)) {
          BasicTabbedPaneUI.this.tabScroller.tabPanel.scrollRectToVisible((Rectangle)BasicTabbedPaneUI.this.rects[i].clone());
        }
      }
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mouseReleased(MouseEvent paramMouseEvent) {}
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      BasicTabbedPaneUI.this.setRolloverTab(paramMouseEvent.getX(), paramMouseEvent.getY());
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      BasicTabbedPaneUI.this.setRolloverTab(-1);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (!BasicTabbedPaneUI.this.tabPane.isEnabled()) {
        return;
      }
      int i = BasicTabbedPaneUI.this.tabForCoordinate(BasicTabbedPaneUI.this.tabPane, paramMouseEvent.getX(), paramMouseEvent.getY());
      if ((i >= 0) && (BasicTabbedPaneUI.this.tabPane.isEnabledAt(i))) {
        if (i != BasicTabbedPaneUI.this.tabPane.getSelectedIndex()) {
          BasicTabbedPaneUI.this.tabPane.setSelectedIndex(i);
        } else if (BasicTabbedPaneUI.this.tabPane.isRequestFocusEnabled()) {
          BasicTabbedPaneUI.this.tabPane.requestFocus();
        }
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent) {}
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      BasicTabbedPaneUI.this.setRolloverTab(paramMouseEvent.getX(), paramMouseEvent.getY());
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      BasicTabbedPaneUI.this.setFocusIndex(BasicTabbedPaneUI.this.tabPane.getSelectedIndex(), true);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicTabbedPaneUI.this.repaintTab(BasicTabbedPaneUI.this.focusIndex);
    }
    
    public void componentAdded(ContainerEvent paramContainerEvent)
    {
      JTabbedPane localJTabbedPane = (JTabbedPane)paramContainerEvent.getContainer();
      Component localComponent = paramContainerEvent.getChild();
      if ((localComponent instanceof UIResource)) {
        return;
      }
      BasicTabbedPaneUI.this.isRunsDirty = true;
      updateHtmlViews(localJTabbedPane.indexOfComponent(localComponent));
    }
    
    public void componentRemoved(ContainerEvent paramContainerEvent)
    {
      JTabbedPane localJTabbedPane = (JTabbedPane)paramContainerEvent.getContainer();
      Component localComponent = paramContainerEvent.getChild();
      if ((localComponent instanceof UIResource)) {
        return;
      }
      Integer localInteger = (Integer)localJTabbedPane.getClientProperty("__index_to_remove__");
      if (localInteger != null)
      {
        int i = localInteger.intValue();
        if ((BasicTabbedPaneUI.this.htmlViews != null) && (BasicTabbedPaneUI.this.htmlViews.size() > i)) {
          BasicTabbedPaneUI.this.htmlViews.removeElementAt(i);
        }
        localJTabbedPane.putClientProperty("__index_to_remove__", null);
      }
      BasicTabbedPaneUI.this.isRunsDirty = true;
      BasicTabbedPaneUI.this.updateMnemonics();
      BasicTabbedPaneUI.this.validateFocusIndex();
    }
  }
  
  public class MouseHandler
    extends MouseAdapter
  {
    public MouseHandler() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      BasicTabbedPaneUI.this.getHandler().mousePressed(paramMouseEvent);
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicTabbedPaneUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
  
  private class ScrollableTabButton
    extends BasicArrowButton
    implements UIResource, SwingConstants
  {
    public ScrollableTabButton(int paramInt)
    {
      super(UIManager.getColor("TabbedPane.selected"), UIManager.getColor("TabbedPane.shadow"), UIManager.getColor("TabbedPane.darkShadow"), UIManager.getColor("TabbedPane.highlight"));
    }
  }
  
  private class ScrollableTabPanel
    extends JPanel
    implements UIResource
  {
    public ScrollableTabPanel()
    {
      super();
      setOpaque(BasicTabbedPaneUI.this.tabPane.isOpaque());
      Color localColor = UIManager.getColor("TabbedPane.tabAreaBackground");
      if (localColor == null) {
        localColor = BasicTabbedPaneUI.this.tabPane.getBackground();
      }
      setBackground(localColor);
    }
    
    public void paintComponent(Graphics paramGraphics)
    {
      super.paintComponent(paramGraphics);
      BasicTabbedPaneUI.this.paintTabArea(paramGraphics, BasicTabbedPaneUI.this.tabPane.getTabPlacement(), BasicTabbedPaneUI.this.tabPane.getSelectedIndex());
      if ((BasicTabbedPaneUI.this.tabScroller.croppedEdge.isParamsSet()) && (BasicTabbedPaneUI.this.tabContainer == null))
      {
        Rectangle localRectangle = BasicTabbedPaneUI.this.rects[BasicTabbedPaneUI.this.tabScroller.croppedEdge.getTabIndex()];
        paramGraphics.translate(localRectangle.x, localRectangle.y);
        BasicTabbedPaneUI.this.tabScroller.croppedEdge.paintComponent(paramGraphics);
        paramGraphics.translate(-localRectangle.x, -localRectangle.y);
      }
    }
    
    public void doLayout()
    {
      if (getComponentCount() > 0)
      {
        Component localComponent = getComponent(0);
        localComponent.setBounds(0, 0, getWidth(), getHeight());
      }
    }
  }
  
  private class ScrollableTabSupport
    implements ActionListener, ChangeListener
  {
    public BasicTabbedPaneUI.ScrollableTabViewport viewport = new BasicTabbedPaneUI.ScrollableTabViewport(BasicTabbedPaneUI.this);
    public BasicTabbedPaneUI.ScrollableTabPanel tabPanel = new BasicTabbedPaneUI.ScrollableTabPanel(BasicTabbedPaneUI.this);
    public JButton scrollForwardButton;
    public JButton scrollBackwardButton;
    public BasicTabbedPaneUI.CroppedEdge croppedEdge;
    public int leadingTabIndex;
    private Point tabViewPosition = new Point(0, 0);
    
    ScrollableTabSupport(int paramInt)
    {
      this.viewport.setView(this.tabPanel);
      this.viewport.addChangeListener(this);
      this.croppedEdge = new BasicTabbedPaneUI.CroppedEdge(BasicTabbedPaneUI.this);
      createButtons();
    }
    
    void createButtons()
    {
      if (this.scrollForwardButton != null)
      {
        BasicTabbedPaneUI.this.tabPane.remove(this.scrollForwardButton);
        this.scrollForwardButton.removeActionListener(this);
        BasicTabbedPaneUI.this.tabPane.remove(this.scrollBackwardButton);
        this.scrollBackwardButton.removeActionListener(this);
      }
      int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
      if ((i == 1) || (i == 3))
      {
        this.scrollForwardButton = BasicTabbedPaneUI.this.createScrollButton(3);
        this.scrollBackwardButton = BasicTabbedPaneUI.this.createScrollButton(7);
      }
      else
      {
        this.scrollForwardButton = BasicTabbedPaneUI.this.createScrollButton(5);
        this.scrollBackwardButton = BasicTabbedPaneUI.this.createScrollButton(1);
      }
      this.scrollForwardButton.addActionListener(this);
      this.scrollBackwardButton.addActionListener(this);
      BasicTabbedPaneUI.this.tabPane.add(this.scrollForwardButton);
      BasicTabbedPaneUI.this.tabPane.add(this.scrollBackwardButton);
    }
    
    public void scrollForward(int paramInt)
    {
      Dimension localDimension = this.viewport.getViewSize();
      Rectangle localRectangle = this.viewport.getViewRect();
      if ((paramInt == 1) || (paramInt == 3))
      {
        if (localRectangle.width < localDimension.width - localRectangle.x) {}
      }
      else if (localRectangle.height >= localDimension.height - localRectangle.y) {
        return;
      }
      setLeadingTabIndex(paramInt, this.leadingTabIndex + 1);
    }
    
    public void scrollBackward(int paramInt)
    {
      if (this.leadingTabIndex == 0) {
        return;
      }
      setLeadingTabIndex(paramInt, this.leadingTabIndex - 1);
    }
    
    public void setLeadingTabIndex(int paramInt1, int paramInt2)
    {
      this.leadingTabIndex = paramInt2;
      Dimension localDimension1 = this.viewport.getViewSize();
      Rectangle localRectangle = this.viewport.getViewRect();
      Dimension localDimension2;
      switch (paramInt1)
      {
      case 1: 
      case 3: 
        this.tabViewPosition.x = (this.leadingTabIndex == 0 ? 0 : BasicTabbedPaneUI.this.rects[this.leadingTabIndex].x);
        if (localDimension1.width - this.tabViewPosition.x < localRectangle.width)
        {
          localDimension2 = new Dimension(localDimension1.width - this.tabViewPosition.x, localRectangle.height);
          this.viewport.setExtentSize(localDimension2);
        }
        break;
      case 2: 
      case 4: 
        this.tabViewPosition.y = (this.leadingTabIndex == 0 ? 0 : BasicTabbedPaneUI.this.rects[this.leadingTabIndex].y);
        if (localDimension1.height - this.tabViewPosition.y < localRectangle.height)
        {
          localDimension2 = new Dimension(localRectangle.width, localDimension1.height - this.tabViewPosition.y);
          this.viewport.setExtentSize(localDimension2);
        }
        break;
      }
      this.viewport.setViewPosition(this.tabViewPosition);
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      updateView();
    }
    
    private void updateView()
    {
      int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
      int j = BasicTabbedPaneUI.this.tabPane.getTabCount();
      BasicTabbedPaneUI.this.assureRectsCreated(j);
      Rectangle localRectangle1 = this.viewport.getBounds();
      Dimension localDimension = this.viewport.getViewSize();
      Rectangle localRectangle2 = this.viewport.getViewRect();
      this.leadingTabIndex = BasicTabbedPaneUI.this.getClosestTab(localRectangle2.x, localRectangle2.y);
      if (this.leadingTabIndex + 1 < j) {
        switch (i)
        {
        case 1: 
        case 3: 
          if (BasicTabbedPaneUI.this.rects[this.leadingTabIndex].x < localRectangle2.x) {
            this.leadingTabIndex += 1;
          }
          break;
        case 2: 
        case 4: 
          if (BasicTabbedPaneUI.this.rects[this.leadingTabIndex].y < localRectangle2.y) {
            this.leadingTabIndex += 1;
          }
          break;
        }
      }
      Insets localInsets = BasicTabbedPaneUI.this.getContentBorderInsets(i);
      switch (i)
      {
      case 2: 
        BasicTabbedPaneUI.this.tabPane.repaint(localRectangle1.x + localRectangle1.width, localRectangle1.y, localInsets.left, localRectangle1.height);
        this.scrollBackwardButton.setEnabled((localRectangle2.y > 0) && (this.leadingTabIndex > 0));
        this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1) && (localDimension.height - localRectangle2.y > localRectangle2.height));
        break;
      case 4: 
        BasicTabbedPaneUI.this.tabPane.repaint(localRectangle1.x - localInsets.right, localRectangle1.y, localInsets.right, localRectangle1.height);
        this.scrollBackwardButton.setEnabled((localRectangle2.y > 0) && (this.leadingTabIndex > 0));
        this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1) && (localDimension.height - localRectangle2.y > localRectangle2.height));
        break;
      case 3: 
        BasicTabbedPaneUI.this.tabPane.repaint(localRectangle1.x, localRectangle1.y - localInsets.bottom, localRectangle1.width, localInsets.bottom);
        this.scrollBackwardButton.setEnabled((localRectangle2.x > 0) && (this.leadingTabIndex > 0));
        this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1) && (localDimension.width - localRectangle2.x > localRectangle2.width));
        break;
      case 1: 
      default: 
        BasicTabbedPaneUI.this.tabPane.repaint(localRectangle1.x, localRectangle1.y + localRectangle1.height, localRectangle1.width, localInsets.top);
        this.scrollBackwardButton.setEnabled((localRectangle2.x > 0) && (this.leadingTabIndex > 0));
        this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1) && (localDimension.width - localRectangle2.x > localRectangle2.width));
      }
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      ActionMap localActionMap = BasicTabbedPaneUI.this.tabPane.getActionMap();
      if (localActionMap != null)
      {
        String str;
        if (paramActionEvent.getSource() == this.scrollForwardButton) {
          str = "scrollTabsForwardAction";
        } else {
          str = "scrollTabsBackwardAction";
        }
        Action localAction = localActionMap.get(str);
        if ((localAction != null) && (localAction.isEnabled())) {
          localAction.actionPerformed(new ActionEvent(BasicTabbedPaneUI.this.tabPane, 1001, null, paramActionEvent.getWhen(), paramActionEvent.getModifiers()));
        }
      }
    }
    
    public String toString()
    {
      return "viewport.viewSize=" + this.viewport.getViewSize() + "\n" + "viewport.viewRectangle=" + this.viewport.getViewRect() + "\n" + "leadingTabIndex=" + this.leadingTabIndex + "\n" + "tabViewPosition=" + this.tabViewPosition;
    }
  }
  
  private class ScrollableTabViewport
    extends JViewport
    implements UIResource
  {
    public ScrollableTabViewport()
    {
      setName("TabbedPane.scrollableViewport");
      setScrollMode(0);
      setOpaque(BasicTabbedPaneUI.this.tabPane.isOpaque());
      Color localColor = UIManager.getColor("TabbedPane.tabAreaBackground");
      if (localColor == null) {
        localColor = BasicTabbedPaneUI.this.tabPane.getBackground();
      }
      setBackground(localColor);
    }
  }
  
  private class TabContainer
    extends JPanel
    implements UIResource
  {
    private boolean notifyTabbedPane = true;
    
    public TabContainer()
    {
      super();
      setOpaque(false);
    }
    
    public void remove(Component paramComponent)
    {
      int i = BasicTabbedPaneUI.this.tabPane.indexOfTabComponent(paramComponent);
      super.remove(paramComponent);
      if ((this.notifyTabbedPane) && (i != -1)) {
        BasicTabbedPaneUI.this.tabPane.setTabComponentAt(i, null);
      }
    }
    
    private void removeUnusedTabComponents()
    {
      for (Component localComponent : getComponents()) {
        if (!(localComponent instanceof UIResource))
        {
          int k = BasicTabbedPaneUI.this.tabPane.indexOfTabComponent(localComponent);
          if (k == -1) {
            super.remove(localComponent);
          }
        }
      }
    }
    
    public boolean isOptimizedDrawingEnabled()
    {
      return (BasicTabbedPaneUI.this.tabScroller != null) && (!BasicTabbedPaneUI.this.tabScroller.croppedEdge.isParamsSet());
    }
    
    public void doLayout()
    {
      if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled())
      {
        BasicTabbedPaneUI.this.tabScroller.tabPanel.repaint();
        BasicTabbedPaneUI.this.tabScroller.updateView();
      }
      else
      {
        BasicTabbedPaneUI.this.tabPane.repaint(getBounds());
      }
    }
  }
  
  public class TabSelectionHandler
    implements ChangeListener
  {
    public TabSelectionHandler() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      BasicTabbedPaneUI.this.getHandler().stateChanged(paramChangeEvent);
    }
  }
  
  public class TabbedPaneLayout
    implements LayoutManager
  {
    public TabbedPaneLayout() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return calculateSize(false);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return calculateSize(true);
    }
    
    protected Dimension calculateSize(boolean paramBoolean)
    {
      int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
      Insets localInsets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
      Insets localInsets2 = BasicTabbedPaneUI.this.getContentBorderInsets(i);
      Insets localInsets3 = BasicTabbedPaneUI.this.getTabAreaInsets(i);
      Dimension localDimension1 = new Dimension(0, 0);
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      for (int i1 = 0; i1 < BasicTabbedPaneUI.this.tabPane.getTabCount(); i1++)
      {
        Component localComponent = BasicTabbedPaneUI.this.tabPane.getComponentAt(i1);
        if (localComponent != null)
        {
          Dimension localDimension2 = paramBoolean ? localComponent.getMinimumSize() : localComponent.getPreferredSize();
          if (localDimension2 != null)
          {
            n = Math.max(localDimension2.height, n);
            m = Math.max(localDimension2.width, m);
          }
        }
      }
      k += m;
      j += n;
      switch (i)
      {
      case 2: 
      case 4: 
        j = Math.max(j, BasicTabbedPaneUI.this.calculateMaxTabHeight(i));
        i1 = preferredTabAreaWidth(i, j - localInsets3.top - localInsets3.bottom);
        k += i1;
        break;
      case 1: 
      case 3: 
      default: 
        k = Math.max(k, BasicTabbedPaneUI.this.calculateMaxTabWidth(i));
        i1 = preferredTabAreaHeight(i, k - localInsets3.left - localInsets3.right);
        j += i1;
      }
      return new Dimension(k + localInsets1.left + localInsets1.right + localInsets2.left + localInsets2.right, j + localInsets1.bottom + localInsets1.top + localInsets2.top + localInsets2.bottom);
    }
    
    protected int preferredTabAreaHeight(int paramInt1, int paramInt2)
    {
      FontMetrics localFontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
      int i = BasicTabbedPaneUI.this.tabPane.getTabCount();
      int j = 0;
      if (i > 0)
      {
        int k = 1;
        int m = 0;
        int n = BasicTabbedPaneUI.this.calculateMaxTabHeight(paramInt1);
        for (int i1 = 0; i1 < i; i1++)
        {
          int i2 = BasicTabbedPaneUI.this.calculateTabWidth(paramInt1, i1, localFontMetrics);
          if ((m != 0) && (m + i2 > paramInt2))
          {
            k++;
            m = 0;
          }
          m += i2;
        }
        j = BasicTabbedPaneUI.this.calculateTabAreaHeight(paramInt1, k, n);
      }
      return j;
    }
    
    protected int preferredTabAreaWidth(int paramInt1, int paramInt2)
    {
      FontMetrics localFontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
      int i = BasicTabbedPaneUI.this.tabPane.getTabCount();
      int j = 0;
      if (i > 0)
      {
        int k = 1;
        int m = 0;
        int n = localFontMetrics.getHeight();
        BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(paramInt1);
        for (int i1 = 0; i1 < i; i1++)
        {
          int i2 = BasicTabbedPaneUI.this.calculateTabHeight(paramInt1, i1, n);
          if ((m != 0) && (m + i2 > paramInt2))
          {
            k++;
            m = 0;
          }
          m += i2;
        }
        j = BasicTabbedPaneUI.this.calculateTabAreaWidth(paramInt1, k, BasicTabbedPaneUI.this.maxTabWidth);
      }
      return j;
    }
    
    public void layoutContainer(Container paramContainer)
    {
      BasicTabbedPaneUI.this.setRolloverTab(-1);
      int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
      Insets localInsets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
      int j = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
      Component localComponent1 = BasicTabbedPaneUI.this.getVisibleComponent();
      calculateLayoutInfo();
      Component localComponent2 = null;
      if (j < 0)
      {
        if (localComponent1 != null) {
          BasicTabbedPaneUI.this.setVisibleComponent(null);
        }
      }
      else {
        localComponent2 = BasicTabbedPaneUI.this.tabPane.getComponentAt(j);
      }
      int i2 = 0;
      int i3 = 0;
      Insets localInsets2 = BasicTabbedPaneUI.this.getContentBorderInsets(i);
      int i4 = 0;
      if (localComponent2 != null)
      {
        if ((localComponent2 != localComponent1) && (localComponent1 != null) && (SwingUtilities.findFocusOwner(localComponent1) != null)) {
          i4 = 1;
        }
        BasicTabbedPaneUI.this.setVisibleComponent(localComponent2);
      }
      Rectangle localRectangle = BasicTabbedPaneUI.this.tabPane.getBounds();
      int i5 = BasicTabbedPaneUI.this.tabPane.getComponentCount();
      if (i5 > 0)
      {
        int k;
        int m;
        switch (i)
        {
        case 2: 
          i2 = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
          k = localInsets1.left + i2 + localInsets2.left;
          m = localInsets1.top + localInsets2.top;
          break;
        case 4: 
          i2 = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
          k = localInsets1.left + localInsets2.left;
          m = localInsets1.top + localInsets2.top;
          break;
        case 3: 
          i3 = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
          k = localInsets1.left + localInsets2.left;
          m = localInsets1.top + localInsets2.top;
          break;
        case 1: 
        default: 
          i3 = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
          k = localInsets1.left + localInsets2.left;
          m = localInsets1.top + i3 + localInsets2.top;
        }
        int n = localRectangle.width - i2 - localInsets1.left - localInsets1.right - localInsets2.left - localInsets2.right;
        int i1 = localRectangle.height - i3 - localInsets1.top - localInsets1.bottom - localInsets2.top - localInsets2.bottom;
        for (int i6 = 0; i6 < i5; i6++)
        {
          Component localComponent3 = BasicTabbedPaneUI.this.tabPane.getComponent(i6);
          if (localComponent3 == BasicTabbedPaneUI.this.tabContainer)
          {
            int i7 = i2 == 0 ? localRectangle.width : i2 + localInsets1.left + localInsets1.right + localInsets2.left + localInsets2.right;
            int i8 = i3 == 0 ? localRectangle.height : i3 + localInsets1.top + localInsets1.bottom + localInsets2.top + localInsets2.bottom;
            int i9 = 0;
            int i10 = 0;
            if (i == 3) {
              i10 = localRectangle.height - i8;
            } else if (i == 4) {
              i9 = localRectangle.width - i7;
            }
            localComponent3.setBounds(i9, i10, i7, i8);
          }
          else
          {
            localComponent3.setBounds(k, m, n, i1);
          }
        }
      }
      layoutTabComponents();
      if ((i4 != 0) && (!BasicTabbedPaneUI.this.requestFocusForVisibleComponent())) {
        BasicTabbedPaneUI.this.tabPane.requestFocus();
      }
    }
    
    public void calculateLayoutInfo()
    {
      int i = BasicTabbedPaneUI.this.tabPane.getTabCount();
      BasicTabbedPaneUI.this.assureRectsCreated(i);
      calculateTabRects(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), i);
      BasicTabbedPaneUI.this.isRunsDirty = false;
    }
    
    private void layoutTabComponents()
    {
      if (BasicTabbedPaneUI.this.tabContainer == null) {
        return;
      }
      Rectangle localRectangle = new Rectangle();
      Point localPoint = new Point(-BasicTabbedPaneUI.this.tabContainer.getX(), -BasicTabbedPaneUI.this.tabContainer.getY());
      if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
        BasicTabbedPaneUI.this.translatePointToTabPanel(0, 0, localPoint);
      }
      for (int i = 0; i < BasicTabbedPaneUI.this.tabPane.getTabCount(); i++)
      {
        Component localComponent = BasicTabbedPaneUI.this.tabPane.getTabComponentAt(i);
        if (localComponent != null)
        {
          BasicTabbedPaneUI.this.getTabBounds(i, localRectangle);
          Dimension localDimension = localComponent.getPreferredSize();
          Insets localInsets = BasicTabbedPaneUI.this.getTabInsets(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), i);
          int j = localRectangle.x + localInsets.left + localPoint.x;
          int k = localRectangle.y + localInsets.top + localPoint.y;
          int m = localRectangle.width - localInsets.left - localInsets.right;
          int n = localRectangle.height - localInsets.top - localInsets.bottom;
          int i1 = j + (m - localDimension.width) / 2;
          int i2 = k + (n - localDimension.height) / 2;
          int i3 = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
          boolean bool = i == BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
          localComponent.setBounds(i1 + BasicTabbedPaneUI.this.getTabLabelShiftX(i3, i, bool), i2 + BasicTabbedPaneUI.this.getTabLabelShiftY(i3, i, bool), localDimension.width, localDimension.height);
        }
      }
    }
    
    protected void calculateTabRects(int paramInt1, int paramInt2)
    {
      FontMetrics localFontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
      Dimension localDimension = BasicTabbedPaneUI.this.tabPane.getSize();
      Insets localInsets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
      Insets localInsets2 = BasicTabbedPaneUI.this.getTabAreaInsets(paramInt1);
      int i = localFontMetrics.getHeight();
      int j = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
      int i4 = (paramInt1 == 2) || (paramInt1 == 4) ? 1 : 0;
      boolean bool = BasicGraphicsUtils.isLeftToRight(BasicTabbedPaneUI.this.tabPane);
      int i1;
      int i2;
      int i3;
      switch (paramInt1)
      {
      case 2: 
        BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(paramInt1);
        i1 = localInsets1.left + localInsets2.left;
        i2 = localInsets1.top + localInsets2.top;
        i3 = localDimension.height - (localInsets1.bottom + localInsets2.bottom);
        break;
      case 4: 
        BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(paramInt1);
        i1 = localDimension.width - localInsets1.right - localInsets2.right - BasicTabbedPaneUI.this.maxTabWidth;
        i2 = localInsets1.top + localInsets2.top;
        i3 = localDimension.height - (localInsets1.bottom + localInsets2.bottom);
        break;
      case 3: 
        BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(paramInt1);
        i1 = localInsets1.left + localInsets2.left;
        i2 = localDimension.height - localInsets1.bottom - localInsets2.bottom - BasicTabbedPaneUI.this.maxTabHeight;
        i3 = localDimension.width - (localInsets1.right + localInsets2.right);
        break;
      case 1: 
      default: 
        BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(paramInt1);
        i1 = localInsets1.left + localInsets2.left;
        i2 = localInsets1.top + localInsets2.top;
        i3 = localDimension.width - (localInsets1.right + localInsets2.right);
      }
      int k = BasicTabbedPaneUI.this.getTabRunOverlay(paramInt1);
      BasicTabbedPaneUI.this.runCount = 0;
      BasicTabbedPaneUI.this.selectedRun = -1;
      if (paramInt2 == 0) {
        return;
      }
      Rectangle localRectangle;
      for (int m = 0; m < paramInt2; m++)
      {
        localRectangle = BasicTabbedPaneUI.this.rects[m];
        if (i4 == 0)
        {
          if (m > 0)
          {
            localRectangle.x = (BasicTabbedPaneUI.this.rects[(m - 1)].x + BasicTabbedPaneUI.this.rects[(m - 1)].width);
          }
          else
          {
            BasicTabbedPaneUI.this.tabRuns[0] = 0;
            BasicTabbedPaneUI.this.runCount = 1;
            BasicTabbedPaneUI.this.maxTabWidth = 0;
            localRectangle.x = i1;
          }
          localRectangle.width = BasicTabbedPaneUI.this.calculateTabWidth(paramInt1, m, localFontMetrics);
          BasicTabbedPaneUI.this.maxTabWidth = Math.max(BasicTabbedPaneUI.this.maxTabWidth, localRectangle.width);
          if ((localRectangle.x != i1) && (localRectangle.x + localRectangle.width > i3))
          {
            if (BasicTabbedPaneUI.this.runCount > BasicTabbedPaneUI.this.tabRuns.length - 1) {
              BasicTabbedPaneUI.this.expandTabRunsArray();
            }
            BasicTabbedPaneUI.this.tabRuns[BasicTabbedPaneUI.this.runCount] = m;
            BasicTabbedPaneUI.this.runCount += 1;
            localRectangle.x = i1;
          }
          localRectangle.y = i2;
          localRectangle.height = BasicTabbedPaneUI.this.maxTabHeight;
        }
        else
        {
          if (m > 0)
          {
            localRectangle.y = (BasicTabbedPaneUI.this.rects[(m - 1)].y + BasicTabbedPaneUI.this.rects[(m - 1)].height);
          }
          else
          {
            BasicTabbedPaneUI.this.tabRuns[0] = 0;
            BasicTabbedPaneUI.this.runCount = 1;
            BasicTabbedPaneUI.this.maxTabHeight = 0;
            localRectangle.y = i2;
          }
          localRectangle.height = BasicTabbedPaneUI.this.calculateTabHeight(paramInt1, m, i);
          BasicTabbedPaneUI.this.maxTabHeight = Math.max(BasicTabbedPaneUI.this.maxTabHeight, localRectangle.height);
          if ((localRectangle.y != i2) && (localRectangle.y + localRectangle.height > i3))
          {
            if (BasicTabbedPaneUI.this.runCount > BasicTabbedPaneUI.this.tabRuns.length - 1) {
              BasicTabbedPaneUI.this.expandTabRunsArray();
            }
            BasicTabbedPaneUI.this.tabRuns[BasicTabbedPaneUI.this.runCount] = m;
            BasicTabbedPaneUI.this.runCount += 1;
            localRectangle.y = i2;
          }
          localRectangle.x = i1;
          localRectangle.width = BasicTabbedPaneUI.this.maxTabWidth;
        }
        if (m == j) {
          BasicTabbedPaneUI.this.selectedRun = (BasicTabbedPaneUI.this.runCount - 1);
        }
      }
      if (BasicTabbedPaneUI.this.runCount > 1)
      {
        normalizeTabRuns(paramInt1, paramInt2, i4 != 0 ? i2 : i1, i3);
        BasicTabbedPaneUI.this.selectedRun = BasicTabbedPaneUI.this.getRunForTab(paramInt2, j);
        if (BasicTabbedPaneUI.this.shouldRotateTabRuns(paramInt1)) {
          rotateTabRuns(paramInt1, BasicTabbedPaneUI.this.selectedRun);
        }
      }
      int i5;
      for (m = BasicTabbedPaneUI.this.runCount - 1; m >= 0; m--)
      {
        i5 = BasicTabbedPaneUI.this.tabRuns[m];
        int i6 = BasicTabbedPaneUI.this.tabRuns[(m + 1)];
        int i7 = i6 != 0 ? i6 - 1 : paramInt2 - 1;
        int n;
        if (i4 == 0)
        {
          for (n = i5; n <= i7; n++)
          {
            localRectangle = BasicTabbedPaneUI.this.rects[n];
            localRectangle.y = i2;
            localRectangle.x += BasicTabbedPaneUI.this.getTabRunIndent(paramInt1, m);
          }
          if (BasicTabbedPaneUI.this.shouldPadTabRun(paramInt1, m)) {
            padTabRun(paramInt1, i5, i7, i3);
          }
          if (paramInt1 == 3) {
            i2 -= BasicTabbedPaneUI.this.maxTabHeight - k;
          } else {
            i2 += BasicTabbedPaneUI.this.maxTabHeight - k;
          }
        }
        else
        {
          for (n = i5; n <= i7; n++)
          {
            localRectangle = BasicTabbedPaneUI.this.rects[n];
            localRectangle.x = i1;
            localRectangle.y += BasicTabbedPaneUI.this.getTabRunIndent(paramInt1, m);
          }
          if (BasicTabbedPaneUI.this.shouldPadTabRun(paramInt1, m)) {
            padTabRun(paramInt1, i5, i7, i3);
          }
          if (paramInt1 == 4) {
            i1 -= BasicTabbedPaneUI.this.maxTabWidth - k;
          } else {
            i1 += BasicTabbedPaneUI.this.maxTabWidth - k;
          }
        }
      }
      padSelectedTab(paramInt1, j);
      if ((!bool) && (i4 == 0))
      {
        i5 = localDimension.width - (localInsets1.right + localInsets2.right);
        for (m = 0; m < paramInt2; m++) {
          BasicTabbedPaneUI.this.rects[m].x = (i5 - BasicTabbedPaneUI.this.rects[m].x - BasicTabbedPaneUI.this.rects[m].width);
        }
      }
    }
    
    protected void rotateTabRuns(int paramInt1, int paramInt2)
    {
      for (int i = 0; i < paramInt2; i++)
      {
        int j = BasicTabbedPaneUI.this.tabRuns[0];
        for (int k = 1; k < BasicTabbedPaneUI.this.runCount; k++) {
          BasicTabbedPaneUI.this.tabRuns[(k - 1)] = BasicTabbedPaneUI.this.tabRuns[k];
        }
        BasicTabbedPaneUI.this.tabRuns[(BasicTabbedPaneUI.this.runCount - 1)] = j;
      }
    }
    
    protected void normalizeTabRuns(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = (paramInt1 == 2) || (paramInt1 == 4) ? 1 : 0;
      int j = BasicTabbedPaneUI.this.runCount - 1;
      int k = 1;
      double d = 1.25D;
      while (k != 0)
      {
        int m = BasicTabbedPaneUI.this.lastTabInRun(paramInt2, j);
        int n = BasicTabbedPaneUI.this.lastTabInRun(paramInt2, j - 1);
        int i1;
        int i2;
        if (i == 0)
        {
          i1 = BasicTabbedPaneUI.this.rects[m].x + BasicTabbedPaneUI.this.rects[m].width;
          i2 = (int)(BasicTabbedPaneUI.this.maxTabWidth * d);
        }
        else
        {
          i1 = BasicTabbedPaneUI.this.rects[m].y + BasicTabbedPaneUI.this.rects[m].height;
          i2 = (int)(BasicTabbedPaneUI.this.maxTabHeight * d * 2.0D);
        }
        if (paramInt4 - i1 > i2)
        {
          BasicTabbedPaneUI.this.tabRuns[j] = n;
          if (i == 0) {
            BasicTabbedPaneUI.this.rects[n].x = paramInt3;
          } else {
            BasicTabbedPaneUI.this.rects[n].y = paramInt3;
          }
          for (int i3 = n + 1; i3 <= m; i3++) {
            if (i == 0) {
              BasicTabbedPaneUI.this.rects[i3].x = (BasicTabbedPaneUI.this.rects[(i3 - 1)].x + BasicTabbedPaneUI.this.rects[(i3 - 1)].width);
            } else {
              BasicTabbedPaneUI.this.rects[i3].y = (BasicTabbedPaneUI.this.rects[(i3 - 1)].y + BasicTabbedPaneUI.this.rects[(i3 - 1)].height);
            }
          }
        }
        else if (j == BasicTabbedPaneUI.this.runCount - 1)
        {
          k = 0;
        }
        if (j - 1 > 0)
        {
          j--;
        }
        else
        {
          j = BasicTabbedPaneUI.this.runCount - 1;
          d += 0.25D;
        }
      }
    }
    
    protected void padTabRun(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Rectangle localRectangle1 = BasicTabbedPaneUI.this.rects[paramInt3];
      int i;
      int j;
      float f;
      int k;
      Rectangle localRectangle2;
      if ((paramInt1 == 1) || (paramInt1 == 3))
      {
        i = localRectangle1.x + localRectangle1.width - BasicTabbedPaneUI.this.rects[paramInt2].x;
        j = paramInt4 - (localRectangle1.x + localRectangle1.width);
        f = j / i;
        for (k = paramInt2; k <= paramInt3; k++)
        {
          localRectangle2 = BasicTabbedPaneUI.this.rects[k];
          if (k > paramInt2) {
            localRectangle2.x = (BasicTabbedPaneUI.this.rects[(k - 1)].x + BasicTabbedPaneUI.this.rects[(k - 1)].width);
          }
          localRectangle2.width += Math.round(localRectangle2.width * f);
        }
        localRectangle1.width = (paramInt4 - localRectangle1.x);
      }
      else
      {
        i = localRectangle1.y + localRectangle1.height - BasicTabbedPaneUI.this.rects[paramInt2].y;
        j = paramInt4 - (localRectangle1.y + localRectangle1.height);
        f = j / i;
        for (k = paramInt2; k <= paramInt3; k++)
        {
          localRectangle2 = BasicTabbedPaneUI.this.rects[k];
          if (k > paramInt2) {
            localRectangle2.y = (BasicTabbedPaneUI.this.rects[(k - 1)].y + BasicTabbedPaneUI.this.rects[(k - 1)].height);
          }
          localRectangle2.height += Math.round(localRectangle2.height * f);
        }
        localRectangle1.height = (paramInt4 - localRectangle1.y);
      }
    }
    
    protected void padSelectedTab(int paramInt1, int paramInt2)
    {
      if (paramInt2 >= 0)
      {
        Rectangle localRectangle = BasicTabbedPaneUI.this.rects[paramInt2];
        Insets localInsets1 = BasicTabbedPaneUI.this.getSelectedTabPadInsets(paramInt1);
        localRectangle.x -= localInsets1.left;
        localRectangle.width += localInsets1.left + localInsets1.right;
        localRectangle.y -= localInsets1.top;
        localRectangle.height += localInsets1.top + localInsets1.bottom;
        if (!BasicTabbedPaneUI.this.scrollableTabLayoutEnabled())
        {
          Dimension localDimension = BasicTabbedPaneUI.this.tabPane.getSize();
          Insets localInsets2 = BasicTabbedPaneUI.this.tabPane.getInsets();
          int i;
          int j;
          if ((paramInt1 == 2) || (paramInt1 == 4))
          {
            i = localInsets2.top - localRectangle.y;
            if (i > 0)
            {
              localRectangle.y += i;
              localRectangle.height -= i;
            }
            j = localRectangle.y + localRectangle.height + localInsets2.bottom - localDimension.height;
            if (j > 0) {
              localRectangle.height -= j;
            }
          }
          else
          {
            i = localInsets2.left - localRectangle.x;
            if (i > 0)
            {
              localRectangle.x += i;
              localRectangle.width -= i;
            }
            j = localRectangle.x + localRectangle.width + localInsets2.right - localDimension.width;
            if (j > 0) {
              localRectangle.width -= j;
            }
          }
        }
      }
    }
  }
  
  private class TabbedPaneScrollLayout
    extends BasicTabbedPaneUI.TabbedPaneLayout
  {
    private TabbedPaneScrollLayout()
    {
      super();
    }
    
    protected int preferredTabAreaHeight(int paramInt1, int paramInt2)
    {
      return BasicTabbedPaneUI.this.calculateMaxTabHeight(paramInt1);
    }
    
    protected int preferredTabAreaWidth(int paramInt1, int paramInt2)
    {
      return BasicTabbedPaneUI.this.calculateMaxTabWidth(paramInt1);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      BasicTabbedPaneUI.this.setRolloverTab(-1);
      int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
      int j = BasicTabbedPaneUI.this.tabPane.getTabCount();
      Insets localInsets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
      int k = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
      Component localComponent1 = BasicTabbedPaneUI.this.getVisibleComponent();
      calculateLayoutInfo();
      Component localComponent2 = null;
      if (k < 0)
      {
        if (localComponent1 != null) {
          BasicTabbedPaneUI.this.setVisibleComponent(null);
        }
      }
      else {
        localComponent2 = BasicTabbedPaneUI.this.tabPane.getComponentAt(k);
      }
      if (BasicTabbedPaneUI.this.tabPane.getTabCount() == 0)
      {
        BasicTabbedPaneUI.this.tabScroller.croppedEdge.resetParams();
        BasicTabbedPaneUI.this.tabScroller.scrollForwardButton.setVisible(false);
        BasicTabbedPaneUI.this.tabScroller.scrollBackwardButton.setVisible(false);
        return;
      }
      int m = 0;
      if (localComponent2 != null)
      {
        if ((localComponent2 != localComponent1) && (localComponent1 != null) && (SwingUtilities.findFocusOwner(localComponent1) != null)) {
          m = 1;
        }
        BasicTabbedPaneUI.this.setVisibleComponent(localComponent2);
      }
      Insets localInsets2 = BasicTabbedPaneUI.this.getContentBorderInsets(i);
      Rectangle localRectangle = BasicTabbedPaneUI.this.tabPane.getBounds();
      int i8 = BasicTabbedPaneUI.this.tabPane.getComponentCount();
      if (i8 > 0)
      {
        int i2;
        int i3;
        int n;
        int i1;
        int i4;
        int i5;
        int i6;
        int i7;
        switch (i)
        {
        case 2: 
          i2 = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
          i3 = localRectangle.height - localInsets1.top - localInsets1.bottom;
          n = localInsets1.left;
          i1 = localInsets1.top;
          i4 = n + i2 + localInsets2.left;
          i5 = i1 + localInsets2.top;
          i6 = localRectangle.width - localInsets1.left - localInsets1.right - i2 - localInsets2.left - localInsets2.right;
          i7 = localRectangle.height - localInsets1.top - localInsets1.bottom - localInsets2.top - localInsets2.bottom;
          break;
        case 4: 
          i2 = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
          i3 = localRectangle.height - localInsets1.top - localInsets1.bottom;
          n = localRectangle.width - localInsets1.right - i2;
          i1 = localInsets1.top;
          i4 = localInsets1.left + localInsets2.left;
          i5 = localInsets1.top + localInsets2.top;
          i6 = localRectangle.width - localInsets1.left - localInsets1.right - i2 - localInsets2.left - localInsets2.right;
          i7 = localRectangle.height - localInsets1.top - localInsets1.bottom - localInsets2.top - localInsets2.bottom;
          break;
        case 3: 
          i2 = localRectangle.width - localInsets1.left - localInsets1.right;
          i3 = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
          n = localInsets1.left;
          i1 = localRectangle.height - localInsets1.bottom - i3;
          i4 = localInsets1.left + localInsets2.left;
          i5 = localInsets1.top + localInsets2.top;
          i6 = localRectangle.width - localInsets1.left - localInsets1.right - localInsets2.left - localInsets2.right;
          i7 = localRectangle.height - localInsets1.top - localInsets1.bottom - i3 - localInsets2.top - localInsets2.bottom;
          break;
        case 1: 
        default: 
          i2 = localRectangle.width - localInsets1.left - localInsets1.right;
          i3 = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
          n = localInsets1.left;
          i1 = localInsets1.top;
          i4 = n + localInsets2.left;
          i5 = i1 + i3 + localInsets2.top;
          i6 = localRectangle.width - localInsets1.left - localInsets1.right - localInsets2.left - localInsets2.right;
          i7 = localRectangle.height - localInsets1.top - localInsets1.bottom - i3 - localInsets2.top - localInsets2.bottom;
        }
        for (int i9 = 0; i9 < i8; i9++)
        {
          Component localComponent3 = BasicTabbedPaneUI.this.tabPane.getComponent(i9);
          Object localObject1;
          Object localObject2;
          int i10;
          int i11;
          int i13;
          int i14;
          if ((BasicTabbedPaneUI.this.tabScroller != null) && (localComponent3 == BasicTabbedPaneUI.this.tabScroller.viewport))
          {
            localObject1 = (JViewport)localComponent3;
            localObject2 = ((JViewport)localObject1).getViewRect();
            i10 = i2;
            i11 = i3;
            Dimension localDimension = BasicTabbedPaneUI.this.tabScroller.scrollForwardButton.getPreferredSize();
            switch (i)
            {
            case 2: 
            case 4: 
              i13 = BasicTabbedPaneUI.this.rects[(j - 1)].y + BasicTabbedPaneUI.this.rects[(j - 1)].height;
              if (i13 > i3)
              {
                i11 = i3 > 2 * localDimension.height ? i3 - 2 * localDimension.height : 0;
                if (i13 - ((Rectangle)localObject2).y <= i11) {
                  i11 = i13 - ((Rectangle)localObject2).y;
                }
              }
              break;
            case 1: 
            case 3: 
            default: 
              i14 = BasicTabbedPaneUI.this.rects[(j - 1)].x + BasicTabbedPaneUI.this.rects[(j - 1)].width;
              if (i14 > i2)
              {
                i10 = i2 > 2 * localDimension.width ? i2 - 2 * localDimension.width : 0;
                if (i14 - ((Rectangle)localObject2).x <= i10) {
                  i10 = i14 - ((Rectangle)localObject2).x;
                }
              }
              break;
            }
            localComponent3.setBounds(n, i1, i10, i11);
          }
          else if ((BasicTabbedPaneUI.this.tabScroller != null) && ((localComponent3 == BasicTabbedPaneUI.this.tabScroller.scrollForwardButton) || (localComponent3 == BasicTabbedPaneUI.this.tabScroller.scrollBackwardButton)))
          {
            localObject1 = localComponent3;
            localObject2 = ((Component)localObject1).getPreferredSize();
            i10 = 0;
            i11 = 0;
            int i12 = ((Dimension)localObject2).width;
            i13 = ((Dimension)localObject2).height;
            i14 = 0;
            switch (i)
            {
            case 2: 
            case 4: 
              int i15 = BasicTabbedPaneUI.this.rects[(j - 1)].y + BasicTabbedPaneUI.this.rects[(j - 1)].height;
              if (i15 > i3)
              {
                i14 = 1;
                i10 = i == 2 ? n + i2 - ((Dimension)localObject2).width : n;
                i11 = localComponent3 == BasicTabbedPaneUI.this.tabScroller.scrollForwardButton ? localRectangle.height - localInsets1.bottom - ((Dimension)localObject2).height : localRectangle.height - localInsets1.bottom - 2 * ((Dimension)localObject2).height;
              }
              break;
            case 1: 
            case 3: 
            default: 
              int i16 = BasicTabbedPaneUI.this.rects[(j - 1)].x + BasicTabbedPaneUI.this.rects[(j - 1)].width;
              if (i16 > i2)
              {
                i14 = 1;
                i10 = localComponent3 == BasicTabbedPaneUI.this.tabScroller.scrollForwardButton ? localRectangle.width - localInsets1.left - ((Dimension)localObject2).width : localRectangle.width - localInsets1.left - 2 * ((Dimension)localObject2).width;
                i11 = i == 1 ? i1 + i3 - ((Dimension)localObject2).height : i1;
              }
              break;
            }
            localComponent3.setVisible(i14);
            if (i14 != 0) {
              localComponent3.setBounds(i10, i11, i12, i13);
            }
          }
          else
          {
            localComponent3.setBounds(i4, i5, i6, i7);
          }
        }
        super.layoutTabComponents();
        layoutCroppedEdge();
        if ((m != 0) && (!BasicTabbedPaneUI.this.requestFocusForVisibleComponent())) {
          BasicTabbedPaneUI.this.tabPane.requestFocus();
        }
      }
    }
    
    private void layoutCroppedEdge()
    {
      BasicTabbedPaneUI.this.tabScroller.croppedEdge.resetParams();
      Rectangle localRectangle1 = BasicTabbedPaneUI.this.tabScroller.viewport.getViewRect();
      for (int j = 0; j < BasicTabbedPaneUI.this.rects.length; j++)
      {
        Rectangle localRectangle2 = BasicTabbedPaneUI.this.rects[j];
        int i;
        switch (BasicTabbedPaneUI.this.tabPane.getTabPlacement())
        {
        case 2: 
        case 4: 
          i = localRectangle1.y + localRectangle1.height;
          if ((localRectangle2.y < i) && (localRectangle2.y + localRectangle2.height > i)) {
            BasicTabbedPaneUI.this.tabScroller.croppedEdge.setParams(j, i - localRectangle2.y - 1, -BasicTabbedPaneUI.this.currentTabAreaInsets.left, 0);
          }
          break;
        case 1: 
        case 3: 
        default: 
          i = localRectangle1.x + localRectangle1.width;
          if ((localRectangle2.x < i - 1) && (localRectangle2.x + localRectangle2.width > i)) {
            BasicTabbedPaneUI.this.tabScroller.croppedEdge.setParams(j, i - localRectangle2.x - 1, 0, -BasicTabbedPaneUI.this.currentTabAreaInsets.top);
          }
          break;
        }
      }
    }
    
    protected void calculateTabRects(int paramInt1, int paramInt2)
    {
      FontMetrics localFontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
      Dimension localDimension = BasicTabbedPaneUI.this.tabPane.getSize();
      Insets localInsets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
      Insets localInsets2 = BasicTabbedPaneUI.this.getTabAreaInsets(paramInt1);
      int i = localFontMetrics.getHeight();
      int j = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
      int m = (paramInt1 == 2) || (paramInt1 == 4) ? 1 : 0;
      boolean bool = BasicGraphicsUtils.isLeftToRight(BasicTabbedPaneUI.this.tabPane);
      int n = localInsets2.left;
      int i1 = localInsets2.top;
      int i2 = 0;
      int i3 = 0;
      switch (paramInt1)
      {
      case 2: 
      case 4: 
        BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(paramInt1);
        break;
      case 1: 
      case 3: 
      default: 
        BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(paramInt1);
      }
      BasicTabbedPaneUI.this.runCount = 0;
      BasicTabbedPaneUI.this.selectedRun = -1;
      if (paramInt2 == 0) {
        return;
      }
      BasicTabbedPaneUI.this.selectedRun = 0;
      BasicTabbedPaneUI.this.runCount = 1;
      for (int k = 0; k < paramInt2; k++)
      {
        Rectangle localRectangle = BasicTabbedPaneUI.this.rects[k];
        if (m == 0)
        {
          if (k > 0)
          {
            localRectangle.x = (BasicTabbedPaneUI.this.rects[(k - 1)].x + BasicTabbedPaneUI.this.rects[(k - 1)].width);
          }
          else
          {
            BasicTabbedPaneUI.this.tabRuns[0] = 0;
            BasicTabbedPaneUI.this.maxTabWidth = 0;
            i3 += BasicTabbedPaneUI.this.maxTabHeight;
            localRectangle.x = n;
          }
          localRectangle.width = BasicTabbedPaneUI.this.calculateTabWidth(paramInt1, k, localFontMetrics);
          i2 = localRectangle.x + localRectangle.width;
          BasicTabbedPaneUI.this.maxTabWidth = Math.max(BasicTabbedPaneUI.this.maxTabWidth, localRectangle.width);
          localRectangle.y = i1;
          localRectangle.height = BasicTabbedPaneUI.this.maxTabHeight;
        }
        else
        {
          if (k > 0)
          {
            localRectangle.y = (BasicTabbedPaneUI.this.rects[(k - 1)].y + BasicTabbedPaneUI.this.rects[(k - 1)].height);
          }
          else
          {
            BasicTabbedPaneUI.this.tabRuns[0] = 0;
            BasicTabbedPaneUI.this.maxTabHeight = 0;
            i2 = BasicTabbedPaneUI.this.maxTabWidth;
            localRectangle.y = i1;
          }
          localRectangle.height = BasicTabbedPaneUI.this.calculateTabHeight(paramInt1, k, i);
          i3 = localRectangle.y + localRectangle.height;
          BasicTabbedPaneUI.this.maxTabHeight = Math.max(BasicTabbedPaneUI.this.maxTabHeight, localRectangle.height);
          localRectangle.x = n;
          localRectangle.width = BasicTabbedPaneUI.this.maxTabWidth;
        }
      }
      if (BasicTabbedPaneUI.this.tabsOverlapBorder) {
        padSelectedTab(paramInt1, j);
      }
      if ((!bool) && (m == 0))
      {
        int i4 = localDimension.width - (localInsets1.right + localInsets2.right);
        for (k = 0; k < paramInt2; k++) {
          BasicTabbedPaneUI.this.rects[k].x = (i4 - BasicTabbedPaneUI.this.rects[k].x - BasicTabbedPaneUI.this.rects[k].width);
        }
      }
      BasicTabbedPaneUI.this.tabScroller.tabPanel.setPreferredSize(new Dimension(i2, i3));
      BasicTabbedPaneUI.this.tabScroller.tabPanel.invalidate();
    }
  }
}
