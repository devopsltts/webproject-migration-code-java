package javax.swing.colorchooser;

import java.awt.Color;
import javax.swing.event.ChangeListener;

public abstract interface ColorSelectionModel
{
  public abstract Color getSelectedColor();
  
  public abstract void setSelectedColor(Color paramColor);
  
  public abstract void addChangeListener(ChangeListener paramChangeListener);
  
  public abstract void removeChangeListener(ChangeListener paramChangeListener);
}
