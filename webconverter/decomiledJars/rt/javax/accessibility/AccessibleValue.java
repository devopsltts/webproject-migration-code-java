package javax.accessibility;

public abstract interface AccessibleValue
{
  public abstract Number getCurrentAccessibleValue();
  
  public abstract boolean setCurrentAccessibleValue(Number paramNumber);
  
  public abstract Number getMinimumAccessibleValue();
  
  public abstract Number getMaximumAccessibleValue();
}
