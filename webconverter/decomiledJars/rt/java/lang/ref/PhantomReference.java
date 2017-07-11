package java.lang.ref;

public class PhantomReference<T>
  extends Reference<T>
{
  public T get()
  {
    return null;
  }
  
  public PhantomReference(T paramT, ReferenceQueue<? super T> paramReferenceQueue)
  {
    super(paramT, paramReferenceQueue);
  }
}
