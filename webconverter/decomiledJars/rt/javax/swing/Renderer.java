package javax.swing;

import java.awt.Component;

public abstract interface Renderer
{
  public abstract void setValue(Object paramObject, boolean paramBoolean);
  
  public abstract Component getComponent();
}
