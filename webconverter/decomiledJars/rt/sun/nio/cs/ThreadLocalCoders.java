package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class ThreadLocalCoders
{
  private static final int CACHE_SIZE = 3;
  private static Cache decoderCache = new Cache(3)
  {
    boolean hasName(Object paramAnonymousObject1, Object paramAnonymousObject2)
    {
      if ((paramAnonymousObject2 instanceof String)) {
        return ((CharsetDecoder)paramAnonymousObject1).charset().name().equals(paramAnonymousObject2);
      }
      if ((paramAnonymousObject2 instanceof Charset)) {
        return ((CharsetDecoder)paramAnonymousObject1).charset().equals(paramAnonymousObject2);
      }
      return false;
    }
    
    Object create(Object paramAnonymousObject)
    {
      if ((paramAnonymousObject instanceof String)) {
        return Charset.forName((String)paramAnonymousObject).newDecoder();
      }
      if ((paramAnonymousObject instanceof Charset)) {
        return ((Charset)paramAnonymousObject).newDecoder();
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      return null;
    }
  };
  private static Cache encoderCache = new Cache(3)
  {
    boolean hasName(Object paramAnonymousObject1, Object paramAnonymousObject2)
    {
      if ((paramAnonymousObject2 instanceof String)) {
        return ((CharsetEncoder)paramAnonymousObject1).charset().name().equals(paramAnonymousObject2);
      }
      if ((paramAnonymousObject2 instanceof Charset)) {
        return ((CharsetEncoder)paramAnonymousObject1).charset().equals(paramAnonymousObject2);
      }
      return false;
    }
    
    Object create(Object paramAnonymousObject)
    {
      if ((paramAnonymousObject instanceof String)) {
        return Charset.forName((String)paramAnonymousObject).newEncoder();
      }
      if ((paramAnonymousObject instanceof Charset)) {
        return ((Charset)paramAnonymousObject).newEncoder();
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      return null;
    }
  };
  
  public ThreadLocalCoders() {}
  
  public static CharsetDecoder decoderFor(Object paramObject)
  {
    CharsetDecoder localCharsetDecoder = (CharsetDecoder)decoderCache.forName(paramObject);
    localCharsetDecoder.reset();
    return localCharsetDecoder;
  }
  
  public static CharsetEncoder encoderFor(Object paramObject)
  {
    CharsetEncoder localCharsetEncoder = (CharsetEncoder)encoderCache.forName(paramObject);
    localCharsetEncoder.reset();
    return localCharsetEncoder;
  }
  
  private static abstract class Cache
  {
    private ThreadLocal<Object[]> cache = new ThreadLocal();
    private final int size;
    
    Cache(int paramInt)
    {
      this.size = paramInt;
    }
    
    abstract Object create(Object paramObject);
    
    private void moveToFront(Object[] paramArrayOfObject, int paramInt)
    {
      Object localObject = paramArrayOfObject[paramInt];
      for (int i = paramInt; i > 0; i--) {
        paramArrayOfObject[i] = paramArrayOfObject[(i - 1)];
      }
      paramArrayOfObject[0] = localObject;
    }
    
    abstract boolean hasName(Object paramObject1, Object paramObject2);
    
    Object forName(Object paramObject)
    {
      Object[] arrayOfObject = (Object[])this.cache.get();
      if (arrayOfObject == null)
      {
        arrayOfObject = new Object[this.size];
        this.cache.set(arrayOfObject);
      }
      else
      {
        for (int i = 0; i < arrayOfObject.length; i++)
        {
          Object localObject2 = arrayOfObject[i];
          if ((localObject2 != null) && (hasName(localObject2, paramObject)))
          {
            if (i > 0) {
              moveToFront(arrayOfObject, i);
            }
            return localObject2;
          }
        }
      }
      Object localObject1 = create(paramObject);
      arrayOfObject[(arrayOfObject.length - 1)] = localObject1;
      moveToFront(arrayOfObject, arrayOfObject.length - 1);
      return localObject1;
    }
  }
}
