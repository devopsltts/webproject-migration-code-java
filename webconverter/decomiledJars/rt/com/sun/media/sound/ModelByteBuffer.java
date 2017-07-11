package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Iterator;

public final class ModelByteBuffer
{
  private ModelByteBuffer root = this;
  private File file;
  private long fileoffset;
  private byte[] buffer;
  private long offset;
  private final long len;
  
  private ModelByteBuffer(ModelByteBuffer paramModelByteBuffer, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.root = paramModelByteBuffer.root;
    this.offset = 0L;
    long l = paramModelByteBuffer.len;
    if (paramLong1 < 0L) {
      paramLong1 = 0L;
    }
    if (paramLong1 > l) {
      paramLong1 = l;
    }
    if (paramLong2 < 0L) {
      paramLong2 = 0L;
    }
    if (paramLong2 > l) {
      paramLong2 = l;
    }
    if (paramLong1 > paramLong2) {
      paramLong1 = paramLong2;
    }
    this.offset = paramLong1;
    this.len = (paramLong2 - paramLong1);
    if (paramBoolean)
    {
      this.buffer = this.root.buffer;
      if (this.root.file != null)
      {
        this.file = this.root.file;
        this.fileoffset = (this.root.fileoffset + arrayOffset());
        this.offset = 0L;
      }
      else
      {
        this.offset = arrayOffset();
      }
      this.root = this;
    }
  }
  
  public ModelByteBuffer(byte[] paramArrayOfByte)
  {
    this.buffer = paramArrayOfByte;
    this.offset = 0L;
    this.len = paramArrayOfByte.length;
  }
  
  public ModelByteBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer = paramArrayOfByte;
    this.offset = paramInt1;
    this.len = paramInt2;
  }
  
  public ModelByteBuffer(File paramFile)
  {
    this.file = paramFile;
    this.fileoffset = 0L;
    this.len = paramFile.length();
  }
  
  public ModelByteBuffer(File paramFile, long paramLong1, long paramLong2)
  {
    this.file = paramFile;
    this.fileoffset = paramLong1;
    this.len = paramLong2;
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    if ((this.root.file != null) && (this.root.buffer == null))
    {
      InputStream localInputStream = getInputStream();
      byte[] arrayOfByte = new byte['Ѐ'];
      int i;
      while ((i = localInputStream.read(arrayOfByte)) != -1) {
        paramOutputStream.write(arrayOfByte, 0, i);
      }
    }
    else
    {
      paramOutputStream.write(array(), (int)arrayOffset(), (int)capacity());
    }
  }
  
  public InputStream getInputStream()
  {
    if ((this.root.file != null) && (this.root.buffer == null)) {
      try
      {
        return new RandomFileInputStream();
      }
      catch (IOException localIOException)
      {
        return null;
      }
    }
    return new ByteArrayInputStream(array(), (int)arrayOffset(), (int)capacity());
  }
  
  public ModelByteBuffer subbuffer(long paramLong)
  {
    return subbuffer(paramLong, capacity());
  }
  
  public ModelByteBuffer subbuffer(long paramLong1, long paramLong2)
  {
    return subbuffer(paramLong1, paramLong2, false);
  }
  
  public ModelByteBuffer subbuffer(long paramLong1, long paramLong2, boolean paramBoolean)
  {
    return new ModelByteBuffer(this, paramLong1, paramLong2, paramBoolean);
  }
  
  public byte[] array()
  {
    return this.root.buffer;
  }
  
  public long arrayOffset()
  {
    if (this.root != this) {
      return this.root.arrayOffset() + this.offset;
    }
    return this.offset;
  }
  
  public long capacity()
  {
    return this.len;
  }
  
  public ModelByteBuffer getRoot()
  {
    return this.root;
  }
  
  public File getFile()
  {
    return this.file;
  }
  
  public long getFilePointer()
  {
    return this.fileoffset;
  }
  
  public static void loadAll(Collection<ModelByteBuffer> paramCollection)
    throws IOException
  {
    File localFile = null;
    RandomAccessFile localRandomAccessFile = null;
    try
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        ModelByteBuffer localModelByteBuffer = (ModelByteBuffer)localIterator.next();
        localModelByteBuffer = localModelByteBuffer.root;
        if ((localModelByteBuffer.file != null) && (localModelByteBuffer.buffer == null))
        {
          if ((localFile == null) || (!localFile.equals(localModelByteBuffer.file)))
          {
            if (localRandomAccessFile != null)
            {
              localRandomAccessFile.close();
              localRandomAccessFile = null;
            }
            localFile = localModelByteBuffer.file;
            localRandomAccessFile = new RandomAccessFile(localModelByteBuffer.file, "r");
          }
          localRandomAccessFile.seek(localModelByteBuffer.fileoffset);
          byte[] arrayOfByte = new byte[(int)localModelByteBuffer.capacity()];
          int i = 0;
          int j = arrayOfByte.length;
          while (i != j) {
            if (j - i > 65536)
            {
              localRandomAccessFile.readFully(arrayOfByte, i, 65536);
              i += 65536;
            }
            else
            {
              localRandomAccessFile.readFully(arrayOfByte, i, j - i);
              i = j;
            }
          }
          localModelByteBuffer.buffer = arrayOfByte;
          localModelByteBuffer.offset = 0L;
        }
      }
    }
    finally
    {
      if (localRandomAccessFile != null) {
        localRandomAccessFile.close();
      }
    }
  }
  
  public void load()
    throws IOException
  {
    if (this.root != this)
    {
      this.root.load();
      return;
    }
    if (this.buffer != null) {
      return;
    }
    if (this.file == null) {
      throw new IllegalStateException("No file associated with this ByteBuffer!");
    }
    DataInputStream localDataInputStream = new DataInputStream(getInputStream());
    this.buffer = new byte[(int)capacity()];
    this.offset = 0L;
    localDataInputStream.readFully(this.buffer);
    localDataInputStream.close();
  }
  
  public void unload()
  {
    if (this.root != this)
    {
      this.root.unload();
      return;
    }
    if (this.file == null) {
      throw new IllegalStateException("No file associated with this ByteBuffer!");
    }
    this.root.buffer = null;
  }
  
  private class RandomFileInputStream
    extends InputStream
  {
    private final RandomAccessFile raf = new RandomAccessFile(ModelByteBuffer.access$000(ModelByteBuffer.this).file, "r");
    private long left;
    private long mark = 0L;
    private long markleft = 0L;
    
    RandomFileInputStream()
      throws IOException
    {
      this.raf.seek(ModelByteBuffer.access$000(ModelByteBuffer.this).fileoffset + ModelByteBuffer.this.arrayOffset());
      this.left = ModelByteBuffer.this.capacity();
    }
    
    public int available()
      throws IOException
    {
      if (this.left > 2147483647L) {
        return Integer.MAX_VALUE;
      }
      return (int)this.left;
    }
    
    public synchronized void mark(int paramInt)
    {
      try
      {
        this.mark = this.raf.getFilePointer();
        this.markleft = this.left;
      }
      catch (IOException localIOException) {}
    }
    
    public boolean markSupported()
    {
      return true;
    }
    
    public synchronized void reset()
      throws IOException
    {
      this.raf.seek(this.mark);
      this.left = this.markleft;
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (paramLong < 0L) {
        return 0L;
      }
      if (paramLong > this.left) {
        paramLong = this.left;
      }
      long l = this.raf.getFilePointer();
      this.raf.seek(l + paramLong);
      this.left -= paramLong;
      return paramLong;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (paramInt2 > this.left) {
        paramInt2 = (int)this.left;
      }
      if (this.left == 0L) {
        return -1;
      }
      paramInt2 = this.raf.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt2 == -1) {
        return -1;
      }
      this.left -= paramInt2;
      return paramInt2;
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      int i = paramArrayOfByte.length;
      if (i > this.left) {
        i = (int)this.left;
      }
      if (this.left == 0L) {
        return -1;
      }
      i = this.raf.read(paramArrayOfByte, 0, i);
      if (i == -1) {
        return -1;
      }
      this.left -= i;
      return i;
    }
    
    public int read()
      throws IOException
    {
      if (this.left == 0L) {
        return -1;
      }
      int i = this.raf.read();
      if (i == -1) {
        return -1;
      }
      this.left -= 1L;
      return i;
    }
    
    public void close()
      throws IOException
    {
      this.raf.close();
    }
  }
}
