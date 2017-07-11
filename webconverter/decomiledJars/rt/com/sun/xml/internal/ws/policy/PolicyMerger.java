package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public final class PolicyMerger
{
  private static final PolicyMerger merger = new PolicyMerger();
  
  private PolicyMerger() {}
  
  public static PolicyMerger getMerger()
  {
    return merger;
  }
  
  public Policy merge(Collection<Policy> paramCollection)
  {
    if ((paramCollection == null) || (paramCollection.isEmpty())) {
      return null;
    }
    if (paramCollection.size() == 1) {
      return (Policy)paramCollection.iterator().next();
    }
    LinkedList localLinkedList = new LinkedList();
    StringBuilder localStringBuilder = new StringBuilder();
    NamespaceVersion localNamespaceVersion = ((Policy)paramCollection.iterator().next()).getNamespaceVersion();
    Object localObject1 = paramCollection.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Policy)((Iterator)localObject1).next();
      localLinkedList.add(((Policy)localObject2).getContent());
      if (localNamespaceVersion.compareTo(((Policy)localObject2).getNamespaceVersion()) < 0) {
        localNamespaceVersion = ((Policy)localObject2).getNamespaceVersion();
      }
      localObject3 = ((Policy)localObject2).getId();
      if (localObject3 != null)
      {
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append('-');
        }
        localStringBuilder.append((String)localObject3);
      }
    }
    localObject1 = PolicyUtils.Collections.combine(null, localLinkedList, false);
    if ((localObject1 == null) || (((Collection)localObject1).isEmpty())) {
      return Policy.createNullPolicy(localNamespaceVersion, null, localStringBuilder.length() == 0 ? null : localStringBuilder.toString());
    }
    Object localObject2 = new ArrayList(((Collection)localObject1).size());
    Object localObject3 = ((Collection)localObject1).iterator();
    while (((Iterator)localObject3).hasNext())
    {
      Collection localCollection = (Collection)((Iterator)localObject3).next();
      ((Collection)localObject2).add(AssertionSet.createMergedAssertionSet(localCollection));
    }
    return Policy.createPolicy(localNamespaceVersion, null, localStringBuilder.length() == 0 ? null : localStringBuilder.toString(), (Collection)localObject2);
  }
}
