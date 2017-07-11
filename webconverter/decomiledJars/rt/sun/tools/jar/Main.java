package sun.tools.jar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.jar.Pack200.Unpacker;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import sun.misc.JarIndex;

public class Main
{
  String program;
  PrintStream out;
  PrintStream err;
  String fname;
  String mname;
  String ename;
  String zname = "";
  String[] files;
  String rootjar = null;
  Map<String, File> entryMap = new HashMap();
  Set<File> entries = new LinkedHashSet();
  Set<String> paths = new HashSet();
  boolean cflag;
  boolean uflag;
  boolean xflag;
  boolean tflag;
  boolean vflag;
  boolean flag0;
  boolean Mflag;
  boolean iflag;
  boolean nflag;
  boolean pflag;
  static final String MANIFEST_DIR = "META-INF/";
  static final String VERSION = "1.0";
  private static ResourceBundle rsrc;
  private static final boolean useExtractionTime;
  private boolean ok;
  private byte[] copyBuf = new byte[' '];
  private HashSet<String> jarPaths = new HashSet();
  
  private String getMsg(String paramString)
  {
    try
    {
      return rsrc.getString(paramString);
    }
    catch (MissingResourceException localMissingResourceException)
    {
      throw new Error("Error in message file");
    }
  }
  
  private String formatMsg(String paramString1, String paramString2)
  {
    String str = getMsg(paramString1);
    String[] arrayOfString = new String[1];
    arrayOfString[0] = paramString2;
    return MessageFormat.format(str, (Object[])arrayOfString);
  }
  
  private String formatMsg2(String paramString1, String paramString2, String paramString3)
  {
    String str = getMsg(paramString1);
    String[] arrayOfString = new String[2];
    arrayOfString[0] = paramString2;
    arrayOfString[1] = paramString3;
    return MessageFormat.format(str, (Object[])arrayOfString);
  }
  
  public Main(PrintStream paramPrintStream1, PrintStream paramPrintStream2, String paramString)
  {
    this.out = paramPrintStream1;
    this.err = paramPrintStream2;
    this.program = paramString;
  }
  
  private static File createTempFileInSameDirectoryAs(File paramFile)
    throws IOException
  {
    File localFile = paramFile.getParentFile();
    if (localFile == null) {
      localFile = new File(".");
    }
    return File.createTempFile("jartmp", null, localFile);
  }
  
  public synchronized boolean run(String[] paramArrayOfString)
  {
    this.ok = true;
    if (!parseArgs(paramArrayOfString)) {
      return false;
    }
    try
    {
      if (((this.cflag) || (this.uflag)) && (this.fname != null))
      {
        this.zname = this.fname.replace(File.separatorChar, '/');
        if (this.zname.startsWith("./")) {
          this.zname = this.zname.substring(2);
        }
      }
      Object localObject1;
      Object localObject2;
      Object localObject3;
      Object localObject4;
      Object localObject5;
      if (this.cflag)
      {
        localObject1 = null;
        localObject2 = null;
        if (!this.Mflag)
        {
          if (this.mname != null)
          {
            localObject2 = new FileInputStream(this.mname);
            localObject1 = new Manifest(new BufferedInputStream((InputStream)localObject2));
          }
          else
          {
            localObject1 = new Manifest();
          }
          addVersion((Manifest)localObject1);
          addCreatedBy((Manifest)localObject1);
          if (isAmbiguousMainClass((Manifest)localObject1))
          {
            if (localObject2 != null) {
              ((InputStream)localObject2).close();
            }
            return false;
          }
          if (this.ename != null) {
            addMainClass((Manifest)localObject1, this.ename);
          }
        }
        expand(null, this.files, false);
        if (this.fname != null)
        {
          localObject3 = new FileOutputStream(this.fname);
        }
        else
        {
          localObject3 = new FileOutputStream(FileDescriptor.out);
          if (this.vflag) {
            this.vflag = false;
          }
        }
        localObject4 = null;
        localObject5 = localObject3;
        String str = this.fname == null ? "tmpjar" : this.fname.substring(this.fname.indexOf(File.separatorChar) + 1);
        if (this.nflag)
        {
          localObject4 = createTemporaryFile(str, ".jar");
          localObject3 = new FileOutputStream((File)localObject4);
        }
        create(new BufferedOutputStream((OutputStream)localObject3, 4096), (Manifest)localObject1);
        if (localObject2 != null) {
          ((InputStream)localObject2).close();
        }
        ((OutputStream)localObject3).close();
        if (this.nflag)
        {
          JarFile localJarFile = null;
          File localFile = null;
          JarOutputStream localJarOutputStream = null;
          try
          {
            Pack200.Packer localPacker = Pack200.newPacker();
            SortedMap localSortedMap = localPacker.properties();
            localSortedMap.put("pack.effort", "1");
            localJarFile = new JarFile(((File)localObject4).getCanonicalPath());
            localFile = createTemporaryFile(str, ".pack");
            localObject3 = new FileOutputStream(localFile);
            localPacker.pack(localJarFile, (OutputStream)localObject3);
            localJarOutputStream = new JarOutputStream(localObject5);
            Pack200.Unpacker localUnpacker = Pack200.newUnpacker();
            localUnpacker.unpack(localFile, localJarOutputStream);
          }
          catch (IOException localIOException2)
          {
            fatalError(localIOException2);
          }
          finally
          {
            if (localJarFile != null) {
              localJarFile.close();
            }
            if (localObject3 != null) {
              ((OutputStream)localObject3).close();
            }
            if (localJarOutputStream != null) {
              localJarOutputStream.close();
            }
            if ((localObject4 != null) && (((File)localObject4).exists())) {
              ((File)localObject4).delete();
            }
            if ((localFile != null) && (localFile.exists())) {
              localFile.delete();
            }
          }
        }
      }
      else if (this.uflag)
      {
        localObject1 = null;
        localObject2 = null;
        if (this.fname != null)
        {
          localObject1 = new File(this.fname);
          localObject2 = createTempFileInSameDirectoryAs((File)localObject1);
          localObject3 = new FileInputStream((File)localObject1);
          localObject4 = new FileOutputStream((File)localObject2);
        }
        else
        {
          localObject3 = new FileInputStream(FileDescriptor.in);
          localObject4 = new FileOutputStream(FileDescriptor.out);
          this.vflag = false;
        }
        localObject5 = (!this.Mflag) && (this.mname != null) ? new FileInputStream(this.mname) : null;
        expand(null, this.files, true);
        boolean bool = update((InputStream)localObject3, new BufferedOutputStream((OutputStream)localObject4), localObject5, null);
        if (this.ok) {
          this.ok = bool;
        }
        ((FileInputStream)localObject3).close();
        ((FileOutputStream)localObject4).close();
        if (localObject5 != null) {
          localObject5.close();
        }
        if ((this.ok) && (this.fname != null))
        {
          ((File)localObject1).delete();
          if (!((File)localObject2).renameTo((File)localObject1))
          {
            ((File)localObject2).delete();
            throw new IOException(getMsg("error.write.file"));
          }
          ((File)localObject2).delete();
        }
      }
      else if (this.tflag)
      {
        replaceFSC(this.files);
        if (this.fname != null)
        {
          list(this.fname, this.files);
        }
        else
        {
          localObject1 = new FileInputStream(FileDescriptor.in);
          try
          {
            list(new BufferedInputStream((InputStream)localObject1), this.files);
          }
          finally
          {
            ((InputStream)localObject1).close();
          }
        }
      }
      else if (this.xflag)
      {
        replaceFSC(this.files);
        if ((this.fname != null) && (this.files != null))
        {
          extract(this.fname, this.files);
        }
        else
        {
          localObject1 = this.fname == null ? new FileInputStream(FileDescriptor.in) : new FileInputStream(this.fname);
          try
          {
            extract(new BufferedInputStream((InputStream)localObject1), this.files);
          }
          finally
          {
            ((InputStream)localObject1).close();
          }
        }
      }
      else if (this.iflag)
      {
        genIndex(this.rootjar, this.files);
      }
    }
    catch (IOException localIOException1)
    {
      fatalError(localIOException1);
      this.ok = false;
    }
    catch (Error localError)
    {
      localError.printStackTrace();
      this.ok = false;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      this.ok = false;
    }
    this.out.flush();
    this.err.flush();
    return this.ok;
  }
  
  boolean parseArgs(String[] paramArrayOfString)
  {
    try
    {
      paramArrayOfString = CommandLine.parse(paramArrayOfString);
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      fatalError(formatMsg("error.cant.open", localFileNotFoundException.getMessage()));
      return false;
    }
    catch (IOException localIOException)
    {
      fatalError(localIOException);
      return false;
    }
    int i = 1;
    int k;
    try
    {
      String str1 = paramArrayOfString[0];
      if (str1.startsWith("-")) {
        str1 = str1.substring(1);
      }
      for (k = 0; k < str1.length(); k++) {
        switch (str1.charAt(k))
        {
        case 'c': 
          if ((this.xflag) || (this.tflag) || (this.uflag) || (this.iflag))
          {
            usageError();
            return false;
          }
          this.cflag = true;
          break;
        case 'u': 
          if ((this.cflag) || (this.xflag) || (this.tflag) || (this.iflag))
          {
            usageError();
            return false;
          }
          this.uflag = true;
          break;
        case 'x': 
          if ((this.cflag) || (this.uflag) || (this.tflag) || (this.iflag))
          {
            usageError();
            return false;
          }
          this.xflag = true;
          break;
        case 't': 
          if ((this.cflag) || (this.uflag) || (this.xflag) || (this.iflag))
          {
            usageError();
            return false;
          }
          this.tflag = true;
          break;
        case 'M': 
          this.Mflag = true;
          break;
        case 'v': 
          this.vflag = true;
          break;
        case 'f': 
          this.fname = paramArrayOfString[(i++)];
          break;
        case 'm': 
          this.mname = paramArrayOfString[(i++)];
          break;
        case '0': 
          this.flag0 = true;
          break;
        case 'i': 
          if ((this.cflag) || (this.uflag) || (this.xflag) || (this.tflag))
          {
            usageError();
            return false;
          }
          this.rootjar = paramArrayOfString[(i++)];
          this.iflag = true;
          break;
        case 'n': 
          this.nflag = true;
          break;
        case 'e': 
          this.ename = paramArrayOfString[(i++)];
          break;
        case 'P': 
          this.pflag = true;
          break;
        default: 
          error(formatMsg("error.illegal.option", String.valueOf(str1.charAt(k))));
          usageError();
          return false;
        }
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException1)
    {
      usageError();
      return false;
    }
    if ((!this.cflag) && (!this.tflag) && (!this.xflag) && (!this.uflag) && (!this.iflag))
    {
      error(getMsg("error.bad.option"));
      usageError();
      return false;
    }
    int j = paramArrayOfString.length - i;
    if (j > 0)
    {
      k = 0;
      String[] arrayOfString = new String[j];
      try
      {
        for (int m = i; m < paramArrayOfString.length; m++) {
          if (paramArrayOfString[m].equals("-C"))
          {
            String str2 = paramArrayOfString[(++m)];
            str2 = str2 + File.separator;
            for (str2 = str2.replace(File.separatorChar, '/'); str2.indexOf("//") > -1; str2 = str2.replace("//", "/")) {}
            this.paths.add(str2.replace(File.separatorChar, '/'));
            arrayOfString[(k++)] = (str2 + paramArrayOfString[(++m)]);
          }
          else
          {
            arrayOfString[(k++)] = paramArrayOfString[m];
          }
        }
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException2)
      {
        usageError();
        return false;
      }
      this.files = new String[k];
      System.arraycopy(arrayOfString, 0, this.files, 0, k);
    }
    else
    {
      if ((this.cflag) && (this.mname == null))
      {
        error(getMsg("error.bad.cflag"));
        usageError();
        return false;
      }
      if (this.uflag)
      {
        if ((this.mname != null) || (this.ename != null)) {
          return true;
        }
        error(getMsg("error.bad.uflag"));
        usageError();
        return false;
      }
    }
    return true;
  }
  
  void expand(File paramFile, String[] paramArrayOfString, boolean paramBoolean)
  {
    if (paramArrayOfString == null) {
      return;
    }
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      File localFile;
      if (paramFile == null) {
        localFile = new File(paramArrayOfString[i]);
      } else {
        localFile = new File(paramFile, paramArrayOfString[i]);
      }
      if (localFile.isFile())
      {
        if ((this.entries.add(localFile)) && (paramBoolean)) {
          this.entryMap.put(entryName(localFile.getPath()), localFile);
        }
      }
      else if (localFile.isDirectory())
      {
        if (this.entries.add(localFile))
        {
          if (paramBoolean)
          {
            String str = localFile.getPath();
            str = str + File.separator;
            this.entryMap.put(entryName(str), localFile);
          }
          expand(localFile, localFile.list(), paramBoolean);
        }
      }
      else
      {
        error(formatMsg("error.nosuch.fileordir", String.valueOf(localFile)));
        this.ok = false;
      }
    }
  }
  
  void create(OutputStream paramOutputStream, Manifest paramManifest)
    throws IOException
  {
    JarOutputStream localJarOutputStream = new JarOutputStream(paramOutputStream);
    if (this.flag0) {
      localJarOutputStream.setMethod(0);
    }
    if (paramManifest != null)
    {
      if (this.vflag) {
        output(getMsg("out.added.manifest"));
      }
      localObject = new ZipEntry("META-INF/");
      ((ZipEntry)localObject).setTime(System.currentTimeMillis());
      ((ZipEntry)localObject).setSize(0L);
      ((ZipEntry)localObject).setCrc(0L);
      localJarOutputStream.putNextEntry((ZipEntry)localObject);
      localObject = new ZipEntry("META-INF/MANIFEST.MF");
      ((ZipEntry)localObject).setTime(System.currentTimeMillis());
      if (this.flag0) {
        crc32Manifest((ZipEntry)localObject, paramManifest);
      }
      localJarOutputStream.putNextEntry((ZipEntry)localObject);
      paramManifest.write(localJarOutputStream);
      localJarOutputStream.closeEntry();
    }
    Object localObject = this.entries.iterator();
    while (((Iterator)localObject).hasNext())
    {
      File localFile = (File)((Iterator)localObject).next();
      addFile(localJarOutputStream, localFile);
    }
    localJarOutputStream.close();
  }
  
  private char toUpperCaseASCII(char paramChar)
  {
    return (paramChar < 'a') || (paramChar > 'z') ? paramChar : (char)(paramChar + 'A' - 97);
  }
  
  private boolean equalsIgnoreCase(String paramString1, String paramString2)
  {
    assert (paramString2.toUpperCase(Locale.ENGLISH).equals(paramString2));
    int i;
    if ((i = paramString1.length()) != paramString2.length()) {
      return false;
    }
    for (int j = 0; j < i; j++)
    {
      char c1 = paramString1.charAt(j);
      char c2 = paramString2.charAt(j);
      if ((c1 != c2) && (toUpperCaseASCII(c1) != c2)) {
        return false;
      }
    }
    return true;
  }
  
  boolean update(InputStream paramInputStream1, OutputStream paramOutputStream, InputStream paramInputStream2, JarIndex paramJarIndex)
    throws IOException
  {
    ZipInputStream localZipInputStream = new ZipInputStream(paramInputStream1);
    JarOutputStream localJarOutputStream = new JarOutputStream(paramOutputStream);
    ZipEntry localZipEntry = null;
    int i = 0;
    boolean bool1 = true;
    if (paramJarIndex != null) {
      addIndex(paramJarIndex, localJarOutputStream);
    }
    while ((localZipEntry = localZipInputStream.getNextEntry()) != null)
    {
      localObject1 = localZipEntry.getName();
      boolean bool2 = equalsIgnoreCase((String)localObject1, "META-INF/MANIFEST.MF");
      if (((paramJarIndex == null) || (!equalsIgnoreCase((String)localObject1, "META-INF/INDEX.LIST"))) && ((!this.Mflag) || (!bool2)))
      {
        Object localObject2;
        if ((bool2) && ((paramInputStream2 != null) || (this.ename != null)))
        {
          i = 1;
          if (paramInputStream2 != null)
          {
            localObject2 = new FileInputStream(this.mname);
            boolean bool3 = isAmbiguousMainClass(new Manifest((InputStream)localObject2));
            ((FileInputStream)localObject2).close();
            if (bool3) {
              return false;
            }
          }
          localObject2 = new Manifest(localZipInputStream);
          if (paramInputStream2 != null) {
            ((Manifest)localObject2).read(paramInputStream2);
          }
          if (!updateManifest((Manifest)localObject2, localJarOutputStream)) {
            return false;
          }
        }
        else if (!this.entryMap.containsKey(localObject1))
        {
          localObject2 = new ZipEntry((String)localObject1);
          ((ZipEntry)localObject2).setMethod(localZipEntry.getMethod());
          ((ZipEntry)localObject2).setTime(localZipEntry.getTime());
          ((ZipEntry)localObject2).setComment(localZipEntry.getComment());
          ((ZipEntry)localObject2).setExtra(localZipEntry.getExtra());
          if (localZipEntry.getMethod() == 0)
          {
            ((ZipEntry)localObject2).setSize(localZipEntry.getSize());
            ((ZipEntry)localObject2).setCrc(localZipEntry.getCrc());
          }
          localJarOutputStream.putNextEntry((ZipEntry)localObject2);
          copy(localZipInputStream, localJarOutputStream);
        }
        else
        {
          localObject2 = (File)this.entryMap.get(localObject1);
          addFile(localJarOutputStream, (File)localObject2);
          this.entryMap.remove(localObject1);
          this.entries.remove(localObject2);
        }
      }
    }
    Object localObject1 = this.entries.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      File localFile = (File)((Iterator)localObject1).next();
      addFile(localJarOutputStream, localFile);
    }
    if (i == 0) {
      if (paramInputStream2 != null)
      {
        localObject1 = new Manifest(paramInputStream2);
        bool1 = !isAmbiguousMainClass((Manifest)localObject1);
        if ((bool1) && (!updateManifest((Manifest)localObject1, localJarOutputStream))) {
          bool1 = false;
        }
      }
      else if ((this.ename != null) && (!updateManifest(new Manifest(), localJarOutputStream)))
      {
        bool1 = false;
      }
    }
    localZipInputStream.close();
    localJarOutputStream.close();
    return bool1;
  }
  
  private void addIndex(JarIndex paramJarIndex, ZipOutputStream paramZipOutputStream)
    throws IOException
  {
    ZipEntry localZipEntry = new ZipEntry("META-INF/INDEX.LIST");
    localZipEntry.setTime(System.currentTimeMillis());
    if (this.flag0)
    {
      CRC32OutputStream localCRC32OutputStream = new CRC32OutputStream();
      paramJarIndex.write(localCRC32OutputStream);
      localCRC32OutputStream.updateEntry(localZipEntry);
    }
    paramZipOutputStream.putNextEntry(localZipEntry);
    paramJarIndex.write(paramZipOutputStream);
    paramZipOutputStream.closeEntry();
  }
  
  private boolean updateManifest(Manifest paramManifest, ZipOutputStream paramZipOutputStream)
    throws IOException
  {
    addVersion(paramManifest);
    addCreatedBy(paramManifest);
    if (this.ename != null) {
      addMainClass(paramManifest, this.ename);
    }
    ZipEntry localZipEntry = new ZipEntry("META-INF/MANIFEST.MF");
    localZipEntry.setTime(System.currentTimeMillis());
    if (this.flag0) {
      crc32Manifest(localZipEntry, paramManifest);
    }
    paramZipOutputStream.putNextEntry(localZipEntry);
    paramManifest.write(paramZipOutputStream);
    if (this.vflag) {
      output(getMsg("out.update.manifest"));
    }
    return true;
  }
  
  private static final boolean isWinDriveLetter(char paramChar)
  {
    return ((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'A') && (paramChar <= 'Z'));
  }
  
  private String safeName(String paramString)
  {
    if (!this.pflag)
    {
      int i = paramString.length();
      int j = paramString.lastIndexOf("../");
      if (j == -1) {
        j = 0;
      } else {
        j += 3;
      }
      if (File.separatorChar == '\\') {
        while (j < i)
        {
          int k = j;
          if ((j + 1 < i) && (paramString.charAt(j + 1) == ':') && (isWinDriveLetter(paramString.charAt(j)))) {
            j += 2;
          }
          while ((j < i) && (paramString.charAt(j) == '/')) {
            j++;
          }
          if (j == k) {
            break;
          }
        }
      }
      while ((j < i) && (paramString.charAt(j) == '/')) {
        j++;
      }
      if (j != 0) {
        paramString = paramString.substring(j);
      }
    }
    return paramString;
  }
  
  private String entryName(String paramString)
  {
    paramString = paramString.replace(File.separatorChar, '/');
    Object localObject = "";
    Iterator localIterator = this.paths.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if ((paramString.startsWith(str)) && (str.length() > ((String)localObject).length())) {
        localObject = str;
      }
    }
    paramString = paramString.substring(((String)localObject).length());
    paramString = safeName(paramString);
    if (paramString.startsWith("./")) {
      paramString = paramString.substring(2);
    }
    return paramString;
  }
  
  private void addVersion(Manifest paramManifest)
  {
    Attributes localAttributes = paramManifest.getMainAttributes();
    if (localAttributes.getValue(Attributes.Name.MANIFEST_VERSION) == null) {
      localAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
    }
  }
  
  private void addCreatedBy(Manifest paramManifest)
  {
    Attributes localAttributes = paramManifest.getMainAttributes();
    if (localAttributes.getValue(new Attributes.Name("Created-By")) == null)
    {
      String str1 = System.getProperty("java.vendor");
      String str2 = System.getProperty("java.version");
      localAttributes.put(new Attributes.Name("Created-By"), str2 + " (" + str1 + ")");
    }
  }
  
  private void addMainClass(Manifest paramManifest, String paramString)
  {
    Attributes localAttributes = paramManifest.getMainAttributes();
    localAttributes.put(Attributes.Name.MAIN_CLASS, paramString);
  }
  
  private boolean isAmbiguousMainClass(Manifest paramManifest)
  {
    if (this.ename != null)
    {
      Attributes localAttributes = paramManifest.getMainAttributes();
      if (localAttributes.get(Attributes.Name.MAIN_CLASS) != null)
      {
        error(getMsg("error.bad.eflag"));
        usageError();
        return true;
      }
    }
    return false;
  }
  
  void addFile(ZipOutputStream paramZipOutputStream, File paramFile)
    throws IOException
  {
    String str = paramFile.getPath();
    boolean bool = paramFile.isDirectory();
    if (bool) {
      str = str + File.separator;
    }
    str = entryName(str);
    if ((str.equals("")) || (str.equals(".")) || (str.equals(this.zname))) {
      return;
    }
    if (((str.equals("META-INF/")) || (str.equals("META-INF/MANIFEST.MF"))) && (!this.Mflag))
    {
      if (this.vflag) {
        output(formatMsg("out.ignore.entry", str));
      }
      return;
    }
    long l1 = bool ? 0L : paramFile.length();
    if (this.vflag) {
      this.out.print(formatMsg("out.adding", str));
    }
    ZipEntry localZipEntry = new ZipEntry(str);
    localZipEntry.setTime(paramFile.lastModified());
    if (l1 == 0L)
    {
      localZipEntry.setMethod(0);
      localZipEntry.setSize(0L);
      localZipEntry.setCrc(0L);
    }
    else if (this.flag0)
    {
      crc32File(localZipEntry, paramFile);
    }
    paramZipOutputStream.putNextEntry(localZipEntry);
    if (!bool) {
      copy(paramFile, paramZipOutputStream);
    }
    paramZipOutputStream.closeEntry();
    if (this.vflag)
    {
      l1 = localZipEntry.getSize();
      long l2 = localZipEntry.getCompressedSize();
      this.out.print(formatMsg2("out.size", String.valueOf(l1), String.valueOf(l2)));
      if (localZipEntry.getMethod() == 8)
      {
        long l3 = 0L;
        if (l1 != 0L) {
          l3 = (l1 - l2) * 100L / l1;
        }
        output(formatMsg("out.deflated", String.valueOf(l3)));
      }
      else
      {
        output(getMsg("out.stored"));
      }
    }
  }
  
  private void copy(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    int i;
    while ((i = paramInputStream.read(this.copyBuf)) != -1) {
      paramOutputStream.write(this.copyBuf, 0, i);
    }
  }
  
  private void copy(File paramFile, OutputStream paramOutputStream)
    throws IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    try
    {
      copy(localFileInputStream, paramOutputStream);
    }
    finally
    {
      localFileInputStream.close();
    }
  }
  
  private void copy(InputStream paramInputStream, File paramFile)
    throws IOException
  {
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
    try
    {
      copy(paramInputStream, localFileOutputStream);
    }
    finally
    {
      localFileOutputStream.close();
    }
  }
  
  private void crc32Manifest(ZipEntry paramZipEntry, Manifest paramManifest)
    throws IOException
  {
    CRC32OutputStream localCRC32OutputStream = new CRC32OutputStream();
    paramManifest.write(localCRC32OutputStream);
    localCRC32OutputStream.updateEntry(paramZipEntry);
  }
  
  private void crc32File(ZipEntry paramZipEntry, File paramFile)
    throws IOException
  {
    CRC32OutputStream localCRC32OutputStream = new CRC32OutputStream();
    copy(paramFile, localCRC32OutputStream);
    if (localCRC32OutputStream.n != paramFile.length()) {
      throw new JarException(formatMsg("error.incorrect.length", paramFile.getPath()));
    }
    localCRC32OutputStream.updateEntry(paramZipEntry);
  }
  
  void replaceFSC(String[] paramArrayOfString)
  {
    if (paramArrayOfString != null) {
      for (int i = 0; i < paramArrayOfString.length; i++) {
        paramArrayOfString[i] = paramArrayOfString[i].replace(File.separatorChar, '/');
      }
    }
  }
  
  Set<ZipEntry> newDirSet()
  {
    new HashSet()
    {
      public boolean add(ZipEntry paramAnonymousZipEntry)
      {
        return (paramAnonymousZipEntry == null) || (Main.useExtractionTime) ? false : super.add(paramAnonymousZipEntry);
      }
    };
  }
  
  void updateLastModifiedTime(Set<ZipEntry> paramSet)
    throws IOException
  {
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      ZipEntry localZipEntry = (ZipEntry)localIterator.next();
      long l = localZipEntry.getTime();
      if (l != -1L)
      {
        String str = safeName(localZipEntry.getName().replace(File.separatorChar, '/'));
        if (str.length() != 0)
        {
          File localFile = new File(str.replace('/', File.separatorChar));
          localFile.setLastModified(l);
        }
      }
    }
  }
  
  void extract(InputStream paramInputStream, String[] paramArrayOfString)
    throws IOException
  {
    ZipInputStream localZipInputStream = new ZipInputStream(paramInputStream);
    Set localSet = newDirSet();
    ZipEntry localZipEntry;
    while ((localZipEntry = localZipInputStream.getNextEntry()) != null) {
      if (paramArrayOfString == null)
      {
        localSet.add(extractFile(localZipInputStream, localZipEntry));
      }
      else
      {
        String str1 = localZipEntry.getName();
        for (String str2 : paramArrayOfString) {
          if (str1.startsWith(str2))
          {
            localSet.add(extractFile(localZipInputStream, localZipEntry));
            break;
          }
        }
      }
    }
    updateLastModifiedTime(localSet);
  }
  
  void extract(String paramString, String[] paramArrayOfString)
    throws IOException
  {
    ZipFile localZipFile = new ZipFile(paramString);
    Set localSet = newDirSet();
    Enumeration localEnumeration = localZipFile.entries();
    while (localEnumeration.hasMoreElements())
    {
      ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
      if (paramArrayOfString == null)
      {
        localSet.add(extractFile(localZipFile.getInputStream(localZipEntry), localZipEntry));
      }
      else
      {
        String str1 = localZipEntry.getName();
        for (String str2 : paramArrayOfString) {
          if (str1.startsWith(str2))
          {
            localSet.add(extractFile(localZipFile.getInputStream(localZipEntry), localZipEntry));
            break;
          }
        }
      }
    }
    localZipFile.close();
    updateLastModifiedTime(localSet);
  }
  
  ZipEntry extractFile(InputStream paramInputStream, ZipEntry paramZipEntry)
    throws IOException
  {
    ZipEntry localZipEntry = null;
    String str = safeName(paramZipEntry.getName().replace(File.separatorChar, '/'));
    if (str.length() == 0) {
      return localZipEntry;
    }
    File localFile1 = new File(str.replace('/', File.separatorChar));
    if (paramZipEntry.isDirectory())
    {
      if (localFile1.exists())
      {
        if (!localFile1.isDirectory()) {
          throw new IOException(formatMsg("error.create.dir", localFile1.getPath()));
        }
      }
      else
      {
        if (!localFile1.mkdirs()) {
          throw new IOException(formatMsg("error.create.dir", localFile1.getPath()));
        }
        localZipEntry = paramZipEntry;
      }
      if (this.vflag) {
        output(formatMsg("out.create", str));
      }
    }
    else
    {
      if (localFile1.getParent() != null)
      {
        File localFile2 = new File(localFile1.getParent());
        if (((!localFile2.exists()) && (!localFile2.mkdirs())) || (!localFile2.isDirectory())) {
          throw new IOException(formatMsg("error.create.dir", localFile2.getPath()));
        }
      }
      try
      {
        copy(paramInputStream, localFile1);
      }
      finally
      {
        if ((paramInputStream instanceof ZipInputStream)) {
          ((ZipInputStream)paramInputStream).closeEntry();
        } else {
          paramInputStream.close();
        }
      }
      if (this.vflag) {
        if (paramZipEntry.getMethod() == 8) {
          output(formatMsg("out.inflated", str));
        } else {
          output(formatMsg("out.extracted", str));
        }
      }
    }
    if (!useExtractionTime)
    {
      long l = paramZipEntry.getTime();
      if (l != -1L) {
        localFile1.setLastModified(l);
      }
    }
    return localZipEntry;
  }
  
  void list(InputStream paramInputStream, String[] paramArrayOfString)
    throws IOException
  {
    ZipInputStream localZipInputStream = new ZipInputStream(paramInputStream);
    ZipEntry localZipEntry;
    while ((localZipEntry = localZipInputStream.getNextEntry()) != null)
    {
      localZipInputStream.closeEntry();
      printEntry(localZipEntry, paramArrayOfString);
    }
  }
  
  void list(String paramString, String[] paramArrayOfString)
    throws IOException
  {
    ZipFile localZipFile = new ZipFile(paramString);
    Enumeration localEnumeration = localZipFile.entries();
    while (localEnumeration.hasMoreElements()) {
      printEntry((ZipEntry)localEnumeration.nextElement(), paramArrayOfString);
    }
    localZipFile.close();
  }
  
  void dumpIndex(String paramString, JarIndex paramJarIndex)
    throws IOException
  {
    File localFile = new File(paramString);
    Path localPath1 = localFile.toPath();
    Path localPath2 = createTempFileInSameDirectoryAs(localFile).toPath();
    try
    {
      if (update(Files.newInputStream(localPath1, new OpenOption[0]), Files.newOutputStream(localPath2, new OpenOption[0]), null, paramJarIndex)) {
        try {}catch (IOException localIOException)
        {
          throw new IOException(getMsg("error.write.file"), localIOException);
        }
      }
    }
    finally
    {
      Files.deleteIfExists(localPath2);
    }
  }
  
  List<String> getJarPath(String paramString)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(paramString);
    this.jarPaths.add(paramString);
    String str1 = paramString.substring(0, Math.max(0, paramString.lastIndexOf('/') + 1));
    JarFile localJarFile = new JarFile(paramString.replace('/', File.separatorChar));
    if (localJarFile != null)
    {
      Manifest localManifest = localJarFile.getManifest();
      if (localManifest != null)
      {
        Attributes localAttributes = localManifest.getMainAttributes();
        if (localAttributes != null)
        {
          String str2 = localAttributes.getValue(Attributes.Name.CLASS_PATH);
          if (str2 != null)
          {
            StringTokenizer localStringTokenizer = new StringTokenizer(str2);
            while (localStringTokenizer.hasMoreTokens())
            {
              String str3 = localStringTokenizer.nextToken();
              if (!str3.endsWith("/"))
              {
                str3 = str1.concat(str3);
                if (!this.jarPaths.contains(str3)) {
                  localArrayList.addAll(getJarPath(str3));
                }
              }
            }
          }
        }
      }
    }
    localJarFile.close();
    return localArrayList;
  }
  
  void genIndex(String paramString, String[] paramArrayOfString)
    throws IOException
  {
    List localList = getJarPath(paramString);
    int i = localList.size();
    if ((i == 1) && (paramArrayOfString != null))
    {
      for (int j = 0; j < paramArrayOfString.length; j++) {
        localList.addAll(getJarPath(paramArrayOfString[j]));
      }
      i = localList.size();
    }
    String[] arrayOfString = (String[])localList.toArray(new String[i]);
    JarIndex localJarIndex = new JarIndex(arrayOfString);
    dumpIndex(paramString, localJarIndex);
  }
  
  void printEntry(ZipEntry paramZipEntry, String[] paramArrayOfString)
    throws IOException
  {
    if (paramArrayOfString == null)
    {
      printEntry(paramZipEntry);
    }
    else
    {
      String str1 = paramZipEntry.getName();
      for (String str2 : paramArrayOfString) {
        if (str1.startsWith(str2))
        {
          printEntry(paramZipEntry);
          return;
        }
      }
    }
  }
  
  void printEntry(ZipEntry paramZipEntry)
    throws IOException
  {
    if (this.vflag)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      String str = Long.toString(paramZipEntry.getSize());
      for (int i = 6 - str.length(); i > 0; i--) {
        localStringBuilder.append(' ');
      }
      localStringBuilder.append(str).append(' ').append(new Date(paramZipEntry.getTime()).toString());
      localStringBuilder.append(' ').append(paramZipEntry.getName());
      output(localStringBuilder.toString());
    }
    else
    {
      output(paramZipEntry.getName());
    }
  }
  
  void usageError()
  {
    error(getMsg("usage"));
  }
  
  void fatalError(Exception paramException)
  {
    paramException.printStackTrace();
  }
  
  void fatalError(String paramString)
  {
    error(this.program + ": " + paramString);
  }
  
  protected void output(String paramString)
  {
    this.out.println(paramString);
  }
  
  protected void error(String paramString)
  {
    this.err.println(paramString);
  }
  
  public static void main(String[] paramArrayOfString)
  {
    Main localMain = new Main(System.out, System.err, "jar");
    System.exit(localMain.run(paramArrayOfString) ? 0 : 1);
  }
  
  private File createTemporaryFile(String paramString1, String paramString2)
  {
    File localFile1 = null;
    try
    {
      localFile1 = File.createTempFile(paramString1, paramString2);
    }
    catch (IOException|SecurityException localIOException1) {}
    if (localFile1 == null) {
      if (this.fname != null) {
        try
        {
          File localFile2 = new File(this.fname).getAbsoluteFile().getParentFile();
          localFile1 = File.createTempFile(this.fname, ".tmp" + paramString2, localFile2);
        }
        catch (IOException localIOException2)
        {
          fatalError(localIOException2);
        }
      } else {
        fatalError(new IOException(getMsg("error.create.tempfile")));
      }
    }
    return localFile1;
  }
  
  static
  {
    useExtractionTime = Boolean.getBoolean("sun.tools.jar.useExtractionTime");
    try
    {
      rsrc = ResourceBundle.getBundle("sun.tools.jar.resources.jar");
    }
    catch (MissingResourceException localMissingResourceException)
    {
      throw new Error("Fatal: Resource for jar is missing");
    }
  }
  
  private static class CRC32OutputStream
    extends OutputStream
  {
    final CRC32 crc = new CRC32();
    long n = 0L;
    
    CRC32OutputStream() {}
    
    public void write(int paramInt)
      throws IOException
    {
      this.crc.update(paramInt);
      this.n += 1L;
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      this.crc.update(paramArrayOfByte, paramInt1, paramInt2);
      this.n += paramInt2;
    }
    
    public void updateEntry(ZipEntry paramZipEntry)
    {
      paramZipEntry.setMethod(0);
      paramZipEntry.setSize(this.n);
      paramZipEntry.setCrc(this.crc.getValue());
    }
  }
}
