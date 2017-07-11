package javax.imageio;

import javax.imageio.metadata.IIOMetadata;

public abstract interface ImageTranscoder
{
  public abstract IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam);
  
  public abstract IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam);
}
