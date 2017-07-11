package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.ActionMapUIResource;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;

public class BasicInternalFrameTitlePane
  extends JComponent
{
  protected JMenuBar menuBar;
  protected JButton iconButton;
  protected JButton maxButton;
  protected JButton closeButton;
  protected JMenu windowMenu;
  protected JInternalFrame frame;
  protected Color selectedTitleColor;
  protected Color selectedTextColor;
  protected Color notSelectedTitleColor;
  protected Color notSelectedTextColor;
  protected Icon maxIcon;
  protected Icon minIcon;
  protected Icon iconIcon;
  protected Icon closeIcon;
  protected PropertyChangeListener propertyChangeListener;
  protected Action closeAction;
  protected Action maximizeAction;
  protected Action iconifyAction;
  protected Action restoreAction;
  protected Action moveAction;
  protected Action sizeAction;
  protected static final String CLOSE_CMD = UIManager.getString("InternalFrameTitlePane.closeButtonText");
  protected static final String ICONIFY_CMD = UIManager.getString("InternalFrameTitlePane.minimizeButtonText");
  protected static final String RESTORE_CMD = UIManager.getString("InternalFrameTitlePane.restoreButtonText");
  protected static final String MAXIMIZE_CMD = UIManager.getString("InternalFrameTitlePane.maximizeButtonText");
  protected static final String MOVE_CMD = UIManager.getString("InternalFrameTitlePane.moveButtonText");
  protected static final String SIZE_CMD = UIManager.getString("InternalFrameTitlePane.sizeButtonText");
  private String closeButtonToolTip;
  private String iconButtonToolTip;
  private String restoreButtonToolTip;
  private String maxButtonToolTip;
  private Handler handler;
  
  public BasicInternalFrameTitlePane(JInternalFrame paramJInternalFrame)
  {
    this.frame = paramJInternalFrame;
    installTitlePane();
  }
  
  protected void installTitlePane()
  {
    installDefaults();
    installListeners();
    createActions();
    enableActions();
    createActionMap();
    setLayout(createLayout());
    assembleSystemMenu();
    createButtons();
    addSubComponents();
    updateProperties();
  }
  
  private void updateProperties()
  {
    Object localObject = this.frame.getClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY);
    putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, localObject);
  }
  
  protected void addSubComponents()
  {
    add(this.menuBar);
    add(this.iconButton);
    add(this.maxButton);
    add(this.closeButton);
  }
  
  protected void createActions()
  {
    this.maximizeAction = new MaximizeAction();
    this.iconifyAction = new IconifyAction();
    this.closeAction = new CloseAction();
    this.restoreAction = new RestoreAction();
    this.moveAction = new MoveAction();
    this.sizeAction = new SizeAction();
  }
  
  ActionMap createActionMap()
  {
    ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
    localActionMapUIResource.put("showSystemMenu", new ShowSystemMenuAction(true));
    localActionMapUIResource.put("hideSystemMenu", new ShowSystemMenuAction(false));
    return localActionMapUIResource;
  }
  
  protected void installListeners()
  {
    if (this.propertyChangeListener == null) {
      this.propertyChangeListener = createPropertyChangeListener();
    }
    this.frame.addPropertyChangeListener(this.propertyChangeListener);
  }
  
  protected void uninstallListeners()
  {
    this.frame.removePropertyChangeListener(this.propertyChangeListener);
    this.handler = null;
  }
  
  protected void installDefaults()
  {
    this.maxIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
    this.minIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
    this.iconIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
    this.closeIcon = UIManager.getIcon("InternalFrame.closeIcon");
    this.selectedTitleColor = UIManager.getColor("InternalFrame.activeTitleBackground");
    this.selectedTextColor = UIManager.getColor("InternalFrame.activeTitleForeground");
    this.notSelectedTitleColor = UIManager.getColor("InternalFrame.inactiveTitleBackground");
    this.notSelectedTextColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");
    setFont(UIManager.getFont("InternalFrame.titleFont"));
    this.closeButtonToolTip = UIManager.getString("InternalFrame.closeButtonToolTip");
    this.iconButtonToolTip = UIManager.getString("InternalFrame.iconButtonToolTip");
    this.restoreButtonToolTip = UIManager.getString("InternalFrame.restoreButtonToolTip");
    this.maxButtonToolTip = UIManager.getString("InternalFrame.maxButtonToolTip");
  }
  
  protected void uninstallDefaults() {}
  
  protected void createButtons()
  {
    this.iconButton = new NoFocusButton("InternalFrameTitlePane.iconifyButtonAccessibleName", "InternalFrameTitlePane.iconifyButtonOpacity");
    this.iconButton.addActionListener(this.iconifyAction);
    if ((this.iconButtonToolTip != null) && (this.iconButtonToolTip.length() != 0)) {
      this.iconButton.setToolTipText(this.iconButtonToolTip);
    }
    this.maxButton = new NoFocusButton("InternalFrameTitlePane.maximizeButtonAccessibleName", "InternalFrameTitlePane.maximizeButtonOpacity");
    this.maxButton.addActionListener(this.maximizeAction);
    this.closeButton = new NoFocusButton("InternalFrameTitlePane.closeButtonAccessibleName", "InternalFrameTitlePane.closeButtonOpacity");
    this.closeButton.addActionListener(this.closeAction);
    if ((this.closeButtonToolTip != null) && (this.closeButtonToolTip.length() != 0)) {
      this.closeButton.setToolTipText(this.closeButtonToolTip);
    }
    setButtonIcons();
  }
  
  protected void setButtonIcons()
  {
    if (this.frame.isIcon())
    {
      if (this.minIcon != null) {
        this.iconButton.setIcon(this.minIcon);
      }
      if ((this.restoreButtonToolTip != null) && (this.restoreButtonToolTip.length() != 0)) {
        this.iconButton.setToolTipText(this.restoreButtonToolTip);
      }
      if (this.maxIcon != null) {
        this.maxButton.setIcon(this.maxIcon);
      }
      if ((this.maxButtonToolTip != null) && (this.maxButtonToolTip.length() != 0)) {
        this.maxButton.setToolTipText(this.maxButtonToolTip);
      }
    }
    else if (this.frame.isMaximum())
    {
      if (this.iconIcon != null) {
        this.iconButton.setIcon(this.iconIcon);
      }
      if ((this.iconButtonToolTip != null) && (this.iconButtonToolTip.length() != 0)) {
        this.iconButton.setToolTipText(this.iconButtonToolTip);
      }
      if (this.minIcon != null) {
        this.maxButton.setIcon(this.minIcon);
      }
      if ((this.restoreButtonToolTip != null) && (this.restoreButtonToolTip.length() != 0)) {
        this.maxButton.setToolTipText(this.restoreButtonToolTip);
      }
    }
    else
    {
      if (this.iconIcon != null) {
        this.iconButton.setIcon(this.iconIcon);
      }
      if ((this.iconButtonToolTip != null) && (this.iconButtonToolTip.length() != 0)) {
        this.iconButton.setToolTipText(this.iconButtonToolTip);
      }
      if (this.maxIcon != null) {
        this.maxButton.setIcon(this.maxIcon);
      }
      if ((this.maxButtonToolTip != null) && (this.maxButtonToolTip.length() != 0)) {
        this.maxButton.setToolTipText(this.maxButtonToolTip);
      }
    }
    if (this.closeIcon != null) {
      this.closeButton.setIcon(this.closeIcon);
    }
  }
  
  protected void assembleSystemMenu()
  {
    this.menuBar = createSystemMenuBar();
    this.windowMenu = createSystemMenu();
    this.menuBar.add(this.windowMenu);
    addSystemMenuItems(this.windowMenu);
    enableActions();
  }
  
  protected void addSystemMenuItems(JMenu paramJMenu)
  {
    JMenuItem localJMenuItem = paramJMenu.add(this.restoreAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("restore"));
    localJMenuItem = paramJMenu.add(this.moveAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("move"));
    localJMenuItem = paramJMenu.add(this.sizeAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("size"));
    localJMenuItem = paramJMenu.add(this.iconifyAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("minimize"));
    localJMenuItem = paramJMenu.add(this.maximizeAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("maximize"));
    paramJMenu.add(new JSeparator());
    localJMenuItem = paramJMenu.add(this.closeAction);
    localJMenuItem.setMnemonic(getButtonMnemonic("close"));
  }
  
  private static int getButtonMnemonic(String paramString)
  {
    try
    {
      return Integer.parseInt(UIManager.getString("InternalFrameTitlePane." + paramString + "Button.mnemonic"));
    }
    catch (NumberFormatException localNumberFormatException) {}
    return -1;
  }
  
  protected JMenu createSystemMenu()
  {
    return new JMenu("    ");
  }
  
  protected JMenuBar createSystemMenuBar()
  {
    this.menuBar = new SystemMenuBar();
    this.menuBar.setBorderPainted(false);
    return this.menuBar;
  }
  
  protected void showSystemMenu()
  {
    this.windowMenu.doClick();
  }
  
  public void paintComponent(Graphics paramGraphics)
  {
    paintTitleBackground(paramGraphics);
    if (this.frame.getTitle() != null)
    {
      boolean bool = this.frame.isSelected();
      Font localFont = paramGraphics.getFont();
      paramGraphics.setFont(getFont());
      if (bool) {
        paramGraphics.setColor(this.selectedTextColor);
      } else {
        paramGraphics.setColor(this.notSelectedTextColor);
      }
      FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(this.frame, paramGraphics);
      int i = (getHeight() + localFontMetrics.getAscent() - localFontMetrics.getLeading() - localFontMetrics.getDescent()) / 2;
      Rectangle localRectangle = new Rectangle(0, 0, 0, 0);
      if (this.frame.isIconifiable()) {
        localRectangle = this.iconButton.getBounds();
      } else if (this.frame.isMaximizable()) {
        localRectangle = this.maxButton.getBounds();
      } else if (this.frame.isClosable()) {
        localRectangle = this.closeButton.getBounds();
      }
      String str = this.frame.getTitle();
      int j;
      if (BasicGraphicsUtils.isLeftToRight(this.frame))
      {
        if (localRectangle.x == 0) {
          localRectangle.x = (this.frame.getWidth() - this.frame.getInsets().right);
        }
        j = this.menuBar.getX() + this.menuBar.getWidth() + 2;
        int k = localRectangle.x - j - 3;
        str = getTitle(this.frame.getTitle(), localFontMetrics, k);
      }
      else
      {
        j = this.menuBar.getX() - 2 - SwingUtilities2.stringWidth(this.frame, localFontMetrics, str);
      }
      SwingUtilities2.drawString(this.frame, paramGraphics, str, j, i);
      paramGraphics.setFont(localFont);
    }
  }
  
  protected void paintTitleBackground(Graphics paramGraphics)
  {
    boolean bool = this.frame.isSelected();
    if (bool) {
      paramGraphics.setColor(this.selectedTitleColor);
    } else {
      paramGraphics.setColor(this.notSelectedTitleColor);
    }
    paramGraphics.fillRect(0, 0, getWidth(), getHeight());
  }
  
  protected String getTitle(String paramString, FontMetrics paramFontMetrics, int paramInt)
  {
    return SwingUtilities2.clipStringIfNecessary(this.frame, paramFontMetrics, paramString, paramInt);
  }
  
  protected void postClosingEvent(JInternalFrame paramJInternalFrame)
  {
    InternalFrameEvent localInternalFrameEvent = new InternalFrameEvent(paramJInternalFrame, 25550);
    try
    {
      Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(localInternalFrameEvent);
    }
    catch (SecurityException localSecurityException)
    {
      paramJInternalFrame.dispatchEvent(localInternalFrameEvent);
    }
  }
  
  protected void enableActions()
  {
    this.restoreAction.setEnabled((this.frame.isMaximum()) || (this.frame.isIcon()));
    this.maximizeAction.setEnabled(((this.frame.isMaximizable()) && (!this.frame.isMaximum()) && (!this.frame.isIcon())) || ((this.frame.isMaximizable()) && (this.frame.isIcon())));
    this.iconifyAction.setEnabled((this.frame.isIconifiable()) && (!this.frame.isIcon()));
    this.closeAction.setEnabled(this.frame.isClosable());
    this.sizeAction.setEnabled(false);
    this.moveAction.setEnabled(false);
  }
  
  private Handler getHandler()
  {
    if (this.handler == null) {
      this.handler = new Handler(null);
    }
    return this.handler;
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  protected LayoutManager createLayout()
  {
    return getHandler();
  }
  
  public class CloseAction
    extends AbstractAction
  {
    public CloseAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (BasicInternalFrameTitlePane.this.frame.isClosable()) {
        BasicInternalFrameTitlePane.this.frame.doDefaultCloseAction();
      }
    }
  }
  
  private class Handler
    implements LayoutManager, PropertyChangeListener
  {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (str == "selected")
      {
        BasicInternalFrameTitlePane.this.repaint();
        return;
      }
      if ((str == "icon") || (str == "maximum"))
      {
        BasicInternalFrameTitlePane.this.setButtonIcons();
        BasicInternalFrameTitlePane.this.enableActions();
        return;
      }
      if ("closable" == str)
      {
        if (paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
          BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.closeButton);
        } else {
          BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.closeButton);
        }
      }
      else if ("maximizable" == str)
      {
        if (paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
          BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.maxButton);
        } else {
          BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.maxButton);
        }
      }
      else if ("iconable" == str) {
        if (paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
          BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.iconButton);
        } else {
          BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.iconButton);
        }
      }
      BasicInternalFrameTitlePane.this.enableActions();
      BasicInternalFrameTitlePane.this.revalidate();
      BasicInternalFrameTitlePane.this.repaint();
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return minimumLayoutSize(paramContainer);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      int i = 22;
      if (BasicInternalFrameTitlePane.this.frame.isClosable()) {
        i += 19;
      }
      if (BasicInternalFrameTitlePane.this.frame.isMaximizable()) {
        i += 19;
      }
      if (BasicInternalFrameTitlePane.this.frame.isIconifiable()) {
        i += 19;
      }
      FontMetrics localFontMetrics = BasicInternalFrameTitlePane.this.frame.getFontMetrics(BasicInternalFrameTitlePane.this.getFont());
      String str = BasicInternalFrameTitlePane.this.frame.getTitle();
      int j = str != null ? SwingUtilities2.stringWidth(BasicInternalFrameTitlePane.this.frame, localFontMetrics, str) : 0;
      int k = str != null ? str.length() : 0;
      if (k > 3)
      {
        int m = SwingUtilities2.stringWidth(BasicInternalFrameTitlePane.this.frame, localFontMetrics, str.substring(0, 3) + "...");
        i += (j < m ? j : m);
      }
      else
      {
        i += j;
      }
      Icon localIcon = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
      int n = localFontMetrics.getHeight();
      n += 2;
      int i1 = 0;
      if (localIcon != null) {
        i1 = Math.min(localIcon.getIconHeight(), 16);
      }
      i1 += 2;
      int i2 = Math.max(n, i1);
      Dimension localDimension = new Dimension(i, i2);
      if (BasicInternalFrameTitlePane.this.getBorder() != null)
      {
        Insets localInsets = BasicInternalFrameTitlePane.this.getBorder().getBorderInsets(paramContainer);
        localDimension.height += localInsets.top + localInsets.bottom;
        localDimension.width += localInsets.left + localInsets.right;
      }
      return localDimension;
    }
    
    public void layoutContainer(Container paramContainer)
    {
      boolean bool = BasicGraphicsUtils.isLeftToRight(BasicInternalFrameTitlePane.this.frame);
      int i = BasicInternalFrameTitlePane.this.getWidth();
      int j = BasicInternalFrameTitlePane.this.getHeight();
      int m = BasicInternalFrameTitlePane.this.closeButton.getIcon().getIconHeight();
      Icon localIcon = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
      int n = 0;
      if (localIcon != null) {
        n = localIcon.getIconHeight();
      }
      int k = bool ? 2 : i - 16 - 2;
      BasicInternalFrameTitlePane.this.menuBar.setBounds(k, (j - n) / 2, 16, 16);
      k = bool ? i - 16 - 2 : 2;
      if (BasicInternalFrameTitlePane.this.frame.isClosable())
      {
        BasicInternalFrameTitlePane.this.closeButton.setBounds(k, (j - m) / 2, 16, 14);
        k += (bool ? -18 : 18);
      }
      if (BasicInternalFrameTitlePane.this.frame.isMaximizable())
      {
        BasicInternalFrameTitlePane.this.maxButton.setBounds(k, (j - m) / 2, 16, 14);
        k += (bool ? -18 : 18);
      }
      if (BasicInternalFrameTitlePane.this.frame.isIconifiable()) {
        BasicInternalFrameTitlePane.this.iconButton.setBounds(k, (j - m) / 2, 16, 14);
      }
    }
  }
  
  public class IconifyAction
    extends AbstractAction
  {
    public IconifyAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (BasicInternalFrameTitlePane.this.frame.isIconifiable()) {
        if (!BasicInternalFrameTitlePane.this.frame.isIcon()) {
          try
          {
            BasicInternalFrameTitlePane.this.frame.setIcon(true);
          }
          catch (PropertyVetoException localPropertyVetoException1) {}
        } else {
          try
          {
            BasicInternalFrameTitlePane.this.frame.setIcon(false);
          }
          catch (PropertyVetoException localPropertyVetoException2) {}
        }
      }
    }
  }
  
  public class MaximizeAction
    extends AbstractAction
  {
    public MaximizeAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (BasicInternalFrameTitlePane.this.frame.isMaximizable()) {
        if ((BasicInternalFrameTitlePane.this.frame.isMaximum()) && (BasicInternalFrameTitlePane.this.frame.isIcon())) {
          try
          {
            BasicInternalFrameTitlePane.this.frame.setIcon(false);
          }
          catch (PropertyVetoException localPropertyVetoException1) {}
        } else if (!BasicInternalFrameTitlePane.this.frame.isMaximum()) {
          try
          {
            BasicInternalFrameTitlePane.this.frame.setMaximum(true);
          }
          catch (PropertyVetoException localPropertyVetoException2) {}
        } else {
          try
          {
            BasicInternalFrameTitlePane.this.frame.setMaximum(false);
          }
          catch (PropertyVetoException localPropertyVetoException3) {}
        }
      }
    }
  }
  
  public class MoveAction
    extends AbstractAction
  {
    public MoveAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent) {}
  }
  
  private class NoFocusButton
    extends JButton
  {
    private String uiKey;
    
    public NoFocusButton(String paramString1, String paramString2)
    {
      setFocusPainted(false);
      setMargin(new Insets(0, 0, 0, 0));
      this.uiKey = paramString1;
      Object localObject = UIManager.get(paramString2);
      if ((localObject instanceof Boolean)) {
        setOpaque(((Boolean)localObject).booleanValue());
      }
    }
    
    public boolean isFocusTraversable()
    {
      return false;
    }
    
    public void requestFocus() {}
    
    public AccessibleContext getAccessibleContext()
    {
      AccessibleContext localAccessibleContext = super.getAccessibleContext();
      if (this.uiKey != null)
      {
        localAccessibleContext.setAccessibleName(UIManager.getString(this.uiKey));
        this.uiKey = null;
      }
      return localAccessibleContext;
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicInternalFrameTitlePane.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
  
  public class RestoreAction
    extends AbstractAction
  {
    public RestoreAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if ((BasicInternalFrameTitlePane.this.frame.isMaximizable()) && (BasicInternalFrameTitlePane.this.frame.isMaximum()) && (BasicInternalFrameTitlePane.this.frame.isIcon())) {
        try
        {
          BasicInternalFrameTitlePane.this.frame.setIcon(false);
        }
        catch (PropertyVetoException localPropertyVetoException1) {}
      } else if ((BasicInternalFrameTitlePane.this.frame.isMaximizable()) && (BasicInternalFrameTitlePane.this.frame.isMaximum())) {
        try
        {
          BasicInternalFrameTitlePane.this.frame.setMaximum(false);
        }
        catch (PropertyVetoException localPropertyVetoException2) {}
      } else if ((BasicInternalFrameTitlePane.this.frame.isIconifiable()) && (BasicInternalFrameTitlePane.this.frame.isIcon())) {
        try
        {
          BasicInternalFrameTitlePane.this.frame.setIcon(false);
        }
        catch (PropertyVetoException localPropertyVetoException3) {}
      }
    }
  }
  
  private class ShowSystemMenuAction
    extends AbstractAction
  {
    private boolean show;
    
    public ShowSystemMenuAction(boolean paramBoolean)
    {
      this.show = paramBoolean;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (this.show) {
        BasicInternalFrameTitlePane.this.windowMenu.doClick();
      } else {
        BasicInternalFrameTitlePane.this.windowMenu.setVisible(false);
      }
    }
  }
  
  public class SizeAction
    extends AbstractAction
  {
    public SizeAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent) {}
  }
  
  public class SystemMenuBar
    extends JMenuBar
  {
    public SystemMenuBar() {}
    
    public boolean isFocusTraversable()
    {
      return false;
    }
    
    public void requestFocus() {}
    
    public void paint(Graphics paramGraphics)
    {
      Icon localIcon = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
      if (localIcon == null) {
        localIcon = (Icon)DefaultLookup.get(BasicInternalFrameTitlePane.this.frame, BasicInternalFrameTitlePane.this.frame.getUI(), "InternalFrame.icon");
      }
      if (localIcon != null)
      {
        if (((localIcon instanceof ImageIcon)) && ((localIcon.getIconWidth() > 16) || (localIcon.getIconHeight() > 16)))
        {
          Image localImage = ((ImageIcon)localIcon).getImage();
          ((ImageIcon)localIcon).setImage(localImage.getScaledInstance(16, 16, 4));
        }
        localIcon.paintIcon(this, paramGraphics, 0, 0);
      }
    }
    
    public boolean isOpaque()
    {
      return true;
    }
  }
  
  public class TitlePaneLayout
    implements LayoutManager
  {
    public TitlePaneLayout() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent)
    {
      BasicInternalFrameTitlePane.this.getHandler().addLayoutComponent(paramString, paramComponent);
    }
    
    public void removeLayoutComponent(Component paramComponent)
    {
      BasicInternalFrameTitlePane.this.getHandler().removeLayoutComponent(paramComponent);
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return BasicInternalFrameTitlePane.this.getHandler().preferredLayoutSize(paramContainer);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return BasicInternalFrameTitlePane.this.getHandler().minimumLayoutSize(paramContainer);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      BasicInternalFrameTitlePane.this.getHandler().layoutContainer(paramContainer);
    }
  }
}
