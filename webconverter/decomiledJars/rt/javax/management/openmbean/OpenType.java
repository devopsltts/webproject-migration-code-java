package javax.management.openmbean;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;

public abstract class OpenType<T>
  implements Serializable
{
  static final long serialVersionUID = -9195195325186646468L;
  public static final List<String> ALLOWED_CLASSNAMES_LIST = Collections.unmodifiableList(Arrays.asList(new String[] { "java.lang.Void", "java.lang.Boolean", "java.lang.Character", "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.String", "java.math.BigDecimal", "java.math.BigInteger", "java.util.Date", "javax.management.ObjectName", CompositeData.class.getName(), TabularData.class.getName() }));
  @Deprecated
  public static final String[] ALLOWED_CLASSNAMES = (String[])ALLOWED_CLASSNAMES_LIST.toArray(new String[0]);
  private String className;
  private String description;
  private String typeName;
  private transient boolean isArray = false;
  private transient Descriptor descriptor;
  
  protected OpenType(String paramString1, String paramString2, String paramString3)
    throws OpenDataException
  {
    checkClassNameOverride();
    this.typeName = valid("typeName", paramString2);
    this.description = valid("description", paramString3);
    this.className = validClassName(paramString1);
    this.isArray = ((this.className != null) && (this.className.startsWith("[")));
  }
  
  OpenType(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    this.className = valid("className", paramString1);
    this.typeName = valid("typeName", paramString2);
    this.description = valid("description", paramString3);
    this.isArray = paramBoolean;
  }
  
  private void checkClassNameOverride()
    throws SecurityException
  {
    if (getClass().getClassLoader() == null) {
      return;
    }
    if (overridesGetClassName(getClass()))
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.extend.open.types");
      if (AccessController.doPrivileged(localGetPropertyAction) == null) {
        throw new SecurityException("Cannot override getClassName() unless -Djmx.extend.open.types");
      }
    }
  }
  
  private static boolean overridesGetClassName(Class<?> paramClass)
  {
    ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        try
        {
          return Boolean.valueOf(this.val$c.getMethod("getClassName", new Class[0]).getDeclaringClass() != OpenType.class);
        }
        catch (Exception localException) {}
        return Boolean.valueOf(true);
      }
    })).booleanValue();
  }
  
  private static String validClassName(String paramString)
    throws OpenDataException
  {
    paramString = valid("className", paramString);
    for (int i = 0; paramString.startsWith("[", i); i++) {}
    int j = 0;
    String str;
    if (i > 0)
    {
      if ((paramString.startsWith("L", i)) && (paramString.endsWith(";")))
      {
        str = paramString.substring(i + 1, paramString.length() - 1);
      }
      else if (i == paramString.length() - 1)
      {
        str = paramString.substring(i, paramString.length());
        j = 1;
      }
      else
      {
        throw new OpenDataException("Argument className=\"" + paramString + "\" is not a valid class name");
      }
    }
    else {
      str = paramString;
    }
    boolean bool = false;
    if (j != 0) {
      bool = ArrayType.isPrimitiveContentType(str);
    } else {
      bool = ALLOWED_CLASSNAMES_LIST.contains(str);
    }
    if (!bool) {
      throw new OpenDataException("Argument className=\"" + paramString + "\" is not one of the allowed Java class names for open data.");
    }
    return paramString;
  }
  
  private static String valid(String paramString1, String paramString2)
  {
    if ((paramString2 == null) || ((paramString2 = paramString2.trim()).equals(""))) {
      throw new IllegalArgumentException("Argument " + paramString1 + " cannot be null or empty");
    }
    return paramString2;
  }
  
  synchronized Descriptor getDescriptor()
  {
    if (this.descriptor == null) {
      this.descriptor = new ImmutableDescriptor(new String[] { "openType" }, new Object[] { this });
    }
    return this.descriptor;
  }
  
  public String getClassName()
  {
    return this.className;
  }
  
  String safeGetClassName()
  {
    return this.className;
  }
  
  public String getTypeName()
  {
    return this.typeName;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public boolean isArray()
  {
    return this.isArray;
  }
  
  public abstract boolean isValue(Object paramObject);
  
  boolean isAssignableFrom(OpenType<?> paramOpenType)
  {
    return equals(paramOpenType);
  }
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    checkClassNameOverride();
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    String str1;
    String str2;
    String str3;
    try
    {
      str1 = validClassName((String)localGetField.get("className", null));
      str2 = valid("description", (String)localGetField.get("description", null));
      str3 = valid("typeName", (String)localGetField.get("typeName", null));
    }
    catch (Exception localException)
    {
      InvalidObjectException localInvalidObjectException = new InvalidObjectException(localException.getMessage());
      localInvalidObjectException.initCause(localException);
      throw localInvalidObjectException;
    }
    this.className = str1;
    this.description = str2;
    this.typeName = str3;
    this.isArray = this.className.startsWith("[");
  }
}
