package sun.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.net.www.ParseUtil;
import sun.security.action.GetPropertyAction;

public class ExtensionDependency
{
  private static Vector<ExtensionInstallationProvider> providers;
  static final boolean DEBUG = false;
  
  public ExtensionDependency() {}
  
  public static synchronized void addExtensionInstallationProvider(ExtensionInstallationProvider paramExtensionInstallationProvider)
  {
    if (providers == null) {
      providers = new Vector();
    }
    providers.add(paramExtensionInstallationProvider);
  }
  
  public static synchronized void removeExtensionInstallationProvider(ExtensionInstallationProvider paramExtensionInstallationProvider)
  {
    providers.remove(paramExtensionInstallationProvider);
  }
  
  public static boolean checkExtensionsDependencies(JarFile paramJarFile)
  {
    if (providers == null) {
      return true;
    }
    try
    {
      ExtensionDependency localExtensionDependency = new ExtensionDependency();
      return localExtensionDependency.checkExtensions(paramJarFile);
    }
    catch (ExtensionInstallationException localExtensionInstallationException)
    {
      debug(localExtensionInstallationException.getMessage());
    }
    return false;
  }
  
  protected boolean checkExtensions(JarFile paramJarFile)
    throws ExtensionInstallationException
  {
    Manifest localManifest;
    try
    {
      localManifest = paramJarFile.getManifest();
    }
    catch (IOException localIOException)
    {
      return false;
    }
    if (localManifest == null) {
      return true;
    }
    boolean bool = true;
    Attributes localAttributes = localManifest.getMainAttributes();
    if (localAttributes != null)
    {
      String str1 = localAttributes.getValue(Attributes.Name.EXTENSION_LIST);
      if (str1 != null)
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(str1);
        while (localStringTokenizer.hasMoreTokens())
        {
          String str2 = localStringTokenizer.nextToken();
          debug("The file " + paramJarFile.getName() + " appears to depend on " + str2);
          String str3 = str2 + "-" + Attributes.Name.EXTENSION_NAME.toString();
          if (localAttributes.getValue(str3) == null)
          {
            debug("The jar file " + paramJarFile.getName() + " appers to depend on " + str2 + " but does not define the " + str3 + " attribute in its manifest ");
          }
          else if (!checkExtension(str2, localAttributes))
          {
            debug("Failed installing " + str2);
            bool = false;
          }
        }
      }
      else
      {
        debug("No dependencies for " + paramJarFile.getName());
      }
    }
    return bool;
  }
  
  protected synchronized boolean checkExtension(String paramString, Attributes paramAttributes)
    throws ExtensionInstallationException
  {
    debug("Checking extension " + paramString);
    if (checkExtensionAgainstInstalled(paramString, paramAttributes)) {
      return true;
    }
    debug("Extension not currently installed ");
    ExtensionInfo localExtensionInfo = new ExtensionInfo(paramString, paramAttributes);
    return installExtension(localExtensionInfo, null);
  }
  
  boolean checkExtensionAgainstInstalled(String paramString, Attributes paramAttributes)
    throws ExtensionInstallationException
  {
    File localFile = checkExtensionExists(paramString);
    if (localFile != null)
    {
      try
      {
        if (checkExtensionAgainst(paramString, paramAttributes, localFile)) {
          return true;
        }
      }
      catch (FileNotFoundException localFileNotFoundException1)
      {
        debugException(localFileNotFoundException1);
      }
      catch (IOException localIOException1)
      {
        debugException(localIOException1);
      }
      return false;
    }
    File[] arrayOfFile;
    try
    {
      arrayOfFile = getInstalledExtensions();
    }
    catch (IOException localIOException2)
    {
      debugException(localIOException2);
      return false;
    }
    for (int i = 0; i < arrayOfFile.length; i++) {
      try
      {
        if (checkExtensionAgainst(paramString, paramAttributes, arrayOfFile[i])) {
          return true;
        }
      }
      catch (FileNotFoundException localFileNotFoundException2)
      {
        debugException(localFileNotFoundException2);
      }
      catch (IOException localIOException3)
      {
        debugException(localIOException3);
      }
    }
    return false;
  }
  
  protected boolean checkExtensionAgainst(String paramString, Attributes paramAttributes, final File paramFile)
    throws IOException, FileNotFoundException, ExtensionInstallationException
  {
    debug("Checking extension " + paramString + " against " + paramFile.getName());
    Manifest localManifest;
    try
    {
      localManifest = (Manifest)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Manifest run()
          throws IOException, FileNotFoundException
        {
          if (!paramFile.exists()) {
            throw new FileNotFoundException(paramFile.getName());
          }
          JarFile localJarFile = new JarFile(paramFile);
          return localJarFile.getManifest();
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      if ((localPrivilegedActionException.getException() instanceof FileNotFoundException)) {
        throw ((FileNotFoundException)localPrivilegedActionException.getException());
      }
      throw ((IOException)localPrivilegedActionException.getException());
    }
    ExtensionInfo localExtensionInfo1 = new ExtensionInfo(paramString, paramAttributes);
    debug("Requested Extension : " + localExtensionInfo1);
    int i = 4;
    ExtensionInfo localExtensionInfo2 = null;
    if (localManifest != null)
    {
      Attributes localAttributes = localManifest.getMainAttributes();
      if (localAttributes != null)
      {
        localExtensionInfo2 = new ExtensionInfo(null, localAttributes);
        debug("Extension Installed " + localExtensionInfo2);
        i = localExtensionInfo2.isCompatibleWith(localExtensionInfo1);
        switch (i)
        {
        case 0: 
          debug("Extensions are compatible");
          return true;
        case 4: 
          debug("Extensions are incompatible");
          return false;
        }
        debug("Extensions require an upgrade or vendor switch");
        return installExtension(localExtensionInfo1, localExtensionInfo2);
      }
    }
    return false;
  }
  
  protected boolean installExtension(ExtensionInfo paramExtensionInfo1, ExtensionInfo paramExtensionInfo2)
    throws ExtensionInstallationException
  {
    Object localObject2;
    Object localObject1;
    synchronized (providers)
    {
      localObject2 = (Vector)providers.clone();
      localObject1 = localObject2;
    }
    ??? = localObject1.elements();
    while (((Enumeration)???).hasMoreElements())
    {
      localObject2 = (ExtensionInstallationProvider)((Enumeration)???).nextElement();
      if ((localObject2 != null) && (((ExtensionInstallationProvider)localObject2).installExtension(paramExtensionInfo1, paramExtensionInfo2)))
      {
        debug(paramExtensionInfo1.name + " installation successful");
        Launcher.ExtClassLoader localExtClassLoader = (Launcher.ExtClassLoader)Launcher.getLauncher().getClassLoader().getParent();
        addNewExtensionsToClassLoader(localExtClassLoader);
        return true;
      }
    }
    debug(paramExtensionInfo1.name + " installation failed");
    return false;
  }
  
  private File checkExtensionExists(String paramString)
  {
    final String str = paramString;
    final String[] arrayOfString = { ".jar", ".zip" };
    (File)AccessController.doPrivileged(new PrivilegedAction()
    {
      public File run()
      {
        try
        {
          File[] arrayOfFile = ExtensionDependency.access$000();
          for (int i = 0; i < arrayOfFile.length; i++) {
            for (int j = 0; j < arrayOfString.length; j++)
            {
              File localFile;
              if (str.toLowerCase().endsWith(arrayOfString[j])) {
                localFile = new File(arrayOfFile[i], str);
              } else {
                localFile = new File(arrayOfFile[i], str + arrayOfString[j]);
              }
              ExtensionDependency.debug("checkExtensionExists:fileName " + localFile.getName());
              if (localFile.exists()) {
                return localFile;
              }
            }
          }
          return null;
        }
        catch (Exception localException)
        {
          ExtensionDependency.this.debugException(localException);
        }
        return null;
      }
    });
  }
  
  private static File[] getExtDirs()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.ext.dirs"));
    File[] arrayOfFile;
    if (str != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(str, File.pathSeparator);
      int i = localStringTokenizer.countTokens();
      debug("getExtDirs count " + i);
      arrayOfFile = new File[i];
      for (int j = 0; j < i; j++)
      {
        arrayOfFile[j] = new File(localStringTokenizer.nextToken());
        debug("getExtDirs dirs[" + j + "] " + arrayOfFile[j]);
      }
    }
    else
    {
      arrayOfFile = new File[0];
      debug("getExtDirs dirs " + arrayOfFile);
    }
    debug("getExtDirs dirs.length " + arrayOfFile.length);
    return arrayOfFile;
  }
  
  private static File[] getExtFiles(File[] paramArrayOfFile)
    throws IOException
  {
    Vector localVector = new Vector();
    for (int i = 0; i < paramArrayOfFile.length; i++)
    {
      String[] arrayOfString = paramArrayOfFile[i].list(new JarFilter());
      if (arrayOfString != null)
      {
        debug("getExtFiles files.length " + arrayOfString.length);
        for (int j = 0; j < arrayOfString.length; j++)
        {
          File localFile = new File(paramArrayOfFile[i], arrayOfString[j]);
          localVector.add(localFile);
          debug("getExtFiles f[" + j + "] " + localFile);
        }
      }
    }
    File[] arrayOfFile = new File[localVector.size()];
    localVector.copyInto(arrayOfFile);
    debug("getExtFiles ua.length " + arrayOfFile.length);
    return arrayOfFile;
  }
  
  private File[] getInstalledExtensions()
    throws IOException
  {
    (File[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public File[] run()
      {
        try
        {
          return ExtensionDependency.getExtFiles(ExtensionDependency.access$000());
        }
        catch (IOException localIOException)
        {
          ExtensionDependency.debug("Cannot get list of installed extensions");
          ExtensionDependency.this.debugException(localIOException);
        }
        return new File[0];
      }
    });
  }
  
  private Boolean addNewExtensionsToClassLoader(Launcher.ExtClassLoader paramExtClassLoader)
  {
    try
    {
      File[] arrayOfFile = getInstalledExtensions();
      for (int i = 0; i < arrayOfFile.length; i++)
      {
        final File localFile = arrayOfFile[i];
        URL localURL = (URL)AccessController.doPrivileged(new PrivilegedAction()
        {
          public URL run()
          {
            try
            {
              return ParseUtil.fileToEncodedURL(localFile);
            }
            catch (MalformedURLException localMalformedURLException)
            {
              ExtensionDependency.this.debugException(localMalformedURLException);
            }
            return null;
          }
        });
        if (localURL != null)
        {
          URL[] arrayOfURL = paramExtClassLoader.getURLs();
          int j = 0;
          for (int k = 0; k < arrayOfURL.length; k++)
          {
            debug("URL[" + k + "] is " + arrayOfURL[k] + " looking for " + localURL);
            if (arrayOfURL[k].toString().compareToIgnoreCase(localURL.toString()) == 0)
            {
              j = 1;
              debug("Found !");
            }
          }
          if (j == 0)
          {
            debug("Not Found ! adding to the classloader " + localURL);
            paramExtClassLoader.addExtURL(localURL);
          }
        }
      }
    }
    catch (MalformedURLException localMalformedURLException)
    {
      localMalformedURLException.printStackTrace();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    return Boolean.TRUE;
  }
  
  private static void debug(String paramString) {}
  
  private void debugException(Throwable paramThrowable) {}
}
