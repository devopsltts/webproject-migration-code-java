package javax.lang.model.element;

import java.util.List;

public abstract interface PackageElement
  extends Element, QualifiedNameable
{
  public abstract Name getQualifiedName();
  
  public abstract Name getSimpleName();
  
  public abstract List<? extends Element> getEnclosedElements();
  
  public abstract boolean isUnnamed();
  
  public abstract Element getEnclosingElement();
}
