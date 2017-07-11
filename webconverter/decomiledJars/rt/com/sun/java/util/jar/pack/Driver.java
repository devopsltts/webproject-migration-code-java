package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.jar.Pack200.Unpacker;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class Driver
{
  private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("com.sun.java.util.jar.pack.DriverResource");
  private static final String PACK200_OPTION_MAP = "--repack                 $ \n  -r +>- @--repack              $ \n--no-gzip                $ \n  -g +>- @--no-gzip             $ \n--strip-debug            $ \n  -G +>- @--strip-debug         $ \n--no-keep-file-order     $ \n  -O +>- @--no-keep-file-order  $ \n--segment-limit=      *> = \n  -S +>  @--segment-limit=      = \n--effort=             *> = \n  -E +>  @--effort=             = \n--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--modification-time=  *> = \n  -m +>  @--modification-time=  = \n--pass-file=        *> &\000 \n  -P +>  @--pass-file=        &\000 \n--unknown-attribute=  *> = \n  -U +>  @--unknown-attribute=  = \n--class-attribute=  *> &\000 \n  -C +>  @--class-attribute=  &\000 \n--field-attribute=  *> &\000 \n  -F +>  @--field-attribute=  &\000 \n--method-attribute= *> &\000 \n  -M +>  @--method-attribute= &\000 \n--code-attribute=   *> &\000 \n  -D +>  @--code-attribute=   &\000 \n--config-file=      *>   . \n  -f +>  @--config-file=        . \n--no-strip-debug  !--strip-debug         \n--gzip            !--no-gzip             \n--keep-file-order !--no-keep-file-order  \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n--           . \n-   +?    >- . \n";
  private static final String UNPACK200_OPTION_MAP = "--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--remove-pack-file       $ \n  -r +>- @--remove-pack-file    $ \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--config-file=        *> . \n  -f +>  @--config-file=        . \n--           . \n-   +?    >- . \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n";
  private static final String[] PACK200_PROPERTY_TO_OPTION = { "pack.segment.limit", "--segment-limit=", "pack.keep.file.order", "--no-keep-file-order", "pack.effort", "--effort=", "pack.deflate.hint", "--deflate-hint=", "pack.modification.time", "--modification-time=", "pack.pass.file.", "--pass-file=", "pack.unknown.attribute", "--unknown-attribute=", "pack.class.attribute.", "--class-attribute=", "pack.field.attribute.", "--field-attribute=", "pack.method.attribute.", "--method-attribute=", "pack.code.attribute.", "--code-attribute=", "com.sun.java.util.jar.pack.verbose", "--verbose", "com.sun.java.util.jar.pack.strip.debug", "--strip-debug" };
  private static final String[] UNPACK200_PROPERTY_TO_OPTION = { "unpack.deflate.hint", "--deflate-hint=", "com.sun.java.util.jar.pack.verbose", "--verbose", "com.sun.java.util.jar.pack.unpack.remove.packfile", "--remove-pack-file" };
  
  Driver() {}
  
  public static void main(String[] paramArrayOfString)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList(Arrays.asList(paramArrayOfString));
    boolean bool1 = true;
    int i = 0;
    int j = 0;
    int k = 1;
    Object localObject1 = null;
    String str1 = "com.sun.java.util.jar.pack.verbose";
    Object localObject2 = localArrayList.isEmpty() ? "" : (String)localArrayList.get(0);
    Object localObject3 = localObject2;
    int m = -1;
    switch (((String)localObject3).hashCode())
    {
    case 1333303225: 
      if (((String)localObject3).equals("--pack")) {
        m = 0;
      }
      break;
    case 1559677394: 
      if (((String)localObject3).equals("--unpack")) {
        m = 1;
      }
      break;
    }
    switch (m)
    {
    case 0: 
      localArrayList.remove(0);
      break;
    case 1: 
      localArrayList.remove(0);
      bool1 = false;
      i = 1;
    }
    localObject2 = new HashMap();
    ((Map)localObject2).put(str1, System.getProperty(str1));
    String[] arrayOfString1;
    if (bool1)
    {
      localObject3 = "--repack                 $ \n  -r +>- @--repack              $ \n--no-gzip                $ \n  -g +>- @--no-gzip             $ \n--strip-debug            $ \n  -G +>- @--strip-debug         $ \n--no-keep-file-order     $ \n  -O +>- @--no-keep-file-order  $ \n--segment-limit=      *> = \n  -S +>  @--segment-limit=      = \n--effort=             *> = \n  -E +>  @--effort=             = \n--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--modification-time=  *> = \n  -m +>  @--modification-time=  = \n--pass-file=        *> &\000 \n  -P +>  @--pass-file=        &\000 \n--unknown-attribute=  *> = \n  -U +>  @--unknown-attribute=  = \n--class-attribute=  *> &\000 \n  -C +>  @--class-attribute=  &\000 \n--field-attribute=  *> &\000 \n  -F +>  @--field-attribute=  &\000 \n--method-attribute= *> &\000 \n  -M +>  @--method-attribute= &\000 \n--code-attribute=   *> &\000 \n  -D +>  @--code-attribute=   &\000 \n--config-file=      *>   . \n  -f +>  @--config-file=        . \n--no-strip-debug  !--strip-debug         \n--gzip            !--no-gzip             \n--keep-file-order !--no-keep-file-order  \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n--           . \n-   +?    >- . \n";
      arrayOfString1 = PACK200_PROPERTY_TO_OPTION;
    }
    else
    {
      localObject3 = "--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--remove-pack-file       $ \n  -r +>- @--remove-pack-file    $ \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--config-file=        *> . \n  -f +>  @--config-file=        . \n--           . \n-   +?    >- . \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n";
      arrayOfString1 = UNPACK200_PROPERTY_TO_OPTION;
    }
    HashMap localHashMap = new HashMap();
    Object localObject11;
    Object localObject12;
    try
    {
      for (;;)
      {
        String str2 = parseCommandOptions(localArrayList, (String)localObject3, localHashMap);
        localObject5 = localHashMap.keySet().iterator();
        Object localObject8;
        while (((Iterator)localObject5).hasNext())
        {
          localObject6 = (String)((Iterator)localObject5).next();
          localObject7 = null;
          for (int i1 = 0; i1 < arrayOfString1.length; i1 += 2) {
            if (((String)localObject6).equals(arrayOfString1[(1 + i1)]))
            {
              localObject7 = arrayOfString1[(0 + i1)];
              break;
            }
          }
          if (localObject7 != null)
          {
            localObject8 = (String)localHashMap.get(localObject6);
            ((Iterator)localObject5).remove();
            if (!((String)localObject7).endsWith("."))
            {
              if ((!((String)localObject6).equals("--verbose")) && (!((String)localObject6).endsWith("=")))
              {
                int i3 = localObject8 != null ? 1 : 0;
                if (((String)localObject6).startsWith("--no-")) {
                  i3 = i3 == 0 ? 1 : 0;
                }
                localObject8 = i3 != 0 ? "true" : "false";
              }
              ((Map)localObject2).put(localObject7, localObject8);
            }
            else if (((String)localObject7).contains(".attribute."))
            {
              for (String str5 : ((String)localObject8).split("\000"))
              {
                localObject11 = str5.split("=", 2);
                ((Map)localObject2).put((String)localObject7 + localObject11[0], localObject11[1]);
              }
            }
            else
            {
              int i4 = 1;
              for (localObject11 : ((String)localObject8).split("\000"))
              {
                do
                {
                  localObject12 = (String)localObject7 + "cli." + i4++;
                } while (((Map)localObject2).containsKey(localObject12));
                ((Map)localObject2).put(localObject12, localObject11);
              }
            }
          }
        }
        if ("--config-file=".equals(str2))
        {
          localObject5 = (String)localArrayList.remove(0);
          localObject6 = new Properties();
          localObject7 = new FileInputStream((String)localObject5);
          localObject8 = null;
          try
          {
            ((Properties)localObject6).load((InputStream)localObject7);
          }
          catch (Throwable localThrowable2)
          {
            localObject8 = localThrowable2;
            throw localThrowable2;
          }
          finally
          {
            if (localObject7 != null) {
              if (localObject8 != null) {
                try
                {
                  ((InputStream)localObject7).close();
                }
                catch (Throwable localThrowable3)
                {
                  ((Throwable)localObject8).addSuppressed(localThrowable3);
                }
              } else {
                ((InputStream)localObject7).close();
              }
            }
          }
          if (((Map)localObject2).get(str1) != null) {
            ((Properties)localObject6).list(System.out);
          }
          localObject7 = ((Properties)localObject6).entrySet().iterator();
          while (((Iterator)localObject7).hasNext())
          {
            localObject8 = (Map.Entry)((Iterator)localObject7).next();
            ((Map)localObject2).put((String)((Map.Entry)localObject8).getKey(), (String)((Map.Entry)localObject8).getValue());
          }
        }
        else
        {
          if ("--version".equals(str2))
          {
            System.out.println(MessageFormat.format(RESOURCE.getString("VERSION"), new Object[] { Driver.class.getName(), "1.31, 07/05/05" }));
            return;
          }
          if (!"--help".equals(str2)) {
            break;
          }
          printUsage(bool1, true, System.out);
          System.exit(1);
          return;
        }
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      System.err.println(MessageFormat.format(RESOURCE.getString("BAD_ARGUMENT"), new Object[] { localIllegalArgumentException }));
      printUsage(bool1, false, System.err);
      System.exit(2);
      return;
    }
    Object localObject4 = localHashMap.keySet().iterator();
    while (((Iterator)localObject4).hasNext())
    {
      localObject5 = (String)((Iterator)localObject4).next();
      localObject6 = (String)localHashMap.get(localObject5);
      localObject7 = localObject5;
      int i2 = -1;
      switch (((String)localObject7).hashCode())
      {
      case 1465478252: 
        if (((String)localObject7).equals("--repack")) {
          i2 = 0;
        }
        break;
      case -845245370: 
        if (((String)localObject7).equals("--no-gzip")) {
          i2 = 1;
        }
        break;
      case 1339571416: 
        if (((String)localObject7).equals("--log-file=")) {
          i2 = 2;
        }
        break;
      }
      switch (i2)
      {
      case 0: 
        j = 1;
        break;
      case 1: 
        k = localObject6 == null ? 1 : 0;
        break;
      case 2: 
        localObject1 = localObject6;
        break;
      default: 
        throw new InternalError(MessageFormat.format(RESOURCE.getString("BAD_OPTION"), new Object[] { localObject5, localHashMap.get(localObject5) }));
      }
    }
    if ((localObject1 != null) && (!localObject1.equals(""))) {
      if (localObject1.equals("-"))
      {
        System.setErr(System.out);
      }
      else
      {
        localObject4 = new FileOutputStream(localObject1);
        System.setErr(new PrintStream((OutputStream)localObject4));
      }
    }
    int n = ((Map)localObject2).get(str1) != null ? 1 : 0;
    Object localObject5 = "";
    if (!localArrayList.isEmpty()) {
      localObject5 = (String)localArrayList.remove(0);
    }
    Object localObject6 = "";
    if (!localArrayList.isEmpty()) {
      localObject6 = (String)localArrayList.remove(0);
    }
    Object localObject7 = "";
    String str3 = "";
    String str4 = "";
    if (j != 0)
    {
      if ((((String)localObject5).toLowerCase().endsWith(".pack")) || (((String)localObject5).toLowerCase().endsWith(".pac")) || (((String)localObject5).toLowerCase().endsWith(".gz")))
      {
        System.err.println(MessageFormat.format(RESOURCE.getString("BAD_REPACK_OUTPUT"), new Object[] { localObject5 }));
        printUsage(bool1, false, System.err);
        System.exit(2);
      }
      localObject7 = localObject5;
      if (((String)localObject6).equals("")) {
        localObject6 = localObject7;
      }
      str4 = createTempFile((String)localObject7, ".pack").getPath();
      localObject5 = str4;
      k = 0;
    }
    if ((!localArrayList.isEmpty()) || ((!((String)localObject6).toLowerCase().endsWith(".jar")) && (!((String)localObject6).toLowerCase().endsWith(".zip")) && ((!((String)localObject6).equals("-")) || (bool1))))
    {
      printUsage(bool1, false, System.err);
      System.exit(2);
      return;
    }
    if (j != 0) {
      bool1 = i = 1;
    } else if (bool1) {
      i = 0;
    }
    ??? = Pack200.newPacker();
    Pack200.Unpacker localUnpacker = Pack200.newUnpacker();
    ((Pack200.Packer)???).properties().putAll((Map)localObject2);
    localUnpacker.properties().putAll((Map)localObject2);
    Object localObject10;
    if ((j != 0) && (((String)localObject7).equals(localObject6)))
    {
      localObject10 = getZipComment((String)localObject6);
      if ((n != 0) && (((String)localObject10).length() > 0)) {
        System.out.println(MessageFormat.format(RESOURCE.getString("DETECTED_ZIP_COMMENT"), new Object[] { localObject10 }));
      }
      if (((String)localObject10).indexOf("PACK200") >= 0)
      {
        System.out.println(MessageFormat.format(RESOURCE.getString("SKIP_FOR_REPACKED"), new Object[] { localObject6 }));
        bool1 = false;
        i = 0;
        j = 0;
      }
    }
    try
    {
      if (bool1)
      {
        localObject10 = new JarFile(new File((String)localObject6));
        if (((String)localObject5).equals("-"))
        {
          localObject11 = System.out;
          System.setOut(System.err);
        }
        else if (k != 0)
        {
          if (!((String)localObject5).endsWith(".gz"))
          {
            System.err.println(MessageFormat.format(RESOURCE.getString("WRITE_PACK_FILE"), new Object[] { localObject5 }));
            printUsage(bool1, false, System.err);
            System.exit(2);
          }
          localObject11 = new FileOutputStream((String)localObject5);
          localObject11 = new BufferedOutputStream((OutputStream)localObject11);
          localObject11 = new GZIPOutputStream((OutputStream)localObject11);
        }
        else
        {
          if ((!((String)localObject5).toLowerCase().endsWith(".pack")) && (!((String)localObject5).toLowerCase().endsWith(".pac")))
          {
            System.err.println(MessageFormat.format(RESOURCE.getString("WRITE_PACKGZ_FILE"), new Object[] { localObject5 }));
            printUsage(bool1, false, System.err);
            System.exit(2);
          }
          localObject11 = new FileOutputStream((String)localObject5);
          localObject11 = new BufferedOutputStream((OutputStream)localObject11);
        }
        ((Pack200.Packer)???).pack((JarFile)localObject10, (OutputStream)localObject11);
        ((OutputStream)localObject11).close();
      }
      if ((j != 0) && (((String)localObject7).equals(localObject6)))
      {
        localObject10 = createTempFile((String)localObject6, ".bak");
        ((File)localObject10).delete();
        boolean bool2 = new File((String)localObject6).renameTo((File)localObject10);
        if (!bool2) {
          throw new Error(MessageFormat.format(RESOURCE.getString("SKIP_FOR_MOVE_FAILED"), new Object[] { str3 }));
        }
        str3 = ((File)localObject10).getPath();
      }
      if (i != 0)
      {
        if (((String)localObject5).equals("-")) {
          localObject10 = System.in;
        } else {
          localObject10 = new FileInputStream(new File((String)localObject5));
        }
        BufferedInputStream localBufferedInputStream = new BufferedInputStream((InputStream)localObject10);
        localObject10 = localBufferedInputStream;
        if (Utils.isGZIPMagic(Utils.readMagic(localBufferedInputStream))) {
          localObject10 = new GZIPInputStream((InputStream)localObject10);
        }
        localObject12 = ((String)localObject7).equals("") ? localObject6 : localObject7;
        if (((String)localObject12).equals("-")) {
          localObject14 = System.out;
        } else {
          localObject14 = new FileOutputStream((String)localObject12);
        }
        Object localObject14 = new BufferedOutputStream((OutputStream)localObject14);
        JarOutputStream localJarOutputStream = new JarOutputStream((OutputStream)localObject14);
        Object localObject15 = null;
        try
        {
          localUnpacker.unpack((InputStream)localObject10, localJarOutputStream);
        }
        catch (Throwable localThrowable5)
        {
          localObject15 = localThrowable5;
          throw localThrowable5;
        }
        finally
        {
          if (localJarOutputStream != null) {
            if (localObject15 != null) {
              try
              {
                localJarOutputStream.close();
              }
              catch (Throwable localThrowable6)
              {
                localObject15.addSuppressed(localThrowable6);
              }
            } else {
              localJarOutputStream.close();
            }
          }
        }
      }
      if (!str3.equals(""))
      {
        new File(str3).delete();
        str3 = "";
      }
    }
    finally
    {
      if (!str3.equals(""))
      {
        File localFile = new File((String)localObject6);
        localFile.delete();
        new File(str3).renameTo(localFile);
      }
      if (!str4.equals("")) {
        new File(str4).delete();
      }
    }
  }
  
  private static File createTempFile(String paramString1, String paramString2)
    throws IOException
  {
    File localFile1 = new File(paramString1);
    String str = localFile1.getName();
    if (str.length() < 3) {
      str = str + "tmp";
    }
    File localFile2 = (localFile1.getParentFile() == null) && (paramString2.equals(".bak")) ? new File(".").getAbsoluteFile() : localFile1.getParentFile();
    Path localPath = localFile2 == null ? Files.createTempFile(str, paramString2, new FileAttribute[0]) : Files.createTempFile(localFile2.toPath(), str, paramString2, new FileAttribute[0]);
    return localPath.toFile();
  }
  
  private static void printUsage(boolean paramBoolean1, boolean paramBoolean2, PrintStream paramPrintStream)
  {
    String str = paramBoolean1 ? "pack200" : "unpack200";
    String[] arrayOfString1 = (String[])RESOURCE.getObject("PACK_HELP");
    String[] arrayOfString2 = (String[])RESOURCE.getObject("UNPACK_HELP");
    String[] arrayOfString3 = paramBoolean1 ? arrayOfString1 : arrayOfString2;
    for (int i = 0; i < arrayOfString3.length; i++)
    {
      paramPrintStream.println(arrayOfString3[i]);
      if (!paramBoolean2)
      {
        paramPrintStream.println(MessageFormat.format(RESOURCE.getString("MORE_INFO"), new Object[] { str }));
        break;
      }
    }
  }
  
  private static String getZipComment(String paramString)
    throws IOException
  {
    byte[] arrayOfByte = new byte['Ϩ'];
    long l1 = new File(paramString).length();
    if (l1 <= 0L) {
      return "";
    }
    long l2 = Math.max(0L, l1 - arrayOfByte.length);
    FileInputStream localFileInputStream = new FileInputStream(new File(paramString));
    Object localObject1 = null;
    try
    {
      localFileInputStream.skip(l2);
      localFileInputStream.read(arrayOfByte);
      for (int i = arrayOfByte.length - 4; i >= 0; i--) {
        if ((arrayOfByte[(i + 0)] == 80) && (arrayOfByte[(i + 1)] == 75) && (arrayOfByte[(i + 2)] == 5) && (arrayOfByte[(i + 3)] == 6))
        {
          i += 22;
          if (i < arrayOfByte.length)
          {
            str2 = new String(arrayOfByte, i, arrayOfByte.length - i, "UTF8");
            return str2;
          }
          String str2 = "";
          return str2;
        }
      }
      String str1 = "";
      return str1;
    }
    catch (Throwable localThrowable1)
    {
      localObject1 = localThrowable1;
      throw localThrowable1;
    }
    finally
    {
      if (localFileInputStream != null) {
        if (localObject1 != null) {
          try
          {
            localFileInputStream.close();
          }
          catch (Throwable localThrowable5)
          {
            localObject1.addSuppressed(localThrowable5);
          }
        } else {
          localFileInputStream.close();
        }
      }
    }
  }
  
  private static String parseCommandOptions(List<String> paramList, String paramString, Map<String, String> paramMap)
  {
    Object localObject1 = null;
    TreeMap localTreeMap = new TreeMap();
    Object localObject4;
    Object localObject5;
    for (Object localObject3 : paramString.split("\n"))
    {
      localObject4 = localObject3.split("\\p{Space}+");
      if (localObject4.length != 0)
      {
        localObject5 = localObject4[0];
        localObject4[0] = "";
        if ((((String)localObject5).length() == 0) && (localObject4.length >= 1))
        {
          localObject5 = localObject4[1];
          localObject4[1] = "";
        }
        if (((String)localObject5).length() != 0)
        {
          String[] arrayOfString1 = (String[])localTreeMap.put(localObject5, localObject4);
          if (arrayOfString1 != null) {
            throw new RuntimeException(MessageFormat.format(RESOURCE.getString("DUPLICATE_OPTION"), new Object[] { localObject3.trim() }));
          }
        }
      }
    }
    ??? = paramList.listIterator();
    ListIterator localListIterator = new ArrayList().listIterator();
    String str1;
    if (localListIterator.hasPrevious())
    {
      str1 = (String)localListIterator.previous();
      localListIterator.remove();
    }
    else
    {
      if (!((ListIterator)???).hasNext()) {
        break label1211;
      }
      str1 = (String)((ListIterator)???).next();
    }
    int k = str1.length();
    for (;;)
    {
      localObject4 = str1.substring(0, k);
      int m;
      if (!localTreeMap.containsKey(localObject4))
      {
        if (k == 0) {
          break label1199;
        }
        localObject5 = localTreeMap.headMap(localObject4);
        m = ((SortedMap)localObject5).isEmpty() ? 0 : ((String)((SortedMap)localObject5).lastKey()).length();
        k = Math.min(m, k - 1);
        localObject4 = str1.substring(0, k);
      }
      else
      {
        localObject4 = ((String)localObject4).intern();
        assert (str1.startsWith((String)localObject4));
        assert (((String)localObject4).length() == k);
        localObject5 = str1.substring(k);
        m = 0;
        int n = 0;
        int i1 = localListIterator.nextIndex();
        String[] arrayOfString2 = (String[])localTreeMap.get(localObject4);
        for (String str2 : arrayOfString2) {
          if (str2.length() != 0)
          {
            if (str2.startsWith("#")) {
              break;
            }
            int i4 = 0;
            int i5 = str2.charAt(i4++);
            int i6;
            switch (i5)
            {
            case 43: 
              i6 = ((String)localObject5).length() != 0 ? 1 : 0;
              i5 = str2.charAt(i4++);
              break;
            case 42: 
              i6 = 1;
              i5 = str2.charAt(i4++);
              break;
            default: 
              i6 = ((String)localObject5).length() == 0 ? 1 : 0;
            }
            if (i6 != 0)
            {
              String str3 = str2.substring(i4);
              switch (i5)
              {
              case 46: 
                localObject1 = str3.length() != 0 ? str3.intern() : localObject4;
                break;
              case 63: 
                localObject1 = str3.length() != 0 ? str3.intern() : str1;
                n = 1;
                break;
              case 64: 
                localObject4 = str3.intern();
                break;
              case 62: 
                localListIterator.add(str3 + (String)localObject5);
                localObject5 = "";
                break;
              case 33: 
                Object localObject6 = str3.length() != 0 ? str3.intern() : localObject4;
                paramMap.remove(localObject6);
                paramMap.put(localObject6, null);
                m = 1;
                break;
              case 36: 
                String str4;
                if (str3.length() != 0)
                {
                  str4 = str3;
                }
                else
                {
                  String str5 = (String)paramMap.get(localObject4);
                  if ((str5 == null) || (str5.length() == 0)) {
                    str4 = "1";
                  } else {
                    str4 = "" + (1 + Integer.parseInt(str5));
                  }
                }
                paramMap.put(localObject4, str4);
                m = 1;
                break;
              case 38: 
              case 61: 
                int i7 = i5 == 38 ? 1 : 0;
                String str6;
                if (localListIterator.hasPrevious())
                {
                  str6 = (String)localListIterator.previous();
                  localListIterator.remove();
                }
                else if (((ListIterator)???).hasNext())
                {
                  str6 = (String)((ListIterator)???).next();
                }
                else
                {
                  localObject1 = str1 + " ?";
                  n = 1;
                  break label1128;
                }
                if (i7 != 0)
                {
                  String str7 = (String)paramMap.get(localObject4);
                  if (str7 != null)
                  {
                    String str8 = str3;
                    if (str8.length() == 0) {
                      str8 = " ";
                    }
                    str6 = str7 + str3 + str6;
                  }
                }
                paramMap.put(localObject4, str6);
                m = 1;
                break;
              default: 
                throw new RuntimeException(MessageFormat.format(RESOURCE.getString("BAD_SPEC"), new Object[] { localObject4, str2 }));
              }
            }
          }
        }
        label1128:
        if ((m != 0) && (n == 0)) {
          break;
        }
        while (localListIterator.nextIndex() > i1)
        {
          localListIterator.previous();
          localListIterator.remove();
        }
        if (n != 0) {
          throw new IllegalArgumentException((String)localObject1);
        }
        if (k == 0) {
          break label1199;
        }
        k--;
      }
    }
    label1199:
    localListIterator.add(str1);
    label1211:
    paramList.subList(0, ((ListIterator)???).nextIndex()).clear();
    while (localListIterator.hasPrevious()) {
      paramList.add(0, localListIterator.previous());
    }
    return localObject1;
  }
}
