package com.sun.xml.internal.ws.policy;

abstract interface PolicyMapKeyHandler
{
  public abstract boolean areEqual(PolicyMapKey paramPolicyMapKey1, PolicyMapKey paramPolicyMapKey2);
  
  public abstract int generateHashCode(PolicyMapKey paramPolicyMapKey);
}
