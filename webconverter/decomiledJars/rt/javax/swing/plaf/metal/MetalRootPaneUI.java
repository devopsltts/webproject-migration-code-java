package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

public class MetalRootPaneUI
  extends BasicRootPaneUI
{
  private static final String[] borderKeys = { null, "RootPane.frameBorder", "RootPane.plainDialogBorder", "RootPane.informationDialogBorder", "RootPane.errorDialogBorder", "RootPane.colorChooserDialogBorder", "RootPane.fileChooserDialogBorder", "RootPane.questionDialogBorder", "RootPane.warningDialogBorder" };
  private static final int CORNER_DRAG_WIDTH = 16;
  private static final int BORDER_DRAG_THICKNESS = 5;
  private Window window;
  private JComponent titlePane;
  private MouseInputListener mouseInputListener;
  private LayoutManager layoutManager;
  private LayoutManager savedOldLayout;
  private JRootPane root;
  private Cursor lastCursor = Cursor.getPredefinedCursor(0);
  private static final int[] cursorMapping = { 6, 6, 8, 7, 7, 6, 0, 0, 0, 7, 10, 0, 0, 0, 11, 4, 0, 0, 0, 5, 4, 4, 9, 5, 5 };
  
  public MetalRootPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalRootPaneUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    this.root = ((JRootPane)paramJComponent);
    int i = this.root.getWindowDecorationStyle();
    if (i != 0) {
      installClientDecorations(this.root);
    }
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    super.uninstallUI(paramJComponent);
    uninstallClientDecorations(this.root);
    this.layoutManager = null;
    this.mouseInputListener = null;
    this.root = null;
  }
  
  void installBorder(JRootPane paramJRootPane)
  {
    int i = paramJRootPane.getWindowDecorationStyle();
    if (i == 0) {
      LookAndFeel.uninstallBorder(paramJRootPane);
    } else {
      LookAndFeel.installBorder(paramJRootPane, borderKeys[i]);
    }
  }
  
  private void uninstallBorder(JRootPane paramJRootPane)
  {
    LookAndFeel.uninstallBorder(paramJRootPane);
  }
  
  private void installWindowListeners(JRootPane paramJRootPane, Component paramComponent)
  {
    if ((paramComponent instanceof Window)) {
      this.window = ((Window)paramComponent);
    } else {
      this.window = SwingUtilities.getWindowAncestor(paramComponent);
    }
    if (this.window != null)
    {
      if (this.mouseInputListener == null) {
        this.mouseInputListener = createWindowMouseInputListener(paramJRootPane);
      }
      this.window.addMouseListener(this.mouseInputListener);
      this.window.addMouseMotionListener(this.mouseInputListener);
    }
  }
  
  private void uninstallWindowListeners(JRootPane paramJRootPane)
  {
    if (this.window != null)
    {
      this.window.removeMouseListener(this.mouseInputListener);
      this.window.removeMouseMotionListener(this.mouseInputListener);
    }
  }
  
  private void installLayout(JRootPane paramJRootPane)
  {
    if (this.layoutManager == null) {
      this.layoutManager = createLayoutManager();
    }
    this.savedOldLayout = paramJRootPane.getLayout();
    paramJRootPane.setLayout(this.layoutManager);
  }
  
  private void uninstallLayout(JRootPane paramJRootPane)
  {
    if (this.savedOldLayout != null)
    {
      paramJRootPane.setLayout(this.savedOldLayout);
      this.savedOldLayout = null;
    }
  }
  
  private void installClientDecorations(JRootPane paramJRootPane)
  {
    installBorder(paramJRootPane);
    JComponent localJComponent = createTitlePane(paramJRootPane);
    setTitlePane(paramJRootPane, localJComponent);
    installWindowListeners(paramJRootPane, paramJRootPane.getParent());
    installLayout(paramJRootPane);
    if (this.window != null)
    {
      paramJRootPane.revalidate();
      paramJRootPane.repaint();
    }
  }
  
  private void uninstallClientDecorations(JRootPane paramJRootPane)
  {
    uninstallBorder(paramJRootPane);
    uninstallWindowListeners(paramJRootPane);
    setTitlePane(paramJRootPane, null);
    uninstallLayout(paramJRootPane);
    int i = paramJRootPane.getWindowDecorationStyle();
    if (i == 0)
    {
      paramJRootPane.repaint();
      paramJRootPane.revalidate();
    }
    if (this.window != null) {
      this.window.setCursor(Cursor.getPredefinedCursor(0));
    }
    this.window = null;
  }
  
  private JComponent createTitlePane(JRootPane paramJRootPane)
  {
    return new MetalTitlePane(paramJRootPane, this);
  }
  
  private MouseInputListener createWindowMouseInputListener(JRootPane paramJRootPane)
  {
    return new MouseInputHandler(null);
  }
  
  private LayoutManager createLayoutManager()
  {
    return new MetalRootLayout(null);
  }
  
  private void setTitlePane(JRootPane paramJRootPane, JComponent paramJComponent)
  {
    JLayeredPane localJLayeredPane = paramJRootPane.getLayeredPane();
    JComponent localJComponent = getTitlePane();
    if (localJComponent != null)
    {
      localJComponent.setVisible(false);
      localJLayeredPane.remove(localJComponent);
    }
    if (paramJComponent != null)
    {
      localJLayeredPane.add(paramJComponent, JLayeredPane.FRAME_CONTENT_LAYER);
      paramJComponent.setVisible(true);
    }
    this.titlePane = paramJComponent;
  }
  
  private JComponent getTitlePane()
  {
    return this.titlePane;
  }
  
  private JRootPane getRootPane()
  {
    return this.root;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.propertyChange(paramPropertyChangeEvent);
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str == null) {
      return;
    }
    if (str.equals("windowDecorationStyle"))
    {
      JRootPane localJRootPane = (JRootPane)paramPropertyChangeEvent.getSource();
      int i = localJRootPane.getWindowDecorationStyle();
      uninstallClientDecorations(localJRootPane);
      if (i != 0) {
        installClientDecorations(localJRootPane);
      }
    }
    else if (str.equals("ancestor"))
    {
      uninstallWindowListeners(this.root);
      if (((JRootPane)paramPropertyChangeEvent.getSource()).getWindowDecorationStyle() != 0) {
        installWindowListeners(this.root, this.root.getParent());
      }
    }
  }
  
  private static class MetalRootLayout
    implements LayoutManager2
  {
    private MetalRootLayout() {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      Insets localInsets = paramContainer.getInsets();
      JRootPane localJRootPane = (JRootPane)paramContainer;
      Dimension localDimension1;
      if (localJRootPane.getContentPane() != null) {
        localDimension1 = localJRootPane.getContentPane().getPreferredSize();
      } else {
        localDimension1 = localJRootPane.getSize();
      }
      if (localDimension1 != null)
      {
        i = localDimension1.width;
        j = localDimension1.height;
      }
      if (localJRootPane.getMenuBar() != null)
      {
        Dimension localDimension2 = localJRootPane.getMenuBar().getPreferredSize();
        if (localDimension2 != null)
        {
          k = localDimension2.width;
          m = localDimension2.height;
        }
      }
      if ((localJRootPane.getWindowDecorationStyle() != 0) && ((localJRootPane.getUI() instanceof MetalRootPaneUI)))
      {
        JComponent localJComponent = ((MetalRootPaneUI)localJRootPane.getUI()).getTitlePane();
        if (localJComponent != null)
        {
          Dimension localDimension3 = localJComponent.getPreferredSize();
          if (localDimension3 != null)
          {
            n = localDimension3.width;
            i1 = localDimension3.height;
          }
        }
      }
      return new Dimension(Math.max(Math.max(i, k), n) + localInsets.left + localInsets.right, j + m + n + localInsets.top + localInsets.bottom);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      Insets localInsets = paramContainer.getInsets();
      JRootPane localJRootPane = (JRootPane)paramContainer;
      Dimension localDimension1;
      if (localJRootPane.getContentPane() != null) {
        localDimension1 = localJRootPane.getContentPane().getMinimumSize();
      } else {
        localDimension1 = localJRootPane.getSize();
      }
      if (localDimension1 != null)
      {
        i = localDimension1.width;
        j = localDimension1.height;
      }
      if (localJRootPane.getMenuBar() != null)
      {
        Dimension localDimension2 = localJRootPane.getMenuBar().getMinimumSize();
        if (localDimension2 != null)
        {
          k = localDimension2.width;
          m = localDimension2.height;
        }
      }
      if ((localJRootPane.getWindowDecorationStyle() != 0) && ((localJRootPane.getUI() instanceof MetalRootPaneUI)))
      {
        JComponent localJComponent = ((MetalRootPaneUI)localJRootPane.getUI()).getTitlePane();
        if (localJComponent != null)
        {
          Dimension localDimension3 = localJComponent.getMinimumSize();
          if (localDimension3 != null)
          {
            n = localDimension3.width;
            i1 = localDimension3.height;
          }
        }
      }
      return new Dimension(Math.max(Math.max(i, k), n) + localInsets.left + localInsets.right, j + m + n + localInsets.top + localInsets.bottom);
    }
    
    public Dimension maximumLayoutSize(Container paramContainer)
    {
      int i = Integer.MAX_VALUE;
      int j = Integer.MAX_VALUE;
      int k = Integer.MAX_VALUE;
      int m = Integer.MAX_VALUE;
      int n = Integer.MAX_VALUE;
      int i1 = Integer.MAX_VALUE;
      Insets localInsets = paramContainer.getInsets();
      JRootPane localJRootPane = (JRootPane)paramContainer;
      if (localJRootPane.getContentPane() != null)
      {
        Dimension localDimension1 = localJRootPane.getContentPane().getMaximumSize();
        if (localDimension1 != null)
        {
          i = localDimension1.width;
          j = localDimension1.height;
        }
      }
      if (localJRootPane.getMenuBar() != null)
      {
        Dimension localDimension2 = localJRootPane.getMenuBar().getMaximumSize();
        if (localDimension2 != null)
        {
          k = localDimension2.width;
          m = localDimension2.height;
        }
      }
      if ((localJRootPane.getWindowDecorationStyle() != 0) && ((localJRootPane.getUI() instanceof MetalRootPaneUI)))
      {
        JComponent localJComponent = ((MetalRootPaneUI)localJRootPane.getUI()).getTitlePane();
        if (localJComponent != null)
        {
          Dimension localDimension3 = localJComponent.getMaximumSize();
          if (localDimension3 != null)
          {
            n = localDimension3.width;
            i1 = localDimension3.height;
          }
        }
      }
      int i2 = Math.max(Math.max(j, m), i1);
      if (i2 != Integer.MAX_VALUE) {
        i2 = j + m + i1 + localInsets.top + localInsets.bottom;
      }
      int i3 = Math.max(Math.max(i, k), n);
      if (i3 != Integer.MAX_VALUE) {
        i3 += localInsets.left + localInsets.right;
      }
      return new Dimension(i3, i2);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      JRootPane localJRootPane = (JRootPane)paramContainer;
      Rectangle localRectangle = localJRootPane.getBounds();
      Insets localInsets = localJRootPane.getInsets();
      int i = 0;
      int j = localRectangle.width - localInsets.right - localInsets.left;
      int k = localRectangle.height - localInsets.top - localInsets.bottom;
      if (localJRootPane.getLayeredPane() != null) {
        localJRootPane.getLayeredPane().setBounds(localInsets.left, localInsets.top, j, k);
      }
      if (localJRootPane.getGlassPane() != null) {
        localJRootPane.getGlassPane().setBounds(localInsets.left, localInsets.top, j, k);
      }
      Object localObject;
      if ((localJRootPane.getWindowDecorationStyle() != 0) && ((localJRootPane.getUI() instanceof MetalRootPaneUI)))
      {
        localObject = ((MetalRootPaneUI)localJRootPane.getUI()).getTitlePane();
        if (localObject != null)
        {
          Dimension localDimension = ((JComponent)localObject).getPreferredSize();
          if (localDimension != null)
          {
            int m = localDimension.height;
            ((JComponent)localObject).setBounds(0, 0, j, m);
            i += m;
          }
        }
      }
      if (localJRootPane.getMenuBar() != null)
      {
        localObject = localJRootPane.getMenuBar().getPreferredSize();
        localJRootPane.getMenuBar().setBounds(0, i, j, ((Dimension)localObject).height);
        i += ((Dimension)localObject).height;
      }
      if (localJRootPane.getContentPane() != null)
      {
        localObject = localJRootPane.getContentPane().getPreferredSize();
        localJRootPane.getContentPane().setBounds(0, i, j, k < i ? 0 : k - i);
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
  
  private class MouseInputHandler
    implements MouseInputListener
  {
    private boolean isMovingWindow;
    private int dragCursor;
    private int dragOffsetX;
    private int dragOffsetY;
    private int dragWidth;
    private int dragHeight;
    
    private MouseInputHandler() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      JRootPane localJRootPane = MetalRootPaneUI.this.getRootPane();
      if (localJRootPane.getWindowDecorationStyle() == 0) {
        return;
      }
      Point localPoint1 = paramMouseEvent.getPoint();
      Window localWindow = (Window)paramMouseEvent.getSource();
      if (localWindow != null) {
        localWindow.toFront();
      }
      Point localPoint2 = SwingUtilities.convertPoint(localWindow, localPoint1, MetalRootPaneUI.this.getTitlePane());
      Frame localFrame = null;
      Dialog localDialog = null;
      if ((localWindow instanceof Frame)) {
        localFrame = (Frame)localWindow;
      } else if ((localWindow instanceof Dialog)) {
        localDialog = (Dialog)localWindow;
      }
      int i = localFrame != null ? localFrame.getExtendedState() : 0;
      if ((MetalRootPaneUI.this.getTitlePane() != null) && (MetalRootPaneUI.this.getTitlePane().contains(localPoint2)))
      {
        if (((localFrame != null) && ((i & 0x6) == 0)) || ((localDialog != null) && (localPoint1.y >= 5) && (localPoint1.x >= 5) && (localPoint1.x < localWindow.getWidth() - 5)))
        {
          this.isMovingWindow = true;
          this.dragOffsetX = localPoint1.x;
          this.dragOffsetY = localPoint1.y;
        }
      }
      else if (((localFrame != null) && (localFrame.isResizable()) && ((i & 0x6) == 0)) || ((localDialog != null) && (localDialog.isResizable())))
      {
        this.dragOffsetX = localPoint1.x;
        this.dragOffsetY = localPoint1.y;
        this.dragWidth = localWindow.getWidth();
        this.dragHeight = localWindow.getHeight();
        this.dragCursor = getCursor(calculateCorner(localWindow, localPoint1.x, localPoint1.y));
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if ((this.dragCursor != 0) && (MetalRootPaneUI.this.window != null) && (!MetalRootPaneUI.this.window.isValid()))
      {
        MetalRootPaneUI.this.window.validate();
        MetalRootPaneUI.this.getRootPane().repaint();
      }
      this.isMovingWindow = false;
      this.dragCursor = 0;
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      JRootPane localJRootPane = MetalRootPaneUI.this.getRootPane();
      if (localJRootPane.getWindowDecorationStyle() == 0) {
        return;
      }
      Window localWindow = (Window)paramMouseEvent.getSource();
      Frame localFrame = null;
      Dialog localDialog = null;
      if ((localWindow instanceof Frame)) {
        localFrame = (Frame)localWindow;
      } else if ((localWindow instanceof Dialog)) {
        localDialog = (Dialog)localWindow;
      }
      int i = getCursor(calculateCorner(localWindow, paramMouseEvent.getX(), paramMouseEvent.getY()));
      if ((i != 0) && (((localFrame != null) && (localFrame.isResizable()) && ((localFrame.getExtendedState() & 0x6) == 0)) || ((localDialog != null) && (localDialog.isResizable())))) {
        localWindow.setCursor(Cursor.getPredefinedCursor(i));
      } else {
        localWindow.setCursor(MetalRootPaneUI.this.lastCursor);
      }
    }
    
    private void adjust(Rectangle paramRectangle, Dimension paramDimension, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramRectangle.x += paramInt1;
      paramRectangle.y += paramInt2;
      paramRectangle.width += paramInt3;
      paramRectangle.height += paramInt4;
      if (paramDimension != null)
      {
        int i;
        if (paramRectangle.width < paramDimension.width)
        {
          i = paramDimension.width - paramRectangle.width;
          if (paramInt1 != 0) {
            paramRectangle.x -= i;
          }
          paramRectangle.width = paramDimension.width;
        }
        if (paramRectangle.height < paramDimension.height)
        {
          i = paramDimension.height - paramRectangle.height;
          if (paramInt2 != 0) {
            paramRectangle.y -= i;
          }
          paramRectangle.height = paramDimension.height;
        }
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      Window localWindow = (Window)paramMouseEvent.getSource();
      Point localPoint = paramMouseEvent.getPoint();
      Object localObject;
      if (this.isMovingWindow)
      {
        localObject = paramMouseEvent.getLocationOnScreen();
        localWindow.setLocation(((Point)localObject).x - this.dragOffsetX, ((Point)localObject).y - this.dragOffsetY);
      }
      else if (this.dragCursor != 0)
      {
        localObject = localWindow.getBounds();
        Rectangle localRectangle = new Rectangle((Rectangle)localObject);
        Dimension localDimension = localWindow.getMinimumSize();
        switch (this.dragCursor)
        {
        case 11: 
          adjust((Rectangle)localObject, localDimension, 0, 0, localPoint.x + (this.dragWidth - this.dragOffsetX) - ((Rectangle)localObject).width, 0);
          break;
        case 9: 
          adjust((Rectangle)localObject, localDimension, 0, 0, 0, localPoint.y + (this.dragHeight - this.dragOffsetY) - ((Rectangle)localObject).height);
          break;
        case 8: 
          adjust((Rectangle)localObject, localDimension, 0, localPoint.y - this.dragOffsetY, 0, -(localPoint.y - this.dragOffsetY));
          break;
        case 10: 
          adjust((Rectangle)localObject, localDimension, localPoint.x - this.dragOffsetX, 0, -(localPoint.x - this.dragOffsetX), 0);
          break;
        case 7: 
          adjust((Rectangle)localObject, localDimension, 0, localPoint.y - this.dragOffsetY, localPoint.x + (this.dragWidth - this.dragOffsetX) - ((Rectangle)localObject).width, -(localPoint.y - this.dragOffsetY));
          break;
        case 5: 
          adjust((Rectangle)localObject, localDimension, 0, 0, localPoint.x + (this.dragWidth - this.dragOffsetX) - ((Rectangle)localObject).width, localPoint.y + (this.dragHeight - this.dragOffsetY) - ((Rectangle)localObject).height);
          break;
        case 6: 
          adjust((Rectangle)localObject, localDimension, localPoint.x - this.dragOffsetX, localPoint.y - this.dragOffsetY, -(localPoint.x - this.dragOffsetX), -(localPoint.y - this.dragOffsetY));
          break;
        case 4: 
          adjust((Rectangle)localObject, localDimension, localPoint.x - this.dragOffsetX, 0, -(localPoint.x - this.dragOffsetX), localPoint.y + (this.dragHeight - this.dragOffsetY) - ((Rectangle)localObject).height);
          break;
        }
        if (!((Rectangle)localObject).equals(localRectangle))
        {
          localWindow.setBounds((Rectangle)localObject);
          if (Toolkit.getDefaultToolkit().isDynamicLayoutActive())
          {
            localWindow.validate();
            MetalRootPaneUI.this.getRootPane().repaint();
          }
        }
      }
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      Window localWindow = (Window)paramMouseEvent.getSource();
      MetalRootPaneUI.this.lastCursor = localWindow.getCursor();
      mouseMoved(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      Window localWindow = (Window)paramMouseEvent.getSource();
      localWindow.setCursor(MetalRootPaneUI.this.lastCursor);
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      Window localWindow = (Window)paramMouseEvent.getSource();
      Frame localFrame = null;
      if ((localWindow instanceof Frame)) {
        localFrame = (Frame)localWindow;
      } else {
        return;
      }
      Point localPoint = SwingUtilities.convertPoint(localWindow, paramMouseEvent.getPoint(), MetalRootPaneUI.this.getTitlePane());
      int i = localFrame.getExtendedState();
      if ((MetalRootPaneUI.this.getTitlePane() != null) && (MetalRootPaneUI.this.getTitlePane().contains(localPoint)) && (paramMouseEvent.getClickCount() % 2 == 0) && ((paramMouseEvent.getModifiers() & 0x10) != 0) && (localFrame.isResizable()))
      {
        if ((i & 0x6) != 0) {
          localFrame.setExtendedState(i & 0xFFFFFFF9);
        } else {
          localFrame.setExtendedState(i | 0x6);
        }
        return;
      }
    }
    
    private int calculateCorner(Window paramWindow, int paramInt1, int paramInt2)
    {
      Insets localInsets = paramWindow.getInsets();
      int i = calculatePosition(paramInt1 - localInsets.left, paramWindow.getWidth() - localInsets.left - localInsets.right);
      int j = calculatePosition(paramInt2 - localInsets.top, paramWindow.getHeight() - localInsets.top - localInsets.bottom);
      if ((i == -1) || (j == -1)) {
        return -1;
      }
      return j * 5 + i;
    }
    
    private int getCursor(int paramInt)
    {
      if (paramInt == -1) {
        return 0;
      }
      return MetalRootPaneUI.cursorMapping[paramInt];
    }
    
    private int calculatePosition(int paramInt1, int paramInt2)
    {
      if (paramInt1 < 5) {
        return 0;
      }
      if (paramInt1 < 16) {
        return 1;
      }
      if (paramInt1 >= paramInt2 - 5) {
        return 4;
      }
      if (paramInt1 >= paramInt2 - 16) {
        return 3;
      }
      return 2;
    }
  }
}
