package javax.swing.plaf.synth;

import javax.swing.JComponent;

public abstract class SynthStyleFactory
{
  public SynthStyleFactory() {}
  
  public abstract SynthStyle getStyle(JComponent paramJComponent, Region paramRegion);
}
