package sun.misc;

public final class ThreadGroupUtils
{
  private ThreadGroupUtils() {}
  
  public static ThreadGroup getRootThreadGroup()
  {
    Object localObject = Thread.currentThread().getThreadGroup();
    for (ThreadGroup localThreadGroup = ((ThreadGroup)localObject).getParent(); localThreadGroup != null; localThreadGroup = ((ThreadGroup)localObject).getParent()) {
      localObject = localThreadGroup;
    }
    return localObject;
  }
}
