package java.beans;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

class NameGenerator
{
  private Map<Object, String> valueToName = new IdentityHashMap();
  private Map<String, Integer> nameToCount = new HashMap();
  
  public NameGenerator() {}
  
  public void clear()
  {
    this.valueToName.clear();
    this.nameToCount.clear();
  }
  
  public static String unqualifiedClassName(Class paramClass)
  {
    if (paramClass.isArray()) {
      return unqualifiedClassName(paramClass.getComponentType()) + "Array";
    }
    String str = paramClass.getName();
    return str.substring(str.lastIndexOf('.') + 1);
  }
  
  public static String capitalize(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramString;
    }
    return paramString.substring(0, 1).toUpperCase(Locale.ENGLISH) + paramString.substring(1);
  }
  
  public String instanceName(Object paramObject)
  {
    if (paramObject == null) {
      return "null";
    }
    if ((paramObject instanceof Class)) {
      return unqualifiedClassName((Class)paramObject);
    }
    String str1 = (String)this.valueToName.get(paramObject);
    if (str1 != null) {
      return str1;
    }
    Class localClass = paramObject.getClass();
    String str2 = unqualifiedClassName(localClass);
    Integer localInteger = (Integer)this.nameToCount.get(str2);
    int i = localInteger == null ? 0 : localInteger.intValue() + 1;
    this.nameToCount.put(str2, new Integer(i));
    str1 = str2 + i;
    this.valueToName.put(paramObject, str1);
    return str1;
  }
}
