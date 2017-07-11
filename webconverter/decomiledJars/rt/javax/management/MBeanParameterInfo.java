package javax.management;

import java.util.Objects;

public class MBeanParameterInfo
  extends MBeanFeatureInfo
  implements Cloneable
{
  static final long serialVersionUID = 7432616882776782338L;
  static final MBeanParameterInfo[] NO_PARAMS = new MBeanParameterInfo[0];
  private final String type;
  
  public MBeanParameterInfo(String paramString1, String paramString2, String paramString3)
  {
    this(paramString1, paramString2, paramString3, (Descriptor)null);
  }
  
  public MBeanParameterInfo(String paramString1, String paramString2, String paramString3, Descriptor paramDescriptor)
  {
    super(paramString1, paramString3, paramDescriptor);
    this.type = paramString2;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + "description=" + getDescription() + ", " + "name=" + getName() + ", " + "type=" + getType() + ", " + "descriptor=" + getDescriptor() + "]";
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof MBeanParameterInfo)) {
      return false;
    }
    MBeanParameterInfo localMBeanParameterInfo = (MBeanParameterInfo)paramObject;
    return (Objects.equals(localMBeanParameterInfo.getName(), getName())) && (Objects.equals(localMBeanParameterInfo.getType(), getType())) && (Objects.equals(localMBeanParameterInfo.getDescription(), getDescription())) && (Objects.equals(localMBeanParameterInfo.getDescriptor(), getDescriptor()));
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { getName(), getType() });
  }
}
