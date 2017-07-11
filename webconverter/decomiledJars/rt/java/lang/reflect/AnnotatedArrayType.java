package java.lang.reflect;

public abstract interface AnnotatedArrayType
  extends AnnotatedType
{
  public abstract AnnotatedType getAnnotatedGenericComponentType();
}
