package com.sun.java.util.jar.pack;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200.Unpacker;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

public class UnpackerImpl
  extends TLGlobals
  implements Pack200.Unpacker
{
  Object _nunp;
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    this.props.addListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    this.props.removeListener(paramPropertyChangeListener);
  }
  
  public UnpackerImpl() {}
  
  public SortedMap<String, String> properties()
  {
    return this.props;
  }
  
  public String toString()
  {
    return Utils.getVersionString();
  }
  
  public synchronized void unpack(InputStream paramInputStream, JarOutputStream paramJarOutputStream)
    throws IOException
  {
    if (paramInputStream == null) {
      throw new NullPointerException("null input");
    }
    if (paramJarOutputStream == null) {
      throw new NullPointerException("null output");
    }
    assert (Utils.currentInstance.get() == null);
    int i = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone") ? 1 : 0;
    try
    {
      Utils.currentInstance.set(this);
      if (i != 0) {
        Utils.changeDefaultTimeZoneToUtc();
      }
      int j = this.props.getInteger("com.sun.java.util.jar.pack.verbose");
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream);
      if (Utils.isJarMagic(Utils.readMagic(localBufferedInputStream)))
      {
        if (j > 0) {
          Utils.log.info("Copying unpacked JAR file...");
        }
        Utils.copyJarFile(new JarInputStream(localBufferedInputStream), paramJarOutputStream);
      }
      else if (this.props.getBoolean("com.sun.java.util.jar.pack.disable.native"))
      {
        new DoUnpack(null).run(localBufferedInputStream, paramJarOutputStream);
        localBufferedInputStream.close();
        Utils.markJarFile(paramJarOutputStream);
      }
      else
      {
        try
        {
          new NativeUnpack(this).run(localBufferedInputStream, paramJarOutputStream);
        }
        catch (UnsatisfiedLinkError|NoClassDefFoundError localUnsatisfiedLinkError)
        {
          new DoUnpack(null).run(localBufferedInputStream, paramJarOutputStream);
        }
        localBufferedInputStream.close();
        Utils.markJarFile(paramJarOutputStream);
      }
    }
    finally
    {
      this._nunp = null;
      Utils.currentInstance.set(null);
      if (i != 0) {
        Utils.restoreDefaultTimeZone();
      }
    }
  }
  
  public synchronized void unpack(File paramFile, JarOutputStream paramJarOutputStream)
    throws IOException
  {
    if (paramFile == null) {
      throw new NullPointerException("null input");
    }
    if (paramJarOutputStream == null) {
      throw new NullPointerException("null output");
    }
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    Object localObject1 = null;
    try
    {
      unpack(localFileInputStream, paramJarOutputStream);
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
    if (this.props.getBoolean("com.sun.java.util.jar.pack.unpack.remove.packfile")) {
      paramFile.delete();
    }
  }
  
  private class DoUnpack
  {
    final int verbose = UnpackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.verbose");
    final Package pkg;
    final boolean keepModtime;
    final boolean keepDeflateHint;
    final int modtime;
    final boolean deflateHint;
    final CRC32 crc;
    final ByteArrayOutputStream bufOut;
    final OutputStream crcOut;
    
    private DoUnpack()
    {
      UnpackerImpl.this.props.setInteger("unpack.progress", 0);
      this.pkg = new Package();
      this.keepModtime = "keep".equals(UnpackerImpl.this.props.getProperty("com.sun.java.util.jar.pack.unpack.modification.time", "keep"));
      this.keepDeflateHint = "keep".equals(UnpackerImpl.this.props.getProperty("unpack.deflate.hint", "keep"));
      if (!this.keepModtime) {
        this.modtime = UnpackerImpl.this.props.getTime("com.sun.java.util.jar.pack.unpack.modification.time");
      } else {
        this.modtime = this.pkg.default_modtime;
      }
      this.deflateHint = (this.keepDeflateHint ? false : UnpackerImpl.this.props.getBoolean("unpack.deflate.hint"));
      this.crc = new CRC32();
      this.bufOut = new ByteArrayOutputStream();
      this.crcOut = new CheckedOutputStream(this.bufOut, this.crc);
    }
    
    public void run(BufferedInputStream paramBufferedInputStream, JarOutputStream paramJarOutputStream)
      throws IOException
    {
      if (this.verbose > 0) {
        UnpackerImpl.this.props.list(System.out);
      }
      for (int i = 1;; i++)
      {
        unpackSegment(paramBufferedInputStream, paramJarOutputStream);
        if (!Utils.isPackMagic(Utils.readMagic(paramBufferedInputStream))) {
          break;
        }
        if (this.verbose > 0) {
          Utils.log.info("Finished segment #" + i);
        }
      }
    }
    
    private void unpackSegment(InputStream paramInputStream, JarOutputStream paramJarOutputStream)
      throws IOException
    {
      UnpackerImpl.this.props.setProperty("unpack.progress", "0");
      new PackageReader(this.pkg, paramInputStream).read();
      if (UnpackerImpl.this.props.getBoolean("unpack.strip.debug")) {
        this.pkg.stripAttributeKind("Debug");
      }
      if (UnpackerImpl.this.props.getBoolean("unpack.strip.compile")) {
        this.pkg.stripAttributeKind("Compile");
      }
      UnpackerImpl.this.props.setProperty("unpack.progress", "50");
      this.pkg.ensureAllClassFiles();
      HashSet localHashSet = new HashSet(this.pkg.getClasses());
      Iterator localIterator = this.pkg.getFiles().iterator();
      while (localIterator.hasNext())
      {
        Package.File localFile = (Package.File)localIterator.next();
        String str = localFile.nameString;
        JarEntry localJarEntry = new JarEntry(Utils.getJarEntryName(str));
        boolean bool = this.keepDeflateHint ? false : ((localFile.options & 0x1) != 0) || ((this.pkg.default_options & 0x20) != 0) ? true : this.deflateHint;
        int i = !bool ? 1 : 0;
        if (i != 0) {
          this.crc.reset();
        }
        this.bufOut.reset();
        if (localFile.isClassStub())
        {
          Package.Class localClass = localFile.getStubClass();
          assert (localClass != null);
          new ClassWriter(localClass, i != 0 ? this.crcOut : this.bufOut).write();
          localHashSet.remove(localClass);
        }
        else
        {
          localFile.writeTo(i != 0 ? this.crcOut : this.bufOut);
        }
        localJarEntry.setMethod(bool ? 8 : 0);
        if (i != 0)
        {
          if (this.verbose > 0) {
            Utils.log.info("stored size=" + this.bufOut.size() + " and crc=" + this.crc.getValue());
          }
          localJarEntry.setMethod(0);
          localJarEntry.setSize(this.bufOut.size());
          localJarEntry.setCrc(this.crc.getValue());
        }
        if (this.keepModtime)
        {
          localJarEntry.setTime(localFile.modtime);
          localJarEntry.setTime(localFile.modtime * 1000L);
        }
        else
        {
          localJarEntry.setTime(this.modtime * 1000L);
        }
        paramJarOutputStream.putNextEntry(localJarEntry);
        this.bufOut.writeTo(paramJarOutputStream);
        paramJarOutputStream.closeEntry();
        if (this.verbose > 0) {
          Utils.log.info("Writing " + Utils.zeString(localJarEntry));
        }
      }
      assert (localHashSet.isEmpty());
      UnpackerImpl.this.props.setProperty("unpack.progress", "100");
      this.pkg.reset();
    }
  }
}
