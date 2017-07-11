package javax.activation;

import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MailcapFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MailcapCommandMap
  extends CommandMap
{
  private MailcapFile[] DB;
  private static final int PROG = 0;
  
  public MailcapCommandMap()
  {
    ArrayList localArrayList = new ArrayList(5);
    MailcapFile localMailcapFile = null;
    localArrayList.add(null);
    LogSupport.log("MailcapCommandMap: load HOME");
    try
    {
      String str1 = System.getProperty("user.home");
      if (str1 != null)
      {
        String str3 = str1 + File.separator + ".mailcap";
        localMailcapFile = loadFile(str3);
        if (localMailcapFile != null) {
          localArrayList.add(localMailcapFile);
        }
      }
    }
    catch (SecurityException localSecurityException1) {}
    LogSupport.log("MailcapCommandMap: load SYS");
    try
    {
      String str2 = System.getProperty("java.home") + File.separator + "lib" + File.separator + "mailcap";
      localMailcapFile = loadFile(str2);
      if (localMailcapFile != null) {
        localArrayList.add(localMailcapFile);
      }
    }
    catch (SecurityException localSecurityException2) {}
    LogSupport.log("MailcapCommandMap: load JAR");
    loadAllResources(localArrayList, "META-INF/mailcap");
    LogSupport.log("MailcapCommandMap: load DEF");
    localMailcapFile = loadResource("/META-INF/mailcap.default");
    if (localMailcapFile != null) {
      localArrayList.add(localMailcapFile);
    }
    this.DB = new MailcapFile[localArrayList.size()];
    this.DB = ((MailcapFile[])localArrayList.toArray(this.DB));
  }
  
  private MailcapFile loadResource(String paramString)
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = SecuritySupport.getResourceAsStream(getClass(), paramString);
      if (localInputStream != null)
      {
        MailcapFile localMailcapFile1 = new MailcapFile(localInputStream);
        if (LogSupport.isLoggable()) {
          LogSupport.log("MailcapCommandMap: successfully loaded mailcap file: " + paramString);
        }
        MailcapFile localMailcapFile2 = localMailcapFile1;
        return localMailcapFile2;
      }
      if (LogSupport.isLoggable()) {
        LogSupport.log("MailcapCommandMap: not loading mailcap file: " + paramString);
      }
      return null;
    }
    catch (IOException localIOException2)
    {
      if (LogSupport.isLoggable()) {
        LogSupport.log("MailcapCommandMap: can't load " + paramString, localIOException2);
      }
    }
    catch (SecurityException localSecurityException)
    {
      if (LogSupport.isLoggable()) {
        LogSupport.log("MailcapCommandMap: can't load " + paramString, localSecurityException);
      }
    }
    finally
    {
      try
      {
        if (localInputStream != null) {
          localInputStream.close();
        }
      }
      catch (IOException localIOException6) {}
    }
  }
  
  private void loadAllResources(List paramList, String paramString)
  {
    int i = 0;
    try
    {
      ClassLoader localClassLoader = null;
      localClassLoader = SecuritySupport.getContextClassLoader();
      if (localClassLoader == null) {
        localClassLoader = getClass().getClassLoader();
      }
      URL[] arrayOfURL;
      if (localClassLoader != null) {
        arrayOfURL = SecuritySupport.getResources(localClassLoader, paramString);
      } else {
        arrayOfURL = SecuritySupport.getSystemResources(paramString);
      }
      if (arrayOfURL != null)
      {
        if (LogSupport.isLoggable()) {
          LogSupport.log("MailcapCommandMap: getResources");
        }
        int j = 0;
        while (j < arrayOfURL.length)
        {
          URL localURL = arrayOfURL[j];
          InputStream localInputStream = null;
          if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: URL " + localURL);
          }
          try
          {
            localInputStream = SecuritySupport.openStream(localURL);
            if (localInputStream != null)
            {
              paramList.add(new MailcapFile(localInputStream));
              i = 1;
              if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: successfully loaded mailcap file from URL: " + localURL);
              }
            }
            else if (LogSupport.isLoggable())
            {
              LogSupport.log("MailcapCommandMap: not loading mailcap file from URL: " + localURL);
            }
            try
            {
              if (localInputStream != null) {
                localInputStream.close();
              }
            }
            catch (IOException localIOException1) {}
            j++;
          }
          catch (IOException localIOException2)
          {
            if (LogSupport.isLoggable()) {
              LogSupport.log("MailcapCommandMap: can't load " + localURL, localIOException2);
            }
          }
          catch (SecurityException localSecurityException)
          {
            if (LogSupport.isLoggable()) {
              LogSupport.log("MailcapCommandMap: can't load " + localURL, localSecurityException);
            }
          }
          finally
          {
            try
            {
              if (localInputStream != null) {
                localInputStream.close();
              }
            }
            catch (IOException localIOException5) {}
          }
        }
      }
    }
    catch (Exception localException)
    {
      if (LogSupport.isLoggable()) {
        LogSupport.log("MailcapCommandMap: can't load " + paramString, localException);
      }
    }
    if (i == 0)
    {
      if (LogSupport.isLoggable()) {
        LogSupport.log("MailcapCommandMap: !anyLoaded");
      }
      MailcapFile localMailcapFile = loadResource("/" + paramString);
      if (localMailcapFile != null) {
        paramList.add(localMailcapFile);
      }
    }
  }
  
  private MailcapFile loadFile(String paramString)
  {
    MailcapFile localMailcapFile = null;
    try
    {
      localMailcapFile = new MailcapFile(paramString);
    }
    catch (IOException localIOException) {}
    return localMailcapFile;
  }
  
  public MailcapCommandMap(String paramString)
    throws IOException
  {
    this();
    if (LogSupport.isLoggable()) {
      LogSupport.log("MailcapCommandMap: load PROG from " + paramString);
    }
    if (this.DB[0] == null) {
      this.DB[0] = new MailcapFile(paramString);
    }
  }
  
  public MailcapCommandMap(InputStream paramInputStream)
  {
    this();
    LogSupport.log("MailcapCommandMap: load PROG");
    if (this.DB[0] == null) {
      try
      {
        this.DB[0] = new MailcapFile(paramInputStream);
      }
      catch (IOException localIOException) {}
    }
  }
  
  public synchronized CommandInfo[] getPreferredCommands(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramString != null) {
      paramString = paramString.toLowerCase(Locale.ENGLISH);
    }
    Map localMap;
    for (int i = 0; i < this.DB.length; i++) {
      if (this.DB[i] != null)
      {
        localMap = this.DB[i].getMailcapList(paramString);
        if (localMap != null) {
          appendPrefCmdsToList(localMap, localArrayList);
        }
      }
    }
    for (i = 0; i < this.DB.length; i++) {
      if (this.DB[i] != null)
      {
        localMap = this.DB[i].getMailcapFallbackList(paramString);
        if (localMap != null) {
          appendPrefCmdsToList(localMap, localArrayList);
        }
      }
    }
    CommandInfo[] arrayOfCommandInfo = new CommandInfo[localArrayList.size()];
    arrayOfCommandInfo = (CommandInfo[])localArrayList.toArray(arrayOfCommandInfo);
    return arrayOfCommandInfo;
  }
  
  private void appendPrefCmdsToList(Map paramMap, List paramList)
  {
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      if (!checkForVerb(paramList, str1))
      {
        List localList = (List)paramMap.get(str1);
        String str2 = (String)localList.get(0);
        paramList.add(new CommandInfo(str1, str2));
      }
    }
  }
  
  private boolean checkForVerb(List paramList, String paramString)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = ((CommandInfo)localIterator.next()).getCommandName();
      if (str.equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  public synchronized CommandInfo[] getAllCommands(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramString != null) {
      paramString = paramString.toLowerCase(Locale.ENGLISH);
    }
    Map localMap;
    for (int i = 0; i < this.DB.length; i++) {
      if (this.DB[i] != null)
      {
        localMap = this.DB[i].getMailcapList(paramString);
        if (localMap != null) {
          appendCmdsToList(localMap, localArrayList);
        }
      }
    }
    for (i = 0; i < this.DB.length; i++) {
      if (this.DB[i] != null)
      {
        localMap = this.DB[i].getMailcapFallbackList(paramString);
        if (localMap != null) {
          appendCmdsToList(localMap, localArrayList);
        }
      }
    }
    CommandInfo[] arrayOfCommandInfo = new CommandInfo[localArrayList.size()];
    arrayOfCommandInfo = (CommandInfo[])localArrayList.toArray(arrayOfCommandInfo);
    return arrayOfCommandInfo;
  }
  
  private void appendCmdsToList(Map paramMap, List paramList)
  {
    Iterator localIterator1 = paramMap.keySet().iterator();
    while (localIterator1.hasNext())
    {
      String str1 = (String)localIterator1.next();
      List localList = (List)paramMap.get(str1);
      Iterator localIterator2 = localList.iterator();
      while (localIterator2.hasNext())
      {
        String str2 = (String)localIterator2.next();
        paramList.add(new CommandInfo(str1, str2));
      }
    }
  }
  
  public synchronized CommandInfo getCommand(String paramString1, String paramString2)
  {
    if (paramString1 != null) {
      paramString1 = paramString1.toLowerCase(Locale.ENGLISH);
    }
    Map localMap;
    List localList;
    String str;
    for (int i = 0; i < this.DB.length; i++) {
      if (this.DB[i] != null)
      {
        localMap = this.DB[i].getMailcapList(paramString1);
        if (localMap != null)
        {
          localList = (List)localMap.get(paramString2);
          if (localList != null)
          {
            str = (String)localList.get(0);
            if (str != null) {
              return new CommandInfo(paramString2, str);
            }
          }
        }
      }
    }
    for (i = 0; i < this.DB.length; i++) {
      if (this.DB[i] != null)
      {
        localMap = this.DB[i].getMailcapFallbackList(paramString1);
        if (localMap != null)
        {
          localList = (List)localMap.get(paramString2);
          if (localList != null)
          {
            str = (String)localList.get(0);
            if (str != null) {
              return new CommandInfo(paramString2, str);
            }
          }
        }
      }
    }
    return null;
  }
  
  public synchronized void addMailcap(String paramString)
  {
    LogSupport.log("MailcapCommandMap: add to PROG");
    if (this.DB[0] == null) {
      this.DB[0] = new MailcapFile();
    }
    this.DB[0].appendToMailcap(paramString);
  }
  
  public synchronized DataContentHandler createDataContentHandler(String paramString)
  {
    if (LogSupport.isLoggable()) {
      LogSupport.log("MailcapCommandMap: createDataContentHandler for " + paramString);
    }
    if (paramString != null) {
      paramString = paramString.toLowerCase(Locale.ENGLISH);
    }
    Map localMap;
    List localList;
    String str;
    DataContentHandler localDataContentHandler;
    for (int i = 0; i < this.DB.length; i++) {
      if (this.DB[i] != null)
      {
        if (LogSupport.isLoggable()) {
          LogSupport.log("  search DB #" + i);
        }
        localMap = this.DB[i].getMailcapList(paramString);
        if (localMap != null)
        {
          localList = (List)localMap.get("content-handler");
          if (localList != null)
          {
            str = (String)localList.get(0);
            localDataContentHandler = getDataContentHandler(str);
            if (localDataContentHandler != null) {
              return localDataContentHandler;
            }
          }
        }
      }
    }
    for (i = 0; i < this.DB.length; i++) {
      if (this.DB[i] != null)
      {
        if (LogSupport.isLoggable()) {
          LogSupport.log("  search fallback DB #" + i);
        }
        localMap = this.DB[i].getMailcapFallbackList(paramString);
        if (localMap != null)
        {
          localList = (List)localMap.get("content-handler");
          if (localList != null)
          {
            str = (String)localList.get(0);
            localDataContentHandler = getDataContentHandler(str);
            if (localDataContentHandler != null) {
              return localDataContentHandler;
            }
          }
        }
      }
    }
    return null;
  }
  
  private DataContentHandler getDataContentHandler(String paramString)
  {
    if (LogSupport.isLoggable()) {
      LogSupport.log("    got content-handler");
    }
    if (LogSupport.isLoggable()) {
      LogSupport.log("      class " + paramString);
    }
    try
    {
      ClassLoader localClassLoader = null;
      localClassLoader = SecuritySupport.getContextClassLoader();
      if (localClassLoader == null) {
        localClassLoader = getClass().getClassLoader();
      }
      Class localClass = null;
      try
      {
        localClass = localClassLoader.loadClass(paramString);
      }
      catch (Exception localException)
      {
        localClass = Class.forName(paramString);
      }
      if (localClass != null) {
        return (DataContentHandler)localClass.newInstance();
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      if (LogSupport.isLoggable()) {
        LogSupport.log("Can't load DCH " + paramString, localIllegalAccessException);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (LogSupport.isLoggable()) {
        LogSupport.log("Can't load DCH " + paramString, localClassNotFoundException);
      }
    }
    catch (InstantiationException localInstantiationException)
    {
      if (LogSupport.isLoggable()) {
        LogSupport.log("Can't load DCH " + paramString, localInstantiationException);
      }
    }
    return null;
  }
  
  public synchronized String[] getMimeTypes()
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < this.DB.length; i++) {
      if (this.DB[i] != null)
      {
        String[] arrayOfString2 = this.DB[i].getMimeTypes();
        if (arrayOfString2 != null) {
          for (int j = 0; j < arrayOfString2.length; j++) {
            if (!localArrayList.contains(arrayOfString2[j])) {
              localArrayList.add(arrayOfString2[j]);
            }
          }
        }
      }
    }
    String[] arrayOfString1 = new String[localArrayList.size()];
    arrayOfString1 = (String[])localArrayList.toArray(arrayOfString1);
    return arrayOfString1;
  }
  
  public synchronized String[] getNativeCommands(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramString != null) {
      paramString = paramString.toLowerCase(Locale.ENGLISH);
    }
    for (int i = 0; i < this.DB.length; i++) {
      if (this.DB[i] != null)
      {
        String[] arrayOfString2 = this.DB[i].getNativeCommands(paramString);
        if (arrayOfString2 != null) {
          for (int j = 0; j < arrayOfString2.length; j++) {
            if (!localArrayList.contains(arrayOfString2[j])) {
              localArrayList.add(arrayOfString2[j]);
            }
          }
        }
      }
    }
    String[] arrayOfString1 = new String[localArrayList.size()];
    arrayOfString1 = (String[])localArrayList.toArray(arrayOfString1);
    return arrayOfString1;
  }
}
