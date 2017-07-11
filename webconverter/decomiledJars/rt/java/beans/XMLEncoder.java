package java.beans;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XMLEncoder
  extends Encoder
  implements AutoCloseable
{
  private final CharsetEncoder encoder;
  private final String charset;
  private final boolean declaration;
  private OutputStreamWriter out;
  private Object owner;
  private int indentation = 0;
  private boolean internal = false;
  private Map<Object, ValueData> valueToExpression;
  private Map<Object, List<Statement>> targetToStatementList;
  private boolean preambleWritten = false;
  private NameGenerator nameGenerator;
  
  public XMLEncoder(OutputStream paramOutputStream)
  {
    this(paramOutputStream, "UTF-8", true, 0);
  }
  
  public XMLEncoder(OutputStream paramOutputStream, String paramString, boolean paramBoolean, int paramInt)
  {
    if (paramOutputStream == null) {
      throw new IllegalArgumentException("the output stream cannot be null");
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException("the indentation must be >= 0");
    }
    Charset localCharset = Charset.forName(paramString);
    this.encoder = localCharset.newEncoder();
    this.charset = paramString;
    this.declaration = paramBoolean;
    this.indentation = paramInt;
    this.out = new OutputStreamWriter(paramOutputStream, localCharset.newEncoder());
    this.valueToExpression = new IdentityHashMap();
    this.targetToStatementList = new IdentityHashMap();
    this.nameGenerator = new NameGenerator();
  }
  
  public void setOwner(Object paramObject)
  {
    this.owner = paramObject;
    writeExpression(new Expression(this, "getOwner", new Object[0]));
  }
  
  public Object getOwner()
  {
    return this.owner;
  }
  
  public void writeObject(Object paramObject)
  {
    if (this.internal) {
      super.writeObject(paramObject);
    } else {
      writeStatement(new Statement(this, "writeObject", new Object[] { paramObject }));
    }
  }
  
  private List<Statement> statementList(Object paramObject)
  {
    Object localObject = (List)this.targetToStatementList.get(paramObject);
    if (localObject == null)
    {
      localObject = new ArrayList();
      this.targetToStatementList.put(paramObject, localObject);
    }
    return localObject;
  }
  
  private void mark(Object paramObject, boolean paramBoolean)
  {
    if ((paramObject == null) || (paramObject == this)) {
      return;
    }
    ValueData localValueData = getValueData(paramObject);
    Expression localExpression = localValueData.exp;
    if ((paramObject.getClass() == String.class) && (localExpression == null)) {
      return;
    }
    if (paramBoolean) {
      localValueData.refs += 1;
    }
    if (localValueData.marked) {
      return;
    }
    localValueData.marked = true;
    Object localObject = localExpression.getTarget();
    mark(localExpression);
    if (!(localObject instanceof Class))
    {
      statementList(localObject).add(localExpression);
      localValueData.refs += 1;
    }
  }
  
  private void mark(Statement paramStatement)
  {
    Object[] arrayOfObject = paramStatement.getArguments();
    for (int i = 0; i < arrayOfObject.length; i++)
    {
      Object localObject = arrayOfObject[i];
      mark(localObject, true);
    }
    mark(paramStatement.getTarget(), paramStatement instanceof Expression);
  }
  
  public void writeStatement(Statement paramStatement)
  {
    boolean bool = this.internal;
    this.internal = true;
    try
    {
      super.writeStatement(paramStatement);
      mark(paramStatement);
      Object localObject = paramStatement.getTarget();
      if ((localObject instanceof Field))
      {
        String str = paramStatement.getMethodName();
        Object[] arrayOfObject = paramStatement.getArguments();
        if ((str != null) && (arrayOfObject != null)) {
          if ((str.equals("get")) && (arrayOfObject.length == 1)) {
            localObject = arrayOfObject[0];
          } else if ((str.equals("set")) && (arrayOfObject.length == 2)) {
            localObject = arrayOfObject[0];
          }
        }
      }
      statementList(localObject).add(paramStatement);
    }
    catch (Exception localException)
    {
      getExceptionListener().exceptionThrown(new Exception("XMLEncoder: discarding statement " + paramStatement, localException));
    }
    this.internal = bool;
  }
  
  public void writeExpression(Expression paramExpression)
  {
    boolean bool = this.internal;
    this.internal = true;
    Object localObject = getValue(paramExpression);
    if ((get(localObject) == null) || (((localObject instanceof String)) && (!bool)))
    {
      getValueData(localObject).exp = paramExpression;
      super.writeExpression(paramExpression);
    }
    this.internal = bool;
  }
  
  public void flush()
  {
    if (!this.preambleWritten)
    {
      if (this.declaration) {
        writeln("<?xml version=" + quote("1.0") + " encoding=" + quote(this.charset) + "?>");
      }
      writeln("<java version=" + quote(System.getProperty("java.version")) + " class=" + quote(XMLDecoder.class.getName()) + ">");
      this.preambleWritten = true;
    }
    this.indentation += 1;
    List localList = statementList(this);
    while (!localList.isEmpty())
    {
      localStatement = (Statement)localList.remove(0);
      if ("writeObject".equals(localStatement.getMethodName())) {
        outputValue(localStatement.getArguments()[0], this, true);
      } else {
        outputStatement(localStatement, this, false);
      }
    }
    this.indentation -= 1;
    for (Statement localStatement = getMissedStatement(); localStatement != null; localStatement = getMissedStatement()) {
      outputStatement(localStatement, this, false);
    }
    try
    {
      this.out.flush();
    }
    catch (IOException localIOException)
    {
      getExceptionListener().exceptionThrown(localIOException);
    }
    clear();
  }
  
  void clear()
  {
    super.clear();
    this.nameGenerator.clear();
    this.valueToExpression.clear();
    this.targetToStatementList.clear();
  }
  
  Statement getMissedStatement()
  {
    Iterator localIterator = this.targetToStatementList.values().iterator();
    while (localIterator.hasNext())
    {
      List localList = (List)localIterator.next();
      for (int i = 0; i < localList.size(); i++) {
        if (Statement.class == ((Statement)localList.get(i)).getClass()) {
          return (Statement)localList.remove(i);
        }
      }
    }
    return null;
  }
  
  public void close()
  {
    flush();
    writeln("</java>");
    try
    {
      this.out.close();
    }
    catch (IOException localIOException)
    {
      getExceptionListener().exceptionThrown(localIOException);
    }
  }
  
  private String quote(String paramString)
  {
    return "\"" + paramString + "\"";
  }
  
  private ValueData getValueData(Object paramObject)
  {
    ValueData localValueData = (ValueData)this.valueToExpression.get(paramObject);
    if (localValueData == null)
    {
      localValueData = new ValueData(null);
      this.valueToExpression.put(paramObject, localValueData);
    }
    return localValueData;
  }
  
  private static boolean isValidCharCode(int paramInt)
  {
    return ((32 <= paramInt) && (paramInt <= 55295)) || (10 == paramInt) || (9 == paramInt) || (13 == paramInt) || ((57344 <= paramInt) && (paramInt <= 65533)) || ((65536 <= paramInt) && (paramInt <= 1114111));
  }
  
  private void writeln(String paramString)
  {
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      for (int i = 0; i < this.indentation; i++) {
        localStringBuilder.append(' ');
      }
      localStringBuilder.append(paramString);
      localStringBuilder.append('\n');
      this.out.write(localStringBuilder.toString());
    }
    catch (IOException localIOException)
    {
      getExceptionListener().exceptionThrown(localIOException);
    }
  }
  
  private void outputValue(Object paramObject1, Object paramObject2, boolean paramBoolean)
  {
    if (paramObject1 == null)
    {
      writeln("<null/>");
      return;
    }
    if ((paramObject1 instanceof Class))
    {
      writeln("<class>" + ((Class)paramObject1).getName() + "</class>");
      return;
    }
    ValueData localValueData = getValueData(paramObject1);
    if (localValueData.exp != null)
    {
      Object localObject1 = localValueData.exp.getTarget();
      String str1 = localValueData.exp.getMethodName();
      if ((localObject1 == null) || (str1 == null)) {
        throw new NullPointerException((localObject1 == null ? "target" : "methodName") + " should not be null");
      }
      if ((paramBoolean) && ((localObject1 instanceof Field)) && (str1.equals("get")))
      {
        localObject2 = (Field)localObject1;
        writeln("<object class=" + quote(((Field)localObject2).getDeclaringClass().getName()) + " field=" + quote(((Field)localObject2).getName()) + "/>");
        return;
      }
      Object localObject2 = primitiveTypeFor(paramObject1.getClass());
      if ((localObject2 != null) && (localObject1 == paramObject1.getClass()) && (str1.equals("new")))
      {
        String str2 = ((Class)localObject2).getName();
        if (localObject2 == Character.TYPE)
        {
          char c = ((Character)paramObject1).charValue();
          if (!isValidCharCode(c))
          {
            writeln(createString(c));
            return;
          }
          paramObject1 = quoteCharCode(c);
          if (paramObject1 == null) {
            paramObject1 = Character.valueOf(c);
          }
        }
        writeln("<" + str2 + ">" + paramObject1 + "</" + str2 + ">");
        return;
      }
    }
    else if ((paramObject1 instanceof String))
    {
      writeln(createString((String)paramObject1));
      return;
    }
    if (localValueData.name != null)
    {
      if (paramBoolean) {
        writeln("<object idref=" + quote(localValueData.name) + "/>");
      } else {
        outputXML("void", " idref=" + quote(localValueData.name), paramObject1, new Object[0]);
      }
    }
    else if (localValueData.exp != null) {
      outputStatement(localValueData.exp, paramObject2, paramBoolean);
    }
  }
  
  private static String quoteCharCode(int paramInt)
  {
    switch (paramInt)
    {
    case 38: 
      return "&amp;";
    case 60: 
      return "&lt;";
    case 62: 
      return "&gt;";
    case 34: 
      return "&quot;";
    case 39: 
      return "&apos;";
    case 13: 
      return "&#13;";
    }
    return null;
  }
  
  private static String createString(int paramInt)
  {
    return "<char code=\"#" + Integer.toString(paramInt, 16) + "\"/>";
  }
  
  private String createString(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<string>");
    int i = 0;
    while (i < paramString.length())
    {
      int j = paramString.codePointAt(i);
      int k = Character.charCount(j);
      if ((isValidCharCode(j)) && (this.encoder.canEncode(paramString.substring(i, i + k))))
      {
        String str = quoteCharCode(j);
        if (str != null) {
          localStringBuilder.append(str);
        } else {
          localStringBuilder.appendCodePoint(j);
        }
        i += k;
      }
      else
      {
        localStringBuilder.append(createString(paramString.charAt(i)));
        i++;
      }
    }
    localStringBuilder.append("</string>");
    return localStringBuilder.toString();
  }
  
  private void outputStatement(Statement paramStatement, Object paramObject, boolean paramBoolean)
  {
    Object localObject1 = paramStatement.getTarget();
    String str1 = paramStatement.getMethodName();
    if ((localObject1 == null) || (str1 == null)) {
      throw new NullPointerException((localObject1 == null ? "target" : "methodName") + " should not be null");
    }
    Object[] arrayOfObject = paramStatement.getArguments();
    int i = paramStatement.getClass() == Expression.class ? 1 : 0;
    Object localObject2 = i != 0 ? getValue((Expression)paramStatement) : null;
    String str2 = (i != 0) && (paramBoolean) ? "object" : "void";
    String str3 = "";
    ValueData localValueData = getValueData(localObject2);
    Object localObject3;
    if (localObject1 != paramObject) {
      if ((localObject1 == Array.class) && (str1.equals("newInstance")))
      {
        str2 = "array";
        str3 = str3 + " class=" + quote(((Class)arrayOfObject[0]).getName());
        str3 = str3 + " length=" + quote(arrayOfObject[1].toString());
        arrayOfObject = new Object[0];
      }
      else if (localObject1.getClass() == Class.class)
      {
        str3 = str3 + " class=" + quote(((Class)localObject1).getName());
      }
      else
      {
        localValueData.refs = 2;
        if (localValueData.name == null)
        {
          getValueData(localObject1).refs += 1;
          localObject3 = statementList(localObject1);
          if (!((List)localObject3).contains(paramStatement)) {
            ((List)localObject3).add(paramStatement);
          }
          outputValue(localObject1, paramObject, false);
        }
        if (i != 0) {
          outputValue(localObject2, paramObject, paramBoolean);
        }
        return;
      }
    }
    if ((i != 0) && (localValueData.refs > 1))
    {
      localObject3 = this.nameGenerator.instanceName(localObject2);
      localValueData.name = ((String)localObject3);
      str3 = str3 + " id=" + quote((String)localObject3);
    }
    if (((i == 0) && (str1.equals("set")) && (arrayOfObject.length == 2) && ((arrayOfObject[0] instanceof Integer))) || ((i != 0) && (str1.equals("get")) && (arrayOfObject.length == 1) && ((arrayOfObject[0] instanceof Integer))))
    {
      str3 = str3 + " index=" + quote(arrayOfObject[0].toString());
      arrayOfObject = new Object[] { arrayOfObject.length == 1 ? new Object[0] : arrayOfObject[1] };
    }
    else if (((i == 0) && (str1.startsWith("set")) && (arrayOfObject.length == 1)) || ((i != 0) && (str1.startsWith("get")) && (arrayOfObject.length == 0)))
    {
      if (3 < str1.length()) {
        str3 = str3 + " property=" + quote(Introspector.decapitalize(str1.substring(3)));
      }
    }
    else if ((!str1.equals("new")) && (!str1.equals("newInstance")))
    {
      str3 = str3 + " method=" + quote(str1);
    }
    outputXML(str2, str3, localObject2, arrayOfObject);
  }
  
  private void outputXML(String paramString1, String paramString2, Object paramObject, Object... paramVarArgs)
  {
    List localList = statementList(paramObject);
    if ((paramVarArgs.length == 0) && (localList.size() == 0))
    {
      writeln("<" + paramString1 + paramString2 + "/>");
      return;
    }
    writeln("<" + paramString1 + paramString2 + ">");
    this.indentation += 1;
    for (int i = 0; i < paramVarArgs.length; i++) {
      outputValue(paramVarArgs[i], null, true);
    }
    while (!localList.isEmpty())
    {
      Statement localStatement = (Statement)localList.remove(0);
      outputStatement(localStatement, paramObject, false);
    }
    this.indentation -= 1;
    writeln("</" + paramString1 + ">");
  }
  
  static Class primitiveTypeFor(Class paramClass)
  {
    if (paramClass == Boolean.class) {
      return Boolean.TYPE;
    }
    if (paramClass == Byte.class) {
      return Byte.TYPE;
    }
    if (paramClass == Character.class) {
      return Character.TYPE;
    }
    if (paramClass == Short.class) {
      return Short.TYPE;
    }
    if (paramClass == Integer.class) {
      return Integer.TYPE;
    }
    if (paramClass == Long.class) {
      return Long.TYPE;
    }
    if (paramClass == Float.class) {
      return Float.TYPE;
    }
    if (paramClass == Double.class) {
      return Double.TYPE;
    }
    if (paramClass == Void.class) {
      return Void.TYPE;
    }
    return null;
  }
  
  private class ValueData
  {
    public int refs = 0;
    public boolean marked = false;
    public String name = null;
    public Expression exp = null;
    
    private ValueData() {}
  }
}
