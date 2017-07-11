package java.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

public final class FileDescriptor
{
  private int fd = -1;
  private long handle = -1L;
  private Closeable parent;
  private List<Closeable> otherParents;
  private boolean closed;
  public static final FileDescriptor in = standardStream(0);
  public static final FileDescriptor out = standardStream(1);
  public static final FileDescriptor err = standardStream(2);
  
  public FileDescriptor() {}
  
  public boolean valid()
  {
    return (this.handle != -1L) || (this.fd != -1);
  }
  
  public native void sync()
    throws SyncFailedException;
  
  private static native void initIDs();
  
  private static native long set(int paramInt);
  
  private static FileDescriptor standardStream(int paramInt)
  {
    FileDescriptor localFileDescriptor = new FileDescriptor();
    localFileDescriptor.handle = set(paramInt);
    return localFileDescriptor;
  }
  
  synchronized void attach(Closeable paramCloseable)
  {
    if (this.parent == null)
    {
      this.parent = paramCloseable;
    }
    else if (this.otherParents == null)
    {
      this.otherParents = new ArrayList();
      this.otherParents.add(this.parent);
      this.otherParents.add(paramCloseable);
    }
    else
    {
      this.otherParents.add(paramCloseable);
    }
  }
  
  synchronized void closeAll(Closeable paramCloseable)
    throws IOException
  {
    if (!this.closed)
    {
      this.closed = true;
      Object localObject1 = null;
      try
      {
        Closeable localCloseable1 = paramCloseable;
        Object localObject2 = null;
        try
        {
          if (this.otherParents != null)
          {
            Iterator localIterator = this.otherParents.iterator();
            while (localIterator.hasNext())
            {
              Closeable localCloseable2 = (Closeable)localIterator.next();
              try
              {
                localCloseable2.close();
              }
              catch (IOException localIOException2)
              {
                if (localObject1 == null) {
                  localObject1 = localIOException2;
                } else {
                  localObject1.addSuppressed(localIOException2);
                }
              }
            }
          }
        }
        catch (Throwable localThrowable2)
        {
          localObject2 = localThrowable2;
          throw localThrowable2;
        }
        finally
        {
          if (localCloseable1 != null) {
            if (localObject2 != null) {
              try
              {
                localCloseable1.close();
              }
              catch (Throwable localThrowable3)
              {
                localObject2.addSuppressed(localThrowable3);
              }
            } else {
              localCloseable1.close();
            }
          }
        }
      }
      catch (IOException localIOException1)
      {
        if (localObject1 != null) {
          localIOException1.addSuppressed(localObject1);
        }
        localObject1 = localIOException1;
      }
      finally
      {
        if (localObject1 != null) {
          throw localObject1;
        }
      }
    }
  }
  
  static
  {
    initIDs();
    SharedSecrets.setJavaIOFileDescriptorAccess(new JavaIOFileDescriptorAccess()
    {
      public void set(FileDescriptor paramAnonymousFileDescriptor, int paramAnonymousInt)
      {
        paramAnonymousFileDescriptor.fd = paramAnonymousInt;
      }
      
      public int get(FileDescriptor paramAnonymousFileDescriptor)
      {
        return paramAnonymousFileDescriptor.fd;
      }
      
      public void setHandle(FileDescriptor paramAnonymousFileDescriptor, long paramAnonymousLong)
      {
        paramAnonymousFileDescriptor.handle = paramAnonymousLong;
      }
      
      public long getHandle(FileDescriptor paramAnonymousFileDescriptor)
      {
        return paramAnonymousFileDescriptor.handle;
      }
    });
  }
}
