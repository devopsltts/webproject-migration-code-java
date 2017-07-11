package java.util.logging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Level
  implements Serializable
{
  private static final String defaultBundle = "sun.util.logging.resources.logging";
  private final String name;
  private final int value;
  private final String resourceBundleName;
  private transient String localizedLevelName;
  private transient Locale cachedLocale;
  public static final Level OFF = new Level("OFF", Integer.MAX_VALUE, "sun.util.logging.resources.logging");
  public static final Level SEVERE = new Level("SEVERE", 1000, "sun.util.logging.resources.logging");
  public static final Level WARNING = new Level("WARNING", 900, "sun.util.logging.resources.logging");
  public static final Level INFO = new Level("INFO", 800, "sun.util.logging.resources.logging");
  public static final Level CONFIG = new Level("CONFIG", 700, "sun.util.logging.resources.logging");
  public static final Level FINE = new Level("FINE", 500, "sun.util.logging.resources.logging");
  public static final Level FINER = new Level("FINER", 400, "sun.util.logging.resources.logging");
  public static final Level FINEST = new Level("FINEST", 300, "sun.util.logging.resources.logging");
  public static final Level ALL = new Level("ALL", Integer.MIN_VALUE, "sun.util.logging.resources.logging");
  private static final long serialVersionUID = -8176160795706313070L;
  
  protected Level(String paramString, int paramInt)
  {
    this(paramString, paramInt, null);
  }
  
  protected Level(String paramString1, int paramInt, String paramString2)
  {
    this(paramString1, paramInt, paramString2, true);
  }
  
  private Level(String paramString1, int paramInt, String paramString2, boolean paramBoolean)
  {
    if (paramString1 == null) {
      throw new NullPointerException();
    }
    this.name = paramString1;
    this.value = paramInt;
    this.resourceBundleName = paramString2;
    this.localizedLevelName = (paramString2 == null ? paramString1 : null);
    this.cachedLocale = null;
    if (paramBoolean) {
      KnownLevel.add(this);
    }
  }
  
  public String getResourceBundleName()
  {
    return this.resourceBundleName;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getLocalizedName()
  {
    return getLocalizedLevelName();
  }
  
  final String getLevelName()
  {
    return this.name;
  }
  
  private String computeLocalizedLevelName(Locale paramLocale)
  {
    ResourceBundle localResourceBundle = ResourceBundle.getBundle(this.resourceBundleName, paramLocale);
    String str = localResourceBundle.getString(this.name);
    boolean bool = "sun.util.logging.resources.logging".equals(this.resourceBundleName);
    if (!bool) {
      return str;
    }
    Locale localLocale1 = localResourceBundle.getLocale();
    Locale localLocale2 = (Locale.ROOT.equals(localLocale1)) || (this.name.equals(str.toUpperCase(Locale.ROOT))) ? Locale.ROOT : localLocale1;
    return Locale.ROOT.equals(localLocale2) ? this.name : str.toUpperCase(localLocale2);
  }
  
  final String getCachedLocalizedLevelName()
  {
    if ((this.localizedLevelName != null) && (this.cachedLocale != null) && (this.cachedLocale.equals(Locale.getDefault()))) {
      return this.localizedLevelName;
    }
    if (this.resourceBundleName == null) {
      return this.name;
    }
    return null;
  }
  
  final synchronized String getLocalizedLevelName()
  {
    String str = getCachedLocalizedLevelName();
    if (str != null) {
      return str;
    }
    Locale localLocale = Locale.getDefault();
    try
    {
      this.localizedLevelName = computeLocalizedLevelName(localLocale);
    }
    catch (Exception localException)
    {
      this.localizedLevelName = this.name;
    }
    this.cachedLocale = localLocale;
    return this.localizedLevelName;
  }
  
  static Level findLevel(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    KnownLevel localKnownLevel = KnownLevel.findByName(paramString);
    if (localKnownLevel != null) {
      return localKnownLevel.mirroredLevel;
    }
    try
    {
      int i = Integer.parseInt(paramString);
      localKnownLevel = KnownLevel.findByValue(i);
      if (localKnownLevel == null)
      {
        Level localLevel = new Level(paramString, i);
        localKnownLevel = KnownLevel.findByValue(i);
      }
      return localKnownLevel.mirroredLevel;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      localKnownLevel = KnownLevel.findByLocalizedLevelName(paramString);
      if (localKnownLevel != null) {
        return localKnownLevel.mirroredLevel;
      }
    }
    return null;
  }
  
  public final String toString()
  {
    return this.name;
  }
  
  public final int intValue()
  {
    return this.value;
  }
  
  private Object readResolve()
  {
    KnownLevel localKnownLevel = KnownLevel.matches(this);
    if (localKnownLevel != null) {
      return localKnownLevel.levelObject;
    }
    Level localLevel = new Level(this.name, this.value, this.resourceBundleName);
    return localLevel;
  }
  
  public static synchronized Level parse(String paramString)
    throws IllegalArgumentException
  {
    paramString.length();
    KnownLevel localKnownLevel = KnownLevel.findByName(paramString);
    if (localKnownLevel != null) {
      return localKnownLevel.levelObject;
    }
    try
    {
      int i = Integer.parseInt(paramString);
      localKnownLevel = KnownLevel.findByValue(i);
      if (localKnownLevel == null)
      {
        Level localLevel = new Level(paramString, i);
        localKnownLevel = KnownLevel.findByValue(i);
      }
      return localKnownLevel.levelObject;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      localKnownLevel = KnownLevel.findByLocalizedLevelName(paramString);
      if (localKnownLevel != null) {
        return localKnownLevel.levelObject;
      }
      throw new IllegalArgumentException("Bad level \"" + paramString + "\"");
    }
  }
  
  public boolean equals(Object paramObject)
  {
    try
    {
      Level localLevel = (Level)paramObject;
      return localLevel.value == this.value;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public int hashCode()
  {
    return this.value;
  }
  
  static final class KnownLevel
  {
    private static Map<String, List<KnownLevel>> nameToLevels = new HashMap();
    private static Map<Integer, List<KnownLevel>> intToLevels = new HashMap();
    final Level levelObject;
    final Level mirroredLevel;
    
    KnownLevel(Level paramLevel)
    {
      this.levelObject = paramLevel;
      if (paramLevel.getClass() == Level.class) {
        this.mirroredLevel = paramLevel;
      } else {
        this.mirroredLevel = new Level(paramLevel.name, paramLevel.value, paramLevel.resourceBundleName, false, null);
      }
    }
    
    static synchronized void add(Level paramLevel)
    {
      KnownLevel localKnownLevel = new KnownLevel(paramLevel);
      Object localObject = (List)nameToLevels.get(paramLevel.name);
      if (localObject == null)
      {
        localObject = new ArrayList();
        nameToLevels.put(paramLevel.name, localObject);
      }
      ((List)localObject).add(localKnownLevel);
      localObject = (List)intToLevels.get(Integer.valueOf(paramLevel.value));
      if (localObject == null)
      {
        localObject = new ArrayList();
        intToLevels.put(Integer.valueOf(paramLevel.value), localObject);
      }
      ((List)localObject).add(localKnownLevel);
    }
    
    static synchronized KnownLevel findByName(String paramString)
    {
      List localList = (List)nameToLevels.get(paramString);
      if (localList != null) {
        return (KnownLevel)localList.get(0);
      }
      return null;
    }
    
    static synchronized KnownLevel findByValue(int paramInt)
    {
      List localList = (List)intToLevels.get(Integer.valueOf(paramInt));
      if (localList != null) {
        return (KnownLevel)localList.get(0);
      }
      return null;
    }
    
    static synchronized KnownLevel findByLocalizedLevelName(String paramString)
    {
      Iterator localIterator1 = nameToLevels.values().iterator();
      while (localIterator1.hasNext())
      {
        List localList = (List)localIterator1.next();
        Iterator localIterator2 = localList.iterator();
        while (localIterator2.hasNext())
        {
          KnownLevel localKnownLevel = (KnownLevel)localIterator2.next();
          String str = localKnownLevel.levelObject.getLocalizedLevelName();
          if (paramString.equals(str)) {
            return localKnownLevel;
          }
        }
      }
      return null;
    }
    
    static synchronized KnownLevel matches(Level paramLevel)
    {
      List localList = (List)nameToLevels.get(paramLevel.name);
      if (localList != null)
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          KnownLevel localKnownLevel = (KnownLevel)localIterator.next();
          Level localLevel = localKnownLevel.mirroredLevel;
          if ((paramLevel.value == localLevel.value) && ((paramLevel.resourceBundleName == localLevel.resourceBundleName) || ((paramLevel.resourceBundleName != null) && (paramLevel.resourceBundleName.equals(localLevel.resourceBundleName))))) {
            return localKnownLevel;
          }
        }
      }
      return null;
    }
  }
}
