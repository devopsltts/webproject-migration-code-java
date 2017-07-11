package javax.naming.spi;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import com.sun.naming.internal.VersionHelper;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

public class NamingManager
{
  static final VersionHelper helper = ;
  private static ObjectFactoryBuilder object_factory_builder = null;
  private static final String defaultPkgPrefix = "com.sun.jndi.url";
  private static InitialContextFactoryBuilder initctx_factory_builder = null;
  public static final String CPE = "java.naming.spi.CannotProceedException";
  
  NamingManager() {}
  
  public static synchronized void setObjectFactoryBuilder(ObjectFactoryBuilder paramObjectFactoryBuilder)
    throws NamingException
  {
    if (object_factory_builder != null) {
      throw new IllegalStateException("ObjectFactoryBuilder already set");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    object_factory_builder = paramObjectFactoryBuilder;
  }
  
  static synchronized ObjectFactoryBuilder getObjectFactoryBuilder()
  {
    return object_factory_builder;
  }
  
  static ObjectFactory getObjectFactoryFromReference(Reference paramReference, String paramString)
    throws IllegalAccessException, InstantiationException, MalformedURLException
  {
    Class localClass = null;
    try
    {
      localClass = helper.loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException1) {}
    String str;
    if ((localClass == null) && ((str = paramReference.getFactoryClassLocation()) != null)) {
      try
      {
        localClass = helper.loadClass(paramString, str);
      }
      catch (ClassNotFoundException localClassNotFoundException2) {}
    }
    return localClass != null ? (ObjectFactory)localClass.newInstance() : null;
  }
  
  private static Object createObjectFromFactories(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws Exception
  {
    FactoryEnumeration localFactoryEnumeration = ResourceManager.getFactories("java.naming.factory.object", paramHashtable, paramContext);
    if (localFactoryEnumeration == null) {
      return null;
    }
    ObjectFactory localObjectFactory;
    for (Object localObject = null; (localObject == null) && (localFactoryEnumeration.hasMore()); localObject = localObjectFactory.getObjectInstance(paramObject, paramName, paramContext, paramHashtable)) {
      localObjectFactory = (ObjectFactory)localFactoryEnumeration.next();
    }
    return localObject;
  }
  
  private static String getURLScheme(String paramString)
  {
    int i = paramString.indexOf(':');
    int j = paramString.indexOf('/');
    if ((i > 0) && ((j == -1) || (i < j))) {
      return paramString.substring(0, i);
    }
    return null;
  }
  
  public static Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws Exception
  {
    ObjectFactoryBuilder localObjectFactoryBuilder = getObjectFactoryBuilder();
    ObjectFactory localObjectFactory;
    if (localObjectFactoryBuilder != null)
    {
      localObjectFactory = localObjectFactoryBuilder.createObjectFactory(paramObject, paramHashtable);
      return localObjectFactory.getObjectInstance(paramObject, paramName, paramContext, paramHashtable);
    }
    Reference localReference = null;
    if ((paramObject instanceof Reference)) {
      localReference = (Reference)paramObject;
    } else if ((paramObject instanceof Referenceable)) {
      localReference = ((Referenceable)paramObject).getReference();
    }
    if (localReference != null)
    {
      String str = localReference.getFactoryClassName();
      if (str != null)
      {
        localObjectFactory = getObjectFactoryFromReference(localReference, str);
        if (localObjectFactory != null) {
          return localObjectFactory.getObjectInstance(localReference, paramName, paramContext, paramHashtable);
        }
        return paramObject;
      }
      localObject = processURLAddrs(localReference, paramName, paramContext, paramHashtable);
      if (localObject != null) {
        return localObject;
      }
    }
    Object localObject = createObjectFromFactories(paramObject, paramName, paramContext, paramHashtable);
    return localObject != null ? localObject : paramObject;
  }
  
  static Object processURLAddrs(Reference paramReference, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    for (int i = 0; i < paramReference.size(); i++)
    {
      RefAddr localRefAddr = paramReference.get(i);
      if (((localRefAddr instanceof StringRefAddr)) && (localRefAddr.getType().equalsIgnoreCase("URL")))
      {
        String str = (String)localRefAddr.getContent();
        Object localObject = processURL(str, paramName, paramContext, paramHashtable);
        if (localObject != null) {
          return localObject;
        }
      }
    }
    return null;
  }
  
  private static Object processURL(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    Object localObject2;
    Object localObject1;
    if ((paramObject instanceof String))
    {
      localObject2 = (String)paramObject;
      String str1 = getURLScheme((String)localObject2);
      if (str1 != null)
      {
        localObject1 = getURLObject(str1, paramObject, paramName, paramContext, paramHashtable);
        if (localObject1 != null) {
          return localObject1;
        }
      }
    }
    if ((paramObject instanceof String[]))
    {
      localObject2 = (String[])paramObject;
      for (int i = 0; i < localObject2.length; i++)
      {
        String str2 = getURLScheme(localObject2[i]);
        if (str2 != null)
        {
          localObject1 = getURLObject(str2, paramObject, paramName, paramContext, paramHashtable);
          if (localObject1 != null) {
            return localObject1;
          }
        }
      }
    }
    return null;
  }
  
  static Context getContext(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if ((paramObject instanceof Context)) {
      return (Context)paramObject;
    }
    Object localObject;
    try
    {
      localObject = getObjectInstance(paramObject, paramName, paramContext, paramHashtable);
    }
    catch (NamingException localNamingException1)
    {
      throw localNamingException1;
    }
    catch (Exception localException)
    {
      NamingException localNamingException2 = new NamingException();
      localNamingException2.setRootCause(localException);
      throw localNamingException2;
    }
    return (localObject instanceof Context) ? (Context)localObject : null;
  }
  
  static Resolver getResolver(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if ((paramObject instanceof Resolver)) {
      return (Resolver)paramObject;
    }
    Object localObject;
    try
    {
      localObject = getObjectInstance(paramObject, paramName, paramContext, paramHashtable);
    }
    catch (NamingException localNamingException1)
    {
      throw localNamingException1;
    }
    catch (Exception localException)
    {
      NamingException localNamingException2 = new NamingException();
      localNamingException2.setRootCause(localException);
      throw localNamingException2;
    }
    return (localObject instanceof Resolver) ? (Resolver)localObject : null;
  }
  
  public static Context getURLContext(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    Object localObject = getURLObject(paramString, null, null, null, paramHashtable);
    if ((localObject instanceof Context)) {
      return (Context)localObject;
    }
    return null;
  }
  
  private static Object getURLObject(String paramString, Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    ObjectFactory localObjectFactory = (ObjectFactory)ResourceManager.getFactory("java.naming.factory.url.pkgs", paramHashtable, paramContext, "." + paramString + "." + paramString + "URLContextFactory", "com.sun.jndi.url");
    if (localObjectFactory == null) {
      return null;
    }
    try
    {
      return localObjectFactory.getObjectInstance(paramObject, paramName, paramContext, paramHashtable);
    }
    catch (NamingException localNamingException1)
    {
      throw localNamingException1;
    }
    catch (Exception localException)
    {
      NamingException localNamingException2 = new NamingException();
      localNamingException2.setRootCause(localException);
      throw localNamingException2;
    }
  }
  
  private static synchronized InitialContextFactoryBuilder getInitialContextFactoryBuilder()
  {
    return initctx_factory_builder;
  }
  
  public static Context getInitialContext(Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    InitialContextFactoryBuilder localInitialContextFactoryBuilder = getInitialContextFactoryBuilder();
    InitialContextFactory localInitialContextFactory;
    if (localInitialContextFactoryBuilder == null)
    {
      String str = paramHashtable != null ? (String)paramHashtable.get("java.naming.factory.initial") : null;
      if (str == null)
      {
        NoInitialContextException localNoInitialContextException1 = new NoInitialContextException("Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial");
        throw localNoInitialContextException1;
      }
      try
      {
        localInitialContextFactory = (InitialContextFactory)helper.loadClass(str).newInstance();
      }
      catch (Exception localException)
      {
        NoInitialContextException localNoInitialContextException2 = new NoInitialContextException("Cannot instantiate class: " + str);
        localNoInitialContextException2.setRootCause(localException);
        throw localNoInitialContextException2;
      }
    }
    else
    {
      localInitialContextFactory = localInitialContextFactoryBuilder.createInitialContextFactory(paramHashtable);
    }
    return localInitialContextFactory.getInitialContext(paramHashtable);
  }
  
  public static synchronized void setInitialContextFactoryBuilder(InitialContextFactoryBuilder paramInitialContextFactoryBuilder)
    throws NamingException
  {
    if (initctx_factory_builder != null) {
      throw new IllegalStateException("InitialContextFactoryBuilder already set");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    initctx_factory_builder = paramInitialContextFactoryBuilder;
  }
  
  public static boolean hasInitialContextFactoryBuilder()
  {
    return getInitialContextFactoryBuilder() != null;
  }
  
  public static Context getContinuationContext(CannotProceedException paramCannotProceedException)
    throws NamingException
  {
    Hashtable localHashtable = paramCannotProceedException.getEnvironment();
    if (localHashtable == null) {
      localHashtable = new Hashtable(7);
    } else {
      localHashtable = (Hashtable)localHashtable.clone();
    }
    localHashtable.put("java.naming.spi.CannotProceedException", paramCannotProceedException);
    ContinuationContext localContinuationContext = new ContinuationContext(paramCannotProceedException, localHashtable);
    return localContinuationContext.getTargetContext();
  }
  
  public static Object getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    FactoryEnumeration localFactoryEnumeration = ResourceManager.getFactories("java.naming.factory.state", paramHashtable, paramContext);
    if (localFactoryEnumeration == null) {
      return paramObject;
    }
    StateFactory localStateFactory;
    for (Object localObject = null; (localObject == null) && (localFactoryEnumeration.hasMore()); localObject = localStateFactory.getStateToBind(paramObject, paramName, paramContext, paramHashtable)) {
      localStateFactory = (StateFactory)localFactoryEnumeration.next();
    }
    return localObject != null ? localObject : paramObject;
  }
}
