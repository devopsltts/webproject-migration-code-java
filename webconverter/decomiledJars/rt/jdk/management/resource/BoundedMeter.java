package jdk.management.resource;

public class BoundedMeter
  extends NotifyingMeter
  implements ResourceMeter, ResourceRequest
{
  private volatile long bound;
  
  public static BoundedMeter create(ResourceType paramResourceType, long paramLong)
  {
    return create(paramResourceType, paramLong, null, null);
  }
  
  public static BoundedMeter create(ResourceType paramResourceType, long paramLong, ResourceRequest paramResourceRequest)
  {
    return create(paramResourceType, paramLong, paramResourceRequest, null);
  }
  
  public static BoundedMeter create(ResourceType paramResourceType, long paramLong, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover)
  {
    return new BoundedMeter(paramResourceType, paramLong, paramResourceRequest, paramResourceApprover);
  }
  
  public static BoundedMeter create(ResourceType paramResourceType, long paramLong, ResourceApprover paramResourceApprover)
  {
    return create(paramResourceType, paramLong, null, paramResourceApprover);
  }
  
  protected BoundedMeter(ResourceType paramResourceType, long paramLong, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover)
  {
    super(paramResourceType, paramResourceRequest, paramResourceApprover);
    if (paramLong < 0L) {
      throw new IllegalArgumentException("bound must be zero or greater");
    }
    this.bound = paramLong;
  }
  
  protected long validate(long paramLong1, long paramLong2, ResourceId paramResourceId)
  {
    ResourceApprover localResourceApprover = getApprover();
    long l1 = paramLong2;
    if (localResourceApprover != null)
    {
      long l2 = getGranularity();
      long l3 = paramLong1 + paramLong2;
      long l4 = Math.floorDiv(paramLong1, l2);
      long l5 = Math.floorDiv(l3, l2);
      if ((l4 != l5) || (this.bound - l3 < 0L))
      {
        l1 = localResourceApprover.request(this, paramLong1, paramLong2, paramResourceId);
        if ((l1 != paramLong2) && (l1 != 0L)) {
          l1 = paramLong2;
        }
      }
    }
    if (this.bound - (paramLong1 + l1) < 0L) {
      l1 = 0L;
    }
    return l1;
  }
  
  public final synchronized long getBound()
  {
    return this.bound;
  }
  
  public final synchronized long setBound(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("bound must be zero or greater");
    }
    long l = this.bound;
    this.bound = paramLong;
    return l;
  }
  
  synchronized long setGranularityInternal(long paramLong)
  {
    long l = super.setGranularityInternal(paramLong);
    return l;
  }
  
  public String toString()
  {
    return super.toString() + "; bound: " + Long.toString(this.bound);
  }
}
