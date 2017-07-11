package java.security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;
import sun.security.jca.ProviderList;
import sun.security.jca.Providers;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;

public final class Security
{
  private static final Debug sdebug = Debug.getInstance("properties");
  private static Properties props;
  private static final Map<String, Class<?>> spiMap = new ConcurrentHashMap();
  
  private static void initialize()
  {
    props = new Properties();
    int i = 0;
    int j = 0;
    File localFile = securityPropFile("java.security");
    if (localFile.exists())
    {
      localObject1 = null;
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(localFile);
        localObject1 = new BufferedInputStream(localFileInputStream);
        props.load((InputStream)localObject1);
        i = 1;
        if (sdebug != null) {
          sdebug.println("reading security properties file: " + localFile);
        }
        if (localObject1 != null) {
          try
          {
            ((InputStream)localObject1).close();
          }
          catch (IOException localIOException1)
          {
            if (sdebug != null) {
              sdebug.println("unable to close input stream");
            }
          }
        }
        if (!"true".equalsIgnoreCase(props.getProperty("security.overridePropertiesFile"))) {
          break label566;
        }
      }
      catch (IOException localIOException2)
      {
        if (sdebug != null)
        {
          sdebug.println("unable to load security properties from " + localFile);
          localIOException2.printStackTrace();
        }
      }
      finally
      {
        if (localObject1 != null) {
          try
          {
            ((InputStream)localObject1).close();
          }
          catch (IOException localIOException6)
          {
            if (sdebug != null) {
              sdebug.println("unable to close input stream");
            }
          }
        }
      }
    }
    Object localObject1 = System.getProperty("java.security.properties");
    if ((localObject1 != null) && (((String)localObject1).startsWith("=")))
    {
      j = 1;
      localObject1 = ((String)localObject1).substring(1);
    }
    if (j != 0)
    {
      props = new Properties();
      if (sdebug != null) {
        sdebug.println("overriding other security properties files!");
      }
    }
    if (localObject1 != null)
    {
      BufferedInputStream localBufferedInputStream = null;
      try
      {
        localObject1 = PropertyExpander.expand((String)localObject1);
        localFile = new File((String)localObject1);
        URL localURL;
        if (localFile.exists()) {
          localURL = new URL("file:" + localFile.getCanonicalPath());
        } else {
          localURL = new URL((String)localObject1);
        }
        localBufferedInputStream = new BufferedInputStream(localURL.openStream());
        props.load(localBufferedInputStream);
        i = 1;
        if (sdebug != null)
        {
          sdebug.println("reading security properties file: " + localURL);
          if (j != 0) {
            sdebug.println("overriding other security properties files!");
          }
        }
        if (localBufferedInputStream != null) {
          try
          {
            localBufferedInputStream.close();
          }
          catch (IOException localIOException4)
          {
            if (sdebug != null) {
              sdebug.println("unable to close input stream");
            }
          }
        }
        if (i != 0) {
          return;
        }
      }
      catch (Exception localException)
      {
        if (sdebug != null)
        {
          sdebug.println("unable to load security properties from " + (String)localObject1);
          localException.printStackTrace();
        }
      }
      finally
      {
        if (localBufferedInputStream != null) {
          try
          {
            localBufferedInputStream.close();
          }
          catch (IOException localIOException7)
          {
            if (sdebug != null) {
              sdebug.println("unable to close input stream");
            }
          }
        }
      }
    }
    label566:
    initializeStatic();
    if (sdebug != null) {
      sdebug.println("unable to load security properties -- using defaults");
    }
  }
  
  private static void initializeStatic()
  {
    props.put("security.provider.1", "sun.security.provider.Sun");
    props.put("security.provider.2", "sun.security.rsa.SunRsaSign");
    props.put("security.provider.3", "com.sun.net.ssl.internal.ssl.Provider");
    props.put("security.provider.4", "com.sun.crypto.provider.SunJCE");
    props.put("security.provider.5", "sun.security.jgss.SunProvider");
    props.put("security.provider.6", "com.sun.security.sasl.Provider");
  }
  
  private Security() {}
  
  private static File securityPropFile(String paramString)
  {
    String str = File.separator;
    return new File(System.getProperty("java.home") + str + "lib" + str + "security" + str + paramString);
  }
  
  private static ProviderProperty getProviderProperty(String paramString)
  {
    ProviderProperty localProviderProperty = null;
    List localList = Providers.getProviderList().providers();
    for (int i = 0; i < localList.size(); i++)
    {
      String str1 = null;
      Provider localProvider = (Provider)localList.get(i);
      String str2 = localProvider.getProperty(paramString);
      Object localObject;
      if (str2 == null)
      {
        localObject = localProvider.keys();
        while ((((Enumeration)localObject).hasMoreElements()) && (str2 == null))
        {
          str1 = (String)((Enumeration)localObject).nextElement();
          if (paramString.equalsIgnoreCase(str1)) {
            str2 = localProvider.getProperty(str1);
          }
        }
      }
      if (str2 != null)
      {
        localObject = new ProviderProperty(null);
        ((ProviderProperty)localObject).className = str2;
        ((ProviderProperty)localObject).provider = localProvider;
        return localObject;
      }
    }
    return localProviderProperty;
  }
  
  private static String getProviderProperty(String paramString, Provider paramProvider)
  {
    String str1 = paramProvider.getProperty(paramString);
    if (str1 == null)
    {
      Enumeration localEnumeration = paramProvider.keys();
      while ((localEnumeration.hasMoreElements()) && (str1 == null))
      {
        String str2 = (String)localEnumeration.nextElement();
        if (paramString.equalsIgnoreCase(str2))
        {
          str1 = paramProvider.getProperty(str2);
          break;
        }
      }
    }
    return str1;
  }
  
  @Deprecated
  public static String getAlgorithmProperty(String paramString1, String paramString2)
  {
    ProviderProperty localProviderProperty = getProviderProperty("Alg." + paramString2 + "." + paramString1);
    if (localProviderProperty != null) {
      return localProviderProperty.className;
    }
    return null;
  }
  
  public static synchronized int insertProviderAt(Provider paramProvider, int paramInt)
  {
    String str = paramProvider.getName();
    checkInsertProvider(str);
    ProviderList localProviderList1 = Providers.getFullProviderList();
    ProviderList localProviderList2 = ProviderList.insertAt(localProviderList1, paramProvider, paramInt - 1);
    if (localProviderList1 == localProviderList2) {
      return -1;
    }
    Providers.setProviderList(localProviderList2);
    return localProviderList2.getIndex(str) + 1;
  }
  
  public static int addProvider(Provider paramProvider)
  {
    return insertProviderAt(paramProvider, 0);
  }
  
  public static synchronized void removeProvider(String paramString)
  {
    check("removeProvider." + paramString);
    ProviderList localProviderList1 = Providers.getFullProviderList();
    ProviderList localProviderList2 = ProviderList.remove(localProviderList1, paramString);
    Providers.setProviderList(localProviderList2);
  }
  
  public static Provider[] getProviders()
  {
    return Providers.getFullProviderList().toArray();
  }
  
  public static Provider getProvider(String paramString)
  {
    return Providers.getProviderList().getProvider(paramString);
  }
  
  public static Provider[] getProviders(String paramString)
  {
    String str1 = null;
    String str2 = null;
    int i = paramString.indexOf(':');
    if (i == -1)
    {
      str1 = paramString;
      str2 = "";
    }
    else
    {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    }
    Hashtable localHashtable = new Hashtable(1);
    localHashtable.put(str1, str2);
    return getProviders(localHashtable);
  }
  
  public static Provider[] getProviders(Map<String, String> paramMap)
  {
    Provider[] arrayOfProvider = getProviders();
    Set localSet = paramMap.keySet();
    Object localObject1 = new LinkedHashSet(5);
    if ((localSet == null) || (arrayOfProvider == null)) {
      return arrayOfProvider;
    }
    int i = 1;
    Object localObject2 = localSet.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (String)((Iterator)localObject2).next();
      String str = (String)paramMap.get(localObject3);
      LinkedHashSet localLinkedHashSet = getAllQualifyingCandidates((String)localObject3, str, arrayOfProvider);
      if (i != 0)
      {
        localObject1 = localLinkedHashSet;
        i = 0;
      }
      if ((localLinkedHashSet != null) && (!localLinkedHashSet.isEmpty()))
      {
        Iterator localIterator = ((LinkedHashSet)localObject1).iterator();
        while (localIterator.hasNext())
        {
          Provider localProvider = (Provider)localIterator.next();
          if (!localLinkedHashSet.contains(localProvider)) {
            localIterator.remove();
          }
        }
      }
      else
      {
        localObject1 = null;
        break;
      }
    }
    if ((localObject1 == null) || (((LinkedHashSet)localObject1).isEmpty())) {
      return null;
    }
    localObject2 = ((LinkedHashSet)localObject1).toArray();
    Object localObject3 = new Provider[localObject2.length];
    for (int j = 0; j < localObject3.length; j++) {
      localObject3[j] = ((Provider)localObject2[j]);
    }
    return localObject3;
  }
  
  private static Class<?> getSpiClass(String paramString)
  {
    Class localClass = (Class)spiMap.get(paramString);
    if (localClass != null) {
      return localClass;
    }
    try
    {
      localClass = Class.forName("java.security." + paramString + "Spi");
      spiMap.put(paramString, localClass);
      return localClass;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new AssertionError("Spi class not found", localClassNotFoundException);
    }
  }
  
  static Object[] getImpl(String paramString1, String paramString2, String paramString3)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    if (paramString3 == null) {
      return GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1).toArray();
    }
    return GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1, paramString3).toArray();
  }
  
  static Object[] getImpl(String paramString1, String paramString2, String paramString3, Object paramObject)
    throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException
  {
    if (paramString3 == null) {
      return GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1, paramObject).toArray();
    }
    return GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1, paramObject, paramString3).toArray();
  }
  
  static Object[] getImpl(String paramString1, String paramString2, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    return GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1, paramProvider).toArray();
  }
  
  static Object[] getImpl(String paramString1, String paramString2, Provider paramProvider, Object paramObject)
    throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
  {
    return GetInstance.getInstance(paramString2, getSpiClass(paramString2), paramString1, paramObject, paramProvider).toArray();
  }
  
  public static String getProperty(String paramString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new SecurityPermission("getProperty." + paramString));
    }
    String str = props.getProperty(paramString);
    if (str != null) {
      str = str.trim();
    }
    return str;
  }
  
  public static void setProperty(String paramString1, String paramString2)
  {
    check("setProperty." + paramString1);
    props.put(paramString1, paramString2);
    invalidateSMCache(paramString1);
  }
  
  private static void invalidateSMCache(String paramString)
  {
    boolean bool1 = paramString.equals("package.access");
    boolean bool2 = paramString.equals("package.definition");
    if ((bool1) || (bool2)) {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          try
          {
            Class localClass = Class.forName("java.lang.SecurityManager", false, null);
            Field localField = null;
            boolean bool = false;
            if (this.val$pa)
            {
              localField = localClass.getDeclaredField("packageAccessValid");
              bool = localField.isAccessible();
              localField.setAccessible(true);
            }
            else
            {
              localField = localClass.getDeclaredField("packageDefinitionValid");
              bool = localField.isAccessible();
              localField.setAccessible(true);
            }
            localField.setBoolean(localField, false);
            localField.setAccessible(bool);
          }
          catch (Exception localException) {}
          return null;
        }
      });
    }
  }
  
  private static void check(String paramString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSecurityAccess(paramString);
    }
  }
  
  private static void checkInsertProvider(String paramString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkSecurityAccess("insertProvider");
      }
      catch (SecurityException localSecurityException1)
      {
        try
        {
          localSecurityManager.checkSecurityAccess("insertProvider." + paramString);
        }
        catch (SecurityException localSecurityException2)
        {
          localSecurityException1.addSuppressed(localSecurityException2);
          throw localSecurityException1;
        }
      }
    }
  }
  
  private static LinkedHashSet<Provider> getAllQualifyingCandidates(String paramString1, String paramString2, Provider[] paramArrayOfProvider)
  {
    String[] arrayOfString = getFilterComponents(paramString1, paramString2);
    String str1 = arrayOfString[0];
    String str2 = arrayOfString[1];
    String str3 = arrayOfString[2];
    return getProvidersNotUsingCache(str1, str2, str3, paramString2, paramArrayOfProvider);
  }
  
  private static LinkedHashSet<Provider> getProvidersNotUsingCache(String paramString1, String paramString2, String paramString3, String paramString4, Provider[] paramArrayOfProvider)
  {
    LinkedHashSet localLinkedHashSet = new LinkedHashSet(5);
    for (int i = 0; i < paramArrayOfProvider.length; i++) {
      if (isCriterionSatisfied(paramArrayOfProvider[i], paramString1, paramString2, paramString3, paramString4)) {
        localLinkedHashSet.add(paramArrayOfProvider[i]);
      }
    }
    return localLinkedHashSet;
  }
  
  private static boolean isCriterionSatisfied(Provider paramProvider, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    String str1 = paramString1 + '.' + paramString2;
    if (paramString3 != null) {
      str1 = str1 + ' ' + paramString3;
    }
    String str2 = getProviderProperty(str1, paramProvider);
    if (str2 == null)
    {
      String str3 = getProviderProperty("Alg.Alias." + paramString1 + "." + paramString2, paramProvider);
      if (str3 != null)
      {
        str1 = paramString1 + "." + str3;
        if (paramString3 != null) {
          str1 = str1 + ' ' + paramString3;
        }
        str2 = getProviderProperty(str1, paramProvider);
      }
      if (str2 == null) {
        return false;
      }
    }
    if (paramString3 == null) {
      return true;
    }
    if (isStandardAttr(paramString3)) {
      return isConstraintSatisfied(paramString3, paramString4, str2);
    }
    return paramString4.equalsIgnoreCase(str2);
  }
  
  private static boolean isStandardAttr(String paramString)
  {
    if (paramString.equalsIgnoreCase("KeySize")) {
      return true;
    }
    return paramString.equalsIgnoreCase("ImplementedIn");
  }
  
  private static boolean isConstraintSatisfied(String paramString1, String paramString2, String paramString3)
  {
    if (paramString1.equalsIgnoreCase("KeySize"))
    {
      int i = Integer.parseInt(paramString2);
      int j = Integer.parseInt(paramString3);
      return i <= j;
    }
    if (paramString1.equalsIgnoreCase("ImplementedIn")) {
      return paramString2.equalsIgnoreCase(paramString3);
    }
    return false;
  }
  
  static String[] getFilterComponents(String paramString1, String paramString2)
  {
    int i = paramString1.indexOf('.');
    if (i < 0) {
      throw new InvalidParameterException("Invalid filter");
    }
    String str1 = paramString1.substring(0, i);
    String str2 = null;
    String str3 = null;
    if (paramString2.length() == 0)
    {
      str2 = paramString1.substring(i + 1).trim();
      if (str2.length() == 0) {
        throw new InvalidParameterException("Invalid filter");
      }
    }
    else
    {
      int j = paramString1.indexOf(' ');
      if (j == -1) {
        throw new InvalidParameterException("Invalid filter");
      }
      str3 = paramString1.substring(j + 1).trim();
      if (str3.length() == 0) {
        throw new InvalidParameterException("Invalid filter");
      }
      if ((j < i) || (i == j - 1)) {
        throw new InvalidParameterException("Invalid filter");
      }
      str2 = paramString1.substring(i + 1, j);
    }
    String[] arrayOfString = new String[3];
    arrayOfString[0] = str1;
    arrayOfString[1] = str2;
    arrayOfString[2] = str3;
    return arrayOfString;
  }
  
  public static Set<String> getAlgorithms(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramString.endsWith("."))) {
      return Collections.emptySet();
    }
    HashSet localHashSet = new HashSet();
    Provider[] arrayOfProvider = getProviders();
    for (int i = 0; i < arrayOfProvider.length; i++)
    {
      Enumeration localEnumeration = arrayOfProvider[i].keys();
      while (localEnumeration.hasMoreElements())
      {
        String str = ((String)localEnumeration.nextElement()).toUpperCase(Locale.ENGLISH);
        if ((str.startsWith(paramString.toUpperCase(Locale.ENGLISH))) && (str.indexOf(" ") < 0)) {
          localHashSet.add(str.substring(paramString.length() + 1));
        }
      }
    }
    return Collections.unmodifiableSet(localHashSet);
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        Security.access$000();
        return null;
      }
    });
  }
  
  private static class ProviderProperty
  {
    String className;
    Provider provider;
    
    private ProviderProperty() {}
  }
}
