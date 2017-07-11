package sun.java2d;

public abstract interface StateTrackable
{
  public abstract State getState();
  
  public abstract StateTracker getStateTracker();
  
  public static enum State
  {
    IMMUTABLE,  STABLE,  DYNAMIC,  UNTRACKABLE;
    
    private State() {}
  }
}
