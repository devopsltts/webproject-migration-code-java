package com.sun.java.swing.plaf.windows;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import sun.awt.AppContext;
import sun.security.action.GetBooleanAction;
import sun.swing.UIClientPropertyKey;

class AnimationController
  implements ActionListener, PropertyChangeListener
{
  private static final boolean VISTA_ANIMATION_DISABLED = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("swing.disablevistaanimation"))).booleanValue();
  private static final Object ANIMATION_CONTROLLER_KEY = new StringBuilder("ANIMATION_CONTROLLER_KEY");
  private final Map<JComponent, Map<TMSchema.Part, AnimationState>> animationStateMap = new WeakHashMap();
  private final Timer timer = new Timer(33, this);
  
  private static synchronized AnimationController getAnimationController()
  {
    AppContext localAppContext = AppContext.getAppContext();
    Object localObject = localAppContext.get(ANIMATION_CONTROLLER_KEY);
    if (localObject == null)
    {
      localObject = new AnimationController();
      localAppContext.put(ANIMATION_CONTROLLER_KEY, localObject);
    }
    return (AnimationController)localObject;
  }
  
  private AnimationController()
  {
    this.timer.setRepeats(true);
    this.timer.setCoalesce(true);
    UIManager.addPropertyChangeListener(this);
  }
  
  private static void triggerAnimation(JComponent paramJComponent, TMSchema.Part paramPart, TMSchema.State paramState)
  {
    if (((paramJComponent instanceof JTabbedPane)) || (paramPart == TMSchema.Part.TP_BUTTON)) {
      return;
    }
    AnimationController localAnimationController = getAnimationController();
    TMSchema.State localState = localAnimationController.getState(paramJComponent, paramPart);
    if (localState != paramState)
    {
      localAnimationController.putState(paramJComponent, paramPart, paramState);
      if (paramState == TMSchema.State.DEFAULTED) {
        localState = TMSchema.State.HOT;
      }
      if (localState != null)
      {
        long l;
        if (paramState == TMSchema.State.DEFAULTED)
        {
          l = 1000L;
        }
        else
        {
          XPStyle localXPStyle = XPStyle.getXP();
          l = localXPStyle != null ? localXPStyle.getThemeTransitionDuration(paramJComponent, paramPart, normalizeState(localState), normalizeState(paramState), TMSchema.Prop.TRANSITIONDURATIONS) : 1000L;
        }
        localAnimationController.startAnimation(paramJComponent, paramPart, localState, paramState, l);
      }
    }
  }
  
  private static TMSchema.State normalizeState(TMSchema.State paramState)
  {
    TMSchema.State localState;
    switch (1.$SwitchMap$com$sun$java$swing$plaf$windows$TMSchema$State[paramState.ordinal()])
    {
    case 1: 
    case 2: 
    case 3: 
      localState = TMSchema.State.UPPRESSED;
      break;
    case 4: 
    case 5: 
    case 6: 
      localState = TMSchema.State.UPDISABLED;
      break;
    case 7: 
    case 8: 
    case 9: 
      localState = TMSchema.State.UPHOT;
      break;
    case 10: 
    case 11: 
    case 12: 
      localState = TMSchema.State.UPNORMAL;
      break;
    default: 
      localState = paramState;
    }
    return localState;
  }
  
  private synchronized TMSchema.State getState(JComponent paramJComponent, TMSchema.Part paramPart)
  {
    TMSchema.State localState = null;
    Object localObject = paramJComponent.getClientProperty(PartUIClientPropertyKey.getKey(paramPart));
    if ((localObject instanceof TMSchema.State)) {
      localState = (TMSchema.State)localObject;
    }
    return localState;
  }
  
  private synchronized void putState(JComponent paramJComponent, TMSchema.Part paramPart, TMSchema.State paramState)
  {
    paramJComponent.putClientProperty(PartUIClientPropertyKey.getKey(paramPart), paramState);
  }
  
  private synchronized void startAnimation(JComponent paramJComponent, TMSchema.Part paramPart, TMSchema.State paramState1, TMSchema.State paramState2, long paramLong)
  {
    boolean bool = false;
    if (paramState2 == TMSchema.State.DEFAULTED) {
      bool = true;
    }
    Object localObject = (Map)this.animationStateMap.get(paramJComponent);
    if (paramLong <= 0L)
    {
      if (localObject != null)
      {
        ((Map)localObject).remove(paramPart);
        if (((Map)localObject).size() == 0) {
          this.animationStateMap.remove(paramJComponent);
        }
      }
      return;
    }
    if (localObject == null)
    {
      localObject = new EnumMap(TMSchema.Part.class);
      this.animationStateMap.put(paramJComponent, localObject);
    }
    ((Map)localObject).put(paramPart, new AnimationState(paramState1, paramLong, bool));
    if (!this.timer.isRunning()) {
      this.timer.start();
    }
  }
  
  static void paintSkin(JComponent paramJComponent, XPStyle.Skin paramSkin, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState)
  {
    if (VISTA_ANIMATION_DISABLED)
    {
      paramSkin.paintSkinRaw(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
      return;
    }
    triggerAnimation(paramJComponent, paramSkin.part, paramState);
    AnimationController localAnimationController = getAnimationController();
    synchronized (localAnimationController)
    {
      AnimationState localAnimationState = null;
      Map localMap = (Map)localAnimationController.animationStateMap.get(paramJComponent);
      if (localMap != null) {
        localAnimationState = (AnimationState)localMap.get(paramSkin.part);
      }
      if (localAnimationState != null) {
        localAnimationState.paintSkin(paramSkin, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
      } else {
        paramSkin.paintSkinRaw(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
      }
    }
  }
  
  public synchronized void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (("lookAndFeel" == paramPropertyChangeEvent.getPropertyName()) && (!(paramPropertyChangeEvent.getNewValue() instanceof WindowsLookAndFeel))) {
      dispose();
    }
  }
  
  public synchronized void actionPerformed(ActionEvent paramActionEvent)
  {
    ArrayList localArrayList1 = null;
    ArrayList localArrayList2 = null;
    Iterator localIterator1 = this.animationStateMap.keySet().iterator();
    JComponent localJComponent;
    while (localIterator1.hasNext())
    {
      localJComponent = (JComponent)localIterator1.next();
      localJComponent.repaint();
      if (localArrayList2 != null) {
        localArrayList2.clear();
      }
      Map localMap = (Map)this.animationStateMap.get(localJComponent);
      if ((!localJComponent.isShowing()) || (localMap == null) || (localMap.size() == 0))
      {
        if (localArrayList1 == null) {
          localArrayList1 = new ArrayList();
        }
        localArrayList1.add(localJComponent);
      }
      else
      {
        Iterator localIterator2 = localMap.keySet().iterator();
        TMSchema.Part localPart;
        while (localIterator2.hasNext())
        {
          localPart = (TMSchema.Part)localIterator2.next();
          if (((AnimationState)localMap.get(localPart)).isDone())
          {
            if (localArrayList2 == null) {
              localArrayList2 = new ArrayList();
            }
            localArrayList2.add(localPart);
          }
        }
        if (localArrayList2 != null) {
          if (localArrayList2.size() == localMap.size())
          {
            if (localArrayList1 == null) {
              localArrayList1 = new ArrayList();
            }
            localArrayList1.add(localJComponent);
          }
          else
          {
            localIterator2 = localArrayList2.iterator();
            while (localIterator2.hasNext())
            {
              localPart = (TMSchema.Part)localIterator2.next();
              localMap.remove(localPart);
            }
          }
        }
      }
    }
    if (localArrayList1 != null)
    {
      localIterator1 = localArrayList1.iterator();
      while (localIterator1.hasNext())
      {
        localJComponent = (JComponent)localIterator1.next();
        this.animationStateMap.remove(localJComponent);
      }
    }
    if (this.animationStateMap.size() == 0) {
      this.timer.stop();
    }
  }
  
  private synchronized void dispose()
  {
    this.timer.stop();
    UIManager.removePropertyChangeListener(this);
    synchronized (AnimationController.class)
    {
      AppContext.getAppContext().put(ANIMATION_CONTROLLER_KEY, null);
    }
  }
  
  private static class AnimationState
  {
    private final TMSchema.State startState;
    private final long duration;
    private long startTime;
    private boolean isForward = true;
    private boolean isForwardAndReverse;
    private float progress;
    
    AnimationState(TMSchema.State paramState, long paramLong, boolean paramBoolean)
    {
      assert ((paramState != null) && (paramLong > 0L));
      assert (SwingUtilities.isEventDispatchThread());
      this.startState = paramState;
      this.duration = (paramLong * 1000000L);
      this.startTime = System.nanoTime();
      this.isForwardAndReverse = paramBoolean;
      this.progress = 0.0F;
    }
    
    private void updateProgress()
    {
      assert (SwingUtilities.isEventDispatchThread());
      if (isDone()) {
        return;
      }
      long l = System.nanoTime();
      this.progress = ((float)(l - this.startTime) / (float)this.duration);
      this.progress = Math.max(this.progress, 0.0F);
      if (this.progress >= 1.0F)
      {
        this.progress = 1.0F;
        if (this.isForwardAndReverse)
        {
          this.startTime = l;
          this.progress = 0.0F;
          this.isForward = (!this.isForward);
        }
      }
    }
    
    void paintSkin(XPStyle.Skin paramSkin, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState)
    {
      assert (SwingUtilities.isEventDispatchThread());
      updateProgress();
      if (!isDone())
      {
        Graphics2D localGraphics2D = (Graphics2D)paramGraphics.create();
        paramSkin.paintSkinRaw(localGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, this.startState);
        float f;
        if (this.isForward) {
          f = this.progress;
        } else {
          f = 1.0F - this.progress;
        }
        localGraphics2D.setComposite(AlphaComposite.SrcOver.derive(f));
        paramSkin.paintSkinRaw(localGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
        localGraphics2D.dispose();
      }
      else
      {
        paramSkin.paintSkinRaw(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
      }
    }
    
    boolean isDone()
    {
      assert (SwingUtilities.isEventDispatchThread());
      return this.progress >= 1.0F;
    }
  }
  
  private static class PartUIClientPropertyKey
    implements UIClientPropertyKey
  {
    private static final Map<TMSchema.Part, PartUIClientPropertyKey> map = new EnumMap(TMSchema.Part.class);
    private final TMSchema.Part part;
    
    static synchronized PartUIClientPropertyKey getKey(TMSchema.Part paramPart)
    {
      PartUIClientPropertyKey localPartUIClientPropertyKey = (PartUIClientPropertyKey)map.get(paramPart);
      if (localPartUIClientPropertyKey == null)
      {
        localPartUIClientPropertyKey = new PartUIClientPropertyKey(paramPart);
        map.put(paramPart, localPartUIClientPropertyKey);
      }
      return localPartUIClientPropertyKey;
    }
    
    private PartUIClientPropertyKey(TMSchema.Part paramPart)
    {
      this.part = paramPart;
    }
    
    public String toString()
    {
      return this.part.toString();
    }
  }
}
