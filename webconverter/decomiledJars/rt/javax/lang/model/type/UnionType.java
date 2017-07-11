package javax.lang.model.type;

import java.util.List;

public abstract interface UnionType
  extends TypeMirror
{
  public abstract List<? extends TypeMirror> getAlternatives();
}
