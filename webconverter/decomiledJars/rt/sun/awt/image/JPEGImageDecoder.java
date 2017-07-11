package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

public class JPEGImageDecoder
  extends ImageDecoder
{
  private static ColorModel RGBcolormodel;
  private static ColorModel ARGBcolormodel;
  private static ColorModel Graycolormodel;
  private static final Class InputStreamClass = InputStream.class;
  private ColorModel colormodel;
  Hashtable props = new Hashtable();
  private static final int hintflags = 22;
  
  private static native void initIDs(Class paramClass);
  
  private native void readImage(InputStream paramInputStream, byte[] paramArrayOfByte)
    throws ImageFormatException, IOException;
  
  public JPEGImageDecoder(InputStreamImageSource paramInputStreamImageSource, InputStream paramInputStream)
  {
    super(paramInputStreamImageSource, paramInputStream);
  }
  
  private static void error(String paramString)
    throws ImageFormatException
  {
    throw new ImageFormatException(paramString);
  }
  
  public boolean sendHeaderInfo(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    setDimensions(paramInt1, paramInt2);
    setProperties(this.props);
    if (paramBoolean1) {
      this.colormodel = Graycolormodel;
    } else if (paramBoolean2) {
      this.colormodel = ARGBcolormodel;
    } else {
      this.colormodel = RGBcolormodel;
    }
    setColorModel(this.colormodel);
    int i = 22;
    if (!paramBoolean3) {
      i |= 0x8;
    }
    setHints(22);
    headerComplete();
    return true;
  }
  
  public boolean sendPixels(int[] paramArrayOfInt, int paramInt)
  {
    int i = setPixels(0, paramInt, paramArrayOfInt.length, 1, this.colormodel, paramArrayOfInt, 0, paramArrayOfInt.length);
    if (i <= 0) {
      this.aborted = true;
    }
    return !this.aborted;
  }
  
  public boolean sendPixels(byte[] paramArrayOfByte, int paramInt)
  {
    int i = setPixels(0, paramInt, paramArrayOfByte.length, 1, this.colormodel, paramArrayOfByte, 0, paramArrayOfByte.length);
    if (i <= 0) {
      this.aborted = true;
    }
    return !this.aborted;
  }
  
  public void produceImage()
    throws IOException, ImageFormatException
  {
    try
    {
      readImage(this.input, new byte['Ѐ']);
      if (!this.aborted) {
        imageComplete(3, true);
      }
    }
    catch (IOException localIOException)
    {
      if (!this.aborted) {
        throw localIOException;
      }
    }
    finally
    {
      close();
    }
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("jpeg");
        return null;
      }
    });
    initIDs(InputStreamClass);
    RGBcolormodel = new DirectColorModel(24, 16711680, 65280, 255);
    ARGBcolormodel = ColorModel.getRGBdefault();
    byte[] arrayOfByte = new byte['Ā'];
    for (int i = 0; i < 256; i++) {
      arrayOfByte[i] = ((byte)i);
    }
    Graycolormodel = new IndexColorModel(8, 256, arrayOfByte, arrayOfByte, arrayOfByte);
  }
}
