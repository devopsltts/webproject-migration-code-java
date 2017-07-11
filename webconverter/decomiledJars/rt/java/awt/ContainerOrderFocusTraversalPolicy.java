package java.awt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class ContainerOrderFocusTraversalPolicy
  extends FocusTraversalPolicy
  implements Serializable
{
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.ContainerOrderFocusTraversalPolicy");
  private final int FORWARD_TRAVERSAL = 0;
  private final int BACKWARD_TRAVERSAL = 1;
  private static final long serialVersionUID = 486933713763926351L;
  private boolean implicitDownCycleTraversal = true;
  private transient Container cachedRoot;
  private transient List<Component> cachedCycle;
  
  public ContainerOrderFocusTraversalPolicy() {}
  
  private List<Component> getFocusTraversalCycle(Container paramContainer)
  {
    ArrayList localArrayList = new ArrayList();
    enumerateCycle(paramContainer, localArrayList);
    return localArrayList;
  }
  
  private int getComponentIndex(List<Component> paramList, Component paramComponent)
  {
    return paramList.indexOf(paramComponent);
  }
  
  private void enumerateCycle(Container paramContainer, List<Component> paramList)
  {
    if ((!paramContainer.isVisible()) || (!paramContainer.isDisplayable())) {
      return;
    }
    paramList.add(paramContainer);
    Component[] arrayOfComponent = paramContainer.getComponents();
    for (int i = 0; i < arrayOfComponent.length; i++)
    {
      Component localComponent = arrayOfComponent[i];
      if ((localComponent instanceof Container))
      {
        Container localContainer = (Container)localComponent;
        if ((!localContainer.isFocusCycleRoot()) && (!localContainer.isFocusTraversalPolicyProvider()))
        {
          enumerateCycle(localContainer, paramList);
          continue;
        }
      }
      paramList.add(localComponent);
    }
  }
  
  private Container getTopmostProvider(Container paramContainer, Component paramComponent)
  {
    Container localContainer1 = paramComponent.getParent();
    Container localContainer2 = null;
    while ((localContainer1 != paramContainer) && (localContainer1 != null))
    {
      if (localContainer1.isFocusTraversalPolicyProvider()) {
        localContainer2 = localContainer1;
      }
      localContainer1 = localContainer1.getParent();
    }
    if (localContainer1 == null) {
      return null;
    }
    return localContainer2;
  }
  
  private Component getComponentDownCycle(Component paramComponent, int paramInt)
  {
    Component localComponent = null;
    if ((paramComponent instanceof Container))
    {
      Container localContainer = (Container)paramComponent;
      if (localContainer.isFocusCycleRoot())
      {
        if (getImplicitDownCycleTraversal())
        {
          localComponent = localContainer.getFocusTraversalPolicy().getDefaultComponent(localContainer);
          if ((localComponent != null) && (log.isLoggable(PlatformLogger.Level.FINE))) {
            log.fine("### Transfered focus down-cycle to " + localComponent + " in the focus cycle root " + localContainer);
          }
        }
        else
        {
          return null;
        }
      }
      else if (localContainer.isFocusTraversalPolicyProvider())
      {
        localComponent = paramInt == 0 ? localContainer.getFocusTraversalPolicy().getDefaultComponent(localContainer) : localContainer.getFocusTraversalPolicy().getLastComponent(localContainer);
        if ((localComponent != null) && (log.isLoggable(PlatformLogger.Level.FINE))) {
          log.fine("### Transfered focus to " + localComponent + " in the FTP provider " + localContainer);
        }
      }
    }
    return localComponent;
  }
  
  public Component getComponentAfter(Container paramContainer, Component paramComponent)
  {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("### Searching in " + paramContainer + " for component after " + paramComponent);
    }
    if ((paramContainer == null) || (paramComponent == null)) {
      throw new IllegalArgumentException("aContainer and aComponent cannot be null");
    }
    if ((!paramContainer.isFocusTraversalPolicyProvider()) && (!paramContainer.isFocusCycleRoot())) {
      throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider");
    }
    if ((paramContainer.isFocusCycleRoot()) && (!paramComponent.isFocusCycleRoot(paramContainer))) {
      throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent");
    }
    synchronized (paramContainer.getTreeLock())
    {
      if ((!paramContainer.isVisible()) || (!paramContainer.isDisplayable())) {
        return null;
      }
      Component localComponent1 = getComponentDownCycle(paramComponent, 0);
      if (localComponent1 != null) {
        return localComponent1;
      }
      Container localContainer = getTopmostProvider(paramContainer, paramComponent);
      if (localContainer != null)
      {
        if (log.isLoggable(PlatformLogger.Level.FINE)) {
          log.fine("### Asking FTP " + localContainer + " for component after " + paramComponent);
        }
        localObject1 = localContainer.getFocusTraversalPolicy();
        Component localComponent2 = ((FocusTraversalPolicy)localObject1).getComponentAfter(localContainer, paramComponent);
        if (localComponent2 != null)
        {
          if (log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("### FTP returned " + localComponent2);
          }
          return localComponent2;
        }
        paramComponent = localContainer;
      }
      Object localObject1 = getFocusTraversalCycle(paramContainer);
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("### Cycle is " + localObject1 + ", component is " + paramComponent);
      }
      int i = getComponentIndex((List)localObject1, paramComponent);
      if (i < 0)
      {
        if (log.isLoggable(PlatformLogger.Level.FINE)) {
          log.fine("### Didn't find component " + paramComponent + " in a cycle " + paramContainer);
        }
        return getFirstComponent(paramContainer);
      }
      i++;
      while (i < ((List)localObject1).size())
      {
        localComponent1 = (Component)((List)localObject1).get(i);
        if (accept(localComponent1)) {
          return localComponent1;
        }
        if ((localComponent1 = getComponentDownCycle(localComponent1, 0)) != null) {
          return localComponent1;
        }
        i++;
      }
      if (paramContainer.isFocusCycleRoot())
      {
        this.cachedRoot = paramContainer;
        this.cachedCycle = ((List)localObject1);
        localComponent1 = getFirstComponent(paramContainer);
        this.cachedRoot = null;
        this.cachedCycle = null;
        return localComponent1;
      }
    }
    return null;
  }
  
  public Component getComponentBefore(Container paramContainer, Component paramComponent)
  {
    if ((paramContainer == null) || (paramComponent == null)) {
      throw new IllegalArgumentException("aContainer and aComponent cannot be null");
    }
    if ((!paramContainer.isFocusTraversalPolicyProvider()) && (!paramContainer.isFocusCycleRoot())) {
      throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider");
    }
    if ((paramContainer.isFocusCycleRoot()) && (!paramComponent.isFocusCycleRoot(paramContainer))) {
      throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent");
    }
    synchronized (paramContainer.getTreeLock())
    {
      if ((!paramContainer.isVisible()) || (!paramContainer.isDisplayable())) {
        return null;
      }
      Container localContainer = getTopmostProvider(paramContainer, paramComponent);
      if (localContainer != null)
      {
        if (log.isLoggable(PlatformLogger.Level.FINE)) {
          log.fine("### Asking FTP " + localContainer + " for component after " + paramComponent);
        }
        localObject1 = localContainer.getFocusTraversalPolicy();
        Component localComponent1 = ((FocusTraversalPolicy)localObject1).getComponentBefore(localContainer, paramComponent);
        if (localComponent1 != null)
        {
          if (log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("### FTP returned " + localComponent1);
          }
          return localComponent1;
        }
        paramComponent = localContainer;
        if (accept(paramComponent)) {
          return paramComponent;
        }
      }
      Object localObject1 = getFocusTraversalCycle(paramContainer);
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("### Cycle is " + localObject1 + ", component is " + paramComponent);
      }
      int i = getComponentIndex((List)localObject1, paramComponent);
      if (i < 0)
      {
        if (log.isLoggable(PlatformLogger.Level.FINE)) {
          log.fine("### Didn't find component " + paramComponent + " in a cycle " + paramContainer);
        }
        return getLastComponent(paramContainer);
      }
      Component localComponent2 = null;
      Component localComponent3 = null;
      i--;
      while (i >= 0)
      {
        localComponent2 = (Component)((List)localObject1).get(i);
        if ((localComponent2 != paramContainer) && ((localComponent3 = getComponentDownCycle(localComponent2, 1)) != null)) {
          return localComponent3;
        }
        if (accept(localComponent2)) {
          return localComponent2;
        }
        i--;
      }
      if (paramContainer.isFocusCycleRoot())
      {
        this.cachedRoot = paramContainer;
        this.cachedCycle = ((List)localObject1);
        localComponent2 = getLastComponent(paramContainer);
        this.cachedRoot = null;
        this.cachedCycle = null;
        return localComponent2;
      }
    }
    return null;
  }
  
  public Component getFirstComponent(Container paramContainer)
  {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("### Getting first component in " + paramContainer);
    }
    if (paramContainer == null) {
      throw new IllegalArgumentException("aContainer cannot be null");
    }
    synchronized (paramContainer.getTreeLock())
    {
      if ((!paramContainer.isVisible()) || (!paramContainer.isDisplayable())) {
        return null;
      }
      List localList;
      if (this.cachedRoot == paramContainer) {
        localList = this.cachedCycle;
      } else {
        localList = getFocusTraversalCycle(paramContainer);
      }
      if (localList.size() == 0)
      {
        if (log.isLoggable(PlatformLogger.Level.FINE)) {
          log.fine("### Cycle is empty");
        }
        return null;
      }
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("### Cycle is " + localList);
      }
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        Component localComponent = (Component)localIterator.next();
        if (accept(localComponent)) {
          return localComponent;
        }
        if ((localComponent != paramContainer) && ((localComponent = getComponentDownCycle(localComponent, 0)) != null)) {
          return localComponent;
        }
      }
    }
    return null;
  }
  
  public Component getLastComponent(Container paramContainer)
  {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("### Getting last component in " + paramContainer);
    }
    if (paramContainer == null) {
      throw new IllegalArgumentException("aContainer cannot be null");
    }
    synchronized (paramContainer.getTreeLock())
    {
      if ((!paramContainer.isVisible()) || (!paramContainer.isDisplayable())) {
        return null;
      }
      List localList;
      if (this.cachedRoot == paramContainer) {
        localList = this.cachedCycle;
      } else {
        localList = getFocusTraversalCycle(paramContainer);
      }
      if (localList.size() == 0)
      {
        if (log.isLoggable(PlatformLogger.Level.FINE)) {
          log.fine("### Cycle is empty");
        }
        return null;
      }
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("### Cycle is " + localList);
      }
      for (int i = localList.size() - 1; i >= 0; i--)
      {
        Component localComponent1 = (Component)localList.get(i);
        if (accept(localComponent1)) {
          return localComponent1;
        }
        if (((localComponent1 instanceof Container)) && (localComponent1 != paramContainer))
        {
          Container localContainer = (Container)localComponent1;
          if (localContainer.isFocusTraversalPolicyProvider())
          {
            Component localComponent2 = localContainer.getFocusTraversalPolicy().getLastComponent(localContainer);
            if (localComponent2 != null) {
              return localComponent2;
            }
          }
        }
      }
    }
    return null;
  }
  
  public Component getDefaultComponent(Container paramContainer)
  {
    return getFirstComponent(paramContainer);
  }
  
  public void setImplicitDownCycleTraversal(boolean paramBoolean)
  {
    this.implicitDownCycleTraversal = paramBoolean;
  }
  
  public boolean getImplicitDownCycleTraversal()
  {
    return this.implicitDownCycleTraversal;
  }
  
  protected boolean accept(Component paramComponent)
  {
    if (!paramComponent.canBeFocusOwner()) {
      return false;
    }
    if (!(paramComponent instanceof Window)) {
      for (Container localContainer = paramComponent.getParent(); localContainer != null; localContainer = localContainer.getParent())
      {
        if ((!localContainer.isEnabled()) && (!localContainer.isLightweight())) {
          return false;
        }
        if ((localContainer instanceof Window)) {
          break;
        }
      }
    }
    return true;
  }
}
