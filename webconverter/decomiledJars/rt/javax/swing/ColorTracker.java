package javax.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

class ColorTracker
  implements ActionListener, Serializable
{
  JColorChooser chooser;
  Color color;
  
  public ColorTracker(JColorChooser paramJColorChooser)
  {
    this.chooser = paramJColorChooser;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    this.color = this.chooser.getColor();
  }
  
  public Color getColor()
  {
    return this.color;
  }
}
