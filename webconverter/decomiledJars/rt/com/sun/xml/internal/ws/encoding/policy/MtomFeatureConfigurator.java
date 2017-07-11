package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class MtomFeatureConfigurator
  implements PolicyFeatureConfigurator
{
  public MtomFeatureConfigurator() {}
  
  public Collection<WebServiceFeature> getFeatures(PolicyMapKey paramPolicyMapKey, PolicyMap paramPolicyMap)
    throws PolicyException
  {
    LinkedList localLinkedList = new LinkedList();
    if ((paramPolicyMapKey != null) && (paramPolicyMap != null))
    {
      Policy localPolicy = paramPolicyMap.getEndpointEffectivePolicy(paramPolicyMapKey);
      if ((null != localPolicy) && (localPolicy.contains(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION)))
      {
        Iterator localIterator1 = localPolicy.iterator();
        while (localIterator1.hasNext())
        {
          AssertionSet localAssertionSet = (AssertionSet)localIterator1.next();
          Iterator localIterator2 = localAssertionSet.iterator();
          while (localIterator2.hasNext())
          {
            PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator2.next();
            if (EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION.equals(localPolicyAssertion.getName())) {
              localLinkedList.add(new MTOMFeature(true));
            }
          }
        }
      }
    }
    return localLinkedList;
  }
}
