package javax.lang.model.type;

import javax.lang.model.element.Element;

public abstract interface TypeVariable
  extends ReferenceType
{
  public abstract Element asElement();
  
  public abstract TypeMirror getUpperBound();
  
  public abstract TypeMirror getLowerBound();
}
