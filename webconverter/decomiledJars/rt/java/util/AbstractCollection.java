package java.util;

import java.lang.reflect.Array;

public abstract class AbstractCollection<E>
  implements Collection<E>
{
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  protected AbstractCollection() {}
  
  public abstract Iterator<E> iterator();
  
  public abstract int size();
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public boolean contains(Object paramObject)
  {
    Iterator localIterator = iterator();
    if (paramObject == null)
    {
      do
      {
        if (!localIterator.hasNext()) {
          break;
        }
      } while (localIterator.next() != null);
      return true;
    }
    while (localIterator.hasNext()) {
      if (paramObject.equals(localIterator.next())) {
        return true;
      }
    }
    return false;
  }
  
  public Object[] toArray()
  {
    Object[] arrayOfObject = new Object[size()];
    Iterator localIterator = iterator();
    for (int i = 0; i < arrayOfObject.length; i++)
    {
      if (!localIterator.hasNext()) {
        return Arrays.copyOf(arrayOfObject, i);
      }
      arrayOfObject[i] = localIterator.next();
    }
    return localIterator.hasNext() ? finishToArray(arrayOfObject, localIterator) : arrayOfObject;
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    int i = size();
    Object[] arrayOfObject = paramArrayOfT.length >= i ? paramArrayOfT : (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
    Iterator localIterator = iterator();
    for (int j = 0; j < arrayOfObject.length; j++)
    {
      if (!localIterator.hasNext())
      {
        if (paramArrayOfT == arrayOfObject)
        {
          arrayOfObject[j] = null;
        }
        else
        {
          if (paramArrayOfT.length < j) {
            return Arrays.copyOf(arrayOfObject, j);
          }
          System.arraycopy(arrayOfObject, 0, paramArrayOfT, 0, j);
          if (paramArrayOfT.length > j) {
            paramArrayOfT[j] = null;
          }
        }
        return paramArrayOfT;
      }
      arrayOfObject[j] = localIterator.next();
    }
    return localIterator.hasNext() ? finishToArray(arrayOfObject, localIterator) : arrayOfObject;
  }
  
  private static <T> T[] finishToArray(T[] paramArrayOfT, Iterator<?> paramIterator)
  {
    int i = paramArrayOfT.length;
    while (paramIterator.hasNext())
    {
      int j = paramArrayOfT.length;
      if (i == j)
      {
        int k = j + (j >> 1) + 1;
        if (k - 2147483639 > 0) {
          k = hugeCapacity(j + 1);
        }
        paramArrayOfT = Arrays.copyOf(paramArrayOfT, k);
      }
      paramArrayOfT[(i++)] = paramIterator.next();
    }
    return i == paramArrayOfT.length ? paramArrayOfT : Arrays.copyOf(paramArrayOfT, i);
  }
  
  private static int hugeCapacity(int paramInt)
  {
    if (paramInt < 0) {
      throw new OutOfMemoryError("Required array size too large");
    }
    return paramInt > 2147483639 ? Integer.MAX_VALUE : 2147483639;
  }
  
  public boolean add(E paramE)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(Object paramObject)
  {
    Iterator localIterator = iterator();
    if (paramObject == null)
    {
      do
      {
        if (!localIterator.hasNext()) {
          break;
        }
      } while (localIterator.next() != null);
      localIterator.remove();
      return true;
    }
    while (localIterator.hasNext()) {
      if (paramObject.equals(localIterator.next()))
      {
        localIterator.remove();
        return true;
      }
    }
    return false;
  }
  
  public boolean containsAll(Collection<?> paramCollection)
  {
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (!contains(localObject)) {
        return false;
      }
    }
    return true;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    boolean bool = false;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (add(localObject)) {
        bool = true;
      }
    }
    return bool;
  }
  
  public boolean removeAll(Collection<?> paramCollection)
  {
    Objects.requireNonNull(paramCollection);
    boolean bool = false;
    Iterator localIterator = iterator();
    while (localIterator.hasNext()) {
      if (paramCollection.contains(localIterator.next()))
      {
        localIterator.remove();
        bool = true;
      }
    }
    return bool;
  }
  
  public boolean retainAll(Collection<?> paramCollection)
  {
    Objects.requireNonNull(paramCollection);
    boolean bool = false;
    Iterator localIterator = iterator();
    while (localIterator.hasNext()) {
      if (!paramCollection.contains(localIterator.next()))
      {
        localIterator.remove();
        bool = true;
      }
    }
    return bool;
  }
  
  public void clear()
  {
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      localIterator.next();
      localIterator.remove();
    }
  }
  
  public String toString()
  {
    Iterator localIterator = iterator();
    if (!localIterator.hasNext()) {
      return "[]";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('[');
    for (;;)
    {
      Object localObject = localIterator.next();
      localStringBuilder.append(localObject == this ? "(this Collection)" : localObject);
      if (!localIterator.hasNext()) {
        return ']';
      }
      localStringBuilder.append(',').append(' ');
    }
  }
}
