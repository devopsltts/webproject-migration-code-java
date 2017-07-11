package java.lang.reflect;

public abstract interface WildcardType
  extends Type
{
  public abstract Type[] getUpperBounds();
  
  public abstract Type[] getLowerBounds();
}
