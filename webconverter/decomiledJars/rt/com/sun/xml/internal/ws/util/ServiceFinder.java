package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentEx;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class ServiceFinder<T>
  implements Iterable<T>
{
  private static final String prefix = "META-INF/services/";
  private static WeakHashMap<ClassLoader, ConcurrentHashMap<String, ServiceName[]>> serviceNameCache = new WeakHashMap();
  private final Class<T> serviceClass;
  @Nullable
  private final ClassLoader classLoader;
  @Nullable
  private final ComponentEx component;
  
  public static <T> ServiceFinder<T> find(@NotNull Class<T> paramClass, @Nullable ClassLoader paramClassLoader, Component paramComponent)
  {
    return new ServiceFinder(paramClass, paramClassLoader, paramComponent);
  }
  
  public static <T> ServiceFinder<T> find(@NotNull Class<T> paramClass, Component paramComponent)
  {
    return find(paramClass, Thread.currentThread().getContextClassLoader(), paramComponent);
  }
  
  public static <T> ServiceFinder<T> find(@NotNull Class<T> paramClass, @Nullable ClassLoader paramClassLoader)
  {
    return find(paramClass, paramClassLoader, ContainerResolver.getInstance().getContainer());
  }
  
  public static <T> ServiceFinder<T> find(Class<T> paramClass)
  {
    return find(paramClass, Thread.currentThread().getContextClassLoader());
  }
  
  private ServiceFinder(Class<T> paramClass, ClassLoader paramClassLoader, Component paramComponent)
  {
    this.serviceClass = paramClass;
    this.classLoader = paramClassLoader;
    this.component = getComponentEx(paramComponent);
  }
  
  private static ServiceName[] serviceClassNames(Class paramClass, ClassLoader paramClassLoader)
  {
    ArrayList localArrayList = new ArrayList();
    ServiceNameIterator localServiceNameIterator = new ServiceNameIterator(paramClass, paramClassLoader, null);
    while (localServiceNameIterator.hasNext()) {
      localArrayList.add(localServiceNameIterator.next());
    }
    return (ServiceName[])localArrayList.toArray(new ServiceName[localArrayList.size()]);
  }
  
  public Iterator<T> iterator()
  {
    LazyIterator localLazyIterator = new LazyIterator(this.serviceClass, this.classLoader, null);
    return this.component != null ? new CompositeIterator(new Iterator[] { this.component.getIterableSPI(this.serviceClass).iterator(), localLazyIterator }) : localLazyIterator;
  }
  
  public T[] toArray()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      localArrayList.add(localObject);
    }
    return localArrayList.toArray((Object[])Array.newInstance(this.serviceClass, localArrayList.size()));
  }
  
  private static void fail(Class paramClass, String paramString, Throwable paramThrowable)
    throws ServiceConfigurationError
  {
    ServiceConfigurationError localServiceConfigurationError = new ServiceConfigurationError(paramClass.getName() + ": " + paramString);
    localServiceConfigurationError.initCause(paramThrowable);
    throw localServiceConfigurationError;
  }
  
  private static void fail(Class paramClass, String paramString)
    throws ServiceConfigurationError
  {
    throw new ServiceConfigurationError(paramClass.getName() + ": " + paramString);
  }
  
  private static void fail(Class paramClass, URL paramURL, int paramInt, String paramString)
    throws ServiceConfigurationError
  {
    fail(paramClass, paramURL + ":" + paramInt + ": " + paramString);
  }
  
  private static int parseLine(Class paramClass, URL paramURL, BufferedReader paramBufferedReader, int paramInt, List<String> paramList, Set<String> paramSet)
    throws IOException, ServiceConfigurationError
  {
    String str = paramBufferedReader.readLine();
    if (str == null) {
      return -1;
    }
    int i = str.indexOf('#');
    if (i >= 0) {
      str = str.substring(0, i);
    }
    str = str.trim();
    int j = str.length();
    if (j != 0)
    {
      if ((str.indexOf(' ') >= 0) || (str.indexOf('\t') >= 0)) {
        fail(paramClass, paramURL, paramInt, "Illegal configuration-file syntax");
      }
      int k = str.codePointAt(0);
      if (!Character.isJavaIdentifierStart(k)) {
        fail(paramClass, paramURL, paramInt, "Illegal provider-class name: " + str);
      }
      int m = Character.charCount(k);
      while (m < j)
      {
        k = str.codePointAt(m);
        if ((!Character.isJavaIdentifierPart(k)) && (k != 46)) {
          fail(paramClass, paramURL, paramInt, "Illegal provider-class name: " + str);
        }
        m += Character.charCount(k);
      }
      if (!paramSet.contains(str))
      {
        paramList.add(str);
        paramSet.add(str);
      }
    }
    return paramInt + 1;
  }
  
  private static Iterator<String> parse(Class paramClass, URL paramURL, Set<String> paramSet)
    throws ServiceConfigurationError
  {
    InputStream localInputStream = null;
    BufferedReader localBufferedReader = null;
    localArrayList = new ArrayList();
    try
    {
      localInputStream = paramURL.openStream();
      localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream, "utf-8"));
      int i = 1;
      while ((i = parseLine(paramClass, paramURL, localBufferedReader, i, localArrayList, paramSet)) >= 0) {}
      return localArrayList.iterator();
    }
    catch (IOException localIOException2)
    {
      fail(paramClass, ": " + localIOException2);
    }
    finally
    {
      try
      {
        if (localBufferedReader != null) {
          localBufferedReader.close();
        }
        if (localInputStream != null) {
          localInputStream.close();
        }
      }
      catch (IOException localIOException4)
      {
        fail(paramClass, ": " + localIOException4);
      }
    }
  }
  
  private static ComponentEx getComponentEx(Component paramComponent)
  {
    if ((paramComponent instanceof ComponentEx)) {
      return (ComponentEx)paramComponent;
    }
    return paramComponent != null ? new ComponentExWrapper(paramComponent) : null;
  }
  
  private static class ComponentExWrapper
    implements ComponentEx
  {
    private final Component component;
    
    public ComponentExWrapper(Component paramComponent)
    {
      this.component = paramComponent;
    }
    
    public <S> S getSPI(Class<S> paramClass)
    {
      return this.component.getSPI(paramClass);
    }
    
    public <S> Iterable<S> getIterableSPI(Class<S> paramClass)
    {
      Object localObject = getSPI(paramClass);
      if (localObject != null)
      {
        List localList = Collections.singletonList(localObject);
        return localList;
      }
      return Collections.emptySet();
    }
  }
  
  private static class CompositeIterator<T>
    implements Iterator<T>
  {
    private final Iterator<Iterator<T>> it;
    private Iterator<T> current = null;
    
    public CompositeIterator(Iterator<T>... paramVarArgs)
    {
      this.it = Arrays.asList(paramVarArgs).iterator();
    }
    
    public boolean hasNext()
    {
      if ((this.current != null) && (this.current.hasNext())) {
        return true;
      }
      while (this.it.hasNext())
      {
        this.current = ((Iterator)this.it.next());
        if (this.current.hasNext()) {
          return true;
        }
      }
      return false;
    }
    
    public T next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return this.current.next();
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class LazyIterator<T>
    implements Iterator<T>
  {
    Class<T> service;
    @Nullable
    ClassLoader loader;
    ServiceFinder.ServiceName[] names;
    int index;
    
    private LazyIterator(Class<T> paramClass, ClassLoader paramClassLoader)
    {
      this.service = paramClass;
      this.loader = paramClassLoader;
      this.names = null;
      this.index = 0;
    }
    
    public boolean hasNext()
    {
      if (this.names == null)
      {
        ConcurrentHashMap localConcurrentHashMap = null;
        synchronized (ServiceFinder.serviceNameCache)
        {
          localConcurrentHashMap = (ConcurrentHashMap)ServiceFinder.serviceNameCache.get(this.loader);
        }
        this.names = (localConcurrentHashMap != null ? (ServiceFinder.ServiceName[])localConcurrentHashMap.get(this.service.getName()) : null);
        if (this.names == null)
        {
          this.names = ServiceFinder.serviceClassNames(this.service, this.loader);
          if (localConcurrentHashMap == null) {
            localConcurrentHashMap = new ConcurrentHashMap();
          }
          localConcurrentHashMap.put(this.service.getName(), this.names);
          synchronized (ServiceFinder.serviceNameCache)
          {
            ServiceFinder.serviceNameCache.put(this.loader, localConcurrentHashMap);
          }
        }
      }
      return this.index < this.names.length;
    }
    
    public T next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      ServiceFinder.ServiceName localServiceName = this.names[(this.index++)];
      String str = localServiceName.className;
      URL localURL = localServiceName.config;
      try
      {
        return this.service.cast(Class.forName(str, true, this.loader).newInstance());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        ServiceFinder.fail(this.service, "Provider " + str + " is specified in " + localURL + " but not found");
      }
      catch (Exception localException)
      {
        ServiceFinder.fail(this.service, "Provider " + str + " is specified in " + localURL + "but could not be instantiated: " + localException, localException);
      }
      return null;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class ServiceName
  {
    final String className;
    final URL config;
    
    public ServiceName(String paramString, URL paramURL)
    {
      this.className = paramString;
      this.config = paramURL;
    }
  }
  
  private static class ServiceNameIterator
    implements Iterator<ServiceFinder.ServiceName>
  {
    Class service;
    @Nullable
    ClassLoader loader;
    Enumeration<URL> configs = null;
    Iterator<String> pending = null;
    Set<String> returned = new TreeSet();
    String nextName = null;
    URL currentConfig = null;
    
    private ServiceNameIterator(Class paramClass, ClassLoader paramClassLoader)
    {
      this.service = paramClass;
      this.loader = paramClassLoader;
    }
    
    public boolean hasNext()
      throws ServiceConfigurationError
    {
      if (this.nextName != null) {
        return true;
      }
      if (this.configs == null) {
        try
        {
          String str = "META-INF/services/" + this.service.getName();
          if (this.loader == null) {
            this.configs = ClassLoader.getSystemResources(str);
          } else {
            this.configs = this.loader.getResources(str);
          }
        }
        catch (IOException localIOException)
        {
          ServiceFinder.fail(this.service, ": " + localIOException);
        }
      }
      while ((this.pending == null) || (!this.pending.hasNext()))
      {
        if (!this.configs.hasMoreElements()) {
          return false;
        }
        this.currentConfig = ((URL)this.configs.nextElement());
        this.pending = ServiceFinder.parse(this.service, this.currentConfig, this.returned);
      }
      this.nextName = ((String)this.pending.next());
      return true;
    }
    
    public ServiceFinder.ServiceName next()
      throws ServiceConfigurationError
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      String str = this.nextName;
      this.nextName = null;
      return new ServiceFinder.ServiceName(str, this.currentConfig);
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}
