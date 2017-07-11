package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Class2HTML
  implements Constants
{
  private JavaClass java_class;
  private String dir;
  private static String class_package;
  private static String class_name;
  private static ConstantPool constant_pool;
  
  public Class2HTML(JavaClass paramJavaClass, String paramString)
    throws IOException
  {
    Method[] arrayOfMethod = paramJavaClass.getMethods();
    this.java_class = paramJavaClass;
    this.dir = paramString;
    class_name = paramJavaClass.getClassName();
    constant_pool = paramJavaClass.getConstantPool();
    int i = class_name.lastIndexOf('.');
    if (i > -1) {
      class_package = class_name.substring(0, i);
    } else {
      class_package = "";
    }
    ConstantHTML localConstantHTML = new ConstantHTML(paramString, class_name, class_package, arrayOfMethod, constant_pool);
    AttributeHTML localAttributeHTML = new AttributeHTML(paramString, class_name, constant_pool, localConstantHTML);
    MethodHTML localMethodHTML = new MethodHTML(paramString, class_name, arrayOfMethod, paramJavaClass.getFields(), localConstantHTML, localAttributeHTML);
    writeMainHTML(localAttributeHTML);
    new CodeHTML(paramString, class_name, arrayOfMethod, constant_pool, localConstantHTML);
    localAttributeHTML.close();
  }
  
  public static void _main(String[] paramArrayOfString)
  {
    String[] arrayOfString = new String[paramArrayOfString.length];
    int i = 0;
    ClassParser localClassParser = null;
    JavaClass localJavaClass = null;
    String str1 = null;
    char c = SecuritySupport.getSystemProperty("file.separator").toCharArray()[0];
    String str2 = "." + c;
    try
    {
      for (int j = 0; j < paramArrayOfString.length; j++) {
        if (paramArrayOfString[j].charAt(0) == '-')
        {
          if (paramArrayOfString[j].equals("-d"))
          {
            str2 = paramArrayOfString[(++j)];
            if (!str2.endsWith("" + c)) {
              str2 = str2 + c;
            }
            new File(str2).mkdirs();
          }
          else if (paramArrayOfString[j].equals("-zip"))
          {
            str1 = paramArrayOfString[(++j)];
          }
          else
          {
            System.out.println("Unknown option " + paramArrayOfString[j]);
          }
        }
        else {
          arrayOfString[(i++)] = paramArrayOfString[j];
        }
      }
      if (i == 0) {
        System.err.println("Class2HTML: No input files specified.");
      } else {
        for (j = 0; j < i; j++)
        {
          System.out.print("Processing " + arrayOfString[j] + "...");
          if (str1 == null) {
            localClassParser = new ClassParser(arrayOfString[j]);
          } else {
            localClassParser = new ClassParser(str1, arrayOfString[j]);
          }
          localJavaClass = localClassParser.parse();
          new Class2HTML(localJavaClass, str2);
          System.out.println("Done.");
        }
      }
    }
    catch (Exception localException)
    {
      System.out.println(localException);
      localException.printStackTrace(System.out);
    }
  }
  
  static String referenceClass(int paramInt)
  {
    String str = constant_pool.getConstantString(paramInt, (byte)7);
    str = Utility.compactClassName(str);
    str = Utility.compactClassName(str, class_package + ".", true);
    return "<A HREF=\"" + class_name + "_cp.html#cp" + paramInt + "\" TARGET=ConstantPool>" + str + "</A>";
  }
  
  static final String referenceType(String paramString)
  {
    String str = Utility.compactClassName(paramString);
    str = Utility.compactClassName(str, class_package + ".", true);
    int i = paramString.indexOf('[');
    if (i > -1) {
      paramString = paramString.substring(0, i);
    }
    if ((paramString.equals("int")) || (paramString.equals("short")) || (paramString.equals("boolean")) || (paramString.equals("void")) || (paramString.equals("char")) || (paramString.equals("byte")) || (paramString.equals("long")) || (paramString.equals("double")) || (paramString.equals("float"))) {
      return "<FONT COLOR=\"#00FF00\">" + paramString + "</FONT>";
    }
    return "<A HREF=\"" + paramString + ".html\" TARGET=_top>" + str + "</A>";
  }
  
  static String toHTML(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    try
    {
      for (int i = 0; i < paramString.length(); i++)
      {
        char c;
        switch (c = paramString.charAt(i))
        {
        case '<': 
          localStringBuffer.append("&lt;");
          break;
        case '>': 
          localStringBuffer.append("&gt;");
          break;
        case '\n': 
          localStringBuffer.append("\\n");
          break;
        case '\r': 
          localStringBuffer.append("\\r");
          break;
        default: 
          localStringBuffer.append(c);
        }
      }
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}
    return localStringBuffer.toString();
  }
  
  private void writeMainHTML(AttributeHTML paramAttributeHTML)
    throws IOException
  {
    PrintWriter localPrintWriter = new PrintWriter(new FileOutputStream(this.dir + class_name + ".html"));
    Attribute[] arrayOfAttribute = this.java_class.getAttributes();
    localPrintWriter.println("<HTML>\n<HEAD><TITLE>Documentation for " + class_name + "</TITLE>" + "</HEAD>\n" + "<FRAMESET BORDER=1 cols=\"30%,*\">\n" + "<FRAMESET BORDER=1 rows=\"80%,*\">\n" + "<FRAME NAME=\"ConstantPool\" SRC=\"" + class_name + "_cp.html" + "\"\n MARGINWIDTH=\"0\" " + "MARGINHEIGHT=\"0\" FRAMEBORDER=\"1\" SCROLLING=\"AUTO\">\n" + "<FRAME NAME=\"Attributes\" SRC=\"" + class_name + "_attributes.html" + "\"\n MARGINWIDTH=\"0\" " + "MARGINHEIGHT=\"0\" FRAMEBORDER=\"1\" SCROLLING=\"AUTO\">\n" + "</FRAMESET>\n" + "<FRAMESET BORDER=1 rows=\"80%,*\">\n" + "<FRAME NAME=\"Code\" SRC=\"" + class_name + "_code.html\"\n MARGINWIDTH=0 " + "MARGINHEIGHT=0 FRAMEBORDER=1 SCROLLING=\"AUTO\">\n" + "<FRAME NAME=\"Methods\" SRC=\"" + class_name + "_methods.html\"\n MARGINWIDTH=0 " + "MARGINHEIGHT=0 FRAMEBORDER=1 SCROLLING=\"AUTO\">\n" + "</FRAMESET></FRAMESET></HTML>");
    localPrintWriter.close();
    for (int i = 0; i < arrayOfAttribute.length; i++) {
      paramAttributeHTML.writeAttribute(arrayOfAttribute[i], "class" + i);
    }
  }
}
