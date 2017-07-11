package java.lang;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;
import sun.net.www.ParseUtil;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class Package
  implements AnnotatedElement
{
  private static Map<String, Package> pkgs = new HashMap(31);
  private static Map<String, URL> urls = new HashMap(10);
  private static Map<String, Manifest> mans = new HashMap(10);
  private final String pkgName;
  private final String specTitle;
  private final String specVersion;
  private final String specVendor;
  private final String implTitle;
  private final String implVersion;
  private final String implVendor;
  private final URL sealBase;
  private final transient ClassLoader loader;
  private transient Class<?> packageInfo;
  
  public String getName()
  {
    return this.pkgName;
  }
  
  public String getSpecificationTitle()
  {
    return this.specTitle;
  }
  
  public String getSpecificationVersion()
  {
    return this.specVersion;
  }
  
  public String getSpecificationVendor()
  {
    return this.specVendor;
  }
  
  public String getImplementationTitle()
  {
    return this.implTitle;
  }
  
  public String getImplementationVersion()
  {
    return this.implVersion;
  }
  
  public String getImplementationVendor()
  {
    return this.implVendor;
  }
  
  public boolean isSealed()
  {
    return this.sealBase != null;
  }
  
  public boolean isSealed(URL paramURL)
  {
    return paramURL.equals(this.sealBase);
  }
  
  public boolean isCompatibleWith(String paramString)
    throws NumberFormatException
  {
    if ((this.specVersion == null) || (this.specVersion.length() < 1)) {
      throw new NumberFormatException("Empty version string");
    }
    String[] arrayOfString1 = this.specVersion.split("\\.", -1);
    int[] arrayOfInt1 = new int[arrayOfString1.length];
    for (int i = 0; i < arrayOfString1.length; i++)
    {
      arrayOfInt1[i] = Integer.parseInt(arrayOfString1[i]);
      if (arrayOfInt1[i] < 0) {
        throw NumberFormatException.forInputString("" + arrayOfInt1[i]);
      }
    }
    String[] arrayOfString2 = paramString.split("\\.", -1);
    int[] arrayOfInt2 = new int[arrayOfString2.length];
    for (int j = 0; j < arrayOfString2.length; j++)
    {
      arrayOfInt2[j] = Integer.parseInt(arrayOfString2[j]);
      if (arrayOfInt2[j] < 0) {
        throw NumberFormatException.forInputString("" + arrayOfInt2[j]);
      }
    }
    j = Math.max(arrayOfInt2.length, arrayOfInt1.length);
    for (int k = 0; k < j; k++)
    {
      int m = k < arrayOfInt2.length ? arrayOfInt2[k] : 0;
      int n = k < arrayOfInt1.length ? arrayOfInt1[k] : 0;
      if (n < m) {
        return false;
      }
      if (n > m) {
        return true;
      }
    }
    return true;
  }
  
  @CallerSensitive
  public static Package getPackage(String paramString)
  {
    ClassLoader localClassLoader = ClassLoader.getClassLoader(Reflection.getCallerClass());
    if (localClassLoader != null) {
      return localClassLoader.getPackage(paramString);
    }
    return getSystemPackage(paramString);
  }
  
  @CallerSensitive
  public static Package[] getPackages()
  {
    ClassLoader localClassLoader = ClassLoader.getClassLoader(Reflection.getCallerClass());
    if (localClassLoader != null) {
      return localClassLoader.getPackages();
    }
    return getSystemPackages();
  }
  
  static Package getPackage(Class<?> paramClass)
  {
    String str = paramClass.getName();
    int i = str.lastIndexOf('.');
    if (i != -1)
    {
      str = str.substring(0, i);
      ClassLoader localClassLoader = paramClass.getClassLoader();
      if (localClassLoader != null) {
        return localClassLoader.getPackage(str);
      }
      return getSystemPackage(str);
    }
    return null;
  }
  
  public int hashCode()
  {
    return this.pkgName.hashCode();
  }
  
  public String toString()
  {
    String str1 = this.specTitle;
    String str2 = this.specVersion;
    if ((str1 != null) && (str1.length() > 0)) {
      str1 = ", " + str1;
    } else {
      str1 = "";
    }
    if ((str2 != null) && (str2.length() > 0)) {
      str2 = ", version " + str2;
    } else {
      str2 = "";
    }
    return "package " + this.pkgName + str1 + str2;
  }
  
  private Class<?> getPackageInfo()
  {
    if (this.packageInfo == null) {
      try
      {
        this.packageInfo = Class.forName(this.pkgName + ".package-info", false, this.loader);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        this.packageInfo = 1PackageInfoProxy.class;
      }
    }
    return this.packageInfo;
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> paramClass)
  {
    return getPackageInfo().getAnnotation(paramClass);
  }
  
  public boolean isAnnotationPresent(Class<? extends Annotation> paramClass)
  {
    return super.isAnnotationPresent(paramClass);
  }
  
  public <A extends Annotation> A[] getAnnotationsByType(Class<A> paramClass)
  {
    return getPackageInfo().getAnnotationsByType(paramClass);
  }
  
  public Annotation[] getAnnotations()
  {
    return getPackageInfo().getAnnotations();
  }
  
  public <A extends Annotation> A getDeclaredAnnotation(Class<A> paramClass)
  {
    return getPackageInfo().getDeclaredAnnotation(paramClass);
  }
  
  public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> paramClass)
  {
    return getPackageInfo().getDeclaredAnnotationsByType(paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations()
  {
    return getPackageInfo().getDeclaredAnnotations();
  }
  
  Package(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, URL paramURL, ClassLoader paramClassLoader)
  {
    this.pkgName = paramString1;
    this.implTitle = paramString5;
    this.implVersion = paramString6;
    this.implVendor = paramString7;
    this.specTitle = paramString2;
    this.specVersion = paramString3;
    this.specVendor = paramString4;
    this.sealBase = paramURL;
    this.loader = paramClassLoader;
  }
  
  private Package(String paramString, Manifest paramManifest, URL paramURL, ClassLoader paramClassLoader)
  {
    String str1 = paramString.replace('.', '/').concat("/");
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    String str6 = null;
    String str7 = null;
    String str8 = null;
    URL localURL = null;
    Attributes localAttributes = paramManifest.getAttributes(str1);
    if (localAttributes != null)
    {
      str3 = localAttributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
      str4 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
      str5 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
      str6 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
      str7 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
      str8 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
      str2 = localAttributes.getValue(Attributes.Name.SEALED);
    }
    localAttributes = paramManifest.getMainAttributes();
    if (localAttributes != null)
    {
      if (str3 == null) {
        str3 = localAttributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
      }
      if (str4 == null) {
        str4 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
      }
      if (str5 == null) {
        str5 = localAttributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
      }
      if (str6 == null) {
        str6 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
      }
      if (str7 == null) {
        str7 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
      }
      if (str8 == null) {
        str8 = localAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
      }
      if (str2 == null) {
        str2 = localAttributes.getValue(Attributes.Name.SEALED);
      }
    }
    if ("true".equalsIgnoreCase(str2)) {
      localURL = paramURL;
    }
    this.pkgName = paramString;
    this.specTitle = str3;
    this.specVersion = str4;
    this.specVendor = str5;
    this.implTitle = str6;
    this.implVersion = str7;
    this.implVendor = str8;
    this.sealBase = localURL;
    this.loader = paramClassLoader;
  }
  
  static Package getSystemPackage(String paramString)
  {
    synchronized (pkgs)
    {
      Package localPackage = (Package)pkgs.get(paramString);
      if (localPackage == null)
      {
        paramString = paramString.replace('.', '/').concat("/");
        String str = getSystemPackage0(paramString);
        if (str != null) {
          localPackage = defineSystemPackage(paramString, str);
        }
      }
      return localPackage;
    }
  }
  
  static Package[] getSystemPackages()
  {
    String[] arrayOfString = getSystemPackages0();
    synchronized (pkgs)
    {
      for (int i = 0; i < arrayOfString.length; i++) {
        defineSystemPackage(arrayOfString[i], getSystemPackage0(arrayOfString[i]));
      }
      return (Package[])pkgs.values().toArray(new Package[pkgs.size()]);
    }
  }
  
  private static Package defineSystemPackage(String paramString1, final String paramString2)
  {
    (Package)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Package run()
      {
        String str = this.val$iname;
        URL localURL = (URL)Package.urls.get(paramString2);
        Object localObject;
        if (localURL == null)
        {
          localObject = new File(paramString2);
          try
          {
            localURL = ParseUtil.fileToEncodedURL((File)localObject);
          }
          catch (MalformedURLException localMalformedURLException) {}
          if (localURL != null)
          {
            Package.urls.put(paramString2, localURL);
            if (((File)localObject).isFile()) {
              Package.mans.put(paramString2, Package.loadManifest(paramString2));
            }
          }
        }
        str = str.substring(0, str.length() - 1).replace('/', '.');
        Manifest localManifest = (Manifest)Package.mans.get(paramString2);
        if (localManifest != null) {
          localObject = new Package(str, localManifest, localURL, null, null);
        } else {
          localObject = new Package(str, null, null, null, null, null, null, null, null);
        }
        Package.pkgs.put(str, localObject);
        return localObject;
      }
    });
  }
  
  /* Error */
  private static Manifest loadManifest(String paramString)
  {
    // Byte code:
    //   0: new 157	java/io/FileInputStream
    //   3: dup
    //   4: aload_0
    //   5: invokespecial 322	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   8: astore_1
    //   9: aconst_null
    //   10: astore_2
    //   11: new 179	java/util/jar/JarInputStream
    //   14: dup
    //   15: aload_1
    //   16: iconst_0
    //   17: invokespecial 367	java/util/jar/JarInputStream:<init>	(Ljava/io/InputStream;Z)V
    //   20: astore_3
    //   21: aconst_null
    //   22: astore 4
    //   24: aload_3
    //   25: invokevirtual 368	java/util/jar/JarInputStream:getManifest	()Ljava/util/jar/Manifest;
    //   28: astore 5
    //   30: aload_3
    //   31: ifnull +31 -> 62
    //   34: aload 4
    //   36: ifnull +22 -> 58
    //   39: aload_3
    //   40: invokevirtual 366	java/util/jar/JarInputStream:close	()V
    //   43: goto +19 -> 62
    //   46: astore 6
    //   48: aload 4
    //   50: aload 6
    //   52: invokevirtual 361	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   55: goto +7 -> 62
    //   58: aload_3
    //   59: invokevirtual 366	java/util/jar/JarInputStream:close	()V
    //   62: aload_1
    //   63: ifnull +29 -> 92
    //   66: aload_2
    //   67: ifnull +21 -> 88
    //   70: aload_1
    //   71: invokevirtual 321	java/io/FileInputStream:close	()V
    //   74: goto +18 -> 92
    //   77: astore 6
    //   79: aload_2
    //   80: aload 6
    //   82: invokevirtual 361	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   85: goto +7 -> 92
    //   88: aload_1
    //   89: invokevirtual 321	java/io/FileInputStream:close	()V
    //   92: aload 5
    //   94: areturn
    //   95: astore 5
    //   97: aload 5
    //   99: astore 4
    //   101: aload 5
    //   103: athrow
    //   104: astore 7
    //   106: aload_3
    //   107: ifnull +31 -> 138
    //   110: aload 4
    //   112: ifnull +22 -> 134
    //   115: aload_3
    //   116: invokevirtual 366	java/util/jar/JarInputStream:close	()V
    //   119: goto +19 -> 138
    //   122: astore 8
    //   124: aload 4
    //   126: aload 8
    //   128: invokevirtual 361	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   131: goto +7 -> 138
    //   134: aload_3
    //   135: invokevirtual 366	java/util/jar/JarInputStream:close	()V
    //   138: aload 7
    //   140: athrow
    //   141: astore_3
    //   142: aload_3
    //   143: astore_2
    //   144: aload_3
    //   145: athrow
    //   146: astore 9
    //   148: aload_1
    //   149: ifnull +29 -> 178
    //   152: aload_2
    //   153: ifnull +21 -> 174
    //   156: aload_1
    //   157: invokevirtual 321	java/io/FileInputStream:close	()V
    //   160: goto +18 -> 178
    //   163: astore 10
    //   165: aload_2
    //   166: aload 10
    //   168: invokevirtual 361	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   171: goto +7 -> 178
    //   174: aload_1
    //   175: invokevirtual 321	java/io/FileInputStream:close	()V
    //   178: aload 9
    //   180: athrow
    //   181: astore_1
    //   182: aconst_null
    //   183: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	184	0	paramString	String
    //   8	167	1	localFileInputStream	java.io.FileInputStream
    //   181	1	1	localIOException	java.io.IOException
    //   10	156	2	localObject1	Object
    //   20	115	3	localJarInputStream	java.util.jar.JarInputStream
    //   141	4	3	localThrowable1	Throwable
    //   22	103	4	localObject2	Object
    //   28	65	5	localManifest	Manifest
    //   95	7	5	localThrowable2	Throwable
    //   46	5	6	localThrowable3	Throwable
    //   77	4	6	localThrowable4	Throwable
    //   104	35	7	localObject3	Object
    //   122	5	8	localThrowable5	Throwable
    //   146	33	9	localObject4	Object
    //   163	4	10	localThrowable6	Throwable
    // Exception table:
    //   from	to	target	type
    //   39	43	46	java/lang/Throwable
    //   70	74	77	java/lang/Throwable
    //   24	30	95	java/lang/Throwable
    //   24	30	104	finally
    //   95	106	104	finally
    //   115	119	122	java/lang/Throwable
    //   11	62	141	java/lang/Throwable
    //   95	141	141	java/lang/Throwable
    //   11	62	146	finally
    //   95	148	146	finally
    //   156	160	163	java/lang/Throwable
    //   0	92	181	java/io/IOException
    //   95	181	181	java/io/IOException
  }
  
  private static native String getSystemPackage0(String paramString);
  
  private static native String[] getSystemPackages0();
}
