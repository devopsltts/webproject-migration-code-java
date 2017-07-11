package java.beans;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

class BeansAppletContext
  implements AppletContext
{
  Applet target;
  Hashtable<URL, Object> imageCache = new Hashtable();
  
  BeansAppletContext(Applet paramApplet)
  {
    this.target = paramApplet;
  }
  
  public AudioClip getAudioClip(URL paramURL)
  {
    try
    {
      return (AudioClip)paramURL.getContent();
    }
    catch (Exception localException) {}
    return null;
  }
  
  public synchronized Image getImage(URL paramURL)
  {
    Object localObject = this.imageCache.get(paramURL);
    if (localObject != null) {
      return (Image)localObject;
    }
    try
    {
      localObject = paramURL.getContent();
      if (localObject == null) {
        return null;
      }
      if ((localObject instanceof Image))
      {
        this.imageCache.put(paramURL, localObject);
        return (Image)localObject;
      }
      Image localImage = this.target.createImage((ImageProducer)localObject);
      this.imageCache.put(paramURL, localImage);
      return localImage;
    }
    catch (Exception localException) {}
    return null;
  }
  
  public Applet getApplet(String paramString)
  {
    return null;
  }
  
  public Enumeration<Applet> getApplets()
  {
    Vector localVector = new Vector();
    localVector.addElement(this.target);
    return localVector.elements();
  }
  
  public void showDocument(URL paramURL) {}
  
  public void showDocument(URL paramURL, String paramString) {}
  
  public void showStatus(String paramString) {}
  
  public void setStream(String paramString, InputStream paramInputStream)
    throws IOException
  {}
  
  public InputStream getStream(String paramString)
  {
    return null;
  }
  
  public Iterator<String> getStreamKeys()
  {
    return null;
  }
}
