package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.ReaderUtil;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public class WBMPImageReaderSpi
  extends ImageReaderSpi
{
  private static final int MAX_WBMP_WIDTH = 1024;
  private static final int MAX_WBMP_HEIGHT = 768;
  private static String[] writerSpiNames = { "com.sun.imageio.plugins.wbmp.WBMPImageWriterSpi" };
  private static String[] formatNames = { "wbmp", "WBMP" };
  private static String[] entensions = { "wbmp" };
  private static String[] mimeType = { "image/vnd.wap.wbmp" };
  private boolean registered = false;
  
  public WBMPImageReaderSpi()
  {
    super("Oracle Corporation", "1.0", formatNames, entensions, mimeType, "com.sun.imageio.plugins.wbmp.WBMPImageReader", new Class[] { ImageInputStream.class }, writerSpiNames, true, null, null, null, null, true, "javax_imageio_wbmp_1.0", "com.sun.imageio.plugins.wbmp.WBMPMetadataFormat", null, null);
  }
  
  public void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass)
  {
    if (this.registered) {
      return;
    }
    this.registered = true;
  }
  
  public String getDescription(Locale paramLocale)
  {
    return "Standard WBMP Image Reader";
  }
  
  public boolean canDecodeInput(Object paramObject)
    throws IOException
  {
    if (!(paramObject instanceof ImageInputStream)) {
      return false;
    }
    ImageInputStream localImageInputStream = (ImageInputStream)paramObject;
    localImageInputStream.mark();
    try
    {
      int i = localImageInputStream.readByte();
      int j = localImageInputStream.readByte();
      if ((i != 0) || (j != 0))
      {
        boolean bool1 = false;
        return bool1;
      }
      int k = ReaderUtil.readMultiByteInteger(localImageInputStream);
      int m = ReaderUtil.readMultiByteInteger(localImageInputStream);
      if ((k <= 0) || (m <= 0))
      {
        boolean bool2 = false;
        return bool2;
      }
      long l1 = localImageInputStream.length();
      if (l1 == -1L)
      {
        boolean bool3 = (k < 1024) && (m < 768);
        return bool3;
      }
      l1 -= localImageInputStream.getStreamPosition();
      long l2 = k / 8 + (k % 8 == 0 ? 0 : 1);
      boolean bool4 = l1 == l2 * m;
      return bool4;
    }
    finally
    {
      localImageInputStream.reset();
    }
  }
  
  public ImageReader createReaderInstance(Object paramObject)
    throws IIOException
  {
    return new WBMPImageReader(this);
  }
}
