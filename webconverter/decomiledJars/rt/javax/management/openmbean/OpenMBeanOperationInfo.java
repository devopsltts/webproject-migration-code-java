package javax.management.openmbean;

import javax.management.MBeanParameterInfo;

public abstract interface OpenMBeanOperationInfo
{
  public abstract String getDescription();
  
  public abstract String getName();
  
  public abstract MBeanParameterInfo[] getSignature();
  
  public abstract int getImpact();
  
  public abstract String getReturnType();
  
  public abstract OpenType<?> getReturnOpenType();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}
