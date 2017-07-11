package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public final class PolicyMap
  implements Iterable<Policy>
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMap.class);
  private static final PolicyMapKeyHandler serviceKeyHandler = new PolicyMapKeyHandler()
  {
    public boolean areEqual(PolicyMapKey paramAnonymousPolicyMapKey1, PolicyMapKey paramAnonymousPolicyMapKey2)
    {
      return paramAnonymousPolicyMapKey1.getService().equals(paramAnonymousPolicyMapKey2.getService());
    }
    
    public int generateHashCode(PolicyMapKey paramAnonymousPolicyMapKey)
    {
      int i = 17;
      i = 37 * i + paramAnonymousPolicyMapKey.getService().hashCode();
      return i;
    }
  };
  private static final PolicyMapKeyHandler endpointKeyHandler = new PolicyMapKeyHandler()
  {
    public boolean areEqual(PolicyMapKey paramAnonymousPolicyMapKey1, PolicyMapKey paramAnonymousPolicyMapKey2)
    {
      boolean bool = true;
      bool = (bool) && (paramAnonymousPolicyMapKey1.getService().equals(paramAnonymousPolicyMapKey2.getService()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getPort() == null ? paramAnonymousPolicyMapKey2.getPort() == null : paramAnonymousPolicyMapKey1.getPort().equals(paramAnonymousPolicyMapKey2.getPort()));
      return bool;
    }
    
    public int generateHashCode(PolicyMapKey paramAnonymousPolicyMapKey)
    {
      int i = 17;
      i = 37 * i + paramAnonymousPolicyMapKey.getService().hashCode();
      i = 37 * i + (paramAnonymousPolicyMapKey.getPort() == null ? 0 : paramAnonymousPolicyMapKey.getPort().hashCode());
      return i;
    }
  };
  private static final PolicyMapKeyHandler operationAndInputOutputMessageKeyHandler = new PolicyMapKeyHandler()
  {
    public boolean areEqual(PolicyMapKey paramAnonymousPolicyMapKey1, PolicyMapKey paramAnonymousPolicyMapKey2)
    {
      boolean bool = true;
      bool = (bool) && (paramAnonymousPolicyMapKey1.getService().equals(paramAnonymousPolicyMapKey2.getService()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getPort() == null ? paramAnonymousPolicyMapKey2.getPort() == null : paramAnonymousPolicyMapKey1.getPort().equals(paramAnonymousPolicyMapKey2.getPort()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getOperation() == null ? paramAnonymousPolicyMapKey2.getOperation() == null : paramAnonymousPolicyMapKey1.getOperation().equals(paramAnonymousPolicyMapKey2.getOperation()));
      return bool;
    }
    
    public int generateHashCode(PolicyMapKey paramAnonymousPolicyMapKey)
    {
      int i = 17;
      i = 37 * i + paramAnonymousPolicyMapKey.getService().hashCode();
      i = 37 * i + (paramAnonymousPolicyMapKey.getPort() == null ? 0 : paramAnonymousPolicyMapKey.getPort().hashCode());
      i = 37 * i + (paramAnonymousPolicyMapKey.getOperation() == null ? 0 : paramAnonymousPolicyMapKey.getOperation().hashCode());
      return i;
    }
  };
  private static final PolicyMapKeyHandler faultMessageHandler = new PolicyMapKeyHandler()
  {
    public boolean areEqual(PolicyMapKey paramAnonymousPolicyMapKey1, PolicyMapKey paramAnonymousPolicyMapKey2)
    {
      boolean bool = true;
      bool = (bool) && (paramAnonymousPolicyMapKey1.getService().equals(paramAnonymousPolicyMapKey2.getService()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getPort() == null ? paramAnonymousPolicyMapKey2.getPort() == null : paramAnonymousPolicyMapKey1.getPort().equals(paramAnonymousPolicyMapKey2.getPort()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getOperation() == null ? paramAnonymousPolicyMapKey2.getOperation() == null : paramAnonymousPolicyMapKey1.getOperation().equals(paramAnonymousPolicyMapKey2.getOperation()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getFaultMessage() == null ? paramAnonymousPolicyMapKey2.getFaultMessage() == null : paramAnonymousPolicyMapKey1.getFaultMessage().equals(paramAnonymousPolicyMapKey2.getFaultMessage()));
      return bool;
    }
    
    public int generateHashCode(PolicyMapKey paramAnonymousPolicyMapKey)
    {
      int i = 17;
      i = 37 * i + paramAnonymousPolicyMapKey.getService().hashCode();
      i = 37 * i + (paramAnonymousPolicyMapKey.getPort() == null ? 0 : paramAnonymousPolicyMapKey.getPort().hashCode());
      i = 37 * i + (paramAnonymousPolicyMapKey.getOperation() == null ? 0 : paramAnonymousPolicyMapKey.getOperation().hashCode());
      i = 37 * i + (paramAnonymousPolicyMapKey.getFaultMessage() == null ? 0 : paramAnonymousPolicyMapKey.getFaultMessage().hashCode());
      return i;
    }
  };
  private static final PolicyMerger merger = PolicyMerger.getMerger();
  private final ScopeMap serviceMap = new ScopeMap(merger, serviceKeyHandler);
  private final ScopeMap endpointMap = new ScopeMap(merger, endpointKeyHandler);
  private final ScopeMap operationMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
  private final ScopeMap inputMessageMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
  private final ScopeMap outputMessageMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
  private final ScopeMap faultMessageMap = new ScopeMap(merger, faultMessageHandler);
  
  private PolicyMap() {}
  
  public static PolicyMap createPolicyMap(Collection<? extends PolicyMapMutator> paramCollection)
  {
    PolicyMap localPolicyMap = new PolicyMap();
    if ((paramCollection != null) && (!paramCollection.isEmpty()))
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        PolicyMapMutator localPolicyMapMutator = (PolicyMapMutator)localIterator.next();
        localPolicyMapMutator.connect(localPolicyMap);
      }
    }
    return localPolicyMap;
  }
  
  public Policy getServiceEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return this.serviceMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Policy getEndpointEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return this.endpointMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Policy getOperationEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return this.operationMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Policy getInputMessageEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return this.inputMessageMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Policy getOutputMessageEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return this.outputMessageMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Policy getFaultMessageEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return this.faultMessageMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Collection<PolicyMapKey> getAllServiceScopeKeys()
  {
    return this.serviceMap.getAllKeys();
  }
  
  public Collection<PolicyMapKey> getAllEndpointScopeKeys()
  {
    return this.endpointMap.getAllKeys();
  }
  
  public Collection<PolicyMapKey> getAllOperationScopeKeys()
  {
    return this.operationMap.getAllKeys();
  }
  
  public Collection<PolicyMapKey> getAllInputMessageScopeKeys()
  {
    return this.inputMessageMap.getAllKeys();
  }
  
  public Collection<PolicyMapKey> getAllOutputMessageScopeKeys()
  {
    return this.outputMessageMap.getAllKeys();
  }
  
  public Collection<PolicyMapKey> getAllFaultMessageScopeKeys()
  {
    return this.faultMessageMap.getAllKeys();
  }
  
  void putSubject(ScopeType paramScopeType, PolicyMapKey paramPolicyMapKey, PolicySubject paramPolicySubject)
  {
    switch (6.$SwitchMap$com$sun$xml$internal$ws$policy$PolicyMap$ScopeType[paramScopeType.ordinal()])
    {
    case 1: 
      this.serviceMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    case 2: 
      this.endpointMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    case 3: 
      this.operationMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    case 4: 
      this.inputMessageMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    case 5: 
      this.outputMessageMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    case 6: 
      this.faultMessageMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    default: 
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE(paramScopeType))));
    }
  }
  
  void setNewEffectivePolicyForScope(ScopeType paramScopeType, PolicyMapKey paramPolicyMapKey, Policy paramPolicy)
    throws IllegalArgumentException
  {
    if ((paramScopeType == null) || (paramPolicyMapKey == null) || (paramPolicy == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL())));
    }
    switch (6.$SwitchMap$com$sun$xml$internal$ws$policy$PolicyMap$ScopeType[paramScopeType.ordinal()])
    {
    case 1: 
      this.serviceMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    case 2: 
      this.endpointMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    case 3: 
      this.operationMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    case 4: 
      this.inputMessageMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    case 5: 
      this.outputMessageMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    case 6: 
      this.faultMessageMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    default: 
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE(paramScopeType))));
    }
  }
  
  public Collection<PolicySubject> getPolicySubjects()
  {
    LinkedList localLinkedList = new LinkedList();
    addSubjects(localLinkedList, this.serviceMap);
    addSubjects(localLinkedList, this.endpointMap);
    addSubjects(localLinkedList, this.operationMap);
    addSubjects(localLinkedList, this.inputMessageMap);
    addSubjects(localLinkedList, this.outputMessageMap);
    addSubjects(localLinkedList, this.faultMessageMap);
    return localLinkedList;
  }
  
  public boolean isInputMessageSubject(PolicySubject paramPolicySubject)
  {
    Iterator localIterator = this.inputMessageMap.getStoredScopes().iterator();
    while (localIterator.hasNext())
    {
      PolicyScope localPolicyScope = (PolicyScope)localIterator.next();
      if (localPolicyScope.getPolicySubjects().contains(paramPolicySubject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isOutputMessageSubject(PolicySubject paramPolicySubject)
  {
    Iterator localIterator = this.outputMessageMap.getStoredScopes().iterator();
    while (localIterator.hasNext())
    {
      PolicyScope localPolicyScope = (PolicyScope)localIterator.next();
      if (localPolicyScope.getPolicySubjects().contains(paramPolicySubject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isFaultMessageSubject(PolicySubject paramPolicySubject)
  {
    Iterator localIterator = this.faultMessageMap.getStoredScopes().iterator();
    while (localIterator.hasNext())
    {
      PolicyScope localPolicyScope = (PolicyScope)localIterator.next();
      if (localPolicyScope.getPolicySubjects().contains(paramPolicySubject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isEmpty()
  {
    return (this.serviceMap.isEmpty()) && (this.endpointMap.isEmpty()) && (this.operationMap.isEmpty()) && (this.inputMessageMap.isEmpty()) && (this.outputMessageMap.isEmpty()) && (this.faultMessageMap.isEmpty());
  }
  
  private void addSubjects(Collection<PolicySubject> paramCollection, ScopeMap paramScopeMap)
  {
    Iterator localIterator = paramScopeMap.getStoredScopes().iterator();
    while (localIterator.hasNext())
    {
      PolicyScope localPolicyScope = (PolicyScope)localIterator.next();
      Collection localCollection = localPolicyScope.getPolicySubjects();
      paramCollection.addAll(localCollection);
    }
  }
  
  public static PolicyMapKey createWsdlServiceScopeKey(QName paramQName)
    throws IllegalArgumentException
  {
    if (paramQName == null) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0031_SERVICE_PARAM_MUST_NOT_BE_NULL())));
    }
    return new PolicyMapKey(paramQName, null, null, serviceKeyHandler);
  }
  
  public static PolicyMapKey createWsdlEndpointScopeKey(QName paramQName1, QName paramQName2)
    throws IllegalArgumentException
  {
    if ((paramQName1 == null) || (paramQName2 == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0033_SERVICE_AND_PORT_PARAM_MUST_NOT_BE_NULL(paramQName1, paramQName2))));
    }
    return new PolicyMapKey(paramQName1, paramQName2, null, endpointKeyHandler);
  }
  
  public static PolicyMapKey createWsdlOperationScopeKey(QName paramQName1, QName paramQName2, QName paramQName3)
    throws IllegalArgumentException
  {
    return createOperationOrInputOutputMessageKey(paramQName1, paramQName2, paramQName3);
  }
  
  public static PolicyMapKey createWsdlMessageScopeKey(QName paramQName1, QName paramQName2, QName paramQName3)
    throws IllegalArgumentException
  {
    return createOperationOrInputOutputMessageKey(paramQName1, paramQName2, paramQName3);
  }
  
  public static PolicyMapKey createWsdlFaultMessageScopeKey(QName paramQName1, QName paramQName2, QName paramQName3, QName paramQName4)
    throws IllegalArgumentException
  {
    if ((paramQName1 == null) || (paramQName2 == null) || (paramQName3 == null) || (paramQName4 == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0030_SERVICE_PORT_OPERATION_FAULT_MSG_PARAM_MUST_NOT_BE_NULL(paramQName1, paramQName2, paramQName3, paramQName4))));
    }
    return new PolicyMapKey(paramQName1, paramQName2, paramQName3, paramQName4, faultMessageHandler);
  }
  
  private static PolicyMapKey createOperationOrInputOutputMessageKey(QName paramQName1, QName paramQName2, QName paramQName3)
  {
    if ((paramQName1 == null) || (paramQName2 == null) || (paramQName3 == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0029_SERVICE_PORT_OPERATION_PARAM_MUST_NOT_BE_NULL(paramQName1, paramQName2, paramQName3))));
    }
    return new PolicyMapKey(paramQName1, paramQName2, paramQName3, operationAndInputOutputMessageKeyHandler);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (null != this.serviceMap) {
      localStringBuffer.append("\nServiceMap=").append(this.serviceMap);
    }
    if (null != this.endpointMap) {
      localStringBuffer.append("\nEndpointMap=").append(this.endpointMap);
    }
    if (null != this.operationMap) {
      localStringBuffer.append("\nOperationMap=").append(this.operationMap);
    }
    if (null != this.inputMessageMap) {
      localStringBuffer.append("\nInputMessageMap=").append(this.inputMessageMap);
    }
    if (null != this.outputMessageMap) {
      localStringBuffer.append("\nOutputMessageMap=").append(this.outputMessageMap);
    }
    if (null != this.faultMessageMap) {
      localStringBuffer.append("\nFaultMessageMap=").append(this.faultMessageMap);
    }
    return localStringBuffer.toString();
  }
  
  public Iterator<Policy> iterator()
  {
    new Iterator()
    {
      private final Iterator<Iterator<Policy>> mainIterator;
      private Iterator<Policy> currentScopeIterator;
      
      public boolean hasNext()
      {
        while (!this.currentScopeIterator.hasNext()) {
          if (this.mainIterator.hasNext()) {
            this.currentScopeIterator = ((Iterator)this.mainIterator.next());
          } else {
            return false;
          }
        }
        return true;
      }
      
      public Policy next()
      {
        if (hasNext()) {
          return (Policy)this.currentScopeIterator.next();
        }
        throw ((NoSuchElementException)PolicyMap.LOGGER.logSevereException(new NoSuchElementException(LocalizationMessages.WSP_0054_NO_MORE_ELEMS_IN_POLICY_MAP())));
      }
      
      public void remove()
      {
        throw ((UnsupportedOperationException)PolicyMap.LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED())));
      }
    };
  }
  
  private static final class ScopeMap
    implements Iterable<Policy>
  {
    private final Map<PolicyMapKey, PolicyScope> internalMap = new HashMap();
    private final PolicyMapKeyHandler scopeKeyHandler;
    private final PolicyMerger merger;
    
    ScopeMap(PolicyMerger paramPolicyMerger, PolicyMapKeyHandler paramPolicyMapKeyHandler)
    {
      this.merger = paramPolicyMerger;
      this.scopeKeyHandler = paramPolicyMapKeyHandler;
    }
    
    Policy getEffectivePolicy(PolicyMapKey paramPolicyMapKey)
      throws PolicyException
    {
      PolicyScope localPolicyScope = (PolicyScope)this.internalMap.get(createLocalCopy(paramPolicyMapKey));
      return localPolicyScope == null ? null : localPolicyScope.getEffectivePolicy(this.merger);
    }
    
    void putSubject(PolicyMapKey paramPolicyMapKey, PolicySubject paramPolicySubject)
    {
      PolicyMapKey localPolicyMapKey = createLocalCopy(paramPolicyMapKey);
      PolicyScope localPolicyScope = (PolicyScope)this.internalMap.get(localPolicyMapKey);
      if (localPolicyScope == null)
      {
        LinkedList localLinkedList = new LinkedList();
        localLinkedList.add(paramPolicySubject);
        this.internalMap.put(localPolicyMapKey, new PolicyScope(localLinkedList));
      }
      else
      {
        localPolicyScope.attach(paramPolicySubject);
      }
    }
    
    void setNewEffectivePolicy(PolicyMapKey paramPolicyMapKey, Policy paramPolicy)
    {
      PolicySubject localPolicySubject = new PolicySubject(paramPolicyMapKey, paramPolicy);
      PolicyMapKey localPolicyMapKey = createLocalCopy(paramPolicyMapKey);
      PolicyScope localPolicyScope = (PolicyScope)this.internalMap.get(localPolicyMapKey);
      if (localPolicyScope == null)
      {
        LinkedList localLinkedList = new LinkedList();
        localLinkedList.add(localPolicySubject);
        this.internalMap.put(localPolicyMapKey, new PolicyScope(localLinkedList));
      }
      else
      {
        localPolicyScope.dettachAllSubjects();
        localPolicyScope.attach(localPolicySubject);
      }
    }
    
    Collection<PolicyScope> getStoredScopes()
    {
      return this.internalMap.values();
    }
    
    Set<PolicyMapKey> getAllKeys()
    {
      return this.internalMap.keySet();
    }
    
    private PolicyMapKey createLocalCopy(PolicyMapKey paramPolicyMapKey)
    {
      if (paramPolicyMapKey == null) {
        throw ((IllegalArgumentException)PolicyMap.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0045_POLICY_MAP_KEY_MUST_NOT_BE_NULL())));
      }
      PolicyMapKey localPolicyMapKey = new PolicyMapKey(paramPolicyMapKey);
      localPolicyMapKey.setHandler(this.scopeKeyHandler);
      return localPolicyMapKey;
    }
    
    public Iterator<Policy> iterator()
    {
      new Iterator()
      {
        private final Iterator<PolicyMapKey> keysIterator = PolicyMap.ScopeMap.this.internalMap.keySet().iterator();
        
        public boolean hasNext()
        {
          return this.keysIterator.hasNext();
        }
        
        public Policy next()
        {
          PolicyMapKey localPolicyMapKey = (PolicyMapKey)this.keysIterator.next();
          try
          {
            return PolicyMap.ScopeMap.this.getEffectivePolicy(localPolicyMapKey);
          }
          catch (PolicyException localPolicyException)
          {
            throw ((IllegalStateException)PolicyMap.LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0069_EXCEPTION_WHILE_RETRIEVING_EFFECTIVE_POLICY_FOR_KEY(localPolicyMapKey), localPolicyException)));
          }
        }
        
        public void remove()
        {
          throw ((UnsupportedOperationException)PolicyMap.LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED())));
        }
      };
    }
    
    public boolean isEmpty()
    {
      return this.internalMap.isEmpty();
    }
    
    public String toString()
    {
      return this.internalMap.toString();
    }
  }
  
  static enum ScopeType
  {
    SERVICE,  ENDPOINT,  OPERATION,  INPUT_MESSAGE,  OUTPUT_MESSAGE,  FAULT_MESSAGE;
    
    private ScopeType() {}
  }
}
