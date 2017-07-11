package com.sun.java.util.jar.pack;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.jar.Pack200.Packer;

public class PackerImpl
  extends TLGlobals
  implements Pack200.Packer
{
  public PackerImpl() {}
  
  public SortedMap<String, String> properties()
  {
    return this.props;
  }
  
  public synchronized void pack(JarFile paramJarFile, OutputStream paramOutputStream)
    throws IOException
  {
    assert (Utils.currentInstance.get() == null);
    int i = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone") ? 1 : 0;
    try
    {
      Utils.currentInstance.set(this);
      if (i != 0) {
        Utils.changeDefaultTimeZoneToUtc();
      }
      if ("0".equals(this.props.getProperty("pack.effort"))) {
        Utils.copyJarFile(paramJarFile, paramOutputStream);
      } else {
        new DoPack(null).run(paramJarFile, paramOutputStream);
      }
    }
    finally
    {
      Utils.currentInstance.set(null);
      if (i != 0) {
        Utils.restoreDefaultTimeZone();
      }
      paramJarFile.close();
    }
  }
  
  public synchronized void pack(JarInputStream paramJarInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    assert (Utils.currentInstance.get() == null);
    int i = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone") ? 1 : 0;
    try
    {
      Utils.currentInstance.set(this);
      if (i != 0) {
        Utils.changeDefaultTimeZoneToUtc();
      }
      if ("0".equals(this.props.getProperty("pack.effort"))) {
        Utils.copyJarFile(paramJarInputStream, paramOutputStream);
      } else {
        new DoPack(null).run(paramJarInputStream, paramOutputStream);
      }
    }
    finally
    {
      Utils.currentInstance.set(null);
      if (i != 0) {
        Utils.restoreDefaultTimeZone();
      }
      paramJarInputStream.close();
    }
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    this.props.addListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    this.props.removeListener(paramPropertyChangeListener);
  }
  
  private class DoPack
  {
    final int verbose = PackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.verbose");
    final Package pkg;
    final String unknownAttrCommand;
    final String classFormatCommand;
    final Map<Attribute.Layout, Attribute> attrDefs;
    final Map<Attribute.Layout, String> attrCommands;
    final boolean keepFileOrder;
    final boolean keepClassOrder;
    final boolean keepModtime;
    final boolean latestModtime;
    final boolean keepDeflateHint;
    long totalOutputSize;
    int segmentCount;
    long segmentTotalSize;
    long segmentSize;
    final long segmentLimit;
    final List<String> passFiles;
    private int nread;
    
    private DoPack()
    {
      PackerImpl.this.props.setInteger("pack.progress", 0);
      if (this.verbose > 0) {
        Utils.log.info(PackerImpl.this.props.toString());
      }
      this.pkg = new Package(Package.Version.makeVersion(PackerImpl.this.props, "min.class"), Package.Version.makeVersion(PackerImpl.this.props, "max.class"), Package.Version.makeVersion(PackerImpl.this.props, "package"));
      Object localObject1 = PackerImpl.this.props.getProperty("pack.unknown.attribute", "pass");
      if ((!"strip".equals(localObject1)) && (!"pass".equals(localObject1)) && (!"error".equals(localObject1))) {
        throw new RuntimeException("Bad option: pack.unknown.attribute = " + (String)localObject1);
      }
      this.unknownAttrCommand = ((String)localObject1).intern();
      localObject1 = PackerImpl.this.props.getProperty("com.sun.java.util.jar.pack.class.format.error", "pass");
      if ((!"pass".equals(localObject1)) && (!"error".equals(localObject1))) {
        throw new RuntimeException("Bad option: com.sun.java.util.jar.pack.class.format.error = " + (String)localObject1);
      }
      this.classFormatCommand = ((String)localObject1).intern();
      localObject1 = new HashMap();
      Object localObject2 = new HashMap();
      String[] arrayOfString = { "pack.class.attribute.", "pack.field.attribute.", "pack.method.attribute.", "pack.code.attribute." };
      int[] arrayOfInt = { 0, 1, 2, 3 };
      for (int k = 0; k < arrayOfInt.length; k++)
      {
        String str1 = arrayOfString[k];
        SortedMap localSortedMap = PackerImpl.this.props.prefixMap(str1);
        Iterator localIterator = localSortedMap.keySet().iterator();
        while (localIterator.hasNext())
        {
          String str2 = (String)localIterator.next();
          assert (str2.startsWith(str1));
          String str3 = str2.substring(str1.length());
          String str4 = PackerImpl.this.props.getProperty(str2);
          Attribute.Layout localLayout = Attribute.keyForLookup(arrayOfInt[k], str3);
          if (("strip".equals(str4)) || ("pass".equals(str4)) || ("error".equals(str4)))
          {
            ((Map)localObject2).put(localLayout, str4.intern());
          }
          else
          {
            Attribute.define((Map)localObject1, arrayOfInt[k], str3, str4);
            if (this.verbose > 1) {
              Utils.log.fine("Added layout for " + Constants.ATTR_CONTEXT_NAME[k] + " attribute " + str3 + " = " + str4);
            }
            assert (((Map)localObject1).containsKey(localLayout));
          }
        }
      }
      this.attrDefs = (((Map)localObject1).isEmpty() ? null : (Map)localObject1);
      this.attrCommands = (((Map)localObject2).isEmpty() ? null : (Map)localObject2);
      this.keepFileOrder = PackerImpl.this.props.getBoolean("pack.keep.file.order");
      this.keepClassOrder = PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.keep.class.order");
      this.keepModtime = "keep".equals(PackerImpl.this.props.getProperty("pack.modification.time"));
      this.latestModtime = "latest".equals(PackerImpl.this.props.getProperty("pack.modification.time"));
      this.keepDeflateHint = "keep".equals(PackerImpl.this.props.getProperty("pack.deflate.hint"));
      if ((!this.keepModtime) && (!this.latestModtime))
      {
        int i = PackerImpl.this.props.getTime("pack.modification.time");
        if (i != 0) {
          this.pkg.default_modtime = i;
        }
      }
      if (!this.keepDeflateHint)
      {
        boolean bool = PackerImpl.this.props.getBoolean("pack.deflate.hint");
        if (bool) {
          this.pkg.default_options |= 0x20;
        }
      }
      this.totalOutputSize = 0L;
      this.segmentCount = 0;
      this.segmentTotalSize = 0L;
      this.segmentSize = 0L;
      if (PackerImpl.this.props.getProperty("pack.segment.limit", "").equals("")) {
        l = -1L;
      } else {
        l = PackerImpl.this.props.getLong("pack.segment.limit");
      }
      long l = Math.min(2147483647L, l);
      l = Math.max(-1L, l);
      if (l == -1L) {
        l = Long.MAX_VALUE;
      }
      this.segmentLimit = l;
      this.passFiles = PackerImpl.this.props.getProperties("pack.pass.file.");
      ListIterator localListIterator = this.passFiles.listIterator();
      while (localListIterator.hasNext())
      {
        localObject2 = (String)localListIterator.next();
        if (localObject2 == null)
        {
          localListIterator.remove();
        }
        else
        {
          localObject2 = Utils.getJarEntryName((String)localObject2);
          if (((String)localObject2).endsWith("/")) {
            localObject2 = ((String)localObject2).substring(0, ((String)localObject2).length() - 1);
          }
          localListIterator.set(localObject2);
        }
      }
      if (this.verbose > 0) {
        Utils.log.info("passFiles = " + this.passFiles);
      }
      int j = PackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.archive.options");
      if (j != 0) {
        this.pkg.default_options |= j;
      }
      this.nread = 0;
    }
    
    boolean isClassFile(String paramString)
    {
      if (!paramString.endsWith(".class")) {
        return false;
      }
      int i;
      for (String str = paramString;; str = str.substring(0, i))
      {
        if (this.passFiles.contains(str)) {
          return false;
        }
        i = str.lastIndexOf('/');
        if (i < 0) {
          break;
        }
      }
      return true;
    }
    
    boolean isMetaInfFile(String paramString)
    {
      return (paramString.startsWith("/META-INF")) || (paramString.startsWith("META-INF"));
    }
    
    private void makeNextPackage()
    {
      this.pkg.reset();
    }
    
    private void noteRead(InFile paramInFile)
    {
      this.nread += 1;
      if (this.verbose > 2) {
        Utils.log.fine("...read " + paramInFile.name);
      }
      if ((this.verbose > 0) && (this.nread % 1000 == 0)) {
        Utils.log.info("Have read " + this.nread + " files...");
      }
    }
    
    void run(JarInputStream paramJarInputStream, OutputStream paramOutputStream)
      throws IOException
    {
      Object localObject1;
      Object localObject2;
      if (paramJarInputStream.getManifest() != null)
      {
        localObject1 = new ByteArrayOutputStream();
        paramJarInputStream.getManifest().write((OutputStream)localObject1);
        localObject2 = new ByteArrayInputStream(((ByteArrayOutputStream)localObject1).toByteArray());
        this.pkg.addFile(readFile("META-INF/MANIFEST.MF", (InputStream)localObject2));
      }
      while ((localObject1 = paramJarInputStream.getNextJarEntry()) != null)
      {
        localObject2 = new InFile((JarEntry)localObject1);
        String str = ((InFile)localObject2).name;
        Package.File localFile1 = readFile(str, paramJarInputStream);
        Package.File localFile2 = null;
        long l = isMetaInfFile(str) ? 0L : ((InFile)localObject2).getInputLength();
        if (this.segmentSize += l > this.segmentLimit)
        {
          this.segmentSize -= l;
          int i = -1;
          flushPartial(paramOutputStream, i);
        }
        if (this.verbose > 1) {
          Utils.log.fine("Reading " + str);
        }
        assert (((JarEntry)localObject1).isDirectory() == str.endsWith("/"));
        if (isClassFile(str)) {
          localFile2 = readClass(str, localFile1.getInputStream());
        }
        if (localFile2 == null)
        {
          localFile2 = localFile1;
          this.pkg.addFile(localFile2);
        }
        ((InFile)localObject2).copyTo(localFile2);
        noteRead((InFile)localObject2);
      }
      flushAll(paramOutputStream);
    }
    
    void run(JarFile paramJarFile, OutputStream paramOutputStream)
      throws IOException
    {
      List localList = scanJar(paramJarFile);
      if (this.verbose > 0) {
        Utils.log.info("Reading " + localList.size() + " files...");
      }
      int i = 0;
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        InFile localInFile = (InFile)localIterator.next();
        String str = localInFile.name;
        long l = isMetaInfFile(str) ? 0L : localInFile.getInputLength();
        if (this.segmentSize += l > this.segmentLimit)
        {
          this.segmentSize -= l;
          float f1 = i + 1;
          float f2 = this.segmentCount + 1;
          float f3 = localList.size() - f1;
          float f4 = f3 * (f2 / f1);
          if (this.verbose > 1) {
            Utils.log.fine("Estimated segments to do: " + f4);
          }
          flushPartial(paramOutputStream, (int)Math.ceil(f4));
        }
        InputStream localInputStream = localInFile.getInputStream();
        if (this.verbose > 1) {
          Utils.log.fine("Reading " + str);
        }
        Package.File localFile = null;
        if (isClassFile(str))
        {
          localFile = readClass(str, localInputStream);
          if (localFile == null)
          {
            localInputStream.close();
            localInputStream = localInFile.getInputStream();
          }
        }
        if (localFile == null)
        {
          localFile = readFile(str, localInputStream);
          this.pkg.addFile(localFile);
        }
        localInFile.copyTo(localFile);
        localInputStream.close();
        noteRead(localInFile);
        i++;
      }
      flushAll(paramOutputStream);
    }
    
    Package.File readClass(String paramString, InputStream paramInputStream)
      throws IOException
    {
      Package tmp8_5 = this.pkg;
      tmp8_5.getClass();
      Package.Class localClass = new Package.Class(tmp8_5, paramString);
      paramInputStream = new BufferedInputStream(paramInputStream);
      ClassReader localClassReader = new ClassReader(localClass, paramInputStream);
      localClassReader.setAttrDefs(this.attrDefs);
      localClassReader.setAttrCommands(this.attrCommands);
      localClassReader.unknownAttrCommand = this.unknownAttrCommand;
      try
      {
        localClassReader.read();
      }
      catch (IOException localIOException)
      {
        String str = "Passing class file uncompressed due to";
        Object localObject;
        if ((localIOException instanceof Attribute.FormatException))
        {
          localObject = (Attribute.FormatException)localIOException;
          if (((Attribute.FormatException)localObject).layout.equals("pass"))
          {
            Utils.log.info(((Attribute.FormatException)localObject).toString());
            Utils.log.warning(str + " unrecognized attribute: " + paramString);
            return null;
          }
        }
        else if ((localIOException instanceof ClassReader.ClassFormatException))
        {
          localObject = (ClassReader.ClassFormatException)localIOException;
          if (this.classFormatCommand.equals("pass"))
          {
            Utils.log.info(((ClassReader.ClassFormatException)localObject).toString());
            Utils.log.warning(str + " unknown class format: " + paramString);
            return null;
          }
        }
        throw localIOException;
      }
      this.pkg.addClass(localClass);
      return localClass.file;
    }
    
    Package.File readFile(String paramString, InputStream paramInputStream)
      throws IOException
    {
      Package tmp8_5 = this.pkg;
      tmp8_5.getClass();
      Package.File localFile = new Package.File(tmp8_5, paramString);
      localFile.readFrom(paramInputStream);
      if ((localFile.isDirectory()) && (localFile.getFileLength() != 0L)) {
        throw new IllegalArgumentException("Non-empty directory: " + localFile.getFileName());
      }
      return localFile;
    }
    
    void flushPartial(OutputStream paramOutputStream, int paramInt)
      throws IOException
    {
      if ((this.pkg.files.isEmpty()) && (this.pkg.classes.isEmpty())) {
        return;
      }
      flushPackage(paramOutputStream, Math.max(1, paramInt));
      PackerImpl.this.props.setInteger("pack.progress", 25);
      makeNextPackage();
      this.segmentCount += 1;
      this.segmentTotalSize += this.segmentSize;
      this.segmentSize = 0L;
    }
    
    void flushAll(OutputStream paramOutputStream)
      throws IOException
    {
      PackerImpl.this.props.setInteger("pack.progress", 50);
      flushPackage(paramOutputStream, 0);
      paramOutputStream.flush();
      PackerImpl.this.props.setInteger("pack.progress", 100);
      this.segmentCount += 1;
      this.segmentTotalSize += this.segmentSize;
      this.segmentSize = 0L;
      if ((this.verbose > 0) && (this.segmentCount > 1)) {
        Utils.log.info("Transmitted " + this.segmentTotalSize + " input bytes in " + this.segmentCount + " segments totaling " + this.totalOutputSize + " bytes");
      }
    }
    
    void flushPackage(OutputStream paramOutputStream, int paramInt)
      throws IOException
    {
      int i = this.pkg.files.size();
      if (!this.keepFileOrder)
      {
        if (this.verbose > 1) {
          Utils.log.fine("Reordering files.");
        }
        boolean bool = true;
        this.pkg.reorderFiles(this.keepClassOrder, bool);
      }
      else
      {
        assert (this.pkg.files.containsAll(this.pkg.getClassStubs()));
        localObject = this.pkg.files;
        if ((($assertionsDisabled) || ((localObject = new ArrayList(this.pkg.files)).retainAll(this.pkg.getClassStubs()))) || ((!$assertionsDisabled) && (!((List)localObject).equals(this.pkg.getClassStubs())))) {
          throw new AssertionError();
        }
      }
      this.pkg.trimStubs();
      if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.debug")) {
        this.pkg.stripAttributeKind("Debug");
      }
      if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.compile")) {
        this.pkg.stripAttributeKind("Compile");
      }
      if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.constants")) {
        this.pkg.stripAttributeKind("Constant");
      }
      if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.exceptions")) {
        this.pkg.stripAttributeKind("Exceptions");
      }
      if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.innerclasses")) {
        this.pkg.stripAttributeKind("InnerClasses");
      }
      Object localObject = new PackageWriter(this.pkg, paramOutputStream);
      ((PackageWriter)localObject).archiveNextCount = paramInt;
      ((PackageWriter)localObject).write();
      paramOutputStream.flush();
      if (this.verbose > 0)
      {
        long l1 = ((PackageWriter)localObject).archiveSize0 + ((PackageWriter)localObject).archiveSize1;
        this.totalOutputSize += l1;
        long l2 = this.segmentSize;
        Utils.log.info("Transmitted " + i + " files of " + l2 + " input bytes in a segment of " + l1 + " bytes");
      }
    }
    
    List<InFile> scanJar(JarFile paramJarFile)
      throws IOException
    {
      ArrayList localArrayList = new ArrayList();
      try
      {
        Iterator localIterator = Collections.list(paramJarFile.entries()).iterator();
        while (localIterator.hasNext())
        {
          JarEntry localJarEntry = (JarEntry)localIterator.next();
          InFile localInFile = new InFile(paramJarFile, localJarEntry);
          assert (localJarEntry.isDirectory() == localInFile.name.endsWith("/"));
          localArrayList.add(localInFile);
        }
      }
      catch (IllegalStateException localIllegalStateException)
      {
        throw new IOException(localIllegalStateException.getLocalizedMessage(), localIllegalStateException);
      }
      return localArrayList;
    }
    
    final class InFile
    {
      final String name;
      final JarFile jf;
      final JarEntry je;
      final File f;
      int modtime = 0;
      int options;
      
      InFile(String paramString)
      {
        this.name = Utils.getJarEntryName(paramString);
        this.f = new File(paramString);
        this.jf = null;
        this.je = null;
        int i = getModtime(this.f.lastModified());
        if ((PackerImpl.DoPack.this.keepModtime) && (i != 0)) {
          this.modtime = i;
        } else if ((PackerImpl.DoPack.this.latestModtime) && (i > PackerImpl.DoPack.this.pkg.default_modtime)) {
          PackerImpl.DoPack.this.pkg.default_modtime = i;
        }
      }
      
      InFile(JarFile paramJarFile, JarEntry paramJarEntry)
      {
        this.name = Utils.getJarEntryName(paramJarEntry.getName());
        this.f = null;
        this.jf = paramJarFile;
        this.je = paramJarEntry;
        int i = getModtime(paramJarEntry.getTime());
        if ((PackerImpl.DoPack.this.keepModtime) && (i != 0)) {
          this.modtime = i;
        } else if ((PackerImpl.DoPack.this.latestModtime) && (i > PackerImpl.DoPack.this.pkg.default_modtime)) {
          PackerImpl.DoPack.this.pkg.default_modtime = i;
        }
        if ((PackerImpl.DoPack.this.keepDeflateHint) && (paramJarEntry.getMethod() == 8)) {
          this.options |= 0x1;
        }
      }
      
      InFile(JarEntry paramJarEntry)
      {
        this(null, paramJarEntry);
      }
      
      long getInputLength()
      {
        long l = this.je != null ? this.je.getSize() : this.f.length();
        assert (l >= 0L) : (this + ".len=" + l);
        return Math.max(0L, l) + this.name.length() + 5L;
      }
      
      int getModtime(long paramLong)
      {
        long l = (paramLong + 500L) / 1000L;
        if ((int)l == l) {
          return (int)l;
        }
        Utils.log.warning("overflow in modtime for " + this.f);
        return 0;
      }
      
      void copyTo(Package.File paramFile)
      {
        if (this.modtime != 0) {
          paramFile.modtime = this.modtime;
        }
        paramFile.options |= this.options;
      }
      
      InputStream getInputStream()
        throws IOException
      {
        if (this.jf != null) {
          return this.jf.getInputStream(this.je);
        }
        return new FileInputStream(this.f);
      }
      
      public String toString()
      {
        return this.name;
      }
    }
  }
}
