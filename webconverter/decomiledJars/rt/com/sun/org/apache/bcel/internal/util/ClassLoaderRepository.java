package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ClassLoaderRepository
  implements Repository
{
  private ClassLoader loader;
  private HashMap loadedClasses = new HashMap();
  
  public ClassLoaderRepository(ClassLoader paramClassLoader)
  {
    this.loader = paramClassLoader;
  }
  
  public void storeClass(JavaClass paramJavaClass)
  {
    this.loadedClasses.put(paramJavaClass.getClassName(), paramJavaClass);
    paramJavaClass.setRepository(this);
  }
  
  public void removeClass(JavaClass paramJavaClass)
  {
    this.loadedClasses.remove(paramJavaClass.getClassName());
  }
  
  public JavaClass findClass(String paramString)
  {
    if (this.loadedClasses.containsKey(paramString)) {
      return (JavaClass)this.loadedClasses.get(paramString);
    }
    return null;
  }
  
  public JavaClass loadClass(String paramString)
    throws ClassNotFoundException
  {
    String str = paramString.replace('.', '/');
    JavaClass localJavaClass = findClass(paramString);
    if (localJavaClass != null) {
      return localJavaClass;
    }
    try
    {
      InputStream localInputStream = this.loader.getResourceAsStream(str + ".class");
      if (localInputStream == null) {
        throw new ClassNotFoundException(paramString + " not found.");
      }
      ClassParser localClassParser = new ClassParser(localInputStream, paramString);
      localJavaClass = localClassParser.parse();
      storeClass(localJavaClass);
      return localJavaClass;
    }
    catch (IOException localIOException)
    {
      throw new ClassNotFoundException(localIOException.toString());
    }
  }
  
  public JavaClass loadClass(Class paramClass)
    throws ClassNotFoundException
  {
    return loadClass(paramClass.getName());
  }
  
  public void clear()
  {
    this.loadedClasses.clear();
  }
}
