package java.beans;

import java.awt.Component;
import java.awt.event.ComponentListener;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EventListener;
import java.util.Objects;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeListener;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class DefaultPersistenceDelegate
  extends PersistenceDelegate
{
  private static final String[] EMPTY = new String[0];
  private final String[] constructor;
  private Boolean definesEquals;
  
  public DefaultPersistenceDelegate()
  {
    this.constructor = EMPTY;
  }
  
  public DefaultPersistenceDelegate(String[] paramArrayOfString)
  {
    this.constructor = (paramArrayOfString == null ? EMPTY : (String[])paramArrayOfString.clone());
  }
  
  private static boolean definesEquals(Class<?> paramClass)
  {
    try
    {
      return paramClass == paramClass.getMethod("equals", new Class[] { Object.class }).getDeclaringClass();
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return false;
  }
  
  private boolean definesEquals(Object paramObject)
  {
    if (this.definesEquals != null) {
      return this.definesEquals == Boolean.TRUE;
    }
    boolean bool = definesEquals(paramObject.getClass());
    this.definesEquals = (bool ? Boolean.TRUE : Boolean.FALSE);
    return bool;
  }
  
  protected boolean mutatesTo(Object paramObject1, Object paramObject2)
  {
    return (this.constructor.length == 0) || (!definesEquals(paramObject1)) ? super.mutatesTo(paramObject1, paramObject2) : paramObject1.equals(paramObject2);
  }
  
  protected Expression instantiate(Object paramObject, Encoder paramEncoder)
  {
    int i = this.constructor.length;
    Class localClass = paramObject.getClass();
    Object[] arrayOfObject = new Object[i];
    for (int j = 0; j < i; j++) {
      try
      {
        Method localMethod = findMethod(localClass, this.constructor[j]);
        arrayOfObject[j] = MethodUtil.invoke(localMethod, paramObject, new Object[0]);
      }
      catch (Exception localException)
      {
        paramEncoder.getExceptionListener().exceptionThrown(localException);
      }
    }
    return new Expression(paramObject, paramObject.getClass(), "new", arrayOfObject);
  }
  
  private Method findMethod(Class<?> paramClass, String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Property name is null");
    }
    PropertyDescriptor localPropertyDescriptor = getPropertyDescriptor(paramClass, paramString);
    if (localPropertyDescriptor == null) {
      throw new IllegalStateException("Could not find property by the name " + paramString);
    }
    Method localMethod = localPropertyDescriptor.getReadMethod();
    if (localMethod == null) {
      throw new IllegalStateException("Could not find getter for the property " + paramString);
    }
    return localMethod;
  }
  
  private void doProperty(Class<?> paramClass, PropertyDescriptor paramPropertyDescriptor, Object paramObject1, Object paramObject2, Encoder paramEncoder)
    throws Exception
  {
    Method localMethod1 = paramPropertyDescriptor.getReadMethod();
    Method localMethod2 = paramPropertyDescriptor.getWriteMethod();
    if ((localMethod1 != null) && (localMethod2 != null))
    {
      Expression localExpression1 = new Expression(paramObject1, localMethod1.getName(), new Object[0]);
      Expression localExpression2 = new Expression(paramObject2, localMethod1.getName(), new Object[0]);
      Object localObject1 = localExpression1.getValue();
      Object localObject2 = localExpression2.getValue();
      paramEncoder.writeExpression(localExpression1);
      if (!Objects.equals(localObject2, paramEncoder.get(localObject1)))
      {
        Object[] arrayOfObject1 = (Object[])paramPropertyDescriptor.getValue("enumerationValues");
        if (((arrayOfObject1 instanceof Object[])) && (Array.getLength(arrayOfObject1) % 3 == 0))
        {
          Object[] arrayOfObject2 = (Object[])arrayOfObject1;
          int i = 0;
          while (i < arrayOfObject2.length)
          {
            try
            {
              Field localField = paramClass.getField((String)arrayOfObject2[i]);
              if (localField.get(null).equals(localObject1))
              {
                paramEncoder.remove(localObject1);
                paramEncoder.writeExpression(new Expression(localObject1, localField, "get", new Object[] { null }));
              }
            }
            catch (Exception localException) {}
            i += 3;
          }
        }
        invokeStatement(paramObject1, localMethod2.getName(), new Object[] { localObject1 }, paramEncoder);
      }
    }
  }
  
  static void invokeStatement(Object paramObject, String paramString, Object[] paramArrayOfObject, Encoder paramEncoder)
  {
    paramEncoder.writeStatement(new Statement(paramObject, paramString, paramArrayOfObject));
  }
  
  private void initBean(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
  {
    Object localObject4;
    Object localObject5;
    Object localObject6;
    for (Object localObject3 : paramClass.getFields()) {
      if (ReflectUtil.isPackageAccessible(localObject3.getDeclaringClass()))
      {
        int m = localObject3.getModifiers();
        if ((!Modifier.isFinal(m)) && (!Modifier.isStatic(m)) && (!Modifier.isTransient(m))) {
          try
          {
            Expression localExpression = new Expression(localObject3, "get", new Object[] { paramObject1 });
            localObject4 = new Expression(localObject3, "get", new Object[] { paramObject2 });
            localObject5 = localExpression.getValue();
            localObject6 = ((Expression)localObject4).getValue();
            paramEncoder.writeExpression(localExpression);
            if (!Objects.equals(localObject6, paramEncoder.get(localObject5))) {
              paramEncoder.writeStatement(new Statement(localObject3, "set", new Object[] { paramObject1, localObject5 }));
            }
          }
          catch (Exception localException1)
          {
            paramEncoder.getExceptionListener().exceptionThrown(localException1);
          }
        }
      }
    }
    try
    {
      ??? = Introspector.getBeanInfo(paramClass);
    }
    catch (IntrospectionException localIntrospectionException)
    {
      return;
    }
    PropertyDescriptor localPropertyDescriptor;
    for (localPropertyDescriptor : ((BeanInfo)???).getPropertyDescriptors()) {
      if (!localPropertyDescriptor.isTransient()) {
        try
        {
          doProperty(paramClass, localPropertyDescriptor, paramObject1, paramObject2, paramEncoder);
        }
        catch (Exception localException2)
        {
          paramEncoder.getExceptionListener().exceptionThrown(localException2);
        }
      }
    }
    if (!Component.class.isAssignableFrom(paramClass)) {
      return;
    }
    for (localPropertyDescriptor : ((BeanInfo)???).getEventSetDescriptors()) {
      if (!localPropertyDescriptor.isTransient())
      {
        Class localClass = localPropertyDescriptor.getListenerType();
        if ((localClass != ComponentListener.class) && ((localClass != ChangeListener.class) || (paramClass != JMenuItem.class)))
        {
          localObject4 = new EventListener[0];
          localObject5 = new EventListener[0];
          try
          {
            localObject6 = localPropertyDescriptor.getGetListenerMethod();
            localObject4 = (EventListener[])MethodUtil.invoke((Method)localObject6, paramObject1, new Object[0]);
            localObject5 = (EventListener[])MethodUtil.invoke((Method)localObject6, paramObject2, new Object[0]);
          }
          catch (Exception localException3)
          {
            try
            {
              Method localMethod = paramClass.getMethod("getListeners", new Class[] { Class.class });
              localObject4 = (EventListener[])MethodUtil.invoke(localMethod, paramObject1, new Object[] { localClass });
              localObject5 = (EventListener[])MethodUtil.invoke(localMethod, paramObject2, new Object[] { localClass });
            }
            catch (Exception localException4)
            {
              return;
            }
          }
          String str1 = localPropertyDescriptor.getAddListenerMethod().getName();
          for (int n = localObject5.length; n < localObject4.length; n++) {
            invokeStatement(paramObject1, str1, new Object[] { localObject4[n] }, paramEncoder);
          }
          String str2 = localPropertyDescriptor.getRemoveListenerMethod().getName();
          for (int i1 = localObject4.length; i1 < localObject5.length; i1++) {
            invokeStatement(paramObject1, str2, new Object[] { localObject5[i1] }, paramEncoder);
          }
        }
      }
    }
  }
  
  protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
  {
    super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
    if (paramObject1.getClass() == paramClass) {
      initBean(paramClass, paramObject1, paramObject2, paramEncoder);
    }
  }
  
  private static PropertyDescriptor getPropertyDescriptor(Class<?> paramClass, String paramString)
  {
    try
    {
      for (PropertyDescriptor localPropertyDescriptor : Introspector.getBeanInfo(paramClass).getPropertyDescriptors()) {
        if (paramString.equals(localPropertyDescriptor.getName())) {
          return localPropertyDescriptor;
        }
      }
    }
    catch (IntrospectionException localIntrospectionException) {}
    return null;
  }
}
