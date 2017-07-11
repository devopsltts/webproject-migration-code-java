package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.TypeMirror;

public abstract interface ExecutableElement
  extends Element, Parameterizable
{
  public abstract List<? extends TypeParameterElement> getTypeParameters();
  
  public abstract TypeMirror getReturnType();
  
  public abstract List<? extends VariableElement> getParameters();
  
  public abstract TypeMirror getReceiverType();
  
  public abstract boolean isVarArgs();
  
  public abstract boolean isDefault();
  
  public abstract List<? extends TypeMirror> getThrownTypes();
  
  public abstract AnnotationValue getDefaultValue();
  
  public abstract Name getSimpleName();
}
