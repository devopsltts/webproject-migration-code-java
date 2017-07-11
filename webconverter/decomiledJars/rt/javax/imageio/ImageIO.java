package javax.imageio;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ImageTranscoderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry.Filter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;

public final class ImageIO
{
  private static final IIORegistry theRegistry = ;
  private static Method readerFormatNamesMethod;
  private static Method readerFileSuffixesMethod;
  private static Method readerMIMETypesMethod;
  private static Method writerFormatNamesMethod;
  private static Method writerFileSuffixesMethod;
  private static Method writerMIMETypesMethod;
  
  private ImageIO() {}
  
  public static void scanForPlugins()
  {
    theRegistry.registerApplicationClasspathSpis();
  }
  
  private static synchronized CacheInfo getCacheInfo()
  {
    AppContext localAppContext = AppContext.getAppContext();
    CacheInfo localCacheInfo = (CacheInfo)localAppContext.get(CacheInfo.class);
    if (localCacheInfo == null)
    {
      localCacheInfo = new CacheInfo();
      localAppContext.put(CacheInfo.class, localCacheInfo);
    }
    return localCacheInfo;
  }
  
  private static String getTempDir()
  {
    GetPropertyAction localGetPropertyAction = new GetPropertyAction("java.io.tmpdir");
    return (String)AccessController.doPrivileged(localGetPropertyAction);
  }
  
  private static boolean hasCachePermission()
  {
    Boolean localBoolean = getCacheInfo().getHasPermission();
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    try
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        File localFile = getCacheDirectory();
        String str1;
        if (localFile != null)
        {
          str1 = localFile.getPath();
        }
        else
        {
          str1 = getTempDir();
          if ((str1 == null) || (str1.isEmpty()))
          {
            getCacheInfo().setHasPermission(Boolean.FALSE);
            return false;
          }
        }
        String str2 = str1;
        if (!str2.endsWith(File.separator)) {
          str2 = str2 + File.separator;
        }
        str2 = str2 + "*";
        localSecurityManager.checkPermission(new FilePermission(str2, "read, write, delete"));
      }
    }
    catch (SecurityException localSecurityException)
    {
      getCacheInfo().setHasPermission(Boolean.FALSE);
      return false;
    }
    getCacheInfo().setHasPermission(Boolean.TRUE);
    return true;
  }
  
  public static void setUseCache(boolean paramBoolean)
  {
    getCacheInfo().setUseCache(paramBoolean);
  }
  
  public static boolean getUseCache()
  {
    return getCacheInfo().getUseCache();
  }
  
  public static void setCacheDirectory(File paramFile)
  {
    if ((paramFile != null) && (!paramFile.isDirectory())) {
      throw new IllegalArgumentException("Not a directory!");
    }
    getCacheInfo().setCacheDirectory(paramFile);
    getCacheInfo().setHasPermission(null);
  }
  
  public static File getCacheDirectory()
  {
    return getCacheInfo().getCacheDirectory();
  }
  
  public static ImageInputStream createImageInputStream(Object paramObject)
    throws IOException
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("input == null!");
    }
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(ImageInputStreamSpi.class, true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return null;
    }
    boolean bool = (getUseCache()) && (hasCachePermission());
    while (localIterator.hasNext())
    {
      ImageInputStreamSpi localImageInputStreamSpi = (ImageInputStreamSpi)localIterator.next();
      if (localImageInputStreamSpi.getInputClass().isInstance(paramObject)) {
        try
        {
          return localImageInputStreamSpi.createInputStreamInstance(paramObject, bool, getCacheDirectory());
        }
        catch (IOException localIOException)
        {
          throw new IIOException("Can't create cache file!", localIOException);
        }
      }
    }
    return null;
  }
  
  public static ImageOutputStream createImageOutputStream(Object paramObject)
    throws IOException
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("output == null!");
    }
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(ImageOutputStreamSpi.class, true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return null;
    }
    boolean bool = (getUseCache()) && (hasCachePermission());
    while (localIterator.hasNext())
    {
      ImageOutputStreamSpi localImageOutputStreamSpi = (ImageOutputStreamSpi)localIterator.next();
      if (localImageOutputStreamSpi.getOutputClass().isInstance(paramObject)) {
        try
        {
          return localImageOutputStreamSpi.createOutputStreamInstance(paramObject, bool, getCacheDirectory());
        }
        catch (IOException localIOException)
        {
          throw new IIOException("Can't create cache file!", localIOException);
        }
      }
    }
    return null;
  }
  
  private static <S extends ImageReaderWriterSpi> String[] getReaderWriterInfo(Class<S> paramClass, SpiInfo paramSpiInfo)
  {
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(paramClass, true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return new String[0];
    }
    HashSet localHashSet = new HashSet();
    while (localIterator.hasNext())
    {
      ImageReaderWriterSpi localImageReaderWriterSpi = (ImageReaderWriterSpi)localIterator.next();
      Collections.addAll(localHashSet, paramSpiInfo.info(localImageReaderWriterSpi));
    }
    return (String[])localHashSet.toArray(new String[localHashSet.size()]);
  }
  
  public static String[] getReaderFormatNames()
  {
    return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.FORMAT_NAMES);
  }
  
  public static String[] getReaderMIMETypes()
  {
    return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.MIME_TYPES);
  }
  
  public static String[] getReaderFileSuffixes()
  {
    return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.FILE_SUFFIXES);
  }
  
  public static Iterator<ImageReader> getImageReaders(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("input == null!");
    }
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new CanDecodeInputFilter(paramObject), true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return Collections.emptyIterator();
    }
    return new ImageReaderIterator(localIterator);
  }
  
  public static Iterator<ImageReader> getImageReadersByFormatName(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("formatName == null!");
    }
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(readerFormatNamesMethod, paramString), true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return Collections.emptyIterator();
    }
    return new ImageReaderIterator(localIterator);
  }
  
  public static Iterator<ImageReader> getImageReadersBySuffix(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("fileSuffix == null!");
    }
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(readerFileSuffixesMethod, paramString), true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return Collections.emptyIterator();
    }
    return new ImageReaderIterator(localIterator);
  }
  
  public static Iterator<ImageReader> getImageReadersByMIMEType(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("MIMEType == null!");
    }
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(readerMIMETypesMethod, paramString), true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return Collections.emptyIterator();
    }
    return new ImageReaderIterator(localIterator);
  }
  
  public static String[] getWriterFormatNames()
  {
    return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.FORMAT_NAMES);
  }
  
  public static String[] getWriterMIMETypes()
  {
    return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.MIME_TYPES);
  }
  
  public static String[] getWriterFileSuffixes()
  {
    return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.FILE_SUFFIXES);
  }
  
  private static boolean contains(String[] paramArrayOfString, String paramString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramString.equalsIgnoreCase(paramArrayOfString[i])) {
        return true;
      }
    }
    return false;
  }
  
  public static Iterator<ImageWriter> getImageWritersByFormatName(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("formatName == null!");
    }
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(writerFormatNamesMethod, paramString), true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return Collections.emptyIterator();
    }
    return new ImageWriterIterator(localIterator);
  }
  
  public static Iterator<ImageWriter> getImageWritersBySuffix(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("fileSuffix == null!");
    }
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(writerFileSuffixesMethod, paramString), true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return Collections.emptyIterator();
    }
    return new ImageWriterIterator(localIterator);
  }
  
  public static Iterator<ImageWriter> getImageWritersByMIMEType(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("MIMEType == null!");
    }
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(writerMIMETypesMethod, paramString), true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return Collections.emptyIterator();
    }
    return new ImageWriterIterator(localIterator);
  }
  
  public static ImageWriter getImageWriter(ImageReader paramImageReader)
  {
    if (paramImageReader == null) {
      throw new IllegalArgumentException("reader == null!");
    }
    Object localObject1 = paramImageReader.getOriginatingProvider();
    if (localObject1 == null)
    {
      try
      {
        localObject2 = theRegistry.getServiceProviders(ImageReaderSpi.class, false);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        return null;
      }
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (ImageReaderSpi)((Iterator)localObject2).next();
        if (((ImageReaderSpi)localObject3).isOwnReader(paramImageReader))
        {
          localObject1 = localObject3;
          break;
        }
      }
      if (localObject1 == null) {
        return null;
      }
    }
    Object localObject2 = ((ImageReaderSpi)localObject1).getImageWriterSpiNames();
    if (localObject2 == null) {
      return null;
    }
    Object localObject3 = null;
    try
    {
      localObject3 = Class.forName(localObject2[0], true, ClassLoader.getSystemClassLoader());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      return null;
    }
    ImageWriterSpi localImageWriterSpi = (ImageWriterSpi)theRegistry.getServiceProviderByClass((Class)localObject3);
    if (localImageWriterSpi == null) {
      return null;
    }
    try
    {
      return localImageWriterSpi.createWriterInstance();
    }
    catch (IOException localIOException)
    {
      theRegistry.deregisterServiceProvider(localImageWriterSpi, ImageWriterSpi.class);
    }
    return null;
  }
  
  public static ImageReader getImageReader(ImageWriter paramImageWriter)
  {
    if (paramImageWriter == null) {
      throw new IllegalArgumentException("writer == null!");
    }
    Object localObject1 = paramImageWriter.getOriginatingProvider();
    if (localObject1 == null)
    {
      try
      {
        localObject2 = theRegistry.getServiceProviders(ImageWriterSpi.class, false);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        return null;
      }
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (ImageWriterSpi)((Iterator)localObject2).next();
        if (((ImageWriterSpi)localObject3).isOwnWriter(paramImageWriter))
        {
          localObject1 = localObject3;
          break;
        }
      }
      if (localObject1 == null) {
        return null;
      }
    }
    Object localObject2 = ((ImageWriterSpi)localObject1).getImageReaderSpiNames();
    if (localObject2 == null) {
      return null;
    }
    Object localObject3 = null;
    try
    {
      localObject3 = Class.forName(localObject2[0], true, ClassLoader.getSystemClassLoader());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      return null;
    }
    ImageReaderSpi localImageReaderSpi = (ImageReaderSpi)theRegistry.getServiceProviderByClass((Class)localObject3);
    if (localImageReaderSpi == null) {
      return null;
    }
    try
    {
      return localImageReaderSpi.createReaderInstance();
    }
    catch (IOException localIOException)
    {
      theRegistry.deregisterServiceProvider(localImageReaderSpi, ImageReaderSpi.class);
    }
    return null;
  }
  
  public static Iterator<ImageWriter> getImageWriters(ImageTypeSpecifier paramImageTypeSpecifier, String paramString)
  {
    if (paramImageTypeSpecifier == null) {
      throw new IllegalArgumentException("type == null!");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("formatName == null!");
    }
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new CanEncodeImageAndFormatFilter(paramImageTypeSpecifier, paramString), true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return Collections.emptyIterator();
    }
    return new ImageWriterIterator(localIterator);
  }
  
  public static Iterator<ImageTranscoder> getImageTranscoders(ImageReader paramImageReader, ImageWriter paramImageWriter)
  {
    if (paramImageReader == null) {
      throw new IllegalArgumentException("reader == null!");
    }
    if (paramImageWriter == null) {
      throw new IllegalArgumentException("writer == null!");
    }
    ImageReaderSpi localImageReaderSpi = paramImageReader.getOriginatingProvider();
    ImageWriterSpi localImageWriterSpi = paramImageWriter.getOriginatingProvider();
    TranscoderFilter localTranscoderFilter = new TranscoderFilter(localImageReaderSpi, localImageWriterSpi);
    Iterator localIterator;
    try
    {
      localIterator = theRegistry.getServiceProviders(ImageTranscoderSpi.class, localTranscoderFilter, true);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return Collections.emptyIterator();
    }
    return new ImageTranscoderIterator(localIterator);
  }
  
  public static BufferedImage read(File paramFile)
    throws IOException
  {
    if (paramFile == null) {
      throw new IllegalArgumentException("input == null!");
    }
    if (!paramFile.canRead()) {
      throw new IIOException("Can't read input file!");
    }
    ImageInputStream localImageInputStream = createImageInputStream(paramFile);
    if (localImageInputStream == null) {
      throw new IIOException("Can't create an ImageInputStream!");
    }
    BufferedImage localBufferedImage = read(localImageInputStream);
    if (localBufferedImage == null) {
      localImageInputStream.close();
    }
    return localBufferedImage;
  }
  
  public static BufferedImage read(InputStream paramInputStream)
    throws IOException
  {
    if (paramInputStream == null) {
      throw new IllegalArgumentException("input == null!");
    }
    ImageInputStream localImageInputStream = createImageInputStream(paramInputStream);
    BufferedImage localBufferedImage = read(localImageInputStream);
    if (localBufferedImage == null) {
      localImageInputStream.close();
    }
    return localBufferedImage;
  }
  
  public static BufferedImage read(URL paramURL)
    throws IOException
  {
    if (paramURL == null) {
      throw new IllegalArgumentException("input == null!");
    }
    InputStream localInputStream = null;
    try
    {
      localInputStream = paramURL.openStream();
    }
    catch (IOException localIOException)
    {
      throw new IIOException("Can't get input stream from URL!", localIOException);
    }
    ImageInputStream localImageInputStream = createImageInputStream(localInputStream);
    BufferedImage localBufferedImage;
    try
    {
      localBufferedImage = read(localImageInputStream);
      if (localBufferedImage == null) {
        localImageInputStream.close();
      }
    }
    finally
    {
      localInputStream.close();
    }
    return localBufferedImage;
  }
  
  public static BufferedImage read(ImageInputStream paramImageInputStream)
    throws IOException
  {
    if (paramImageInputStream == null) {
      throw new IllegalArgumentException("stream == null!");
    }
    Iterator localIterator = getImageReaders(paramImageInputStream);
    if (!localIterator.hasNext()) {
      return null;
    }
    ImageReader localImageReader = (ImageReader)localIterator.next();
    ImageReadParam localImageReadParam = localImageReader.getDefaultReadParam();
    localImageReader.setInput(paramImageInputStream, true, true);
    BufferedImage localBufferedImage;
    try
    {
      localBufferedImage = localImageReader.read(0, localImageReadParam);
    }
    finally
    {
      localImageReader.dispose();
      paramImageInputStream.close();
    }
    return localBufferedImage;
  }
  
  public static boolean write(RenderedImage paramRenderedImage, String paramString, ImageOutputStream paramImageOutputStream)
    throws IOException
  {
    if (paramRenderedImage == null) {
      throw new IllegalArgumentException("im == null!");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("formatName == null!");
    }
    if (paramImageOutputStream == null) {
      throw new IllegalArgumentException("output == null!");
    }
    return doWrite(paramRenderedImage, getWriter(paramRenderedImage, paramString), paramImageOutputStream);
  }
  
  public static boolean write(RenderedImage paramRenderedImage, String paramString, File paramFile)
    throws IOException
  {
    if (paramFile == null) {
      throw new IllegalArgumentException("output == null!");
    }
    ImageOutputStream localImageOutputStream = null;
    ImageWriter localImageWriter = getWriter(paramRenderedImage, paramString);
    if (localImageWriter == null) {
      return false;
    }
    try
    {
      paramFile.delete();
      localImageOutputStream = createImageOutputStream(paramFile);
    }
    catch (IOException localIOException)
    {
      throw new IIOException("Can't create output stream!", localIOException);
    }
    try
    {
      boolean bool = doWrite(paramRenderedImage, localImageWriter, localImageOutputStream);
      return bool;
    }
    finally
    {
      localImageOutputStream.close();
    }
  }
  
  public static boolean write(RenderedImage paramRenderedImage, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if (paramOutputStream == null) {
      throw new IllegalArgumentException("output == null!");
    }
    ImageOutputStream localImageOutputStream = null;
    try
    {
      localImageOutputStream = createImageOutputStream(paramOutputStream);
    }
    catch (IOException localIOException)
    {
      throw new IIOException("Can't create output stream!", localIOException);
    }
    try
    {
      boolean bool = doWrite(paramRenderedImage, getWriter(paramRenderedImage, paramString), localImageOutputStream);
      return bool;
    }
    finally
    {
      localImageOutputStream.close();
    }
  }
  
  private static ImageWriter getWriter(RenderedImage paramRenderedImage, String paramString)
  {
    ImageTypeSpecifier localImageTypeSpecifier = ImageTypeSpecifier.createFromRenderedImage(paramRenderedImage);
    Iterator localIterator = getImageWriters(localImageTypeSpecifier, paramString);
    if (localIterator.hasNext()) {
      return (ImageWriter)localIterator.next();
    }
    return null;
  }
  
  private static boolean doWrite(RenderedImage paramRenderedImage, ImageWriter paramImageWriter, ImageOutputStream paramImageOutputStream)
    throws IOException
  {
    if (paramImageWriter == null) {
      return false;
    }
    paramImageWriter.setOutput(paramImageOutputStream);
    try
    {
      paramImageWriter.write(paramRenderedImage);
      paramImageWriter.dispose();
      paramImageOutputStream.flush();
    }
    finally
    {
      paramImageWriter.dispose();
      paramImageOutputStream.flush();
    }
    return true;
  }
  
  static
  {
    try
    {
      readerFormatNamesMethod = ImageReaderSpi.class.getMethod("getFormatNames", new Class[0]);
      readerFileSuffixesMethod = ImageReaderSpi.class.getMethod("getFileSuffixes", new Class[0]);
      readerMIMETypesMethod = ImageReaderSpi.class.getMethod("getMIMETypes", new Class[0]);
      writerFormatNamesMethod = ImageWriterSpi.class.getMethod("getFormatNames", new Class[0]);
      writerFileSuffixesMethod = ImageWriterSpi.class.getMethod("getFileSuffixes", new Class[0]);
      writerMIMETypesMethod = ImageWriterSpi.class.getMethod("getMIMETypes", new Class[0]);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      localNoSuchMethodException.printStackTrace();
    }
  }
  
  static class CacheInfo
  {
    boolean useCache = true;
    File cacheDirectory = null;
    Boolean hasPermission = null;
    
    public CacheInfo() {}
    
    public boolean getUseCache()
    {
      return this.useCache;
    }
    
    public void setUseCache(boolean paramBoolean)
    {
      this.useCache = paramBoolean;
    }
    
    public File getCacheDirectory()
    {
      return this.cacheDirectory;
    }
    
    public void setCacheDirectory(File paramFile)
    {
      this.cacheDirectory = paramFile;
    }
    
    public Boolean getHasPermission()
    {
      return this.hasPermission;
    }
    
    public void setHasPermission(Boolean paramBoolean)
    {
      this.hasPermission = paramBoolean;
    }
  }
  
  static class CanDecodeInputFilter
    implements ServiceRegistry.Filter
  {
    Object input;
    
    public CanDecodeInputFilter(Object paramObject)
    {
      this.input = paramObject;
    }
    
    public boolean filter(Object paramObject)
    {
      try
      {
        ImageReaderSpi localImageReaderSpi = (ImageReaderSpi)paramObject;
        ImageInputStream localImageInputStream = null;
        if ((this.input instanceof ImageInputStream)) {
          localImageInputStream = (ImageInputStream)this.input;
        }
        boolean bool = false;
        if (localImageInputStream != null) {
          localImageInputStream.mark();
        }
        bool = localImageReaderSpi.canDecodeInput(this.input);
        if (localImageInputStream != null) {
          localImageInputStream.reset();
        }
        return bool;
      }
      catch (IOException localIOException) {}
      return false;
    }
  }
  
  static class CanEncodeImageAndFormatFilter
    implements ServiceRegistry.Filter
  {
    ImageTypeSpecifier type;
    String formatName;
    
    public CanEncodeImageAndFormatFilter(ImageTypeSpecifier paramImageTypeSpecifier, String paramString)
    {
      this.type = paramImageTypeSpecifier;
      this.formatName = paramString;
    }
    
    public boolean filter(Object paramObject)
    {
      ImageWriterSpi localImageWriterSpi = (ImageWriterSpi)paramObject;
      return (Arrays.asList(localImageWriterSpi.getFormatNames()).contains(this.formatName)) && (localImageWriterSpi.canEncodeImage(this.type));
    }
  }
  
  static class ContainsFilter
    implements ServiceRegistry.Filter
  {
    Method method;
    String name;
    
    public ContainsFilter(Method paramMethod, String paramString)
    {
      this.method = paramMethod;
      this.name = paramString;
    }
    
    public boolean filter(Object paramObject)
    {
      try
      {
        return ImageIO.contains((String[])this.method.invoke(paramObject, new Object[0]), this.name);
      }
      catch (Exception localException) {}
      return false;
    }
  }
  
  static class ImageReaderIterator
    implements Iterator<ImageReader>
  {
    public Iterator iter;
    
    public ImageReaderIterator(Iterator paramIterator)
    {
      this.iter = paramIterator;
    }
    
    public boolean hasNext()
    {
      return this.iter.hasNext();
    }
    
    public ImageReader next()
    {
      ImageReaderSpi localImageReaderSpi = null;
      try
      {
        localImageReaderSpi = (ImageReaderSpi)this.iter.next();
        return localImageReaderSpi.createReaderInstance();
      }
      catch (IOException localIOException)
      {
        ImageIO.theRegistry.deregisterServiceProvider(localImageReaderSpi, ImageReaderSpi.class);
      }
      return null;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  static class ImageTranscoderIterator
    implements Iterator<ImageTranscoder>
  {
    public Iterator iter;
    
    public ImageTranscoderIterator(Iterator paramIterator)
    {
      this.iter = paramIterator;
    }
    
    public boolean hasNext()
    {
      return this.iter.hasNext();
    }
    
    public ImageTranscoder next()
    {
      ImageTranscoderSpi localImageTranscoderSpi = null;
      localImageTranscoderSpi = (ImageTranscoderSpi)this.iter.next();
      return localImageTranscoderSpi.createTranscoderInstance();
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  static class ImageWriterIterator
    implements Iterator<ImageWriter>
  {
    public Iterator iter;
    
    public ImageWriterIterator(Iterator paramIterator)
    {
      this.iter = paramIterator;
    }
    
    public boolean hasNext()
    {
      return this.iter.hasNext();
    }
    
    public ImageWriter next()
    {
      ImageWriterSpi localImageWriterSpi = null;
      try
      {
        localImageWriterSpi = (ImageWriterSpi)this.iter.next();
        return localImageWriterSpi.createWriterInstance();
      }
      catch (IOException localIOException)
      {
        ImageIO.theRegistry.deregisterServiceProvider(localImageWriterSpi, ImageWriterSpi.class);
      }
      return null;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  private static abstract enum SpiInfo
  {
    FORMAT_NAMES,  MIME_TYPES,  FILE_SUFFIXES;
    
    private SpiInfo() {}
    
    abstract String[] info(ImageReaderWriterSpi paramImageReaderWriterSpi);
  }
  
  static class TranscoderFilter
    implements ServiceRegistry.Filter
  {
    String readerSpiName;
    String writerSpiName;
    
    public TranscoderFilter(ImageReaderSpi paramImageReaderSpi, ImageWriterSpi paramImageWriterSpi)
    {
      this.readerSpiName = paramImageReaderSpi.getClass().getName();
      this.writerSpiName = paramImageWriterSpi.getClass().getName();
    }
    
    public boolean filter(Object paramObject)
    {
      ImageTranscoderSpi localImageTranscoderSpi = (ImageTranscoderSpi)paramObject;
      String str1 = localImageTranscoderSpi.getReaderServiceProviderName();
      String str2 = localImageTranscoderSpi.getWriterServiceProviderName();
      return (str1.equals(this.readerSpiName)) && (str2.equals(this.writerSpiName));
    }
  }
}
