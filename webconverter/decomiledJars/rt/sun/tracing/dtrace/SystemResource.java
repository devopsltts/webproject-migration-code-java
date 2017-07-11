package sun.tracing.dtrace;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;

class SystemResource
  extends WeakReference<Activation>
{
  private long handle;
  private static ReferenceQueue<Activation> referenceQueue = SystemResource.referenceQueue = new ReferenceQueue();
  static HashSet<SystemResource> resources = new HashSet();
  
  SystemResource(Activation paramActivation, long paramLong)
  {
    super(paramActivation, referenceQueue);
    this.handle = paramLong;
    flush();
    resources.add(this);
  }
  
  void dispose()
  {
    JVM.dispose(this.handle);
    resources.remove(this);
    this.handle = 0L;
  }
  
  static void flush()
  {
    SystemResource localSystemResource = null;
    while ((localSystemResource = (SystemResource)referenceQueue.poll()) != null) {
      if (localSystemResource.handle != 0L) {
        localSystemResource.dispose();
      }
    }
  }
}
