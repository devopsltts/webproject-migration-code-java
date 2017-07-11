package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;

public abstract interface RenderedImageFactory
{
  public abstract RenderedImage create(ParameterBlock paramParameterBlock, RenderingHints paramRenderingHints);
}
