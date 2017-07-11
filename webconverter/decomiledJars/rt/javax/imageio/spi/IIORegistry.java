package javax.imageio.spi;

import com.sun.imageio.plugins.bmp.BMPImageReaderSpi;
import com.sun.imageio.plugins.bmp.BMPImageWriterSpi;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import com.sun.imageio.plugins.gif.GIFImageWriterSpi;
import com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi;
import com.sun.imageio.plugins.png.PNGImageReaderSpi;
import com.sun.imageio.plugins.png.PNGImageWriterSpi;
import com.sun.imageio.plugins.wbmp.WBMPImageReaderSpi;
import com.sun.imageio.plugins.wbmp.WBMPImageWriterSpi;
import com.sun.imageio.spi.FileImageInputStreamSpi;
import com.sun.imageio.spi.FileImageOutputStreamSpi;
import com.sun.imageio.spi.InputStreamImageInputStreamSpi;
import com.sun.imageio.spi.OutputStreamImageOutputStreamSpi;
import com.sun.imageio.spi.RAFImageInputStreamSpi;
import com.sun.imageio.spi.RAFImageOutputStreamSpi;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Vector;
import sun.awt.AppContext;

public final class IIORegistry
  extends ServiceRegistry
{
  private static final Vector initialCategories = new Vector(5);
  
  private IIORegistry()
  {
    super(initialCategories.iterator());
    registerStandardSpis();
    registerApplicationClasspathSpis();
  }
  
  public static IIORegistry getDefaultInstance()
  {
    AppContext localAppContext = AppContext.getAppContext();
    IIORegistry localIIORegistry = (IIORegistry)localAppContext.get(IIORegistry.class);
    if (localIIORegistry == null)
    {
      localIIORegistry = new IIORegistry();
      localAppContext.put(IIORegistry.class, localIIORegistry);
    }
    return localIIORegistry;
  }
  
  private void registerStandardSpis()
  {
    registerServiceProvider(new GIFImageReaderSpi());
    registerServiceProvider(new GIFImageWriterSpi());
    registerServiceProvider(new BMPImageReaderSpi());
    registerServiceProvider(new BMPImageWriterSpi());
    registerServiceProvider(new WBMPImageReaderSpi());
    registerServiceProvider(new WBMPImageWriterSpi());
    registerServiceProvider(new PNGImageReaderSpi());
    registerServiceProvider(new PNGImageWriterSpi());
    registerServiceProvider(new JPEGImageReaderSpi());
    registerServiceProvider(new JPEGImageWriterSpi());
    registerServiceProvider(new FileImageInputStreamSpi());
    registerServiceProvider(new FileImageOutputStreamSpi());
    registerServiceProvider(new InputStreamImageInputStreamSpi());
    registerServiceProvider(new OutputStreamImageOutputStreamSpi());
    registerServiceProvider(new RAFImageInputStreamSpi());
    registerServiceProvider(new RAFImageOutputStreamSpi());
    registerInstalledProviders();
  }
  
  public void registerApplicationClasspathSpis()
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    Iterator localIterator1 = getCategories();
    while (localIterator1.hasNext())
    {
      Class localClass = (Class)localIterator1.next();
      Iterator localIterator2 = ServiceLoader.load(localClass, localClassLoader).iterator();
      while (localIterator2.hasNext()) {
        try
        {
          IIOServiceProvider localIIOServiceProvider = (IIOServiceProvider)localIterator2.next();
          registerServiceProvider(localIIOServiceProvider);
        }
        catch (ServiceConfigurationError localServiceConfigurationError)
        {
          if (System.getSecurityManager() != null) {
            localServiceConfigurationError.printStackTrace();
          } else {
            throw localServiceConfigurationError;
          }
        }
      }
    }
  }
  
  private void registerInstalledProviders()
  {
    PrivilegedAction local1 = new PrivilegedAction()
    {
      public Object run()
      {
        Iterator localIterator1 = IIORegistry.this.getCategories();
        while (localIterator1.hasNext())
        {
          Class localClass = (Class)localIterator1.next();
          Iterator localIterator2 = ServiceLoader.loadInstalled(localClass).iterator();
          while (localIterator2.hasNext())
          {
            IIOServiceProvider localIIOServiceProvider = (IIOServiceProvider)localIterator2.next();
            IIORegistry.this.registerServiceProvider(localIIOServiceProvider);
          }
        }
        return this;
      }
    };
    AccessController.doPrivileged(local1);
  }
  
  static
  {
    initialCategories.add(ImageReaderSpi.class);
    initialCategories.add(ImageWriterSpi.class);
    initialCategories.add(ImageTranscoderSpi.class);
    initialCategories.add(ImageInputStreamSpi.class);
    initialCategories.add(ImageOutputStreamSpi.class);
  }
}
