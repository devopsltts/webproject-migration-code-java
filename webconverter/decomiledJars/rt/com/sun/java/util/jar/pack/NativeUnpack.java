package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.jar.JarOutputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

class NativeUnpack
{
  private long unpackerPtr;
  private BufferedInputStream in;
  private int _verbose;
  private long _byteCount;
  private int _segCount;
  private int _fileCount;
  private long _estByteLimit;
  private int _estSegLimit;
  private int _estFileLimit;
  private int _prevPercent = -1;
  private final CRC32 _crc32 = new CRC32();
  private byte[] _buf = new byte['䀀'];
  private UnpackerImpl _p200;
  private PropMap _props;
  
  private static synchronized native void initIDs();
  
  private synchronized native long start(ByteBuffer paramByteBuffer, long paramLong);
  
  private synchronized native boolean getNextFile(Object[] paramArrayOfObject);
  
  private synchronized native ByteBuffer getUnusedInput();
  
  private synchronized native long finish();
  
  protected synchronized native boolean setOption(String paramString1, String paramString2);
  
  protected synchronized native String getOption(String paramString);
  
  NativeUnpack(UnpackerImpl paramUnpackerImpl)
  {
    this._p200 = paramUnpackerImpl;
    this._props = paramUnpackerImpl.props;
    paramUnpackerImpl._nunp = this;
  }
  
  private static Object currentInstance()
  {
    UnpackerImpl localUnpackerImpl = (UnpackerImpl)Utils.getTLGlobals();
    return localUnpackerImpl == null ? null : localUnpackerImpl._nunp;
  }
  
  private synchronized long getUnpackerPtr()
  {
    return this.unpackerPtr;
  }
  
  private long readInputFn(ByteBuffer paramByteBuffer, long paramLong)
    throws IOException
  {
    if (this.in == null) {
      return 0L;
    }
    long l1 = paramByteBuffer.capacity() - paramByteBuffer.position();
    assert (paramLong <= l1);
    long l2 = 0L;
    int i = 0;
    while (l2 < paramLong)
    {
      i++;
      int j = this._buf.length;
      if (j > l1 - l2) {
        j = (int)(l1 - l2);
      }
      int k = this.in.read(this._buf, 0, j);
      if (k <= 0) {
        break;
      }
      l2 += k;
      assert (l2 <= l1);
      paramByteBuffer.put(this._buf, 0, k);
    }
    if (this._verbose > 1) {
      Utils.log.fine("readInputFn(" + paramLong + "," + l1 + ") => " + l2 + " steps=" + i);
    }
    if (l1 > 100L) {
      this._estByteLimit = (this._byteCount + l1);
    } else {
      this._estByteLimit = ((this._byteCount + l2) * 20L);
    }
    this._byteCount += l2;
    updateProgress();
    return l2;
  }
  
  private void updateProgress()
  {
    double d1 = this._segCount;
    if ((this._estByteLimit > 0L) && (this._byteCount > 0L)) {
      d1 += this._byteCount / this._estByteLimit;
    }
    double d2 = this._fileCount;
    double d3 = 0.33D * d1 / Math.max(this._estSegLimit, 1) + 0.67D * d2 / Math.max(this._estFileLimit, 1);
    int i = (int)Math.round(100.0D * d3);
    if (i > 100) {
      i = 100;
    }
    if (i > this._prevPercent)
    {
      this._prevPercent = i;
      this._props.setInteger("unpack.progress", i);
      if (this._verbose > 0) {
        Utils.log.info("progress = " + i);
      }
    }
  }
  
  private void copyInOption(String paramString)
  {
    String str = this._props.getProperty(paramString);
    if (this._verbose > 0) {
      Utils.log.info("set " + paramString + "=" + str);
    }
    if (str != null)
    {
      boolean bool = setOption(paramString, str);
      if (!bool) {
        Utils.log.warning("Invalid option " + paramString + "=" + str);
      }
    }
  }
  
  void run(InputStream paramInputStream, JarOutputStream paramJarOutputStream, ByteBuffer paramByteBuffer)
    throws IOException
  {
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream);
    this.in = localBufferedInputStream;
    this._verbose = this._props.getInteger("com.sun.java.util.jar.pack.verbose");
    int i = "keep".equals(this._props.getProperty("com.sun.java.util.jar.pack.unpack.modification.time", "0")) ? 0 : this._props.getTime("com.sun.java.util.jar.pack.unpack.modification.time");
    copyInOption("com.sun.java.util.jar.pack.verbose");
    copyInOption("unpack.deflate.hint");
    if (i == 0) {
      copyInOption("com.sun.java.util.jar.pack.unpack.modification.time");
    }
    updateProgress();
    for (;;)
    {
      long l1 = start(paramByteBuffer, 0L);
      this._byteCount = (this._estByteLimit = 0L);
      this._segCount += 1;
      int j = (int)(l1 >>> 32);
      int k = (int)(l1 >>> 0);
      this._estSegLimit = (this._segCount + j);
      double d = this._fileCount + k;
      this._estFileLimit = ((int)(d * this._estSegLimit / this._segCount));
      int[] arrayOfInt = { 0, 0, 0, 0 };
      Object[] arrayOfObject = { arrayOfInt, null, null, null };
      while (getNextFile(arrayOfObject))
      {
        String str = (String)arrayOfObject[1];
        long l3 = (arrayOfInt[0] << 32) + (arrayOfInt[1] << 32 >>> 32);
        long l4 = i != 0 ? i : arrayOfInt[2];
        boolean bool = arrayOfInt[3] != 0;
        ByteBuffer localByteBuffer1 = (ByteBuffer)arrayOfObject[2];
        ByteBuffer localByteBuffer2 = (ByteBuffer)arrayOfObject[3];
        writeEntry(paramJarOutputStream, str, l4, l3, bool, localByteBuffer1, localByteBuffer2);
        this._fileCount += 1;
        updateProgress();
      }
      paramByteBuffer = getUnusedInput();
      long l2 = finish();
      if (this._verbose > 0) {
        Utils.log.info("bytes consumed = " + l2);
      }
      if ((paramByteBuffer == null) && (!Utils.isPackMagic(Utils.readMagic(localBufferedInputStream)))) {
        break;
      }
      if ((this._verbose > 0) && (paramByteBuffer != null)) {
        Utils.log.info("unused input = " + paramByteBuffer);
      }
    }
  }
  
  void run(InputStream paramInputStream, JarOutputStream paramJarOutputStream)
    throws IOException
  {
    run(paramInputStream, paramJarOutputStream, null);
  }
  
  void run(File paramFile, JarOutputStream paramJarOutputStream)
    throws IOException
  {
    ByteBuffer localByteBuffer = null;
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    Object localObject1 = null;
    try
    {
      run(localFileInputStream, paramJarOutputStream, localByteBuffer);
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localFileInputStream != null) {
        if (localObject1 != null) {
          try
          {
            localFileInputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            localObject1.addSuppressed(localThrowable3);
          }
        } else {
          localFileInputStream.close();
        }
      }
    }
  }
  
  private void writeEntry(JarOutputStream paramJarOutputStream, String paramString, long paramLong1, long paramLong2, boolean paramBoolean, ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2)
    throws IOException
  {
    int i = (int)paramLong2;
    if (i != paramLong2) {
      throw new IOException("file too large: " + paramLong2);
    }
    CRC32 localCRC32 = this._crc32;
    if (this._verbose > 1) {
      Utils.log.fine("Writing entry: " + paramString + " size=" + i + (paramBoolean ? " deflated" : ""));
    }
    if (this._buf.length < i)
    {
      j = i;
      while (j < this._buf.length)
      {
        j <<= 1;
        if (j <= 0) {
          j = i;
        }
      }
      this._buf = new byte[j];
    }
    assert (this._buf.length >= i);
    int j = 0;
    int k;
    if (paramByteBuffer1 != null)
    {
      k = paramByteBuffer1.capacity();
      paramByteBuffer1.get(this._buf, j, k);
      j += k;
    }
    if (paramByteBuffer2 != null)
    {
      k = paramByteBuffer2.capacity();
      paramByteBuffer2.get(this._buf, j, k);
      j += k;
    }
    while (j < i)
    {
      k = this.in.read(this._buf, j, i - j);
      if (k <= 0) {
        throw new IOException("EOF at end of archive");
      }
      j += k;
    }
    ZipEntry localZipEntry = new ZipEntry(paramString);
    localZipEntry.setTime(paramLong1 * 1000L);
    if (i == 0)
    {
      localZipEntry.setMethod(0);
      localZipEntry.setSize(0L);
      localZipEntry.setCrc(0L);
      localZipEntry.setCompressedSize(0L);
    }
    else if (!paramBoolean)
    {
      localZipEntry.setMethod(0);
      localZipEntry.setSize(i);
      localZipEntry.setCompressedSize(i);
      localCRC32.reset();
      localCRC32.update(this._buf, 0, i);
      localZipEntry.setCrc(localCRC32.getValue());
    }
    else
    {
      localZipEntry.setMethod(8);
      localZipEntry.setSize(i);
    }
    paramJarOutputStream.putNextEntry(localZipEntry);
    if (i > 0) {
      paramJarOutputStream.write(this._buf, 0, i);
    }
    paramJarOutputStream.closeEntry();
    if (this._verbose > 0) {
      Utils.log.info("Writing " + Utils.zeString(localZipEntry));
    }
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("unpack");
        return null;
      }
    });
    initIDs();
  }
}
