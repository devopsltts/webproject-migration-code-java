package javax.lang.model.type;

import javax.lang.model.UnknownEntityException;

public class UnknownTypeException
  extends UnknownEntityException
{
  private static final long serialVersionUID = 269L;
  private transient TypeMirror type;
  private transient Object parameter;
  
  public UnknownTypeException(TypeMirror paramTypeMirror, Object paramObject)
  {
    super("Unknown type: " + paramTypeMirror);
    this.type = paramTypeMirror;
    this.parameter = paramObject;
  }
  
  public TypeMirror getUnknownType()
  {
    return this.type;
  }
  
  public Object getArgument()
  {
    return this.parameter;
  }
}
