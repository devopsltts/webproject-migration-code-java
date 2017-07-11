package java.awt;

import java.awt.peer.DesktopPeer;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import sun.awt.AppContext;
import sun.awt.DesktopBrowse;
import sun.awt.SunToolkit;

public class Desktop
{
  private DesktopPeer peer = Toolkit.getDefaultToolkit().createDesktopPeer(this);
  
  private Desktop() {}
  
  public static synchronized Desktop getDesktop()
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    if (!isDesktopSupported()) {
      throw new UnsupportedOperationException("Desktop API is not supported on the current platform");
    }
    AppContext localAppContext = AppContext.getAppContext();
    Desktop localDesktop = (Desktop)localAppContext.get(Desktop.class);
    if (localDesktop == null)
    {
      localDesktop = new Desktop();
      localAppContext.put(Desktop.class, localDesktop);
    }
    return localDesktop;
  }
  
  public static boolean isDesktopSupported()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((localToolkit instanceof SunToolkit)) {
      return ((SunToolkit)localToolkit).isDesktopSupported();
    }
    return false;
  }
  
  public boolean isSupported(Action paramAction)
  {
    return this.peer.isSupported(paramAction);
  }
  
  private static void checkFileValidation(File paramFile)
  {
    if (paramFile == null) {
      throw new NullPointerException("File must not be null");
    }
    if (!paramFile.exists()) {
      throw new IllegalArgumentException("The file: " + paramFile.getPath() + " doesn't exist.");
    }
    paramFile.canRead();
  }
  
  private void checkActionSupport(Action paramAction)
  {
    if (!isSupported(paramAction)) {
      throw new UnsupportedOperationException("The " + paramAction.name() + " action is not supported on the current platform!");
    }
  }
  
  private void checkAWTPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new AWTPermission("showWindowWithoutWarningBanner"));
    }
  }
  
  public void open(File paramFile)
    throws IOException
  {
    checkAWTPermission();
    checkExec();
    checkActionSupport(Action.OPEN);
    checkFileValidation(paramFile);
    this.peer.open(paramFile);
  }
  
  public void edit(File paramFile)
    throws IOException
  {
    checkAWTPermission();
    checkExec();
    checkActionSupport(Action.EDIT);
    paramFile.canWrite();
    checkFileValidation(paramFile);
    this.peer.edit(paramFile);
  }
  
  public void print(File paramFile)
    throws IOException
  {
    checkExec();
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPrintJobAccess();
    }
    checkActionSupport(Action.PRINT);
    checkFileValidation(paramFile);
    this.peer.print(paramFile);
  }
  
  public void browse(URI paramURI)
    throws IOException
  {
    Object localObject = null;
    try
    {
      checkAWTPermission();
      checkExec();
    }
    catch (SecurityException localSecurityException)
    {
      localObject = localSecurityException;
    }
    checkActionSupport(Action.BROWSE);
    if (paramURI == null) {
      throw new NullPointerException();
    }
    if (localObject == null)
    {
      this.peer.browse(paramURI);
      return;
    }
    URL localURL = null;
    try
    {
      localURL = paramURI.toURL();
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new IllegalArgumentException("Unable to convert URI to URL", localMalformedURLException);
    }
    DesktopBrowse localDesktopBrowse = DesktopBrowse.getInstance();
    if (localDesktopBrowse == null) {
      throw localObject;
    }
    localDesktopBrowse.browse(localURL);
  }
  
  public void mail()
    throws IOException
  {
    checkAWTPermission();
    checkExec();
    checkActionSupport(Action.MAIL);
    URI localURI = null;
    try
    {
      localURI = new URI("mailto:?");
      this.peer.mail(localURI);
    }
    catch (URISyntaxException localURISyntaxException) {}
  }
  
  public void mail(URI paramURI)
    throws IOException
  {
    checkAWTPermission();
    checkExec();
    checkActionSupport(Action.MAIL);
    if (paramURI == null) {
      throw new NullPointerException();
    }
    if (!"mailto".equalsIgnoreCase(paramURI.getScheme())) {
      throw new IllegalArgumentException("URI scheme is not \"mailto\"");
    }
    this.peer.mail(paramURI);
  }
  
  private void checkExec()
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new FilePermission("<<ALL FILES>>", "execute"));
    }
  }
  
  public static enum Action
  {
    OPEN,  EDIT,  PRINT,  MAIL,  BROWSE;
    
    private Action() {}
  }
}
