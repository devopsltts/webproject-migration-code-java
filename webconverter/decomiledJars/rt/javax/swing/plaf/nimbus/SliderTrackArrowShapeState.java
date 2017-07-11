package javax.swing.plaf.nimbus;

import javax.swing.JComponent;

class SliderTrackArrowShapeState
  extends State
{
  SliderTrackArrowShapeState()
  {
    super("ArrowShape");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return paramJComponent.getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE;
  }
}
