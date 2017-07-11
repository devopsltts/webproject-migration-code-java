package java.beans;

import com.sun.beans.finder.ClassFinder;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;

class ObjectInputStreamWithLoader
  extends ObjectInputStream
{
  private ClassLoader loader;
  
  public ObjectInputStreamWithLoader(InputStream paramInputStream, ClassLoader paramClassLoader)
    throws IOException, StreamCorruptedException
  {
    super(paramInputStream);
    if (paramClassLoader == null) {
      throw new IllegalArgumentException("Illegal null argument to ObjectInputStreamWithLoader");
    }
    this.loader = paramClassLoader;
  }
  
  protected Class resolveClass(ObjectStreamClass paramObjectStreamClass)
    throws IOException, ClassNotFoundException
  {
    String str = paramObjectStreamClass.getName();
    return ClassFinder.resolveClass(str, this.loader);
  }
}
