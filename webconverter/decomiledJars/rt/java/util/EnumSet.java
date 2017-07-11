package java.util;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public abstract class EnumSet<E extends Enum<E>>
  extends AbstractSet<E>
  implements Cloneable, Serializable
{
  final Class<E> elementType;
  final Enum<?>[] universe;
  private static Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = new Enum[0];
  
  EnumSet(Class<E> paramClass, Enum<?>[] paramArrayOfEnum)
  {
    this.elementType = paramClass;
    this.universe = paramArrayOfEnum;
  }
  
  public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> paramClass)
  {
    Enum[] arrayOfEnum = getUniverse(paramClass);
    if (arrayOfEnum == null) {
      throw new ClassCastException(paramClass + " not an enum");
    }
    if (arrayOfEnum.length <= 64) {
      return new RegularEnumSet(paramClass, arrayOfEnum);
    }
    return new JumboEnumSet(paramClass, arrayOfEnum);
  }
  
  public static <E extends Enum<E>> EnumSet<E> allOf(Class<E> paramClass)
  {
    EnumSet localEnumSet = noneOf(paramClass);
    localEnumSet.addAll();
    return localEnumSet;
  }
  
  abstract void addAll();
  
  public static <E extends Enum<E>> EnumSet<E> copyOf(EnumSet<E> paramEnumSet)
  {
    return paramEnumSet.clone();
  }
  
  public static <E extends Enum<E>> EnumSet<E> copyOf(Collection<E> paramCollection)
  {
    if ((paramCollection instanceof EnumSet)) {
      return ((EnumSet)paramCollection).clone();
    }
    if (paramCollection.isEmpty()) {
      throw new IllegalArgumentException("Collection is empty");
    }
    Iterator localIterator = paramCollection.iterator();
    Enum localEnum = (Enum)localIterator.next();
    EnumSet localEnumSet = of(localEnum);
    while (localIterator.hasNext()) {
      localEnumSet.add(localIterator.next());
    }
    return localEnumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> complementOf(EnumSet<E> paramEnumSet)
  {
    EnumSet localEnumSet = copyOf(paramEnumSet);
    localEnumSet.complement();
    return localEnumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE)
  {
    EnumSet localEnumSet = noneOf(paramE.getDeclaringClass());
    localEnumSet.add(paramE);
    return localEnumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2)
  {
    EnumSet localEnumSet = noneOf(paramE1.getDeclaringClass());
    localEnumSet.add(paramE1);
    localEnumSet.add(paramE2);
    return localEnumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2, E paramE3)
  {
    EnumSet localEnumSet = noneOf(paramE1.getDeclaringClass());
    localEnumSet.add(paramE1);
    localEnumSet.add(paramE2);
    localEnumSet.add(paramE3);
    return localEnumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2, E paramE3, E paramE4)
  {
    EnumSet localEnumSet = noneOf(paramE1.getDeclaringClass());
    localEnumSet.add(paramE1);
    localEnumSet.add(paramE2);
    localEnumSet.add(paramE3);
    localEnumSet.add(paramE4);
    return localEnumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2, E paramE3, E paramE4, E paramE5)
  {
    EnumSet localEnumSet = noneOf(paramE1.getDeclaringClass());
    localEnumSet.add(paramE1);
    localEnumSet.add(paramE2);
    localEnumSet.add(paramE3);
    localEnumSet.add(paramE4);
    localEnumSet.add(paramE5);
    return localEnumSet;
  }
  
  @SafeVarargs
  public static <E extends Enum<E>> EnumSet<E> of(E paramE, E... paramVarArgs)
  {
    EnumSet localEnumSet = noneOf(paramE.getDeclaringClass());
    localEnumSet.add(paramE);
    for (E ? : paramVarArgs) {
      localEnumSet.add(?);
    }
    return localEnumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> range(E paramE1, E paramE2)
  {
    if (paramE1.compareTo(paramE2) > 0) {
      throw new IllegalArgumentException(paramE1 + " > " + paramE2);
    }
    EnumSet localEnumSet = noneOf(paramE1.getDeclaringClass());
    localEnumSet.addRange(paramE1, paramE2);
    return localEnumSet;
  }
  
  abstract void addRange(E paramE1, E paramE2);
  
  public EnumSet<E> clone()
  {
    try
    {
      return (EnumSet)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
  }
  
  abstract void complement();
  
  final void typeCheck(E paramE)
  {
    Class localClass = paramE.getClass();
    if ((localClass != this.elementType) && (localClass.getSuperclass() != this.elementType)) {
      throw new ClassCastException(localClass + " != " + this.elementType);
    }
  }
  
  private static <E extends Enum<E>> E[] getUniverse(Class<E> paramClass)
  {
    return SharedSecrets.getJavaLangAccess().getEnumConstantsShared(paramClass);
  }
  
  Object writeReplace()
  {
    return new SerializationProxy(this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Proxy required");
  }
  
  private static class SerializationProxy<E extends Enum<E>>
    implements Serializable
  {
    private final Class<E> elementType;
    private final Enum<?>[] elements;
    private static final long serialVersionUID = 362491234563181265L;
    
    SerializationProxy(EnumSet<E> paramEnumSet)
    {
      this.elementType = paramEnumSet.elementType;
      this.elements = ((Enum[])paramEnumSet.toArray(EnumSet.ZERO_LENGTH_ENUM_ARRAY));
    }
    
    private Object readResolve()
    {
      EnumSet localEnumSet = EnumSet.noneOf(this.elementType);
      for (Enum localEnum : this.elements) {
        localEnumSet.add(localEnum);
      }
      return localEnumSet;
    }
  }
}
