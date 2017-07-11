package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.orbutil.ObjectUtility;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class IDLNameTranslatorImpl
  implements IDLNameTranslator
{
  private static String[] IDL_KEYWORDS = { "abstract", "any", "attribute", "boolean", "case", "char", "const", "context", "custom", "default", "double", "enum", "exception", "factory", "FALSE", "fixed", "float", "in", "inout", "interface", "long", "module", "native", "Object", "octet", "oneway", "out", "private", "public", "raises", "readonly", "sequence", "short", "string", "struct", "supports", "switch", "TRUE", "truncatable", "typedef", "unsigned", "union", "ValueBase", "valuetype", "void", "wchar", "wstring" };
  private static char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  private static final String UNDERSCORE = "_";
  private static final String INNER_CLASS_SEPARATOR = "__";
  private static final String[] BASE_IDL_ARRAY_MODULE_TYPE = { "org", "omg", "boxedRMI" };
  private static final String BASE_IDL_ARRAY_ELEMENT_TYPE = "seq";
  private static final String LEADING_UNDERSCORE_CHAR = "J";
  private static final String ID_CONTAINER_CLASH_CHAR = "_";
  private static final String OVERLOADED_TYPE_SEPARATOR = "__";
  private static final String ATTRIBUTE_METHOD_CLASH_MANGLE_CHARS = "__";
  private static final String GET_ATTRIBUTE_PREFIX = "_get_";
  private static final String SET_ATTRIBUTE_PREFIX = "_set_";
  private static final String IS_ATTRIBUTE_PREFIX = "_get_";
  private static Set idlKeywords_ = new HashSet();
  private Class[] interf_;
  private Map methodToIDLNameMap_;
  private Map IDLNameToMethodMap_;
  private Method[] methods_;
  
  public static IDLNameTranslator get(Class paramClass)
  {
    return new IDLNameTranslatorImpl(new Class[] { paramClass });
  }
  
  public static IDLNameTranslator get(Class[] paramArrayOfClass)
  {
    return new IDLNameTranslatorImpl(paramArrayOfClass);
  }
  
  public static String getExceptionId(Class paramClass)
  {
    IDLType localIDLType = classToIDLType(paramClass);
    return localIDLType.getExceptionName();
  }
  
  public Class[] getInterfaces()
  {
    return this.interf_;
  }
  
  public Method[] getMethods()
  {
    return this.methods_;
  }
  
  public Method getMethod(String paramString)
  {
    return (Method)this.IDLNameToMethodMap_.get(paramString);
  }
  
  public String getIDLName(Method paramMethod)
  {
    return (String)this.methodToIDLNameMap_.get(paramMethod);
  }
  
  private IDLNameTranslatorImpl(Class[] paramArrayOfClass)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new DynamicAccessPermission("access"));
    }
    try
    {
      IDLTypesUtil localIDLTypesUtil = new IDLTypesUtil();
      for (int i = 0; i < paramArrayOfClass.length; i++) {
        localIDLTypesUtil.validateRemoteInterface(paramArrayOfClass[i]);
      }
      this.interf_ = paramArrayOfClass;
      buildNameTranslation();
    }
    catch (IDLTypeException localIDLTypeException)
    {
      String str = localIDLTypeException.getMessage();
      IllegalStateException localIllegalStateException = new IllegalStateException(str);
      localIllegalStateException.initCause(localIDLTypeException);
      throw localIllegalStateException;
    }
  }
  
  private void buildNameTranslation()
  {
    HashMap localHashMap = new HashMap();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    for (int i = 0; i < this.interf_.length; i++)
    {
      localObject1 = this.interf_[i];
      localObject2 = new IDLTypesUtil();
      localObject3 = ((Class)localObject1).getMethods();
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          Method.setAccessible(this.val$methods, true);
          return null;
        }
      });
      for (int k = 0; k < localObject3.length; k++)
      {
        Method localMethod = localObject3[k];
        IDLMethodInfo localIDLMethodInfo = new IDLMethodInfo(null);
        localIDLMethodInfo.method = localMethod;
        if (((IDLTypesUtil)localObject2).isPropertyAccessorMethod(localMethod, (Class)localObject1))
        {
          localIDLMethodInfo.isProperty = true;
          String str = ((IDLTypesUtil)localObject2).getAttributeNameForProperty(localMethod.getName());
          localIDLMethodInfo.originalName = str;
          localIDLMethodInfo.mangledName = str;
        }
        else
        {
          localIDLMethodInfo.isProperty = false;
          localIDLMethodInfo.originalName = localMethod.getName();
          localIDLMethodInfo.mangledName = localMethod.getName();
        }
        localHashMap.put(localMethod, localIDLMethodInfo);
      }
    }
    Iterator localIterator1 = localHashMap.values().iterator();
    while (localIterator1.hasNext())
    {
      localObject1 = (IDLMethodInfo)localIterator1.next();
      localObject2 = localHashMap.values().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (IDLMethodInfo)((Iterator)localObject2).next();
        if ((localObject1 != localObject3) && (!((IDLMethodInfo)localObject1).originalName.equals(((IDLMethodInfo)localObject3).originalName)) && (((IDLMethodInfo)localObject1).originalName.equalsIgnoreCase(((IDLMethodInfo)localObject3).originalName)))
        {
          ((IDLMethodInfo)localObject1).mangledName = mangleCaseSensitiveCollision(((IDLMethodInfo)localObject1).originalName);
          break;
        }
      }
    }
    localIterator1 = localHashMap.values().iterator();
    while (localIterator1.hasNext())
    {
      localObject1 = (IDLMethodInfo)localIterator1.next();
      ((IDLMethodInfo)localObject1).mangledName = mangleIdentifier(((IDLMethodInfo)localObject1).mangledName, ((IDLMethodInfo)localObject1).isProperty);
    }
    localIterator1 = localHashMap.values().iterator();
    while (localIterator1.hasNext())
    {
      localObject1 = (IDLMethodInfo)localIterator1.next();
      if (!((IDLMethodInfo)localObject1).isProperty)
      {
        localObject2 = localHashMap.values().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (IDLMethodInfo)((Iterator)localObject2).next();
          if ((localObject1 != localObject3) && (!((IDLMethodInfo)localObject3).isProperty) && (((IDLMethodInfo)localObject1).originalName.equals(((IDLMethodInfo)localObject3).originalName)))
          {
            ((IDLMethodInfo)localObject1).mangledName = mangleOverloadedMethod(((IDLMethodInfo)localObject1).mangledName, ((IDLMethodInfo)localObject1).method);
            break;
          }
        }
      }
    }
    localIterator1 = localHashMap.values().iterator();
    while (localIterator1.hasNext())
    {
      localObject1 = (IDLMethodInfo)localIterator1.next();
      if (((IDLMethodInfo)localObject1).isProperty)
      {
        localObject2 = localHashMap.values().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (IDLMethodInfo)((Iterator)localObject2).next();
          if ((localObject1 != localObject3) && (!((IDLMethodInfo)localObject3).isProperty) && (((IDLMethodInfo)localObject1).mangledName.equals(((IDLMethodInfo)localObject3).mangledName)))
          {
            ((IDLMethodInfo)localObject1).mangledName += "__";
            break;
          }
        }
      }
    }
    Object localObject4;
    for (int j = 0; j < this.interf_.length; j++)
    {
      localObject1 = this.interf_[j];
      localObject2 = getMappedContainerName((Class)localObject1);
      localObject3 = localHashMap.values().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (IDLMethodInfo)((Iterator)localObject3).next();
        if ((!((IDLMethodInfo)localObject4).isProperty) && (identifierClashesWithContainer((String)localObject2, ((IDLMethodInfo)localObject4).mangledName))) {
          ((IDLMethodInfo)localObject4).mangledName = mangleContainerClash(((IDLMethodInfo)localObject4).mangledName);
        }
      }
    }
    this.methodToIDLNameMap_ = new HashMap();
    this.IDLNameToMethodMap_ = new HashMap();
    this.methods_ = ((Method[])localHashMap.keySet().toArray(new Method[0]));
    Iterator localIterator2 = localHashMap.values().iterator();
    while (localIterator2.hasNext())
    {
      localObject1 = (IDLMethodInfo)localIterator2.next();
      localObject2 = ((IDLMethodInfo)localObject1).mangledName;
      if (((IDLMethodInfo)localObject1).isProperty)
      {
        localObject3 = ((IDLMethodInfo)localObject1).method.getName();
        localObject4 = "";
        if (((String)localObject3).startsWith("get")) {
          localObject4 = "_get_";
        } else if (((String)localObject3).startsWith("set")) {
          localObject4 = "_set_";
        } else {
          localObject4 = "_get_";
        }
        localObject2 = (String)localObject4 + ((IDLMethodInfo)localObject1).mangledName;
      }
      this.methodToIDLNameMap_.put(((IDLMethodInfo)localObject1).method, localObject2);
      if (this.IDLNameToMethodMap_.containsKey(localObject2))
      {
        localObject3 = (Method)this.IDLNameToMethodMap_.get(localObject2);
        throw new IllegalStateException("Error : methods " + localObject3 + " and " + ((IDLMethodInfo)localObject1).method + " both result in IDL name '" + (String)localObject2 + "'");
      }
      this.IDLNameToMethodMap_.put(localObject2, ((IDLMethodInfo)localObject1).method);
    }
  }
  
  private static String mangleIdentifier(String paramString)
  {
    return mangleIdentifier(paramString, false);
  }
  
  private static String mangleIdentifier(String paramString, boolean paramBoolean)
  {
    String str = paramString;
    if (hasLeadingUnderscore(str)) {
      str = mangleLeadingUnderscore(str);
    }
    if ((!paramBoolean) && (isIDLKeyword(str))) {
      str = mangleIDLKeywordClash(str);
    }
    if (!isIDLIdentifier(str)) {
      str = mangleUnicodeChars(str);
    }
    return str;
  }
  
  static boolean isIDLKeyword(String paramString)
  {
    String str = paramString.toUpperCase();
    return idlKeywords_.contains(str);
  }
  
  static String mangleIDLKeywordClash(String paramString)
  {
    return "_" + paramString;
  }
  
  private static String mangleLeadingUnderscore(String paramString)
  {
    return "J" + paramString;
  }
  
  private static boolean hasLeadingUnderscore(String paramString)
  {
    return paramString.startsWith("_");
  }
  
  static String mangleUnicodeChars(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if (isIDLIdentifierChar(c))
      {
        localStringBuffer.append(c);
      }
      else
      {
        String str = charToUnicodeRepresentation(c);
        localStringBuffer.append(str);
      }
    }
    return localStringBuffer.toString();
  }
  
  String mangleCaseSensitiveCollision(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramString);
    localStringBuffer.append("_");
    int i = 0;
    for (int j = 0; j < paramString.length(); j++)
    {
      char c = paramString.charAt(j);
      if (Character.isUpperCase(c))
      {
        if (i != 0) {
          localStringBuffer.append("_");
        }
        localStringBuffer.append(j);
        i = 1;
      }
    }
    return localStringBuffer.toString();
  }
  
  private static String mangleContainerClash(String paramString)
  {
    return paramString + "_";
  }
  
  private static boolean identifierClashesWithContainer(String paramString1, String paramString2)
  {
    return paramString2.equalsIgnoreCase(paramString1);
  }
  
  public static String charToUnicodeRepresentation(char paramChar)
  {
    int i = paramChar;
    StringBuffer localStringBuffer = new StringBuffer();
    for (int j = i; j > 0; j = k)
    {
      k = j / 16;
      m = j % 16;
      localStringBuffer.insert(0, HEX_DIGITS[m]);
    }
    int k = 4 - localStringBuffer.length();
    for (int m = 0; m < k; m++) {
      localStringBuffer.insert(0, "0");
    }
    localStringBuffer.insert(0, "U");
    return localStringBuffer.toString();
  }
  
  private static boolean isIDLIdentifier(String paramString)
  {
    boolean bool = true;
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      bool = i == 0 ? isIDLAlphabeticChar(c) : isIDLIdentifierChar(c);
      if (!bool) {
        break;
      }
    }
    return bool;
  }
  
  private static boolean isIDLIdentifierChar(char paramChar)
  {
    return (isIDLAlphabeticChar(paramChar)) || (isIDLDecimalDigit(paramChar)) || (isUnderscore(paramChar));
  }
  
  private static boolean isIDLAlphabeticChar(char paramChar)
  {
    boolean bool = ((paramChar >= 'A') && (paramChar <= 'Z')) || ((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'À') && (paramChar <= 'ÿ') && (paramChar != '×') && (paramChar != '÷'));
    return bool;
  }
  
  private static boolean isIDLDecimalDigit(char paramChar)
  {
    return (paramChar >= '0') && (paramChar <= '9');
  }
  
  private static boolean isUnderscore(char paramChar)
  {
    return paramChar == '_';
  }
  
  private static String mangleOverloadedMethod(String paramString, Method paramMethod)
  {
    IDLTypesUtil localIDLTypesUtil = new IDLTypesUtil();
    String str1 = paramString + "__";
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      Class localClass = arrayOfClass[i];
      if (i > 0) {
        str1 = str1 + "__";
      }
      IDLType localIDLType = classToIDLType(localClass);
      String str2 = localIDLType.getModuleName();
      String str3 = localIDLType.getMemberName();
      String str4 = str2.length() > 0 ? str2 + "_" + str3 : str3;
      if ((!localIDLTypesUtil.isPrimitive(localClass)) && (localIDLTypesUtil.getSpecialCaseIDLTypeMapping(localClass) == null) && (isIDLKeyword(str4))) {
        str4 = mangleIDLKeywordClash(str4);
      }
      str4 = mangleUnicodeChars(str4);
      str1 = str1 + str4;
    }
    return str1;
  }
  
  private static IDLType classToIDLType(Class paramClass)
  {
    IDLType localIDLType = null;
    IDLTypesUtil localIDLTypesUtil = new IDLTypesUtil();
    if (localIDLTypesUtil.isPrimitive(paramClass))
    {
      localIDLType = localIDLTypesUtil.getPrimitiveIDLTypeMapping(paramClass);
    }
    else
    {
      Object localObject1;
      Object localObject2;
      String[] arrayOfString;
      if (paramClass.isArray())
      {
        localObject1 = paramClass.getComponentType();
        for (int i = 1; ((Class)localObject1).isArray(); i++) {
          localObject1 = ((Class)localObject1).getComponentType();
        }
        localObject2 = classToIDLType((Class)localObject1);
        arrayOfString = BASE_IDL_ARRAY_MODULE_TYPE;
        if (((IDLType)localObject2).hasModule()) {
          arrayOfString = (String[])ObjectUtility.concatenateArrays(arrayOfString, ((IDLType)localObject2).getModules());
        }
        String str2 = "seq" + i + "_" + ((IDLType)localObject2).getMemberName();
        localIDLType = new IDLType(paramClass, arrayOfString, str2);
      }
      else
      {
        localIDLType = localIDLTypesUtil.getSpecialCaseIDLTypeMapping(paramClass);
        if (localIDLType == null)
        {
          localObject1 = getUnmappedContainerName(paramClass);
          localObject1 = ((String)localObject1).replaceAll("\\$", "__");
          if (hasLeadingUnderscore((String)localObject1)) {
            localObject1 = mangleLeadingUnderscore((String)localObject1);
          }
          String str1 = getPackageName(paramClass);
          if (str1 == null)
          {
            localIDLType = new IDLType(paramClass, (String)localObject1);
          }
          else
          {
            if (localIDLTypesUtil.isEntity(paramClass)) {
              str1 = "org.omg.boxedIDL." + str1;
            }
            localObject2 = new StringTokenizer(str1, ".");
            arrayOfString = new String[((StringTokenizer)localObject2).countTokens()];
            int j = 0;
            while (((StringTokenizer)localObject2).hasMoreElements())
            {
              String str3 = ((StringTokenizer)localObject2).nextToken();
              String str4 = hasLeadingUnderscore(str3) ? mangleLeadingUnderscore(str3) : str3;
              arrayOfString[(j++)] = str4;
            }
            localIDLType = new IDLType(paramClass, arrayOfString, (String)localObject1);
          }
        }
      }
    }
    return localIDLType;
  }
  
  private static String getPackageName(Class paramClass)
  {
    Package localPackage = paramClass.getPackage();
    String str1 = null;
    if (localPackage != null)
    {
      str1 = localPackage.getName();
    }
    else
    {
      String str2 = paramClass.getName();
      int i = str2.indexOf('.');
      str1 = i == -1 ? null : str2.substring(0, i);
    }
    return str1;
  }
  
  private static String getMappedContainerName(Class paramClass)
  {
    String str = getUnmappedContainerName(paramClass);
    return mangleIdentifier(str);
  }
  
  private static String getUnmappedContainerName(Class paramClass)
  {
    Object localObject = null;
    String str1 = getPackageName(paramClass);
    String str2 = paramClass.getName();
    if (str1 != null)
    {
      int i = str1.length();
      localObject = str2.substring(i + 1);
    }
    else
    {
      localObject = str2;
    }
    return localObject;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("IDLNameTranslator[");
    for (int i = 0; i < this.interf_.length; i++)
    {
      if (i != 0) {
        localStringBuffer.append(" ");
      }
      localStringBuffer.append(this.interf_[i].getName());
    }
    localStringBuffer.append("]\n");
    Iterator localIterator = this.methodToIDLNameMap_.keySet().iterator();
    while (localIterator.hasNext())
    {
      Method localMethod = (Method)localIterator.next();
      String str = (String)this.methodToIDLNameMap_.get(localMethod);
      localStringBuffer.append(str + ":" + localMethod + "\n");
    }
    return localStringBuffer.toString();
  }
  
  static
  {
    for (int i = 0; i < IDL_KEYWORDS.length; i++)
    {
      String str1 = IDL_KEYWORDS[i];
      String str2 = str1.toUpperCase();
      idlKeywords_.add(str2);
    }
  }
  
  private static class IDLMethodInfo
  {
    public Method method;
    public boolean isProperty;
    public String originalName;
    public String mangledName;
    
    private IDLMethodInfo() {}
  }
}
