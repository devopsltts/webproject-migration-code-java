package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

final class SnmpRequestTree
{
  private Hashtable<Object, Handler> hashtable = null;
  private SnmpMibRequest request = null;
  private int version = 0;
  private boolean creationflag = false;
  private boolean getnextflag = false;
  private int type = 0;
  private boolean setreqflag = false;
  
  SnmpRequestTree(SnmpMibRequest paramSnmpMibRequest, boolean paramBoolean, int paramInt)
  {
    this.request = paramSnmpMibRequest;
    this.version = paramSnmpMibRequest.getVersion();
    this.creationflag = paramBoolean;
    this.hashtable = new Hashtable();
    setPduType(paramInt);
  }
  
  public static int mapSetException(int paramInt1, int paramInt2)
    throws SnmpStatusException
  {
    int i = paramInt1;
    if (paramInt2 == 0) {
      return i;
    }
    int j = i;
    if (i == 225) {
      j = 17;
    } else if (i == 224) {
      j = 17;
    }
    return j;
  }
  
  public static int mapGetException(int paramInt1, int paramInt2)
    throws SnmpStatusException
  {
    int i = paramInt1;
    if (paramInt2 == 0) {
      return i;
    }
    int j = i;
    if (i == 225) {
      j = i;
    } else if (i == 224) {
      j = i;
    } else if (i == 6) {
      j = 224;
    } else if (i == 18) {
      j = 224;
    } else if ((i >= 7) && (i <= 12)) {
      j = 224;
    } else if (i == 4) {
      j = 224;
    } else if ((i != 16) && (i != 5)) {
      j = 225;
    }
    return j;
  }
  
  public Object getUserData()
  {
    return this.request.getUserData();
  }
  
  public boolean isCreationAllowed()
  {
    return this.creationflag;
  }
  
  public boolean isSetRequest()
  {
    return this.setreqflag;
  }
  
  public int getVersion()
  {
    return this.version;
  }
  
  public int getRequestPduVersion()
  {
    return this.request.getRequestPduVersion();
  }
  
  public SnmpMibNode getMetaNode(Handler paramHandler)
  {
    return paramHandler.meta;
  }
  
  public int getOidDepth(Handler paramHandler)
  {
    return paramHandler.depth;
  }
  
  public Enumeration<SnmpMibSubRequest> getSubRequests(Handler paramHandler)
  {
    return new Enum(this, paramHandler);
  }
  
  public Enumeration<Handler> getHandlers()
  {
    return this.hashtable.elements();
  }
  
  public void add(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpVarBind paramSnmpVarBind)
    throws SnmpStatusException
  {
    registerNode(paramSnmpMibNode, paramInt, null, paramSnmpVarBind, false, null);
  }
  
  public void add(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpOid paramSnmpOid, SnmpVarBind paramSnmpVarBind, boolean paramBoolean)
    throws SnmpStatusException
  {
    registerNode(paramSnmpMibNode, paramInt, paramSnmpOid, paramSnmpVarBind, paramBoolean, null);
  }
  
  public void add(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpOid paramSnmpOid, SnmpVarBind paramSnmpVarBind1, boolean paramBoolean, SnmpVarBind paramSnmpVarBind2)
    throws SnmpStatusException
  {
    registerNode(paramSnmpMibNode, paramInt, paramSnmpOid, paramSnmpVarBind1, paramBoolean, paramSnmpVarBind2);
  }
  
  void setPduType(int paramInt)
  {
    this.type = paramInt;
    this.setreqflag = ((paramInt == 253) || (paramInt == 163));
  }
  
  void setGetNextFlag()
  {
    this.getnextflag = true;
  }
  
  void switchCreationFlag(boolean paramBoolean)
  {
    this.creationflag = paramBoolean;
  }
  
  SnmpMibSubRequest getSubRequest(Handler paramHandler)
  {
    if (paramHandler == null) {
      return null;
    }
    return new SnmpMibSubRequestImpl(this.request, paramHandler.getSubList(), null, false, this.getnextflag, null);
  }
  
  SnmpMibSubRequest getSubRequest(Handler paramHandler, SnmpOid paramSnmpOid)
  {
    if (paramHandler == null) {
      return null;
    }
    int i = paramHandler.getEntryPos(paramSnmpOid);
    if (i == -1) {
      return null;
    }
    return new SnmpMibSubRequestImpl(this.request, paramHandler.getEntrySubList(i), paramHandler.getEntryOid(i), paramHandler.isNewEntry(i), this.getnextflag, paramHandler.getRowStatusVarBind(i));
  }
  
  SnmpMibSubRequest getSubRequest(Handler paramHandler, int paramInt)
  {
    if (paramHandler == null) {
      return null;
    }
    return new SnmpMibSubRequestImpl(this.request, paramHandler.getEntrySubList(paramInt), paramHandler.getEntryOid(paramInt), paramHandler.isNewEntry(paramInt), this.getnextflag, paramHandler.getRowStatusVarBind(paramInt));
  }
  
  private void put(Object paramObject, Handler paramHandler)
  {
    if (paramHandler == null) {
      return;
    }
    if (paramObject == null) {
      return;
    }
    if (this.hashtable == null) {
      this.hashtable = new Hashtable();
    }
    this.hashtable.put(paramObject, paramHandler);
  }
  
  private Handler get(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if (this.hashtable == null) {
      return null;
    }
    return (Handler)this.hashtable.get(paramObject);
  }
  
  private static int findOid(SnmpOid[] paramArrayOfSnmpOid, int paramInt, SnmpOid paramSnmpOid)
  {
    int i = paramInt;
    int j = 0;
    int k = i - 1;
    for (int m = j + (k - j) / 2; j <= k; m = j + (k - j) / 2)
    {
      SnmpOid localSnmpOid = paramArrayOfSnmpOid[m];
      int n = paramSnmpOid.compareTo(localSnmpOid);
      if (n == 0) {
        return m;
      }
      if (paramSnmpOid.equals(localSnmpOid)) {
        return m;
      }
      if (n > 0) {
        j = m + 1;
      } else {
        k = m - 1;
      }
    }
    return -1;
  }
  
  private static int getInsertionPoint(SnmpOid[] paramArrayOfSnmpOid, int paramInt, SnmpOid paramSnmpOid)
  {
    SnmpOid[] arrayOfSnmpOid = paramArrayOfSnmpOid;
    int i = paramInt;
    int j = 0;
    int k = i - 1;
    for (int m = j + (k - j) / 2; j <= k; m = j + (k - j) / 2)
    {
      SnmpOid localSnmpOid = arrayOfSnmpOid[m];
      int n = paramSnmpOid.compareTo(localSnmpOid);
      if (n == 0) {
        return m;
      }
      if (n > 0) {
        j = m + 1;
      } else {
        k = m - 1;
      }
    }
    return m;
  }
  
  private void registerNode(SnmpMibNode paramSnmpMibNode, int paramInt, SnmpOid paramSnmpOid, SnmpVarBind paramSnmpVarBind1, boolean paramBoolean, SnmpVarBind paramSnmpVarBind2)
    throws SnmpStatusException
  {
    if (paramSnmpMibNode == null)
    {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpRequestTree.class.getName(), "registerNode", "meta-node is null!");
      return;
    }
    if (paramSnmpVarBind1 == null)
    {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpRequestTree.class.getName(), "registerNode", "varbind is null!");
      return;
    }
    SnmpMibNode localSnmpMibNode = paramSnmpMibNode;
    Handler localHandler = get(localSnmpMibNode);
    if (localHandler == null)
    {
      localHandler = new Handler(this.type);
      localHandler.meta = paramSnmpMibNode;
      localHandler.depth = paramInt;
      put(localSnmpMibNode, localHandler);
    }
    if (paramSnmpOid == null) {
      localHandler.addVarbind(paramSnmpVarBind1);
    } else {
      localHandler.addVarbind(paramSnmpVarBind1, paramSnmpOid, paramBoolean, paramSnmpVarBind2);
    }
  }
  
  static final class Enum
    implements Enumeration<SnmpMibSubRequest>
  {
    private final SnmpRequestTree.Handler handler;
    private final SnmpRequestTree hlist;
    private int entry = 0;
    private int iter = 0;
    private int size = 0;
    
    Enum(SnmpRequestTree paramSnmpRequestTree, SnmpRequestTree.Handler paramHandler)
    {
      this.handler = paramHandler;
      this.hlist = paramSnmpRequestTree;
      this.size = paramHandler.getSubReqCount();
    }
    
    public boolean hasMoreElements()
    {
      return this.iter < this.size;
    }
    
    public SnmpMibSubRequest nextElement()
      throws NoSuchElementException
    {
      if ((this.iter == 0) && (this.handler.sublist != null))
      {
        this.iter += 1;
        return this.hlist.getSubRequest(this.handler);
      }
      this.iter += 1;
      if (this.iter > this.size) {
        throw new NoSuchElementException();
      }
      SnmpMibSubRequest localSnmpMibSubRequest = this.hlist.getSubRequest(this.handler, this.entry);
      this.entry += 1;
      return localSnmpMibSubRequest;
    }
  }
  
  static final class Handler
  {
    SnmpMibNode meta;
    int depth;
    Vector<SnmpVarBind> sublist;
    SnmpOid[] entryoids = null;
    Vector<SnmpVarBind>[] entrylists = null;
    boolean[] isentrynew = null;
    SnmpVarBind[] rowstatus = null;
    int entrycount = 0;
    int entrysize = 0;
    final int type;
    private static final int Delta = 10;
    
    public Handler(int paramInt)
    {
      this.type = paramInt;
    }
    
    public void addVarbind(SnmpVarBind paramSnmpVarBind)
    {
      if (this.sublist == null) {
        this.sublist = new Vector();
      }
      this.sublist.addElement(paramSnmpVarBind);
    }
    
    void add(int paramInt, SnmpOid paramSnmpOid, Vector<SnmpVarBind> paramVector, boolean paramBoolean, SnmpVarBind paramSnmpVarBind)
    {
      if (this.entryoids == null)
      {
        this.entryoids = new SnmpOid[10];
        this.entrylists = ((Vector[])new Vector[10]);
        this.isentrynew = new boolean[10];
        this.rowstatus = new SnmpVarBind[10];
        this.entrysize = 10;
        paramInt = 0;
      }
      else if ((paramInt >= this.entrysize) || (this.entrycount == this.entrysize))
      {
        SnmpOid[] arrayOfSnmpOid = this.entryoids;
        Vector[] arrayOfVector = this.entrylists;
        boolean[] arrayOfBoolean = this.isentrynew;
        SnmpVarBind[] arrayOfSnmpVarBind = this.rowstatus;
        this.entrysize += 10;
        this.entryoids = new SnmpOid[this.entrysize];
        this.entrylists = ((Vector[])new Vector[this.entrysize]);
        this.isentrynew = new boolean[this.entrysize];
        this.rowstatus = new SnmpVarBind[this.entrysize];
        if (paramInt > this.entrycount) {
          paramInt = this.entrycount;
        }
        if (paramInt < 0) {
          paramInt = 0;
        }
        int k = paramInt;
        int m = this.entrycount - paramInt;
        if (k > 0)
        {
          System.arraycopy(arrayOfSnmpOid, 0, this.entryoids, 0, k);
          System.arraycopy(arrayOfVector, 0, this.entrylists, 0, k);
          System.arraycopy(arrayOfBoolean, 0, this.isentrynew, 0, k);
          System.arraycopy(arrayOfSnmpVarBind, 0, this.rowstatus, 0, k);
        }
        if (m > 0)
        {
          int n = k + 1;
          System.arraycopy(arrayOfSnmpOid, k, this.entryoids, n, m);
          System.arraycopy(arrayOfVector, k, this.entrylists, n, m);
          System.arraycopy(arrayOfBoolean, k, this.isentrynew, n, m);
          System.arraycopy(arrayOfSnmpVarBind, k, this.rowstatus, n, m);
        }
      }
      else if (paramInt < this.entrycount)
      {
        int i = paramInt + 1;
        int j = this.entrycount - paramInt;
        System.arraycopy(this.entryoids, paramInt, this.entryoids, i, j);
        System.arraycopy(this.entrylists, paramInt, this.entrylists, i, j);
        System.arraycopy(this.isentrynew, paramInt, this.isentrynew, i, j);
        System.arraycopy(this.rowstatus, paramInt, this.rowstatus, i, j);
      }
      this.entryoids[paramInt] = paramSnmpOid;
      this.entrylists[paramInt] = paramVector;
      this.isentrynew[paramInt] = paramBoolean;
      this.rowstatus[paramInt] = paramSnmpVarBind;
      this.entrycount += 1;
    }
    
    public void addVarbind(SnmpVarBind paramSnmpVarBind1, SnmpOid paramSnmpOid, boolean paramBoolean, SnmpVarBind paramSnmpVarBind2)
      throws SnmpStatusException
    {
      Vector localVector = null;
      SnmpVarBind localSnmpVarBind = paramSnmpVarBind2;
      if (this.entryoids == null)
      {
        localVector = new Vector();
        add(0, paramSnmpOid, localVector, paramBoolean, localSnmpVarBind);
      }
      else
      {
        int i = SnmpRequestTree.getInsertionPoint(this.entryoids, this.entrycount, paramSnmpOid);
        if ((i > -1) && (i < this.entrycount) && (paramSnmpOid.compareTo(this.entryoids[i]) == 0))
        {
          localVector = this.entrylists[i];
          localSnmpVarBind = this.rowstatus[i];
        }
        else
        {
          localVector = new Vector();
          add(i, paramSnmpOid, localVector, paramBoolean, localSnmpVarBind);
        }
        if (paramSnmpVarBind2 != null)
        {
          if ((localSnmpVarBind != null) && (localSnmpVarBind != paramSnmpVarBind2) && ((this.type == 253) || (this.type == 163))) {
            throw new SnmpStatusException(12);
          }
          this.rowstatus[i] = paramSnmpVarBind2;
        }
      }
      if (paramSnmpVarBind2 != paramSnmpVarBind1) {
        localVector.addElement(paramSnmpVarBind1);
      }
    }
    
    public int getSubReqCount()
    {
      int i = 0;
      if (this.sublist != null) {
        i++;
      }
      if (this.entryoids != null) {
        i += this.entrycount;
      }
      return i;
    }
    
    public Vector<SnmpVarBind> getSubList()
    {
      return this.sublist;
    }
    
    public int getEntryPos(SnmpOid paramSnmpOid)
    {
      return SnmpRequestTree.findOid(this.entryoids, this.entrycount, paramSnmpOid);
    }
    
    public SnmpOid getEntryOid(int paramInt)
    {
      if (this.entryoids == null) {
        return null;
      }
      if ((paramInt == -1) || (paramInt >= this.entrycount)) {
        return null;
      }
      return this.entryoids[paramInt];
    }
    
    public boolean isNewEntry(int paramInt)
    {
      if (this.entryoids == null) {
        return false;
      }
      if ((paramInt == -1) || (paramInt >= this.entrycount)) {
        return false;
      }
      return this.isentrynew[paramInt];
    }
    
    public SnmpVarBind getRowStatusVarBind(int paramInt)
    {
      if (this.entryoids == null) {
        return null;
      }
      if ((paramInt == -1) || (paramInt >= this.entrycount)) {
        return null;
      }
      return this.rowstatus[paramInt];
    }
    
    public Vector<SnmpVarBind> getEntrySubList(int paramInt)
    {
      if (this.entrylists == null) {
        return null;
      }
      if ((paramInt == -1) || (paramInt >= this.entrycount)) {
        return null;
      }
      return this.entrylists[paramInt];
    }
    
    public Iterator<SnmpOid> getEntryOids()
    {
      if (this.entryoids == null) {
        return null;
      }
      return Arrays.asList(this.entryoids).iterator();
    }
    
    public int getEntryCount()
    {
      if (this.entryoids == null) {
        return 0;
      }
      return this.entrycount;
    }
  }
  
  static final class SnmpMibSubRequestImpl
    implements SnmpMibSubRequest
  {
    private final Vector<SnmpVarBind> varbinds;
    private final SnmpMibRequest global;
    private final int version;
    private final boolean isnew;
    private final SnmpOid entryoid;
    private final boolean getnextflag;
    private final SnmpVarBind statusvb;
    
    SnmpMibSubRequestImpl(SnmpMibRequest paramSnmpMibRequest, Vector<SnmpVarBind> paramVector, SnmpOid paramSnmpOid, boolean paramBoolean1, boolean paramBoolean2, SnmpVarBind paramSnmpVarBind)
    {
      this.global = paramSnmpMibRequest;
      this.varbinds = paramVector;
      this.version = paramSnmpMibRequest.getVersion();
      this.entryoid = paramSnmpOid;
      this.isnew = paramBoolean1;
      this.getnextflag = paramBoolean2;
      this.statusvb = paramSnmpVarBind;
    }
    
    public Enumeration<SnmpVarBind> getElements()
    {
      return this.varbinds.elements();
    }
    
    public Vector<SnmpVarBind> getSubList()
    {
      return this.varbinds;
    }
    
    public final int getSize()
    {
      if (this.varbinds == null) {
        return 0;
      }
      return this.varbinds.size();
    }
    
    public void addVarBind(SnmpVarBind paramSnmpVarBind)
    {
      this.varbinds.addElement(paramSnmpVarBind);
      this.global.addVarBind(paramSnmpVarBind);
    }
    
    public boolean isNewEntry()
    {
      return this.isnew;
    }
    
    public SnmpOid getEntryOid()
    {
      return this.entryoid;
    }
    
    public int getVarIndex(SnmpVarBind paramSnmpVarBind)
    {
      if (paramSnmpVarBind == null) {
        return 0;
      }
      return this.global.getVarIndex(paramSnmpVarBind);
    }
    
    public Object getUserData()
    {
      return this.global.getUserData();
    }
    
    public void registerGetException(SnmpVarBind paramSnmpVarBind, SnmpStatusException paramSnmpStatusException)
      throws SnmpStatusException
    {
      if (this.version == 0) {
        throw new SnmpStatusException(paramSnmpStatusException, getVarIndex(paramSnmpVarBind) + 1);
      }
      if (paramSnmpVarBind == null) {
        throw paramSnmpStatusException;
      }
      if (this.getnextflag)
      {
        paramSnmpVarBind.value = SnmpVarBind.endOfMibView;
        return;
      }
      int i = SnmpRequestTree.mapGetException(paramSnmpStatusException.getStatus(), this.version);
      if (i == 225) {
        paramSnmpVarBind.value = SnmpVarBind.noSuchObject;
      } else if (i == 224) {
        paramSnmpVarBind.value = SnmpVarBind.noSuchInstance;
      } else {
        throw new SnmpStatusException(i, getVarIndex(paramSnmpVarBind) + 1);
      }
    }
    
    public void registerSetException(SnmpVarBind paramSnmpVarBind, SnmpStatusException paramSnmpStatusException)
      throws SnmpStatusException
    {
      if (this.version == 0) {
        throw new SnmpStatusException(paramSnmpStatusException, getVarIndex(paramSnmpVarBind) + 1);
      }
      throw new SnmpStatusException(15, getVarIndex(paramSnmpVarBind) + 1);
    }
    
    public void registerCheckException(SnmpVarBind paramSnmpVarBind, SnmpStatusException paramSnmpStatusException)
      throws SnmpStatusException
    {
      int i = paramSnmpStatusException.getStatus();
      int j = SnmpRequestTree.mapSetException(i, this.version);
      if (i != j) {
        throw new SnmpStatusException(j, getVarIndex(paramSnmpVarBind) + 1);
      }
      throw new SnmpStatusException(paramSnmpStatusException, getVarIndex(paramSnmpVarBind) + 1);
    }
    
    public int getVersion()
    {
      return this.version;
    }
    
    public SnmpVarBind getRowStatusVarBind()
    {
      return this.statusvb;
    }
    
    public SnmpPdu getPdu()
    {
      return this.global.getPdu();
    }
    
    public int getRequestPduVersion()
    {
      return this.global.getRequestPduVersion();
    }
    
    public SnmpEngine getEngine()
    {
      return this.global.getEngine();
    }
    
    public String getPrincipal()
    {
      return this.global.getPrincipal();
    }
    
    public int getSecurityLevel()
    {
      return this.global.getSecurityLevel();
    }
    
    public int getSecurityModel()
    {
      return this.global.getSecurityModel();
    }
    
    public byte[] getContextName()
    {
      return this.global.getContextName();
    }
    
    public byte[] getAccessContextName()
    {
      return this.global.getAccessContextName();
    }
  }
}
