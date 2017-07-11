package javax.management;

@Deprecated
public class DefaultLoaderRepository
{
  public DefaultLoaderRepository() {}
  
  public static Class<?> loadClass(String paramString)
    throws ClassNotFoundException
  {
    return javax.management.loading.DefaultLoaderRepository.loadClass(paramString);
  }
  
  public static Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException
  {
    return javax.management.loading.DefaultLoaderRepository.loadClassWithout(paramClassLoader, paramString);
  }
}
