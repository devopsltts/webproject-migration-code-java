package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpInt;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

public abstract class SnmpMibTable
  extends SnmpMibNode
  implements NotificationBroadcaster, Serializable
{
  protected int nodeId = 1;
  protected SnmpMib theMib;
  protected boolean creationEnabled = false;
  protected SnmpTableEntryFactory factory = null;
  private int size = 0;
  private static final int Delta = 16;
  private int tablecount = 0;
  private int tablesize = 16;
  private SnmpOid[] tableoids = new SnmpOid[this.tablesize];
  private final Vector<Object> entries = new Vector();
  private final Vector<ObjectName> entrynames = new Vector();
  private Hashtable<NotificationListener, Vector<Object>> handbackTable = new Hashtable();
  private Hashtable<NotificationListener, Vector<NotificationFilter>> filterTable = new Hashtable();
  transient long sequenceNumber = 0L;
  
  public SnmpMibTable(SnmpMib paramSnmpMib)
  {
    this.theMib = paramSnmpMib;
    setCreationEnabled(false);
  }
  
  public abstract void createNewEntry(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException;
  
  public abstract boolean isRegistrationRequired();
  
  public boolean isCreationEnabled()
  {
    return this.creationEnabled;
  }
  
  public void setCreationEnabled(boolean paramBoolean)
  {
    this.creationEnabled = paramBoolean;
  }
  
  public boolean hasRowStatus()
  {
    return false;
  }
  
  public void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    boolean bool = paramSnmpMibSubRequest.isNewEntry();
    SnmpMibSubRequest localSnmpMibSubRequest = paramSnmpMibSubRequest;
    if (bool)
    {
      Enumeration localEnumeration = localSnmpMibSubRequest.getElements();
      while (localEnumeration.hasMoreElements())
      {
        localObject = (SnmpVarBind)localEnumeration.nextElement();
        localSnmpMibSubRequest.registerGetException((SnmpVarBind)localObject, new SnmpStatusException(224));
      }
    }
    Object localObject = localSnmpMibSubRequest.getEntryOid();
    get(paramSnmpMibSubRequest, (SnmpOid)localObject, paramInt + 1);
  }
  
  public void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    SnmpOid localSnmpOid = paramSnmpMibSubRequest.getEntryOid();
    int i = getRowAction(paramSnmpMibSubRequest, localSnmpOid, paramInt + 1);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "check", "Calling beginRowAction");
    }
    beginRowAction(paramSnmpMibSubRequest, localSnmpOid, paramInt + 1, i);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "check", "Calling check for " + paramSnmpMibSubRequest.getSize() + " varbinds");
    }
    check(paramSnmpMibSubRequest, localSnmpOid, paramInt + 1);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "check", "check finished");
    }
  }
  
  public void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "set", "Entering set");
    }
    SnmpOid localSnmpOid = paramSnmpMibSubRequest.getEntryOid();
    int i = getRowAction(paramSnmpMibSubRequest, localSnmpOid, paramInt + 1);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "set", "Calling set for " + paramSnmpMibSubRequest.getSize() + " varbinds");
    }
    set(paramSnmpMibSubRequest, localSnmpOid, paramInt + 1);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "set", "Calling endRowAction");
    }
    endRowAction(paramSnmpMibSubRequest, localSnmpOid, paramInt + 1, i);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "set", "RowAction finished");
    }
  }
  
  public void addEntry(SnmpOid paramSnmpOid, Object paramObject)
    throws SnmpStatusException
  {
    addEntry(paramSnmpOid, null, paramObject);
  }
  
  public synchronized void addEntry(SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject)
    throws SnmpStatusException
  {
    if ((isRegistrationRequired() == true) && (paramObjectName == null)) {
      throw new SnmpStatusException(3);
    }
    if (this.size == 0)
    {
      insertOid(0, paramSnmpOid);
      if (this.entries != null) {
        this.entries.addElement(paramObject);
      }
      if (this.entrynames != null) {
        this.entrynames.addElement(paramObjectName);
      }
      this.size += 1;
      if (this.factory != null) {
        try
        {
          this.factory.addEntryCb(0, paramSnmpOid, paramObjectName, paramObject, this);
        }
        catch (SnmpStatusException localSnmpStatusException1)
        {
          removeOid(0);
          if (this.entries != null) {
            this.entries.removeElementAt(0);
          }
          if (this.entrynames != null) {
            this.entrynames.removeElementAt(0);
          }
          throw localSnmpStatusException1;
        }
      }
      sendNotification("jmx.snmp.table.entry.added", new Date().getTime(), paramObject, paramObjectName);
      return;
    }
    int i = 0;
    i = getInsertionPoint(paramSnmpOid, true);
    if (i == this.size)
    {
      insertOid(this.tablecount, paramSnmpOid);
      if (this.entries != null) {
        this.entries.addElement(paramObject);
      }
      if (this.entrynames != null) {
        this.entrynames.addElement(paramObjectName);
      }
      this.size += 1;
    }
    else
    {
      try
      {
        insertOid(i, paramSnmpOid);
        if (this.entries != null) {
          this.entries.insertElementAt(paramObject, i);
        }
        if (this.entrynames != null) {
          this.entrynames.insertElementAt(paramObjectName, i);
        }
        this.size += 1;
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    }
    if (this.factory != null) {
      try
      {
        this.factory.addEntryCb(i, paramSnmpOid, paramObjectName, paramObject, this);
      }
      catch (SnmpStatusException localSnmpStatusException2)
      {
        removeOid(i);
        if (this.entries != null) {
          this.entries.removeElementAt(i);
        }
        if (this.entrynames != null) {
          this.entrynames.removeElementAt(i);
        }
        throw localSnmpStatusException2;
      }
    }
    sendNotification("jmx.snmp.table.entry.added", new Date().getTime(), paramObject, paramObjectName);
  }
  
  public synchronized void removeEntry(SnmpOid paramSnmpOid, Object paramObject)
    throws SnmpStatusException
  {
    int i = findObject(paramSnmpOid);
    if (i == -1) {
      return;
    }
    removeEntry(i, paramObject);
  }
  
  public void removeEntry(SnmpOid paramSnmpOid)
    throws SnmpStatusException
  {
    int i = findObject(paramSnmpOid);
    if (i == -1) {
      return;
    }
    removeEntry(i, null);
  }
  
  public synchronized void removeEntry(int paramInt, Object paramObject)
    throws SnmpStatusException
  {
    if (paramInt == -1) {
      return;
    }
    if (paramInt >= this.size) {
      return;
    }
    Object localObject = paramObject;
    if ((this.entries != null) && (this.entries.size() > paramInt))
    {
      localObject = this.entries.elementAt(paramInt);
      this.entries.removeElementAt(paramInt);
    }
    ObjectName localObjectName = null;
    if ((this.entrynames != null) && (this.entrynames.size() > paramInt))
    {
      localObjectName = (ObjectName)this.entrynames.elementAt(paramInt);
      this.entrynames.removeElementAt(paramInt);
    }
    SnmpOid localSnmpOid = this.tableoids[paramInt];
    removeOid(paramInt);
    this.size -= 1;
    if (localObject == null) {
      localObject = paramObject;
    }
    if (this.factory != null) {
      this.factory.removeEntryCb(paramInt, localSnmpOid, localObjectName, localObject, this);
    }
    sendNotification("jmx.snmp.table.entry.removed", new Date().getTime(), localObject, localObjectName);
  }
  
  public synchronized Object getEntry(SnmpOid paramSnmpOid)
    throws SnmpStatusException
  {
    int i = findObject(paramSnmpOid);
    if (i == -1) {
      throw new SnmpStatusException(224);
    }
    return this.entries.elementAt(i);
  }
  
  public synchronized ObjectName getEntryName(SnmpOid paramSnmpOid)
    throws SnmpStatusException
  {
    int i = findObject(paramSnmpOid);
    if (this.entrynames == null) {
      return null;
    }
    if ((i == -1) || (i >= this.entrynames.size())) {
      throw new SnmpStatusException(224);
    }
    return (ObjectName)this.entrynames.elementAt(i);
  }
  
  public Object[] getBasicEntries()
  {
    Object[] arrayOfObject = new Object[this.size];
    this.entries.copyInto(arrayOfObject);
    return arrayOfObject;
  }
  
  public int getSize()
  {
    return this.size;
  }
  
  public synchronized void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
  {
    if (paramNotificationListener == null) {
      throw new IllegalArgumentException("Listener can't be null");
    }
    Vector localVector1 = (Vector)this.handbackTable.get(paramNotificationListener);
    Vector localVector2 = (Vector)this.filterTable.get(paramNotificationListener);
    if (localVector1 == null)
    {
      localVector1 = new Vector();
      localVector2 = new Vector();
      this.handbackTable.put(paramNotificationListener, localVector1);
      this.filterTable.put(paramNotificationListener, localVector2);
    }
    localVector1.addElement(paramObject);
    localVector2.addElement(paramNotificationFilter);
  }
  
  public synchronized void removeNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException
  {
    Vector localVector = (Vector)this.handbackTable.get(paramNotificationListener);
    if (localVector == null) {
      throw new ListenerNotFoundException("listener");
    }
    this.handbackTable.remove(paramNotificationListener);
    this.filterTable.remove(paramNotificationListener);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    String[] arrayOfString = { "jmx.snmp.table.entry.added", "jmx.snmp.table.entry.removed" };
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = { new MBeanNotificationInfo(arrayOfString, "com.sun.jmx.snmp.agent.SnmpTableEntryNotification", "Notifications sent by the SnmpMibTable") };
    return arrayOfMBeanNotificationInfo;
  }
  
  public void registerEntryFactory(SnmpTableEntryFactory paramSnmpTableEntryFactory)
  {
    this.factory = paramSnmpTableEntryFactory;
  }
  
  protected boolean isRowStatus(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
  {
    return false;
  }
  
  protected int getRowAction(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException
  {
    boolean bool = paramSnmpMibSubRequest.isNewEntry();
    SnmpVarBind localSnmpVarBind = paramSnmpMibSubRequest.getRowStatusVarBind();
    if (localSnmpVarBind == null)
    {
      if ((bool) && (!hasRowStatus())) {
        return 4;
      }
      return 0;
    }
    try
    {
      return mapRowStatus(paramSnmpOid, localSnmpVarBind, paramSnmpMibSubRequest.getUserData());
    }
    catch (SnmpStatusException localSnmpStatusException)
    {
      checkRowStatusFail(paramSnmpMibSubRequest, localSnmpStatusException.getStatus());
    }
    return 0;
  }
  
  protected int mapRowStatus(SnmpOid paramSnmpOid, SnmpVarBind paramSnmpVarBind, Object paramObject)
    throws SnmpStatusException
  {
    SnmpValue localSnmpValue = paramSnmpVarBind.value;
    if ((localSnmpValue instanceof SnmpInt)) {
      return ((SnmpInt)localSnmpValue).intValue();
    }
    throw new SnmpStatusException(12);
  }
  
  protected SnmpValue setRowStatus(SnmpOid paramSnmpOid, int paramInt, Object paramObject)
    throws SnmpStatusException
  {
    return null;
  }
  
  protected boolean isRowReady(SnmpOid paramSnmpOid, Object paramObject)
    throws SnmpStatusException
  {
    return true;
  }
  
  protected void checkRowStatusChange(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt1, int paramInt2)
    throws SnmpStatusException
  {}
  
  protected void checkRemoveTableRow(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException
  {}
  
  protected void removeTableRow(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException
  {
    removeEntry(paramSnmpOid);
  }
  
  protected synchronized void beginRowAction(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt1, int paramInt2)
    throws SnmpStatusException
  {
    boolean bool = paramSnmpMibSubRequest.isNewEntry();
    SnmpOid localSnmpOid = paramSnmpOid;
    int i = paramInt2;
    switch (i)
    {
    case 0: 
      if (bool)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Failed to create row[" + paramSnmpOid + "] : RowStatus = unspecified");
        }
        checkRowStatusFail(paramSnmpMibSubRequest, 6);
      }
      break;
    case 4: 
    case 5: 
      if (bool)
      {
        if (isCreationEnabled())
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Creating row[" + paramSnmpOid + "] : RowStatus = createAndGo | createAndWait");
          }
          createNewEntry(paramSnmpMibSubRequest, localSnmpOid, paramInt1);
        }
        else
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Can't create row[" + paramSnmpOid + "] : RowStatus = createAndGo | createAndWait " + "but creation is disabled");
          }
          checkRowStatusFail(paramSnmpMibSubRequest, 6);
        }
      }
      else
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Can't create row[" + paramSnmpOid + "] : RowStatus = createAndGo | createAndWait " + "but row already exists");
        }
        checkRowStatusFail(paramSnmpMibSubRequest, 12);
      }
      break;
    case 6: 
      if (bool)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Warning: can't destroy row[" + paramSnmpOid + "] : RowStatus = destroy but row does not exist");
        }
      }
      else if (!isCreationEnabled())
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Can't destroy row[" + paramSnmpOid + "] : " + "RowStatus = destroy but creation is disabled");
        }
        checkRowStatusFail(paramSnmpMibSubRequest, 6);
      }
      checkRemoveTableRow(paramSnmpMibSubRequest, paramSnmpOid, paramInt1);
      break;
    case 1: 
    case 2: 
      if (bool)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Can't switch state of row[" + paramSnmpOid + "] : specified RowStatus = active | " + "notInService but row does not exist");
        }
        checkRowStatusFail(paramSnmpMibSubRequest, 12);
      }
      checkRowStatusChange(paramSnmpMibSubRequest, paramSnmpOid, paramInt1, i);
      break;
    case 3: 
    default: 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "beginRowAction", "Invalid RowStatus value for row[" + paramSnmpOid + "] : specified RowStatus = " + i);
      }
      checkRowStatusFail(paramSnmpMibSubRequest, 12);
    }
  }
  
  protected void endRowAction(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt1, int paramInt2)
    throws SnmpStatusException
  {
    boolean bool = paramSnmpMibSubRequest.isNewEntry();
    SnmpOid localSnmpOid = paramSnmpOid;
    int i = paramInt2;
    Object localObject = paramSnmpMibSubRequest.getUserData();
    SnmpValue localSnmpValue = null;
    switch (i)
    {
    case 0: 
      break;
    case 4: 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Setting RowStatus to 'active' for row[" + paramSnmpOid + "] : requested RowStatus = " + "createAndGo");
      }
      localSnmpValue = setRowStatus(localSnmpOid, 1, localObject);
      break;
    case 5: 
      if (isRowReady(localSnmpOid, localObject))
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Setting RowStatus to 'notInService' for row[" + paramSnmpOid + "] : requested RowStatus = createAndWait");
        }
        localSnmpValue = setRowStatus(localSnmpOid, 2, localObject);
      }
      else
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Setting RowStatus to 'notReady' for row[" + paramSnmpOid + "] : requested RowStatus = " + "createAndWait");
        }
        localSnmpValue = setRowStatus(localSnmpOid, 3, localObject);
      }
      break;
    case 6: 
      if (bool)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Warning: requested RowStatus = destroy, but row[" + paramSnmpOid + "] does not exist");
        }
      }
      else if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Destroying row[" + paramSnmpOid + "] : requested RowStatus = destroy");
      }
      removeTableRow(paramSnmpMibSubRequest, localSnmpOid, paramInt1);
      break;
    case 1: 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Setting RowStatus to 'active' for row[" + paramSnmpOid + "] : requested RowStatus = active");
      }
      localSnmpValue = setRowStatus(localSnmpOid, 1, localObject);
      break;
    case 2: 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Setting RowStatus to 'notInService' for row[" + paramSnmpOid + "] : requested RowStatus = notInService");
      }
      localSnmpValue = setRowStatus(localSnmpOid, 2, localObject);
      break;
    case 3: 
    default: 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMibTable.class.getName(), "endRowAction", "Invalid RowStatus value for row[" + paramSnmpOid + "] : specified RowStatus = " + i);
      }
      setRowStatusFail(paramSnmpMibSubRequest, 12);
    }
    if (localSnmpValue != null)
    {
      SnmpVarBind localSnmpVarBind = paramSnmpMibSubRequest.getRowStatusVarBind();
      if (localSnmpVarBind != null) {
        localSnmpVarBind.value = localSnmpValue;
      }
    }
  }
  
  protected long getNextVarEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject, int paramInt)
    throws SnmpStatusException
  {
    long l = paramLong;
    do
    {
      l = getNextVarEntryId(paramSnmpOid, l, paramObject);
    } while (skipEntryVariable(paramSnmpOid, l, paramObject, paramInt));
    return l;
  }
  
  protected boolean skipEntryVariable(SnmpOid paramSnmpOid, long paramLong, Object paramObject, int paramInt)
  {
    return false;
  }
  
  protected SnmpOid getNextOid(SnmpOid paramSnmpOid, Object paramObject)
    throws SnmpStatusException
  {
    if (this.size == 0) {
      throw new SnmpStatusException(224);
    }
    SnmpOid localSnmpOid1 = paramSnmpOid;
    SnmpOid localSnmpOid2 = this.tableoids[(this.tablecount - 1)];
    if (localSnmpOid2.equals(localSnmpOid1)) {
      throw new SnmpStatusException(224);
    }
    int i = getInsertionPoint(localSnmpOid1, false);
    if ((i > -1) && (i < this.size)) {
      try
      {
        localSnmpOid2 = this.tableoids[i];
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        throw new SnmpStatusException(224);
      }
    } else {
      throw new SnmpStatusException(224);
    }
    return localSnmpOid2;
  }
  
  protected SnmpOid getNextOid(Object paramObject)
    throws SnmpStatusException
  {
    if (this.size == 0) {
      throw new SnmpStatusException(224);
    }
    return this.tableoids[0];
  }
  
  protected abstract long getNextVarEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
    throws SnmpStatusException;
  
  protected abstract void validateVarEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
    throws SnmpStatusException;
  
  protected abstract boolean isReadableEntryId(SnmpOid paramSnmpOid, long paramLong, Object paramObject)
    throws SnmpStatusException;
  
  protected abstract void get(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException;
  
  protected abstract void check(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException;
  
  protected abstract void set(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt)
    throws SnmpStatusException;
  
  SnmpOid getNextOid(long[] paramArrayOfLong, int paramInt, Object paramObject)
    throws SnmpStatusException
  {
    SnmpEntryOid localSnmpEntryOid = new SnmpEntryOid(paramArrayOfLong, paramInt);
    return getNextOid(localSnmpEntryOid, paramObject);
  }
  
  static void checkRowStatusFail(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    SnmpVarBind localSnmpVarBind = paramSnmpMibSubRequest.getRowStatusVarBind();
    SnmpStatusException localSnmpStatusException = new SnmpStatusException(paramInt);
    paramSnmpMibSubRequest.registerCheckException(localSnmpVarBind, localSnmpStatusException);
  }
  
  static void setRowStatusFail(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    SnmpVarBind localSnmpVarBind = paramSnmpMibSubRequest.getRowStatusVarBind();
    SnmpStatusException localSnmpStatusException = new SnmpStatusException(paramInt);
    paramSnmpMibSubRequest.registerSetException(localSnmpVarBind, localSnmpStatusException);
  }
  
  final synchronized void findHandlingNode(SnmpVarBind paramSnmpVarBind, long[] paramArrayOfLong, int paramInt, SnmpRequestTree paramSnmpRequestTree)
    throws SnmpStatusException
  {
    int i = paramArrayOfLong.length;
    if (paramSnmpRequestTree == null) {
      throw new SnmpStatusException(5);
    }
    if (paramInt >= i) {
      throw new SnmpStatusException(6);
    }
    if (paramArrayOfLong[paramInt] != this.nodeId) {
      throw new SnmpStatusException(6);
    }
    if (paramInt + 2 >= i) {
      throw new SnmpStatusException(6);
    }
    SnmpEntryOid localSnmpEntryOid = new SnmpEntryOid(paramArrayOfLong, paramInt + 2);
    Object localObject = paramSnmpRequestTree.getUserData();
    boolean bool = contains(localSnmpEntryOid, localObject);
    if (!bool)
    {
      if (!paramSnmpRequestTree.isCreationAllowed()) {
        throw new SnmpStatusException(224);
      }
      if (!isCreationEnabled()) {
        throw new SnmpStatusException(6);
      }
    }
    long l = paramArrayOfLong[(paramInt + 1)];
    if (bool) {
      validateVarEntryId(localSnmpEntryOid, l, localObject);
    }
    if ((paramSnmpRequestTree.isSetRequest()) && (isRowStatus(localSnmpEntryOid, l, localObject))) {
      paramSnmpRequestTree.add(this, paramInt, localSnmpEntryOid, paramSnmpVarBind, !bool, paramSnmpVarBind);
    } else {
      paramSnmpRequestTree.add(this, paramInt, localSnmpEntryOid, paramSnmpVarBind, !bool);
    }
  }
  
  final synchronized long[] findNextHandlingNode(SnmpVarBind paramSnmpVarBind, long[] paramArrayOfLong, int paramInt1, int paramInt2, SnmpRequestTree paramSnmpRequestTree, AcmChecker paramAcmChecker)
    throws SnmpStatusException
  {
    int i = paramArrayOfLong.length;
    if (paramSnmpRequestTree == null) {
      throw new SnmpStatusException(225);
    }
    Object localObject = paramSnmpRequestTree.getUserData();
    int j = paramSnmpRequestTree.getRequestPduVersion();
    long l = -1L;
    if (paramInt1 >= i)
    {
      paramArrayOfLong = new long[1];
      paramArrayOfLong[0] = this.nodeId;
      paramInt1 = 0;
      i = 1;
    }
    else
    {
      if (paramArrayOfLong[paramInt1] > this.nodeId) {
        throw new SnmpStatusException(225);
      }
      if (paramArrayOfLong[paramInt1] < this.nodeId)
      {
        paramArrayOfLong = new long[1];
        paramArrayOfLong[0] = this.nodeId;
        paramInt1 = 0;
        i = 0;
      }
      else if (paramInt1 + 1 < i)
      {
        l = paramArrayOfLong[(paramInt1 + 1)];
      }
    }
    SnmpOid localSnmpOid;
    if (paramInt1 == i - 1)
    {
      localSnmpOid = getNextOid(localObject);
      l = getNextVarEntryId(localSnmpOid, l, localObject, j);
    }
    else if (paramInt1 == i - 2)
    {
      localSnmpOid = getNextOid(localObject);
      if (skipEntryVariable(localSnmpOid, l, localObject, j)) {
        l = getNextVarEntryId(localSnmpOid, l, localObject, j);
      }
    }
    else
    {
      try
      {
        localSnmpOid = getNextOid(paramArrayOfLong, paramInt1 + 2, localObject);
        if (skipEntryVariable(localSnmpOid, l, localObject, j)) {
          throw new SnmpStatusException(225);
        }
      }
      catch (SnmpStatusException localSnmpStatusException)
      {
        localSnmpOid = getNextOid(localObject);
        l = getNextVarEntryId(localSnmpOid, l, localObject, j);
      }
    }
    return findNextAccessibleOid(localSnmpOid, paramSnmpVarBind, paramArrayOfLong, paramInt2, paramSnmpRequestTree, paramAcmChecker, localObject, l);
  }
  
  private long[] findNextAccessibleOid(SnmpOid paramSnmpOid, SnmpVarBind paramSnmpVarBind, long[] paramArrayOfLong, int paramInt, SnmpRequestTree paramSnmpRequestTree, AcmChecker paramAcmChecker, Object paramObject, long paramLong)
    throws SnmpStatusException
  {
    int i = paramSnmpRequestTree.getRequestPduVersion();
    do
    {
      if ((paramSnmpOid == null) || (paramLong == -1L)) {
        throw new SnmpStatusException(225);
      }
      try
      {
        if (!isReadableEntryId(paramSnmpOid, paramLong, paramObject)) {
          throw new SnmpStatusException(225);
        }
        long[] arrayOfLong1 = paramSnmpOid.longValue(false);
        int j = arrayOfLong1.length;
        long[] arrayOfLong2 = new long[paramInt + 2 + j];
        arrayOfLong2[0] = -1L;
        System.arraycopy(arrayOfLong1, 0, arrayOfLong2, paramInt + 2, j);
        arrayOfLong2[paramInt] = this.nodeId;
        arrayOfLong2[(paramInt + 1)] = paramLong;
        paramAcmChecker.add(paramInt, arrayOfLong2, paramInt, j + 2);
        try
        {
          paramAcmChecker.checkCurrentOid();
          paramSnmpRequestTree.add(this, paramInt, paramSnmpOid, paramSnmpVarBind, false);
          long[] arrayOfLong3 = arrayOfLong2;
          return arrayOfLong3;
        }
        catch (SnmpStatusException localSnmpStatusException2)
        {
          paramSnmpOid = getNextOid(paramSnmpOid, paramObject);
        }
        finally
        {
          paramAcmChecker.remove(paramInt, j + 2);
        }
      }
      catch (SnmpStatusException localSnmpStatusException1)
      {
        paramSnmpOid = getNextOid(paramObject);
        paramLong = getNextVarEntryId(paramSnmpOid, paramLong, paramObject, i);
      }
    } while ((paramSnmpOid != null) && (paramLong != -1L));
    throw new SnmpStatusException(225);
  }
  
  final void validateOid(long[] paramArrayOfLong, int paramInt)
    throws SnmpStatusException
  {
    int i = paramArrayOfLong.length;
    if (paramInt + 2 >= i) {
      throw new SnmpStatusException(224);
    }
    if (paramArrayOfLong[paramInt] != this.nodeId) {
      throw new SnmpStatusException(225);
    }
  }
  
  private synchronized void sendNotification(Notification paramNotification)
  {
    Enumeration localEnumeration1 = this.handbackTable.keys();
    while (localEnumeration1.hasMoreElements())
    {
      NotificationListener localNotificationListener = (NotificationListener)localEnumeration1.nextElement();
      Vector localVector1 = (Vector)this.handbackTable.get(localNotificationListener);
      Vector localVector2 = (Vector)this.filterTable.get(localNotificationListener);
      Enumeration localEnumeration2 = localVector2.elements();
      Enumeration localEnumeration3 = localVector1.elements();
      while (localEnumeration3.hasMoreElements())
      {
        Object localObject = localEnumeration3.nextElement();
        NotificationFilter localNotificationFilter = (NotificationFilter)localEnumeration2.nextElement();
        if ((localNotificationFilter == null) || (localNotificationFilter.isNotificationEnabled(paramNotification))) {
          localNotificationListener.handleNotification(paramNotification, localObject);
        }
      }
    }
  }
  
  private void sendNotification(String paramString, long paramLong, Object paramObject, ObjectName paramObjectName)
  {
    synchronized (this)
    {
      this.sequenceNumber += 1L;
    }
    ??? = new SnmpTableEntryNotification(paramString, this, this.sequenceNumber, paramLong, paramObject, paramObjectName);
    sendNotification((Notification)???);
  }
  
  protected boolean contains(SnmpOid paramSnmpOid, Object paramObject)
  {
    return findObject(paramSnmpOid) > -1;
  }
  
  private int findObject(SnmpOid paramSnmpOid)
  {
    int i = 0;
    int j = this.size - 1;
    for (int m = i + (j - i) / 2; i <= j; m = i + (j - i) / 2)
    {
      SnmpOid localSnmpOid = this.tableoids[m];
      int k = paramSnmpOid.compareTo(localSnmpOid);
      if (k == 0) {
        return m;
      }
      if (paramSnmpOid.equals(localSnmpOid) == true) {
        return m;
      }
      if (k > 0) {
        i = m + 1;
      } else {
        j = m - 1;
      }
    }
    return -1;
  }
  
  private int getInsertionPoint(SnmpOid paramSnmpOid, boolean paramBoolean)
    throws SnmpStatusException
  {
    int i = 0;
    int j = this.size - 1;
    for (int m = i + (j - i) / 2; i <= j; m = i + (j - i) / 2)
    {
      SnmpOid localSnmpOid = this.tableoids[m];
      int k = paramSnmpOid.compareTo(localSnmpOid);
      if (k == 0)
      {
        if (paramBoolean) {
          throw new SnmpStatusException(17, m);
        }
        return m + 1;
      }
      if (k > 0) {
        i = m + 1;
      } else {
        j = m - 1;
      }
    }
    return m;
  }
  
  private void removeOid(int paramInt)
  {
    if (paramInt >= this.tablecount) {
      return;
    }
    if (paramInt < 0) {
      return;
    }
    int i = --this.tablecount - paramInt;
    this.tableoids[paramInt] = null;
    if (i > 0) {
      System.arraycopy(this.tableoids, paramInt + 1, this.tableoids, paramInt, i);
    }
    this.tableoids[this.tablecount] = null;
  }
  
  private void insertOid(int paramInt, SnmpOid paramSnmpOid)
  {
    if ((paramInt >= this.tablesize) || (this.tablecount == this.tablesize))
    {
      SnmpOid[] arrayOfSnmpOid = this.tableoids;
      this.tablesize += 16;
      this.tableoids = new SnmpOid[this.tablesize];
      if (paramInt > this.tablecount) {
        paramInt = this.tablecount;
      }
      if (paramInt < 0) {
        paramInt = 0;
      }
      int i = paramInt;
      int j = this.tablecount - paramInt;
      if (i > 0) {
        System.arraycopy(arrayOfSnmpOid, 0, this.tableoids, 0, i);
      }
      if (j > 0) {
        System.arraycopy(arrayOfSnmpOid, i, this.tableoids, i + 1, j);
      }
    }
    else if (paramInt < this.tablecount)
    {
      System.arraycopy(this.tableoids, paramInt, this.tableoids, paramInt + 1, this.tablecount - paramInt);
    }
    this.tableoids[paramInt] = paramSnmpOid;
    this.tablecount += 1;
  }
}
