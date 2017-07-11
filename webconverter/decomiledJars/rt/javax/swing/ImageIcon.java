package javax.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.beans.ConstructorProperties;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AppContext;

public class ImageIcon
  implements Icon, Serializable, Accessible
{
  private transient String filename;
  private transient URL location;
  transient Image image;
  transient int loadStatus = 0;
  ImageObserver imageObserver;
  String description = null;
  @Deprecated
  protected static final Component component = (Component)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Component run()
    {
      try
      {
        Component localComponent = ImageIcon.access$000();
        Field localField = Component.class.getDeclaredField("appContext");
        localField.setAccessible(true);
        localField.set(localComponent, null);
        return localComponent;
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
      }
      return null;
    }
  });
  @Deprecated
  protected static final MediaTracker tracker = new MediaTracker(component);
  private static int mediaTrackerID;
  private static final Object TRACKER_KEY = new StringBuilder("TRACKER_KEY");
  int width = -1;
  int height = -1;
  private AccessibleImageIcon accessibleContext = null;
  
  private static Component createNoPermsComponent()
  {
    (Component)AccessController.doPrivileged(new PrivilegedAction()new AccessControlContextnew ProtectionDomain
    {
      public Component run()
      {
        new Component() {};
      }
    }, new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) }));
  }
  
  public ImageIcon(String paramString1, String paramString2)
  {
    this.image = Toolkit.getDefaultToolkit().getImage(paramString1);
    if (this.image == null) {
      return;
    }
    this.filename = paramString1;
    this.description = paramString2;
    loadImage(this.image);
  }
  
  @ConstructorProperties({"description"})
  public ImageIcon(String paramString)
  {
    this(paramString, paramString);
  }
  
  public ImageIcon(URL paramURL, String paramString)
  {
    this.image = Toolkit.getDefaultToolkit().getImage(paramURL);
    if (this.image == null) {
      return;
    }
    this.location = paramURL;
    this.description = paramString;
    loadImage(this.image);
  }
  
  public ImageIcon(URL paramURL)
  {
    this(paramURL, paramURL.toExternalForm());
  }
  
  public ImageIcon(Image paramImage, String paramString)
  {
    this(paramImage);
    this.description = paramString;
  }
  
  public ImageIcon(Image paramImage)
  {
    this.image = paramImage;
    Object localObject = paramImage.getProperty("comment", this.imageObserver);
    if ((localObject instanceof String)) {
      this.description = ((String)localObject);
    }
    loadImage(paramImage);
  }
  
  public ImageIcon(byte[] paramArrayOfByte, String paramString)
  {
    this.image = Toolkit.getDefaultToolkit().createImage(paramArrayOfByte);
    if (this.image == null) {
      return;
    }
    this.description = paramString;
    loadImage(this.image);
  }
  
  public ImageIcon(byte[] paramArrayOfByte)
  {
    this.image = Toolkit.getDefaultToolkit().createImage(paramArrayOfByte);
    if (this.image == null) {
      return;
    }
    Object localObject = this.image.getProperty("comment", this.imageObserver);
    if ((localObject instanceof String)) {
      this.description = ((String)localObject);
    }
    loadImage(this.image);
  }
  
  public ImageIcon() {}
  
  protected void loadImage(Image paramImage)
  {
    MediaTracker localMediaTracker = getTracker();
    synchronized (localMediaTracker)
    {
      int i = getNextID();
      localMediaTracker.addImage(paramImage, i);
      try
      {
        localMediaTracker.waitForID(i, 0L);
      }
      catch (InterruptedException localInterruptedException)
      {
        System.out.println("INTERRUPTED while loading Image");
      }
      this.loadStatus = localMediaTracker.statusID(i, false);
      localMediaTracker.removeImage(paramImage, i);
      this.width = paramImage.getWidth(this.imageObserver);
      this.height = paramImage.getHeight(this.imageObserver);
    }
  }
  
  private int getNextID()
  {
    synchronized (getTracker())
    {
      return ++mediaTrackerID;
    }
  }
  
  private MediaTracker getTracker()
  {
    AppContext localAppContext = AppContext.getAppContext();
    Object localObject1;
    synchronized (localAppContext)
    {
      localObject1 = localAppContext.get(TRACKER_KEY);
      if (localObject1 == null)
      {
        Component local3 = new Component() {};
        localObject1 = new MediaTracker(local3);
        localAppContext.put(TRACKER_KEY, localObject1);
      }
    }
    return (MediaTracker)localObject1;
  }
  
  public int getImageLoadStatus()
  {
    return this.loadStatus;
  }
  
  @Transient
  public Image getImage()
  {
    return this.image;
  }
  
  public void setImage(Image paramImage)
  {
    this.image = paramImage;
    loadImage(paramImage);
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String paramString)
  {
    this.description = paramString;
  }
  
  public synchronized void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    if (this.imageObserver == null) {
      paramGraphics.drawImage(this.image, paramInt1, paramInt2, paramComponent);
    } else {
      paramGraphics.drawImage(this.image, paramInt1, paramInt2, this.imageObserver);
    }
  }
  
  public int getIconWidth()
  {
    return this.width;
  }
  
  public int getIconHeight()
  {
    return this.height;
  }
  
  public void setImageObserver(ImageObserver paramImageObserver)
  {
    this.imageObserver = paramImageObserver;
  }
  
  @Transient
  public ImageObserver getImageObserver()
  {
    return this.imageObserver;
  }
  
  public String toString()
  {
    if (this.description != null) {
      return this.description;
    }
    return super.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    int j = paramObjectInputStream.readInt();
    int[] arrayOfInt = (int[])paramObjectInputStream.readObject();
    if (arrayOfInt != null)
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      ColorModel localColorModel = ColorModel.getRGBdefault();
      this.image = localToolkit.createImage(new MemoryImageSource(i, j, localColorModel, arrayOfInt, 0, i));
      loadImage(this.image);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    int i = getIconWidth();
    int j = getIconHeight();
    int[] arrayOfInt = this.image != null ? new int[i * j] : null;
    if (this.image != null) {
      try
      {
        PixelGrabber localPixelGrabber = new PixelGrabber(this.image, 0, 0, i, j, arrayOfInt, 0, i);
        localPixelGrabber.grabPixels();
        if ((localPixelGrabber.getStatus() & 0x80) != 0) {
          throw new IOException("failed to load image contents");
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new IOException("image load interrupted");
      }
    }
    paramObjectOutputStream.writeInt(i);
    paramObjectOutputStream.writeInt(j);
    paramObjectOutputStream.writeObject(arrayOfInt);
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (this.accessibleContext == null) {
      this.accessibleContext = new AccessibleImageIcon();
    }
    return this.accessibleContext;
  }
  
  protected class AccessibleImageIcon
    extends AccessibleContext
    implements AccessibleIcon, Serializable
  {
    protected AccessibleImageIcon() {}
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.ICON;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      return null;
    }
    
    public Accessible getAccessibleParent()
    {
      return null;
    }
    
    public int getAccessibleIndexInParent()
    {
      return -1;
    }
    
    public int getAccessibleChildrenCount()
    {
      return 0;
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      return null;
    }
    
    public Locale getLocale()
      throws IllegalComponentStateException
    {
      return null;
    }
    
    public String getAccessibleIconDescription()
    {
      return ImageIcon.this.getDescription();
    }
    
    public void setAccessibleIconDescription(String paramString)
    {
      ImageIcon.this.setDescription(paramString);
    }
    
    public int getAccessibleIconHeight()
    {
      return ImageIcon.this.height;
    }
    
    public int getAccessibleIconWidth()
    {
      return ImageIcon.this.width;
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws ClassNotFoundException, IOException
    {
      paramObjectInputStream.defaultReadObject();
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
    }
  }
}
