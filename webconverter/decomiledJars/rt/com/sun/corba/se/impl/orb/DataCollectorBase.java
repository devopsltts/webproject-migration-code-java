package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.PropertyParser;
import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public abstract class DataCollectorBase
  implements DataCollector
{
  private PropertyParser parser;
  private Set propertyNames;
  private Set propertyPrefixes;
  private Set URLPropertyNames = new HashSet();
  protected String localHostName;
  protected String configurationHostName;
  private boolean setParserCalled;
  private Properties originalProps;
  private Properties resultProps;
  
  public DataCollectorBase(Properties paramProperties, String paramString1, String paramString2)
  {
    this.URLPropertyNames.add("org.omg.CORBA.ORBInitialServices");
    this.propertyNames = new HashSet();
    this.propertyNames.add("org.omg.CORBA.ORBInitRef");
    this.propertyPrefixes = new HashSet();
    this.originalProps = paramProperties;
    this.localHostName = paramString1;
    this.configurationHostName = paramString2;
    this.setParserCalled = false;
    this.resultProps = new Properties();
  }
  
  public boolean initialHostIsLocal()
  {
    checkSetParserCalled();
    return this.localHostName.equals(this.resultProps.getProperty("org.omg.CORBA.ORBInitialHost"));
  }
  
  public void setParser(PropertyParser paramPropertyParser)
  {
    Iterator localIterator = paramPropertyParser.iterator();
    while (localIterator.hasNext())
    {
      ParserAction localParserAction = (ParserAction)localIterator.next();
      if (localParserAction.isPrefix()) {
        this.propertyPrefixes.add(localParserAction.getPropertyName());
      } else {
        this.propertyNames.add(localParserAction.getPropertyName());
      }
    }
    collect();
    this.setParserCalled = true;
  }
  
  public Properties getProperties()
  {
    checkSetParserCalled();
    return this.resultProps;
  }
  
  public abstract boolean isApplet();
  
  protected abstract void collect();
  
  protected void checkPropertyDefaults()
  {
    String str1 = this.resultProps.getProperty("org.omg.CORBA.ORBInitialHost");
    if ((str1 == null) || (str1.equals(""))) {
      setProperty("org.omg.CORBA.ORBInitialHost", this.configurationHostName);
    }
    String str2 = this.resultProps.getProperty("com.sun.CORBA.ORBServerHost");
    if ((str2 == null) || (str2.equals("")) || (str2.equals("0.0.0.0")) || (str2.equals("::")) || (str2.toLowerCase().equals("::ffff:0.0.0.0")))
    {
      setProperty("com.sun.CORBA.ORBServerHost", this.localHostName);
      setProperty("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", "com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces");
    }
  }
  
  protected void findPropertiesFromArgs(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {
      return;
    }
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str2 = null;
      String str1 = null;
      if ((paramArrayOfString[i] != null) && (paramArrayOfString[i].startsWith("-ORB")))
      {
        String str3 = paramArrayOfString[i].substring(1);
        str1 = findMatchingPropertyName(this.propertyNames, str3);
        if ((str1 != null) && (i + 1 < paramArrayOfString.length) && (paramArrayOfString[(i + 1)] != null)) {
          str2 = paramArrayOfString[(++i)];
        }
      }
      if (str2 != null) {
        setProperty(str1, str2);
      }
    }
  }
  
  protected void findPropertiesFromApplet(final Applet paramApplet)
  {
    if (paramApplet == null) {
      return;
    }
    PropertyCallback local1 = new PropertyCallback()
    {
      public String get(String paramAnonymousString)
      {
        return paramApplet.getParameter(paramAnonymousString);
      }
    };
    findPropertiesByName(this.propertyNames.iterator(), local1);
    PropertyCallback local2 = new PropertyCallback()
    {
      public String get(String paramAnonymousString)
      {
        String str = DataCollectorBase.this.resultProps.getProperty(paramAnonymousString);
        if (str == null) {
          return null;
        }
        try
        {
          URL localURL = new URL(paramApplet.getDocumentBase(), str);
          return localURL.toExternalForm();
        }
        catch (MalformedURLException localMalformedURLException) {}
        return str;
      }
    };
    findPropertiesByName(this.URLPropertyNames.iterator(), local2);
  }
  
  private void doProperties(final Properties paramProperties)
  {
    PropertyCallback local3 = new PropertyCallback()
    {
      public String get(String paramAnonymousString)
      {
        return paramProperties.getProperty(paramAnonymousString);
      }
    };
    findPropertiesByName(this.propertyNames.iterator(), local3);
    findPropertiesByPrefix(this.propertyPrefixes, makeIterator(paramProperties.propertyNames()), local3);
  }
  
  protected void findPropertiesFromFile()
  {
    Properties localProperties = getFileProperties();
    if (localProperties == null) {
      return;
    }
    doProperties(localProperties);
  }
  
  protected void findPropertiesFromProperties()
  {
    if (this.originalProps == null) {
      return;
    }
    doProperties(this.originalProps);
  }
  
  protected void findPropertiesFromSystem()
  {
    Set localSet1 = getCORBAPrefixes(this.propertyNames);
    Set localSet2 = getCORBAPrefixes(this.propertyPrefixes);
    PropertyCallback local4 = new PropertyCallback()
    {
      public String get(String paramAnonymousString)
      {
        return DataCollectorBase.getSystemProperty(paramAnonymousString);
      }
    };
    findPropertiesByName(localSet1.iterator(), local4);
    findPropertiesByPrefix(localSet2, getSystemPropertyNames(), local4);
  }
  
  private void setProperty(String paramString1, String paramString2)
  {
    if (paramString1.equals("org.omg.CORBA.ORBInitRef"))
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString2, "=");
      if (localStringTokenizer.countTokens() != 2) {
        throw new IllegalArgumentException();
      }
      String str1 = localStringTokenizer.nextToken();
      String str2 = localStringTokenizer.nextToken();
      this.resultProps.setProperty(paramString1 + "." + str1, str2);
    }
    else
    {
      this.resultProps.setProperty(paramString1, paramString2);
    }
  }
  
  private void checkSetParserCalled()
  {
    if (!this.setParserCalled) {
      throw new IllegalStateException("setParser not called.");
    }
  }
  
  private void findPropertiesByPrefix(Set paramSet, Iterator paramIterator, PropertyCallback paramPropertyCallback)
  {
    while (paramIterator.hasNext())
    {
      String str1 = (String)paramIterator.next();
      Iterator localIterator = paramSet.iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        if (str1.startsWith(str2))
        {
          String str3 = paramPropertyCallback.get(str1);
          setProperty(str1, str3);
        }
      }
    }
  }
  
  private void findPropertiesByName(Iterator paramIterator, PropertyCallback paramPropertyCallback)
  {
    while (paramIterator.hasNext())
    {
      String str1 = (String)paramIterator.next();
      String str2 = paramPropertyCallback.get(str1);
      if (str2 != null) {
        setProperty(str1, str2);
      }
    }
  }
  
  private static String getSystemProperty(String paramString)
  {
    return (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
  }
  
  private String findMatchingPropertyName(Set paramSet, String paramString)
  {
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (str.endsWith(paramString)) {
        return str;
      }
    }
    return null;
  }
  
  private static Iterator makeIterator(Enumeration paramEnumeration)
  {
    new Iterator()
    {
      public boolean hasNext()
      {
        return this.val$enumeration.hasMoreElements();
      }
      
      public Object next()
      {
        return this.val$enumeration.nextElement();
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  private static Iterator getSystemPropertyNames()
  {
    Enumeration localEnumeration = (Enumeration)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getProperties().propertyNames();
      }
    });
    return makeIterator(localEnumeration);
  }
  
  private void getPropertiesFromFile(Properties paramProperties, String paramString)
  {
    try
    {
      File localFile = new File(paramString);
      if (!localFile.exists()) {
        return;
      }
      FileInputStream localFileInputStream = new FileInputStream(localFile);
      try
      {
        paramProperties.load(localFileInputStream);
      }
      finally
      {
        localFileInputStream.close();
      }
    }
    catch (Exception localException) {}
  }
  
  private Properties getFileProperties()
  {
    Properties localProperties1 = new Properties();
    String str1 = getSystemProperty("java.home");
    String str2 = str1 + File.separator + "lib" + File.separator + "orb.properties";
    getPropertiesFromFile(localProperties1, str2);
    Properties localProperties2 = new Properties(localProperties1);
    String str3 = getSystemProperty("user.home");
    str2 = str3 + File.separator + "orb.properties";
    getPropertiesFromFile(localProperties2, str2);
    return localProperties2;
  }
  
  private boolean hasCORBAPrefix(String paramString)
  {
    return (paramString.startsWith("org.omg.")) || (paramString.startsWith("com.sun.CORBA.")) || (paramString.startsWith("com.sun.corba.")) || (paramString.startsWith("com.sun.corba.se."));
  }
  
  private Set getCORBAPrefixes(Set paramSet)
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (hasCORBAPrefix(str)) {
        localHashSet.add(str);
      }
    }
    return localHashSet;
  }
}
