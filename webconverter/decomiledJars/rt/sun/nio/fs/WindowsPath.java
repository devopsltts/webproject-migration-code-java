package sun.nio.fs;

import com.sun.nio.file.ExtendedWatchEventModifier;
import java.io.IOError;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

class WindowsPath
  extends AbstractPath
{
  private static final int MAX_PATH = 247;
  private static final int MAX_LONG_PATH = 32000;
  private final WindowsFileSystem fs;
  private final WindowsPathType type;
  private final String root;
  private final String path;
  private volatile WeakReference<String> pathForWin32Calls;
  private volatile Integer[] offsets;
  private int hash;
  
  private WindowsPath(WindowsFileSystem paramWindowsFileSystem, WindowsPathType paramWindowsPathType, String paramString1, String paramString2)
  {
    this.fs = paramWindowsFileSystem;
    this.type = paramWindowsPathType;
    this.root = paramString1;
    this.path = paramString2;
  }
  
  static WindowsPath parse(WindowsFileSystem paramWindowsFileSystem, String paramString)
  {
    WindowsPathParser.Result localResult = WindowsPathParser.parse(paramString);
    return new WindowsPath(paramWindowsFileSystem, localResult.type(), localResult.root(), localResult.path());
  }
  
  static WindowsPath createFromNormalizedPath(WindowsFileSystem paramWindowsFileSystem, String paramString, BasicFileAttributes paramBasicFileAttributes)
  {
    try
    {
      WindowsPathParser.Result localResult = WindowsPathParser.parseNormalizedPath(paramString);
      if (paramBasicFileAttributes == null) {
        return new WindowsPath(paramWindowsFileSystem, localResult.type(), localResult.root(), localResult.path());
      }
      return new WindowsPathWithAttributes(paramWindowsFileSystem, localResult.type(), localResult.root(), localResult.path(), paramBasicFileAttributes);
    }
    catch (InvalidPathException localInvalidPathException)
    {
      throw new AssertionError(localInvalidPathException.getMessage());
    }
  }
  
  static WindowsPath createFromNormalizedPath(WindowsFileSystem paramWindowsFileSystem, String paramString)
  {
    return createFromNormalizedPath(paramWindowsFileSystem, paramString, null);
  }
  
  String getPathForExceptionMessage()
  {
    return this.path;
  }
  
  String getPathForPermissionCheck()
  {
    return this.path;
  }
  
  String getPathForWin32Calls()
    throws WindowsException
  {
    if ((isAbsolute()) && (this.path.length() <= 247)) {
      return this.path;
    }
    WeakReference localWeakReference = this.pathForWin32Calls;
    String str = localWeakReference != null ? (String)localWeakReference.get() : null;
    if (str != null) {
      return str;
    }
    str = getAbsolutePath();
    if (str.length() > 247)
    {
      if (str.length() > 32000) {
        throw new WindowsException("Cannot access file with path exceeding 32000 characters");
      }
      str = addPrefixIfNeeded(WindowsNativeDispatcher.GetFullPathName(str));
    }
    if (this.type != WindowsPathType.DRIVE_RELATIVE) {
      synchronized (this.path)
      {
        this.pathForWin32Calls = new WeakReference(str);
      }
    }
    return str;
  }
  
  private String getAbsolutePath()
    throws WindowsException
  {
    if (isAbsolute()) {
      return this.path;
    }
    String str1;
    Object localObject;
    if (this.type == WindowsPathType.RELATIVE)
    {
      str1 = getFileSystem().defaultDirectory();
      if (isEmpty()) {
        return str1;
      }
      if (str1.endsWith("\\")) {
        return str1 + this.path;
      }
      localObject = new StringBuilder(str1.length() + this.path.length() + 1);
      return str1 + '\\' + this.path;
    }
    if (this.type == WindowsPathType.DIRECTORY_RELATIVE)
    {
      str1 = getFileSystem().defaultRoot();
      return str1 + this.path.substring(1);
    }
    if (isSameDrive(this.root, getFileSystem().defaultRoot()))
    {
      str1 = this.path.substring(this.root.length());
      localObject = getFileSystem().defaultDirectory();
      String str3;
      if (((String)localObject).endsWith("\\")) {
        str3 = (String)localObject + str1;
      } else {
        str3 = (String)localObject + "\\" + str1;
      }
      return str3;
    }
    try
    {
      int i = WindowsNativeDispatcher.GetDriveType(this.root + "\\");
      if ((i == 0) || (i == 1)) {
        throw new WindowsException("");
      }
      str1 = WindowsNativeDispatcher.GetFullPathName(this.root + ".");
    }
    catch (WindowsException localWindowsException)
    {
      throw new WindowsException("Unable to get working directory of drive '" + Character.toUpperCase(this.root.charAt(0)) + "'");
    }
    String str2 = str1;
    if (str1.endsWith("\\")) {
      str2 = str2 + this.path.substring(this.root.length());
    } else if (this.path.length() > this.root.length()) {
      str2 = str2 + "\\" + this.path.substring(this.root.length());
    }
    return str2;
  }
  
  private static boolean isSameDrive(String paramString1, String paramString2)
  {
    return Character.toUpperCase(paramString1.charAt(0)) == Character.toUpperCase(paramString2.charAt(0));
  }
  
  static String addPrefixIfNeeded(String paramString)
  {
    if (paramString.length() > 247) {
      if (paramString.startsWith("\\\\")) {
        paramString = "\\\\?\\UNC" + paramString.substring(1, paramString.length());
      } else {
        paramString = "\\\\?\\" + paramString;
      }
    }
    return paramString;
  }
  
  public WindowsFileSystem getFileSystem()
  {
    return this.fs;
  }
  
  private boolean isEmpty()
  {
    return this.path.length() == 0;
  }
  
  private WindowsPath emptyPath()
  {
    return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", "");
  }
  
  public Path getFileName()
  {
    int i = this.path.length();
    if (i == 0) {
      return this;
    }
    if (this.root.length() == i) {
      return null;
    }
    int j = this.path.lastIndexOf('\\');
    if (j < this.root.length()) {
      j = this.root.length();
    } else {
      j++;
    }
    return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", this.path.substring(j));
  }
  
  public WindowsPath getParent()
  {
    if (this.root.length() == this.path.length()) {
      return null;
    }
    int i = this.path.lastIndexOf('\\');
    if (i < this.root.length()) {
      return getRoot();
    }
    return new WindowsPath(getFileSystem(), this.type, this.root, this.path.substring(0, i));
  }
  
  public WindowsPath getRoot()
  {
    if (this.root.length() == 0) {
      return null;
    }
    return new WindowsPath(getFileSystem(), this.type, this.root, this.root);
  }
  
  WindowsPathType type()
  {
    return this.type;
  }
  
  boolean isUnc()
  {
    return this.type == WindowsPathType.UNC;
  }
  
  boolean needsSlashWhenResolving()
  {
    if (this.path.endsWith("\\")) {
      return false;
    }
    return this.path.length() > this.root.length();
  }
  
  public boolean isAbsolute()
  {
    return (this.type == WindowsPathType.ABSOLUTE) || (this.type == WindowsPathType.UNC);
  }
  
  static WindowsPath toWindowsPath(Path paramPath)
  {
    if (paramPath == null) {
      throw new NullPointerException();
    }
    if (!(paramPath instanceof WindowsPath)) {
      throw new ProviderMismatchException();
    }
    return (WindowsPath)paramPath;
  }
  
  public WindowsPath relativize(Path paramPath)
  {
    WindowsPath localWindowsPath = toWindowsPath(paramPath);
    if (equals(localWindowsPath)) {
      return emptyPath();
    }
    if (this.type != localWindowsPath.type) {
      throw new IllegalArgumentException("'other' is different type of Path");
    }
    if (!this.root.equalsIgnoreCase(localWindowsPath.root)) {
      throw new IllegalArgumentException("'other' has different root");
    }
    int i = getNameCount();
    int j = localWindowsPath.getNameCount();
    int k = i > j ? j : i;
    for (int m = 0; (m < k) && (getName(m).equals(localWindowsPath.getName(m))); m++) {}
    StringBuilder localStringBuilder = new StringBuilder();
    for (int n = m; n < i; n++) {
      localStringBuilder.append("..\\");
    }
    for (n = m; n < j; n++)
    {
      localStringBuilder.append(localWindowsPath.getName(n).toString());
      localStringBuilder.append("\\");
    }
    localStringBuilder.setLength(localStringBuilder.length() - 1);
    return createFromNormalizedPath(getFileSystem(), localStringBuilder.toString());
  }
  
  public Path normalize()
  {
    int i = getNameCount();
    if ((i == 0) || (isEmpty())) {
      return this;
    }
    boolean[] arrayOfBoolean = new boolean[i];
    int j = i;
    int k;
    do
    {
      k = j;
      int m = -1;
      for (n = 0; n < i; n++) {
        if (arrayOfBoolean[n] == 0)
        {
          String str = elementAsString(n);
          if (str.length() > 2)
          {
            m = n;
          }
          else if (str.length() == 1)
          {
            if (str.charAt(0) == '.')
            {
              arrayOfBoolean[n] = true;
              j--;
            }
            else
            {
              m = n;
            }
          }
          else if ((str.charAt(0) != '.') || (str.charAt(1) != '.'))
          {
            m = n;
          }
          else if (m >= 0)
          {
            arrayOfBoolean[m] = true;
            arrayOfBoolean[n] = true;
            j -= 2;
            m = -1;
          }
          else if ((isAbsolute()) || (this.type == WindowsPathType.DIRECTORY_RELATIVE))
          {
            int i1 = 0;
            for (int i2 = 0; i2 < n; i2++) {
              if (arrayOfBoolean[i2] == 0)
              {
                i1 = 1;
                break;
              }
            }
            if (i1 == 0)
            {
              arrayOfBoolean[n] = true;
              j--;
            }
          }
        }
      }
    } while (k > j);
    if (j == i) {
      return this;
    }
    if (j == 0) {
      return this.root.length() == 0 ? emptyPath() : getRoot();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.root != null) {
      localStringBuilder.append(this.root);
    }
    for (int n = 0; n < i; n++) {
      if (arrayOfBoolean[n] == 0)
      {
        localStringBuilder.append(getName(n));
        localStringBuilder.append("\\");
      }
    }
    localStringBuilder.setLength(localStringBuilder.length() - 1);
    return createFromNormalizedPath(getFileSystem(), localStringBuilder.toString());
  }
  
  public WindowsPath resolve(Path paramPath)
  {
    WindowsPath localWindowsPath = toWindowsPath(paramPath);
    if (localWindowsPath.isEmpty()) {
      return this;
    }
    if (localWindowsPath.isAbsolute()) {
      return localWindowsPath;
    }
    String str1;
    switch (1.$SwitchMap$sun$nio$fs$WindowsPathType[localWindowsPath.type.ordinal()])
    {
    case 1: 
      if ((this.path.endsWith("\\")) || (this.root.length() == this.path.length())) {
        str1 = this.path + localWindowsPath.path;
      } else {
        str1 = this.path + "\\" + localWindowsPath.path;
      }
      return new WindowsPath(getFileSystem(), this.type, this.root, str1);
    case 2: 
      if (this.root.endsWith("\\")) {
        str1 = this.root + localWindowsPath.path.substring(1);
      } else {
        str1 = this.root + localWindowsPath.path;
      }
      return createFromNormalizedPath(getFileSystem(), str1);
    case 3: 
      if (!this.root.endsWith("\\")) {
        return localWindowsPath;
      }
      str1 = this.root.substring(0, this.root.length() - 1);
      if (!str1.equalsIgnoreCase(localWindowsPath.root)) {
        return localWindowsPath;
      }
      String str2 = localWindowsPath.path.substring(localWindowsPath.root.length());
      String str3;
      if (this.path.endsWith("\\")) {
        str3 = this.path + str2;
      } else {
        str3 = this.path + "\\" + str2;
      }
      return createFromNormalizedPath(getFileSystem(), str3);
    }
    throw new AssertionError();
  }
  
  private void initOffsets()
  {
    if (this.offsets == null)
    {
      ArrayList localArrayList = new ArrayList();
      if (isEmpty())
      {
        localArrayList.add(Integer.valueOf(0));
      }
      else
      {
        int i = this.root.length();
        int j = this.root.length();
        while (j < this.path.length()) {
          if (this.path.charAt(j) != '\\')
          {
            j++;
          }
          else
          {
            localArrayList.add(Integer.valueOf(i));
            j++;
            i = j;
          }
        }
        if (i != j) {
          localArrayList.add(Integer.valueOf(i));
        }
      }
      synchronized (this)
      {
        if (this.offsets == null) {
          this.offsets = ((Integer[])localArrayList.toArray(new Integer[localArrayList.size()]));
        }
      }
    }
  }
  
  public int getNameCount()
  {
    initOffsets();
    return this.offsets.length;
  }
  
  private String elementAsString(int paramInt)
  {
    initOffsets();
    if (paramInt == this.offsets.length - 1) {
      return this.path.substring(this.offsets[paramInt].intValue());
    }
    return this.path.substring(this.offsets[paramInt].intValue(), this.offsets[(paramInt + 1)].intValue() - 1);
  }
  
  public WindowsPath getName(int paramInt)
  {
    initOffsets();
    if ((paramInt < 0) || (paramInt >= this.offsets.length)) {
      throw new IllegalArgumentException();
    }
    return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", elementAsString(paramInt));
  }
  
  public WindowsPath subpath(int paramInt1, int paramInt2)
  {
    initOffsets();
    if (paramInt1 < 0) {
      throw new IllegalArgumentException();
    }
    if (paramInt1 >= this.offsets.length) {
      throw new IllegalArgumentException();
    }
    if (paramInt2 > this.offsets.length) {
      throw new IllegalArgumentException();
    }
    if (paramInt1 >= paramInt2) {
      throw new IllegalArgumentException();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    Integer[] arrayOfInteger = new Integer[paramInt2 - paramInt1];
    for (int i = paramInt1; i < paramInt2; i++)
    {
      arrayOfInteger[(i - paramInt1)] = Integer.valueOf(localStringBuilder.length());
      localStringBuilder.append(elementAsString(i));
      if (i != paramInt2 - 1) {
        localStringBuilder.append("\\");
      }
    }
    return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", localStringBuilder.toString());
  }
  
  public boolean startsWith(Path paramPath)
  {
    if (!(Objects.requireNonNull(paramPath) instanceof WindowsPath)) {
      return false;
    }
    WindowsPath localWindowsPath = (WindowsPath)paramPath;
    if (!this.root.equalsIgnoreCase(localWindowsPath.root)) {
      return false;
    }
    if (localWindowsPath.isEmpty()) {
      return isEmpty();
    }
    int i = getNameCount();
    int j = localWindowsPath.getNameCount();
    if (j <= i)
    {
      for (;;)
      {
        j--;
        if (j < 0) {
          break;
        }
        String str1 = elementAsString(j);
        String str2 = localWindowsPath.elementAsString(j);
        if (!str1.equalsIgnoreCase(str2)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public boolean endsWith(Path paramPath)
  {
    if (!(Objects.requireNonNull(paramPath) instanceof WindowsPath)) {
      return false;
    }
    WindowsPath localWindowsPath = (WindowsPath)paramPath;
    if (localWindowsPath.path.length() > this.path.length()) {
      return false;
    }
    if (localWindowsPath.isEmpty()) {
      return isEmpty();
    }
    int i = getNameCount();
    int j = localWindowsPath.getNameCount();
    if (j > i) {
      return false;
    }
    if (localWindowsPath.root.length() > 0)
    {
      if (j < i) {
        return false;
      }
      if (!this.root.equalsIgnoreCase(localWindowsPath.root)) {
        return false;
      }
    }
    int k = i - j;
    for (;;)
    {
      j--;
      if (j < 0) {
        break;
      }
      String str1 = elementAsString(k + j);
      String str2 = localWindowsPath.elementAsString(j);
      if (!str1.equalsIgnoreCase(str2)) {
        return false;
      }
    }
    return true;
  }
  
  public int compareTo(Path paramPath)
  {
    if (paramPath == null) {
      throw new NullPointerException();
    }
    String str1 = this.path;
    String str2 = ((WindowsPath)paramPath).path;
    int i = str1.length();
    int j = str2.length();
    int k = Math.min(i, j);
    for (int m = 0; m < k; m++)
    {
      char c1 = str1.charAt(m);
      char c2 = str2.charAt(m);
      if (c1 != c2)
      {
        c1 = Character.toUpperCase(c1);
        c2 = Character.toUpperCase(c2);
        if (c1 != c2) {
          return c1 - c2;
        }
      }
    }
    return i - j;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof WindowsPath))) {
      return compareTo((Path)paramObject) == 0;
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = this.hash;
    if (i == 0)
    {
      for (int j = 0; j < this.path.length(); j++) {
        i = 31 * i + Character.toUpperCase(this.path.charAt(j));
      }
      this.hash = i;
    }
    return i;
  }
  
  public String toString()
  {
    return this.path;
  }
  
  long openForReadAttributeAccess(boolean paramBoolean)
    throws WindowsException
  {
    int i = 33554432;
    if ((!paramBoolean) && (getFileSystem().supportsLinks())) {
      i |= 0x200000;
    }
    return WindowsNativeDispatcher.CreateFile(getPathForWin32Calls(), 128, 7, 0L, 3, i);
  }
  
  void checkRead()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(getPathForPermissionCheck());
    }
  }
  
  void checkWrite()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkWrite(getPathForPermissionCheck());
    }
  }
  
  void checkDelete()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkDelete(getPathForPermissionCheck());
    }
  }
  
  public URI toUri()
  {
    return WindowsUriSupport.toUri(this);
  }
  
  public WindowsPath toAbsolutePath()
  {
    if (isAbsolute()) {
      return this;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPropertyAccess("user.dir");
    }
    try
    {
      return createFromNormalizedPath(getFileSystem(), getAbsolutePath());
    }
    catch (WindowsException localWindowsException)
    {
      throw new IOError(new IOException(localWindowsException.getMessage()));
    }
  }
  
  public WindowsPath toRealPath(LinkOption... paramVarArgs)
    throws IOException
  {
    checkRead();
    String str = WindowsLinkSupport.getRealPath(this, Util.followLinks(paramVarArgs));
    return createFromNormalizedPath(getFileSystem(), str);
  }
  
  public WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier... paramVarArgs)
    throws IOException
  {
    if (paramWatchService == null) {
      throw new NullPointerException();
    }
    if (!(paramWatchService instanceof WindowsWatchService)) {
      throw new ProviderMismatchException();
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      int i = 0;
      int j = paramVarArgs.length;
      if (j > 0)
      {
        paramVarArgs = (WatchEvent.Modifier[])Arrays.copyOf(paramVarArgs, j);
        int k = 0;
        while (k < j) {
          if (paramVarArgs[(k++)] == ExtendedWatchEventModifier.FILE_TREE) {
            i = 1;
          }
        }
      }
      String str = getPathForPermissionCheck();
      localSecurityManager.checkRead(str);
      if (i != 0) {
        localSecurityManager.checkRead(str + "\\-");
      }
    }
    return ((WindowsWatchService)paramWatchService).register(this, paramArrayOfKind, paramVarArgs);
  }
  
  private static class WindowsPathWithAttributes
    extends WindowsPath
    implements BasicFileAttributesHolder
  {
    final WeakReference<BasicFileAttributes> ref;
    
    WindowsPathWithAttributes(WindowsFileSystem paramWindowsFileSystem, WindowsPathType paramWindowsPathType, String paramString1, String paramString2, BasicFileAttributes paramBasicFileAttributes)
    {
      super(paramWindowsPathType, paramString1, paramString2, null);
      this.ref = new WeakReference(paramBasicFileAttributes);
    }
    
    public BasicFileAttributes get()
    {
      return (BasicFileAttributes)this.ref.get();
    }
    
    public void invalidate()
    {
      this.ref.clear();
    }
  }
}
