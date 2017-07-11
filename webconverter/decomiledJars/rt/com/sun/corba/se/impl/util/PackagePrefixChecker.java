package com.sun.corba.se.impl.util;

public final class PackagePrefixChecker
{
  private static final String PACKAGE_PREFIX = "org.omg.stub.";
  
  public PackagePrefixChecker() {}
  
  public static String packagePrefix()
  {
    return "org.omg.stub.";
  }
  
  public static String correctPackageName(String paramString)
  {
    if (paramString == null) {
      return paramString;
    }
    if (hasOffendingPrefix(paramString)) {
      return "org.omg.stub." + paramString;
    }
    return paramString;
  }
  
  public static boolean isOffendingPackage(String paramString)
  {
    return (paramString != null) && (hasOffendingPrefix(paramString));
  }
  
  public static boolean hasOffendingPrefix(String paramString)
  {
    return (paramString.startsWith("java.")) || (paramString.equals("java")) || (paramString.startsWith("net.jini.")) || (paramString.equals("net.jini")) || (paramString.startsWith("jini.")) || (paramString.equals("jini")) || (paramString.startsWith("javax.")) || (paramString.equals("javax"));
  }
  
  public static boolean hasBeenPrefixed(String paramString)
  {
    return paramString.startsWith(packagePrefix());
  }
  
  public static String withoutPackagePrefix(String paramString)
  {
    if (hasBeenPrefixed(paramString)) {
      return paramString.substring(packagePrefix().length());
    }
    return paramString;
  }
}
