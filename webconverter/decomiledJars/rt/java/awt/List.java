package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.ListPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class List
  extends Component
  implements ItemSelectable, Accessible
{
  Vector<String> items = new Vector();
  int rows = 0;
  boolean multipleMode = false;
  int[] selected = new int[0];
  int visibleIndex = -1;
  transient ActionListener actionListener;
  transient ItemListener itemListener;
  private static final String base = "list";
  private static int nameCounter = 0;
  private static final long serialVersionUID = -3304312411574666869L;
  static final int DEFAULT_VISIBLE_ROWS = 4;
  private int listSerializedDataVersion = 1;
  
  public List()
    throws HeadlessException
  {
    this(0, false);
  }
  
  public List(int paramInt)
    throws HeadlessException
  {
    this(paramInt, false);
  }
  
  public List(int paramInt, boolean paramBoolean)
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    this.rows = (paramInt != 0 ? paramInt : 4);
    this.multipleMode = paramBoolean;
  }
  
  String constructComponentName()
  {
    synchronized (List.class)
    {
      return "list" + nameCounter++;
    }
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if (this.peer == null) {
        this.peer = getToolkit().createList(this);
      }
      super.addNotify();
    }
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      ListPeer localListPeer = (ListPeer)this.peer;
      if (localListPeer != null) {
        this.selected = localListPeer.getSelectedIndexes();
      }
      super.removeNotify();
    }
  }
  
  public int getItemCount()
  {
    return countItems();
  }
  
  @Deprecated
  public int countItems()
  {
    return this.items.size();
  }
  
  public String getItem(int paramInt)
  {
    return getItemImpl(paramInt);
  }
  
  final String getItemImpl(int paramInt)
  {
    return (String)this.items.elementAt(paramInt);
  }
  
  public synchronized String[] getItems()
  {
    String[] arrayOfString = new String[this.items.size()];
    this.items.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public void add(String paramString)
  {
    addItem(paramString);
  }
  
  @Deprecated
  public void addItem(String paramString)
  {
    addItem(paramString, -1);
  }
  
  public void add(String paramString, int paramInt)
  {
    addItem(paramString, paramInt);
  }
  
  @Deprecated
  public synchronized void addItem(String paramString, int paramInt)
  {
    if ((paramInt < -1) || (paramInt >= this.items.size())) {
      paramInt = -1;
    }
    if (paramString == null) {
      paramString = "";
    }
    if (paramInt == -1) {
      this.items.addElement(paramString);
    } else {
      this.items.insertElementAt(paramString, paramInt);
    }
    ListPeer localListPeer = (ListPeer)this.peer;
    if (localListPeer != null) {
      localListPeer.add(paramString, paramInt);
    }
  }
  
  public synchronized void replaceItem(String paramString, int paramInt)
  {
    remove(paramInt);
    add(paramString, paramInt);
  }
  
  public void removeAll()
  {
    clear();
  }
  
  @Deprecated
  public synchronized void clear()
  {
    ListPeer localListPeer = (ListPeer)this.peer;
    if (localListPeer != null) {
      localListPeer.removeAll();
    }
    this.items = new Vector();
    this.selected = new int[0];
  }
  
  public synchronized void remove(String paramString)
  {
    int i = this.items.indexOf(paramString);
    if (i < 0) {
      throw new IllegalArgumentException("item " + paramString + " not found in list");
    }
    remove(i);
  }
  
  public void remove(int paramInt)
  {
    delItem(paramInt);
  }
  
  @Deprecated
  public void delItem(int paramInt)
  {
    delItems(paramInt, paramInt);
  }
  
  public synchronized int getSelectedIndex()
  {
    int[] arrayOfInt = getSelectedIndexes();
    return arrayOfInt.length == 1 ? arrayOfInt[0] : -1;
  }
  
  public synchronized int[] getSelectedIndexes()
  {
    ListPeer localListPeer = (ListPeer)this.peer;
    if (localListPeer != null) {
      this.selected = localListPeer.getSelectedIndexes();
    }
    return (int[])this.selected.clone();
  }
  
  public synchronized String getSelectedItem()
  {
    int i = getSelectedIndex();
    return i < 0 ? null : getItem(i);
  }
  
  public synchronized String[] getSelectedItems()
  {
    int[] arrayOfInt = getSelectedIndexes();
    String[] arrayOfString = new String[arrayOfInt.length];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfString[i] = getItem(arrayOfInt[i]);
    }
    return arrayOfString;
  }
  
  public Object[] getSelectedObjects()
  {
    return getSelectedItems();
  }
  
  public void select(int paramInt)
  {
    ListPeer localListPeer;
    do
    {
      localListPeer = (ListPeer)this.peer;
      if (localListPeer != null)
      {
        localListPeer.select(paramInt);
        return;
      }
      synchronized (this)
      {
        int i = 0;
        for (int j = 0; j < this.selected.length; j++) {
          if (this.selected[j] == paramInt)
          {
            i = 1;
            break;
          }
        }
        if (i == 0) {
          if (!this.multipleMode)
          {
            this.selected = new int[1];
            this.selected[0] = paramInt;
          }
          else
          {
            int[] arrayOfInt = new int[this.selected.length + 1];
            System.arraycopy(this.selected, 0, arrayOfInt, 0, this.selected.length);
            arrayOfInt[this.selected.length] = paramInt;
            this.selected = arrayOfInt;
          }
        }
      }
    } while (localListPeer != this.peer);
  }
  
  public synchronized void deselect(int paramInt)
  {
    ListPeer localListPeer = (ListPeer)this.peer;
    if ((localListPeer != null) && ((isMultipleMode()) || (getSelectedIndex() == paramInt))) {
      localListPeer.deselect(paramInt);
    }
    for (int i = 0; i < this.selected.length; i++) {
      if (this.selected[i] == paramInt)
      {
        int[] arrayOfInt = new int[this.selected.length - 1];
        System.arraycopy(this.selected, 0, arrayOfInt, 0, i);
        System.arraycopy(this.selected, i + 1, arrayOfInt, i, this.selected.length - (i + 1));
        this.selected = arrayOfInt;
        return;
      }
    }
  }
  
  public boolean isIndexSelected(int paramInt)
  {
    return isSelected(paramInt);
  }
  
  @Deprecated
  public boolean isSelected(int paramInt)
  {
    int[] arrayOfInt = getSelectedIndexes();
    for (int i = 0; i < arrayOfInt.length; i++) {
      if (arrayOfInt[i] == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  public int getRows()
  {
    return this.rows;
  }
  
  public boolean isMultipleMode()
  {
    return allowsMultipleSelections();
  }
  
  @Deprecated
  public boolean allowsMultipleSelections()
  {
    return this.multipleMode;
  }
  
  public void setMultipleMode(boolean paramBoolean)
  {
    setMultipleSelections(paramBoolean);
  }
  
  @Deprecated
  public synchronized void setMultipleSelections(boolean paramBoolean)
  {
    if (paramBoolean != this.multipleMode)
    {
      this.multipleMode = paramBoolean;
      ListPeer localListPeer = (ListPeer)this.peer;
      if (localListPeer != null) {
        localListPeer.setMultipleMode(paramBoolean);
      }
    }
  }
  
  public int getVisibleIndex()
  {
    return this.visibleIndex;
  }
  
  public synchronized void makeVisible(int paramInt)
  {
    this.visibleIndex = paramInt;
    ListPeer localListPeer = (ListPeer)this.peer;
    if (localListPeer != null) {
      localListPeer.makeVisible(paramInt);
    }
  }
  
  public Dimension getPreferredSize(int paramInt)
  {
    return preferredSize(paramInt);
  }
  
  @Deprecated
  public Dimension preferredSize(int paramInt)
  {
    synchronized (getTreeLock())
    {
      ListPeer localListPeer = (ListPeer)this.peer;
      return localListPeer != null ? localListPeer.getPreferredSize(paramInt) : super.preferredSize();
    }
  }
  
  public Dimension getPreferredSize()
  {
    return preferredSize();
  }
  
  @Deprecated
  public Dimension preferredSize()
  {
    synchronized (getTreeLock())
    {
      return this.rows > 0 ? preferredSize(this.rows) : super.preferredSize();
    }
  }
  
  public Dimension getMinimumSize(int paramInt)
  {
    return minimumSize(paramInt);
  }
  
  @Deprecated
  public Dimension minimumSize(int paramInt)
  {
    synchronized (getTreeLock())
    {
      ListPeer localListPeer = (ListPeer)this.peer;
      return localListPeer != null ? localListPeer.getMinimumSize(paramInt) : super.minimumSize();
    }
  }
  
  public Dimension getMinimumSize()
  {
    return minimumSize();
  }
  
  @Deprecated
  public Dimension minimumSize()
  {
    synchronized (getTreeLock())
    {
      return this.rows > 0 ? minimumSize(this.rows) : super.minimumSize();
    }
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
  
  public synchronized void addActionListener(ActionListener paramActionListener)
  {
    if (paramActionListener == null) {
      return;
    }
    this.actionListener = AWTEventMulticaster.add(this.actionListener, paramActionListener);
    this.newEventsOnly = true;
  }
  
  public synchronized void removeActionListener(ActionListener paramActionListener)
  {
    if (paramActionListener == null) {
      return;
    }
    this.actionListener = AWTEventMulticaster.remove(this.actionListener, paramActionListener);
  }
  
  public synchronized ActionListener[] getActionListeners()
  {
    return (ActionListener[])getListeners(ActionListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    Object localObject = null;
    if (paramClass == ActionListener.class) {
      localObject = this.actionListener;
    } else if (paramClass == ItemListener.class) {
      localObject = this.itemListener;
    } else {
      return super.getListeners(paramClass);
    }
    return AWTEventMulticaster.getListeners((EventListener)localObject, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    switch (paramAWTEvent.id)
    {
    case 1001: 
      return ((this.eventMask & 0x80) != 0L) || (this.actionListener != null);
    case 701: 
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
    if ((paramAWTEvent instanceof ActionEvent))
    {
      processActionEvent((ActionEvent)paramAWTEvent);
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
  
  protected void processActionEvent(ActionEvent paramActionEvent)
  {
    ActionListener localActionListener = this.actionListener;
    if (localActionListener != null) {
      localActionListener.actionPerformed(paramActionEvent);
    }
  }
  
  protected String paramString()
  {
    return super.paramString() + ",selected=" + getSelectedItem();
  }
  
  @Deprecated
  public synchronized void delItems(int paramInt1, int paramInt2)
  {
    for (int i = paramInt2; i >= paramInt1; i--) {
      this.items.removeElementAt(i);
    }
    ListPeer localListPeer = (ListPeer)this.peer;
    if (localListPeer != null) {
      localListPeer.delItems(paramInt1, paramInt2);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    synchronized (this)
    {
      ListPeer localListPeer = (ListPeer)this.peer;
      if (localListPeer != null) {
        this.selected = localListPeer.getSelectedIndexes();
      }
    }
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "itemL", this.itemListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "actionL", this.actionListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    paramObjectInputStream.defaultReadObject();
    Object localObject;
    while (null != (localObject = paramObjectInputStream.readObject()))
    {
      String str = ((String)localObject).intern();
      if ("itemL" == str) {
        addItemListener((ItemListener)paramObjectInputStream.readObject());
      } else if ("actionL" == str) {
        addActionListener((ActionListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleAWTList();
    }
    return this.accessibleContext;
  }
  
  protected class AccessibleAWTList
    extends Component.AccessibleAWTComponent
    implements AccessibleSelection, ItemListener, ActionListener
  {
    private static final long serialVersionUID = 7924617370136012829L;
    
    public AccessibleAWTList()
    {
      super();
      List.this.addActionListener(this);
      List.this.addItemListener(this);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent) {}
    
    public void itemStateChanged(ItemEvent paramItemEvent) {}
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (List.this.isMultipleMode()) {
        localAccessibleStateSet.add(AccessibleState.MULTISELECTABLE);
      }
      return localAccessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.LIST;
    }
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      return null;
    }
    
    public int getAccessibleChildrenCount()
    {
      return List.this.getItemCount();
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      synchronized (List.this)
      {
        if (paramInt >= List.this.getItemCount()) {
          return null;
        }
        return new AccessibleAWTListChild(List.this, paramInt);
      }
    }
    
    public AccessibleSelection getAccessibleSelection()
    {
      return this;
    }
    
    public int getAccessibleSelectionCount()
    {
      return List.this.getSelectedIndexes().length;
    }
    
    public Accessible getAccessibleSelection(int paramInt)
    {
      synchronized (List.this)
      {
        int i = getAccessibleSelectionCount();
        if ((paramInt < 0) || (paramInt >= i)) {
          return null;
        }
        return getAccessibleChild(List.this.getSelectedIndexes()[paramInt]);
      }
    }
    
    public boolean isAccessibleChildSelected(int paramInt)
    {
      return List.this.isIndexSelected(paramInt);
    }
    
    public void addAccessibleSelection(int paramInt)
    {
      List.this.select(paramInt);
    }
    
    public void removeAccessibleSelection(int paramInt)
    {
      List.this.deselect(paramInt);
    }
    
    public void clearAccessibleSelection()
    {
      synchronized (List.this)
      {
        int[] arrayOfInt = List.this.getSelectedIndexes();
        if (arrayOfInt == null) {
          return;
        }
        for (int i = arrayOfInt.length - 1; i >= 0; i--) {
          List.this.deselect(arrayOfInt[i]);
        }
      }
    }
    
    public void selectAllAccessibleSelection()
    {
      synchronized (List.this)
      {
        for (int i = List.this.getItemCount() - 1; i >= 0; i--) {
          List.this.select(i);
        }
      }
    }
    
    protected class AccessibleAWTListChild
      extends Component.AccessibleAWTComponent
      implements Accessible
    {
      private static final long serialVersionUID = 4412022926028300317L;
      private List parent;
      private int indexInParent;
      
      public AccessibleAWTListChild(List paramList, int paramInt)
      {
        super();
        this.parent = paramList;
        setAccessibleParent(paramList);
        this.indexInParent = paramInt;
      }
      
      public AccessibleContext getAccessibleContext()
      {
        return this;
      }
      
      public AccessibleRole getAccessibleRole()
      {
        return AccessibleRole.LIST_ITEM;
      }
      
      public AccessibleStateSet getAccessibleStateSet()
      {
        AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
        if (this.parent.isIndexSelected(this.indexInParent)) {
          localAccessibleStateSet.add(AccessibleState.SELECTED);
        }
        return localAccessibleStateSet;
      }
      
      public Locale getLocale()
      {
        return this.parent.getLocale();
      }
      
      public int getAccessibleIndexInParent()
      {
        return this.indexInParent;
      }
      
      public int getAccessibleChildrenCount()
      {
        return 0;
      }
      
      public Accessible getAccessibleChild(int paramInt)
      {
        return null;
      }
      
      public Color getBackground()
      {
        return this.parent.getBackground();
      }
      
      public void setBackground(Color paramColor)
      {
        this.parent.setBackground(paramColor);
      }
      
      public Color getForeground()
      {
        return this.parent.getForeground();
      }
      
      public void setForeground(Color paramColor)
      {
        this.parent.setForeground(paramColor);
      }
      
      public Cursor getCursor()
      {
        return this.parent.getCursor();
      }
      
      public void setCursor(Cursor paramCursor)
      {
        this.parent.setCursor(paramCursor);
      }
      
      public Font getFont()
      {
        return this.parent.getFont();
      }
      
      public void setFont(Font paramFont)
      {
        this.parent.setFont(paramFont);
      }
      
      public FontMetrics getFontMetrics(Font paramFont)
      {
        return this.parent.getFontMetrics(paramFont);
      }
      
      public boolean isEnabled()
      {
        return this.parent.isEnabled();
      }
      
      public void setEnabled(boolean paramBoolean)
      {
        this.parent.setEnabled(paramBoolean);
      }
      
      public boolean isVisible()
      {
        return false;
      }
      
      public void setVisible(boolean paramBoolean)
      {
        this.parent.setVisible(paramBoolean);
      }
      
      public boolean isShowing()
      {
        return false;
      }
      
      public boolean contains(Point paramPoint)
      {
        return false;
      }
      
      public Point getLocationOnScreen()
      {
        return null;
      }
      
      public Point getLocation()
      {
        return null;
      }
      
      public void setLocation(Point paramPoint) {}
      
      public Rectangle getBounds()
      {
        return null;
      }
      
      public void setBounds(Rectangle paramRectangle) {}
      
      public Dimension getSize()
      {
        return null;
      }
      
      public void setSize(Dimension paramDimension) {}
      
      public Accessible getAccessibleAt(Point paramPoint)
      {
        return null;
      }
      
      public boolean isFocusTraversable()
      {
        return false;
      }
      
      public void requestFocus() {}
      
      public void addFocusListener(FocusListener paramFocusListener) {}
      
      public void removeFocusListener(FocusListener paramFocusListener) {}
    }
  }
}
