package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelGenerator;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelGenerator.PolicySourceModelCreator;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;

public abstract class ModelGenerator
  extends PolicyModelGenerator
{
  private static final SourceModelCreator CREATOR = new SourceModelCreator();
  
  private ModelGenerator() {}
  
  public static PolicyModelGenerator getGenerator()
  {
    return PolicyModelGenerator.getCompactGenerator(CREATOR);
  }
  
  protected static class SourceModelCreator
    extends PolicyModelGenerator.PolicySourceModelCreator
  {
    protected SourceModelCreator() {}
    
    protected PolicySourceModel create(Policy paramPolicy)
    {
      return SourceModel.createPolicySourceModel(paramPolicy.getNamespaceVersion(), paramPolicy.getId(), paramPolicy.getName());
    }
  }
}
