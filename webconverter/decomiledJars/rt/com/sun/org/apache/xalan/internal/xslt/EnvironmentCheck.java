package com.sun.org.apache.xalan.internal.xslt;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;

public class EnvironmentCheck
{
  public static final String ERROR = "ERROR.";
  public static final String WARNING = "WARNING.";
  public static final String ERROR_FOUND = "At least one error was found!";
  public static final String VERSION = "version.";
  public static final String FOUNDCLASSES = "foundclasses.";
  public static final String CLASS_PRESENT = "present-unknown-version";
  public static final String CLASS_NOTPRESENT = "not-present";
  public String[] jarNames = { "xalan.jar", "xalansamples.jar", "xalanj1compat.jar", "xalanservlet.jar", "serializer.jar", "xerces.jar", "xercesImpl.jar", "testxsl.jar", "crimson.jar", "lotusxsl.jar", "jaxp.jar", "parser.jar", "dom.jar", "sax.jar", "xml.jar", "xml-apis.jar", "xsltc.jar" };
  private static final Map<Long, String> JARVERSIONS;
  protected PrintWriter outWriter = new PrintWriter(System.out, true);
  
  public EnvironmentCheck() {}
  
  public static void main(String[] paramArrayOfString)
  {
    PrintWriter localPrintWriter = new PrintWriter(System.out, true);
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if ("-out".equalsIgnoreCase(paramArrayOfString[i]))
      {
        i++;
        if (i < paramArrayOfString.length) {
          try
          {
            localPrintWriter = new PrintWriter(new FileWriter(paramArrayOfString[i], true));
          }
          catch (Exception localException)
          {
            System.err.println("# WARNING: -out " + paramArrayOfString[i] + " threw " + localException.toString());
          }
        } else {
          System.err.println("# WARNING: -out argument should have a filename, output sent to console");
        }
      }
    }
    EnvironmentCheck localEnvironmentCheck = new EnvironmentCheck();
    localEnvironmentCheck.checkEnvironment(localPrintWriter);
  }
  
  public boolean checkEnvironment(PrintWriter paramPrintWriter)
  {
    if (null != paramPrintWriter) {
      this.outWriter = paramPrintWriter;
    }
    Map localMap = getEnvironmentHash();
    boolean bool = writeEnvironmentReport(localMap);
    if (bool)
    {
      logMsg("# WARNING: Potential problems found in your environment!");
      logMsg("#    Check any 'ERROR' items above against the Xalan FAQs");
      logMsg("#    to correct potential problems with your classes/jars");
      logMsg("#    http://xml.apache.org/xalan-j/faq.html");
      if (null != this.outWriter) {
        this.outWriter.flush();
      }
      return false;
    }
    logMsg("# YAHOO! Your environment seems to be OK.");
    if (null != this.outWriter) {
      this.outWriter.flush();
    }
    return true;
  }
  
  public Map<String, Object> getEnvironmentHash()
  {
    HashMap localHashMap = new HashMap();
    checkJAXPVersion(localHashMap);
    checkProcessorVersion(localHashMap);
    checkParserVersion(localHashMap);
    checkAntVersion(localHashMap);
    if (!checkDOML3(localHashMap)) {
      checkDOMVersion(localHashMap);
    }
    checkSAXVersion(localHashMap);
    checkSystemProperties(localHashMap);
    return localHashMap;
  }
  
  protected boolean writeEnvironmentReport(Map<String, Object> paramMap)
  {
    if (null == paramMap)
    {
      logMsg("# ERROR: writeEnvironmentReport called with null Map");
      return false;
    }
    boolean bool = false;
    logMsg("#---- BEGIN writeEnvironmentReport($Revision: 1.10 $): Useful stuff found: ----");
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      try
      {
        if (str.startsWith("foundclasses."))
        {
          ArrayList localArrayList = (ArrayList)localEntry.getValue();
          bool |= logFoundJars(localArrayList, str);
        }
        else
        {
          if (str.startsWith("ERROR.")) {
            bool = true;
          }
          logMsg(str + "=" + paramMap.get(str));
        }
      }
      catch (Exception localException)
      {
        logMsg("Reading-" + str + "= threw: " + localException.toString());
      }
    }
    logMsg("#----- END writeEnvironmentReport: Useful properties found: -----");
    return bool;
  }
  
  protected boolean logFoundJars(List<Map> paramList, String paramString)
  {
    if ((null == paramList) || (paramList.size() < 1)) {
      return false;
    }
    boolean bool = false;
    logMsg("#---- BEGIN Listing XML-related jars in: " + paramString + " ----");
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
    {
      Map localMap = (Map)localIterator1.next();
      Iterator localIterator2 = localMap.entrySet().iterator();
      while (localIterator2.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator2.next();
        String str = (String)localEntry.getKey();
        try
        {
          if (str.startsWith("ERROR.")) {
            bool = true;
          }
          logMsg(str + "=" + (String)localEntry.getValue());
        }
        catch (Exception localException)
        {
          bool = true;
          logMsg("Reading-" + str + "= threw: " + localException.toString());
        }
      }
    }
    logMsg("#----- END Listing XML-related jars in: " + paramString + " -----");
    return bool;
  }
  
  public void appendEnvironmentReport(Node paramNode, Document paramDocument, Map<String, Object> paramMap)
  {
    if ((null == paramNode) || (null == paramDocument)) {
      return;
    }
    try
    {
      Element localElement1 = paramDocument.createElement("EnvironmentCheck");
      localElement1.setAttribute("version", "$Revision: 1.10 $");
      paramNode.appendChild(localElement1);
      if (null == paramMap)
      {
        Element localElement2 = paramDocument.createElement("status");
        localElement2.setAttribute("result", "ERROR");
        localElement2.appendChild(paramDocument.createTextNode("appendEnvironmentReport called with null Map!"));
        localElement1.appendChild(localElement2);
        return;
      }
      boolean bool = false;
      Element localElement3 = paramDocument.createElement("environment");
      localElement1.appendChild(localElement3);
      Object localObject1 = paramMap.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject1).next();
        String str = (String)localEntry.getKey();
        try
        {
          Object localObject2;
          if (str.startsWith("foundclasses."))
          {
            localObject2 = (List)localEntry.getValue();
            bool |= appendFoundJars(localElement3, paramDocument, (List)localObject2, str);
          }
          else
          {
            if (str.startsWith("ERROR.")) {
              bool = true;
            }
            localObject2 = paramDocument.createElement("item");
            ((Element)localObject2).setAttribute("key", str);
            ((Element)localObject2).appendChild(paramDocument.createTextNode((String)paramMap.get(str)));
            localElement3.appendChild((Node)localObject2);
          }
        }
        catch (Exception localException2)
        {
          bool = true;
          Element localElement4 = paramDocument.createElement("item");
          localElement4.setAttribute("key", str);
          localElement4.appendChild(paramDocument.createTextNode("ERROR. Reading " + str + " threw: " + localException2.toString()));
          localElement3.appendChild(localElement4);
        }
      }
      localObject1 = paramDocument.createElement("status");
      ((Element)localObject1).setAttribute("result", bool ? "ERROR" : "OK");
      localElement1.appendChild((Node)localObject1);
    }
    catch (Exception localException1)
    {
      System.err.println("appendEnvironmentReport threw: " + localException1.toString());
      localException1.printStackTrace();
    }
  }
  
  protected boolean appendFoundJars(Node paramNode, Document paramDocument, List<Map> paramList, String paramString)
  {
    if ((null == paramList) || (paramList.size() < 1)) {
      return false;
    }
    boolean bool = false;
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
    {
      Map localMap = (Map)localIterator1.next();
      Iterator localIterator2 = localMap.entrySet().iterator();
      while (localIterator2.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator2.next();
        String str = (String)localEntry.getKey();
        try
        {
          if (str.startsWith("ERROR.")) {
            bool = true;
          }
          Element localElement1 = paramDocument.createElement("foundJar");
          localElement1.setAttribute("name", str.substring(0, str.indexOf("-")));
          localElement1.setAttribute("desc", str.substring(str.indexOf("-") + 1));
          localElement1.appendChild(paramDocument.createTextNode((String)localEntry.getValue()));
          paramNode.appendChild(localElement1);
        }
        catch (Exception localException)
        {
          bool = true;
          Element localElement2 = paramDocument.createElement("foundJar");
          localElement2.appendChild(paramDocument.createTextNode("ERROR. Reading " + str + " threw: " + localException.toString()));
          paramNode.appendChild(localElement2);
        }
      }
    }
    return bool;
  }
  
  protected void checkSystemProperties(Map<String, Object> paramMap)
  {
    if (null == paramMap) {
      paramMap = new HashMap();
    }
    try
    {
      String str1 = SecuritySupport.getSystemProperty("java.version");
      paramMap.put("java.version", str1);
    }
    catch (SecurityException localSecurityException1)
    {
      paramMap.put("java.version", "WARNING: SecurityException thrown accessing system version properties");
    }
    try
    {
      String str2 = SecuritySupport.getSystemProperty("java.class.path");
      paramMap.put("java.class.path", str2);
      List localList = checkPathForJars(str2, this.jarNames);
      if (null != localList) {
        paramMap.put("foundclasses.java.class.path", localList);
      }
      String str3 = SecuritySupport.getSystemProperty("sun.boot.class.path");
      if (null != str3)
      {
        paramMap.put("sun.boot.class.path", str3);
        localList = checkPathForJars(str3, this.jarNames);
        if (null != localList) {
          paramMap.put("foundclasses.sun.boot.class.path", localList);
        }
      }
      str3 = SecuritySupport.getSystemProperty("java.ext.dirs");
      if (null != str3)
      {
        paramMap.put("java.ext.dirs", str3);
        localList = checkPathForJars(str3, this.jarNames);
        if (null != localList) {
          paramMap.put("foundclasses.java.ext.dirs", localList);
        }
      }
    }
    catch (SecurityException localSecurityException2)
    {
      paramMap.put("java.class.path", "WARNING: SecurityException thrown accessing system classpath properties");
    }
  }
  
  protected List<Map> checkPathForJars(String paramString, String[] paramArrayOfString)
  {
    if ((null == paramString) || (null == paramArrayOfString) || (0 == paramString.length()) || (0 == paramArrayOfString.length)) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      for (int i = 0; i < paramArrayOfString.length; i++) {
        if (str.indexOf(paramArrayOfString[i]) > -1)
        {
          File localFile = new File(str);
          if (localFile.exists())
          {
            try
            {
              HashMap localHashMap1 = new HashMap(2);
              localHashMap1.put(paramArrayOfString[i] + "-path", localFile.getAbsolutePath());
              if (!"xalan.jar".equalsIgnoreCase(paramArrayOfString[i])) {
                localHashMap1.put(paramArrayOfString[i] + "-apparent.version", getApparentVersion(paramArrayOfString[i], localFile.length()));
              }
              localArrayList.add(localHashMap1);
            }
            catch (Exception localException) {}
          }
          else
          {
            HashMap localHashMap2 = new HashMap(2);
            localHashMap2.put(paramArrayOfString[i] + "-path", "WARNING. Classpath entry: " + str + " does not exist");
            localHashMap2.put(paramArrayOfString[i] + "-apparent.version", "not-present");
            localArrayList.add(localHashMap2);
          }
        }
      }
    }
    return localArrayList;
  }
  
  protected String getApparentVersion(String paramString, long paramLong)
  {
    String str = (String)JARVERSIONS.get(new Long(paramLong));
    if ((null != str) && (str.startsWith(paramString))) {
      return str;
    }
    if (("xerces.jar".equalsIgnoreCase(paramString)) || ("xercesImpl.jar".equalsIgnoreCase(paramString))) {
      return paramString + " " + "WARNING." + "present-unknown-version";
    }
    return paramString + " " + "present-unknown-version";
  }
  
  protected void checkJAXPVersion(Map<String, Object> paramMap)
  {
    if (null == paramMap) {
      paramMap = new HashMap();
    }
    Class localClass = null;
    try
    {
      String str = "javax.xml.stream.XMLStreamConstants";
      localClass = ObjectFactory.findProviderClass("javax.xml.stream.XMLStreamConstants", true);
      paramMap.put("version.JAXP", "1.4");
    }
    catch (Exception localException)
    {
      paramMap.put("ERROR.version.JAXP", "1.3");
      paramMap.put("ERROR.", "At least one error was found!");
    }
  }
  
  protected void checkProcessorVersion(Map<String, Object> paramMap)
  {
    if (null == paramMap) {
      paramMap = new HashMap();
    }
    Object localObject1;
    Object localObject2;
    Object localObject3;
    try
    {
      String str1 = "com.sun.org.apache.xalan.internal.xslt.XSLProcessorVersion";
      localObject1 = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.xslt.XSLProcessorVersion", true);
      localObject2 = new StringBuffer();
      localObject3 = ((Class)localObject1).getField("PRODUCT");
      ((StringBuffer)localObject2).append(((Field)localObject3).get(null));
      ((StringBuffer)localObject2).append(';');
      localObject3 = ((Class)localObject1).getField("LANGUAGE");
      ((StringBuffer)localObject2).append(((Field)localObject3).get(null));
      ((StringBuffer)localObject2).append(';');
      localObject3 = ((Class)localObject1).getField("S_VERSION");
      ((StringBuffer)localObject2).append(((Field)localObject3).get(null));
      ((StringBuffer)localObject2).append(';');
      paramMap.put("version.xalan1", ((StringBuffer)localObject2).toString());
    }
    catch (Exception localException1)
    {
      paramMap.put("version.xalan1", "not-present");
    }
    try
    {
      String str2 = "com.sun.org.apache.xalan.internal.processor.XSLProcessorVersion";
      localObject1 = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.processor.XSLProcessorVersion", true);
      localObject2 = new StringBuffer();
      localObject3 = ((Class)localObject1).getField("S_VERSION");
      ((StringBuffer)localObject2).append(((Field)localObject3).get(null));
      paramMap.put("version.xalan2x", ((StringBuffer)localObject2).toString());
    }
    catch (Exception localException2)
    {
      paramMap.put("version.xalan2x", "not-present");
    }
    try
    {
      String str3 = "com.sun.org.apache.xalan.internal.Version";
      localObject1 = "getVersion";
      localObject2 = new Class[0];
      localObject3 = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.Version", true);
      Method localMethod = ((Class)localObject3).getMethod("getVersion", (Class[])localObject2);
      Object localObject4 = localMethod.invoke(null, new Object[0]);
      paramMap.put("version.xalan2_2", (String)localObject4);
    }
    catch (Exception localException3)
    {
      paramMap.put("version.xalan2_2", "not-present");
    }
  }
  
  protected void checkParserVersion(Map<String, Object> paramMap)
  {
    if (null == paramMap) {
      paramMap = new HashMap();
    }
    Class localClass;
    Field localField;
    String str4;
    try
    {
      String str1 = "com.sun.org.apache.xerces.internal.framework.Version";
      localClass = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.framework.Version", true);
      localField = localClass.getField("fVersion");
      str4 = (String)localField.get(null);
      paramMap.put("version.xerces1", str4);
    }
    catch (Exception localException1)
    {
      paramMap.put("version.xerces1", "not-present");
    }
    try
    {
      String str2 = "com.sun.org.apache.xerces.internal.impl.Version";
      localClass = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.impl.Version", true);
      localField = localClass.getField("fVersion");
      str4 = (String)localField.get(null);
      paramMap.put("version.xerces2", str4);
    }
    catch (Exception localException2)
    {
      paramMap.put("version.xerces2", "not-present");
    }
    try
    {
      String str3 = "org.apache.crimson.parser.Parser2";
      localClass = ObjectFactory.findProviderClass("org.apache.crimson.parser.Parser2", true);
      paramMap.put("version.crimson", "present-unknown-version");
    }
    catch (Exception localException3)
    {
      paramMap.put("version.crimson", "not-present");
    }
  }
  
  protected void checkAntVersion(Map<String, Object> paramMap)
  {
    if (null == paramMap) {
      paramMap = new HashMap();
    }
    try
    {
      String str1 = "org.apache.tools.ant.Main";
      String str2 = "getAntVersion";
      Class[] arrayOfClass = new Class[0];
      Class localClass = ObjectFactory.findProviderClass("org.apache.tools.ant.Main", true);
      Method localMethod = localClass.getMethod("getAntVersion", arrayOfClass);
      Object localObject = localMethod.invoke(null, new Object[0]);
      paramMap.put("version.ant", (String)localObject);
    }
    catch (Exception localException)
    {
      paramMap.put("version.ant", "not-present");
    }
  }
  
  protected boolean checkDOML3(Map<String, Object> paramMap)
  {
    if (null == paramMap) {
      paramMap = new HashMap();
    }
    String str1 = "org.w3c.dom.Document";
    String str2 = "getDoctype";
    try
    {
      Class localClass = ObjectFactory.findProviderClass("org.w3c.dom.Document", true);
      Method localMethod = localClass.getMethod("getDoctype", (Class[])null);
      paramMap.put("version.DOM", "3.0");
      return true;
    }
    catch (Exception localException) {}
    return false;
  }
  
  protected void checkDOMVersion(Map<String, Object> paramMap)
  {
    if (null == paramMap) {
      paramMap = new HashMap();
    }
    String str1 = "org.w3c.dom.Document";
    String str2 = "createElementNS";
    String str3 = "getDoctype";
    String str4 = "org.w3c.dom.Node";
    String str5 = "supported";
    String str6 = "org.w3c.dom.Node";
    String str7 = "isSupported";
    Class[] arrayOfClass = { String.class, String.class };
    try
    {
      Class localClass = ObjectFactory.findProviderClass("org.w3c.dom.Document", true);
      Method localMethod = localClass.getMethod("createElementNS", arrayOfClass);
      paramMap.put("version.DOM", "2.0");
      try
      {
        localClass = ObjectFactory.findProviderClass("org.w3c.dom.Node", true);
        localMethod = localClass.getMethod("supported", arrayOfClass);
        paramMap.put("ERROR.version.DOM.draftlevel", "2.0wd");
        paramMap.put("ERROR.", "At least one error was found!");
      }
      catch (Exception localException2)
      {
        try
        {
          localClass = ObjectFactory.findProviderClass("org.w3c.dom.Node", true);
          localMethod = localClass.getMethod("isSupported", arrayOfClass);
          paramMap.put("version.DOM.draftlevel", "2.0fd");
        }
        catch (Exception localException3)
        {
          paramMap.put("ERROR.version.DOM.draftlevel", "2.0unknown");
          paramMap.put("ERROR.", "At least one error was found!");
        }
      }
    }
    catch (Exception localException1)
    {
      paramMap.put("ERROR.version.DOM", "ERROR attempting to load DOM level 2 class: " + localException1.toString());
      paramMap.put("ERROR.", "At least one error was found!");
    }
  }
  
  protected void checkSAXVersion(Map<String, Object> paramMap)
  {
    if (null == paramMap) {
      paramMap = new HashMap();
    }
    String str1 = "org.xml.sax.Parser";
    String str2 = "parse";
    String str3 = "org.xml.sax.XMLReader";
    String str4 = "parse";
    String str5 = "org.xml.sax.helpers.AttributesImpl";
    String str6 = "setAttributes";
    Class[] arrayOfClass1 = { String.class };
    Class[] arrayOfClass2 = { Attributes.class };
    try
    {
      Class localClass = ObjectFactory.findProviderClass("org.xml.sax.helpers.AttributesImpl", true);
      localObject1 = localClass.getMethod("setAttributes", arrayOfClass2);
      paramMap.put("version.SAX", "2.0");
    }
    catch (Exception localException1)
    {
      Object localObject1;
      paramMap.put("ERROR.version.SAX", "ERROR attempting to load SAX version 2 class: " + localException1.toString());
      paramMap.put("ERROR.", "At least one error was found!");
      try
      {
        localObject1 = ObjectFactory.findProviderClass("org.xml.sax.XMLReader", true);
        localObject2 = ((Class)localObject1).getMethod("parse", arrayOfClass1);
        paramMap.put("version.SAX-backlevel", "2.0beta2-or-earlier");
      }
      catch (Exception localException2)
      {
        Object localObject2;
        paramMap.put("ERROR.version.SAX", "ERROR attempting to load SAX version 2 class: " + localException1.toString());
        paramMap.put("ERROR.", "At least one error was found!");
        try
        {
          localObject2 = ObjectFactory.findProviderClass("org.xml.sax.Parser", true);
          Method localMethod = ((Class)localObject2).getMethod("parse", arrayOfClass1);
          paramMap.put("version.SAX-backlevel", "1.0");
        }
        catch (Exception localException3)
        {
          paramMap.put("ERROR.version.SAX-backlevel", "ERROR attempting to load SAX version 1 class: " + localException3.toString());
        }
      }
    }
  }
  
  protected void logMsg(String paramString)
  {
    this.outWriter.println(paramString);
  }
  
  static
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put(new Long(857192L), "xalan.jar from xalan-j_1_1");
    localHashMap.put(new Long(440237L), "xalan.jar from xalan-j_1_2");
    localHashMap.put(new Long(436094L), "xalan.jar from xalan-j_1_2_1");
    localHashMap.put(new Long(426249L), "xalan.jar from xalan-j_1_2_2");
    localHashMap.put(new Long(702536L), "xalan.jar from xalan-j_2_0_0");
    localHashMap.put(new Long(720930L), "xalan.jar from xalan-j_2_0_1");
    localHashMap.put(new Long(732330L), "xalan.jar from xalan-j_2_1_0");
    localHashMap.put(new Long(872241L), "xalan.jar from xalan-j_2_2_D10");
    localHashMap.put(new Long(882739L), "xalan.jar from xalan-j_2_2_D11");
    localHashMap.put(new Long(923866L), "xalan.jar from xalan-j_2_2_0");
    localHashMap.put(new Long(905872L), "xalan.jar from xalan-j_2_3_D1");
    localHashMap.put(new Long(906122L), "xalan.jar from xalan-j_2_3_0");
    localHashMap.put(new Long(906248L), "xalan.jar from xalan-j_2_3_1");
    localHashMap.put(new Long(983377L), "xalan.jar from xalan-j_2_4_D1");
    localHashMap.put(new Long(997276L), "xalan.jar from xalan-j_2_4_0");
    localHashMap.put(new Long(1031036L), "xalan.jar from xalan-j_2_4_1");
    localHashMap.put(new Long(596540L), "xsltc.jar from xalan-j_2_2_0");
    localHashMap.put(new Long(590247L), "xsltc.jar from xalan-j_2_3_D1");
    localHashMap.put(new Long(589914L), "xsltc.jar from xalan-j_2_3_0");
    localHashMap.put(new Long(589915L), "xsltc.jar from xalan-j_2_3_1");
    localHashMap.put(new Long(1306667L), "xsltc.jar from xalan-j_2_4_D1");
    localHashMap.put(new Long(1328227L), "xsltc.jar from xalan-j_2_4_0");
    localHashMap.put(new Long(1344009L), "xsltc.jar from xalan-j_2_4_1");
    localHashMap.put(new Long(1348361L), "xsltc.jar from xalan-j_2_5_D1");
    localHashMap.put(new Long(1268634L), "xsltc.jar-bundled from xalan-j_2_3_0");
    localHashMap.put(new Long(100196L), "xml-apis.jar from xalan-j_2_2_0 or xalan-j_2_3_D1");
    localHashMap.put(new Long(108484L), "xml-apis.jar from xalan-j_2_3_0, or xalan-j_2_3_1 from xml-commons-1.0.b2");
    localHashMap.put(new Long(109049L), "xml-apis.jar from xalan-j_2_4_0 from xml-commons RIVERCOURT1 branch");
    localHashMap.put(new Long(113749L), "xml-apis.jar from xalan-j_2_4_1 from factoryfinder-build of xml-commons RIVERCOURT1");
    localHashMap.put(new Long(124704L), "xml-apis.jar from tck-jaxp-1_2_0 branch of xml-commons");
    localHashMap.put(new Long(124724L), "xml-apis.jar from tck-jaxp-1_2_0 branch of xml-commons, tag: xml-commons-external_1_2_01");
    localHashMap.put(new Long(194205L), "xml-apis.jar from head branch of xml-commons, tag: xml-commons-external_1_3_02");
    localHashMap.put(new Long(424490L), "xalan.jar from Xerces Tools releases - ERROR:DO NOT USE!");
    localHashMap.put(new Long(1591855L), "xerces.jar from xalan-j_1_1 from xerces-1...");
    localHashMap.put(new Long(1498679L), "xerces.jar from xalan-j_1_2 from xerces-1_2_0.bin");
    localHashMap.put(new Long(1484896L), "xerces.jar from xalan-j_1_2_1 from xerces-1_2_1.bin");
    localHashMap.put(new Long(804460L), "xerces.jar from xalan-j_1_2_2 from xerces-1_2_2.bin");
    localHashMap.put(new Long(1499244L), "xerces.jar from xalan-j_2_0_0 from xerces-1_2_3.bin");
    localHashMap.put(new Long(1605266L), "xerces.jar from xalan-j_2_0_1 from xerces-1_3_0.bin");
    localHashMap.put(new Long(904030L), "xerces.jar from xalan-j_2_1_0 from xerces-1_4.bin");
    localHashMap.put(new Long(904030L), "xerces.jar from xerces-1_4_0.bin");
    localHashMap.put(new Long(1802885L), "xerces.jar from xerces-1_4_2.bin");
    localHashMap.put(new Long(1734594L), "xerces.jar from Xerces-J-bin.2.0.0.beta3");
    localHashMap.put(new Long(1808883L), "xerces.jar from xalan-j_2_2_D10,D11,D12 or xerces-1_4_3.bin");
    localHashMap.put(new Long(1812019L), "xerces.jar from xalan-j_2_2_0");
    localHashMap.put(new Long(1720292L), "xercesImpl.jar from xalan-j_2_3_D1");
    localHashMap.put(new Long(1730053L), "xercesImpl.jar from xalan-j_2_3_0 or xalan-j_2_3_1 from xerces-2_0_0");
    localHashMap.put(new Long(1728861L), "xercesImpl.jar from xalan-j_2_4_D1 from xerces-2_0_1");
    localHashMap.put(new Long(972027L), "xercesImpl.jar from xalan-j_2_4_0 from xerces-2_1");
    localHashMap.put(new Long(831587L), "xercesImpl.jar from xalan-j_2_4_1 from xerces-2_2");
    localHashMap.put(new Long(891817L), "xercesImpl.jar from xalan-j_2_5_D1 from xerces-2_3");
    localHashMap.put(new Long(895924L), "xercesImpl.jar from xerces-2_4");
    localHashMap.put(new Long(1010806L), "xercesImpl.jar from Xerces-J-bin.2.6.2");
    localHashMap.put(new Long(1203860L), "xercesImpl.jar from Xerces-J-bin.2.7.1");
    localHashMap.put(new Long(37485L), "xalanj1compat.jar from xalan-j_2_0_0");
    localHashMap.put(new Long(38100L), "xalanj1compat.jar from xalan-j_2_0_1");
    localHashMap.put(new Long(18779L), "xalanservlet.jar from xalan-j_2_0_0");
    localHashMap.put(new Long(21453L), "xalanservlet.jar from xalan-j_2_0_1");
    localHashMap.put(new Long(24826L), "xalanservlet.jar from xalan-j_2_3_1 or xalan-j_2_4_1");
    localHashMap.put(new Long(24831L), "xalanservlet.jar from xalan-j_2_4_1");
    localHashMap.put(new Long(5618L), "jaxp.jar from jaxp1.0.1");
    localHashMap.put(new Long(136133L), "parser.jar from jaxp1.0.1");
    localHashMap.put(new Long(28404L), "jaxp.jar from jaxp-1.1");
    localHashMap.put(new Long(187162L), "crimson.jar from jaxp-1.1");
    localHashMap.put(new Long(801714L), "xalan.jar from jaxp-1.1");
    localHashMap.put(new Long(196399L), "crimson.jar from crimson-1.1.1");
    localHashMap.put(new Long(33323L), "jaxp.jar from crimson-1.1.1 or jakarta-ant-1.4.1b1");
    localHashMap.put(new Long(152717L), "crimson.jar from crimson-1.1.2beta2");
    localHashMap.put(new Long(88143L), "xml-apis.jar from crimson-1.1.2beta2");
    localHashMap.put(new Long(206384L), "crimson.jar from crimson-1.1.3 or jakarta-ant-1.4.1b1");
    localHashMap.put(new Long(136198L), "parser.jar from jakarta-ant-1.3 or 1.2");
    localHashMap.put(new Long(5537L), "jaxp.jar from jakarta-ant-1.3 or 1.2");
    JARVERSIONS = Collections.unmodifiableMap(localHashMap);
  }
}
