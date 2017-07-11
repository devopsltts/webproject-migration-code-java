package sun.net.www.protocol.jar;

import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.HashMap;
import java.util.jar.JarFile;
import sun.net.util.URLUtil;

class JarFileFactory
  implements URLJarFile.URLJarFileCloseController
{
  private static final HashMap<String, JarFile> fileCache = new HashMap();
  private static final HashMap<JarFile, URL> urlCache = new HashMap();
  private static final JarFileFactory instance = new JarFileFactory();
  
  private JarFileFactory() {}
  
  public static JarFileFactory getInstance()
  {
    return instance;
  }
  
  URLConnection getConnection(JarFile paramJarFile)
    throws IOException
  {
    URL localURL;
    synchronized (instance)
    {
      localURL = (URL)urlCache.get(paramJarFile);
    }
    if (localURL != null) {
      return localURL.openConnection();
    }
    return null;
  }
  
  public JarFile get(URL paramURL)
    throws IOException
  {
    return get(paramURL, true);
  }
  
  JarFile get(URL paramURL, boolean paramBoolean)
    throws IOException
  {
    Object localObject1;
    if (paramURL.getProtocol().equalsIgnoreCase("file"))
    {
      localObject1 = paramURL.getHost();
      if ((localObject1 != null) && (!((String)localObject1).equals("")) && (!((String)localObject1).equalsIgnoreCase("localhost"))) {
        paramURL = new URL("file", "", "//" + (String)localObject1 + paramURL.getPath());
      }
    }
    if (paramBoolean)
    {
      synchronized (instance)
      {
        localObject1 = getCachedJarFile(paramURL);
      }
      if (localObject1 == null)
      {
        JarFile localJarFile = URLJarFile.getJarFile(paramURL, this);
        synchronized (instance)
        {
          localObject1 = getCachedJarFile(paramURL);
          if (localObject1 == null)
          {
            fileCache.put(URLUtil.urlNoFragString(paramURL), localJarFile);
            urlCache.put(localJarFile, paramURL);
            localObject1 = localJarFile;
          }
          else if (localJarFile != null)
          {
            localJarFile.close();
          }
        }
      }
    }
    else
    {
      localObject1 = URLJarFile.getJarFile(paramURL, this);
    }
    if (localObject1 == null) {
      throw new FileNotFoundException(paramURL.toString());
    }
    return localObject1;
  }
  
  public void close(JarFile paramJarFile)
  {
    synchronized (instance)
    {
      URL localURL = (URL)urlCache.remove(paramJarFile);
      if (localURL != null) {
        fileCache.remove(URLUtil.urlNoFragString(localURL));
      }
    }
  }
  
  private JarFile getCachedJarFile(URL paramURL)
  {
    assert (Thread.holdsLock(instance));
    JarFile localJarFile = (JarFile)fileCache.get(URLUtil.urlNoFragString(paramURL));
    if (localJarFile != null)
    {
      Permission localPermission = getPermission(localJarFile);
      if (localPermission != null)
      {
        SecurityManager localSecurityManager = System.getSecurityManager();
        if (localSecurityManager != null) {
          try
          {
            localSecurityManager.checkPermission(localPermission);
          }
          catch (SecurityException localSecurityException)
          {
            if (((localPermission instanceof FilePermission)) && (localPermission.getActions().indexOf("read") != -1)) {
              localSecurityManager.checkRead(localPermission.getName());
            } else if (((localPermission instanceof SocketPermission)) && (localPermission.getActions().indexOf("connect") != -1)) {
              localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
            } else {
              throw localSecurityException;
            }
          }
        }
      }
    }
    return localJarFile;
  }
  
  private Permission getPermission(JarFile paramJarFile)
  {
    try
    {
      URLConnection localURLConnection = getConnection(paramJarFile);
      if (localURLConnection != null) {
        return localURLConnection.getPermission();
      }
    }
    catch (IOException localIOException) {}
    return null;
  }
}
