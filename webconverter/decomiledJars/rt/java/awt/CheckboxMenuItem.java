package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxMenuItemPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.CheckboxMenuItemAccessor;

public class CheckboxMenuItem
  extends MenuItem
  implements ItemSelectable, Accessible
{
  boolean state = false;
  transient ItemListener itemListener;
  private static final String base = "chkmenuitem";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 6190621106981774043L;
  private int checkboxMenuItemSerializedDataVersion = 1;
  
  public CheckboxMenuItem()
    throws HeadlessException
  {
    this("", false);
  }
  
  public CheckboxMenuItem(String paramString)
    throws HeadlessException
  {
    this(paramString, false);
  }
  
  public CheckboxMenuItem(String paramString, boolean paramBoolean)
    throws HeadlessException
  {
    super(paramString);
    this.state = paramBoolean;
  }
  
  String constructComponentName()
  {
    synchronized (CheckboxMenuItem.class)
    {
      return "chkmenuitem" + nameCounter++;
    }
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if (this.peer == null) {
        this.peer = Toolkit.getDefaultToolkit().createCheckboxMenuItem(this);
      }
      super.addNotify();
    }
  }
  
  public boolean getState()
  {
    return this.state;
  }
  
  public synchronized void setState(boolean paramBoolean)
  {
    this.state = paramBoolean;
    CheckboxMenuItemPeer localCheckboxMenuItemPeer = (CheckboxMenuItemPeer)this.peer;
    if (localCheckboxMenuItemPeer != null) {
      localCheckboxMenuItemPeer.setState(paramBoolean);
    }
  }
  
  public synchronized Object[] getSelectedObjects()
  {
    if (this.state)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = this.label;
      return arrayOfObject;
    }
    return null;
  }
  
  public synchronized void addItemListener(ItemListener paramItemListener)
  {
    if (paramItemListener == null) {
      return;
    }
    this.itemListener = AWTEventMulticaster.add(this.itemListener, paramItemListener);
    this.newEventsOnly = true;
  }
  
  public synchronized void removeItemListener(ItemListener paramItemListener)
  {
    if (paramItemListener == null) {
      return;
    }
    this.itemListener = AWTEventMulticaster.remove(this.itemListener, paramItemListener);
  }
  
  public synchronized ItemListener[] getItemListeners()
  {
    return (ItemListener[])getListeners(ItemListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    ItemListener localItemListener = null;
    if (paramClass == ItemListener.class) {
      localItemListener = this.itemListener;
    } else {
      return super.getListeners(paramClass);
    }
    return AWTEventMulticaster.getListeners(localItemListener, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    if (paramAWTEvent.id == 701) {
      return ((this.eventMask & 0x200) != 0L) || (this.itemListener != null);
    }
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof ItemEvent))
    {
      processItemEvent((ItemEvent)paramAWTEvent);
      return;
    }
    super.processEvent(paramAWTEvent);
  }
  
  protected void processItemEvent(ItemEvent paramItemEvent)
  {
    ItemListener localItemListener = this.itemListener;
    if (localItemListener != null) {
      localItemListener.itemStateChanged(paramItemEvent);
    }
  }
  
  void doMenuEvent(long paramLong, int paramInt)
  {
    setState(!this.state);
    Toolkit.getEventQueue().postEvent(new ItemEvent(this, 701, getLabel(), this.state ? 1 : 2));
  }
  
  public String paramString()
  {
    return super.paramString() + ",state=" + this.state;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "itemL", this.itemListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    Object localObject;
    while (null != (localObject = paramObjectInputStream.readObject()))
    {
      String str = ((String)localObject).intern();
      if ("itemL" == str) {
        addItemListener((ItemListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
  }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleAWTCheckboxMenuItem();
    }
    return this.accessibleContext;
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setCheckboxMenuItemAccessor(new AWTAccessor.CheckboxMenuItemAccessor()
    {
      public boolean getState(CheckboxMenuItem paramAnonymousCheckboxMenuItem)
      {
        return paramAnonymousCheckboxMenuItem.state;
      }
    });
  }
  
  protected class AccessibleAWTCheckboxMenuItem
    extends MenuItem.AccessibleAWTMenuItem
    implements AccessibleAction, AccessibleValue
  {
    private static final long serialVersionUID = -1122642964303476L;
    
    protected AccessibleAWTCheckboxMenuItem()
    {
      super();
    }
    
    public AccessibleAction getAccessibleAction()
    {
      return this;
    }
    
    public AccessibleValue getAccessibleValue()
    {
      return this;
    }
    
    public int getAccessibleActionCount()
    {
      return 0;
    }
    
    public String getAccessibleActionDescription(int paramInt)
    {
      return null;
    }
    
    public boolean doAccessibleAction(int paramInt)
    {
      return false;
    }
    
    public Number getCurrentAccessibleValue()
    {
      return null;
    }
    
    public boolean setCurrentAccessibleValue(Number paramNumber)
    {
      return false;
    }
    
    public Number getMinimumAccessibleValue()
    {
      return null;
    }
    
    public Number getMaximumAccessibleValue()
    {
      return null;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.CHECK_BOX;
    }
  }
}
