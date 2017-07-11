package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopPaneUI;

public class JDesktopPane
  extends JLayeredPane
  implements Accessible
{
  private static final String uiClassID = "DesktopPaneUI";
  transient DesktopManager desktopManager;
  private transient JInternalFrame selectedFrame = null;
  public static final int LIVE_DRAG_MODE = 0;
  public static final int OUTLINE_DRAG_MODE = 1;
  private int dragMode = 0;
  private boolean dragModeSet = false;
  private transient List<JInternalFrame> framesCache;
  private boolean componentOrderCheckingEnabled = true;
  private boolean componentOrderChanged = false;
  
  public JDesktopPane()
  {
    setUIProperty("opaque", Boolean.TRUE);
    setFocusCycleRoot(true);
    setFocusTraversalPolicy(new LayoutFocusTraversalPolicy()
    {
      public Component getDefaultComponent(Container paramAnonymousContainer)
      {
        JInternalFrame[] arrayOfJInternalFrame1 = JDesktopPane.this.getAllFrames();
        Component localComponent = null;
        for (JInternalFrame localJInternalFrame : arrayOfJInternalFrame1)
        {
          localComponent = localJInternalFrame.getFocusTraversalPolicy().getDefaultComponent(localJInternalFrame);
          if (localComponent != null) {
            break;
          }
        }
        return localComponent;
      }
    });
    updateUI();
  }
  
  public DesktopPaneUI getUI()
  {
    return (DesktopPaneUI)this.ui;
  }
  
  public void setUI(DesktopPaneUI paramDesktopPaneUI)
  {
    super.setUI(paramDesktopPaneUI);
  }
  
  public void setDragMode(int paramInt)
  {
    int i = this.dragMode;
    this.dragMode = paramInt;
    firePropertyChange("dragMode", i, this.dragMode);
    this.dragModeSet = true;
  }
  
  public int getDragMode()
  {
    return this.dragMode;
  }
  
  public DesktopManager getDesktopManager()
  {
    return this.desktopManager;
  }
  
  public void setDesktopManager(DesktopManager paramDesktopManager)
  {
    DesktopManager localDesktopManager = this.desktopManager;
    this.desktopManager = paramDesktopManager;
    firePropertyChange("desktopManager", localDesktopManager, this.desktopManager);
  }
  
  public void updateUI()
  {
    setUI((DesktopPaneUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "DesktopPaneUI";
  }
  
  public JInternalFrame[] getAllFrames()
  {
    return (JInternalFrame[])getAllFrames(this).toArray(new JInternalFrame[0]);
  }
  
  private static Collection<JInternalFrame> getAllFrames(Container paramContainer)
  {
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    int j = paramContainer.getComponentCount();
    for (int i = 0; i < j; i++)
    {
      Component localComponent = paramContainer.getComponent(i);
      if ((localComponent instanceof JInternalFrame))
      {
        localLinkedHashSet.add((JInternalFrame)localComponent);
      }
      else if ((localComponent instanceof JInternalFrame.JDesktopIcon))
      {
        JInternalFrame localJInternalFrame = ((JInternalFrame.JDesktopIcon)localComponent).getInternalFrame();
        if (localJInternalFrame != null) {
          localLinkedHashSet.add(localJInternalFrame);
        }
      }
      else if ((localComponent instanceof Container))
      {
        localLinkedHashSet.addAll(getAllFrames((Container)localComponent));
      }
    }
    return localLinkedHashSet;
  }
  
  public JInternalFrame getSelectedFrame()
  {
    return this.selectedFrame;
  }
  
  public void setSelectedFrame(JInternalFrame paramJInternalFrame)
  {
    this.selectedFrame = paramJInternalFrame;
  }
  
  public JInternalFrame[] getAllFramesInLayer(int paramInt)
  {
    Collection localCollection = getAllFrames(this);
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext()) {
      if (((JInternalFrame)localIterator.next()).getLayer() != paramInt) {
        localIterator.remove();
      }
    }
    return (JInternalFrame[])localCollection.toArray(new JInternalFrame[0]);
  }
  
  private List<JInternalFrame> getFrames()
  {
    TreeSet localTreeSet = new TreeSet();
    for (int i = 0; i < getComponentCount(); i++)
    {
      Object localObject = getComponent(i);
      if ((localObject instanceof JInternalFrame))
      {
        localTreeSet.add(new ComponentPosition((JInternalFrame)localObject, getLayer((Component)localObject), i));
      }
      else if ((localObject instanceof JInternalFrame.JDesktopIcon))
      {
        localObject = ((JInternalFrame.JDesktopIcon)localObject).getInternalFrame();
        localTreeSet.add(new ComponentPosition((JInternalFrame)localObject, getLayer((Component)localObject), i));
      }
    }
    ArrayList localArrayList = new ArrayList(localTreeSet.size());
    Iterator localIterator = localTreeSet.iterator();
    while (localIterator.hasNext())
    {
      ComponentPosition localComponentPosition = (ComponentPosition)localIterator.next();
      localArrayList.add(localComponentPosition.component);
    }
    return localArrayList;
  }
  
  private JInternalFrame getNextFrame(JInternalFrame paramJInternalFrame, boolean paramBoolean)
  {
    verifyFramesCache();
    if (paramJInternalFrame == null) {
      return getTopInternalFrame();
    }
    int i = this.framesCache.indexOf(paramJInternalFrame);
    if ((i == -1) || (this.framesCache.size() == 1)) {
      return null;
    }
    if (paramBoolean)
    {
      i++;
      if (i == this.framesCache.size()) {
        i = 0;
      }
    }
    else
    {
      i--;
      if (i == -1) {
        i = this.framesCache.size() - 1;
      }
    }
    return (JInternalFrame)this.framesCache.get(i);
  }
  
  JInternalFrame getNextFrame(JInternalFrame paramJInternalFrame)
  {
    return getNextFrame(paramJInternalFrame, true);
  }
  
  private JInternalFrame getTopInternalFrame()
  {
    if (this.framesCache.size() == 0) {
      return null;
    }
    return (JInternalFrame)this.framesCache.get(0);
  }
  
  private void updateFramesCache()
  {
    this.framesCache = getFrames();
  }
  
  private void verifyFramesCache()
  {
    if (this.componentOrderChanged)
    {
      this.componentOrderChanged = false;
      updateFramesCache();
    }
  }
  
  public void remove(Component paramComponent)
  {
    super.remove(paramComponent);
    updateFramesCache();
  }
  
  public JInternalFrame selectFrame(boolean paramBoolean)
  {
    JInternalFrame localJInternalFrame1 = getSelectedFrame();
    JInternalFrame localJInternalFrame2 = getNextFrame(localJInternalFrame1, paramBoolean);
    if (localJInternalFrame2 == null) {
      return null;
    }
    setComponentOrderCheckingEnabled(false);
    if ((paramBoolean) && (localJInternalFrame1 != null)) {
      localJInternalFrame1.moveToBack();
    }
    try
    {
      localJInternalFrame2.setSelected(true);
    }
    catch (PropertyVetoException localPropertyVetoException) {}
    setComponentOrderCheckingEnabled(true);
    return localJInternalFrame2;
  }
  
  void setComponentOrderCheckingEnabled(boolean paramBoolean)
  {
    this.componentOrderCheckingEnabled = paramBoolean;
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    super.addImpl(paramComponent, paramObject, paramInt);
    if ((this.componentOrderCheckingEnabled) && (((paramComponent instanceof JInternalFrame)) || ((paramComponent instanceof JInternalFrame.JDesktopIcon)))) {
      this.componentOrderChanged = true;
    }
  }
  
  public void remove(int paramInt)
  {
    if (this.componentOrderCheckingEnabled)
    {
      Component localComponent = getComponent(paramInt);
      if (((localComponent instanceof JInternalFrame)) || ((localComponent instanceof JInternalFrame.JDesktopIcon))) {
        this.componentOrderChanged = true;
      }
    }
    super.remove(paramInt);
  }
  
  public void removeAll()
  {
    if (this.componentOrderCheckingEnabled)
    {
      int i = getComponentCount();
      for (int j = 0; j < i; j++)
      {
        Component localComponent = getComponent(j);
        if (((localComponent instanceof JInternalFrame)) || ((localComponent instanceof JInternalFrame.JDesktopIcon)))
        {
          this.componentOrderChanged = true;
          break;
        }
      }
    }
    super.removeAll();
  }
  
  public void setComponentZOrder(Component paramComponent, int paramInt)
  {
    super.setComponentZOrder(paramComponent, paramInt);
    if ((this.componentOrderCheckingEnabled) && (((paramComponent instanceof JInternalFrame)) || ((paramComponent instanceof JInternalFrame.JDesktopIcon)))) {
      this.componentOrderChanged = true;
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("DesktopPaneUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (this.ui != null)) {
        this.ui.installUI(this);
      }
    }
  }
  
  void setUIProperty(String paramString, Object paramObject)
  {
    if (paramString == "dragMode")
    {
      if (!this.dragModeSet)
      {
        setDragMode(((Integer)paramObject).intValue());
        this.dragModeSet = false;
      }
    }
    else {
      super.setUIProperty(paramString, paramObject);
    }
  }
  
  protected String paramString()
  {
    String str = this.desktopManager != null ? this.desktopManager.toString() : "";
    return super.paramString() + ",desktopManager=" + str;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleJDesktopPane();
    }
    return this.accessibleContext;
  }
  
  protected class AccessibleJDesktopPane
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJDesktopPane()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.DESKTOP_PANE;
    }
  }
  
  private static class ComponentPosition
    implements Comparable<ComponentPosition>
  {
    private final JInternalFrame component;
    private final int layer;
    private final int zOrder;
    
    ComponentPosition(JInternalFrame paramJInternalFrame, int paramInt1, int paramInt2)
    {
      this.component = paramJInternalFrame;
      this.layer = paramInt1;
      this.zOrder = paramInt2;
    }
    
    public int compareTo(ComponentPosition paramComponentPosition)
    {
      int i = paramComponentPosition.layer - this.layer;
      if (i == 0) {
        return this.zOrder - paramComponentPosition.zOrder;
      }
      return i;
    }
  }
}
