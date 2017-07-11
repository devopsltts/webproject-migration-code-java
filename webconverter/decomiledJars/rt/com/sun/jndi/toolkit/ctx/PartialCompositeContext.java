package com.sun.jndi.toolkit.ctx;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ResolveResult;
import javax.naming.spi.Resolver;

public abstract class PartialCompositeContext
  implements Context, Resolver
{
  protected static final int _PARTIAL = 1;
  protected static final int _COMPONENT = 2;
  protected static final int _ATOMIC = 3;
  protected int _contextType = 1;
  static final CompositeName _EMPTY_NAME = new CompositeName();
  static CompositeName _NNS_NAME;
  
  protected PartialCompositeContext() {}
  
  protected abstract ResolveResult p_resolveToClass(Name paramName, Class<?> paramClass, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract Object p_lookup(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract Object p_lookupLink(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NamingEnumeration<NameClassPair> p_list(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NamingEnumeration<Binding> p_listBindings(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void p_bind(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void p_rebind(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void p_unbind(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void p_destroySubcontext(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract Context p_createSubcontext(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void p_rename(Name paramName1, Name paramName2, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NameParser p_getNameParser(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected Hashtable<?, ?> p_getEnvironment()
    throws NamingException
  {
    return getEnvironment();
  }
  
  public ResolveResult resolveToClass(String paramString, Class<? extends Context> paramClass)
    throws NamingException
  {
    return resolveToClass(new CompositeName(paramString), paramClass);
  }
  
  public ResolveResult resolveToClass(Name paramName, Class<? extends Context> paramClass)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    ResolveResult localResolveResult;
    try
    {
      for (localResolveResult = localPartialCompositeContext.p_resolveToClass(localName, paramClass, localContinuation); localContinuation.isContinue(); localResolveResult = localPartialCompositeContext.p_resolveToClass(localName, paramClass, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = NamingManager.getContinuationContext(localCannotProceedException);
      if (!(localContext instanceof Resolver)) {
        throw localCannotProceedException;
      }
      localResolveResult = ((Resolver)localContext).resolveToClass(localCannotProceedException.getRemainingName(), paramClass);
    }
    return localResolveResult;
  }
  
  public Object lookup(String paramString)
    throws NamingException
  {
    return lookup(new CompositeName(paramString));
  }
  
  public Object lookup(Name paramName)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    Object localObject;
    try
    {
      for (localObject = localPartialCompositeContext.p_lookup(localName, localContinuation); localContinuation.isContinue(); localObject = localPartialCompositeContext.p_lookup(localName, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = NamingManager.getContinuationContext(localCannotProceedException);
      localObject = localContext.lookup(localCannotProceedException.getRemainingName());
    }
    return localObject;
  }
  
  public void bind(String paramString, Object paramObject)
    throws NamingException
  {
    bind(new CompositeName(paramString), paramObject);
  }
  
  public void bind(Name paramName, Object paramObject)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Name localName = paramName;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    try
    {
      localPartialCompositeContext.p_bind(localName, paramObject, localContinuation);
      while (localContinuation.isContinue())
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
        localPartialCompositeContext.p_bind(localName, paramObject, localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = NamingManager.getContinuationContext(localCannotProceedException);
      localContext.bind(localCannotProceedException.getRemainingName(), paramObject);
    }
  }
  
  public void rebind(String paramString, Object paramObject)
    throws NamingException
  {
    rebind(new CompositeName(paramString), paramObject);
  }
  
  public void rebind(Name paramName, Object paramObject)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Name localName = paramName;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    try
    {
      localPartialCompositeContext.p_rebind(localName, paramObject, localContinuation);
      while (localContinuation.isContinue())
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
        localPartialCompositeContext.p_rebind(localName, paramObject, localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = NamingManager.getContinuationContext(localCannotProceedException);
      localContext.rebind(localCannotProceedException.getRemainingName(), paramObject);
    }
  }
  
  public void unbind(String paramString)
    throws NamingException
  {
    unbind(new CompositeName(paramString));
  }
  
  public void unbind(Name paramName)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Name localName = paramName;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    try
    {
      localPartialCompositeContext.p_unbind(localName, localContinuation);
      while (localContinuation.isContinue())
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
        localPartialCompositeContext.p_unbind(localName, localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = NamingManager.getContinuationContext(localCannotProceedException);
      localContext.unbind(localCannotProceedException.getRemainingName());
    }
  }
  
  public void rename(String paramString1, String paramString2)
    throws NamingException
  {
    rename(new CompositeName(paramString1), new CompositeName(paramString2));
  }
  
  public void rename(Name paramName1, Name paramName2)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Name localName = paramName1;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName1, localHashtable);
    try
    {
      localPartialCompositeContext.p_rename(localName, paramName2, localContinuation);
      while (localContinuation.isContinue())
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
        localPartialCompositeContext.p_rename(localName, paramName2, localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = NamingManager.getContinuationContext(localCannotProceedException);
      if (localCannotProceedException.getRemainingNewName() != null) {
        paramName2 = localCannotProceedException.getRemainingNewName();
      }
      localContext.rename(localCannotProceedException.getRemainingName(), paramName2);
    }
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString)
    throws NamingException
  {
    return list(new CompositeName(paramString));
  }
  
  public NamingEnumeration<NameClassPair> list(Name paramName)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Name localName = paramName;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    NamingEnumeration localNamingEnumeration;
    try
    {
      for (localNamingEnumeration = localPartialCompositeContext.p_list(localName, localContinuation); localContinuation.isContinue(); localNamingEnumeration = localPartialCompositeContext.p_list(localName, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = NamingManager.getContinuationContext(localCannotProceedException);
      localNamingEnumeration = localContext.list(localCannotProceedException.getRemainingName());
    }
    return localNamingEnumeration;
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString)
    throws NamingException
  {
    return listBindings(new CompositeName(paramString));
  }
  
  public NamingEnumeration<Binding> listBindings(Name paramName)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Name localName = paramName;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    NamingEnumeration localNamingEnumeration;
    try
    {
      for (localNamingEnumeration = localPartialCompositeContext.p_listBindings(localName, localContinuation); localContinuation.isContinue(); localNamingEnumeration = localPartialCompositeContext.p_listBindings(localName, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = NamingManager.getContinuationContext(localCannotProceedException);
      localNamingEnumeration = localContext.listBindings(localCannotProceedException.getRemainingName());
    }
    return localNamingEnumeration;
  }
  
  public void destroySubcontext(String paramString)
    throws NamingException
  {
    destroySubcontext(new CompositeName(paramString));
  }
  
  public void destroySubcontext(Name paramName)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Name localName = paramName;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    try
    {
      localPartialCompositeContext.p_destroySubcontext(localName, localContinuation);
      while (localContinuation.isContinue())
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
        localPartialCompositeContext.p_destroySubcontext(localName, localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = NamingManager.getContinuationContext(localCannotProceedException);
      localContext.destroySubcontext(localCannotProceedException.getRemainingName());
    }
  }
  
  public Context createSubcontext(String paramString)
    throws NamingException
  {
    return createSubcontext(new CompositeName(paramString));
  }
  
  public Context createSubcontext(Name paramName)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Name localName = paramName;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Context localContext1;
    try
    {
      for (localContext1 = localPartialCompositeContext.p_createSubcontext(localName, localContinuation); localContinuation.isContinue(); localContext1 = localPartialCompositeContext.p_createSubcontext(localName, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext2 = NamingManager.getContinuationContext(localCannotProceedException);
      localContext1 = localContext2.createSubcontext(localCannotProceedException.getRemainingName());
    }
    return localContext1;
  }
  
  public Object lookupLink(String paramString)
    throws NamingException
  {
    return lookupLink(new CompositeName(paramString));
  }
  
  public Object lookupLink(Name paramName)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    Object localObject;
    try
    {
      for (localObject = localPartialCompositeContext.p_lookupLink(localName, localContinuation); localContinuation.isContinue(); localObject = localPartialCompositeContext.p_lookupLink(localName, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = NamingManager.getContinuationContext(localCannotProceedException);
      localObject = localContext.lookupLink(localCannotProceedException.getRemainingName());
    }
    return localObject;
  }
  
  public NameParser getNameParser(String paramString)
    throws NamingException
  {
    return getNameParser(new CompositeName(paramString));
  }
  
  public NameParser getNameParser(Name paramName)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = this;
    Name localName = paramName;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    NameParser localNameParser;
    try
    {
      for (localNameParser = localPartialCompositeContext.p_getNameParser(localName, localContinuation); localContinuation.isContinue(); localNameParser = localPartialCompositeContext.p_getNameParser(localName, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeContext = getPCContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = NamingManager.getContinuationContext(localCannotProceedException);
      localNameParser = localContext.getNameParser(localCannotProceedException.getRemainingName());
    }
    return localNameParser;
  }
  
  public String composeName(String paramString1, String paramString2)
    throws NamingException
  {
    Name localName = composeName(new CompositeName(paramString1), new CompositeName(paramString2));
    return localName.toString();
  }
  
  public Name composeName(Name paramName1, Name paramName2)
    throws NamingException
  {
    Name localName = (Name)paramName2.clone();
    if (paramName1 == null) {
      return localName;
    }
    localName.addAll(paramName1);
    String str = (String)p_getEnvironment().get("java.naming.provider.compose.elideEmpty");
    if ((str == null) || (!str.equalsIgnoreCase("true"))) {
      return localName;
    }
    int i = paramName2.size();
    if ((!allEmpty(paramName2)) && (!allEmpty(paramName1))) {
      if (localName.get(i - 1).equals("")) {
        localName.remove(i - 1);
      } else if (localName.get(i).equals("")) {
        localName.remove(i);
      }
    }
    return localName;
  }
  
  protected static boolean allEmpty(Name paramName)
  {
    Enumeration localEnumeration = paramName.getAll();
    while (localEnumeration.hasMoreElements()) {
      if (!((String)localEnumeration.nextElement()).isEmpty()) {
        return false;
      }
    }
    return true;
  }
  
  protected static PartialCompositeContext getPCContext(Continuation paramContinuation)
    throws NamingException
  {
    Object localObject1 = paramContinuation.getResolvedObj();
    Object localObject2 = null;
    if ((localObject1 instanceof PartialCompositeContext)) {
      return (PartialCompositeContext)localObject1;
    }
    throw paramContinuation.fillInException(new CannotProceedException());
  }
  
  static
  {
    try
    {
      _NNS_NAME = new CompositeName("/");
    }
    catch (InvalidNameException localInvalidNameException) {}
  }
}
