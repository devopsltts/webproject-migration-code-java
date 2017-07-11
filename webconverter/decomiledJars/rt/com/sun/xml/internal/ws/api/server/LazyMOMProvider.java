package com.sun.xml.internal.ws.api.server;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public enum LazyMOMProvider
{
  INSTANCE;
  
  private final Set<WSEndpointScopeChangeListener> endpointsWaitingForMOM = new HashSet();
  private final Set<DefaultScopeChangeListener> listeners = new HashSet();
  private volatile Scope scope = Scope.STANDALONE;
  
  private LazyMOMProvider() {}
  
  public void initMOMForScope(Scope paramScope)
  {
    if ((this.scope == Scope.GLASSFISH_JMX) || ((paramScope == Scope.STANDALONE) && ((this.scope == Scope.GLASSFISH_JMX) || (this.scope == Scope.GLASSFISH_NO_JMX))) || (this.scope == paramScope)) {
      return;
    }
    this.scope = paramScope;
    fireScopeChanged();
  }
  
  private void fireScopeChanged()
  {
    Iterator localIterator = this.endpointsWaitingForMOM.iterator();
    ScopeChangeListener localScopeChangeListener;
    while (localIterator.hasNext())
    {
      localScopeChangeListener = (ScopeChangeListener)localIterator.next();
      localScopeChangeListener.scopeChanged(this.scope);
    }
    localIterator = this.listeners.iterator();
    while (localIterator.hasNext())
    {
      localScopeChangeListener = (ScopeChangeListener)localIterator.next();
      localScopeChangeListener.scopeChanged(this.scope);
    }
  }
  
  public void registerListener(DefaultScopeChangeListener paramDefaultScopeChangeListener)
  {
    this.listeners.add(paramDefaultScopeChangeListener);
    if (!isProviderInDefaultScope()) {
      paramDefaultScopeChangeListener.scopeChanged(this.scope);
    }
  }
  
  private boolean isProviderInDefaultScope()
  {
    return this.scope == Scope.STANDALONE;
  }
  
  public Scope getScope()
  {
    return this.scope;
  }
  
  public void registerEndpoint(WSEndpointScopeChangeListener paramWSEndpointScopeChangeListener)
  {
    this.endpointsWaitingForMOM.add(paramWSEndpointScopeChangeListener);
    if (!isProviderInDefaultScope()) {
      paramWSEndpointScopeChangeListener.scopeChanged(this.scope);
    }
  }
  
  public void unregisterEndpoint(WSEndpointScopeChangeListener paramWSEndpointScopeChangeListener)
  {
    this.endpointsWaitingForMOM.remove(paramWSEndpointScopeChangeListener);
  }
  
  public static abstract interface DefaultScopeChangeListener
    extends LazyMOMProvider.ScopeChangeListener
  {}
  
  public static enum Scope
  {
    STANDALONE,  GLASSFISH_NO_JMX,  GLASSFISH_JMX;
    
    private Scope() {}
  }
  
  public static abstract interface ScopeChangeListener
  {
    public abstract void scopeChanged(LazyMOMProvider.Scope paramScope);
  }
  
  public static abstract interface WSEndpointScopeChangeListener
    extends LazyMOMProvider.ScopeChangeListener
  {}
}
