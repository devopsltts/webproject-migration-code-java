package sun.swing;

import java.awt.Color;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.plaf.ColorUIResource;
import sun.reflect.misc.ReflectUtil;

public class SwingLazyValue
  implements UIDefaults.LazyValue
{
  private String className;
  private String methodName;
  private Object[] args;
  
  public SwingLazyValue(String paramString)
  {
    this(paramString, (String)null);
  }
  
  public SwingLazyValue(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public SwingLazyValue(String paramString, Object[] paramArrayOfObject)
  {
    this(paramString, null, paramArrayOfObject);
  }
  
  public SwingLazyValue(String paramString1, String paramString2, Object[] paramArrayOfObject)
  {
    this.className = paramString1;
    this.methodName = paramString2;
    if (paramArrayOfObject != null) {
      this.args = ((Object[])paramArrayOfObject.clone());
    }
  }
  
  public Object createValue(UIDefaults paramUIDefaults)
  {
    try
    {
      ReflectUtil.checkPackageAccess(this.className);
      Class localClass = Class.forName(this.className, true, null);
      if (this.methodName != null)
      {
        arrayOfClass = getClassArray(this.args);
        localObject = localClass.getMethod(this.methodName, arrayOfClass);
        makeAccessible((AccessibleObject)localObject);
        return ((Method)localObject).invoke(localClass, this.args);
      }
      Class[] arrayOfClass = getClassArray(this.args);
      Object localObject = localClass.getConstructor(arrayOfClass);
      makeAccessible((AccessibleObject)localObject);
      return ((Constructor)localObject).newInstance(this.args);
    }
    catch (Exception localException) {}
    return null;
  }
  
  private void makeAccessible(final AccessibleObject paramAccessibleObject)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        paramAccessibleObject.setAccessible(true);
        return null;
      }
    });
  }
  
  private Class[] getClassArray(Object[] paramArrayOfObject)
  {
    Class[] arrayOfClass = null;
    if (paramArrayOfObject != null)
    {
      arrayOfClass = new Class[paramArrayOfObject.length];
      for (int i = 0; i < paramArrayOfObject.length; i++) {
        if ((paramArrayOfObject[i] instanceof Integer)) {
          arrayOfClass[i] = Integer.TYPE;
        } else if ((paramArrayOfObject[i] instanceof Boolean)) {
          arrayOfClass[i] = Boolean.TYPE;
        } else if ((paramArrayOfObject[i] instanceof ColorUIResource)) {
          arrayOfClass[i] = Color.class;
        } else {
          arrayOfClass[i] = paramArrayOfObject[i].getClass();
        }
      }
    }
    return arrayOfClass;
  }
}
