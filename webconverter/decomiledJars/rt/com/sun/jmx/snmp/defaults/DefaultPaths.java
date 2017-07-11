package com.sun.jmx.snmp.defaults;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DefaultPaths
{
  private static final String INSTALL_PATH_RESOURCE_NAME = "com/sun/jdmk/defaults/install.path";
  private static String etcDir;
  private static String tmpDir;
  private static String installDir;
  
  private DefaultPaths() {}
  
  public static String getInstallDir()
  {
    if (installDir == null) {
      return useRessourceFile();
    }
    return installDir;
  }
  
  public static String getInstallDir(String paramString)
  {
    if (installDir == null)
    {
      if (paramString == null) {
        return getInstallDir();
      }
      return getInstallDir() + File.separator + paramString;
    }
    if (paramString == null) {
      return installDir;
    }
    return installDir + File.separator + paramString;
  }
  
  public static void setInstallDir(String paramString)
  {
    installDir = paramString;
  }
  
  public static String getEtcDir()
  {
    if (etcDir == null) {
      return getInstallDir("etc");
    }
    return etcDir;
  }
  
  public static String getEtcDir(String paramString)
  {
    if (etcDir == null)
    {
      if (paramString == null) {
        return getEtcDir();
      }
      return getEtcDir() + File.separator + paramString;
    }
    if (paramString == null) {
      return etcDir;
    }
    return etcDir + File.separator + paramString;
  }
  
  public static void setEtcDir(String paramString)
  {
    etcDir = paramString;
  }
  
  public static String getTmpDir()
  {
    if (tmpDir == null) {
      return getInstallDir("tmp");
    }
    return tmpDir;
  }
  
  public static String getTmpDir(String paramString)
  {
    if (tmpDir == null)
    {
      if (paramString == null) {
        return getTmpDir();
      }
      return getTmpDir() + File.separator + paramString;
    }
    if (paramString == null) {
      return tmpDir;
    }
    return tmpDir + File.separator + paramString;
  }
  
  public static void setTmpDir(String paramString)
  {
    tmpDir = paramString;
  }
  
  private static String useRessourceFile()
  {
    InputStream localInputStream = null;
    BufferedReader localBufferedReader = null;
    try
    {
      localInputStream = DefaultPaths.class.getClassLoader().getResourceAsStream("com/sun/jdmk/defaults/install.path");
      if (localInputStream == null)
      {
        String str = null;
        return str;
      }
      localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream));
      installDir = localBufferedReader.readLine();
      return installDir;
    }
    catch (Exception localException2) {}finally
    {
      try
      {
        if (localInputStream != null) {
          localInputStream.close();
        }
        if (localBufferedReader != null) {
          localBufferedReader.close();
        }
      }
      catch (Exception localException5) {}
    }
  }
}
