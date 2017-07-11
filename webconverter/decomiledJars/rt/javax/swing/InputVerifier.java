package javax.swing;

public abstract class InputVerifier
{
  public InputVerifier() {}
  
  public abstract boolean verify(JComponent paramJComponent);
  
  public boolean shouldYieldFocus(JComponent paramJComponent)
  {
    return verify(paramJComponent);
  }
}
