package com.sun.xml.internal.ws.policy;

public final class PolicyMapExtender
  extends PolicyMapMutator
{
  private PolicyMapExtender() {}
  
  public static PolicyMapExtender createPolicyMapExtender()
  {
    return new PolicyMapExtender();
  }
  
  public void putServiceSubject(PolicyMapKey paramPolicyMapKey, PolicySubject paramPolicySubject)
  {
    getMap().putSubject(PolicyMap.ScopeType.SERVICE, paramPolicyMapKey, paramPolicySubject);
  }
  
  public void putEndpointSubject(PolicyMapKey paramPolicyMapKey, PolicySubject paramPolicySubject)
  {
    getMap().putSubject(PolicyMap.ScopeType.ENDPOINT, paramPolicyMapKey, paramPolicySubject);
  }
  
  public void putOperationSubject(PolicyMapKey paramPolicyMapKey, PolicySubject paramPolicySubject)
  {
    getMap().putSubject(PolicyMap.ScopeType.OPERATION, paramPolicyMapKey, paramPolicySubject);
  }
  
  public void putInputMessageSubject(PolicyMapKey paramPolicyMapKey, PolicySubject paramPolicySubject)
  {
    getMap().putSubject(PolicyMap.ScopeType.INPUT_MESSAGE, paramPolicyMapKey, paramPolicySubject);
  }
  
  public void putOutputMessageSubject(PolicyMapKey paramPolicyMapKey, PolicySubject paramPolicySubject)
  {
    getMap().putSubject(PolicyMap.ScopeType.OUTPUT_MESSAGE, paramPolicyMapKey, paramPolicySubject);
  }
  
  public void putFaultMessageSubject(PolicyMapKey paramPolicyMapKey, PolicySubject paramPolicySubject)
  {
    getMap().putSubject(PolicyMap.ScopeType.FAULT_MESSAGE, paramPolicyMapKey, paramPolicySubject);
  }
}
