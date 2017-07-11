package javax.sound.sampled;

public abstract class CompoundControl
  extends Control
{
  private Control[] controls;
  
  protected CompoundControl(Type paramType, Control[] paramArrayOfControl)
  {
    super(paramType);
    this.controls = paramArrayOfControl;
  }
  
  public Control[] getMemberControls()
  {
    Control[] arrayOfControl = new Control[this.controls.length];
    for (int i = 0; i < this.controls.length; i++) {
      arrayOfControl[i] = this.controls[i];
    }
    return arrayOfControl;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < this.controls.length; i++)
    {
      if (i != 0)
      {
        localStringBuffer.append(", ");
        if (i + 1 == this.controls.length) {
          localStringBuffer.append("and ");
        }
      }
      localStringBuffer.append(this.controls[i].getType());
    }
    return new String(getType() + " Control containing " + localStringBuffer + " Controls.");
  }
  
  public static class Type
    extends Control.Type
  {
    protected Type(String paramString)
    {
      super();
    }
  }
}
