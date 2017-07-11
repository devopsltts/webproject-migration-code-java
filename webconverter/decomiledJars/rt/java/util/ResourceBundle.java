package java.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.spi.ResourceBundleControlProvider;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.util.locale.BaseLocale;
import sun.util.locale.LocaleObjectCache;

public abstract class ResourceBundle
{
  private static final int INITIAL_CACHE_SIZE = 32;
  private static final ResourceBundle NONEXISTENT_BUNDLE;
  private static final ConcurrentMap<CacheKey, BundleReference> cacheList;
  private static final ReferenceQueue<Object> referenceQueue;
  protected ResourceBundle parent = null;
  private Locale locale = null;
  private String name;
  private volatile boolean expired;
  private volatile CacheKey cacheKey;
  private volatile Set<String> keySet;
  private static final List<ResourceBundleControlProvider> providers;
  
  public String getBaseBundleName()
  {
    return this.name;
  }
  
  public ResourceBundle() {}
  
  public final String getString(String paramString)
  {
    return (String)getObject(paramString);
  }
  
  public final String[] getStringArray(String paramString)
  {
    return (String[])getObject(paramString);
  }
  
  public final Object getObject(String paramString)
  {
    Object localObject = handleGetObject(paramString);
    if (localObject == null)
    {
      if (this.parent != null) {
        localObject = this.parent.getObject(paramString);
      }
      if (localObject == null) {
        throw new MissingResourceException("Can't find resource for bundle " + getClass().getName() + ", key " + paramString, getClass().getName(), paramString);
      }
    }
    return localObject;
  }
  
  public Locale getLocale()
  {
    return this.locale;
  }
  
  private static ClassLoader getLoader(Class<?> paramClass)
  {
    Object localObject = paramClass == null ? null : paramClass.getClassLoader();
    if (localObject == null) {
      localObject = RBClassLoader.INSTANCE;
    }
    return localObject;
  }
  
  protected void setParent(ResourceBundle paramResourceBundle)
  {
    assert (paramResourceBundle != NONEXISTENT_BUNDLE);
    this.parent = paramResourceBundle;
  }
  
  @CallerSensitive
  public static final ResourceBundle getBundle(String paramString)
  {
    return getBundleImpl(paramString, Locale.getDefault(), getLoader(Reflection.getCallerClass()), getDefaultControl(paramString));
  }
  
  @CallerSensitive
  public static final ResourceBundle getBundle(String paramString, Control paramControl)
  {
    return getBundleImpl(paramString, Locale.getDefault(), getLoader(Reflection.getCallerClass()), paramControl);
  }
  
  @CallerSensitive
  public static final ResourceBundle getBundle(String paramString, Locale paramLocale)
  {
    return getBundleImpl(paramString, paramLocale, getLoader(Reflection.getCallerClass()), getDefaultControl(paramString));
  }
  
  @CallerSensitive
  public static final ResourceBundle getBundle(String paramString, Locale paramLocale, Control paramControl)
  {
    return getBundleImpl(paramString, paramLocale, getLoader(Reflection.getCallerClass()), paramControl);
  }
  
  public static ResourceBundle getBundle(String paramString, Locale paramLocale, ClassLoader paramClassLoader)
  {
    if (paramClassLoader == null) {
      throw new NullPointerException();
    }
    return getBundleImpl(paramString, paramLocale, paramClassLoader, getDefaultControl(paramString));
  }
  
  public static ResourceBundle getBundle(String paramString, Locale paramLocale, ClassLoader paramClassLoader, Control paramControl)
  {
    if ((paramClassLoader == null) || (paramControl == null)) {
      throw new NullPointerException();
    }
    return getBundleImpl(paramString, paramLocale, paramClassLoader, paramControl);
  }
  
  private static Control getDefaultControl(String paramString)
  {
    if (providers != null)
    {
      Iterator localIterator = providers.iterator();
      while (localIterator.hasNext())
      {
        ResourceBundleControlProvider localResourceBundleControlProvider = (ResourceBundleControlProvider)localIterator.next();
        Control localControl = localResourceBundleControlProvider.getControl(paramString);
        if (localControl != null) {
          return localControl;
        }
      }
    }
    return Control.INSTANCE;
  }
  
  private static ResourceBundle getBundleImpl(String paramString, Locale paramLocale, ClassLoader paramClassLoader, Control paramControl)
  {
    if ((paramLocale == null) || (paramControl == null)) {
      throw new NullPointerException();
    }
    CacheKey localCacheKey = new CacheKey(paramString, paramLocale, paramClassLoader);
    Object localObject1 = null;
    BundleReference localBundleReference = (BundleReference)cacheList.get(localCacheKey);
    if (localBundleReference != null)
    {
      localObject1 = (ResourceBundle)localBundleReference.get();
      localBundleReference = null;
    }
    if ((isValidBundle((ResourceBundle)localObject1)) && (hasValidParentChain((ResourceBundle)localObject1))) {
      return localObject1;
    }
    int i = (paramControl == Control.INSTANCE) || ((paramControl instanceof SingleFormatControl)) ? 1 : 0;
    List localList1 = paramControl.getFormats(paramString);
    if ((i == 0) && (!checkList(localList1))) {
      throw new IllegalArgumentException("Invalid Control: getFormats");
    }
    Object localObject2 = null;
    for (Locale localLocale = paramLocale; localLocale != null; localLocale = paramControl.getFallbackLocale(paramString, localLocale))
    {
      List localList2 = paramControl.getCandidateLocales(paramString, localLocale);
      if ((i == 0) && (!checkList(localList2))) {
        throw new IllegalArgumentException("Invalid Control: getCandidateLocales");
      }
      localObject1 = findBundle(localCacheKey, localList2, localList1, 0, paramControl, (ResourceBundle)localObject2);
      if (isValidBundle((ResourceBundle)localObject1))
      {
        boolean bool = Locale.ROOT.equals(((ResourceBundle)localObject1).locale);
        if ((!bool) || (((ResourceBundle)localObject1).locale.equals(paramLocale)) || ((localList2.size() == 1) && (((ResourceBundle)localObject1).locale.equals(localList2.get(0))))) {
          break;
        }
        if ((bool) && (localObject2 == null)) {
          localObject2 = localObject1;
        }
      }
    }
    if (localObject1 == null)
    {
      if (localObject2 == null) {
        throwMissingResourceException(paramString, paramLocale, localCacheKey.getCause());
      }
      localObject1 = localObject2;
    }
    return localObject1;
  }
  
  private static boolean checkList(List<?> paramList)
  {
    boolean bool = (paramList != null) && (!paramList.isEmpty());
    if (bool)
    {
      int i = paramList.size();
      for (int j = 0; (bool) && (j < i); j++) {
        bool = paramList.get(j) != null;
      }
    }
    return bool;
  }
  
  private static ResourceBundle findBundle(CacheKey paramCacheKey, List<Locale> paramList, List<String> paramList1, int paramInt, Control paramControl, ResourceBundle paramResourceBundle)
  {
    Locale localLocale = (Locale)paramList.get(paramInt);
    ResourceBundle localResourceBundle1 = null;
    if (paramInt != paramList.size() - 1) {
      localResourceBundle1 = findBundle(paramCacheKey, paramList, paramList1, paramInt + 1, paramControl, paramResourceBundle);
    } else if ((paramResourceBundle != null) && (Locale.ROOT.equals(localLocale))) {
      return paramResourceBundle;
    }
    Reference localReference;
    while ((localReference = referenceQueue.poll()) != null) {
      cacheList.remove(((CacheKeyReference)localReference).getCacheKey());
    }
    boolean bool = false;
    paramCacheKey.setLocale(localLocale);
    ResourceBundle localResourceBundle2 = findBundleInCache(paramCacheKey, paramControl);
    Object localObject1;
    if (isValidBundle(localResourceBundle2))
    {
      bool = localResourceBundle2.expired;
      if (!bool)
      {
        if (localResourceBundle2.parent == localResourceBundle1) {
          return localResourceBundle2;
        }
        localObject1 = (BundleReference)cacheList.get(paramCacheKey);
        if ((localObject1 != null) && (((BundleReference)localObject1).get() == localResourceBundle2)) {
          cacheList.remove(paramCacheKey, localObject1);
        }
      }
    }
    if (localResourceBundle2 != NONEXISTENT_BUNDLE)
    {
      localObject1 = (CacheKey)paramCacheKey.clone();
      try
      {
        localResourceBundle2 = loadBundle(paramCacheKey, paramList1, paramControl, bool);
        if (localResourceBundle2 != null)
        {
          if (localResourceBundle2.parent == null) {
            localResourceBundle2.setParent(localResourceBundle1);
          }
          localResourceBundle2.locale = localLocale;
          localResourceBundle2 = putBundleInCache(paramCacheKey, localResourceBundle2, paramControl);
          ResourceBundle localResourceBundle3 = localResourceBundle2;
          return localResourceBundle3;
        }
        putBundleInCache(paramCacheKey, NONEXISTENT_BUNDLE, paramControl);
      }
      finally
      {
        if ((((CacheKey)localObject1).getCause() instanceof InterruptedException)) {
          Thread.currentThread().interrupt();
        }
      }
    }
    return localResourceBundle1;
  }
  
  private static ResourceBundle loadBundle(CacheKey paramCacheKey, List<String> paramList, Control paramControl, boolean paramBoolean)
  {
    Locale localLocale = paramCacheKey.getLocale();
    ResourceBundle localResourceBundle = null;
    int i = paramList.size();
    for (int j = 0; j < i; j++)
    {
      String str = (String)paramList.get(j);
      try
      {
        localResourceBundle = paramControl.newBundle(paramCacheKey.getName(), localLocale, str, paramCacheKey.getLoader(), paramBoolean);
      }
      catch (LinkageError localLinkageError)
      {
        paramCacheKey.setCause(localLinkageError);
      }
      catch (Exception localException)
      {
        paramCacheKey.setCause(localException);
      }
      if (localResourceBundle != null)
      {
        paramCacheKey.setFormat(str);
        localResourceBundle.name = paramCacheKey.getName();
        localResourceBundle.locale = localLocale;
        localResourceBundle.expired = false;
        break;
      }
    }
    return localResourceBundle;
  }
  
  private static boolean isValidBundle(ResourceBundle paramResourceBundle)
  {
    return (paramResourceBundle != null) && (paramResourceBundle != NONEXISTENT_BUNDLE);
  }
  
  private static boolean hasValidParentChain(ResourceBundle paramResourceBundle)
  {
    long l1 = System.currentTimeMillis();
    while (paramResourceBundle != null)
    {
      if (paramResourceBundle.expired) {
        return false;
      }
      CacheKey localCacheKey = paramResourceBundle.cacheKey;
      if (localCacheKey != null)
      {
        long l2 = localCacheKey.expirationTime;
        if ((l2 >= 0L) && (l2 <= l1)) {
          return false;
        }
      }
      paramResourceBundle = paramResourceBundle.parent;
    }
    return true;
  }
  
  private static void throwMissingResourceException(String paramString, Locale paramLocale, Throwable paramThrowable)
  {
    if ((paramThrowable instanceof MissingResourceException)) {
      paramThrowable = null;
    }
    throw new MissingResourceException("Can't find bundle for base name " + paramString + ", locale " + paramLocale, paramString + "_" + paramLocale, "", paramThrowable);
  }
  
  private static ResourceBundle findBundleInCache(CacheKey paramCacheKey, Control paramControl)
  {
    BundleReference localBundleReference = (BundleReference)cacheList.get(paramCacheKey);
    if (localBundleReference == null) {
      return null;
    }
    ResourceBundle localResourceBundle1 = (ResourceBundle)localBundleReference.get();
    if (localResourceBundle1 == null) {
      return null;
    }
    ResourceBundle localResourceBundle2 = localResourceBundle1.parent;
    assert (localResourceBundle2 != NONEXISTENT_BUNDLE);
    if ((localResourceBundle2 != null) && (localResourceBundle2.expired))
    {
      assert (localResourceBundle1 != NONEXISTENT_BUNDLE);
      localResourceBundle1.expired = true;
      localResourceBundle1.cacheKey = null;
      cacheList.remove(paramCacheKey, localBundleReference);
      localResourceBundle1 = null;
    }
    else
    {
      CacheKey localCacheKey = localBundleReference.getCacheKey();
      long l = localCacheKey.expirationTime;
      if ((!localResourceBundle1.expired) && (l >= 0L) && (l <= System.currentTimeMillis())) {
        if (localResourceBundle1 != NONEXISTENT_BUNDLE)
        {
          synchronized (localResourceBundle1)
          {
            l = localCacheKey.expirationTime;
            if ((!localResourceBundle1.expired) && (l >= 0L) && (l <= System.currentTimeMillis()))
            {
              try
              {
                localResourceBundle1.expired = paramControl.needsReload(localCacheKey.getName(), localCacheKey.getLocale(), localCacheKey.getFormat(), localCacheKey.getLoader(), localResourceBundle1, localCacheKey.loadTime);
              }
              catch (Exception localException)
              {
                paramCacheKey.setCause(localException);
              }
              if (localResourceBundle1.expired)
              {
                localResourceBundle1.cacheKey = null;
                cacheList.remove(paramCacheKey, localBundleReference);
              }
              else
              {
                setExpirationTime(localCacheKey, paramControl);
              }
            }
          }
        }
        else
        {
          cacheList.remove(paramCacheKey, localBundleReference);
          localResourceBundle1 = null;
        }
      }
    }
    return localResourceBundle1;
  }
  
  private static ResourceBundle putBundleInCache(CacheKey paramCacheKey, ResourceBundle paramResourceBundle, Control paramControl)
  {
    setExpirationTime(paramCacheKey, paramControl);
    if (paramCacheKey.expirationTime != -1L)
    {
      CacheKey localCacheKey = (CacheKey)paramCacheKey.clone();
      BundleReference localBundleReference1 = new BundleReference(paramResourceBundle, referenceQueue, localCacheKey);
      paramResourceBundle.cacheKey = localCacheKey;
      BundleReference localBundleReference2 = (BundleReference)cacheList.putIfAbsent(localCacheKey, localBundleReference1);
      if (localBundleReference2 != null)
      {
        ResourceBundle localResourceBundle = (ResourceBundle)localBundleReference2.get();
        if ((localResourceBundle != null) && (!localResourceBundle.expired))
        {
          paramResourceBundle.cacheKey = null;
          paramResourceBundle = localResourceBundle;
          localBundleReference1.clear();
        }
        else
        {
          cacheList.put(localCacheKey, localBundleReference1);
        }
      }
    }
    return paramResourceBundle;
  }
  
  private static void setExpirationTime(CacheKey paramCacheKey, Control paramControl)
  {
    long l1 = paramControl.getTimeToLive(paramCacheKey.getName(), paramCacheKey.getLocale());
    if (l1 >= 0L)
    {
      long l2 = System.currentTimeMillis();
      paramCacheKey.loadTime = l2;
      paramCacheKey.expirationTime = (l2 + l1);
    }
    else if (l1 >= -2L)
    {
      paramCacheKey.expirationTime = l1;
    }
    else
    {
      throw new IllegalArgumentException("Invalid Control: TTL=" + l1);
    }
  }
  
  @CallerSensitive
  public static final void clearCache()
  {
    clearCache(getLoader(Reflection.getCallerClass()));
  }
  
  public static final void clearCache(ClassLoader paramClassLoader)
  {
    if (paramClassLoader == null) {
      throw new NullPointerException();
    }
    Set localSet = cacheList.keySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      CacheKey localCacheKey = (CacheKey)localIterator.next();
      if (localCacheKey.getLoader() == paramClassLoader) {
        localSet.remove(localCacheKey);
      }
    }
  }
  
  protected abstract Object handleGetObject(String paramString);
  
  public abstract Enumeration<String> getKeys();
  
  public boolean containsKey(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    for (ResourceBundle localResourceBundle = this; localResourceBundle != null; localResourceBundle = localResourceBundle.parent) {
      if (localResourceBundle.handleKeySet().contains(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  public Set<String> keySet()
  {
    HashSet localHashSet = new HashSet();
    for (ResourceBundle localResourceBundle = this; localResourceBundle != null; localResourceBundle = localResourceBundle.parent) {
      localHashSet.addAll(localResourceBundle.handleKeySet());
    }
    return localHashSet;
  }
  
  protected Set<String> handleKeySet()
  {
    if (this.keySet == null) {
      synchronized (this)
      {
        if (this.keySet == null)
        {
          HashSet localHashSet = new HashSet();
          Enumeration localEnumeration = getKeys();
          while (localEnumeration.hasMoreElements())
          {
            String str = (String)localEnumeration.nextElement();
            if (handleGetObject(str) != null) {
              localHashSet.add(str);
            }
          }
          this.keySet = localHashSet;
        }
      }
    }
    return this.keySet;
  }
  
  static
  {
    NONEXISTENT_BUNDLE = new ResourceBundle()
    {
      public Enumeration<String> getKeys()
      {
        return null;
      }
      
      protected Object handleGetObject(String paramAnonymousString)
      {
        return null;
      }
      
      public String toString()
      {
        return "NONEXISTENT_BUNDLE";
      }
    };
    cacheList = new ConcurrentHashMap(32);
    referenceQueue = new ReferenceQueue();
    ArrayList localArrayList = null;
    ServiceLoader localServiceLoader = ServiceLoader.loadInstalled(ResourceBundleControlProvider.class);
    Iterator localIterator = localServiceLoader.iterator();
    while (localIterator.hasNext())
    {
      ResourceBundleControlProvider localResourceBundleControlProvider = (ResourceBundleControlProvider)localIterator.next();
      if (localArrayList == null) {
        localArrayList = new ArrayList();
      }
      localArrayList.add(localResourceBundleControlProvider);
    }
    providers = localArrayList;
  }
  
  private static class BundleReference
    extends SoftReference<ResourceBundle>
    implements ResourceBundle.CacheKeyReference
  {
    private ResourceBundle.CacheKey cacheKey;
    
    BundleReference(ResourceBundle paramResourceBundle, ReferenceQueue<Object> paramReferenceQueue, ResourceBundle.CacheKey paramCacheKey)
    {
      super(paramReferenceQueue);
      this.cacheKey = paramCacheKey;
    }
    
    public ResourceBundle.CacheKey getCacheKey()
    {
      return this.cacheKey;
    }
  }
  
  private static class CacheKey
    implements Cloneable
  {
    private String name;
    private Locale locale;
    private ResourceBundle.LoaderReference loaderRef;
    private String format;
    private volatile long loadTime;
    private volatile long expirationTime;
    private Throwable cause;
    private int hashCodeCache;
    
    CacheKey(String paramString, Locale paramLocale, ClassLoader paramClassLoader)
    {
      this.name = paramString;
      this.locale = paramLocale;
      if (paramClassLoader == null) {
        this.loaderRef = null;
      } else {
        this.loaderRef = new ResourceBundle.LoaderReference(paramClassLoader, ResourceBundle.referenceQueue, this);
      }
      calculateHashCode();
    }
    
    String getName()
    {
      return this.name;
    }
    
    CacheKey setName(String paramString)
    {
      if (!this.name.equals(paramString))
      {
        this.name = paramString;
        calculateHashCode();
      }
      return this;
    }
    
    Locale getLocale()
    {
      return this.locale;
    }
    
    CacheKey setLocale(Locale paramLocale)
    {
      if (!this.locale.equals(paramLocale))
      {
        this.locale = paramLocale;
        calculateHashCode();
      }
      return this;
    }
    
    ClassLoader getLoader()
    {
      return this.loaderRef != null ? (ClassLoader)this.loaderRef.get() : null;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      try
      {
        CacheKey localCacheKey = (CacheKey)paramObject;
        if (this.hashCodeCache != localCacheKey.hashCodeCache) {
          return false;
        }
        if (!this.name.equals(localCacheKey.name)) {
          return false;
        }
        if (!this.locale.equals(localCacheKey.locale)) {
          return false;
        }
        if (this.loaderRef == null) {
          return localCacheKey.loaderRef == null;
        }
        ClassLoader localClassLoader = (ClassLoader)this.loaderRef.get();
        return (localCacheKey.loaderRef != null) && (localClassLoader != null) && (localClassLoader == localCacheKey.loaderRef.get());
      }
      catch (NullPointerException|ClassCastException localNullPointerException) {}
      return false;
    }
    
    public int hashCode()
    {
      return this.hashCodeCache;
    }
    
    private void calculateHashCode()
    {
      this.hashCodeCache = (this.name.hashCode() << 3);
      this.hashCodeCache ^= this.locale.hashCode();
      ClassLoader localClassLoader = getLoader();
      if (localClassLoader != null) {
        this.hashCodeCache ^= localClassLoader.hashCode();
      }
    }
    
    public Object clone()
    {
      try
      {
        CacheKey localCacheKey = (CacheKey)super.clone();
        if (this.loaderRef != null) {
          localCacheKey.loaderRef = new ResourceBundle.LoaderReference((ClassLoader)this.loaderRef.get(), ResourceBundle.referenceQueue, localCacheKey);
        }
        localCacheKey.cause = null;
        return localCacheKey;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new InternalError(localCloneNotSupportedException);
      }
    }
    
    String getFormat()
    {
      return this.format;
    }
    
    void setFormat(String paramString)
    {
      this.format = paramString;
    }
    
    private void setCause(Throwable paramThrowable)
    {
      if (this.cause == null) {
        this.cause = paramThrowable;
      } else if ((this.cause instanceof ClassNotFoundException)) {
        this.cause = paramThrowable;
      }
    }
    
    private Throwable getCause()
    {
      return this.cause;
    }
    
    public String toString()
    {
      String str = this.locale.toString();
      if (str.length() == 0) {
        if (this.locale.getVariant().length() != 0) {
          str = "__" + this.locale.getVariant();
        } else {
          str = "\"\"";
        }
      }
      return "CacheKey[" + this.name + ", lc=" + str + ", ldr=" + getLoader() + "(format=" + this.format + ")]";
    }
  }
  
  private static abstract interface CacheKeyReference
  {
    public abstract ResourceBundle.CacheKey getCacheKey();
  }
  
  public static class Control
  {
    public static final List<String> FORMAT_DEFAULT = Collections.unmodifiableList(Arrays.asList(new String[] { "java.class", "java.properties" }));
    public static final List<String> FORMAT_CLASS = Collections.unmodifiableList(Arrays.asList(new String[] { "java.class" }));
    public static final List<String> FORMAT_PROPERTIES = Collections.unmodifiableList(Arrays.asList(new String[] { "java.properties" }));
    public static final long TTL_DONT_CACHE = -1L;
    public static final long TTL_NO_EXPIRATION_CONTROL = -2L;
    private static final Control INSTANCE = new Control();
    private static final CandidateListCache CANDIDATES_CACHE = new CandidateListCache(null);
    
    protected Control() {}
    
    public static final Control getControl(List<String> paramList)
    {
      if (paramList.equals(FORMAT_PROPERTIES)) {
        return ResourceBundle.SingleFormatControl.access$800();
      }
      if (paramList.equals(FORMAT_CLASS)) {
        return ResourceBundle.SingleFormatControl.access$900();
      }
      if (paramList.equals(FORMAT_DEFAULT)) {
        return INSTANCE;
      }
      throw new IllegalArgumentException();
    }
    
    public static final Control getNoFallbackControl(List<String> paramList)
    {
      if (paramList.equals(FORMAT_DEFAULT)) {
        return ResourceBundle.NoFallbackControl.access$1000();
      }
      if (paramList.equals(FORMAT_PROPERTIES)) {
        return ResourceBundle.NoFallbackControl.access$1100();
      }
      if (paramList.equals(FORMAT_CLASS)) {
        return ResourceBundle.NoFallbackControl.access$1200();
      }
      throw new IllegalArgumentException();
    }
    
    public List<String> getFormats(String paramString)
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      return FORMAT_DEFAULT;
    }
    
    public List<Locale> getCandidateLocales(String paramString, Locale paramLocale)
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      return new ArrayList((Collection)CANDIDATES_CACHE.get(paramLocale.getBaseLocale()));
    }
    
    public Locale getFallbackLocale(String paramString, Locale paramLocale)
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      Locale localLocale = Locale.getDefault();
      return paramLocale.equals(localLocale) ? null : localLocale;
    }
    
    public ResourceBundle newBundle(String paramString1, Locale paramLocale, String paramString2, ClassLoader paramClassLoader, boolean paramBoolean)
      throws IllegalAccessException, InstantiationException, IOException
    {
      String str1 = toBundleName(paramString1, paramLocale);
      Object localObject1 = null;
      if (paramString2.equals("java.class"))
      {
        try
        {
          Class localClass = paramClassLoader.loadClass(str1);
          if (ResourceBundle.class.isAssignableFrom(localClass)) {
            localObject1 = (ResourceBundle)localClass.newInstance();
          } else {
            throw new ClassCastException(localClass.getName() + " cannot be cast to ResourceBundle");
          }
        }
        catch (ClassNotFoundException localClassNotFoundException) {}
      }
      else if (paramString2.equals("java.properties"))
      {
        final String str2 = toResourceName0(str1, "properties");
        if (str2 == null) {
          return localObject1;
        }
        final ClassLoader localClassLoader = paramClassLoader;
        final boolean bool = paramBoolean;
        InputStream localInputStream = null;
        try
        {
          localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            public InputStream run()
              throws IOException
            {
              InputStream localInputStream = null;
              if (bool)
              {
                URL localURL = localClassLoader.getResource(str2);
                if (localURL != null)
                {
                  URLConnection localURLConnection = localURL.openConnection();
                  if (localURLConnection != null)
                  {
                    localURLConnection.setUseCaches(false);
                    localInputStream = localURLConnection.getInputStream();
                  }
                }
              }
              else
              {
                localInputStream = localClassLoader.getResourceAsStream(str2);
              }
              return localInputStream;
            }
          });
        }
        catch (PrivilegedActionException localPrivilegedActionException)
        {
          throw ((IOException)localPrivilegedActionException.getException());
        }
        if (localInputStream != null) {
          try
          {
            localObject1 = new PropertyResourceBundle(localInputStream);
          }
          finally
          {
            localInputStream.close();
          }
        }
      }
      else
      {
        throw new IllegalArgumentException("unknown format: " + paramString2);
      }
      return localObject1;
    }
    
    public long getTimeToLive(String paramString, Locale paramLocale)
    {
      if ((paramString == null) || (paramLocale == null)) {
        throw new NullPointerException();
      }
      return -2L;
    }
    
    public boolean needsReload(String paramString1, Locale paramLocale, String paramString2, ClassLoader paramClassLoader, ResourceBundle paramResourceBundle, long paramLong)
    {
      if (paramResourceBundle == null) {
        throw new NullPointerException();
      }
      if ((paramString2.equals("java.class")) || (paramString2.equals("java.properties"))) {
        paramString2 = paramString2.substring(5);
      }
      boolean bool = false;
      try
      {
        String str = toResourceName0(toBundleName(paramString1, paramLocale), paramString2);
        if (str == null) {
          return bool;
        }
        URL localURL = paramClassLoader.getResource(str);
        if (localURL != null)
        {
          long l = 0L;
          URLConnection localURLConnection = localURL.openConnection();
          if (localURLConnection != null)
          {
            localURLConnection.setUseCaches(false);
            if ((localURLConnection instanceof JarURLConnection))
            {
              JarEntry localJarEntry = ((JarURLConnection)localURLConnection).getJarEntry();
              if (localJarEntry != null)
              {
                l = localJarEntry.getTime();
                if (l == -1L) {
                  l = 0L;
                }
              }
            }
            else
            {
              l = localURLConnection.getLastModified();
            }
          }
          bool = l >= paramLong;
        }
      }
      catch (NullPointerException localNullPointerException)
      {
        throw localNullPointerException;
      }
      catch (Exception localException) {}
      return bool;
    }
    
    public String toBundleName(String paramString, Locale paramLocale)
    {
      if (paramLocale == Locale.ROOT) {
        return paramString;
      }
      String str1 = paramLocale.getLanguage();
      String str2 = paramLocale.getScript();
      String str3 = paramLocale.getCountry();
      String str4 = paramLocale.getVariant();
      if ((str1 == "") && (str3 == "") && (str4 == "")) {
        return paramString;
      }
      StringBuilder localStringBuilder = new StringBuilder(paramString);
      localStringBuilder.append('_');
      if (str2 != "")
      {
        if (str4 != "") {
          localStringBuilder.append(str1).append('_').append(str2).append('_').append(str3).append('_').append(str4);
        } else if (str3 != "") {
          localStringBuilder.append(str1).append('_').append(str2).append('_').append(str3);
        } else {
          localStringBuilder.append(str1).append('_').append(str2);
        }
      }
      else if (str4 != "") {
        localStringBuilder.append(str1).append('_').append(str3).append('_').append(str4);
      } else if (str3 != "") {
        localStringBuilder.append(str1).append('_').append(str3);
      } else {
        localStringBuilder.append(str1);
      }
      return localStringBuilder.toString();
    }
    
    public final String toResourceName(String paramString1, String paramString2)
    {
      StringBuilder localStringBuilder = new StringBuilder(paramString1.length() + 1 + paramString2.length());
      localStringBuilder.append(paramString1.replace('.', '/')).append('.').append(paramString2);
      return localStringBuilder.toString();
    }
    
    private String toResourceName0(String paramString1, String paramString2)
    {
      if (paramString1.contains("://")) {
        return null;
      }
      return toResourceName(paramString1, paramString2);
    }
    
    private static class CandidateListCache
      extends LocaleObjectCache<BaseLocale, List<Locale>>
    {
      private CandidateListCache() {}
      
      protected List<Locale> createObject(BaseLocale paramBaseLocale)
      {
        String str1 = paramBaseLocale.getLanguage();
        String str2 = paramBaseLocale.getScript();
        String str3 = paramBaseLocale.getRegion();
        String str4 = paramBaseLocale.getVariant();
        int i = 0;
        int j = 0;
        if (str1.equals("no")) {
          if ((str3.equals("NO")) && (str4.equals("NY")))
          {
            str4 = "";
            j = 1;
          }
          else
          {
            i = 1;
          }
        }
        Object localObject;
        if ((str1.equals("nb")) || (i != 0))
        {
          localObject = getDefaultList("nb", str2, str3, str4);
          LinkedList localLinkedList = new LinkedList();
          Iterator localIterator = ((List)localObject).iterator();
          while (localIterator.hasNext())
          {
            Locale localLocale = (Locale)localIterator.next();
            localLinkedList.add(localLocale);
            if (localLocale.getLanguage().length() == 0) {
              break;
            }
            localLinkedList.add(Locale.getInstance("no", localLocale.getScript(), localLocale.getCountry(), localLocale.getVariant(), null));
          }
          return localLinkedList;
        }
        int k;
        if ((str1.equals("nn")) || (j != 0))
        {
          localObject = getDefaultList("nn", str2, str3, str4);
          k = ((List)localObject).size() - 1;
          ((List)localObject).add(k++, Locale.getInstance("no", "NO", "NY"));
          ((List)localObject).add(k++, Locale.getInstance("no", "NO", ""));
          ((List)localObject).add(k++, Locale.getInstance("no", "", ""));
          return localObject;
        }
        if (str1.equals("zh")) {
          if ((str2.length() == 0) && (str3.length() > 0))
          {
            localObject = str3;
            k = -1;
            switch (((String)localObject).hashCode())
            {
            case 2691: 
              if (((String)localObject).equals("TW")) {
                k = 0;
              }
              break;
            case 2307: 
              if (((String)localObject).equals("HK")) {
                k = 1;
              }
              break;
            case 2466: 
              if (((String)localObject).equals("MO")) {
                k = 2;
              }
              break;
            case 2155: 
              if (((String)localObject).equals("CN")) {
                k = 3;
              }
              break;
            case 2644: 
              if (((String)localObject).equals("SG")) {
                k = 4;
              }
              break;
            }
            switch (k)
            {
            case 0: 
            case 1: 
            case 2: 
              str2 = "Hant";
              break;
            case 3: 
            case 4: 
              str2 = "Hans";
            }
          }
          else if ((str2.length() > 0) && (str3.length() == 0))
          {
            localObject = str2;
            k = -1;
            switch (((String)localObject).hashCode())
            {
            case 2241694: 
              if (((String)localObject).equals("Hans")) {
                k = 0;
              }
              break;
            case 2241695: 
              if (((String)localObject).equals("Hant")) {
                k = 1;
              }
              break;
            }
            switch (k)
            {
            case 0: 
              str3 = "CN";
              break;
            case 1: 
              str3 = "TW";
            }
          }
        }
        return getDefaultList(str1, str2, str3, str4);
      }
      
      private static List<Locale> getDefaultList(String paramString1, String paramString2, String paramString3, String paramString4)
      {
        LinkedList localLinkedList1 = null;
        if (paramString4.length() > 0)
        {
          localLinkedList1 = new LinkedList();
          for (int i = paramString4.length(); i != -1; i = paramString4.lastIndexOf('_', --i)) {
            localLinkedList1.add(paramString4.substring(0, i));
          }
        }
        LinkedList localLinkedList2 = new LinkedList();
        Iterator localIterator;
        String str;
        if (localLinkedList1 != null)
        {
          localIterator = localLinkedList1.iterator();
          while (localIterator.hasNext())
          {
            str = (String)localIterator.next();
            localLinkedList2.add(Locale.getInstance(paramString1, paramString2, paramString3, str, null));
          }
        }
        if (paramString3.length() > 0) {
          localLinkedList2.add(Locale.getInstance(paramString1, paramString2, paramString3, "", null));
        }
        if (paramString2.length() > 0)
        {
          localLinkedList2.add(Locale.getInstance(paramString1, paramString2, "", "", null));
          if (localLinkedList1 != null)
          {
            localIterator = localLinkedList1.iterator();
            while (localIterator.hasNext())
            {
              str = (String)localIterator.next();
              localLinkedList2.add(Locale.getInstance(paramString1, "", paramString3, str, null));
            }
          }
          if (paramString3.length() > 0) {
            localLinkedList2.add(Locale.getInstance(paramString1, "", paramString3, "", null));
          }
        }
        if (paramString1.length() > 0) {
          localLinkedList2.add(Locale.getInstance(paramString1, "", "", "", null));
        }
        localLinkedList2.add(Locale.ROOT);
        return localLinkedList2;
      }
    }
  }
  
  private static class LoaderReference
    extends WeakReference<ClassLoader>
    implements ResourceBundle.CacheKeyReference
  {
    private ResourceBundle.CacheKey cacheKey;
    
    LoaderReference(ClassLoader paramClassLoader, ReferenceQueue<Object> paramReferenceQueue, ResourceBundle.CacheKey paramCacheKey)
    {
      super(paramReferenceQueue);
      this.cacheKey = paramCacheKey;
    }
    
    public ResourceBundle.CacheKey getCacheKey()
    {
      return this.cacheKey;
    }
  }
  
  private static final class NoFallbackControl
    extends ResourceBundle.SingleFormatControl
  {
    private static final ResourceBundle.Control NO_FALLBACK = new NoFallbackControl(FORMAT_DEFAULT);
    private static final ResourceBundle.Control PROPERTIES_ONLY_NO_FALLBACK = new NoFallbackControl(FORMAT_PROPERTIES);
    private static final ResourceBundle.Control CLASS_ONLY_NO_FALLBACK = new NoFallbackControl(FORMAT_CLASS);
    
    protected NoFallbackControl(List<String> paramList)
    {
      super();
    }
    
    public Locale getFallbackLocale(String paramString, Locale paramLocale)
    {
      if ((paramString == null) || (paramLocale == null)) {
        throw new NullPointerException();
      }
      return null;
    }
  }
  
  private static class RBClassLoader
    extends ClassLoader
  {
    private static final RBClassLoader INSTANCE = (RBClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ResourceBundle.RBClassLoader run()
      {
        return new ResourceBundle.RBClassLoader(null);
      }
    });
    private static final ClassLoader loader = ClassLoader.getSystemClassLoader();
    
    private RBClassLoader() {}
    
    public Class<?> loadClass(String paramString)
      throws ClassNotFoundException
    {
      if (loader != null) {
        return loader.loadClass(paramString);
      }
      return Class.forName(paramString);
    }
    
    public URL getResource(String paramString)
    {
      if (loader != null) {
        return loader.getResource(paramString);
      }
      return ClassLoader.getSystemResource(paramString);
    }
    
    public InputStream getResourceAsStream(String paramString)
    {
      if (loader != null) {
        return loader.getResourceAsStream(paramString);
      }
      return ClassLoader.getSystemResourceAsStream(paramString);
    }
  }
  
  private static class SingleFormatControl
    extends ResourceBundle.Control
  {
    private static final ResourceBundle.Control PROPERTIES_ONLY = new SingleFormatControl(FORMAT_PROPERTIES);
    private static final ResourceBundle.Control CLASS_ONLY = new SingleFormatControl(FORMAT_CLASS);
    private final List<String> formats;
    
    protected SingleFormatControl(List<String> paramList)
    {
      this.formats = paramList;
    }
    
    public List<String> getFormats(String paramString)
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      return this.formats;
    }
  }
}
