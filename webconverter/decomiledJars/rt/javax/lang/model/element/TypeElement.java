package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.TypeMirror;

public abstract interface TypeElement
  extends Element, Parameterizable, QualifiedNameable
{
  public abstract List<? extends Element> getEnclosedElements();
  
  public abstract NestingKind getNestingKind();
  
  public abstract Name getQualifiedName();
  
  public abstract Name getSimpleName();
  
  public abstract TypeMirror getSuperclass();
  
  public abstract List<? extends TypeMirror> getInterfaces();
  
  public abstract List<? extends TypeParameterElement> getTypeParameters();
  
  public abstract Element getEnclosingElement();
}
