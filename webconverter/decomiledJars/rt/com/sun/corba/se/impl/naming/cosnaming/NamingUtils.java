package com.sun.corba.se.impl.naming.cosnaming;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.omg.CosNaming.NameComponent;

public class NamingUtils
{
  public static boolean debug = false;
  public static PrintStream debugStream;
  public static PrintStream errStream;
  
  private NamingUtils() {}
  
  public static void dprint(String paramString)
  {
    if ((debug) && (debugStream != null)) {
      debugStream.println(paramString);
    }
  }
  
  public static void errprint(String paramString)
  {
    if (errStream != null) {
      errStream.println(paramString);
    } else {
      System.err.println(paramString);
    }
  }
  
  public static void printException(Exception paramException)
  {
    if (errStream != null) {
      paramException.printStackTrace(errStream);
    } else {
      paramException.printStackTrace();
    }
  }
  
  public static void makeDebugStream(File paramFile)
    throws IOException
  {
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
    DataOutputStream localDataOutputStream = new DataOutputStream(localFileOutputStream);
    debugStream = new PrintStream(localDataOutputStream);
    debugStream.println("Debug Stream Enabled.");
  }
  
  public static void makeErrStream(File paramFile)
    throws IOException
  {
    if (debug)
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
      DataOutputStream localDataOutputStream = new DataOutputStream(localFileOutputStream);
      errStream = new PrintStream(localDataOutputStream);
      dprint("Error stream setup completed.");
    }
  }
  
  static String getDirectoryStructuredName(NameComponent[] paramArrayOfNameComponent)
  {
    StringBuffer localStringBuffer = new StringBuffer("/");
    for (int i = 0; i < paramArrayOfNameComponent.length; i++) {
      localStringBuffer.append(paramArrayOfNameComponent[i].id + "." + paramArrayOfNameComponent[i].kind);
    }
    return localStringBuffer.toString();
  }
}
