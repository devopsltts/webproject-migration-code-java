package javax.sound.sampled;

public abstract class EnumControl
  extends Control
{
  private Object[] values;
  private Object value;
  
  protected EnumControl(Type paramType, Object[] paramArrayOfObject, Object paramObject)
  {
    super(paramType);
    this.values = paramArrayOfObject;
    this.value = paramObject;
  }
  
  public void setValue(Object paramObject)
  {
    if (!isValueSupported(paramObject)) {
      throw new IllegalArgumentException("Requested value " + paramObject + " is not supported.");
    }
    this.value = paramObject;
  }
  
  public Object getValue()
  {
    return this.value;
  }
  
  public Object[] getValues()
  {
    Object[] arrayOfObject = new Object[this.values.length];
    for (int i = 0; i < this.values.length; i++) {
      arrayOfObject[i] = this.values[i];
    }
    return arrayOfObject;
  }
  
  private boolean isValueSupported(Object paramObject)
  {
    for (int i = 0; i < this.values.length; i++) {
      if (paramObject.equals(this.values[i])) {
        return true;
      }
    }
    return false;
  }
  
  public String toString()
  {
    return new String(getType() + " with current value: " + getValue());
  }
  
  public static class Type
    extends Control.Type
  {
    public static final Type REVERB = new Type("Reverb");
    
    protected Type(String paramString)
    {
      super();
    }
  }
}
