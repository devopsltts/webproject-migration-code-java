package javax.lang.model.type;

import java.util.List;

public abstract interface ExecutableType
  extends TypeMirror
{
  public abstract List<? extends TypeVariable> getTypeVariables();
  
  public abstract TypeMirror getReturnType();
  
  public abstract List<? extends TypeMirror> getParameterTypes();
  
  public abstract TypeMirror getReceiverType();
  
  public abstract List<? extends TypeMirror> getThrownTypes();
}
