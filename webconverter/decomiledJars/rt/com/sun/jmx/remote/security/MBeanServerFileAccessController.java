package com.sun.jmx.remote.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.security.auth.Subject;

public class MBeanServerFileAccessController
  extends MBeanServerAccessController
{
  static final String READONLY = "readonly";
  static final String READWRITE = "readwrite";
  static final String CREATE = "create";
  static final String UNREGISTER = "unregister";
  private Map<String, Access> accessMap;
  private Properties originalProps;
  private String accessFileName;
  
  public MBeanServerFileAccessController(String paramString)
    throws IOException
  {
    this.accessFileName = paramString;
    Properties localProperties = propertiesFromFile(paramString);
    parseProperties(localProperties);
  }
  
  public MBeanServerFileAccessController(String paramString, MBeanServer paramMBeanServer)
    throws IOException
  {
    this(paramString);
    setMBeanServer(paramMBeanServer);
  }
  
  public MBeanServerFileAccessController(Properties paramProperties)
    throws IOException
  {
    if (paramProperties == null) {
      throw new IllegalArgumentException("Null properties");
    }
    this.originalProps = paramProperties;
    parseProperties(paramProperties);
  }
  
  public MBeanServerFileAccessController(Properties paramProperties, MBeanServer paramMBeanServer)
    throws IOException
  {
    this(paramProperties);
    setMBeanServer(paramMBeanServer);
  }
  
  public void checkRead()
  {
    checkAccess(AccessType.READ, null);
  }
  
  public void checkWrite()
  {
    checkAccess(AccessType.WRITE, null);
  }
  
  public void checkCreate(String paramString)
  {
    checkAccess(AccessType.CREATE, paramString);
  }
  
  public void checkUnregister(ObjectName paramObjectName)
  {
    checkAccess(AccessType.UNREGISTER, null);
  }
  
  public synchronized void refresh()
    throws IOException
  {
    Properties localProperties;
    if (this.accessFileName == null) {
      localProperties = this.originalProps;
    } else {
      localProperties = propertiesFromFile(this.accessFileName);
    }
    parseProperties(localProperties);
  }
  
  private static Properties propertiesFromFile(String paramString)
    throws IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramString);
    try
    {
      Properties localProperties1 = new Properties();
      localProperties1.load(localFileInputStream);
      Properties localProperties2 = localProperties1;
      return localProperties2;
    }
    finally
    {
      localFileInputStream.close();
    }
  }
  
  private synchronized void checkAccess(AccessType paramAccessType, String paramString)
  {
    final AccessControlContext localAccessControlContext = AccessController.getContext();
    Subject localSubject = (Subject)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Subject run()
      {
        return Subject.getSubject(localAccessControlContext);
      }
    });
    if (localSubject == null) {
      return;
    }
    Set localSet = localSubject.getPrincipals();
    String str = null;
    Object localObject1 = localSet.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Principal)((Iterator)localObject1).next();
      Access localAccess = (Access)this.accessMap.get(((Principal)localObject2).getName());
      if (localAccess != null)
      {
        boolean bool;
        switch (2.$SwitchMap$com$sun$jmx$remote$security$MBeanServerFileAccessController$AccessType[paramAccessType.ordinal()])
        {
        case 1: 
          bool = true;
          break;
        case 2: 
          bool = localAccess.write;
          break;
        case 3: 
          bool = localAccess.unregister;
          if ((!bool) && (localAccess.write)) {
            str = "unregister";
          }
          break;
        case 4: 
          bool = checkCreateAccess(localAccess, paramString);
          if ((!bool) && (localAccess.write)) {
            str = "create " + paramString;
          }
          break;
        default: 
          throw new AssertionError();
        }
        if (bool) {
          return;
        }
      }
    }
    localObject1 = new SecurityException("Access denied! Invalid access level for requested MBeanServer operation.");
    if (str != null)
    {
      localObject2 = new SecurityException("Access property for this identity should be similar to: readwrite " + str);
      ((SecurityException)localObject1).initCause((Throwable)localObject2);
    }
    throw ((Throwable)localObject1);
  }
  
  private static boolean checkCreateAccess(Access paramAccess, String paramString)
  {
    for (String str : paramAccess.createPatterns) {
      if (classNameMatch(str, paramString)) {
        return true;
      }
    }
    return false;
  }
  
  private static boolean classNameMatch(String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString1, "*", true);
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      if (str.equals("*")) {
        localStringBuilder.append("[^.]*");
      } else {
        localStringBuilder.append(Pattern.quote(str));
      }
    }
    return paramString2.matches(localStringBuilder.toString());
  }
  
  private void parseProperties(Properties paramProperties)
  {
    this.accessMap = new HashMap();
    Iterator localIterator = paramProperties.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getKey();
      String str2 = (String)localEntry.getValue();
      Access localAccess = Parser.parseAccess(str1, str2);
      this.accessMap.put(str1, localAccess);
    }
  }
  
  private static class Access
  {
    final boolean write;
    final String[] createPatterns;
    private boolean unregister;
    private final String[] NO_STRINGS = new String[0];
    
    Access(boolean paramBoolean1, boolean paramBoolean2, List<String> paramList)
    {
      this.write = paramBoolean1;
      int i = paramList == null ? 0 : paramList.size();
      if (i == 0) {
        this.createPatterns = this.NO_STRINGS;
      } else {
        this.createPatterns = ((String[])paramList.toArray(new String[i]));
      }
      this.unregister = paramBoolean2;
    }
  }
  
  private static enum AccessType
  {
    READ,  WRITE,  CREATE,  UNREGISTER;
    
    private AccessType() {}
  }
  
  private static class Parser
  {
    private static final int EOS = -1;
    private final String identity;
    private final String s;
    private final int len;
    private int i;
    private int c;
    
    private Parser(String paramString1, String paramString2)
    {
      this.identity = paramString1;
      this.s = paramString2;
      this.len = paramString2.length();
      this.i = 0;
      if (this.i < this.len) {
        this.c = paramString2.codePointAt(this.i);
      } else {
        this.c = -1;
      }
    }
    
    static MBeanServerFileAccessController.Access parseAccess(String paramString1, String paramString2)
    {
      return new Parser(paramString1, paramString2).parseAccess();
    }
    
    private MBeanServerFileAccessController.Access parseAccess()
    {
      skipSpace();
      String str = parseWord();
      MBeanServerFileAccessController.Access localAccess;
      if (str.equals("readonly")) {
        localAccess = new MBeanServerFileAccessController.Access(false, false, null);
      } else if (str.equals("readwrite")) {
        localAccess = parseReadWrite();
      } else {
        throw syntax("Expected readonly or readwrite: " + str);
      }
      if (this.c != -1) {
        throw syntax("Extra text at end of line");
      }
      return localAccess;
    }
    
    private MBeanServerFileAccessController.Access parseReadWrite()
    {
      ArrayList localArrayList = new ArrayList();
      boolean bool = false;
      for (;;)
      {
        skipSpace();
        if (this.c == -1) {
          break;
        }
        String str = parseWord();
        if (str.equals("unregister")) {
          bool = true;
        } else if (str.equals("create")) {
          parseCreate(localArrayList);
        } else {
          throw syntax("Unrecognized keyword " + str);
        }
      }
      return new MBeanServerFileAccessController.Access(true, bool, localArrayList);
    }
    
    private void parseCreate(List<String> paramList)
    {
      for (;;)
      {
        skipSpace();
        paramList.add(parseClassName());
        skipSpace();
        if (this.c != 44) {
          break;
        }
        next();
      }
    }
    
    private String parseClassName()
    {
      int j = this.i;
      int k = 0;
      for (;;)
      {
        if (this.c == 46)
        {
          if (k == 0) {
            throw syntax("Bad . in class name");
          }
          k = 0;
        }
        else
        {
          if ((this.c != 42) && (!Character.isJavaIdentifierPart(this.c))) {
            break;
          }
          k = 1;
        }
        next();
      }
      String str = this.s.substring(j, this.i);
      if (k == 0) {
        throw syntax("Bad class name " + str);
      }
      return str;
    }
    
    private void next()
    {
      if (this.c != -1)
      {
        this.i += Character.charCount(this.c);
        if (this.i < this.len) {
          this.c = this.s.codePointAt(this.i);
        } else {
          this.c = -1;
        }
      }
    }
    
    private void skipSpace()
    {
      while (Character.isWhitespace(this.c)) {
        next();
      }
    }
    
    private String parseWord()
    {
      skipSpace();
      if (this.c == -1) {
        throw syntax("Expected word at end of line");
      }
      int j = this.i;
      while ((this.c != -1) && (!Character.isWhitespace(this.c))) {
        next();
      }
      String str = this.s.substring(j, this.i);
      skipSpace();
      return str;
    }
    
    private IllegalArgumentException syntax(String paramString)
    {
      return new IllegalArgumentException(paramString + " [" + this.identity + " " + this.s + "]");
    }
    
    static
    {
      assert (!Character.isWhitespace(-1));
    }
  }
}
