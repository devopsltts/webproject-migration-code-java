package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import sun.util.logging.PlatformLogger;

class Utils
{
  static final String COM_PREFIX = "com.sun.java.util.jar.pack.";
  static final String METAINF = "META-INF";
  static final String DEBUG_VERBOSE = "com.sun.java.util.jar.pack.verbose";
  static final String DEBUG_DISABLE_NATIVE = "com.sun.java.util.jar.pack.disable.native";
  static final String PACK_DEFAULT_TIMEZONE = "com.sun.java.util.jar.pack.default.timezone";
  static final String UNPACK_MODIFICATION_TIME = "com.sun.java.util.jar.pack.unpack.modification.time";
  static final String UNPACK_STRIP_DEBUG = "com.sun.java.util.jar.pack.unpack.strip.debug";
  static final String UNPACK_REMOVE_PACKFILE = "com.sun.java.util.jar.pack.unpack.remove.packfile";
  static final String NOW = "now";
  static final String PACK_KEEP_CLASS_ORDER = "com.sun.java.util.jar.pack.keep.class.order";
  static final String PACK_ZIP_ARCHIVE_MARKER_COMMENT = "PACK200";
  static final String CLASS_FORMAT_ERROR = "com.sun.java.util.jar.pack.class.format.error";
  static final ThreadLocal<TLGlobals> currentInstance = new ThreadLocal();
  private static TimeZone tz;
  private static int workingPackerCount = 0;
  static final boolean nolog = Boolean.getBoolean("com.sun.java.util.jar.pack.nolog");
  static final boolean SORT_MEMBERS_DESCR_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.members.descr.major");
  static final boolean SORT_HANDLES_KIND_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.handles.kind.major");
  static final boolean SORT_INDY_BSS_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.indy.bss.major");
  static final boolean SORT_BSS_BSM_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.bss.bsm.major");
  static final Pack200Logger log = new Pack200Logger("java.util.jar.Pack200");
  
  static TLGlobals getTLGlobals()
  {
    return (TLGlobals)currentInstance.get();
  }
  
  static PropMap currentPropMap()
  {
    Object localObject = currentInstance.get();
    if ((localObject instanceof PackerImpl)) {
      return ((PackerImpl)localObject).props;
    }
    if ((localObject instanceof UnpackerImpl)) {
      return ((UnpackerImpl)localObject).props;
    }
    return null;
  }
  
  static synchronized void changeDefaultTimeZoneToUtc()
  {
    if (workingPackerCount++ == 0)
    {
      tz = TimeZone.getDefault();
      TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
  }
  
  static synchronized void restoreDefaultTimeZone()
  {
    if (--workingPackerCount == 0)
    {
      if (tz != null) {
        TimeZone.setDefault(tz);
      }
      tz = null;
    }
  }
  
  static String getVersionString()
  {
    return "Pack200, Vendor: " + System.getProperty("java.vendor") + ", Version: " + Constants.MAX_PACKAGE_VERSION;
  }
  
  static void markJarFile(JarOutputStream paramJarOutputStream)
    throws IOException
  {
    paramJarOutputStream.setComment("PACK200");
  }
  
  static void copyJarFile(JarInputStream paramJarInputStream, JarOutputStream paramJarOutputStream)
    throws IOException
  {
    if (paramJarInputStream.getManifest() != null)
    {
      localObject = new ZipEntry("META-INF/MANIFEST.MF");
      paramJarOutputStream.putNextEntry((ZipEntry)localObject);
      paramJarInputStream.getManifest().write(paramJarOutputStream);
      paramJarOutputStream.closeEntry();
    }
    Object localObject = new byte['䀀'];
    JarEntry localJarEntry;
    while ((localJarEntry = paramJarInputStream.getNextJarEntry()) != null)
    {
      paramJarOutputStream.putNextEntry(localJarEntry);
      int i;
      while (0 < (i = paramJarInputStream.read((byte[])localObject))) {
        paramJarOutputStream.write((byte[])localObject, 0, i);
      }
    }
    paramJarInputStream.close();
    markJarFile(paramJarOutputStream);
  }
  
  static void copyJarFile(JarFile paramJarFile, JarOutputStream paramJarOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['䀀'];
    Iterator localIterator = Collections.list(paramJarFile.entries()).iterator();
    while (localIterator.hasNext())
    {
      JarEntry localJarEntry = (JarEntry)localIterator.next();
      paramJarOutputStream.putNextEntry(localJarEntry);
      InputStream localInputStream = paramJarFile.getInputStream(localJarEntry);
      int i;
      while (0 < (i = localInputStream.read(arrayOfByte))) {
        paramJarOutputStream.write(arrayOfByte, 0, i);
      }
    }
    paramJarFile.close();
    markJarFile(paramJarOutputStream);
  }
  
  static void copyJarFile(JarInputStream paramJarInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream = new BufferedOutputStream(paramOutputStream);
    paramOutputStream = new NonCloser(paramOutputStream);
    JarOutputStream localJarOutputStream = new JarOutputStream(paramOutputStream);
    Object localObject1 = null;
    try
    {
      copyJarFile(paramJarInputStream, localJarOutputStream);
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localJarOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localJarOutputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            localObject1.addSuppressed(localThrowable3);
          }
        } else {
          localJarOutputStream.close();
        }
      }
    }
  }
  
  static void copyJarFile(JarFile paramJarFile, OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream = new BufferedOutputStream(paramOutputStream);
    paramOutputStream = new NonCloser(paramOutputStream);
    JarOutputStream localJarOutputStream = new JarOutputStream(paramOutputStream);
    Object localObject1 = null;
    try
    {
      copyJarFile(paramJarFile, localJarOutputStream);
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localJarOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localJarOutputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            localObject1.addSuppressed(localThrowable3);
          }
        } else {
          localJarOutputStream.close();
        }
      }
    }
  }
  
  static String getJarEntryName(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return paramString.replace(File.separatorChar, '/');
  }
  
  static String zeString(ZipEntry paramZipEntry)
  {
    int i = paramZipEntry.getCompressedSize() > 0L ? (int)((1.0D - paramZipEntry.getCompressedSize() / paramZipEntry.getSize()) * 100.0D) : 0;
    return paramZipEntry.getSize() + "\t" + paramZipEntry.getMethod() + "\t" + paramZipEntry.getCompressedSize() + "\t" + i + "%\t" + new Date(paramZipEntry.getTime()) + "\t" + Long.toHexString(paramZipEntry.getCrc()) + "\t" + paramZipEntry.getName();
  }
  
  static byte[] readMagic(BufferedInputStream paramBufferedInputStream)
    throws IOException
  {
    paramBufferedInputStream.mark(4);
    byte[] arrayOfByte = new byte[4];
    for (int i = 0; (i < arrayOfByte.length) && (1 == paramBufferedInputStream.read(arrayOfByte, i, 1)); i++) {}
    paramBufferedInputStream.reset();
    return arrayOfByte;
  }
  
  static boolean isJarMagic(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] == 80) && (paramArrayOfByte[1] == 75) && (paramArrayOfByte[2] >= 1) && (paramArrayOfByte[2] < 8) && (paramArrayOfByte[3] == paramArrayOfByte[2] + 1);
  }
  
  static boolean isPackMagic(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] == -54) && (paramArrayOfByte[1] == -2) && (paramArrayOfByte[2] == -48) && (paramArrayOfByte[3] == 13);
  }
  
  static boolean isGZIPMagic(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] == 31) && (paramArrayOfByte[1] == -117) && (paramArrayOfByte[2] == 8);
  }
  
  private Utils() {}
  
  private static class NonCloser
    extends FilterOutputStream
  {
    NonCloser(OutputStream paramOutputStream)
    {
      super();
    }
    
    public void close()
      throws IOException
    {
      flush();
    }
  }
  
  static class Pack200Logger
  {
    private final String name;
    private PlatformLogger log;
    
    Pack200Logger(String paramString)
    {
      this.name = paramString;
    }
    
    private synchronized PlatformLogger getLogger()
    {
      if (this.log == null) {
        this.log = PlatformLogger.getLogger(this.name);
      }
      return this.log;
    }
    
    public void warning(String paramString, Object paramObject)
    {
      getLogger().warning(paramString, new Object[] { paramObject });
    }
    
    public void warning(String paramString)
    {
      warning(paramString, null);
    }
    
    public void info(String paramString)
    {
      int i = Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
      if (i > 0) {
        if (Utils.nolog) {
          System.out.println(paramString);
        } else {
          getLogger().info(paramString);
        }
      }
    }
    
    public void fine(String paramString)
    {
      int i = Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
      if (i > 0) {
        System.out.println(paramString);
      }
    }
  }
}
