package sun.rmi.transport;

import java.rmi.server.UID;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import sun.rmi.runtime.RuntimeUtil;
import sun.rmi.runtime.RuntimeUtil.GetInstanceAction;
import sun.security.action.GetLongAction;

public class DGCAckHandler
{
  private static final long dgcAckTimeout = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.ackTimeout", 300000L))).longValue();
  private static final ScheduledExecutorService scheduler = ((RuntimeUtil)AccessController.doPrivileged(new RuntimeUtil.GetInstanceAction())).getScheduler();
  private static final Map<UID, DGCAckHandler> idTable = Collections.synchronizedMap(new HashMap());
  private final UID id;
  private List<Object> objList = new ArrayList();
  private Future<?> task = null;
  
  DGCAckHandler(UID paramUID)
  {
    this.id = paramUID;
    if (paramUID != null)
    {
      assert (!idTable.containsKey(paramUID));
      idTable.put(paramUID, this);
    }
  }
  
  synchronized void add(Object paramObject)
  {
    if (this.objList != null) {
      this.objList.add(paramObject);
    }
  }
  
  synchronized void startTimer()
  {
    if ((this.objList != null) && (this.task == null)) {
      this.task = scheduler.schedule(new Runnable()
      {
        public void run()
        {
          DGCAckHandler.this.release();
        }
      }, dgcAckTimeout, TimeUnit.MILLISECONDS);
    }
  }
  
  synchronized void release()
  {
    if (this.task != null)
    {
      this.task.cancel(false);
      this.task = null;
    }
    this.objList = null;
  }
  
  public static void received(UID paramUID)
  {
    DGCAckHandler localDGCAckHandler = (DGCAckHandler)idTable.remove(paramUID);
    if (localDGCAckHandler != null) {
      localDGCAckHandler.release();
    }
  }
}
