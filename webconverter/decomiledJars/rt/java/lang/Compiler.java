package java.lang;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class Compiler
{
  private Compiler() {}
  
  private static native void initialize();
  
  private static native void registerNatives();
  
  public static native boolean compileClass(Class<?> paramClass);
  
  public static native boolean compileClasses(String paramString);
  
  public static native Object command(Object paramObject);
  
  public static native void enable();
  
  public static native void disable();
  
  static
  {
    registerNatives();
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        int i = 0;
        String str1 = System.getProperty("java.compiler");
        if ((str1 != null) && (!str1.equals("NONE")) && (!str1.equals(""))) {
          try
          {
            System.loadLibrary(str1);
            Compiler.access$000();
            i = 1;
          }
          catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
          {
            System.err.println("Warning: JIT compiler \"" + str1 + "\" not found. Will use interpreter.");
          }
        }
        String str2 = System.getProperty("java.vm.info");
        if (i != 0) {
          System.setProperty("java.vm.info", str2 + ", " + str1);
        } else {
          System.setProperty("java.vm.info", str2 + ", nojit");
        }
        return null;
      }
    });
  }
}
