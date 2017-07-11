package com.sun.xml.internal.ws.api.policy;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.ws.WebServiceException;

public abstract interface PolicyResolver
{
  public abstract PolicyMap resolve(ServerContext paramServerContext)
    throws WebServiceException;
  
  public abstract PolicyMap resolve(ClientContext paramClientContext)
    throws WebServiceException;
  
  public static class ClientContext
  {
    private PolicyMap policyMap;
    private Container container;
    
    public ClientContext(@Nullable PolicyMap paramPolicyMap, Container paramContainer)
    {
      this.policyMap = paramPolicyMap;
      this.container = paramContainer;
    }
    
    @Nullable
    public PolicyMap getPolicyMap()
    {
      return this.policyMap;
    }
    
    public Container getContainer()
    {
      return this.container;
    }
  }
  
  public static class ServerContext
  {
    private final PolicyMap policyMap;
    private final Class endpointClass;
    private final Container container;
    private final boolean hasWsdl;
    private final Collection<PolicyMapMutator> mutators;
    
    public ServerContext(@Nullable PolicyMap paramPolicyMap, Container paramContainer, Class paramClass, PolicyMapMutator... paramVarArgs)
    {
      this.policyMap = paramPolicyMap;
      this.endpointClass = paramClass;
      this.container = paramContainer;
      this.hasWsdl = true;
      this.mutators = Arrays.asList(paramVarArgs);
    }
    
    public ServerContext(@Nullable PolicyMap paramPolicyMap, Container paramContainer, Class paramClass, boolean paramBoolean, PolicyMapMutator... paramVarArgs)
    {
      this.policyMap = paramPolicyMap;
      this.endpointClass = paramClass;
      this.container = paramContainer;
      this.hasWsdl = paramBoolean;
      this.mutators = Arrays.asList(paramVarArgs);
    }
    
    @Nullable
    public PolicyMap getPolicyMap()
    {
      return this.policyMap;
    }
    
    @Nullable
    public Class getEndpointClass()
    {
      return this.endpointClass;
    }
    
    public Container getContainer()
    {
      return this.container;
    }
    
    public boolean hasWsdl()
    {
      return this.hasWsdl;
    }
    
    public Collection<PolicyMapMutator> getMutators()
    {
      return this.mutators;
    }
  }
}
