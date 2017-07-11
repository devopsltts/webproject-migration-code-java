package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.AccessController;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.SwingUtilities2.AATextInfo;
import sun.swing.plaf.synth.SynthFileChooserUI;

public class SynthLookAndFeel
  extends BasicLookAndFeel
{
  static final Insets EMPTY_UIRESOURCE_INSETS = new InsetsUIResource(0, 0, 0, 0);
  private static final Object STYLE_FACTORY_KEY = new StringBuffer("com.sun.java.swing.plaf.gtk.StyleCache");
  private static final Object SELECTED_UI_KEY = new StringBuilder("selectedUI");
  private static final Object SELECTED_UI_STATE_KEY = new StringBuilder("selectedUIState");
  private static SynthStyleFactory lastFactory;
  private static AppContext lastContext;
  private SynthStyleFactory factory = new DefaultSynthStyleFactory();
  private Map<String, Object> defaultsMap;
  private Handler _handler = new Handler(null);
  private static ReferenceQueue<LookAndFeel> queue = new ReferenceQueue();
  
  static ComponentUI getSelectedUI()
  {
    return (ComponentUI)AppContext.getAppContext().get(SELECTED_UI_KEY);
  }
  
  static void setSelectedUI(ComponentUI paramComponentUI, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    int i = 0;
    if (paramBoolean1)
    {
      i = 512;
      if (paramBoolean2) {
        i |= 0x100;
      }
    }
    else if ((paramBoolean4) && (paramBoolean3))
    {
      i |= 0x3;
      if (paramBoolean2) {
        i |= 0x100;
      }
    }
    else if (paramBoolean3)
    {
      i |= 0x1;
      if (paramBoolean2) {
        i |= 0x100;
      }
    }
    else
    {
      i |= 0x8;
    }
    AppContext localAppContext = AppContext.getAppContext();
    localAppContext.put(SELECTED_UI_KEY, paramComponentUI);
    localAppContext.put(SELECTED_UI_STATE_KEY, Integer.valueOf(i));
  }
  
  static int getSelectedUIState()
  {
    Integer localInteger = (Integer)AppContext.getAppContext().get(SELECTED_UI_STATE_KEY);
    return localInteger == null ? 0 : localInteger.intValue();
  }
  
  static void resetSelectedUI()
  {
    AppContext.getAppContext().remove(SELECTED_UI_KEY);
  }
  
  public static void setStyleFactory(SynthStyleFactory paramSynthStyleFactory)
  {
    synchronized (SynthLookAndFeel.class)
    {
      AppContext localAppContext = AppContext.getAppContext();
      lastFactory = paramSynthStyleFactory;
      lastContext = localAppContext;
      localAppContext.put(STYLE_FACTORY_KEY, paramSynthStyleFactory);
    }
  }
  
  public static SynthStyleFactory getStyleFactory()
  {
    synchronized (SynthLookAndFeel.class)
    {
      AppContext localAppContext = AppContext.getAppContext();
      if (lastContext == localAppContext) {
        return lastFactory;
      }
      lastContext = localAppContext;
      lastFactory = (SynthStyleFactory)localAppContext.get(STYLE_FACTORY_KEY);
      return lastFactory;
    }
  }
  
  static int getComponentState(Component paramComponent)
  {
    if (paramComponent.isEnabled())
    {
      if (paramComponent.isFocusOwner()) {
        return 257;
      }
      return 1;
    }
    return 8;
  }
  
  public static SynthStyle getStyle(JComponent paramJComponent, Region paramRegion)
  {
    return getStyleFactory().getStyle(paramJComponent, paramRegion);
  }
  
  static boolean shouldUpdateStyle(PropertyChangeEvent paramPropertyChangeEvent)
  {
    LookAndFeel localLookAndFeel = UIManager.getLookAndFeel();
    return ((localLookAndFeel instanceof SynthLookAndFeel)) && (((SynthLookAndFeel)localLookAndFeel).shouldUpdateStyleOnEvent(paramPropertyChangeEvent));
  }
  
  static SynthStyle updateStyle(SynthContext paramSynthContext, SynthUI paramSynthUI)
  {
    SynthStyle localSynthStyle1 = getStyle(paramSynthContext.getComponent(), paramSynthContext.getRegion());
    SynthStyle localSynthStyle2 = paramSynthContext.getStyle();
    if (localSynthStyle1 != localSynthStyle2)
    {
      if (localSynthStyle2 != null) {
        localSynthStyle2.uninstallDefaults(paramSynthContext);
      }
      paramSynthContext.setStyle(localSynthStyle1);
      localSynthStyle1.installDefaults(paramSynthContext, paramSynthUI);
    }
    return localSynthStyle1;
  }
  
  public static void updateStyles(Component paramComponent)
  {
    if ((paramComponent instanceof JComponent))
    {
      localObject1 = paramComponent.getName();
      paramComponent.setName(null);
      if (localObject1 != null) {
        paramComponent.setName((String)localObject1);
      }
      ((JComponent)paramComponent).revalidate();
    }
    Object localObject1 = null;
    if ((paramComponent instanceof JMenu)) {
      localObject1 = ((JMenu)paramComponent).getMenuComponents();
    } else if ((paramComponent instanceof Container)) {
      localObject1 = ((Container)paramComponent).getComponents();
    }
    if (localObject1 != null) {
      for (Component localComponent : localObject1) {
        updateStyles(localComponent);
      }
    }
    paramComponent.repaint();
  }
  
  public static Region getRegion(JComponent paramJComponent)
  {
    return Region.getRegion(paramJComponent);
  }
  
  static Insets getPaintingInsets(SynthContext paramSynthContext, Insets paramInsets)
  {
    if (paramSynthContext.isSubregion()) {
      paramInsets = paramSynthContext.getStyle().getInsets(paramSynthContext, paramInsets);
    } else {
      paramInsets = paramSynthContext.getComponent().getInsets(paramInsets);
    }
    return paramInsets;
  }
  
  static void update(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    paintRegion(paramSynthContext, paramGraphics, null);
  }
  
  static void updateSubregion(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle)
  {
    paintRegion(paramSynthContext, paramGraphics, paramRectangle);
  }
  
  private static void paintRegion(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle)
  {
    JComponent localJComponent = paramSynthContext.getComponent();
    SynthStyle localSynthStyle = paramSynthContext.getStyle();
    int i;
    int j;
    int k;
    int m;
    if (paramRectangle == null)
    {
      i = 0;
      j = 0;
      k = localJComponent.getWidth();
      m = localJComponent.getHeight();
    }
    else
    {
      i = paramRectangle.x;
      j = paramRectangle.y;
      k = paramRectangle.width;
      m = paramRectangle.height;
    }
    boolean bool = paramSynthContext.isSubregion();
    if (((bool) && (localSynthStyle.isOpaque(paramSynthContext))) || ((!bool) && (localJComponent.isOpaque())))
    {
      paramGraphics.setColor(localSynthStyle.getColor(paramSynthContext, ColorType.BACKGROUND));
      paramGraphics.fillRect(i, j, k, m);
    }
  }
  
  static boolean isLeftToRight(Component paramComponent)
  {
    return paramComponent.getComponentOrientation().isLeftToRight();
  }
  
  static Object getUIOfType(ComponentUI paramComponentUI, Class paramClass)
  {
    if (paramClass.isInstance(paramComponentUI)) {
      return paramComponentUI;
    }
    return null;
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    String str = paramJComponent.getUIClassID().intern();
    if (str == "ButtonUI") {
      return SynthButtonUI.createUI(paramJComponent);
    }
    if (str == "CheckBoxUI") {
      return SynthCheckBoxUI.createUI(paramJComponent);
    }
    if (str == "CheckBoxMenuItemUI") {
      return SynthCheckBoxMenuItemUI.createUI(paramJComponent);
    }
    if (str == "ColorChooserUI") {
      return SynthColorChooserUI.createUI(paramJComponent);
    }
    if (str == "ComboBoxUI") {
      return SynthComboBoxUI.createUI(paramJComponent);
    }
    if (str == "DesktopPaneUI") {
      return SynthDesktopPaneUI.createUI(paramJComponent);
    }
    if (str == "DesktopIconUI") {
      return SynthDesktopIconUI.createUI(paramJComponent);
    }
    if (str == "EditorPaneUI") {
      return SynthEditorPaneUI.createUI(paramJComponent);
    }
    if (str == "FileChooserUI") {
      return SynthFileChooserUI.createUI(paramJComponent);
    }
    if (str == "FormattedTextFieldUI") {
      return SynthFormattedTextFieldUI.createUI(paramJComponent);
    }
    if (str == "InternalFrameUI") {
      return SynthInternalFrameUI.createUI(paramJComponent);
    }
    if (str == "LabelUI") {
      return SynthLabelUI.createUI(paramJComponent);
    }
    if (str == "ListUI") {
      return SynthListUI.createUI(paramJComponent);
    }
    if (str == "MenuBarUI") {
      return SynthMenuBarUI.createUI(paramJComponent);
    }
    if (str == "MenuUI") {
      return SynthMenuUI.createUI(paramJComponent);
    }
    if (str == "MenuItemUI") {
      return SynthMenuItemUI.createUI(paramJComponent);
    }
    if (str == "OptionPaneUI") {
      return SynthOptionPaneUI.createUI(paramJComponent);
    }
    if (str == "PanelUI") {
      return SynthPanelUI.createUI(paramJComponent);
    }
    if (str == "PasswordFieldUI") {
      return SynthPasswordFieldUI.createUI(paramJComponent);
    }
    if (str == "PopupMenuSeparatorUI") {
      return SynthSeparatorUI.createUI(paramJComponent);
    }
    if (str == "PopupMenuUI") {
      return SynthPopupMenuUI.createUI(paramJComponent);
    }
    if (str == "ProgressBarUI") {
      return SynthProgressBarUI.createUI(paramJComponent);
    }
    if (str == "RadioButtonUI") {
      return SynthRadioButtonUI.createUI(paramJComponent);
    }
    if (str == "RadioButtonMenuItemUI") {
      return SynthRadioButtonMenuItemUI.createUI(paramJComponent);
    }
    if (str == "RootPaneUI") {
      return SynthRootPaneUI.createUI(paramJComponent);
    }
    if (str == "ScrollBarUI") {
      return SynthScrollBarUI.createUI(paramJComponent);
    }
    if (str == "ScrollPaneUI") {
      return SynthScrollPaneUI.createUI(paramJComponent);
    }
    if (str == "SeparatorUI") {
      return SynthSeparatorUI.createUI(paramJComponent);
    }
    if (str == "SliderUI") {
      return SynthSliderUI.createUI(paramJComponent);
    }
    if (str == "SpinnerUI") {
      return SynthSpinnerUI.createUI(paramJComponent);
    }
    if (str == "SplitPaneUI") {
      return SynthSplitPaneUI.createUI(paramJComponent);
    }
    if (str == "TabbedPaneUI") {
      return SynthTabbedPaneUI.createUI(paramJComponent);
    }
    if (str == "TableUI") {
      return SynthTableUI.createUI(paramJComponent);
    }
    if (str == "TableHeaderUI") {
      return SynthTableHeaderUI.createUI(paramJComponent);
    }
    if (str == "TextAreaUI") {
      return SynthTextAreaUI.createUI(paramJComponent);
    }
    if (str == "TextFieldUI") {
      return SynthTextFieldUI.createUI(paramJComponent);
    }
    if (str == "TextPaneUI") {
      return SynthTextPaneUI.createUI(paramJComponent);
    }
    if (str == "ToggleButtonUI") {
      return SynthToggleButtonUI.createUI(paramJComponent);
    }
    if (str == "ToolBarSeparatorUI") {
      return SynthSeparatorUI.createUI(paramJComponent);
    }
    if (str == "ToolBarUI") {
      return SynthToolBarUI.createUI(paramJComponent);
    }
    if (str == "ToolTipUI") {
      return SynthToolTipUI.createUI(paramJComponent);
    }
    if (str == "TreeUI") {
      return SynthTreeUI.createUI(paramJComponent);
    }
    if (str == "ViewportUI") {
      return SynthViewportUI.createUI(paramJComponent);
    }
    return null;
  }
  
  public SynthLookAndFeel() {}
  
  public void load(InputStream paramInputStream, Class<?> paramClass)
    throws ParseException
  {
    if (paramClass == null) {
      throw new IllegalArgumentException("You must supply a valid resource base Class");
    }
    if (this.defaultsMap == null) {
      this.defaultsMap = new HashMap();
    }
    new SynthParser().parse(paramInputStream, (DefaultSynthStyleFactory)this.factory, null, paramClass, this.defaultsMap);
  }
  
  public void load(URL paramURL)
    throws ParseException, IOException
  {
    if (paramURL == null) {
      throw new IllegalArgumentException("You must supply a valid Synth set URL");
    }
    if (this.defaultsMap == null) {
      this.defaultsMap = new HashMap();
    }
    InputStream localInputStream = paramURL.openStream();
    new SynthParser().parse(localInputStream, (DefaultSynthStyleFactory)this.factory, paramURL, null, this.defaultsMap);
  }
  
  public void initialize()
  {
    super.initialize();
    DefaultLookup.setDefaultLookup(new SynthDefaultLookup());
    setStyleFactory(this.factory);
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(this._handler);
  }
  
  public void uninitialize()
  {
    KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(this._handler);
    super.uninitialize();
  }
  
  public UIDefaults getDefaults()
  {
    UIDefaults localUIDefaults = new UIDefaults(60, 0.75F);
    Region.registerUIs(localUIDefaults);
    localUIDefaults.setDefaultLocale(Locale.getDefault());
    localUIDefaults.addResourceBundle("com.sun.swing.internal.plaf.basic.resources.basic");
    localUIDefaults.addResourceBundle("com.sun.swing.internal.plaf.synth.resources.synth");
    localUIDefaults.put("TabbedPane.isTabRollover", Boolean.TRUE);
    localUIDefaults.put("ColorChooser.swatchesRecentSwatchSize", new Dimension(10, 10));
    localUIDefaults.put("ColorChooser.swatchesDefaultRecentColor", Color.RED);
    localUIDefaults.put("ColorChooser.swatchesSwatchSize", new Dimension(10, 10));
    localUIDefaults.put("html.pendingImage", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/image-delayed.png"));
    localUIDefaults.put("html.missingImage", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/image-failed.png"));
    localUIDefaults.put("PopupMenu.selectedWindowInputMapBindings", new Object[] { "ESCAPE", "cancel", "DOWN", "selectNext", "KP_DOWN", "selectNext", "UP", "selectPrevious", "KP_UP", "selectPrevious", "LEFT", "selectParent", "KP_LEFT", "selectParent", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "ENTER", "return", "SPACE", "return" });
    localUIDefaults.put("PopupMenu.selectedWindowInputMapBindings.RightToLeft", new Object[] { "LEFT", "selectChild", "KP_LEFT", "selectChild", "RIGHT", "selectParent", "KP_RIGHT", "selectParent" });
    flushUnreferenced();
    Object localObject = getAATextInfo();
    localUIDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, localObject);
    new AATextListener(this);
    if (this.defaultsMap != null) {
      localUIDefaults.putAll(this.defaultsMap);
    }
    return localUIDefaults;
  }
  
  public boolean isSupportedLookAndFeel()
  {
    return true;
  }
  
  public boolean isNativeLookAndFeel()
  {
    return false;
  }
  
  public String getDescription()
  {
    return "Synth look and feel";
  }
  
  public String getName()
  {
    return "Synth look and feel";
  }
  
  public String getID()
  {
    return "Synth";
  }
  
  public boolean shouldUpdateStyleOnAncestorChanged()
  {
    return false;
  }
  
  protected boolean shouldUpdateStyleOnEvent(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (("name" == str) || ("componentOrientation" == str)) {
      return true;
    }
    if (("ancestor" == str) && (paramPropertyChangeEvent.getNewValue() != null)) {
      return shouldUpdateStyleOnAncestorChanged();
    }
    return false;
  }
  
  private static Object getAATextInfo()
  {
    String str1 = Locale.getDefault().getLanguage();
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.desktop"));
    int i = (Locale.CHINESE.getLanguage().equals(str1)) || (Locale.JAPANESE.getLanguage().equals(str1)) || (Locale.KOREAN.getLanguage().equals(str1)) ? 1 : 0;
    boolean bool1 = "gnome".equals(str2);
    boolean bool2 = SwingUtilities2.isLocalDisplay();
    boolean bool3 = (bool2) && ((!bool1) || (i == 0));
    SwingUtilities2.AATextInfo localAATextInfo = SwingUtilities2.AATextInfo.getAATextInfo(bool3);
    return localAATextInfo;
  }
  
  private static void flushUnreferenced()
  {
    AATextListener localAATextListener;
    while ((localAATextListener = (AATextListener)queue.poll()) != null) {
      localAATextListener.dispose();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    throw new NotSerializableException(getClass().getName());
  }
  
  private static class AATextListener
    extends WeakReference<LookAndFeel>
    implements PropertyChangeListener
  {
    private String key = "awt.font.desktophints";
    private static boolean updatePending;
    
    AATextListener(LookAndFeel paramLookAndFeel)
    {
      super(SynthLookAndFeel.queue);
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      localToolkit.addPropertyChangeListener(this.key, this);
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
      if (localUIDefaults.getBoolean("Synth.doNotSetTextAA"))
      {
        dispose();
        return;
      }
      LookAndFeel localLookAndFeel = (LookAndFeel)get();
      if ((localLookAndFeel == null) || (localLookAndFeel != UIManager.getLookAndFeel()))
      {
        dispose();
        return;
      }
      Object localObject = SynthLookAndFeel.access$200();
      localUIDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, localObject);
      updateUI();
    }
    
    void dispose()
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      localToolkit.removePropertyChangeListener(this.key, this);
    }
    
    private static void updateWindowUI(Window paramWindow)
    {
      SynthLookAndFeel.updateStyles(paramWindow);
      Window[] arrayOfWindow1 = paramWindow.getOwnedWindows();
      for (Window localWindow : arrayOfWindow1) {
        updateWindowUI(localWindow);
      }
    }
    
    private static void updateAllUIs()
    {
      Frame[] arrayOfFrame1 = Frame.getFrames();
      for (Frame localFrame : arrayOfFrame1) {
        updateWindowUI(localFrame);
      }
    }
    
    private static synchronized void setUpdatePending(boolean paramBoolean)
    {
      updatePending = paramBoolean;
    }
    
    private static synchronized boolean isUpdatePending()
    {
      return updatePending;
    }
    
    protected void updateUI()
    {
      if (!isUpdatePending())
      {
        setUpdatePending(true);
        Runnable local1 = new Runnable()
        {
          public void run()
          {
            SynthLookAndFeel.AATextListener.access$300();
            SynthLookAndFeel.AATextListener.setUpdatePending(false);
          }
        };
        SwingUtilities.invokeLater(local1);
      }
    }
  }
  
  private class Handler
    implements PropertyChangeListener
  {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      Object localObject1 = paramPropertyChangeEvent.getNewValue();
      Object localObject2 = paramPropertyChangeEvent.getOldValue();
      if ("focusOwner" == str)
      {
        if ((localObject2 instanceof JComponent)) {
          repaintIfBackgroundsDiffer((JComponent)localObject2);
        }
        if ((localObject1 instanceof JComponent)) {
          repaintIfBackgroundsDiffer((JComponent)localObject1);
        }
      }
      else if ("managingFocus" == str)
      {
        KeyboardFocusManager localKeyboardFocusManager = (KeyboardFocusManager)paramPropertyChangeEvent.getSource();
        if (localObject1.equals(Boolean.FALSE)) {
          localKeyboardFocusManager.removePropertyChangeListener(SynthLookAndFeel.this._handler);
        } else {
          localKeyboardFocusManager.addPropertyChangeListener(SynthLookAndFeel.this._handler);
        }
      }
    }
    
    private void repaintIfBackgroundsDiffer(JComponent paramJComponent)
    {
      ComponentUI localComponentUI = (ComponentUI)paramJComponent.getClientProperty(SwingUtilities2.COMPONENT_UI_PROPERTY_KEY);
      if ((localComponentUI instanceof SynthUI))
      {
        SynthUI localSynthUI = (SynthUI)localComponentUI;
        SynthContext localSynthContext = localSynthUI.getContext(paramJComponent);
        SynthStyle localSynthStyle = localSynthContext.getStyle();
        int i = localSynthContext.getComponentState();
        Color localColor1 = localSynthStyle.getColor(localSynthContext, ColorType.BACKGROUND);
        i ^= 0x100;
        localSynthContext.setComponentState(i);
        Color localColor2 = localSynthStyle.getColor(localSynthContext, ColorType.BACKGROUND);
        i ^= 0x100;
        localSynthContext.setComponentState(i);
        if ((localColor1 != null) && (!localColor1.equals(localColor2))) {
          paramJComponent.repaint();
        }
        localSynthContext.dispose();
      }
    }
  }
}
