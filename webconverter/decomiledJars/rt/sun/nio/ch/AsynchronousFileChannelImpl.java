package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

abstract class AsynchronousFileChannelImpl
  extends AsynchronousFileChannel
{
  protected final ReadWriteLock closeLock = new ReentrantReadWriteLock();
  protected volatile boolean closed;
  protected final FileDescriptor fdObj;
  protected final boolean reading;
  protected final boolean writing;
  protected final ExecutorService executor;
  private volatile FileLockTable fileLockTable;
  
  protected AsynchronousFileChannelImpl(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, ExecutorService paramExecutorService)
  {
    this.fdObj = paramFileDescriptor;
    this.reading = paramBoolean1;
    this.writing = paramBoolean2;
    this.executor = paramExecutorService;
  }
  
  final ExecutorService executor()
  {
    return this.executor;
  }
  
  public final boolean isOpen()
  {
    return !this.closed;
  }
  
  protected final void begin()
    throws IOException
  {
    this.closeLock.readLock().lock();
    if (this.closed) {
      throw new ClosedChannelException();
    }
  }
  
  protected final void end()
  {
    this.closeLock.readLock().unlock();
  }
  
  protected final void end(boolean paramBoolean)
    throws IOException
  {
    end();
    if ((!paramBoolean) && (!isOpen())) {
      throw new AsynchronousCloseException();
    }
  }
  
  abstract <A> Future<FileLock> implLock(long paramLong1, long paramLong2, boolean paramBoolean, A paramA, CompletionHandler<FileLock, ? super A> paramCompletionHandler);
  
  public final Future<FileLock> lock(long paramLong1, long paramLong2, boolean paramBoolean)
  {
    return implLock(paramLong1, paramLong2, paramBoolean, null, null);
  }
  
  public final <A> void lock(long paramLong1, long paramLong2, boolean paramBoolean, A paramA, CompletionHandler<FileLock, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    implLock(paramLong1, paramLong2, paramBoolean, paramA, paramCompletionHandler);
  }
  
  final void ensureFileLockTableInitialized()
    throws IOException
  {
    if (this.fileLockTable == null) {
      synchronized (this)
      {
        if (this.fileLockTable == null) {
          this.fileLockTable = FileLockTable.newSharedFileLockTable(this, this.fdObj);
        }
      }
    }
  }
  
  final void invalidateAllLocks()
    throws IOException
  {
    if (this.fileLockTable != null)
    {
      Iterator localIterator = this.fileLockTable.removeAll().iterator();
      while (localIterator.hasNext())
      {
        FileLock localFileLock = (FileLock)localIterator.next();
        synchronized (localFileLock)
        {
          if (localFileLock.isValid())
          {
            FileLockImpl localFileLockImpl = (FileLockImpl)localFileLock;
            implRelease(localFileLockImpl);
            localFileLockImpl.invalidate();
          }
        }
      }
    }
  }
  
  protected final FileLockImpl addToFileLockTable(long paramLong1, long paramLong2, boolean paramBoolean)
  {
    FileLockImpl localFileLockImpl1;
    try
    {
      this.closeLock.readLock().lock();
      if (this.closed)
      {
        FileLockImpl localFileLockImpl2 = null;
        return localFileLockImpl2;
      }
      try {}catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
      localFileLockImpl1 = new FileLockImpl(this, paramLong1, paramLong2, paramBoolean);
      this.fileLockTable.add(localFileLockImpl1);
    }
    finally
    {
      end();
    }
    return localFileLockImpl1;
  }
  
  protected final void removeFromFileLockTable(FileLockImpl paramFileLockImpl)
  {
    this.fileLockTable.remove(paramFileLockImpl);
  }
  
  protected abstract void implRelease(FileLockImpl paramFileLockImpl)
    throws IOException;
  
  final void release(FileLockImpl paramFileLockImpl)
    throws IOException
  {
    try
    {
      begin();
      implRelease(paramFileLockImpl);
      removeFromFileLockTable(paramFileLockImpl);
      end();
    }
    finally
    {
      end();
    }
  }
  
  abstract <A> Future<Integer> implRead(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler);
  
  public final Future<Integer> read(ByteBuffer paramByteBuffer, long paramLong)
  {
    return implRead(paramByteBuffer, paramLong, null, null);
  }
  
  public final <A> void read(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    implRead(paramByteBuffer, paramLong, paramA, paramCompletionHandler);
  }
  
  abstract <A> Future<Integer> implWrite(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler);
  
  public final Future<Integer> write(ByteBuffer paramByteBuffer, long paramLong)
  {
    return implWrite(paramByteBuffer, paramLong, null, null);
  }
  
  public final <A> void write(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler)
  {
    if (paramCompletionHandler == null) {
      throw new NullPointerException("'handler' is null");
    }
    implWrite(paramByteBuffer, paramLong, paramA, paramCompletionHandler);
  }
}
