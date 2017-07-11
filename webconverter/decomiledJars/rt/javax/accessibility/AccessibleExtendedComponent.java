package javax.accessibility;

public abstract interface AccessibleExtendedComponent
  extends AccessibleComponent
{
  public abstract String getToolTipText();
  
  public abstract String getTitledBorderText();
  
  public abstract AccessibleKeyBinding getAccessibleKeyBinding();
}
