package sun.reflect;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class ClassDefiner
{
  static final Unsafe unsafe = ;
  
  ClassDefiner() {}
  
  static Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ClassLoader paramClassLoader)
  {
    ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        return new DelegatingClassLoader(this.val$parentClassLoader);
      }
    });
    return unsafe.defineClass(paramString, paramArrayOfByte, paramInt1, paramInt2, localClassLoader, null);
  }
}
