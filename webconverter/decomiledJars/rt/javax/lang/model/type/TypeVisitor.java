package javax.lang.model.type;

public abstract interface TypeVisitor<R, P>
{
  public abstract R visit(TypeMirror paramTypeMirror, P paramP);
  
  public abstract R visit(TypeMirror paramTypeMirror);
  
  public abstract R visitPrimitive(PrimitiveType paramPrimitiveType, P paramP);
  
  public abstract R visitNull(NullType paramNullType, P paramP);
  
  public abstract R visitArray(ArrayType paramArrayType, P paramP);
  
  public abstract R visitDeclared(DeclaredType paramDeclaredType, P paramP);
  
  public abstract R visitError(ErrorType paramErrorType, P paramP);
  
  public abstract R visitTypeVariable(TypeVariable paramTypeVariable, P paramP);
  
  public abstract R visitWildcard(WildcardType paramWildcardType, P paramP);
  
  public abstract R visitExecutable(ExecutableType paramExecutableType, P paramP);
  
  public abstract R visitNoType(NoType paramNoType, P paramP);
  
  public abstract R visitUnknown(TypeMirror paramTypeMirror, P paramP);
  
  public abstract R visitUnion(UnionType paramUnionType, P paramP);
  
  public abstract R visitIntersection(IntersectionType paramIntersectionType, P paramP);
}
