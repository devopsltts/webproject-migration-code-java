package javax.swing.text;

public abstract interface TabableView
{
  public abstract float getTabbedSpan(float paramFloat, TabExpander paramTabExpander);
  
  public abstract float getPartialSpan(int paramInt1, int paramInt2);
}
