package java.nio.file;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import sun.nio.fs.DefaultFileSystemProvider;

public final class FileSystems
{
  private FileSystems() {}
  
  public static FileSystem getDefault()
  {
    return DefaultFileSystemHolder.defaultFileSystem;
  }
  
  public static FileSystem getFileSystem(URI paramURI)
  {
    String str = paramURI.getScheme();
    Iterator localIterator = FileSystemProvider.installedProviders().iterator();
    while (localIterator.hasNext())
    {
      FileSystemProvider localFileSystemProvider = (FileSystemProvider)localIterator.next();
      if (str.equalsIgnoreCase(localFileSystemProvider.getScheme())) {
        return localFileSystemProvider.getFileSystem(paramURI);
      }
    }
    throw new ProviderNotFoundException("Provider \"" + str + "\" not found");
  }
  
  public static FileSystem newFileSystem(URI paramURI, Map<String, ?> paramMap)
    throws IOException
  {
    return newFileSystem(paramURI, paramMap, null);
  }
  
  public static FileSystem newFileSystem(URI paramURI, Map<String, ?> paramMap, ClassLoader paramClassLoader)
    throws IOException
  {
    String str = paramURI.getScheme();
    Object localObject1 = FileSystemProvider.installedProviders().iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (FileSystemProvider)((Iterator)localObject1).next();
      if (str.equalsIgnoreCase(((FileSystemProvider)localObject2).getScheme())) {
        return ((FileSystemProvider)localObject2).newFileSystem(paramURI, paramMap);
      }
    }
    if (paramClassLoader != null)
    {
      localObject1 = ServiceLoader.load(FileSystemProvider.class, paramClassLoader);
      localObject2 = ((ServiceLoader)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        FileSystemProvider localFileSystemProvider = (FileSystemProvider)((Iterator)localObject2).next();
        if (str.equalsIgnoreCase(localFileSystemProvider.getScheme())) {
          return localFileSystemProvider.newFileSystem(paramURI, paramMap);
        }
      }
    }
    throw new ProviderNotFoundException("Provider \"" + str + "\" not found");
  }
  
  public static FileSystem newFileSystem(Path paramPath, ClassLoader paramClassLoader)
    throws IOException
  {
    if (paramPath == null) {
      throw new NullPointerException();
    }
    Map localMap = Collections.emptyMap();
    Object localObject1 = FileSystemProvider.installedProviders().iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (FileSystemProvider)((Iterator)localObject1).next();
      try
      {
        return ((FileSystemProvider)localObject2).newFileSystem(paramPath, localMap);
      }
      catch (UnsupportedOperationException localUnsupportedOperationException1) {}
    }
    if (paramClassLoader != null)
    {
      localObject1 = ServiceLoader.load(FileSystemProvider.class, paramClassLoader);
      localObject2 = ((ServiceLoader)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        FileSystemProvider localFileSystemProvider = (FileSystemProvider)((Iterator)localObject2).next();
        try
        {
          return localFileSystemProvider.newFileSystem(paramPath, localMap);
        }
        catch (UnsupportedOperationException localUnsupportedOperationException2) {}
      }
    }
    throw new ProviderNotFoundException("Provider not found");
  }
  
  private static class DefaultFileSystemHolder
  {
    static final FileSystem defaultFileSystem = ;
    
    private DefaultFileSystemHolder() {}
    
    private static FileSystem defaultFileSystem()
    {
      FileSystemProvider localFileSystemProvider = (FileSystemProvider)AccessController.doPrivileged(new PrivilegedAction()
      {
        public FileSystemProvider run()
        {
          return FileSystems.DefaultFileSystemHolder.access$000();
        }
      });
      return localFileSystemProvider.getFileSystem(URI.create("file:///"));
    }
    
    private static FileSystemProvider getDefaultProvider()
    {
      FileSystemProvider localFileSystemProvider = DefaultFileSystemProvider.create();
      String str1 = System.getProperty("java.nio.file.spi.DefaultFileSystemProvider");
      if (str1 != null) {
        for (String str2 : str1.split(",")) {
          try
          {
            Class localClass = Class.forName(str2, true, ClassLoader.getSystemClassLoader());
            Constructor localConstructor = localClass.getDeclaredConstructor(new Class[] { FileSystemProvider.class });
            localFileSystemProvider = (FileSystemProvider)localConstructor.newInstance(new Object[] { localFileSystemProvider });
            if (!localFileSystemProvider.getScheme().equals("file")) {
              throw new Error("Default provider must use scheme 'file'");
            }
          }
          catch (Exception localException)
          {
            throw new Error(localException);
          }
        }
      }
      return localFileSystemProvider;
    }
  }
}
