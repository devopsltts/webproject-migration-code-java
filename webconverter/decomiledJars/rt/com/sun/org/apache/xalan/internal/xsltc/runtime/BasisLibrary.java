package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator;
import com.sun.org.apache.xalan.internal.xsltc.dom.ArrayNodeListIterator;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMAdapter;
import com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM;
import com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator;
import com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import com.sun.org.apache.xml.internal.serializer.NamespaceMappings;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class BasisLibrary
{
  private static final String EMPTYSTRING = "";
  private static final ThreadLocal<StringBuilder> threadLocalStringBuilder = new ThreadLocal()
  {
    protected StringBuilder initialValue()
    {
      return new StringBuilder();
    }
  };
  private static final ThreadLocal<StringBuffer> threadLocalStringBuffer = new ThreadLocal()
  {
    protected StringBuffer initialValue()
    {
      return new StringBuffer();
    }
  };
  private static final int DOUBLE_FRACTION_DIGITS = 340;
  private static final double lowerBounds = 0.001D;
  private static final double upperBounds = 1.0E7D;
  private static DecimalFormat defaultFormatter;
  private static DecimalFormat xpathFormatter;
  private static String defaultPattern = "";
  private static FieldPosition _fieldPosition;
  private static char[] _characterArray;
  private static int prefixIndex;
  public static final String RUN_TIME_INTERNAL_ERR = "RUN_TIME_INTERNAL_ERR";
  public static final String RUN_TIME_COPY_ERR = "RUN_TIME_COPY_ERR";
  public static final String DATA_CONVERSION_ERR = "DATA_CONVERSION_ERR";
  public static final String EXTERNAL_FUNC_ERR = "EXTERNAL_FUNC_ERR";
  public static final String EQUALITY_EXPR_ERR = "EQUALITY_EXPR_ERR";
  public static final String INVALID_ARGUMENT_ERR = "INVALID_ARGUMENT_ERR";
  public static final String FORMAT_NUMBER_ERR = "FORMAT_NUMBER_ERR";
  public static final String ITERATOR_CLONE_ERR = "ITERATOR_CLONE_ERR";
  public static final String AXIS_SUPPORT_ERR = "AXIS_SUPPORT_ERR";
  public static final String TYPED_AXIS_SUPPORT_ERR = "TYPED_AXIS_SUPPORT_ERR";
  public static final String STRAY_ATTRIBUTE_ERR = "STRAY_ATTRIBUTE_ERR";
  public static final String STRAY_NAMESPACE_ERR = "STRAY_NAMESPACE_ERR";
  public static final String NAMESPACE_PREFIX_ERR = "NAMESPACE_PREFIX_ERR";
  public static final String DOM_ADAPTER_INIT_ERR = "DOM_ADAPTER_INIT_ERR";
  public static final String PARSER_DTD_SUPPORT_ERR = "PARSER_DTD_SUPPORT_ERR";
  public static final String NAMESPACES_SUPPORT_ERR = "NAMESPACES_SUPPORT_ERR";
  public static final String CANT_RESOLVE_RELATIVE_URI_ERR = "CANT_RESOLVE_RELATIVE_URI_ERR";
  public static final String UNSUPPORTED_XSL_ERR = "UNSUPPORTED_XSL_ERR";
  public static final String UNSUPPORTED_EXT_ERR = "UNSUPPORTED_EXT_ERR";
  public static final String UNKNOWN_TRANSLET_VERSION_ERR = "UNKNOWN_TRANSLET_VERSION_ERR";
  public static final String INVALID_QNAME_ERR = "INVALID_QNAME_ERR";
  public static final String INVALID_NCNAME_ERR = "INVALID_NCNAME_ERR";
  public static final String UNALLOWED_EXTENSION_FUNCTION_ERR = "UNALLOWED_EXTENSION_FUNCTION_ERR";
  public static final String UNALLOWED_EXTENSION_ELEMENT_ERR = "UNALLOWED_EXTENSION_ELEMENT_ERR";
  private static ResourceBundle m_bundle;
  public static final String ERROR_MESSAGES_KEY = "error-messages";
  
  public BasisLibrary() {}
  
  public static int countF(DTMAxisIterator paramDTMAxisIterator)
  {
    return paramDTMAxisIterator.getLast();
  }
  
  /**
   * @deprecated
   */
  public static int positionF(DTMAxisIterator paramDTMAxisIterator)
  {
    return paramDTMAxisIterator.isReverse() ? paramDTMAxisIterator.getLast() - paramDTMAxisIterator.getPosition() + 1 : paramDTMAxisIterator.getPosition();
  }
  
  public static double sumF(DTMAxisIterator paramDTMAxisIterator, DOM paramDOM)
  {
    try
    {
      int i;
      for (double d = 0.0D; (i = paramDTMAxisIterator.next()) != -1; d += Double.parseDouble(paramDOM.getStringValueX(i))) {}
      return d;
    }
    catch (NumberFormatException localNumberFormatException) {}
    return NaN.0D;
  }
  
  public static String stringF(int paramInt, DOM paramDOM)
  {
    return paramDOM.getStringValueX(paramInt);
  }
  
  public static String stringF(Object paramObject, DOM paramDOM)
  {
    if ((paramObject instanceof DTMAxisIterator)) {
      return paramDOM.getStringValueX(((DTMAxisIterator)paramObject).reset().next());
    }
    if ((paramObject instanceof Node)) {
      return paramDOM.getStringValueX(((Node)paramObject).node);
    }
    if ((paramObject instanceof DOM)) {
      return ((DOM)paramObject).getStringValue();
    }
    return paramObject.toString();
  }
  
  public static String stringF(Object paramObject, int paramInt, DOM paramDOM)
  {
    if ((paramObject instanceof DTMAxisIterator)) {
      return paramDOM.getStringValueX(((DTMAxisIterator)paramObject).reset().next());
    }
    if ((paramObject instanceof Node)) {
      return paramDOM.getStringValueX(((Node)paramObject).node);
    }
    if ((paramObject instanceof DOM)) {
      return ((DOM)paramObject).getStringValue();
    }
    if ((paramObject instanceof Double))
    {
      Double localDouble = (Double)paramObject;
      String str = localDouble.toString();
      int i = str.length();
      if ((str.charAt(i - 2) == '.') && (str.charAt(i - 1) == '0')) {
        return str.substring(0, i - 2);
      }
      return str;
    }
    return paramObject != null ? paramObject.toString() : "";
  }
  
  public static double numberF(int paramInt, DOM paramDOM)
  {
    return stringToReal(paramDOM.getStringValueX(paramInt));
  }
  
  public static double numberF(Object paramObject, DOM paramDOM)
  {
    if ((paramObject instanceof Double)) {
      return ((Double)paramObject).doubleValue();
    }
    if ((paramObject instanceof Integer)) {
      return ((Integer)paramObject).doubleValue();
    }
    if ((paramObject instanceof Boolean)) {
      return ((Boolean)paramObject).booleanValue() ? 1.0D : 0.0D;
    }
    if ((paramObject instanceof String)) {
      return stringToReal((String)paramObject);
    }
    if ((paramObject instanceof DTMAxisIterator))
    {
      localObject = (DTMAxisIterator)paramObject;
      return stringToReal(paramDOM.getStringValueX(((DTMAxisIterator)localObject).reset().next()));
    }
    if ((paramObject instanceof Node)) {
      return stringToReal(paramDOM.getStringValueX(((Node)paramObject).node));
    }
    if ((paramObject instanceof DOM)) {
      return stringToReal(((DOM)paramObject).getStringValue());
    }
    Object localObject = paramObject.getClass().getName();
    runTimeError("INVALID_ARGUMENT_ERR", localObject, "number()");
    return 0.0D;
  }
  
  public static double roundF(double paramDouble)
  {
    return Double.isNaN(paramDouble) ? NaN.0D : paramDouble == 0.0D ? paramDouble : (paramDouble < -0.5D) || (paramDouble > 0.0D) ? Math.floor(paramDouble + 0.5D) : -0.0D;
  }
  
  public static boolean booleanF(Object paramObject)
  {
    if ((paramObject instanceof Double))
    {
      double d = ((Double)paramObject).doubleValue();
      return (d != 0.0D) && (!Double.isNaN(d));
    }
    if ((paramObject instanceof Integer)) {
      return ((Integer)paramObject).doubleValue() != 0.0D;
    }
    if ((paramObject instanceof Boolean)) {
      return ((Boolean)paramObject).booleanValue();
    }
    if ((paramObject instanceof String)) {
      return !((String)paramObject).equals("");
    }
    if ((paramObject instanceof DTMAxisIterator))
    {
      localObject = (DTMAxisIterator)paramObject;
      return ((DTMAxisIterator)localObject).reset().next() != -1;
    }
    if ((paramObject instanceof Node)) {
      return true;
    }
    if ((paramObject instanceof DOM))
    {
      localObject = ((DOM)paramObject).getStringValue();
      return !((String)localObject).equals("");
    }
    Object localObject = paramObject.getClass().getName();
    runTimeError("INVALID_ARGUMENT_ERR", localObject, "boolean()");
    return false;
  }
  
  public static String substringF(String paramString, double paramDouble)
  {
    if (Double.isNaN(paramDouble)) {
      return "";
    }
    int i = getStringLength(paramString);
    int j = (int)Math.round(paramDouble) - 1;
    if (j > i) {
      return "";
    }
    if (j < 1) {
      j = 0;
    }
    try
    {
      j = paramString.offsetByCodePoints(0, j);
      return paramString.substring(j);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      runTimeError("RUN_TIME_INTERNAL_ERR", "substring()");
    }
    return null;
  }
  
  public static String substringF(String paramString, double paramDouble1, double paramDouble2)
  {
    if ((Double.isInfinite(paramDouble1)) || (Double.isNaN(paramDouble1)) || (Double.isNaN(paramDouble2)) || (paramDouble2 < 0.0D)) {
      return "";
    }
    int i = (int)Math.round(paramDouble1) - 1;
    int j = (int)Math.round(paramDouble2);
    int k;
    if (Double.isInfinite(paramDouble2)) {
      k = Integer.MAX_VALUE;
    } else {
      k = i + j;
    }
    int m = getStringLength(paramString);
    if ((k < 0) || (i > m)) {
      return "";
    }
    if (i < 0)
    {
      j += i;
      i = 0;
    }
    try
    {
      i = paramString.offsetByCodePoints(0, i);
      if (k > m) {
        return paramString.substring(i);
      }
      int n = paramString.offsetByCodePoints(i, j);
      return paramString.substring(i, n);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      runTimeError("RUN_TIME_INTERNAL_ERR", "substring()");
    }
    return null;
  }
  
  public static String substring_afterF(String paramString1, String paramString2)
  {
    int i = paramString1.indexOf(paramString2);
    if (i >= 0) {
      return paramString1.substring(i + paramString2.length());
    }
    return "";
  }
  
  public static String substring_beforeF(String paramString1, String paramString2)
  {
    int i = paramString1.indexOf(paramString2);
    if (i >= 0) {
      return paramString1.substring(0, i);
    }
    return "";
  }
  
  public static String translateF(String paramString1, String paramString2, String paramString3)
  {
    int i = paramString3.length();
    int j = paramString2.length();
    int k = paramString1.length();
    StringBuilder localStringBuilder = (StringBuilder)threadLocalStringBuilder.get();
    localStringBuilder.setLength(0);
    for (int n = 0; n < k; n++)
    {
      char c = paramString1.charAt(n);
      for (int m = 0; m < j; m++) {
        if (c == paramString2.charAt(m))
        {
          if (m >= i) {
            break;
          }
          localStringBuilder.append(paramString3.charAt(m));
          break;
        }
      }
      if (m == j) {
        localStringBuilder.append(c);
      }
    }
    return localStringBuilder.toString();
  }
  
  public static String normalize_spaceF(int paramInt, DOM paramDOM)
  {
    return normalize_spaceF(paramDOM.getStringValueX(paramInt));
  }
  
  public static String normalize_spaceF(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    StringBuilder localStringBuilder = (StringBuilder)threadLocalStringBuilder.get();
    localStringBuilder.setLength(0);
    while ((i < j) && (isWhiteSpace(paramString.charAt(i)))) {
      i++;
    }
    for (;;)
    {
      if ((i < j) && (!isWhiteSpace(paramString.charAt(i))))
      {
        localStringBuilder.append(paramString.charAt(i++));
      }
      else
      {
        if (i == j) {
          break;
        }
        while ((i < j) && (isWhiteSpace(paramString.charAt(i)))) {
          i++;
        }
        if (i < j) {
          localStringBuilder.append(' ');
        }
      }
    }
    return localStringBuilder.toString();
  }
  
  public static String generate_idF(int paramInt)
  {
    if (paramInt > 0) {
      return "N" + paramInt;
    }
    return "";
  }
  
  public static String getLocalName(String paramString)
  {
    int i = paramString.lastIndexOf(':');
    if (i >= 0) {
      paramString = paramString.substring(i + 1);
    }
    i = paramString.lastIndexOf('@');
    if (i >= 0) {
      paramString = paramString.substring(i + 1);
    }
    return paramString;
  }
  
  public static void unresolved_externalF(String paramString)
  {
    runTimeError("EXTERNAL_FUNC_ERR", paramString);
  }
  
  public static void unallowed_extension_functionF(String paramString)
  {
    runTimeError("UNALLOWED_EXTENSION_FUNCTION_ERR", paramString);
  }
  
  public static void unallowed_extension_elementF(String paramString)
  {
    runTimeError("UNALLOWED_EXTENSION_ELEMENT_ERR", paramString);
  }
  
  public static void unsupported_ElementF(String paramString, boolean paramBoolean)
  {
    if (paramBoolean) {
      runTimeError("UNSUPPORTED_EXT_ERR", paramString);
    } else {
      runTimeError("UNSUPPORTED_XSL_ERR", paramString);
    }
  }
  
  public static String namespace_uriF(DTMAxisIterator paramDTMAxisIterator, DOM paramDOM)
  {
    return namespace_uriF(paramDTMAxisIterator.next(), paramDOM);
  }
  
  public static String system_propertyF(String paramString)
  {
    if (paramString.equals("xsl:version")) {
      return "1.0";
    }
    if (paramString.equals("xsl:vendor")) {
      return "Apache Software Foundation (Xalan XSLTC)";
    }
    if (paramString.equals("xsl:vendor-url")) {
      return "http://xml.apache.org/xalan-j";
    }
    runTimeError("INVALID_ARGUMENT_ERR", paramString, "system-property()");
    return "";
  }
  
  public static String namespace_uriF(int paramInt, DOM paramDOM)
  {
    String str = paramDOM.getNodeName(paramInt);
    int i = str.lastIndexOf(':');
    if (i >= 0) {
      return str.substring(0, i);
    }
    return "";
  }
  
  public static String objectTypeF(Object paramObject)
  {
    if ((paramObject instanceof String)) {
      return "string";
    }
    if ((paramObject instanceof Boolean)) {
      return "boolean";
    }
    if ((paramObject instanceof Number)) {
      return "number";
    }
    if ((paramObject instanceof DOM)) {
      return "RTF";
    }
    if ((paramObject instanceof DTMAxisIterator)) {
      return "node-set";
    }
    return "unknown";
  }
  
  public static DTMAxisIterator nodesetF(Object paramObject)
  {
    if ((paramObject instanceof DOM))
    {
      localObject = (DOM)paramObject;
      return new SingletonIterator(((DOM)localObject).getDocument(), true);
    }
    if ((paramObject instanceof DTMAxisIterator)) {
      return (DTMAxisIterator)paramObject;
    }
    Object localObject = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", "node-set", localObject);
    return null;
  }
  
  private static boolean isWhiteSpace(char paramChar)
  {
    return (paramChar == ' ') || (paramChar == '\t') || (paramChar == '\n') || (paramChar == '\r');
  }
  
  private static boolean compareStrings(String paramString1, String paramString2, int paramInt, DOM paramDOM)
  {
    switch (paramInt)
    {
    case 0: 
      return paramString1.equals(paramString2);
    case 1: 
      return !paramString1.equals(paramString2);
    case 2: 
      return numberF(paramString1, paramDOM) > numberF(paramString2, paramDOM);
    case 3: 
      return numberF(paramString1, paramDOM) < numberF(paramString2, paramDOM);
    case 4: 
      return numberF(paramString1, paramDOM) >= numberF(paramString2, paramDOM);
    case 5: 
      return numberF(paramString1, paramDOM) <= numberF(paramString2, paramDOM);
    }
    runTimeError("RUN_TIME_INTERNAL_ERR", "compare()");
    return false;
  }
  
  public static boolean compare(DTMAxisIterator paramDTMAxisIterator1, DTMAxisIterator paramDTMAxisIterator2, int paramInt, DOM paramDOM)
  {
    paramDTMAxisIterator1.reset();
    int i;
    while ((i = paramDTMAxisIterator1.next()) != -1)
    {
      String str = paramDOM.getStringValueX(i);
      paramDTMAxisIterator2.reset();
      int j;
      while ((j = paramDTMAxisIterator2.next()) != -1) {
        if (i == j)
        {
          if (paramInt == 0) {
            return true;
          }
          if (paramInt == 1) {
            break;
          }
        }
        else if (compareStrings(str, paramDOM.getStringValueX(j), paramInt, paramDOM))
        {
          return true;
        }
      }
    }
    return false;
  }
  
  public static boolean compare(int paramInt1, DTMAxisIterator paramDTMAxisIterator, int paramInt2, DOM paramDOM)
  {
    int i;
    String str;
    switch (paramInt2)
    {
    case 0: 
      i = paramDTMAxisIterator.next();
      if (i != -1)
      {
        str = paramDOM.getStringValueX(paramInt1);
        do
        {
          if ((paramInt1 == i) || (str.equals(paramDOM.getStringValueX(i)))) {
            return true;
          }
        } while ((i = paramDTMAxisIterator.next()) != -1);
      }
      break;
    case 1: 
      i = paramDTMAxisIterator.next();
      if (i != -1)
      {
        str = paramDOM.getStringValueX(paramInt1);
        do
        {
          if ((paramInt1 != i) && (!str.equals(paramDOM.getStringValueX(i)))) {
            return true;
          }
        } while ((i = paramDTMAxisIterator.next()) != -1);
      }
      break;
    case 3: 
    case 2: 
      while ((i = paramDTMAxisIterator.next()) != -1) {
        if (i > paramInt1)
        {
          return true;
          while ((i = paramDTMAxisIterator.next()) != -1) {
            if (i < paramInt1) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
  public static boolean compare(DTMAxisIterator paramDTMAxisIterator, double paramDouble, int paramInt, DOM paramDOM)
  {
    switch (paramInt)
    {
    }
    int i;
    while ((i = paramDTMAxisIterator.next()) != -1) {
      if (numberF(paramDOM.getStringValueX(i), paramDOM) == paramDouble)
      {
        return true;
        while ((i = paramDTMAxisIterator.next()) != -1) {
          if (numberF(paramDOM.getStringValueX(i), paramDOM) != paramDouble)
          {
            return true;
            while ((i = paramDTMAxisIterator.next()) != -1) {
              if (numberF(paramDOM.getStringValueX(i), paramDOM) > paramDouble)
              {
                return true;
                while ((i = paramDTMAxisIterator.next()) != -1) {
                  if (numberF(paramDOM.getStringValueX(i), paramDOM) < paramDouble)
                  {
                    return true;
                    while ((i = paramDTMAxisIterator.next()) != -1) {
                      if (numberF(paramDOM.getStringValueX(i), paramDOM) >= paramDouble)
                      {
                        return true;
                        while ((i = paramDTMAxisIterator.next()) != -1) {
                          if (numberF(paramDOM.getStringValueX(i), paramDOM) <= paramDouble)
                          {
                            return true;
                            runTimeError("RUN_TIME_INTERNAL_ERR", "compare()");
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return false;
  }
  
  public static boolean compare(DTMAxisIterator paramDTMAxisIterator, String paramString, int paramInt, DOM paramDOM)
  {
    int i;
    while ((i = paramDTMAxisIterator.next()) != -1) {
      if (compareStrings(paramDOM.getStringValueX(i), paramString, paramInt, paramDOM)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean compare(Object paramObject1, Object paramObject2, int paramInt, DOM paramDOM)
  {
    boolean bool1 = false;
    int i = (hasSimpleType(paramObject1)) && (hasSimpleType(paramObject2)) ? 1 : 0;
    if ((paramInt != 0) && (paramInt != 1))
    {
      if (((paramObject1 instanceof Node)) || ((paramObject2 instanceof Node)))
      {
        if ((paramObject1 instanceof Boolean))
        {
          paramObject2 = new Boolean(booleanF(paramObject2));
          i = 1;
        }
        if ((paramObject2 instanceof Boolean))
        {
          paramObject1 = new Boolean(booleanF(paramObject1));
          i = 1;
        }
      }
      if (i != 0)
      {
        switch (paramInt)
        {
        case 2: 
          return numberF(paramObject1, paramDOM) > numberF(paramObject2, paramDOM);
        case 3: 
          return numberF(paramObject1, paramDOM) < numberF(paramObject2, paramDOM);
        case 4: 
          return numberF(paramObject1, paramDOM) >= numberF(paramObject2, paramDOM);
        case 5: 
          return numberF(paramObject1, paramDOM) <= numberF(paramObject2, paramDOM);
        }
        runTimeError("RUN_TIME_INTERNAL_ERR", "compare()");
      }
    }
    if (i != 0)
    {
      if (((paramObject1 instanceof Boolean)) || ((paramObject2 instanceof Boolean))) {
        bool1 = booleanF(paramObject1) == booleanF(paramObject2);
      } else if (((paramObject1 instanceof Double)) || ((paramObject2 instanceof Double)) || ((paramObject1 instanceof Integer)) || ((paramObject2 instanceof Integer))) {
        bool1 = numberF(paramObject1, paramDOM) == numberF(paramObject2, paramDOM);
      } else {
        bool1 = stringF(paramObject1, paramDOM).equals(stringF(paramObject2, paramDOM));
      }
      if (paramInt == 1) {
        bool1 = !bool1;
      }
    }
    else
    {
      if ((paramObject1 instanceof Node)) {
        paramObject1 = new SingletonIterator(((Node)paramObject1).node);
      }
      if ((paramObject2 instanceof Node)) {
        paramObject2 = new SingletonIterator(((Node)paramObject2).node);
      }
      if ((hasSimpleType(paramObject1)) || (((paramObject1 instanceof DOM)) && ((paramObject2 instanceof DTMAxisIterator))))
      {
        localObject = paramObject2;
        paramObject2 = paramObject1;
        paramObject1 = localObject;
        paramInt = Operators.swapOp(paramInt);
      }
      if ((paramObject1 instanceof DOM))
      {
        if ((paramObject2 instanceof Boolean))
        {
          bool1 = ((Boolean)paramObject2).booleanValue();
          return bool1 == (paramInt == 0);
        }
        localObject = ((DOM)paramObject1).getStringValue();
        if ((paramObject2 instanceof Number)) {
          bool1 = ((Number)paramObject2).doubleValue() == stringToReal((String)localObject);
        } else if ((paramObject2 instanceof String)) {
          bool1 = ((String)localObject).equals((String)paramObject2);
        } else if ((paramObject2 instanceof DOM)) {
          bool1 = ((String)localObject).equals(((DOM)paramObject2).getStringValue());
        }
        if (paramInt == 1) {
          bool1 = !bool1;
        }
        return bool1;
      }
      Object localObject = ((DTMAxisIterator)paramObject1).reset();
      if ((paramObject2 instanceof DTMAxisIterator))
      {
        bool1 = compare((DTMAxisIterator)localObject, (DTMAxisIterator)paramObject2, paramInt, paramDOM);
      }
      else if ((paramObject2 instanceof String))
      {
        bool1 = compare((DTMAxisIterator)localObject, (String)paramObject2, paramInt, paramDOM);
      }
      else if ((paramObject2 instanceof Number))
      {
        double d = ((Number)paramObject2).doubleValue();
        bool1 = compare((DTMAxisIterator)localObject, d, paramInt, paramDOM);
      }
      else if ((paramObject2 instanceof Boolean))
      {
        boolean bool2 = ((Boolean)paramObject2).booleanValue();
        bool1 = (((DTMAxisIterator)localObject).reset().next() != -1) == bool2;
      }
      else if ((paramObject2 instanceof DOM))
      {
        bool1 = compare((DTMAxisIterator)localObject, ((DOM)paramObject2).getStringValue(), paramInt, paramDOM);
      }
      else
      {
        if (paramObject2 == null) {
          return false;
        }
        String str = paramObject2.getClass().getName();
        runTimeError("INVALID_ARGUMENT_ERR", str, "compare()");
      }
    }
    return bool1;
  }
  
  public static boolean testLanguage(String paramString, DOM paramDOM, int paramInt)
  {
    String str = paramDOM.getLanguage(paramInt);
    if (str == null) {
      return false;
    }
    str = str.toLowerCase();
    paramString = paramString.toLowerCase();
    if (paramString.length() == 2) {
      return str.startsWith(paramString);
    }
    return str.equals(paramString);
  }
  
  private static boolean hasSimpleType(Object paramObject)
  {
    return ((paramObject instanceof Boolean)) || ((paramObject instanceof Double)) || ((paramObject instanceof Integer)) || ((paramObject instanceof String)) || ((paramObject instanceof Node)) || ((paramObject instanceof DOM));
  }
  
  public static double stringToReal(String paramString)
  {
    try
    {
      return Double.valueOf(paramString).doubleValue();
    }
    catch (NumberFormatException localNumberFormatException) {}
    return NaN.0D;
  }
  
  public static int stringToInt(String paramString)
  {
    try
    {
      return Integer.parseInt(paramString);
    }
    catch (NumberFormatException localNumberFormatException) {}
    return -1;
  }
  
  public static String realToString(double paramDouble)
  {
    double d = Math.abs(paramDouble);
    if ((d >= 0.001D) && (d < 1.0E7D))
    {
      localObject = Double.toString(paramDouble);
      int i = ((String)localObject).length();
      if ((((String)localObject).charAt(i - 2) == '.') && (((String)localObject).charAt(i - 1) == '0')) {
        return ((String)localObject).substring(0, i - 2);
      }
      return localObject;
    }
    if ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble))) {
      return Double.toString(paramDouble);
    }
    paramDouble += 0.0D;
    Object localObject = (StringBuffer)threadLocalStringBuffer.get();
    ((StringBuffer)localObject).setLength(0);
    xpathFormatter.format(paramDouble, (StringBuffer)localObject, _fieldPosition);
    return ((StringBuffer)localObject).toString();
  }
  
  public static int realToInt(double paramDouble)
  {
    return (int)paramDouble;
  }
  
  public static String formatNumber(double paramDouble, String paramString, DecimalFormat paramDecimalFormat)
  {
    if (paramDecimalFormat == null) {
      paramDecimalFormat = defaultFormatter;
    }
    try
    {
      StringBuffer localStringBuffer = (StringBuffer)threadLocalStringBuffer.get();
      localStringBuffer.setLength(0);
      if (paramString != defaultPattern) {
        paramDecimalFormat.applyLocalizedPattern(paramString);
      }
      paramDecimalFormat.format(paramDouble, localStringBuffer, _fieldPosition);
      return localStringBuffer.toString();
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      runTimeError("FORMAT_NUMBER_ERR", Double.toString(paramDouble), paramString);
    }
    return "";
  }
  
  public static DTMAxisIterator referenceToNodeSet(Object paramObject)
  {
    if ((paramObject instanceof Node)) {
      return new SingletonIterator(((Node)paramObject).node);
    }
    if ((paramObject instanceof DTMAxisIterator)) {
      return ((DTMAxisIterator)paramObject).cloneIterator().reset();
    }
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, "node-set");
    return null;
  }
  
  public static NodeList referenceToNodeList(Object paramObject, DOM paramDOM)
  {
    if (((paramObject instanceof Node)) || ((paramObject instanceof DTMAxisIterator)))
    {
      localObject = referenceToNodeSet(paramObject);
      return paramDOM.makeNodeList((DTMAxisIterator)localObject);
    }
    if ((paramObject instanceof DOM))
    {
      paramDOM = (DOM)paramObject;
      return paramDOM.makeNodeList(0);
    }
    Object localObject = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", localObject, "org.w3c.dom.NodeList");
    return null;
  }
  
  public static org.w3c.dom.Node referenceToNode(Object paramObject, DOM paramDOM)
  {
    if (((paramObject instanceof Node)) || ((paramObject instanceof DTMAxisIterator)))
    {
      localObject = referenceToNodeSet(paramObject);
      return paramDOM.makeNode((DTMAxisIterator)localObject);
    }
    if ((paramObject instanceof DOM))
    {
      paramDOM = (DOM)paramObject;
      localObject = paramDOM.getChildren(0);
      return paramDOM.makeNode((DTMAxisIterator)localObject);
    }
    Object localObject = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", localObject, "org.w3c.dom.Node");
    return null;
  }
  
  public static long referenceToLong(Object paramObject)
  {
    if ((paramObject instanceof Number)) {
      return ((Number)paramObject).longValue();
    }
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, Long.TYPE);
    return 0L;
  }
  
  public static double referenceToDouble(Object paramObject)
  {
    if ((paramObject instanceof Number)) {
      return ((Number)paramObject).doubleValue();
    }
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, Double.TYPE);
    return 0.0D;
  }
  
  public static boolean referenceToBoolean(Object paramObject)
  {
    if ((paramObject instanceof Boolean)) {
      return ((Boolean)paramObject).booleanValue();
    }
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, Boolean.TYPE);
    return false;
  }
  
  public static String referenceToString(Object paramObject, DOM paramDOM)
  {
    if ((paramObject instanceof String)) {
      return (String)paramObject;
    }
    if ((paramObject instanceof DTMAxisIterator)) {
      return paramDOM.getStringValueX(((DTMAxisIterator)paramObject).reset().next());
    }
    if ((paramObject instanceof Node)) {
      return paramDOM.getStringValueX(((Node)paramObject).node);
    }
    if ((paramObject instanceof DOM)) {
      return ((DOM)paramObject).getStringValue();
    }
    String str = paramObject.getClass().getName();
    runTimeError("DATA_CONVERSION_ERR", str, String.class);
    return null;
  }
  
  public static DTMAxisIterator node2Iterator(org.w3c.dom.Node paramNode, Translet paramTranslet, DOM paramDOM)
  {
    org.w3c.dom.Node localNode = paramNode;
    NodeList local3 = new NodeList()
    {
      public int getLength()
      {
        return 1;
      }
      
      public org.w3c.dom.Node item(int paramAnonymousInt)
      {
        if (paramAnonymousInt == 0) {
          return this.val$inNode;
        }
        return null;
      }
    };
    return nodeList2Iterator(local3, paramTranslet, paramDOM);
  }
  
  private static DTMAxisIterator nodeList2IteratorUsingHandleFromNode(NodeList paramNodeList, Translet paramTranslet, DOM paramDOM)
  {
    int i = paramNodeList.getLength();
    int[] arrayOfInt = new int[i];
    DTMManager localDTMManager = null;
    if ((paramDOM instanceof MultiDOM)) {
      localDTMManager = ((MultiDOM)paramDOM).getDTMManager();
    }
    for (int j = 0; j < i; j++)
    {
      org.w3c.dom.Node localNode = paramNodeList.item(j);
      int k;
      if (localDTMManager != null)
      {
        k = localDTMManager.getDTMHandleFromNode(localNode);
      }
      else if (((localNode instanceof DTMNodeProxy)) && (((DTMNodeProxy)localNode).getDTM() == paramDOM))
      {
        k = ((DTMNodeProxy)localNode).getDTMNodeNumber();
      }
      else
      {
        runTimeError("RUN_TIME_INTERNAL_ERR", "need MultiDOM");
        return null;
      }
      arrayOfInt[j] = k;
      System.out.println("Node " + j + " has handle 0x" + Integer.toString(k, 16));
    }
    return new ArrayNodeListIterator(arrayOfInt);
  }
  
  public static DTMAxisIterator nodeList2Iterator(NodeList paramNodeList, Translet paramTranslet, DOM paramDOM)
  {
    int i = 0;
    Document localDocument = null;
    DTMManager localDTMManager = null;
    int[] arrayOfInt = new int[paramNodeList.getLength()];
    if ((paramDOM instanceof MultiDOM)) {
      localDTMManager = ((MultiDOM)paramDOM).getDTMManager();
    }
    for (int j = 0; j < paramNodeList.getLength(); j++)
    {
      localObject1 = paramNodeList.item(j);
      Object localObject2;
      if ((localObject1 instanceof DTMNodeProxy))
      {
        DTMNodeProxy localDTMNodeProxy = (DTMNodeProxy)localObject1;
        localObject2 = localDTMNodeProxy.getDTM();
        int m = localDTMNodeProxy.getDTMNodeNumber();
        int i1 = localObject2 == paramDOM ? 1 : 0;
        if ((i1 == 0) && (localDTMManager != null)) {
          try
          {
            i1 = localObject2 == localDTMManager.getDTM(m) ? 1 : 0;
          }
          catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
        }
        if (i1 != 0)
        {
          arrayOfInt[j] = m;
          i++;
          continue;
        }
      }
      arrayOfInt[j] = -1;
      int k = ((org.w3c.dom.Node)localObject1).getNodeType();
      if (localDocument == null)
      {
        if (!(paramDOM instanceof MultiDOM))
        {
          runTimeError("RUN_TIME_INTERNAL_ERR", "need MultiDOM");
          return null;
        }
        try
        {
          localObject2 = (AbstractTranslet)paramTranslet;
          localDocument = ((AbstractTranslet)localObject2).newDocument("", "__top__");
        }
        catch (ParserConfigurationException localParserConfigurationException)
        {
          runTimeError("RUN_TIME_INTERNAL_ERR", localParserConfigurationException.getMessage());
          return null;
        }
      }
      switch (k)
      {
      case 1: 
      case 3: 
      case 4: 
      case 5: 
      case 7: 
      case 8: 
        localObject3 = localDocument.createElementNS(null, "__dummy__");
        ((Element)localObject3).appendChild(localDocument.importNode((org.w3c.dom.Node)localObject1, true));
        localDocument.getDocumentElement().appendChild((org.w3c.dom.Node)localObject3);
        i++;
        break;
      case 2: 
        localObject3 = localDocument.createElementNS(null, "__dummy__");
        ((Element)localObject3).setAttributeNodeNS((Attr)localDocument.importNode((org.w3c.dom.Node)localObject1, true));
        localDocument.getDocumentElement().appendChild((org.w3c.dom.Node)localObject3);
        i++;
        break;
      case 6: 
      default: 
        runTimeError("RUN_TIME_INTERNAL_ERR", "Don't know how to convert node type " + k);
      }
    }
    AbsoluteIterator localAbsoluteIterator = null;
    Object localObject1 = null;
    DTMAxisIterator localDTMAxisIterator1 = null;
    Object localObject4;
    Object localObject5;
    if (localDocument != null)
    {
      localObject3 = (MultiDOM)paramDOM;
      DOM localDOM = (DOM)localDTMManager.getDTM(new DOMSource(localDocument), false, null, true, false);
      localObject4 = new DOMAdapter(localDOM, paramTranslet.getNamesArray(), paramTranslet.getUrisArray(), paramTranslet.getTypesArray(), paramTranslet.getNamespaceArray());
      ((MultiDOM)localObject3).addDOMAdapter((DOMAdapter)localObject4);
      localObject5 = localDOM.getAxisIterator(3);
      DTMAxisIterator localDTMAxisIterator2 = localDOM.getAxisIterator(3);
      localAbsoluteIterator = new AbsoluteIterator(new StepIterator((DTMAxisIterator)localObject5, localDTMAxisIterator2));
      localAbsoluteIterator.setStartNode(0);
      localObject1 = localDOM.getAxisIterator(3);
      localDTMAxisIterator1 = localDOM.getAxisIterator(2);
    }
    Object localObject3 = new int[i];
    i = 0;
    for (int n = 0; n < paramNodeList.getLength(); n++) {
      if (arrayOfInt[n] != -1)
      {
        localObject3[(i++)] = arrayOfInt[n];
      }
      else
      {
        localObject4 = paramNodeList.item(n);
        localObject5 = null;
        int i2 = ((org.w3c.dom.Node)localObject4).getNodeType();
        switch (i2)
        {
        case 1: 
        case 3: 
        case 4: 
        case 5: 
        case 7: 
        case 8: 
          localObject5 = localObject1;
          break;
        case 2: 
          localObject5 = localDTMAxisIterator1;
          break;
        case 6: 
        default: 
          throw new InternalRuntimeError("Mismatched cases");
        }
        if (localObject5 != null)
        {
          ((DTMAxisIterator)localObject5).setStartNode(localAbsoluteIterator.next());
          localObject3[i] = ((DTMAxisIterator)localObject5).next();
          if (localObject3[i] == -1) {
            throw new InternalRuntimeError("Expected element missing at " + n);
          }
          if (((DTMAxisIterator)localObject5).next() != -1) {
            throw new InternalRuntimeError("Too many elements at " + n);
          }
          i++;
        }
      }
    }
    if (i != localObject3.length) {
      throw new InternalRuntimeError("Nodes lost in second pass");
    }
    return new ArrayNodeListIterator((int[])localObject3);
  }
  
  public static DOM referenceToResultTree(Object paramObject)
  {
    try
    {
      return (DOM)paramObject;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      String str = paramObject.getClass().getName();
      runTimeError("DATA_CONVERSION_ERR", "reference", str);
    }
    return null;
  }
  
  public static DTMAxisIterator getSingleNode(DTMAxisIterator paramDTMAxisIterator)
  {
    int i = paramDTMAxisIterator.next();
    return new SingletonIterator(i);
  }
  
  public static void copy(Object paramObject, SerializationHandler paramSerializationHandler, int paramInt, DOM paramDOM)
  {
    try
    {
      Object localObject;
      if ((paramObject instanceof DTMAxisIterator))
      {
        localObject = (DTMAxisIterator)paramObject;
        paramDOM.copy(((DTMAxisIterator)localObject).reset(), paramSerializationHandler);
      }
      else if ((paramObject instanceof Node))
      {
        paramDOM.copy(((Node)paramObject).node, paramSerializationHandler);
      }
      else if ((paramObject instanceof DOM))
      {
        localObject = (DOM)paramObject;
        ((DOM)localObject).copy(((DOM)localObject).getDocument(), paramSerializationHandler);
      }
      else
      {
        localObject = paramObject.toString();
        int i = ((String)localObject).length();
        if (i > _characterArray.length) {
          _characterArray = new char[i];
        }
        ((String)localObject).getChars(0, i, _characterArray, 0);
        paramSerializationHandler.characters(_characterArray, 0, i);
      }
    }
    catch (SAXException localSAXException)
    {
      runTimeError("RUN_TIME_COPY_ERR");
    }
  }
  
  public static void checkAttribQName(String paramString)
  {
    int i = paramString.indexOf(":");
    int j = paramString.lastIndexOf(":");
    String str1 = paramString.substring(j + 1);
    if (i > 0)
    {
      String str2 = paramString.substring(0, i);
      if (i != j)
      {
        String str3 = paramString.substring(i + 1, j);
        if (!XML11Char.isXML11ValidNCName(str3)) {
          runTimeError("INVALID_QNAME_ERR", str3 + ":" + str1);
        }
      }
      if (!XML11Char.isXML11ValidNCName(str2)) {
        runTimeError("INVALID_QNAME_ERR", str2 + ":" + str1);
      }
    }
    if ((!XML11Char.isXML11ValidNCName(str1)) || (str1.equals("xmlns"))) {
      runTimeError("INVALID_QNAME_ERR", str1);
    }
  }
  
  public static void checkNCName(String paramString)
  {
    if (!XML11Char.isXML11ValidNCName(paramString)) {
      runTimeError("INVALID_NCNAME_ERR", paramString);
    }
  }
  
  public static void checkQName(String paramString)
  {
    if (!XML11Char.isXML11ValidQName(paramString)) {
      runTimeError("INVALID_QNAME_ERR", paramString);
    }
  }
  
  public static String startXslElement(String paramString1, String paramString2, SerializationHandler paramSerializationHandler, DOM paramDOM, int paramInt)
  {
    try
    {
      int i = paramString1.indexOf(':');
      String str;
      if (i > 0)
      {
        str = paramString1.substring(0, i);
        if ((paramString2 == null) || (paramString2.length() == 0)) {
          try
          {
            paramString2 = paramDOM.lookupNamespace(paramInt, str);
          }
          catch (RuntimeException localRuntimeException)
          {
            paramSerializationHandler.flushPending();
            NamespaceMappings localNamespaceMappings = paramSerializationHandler.getNamespaceMappings();
            paramString2 = localNamespaceMappings.lookupNamespace(str);
            if (paramString2 == null) {
              runTimeError("NAMESPACE_PREFIX_ERR", str);
            }
          }
        }
        paramSerializationHandler.startElement(paramString2, paramString1.substring(i + 1), paramString1);
        paramSerializationHandler.namespaceAfterStartElement(str, paramString2);
      }
      else if ((paramString2 != null) && (paramString2.length() > 0))
      {
        str = generatePrefix();
        paramString1 = str + ':' + paramString1;
        paramSerializationHandler.startElement(paramString2, paramString1, paramString1);
        paramSerializationHandler.namespaceAfterStartElement(str, paramString2);
      }
      else
      {
        paramSerializationHandler.startElement(null, null, paramString1);
      }
    }
    catch (SAXException localSAXException)
    {
      throw new RuntimeException(localSAXException.getMessage());
    }
    return paramString1;
  }
  
  public static String getPrefix(String paramString)
  {
    int i = paramString.indexOf(':');
    return i > 0 ? paramString.substring(0, i) : null;
  }
  
  public static String generatePrefix()
  {
    synchronized (BasisLibrary.class)
    {
      return "ns" + prefixIndex++;
    }
  }
  
  public static void runTimeError(String paramString)
  {
    throw new RuntimeException(m_bundle.getString(paramString));
  }
  
  public static void runTimeError(String paramString, Object[] paramArrayOfObject)
  {
    String str = MessageFormat.format(m_bundle.getString(paramString), paramArrayOfObject);
    throw new RuntimeException(str);
  }
  
  public static void runTimeError(String paramString, Object paramObject)
  {
    runTimeError(paramString, new Object[] { paramObject });
  }
  
  public static void runTimeError(String paramString, Object paramObject1, Object paramObject2)
  {
    runTimeError(paramString, new Object[] { paramObject1, paramObject2 });
  }
  
  public static void consoleOutput(String paramString)
  {
    System.out.println(paramString);
  }
  
  public static String replace(String paramString1, char paramChar, String paramString2)
  {
    return paramString1.indexOf(paramChar) < 0 ? paramString1 : replace(paramString1, String.valueOf(paramChar), new String[] { paramString2 });
  }
  
  public static String replace(String paramString1, String paramString2, String[] paramArrayOfString)
  {
    int i = paramString1.length();
    StringBuilder localStringBuilder = (StringBuilder)threadLocalStringBuilder.get();
    localStringBuilder.setLength(0);
    for (int j = 0; j < i; j++)
    {
      char c = paramString1.charAt(j);
      int k = paramString2.indexOf(c);
      if (k >= 0) {
        localStringBuilder.append(paramArrayOfString[k]);
      } else {
        localStringBuilder.append(c);
      }
    }
    return localStringBuilder.toString();
  }
  
  public static String mapQNameToJavaName(String paramString)
  {
    return replace(paramString, ".-:/{}?#%*", new String[] { "$dot$", "$dash$", "$colon$", "$slash$", "", "$colon$", "$ques$", "$hash$", "$per$", "$aster$" });
  }
  
  public static int getStringLength(String paramString)
  {
    return paramString.codePointCount(0, paramString.length());
  }
  
  static
  {
    Object localObject = NumberFormat.getInstance(Locale.getDefault());
    defaultFormatter = (localObject instanceof DecimalFormat) ? (DecimalFormat)localObject : new DecimalFormat();
    defaultFormatter.setMaximumFractionDigits(340);
    defaultFormatter.setMinimumFractionDigits(0);
    defaultFormatter.setMinimumIntegerDigits(1);
    defaultFormatter.setGroupingUsed(false);
    xpathFormatter = new DecimalFormat("", new DecimalFormatSymbols(Locale.US));
    xpathFormatter.setMaximumFractionDigits(340);
    xpathFormatter.setMinimumFractionDigits(0);
    xpathFormatter.setMinimumIntegerDigits(1);
    xpathFormatter.setGroupingUsed(false);
    _fieldPosition = new FieldPosition(0);
    _characterArray = new char[32];
    prefixIndex = 0;
    localObject = "com.sun.org.apache.xalan.internal.xsltc.runtime.ErrorMessages";
    m_bundle = SecuritySupport.getResourceBundle((String)localObject);
  }
}
