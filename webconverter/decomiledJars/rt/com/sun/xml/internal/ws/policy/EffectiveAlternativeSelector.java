package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator.Fitness;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class EffectiveAlternativeSelector
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(EffectiveAlternativeSelector.class);
  
  public EffectiveAlternativeSelector() {}
  
  public static void doSelection(EffectivePolicyModifier paramEffectivePolicyModifier)
    throws PolicyException
  {
    AssertionValidationProcessor localAssertionValidationProcessor = AssertionValidationProcessor.getInstance();
    selectAlternatives(paramEffectivePolicyModifier, localAssertionValidationProcessor);
  }
  
  protected static void selectAlternatives(EffectivePolicyModifier paramEffectivePolicyModifier, AssertionValidationProcessor paramAssertionValidationProcessor)
    throws PolicyException
  {
    PolicyMap localPolicyMap = paramEffectivePolicyModifier.getMap();
    Iterator localIterator = localPolicyMap.getAllServiceScopeKeys().iterator();
    PolicyMapKey localPolicyMapKey;
    Policy localPolicy;
    while (localIterator.hasNext())
    {
      localPolicyMapKey = (PolicyMapKey)localIterator.next();
      localPolicy = localPolicyMap.getServiceEffectivePolicy(localPolicyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForServiceScope(localPolicyMapKey, selectBestAlternative(localPolicy, paramAssertionValidationProcessor));
    }
    localIterator = localPolicyMap.getAllEndpointScopeKeys().iterator();
    while (localIterator.hasNext())
    {
      localPolicyMapKey = (PolicyMapKey)localIterator.next();
      localPolicy = localPolicyMap.getEndpointEffectivePolicy(localPolicyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForEndpointScope(localPolicyMapKey, selectBestAlternative(localPolicy, paramAssertionValidationProcessor));
    }
    localIterator = localPolicyMap.getAllOperationScopeKeys().iterator();
    while (localIterator.hasNext())
    {
      localPolicyMapKey = (PolicyMapKey)localIterator.next();
      localPolicy = localPolicyMap.getOperationEffectivePolicy(localPolicyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForOperationScope(localPolicyMapKey, selectBestAlternative(localPolicy, paramAssertionValidationProcessor));
    }
    localIterator = localPolicyMap.getAllInputMessageScopeKeys().iterator();
    while (localIterator.hasNext())
    {
      localPolicyMapKey = (PolicyMapKey)localIterator.next();
      localPolicy = localPolicyMap.getInputMessageEffectivePolicy(localPolicyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForInputMessageScope(localPolicyMapKey, selectBestAlternative(localPolicy, paramAssertionValidationProcessor));
    }
    localIterator = localPolicyMap.getAllOutputMessageScopeKeys().iterator();
    while (localIterator.hasNext())
    {
      localPolicyMapKey = (PolicyMapKey)localIterator.next();
      localPolicy = localPolicyMap.getOutputMessageEffectivePolicy(localPolicyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForOutputMessageScope(localPolicyMapKey, selectBestAlternative(localPolicy, paramAssertionValidationProcessor));
    }
    localIterator = localPolicyMap.getAllFaultMessageScopeKeys().iterator();
    while (localIterator.hasNext())
    {
      localPolicyMapKey = (PolicyMapKey)localIterator.next();
      localPolicy = localPolicyMap.getFaultMessageEffectivePolicy(localPolicyMapKey);
      paramEffectivePolicyModifier.setNewEffectivePolicyForFaultMessageScope(localPolicyMapKey, selectBestAlternative(localPolicy, paramAssertionValidationProcessor));
    }
  }
  
  private static Policy selectBestAlternative(Policy paramPolicy, AssertionValidationProcessor paramAssertionValidationProcessor)
    throws PolicyException
  {
    Object localObject1 = null;
    Object localObject2 = AlternativeFitness.UNEVALUATED;
    Object localObject3 = paramPolicy.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      AssertionSet localAssertionSet = (AssertionSet)((Iterator)localObject3).next();
      AlternativeFitness localAlternativeFitness = localAssertionSet.isEmpty() ? AlternativeFitness.SUPPORTED_EMPTY : AlternativeFitness.UNEVALUATED;
      Iterator localIterator = localAssertionSet.iterator();
      while (localIterator.hasNext())
      {
        PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator.next();
        PolicyAssertionValidator.Fitness localFitness = paramAssertionValidationProcessor.validateClientSide(localPolicyAssertion);
        switch (1.$SwitchMap$com$sun$xml$internal$ws$policy$spi$PolicyAssertionValidator$Fitness[localFitness.ordinal()])
        {
        case 1: 
        case 2: 
        case 4: 
          LOGGER.warning(LocalizationMessages.WSP_0075_PROBLEMATIC_ASSERTION_STATE(localPolicyAssertion.getName(), localFitness));
          break;
        }
        localAlternativeFitness = localAlternativeFitness.combine(localFitness);
      }
      if (((AlternativeFitness)localObject2).compareTo(localAlternativeFitness) < 0)
      {
        localObject1 = localAssertionSet;
        localObject2 = localAlternativeFitness;
      }
      if (localObject2 == AlternativeFitness.SUPPORTED) {
        break;
      }
    }
    switch (1.$SwitchMap$com$sun$xml$internal$ws$policy$EffectiveAlternativeSelector$AlternativeFitness[localObject2.ordinal()])
    {
    case 1: 
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0053_INVALID_CLIENT_SIDE_ALTERNATIVE())));
    case 2: 
    case 3: 
    case 4: 
      LOGGER.warning(LocalizationMessages.WSP_0019_SUBOPTIMAL_ALTERNATIVE_SELECTED(localObject2));
      break;
    }
    localObject3 = null;
    if (localObject1 != null)
    {
      localObject3 = new LinkedList();
      ((Collection)localObject3).add(localObject1);
    }
    return Policy.createPolicy(paramPolicy.getNamespaceVersion(), paramPolicy.getName(), paramPolicy.getId(), (Collection)localObject3);
  }
  
  private static abstract enum AlternativeFitness
  {
    UNEVALUATED,  INVALID,  UNKNOWN,  UNSUPPORTED,  PARTIALLY_SUPPORTED,  SUPPORTED_EMPTY,  SUPPORTED;
    
    private AlternativeFitness() {}
    
    abstract AlternativeFitness combine(PolicyAssertionValidator.Fitness paramFitness);
  }
}
