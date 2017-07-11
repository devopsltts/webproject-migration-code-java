package java.awt.event;

import java.security.AccessController;
import java.security.PrivilegedAction;

class NativeLibLoader
{
  NativeLibLoader() {}
  
  static void loadLibraries()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("awt");
        return null;
      }
    });
  }
}
