package jdk.management.resource.internal.inst;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import jdk.internal.instrumentation.ClassInstrumentation;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.Logger;

public final class StaticInstrumentation
{
  public StaticInstrumentation() {}
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    instrumentClassesForResourceManagement(new File(paramArrayOfString[0]), new File(paramArrayOfString[1]));
  }
  
  public static void instrumentClassesForResourceManagement(File paramFile1, File paramFile2)
    throws Exception
  {
    if (!paramFile1.isDirectory()) {
      throw new Exception(paramFile1 + " is not a directory");
    }
    if (!paramFile2.isDirectory()) {
      throw new Exception(paramFile1 + " is not a directory");
    }
    InstrumentationLogger localInstrumentationLogger = new InstrumentationLogger();
    System.out.println();
    System.out.println("Reading from " + paramFile1);
    System.out.println("Output to " + paramFile2);
    Set localSet = findAllJarFiles(paramFile1);
    HashMap localHashMap = new HashMap();
    System.out.println();
    System.out.println("Searching for classes");
    int i = 0;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    Object localObject6;
    for (localObject2 : InitInstrumentation.hooks)
    {
      localObject3 = findTargetClassName((Class)localObject2);
      System.out.println(i + ":");
      i++;
      System.out.println("   Instrumentation: " + ((Class)localObject2).getName());
      System.out.println("   Target         : " + (String)localObject3);
      int m = 0;
      localObject4 = localSet.iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (File)((Iterator)localObject4).next();
        localObject6 = getJarEntry((String)localObject3, (File)localObject5);
        if (localObject6 != null)
        {
          System.out.println("   Found in jar  : " + localObject5);
          if (((JarEntry)localObject6).getCodeSigners() != null) {
            throw new Exception("The target class '" + (String)localObject3 + "' was found in a signed jar: " + localObject5);
          }
          addNewTask(localHashMap, (File)localObject5, (Class)localObject2);
          m = 1;
          break;
        }
      }
      if (m == 0) {
        throw new Exception("The target class '" + (String)localObject3 + " was not found in any jar");
      }
    }
    System.out.println();
    System.out.println("Instrumenting");
    ??? = localHashMap.keySet().iterator();
    while (((Iterator)???).hasNext())
    {
      File localFile1 = (File)((Iterator)???).next();
      File localFile2 = new File(paramFile2, localFile1.getName());
      Files.copy(localFile1.toPath(), localFile2.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
      System.out.println("   Jar     : " + localFile1);
      System.out.println("   Jar copy: " + localFile2);
      localObject2 = new ArrayList();
      localObject3 = ((List)localHashMap.get(localFile1)).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        Class localClass = (Class)((Iterator)localObject3).next();
        localObject4 = findTargetClassName(localClass);
        System.out.println("      Class: " + (String)localObject4);
        localObject5 = findSourceBytesFor((String)localObject4, localFile2);
        localObject6 = new ClassInstrumentation(localClass, (String)localObject4, (byte[])localObject5, localInstrumentationLogger).getNewBytes();
        File localFile3 = createOutputFile(paramFile2, (String)localObject4);
        writeOutputClass(localFile3, (byte[])localObject6);
        ((List)localObject2).add(localFile3);
      }
      System.out.println("   Updating jar");
      updateJar(paramFile2, localFile2, (List)localObject2);
      System.out.println();
    }
  }
  
  private static void updateJar(File paramFile1, File paramFile2, List<File> paramList)
    throws InterruptedException, IOException
  {
    String str1 = System.getProperty("java.home") + File.separator + "bin" + File.separator + "jar";
    ProcessBuilder localProcessBuilder = new ProcessBuilder(new String[] { str1, "uvf", paramFile2.getAbsolutePath() });
    Object localObject = paramList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      File localFile = (File)((Iterator)localObject).next();
      String str2 = paramFile1.toPath().relativize(localFile.toPath()).toString();
      localProcessBuilder.command().add(str2);
    }
    localProcessBuilder.directory(paramFile1);
    localProcessBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    localProcessBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    System.out.println("Executing: " + (String)localProcessBuilder.command().stream().collect(Collectors.joining(" ")));
    localObject = localProcessBuilder.start();
    ((Process)localObject).waitFor();
  }
  
  private static void addNewTask(HashMap<File, List<Class<?>>> paramHashMap, File paramFile, Class<?> paramClass)
  {
    Object localObject = (List)paramHashMap.get(paramFile);
    if (localObject == null)
    {
      localObject = new ArrayList();
      paramHashMap.put(paramFile, localObject);
    }
    ((List)localObject).add(paramClass);
  }
  
  private static Set<File> findAllJarFiles(File paramFile)
    throws IOException
  {
    HashSet localHashSet = new HashSet();
    LinkedBlockingDeque localLinkedBlockingDeque = new LinkedBlockingDeque();
    localLinkedBlockingDeque.add(paramFile);
    File localFile1;
    while ((localFile1 = (File)localLinkedBlockingDeque.poll()) != null) {
      for (File localFile2 : localFile1.listFiles()) {
        if (localFile2.isDirectory()) {
          localLinkedBlockingDeque.add(localFile2);
        } else if (localFile2.getName().endsWith(".jar")) {
          localHashSet.add(localFile2);
        }
      }
    }
    return localHashSet;
  }
  
  private static File createOutputFile(File paramFile, String paramString)
  {
    File localFile = new File(paramFile, paramString.replace(".", File.separator) + ".class");
    localFile.getParentFile().mkdirs();
    return localFile;
  }
  
  private static void writeOutputClass(File paramFile, byte[] paramArrayOfByte)
    throws FileNotFoundException, IOException
  {
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
    Object localObject1 = null;
    try
    {
      localFileOutputStream.write(paramArrayOfByte);
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localFileOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localFileOutputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            localObject1.addSuppressed(localThrowable3);
          }
        } else {
          localFileOutputStream.close();
        }
      }
    }
  }
  
  private static String findTargetClassName(Class<?> paramClass)
  {
    return ((InstrumentationTarget)paramClass.getAnnotation(InstrumentationTarget.class)).value();
  }
  
  private static JarEntry getJarEntry(String paramString, File paramFile)
    throws Exception
  {
    JarFile localJarFile = new JarFile(paramFile);
    Object localObject1 = null;
    try
    {
      String str = paramString.replace(".", "/") + ".class";
      JarEntry localJarEntry1 = localJarFile.getJarEntry(str);
      JarEntry localJarEntry2 = localJarEntry1;
      return localJarEntry2;
    }
    catch (Throwable localThrowable1)
    {
      localObject1 = localThrowable1;
      throw localThrowable1;
    }
    finally
    {
      if (localJarFile != null) {
        if (localObject1 != null) {
          try
          {
            localJarFile.close();
          }
          catch (Throwable localThrowable3)
          {
            localObject1.addSuppressed(localThrowable3);
          }
        } else {
          localJarFile.close();
        }
      }
    }
  }
  
  private static byte[] findSourceBytesFor(String paramString, File paramFile)
    throws Exception
  {
    JarFile localJarFile = new JarFile(paramFile);
    Object localObject1 = null;
    try
    {
      String str = paramString.replace(".", "/") + ".class";
      ZipEntry localZipEntry = localJarFile.getEntry(str);
      if (localZipEntry == null)
      {
        arrayOfByte1 = null;
        return arrayOfByte1;
      }
      byte[] arrayOfByte1 = readBytes(localJarFile.getInputStream(localZipEntry));
      byte[] arrayOfByte2 = arrayOfByte1;
      return arrayOfByte2;
    }
    catch (Throwable localThrowable1)
    {
      localObject1 = localThrowable1;
      throw localThrowable1;
    }
    finally
    {
      if (localJarFile != null) {
        if (localObject1 != null) {
          try
          {
            localJarFile.close();
          }
          catch (Throwable localThrowable4)
          {
            localObject1.addSuppressed(localThrowable4);
          }
        } else {
          localJarFile.close();
        }
      }
    }
  }
  
  private static byte[] readBytes(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte = new byte['Ѐ'];
    int i;
    while ((i = paramInputStream.read(arrayOfByte)) != -1) {
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  static class InstrumentationLogger
    implements Logger
  {
    InstrumentationLogger() {}
    
    public void error(String paramString)
    {
      System.err.println("StaticInstrumentation error: " + paramString);
    }
    
    public void warn(String paramString)
    {
      System.err.println("StaticInstrumentation warning: " + paramString);
    }
    
    public void info(String paramString)
    {
      System.err.println("StaticInstrumentation info: " + paramString);
    }
    
    public void debug(String paramString) {}
    
    public void trace(String paramString) {}
    
    public void error(String paramString, Throwable paramThrowable)
    {
      System.err.println("StaticInstrumentation error: " + paramString + ": " + paramThrowable);
    }
  }
}
