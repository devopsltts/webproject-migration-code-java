package javax.management.openmbean;

import javax.management.MBeanParameterInfo;

public abstract interface OpenMBeanConstructorInfo
{
  public abstract String getDescription();
  
  public abstract String getName();
  
  public abstract MBeanParameterInfo[] getSignature();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}
