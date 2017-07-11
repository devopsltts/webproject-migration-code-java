package java.beans;

public abstract interface Visibility
{
  public abstract boolean needsGui();
  
  public abstract void dontUseGui();
  
  public abstract void okToUseGui();
  
  public abstract boolean avoidingGui();
}
