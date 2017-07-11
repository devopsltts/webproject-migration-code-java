package javax.swing.plaf.synth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import sun.awt.AppContext;

public class Region
{
  private static final Object UI_TO_REGION_MAP_KEY = new Object();
  private static final Object LOWER_CASE_NAME_MAP_KEY = new Object();
  public static final Region ARROW_BUTTON = new Region("ArrowButton", false);
  public static final Region BUTTON = new Region("Button", false);
  public static final Region CHECK_BOX = new Region("CheckBox", false);
  public static final Region CHECK_BOX_MENU_ITEM = new Region("CheckBoxMenuItem", false);
  public static final Region COLOR_CHOOSER = new Region("ColorChooser", false);
  public static final Region COMBO_BOX = new Region("ComboBox", false);
  public static final Region DESKTOP_PANE = new Region("DesktopPane", false);
  public static final Region DESKTOP_ICON = new Region("DesktopIcon", false);
  public static final Region EDITOR_PANE = new Region("EditorPane", false);
  public static final Region FILE_CHOOSER = new Region("FileChooser", false);
  public static final Region FORMATTED_TEXT_FIELD = new Region("FormattedTextField", false);
  public static final Region INTERNAL_FRAME = new Region("InternalFrame", false);
  public static final Region INTERNAL_FRAME_TITLE_PANE = new Region("InternalFrameTitlePane", false);
  public static final Region LABEL = new Region("Label", false);
  public static final Region LIST = new Region("List", false);
  public static final Region MENU = new Region("Menu", false);
  public static final Region MENU_BAR = new Region("MenuBar", false);
  public static final Region MENU_ITEM = new Region("MenuItem", false);
  public static final Region MENU_ITEM_ACCELERATOR = new Region("MenuItemAccelerator", true);
  public static final Region OPTION_PANE = new Region("OptionPane", false);
  public static final Region PANEL = new Region("Panel", false);
  public static final Region PASSWORD_FIELD = new Region("PasswordField", false);
  public static final Region POPUP_MENU = new Region("PopupMenu", false);
  public static final Region POPUP_MENU_SEPARATOR = new Region("PopupMenuSeparator", false);
  public static final Region PROGRESS_BAR = new Region("ProgressBar", false);
  public static final Region RADIO_BUTTON = new Region("RadioButton", false);
  public static final Region RADIO_BUTTON_MENU_ITEM = new Region("RadioButtonMenuItem", false);
  public static final Region ROOT_PANE = new Region("RootPane", false);
  public static final Region SCROLL_BAR = new Region("ScrollBar", false);
  public static final Region SCROLL_BAR_TRACK = new Region("ScrollBarTrack", true);
  public static final Region SCROLL_BAR_THUMB = new Region("ScrollBarThumb", true);
  public static final Region SCROLL_PANE = new Region("ScrollPane", false);
  public static final Region SEPARATOR = new Region("Separator", false);
  public static final Region SLIDER = new Region("Slider", false);
  public static final Region SLIDER_TRACK = new Region("SliderTrack", true);
  public static final Region SLIDER_THUMB = new Region("SliderThumb", true);
  public static final Region SPINNER = new Region("Spinner", false);
  public static final Region SPLIT_PANE = new Region("SplitPane", false);
  public static final Region SPLIT_PANE_DIVIDER = new Region("SplitPaneDivider", true);
  public static final Region TABBED_PANE = new Region("TabbedPane", false);
  public static final Region TABBED_PANE_TAB = new Region("TabbedPaneTab", true);
  public static final Region TABBED_PANE_TAB_AREA = new Region("TabbedPaneTabArea", true);
  public static final Region TABBED_PANE_CONTENT = new Region("TabbedPaneContent", true);
  public static final Region TABLE = new Region("Table", false);
  public static final Region TABLE_HEADER = new Region("TableHeader", false);
  public static final Region TEXT_AREA = new Region("TextArea", false);
  public static final Region TEXT_FIELD = new Region("TextField", false);
  public static final Region TEXT_PANE = new Region("TextPane", false);
  public static final Region TOGGLE_BUTTON = new Region("ToggleButton", false);
  public static final Region TOOL_BAR = new Region("ToolBar", false);
  public static final Region TOOL_BAR_CONTENT = new Region("ToolBarContent", true);
  public static final Region TOOL_BAR_DRAG_WINDOW = new Region("ToolBarDragWindow", false);
  public static final Region TOOL_TIP = new Region("ToolTip", false);
  public static final Region TOOL_BAR_SEPARATOR = new Region("ToolBarSeparator", false);
  public static final Region TREE = new Region("Tree", false);
  public static final Region TREE_CELL = new Region("TreeCell", true);
  public static final Region VIEWPORT = new Region("Viewport", false);
  private final String name;
  private final boolean subregion;
  
  private static Map<String, Region> getUItoRegionMap()
  {
    AppContext localAppContext = AppContext.getAppContext();
    Object localObject = (Map)localAppContext.get(UI_TO_REGION_MAP_KEY);
    if (localObject == null)
    {
      localObject = new HashMap();
      ((Map)localObject).put("ArrowButtonUI", ARROW_BUTTON);
      ((Map)localObject).put("ButtonUI", BUTTON);
      ((Map)localObject).put("CheckBoxUI", CHECK_BOX);
      ((Map)localObject).put("CheckBoxMenuItemUI", CHECK_BOX_MENU_ITEM);
      ((Map)localObject).put("ColorChooserUI", COLOR_CHOOSER);
      ((Map)localObject).put("ComboBoxUI", COMBO_BOX);
      ((Map)localObject).put("DesktopPaneUI", DESKTOP_PANE);
      ((Map)localObject).put("DesktopIconUI", DESKTOP_ICON);
      ((Map)localObject).put("EditorPaneUI", EDITOR_PANE);
      ((Map)localObject).put("FileChooserUI", FILE_CHOOSER);
      ((Map)localObject).put("FormattedTextFieldUI", FORMATTED_TEXT_FIELD);
      ((Map)localObject).put("InternalFrameUI", INTERNAL_FRAME);
      ((Map)localObject).put("InternalFrameTitlePaneUI", INTERNAL_FRAME_TITLE_PANE);
      ((Map)localObject).put("LabelUI", LABEL);
      ((Map)localObject).put("ListUI", LIST);
      ((Map)localObject).put("MenuUI", MENU);
      ((Map)localObject).put("MenuBarUI", MENU_BAR);
      ((Map)localObject).put("MenuItemUI", MENU_ITEM);
      ((Map)localObject).put("OptionPaneUI", OPTION_PANE);
      ((Map)localObject).put("PanelUI", PANEL);
      ((Map)localObject).put("PasswordFieldUI", PASSWORD_FIELD);
      ((Map)localObject).put("PopupMenuUI", POPUP_MENU);
      ((Map)localObject).put("PopupMenuSeparatorUI", POPUP_MENU_SEPARATOR);
      ((Map)localObject).put("ProgressBarUI", PROGRESS_BAR);
      ((Map)localObject).put("RadioButtonUI", RADIO_BUTTON);
      ((Map)localObject).put("RadioButtonMenuItemUI", RADIO_BUTTON_MENU_ITEM);
      ((Map)localObject).put("RootPaneUI", ROOT_PANE);
      ((Map)localObject).put("ScrollBarUI", SCROLL_BAR);
      ((Map)localObject).put("ScrollPaneUI", SCROLL_PANE);
      ((Map)localObject).put("SeparatorUI", SEPARATOR);
      ((Map)localObject).put("SliderUI", SLIDER);
      ((Map)localObject).put("SpinnerUI", SPINNER);
      ((Map)localObject).put("SplitPaneUI", SPLIT_PANE);
      ((Map)localObject).put("TabbedPaneUI", TABBED_PANE);
      ((Map)localObject).put("TableUI", TABLE);
      ((Map)localObject).put("TableHeaderUI", TABLE_HEADER);
      ((Map)localObject).put("TextAreaUI", TEXT_AREA);
      ((Map)localObject).put("TextFieldUI", TEXT_FIELD);
      ((Map)localObject).put("TextPaneUI", TEXT_PANE);
      ((Map)localObject).put("ToggleButtonUI", TOGGLE_BUTTON);
      ((Map)localObject).put("ToolBarUI", TOOL_BAR);
      ((Map)localObject).put("ToolTipUI", TOOL_TIP);
      ((Map)localObject).put("ToolBarSeparatorUI", TOOL_BAR_SEPARATOR);
      ((Map)localObject).put("TreeUI", TREE);
      ((Map)localObject).put("ViewportUI", VIEWPORT);
      localAppContext.put(UI_TO_REGION_MAP_KEY, localObject);
    }
    return localObject;
  }
  
  private static Map<Region, String> getLowerCaseNameMap()
  {
    AppContext localAppContext = AppContext.getAppContext();
    Object localObject = (Map)localAppContext.get(LOWER_CASE_NAME_MAP_KEY);
    if (localObject == null)
    {
      localObject = new HashMap();
      localAppContext.put(LOWER_CASE_NAME_MAP_KEY, localObject);
    }
    return localObject;
  }
  
  static Region getRegion(JComponent paramJComponent)
  {
    return (Region)getUItoRegionMap().get(paramJComponent.getUIClassID());
  }
  
  static void registerUIs(UIDefaults paramUIDefaults)
  {
    Iterator localIterator = getUItoRegionMap().keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      paramUIDefaults.put(localObject, "javax.swing.plaf.synth.SynthLookAndFeel");
    }
  }
  
  private Region(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      throw new NullPointerException("You must specify a non-null name");
    }
    this.name = paramString;
    this.subregion = paramBoolean;
  }
  
  protected Region(String paramString1, String paramString2, boolean paramBoolean)
  {
    this(paramString1, paramBoolean);
    if (paramString2 != null) {
      getUItoRegionMap().put(paramString2, this);
    }
  }
  
  public boolean isSubregion()
  {
    return this.subregion;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  String getLowerCaseName()
  {
    Map localMap = getLowerCaseNameMap();
    String str = (String)localMap.get(this);
    if (str == null)
    {
      str = this.name.toLowerCase(Locale.ENGLISH);
      localMap.put(this, str);
    }
    return str;
  }
  
  public String toString()
  {
    return this.name;
  }
}
