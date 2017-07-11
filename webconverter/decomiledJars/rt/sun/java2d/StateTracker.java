package sun.java2d;

public abstract interface StateTracker
{
  public static final StateTracker ALWAYS_CURRENT = new StateTracker()
  {
    public boolean isCurrent()
    {
      return true;
    }
  };
  public static final StateTracker NEVER_CURRENT = new StateTracker()
  {
    public boolean isCurrent()
    {
      return false;
    }
  };
  
  public abstract boolean isCurrent();
}
