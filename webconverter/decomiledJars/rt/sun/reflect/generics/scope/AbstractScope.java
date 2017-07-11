package sun.reflect.generics.scope;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;

public abstract class AbstractScope<D extends GenericDeclaration>
  implements Scope
{
  private final D recvr;
  private volatile Scope enclosingScope;
  
  protected AbstractScope(D paramD)
  {
    this.recvr = paramD;
  }
  
  protected D getRecvr()
  {
    return this.recvr;
  }
  
  protected abstract Scope computeEnclosingScope();
  
  protected Scope getEnclosingScope()
  {
    Scope localScope = this.enclosingScope;
    if (localScope == null)
    {
      localScope = computeEnclosingScope();
      this.enclosingScope = localScope;
    }
    return localScope;
  }
  
  public TypeVariable<?> lookup(String paramString)
  {
    TypeVariable[] arrayOfTypeVariable1 = getRecvr().getTypeParameters();
    for (TypeVariable localTypeVariable : arrayOfTypeVariable1) {
      if (localTypeVariable.getName().equals(paramString)) {
        return localTypeVariable;
      }
    }
    return getEnclosingScope().lookup(paramString);
  }
}
