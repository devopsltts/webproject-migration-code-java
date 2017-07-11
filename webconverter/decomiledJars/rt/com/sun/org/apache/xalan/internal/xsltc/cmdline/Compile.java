package com.sun.org.apache.xalan.internal.xsltc.cmdline;

import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;
import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOptsException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.io.File;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.util.Vector;

public final class Compile
{
  private static int VERSION_MAJOR = 1;
  private static int VERSION_MINOR = 4;
  private static int VERSION_DELTA = 0;
  private static boolean _allowExit = true;
  
  public Compile() {}
  
  public static void printUsage()
  {
    System.err.println("XSLTC version " + VERSION_MAJOR + "." + VERSION_MINOR + (VERSION_DELTA > 0 ? "." + VERSION_DELTA : "") + "\n" + new ErrorMsg("COMPILE_USAGE_STR"));
    if (_allowExit) {
      System.exit(-1);
    }
  }
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      int i = 0;
      int j = 0;
      int k = 0;
      GetOpt localGetOpt = new GetOpt(paramArrayOfString, "o:d:j:p:uxhsinv");
      if (paramArrayOfString.length < 1) {
        printUsage();
      }
      XSLTC localXSLTC = new XSLTC(true, new FeatureManager());
      localXSLTC.init();
      int m;
      while ((m = localGetOpt.getNextOption()) != -1) {
        switch (m)
        {
        case 105: 
          j = 1;
          break;
        case 111: 
          localXSLTC.setClassName(localGetOpt.getOptionArg());
          k = 1;
          break;
        case 100: 
          localXSLTC.setDestDirectory(localGetOpt.getOptionArg());
          break;
        case 112: 
          localXSLTC.setPackageName(localGetOpt.getOptionArg());
          break;
        case 106: 
          localXSLTC.setJarFileName(localGetOpt.getOptionArg());
          break;
        case 120: 
          localXSLTC.setDebug(true);
          break;
        case 117: 
          i = 1;
          break;
        case 115: 
          _allowExit = false;
          break;
        case 110: 
          localXSLTC.setTemplateInlining(true);
          break;
        case 101: 
        case 102: 
        case 103: 
        case 104: 
        case 107: 
        case 108: 
        case 109: 
        case 113: 
        case 114: 
        case 116: 
        case 118: 
        case 119: 
        default: 
          printUsage();
        }
      }
      boolean bool;
      if (j != 0)
      {
        if (k == 0)
        {
          System.err.println(new ErrorMsg("COMPILE_STDIN_ERR"));
          if (_allowExit) {
            System.exit(-1);
          }
        }
        bool = localXSLTC.compile(System.in, localXSLTC.getClassName());
      }
      else
      {
        String[] arrayOfString = localGetOpt.getCmdArgs();
        Vector localVector = new Vector();
        for (int n = 0; n < arrayOfString.length; n++)
        {
          String str = arrayOfString[n];
          URL localURL;
          if (i != 0) {
            localURL = new URL(str);
          } else {
            localURL = new File(str).toURI().toURL();
          }
          localVector.addElement(localURL);
        }
        bool = localXSLTC.compile(localVector);
      }
      if (bool)
      {
        localXSLTC.printWarnings();
        if (localXSLTC.getJarFileName() != null) {
          localXSLTC.outputToJar();
        }
        if (_allowExit) {
          System.exit(0);
        }
      }
      else
      {
        localXSLTC.printWarnings();
        localXSLTC.printErrors();
        if (_allowExit) {
          System.exit(-1);
        }
      }
    }
    catch (GetOptsException localGetOptsException)
    {
      System.err.println(localGetOptsException);
      printUsage();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      if (_allowExit) {
        System.exit(-1);
      }
    }
  }
}
