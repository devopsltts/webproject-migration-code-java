package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.PIHandler;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAManagerPackage.State;

public class POAManagerImpl
  extends LocalObject
  implements POAManager
{
  private final POAFactory factory;
  private PIHandler pihandler;
  private State state;
  private Set poas = new HashSet(4);
  private int nInvocations = 0;
  private int nWaiters = 0;
  private int myId = 0;
  private boolean debug;
  private boolean explicitStateChange;
  
  private String stateToString(State paramState)
  {
    switch (paramState.value())
    {
    case 0: 
      return "State[HOLDING]";
    case 1: 
      return "State[ACTIVE]";
    case 2: 
      return "State[DISCARDING]";
    case 3: 
      return "State[INACTIVE]";
    }
    return "State[UNKNOWN]";
  }
  
  public String toString()
  {
    return "POAManagerImpl[myId=" + this.myId + " state=" + stateToString(this.state) + " nInvocations=" + this.nInvocations + " nWaiters=" + this.nWaiters + "]";
  }
  
  POAFactory getFactory()
  {
    return this.factory;
  }
  
  PIHandler getPIHandler()
  {
    return this.pihandler;
  }
  
  /* Error */
  private void countedWait()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   4: ifeq +38 -> 42
    //   7: aload_0
    //   8: new 156	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   15: ldc 6
    //   17: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_0
    //   21: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: ldc 2
    //   26: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: aload_0
    //   30: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   33: invokevirtual 276	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   36: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   39: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   42: aload_0
    //   43: dup
    //   44: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   47: iconst_1
    //   48: iadd
    //   49: putfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   52: aload_0
    //   53: invokevirtual 273	java/lang/Object:wait	()V
    //   56: aload_0
    //   57: dup
    //   58: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   61: iconst_1
    //   62: isub
    //   63: putfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   66: aload_0
    //   67: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   70: ifeq +152 -> 222
    //   73: aload_0
    //   74: new 156	java/lang/StringBuilder
    //   77: dup
    //   78: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   81: ldc 15
    //   83: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: aload_0
    //   87: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   90: ldc 2
    //   92: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: aload_0
    //   96: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   99: invokevirtual 276	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   102: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   105: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   108: goto +114 -> 222
    //   111: astore_1
    //   112: aload_0
    //   113: dup
    //   114: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   117: iconst_1
    //   118: isub
    //   119: putfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   122: aload_0
    //   123: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   126: ifeq +96 -> 222
    //   129: aload_0
    //   130: new 156	java/lang/StringBuilder
    //   133: dup
    //   134: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   137: ldc 15
    //   139: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   142: aload_0
    //   143: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   146: ldc 2
    //   148: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: aload_0
    //   152: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   155: invokevirtual 276	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   158: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   161: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   164: goto +58 -> 222
    //   167: astore_2
    //   168: aload_0
    //   169: dup
    //   170: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   173: iconst_1
    //   174: isub
    //   175: putfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   178: aload_0
    //   179: getfield 243	com/sun/corba/se/impl/oa/poa/POAManagerImpl:debug	Z
    //   182: ifeq +38 -> 220
    //   185: aload_0
    //   186: new 156	java/lang/StringBuilder
    //   189: dup
    //   190: invokespecial 274	java/lang/StringBuilder:<init>	()V
    //   193: ldc 15
    //   195: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   198: aload_0
    //   199: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   202: ldc 2
    //   204: invokevirtual 278	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   207: aload_0
    //   208: getfield 242	com/sun/corba/se/impl/oa/poa/POAManagerImpl:nWaiters	I
    //   211: invokevirtual 276	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   214: invokevirtual 275	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   217: invokestatic 271	com/sun/corba/se/impl/orbutil/ORBUtility:dprint	(Ljava/lang/Object;Ljava/lang/String;)V
    //   220: aload_2
    //   221: athrow
    //   222: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	223	0	this	POAManagerImpl
    //   111	1	1	localInterruptedException	InterruptedException
    //   167	54	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	56	111	java/lang/InterruptedException
    //   0	56	167	finally
  }
  
  private void notifyWaiters()
  {
    if (this.debug) {
      ORBUtility.dprint(this, "Calling notifyWaiters on POAManager " + this + " nWaiters=" + this.nWaiters);
    }
    if (this.nWaiters > 0) {
      notifyAll();
    }
  }
  
  public int getManagerId()
  {
    return this.myId;
  }
  
  POAManagerImpl(POAFactory paramPOAFactory, PIHandler paramPIHandler)
  {
    this.factory = paramPOAFactory;
    paramPOAFactory.addPoaManager(this);
    this.pihandler = paramPIHandler;
    this.myId = paramPOAFactory.newPOAManagerId();
    this.state = State.HOLDING;
    this.debug = paramPOAFactory.getORB().poaDebugFlag;
    this.explicitStateChange = false;
    if (this.debug) {
      ORBUtility.dprint(this, "Creating POAManagerImpl " + this);
    }
  }
  
  synchronized void addPOA(POA paramPOA)
  {
    if (this.state.value() == 3)
    {
      POASystemException localPOASystemException = this.factory.getWrapper();
      throw localPOASystemException.addPoaInactive(CompletionStatus.COMPLETED_NO);
    }
    this.poas.add(paramPOA);
  }
  
  synchronized void removePOA(POA paramPOA)
  {
    this.poas.remove(paramPOA);
    if (this.poas.isEmpty()) {
      this.factory.removePoaManager(this);
    }
  }
  
  public short getORTState()
  {
    switch (this.state.value())
    {
    case 0: 
      return 0;
    case 1: 
      return 1;
    case 3: 
      return 3;
    case 2: 
      return 2;
    }
    return 4;
  }
  
  public synchronized void activate()
    throws AdapterInactive
  {
    this.explicitStateChange = true;
    if (this.debug) {
      ORBUtility.dprint(this, "Calling activate on POAManager " + this);
    }
    try
    {
      if (this.state.value() == 3) {
        throw new AdapterInactive();
      }
      this.state = State.ACTIVE;
      this.pihandler.adapterManagerStateChanged(this.myId, getORTState());
      notifyWaiters();
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting activate on POAManager " + this);
      }
    }
    finally
    {
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting activate on POAManager " + this);
      }
    }
  }
  
  public synchronized void hold_requests(boolean paramBoolean)
    throws AdapterInactive
  {
    this.explicitStateChange = true;
    if (this.debug) {
      ORBUtility.dprint(this, "Calling hold_requests on POAManager " + this);
    }
    try
    {
      if (this.state.value() == 3) {
        throw new AdapterInactive();
      }
      this.state = State.HOLDING;
      this.pihandler.adapterManagerStateChanged(this.myId, getORTState());
      notifyWaiters();
      if (paramBoolean) {
        while ((this.state.value() == 0) && (this.nInvocations > 0)) {
          countedWait();
        }
      }
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting hold_requests on POAManager " + this);
      }
    }
    finally
    {
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting hold_requests on POAManager " + this);
      }
    }
  }
  
  public synchronized void discard_requests(boolean paramBoolean)
    throws AdapterInactive
  {
    this.explicitStateChange = true;
    if (this.debug) {
      ORBUtility.dprint(this, "Calling hold_requests on POAManager " + this);
    }
    try
    {
      if (this.state.value() == 3) {
        throw new AdapterInactive();
      }
      this.state = State.DISCARDING;
      this.pihandler.adapterManagerStateChanged(this.myId, getORTState());
      notifyWaiters();
      if (paramBoolean) {
        while ((this.state.value() == 2) && (this.nInvocations > 0)) {
          countedWait();
        }
      }
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting hold_requests on POAManager " + this);
      }
    }
    finally
    {
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting hold_requests on POAManager " + this);
      }
    }
  }
  
  public void deactivate(boolean paramBoolean1, boolean paramBoolean2)
    throws AdapterInactive
  {
    this.explicitStateChange = true;
    try
    {
      synchronized (this)
      {
        if (this.debug) {
          ORBUtility.dprint(this, "Calling deactivate on POAManager " + this);
        }
        if (this.state.value() == 3) {
          throw new AdapterInactive();
        }
        this.state = State.INACTIVE;
        this.pihandler.adapterManagerStateChanged(this.myId, getORTState());
        notifyWaiters();
      }
      ??? = new POAManagerDeactivator(this, paramBoolean1, this.debug);
      if (paramBoolean2)
      {
        ???.run();
      }
      else
      {
        Thread localThread = new Thread(???);
        localThread.start();
      }
    }
    finally
    {
      synchronized (this)
      {
        if (this.debug) {
          ORBUtility.dprint(this, "Exiting deactivate on POAManager " + this);
        }
      }
    }
  }
  
  public State get_state()
  {
    return this.state;
  }
  
  synchronized void checkIfActive()
  {
    try
    {
      if (this.debug) {
        ORBUtility.dprint(this, "Calling checkIfActive for POAManagerImpl " + this);
      }
      checkState();
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting checkIfActive for POAManagerImpl " + this);
      }
    }
    finally
    {
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting checkIfActive for POAManagerImpl " + this);
      }
    }
  }
  
  private void checkState()
  {
    while (this.state.value() != 1) {
      switch (this.state.value())
      {
      case 0: 
      case 2: 
      case 3: 
        while (this.state.value() == 0)
        {
          countedWait();
          continue;
          throw this.factory.getWrapper().poaDiscarding();
          throw this.factory.getWrapper().poaInactive();
        }
      }
    }
  }
  
  synchronized void enter()
  {
    try
    {
      if (this.debug) {
        ORBUtility.dprint(this, "Calling enter for POAManagerImpl " + this);
      }
      checkState();
      this.nInvocations += 1;
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting enter for POAManagerImpl " + this);
      }
    }
    finally
    {
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting enter for POAManagerImpl " + this);
      }
    }
  }
  
  synchronized void exit()
  {
    try
    {
      if (this.debug) {
        ORBUtility.dprint(this, "Calling exit for POAManagerImpl " + this);
      }
      this.nInvocations -= 1;
      if (this.nInvocations == 0) {
        notifyWaiters();
      }
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting exit for POAManagerImpl " + this);
      }
    }
    finally
    {
      if (this.debug) {
        ORBUtility.dprint(this, "Exiting exit for POAManagerImpl " + this);
      }
    }
  }
  
  public synchronized void implicitActivation()
  {
    if (!this.explicitStateChange) {
      try
      {
        activate();
      }
      catch (AdapterInactive localAdapterInactive) {}
    }
  }
  
  private class POAManagerDeactivator
    implements Runnable
  {
    private boolean etherealize_objects;
    private POAManagerImpl pmi;
    private boolean debug;
    
    POAManagerDeactivator(POAManagerImpl paramPOAManagerImpl, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.etherealize_objects = paramBoolean1;
      this.pmi = paramPOAManagerImpl;
      this.debug = paramBoolean2;
    }
    
    public void run()
    {
      try
      {
        synchronized (this.pmi)
        {
          if (this.debug) {
            ORBUtility.dprint(this, "Calling run with etherealize_objects=" + this.etherealize_objects + " pmi=" + this.pmi);
          }
          while (this.pmi.nInvocations > 0) {
            POAManagerImpl.this.countedWait();
          }
        }
        if (this.etherealize_objects)
        {
          ??? = null;
          synchronized (this.pmi)
          {
            if (this.debug) {
              ORBUtility.dprint(this, "run: Preparing to etherealize with pmi=" + this.pmi);
            }
            ??? = new HashSet(this.pmi.poas).iterator();
          }
          while (???.hasNext()) {
            ((POAImpl)???.next()).etherealizeAll();
          }
          synchronized (this.pmi)
          {
            if (this.debug) {
              ORBUtility.dprint(this, "run: removing POAManager and clearing poas with pmi=" + this.pmi);
            }
            POAManagerImpl.this.factory.removePoaManager(this.pmi);
            POAManagerImpl.this.poas.clear();
          }
        }
      }
      finally
      {
        if (this.debug) {
          synchronized (this.pmi)
          {
            ORBUtility.dprint(this, "Exiting run");
          }
        }
      }
    }
  }
}
