package sun.misc;

public abstract class LRUCache<N, V>
{
  private V[] oa = null;
  private final int size;
  
  public LRUCache(int paramInt)
  {
    this.size = paramInt;
  }
  
  protected abstract V create(N paramN);
  
  protected abstract boolean hasName(V paramV, N paramN);
  
  public static void moveToFront(Object[] paramArrayOfObject, int paramInt)
  {
    Object localObject = paramArrayOfObject[paramInt];
    for (int i = paramInt; i > 0; i--) {
      paramArrayOfObject[i] = paramArrayOfObject[(i - 1)];
    }
    paramArrayOfObject[0] = localObject;
  }
  
  public V forName(N paramN)
  {
    if (this.oa == null)
    {
      Object[] arrayOfObject = (Object[])new Object[this.size];
      this.oa = arrayOfObject;
    }
    else
    {
      for (int i = 0; i < this.oa.length; i++)
      {
        Object localObject2 = this.oa[i];
        if ((localObject2 != null) && (hasName(localObject2, paramN)))
        {
          if (i > 0) {
            moveToFront(this.oa, i);
          }
          return localObject2;
        }
      }
    }
    Object localObject1 = create(paramN);
    this.oa[(this.oa.length - 1)] = localObject1;
    moveToFront(this.oa, this.oa.length - 1);
    return localObject1;
  }
}
