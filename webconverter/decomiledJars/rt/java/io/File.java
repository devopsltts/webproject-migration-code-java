package java.io;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

public class File
  implements Serializable, Comparable<File>
{
  private static final FileSystem fs;
  private final String path;
  private transient PathStatus status = null;
  private final transient int prefixLength;
  public static final char separatorChar;
  public static final String separator;
  public static final char pathSeparatorChar;
  public static final String pathSeparator;
  private static final long PATH_OFFSET;
  private static final long PREFIX_LENGTH_OFFSET;
  private static final Unsafe UNSAFE;
  private static final long serialVersionUID = 301077366599181567L;
  private volatile transient Path filePath;
  
  final boolean isInvalid()
  {
    if (this.status == null) {
      this.status = (this.path.indexOf(0) < 0 ? PathStatus.CHECKED : PathStatus.INVALID);
    }
    return this.status == PathStatus.INVALID;
  }
  
  int getPrefixLength()
  {
    return this.prefixLength;
  }
  
  private File(String paramString, int paramInt)
  {
    this.path = paramString;
    this.prefixLength = paramInt;
  }
  
  private File(String paramString, File paramFile)
  {
    assert (paramFile.path != null);
    assert (!paramFile.path.equals(""));
    this.path = fs.resolve(paramFile.path, paramString);
    this.prefixLength = paramFile.prefixLength;
  }
  
  public File(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    this.path = fs.normalize(paramString);
    this.prefixLength = fs.prefixLength(this.path);
  }
  
  public File(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      throw new NullPointerException();
    }
    if (paramString1 != null)
    {
      if (paramString1.equals("")) {
        this.path = fs.resolve(fs.getDefaultParent(), fs.normalize(paramString2));
      } else {
        this.path = fs.resolve(fs.normalize(paramString1), fs.normalize(paramString2));
      }
    }
    else {
      this.path = fs.normalize(paramString2);
    }
    this.prefixLength = fs.prefixLength(this.path);
  }
  
  public File(File paramFile, String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    if (paramFile != null)
    {
      if (paramFile.path.equals("")) {
        this.path = fs.resolve(fs.getDefaultParent(), fs.normalize(paramString));
      } else {
        this.path = fs.resolve(paramFile.path, fs.normalize(paramString));
      }
    }
    else {
      this.path = fs.normalize(paramString);
    }
    this.prefixLength = fs.prefixLength(this.path);
  }
  
  public File(URI paramURI)
  {
    if (!paramURI.isAbsolute()) {
      throw new IllegalArgumentException("URI is not absolute");
    }
    if (paramURI.isOpaque()) {
      throw new IllegalArgumentException("URI is not hierarchical");
    }
    String str1 = paramURI.getScheme();
    if ((str1 == null) || (!str1.equalsIgnoreCase("file"))) {
      throw new IllegalArgumentException("URI scheme is not \"file\"");
    }
    if (paramURI.getAuthority() != null) {
      throw new IllegalArgumentException("URI has an authority component");
    }
    if (paramURI.getFragment() != null) {
      throw new IllegalArgumentException("URI has a fragment component");
    }
    if (paramURI.getQuery() != null) {
      throw new IllegalArgumentException("URI has a query component");
    }
    String str2 = paramURI.getPath();
    if (str2.equals("")) {
      throw new IllegalArgumentException("URI path component is empty");
    }
    str2 = fs.fromURIPath(str2);
    if (separatorChar != '/') {
      str2 = str2.replace('/', separatorChar);
    }
    this.path = fs.normalize(str2);
    this.prefixLength = fs.prefixLength(this.path);
  }
  
  public String getName()
  {
    int i = this.path.lastIndexOf(separatorChar);
    if (i < this.prefixLength) {
      return this.path.substring(this.prefixLength);
    }
    return this.path.substring(i + 1);
  }
  
  public String getParent()
  {
    int i = this.path.lastIndexOf(separatorChar);
    if (i < this.prefixLength)
    {
      if ((this.prefixLength > 0) && (this.path.length() > this.prefixLength)) {
        return this.path.substring(0, this.prefixLength);
      }
      return null;
    }
    return this.path.substring(0, i);
  }
  
  public File getParentFile()
  {
    String str = getParent();
    if (str == null) {
      return null;
    }
    return new File(str, this.prefixLength);
  }
  
  public String getPath()
  {
    return this.path;
  }
  
  public boolean isAbsolute()
  {
    return fs.isAbsolute(this);
  }
  
  public String getAbsolutePath()
  {
    return fs.resolve(this);
  }
  
  public File getAbsoluteFile()
  {
    String str = getAbsolutePath();
    return new File(str, fs.prefixLength(str));
  }
  
  public String getCanonicalPath()
    throws IOException
  {
    if (isInvalid()) {
      throw new IOException("Invalid file path");
    }
    return fs.canonicalize(fs.resolve(this));
  }
  
  public File getCanonicalFile()
    throws IOException
  {
    String str = getCanonicalPath();
    return new File(str, fs.prefixLength(str));
  }
  
  private static String slashify(String paramString, boolean paramBoolean)
  {
    String str = paramString;
    if (separatorChar != '/') {
      str = str.replace(separatorChar, '/');
    }
    if (!str.startsWith("/")) {
      str = "/" + str;
    }
    if ((!str.endsWith("/")) && (paramBoolean)) {
      str = str + "/";
    }
    return str;
  }
  
  @Deprecated
  public URL toURL()
    throws MalformedURLException
  {
    if (isInvalid()) {
      throw new MalformedURLException("Invalid file path");
    }
    return new URL("file", "", slashify(getAbsolutePath(), isDirectory()));
  }
  
  public URI toURI()
  {
    try
    {
      File localFile = getAbsoluteFile();
      String str = slashify(localFile.getPath(), localFile.isDirectory());
      if (str.startsWith("//")) {
        str = "//" + str;
      }
      return new URI("file", null, str, null);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new Error(localURISyntaxException);
    }
  }
  
  public boolean canRead()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return fs.checkAccess(this, 4);
  }
  
  public boolean canWrite()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return fs.checkAccess(this, 2);
  }
  
  public boolean exists()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return (fs.getBooleanAttributes(this) & 0x1) != 0;
  }
  
  public boolean isDirectory()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return (fs.getBooleanAttributes(this) & 0x4) != 0;
  }
  
  public boolean isFile()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return (fs.getBooleanAttributes(this) & 0x2) != 0;
  }
  
  public boolean isHidden()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return (fs.getBooleanAttributes(this) & 0x8) != 0;
  }
  
  public long lastModified()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(this.path);
    }
    if (isInvalid()) {
      return 0L;
    }
    return fs.getLastModifiedTime(this);
  }
  
  public long length()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(this.path);
    }
    if (isInvalid()) {
      return 0L;
    }
    return fs.getLength(this);
  }
  
  public boolean createNewFile()
    throws IOException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(this.path);
    }
    if (isInvalid()) {
      throw new IOException("Invalid file path");
    }
    return fs.createFileExclusively(this.path);
  }
  
  public boolean delete()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkDelete(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return fs.delete(this);
  }
  
  public void deleteOnExit()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkDelete(this.path);
    }
    if (isInvalid()) {
      return;
    }
    DeleteOnExitHook.add(this.path);
  }
  
  public String[] list()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(this.path);
    }
    if (isInvalid()) {
      return null;
    }
    return fs.list(this);
  }
  
  public String[] list(FilenameFilter paramFilenameFilter)
  {
    String[] arrayOfString = list();
    if ((arrayOfString == null) || (paramFilenameFilter == null)) {
      return arrayOfString;
    }
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < arrayOfString.length; i++) {
      if (paramFilenameFilter.accept(this, arrayOfString[i])) {
        localArrayList.add(arrayOfString[i]);
      }
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  public File[] listFiles()
  {
    String[] arrayOfString = list();
    if (arrayOfString == null) {
      return null;
    }
    int i = arrayOfString.length;
    File[] arrayOfFile = new File[i];
    for (int j = 0; j < i; j++) {
      arrayOfFile[j] = new File(arrayOfString[j], this);
    }
    return arrayOfFile;
  }
  
  public File[] listFiles(FilenameFilter paramFilenameFilter)
  {
    String[] arrayOfString1 = list();
    if (arrayOfString1 == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    for (String str : arrayOfString1) {
      if ((paramFilenameFilter == null) || (paramFilenameFilter.accept(this, str))) {
        localArrayList.add(new File(str, this));
      }
    }
    return (File[])localArrayList.toArray(new File[localArrayList.size()]);
  }
  
  public File[] listFiles(FileFilter paramFileFilter)
  {
    String[] arrayOfString1 = list();
    if (arrayOfString1 == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    for (String str : arrayOfString1)
    {
      File localFile = new File(str, this);
      if ((paramFileFilter == null) || (paramFileFilter.accept(localFile))) {
        localArrayList.add(localFile);
      }
    }
    return (File[])localArrayList.toArray(new File[localArrayList.size()]);
  }
  
  public boolean mkdir()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return fs.createDirectory(this);
  }
  
  public boolean mkdirs()
  {
    if (exists()) {
      return false;
    }
    if (mkdir()) {
      return true;
    }
    File localFile1 = null;
    try
    {
      localFile1 = getCanonicalFile();
    }
    catch (IOException localIOException)
    {
      return false;
    }
    File localFile2 = localFile1.getParentFile();
    return (localFile2 != null) && ((localFile2.mkdirs()) || (localFile2.exists())) && (localFile1.mkdir());
  }
  
  public boolean renameTo(File paramFile)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localSecurityManager.checkWrite(this.path);
      localSecurityManager.checkWrite(paramFile.path);
    }
    if (paramFile == null) {
      throw new NullPointerException();
    }
    if ((isInvalid()) || (paramFile.isInvalid())) {
      return false;
    }
    return fs.rename(this, paramFile);
  }
  
  public boolean setLastModified(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative time");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return fs.setLastModifiedTime(this, paramLong);
  }
  
  public boolean setReadOnly()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return fs.setReadOnly(this);
  }
  
  public boolean setWritable(boolean paramBoolean1, boolean paramBoolean2)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return fs.setPermission(this, 2, paramBoolean1, paramBoolean2);
  }
  
  public boolean setWritable(boolean paramBoolean)
  {
    return setWritable(paramBoolean, true);
  }
  
  public boolean setReadable(boolean paramBoolean1, boolean paramBoolean2)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return fs.setPermission(this, 4, paramBoolean1, paramBoolean2);
  }
  
  public boolean setReadable(boolean paramBoolean)
  {
    return setReadable(paramBoolean, true);
  }
  
  public boolean setExecutable(boolean paramBoolean1, boolean paramBoolean2)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return fs.setPermission(this, 1, paramBoolean1, paramBoolean2);
  }
  
  public boolean setExecutable(boolean paramBoolean)
  {
    return setExecutable(paramBoolean, true);
  }
  
  public boolean canExecute()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkExec(this.path);
    }
    if (isInvalid()) {
      return false;
    }
    return fs.checkAccess(this, 1);
  }
  
  public static File[] listRoots()
  {
    return fs.listRoots();
  }
  
  public long getTotalSpace()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localSecurityManager.checkPermission(new RuntimePermission("getFileSystemAttributes"));
      localSecurityManager.checkRead(this.path);
    }
    if (isInvalid()) {
      return 0L;
    }
    return fs.getSpace(this, 0);
  }
  
  public long getFreeSpace()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localSecurityManager.checkPermission(new RuntimePermission("getFileSystemAttributes"));
      localSecurityManager.checkRead(this.path);
    }
    if (isInvalid()) {
      return 0L;
    }
    return fs.getSpace(this, 1);
  }
  
  public long getUsableSpace()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localSecurityManager.checkPermission(new RuntimePermission("getFileSystemAttributes"));
      localSecurityManager.checkRead(this.path);
    }
    if (isInvalid()) {
      return 0L;
    }
    return fs.getSpace(this, 2);
  }
  
  public static File createTempFile(String paramString1, String paramString2, File paramFile)
    throws IOException
  {
    if (paramString1.length() < 3) {
      throw new IllegalArgumentException("Prefix string too short");
    }
    if (paramString2 == null) {
      paramString2 = ".tmp";
    }
    File localFile1 = paramFile != null ? paramFile : TempDirectory.location();
    SecurityManager localSecurityManager = System.getSecurityManager();
    File localFile2;
    do
    {
      localFile2 = TempDirectory.generateFile(paramString1, paramString2, localFile1);
      if (localSecurityManager != null) {
        try
        {
          localSecurityManager.checkWrite(localFile2.getPath());
        }
        catch (SecurityException localSecurityException)
        {
          if (paramFile == null) {
            throw new SecurityException("Unable to create temporary file");
          }
          throw localSecurityException;
        }
      }
    } while ((fs.getBooleanAttributes(localFile2) & 0x1) != 0);
    if (!fs.createFileExclusively(localFile2.getPath())) {
      throw new IOException("Unable to create temporary file");
    }
    return localFile2;
  }
  
  public static File createTempFile(String paramString1, String paramString2)
    throws IOException
  {
    return createTempFile(paramString1, paramString2, null);
  }
  
  public int compareTo(File paramFile)
  {
    return fs.compare(this, paramFile);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof File))) {
      return compareTo((File)paramObject) == 0;
    }
    return false;
  }
  
  public int hashCode()
  {
    return fs.hashCode(this);
  }
  
  public String toString()
  {
    return getPath();
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeChar(separatorChar);
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    String str1 = (String)localGetField.get("path", null);
    char c = paramObjectInputStream.readChar();
    if (c != separatorChar) {
      str1 = str1.replace(c, separatorChar);
    }
    String str2 = fs.normalize(str1);
    UNSAFE.putObject(this, PATH_OFFSET, str2);
    UNSAFE.putIntVolatile(this, PREFIX_LENGTH_OFFSET, fs.prefixLength(str2));
  }
  
  public Path toPath()
  {
    Path localPath = this.filePath;
    if (localPath == null) {
      synchronized (this)
      {
        localPath = this.filePath;
        if (localPath == null)
        {
          localPath = FileSystems.getDefault().getPath(this.path, new String[0]);
          this.filePath = localPath;
        }
      }
    }
    return localPath;
  }
  
  static
  {
    fs = DefaultFileSystem.getFileSystem();
    separatorChar = fs.getSeparator();
    separator = "" + separatorChar;
    pathSeparatorChar = fs.getPathSeparator();
    pathSeparator = "" + pathSeparatorChar;
    try
    {
      Unsafe localUnsafe = Unsafe.getUnsafe();
      PATH_OFFSET = localUnsafe.objectFieldOffset(File.class.getDeclaredField("path"));
      PREFIX_LENGTH_OFFSET = localUnsafe.objectFieldOffset(File.class.getDeclaredField("prefixLength"));
      UNSAFE = localUnsafe;
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw new Error(localReflectiveOperationException);
    }
  }
  
  private static enum PathStatus
  {
    INVALID,  CHECKED;
    
    private PathStatus() {}
  }
  
  private static class TempDirectory
  {
    private static final File tmpdir = new File((String)AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir")));
    private static final SecureRandom random = new SecureRandom();
    
    private TempDirectory() {}
    
    static File location()
    {
      return tmpdir;
    }
    
    static File generateFile(String paramString1, String paramString2, File paramFile)
      throws IOException
    {
      long l = random.nextLong();
      if (l == Long.MIN_VALUE) {
        l = 0L;
      } else {
        l = Math.abs(l);
      }
      paramString1 = new File(paramString1).getName();
      String str = paramString1 + Long.toString(l) + paramString2;
      File localFile = new File(paramFile, str);
      if ((!str.equals(localFile.getName())) || (localFile.isInvalid()))
      {
        if (System.getSecurityManager() != null) {
          throw new IOException("Unable to create temporary file");
        }
        throw new IOException("Unable to create temporary file, " + localFile);
      }
      return localFile;
    }
  }
}
