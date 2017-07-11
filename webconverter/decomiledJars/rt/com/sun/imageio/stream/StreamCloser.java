package com.sun.imageio.stream;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.WeakHashMap;
import javax.imageio.stream.ImageInputStream;

public class StreamCloser
{
  private static WeakHashMap<CloseAction, Object> toCloseQueue;
  private static Thread streamCloser;
  
  public StreamCloser() {}
  
  public static void addToQueue(CloseAction paramCloseAction)
  {
    synchronized (StreamCloser.class)
    {
      if (toCloseQueue == null) {
        toCloseQueue = new WeakHashMap();
      }
      toCloseQueue.put(paramCloseAction, null);
      if (streamCloser == null)
      {
        Runnable local1 = new Runnable()
        {
          public void run()
          {
            if (StreamCloser.toCloseQueue != null) {
              synchronized (StreamCloser.class)
              {
                Set localSet = StreamCloser.toCloseQueue.keySet();
                StreamCloser.CloseAction[] arrayOfCloseAction1 = new StreamCloser.CloseAction[localSet.size()];
                arrayOfCloseAction1 = (StreamCloser.CloseAction[])localSet.toArray(arrayOfCloseAction1);
                for (StreamCloser.CloseAction localCloseAction : arrayOfCloseAction1) {
                  if (localCloseAction != null) {
                    try
                    {
                      localCloseAction.performAction();
                    }
                    catch (IOException localIOException) {}
                  }
                }
              }
            }
          }
        };
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            Object localObject1 = Thread.currentThread().getThreadGroup();
            for (Object localObject2 = localObject1; localObject2 != null; localObject2 = ((ThreadGroup)localObject1).getParent()) {
              localObject1 = localObject2;
            }
            StreamCloser.access$102(new Thread((ThreadGroup)localObject1, this.val$streamCloserRunnable));
            StreamCloser.streamCloser.setContextClassLoader(null);
            Runtime.getRuntime().addShutdownHook(StreamCloser.streamCloser);
            return null;
          }
        });
      }
    }
  }
  
  public static void removeFromQueue(CloseAction paramCloseAction)
  {
    synchronized (StreamCloser.class)
    {
      if (toCloseQueue != null) {
        toCloseQueue.remove(paramCloseAction);
      }
    }
  }
  
  public static CloseAction createCloseAction(ImageInputStream paramImageInputStream)
  {
    return new CloseAction(paramImageInputStream, null);
  }
  
  public static final class CloseAction
  {
    private ImageInputStream iis;
    
    private CloseAction(ImageInputStream paramImageInputStream)
    {
      this.iis = paramImageInputStream;
    }
    
    public void performAction()
      throws IOException
    {
      if (this.iis != null) {
        this.iis.close();
      }
    }
  }
}
