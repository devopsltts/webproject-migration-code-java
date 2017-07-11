package javax.rmi.CORBA;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

class GetORBPropertiesFileAction
  implements PrivilegedAction
{
  private boolean debug = false;
  
  public GetORBPropertiesFileAction() {}
  
  private String getSystemProperty(final String paramString)
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getProperty(paramString);
      }
    });
    return str;
  }
  
  private void getPropertiesFromFile(Properties paramProperties, String paramString)
  {
    try
    {
      File localFile = new File(paramString);
      if (!localFile.exists()) {
        return;
      }
      FileInputStream localFileInputStream = new FileInputStream(localFile);
      try
      {
        paramProperties.load(localFileInputStream);
      }
      finally
      {
        localFileInputStream.close();
      }
    }
    catch (Exception localException)
    {
      if (this.debug) {
        System.out.println("ORB properties file " + paramString + " not found: " + localException);
      }
    }
  }
  
  public Object run()
  {
    Properties localProperties1 = new Properties();
    String str1 = getSystemProperty("java.home");
    String str2 = str1 + File.separator + "lib" + File.separator + "orb.properties";
    getPropertiesFromFile(localProperties1, str2);
    Properties localProperties2 = new Properties(localProperties1);
    String str3 = getSystemProperty("user.home");
    str2 = str3 + File.separator + "orb.properties";
    getPropertiesFromFile(localProperties2, str2);
    return localProperties2;
  }
}
