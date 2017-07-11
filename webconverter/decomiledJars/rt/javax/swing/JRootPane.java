package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.PrintStream;
import java.io.Serializable;
import java.security.AccessController;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.RootPaneUI;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.security.action.GetBooleanAction;

public class JRootPane
  extends JComponent
  implements Accessible
{
  private static final String uiClassID = "RootPaneUI";
  private static final boolean LOG_DISABLE_TRUE_DOUBLE_BUFFERING = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("swing.logDoubleBufferingDisable"))).booleanValue();
  private static final boolean IGNORE_DISABLE_TRUE_DOUBLE_BUFFERING = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("swing.ignoreDoubleBufferingDisable"))).booleanValue();
  public static final int NONE = 0;
  public static final int FRAME = 1;
  public static final int PLAIN_DIALOG = 2;
  public static final int INFORMATION_DIALOG = 3;
  public static final int ERROR_DIALOG = 4;
  public static final int COLOR_CHOOSER_DIALOG = 5;
  public static final int FILE_CHOOSER_DIALOG = 6;
  public static final int QUESTION_DIALOG = 7;
  public static final int WARNING_DIALOG = 8;
  private int windowDecorationStyle;
  protected JMenuBar menuBar;
  protected Container contentPane;
  protected JLayeredPane layeredPane;
  protected Component glassPane;
  protected JButton defaultButton;
  @Deprecated
  protected DefaultAction defaultPressAction;
  @Deprecated
  protected DefaultAction defaultReleaseAction;
  boolean useTrueDoubleBuffering = true;
  
  public JRootPane()
  {
    setGlassPane(createGlassPane());
    setLayeredPane(createLayeredPane());
    setContentPane(createContentPane());
    setLayout(createRootLayout());
    setDoubleBuffered(true);
    updateUI();
  }
  
  public void setDoubleBuffered(boolean paramBoolean)
  {
    if (isDoubleBuffered() != paramBoolean)
    {
      super.setDoubleBuffered(paramBoolean);
      RepaintManager.currentManager(this).doubleBufferingChanged(this);
    }
  }
  
  public int getWindowDecorationStyle()
  {
    return this.windowDecorationStyle;
  }
  
  public void setWindowDecorationStyle(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 8)) {
      throw new IllegalArgumentException("Invalid decoration style");
    }
    int i = getWindowDecorationStyle();
    this.windowDecorationStyle = paramInt;
    firePropertyChange("windowDecorationStyle", i, paramInt);
  }
  
  public RootPaneUI getUI()
  {
    return (RootPaneUI)this.ui;
  }
  
  public void setUI(RootPaneUI paramRootPaneUI)
  {
    super.setUI(paramRootPaneUI);
  }
  
  public void updateUI()
  {
    setUI((RootPaneUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "RootPaneUI";
  }
  
  protected JLayeredPane createLayeredPane()
  {
    JLayeredPane localJLayeredPane = new JLayeredPane();
    localJLayeredPane.setName(getName() + ".layeredPane");
    return localJLayeredPane;
  }
  
  protected Container createContentPane()
  {
    JPanel localJPanel = new JPanel();
    localJPanel.setName(getName() + ".contentPane");
    localJPanel.setLayout(new BorderLayout()
    {
      public void addLayoutComponent(Component paramAnonymousComponent, Object paramAnonymousObject)
      {
        if (paramAnonymousObject == null) {
          paramAnonymousObject = "Center";
        }
        super.addLayoutComponent(paramAnonymousComponent, paramAnonymousObject);
      }
    });
    return localJPanel;
  }
  
  protected Component createGlassPane()
  {
    JPanel localJPanel = new JPanel();
    localJPanel.setName(getName() + ".glassPane");
    localJPanel.setVisible(false);
    ((JPanel)localJPanel).setOpaque(false);
    return localJPanel;
  }
  
  protected LayoutManager createRootLayout()
  {
    return new RootLayout();
  }
  
  public void setJMenuBar(JMenuBar paramJMenuBar)
  {
    if ((this.menuBar != null) && (this.menuBar.getParent() == this.layeredPane)) {
      this.layeredPane.remove(this.menuBar);
    }
    this.menuBar = paramJMenuBar;
    if (this.menuBar != null) {
      this.layeredPane.add(this.menuBar, JLayeredPane.FRAME_CONTENT_LAYER);
    }
  }
  
  @Deprecated
  public void setMenuBar(JMenuBar paramJMenuBar)
  {
    if ((this.menuBar != null) && (this.menuBar.getParent() == this.layeredPane)) {
      this.layeredPane.remove(this.menuBar);
    }
    this.menuBar = paramJMenuBar;
    if (this.menuBar != null) {
      this.layeredPane.add(this.menuBar, JLayeredPane.FRAME_CONTENT_LAYER);
    }
  }
  
  public JMenuBar getJMenuBar()
  {
    return this.menuBar;
  }
  
  @Deprecated
  public JMenuBar getMenuBar()
  {
    return this.menuBar;
  }
  
  public void setContentPane(Container paramContainer)
  {
    if (paramContainer == null) {
      throw new IllegalComponentStateException("contentPane cannot be set to null.");
    }
    if ((this.contentPane != null) && (this.contentPane.getParent() == this.layeredPane)) {
      this.layeredPane.remove(this.contentPane);
    }
    this.contentPane = paramContainer;
    this.layeredPane.add(this.contentPane, JLayeredPane.FRAME_CONTENT_LAYER);
  }
  
  public Container getContentPane()
  {
    return this.contentPane;
  }
  
  public void setLayeredPane(JLayeredPane paramJLayeredPane)
  {
    if (paramJLayeredPane == null) {
      throw new IllegalComponentStateException("layeredPane cannot be set to null.");
    }
    if ((this.layeredPane != null) && (this.layeredPane.getParent() == this)) {
      remove(this.layeredPane);
    }
    this.layeredPane = paramJLayeredPane;
    add(this.layeredPane, -1);
  }
  
  public JLayeredPane getLayeredPane()
  {
    return this.layeredPane;
  }
  
  public void setGlassPane(Component paramComponent)
  {
    if (paramComponent == null) {
      throw new NullPointerException("glassPane cannot be set to null.");
    }
    AWTAccessor.getComponentAccessor().setMixingCutoutShape(paramComponent, new Rectangle());
    boolean bool = false;
    if ((this.glassPane != null) && (this.glassPane.getParent() == this))
    {
      remove(this.glassPane);
      bool = this.glassPane.isVisible();
    }
    paramComponent.setVisible(bool);
    this.glassPane = paramComponent;
    add(this.glassPane, 0);
    if (bool) {
      repaint();
    }
  }
  
  public Component getGlassPane()
  {
    return this.glassPane;
  }
  
  public boolean isValidateRoot()
  {
    return true;
  }
  
  public boolean isOptimizedDrawingEnabled()
  {
    return !this.glassPane.isVisible();
  }
  
  public void addNotify()
  {
    super.addNotify();
    enableEvents(8L);
  }
  
  public void removeNotify()
  {
    super.removeNotify();
  }
  
  public void setDefaultButton(JButton paramJButton)
  {
    JButton localJButton = this.defaultButton;
    if (localJButton != paramJButton)
    {
      this.defaultButton = paramJButton;
      if (localJButton != null) {
        localJButton.repaint();
      }
      if (paramJButton != null) {
        paramJButton.repaint();
      }
    }
    firePropertyChange("defaultButton", localJButton, paramJButton);
  }
  
  public JButton getDefaultButton()
  {
    return this.defaultButton;
  }
  
  final void setUseTrueDoubleBuffering(boolean paramBoolean)
  {
    this.useTrueDoubleBuffering = paramBoolean;
  }
  
  final boolean getUseTrueDoubleBuffering()
  {
    return this.useTrueDoubleBuffering;
  }
  
  final void disableTrueDoubleBuffering()
  {
    if ((this.useTrueDoubleBuffering) && (!IGNORE_DISABLE_TRUE_DOUBLE_BUFFERING))
    {
      if (LOG_DISABLE_TRUE_DOUBLE_BUFFERING)
      {
        System.out.println("Disabling true double buffering for " + this);
        Thread.dumpStack();
      }
      this.useTrueDoubleBuffering = false;
      RepaintManager.currentManager(this).doubleBufferingChanged(this);
    }
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    super.addImpl(paramComponent, paramObject, paramInt);
    if ((this.glassPane != null) && (this.glassPane.getParent() == this) && (getComponent(0) != this.glassPane)) {
      add(this.glassPane, 0);
    }
  }
  
  protected String paramString()
  {
    return super.paramString();
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleJRootPane();
    }
    return this.accessibleContext;
  }
  
  protected class AccessibleJRootPane
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJRootPane()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.ROOT_PANE;
    }
    
    public int getAccessibleChildrenCount()
    {
      return super.getAccessibleChildrenCount();
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      return super.getAccessibleChild(paramInt);
    }
  }
  
  static class DefaultAction
    extends AbstractAction
  {
    JButton owner;
    JRootPane root;
    boolean press;
    
    DefaultAction(JRootPane paramJRootPane, boolean paramBoolean)
    {
      this.root = paramJRootPane;
      this.press = paramBoolean;
    }
    
    public void setOwner(JButton paramJButton)
    {
      this.owner = paramJButton;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if ((this.owner != null) && (SwingUtilities.getRootPane(this.owner) == this.root))
      {
        ButtonModel localButtonModel = this.owner.getModel();
        if (this.press)
        {
          localButtonModel.setArmed(true);
          localButtonModel.setPressed(true);
        }
        else
        {
          localButtonModel.setPressed(false);
        }
      }
    }
    
    public boolean isEnabled()
    {
      return this.owner.getModel().isEnabled();
    }
  }
  
  protected class RootLayout
    implements LayoutManager2, Serializable
  {
    protected RootLayout() {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      Insets localInsets = JRootPane.this.getInsets();
      Dimension localDimension1;
      if (JRootPane.this.contentPane != null) {
        localDimension1 = JRootPane.this.contentPane.getPreferredSize();
      } else {
        localDimension1 = paramContainer.getSize();
      }
      Dimension localDimension2;
      if ((JRootPane.this.menuBar != null) && (JRootPane.this.menuBar.isVisible())) {
        localDimension2 = JRootPane.this.menuBar.getPreferredSize();
      } else {
        localDimension2 = new Dimension(0, 0);
      }
      return new Dimension(Math.max(localDimension1.width, localDimension2.width) + localInsets.left + localInsets.right, localDimension1.height + localDimension2.height + localInsets.top + localInsets.bottom);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      Insets localInsets = JRootPane.this.getInsets();
      Dimension localDimension1;
      if (JRootPane.this.contentPane != null) {
        localDimension1 = JRootPane.this.contentPane.getMinimumSize();
      } else {
        localDimension1 = paramContainer.getSize();
      }
      Dimension localDimension2;
      if ((JRootPane.this.menuBar != null) && (JRootPane.this.menuBar.isVisible())) {
        localDimension2 = JRootPane.this.menuBar.getMinimumSize();
      } else {
        localDimension2 = new Dimension(0, 0);
      }
      return new Dimension(Math.max(localDimension1.width, localDimension2.width) + localInsets.left + localInsets.right, localDimension1.height + localDimension2.height + localInsets.top + localInsets.bottom);
    }
    
    public Dimension maximumLayoutSize(Container paramContainer)
    {
      Insets localInsets = JRootPane.this.getInsets();
      Dimension localDimension2;
      if ((JRootPane.this.menuBar != null) && (JRootPane.this.menuBar.isVisible())) {
        localDimension2 = JRootPane.this.menuBar.getMaximumSize();
      } else {
        localDimension2 = new Dimension(0, 0);
      }
      Dimension localDimension1;
      if (JRootPane.this.contentPane != null) {
        localDimension1 = JRootPane.this.contentPane.getMaximumSize();
      } else {
        localDimension1 = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE - localInsets.top - localInsets.bottom - localDimension2.height - 1);
      }
      return new Dimension(Math.min(localDimension1.width, localDimension2.width) + localInsets.left + localInsets.right, localDimension1.height + localDimension2.height + localInsets.top + localInsets.bottom);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      Rectangle localRectangle = paramContainer.getBounds();
      Insets localInsets = JRootPane.this.getInsets();
      int i = 0;
      int j = localRectangle.width - localInsets.right - localInsets.left;
      int k = localRectangle.height - localInsets.top - localInsets.bottom;
      if (JRootPane.this.layeredPane != null) {
        JRootPane.this.layeredPane.setBounds(localInsets.left, localInsets.top, j, k);
      }
      if (JRootPane.this.glassPane != null) {
        JRootPane.this.glassPane.setBounds(localInsets.left, localInsets.top, j, k);
      }
      if ((JRootPane.this.menuBar != null) && (JRootPane.this.menuBar.isVisible()))
      {
        Dimension localDimension = JRootPane.this.menuBar.getPreferredSize();
        JRootPane.this.menuBar.setBounds(0, 0, j, localDimension.height);
        i += localDimension.height;
      }
      if (JRootPane.this.contentPane != null) {
        JRootPane.this.contentPane.setBounds(0, i, j, k - i);
      }
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public void addLayoutComponent(Component paramComponent, Object paramObject) {}
    
    public float getLayoutAlignmentX(Container paramContainer)
    {
      return 0.0F;
    }
    
    public float getLayoutAlignmentY(Container paramContainer)
    {
      return 0.0F;
    }
    
    public void invalidateLayout(Container paramContainer) {}
  }
}
