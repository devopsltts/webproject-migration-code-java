package sun.misc;

import java.net.InetAddress;
import java.net.URLClassLoader;

public abstract interface JavaNetAccess
{
  public abstract URLClassPath getURLClassPath(URLClassLoader paramURLClassLoader);
  
  public abstract String getOriginalHostName(InetAddress paramInetAddress);
}
