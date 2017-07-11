package javax.print.attribute;

public abstract interface DocAttributeSet
  extends AttributeSet
{
  public abstract boolean add(Attribute paramAttribute);
  
  public abstract boolean addAll(AttributeSet paramAttributeSet);
}
