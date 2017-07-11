package jdk.management.resource.internal;

import java.io.FileDescriptor;
import java.util.Objects;
import jdk.Exported;
import jdk.management.resource.ResourceAccuracy;
import jdk.management.resource.ResourceId;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@Exported(false)
public class ResourceIdImpl
  implements ResourceId
{
  private static final JavaIOFileDescriptorAccess FD_ACCESS = ;
  private final Object target;
  private final ResourceAccuracy accuracy;
  private final boolean forceUpdate;
  
  public static ResourceIdImpl of(Object paramObject)
  {
    return paramObject == null ? null : new ResourceIdImpl(paramObject, null, false);
  }
  
  public static ResourceIdImpl of(FileDescriptor paramFileDescriptor)
  {
    long l = -1L;
    if (paramFileDescriptor != null)
    {
      l = FD_ACCESS.get(paramFileDescriptor);
      if (l == -1L) {
        try
        {
          l = FD_ACCESS.getHandle(paramFileDescriptor);
        }
        catch (UnsupportedOperationException localUnsupportedOperationException) {}
      }
    }
    return l == -1L ? null : of(Integer.valueOf((int)l));
  }
  
  public static ResourceIdImpl of(Object paramObject, ResourceAccuracy paramResourceAccuracy)
  {
    return paramObject == null ? null : new ResourceIdImpl(paramObject, paramResourceAccuracy, false);
  }
  
  public static ResourceIdImpl of(Object paramObject, ResourceAccuracy paramResourceAccuracy, boolean paramBoolean)
  {
    return paramObject == null ? null : new ResourceIdImpl(paramObject, paramResourceAccuracy, paramBoolean);
  }
  
  protected ResourceIdImpl(Object paramObject, ResourceAccuracy paramResourceAccuracy, boolean paramBoolean)
  {
    this.target = paramObject;
    this.accuracy = paramResourceAccuracy;
    this.forceUpdate = paramBoolean;
  }
  
  public String getName()
  {
    return Objects.toString(this.target, null);
  }
  
  public ResourceAccuracy getAccuracy()
  {
    return this.accuracy;
  }
  
  public boolean isForcedUpdate()
  {
    return this.forceUpdate;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getName());
    ResourceAccuracy localResourceAccuracy = getAccuracy();
    if (localResourceAccuracy != null)
    {
      localStringBuilder.append(", accuracy: ");
      localStringBuilder.append(localResourceAccuracy);
    }
    return localStringBuilder.toString();
  }
}
