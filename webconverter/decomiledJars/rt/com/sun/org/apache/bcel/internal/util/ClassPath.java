package com.sun.org.apache.bcel.internal.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassPath
  implements Serializable
{
  public static final ClassPath SYSTEM_CLASS_PATH = new ClassPath();
  private PathEntry[] paths;
  private String class_path;
  
  public ClassPath(String paramString)
  {
    this.class_path = paramString;
    ArrayList localArrayList = new ArrayList();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, SecuritySupport.getSystemProperty("path.separator"));
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      if (!str.equals(""))
      {
        File localFile = new File(str);
        try
        {
          if (SecuritySupport.getFileExists(localFile)) {
            if (localFile.isDirectory()) {
              localArrayList.add(new Dir(str));
            } else {
              localArrayList.add(new Zip(new ZipFile(localFile)));
            }
          }
        }
        catch (IOException localIOException)
        {
          System.err.println("CLASSPATH component " + localFile + ": " + localIOException);
        }
      }
    }
    this.paths = new PathEntry[localArrayList.size()];
    localArrayList.toArray(this.paths);
  }
  
  /**
   * @deprecated
   */
  public ClassPath()
  {
    this("");
  }
  
  public String toString()
  {
    return this.class_path;
  }
  
  public int hashCode()
  {
    return this.class_path.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ClassPath)) {
      return this.class_path.equals(((ClassPath)paramObject).class_path);
    }
    return false;
  }
  
  private static final void getPathComponents(String paramString, ArrayList paramArrayList)
  {
    if (paramString != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
      while (localStringTokenizer.hasMoreTokens())
      {
        String str = localStringTokenizer.nextToken();
        File localFile = new File(str);
        if (SecuritySupport.getFileExists(localFile)) {
          paramArrayList.add(str);
        }
      }
    }
  }
  
  public static final String getClassPath()
  {
    String str1;
    String str2;
    String str3;
    try
    {
      str1 = SecuritySupport.getSystemProperty("java.class.path");
      str2 = SecuritySupport.getSystemProperty("sun.boot.class.path");
      str3 = SecuritySupport.getSystemProperty("java.ext.dirs");
    }
    catch (SecurityException localSecurityException)
    {
      return "";
    }
    ArrayList localArrayList1 = new ArrayList();
    getPathComponents(str1, localArrayList1);
    getPathComponents(str2, localArrayList1);
    ArrayList localArrayList2 = new ArrayList();
    getPathComponents(str3, localArrayList2);
    Object localObject1 = localArrayList2.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = new File((String)((Iterator)localObject1).next());
      String[] arrayOfString = SecuritySupport.getFileList((File)localObject2, new FilenameFilter()
      {
        public boolean accept(File paramAnonymousFile, String paramAnonymousString)
        {
          paramAnonymousString = paramAnonymousString.toLowerCase();
          return (paramAnonymousString.endsWith(".zip")) || (paramAnonymousString.endsWith(".jar"));
        }
      });
      if (arrayOfString != null) {
        for (int i = 0; i < arrayOfString.length; i++) {
          localArrayList1.add(str3 + File.separatorChar + arrayOfString[i]);
        }
      }
    }
    localObject1 = new StringBuffer();
    Object localObject2 = localArrayList1.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      ((StringBuffer)localObject1).append((String)((Iterator)localObject2).next());
      if (((Iterator)localObject2).hasNext()) {
        ((StringBuffer)localObject1).append(File.pathSeparatorChar);
      }
    }
    return ((StringBuffer)localObject1).toString().intern();
  }
  
  public InputStream getInputStream(String paramString)
    throws IOException
  {
    return getInputStream(paramString, ".class");
  }
  
  public InputStream getInputStream(String paramString1, String paramString2)
    throws IOException
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = getClass().getClassLoader().getResourceAsStream(paramString1 + paramString2);
    }
    catch (Exception localException) {}
    if (localInputStream != null) {
      return localInputStream;
    }
    return getClassFile(paramString1, paramString2).getInputStream();
  }
  
  public ClassFile getClassFile(String paramString1, String paramString2)
    throws IOException
  {
    for (int i = 0; i < this.paths.length; i++)
    {
      ClassFile localClassFile;
      if ((localClassFile = this.paths[i].getClassFile(paramString1, paramString2)) != null) {
        return localClassFile;
      }
    }
    throw new IOException("Couldn't find: " + paramString1 + paramString2);
  }
  
  public ClassFile getClassFile(String paramString)
    throws IOException
  {
    return getClassFile(paramString, ".class");
  }
  
  public byte[] getBytes(String paramString1, String paramString2)
    throws IOException
  {
    InputStream localInputStream = getInputStream(paramString1, paramString2);
    if (localInputStream == null) {
      throw new IOException("Couldn't find: " + paramString1 + paramString2);
    }
    DataInputStream localDataInputStream = new DataInputStream(localInputStream);
    byte[] arrayOfByte = new byte[localInputStream.available()];
    localDataInputStream.readFully(arrayOfByte);
    localDataInputStream.close();
    localInputStream.close();
    return arrayOfByte;
  }
  
  public byte[] getBytes(String paramString)
    throws IOException
  {
    return getBytes(paramString, ".class");
  }
  
  public String getPath(String paramString)
    throws IOException
  {
    int i = paramString.lastIndexOf('.');
    String str = "";
    if (i > 0)
    {
      str = paramString.substring(i);
      paramString = paramString.substring(0, i);
    }
    return getPath(paramString, str);
  }
  
  public String getPath(String paramString1, String paramString2)
    throws IOException
  {
    return getClassFile(paramString1, paramString2).getPath();
  }
  
  public static abstract interface ClassFile
  {
    public abstract InputStream getInputStream()
      throws IOException;
    
    public abstract String getPath();
    
    public abstract String getBase();
    
    public abstract long getTime();
    
    public abstract long getSize();
  }
  
  private static class Dir
    extends ClassPath.PathEntry
  {
    private String dir;
    
    Dir(String paramString)
    {
      super();
      this.dir = paramString;
    }
    
    ClassPath.ClassFile getClassFile(String paramString1, String paramString2)
      throws IOException
    {
      final File localFile = new File(this.dir + File.separatorChar + paramString1.replace('.', File.separatorChar) + paramString2);
      SecuritySupport.getFileExists(localFile) ? new ClassPath.ClassFile()
      {
        public InputStream getInputStream()
          throws IOException
        {
          return new FileInputStream(localFile);
        }
        
        public String getPath()
        {
          try
          {
            return localFile.getCanonicalPath();
          }
          catch (IOException localIOException) {}
          return null;
        }
        
        public long getTime()
        {
          return localFile.lastModified();
        }
        
        public long getSize()
        {
          return localFile.length();
        }
        
        public String getBase()
        {
          return ClassPath.Dir.this.dir;
        }
      } : null;
    }
    
    public String toString()
    {
      return this.dir;
    }
  }
  
  private static abstract class PathEntry
    implements Serializable
  {
    private PathEntry() {}
    
    abstract ClassPath.ClassFile getClassFile(String paramString1, String paramString2)
      throws IOException;
  }
  
  private static class Zip
    extends ClassPath.PathEntry
  {
    private ZipFile zip;
    
    Zip(ZipFile paramZipFile)
    {
      super();
      this.zip = paramZipFile;
    }
    
    ClassPath.ClassFile getClassFile(String paramString1, String paramString2)
      throws IOException
    {
      final ZipEntry localZipEntry = this.zip.getEntry(paramString1.replace('.', '/') + paramString2);
      localZipEntry != null ? new ClassPath.ClassFile()
      {
        public InputStream getInputStream()
          throws IOException
        {
          return ClassPath.Zip.this.zip.getInputStream(localZipEntry);
        }
        
        public String getPath()
        {
          return localZipEntry.toString();
        }
        
        public long getTime()
        {
          return localZipEntry.getTime();
        }
        
        public long getSize()
        {
          return localZipEntry.getSize();
        }
        
        public String getBase()
        {
          return ClassPath.Zip.this.zip.getName();
        }
      } : null;
    }
  }
}
