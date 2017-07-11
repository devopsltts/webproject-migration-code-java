package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

final class Utils
{
  private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
  static final Navigator<Type, Class, Field, Method> REFLECTION_NAVIGATOR;
  
  private Utils() {}
  
  static
  {
    try
    {
      Class localClass = Class.forName("com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator");
      Method localMethod = (Method)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Method run()
        {
          try
          {
            Method localMethod = this.val$refNav.getDeclaredMethod("getInstance", new Class[0]);
            localMethod.setAccessible(true);
            return localMethod;
          }
          catch (NoSuchMethodException localNoSuchMethodException)
          {
            throw new IllegalStateException("ReflectionNavigator.getInstance can't be found");
          }
        }
      });
      REFLECTION_NAVIGATOR = (Navigator)localMethod.invoke(null, new Object[0]);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new IllegalStateException("Can't find ReflectionNavigator class");
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new IllegalStateException("ReflectionNavigator.getInstance throws the exception");
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new IllegalStateException("ReflectionNavigator.getInstance method is inaccessible");
    }
    catch (SecurityException localSecurityException)
    {
      LOGGER.log(Level.FINE, "Unable to access ReflectionNavigator.getInstance", localSecurityException);
      throw localSecurityException;
    }
  }
}
