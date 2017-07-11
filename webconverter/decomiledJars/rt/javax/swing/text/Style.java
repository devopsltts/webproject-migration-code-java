package javax.swing.text;

import javax.swing.event.ChangeListener;

public abstract interface Style
  extends MutableAttributeSet
{
  public abstract String getName();
  
  public abstract void addChangeListener(ChangeListener paramChangeListener);
  
  public abstract void removeChangeListener(ChangeListener paramChangeListener);
}
