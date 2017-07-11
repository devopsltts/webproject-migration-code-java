package com.sun.imageio.plugins.gif;

import com.sun.imageio.plugins.common.PaletteBuilder;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class GIFImageWriterSpi
  extends ImageWriterSpi
{
  private static final String vendorName = "Oracle Corporation";
  private static final String version = "1.0";
  private static final String[] names = { "gif", "GIF" };
  private static final String[] suffixes = { "gif" };
  private static final String[] MIMETypes = { "image/gif" };
  private static final String writerClassName = "com.sun.imageio.plugins.gif.GIFImageWriter";
  private static final String[] readerSpiNames = { "com.sun.imageio.plugins.gif.GIFImageReaderSpi" };
  
  public GIFImageWriterSpi()
  {
    super("Oracle Corporation", "1.0", names, suffixes, MIMETypes, "com.sun.imageio.plugins.gif.GIFImageWriter", new Class[] { ImageOutputStream.class }, readerSpiNames, true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", null, null, true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", null, null);
  }
  
  public boolean canEncodeImage(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    if (paramImageTypeSpecifier == null) {
      throw new IllegalArgumentException("type == null!");
    }
    SampleModel localSampleModel = paramImageTypeSpecifier.getSampleModel();
    ColorModel localColorModel = paramImageTypeSpecifier.getColorModel();
    int i = (localSampleModel.getNumBands() == 1) && (localSampleModel.getSampleSize(0) <= 8) && (localSampleModel.getWidth() <= 65535) && (localSampleModel.getHeight() <= 65535) && ((localColorModel == null) || (localColorModel.getComponentSize()[0] <= 8)) ? 1 : 0;
    if (i != 0) {
      return true;
    }
    return PaletteBuilder.canCreatePalette(paramImageTypeSpecifier);
  }
  
  public String getDescription(Locale paramLocale)
  {
    return "Standard GIF image writer";
  }
  
  public ImageWriter createWriterInstance(Object paramObject)
  {
    return new GIFImageWriter(this);
  }
}
