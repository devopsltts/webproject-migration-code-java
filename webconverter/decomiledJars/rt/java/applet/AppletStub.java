package java.applet;

import java.net.URL;

public abstract interface AppletStub
{
  public abstract boolean isActive();
  
  public abstract URL getDocumentBase();
  
  public abstract URL getCodeBase();
  
  public abstract String getParameter(String paramString);
  
  public abstract AppletContext getAppletContext();
  
  public abstract void appletResize(int paramInt1, int paramInt2);
}
